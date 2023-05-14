package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.DropOutMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Lobby {
    private static final int MAX_AMOUNT_PLAYERS = 10;
    private final int id;
    private final int accessCode;
    private final List<Player> players;
    private Player host;
    private boolean full;
    private final int amountRounds;
    private Game game;
    private final float duration;

    public Lobby(Player host, int id, int accessCode, int amountRounds, float duration){
        this.id = id;
        this.host = host;
        host.setLobbyID(id);
        this.accessCode = accessCode;
        this.players = new ArrayList<>(MAX_AMOUNT_PLAYERS);
        this.players.add(host);
        this.full = false;
        this.amountRounds = amountRounds;
        this.duration = duration;
    }
    public int getId() {
        return id;
    }

    public Game play(PlayerService playerService){
        if (this.game != null) return null;
        game = new Game(id, players, amountRounds, host, playerService, duration);
        game.nextRound();
        return game;
    }

    public int kickPlayer(Player player, WebSocketService ws) {
        int deleteGameOrLobby = -1;
        if(this.game != null) {
            deleteGameOrLobby = game.kickPlayer(player, ws);
        } else {
            players.remove(player);
            if (players.size() < 1){
                deleteGameOrLobby = 0;
            }else{
                boolean isNewHost = false;
                if(Objects.equals(player.getId(), host.getId())){
                    host = players.get(0);
                    isNewHost = true;
                }
                ws.sendMessageToSubscribers("/topic/games/"+id+"/userDropOut",
                        new DropOutMessage(player.getUsername(), Role.GUESSER,isNewHost, host.getId().intValue(), false));
            }
        }
        return deleteGameOrLobby;
    }

    public List<Player> getPlayers(){
        return Collections.unmodifiableList(players);
    }
    public boolean gameStarted(){
        return game != null;
    }

    public boolean addPlayer(Player player){
        if (isFull()) return false;
        players.add(player);
        if(players.size() == MAX_AMOUNT_PLAYERS){
            this.full = true;
        }
        player.setLobbyID(id);
        return true;
    }

    public boolean removePlayer(Player player){
        if (players.contains(player)){
            players.remove(player);
            return true;
        }
        return false;
    }

    public int getAccessCode(){
        return this.accessCode;
    }

    public int getAmountRounds() { return this.amountRounds; }

    public boolean isFull(){
        return this.full;
    }

    public Long getHostId() {
        return host.getId();
    }

    //debugging
    @Override
    public String toString(){
        return String.format("Lobby [id=%d, accessCode=%d, duration=%.1f]", id, accessCode, duration);
    }

}
