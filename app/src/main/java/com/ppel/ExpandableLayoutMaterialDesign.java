package com.ppel;

/**
 * Created by root on 10/2/16.
 */

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ExpandableLayoutMaterialDesign extends MainActivity {

    //ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3, expandableLayout4, expandableLayout5;

    private String ppelServerString;
    private RelativeLayout questionsRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.android_expandable_layout_listview_example);
        //TODO: replace with actual questions we get from API.
        getLayoutInflater().inflate(R.layout.android_expandable_layout_listview_example, relativeLayout);
        setTitle("Questions");

        ppelServerString = getString(R.string.PPEL_server);
        questionsRelativeLayout = (RelativeLayout) findViewById(R.id.questionsRelativeLayout);

        try {
            String jsonString = new RetrieveQuestionsTask()
                    .execute(ppelServerString + getString(R.string.Questions_API)).get(10000, TimeUnit.MILLISECONDS);

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
                VideoView video = new VideoView(ExpandableLayoutMaterialDesign.this);

                MediaController mediaController = new MediaController(ExpandableLayoutMaterialDesign.this);

                //example https://debianvm.eecs.wsu.edu/uploads/somethingblahblah.mp4
                Uri uri = Uri.parse(ppelServerString + jsonObject.get("path").toString());
                video.setVideoURI(uri);
                video.setMediaController(mediaController);
                mediaController.setAnchorView(video);

                TextView text = null;
                if(jsonObject.has("text")) {
                    text = new TextView(ExpandableLayoutMaterialDesign.this);
                    text.setText(jsonObject.get("text").toString());
                }

                RelativeLayout.LayoutParams expLayoutParams = new RelativeLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                expLayoutParams.addRule(RelativeLayout.BELOW, currButtonId);

                button.setOnClickListener(this.handleOnClick(expLayout,
                        video,
                        mediaController));

                RelativeLayout.LayoutParams videoLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

                expLayout.addView(video, videoLayoutParams);
                /*if(jsonObject.has("text")) {
                    RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
                    expLayout.addView(text, textLayoutParams);
                }*/

                questionsRelativeLayout.addView(button, i + i, buttonLayoutParams);
                questionsRelativeLayout.addView(expLayout, i + i + 1, expLayoutParams);

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
                    mediaController.hide();
                    video.setVisibility(VideoView.GONE);
                    video.stopPlayback();
                    //mediaController.clearAnimation();

                } else {
                    video.start();
                    video.setVisibility(VideoView.VISIBLE);
                    video.requestFocus();
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