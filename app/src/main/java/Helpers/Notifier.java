package Helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import dakov.trackingsystemandroid.R;

/**
 * Created by Viktor on 5.9.2016 г..
 */
public class Notifier {
    public static void notify(Context context, String title, String message, int icon){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .show();
    }

    public static void notifyOk(Context context,String title, String message, int icon, DialogInterface.OnClickListener callback)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setPositiveButton("Find", callback)
                .show();

    }
}
