package ch.uzh.ifi.hase.soprafs23.stomp.dto;

public class SpiedObjectOut {
    private Location location;
    private String color;
    private String startTime;
    private float duration;


    public SpiedObjectOut(Location location,String color, String startTime, float duration){
        this.location = location;
        this.color = color;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getColor() {
        return color;
    }

    public Location getLocation() {
        return location;
    }


    public String getStartTime() {
        return startTime;
    }

    public float getDuration() {
        return duration;

    }

    @Override
    public String toString() {
        return String.format("%s [color=%s, location=%s]", getClass().getName(), color, location);

    }
}