package com.example.gpslocationapp.controllers;

import android.location.Location;

import com.example.gpslocationapp.models.GPSLocationContainer;

import java.util.List;

public class GPSLocationRequestController {

    private static GPSLocationContainer gpsLocationContainer;

    public GPSLocationRequestController() {
        gpsLocationContainer = GPSLocationContainer.getInstance();
    }

    public Location getLatestGPSLocation() {
        return gpsLocationContainer.getLatestGPSLocation();
    }

    public List<Location> getAllGPSLocations() {
        return gpsLocationContainer.getAllGPSLocations();
    }
}
