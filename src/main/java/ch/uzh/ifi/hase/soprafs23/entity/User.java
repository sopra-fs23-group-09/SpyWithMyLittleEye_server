package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column
    private Date birthday;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(unique = true)
    private String token;

    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private Date creationDate;

    @Column
    private String profilePicture;

    @Column
    private int highScore;

    @Column
    private int gamesWon;

    @Column
    private int gamesPlayed;

    @Column
    private int lobbyID;

    public User() {
    }

    public User(User u) {
        id = u.id;
        password = u.password;
        birthday = u.birthday;
        username = u.username;
        token = u.token;
        status = u.status;
        creationDate = u.creationDate;
        profilePicture = u.profilePicture;
        highScore = u.highScore;
        gamesPlayed = u.gamesWon;
        lobbyID = u.lobbyID;
    }

    public void setProfilePicture(String profilePicture){this.profilePicture = profilePicture;} //M4
    public String getProfilePicture(){return profilePicture;} //M4

    public void setHighScore(int highScore){this.highScore = highScore;}
    public int getHighScore(){return highScore;}

    public void setGamesWon(int gamesWon) {this.gamesWon = gamesWon;} //M4 ?
    public int getGamesWon(){return gamesWon;}

    public void setGamesPlayed(int gamesPlayed){this.gamesPlayed = gamesPlayed;} //M4?
    public int getGamesPlayed(){return gamesPlayed;}

    public void setLobbyID(int lobbyID){this.lobbyID = lobbyID;}
    public int getLobbyID(){return lobbyID;}

    public void setBirthday(Date birthday){
        this.birthday = birthday;
    }
    public Date getBirthday(){
        return birthday;
    }

    public void setCreationDate(Date creationDate){ this.creationDate = creationDate;}
    public Date getCreationDate(){ return creationDate;}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getPassword() {return password;}
    public void setPassword(String name) {this.password = name;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getToken() {return token;}
    public void setToken(String token) {this.token = token;}

    public UserStatus getStatus() {return status;}
    public void setStatus(UserStatus status) {this.status = status;}

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }else if((obj instanceof User)){
            return this.id.equals(((User) obj).id);
        }
        return false;
    }
}
