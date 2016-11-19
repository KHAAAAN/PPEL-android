package com.ppel;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class CameraActivity extends Activity{

    private final static String TAG_FRAGMENT = "TAG_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState)
        {
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            //ft.addToBackStack(null);
            ft.replace(R.id.container, Camera2VideoFragment.newInstance(), TAG_FRAGMENT)
                    .commit();
        }
    }

     @Override
    public void onBackPressed(){
        final Camera2VideoFragment fragment = (Camera2VideoFragment) getFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (fragment.isPlayingRecording){
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.replace(R.id.container, Camera2VideoFragment.newInstance(), TAG_FRAGMENT)
                    .commit();
        } else{
            super.onBackPressed();
        }
    }
}