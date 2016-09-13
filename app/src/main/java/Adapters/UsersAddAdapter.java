package Adapters;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import Authentication.IdentitySingleton;
import HTTP.DownloadImageTask;
import HTTP.HTTPUtility;
import ViewModels.Account;
import dakov.trackingsystemandroid.R;

/**
 * Created by Viktor on 7.9.2016 г..
 */
public class UsersAddAdapter extends ArrayAdapter<String> {
    private IdentitySingleton identity = IdentitySingleton.getInstance();

    public UsersAddAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (convertView == null)
            convertView = inflater.inflate(R.layout.users_list_item, null);

        super.getView(position,convertView,parent);
        String user = getItem(position);
        FloatingActionButton actionButton = (FloatingActionButton) convertView.findViewById(R.id.action_bar_material);
        actionButton.setOnClickListener(new AddToGroupOnClickListener(user, getContext()));

        ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
        Account account = identity.getAccount();
        try {
            new DownloadImageTask(image)
                    .execute(HTTPUtility.BaseURL + "api/file/" + account.userName)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
