package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Role;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private static int MAX_AMOUNT_PLAYERS;
    private int id;
    private int accessCode;
    private List<User> players;
    private User host;
    private boolean full;
    private Game game;
    private int amountRounds;

    public Lobby(User host, int id, int accessCode, int amountRounds){
        this.id = id;
        this.host = host;
        this.accessCode = accessCode;
        this.players = new ArrayList<User>();
        this.players.add(host);
        this.full = false;
        this.amountRounds = amountRounds;
    }
    public int getId() {
        return id;
    }

    public void play(){
        this.game = new Game(players, host, amountRounds);
        nextRound();
    }
    public void nextRound(){
        this.game.nextRound();
    }

    public List<User> getPlayers(){
        return players;
    }

    public boolean addPlayer(User player){
        if (isFull()) return false;
        players.add(player);
        if(players.size() == MAX_AMOUNT_PLAYERS){
            this.full = true;
        }
        return true;
    }
    public Role getRole(Long playerId){
        return this.game.getRole(playerId);
    }
    public int getAccessCode(){
        return this.accessCode;
    }

    public int getAmountRounds() { return this.amountRounds; }

    public boolean isFull(){
        return this.full;
    }

    public Game getGame() {
        return game;
    }

    public Long getHostId() {
        return host.getId();
    }

    public void setColorAndKeyword(String keyword, String color){
        this.game.setColorAndKeyword(keyword, color);
    }
}
