package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.wrappers.UserPointsWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoundGetDTO {
    private List<UserPointsWrapper> points;
    private String roundOverStatus;
    private String keyword;

    public void setRoundOverStatus(String gameOverStatus){
        this.roundOverStatus = gameOverStatus;
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

    public void setPoints(Map<User, Integer> playerPoints){
        points = new ArrayList<>();
        for (User u : playerPoints.keySet()) {
            points.add(new UserPointsWrapper(u.getUsername(), playerPoints.get(u)));
        }
        points.sort(UserPointsWrapper.compareByPoints());
    }

    public List<UserPointsWrapper> getPoints() {
        return points;
    }
}

