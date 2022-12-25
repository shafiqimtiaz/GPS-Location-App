package com.example.gpslocationapp.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.gpslocationapp.R;
import com.example.gpslocationapp.models.GPSLocationContainer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static int DEFAULT_UPDATE_INTERVAL = 10;
    private static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 100;
    private final GPSLocationContainer gpsLocationContainer = new GPSLocationContainer();
    private final LocationRequest locationRequest = new LocationRequest();
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address, tv_locationCount, tv_interval;
    Switch sw_updates, sw_gps;
    Button btn_resetCounter, btn_showMap, btn_setInterval;
    Spinner spinner_interval;
    Location currentLocation;
    String currentAccuracy = String.valueOf(DEFAULT_UPDATE_INTERVAL);
    private List<Location> savedLocations;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        sw_updates = findViewById(R.id.sw_updates);
        tv_locationCount = findViewById(R.id.tv_locationCount);
        btn_showMap = findViewById(R.id.btn_showMap);
        btn_resetCounter = findViewById(R.id.btn_resetCounter);
        tv_interval = findViewById(R.id.tv_interval);
        btn_setInterval = findViewById(R.id.btn_setInterval);
        spinner_interval = findViewById(R.id.spinner_interval);

        locationRequest.setInterval(1000L * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000L * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Array of Months acting as a data pump
        String[] objects = {"2", "5", "10", "20", "40", "60"};

        //      Generate the drop down list for the spinners.
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, objects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_interval.setAdapter(adapter);

        showGPSAccuracy(spinner_interval, adapter);

        btn_setInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gpsAcc = spinner_interval.getSelectedItem().toString();
                DEFAULT_UPDATE_INTERVAL = Integer.parseInt(gpsAcc);
//                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            }
        });


        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIvalues(locationResult.getLastLocation());
                Location newLocation = new Location("");

                newLocation.setLatitude(locationResult.getLastLocation().getLatitude());
                newLocation.setLongitude(locationResult.getLastLocation().getLongitude());

                gpsLocationContainer.addGPSLocation(newLocation);
//                Log.i("SAVED LOCATIONS", gpsLocationContainer.getAllGPSLocations().toString());
            }
        };

        btn_showMap.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("GPS_DATA", (ArrayList<? extends Parcelable>) savedLocations);
            i.putExtra("GPS_BUNDLE", bundle);
            startActivity(i);
        });

        sw_gps.setOnClickListener(view -> {
            if (sw_gps.isChecked()) {
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                tv_sensor.setText("GPS Sensors");
            } else {
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                tv_sensor.setText("Towers + WIFI");
            }
        });

        sw_updates.setOnClickListener(view -> {
            if (sw_updates.isChecked()) {
                startLocationUpdates();
            } else {
                stopLocationUpdates();
            }
        });

        btn_resetCounter.setOnClickListener(view -> {
            gpsLocationContainer.reset();
            tv_locationCount.setText("0");
        });

        updateGPS();
    }

    private void displayValue(Spinner spinner, ArrayAdapter<CharSequence> adapter, String value) {
        int spinnerPosition = adapter.getPosition(value);
        spinner.setSelection(spinnerPosition);
    }

    private void showGPSAccuracy(Spinner gpsAccuracyView, ArrayAdapter<CharSequence> adapter) {
        displayValue(gpsAccuracyView, adapter, currentAccuracy);
    }

    private void stopLocationUpdates() {
        tv_updates.setText("Location is NOT being tracked");
        tv_lat.setText("Not tracking location");
        tv_lon.setText("Not tracking location");
        tv_speed.setText("Not tracking location");
        tv_address.setText("Not tracking location");
        tv_accuracy.setText("Not tracking location");
        tv_altitude.setText("Not tracking location");
        tv_sensor.setText("Not tracking location");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLocationUpdates() {
        tv_updates.setText("Not tracking location");
        checkPermission();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, "Need permission to run", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

        }
    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location == null) {
                    tv_updates.setText("Check GPS/Internet connection");
                } else {
                    tv_updates.setText("On");
                    updateUIvalues(location);
                    currentLocation = location;
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updateUIvalues(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()) {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        } else {
            tv_altitude.setText("Not Available");
        }

        if (location.hasSpeed()) {
            tv_speed.setText(String.valueOf(location.getSpeed()));
        } else {
            tv_speed.setText("Not Available");
        }

        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addressList.get(0).getAddressLine(0));
        } catch (Exception e) {
            tv_address.setText("Unable to fetch address");
        }

        savedLocations = gpsLocationContainer.getAllGPSLocations();
        if (savedLocations != null) {
            tv_locationCount.setText(Integer.toString(savedLocations.size()));
        }
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }
}