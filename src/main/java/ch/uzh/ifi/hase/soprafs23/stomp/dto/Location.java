package ch.uzh.ifi.hase.soprafs23.stomp.dto;

public class Location {
    private final double lat;
    private final double lng;

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

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj instanceof Location o) {
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
