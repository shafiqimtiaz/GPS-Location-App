package com.example.gpslocationapp.models;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class GPSLocationContainer {
    private static GPSLocationContainer gpsLocationContainer;
    private static List<Location> gpsLocation;

    public GPSLocationContainer() {
        gpsLocation = new ArrayList<Location>();
    }

    public static GPSLocationContainer getInstance() {
        if (gpsLocationContainer == null) {
            gpsLocationContainer = new GPSLocationContainer();
        }
        return gpsLocationContainer;
    }

    public synchronized Location getLatestGPSLocation() {
        if (gpsLocation.size() <= 0) return null;

        Location latestGPSLocation = gpsLocation.get(gpsLocation.size() - 1);
        return new Location(latestGPSLocation);
    }

    public synchronized List<Location> getAllGPSLocations() {
        if (gpsLocation.size() <= 0) return null;

        List<Location> gpsLocationCopy = new ArrayList<>();
        for (Location location : gpsLocation) {
            gpsLocationCopy.add(new Location(location));
        }
        return gpsLocationCopy;
    }

    public synchronized void addGPSLocation(Location lcoation) {
        if (lcoation == null) throw new NullPointerException();
        gpsLocation.add(lcoation);
    }

    public synchronized void reset() {
        gpsLocation.clear();
    }

}
