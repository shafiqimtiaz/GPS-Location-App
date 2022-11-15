package com.example.gpslocationapp.views;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.gpslocationapp.R;
import com.example.gpslocationapp.controllers.GPSLocationRequestController;
import com.example.gpslocationapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private GPSLocationRequestController gpsLocationRequestController = new GPSLocationRequestController();
    private List<Location> savedLocations;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        savedLocations = gpsLocationRequestController.getAllGPSLocations();
        LatLng lastLocationPlaced = new LatLng(-34, 151);

//        if (savedLocations != null) {
        for (Location location : savedLocations) {
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latlng);
            markerOptions.title(String.format(Locale.CANADA, "Latitude: %.3f Longitude: %.3f", location.getLatitude(), location.getLongitude()));
            mMap.addMarker(markerOptions);
            lastLocationPlaced = latlng;
        }
//        }

//        MAP ZOOM but since the location updates at fixed interval,its glitchy
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocationPlaced, 12.0f));

        mMap.setOnMarkerClickListener(marker -> {
            Integer clicks = (Integer) marker.getTag();
            if (clicks == null) {
                clicks = 0;
            }
            clicks++;
            marker.setTag(clicks);
            Toast.makeText(MapsActivity.this,
                    "Marker " + marker.getTitle() + " was clicked " + marker.getTag() + " times",
                    Toast.LENGTH_SHORT).show();
            return false;
        });
    }
}