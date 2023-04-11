package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class LobbyGetDTO {
    private int id;
    private int accessCode;
    private int hostId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(int accessCode) {
        this.accessCode = accessCode;
    }

    public void setHostId(int hostId) { this.hostId = hostId;}

    public int getHostId() { return hostId;}
}

