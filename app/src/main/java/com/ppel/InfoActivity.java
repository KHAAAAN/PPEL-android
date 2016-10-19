package com.ppel;

/**
 * Created by conner on 10/5/16.
 */

import android.os.Bundle;


public class InfoActivity extends MainActivity{
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
