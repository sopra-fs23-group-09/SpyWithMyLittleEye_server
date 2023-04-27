package ch.uzh.ifi.hase.soprafs23.stomp.dto;

public class SpiedObjectIn {

    private String object;
    private String color;
    private Location location;

    public SpiedObjectIn() {
    }

    public SpiedObjectIn(String object, String color, Location location) {
        this.object = object;
        this.color = color;
        this.location = location;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
