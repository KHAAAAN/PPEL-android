package com.ppel;

import android.content.res.Resources;
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
 * Created by jay on 11/16/16.
 */

public class RetrieveEmailTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        String jsonString = "";
        String email = "";
        try {
            URL url = new URL(params[0]);

            try {
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                CookieManager cookieManager = CookieManager.getInstance();
                Resources resources = PPELApplication.resources;
                String uri = resources.getString(R.string.PPEL_server) + resources.getString(R.string.Emails_API);
                String cookie = cookieManager.getCookie(uri); //remove hardcode.

                if(cookie == null){ //write better code than this when we have more time...
                    urlConnection.disconnect();
                    return null;
                }
                urlConnection.setRequestProperty("Cookie", cookie);
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    jsonString = convertStreamToString(in);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    email = jsonObject.get("email").toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    email = null;
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
        return email;
    }

    private String convertStreamToString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
