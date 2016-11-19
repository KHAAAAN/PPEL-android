package com.ppel.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.webkit.CookieManager;

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
                String cookie = cookieManager.getCookie("https://debianvm.eecs.wsu.edu/api");

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
