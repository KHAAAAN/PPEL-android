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
            String jsonString = new RetrieveJSONTask()
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
                //this lets the video stream
                Uri uri = Uri.parse(ppelServerString + jsonObject.get("path").toString());
                Map<String, String> headers = new HashMap<>(1);
                CookieManager cookieManager = CookieManager.getInstance();
                String cookie = cookieManager.getCookie(ppelServerString + getString(R.string.API));
                headers.put("Cookie", cookie);

                video.setVideoURI(uri, headers);
                video.setMediaController(mediaController);
                mediaController.setAnchorView(video);

                RelativeLayout.LayoutParams expLayoutParams = new RelativeLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

                expLayoutParams.addRule(RelativeLayout.BELOW, currButtonId);

                //now take care of response video if it exists
                String responsesString = "";

                responsesString = new RetrieveJSONTask()
                        .execute(ppelServerString + getString(R.string.Responses_API) + "/" + jsonObject.getString("_id")).get(10000, TimeUnit.MILLISECONDS);

                JSONObject responsesJSON = null;
                try {
                    responsesJSON = new JSONObject(responsesString);
                } catch(JSONException exception){
                    responsesJSON = new JSONObject();
                }

                View.OnClickListener onClickListener = null;

                /*String responsePath = "android.resource://" + getPackageName() + "/" + R.raw.sample;
                responseVideo.setVideoURI(Uri.parse(responsePath));*/

                VideoView responseVideo = null;
                Button deleteButton = null;

                if(responsesJSON.has("path")){
                    responseVideo = new VideoView(ExpandableLayoutMaterialDesign.this);
                    responseVideo.setId(View.generateViewId());

                    MediaController mediaController2 = new MediaController(ExpandableLayoutMaterialDesign.this);

                    responseVideo.setVideoURI(Uri.parse(ppelServerString + responsesJSON.getString("path")), headers);
                    responseVideo.setMediaController(mediaController2);
                    mediaController2.setAnchorView(responseVideo);
                    onClickListener = this.handleOnClickWithResponse(expLayout,
                            video, responseVideo,
                            mediaController, mediaController2);

                    //now delete button
                    deleteButton = new Button(ExpandableLayoutMaterialDesign.this);
                    deleteButton.setText("Delete Answer");

                } else{
                    onClickListener = this.handleOnClick(expLayout, video, mediaController);
                }

                button.setOnClickListener(onClickListener);

                RelativeLayout.LayoutParams videoLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

                expLayout.addView(video, videoLayoutParams);

                Log.i("expLayoutId:", "" + expLayout.getId());
                Log.i("videoId:", "" + video.getId());

                RelativeLayout.LayoutParams recordButtonLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                recordButtonLayout.addRule(RelativeLayout.CENTER_IN_PARENT);

                Button recordButton = new Button(ExpandableLayoutMaterialDesign.this);
                recordButton.setId(View.generateViewId());
                recordButton.setText("Record Answer");

                TextView text= null;
                if(jsonObject.has("text")) {
                    text = new TextView(ExpandableLayoutMaterialDesign.this);
                    text.setId(View.generateViewId());
                    text.setText(jsonObject.get("text").toString());
                    text.setTextSize(18);
                    text.setPadding(0, 10, 0, 0);
                    RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
                    textLayoutParams.addRule(RelativeLayout.BELOW, video.getId());

                    expLayout.addView(text, textLayoutParams);

                    //add paramater info for recording button
                    recordButtonLayout.addRule(RelativeLayout.BELOW, text.getId());
                } else{
                    recordButtonLayout.addRule(RelativeLayout.BELOW, video.getId());
                }

                recordButton.setOnClickListener(handleRecording(jsonObject.getString("_id")));
                expLayout.addView(recordButton, recordButtonLayout);

                if(responsesJSON.has("path")) {
                    //add our response video view
                    RelativeLayout.LayoutParams responseVideoLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    responseVideoLayoutParams.addRule(RelativeLayout.BELOW, recordButton.getId());
                    expLayout.addView(responseVideo, responseVideoLayoutParams);

                    //add delete button to layout
                    RelativeLayout.LayoutParams deleteButtonLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    deleteButtonLayout.addRule(RelativeLayout.RIGHT_OF, recordButton.getId());
                    if(text != null) {
                        deleteButtonLayout.addRule(RelativeLayout.BELOW, text.getId());
                    } else{
                        deleteButtonLayout.addRule(RelativeLayout.BELOW, video.getId());
                    }
                    deleteButton.setOnClickListener(this.handleDeleting(expLayout, responsesJSON.getString("_id"), responseVideo, deleteButton));
                    expLayout.addView(deleteButton, deleteButtonLayout);
                }

                questionsRelativeLayout.addView(button, buttonLayoutParams);
                questionsRelativeLayout.addView(expLayout, expLayoutParams);
                expLayout.collapse();

                //add to this list to toggle all other tabs off when inside listener.
                toHide.add(new Triplet(expLayout, video, mediaController));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
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

    private View.OnClickListener handleDeleting(final ExpandableRelativeLayout expLayout,
                                                final String objectId,
                                                final VideoView responseVideo,
                                                final Button deleteButton){
        return new View.OnClickListener(){
            public void onClick(View v){
                DeleteTask deleteTask = new DeleteTask();
                String delete = ppelServerString + getString(R.string.Responses_API) + "/" + objectId;

                deleteTask.execute(delete); // non-blocking

                expLayout.removeView(responseVideo);
                expLayout.removeView(deleteButton);
            }
        };
    }

    private View.OnClickListener handleRecording(final String objectId){
        return new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                intent.putExtra("objectId", objectId);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener handleOnClick(final ExpandableLayout expLayout,
                                               final VideoView video,
                                               final MediaController mediaController)  { //responseVideo's media controller
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

    private View.OnClickListener handleOnClickWithResponse(final ExpandableLayout expLayout,
                                               final VideoView video,
                                               final VideoView responseVideo,
                                               final MediaController mediaController,
                                               final MediaController mediaController2)  { //responseVideo's media controller
        return new View.OnClickListener() {
            public void onClick(View v) {
                expLayout.toggle();

                if(expLayout.isExpanded()) {
                    mediaController.hide();
                    video.setVisibility(VideoView.GONE);
                    video.stopPlayback();

                    mediaController2.hide();
                    responseVideo.setVisibility(VideoView.GONE);
                    responseVideo.stopPlayback();
                } else {
                    video.seekTo(1);
                    video.setVisibility(VideoView.VISIBLE);
                    video.requestFocus();

                    responseVideo.seekTo(1);
                    responseVideo.setVisibility(VideoView.VISIBLE);
                    responseVideo.requestFocus();

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