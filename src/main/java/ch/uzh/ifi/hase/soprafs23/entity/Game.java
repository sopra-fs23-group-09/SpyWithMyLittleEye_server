package ch.uzh.ifi.hase.soprafs23.entity;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "GAME")
public class Game implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ElementCollection
    private List<User> users;

    @Column (nullable = false)
    private User host;

    @Column
    private int remainingRounds;

    @Column
    private Round round;



    public void setUsers(List<User> users){ this.users = users; }
    public List<User> getUsers(){ return users; }

    public void setHost(User host){ this.host = host; }
    public User getHost(){ return host;}

    public void setRemainingRounds(int remainingRounds){ this.remainingRounds = remainingRounds;}
    public int getRemainingRounds(){return remainingRounds;}

    public void setRound(Round round){this.round = round;}
    public Round getRound(){return round;}
}
