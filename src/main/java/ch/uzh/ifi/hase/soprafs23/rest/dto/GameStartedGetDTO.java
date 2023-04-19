package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class GameStartedGetDTO {
    private String event = "started";
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getEvent() {return this.event;}
}

