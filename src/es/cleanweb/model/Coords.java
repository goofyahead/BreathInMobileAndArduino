package es.cleanweb.model;

public class Coords {
    String latitude;
    String longitude;
    
    public Coords (String lat, String lon){
        this.latitude = lat;
        this.longitude = lon;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
