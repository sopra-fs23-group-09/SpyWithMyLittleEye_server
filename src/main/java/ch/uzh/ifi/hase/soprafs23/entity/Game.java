package ch.uzh.ifi.hase.soprafs23.entity;
import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.controller.GameStompController;
import ch.uzh.ifi.hase.soprafs23.entity.wrappers.Guess;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

public class Game {
    public static final int DURATION = 1;
    private final int id;
    private Long hostId;
    private final Map<User, Integer> playerPoints;
    private final List<User> players;
    private final int amountRounds;
    private List<Guess> playerGuesses;
    private int currentRoundNr;
    private Timer roundTimer;
    private boolean timerStarted;
    private Map<Long, Role> playerRoles;
    private String keyword;
    private int nrPlayersGuessedCorrectly;
    private String roundOverStatus;
    private Date startTime;

    public Game(int id, List<User> players, int amountRounds, User host){
        this.playerRoles = new HashMap<>();
        this.playerGuesses = new ArrayList<>();
        this.playerPoints = new HashMap<>();
        this.id = id;
        this.players = players;
        initializePoints();
        this.amountRounds = amountRounds;
        this.currentRoundNr = 0;
        this.nrPlayersGuessedCorrectly = 0;
        this.hostId = host.getId();
    }

    public void updatePointsIfGameEnded(){
        User winner = players.get(0);
        for(User u : players){
            if(playerPoints.get(u) > playerPoints.get(winner)) winner = u;
            u.setHighScore(u.getHighScore()+ playerPoints.get(u));
            u.setGamesPlayed(u.getGamesPlayed() + 1);
        }
        winner.setGamesWon(winner.getGamesWon() + 1);
    }
    public String getKeyword(){
        return keyword;
    }
    public String getRoundOverStatus(){
        return roundOverStatus;
    }

    public void setRoundOverStatus(String roundOverStatus) {
        this.roundOverStatus = roundOverStatus;
    }

    public void runTimer(GameStompController conG){
        if (!timerStarted) {
            timerStarted = true;
            roundTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String message = "time is up";
                    setRoundOverStatus(message);
                    roundTimer.cancel();
                    conG.handleEndRound(id, message, amountRounds, currentRoundNr);
                }
            }, (long) DURATION * 60 * 1000);
        }
    }

    public void endRoundIfAllUsersGuessedCorrectly(GameStompController conG){
        if (this.nrPlayersGuessedCorrectly == (players.size() -1)){
            roundTimer.cancel();
            String message = "all guessed correctly";
            setRoundOverStatus(message);
            conG.handleEndRound(id, message, amountRounds, currentRoundNr);
        }
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
            if(i == (currentRoundNr - 1) % players.size()){
                playerRoles.put(players.get(i).getId(), Role.SPIER);
            }else{
                playerRoles.put(players.get(i).getId(), Role.GUESSER);
            }
        }
    }
    public void allocatePoints(User player, Date guessTime){
        // formula to compute points: duration in seconds - seconds needed to guess
        int points = (int) ((DURATION *60) - (guessTime.getTime()- startTime.getTime())/1000);
        int pointsOfCurrentPlayer = playerPoints.get(player) + points;
        playerPoints.put(player, pointsOfCurrentPlayer);
        this.nrPlayersGuessedCorrectly++;
    }

    public boolean checkGuess(String guess){ //TODO use levenshteindistance (static class in game for example) M4
        return guess.equalsIgnoreCase(this.keyword);
    }

    public void nextRound(){
        if(currentRoundNr + 1  > amountRounds){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All rounds were played");}
        playerRoles = new HashMap<>();
        playerGuesses = new ArrayList<>();
        keyword = null;
        startTime = null;
        roundTimer = new Timer();
        timerStarted = false;
        nrPlayersGuessedCorrectly = 0;
        currentRoundNr++;
        distributeRoles();
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

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }
}
