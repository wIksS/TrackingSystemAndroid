package Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.View;

import Authentication.IdentitySingleton;
import Contracts.ICallback;
import HTTP.HTTPData;
import HTTP.HTTPGetTask;
import Helpers.ErrorHandler;
import Helpers.Notifier;
import ViewModels.Account;
import dakov.trackingsystemandroid.AddUsersToGroup;
import dakov.trackingsystemandroid.R;

/**
 * Created by Viktor on 7.9.2016 г..
 */
public class AddToGroupOnClickListener implements View.OnClickListener, ICallback {
    private String userName;
    private Context context;
    private IdentitySingleton identity = IdentitySingleton.getInstance();

    public AddToGroupOnClickListener(String userName, Context context) {
        this.userName = userName;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Account account = identity.getAccount();
        HTTPData data = new HTTPData();
        data.Type = "POST";
        data.Url = "api/Users/" + this.userName;
        data.Headers = new Pair[]{new Pair("Authorization", "Bearer " + account.access_token)};

        new HTTPGetTask(this).execute(data);
    }

    @Override
    public void execute(final String result) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Notifier.notify(((Activity) context), "Success!", "Added user to group!", R.drawable.ic_error_outline_black_24dp);
            }
        });
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
