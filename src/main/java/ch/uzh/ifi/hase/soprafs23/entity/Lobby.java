package ch.uzh.ifi.hase.soprafs23.entity;

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
        this.full = false;
        this.amountRounds = amountRounds;
        this.game = new Game(players, host, amountRounds); //note c: already initiate game here?!
    }
    public int getId() {
        return id;
    }

    public void play(){
        this.game = new Game(players, host, amountRounds);
        this.game.playRound();
    }
    public void playRound(){
        this.game.playRound();
    }

    public List<User> getPlayers(){
        return players;
    }

    public void addPlayer(User player){
        players.add(player);
        if(players.size() == MAX_AMOUNT_PLAYERS){
            this.full = true;
        }
    }
    public int getAccessCode(){
        return this.accessCode;
    }

    public boolean isFull(){
        return this.full;
    }

    public Game getGame() {
        return game;
    }
}
