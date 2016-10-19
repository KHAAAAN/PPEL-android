package com.ppel;

/**
 * Created by root on 10/2/16.
 */

import android.animation.TimeInterpolator;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.MediaController;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;

public class ExpandableLayoutMaterialDesign extends MainActivity {

    //ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3, expandableLayout4, expandableLayout5;

    private String ppelServerString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ppelServerString = getString(R.string.PPEL_server);

        //setContentView(R.layout.android_expandable_layout_listview_example);
        //TODO: replace with actual questions we get from API.
        getLayoutInflater().inflate(R.layout.android_expandable_layout_listview_example, relativeLayout);
        setTitle("Questions");

        try {
            String jsonString = new RetrieveQuestionsTask()
                    .execute(ppelServerString + getString(R.string.Questions_API)).get(20000, TimeUnit.MILLISECONDS);
            initTabs(jsonString);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void initTabs(String jsonString){
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            int currButtonId;
            int currExpLayoutId = -1;

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Button button = new Button(ExpandableLayoutMaterialDesign.this);

                currButtonId = View.generateViewId();
                button.setId(currButtonId);
                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_float,0);
                button.setPadding(0, 0, 10, 0);
                button.setText(jsonObject.get("title").toString());
                button.setTextColor(Color.parseColor("#FFFFFF"));

                //switch up colors to display evenly
                if(i % 2 == 0){
                    button.setBackgroundColor(ContextCompat.getColor(this, R.color.CoolGray));
                } else{
                    button.setBackgroundColor(ContextCompat.getColor(this, R.color.WSUCrimson));
                }

                RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                if(currExpLayoutId != -1) {
                    buttonLayoutParams.addRule(RelativeLayout.BELOW, currExpLayoutId);
                }

                ExpandableRelativeLayout expLayout =
                        new ExpandableRelativeLayout(ExpandableLayoutMaterialDesign.this);
                currExpLayoutId = View.generateViewId();
                expLayout.setId(currExpLayoutId);
                expLayout.setDuration(400);
                expLayout.setExpanded(false);
                expLayout.setInterpolator(new BounceInterpolator());
                expLayout.setOrientation(ExpandableLayout.VERTICAL);

                /*TextView text = new TextView(ExpandableLayoutMaterialDesign.this);
                text.setText("TESTING");
                expLayout.addView(text);*/

                VideoView video = new VideoView(ExpandableLayoutMaterialDesign.this);

                MediaController mediaController = new MediaController(ExpandableLayoutMaterialDesign.this);

                //example https://debianvm.eecs.wsu.edu/uploads/somethingblahblah.mp4
                Uri uri = Uri.parse(ppelServerString + jsonObject.get("path").toString());
                video.setVideoURI(uri);
                video.setMediaController(mediaController);

                RelativeLayout.LayoutParams expLayoutParams = new RelativeLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                expLayoutParams.addRule(RelativeLayout.BELOW, currButtonId);

                button.setOnClickListener(this.handleOnClick(expLayout,
                        video,
                        mediaController));

                expLayout.addView(video);

                relativeLayout.addView(button, i + i, buttonLayoutParams);
                relativeLayout.addView(expLayout, i + i + 1, expLayoutParams);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener handleOnClick(final ExpandableLayout expLayout,
                                               final VideoView video,
                                               final MediaController mediaController)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                expLayout.toggle();

                if(expLayout.isExpanded()) {
                    video.stopPlayback();
                    video.destroyDrawingCache();
                    mediaController.clearAnimation();
                } else {
                    video.requestFocus();
                    video.start();
                }
            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();
        // to check current activity in the navigation drawer
        navigationView.getMenu().getItem(1).setChecked(true);
    }


    /*public void expandableButton1(View view) {
        expandableLayout1 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout1);
        expandableLayout1.toggle(); // toggle expand and collapse

        // Now you can access an https URL without having the certificate in the truststore
        // Your Code Goes Here

        VideoView video1 = (VideoView) findViewById(R.id.video1);
        MediaController mediaController = new MediaController(this);
        if(expandableLayout1.isExpanded()) {
            video1.stopPlayback();
            mediaController.clearAnimation();
        } else {
            //video1.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.big_buck_bunny));
           // Uri uri = Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
            Uri uri = Uri.parse(ppelServerString + "/uploads/questions/8d2uqq19aittoih1p.mp4");
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
    }*/
}