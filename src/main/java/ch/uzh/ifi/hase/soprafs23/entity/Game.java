package ch.uzh.ifi.hase.soprafs23.entity;
import ch.uzh.ifi.hase.soprafs23.constant.Role;

import java.util.Date;
import java.util.List;

public class Game {

    private List<User> players;
    private Round currentRound;
    private int amountRounds;
    private String googleMapsCoordinates;
    private int currentRoundNr;
    private int id;

    public Game(int id, List<User> players, int amountRounds){
        this.id = id;
        this.players = players;
        this.amountRounds = amountRounds;
        this.currentRoundNr = -1;
    }

    public void nextRound(){
        currentRoundNr++;
        currentRound = new Round(players, currentRoundNr);
    }
    public int getId(){
        return this.id;
    }

    public void storeCoordinates(String googleMapsCoordinates){
        this.googleMapsCoordinates = googleMapsCoordinates;
    }

    public Round getCurrentRound() {
        return currentRound;
    }
    public String getKeyword(){return currentRound.getKeyword();}

    public Date getStartTime(){ return currentRound.getStartTime();}

    public Role getRole(Long playerId){
        return currentRound.getRole(playerId);
    }
    public void setColorAndKeyword(String keyword, String color){
        currentRound.setKeyword(keyword);
        currentRound.setColor(color);
    }

    public int getCurrentRoundNr() {
        return currentRoundNr;
    }

    public int getAmountRounds() {
        return amountRounds;
    }
}
