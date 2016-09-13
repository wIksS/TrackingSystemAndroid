package dakov.trackingsystemandroid;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;

import Authentication.IdentitySingleton;
import Background.LocationReceiver;
import Contracts.ICallback;
import HTTP.DownloadImageTask;
import HTTP.HTTPData;
import HTTP.HTTPGetTask;
import HTTP.HTTPUtility;
import Helpers.LocationHelper;
import Helpers.Notifier;
import ViewModels.Account;
import ViewModels.Coordinate;

public class MainActivity extends AppCompatActivity implements ICallback {
    private Drawer drawer;
    private IdentitySingleton identity = IdentitySingleton.getInstance();
    private boolean isLogged;
    private boolean isExcursionStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isLogged = identity.IsLogged();
        final AppCompatActivity self = this;
        final Button startExcrusionButton = (Button) findViewById(R.id.start_excursion_button);
        if (identity.IsLogged()) {
            DrawNavigationAuth();
            startExcrusionButton.setText("Start excursion");

        } else {
            DrawNavigation();
        }
        final Intent mServiceIntent = new Intent(self, LocationService.class);
        final LocationHelper locationHelper = new LocationHelper(this);
        startExcrusionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (identity.IsLogged()) {
                    isExcursionStarted = !isExcursionStarted;
                    if (isExcursionStarted == true) {
                        Location location = locationHelper.getLastKnownLocation();
                        if (location == null) {
                            Intent gpsOptionsIntent = new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(gpsOptionsIntent);
                        }
                        else {
                            startExcrusionButton.setText("Stop excursion");
                            startService(mServiceIntent);
                        }
                    } else {
                        startExcrusionButton.setText("Start excursion");
                        stopService(mServiceIntent);
                    }
                } else {
                    startExcrusionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(self, Register.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        IntentFilter mStatusIntentFilter = new IntentFilter("LocationIntent");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final Coordinate coord = (Coordinate) intent.getSerializableExtra("Coordinate");
                String message = "Lat: " + coord.Latitude + "\nLong" + coord.Longitude;
                String title = "Location";
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(self)
                                .setSmallIcon(R.drawable.ic_error_outline_black_24dp)
                                .setContentTitle(title)
                                .setContentText(message);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                mBuilder.setSound(alarmSound);
                mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                Intent resultIntent = new Intent(self, MapsActivity.class);
                resultIntent.putExtra("Coord", coord);
                resultIntent.putExtra("IntentToKill", mServiceIntent);
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                self,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                int mNotificationId = 001;
// Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, mBuilder.build());

                Notifier.notifyOk(self, title, message,
                        R.drawable.ic_error_outline_black_24dp,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent mapsIntent = new Intent(getApplicationContext(), MapsActivity.class);
                                mapsIntent.putExtra("Coord", coord);
                                startExcrusionButton.setText("Start excursion");
                                stopService(mServiceIntent);
                                startActivity(mapsIntent);
                            }
                        }
                );
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, mStatusIntentFilter);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (identity.IsLogged() != isLogged) {
            isLogged = identity.IsLogged();
            drawer.closeDrawer();
            if (identity.IsLogged()) {
                DrawNavigationAuth();
                Button startExcrusionButton = (Button) findViewById(R.id.start_excursion_button);
                if (this.isExcursionStarted) {
                    startExcrusionButton.setText("Stop excursion");
                } else {
                    startExcrusionButton.setText("Start excursion");
                }
            } else {
                DrawNavigation();
            }
        }
    }

    private void DrawNavigationAuth() {
//        new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
//                .execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");
        Account account = identity.getAccount();
        Bitmap image = null;
        try {
            image = new DownloadImageTask()
                    .execute(HTTPUtility.BaseURL + "api/file/" + account.userName)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_bg)
                .addProfiles(
                        new ProfileDrawerItem().withName(account.userName)
                                // .withEmail("mikepenz@gmail.com")
                                .withIcon(image)
                )
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        PrimaryDrawerItem itemHome = new PrimaryDrawerItem()
                .withName("Home")
                .withIdentifier(NavigationUtility.HOME_IDENTIFIER)
                .withIcon(R.drawable.ic_home_black_36dp);

        PrimaryDrawerItem itemUsers = new PrimaryDrawerItem()
                .withName("Users")
                .withIdentifier(NavigationUtility.USERS_TO_ADD_IDENTIFIER)
                .withIcon(R.drawable.ic_login_36dp);

        PrimaryDrawerItem itemGroup = new PrimaryDrawerItem()
                .withName("Group")
                .withIdentifier(3)
                .withIcon(R.drawable.ic_home_black_36dp);

        PrimaryDrawerItem itemSettings = new PrimaryDrawerItem()
                .withName("Settings")
                .withIdentifier(4)
                .withIcon(R.drawable.ic_home_black_36dp);

        PrimaryDrawerItem itemEvent = new PrimaryDrawerItem()
                .withName("Event")
                .withIdentifier(5)
                .withIcon(R.drawable.ic_home_black_36dp);

        drawer = new DrawerBuilder().withActivity(this)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        itemHome,
                        itemUsers,
                        itemGroup,
                        itemSettings,
                        itemEvent
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position == NavigationUtility.USERS_TO_ADD_IDENTIFIER) {
                            Intent intent = new Intent(getApplicationContext(), AddUsersToGroup.class);
                            startActivity(intent);
                        }
                        if (position == NavigationUtility.HOME_IDENTIFIER) {
                            drawer.closeDrawer();
                        }
                        if (position == 3) {
                            Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                            startActivity(intent);
                        }
                        if (position == 4) {
                            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(intent);
                        }

                        if (position == 5) {
                            Intent intent = new Intent(getApplicationContext(), EventMapActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                })
                .build();
    }

    private void DrawNavigation() {
        ImageView loginIcon= (ImageView) findViewById(R.id.person_icon_home);
        loginIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        ImageView shutDownIcon = (ImageView) findViewById(R.id.shut_down_image);
        shutDownIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_bg)
                .addProfiles(
                        new ProfileDrawerItem().withName("Not logged")
                                .withEmail("Create an account")
                                .withIcon(R.drawable.ic_person_white_100dp)
                )
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        PrimaryDrawerItem itemHome = new PrimaryDrawerItem()
                .withName("Home")
                .withIdentifier(NavigationUtility.HOME_IDENTIFIER)
                .withIcon(R.drawable.ic_home_black_36dp);
        PrimaryDrawerItem item1 = new PrimaryDrawerItem()
                .withName("Login")
                .withIdentifier(NavigationUtility.LOGIN_IDENTIFIER)
                .withIcon(R.drawable.ic_login_36dp);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem()
                .withIcon(R.drawable.ic_regiser_36dp)
                .withIdentifier(NavigationUtility.REGISTER_IDENTIFIER)
                .withName("Register");

        drawer = new DrawerBuilder().withActivity(this)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        itemHome,
                        item1,
                        item2
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position == NavigationUtility.LOGIN_IDENTIFIER) {
                            Intent loginIntent = new Intent(getApplicationContext(), Login.class);
                            startActivity(loginIntent);
                        }
                        if (position == NavigationUtility.REGISTER_IDENTIFIER) {
                            Intent intent = new Intent(getApplicationContext(), Register.class);
                            startActivity(intent);
                        }
                        if (position == NavigationUtility.HOME_IDENTIFIER) {
                            drawer.closeDrawer();
                        }
                        return true;
                    }
                })
                .build();
    }

    @Override
    public void execute(String result) throws JSONException {
//        byte[] bytes = Base64.decode(result, 0);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        int pesho = 5;
    }

    @Override
    public void error(String error) {

    }
}
