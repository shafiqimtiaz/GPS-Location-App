package com.example.gpslocationapp.views;
//https://www.cs.dartmouth.edu/~campbell/cs65/lecture18/lecture18.html ---> for more help

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.gpslocationapp.R;
import com.example.gpslocationapp.controllers.GPSLocationRequestController;
import com.example.gpslocationapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final GPSLocationRequestController gpsLocationRequestController = new GPSLocationRequestController();
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private List<Location> savedLocations;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("GPS_BUNDLE");
        savedLocations = (List<Location>) args.getSerializable("GPS_DATA");
//        Log.i("MAP LOC", savedLocations.toString());
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

        // init start and end location
        LatLng startLocation, endLocation = new LatLng(-34, 151);

        //init trail polylines
        PolylineOptions polylineOptions = new PolylineOptions();

        if (savedLocations != null) {

            //get first savedLocation and
            startLocation = new LatLng(savedLocations.get(0).getLatitude(), savedLocations.get(0).getLongitude());
            mMap.addMarker((new MarkerOptions()).position(startLocation)
                    .title(String.format(Locale.CANADA, "Latitude: %.3f Longitude: %.3f",
                            startLocation.latitude, startLocation.longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            for (Location location : savedLocations) {
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
//                Log.i("LAT_LNG", String.valueOf(latlng));

                polylineOptions.add(latlng).color(Color.BLUE).width(5).geodesic(true);
                endLocation = latlng;
            }

            // add marker for the last saved location
            mMap.addMarker((new MarkerOptions()).position(endLocation)
                    .title(String.format(Locale.CANADA, "Latitude: %.3f Longitude: %.3f",
                            endLocation.latitude, endLocation.longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }

        mMap.addPolyline(polylineOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(endLocation, 12.0f));

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

    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}