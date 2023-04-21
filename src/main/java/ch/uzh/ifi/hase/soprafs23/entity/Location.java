package ch.uzh.ifi.hase.soprafs23.entity;

public class Location { //if we don't want to store location in game clas probably not needed
    private double lat;
    private double lng;

    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
