package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Internal Player Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "PLAYER")
public class Player implements Serializable {

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
    private PlayerStatus status;

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

    public void setProfilePicture(String profilePicture){this.profilePicture = profilePicture;}
    public String getProfilePicture(){return profilePicture;}

    public void setHighScore(int highScore){this.highScore = highScore;}
    public int getHighScore(){return highScore;}

    public void setGamesWon(int gamesWon) {this.gamesWon = gamesWon;}
    public int getGamesWon(){return gamesWon;}

    public void setGamesPlayed(int gamesPlayed){this.gamesPlayed = gamesPlayed;}
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

    public PlayerStatus getStatus() {return status;}
    public void setStatus(PlayerStatus status) {this.status = status;}

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }else if((obj instanceof Player)){
            return this.id.equals(((Player) obj).id);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[@%s]", username, Integer.toHexString(super.hashCode()));
    }
}
