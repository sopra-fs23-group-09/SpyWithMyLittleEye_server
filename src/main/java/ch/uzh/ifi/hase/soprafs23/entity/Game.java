package ch.uzh.ifi.hase.soprafs23.entity;
import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.entity.wrappers.Guess;

import java.util.*;

public class Game {

    private final Map<User, Integer> playerPoints;
    private List<Guess> playerGuesses;
    private final List<User> players;
    private final int amountRounds;
    private int currentRoundNr;
    private final int id;
    private Map<Long, Role> playerRoles;
    private String keyword;
    private int nrPlayersGuessedCorrectly;

    private String roundOverStatus = "time out"; //TODO adapt that when timer etc works
    private Date startTime; // TODO: need to assign this value correctly, probably own method to assign and return
                            // TODO: instead of the method getStartTime (or let this method set the starTime?

    public Game(int id, List<User> players, int amountRounds){
        this.playerRoles = new HashMap<>();
        this.playerGuesses = new ArrayList<>();
        this.playerPoints = new HashMap<>();
        this.id = id;
        this.players = players;
        initializePoints();
        this.amountRounds = amountRounds;
        this.currentRoundNr = 0;
        this.nrPlayersGuessedCorrectly = 0; //TODO reset after each round
    }

    public void resetKeywordStarttimeNrplayerguessedcorrectly(){
        this.startTime = null;
        this.keyword = null;
        this.nrPlayersGuessedCorrectly = 0;
    }
    public String getKeyword(){
        return keyword;
    }
    public String getRoundOverStatus(){
        return roundOverStatus;
    }
    public Map<User, Integer> getPlayerPoints(){
        return new HashMap<>(playerPoints);
    }
    private void initializePoints(){
        for(User u : players){
            playerPoints.put(u, 0);
        }
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
    public void allocatePoints(User player, Date guessTime){
        // formula to compute points: 500 - seconds needed to guess
        int points = (int) (500 - (guessTime.getTime()- startTime.getTime())/1000);
        //int pointsOfCurrentPlayer = playerPoints.get(player) + points;
        //playerPoints.put(player, pointsOfCurrentPlayer);
        this.nrPlayersGuessedCorrectly++;
    }

    public boolean didAllPlayersGuessCorrectly(){
        if (this.nrPlayersGuessedCorrectly == players.size()){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkGuess(String guess){ //TODO use levenshteindistance (static class in game for example) M4
        return guess.equalsIgnoreCase(this.keyword);
    }

    public boolean nextRound(){ //TODO: return if it was possible to have a next round? no check in gameservice atm
        if(currentRoundNr + 1  > amountRounds){ return false;}
        playerRoles = new HashMap<>();
        playerGuesses = new ArrayList<>();
        currentRoundNr++;
        distributeRoles();
        return true;
    }
    public int getId(){
        return this.id;
    }
    public void storeGuess(String name, String guess){
        Guess g = new Guess(name, guess);
        playerGuesses.add(g);
    }
    public List<Guess> getGuesses(){
        return Collections.unmodifiableList(playerGuesses);
    }

    public Role getRole(Long playerId){
        return playerRoles.get(playerId);
    }
    public void setKeyword(String keyword){
        this.keyword = keyword;
    }

    public int getCurrentRoundNr() {
        return currentRoundNr;
    }

    public int getAmountRounds() {
        return amountRounds;
    }

    public void initializeStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
