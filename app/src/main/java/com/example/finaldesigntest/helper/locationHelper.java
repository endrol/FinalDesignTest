package com.example.finaldesigntest.helper;

import com.example.finaldesigntest.helper.MyApplication;

public class locationHelper {
    private String Latitude, Longitude, city;


    private boolean checkLocation = false;

    public boolean isCheckLocation() {
        return checkLocation;
    }

    public void setCheckLocation(boolean checkLocation) {
        this.checkLocation = checkLocation;
    }

    public String getCity() {
        return city;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
