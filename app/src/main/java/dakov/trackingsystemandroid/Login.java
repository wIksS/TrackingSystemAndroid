package dakov.trackingsystemandroid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import Authentication.IdentitySingleton;
import Contracts.ICallback;
import HTTP.HTTPData;
import HTTP.HTTPGetTask;
import HTTP.HTTPUtility;
import Helpers.ErrorHandler;
import ViewModels.Account;

public class Login extends AppCompatActivity implements ICallback {
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ToolbarUtility.createToolbar(this, "Login");

        email = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);

        final ICallback self = this;
        final HTTPData data = new HTTPData();
        data.Type = "POST";
        data.Url = "token";

        ((Button) findViewById(R.id.btn_login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.Body =new Pair[] {
                        new Pair("grant_type", "password"),
                        new Pair("username", email.getText().toString()),
                        new Pair("password", password.getText().toString())
                };
                new HTTPGetTask(self).execute(data);
            }
        });
    }

    @Override
    public void execute(String response) {
        if (response != null) {
            Gson g = new Gson();
            Account account = g.fromJson(response,Account.class);
            IdentitySingleton.getInstance().login(account);
            finish();
        }
    }

    @Override
    public void error(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ErrorHandler.notifyError(error, Login.this);
            }
        });
    }


}


