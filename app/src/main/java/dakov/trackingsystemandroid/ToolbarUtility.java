package dakov.trackingsystemandroid;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

/**
 * Created by Viktor on 4.9.2016 г..
 */
public class ToolbarUtility {
    public static void createToolbar(final FragmentActivity activity, String title){
        Toolbar toolbar = (Toolbar)activity.findViewById(R.id.toolbar);
        TextView text = (TextView) activity.findViewById(R.id.toolbar_title);
        text.setText(title);
        activity.setActionBar(toolbar);
        activity.getActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getActionBar().setHomeButtonEnabled(true);
        activity.getActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }
}
