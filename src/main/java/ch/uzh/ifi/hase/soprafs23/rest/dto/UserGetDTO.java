package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class UserGetDTO {

    private Long id;
    private String username;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date creationDate;
    private UserStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private String profilePicture;

    private int gamesPlayed;
    private int gamesWon;

    private int highScore;

    public String getProfilePicture(){
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture){
        this.profilePicture = profilePicture;
    }

    public int getHighScore(){return highScore;}
    public void setHighScore(int highScore){this.highScore = highScore;}

    public int getGamesPlayed(){return gamesPlayed;}
    public void setGamesPlayed(int gamesPlayed){this.gamesPlayed = gamesPlayed;}
    public int getGamesWon(){return gamesWon;}
    public void setGamesWon(int gamesWon){this.gamesWon = gamesWon;}
    public Date getBirthday() {
        return birthday;
    }
    public void setBirthday(Date birthday){
        this.birthday = birthday;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Date getCreationDate(){ return creationDate;}
    public void setCreationDate(Date creation_date){this.creationDate = creation_date;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
