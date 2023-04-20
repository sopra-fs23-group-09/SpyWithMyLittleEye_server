package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Role;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Round{
    private List<User> players;

    private User currentSpy;

    private String googleMapsCoordinates;

    private int duration;

    private String keyword;

    private String color; //note c: needed?

    private List<String> guesses; //note c: needed? n: stimmt, glaub das brauchen wir nicht

    private Map<Long, Role> playerRoles;
    private Date startTime;

    public Round(){}

    public Round(List<User> players, int currentRound){
        this.players = players;
        this.playerRoles = new HashMap<>();
        distributeRoles(currentRound);
        this.keyword = "unicorn"; // TO-DO: DELETE, FOR TESTING PURPOSES ONLY
        this.startTime = new Date(); //TO-DO: DELETE, FOR TESTING PURPOSES ONLY
    }

    /**
     * chooses the spier with respect to the number of the current round
     * @param currentRound
     */
    private void distributeRoles(int currentRound){
        for(int i = 0; i < players.size(); i++){
            if(i == currentRound % players.size()){
                playerRoles.put(players.get(i).getId(), Role.SPIER);
            }else{
                playerRoles.put(players.get(i).getId(), Role.GUESSER);
            }
        }
    }

    public Role getRole(Long playerId){
        return playerRoles.get(playerId);
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void startRound() {
        this.startTime = new Date();
    }

    public Date getStartTime() {
        return startTime;
    }
}
