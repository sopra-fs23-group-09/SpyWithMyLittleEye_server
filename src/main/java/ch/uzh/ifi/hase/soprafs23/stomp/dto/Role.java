package ch.uzh.ifi.hase.soprafs23.stomp.dto;

public class Role {

    private int userId;
    private String role;

    public Role(int userId, String role){
        this.userId = userId;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}
