package com.ppel.login;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.webkit.CookieManager;

import com.ppel.PPELApplication;
import com.ppel.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jay on 11/17/16.
 */

public class LogoutTask extends AsyncTask<String , Void, Void> {

    @Override
    protected Void doInBackground(String... params)
    {
        CookieManager cookieManager = CookieManager.getInstance();
        try {
            URL url = new URL(params[0]);

            try {
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                Resources resources = PPELApplication.resources;
                String uri = resources.getString(R.string.PPEL_server) + resources.getString(R.string.API);
                String cookie = cookieManager.getCookie(uri);

                if(cookie == null){ //write better code than this when we have more time...
                    urlConnection.disconnect();
                    return null;
                }
                urlConnection.setRequestProperty("Cookie", cookie);
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
