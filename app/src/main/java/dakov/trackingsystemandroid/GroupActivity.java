package dakov.trackingsystemandroid;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import Adapters.GroupAdapter;
import Adapters.UsersAddAdapter;
import Authentication.IdentitySingleton;
import Contracts.ICallback;
import HTTP.HTTPData;
import HTTP.HTTPGetTask;
import Helpers.ErrorHandler;
import ViewModels.Account;
import ViewModels.User;

public class GroupActivity extends AppCompatActivity implements ICallback {
    private IdentitySingleton identity = IdentitySingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ToolbarUtility.createToolbar(this, "Group");
        Account account = identity.getAccount();
        HTTPData data = new HTTPData();
        data.Type = "GET";
        data.Url = "api/Group/GetUsersInGroup";
        data.Headers =new Pair[] {new Pair("Authorization","Bearer " + account.access_token)};
        new HTTPGetTask(this).execute(data);
    }

    @Override
    public void execute(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Gson g = new Gson();
                Type type = new TypeToken<User[]>() { }.getType();
                User[] users = g.fromJson(result, type);
                String[] usersStrings = new String[users.length];
                for (int i =0;i< users.length;i++){
                    usersStrings[i] = users[i].UserName;
                }

                TextView test = (TextView) findViewById(R.id.list_item_text_view);
                GroupAdapter usersAdapter = new GroupAdapter(
                        GroupActivity.this,
                        R.layout.group_list_item,
                        R.id.list_item_text_view_group,
                        usersStrings
                );

                ListView listView = (ListView)findViewById(R.id.listView_group);
                listView.setAdapter(usersAdapter);
            }
        });
    }

    @Override
    public void error(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ErrorHandler.notifyError(error, GroupActivity.this);
            }
        });
    }
}
