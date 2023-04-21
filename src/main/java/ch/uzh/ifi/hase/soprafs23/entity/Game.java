package ch.uzh.ifi.hase.soprafs23.entity;
import ch.uzh.ifi.hase.soprafs23.constant.Role;

import java.util.*;

public class Game {

    private List<User> players;
    private int amountRounds;
    private int currentRoundNr;
    private int id;
    private Map<Long, Role> playerRoles;
    private String keyword;
    private String color; //note c: needed? n : hmmm prob not because gets returned directly?
    private List<String> guesses; // clear guesses if new round starts... would have been better with round class...
    private Date startTime; // need to assign this value correctly, probably own method to assign and return
                            // instead of the method getStartTime (or let this method set the starTime?

    public Game(int id, List<User> players, int amountRounds){
        this.playerRoles = new HashMap<>();
        this.guesses = new ArrayList<>();
        this.id = id;
        this.players = players;
        this.amountRounds = amountRounds;
        this.currentRoundNr = 0;
    }

    public String getKeyword() {
        return keyword;
    }

    public boolean nextRound(){ //return if it was possible to have a next round? no check in gameservice atm
        if(currentRoundNr + 1  > amountRounds){ return false;}
        guesses = new ArrayList<>();
        playerRoles = new HashMap<>();
        currentRoundNr++;
        distributeRoles();
        return true;
    }
    private void distributeRoles(){
        for(int i = 0; i < players.size(); i++){
            if(i == (currentRoundNr - 1) % players.size()){ //is this better as counting from 0?
                playerRoles.put(players.get(i).getId(), Role.SPIER);
            }else{
                playerRoles.put(players.get(i).getId(), Role.GUESSER);
            }
        }
    }
    public int getId(){
        return this.id;
    }

    public Date getStartTime(){ // either let this method set a new startTime or check for uninitialized variable startTime
        return startTime;
    }

    public Role getRole(Long playerId){
        return playerRoles.get(playerId);
    }
    public void setColorAndKeyword(String keyword, String color){
        this.keyword = keyword;
        this.color = color;
    }

    public int getCurrentRoundNr() {
        return currentRoundNr;
    }

    public int getAmountRounds() {
        return amountRounds;
    }
}
