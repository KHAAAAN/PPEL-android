package com.ppel;

/**
 * Created by root on 10/2/16.
 */

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.webkit.CookieManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ExpandableLayoutMaterialDesign extends MainActivity {

    private String ppelServerString;
    private RelativeLayout questionsRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.android_expandable_layout_listview_example);
        getLayoutInflater().inflate(R.layout.android_expandable_layout_listview_example, relativeLayout);
        setTitle("Questions");

        ppelServerString = getString(R.string.PPEL_server);
        questionsRelativeLayout = (RelativeLayout) findViewById(R.id.questionsRelativeLayout);
        questionsRelativeLayout.setId(View.generateViewId());
        toHide = new ArrayList<>();

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
                video.setId(View.generateViewId());

                MediaController mediaController = new MediaController(ExpandableLayoutMaterialDesign.this);

                //example https://debianvm.eecs.wsu.edu/uploads/somethingblahblah.mp4
                Uri uri = Uri.parse(ppelServerString + jsonObject.get("path").toString());
                Map<String, String> headers = new HashMap<>(1) ;
                CookieManager cookieManager = CookieManager.getInstance();
                String cookie = cookieManager.getCookie("https://debianvm.eecs.wsu.edu/api");
                headers.put("Cookie", cookie);
                video.setVideoURI(uri, headers);
                video.setMediaController(mediaController);
                mediaController.setAnchorView(video);

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

                Log.i("expLayoutId:", "" + expLayout.getId());
                Log.i("videoId:", "" + video.getId());

                RelativeLayout.LayoutParams recordButtonLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                Button recordButton = new Button(ExpandableLayoutMaterialDesign.this);
                recordButton.setText("Record Answer");

                if(jsonObject.has("text")) {
                    TextView text = new TextView(ExpandableLayoutMaterialDesign.this);
                    text.setId(View.generateViewId());
                    text.setText(jsonObject.get("text").toString());
                    text.setTextSize(18);
                    text.setPadding(0, 10, 0, 0);
                    RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
                    textLayoutParams.addRule(RelativeLayout.BELOW, video.getId());

                    expLayout.addView(text, textLayoutParams);
                    Log.i("textId:", "" + text.getId());

                    //add paramater info for recording button
                    recordButtonLayout.addRule(RelativeLayout.BELOW, text.getId());
                } else{
                    recordButtonLayout.addRule(RelativeLayout.BELOW, video.getId());
                }

                recordButton.setOnClickListener(handleRecording());
                expLayout.addView(recordButton, recordButtonLayout);

                questionsRelativeLayout.addView(button, buttonLayoutParams);
                questionsRelativeLayout.addView(expLayout, expLayoutParams);
                expLayout.collapse();

                //add to this list to toggle all other tabs off when inside listener.
                toHide.add(new Triplet(expLayout, video, mediaController));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private  class Triplet{
        private  final ExpandableLayout _expLayout;
        private final VideoView _video;
        private final MediaController _mediaController;

        public Triplet(ExpandableLayout expLayout, VideoView video,
                       MediaController mediaController){
            _expLayout = expLayout;
            _video = video;
            _mediaController = mediaController;
        }

        public void hideIfNotThisId(int id){
            ExpandableRelativeLayout erl = (ExpandableRelativeLayout) _expLayout;
            if(erl.getId() != id && erl.isExpanded()){
                erl.toggle();
                _mediaController.hide();
                _video.setVisibility(VideoView.GONE);
                _video.stopPlayback();
            }
        }
    }
    private List<Triplet> toHide;

    private View.OnClickListener handleRecording(){
        return new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
            }
        };
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
                } else {
                    video.seekTo(1);
                    video.setVisibility(VideoView.VISIBLE);
                    video.requestFocus();
                    int id = ((ExpandableRelativeLayout)expLayout).getId();
                    for(Triplet triplet : toHide){
                        triplet.hideIfNotThisId(id);
                    }
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
}