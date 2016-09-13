package dakov.trackingsystemandroid;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Timer;
import java.util.TimerTask;

import Authentication.IdentitySingleton;
import Contracts.ICallback;
import HTTP.HTTPData;
import HTTP.HTTPGetTask;
import Helpers.ErrorHandler;
import Helpers.LocationHelper;
import ViewModels.Account;
import ViewModels.Coordinate;
import ViewModels.User;

/**
 * Created by Viktor on 9.9.2016 г..
 */
public class LocationService extends Service implements ICallback {
    private IdentitySingleton identity = IdentitySingleton.getInstance();
    private LocationHelper locationHelper;
    private Timer timer;

    public LocationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.locationHelper = new LocationHelper(this);
        LocationListener locationListener = new MyLocationListener();
        final boolean isPermitted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED;
        if (isPermitted) {
            return;
        }
        this.locationHelper.getManager().requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        final Handler h = new Handler();
        final int delay = 3000; //milliseconds
        final Service self = this;
        final ICallback selfCallback = this;

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(self, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(self, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Location location = locationHelper.getLastKnownLocation();
                if (location == null) {
                }
                else {
                    Intent localIntent =
                            new Intent("LocationIntent");

                    String longitude = "Longitude: " + location.getLongitude();
                    String latitude = "Latitude: " + location.getLatitude();
                    Account account = identity.getAccount();
                    final HTTPData data = new HTTPData();
                    data.Type = "POST";
                    data.Url = "api/Location/AddLocation";
                    data.Headers = new Pair[]{new Pair("Authorization", "Bearer " + account.access_token)};
                    data.Body = new Pair[]{
                            new Pair("Latitude", Double.toString(location.getLatitude())),
                            new Pair("Longitude", Double.toString(location.getLongitude()))
                    };
                    new HTTPGetTask(selfCallback).execute(data);
                }
            }
        }, 0, 10000);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void execute(String response) throws JSONException {
        if (response != null) {
//            Gson g = new Gson();
//            Type type = new TypeToken<Coordinate[]>() {
//            }.getType();
           // Coordinate[] coordinates = g.fromJson(response, type);
            Coordinate coord = new Coordinate();
            JSONArray coordinates = new JSONArray(response);
            JSONObject coordinate = coordinates.getJSONObject(0).getJSONObject("Coordinate");
            coord.Latitude = coordinate.getDouble("Latitude");
            coord.Longitude = coordinate.getDouble("Longitude");
            JSONObject user = coordinates.getJSONObject(0).getJSONObject("User");
            coord.User = new Account();
            coord.User.UserName = user.getString("UserName");

            Intent localIntent = new Intent("LocationIntent") ;
            localIntent.putExtra("Coordinate", coord);

            LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(localIntent);
        }
    }

    @Override
    public void error(final String error) {
        Intent localIntent = new Intent("LocationIntent") ;

        LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(localIntent);
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location loc) {
            Intent localIntent = new Intent("LocationIntent") ;

            LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(localIntent);

            String longitude = "Longitude: " + loc.getLongitude();
            String latitude = "Latitude: " + loc.getLatitude();
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

}