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

    private List<String> guesses;

    public Round(List<User> players, String googleMapsCoordinates){
        this.players = players;
        this.googleMapsCoordinates = googleMapsCoordinates;
    }

    public void start(){

    }
}
