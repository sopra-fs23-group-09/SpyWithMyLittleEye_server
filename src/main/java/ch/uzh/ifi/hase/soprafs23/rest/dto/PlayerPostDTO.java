package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class PlayerPostDTO {

    private String password;

    private String username;
    private String profilePicture;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicture(){ return profilePicture;}
    public void setProfilePicture(String profilePicture){ this.profilePicture = profilePicture;}
}
