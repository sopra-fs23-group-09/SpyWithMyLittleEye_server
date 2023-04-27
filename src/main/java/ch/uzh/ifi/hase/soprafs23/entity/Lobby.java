package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lobby {
    private static final int MAX_AMOUNT_PLAYERS = 10;
    private final int id;
    private final int accessCode;
    private final List<User> players;
    private final User host;
    private boolean full;
    private final int amountRounds;
    private boolean gameStarted;

    public Lobby(User host, int id, int accessCode, int amountRounds){
        this.id = id;
        this.host = host;
        this.accessCode = accessCode;
        this.players = new ArrayList<>(MAX_AMOUNT_PLAYERS);
        this.players.add(host);
        this.full = false;
        this.amountRounds = amountRounds;
    }
    public int getId() {
        return id;
    }

    public Game play(){
        if (this.gameStarted) return null;
        Game game = new Game(id, players, amountRounds, host);
        game.nextRound();
        this.gameStarted = true;
        return game;
    }

    public List<User> getPlayers(){
        return Collections.unmodifiableList(players);
    }

    public boolean addPlayer(User player){
        if (isFull()) return false;
        players.add(player);
        if(players.size() == MAX_AMOUNT_PLAYERS){
            this.full = true;
        }
        player.setLobbyID(id);
        return true;
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
        return String.format("Lobby [id=%d, accessCode=%d]", id, accessCode);
    }

}
