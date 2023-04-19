package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.entity.User;

import java.util.ArrayList;
import java.util.List;

public class LobbyStartedGetDTO {
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

