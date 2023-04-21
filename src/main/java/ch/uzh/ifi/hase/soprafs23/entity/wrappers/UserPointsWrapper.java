package ch.uzh.ifi.hase.soprafs23.entity.wrappers;

import java.util.Comparator;

public class UserPointsWrapper {

    private String username;

    private int points;

    public UserPointsWrapper(String username, int points) {
        this.username = username;
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public int getPoints() {
        return points;
    }

    public static Comparator<UserPointsWrapper> compareByUsername() {
        return Comparator.comparing(o -> o.username);
    }

    public static Comparator<UserPointsWrapper> compareByPoints() {
        return (o1, o2) -> o2.points - o1.points;
    }
}
