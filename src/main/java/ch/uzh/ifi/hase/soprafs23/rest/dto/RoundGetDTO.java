package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.wrappers.UserPointsWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoundGetDTO {
    private List<UserPointsWrapper> playerPoints;
    private String roundOverStatus;
    private String keyword;
    private Long hostId;
    private int currentRoundNr;

    public void setRoundOverStatus(String roundOverStatus){
        this.roundOverStatus = roundOverStatus;
    }

    public String getRoundOverStatus() {
        return roundOverStatus;
    }

    public void setKeyword(String keyword){
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setPlayerPoints(Map<Player, Integer> playerPoints){
        this.playerPoints = new ArrayList<>();
        for (Player u : playerPoints.keySet()) {
            this.playerPoints.add(new UserPointsWrapper(u.getUsername(), playerPoints.get(u), u.getProfilePicture()));
        }
        this.playerPoints.sort(UserPointsWrapper.compareByPoints());
    }

    public List<UserPointsWrapper> getPlayerPoints() {
        return this.playerPoints;
    }

    public void setHostId(Long hostId) { this.hostId = hostId;}

    public Long getHostId() { return hostId;}

    public int getCurrentRoundNr() {
        return currentRoundNr;
    }

    public void setCurrentRoundNr(int currentRoundNr) {
        this.currentRoundNr = currentRoundNr;
    }
}

