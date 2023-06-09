package ch.uzh.ifi.hase.soprafs23.entity;
import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.controller.GameStompController;
import ch.uzh.ifi.hase.soprafs23.entity.wrappers.Guess;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.DropOutMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

public class Game {
    public final float duration;
    private final int id;
    private Long hostId;
    private final Map<Player, Integer> playerPoints;
    private final List<Player> players;
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
    private final PlayerService playerService;
    private boolean currentlyInRound;

    public Game(int id, List<Player> players, int amountRounds, Player host, PlayerService playerService, float duration){
        this.playerRoles = new HashMap<>();
        this.playerGuesses = new ArrayList<>();
        this.playerPoints = new HashMap<>();
        this.playerService = playerService;
        this.id = id;
        this.currentlyInRound = false;
        this.duration = duration;
        this.players = players;
        initializePoints();
        this.amountRounds = amountRounds;
        this.currentRoundNr = 0;
        this.nrPlayersGuessedCorrectly = 0;
        this.hostId = host.getId();
    }

    public int kickPlayer(Player player, WebSocketService ws) {
        Role role = playerRoles.get(player.getId());
        boolean host = Objects.equals(player.getId(), hostId);
        Long newHostId = (long)-1;
        playerRoles.remove(player.getId());
        playerPoints.remove(player);
        players.remove(player);
        boolean endGame = players.size() == 1;
        if(endGame){
            roundTimer.cancel();
        }
        if (players.size() < 1){
            roundTimer.cancel();
            timerStarted = false;
            return 1; //return 1 if game must be deleted
        }
        if (currentlyInRound) {
            if(role == Role.SPIER) {
                roundTimer.cancel();
                currentlyInRound = false;
                timerStarted = false;
                if (currentRoundNr + 1  > amountRounds){
                    endGame = true;
                }else {
                    nextRound();
                }
            }
        }

        if(host){
            hostId = players.get(0).getId();
            newHostId = hostId;
        }
        ws.sendMessageToSubscribers(
                "/topic/games/"+id+"/userDropOut",
                new DropOutMessage(player.getUsername(), role, host, newHostId.intValue(), endGame));
        return -1;
    }
    public void updatePointsIfGameEnded(){
        Player winner = players.get(0);
        for(Player player : players){
            if(playerPoints.get(player) > playerPoints.get(winner)) winner = player;
            player.setHighScore(player.getHighScore()+ playerPoints.get(player));
            player.setGamesPlayed(player.getGamesPlayed() + 1);
            playerService.saveFlushUser(player);
        }
        if(playerPoints.get(winner) > 0) {
            winner.setGamesWon(winner.getGamesWon() + 1);
            playerService.saveFlushUser(winner);
        }
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
                    currentlyInRound = false;
                    conG.handleEndRound(id, message, amountRounds, currentRoundNr);
                }
            }, (long)(duration * 60 * 1000));
        }
    }

    public void endRoundIfAllUsersGuessedCorrectly(GameStompController conG){
        if (this.nrPlayersGuessedCorrectly == (players.size() -1)){
            roundTimer.cancel();
            currentlyInRound = false;
            String message = "all guessed correctly";
            setRoundOverStatus(message);
            conG.handleEndRound(id, message, amountRounds, currentRoundNr);
        }
    }

    public Map<Player, Integer> getPlayerPoints(){
        return new HashMap<>(playerPoints);
    }
    private void initializePoints(){
        for(Player u : players){
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
    public void allocatePoints(Player player, Date guessTime){
        // formula to compute points: duration in seconds - seconds needed to guess
        int points = (int) ((duration *60) - (guessTime.getTime()- startTime.getTime())/1000);
        int pointsOfCurrentPlayer = playerPoints.get(player) + points;
        playerPoints.put(player, pointsOfCurrentPlayer);
        this.nrPlayersGuessedCorrectly++;
    }

    public boolean checkGuess(String guess){
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
        currentlyInRound = true;
        distributeRoles();
    }
    public int getId(){
        return this.id;
    }
    public void storeGuess(String name, String guess, int correct){
        Guess g = new Guess(name, guess, correct);
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

    public float getDuration(){
        return duration; //in minutes
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getHost(){
        for(Player player : players){
            if(player.getId() == hostId){
                return player;
            }
        }
        return null;
    }

    public PlayerService getPlayerService() {
        return playerService;
    }
}
