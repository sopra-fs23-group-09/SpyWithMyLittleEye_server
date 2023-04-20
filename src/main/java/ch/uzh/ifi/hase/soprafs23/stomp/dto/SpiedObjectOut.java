package ch.uzh.ifi.hase.soprafs23.stomp.dto;

import ch.uzh.ifi.hase.soprafs23.entity.Location;

public class SpiedObjectOut {
    private Location location;
    private String color;


    public SpiedObjectOut(Location location,String color){
        this.location = location;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public Location getLocation() {
        return location;
    }
}