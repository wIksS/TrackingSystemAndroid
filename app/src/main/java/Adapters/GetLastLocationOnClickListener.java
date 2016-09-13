package Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import Authentication.IdentitySingleton;
import Contracts.ICallback;
import HTTP.HTTPData;
import HTTP.HTTPGetTask;
import Helpers.ErrorHandler;
import Helpers.Notifier;
import ViewModels.Account;
import ViewModels.Coordinate;
import dakov.trackingsystemandroid.MapsActivity;
import dakov.trackingsystemandroid.R;

/**
 * Created by Viktor on 7.9.2016 г..
 */
public class GetLastLocationOnClickListener implements View.OnClickListener, ICallback {
    private String userName;
    private Context context;
    private IdentitySingleton identity = IdentitySingleton.getInstance();

    public GetLastLocationOnClickListener(String userName, Context context) {
        this.userName = userName;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Account account = identity.getAccount();
        HTTPData data = new HTTPData();
        data.Type = "GET";
        data.Url = "api/Location/" + this.userName;
        data.Headers = new Pair[]{new Pair("Authorization", "Bearer " + account.access_token)};

        new HTTPGetTask(this).execute(data);
    }

    @Override
    public void execute(final String result) {
        if(result != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject location = null;
                    Coordinate coord = new Coordinate();
                    try {
                        location = new JSONObject(result);
                        coord.Latitude = location.getDouble("Latitude");
                        coord.Longitude = location.getDouble("Longitude");
                        Intent mapsIntent = new Intent(context, MapsActivity.class);
                        mapsIntent.putExtra("Coord", coord);
                        ((Activity) context).startActivity(mapsIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Notifier.notify(((Activity)context),"Location", "No location has been registered for user",
                                R.drawable.ic_error_outline_black_24dp);
                    }
                }
            });
        }
    }

    @Override
    public void error(final String error) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ErrorHandler.notifyError(error, ((Activity) context));
            }
        });
    }
}
