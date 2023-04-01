package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private int accessCode;

    @ElementCollection
    private List<User> users;

    @Column (nullable = false)
    private User host;

    @Column
    private boolean full;

    @Column
    private int amountRounds;

    public void setAccessCode(int accessCode){ this.accessCode = accessCode; }
    public int getAccessCode(){ return accessCode;}

    public void setUsers(List<User> users){ this.users = users; }
    public List<User> getUsers(){ return users; }

    public void setHost(User host){ this.host = host; }
    public User getHost(){ return host;}

    public void setFull(boolean full){this.full = full;}
    public boolean getFull(){return full;}

    public void setAmountRounds(int amountRounds){this.amountRounds = amountRounds;}
    public int getAmountRounds(){return amountRounds;}
}
