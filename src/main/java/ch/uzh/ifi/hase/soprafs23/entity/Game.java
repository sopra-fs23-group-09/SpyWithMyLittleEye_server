package ch.uzh.ifi.hase.soprafs23.entity;
import ch.uzh.ifi.hase.soprafs23.constant.Role;

import java.util.List;

public class Game {

    private List<User> players;
    private User host;
    private Round[] rounds;
    private int amountRounds;
    private String googleMapsCoordinates;
    private int currentRoundNr;

    public Game(List<User> players, User host, int amountRounds){
        this.players = players;
        this.host = host;
        this.amountRounds = amountRounds;
        this.rounds = new Round[amountRounds];
        this.currentRoundNr = 0;
    }

    public void nextRound(){
        rounds[currentRoundNr] = new Round(players, currentRoundNr);
        currentRoundNr++;
    }

    public void storeCoordinates(String googleMapsCoordinates){
        this.googleMapsCoordinates = googleMapsCoordinates;
    }

    public Round getCurrentRound() {
        return rounds[currentRoundNr];
    }

    public Role getRole(Long playerId){
        return rounds[currentRoundNr].getRole(playerId);
    }
    public void setColorAndKeyword(String keyword, String color){
        rounds[currentRoundNr].setKeyword(keyword);
        rounds[currentRoundNr].setColor(color);
    }

    public int getCurrentRoundNr() {
        return currentRoundNr;
    }

    public int getAmountRounds() {
        return amountRounds;
    }
}
