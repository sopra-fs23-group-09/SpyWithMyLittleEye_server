package ch.uzh.ifi.hase.soprafs23.entity;
import ch.uzh.ifi.hase.soprafs23.constant.Role;

import java.util.List;

public class Game {

    private List<User> players;
    private User host;
    private Round[] rounds;
    private int amountRounds;
    private String googleMapsCoordinates;
    private int currentRound;

    public Game(List<User> players, User host, int amountRounds){
        this.players = players;
        this.host = host;
        this.amountRounds = amountRounds;
        this.rounds = new Round[amountRounds];
        this.currentRound = 0;
    }

    public void nextRound(){
        rounds[currentRound] = new Round(players, currentRound);
        currentRound++;
    }

    public void storeCoordinates(String googleMapsCoordinates){
        this.googleMapsCoordinates = googleMapsCoordinates;
    }

    public Round getCurrentRound() {
        return rounds[currentRound];
    }

    public Role getRole(Long playerId){
        return rounds[currentRound].getRole(playerId);
    }
    public void setColorAndKeyword(String keyword, String color){
        rounds[currentRound].setKeyword(keyword);
        rounds[currentRound].setColor(color);
    }
}
