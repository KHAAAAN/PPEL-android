package com.ppel;

import android.os.AsyncTask;

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
 * Created by jay on 10/27/16.
 */

public class PostQuestionsTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        String jsonString = "";
        try {
            URL url = new URL(params[0]);

            try {
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    jsonString = convertStreamToString(in);
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
        return jsonString;
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
