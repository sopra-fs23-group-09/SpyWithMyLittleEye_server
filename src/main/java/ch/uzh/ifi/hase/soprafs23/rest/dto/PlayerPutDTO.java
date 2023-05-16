package ch.uzh.ifi.hase.soprafs23.rest.dto;
import java.util.Date;

public class PlayerPutDTO {
    private String username;
    private String password;
    private Date birthday;
    private String profilePicture;

    public Date getBirthday() {
        return birthday;
    }
    public void setBirthday(Date birthday){
        this.birthday = birthday;
    }

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getProfilePicture(){
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture){
        this.profilePicture = profilePicture;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
