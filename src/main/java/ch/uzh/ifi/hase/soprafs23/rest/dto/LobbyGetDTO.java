package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.entity.User;

import java.util.ArrayList;
import java.util.List;

public class LobbyGetDTO {
    private String event = "joined";
    private int id;
    private int accessCode;
    private int hostId;

    private int amountRounds;

    private List<String> playerNames;

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

    public void setPlayerNames(List<User> playerNames) {
        this.playerNames = new ArrayList<>(playerNames.size());
        for(User u : playerNames) {
            this.playerNames.add(u.getUsername());
        }
    }

    public void setAmountRounds(int amountRounds) {
        this.amountRounds = amountRounds;
    }

    public int getAmountRounds() { return this.amountRounds; }

    public List<String> getPlayerNames() {
        return playerNames;
    }
    public String getEvent() {return this.event;}
}

