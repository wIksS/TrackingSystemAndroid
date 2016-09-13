package dakov.trackingsystemandroid;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appyvet.rangebar.RangeBar;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import Authentication.IdentitySingleton;
import Contracts.ICallback;
import HTTP.HTTPData;
import HTTP.HTTPGetTask;
import ViewModels.Account;

public class CreateEventActivity extends AppCompatActivity  implements ICallback{
    LatLng latLng = null;
    private IdentitySingleton identity = IdentitySingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        ToolbarUtility.createToolbar(this,"Create event");
        latLng = (LatLng) getIntent().getExtras().get("EventLatLng");
        Button button = (Button) findViewById(R.id.button_create_event);
        final ICallback self = this;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText message = (EditText) findViewById(R.id.event_message);
                RangeBar minutes = (RangeBar) findViewById(R.id.minutes_to_event);
                RangeBar hours = (RangeBar) findViewById(R.id.hours_to_event);

                int minutesInt = Integer.parseInt(minutes.getRightPinValue());
                int hoursInt = Integer.parseInt(hours.getRightPinValue());
                String text = message.getText().toString();

                Account account = identity.getAccount();
                HTTPData data = new HTTPData();
                data.Type = "POST";
                data.Url = "api/Events";
                data.Headers =new Pair[] {new Pair("Authorization","Bearer " + account.access_token)};
                data.Body =new Pair[] {
                        new Pair("Message",text),
                        new Pair("Latitude",Double.toString(latLng.latitude)),
                        new Pair("Longitude",Double.toString(latLng.longitude)),
                };

                new HTTPGetTask(self).execute(data);
            }
        });
    }

    @Override
    public void execute(String result) throws JSONException {

    }

    @Override
    public void error(String error) {

    }
}
