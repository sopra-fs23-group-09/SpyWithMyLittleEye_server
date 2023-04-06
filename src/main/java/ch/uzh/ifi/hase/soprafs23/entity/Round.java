package ch.uzh.ifi.hase.soprafs23.entity;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.List;

@Entity
@Table(name = "ROUND")
public class Round implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ElementCollection
    private List<User> users;

    @Column (nullable = false)
    private User currentSpy;

    @Column (nullable = false)
    private String googleMapsCoordinates;

    @Column (nullable = false)
    private int duration;

    @Column(nullable = false)
    private Time startTime;

    @Column(nullable = false)
    private String keyword;

    //TODO: check if this is correct in class diagram because we do not need to store the guesses?
    // if needed add get set methods (but we check guesses in the service and should probably only store
    // which users guessed correctly with the time they needed?
    // @Column
    // private ... guesses

    @ElementCollection
    private List<String> guesses;

    // note c: to check if everyone has guessed correctly => could also be "nrRemainingGuessers" and set equal to the number of players,
    // and reduced everytime someone guessed correctly, after each guess, it would be checked if this is 0 ?!
    @Column(nullable = false)
    private int nrCorrectGuesses;

    @ElementCollection
    private List<String> hints;

    @Column
    private String color;

    public void setUsers(List<User> users){ this.users = users; }
    public List<User> getUsers(){ return users; }

    public void setCurrentSpy(User currentSpy){ this.currentSpy = currentSpy; }
    public User getCurrentSpy(){ return currentSpy;}

    public void setGoogleMapsCoordinates(String googleMapsCoordinates){ this.googleMapsCoordinates = googleMapsCoordinates;}
    public String getGoogleMapsCoordinates(){return googleMapsCoordinates;}

    public void setDuration(int duration){this.duration = duration;}
    public int getDuration(){return duration;}

    public void setStartTime(Time startTime){this.startTime = startTime;}
    public Time getStartTime(){return startTime;}

    public void setKeyword(String keyword){this.keyword = keyword;}
    public String getKeyword(){return keyword;}

    public void setHints(List<String> hints){this.hints = hints;}
    public List<String> getHints(){return hints;}

    public void setColor(String color){this.color = color;}
    public String getColor(){return color;}

    public void setNrCorrectGuesses(int nrCorrectGuesses) { //note c: rather "increaseNrCorrectGuesses"?!
        this.nrCorrectGuesses = nrCorrectGuesses;
    }

    public int getNrCorrectGuesses() {
        return nrCorrectGuesses;
    }
}
