package ch.uzh.ifi.hase.soprafs23.stomp.dto;


public class Location { // if we don't want to store location in game clas probably not needed
    private double lat;
    private double lng;


    public Location() {
    }

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

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;
        if (obj instanceof Location) {
            Location o = (Location) obj;

            return o.lat == lat && o.lng == lng;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int latHash = Double.hashCode(lat);
        int lngHash = Double.hashCode(lng);
        return 12 * latHash + lngHash;
    }
}
