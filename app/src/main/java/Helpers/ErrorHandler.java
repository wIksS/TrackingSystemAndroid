package Helpers;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import ViewModels.Error;
import dakov.trackingsystemandroid.R;

/**
 * Created by Viktor on 5.9.2016 г..
 */
public class ErrorHandler {
    public static void notifyError(String error, Context context) {
        Gson g = new Gson();
        if (error != "" && error != null) {
            ViewModels.Error errorVM = g.fromJson(error, ViewModels.Error.class);
            Notifier.notify(context, "Something is whrong", errorVM.Message, R.drawable.ic_error_outline_black_24dp);
        }
    }
}
