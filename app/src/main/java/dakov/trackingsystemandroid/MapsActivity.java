package dakov.trackingsystemandroid;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import Contracts.ICallback;
import HTTP.HTTPData;
import HTTP.HTTPGetTask;
import HTTP.HTTPGetTaskBase;
import Helpers.LocationHelper;
import ViewModels.Coordinate;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ICallback {

    private GoogleMap mMap;
    private LatLng position;
    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationHelper = new LocationHelper(this);
        Intent intent = (Intent)getIntent().getExtras().get("IntentToKill");
        if(intent != null) {
            stopService(intent);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        ToolbarUtility.createToolbar(this, "Map");

        LinearLayout findMe = (LinearLayout)findViewById(R.id.find_me_button);
        findMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = locationHelper.getLastKnownLocation();

                LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(pos).title("Position"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos,14), 3000, null);
            }
        });

        final ICallback self = this;
        LinearLayout findPath = (LinearLayout) findViewById(R.id.find_path_button);
        findPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = locationHelper.getLastKnownLocation();

                LatLng destination = new LatLng(location.getLatitude(), location.getLongitude());
                String url =
                        "http://maps.googleapis.com/maps/api/directions/json?origin="
                                + position.latitude + "," + position.longitude + "&destination="
                                + destination.latitude + "," + destination.longitude + "&sensor=false";
                HTTPData data = new HTTPData();
                data.Type = "GET";
                data.Url = url;
                new HTTPGetTaskBase(self).execute(data);
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
        Coordinate coord = (Coordinate) getIntent().getSerializableExtra("Coord");
        // Add a marker in Sydney and move the camera
        LatLng pos = new LatLng(coord.Latitude, coord.Longitude);
        position = pos;
        mMap.addMarker(new MarkerOptions().position(pos).title("Position"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 14), 3000, null);
    }

    @Override
    public void execute(String result) throws JSONException {
        JSONObject resultJSON = new JSONObject(result);
        JSONArray routes = resultJSON.getJSONArray("routes");

        long distanceForSegment = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");

        JSONArray steps = routes.getJSONObject(0).getJSONArray("legs")
                .getJSONObject(0).getJSONArray("steps");

        final List<LatLng> lines = new ArrayList<LatLng>();

        for (int i = 0; i < steps.length(); i++) {
            String polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points");

            for (LatLng p : decodePolyline(polyline)) {
                lines.add(p);
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Polyline polylineToAdd = mMap.addPolyline(new PolylineOptions().addAll(lines).width(6).color(Color.CYAN));

            }
        });
    }

    @Override
    public void error(String error) {

    }

    private List<LatLng> decodePolyline(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();

        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }

        return poly;
    }
}
