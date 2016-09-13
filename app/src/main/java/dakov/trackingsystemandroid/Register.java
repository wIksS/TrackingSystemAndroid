package dakov.trackingsystemandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import Authentication.IdentitySingleton;
import Contracts.ICallback;
import HTTP.HTTPData;
import HTTP.HTTPGetTask;
import Helpers.ErrorHandler;
import Helpers.Notifier;
import ViewModels.Account;

public class Register extends AppCompatActivity implements ICallback {
    private EditText email;
    private EditText password;
    private EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ToolbarUtility.createToolbar(this, "Register");

        email = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);
        confirmPassword = (EditText) findViewById(R.id.input_password_confirm);
        final ICallback self = this;
        final HTTPData data = new HTTPData();
        data.Type = "POST";
        data.Url = "api/Account/Register";

        ((Button) findViewById(R.id.btn_register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.Body =new Pair[] {
                        new Pair("confirmPassword", confirmPassword.getText().toString()),
                        new Pair("email", email.getText().toString()),
                        new Pair("password", password.getText().toString())
                };
                new HTTPGetTask(self).execute(data);
            }
        });
    }

    @Override
    public void execute(String response) {
        if (response != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Notifier.notify(Register.this,"Success!", "Registered successfuly",R.drawable.ic_error_outline_black_24dp);
                }
            });
        }
    }

    @Override
    public void error(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ErrorHandler.notifyError(error, Register.this);
            }
        });
    }
}
