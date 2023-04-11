package ch.uzh.ifi.hase.soprafs23.entity;
import java.sql.Time;
import java.util.List;

public class Round{
    private List<User> players;

    private User currentSpy;

    private String googleMapsCoordinates;

    private int duration;

    private Time startTime;

    private String keyword;

    private String color; //note c: needed?

    private List<String> guesses;

    public Round(){}

    public Round(List<User> players, String googleMapsCoordinates){
        this.players = players;
        this.googleMapsCoordinates = googleMapsCoordinates;
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
}
