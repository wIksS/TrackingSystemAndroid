package dakov.trackingsystemandroid;

import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import Helpers.LocationHelper;

public class EventMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        locationHelper = new LocationHelper(this);
        mapFragment.getMapAsync(this);
        ToolbarUtility.createToolbar(this, "Create event");
        LinearLayout findMe = (LinearLayout) findViewById(R.id.find_me_button);
        findMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = locationHelper.getLastKnownLocation();
                if (location == null) {
                    Intent gpsOptionsIntent = new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsOptionsIntent);
                }
                else {
                    LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(pos).title("Position"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 14), 3000, null);
                }
            }
        });
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

        mMap.setOnMapClickListener(this);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onMapClick(final LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(EventMapActivity.this, CreateEventActivity.class);
                mainIntent.putExtra("EventLatLng", latLng);
                EventMapActivity.this.startActivity(mainIntent);
                EventMapActivity.this.finish();
            }
        }, 1000);
    }
}
