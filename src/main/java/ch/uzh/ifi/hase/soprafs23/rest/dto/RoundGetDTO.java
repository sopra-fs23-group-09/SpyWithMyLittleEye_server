package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.wrappers.UserPointsWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoundGetDTO {
    private List<UserPointsWrapper> playerPoints;
    private String roundOverStatus;
    private String keyword;

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

    public void setPlayerPoints(Map<User, Integer> playerPoints){
        this.playerPoints = new ArrayList<>();
        for (User u : playerPoints.keySet()) {
            this.playerPoints.add(new UserPointsWrapper(u.getUsername(), playerPoints.get(u)));
        }
        this.playerPoints.sort(UserPointsWrapper.compareByPoints());
    }

    public List<UserPointsWrapper> getPlayerPoints() {
        return this.playerPoints;
    }
}

