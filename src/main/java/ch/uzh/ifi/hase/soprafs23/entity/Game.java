package ch.uzh.ifi.hase.soprafs23.entity;
import java.util.List;

public class Game {

    private List<User> players;

    private User host;

    private Round[] rounds;
    private int amountRounds;
    private String googleMapsCoordinates;
    private int currentRound;

    public Game(List<User> players, User host, int amountRounds){
        this.players = players;
        this.host = host;
        this.amountRounds = amountRounds;
        this.rounds = new Round[amountRounds];
        this.currentRound = 0;
    }

    public void playRound(){
        //still need to think about that
    }

    public void storeCoordinates(String googleMapsCoordinates){
        this.googleMapsCoordinates = googleMapsCoordinates;
    }
}
