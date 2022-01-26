package com.mambo.rafiki.data.entities;

public class Location {

    private String longitude;
    private String latitude;

    public Location() {
        // empty constructor for firestore
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
