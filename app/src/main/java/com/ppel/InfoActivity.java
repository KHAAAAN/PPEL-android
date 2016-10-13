package com.ppel;

/**
 * Created by conner on 10/5/16.
 */

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class InfoActivity extends MainActivity{

    LinearLayout infoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: replace with actual info we get from API.
        //setContentView(R.layout.info_main);

        //TODO: waiting to see how activities will be started
        // from drawer menu selection

        getLayoutInflater().inflate(R.layout.info_main, relativeLayout);
        //setContentView(R.layout.android_expandable_layout_listview_example);
        setTitle("Info");

    }

    @Override
    protected void onResume() {
        super.onResume();
        // to check current activity in the navigation drawer
        navigationView.getMenu().getItem(0).setChecked(true);
    }


}
