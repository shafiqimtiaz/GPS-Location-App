package com.example.gpslocationapp.models;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class GPSLocationContainer {
    private static GPSLocationContainer gpsLocationContainer;
    private static List<Location> gpsLocationList;

    public GPSLocationContainer() {
        gpsLocationList = new ArrayList<>();
    }

    public static GPSLocationContainer getInstance() {
        if (gpsLocationContainer == null) {
            gpsLocationContainer = new GPSLocationContainer();
        }
        return gpsLocationContainer;
    }

    public synchronized Location getLatestGPSLocation() {
//        if (gpsLocationList.size() <= 0) return null;

        Location latestGPSLocation = gpsLocationList.get(gpsLocationList.size() - 1);
        return new Location(latestGPSLocation);
    }

    public synchronized List<Location> getAllGPSLocations() {
//        if (gpsLocationList.size() <= 0) return null;

        List<Location> gpsLocationCopy = new ArrayList<>();
        for (Location location : gpsLocationList) {
            gpsLocationCopy.add(new Location(location));
        }
        return gpsLocationCopy;
    }

    public synchronized void addGPSLocation(Location location) {
        if (location == null) throw new NullPointerException();
        gpsLocationList.add(location);
    }

    public synchronized void reset() {
        gpsLocationList.clear();
    }

}
