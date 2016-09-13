package dakov.trackingsystemandroid;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;

import org.json.JSONException;

import Authentication.IdentitySingleton;
import Contracts.ICallback;
import HTTP.HTTPData;
import HTTP.HTTPGetTask;
import Helpers.Notifier;
import ViewModels.Account;

public class SettingsActivity extends AppCompatActivity implements ICallback {
    private IdentitySingleton identity = IdentitySingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final EditText minValue = (EditText) findViewById(R.id.min_distance);
        final EditText maxValue = (EditText) findViewById(R.id.max_distance);
        final TextView currentValue = (TextView) findViewById(R.id.current_distance);
        final RangeBar rangeBar = (RangeBar) findViewById(R.id.rangebar);
        String tickValue = minValue.getText().toString();
        rangeBar.setTickStart(Float.parseFloat(tickValue));
        tickValue = maxValue.getText().toString();
        rangeBar.setTickEnd(Float.parseFloat(tickValue));

        Button button = (Button) findViewById(R.id.button_change_distance);
        final ICallback self = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Account account = identity.getAccount();
                HTTPData data = new HTTPData();
                data.Type = "POST";
                data.Url = "api/Group/ChangeGroupDistance?newDistance=" + currentValue.getText();
                data.Headers =new Pair[] {new Pair("Authorization","Bearer " + account.access_token)};

                new HTTPGetTask(self).execute(data);
            }
        });

        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                String value = rangeBar.getRightPinValue();
                currentValue.setText(value);
                try {
                    Float left = Float.parseFloat(minValue.getText().toString());
                    //rangeBar.setTickStart(left);

                } catch (Exception e) {
                    Log.e("Cast", e.toString());
                }
            }
        });

        minValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Float value = Float.parseFloat(minValue.getText().toString());
                    if (value < rangeBar.getRight()) {
                        rangeBar.setTickStart(value);
                    }
                } catch (Exception e) {
                    Log.e("Cast", e.toString());
                }
            }
        });

        maxValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Float value = Float.parseFloat(maxValue.getText().toString());
                    if (value > rangeBar.getLeft()) {
                        rangeBar.setTickEnd(value);
                    }
                } catch (Exception e) {
                    Log.e("Cast", e.toString());
                }
            }
        });

        ToolbarUtility.createToolbar(this, "Settings");
    }

    @Override
    public void execute(String result) throws JSONException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Notifier.notify(SettingsActivity.this,"Sucess", "Successfully changed group tracking distance", R.drawable.ic_error_outline_black_24dp);
            }
        });
    }

    @Override
    public void error(String error) {
        Log.e("e", error.toString());
    }
}
