package com.ppel;

/**
 * Created by root on 10/2/16.
 */

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import android.widget.MediaController;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

public class ExpandableLayoutMaterialDesign extends MainActivity {

    ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3, expandableLayout4, expandableLayout5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: replace with actual questions we get from API.
        getLayoutInflater().inflate(R.layout.android_expandable_layout_listview_example, relativeLayout);
        //setContentView(R.layout.android_expandable_layout_listview_example);
        setTitle("Questions");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // to check current activity in the navigation drawer
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    public void expandableButton1(View view) {
        expandableLayout1 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout1);

        expandableLayout1.toggle(); // toggle expand and collapse

        VideoView video1 = (VideoView) findViewById(R.id.video1);
        MediaController mediaController = new MediaController(this);
        if(expandableLayout1.isExpanded()) {
            video1.stopPlayback();
            mediaController.clearAnimation();
        } else {
            //video1.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.big_buck_bunny));
            Uri uri = Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
            //Uri uri = Uri.parse("https://debianvm.eecs.wsu.edu/uploads/questions/8d2uqq19aittoh557.mp4");
            video1.setVideoURI(uri);
            video1.setMediaController(mediaController);
            video1.requestFocus();
            video1.start();
        }
    }

    public void expandableButton2(View view) {
        expandableLayout2 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout2);
        expandableLayout2.toggle(); // toggle expand and collapse
    }

    public void expandableButton3(View view) {
        expandableLayout3 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout3);
        expandableLayout3.toggle(); // toggle expand and collapse
    }

    public void expandableButton4(View view) {
        expandableLayout4 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout4);
        expandableLayout4.toggle(); // toggle expand and collapse
    }
}