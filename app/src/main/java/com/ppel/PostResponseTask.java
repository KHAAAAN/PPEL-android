package com.ppel;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jay on 10/27/16.
 */

//first parameter needs to be /api/responses/:_id (the :_id part), second parameter needs to be path of local saved video file.
public class PostResponseTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inStream = null;


        String pathOfYourFile = params[1];
        String urlServer = "https://debianvm.eecs.wsu.edu/api/responses/" + params[0]; //change hardcoded api responses later.
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        //String boundary = "-------------------------" + System.currentTimeMillis();

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        try
        {
            FileInputStream fileInputStream = new FileInputStream(new File(pathOfYourFile) );

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");
            CookieManager cookieManager = CookieManager.getInstance();
            String cookie = cookieManager.getCookie("https://debianvm.eecs.wsu.edu/api");

            connection.setRequestProperty("Cookie", cookie);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");

            outputStream = new DataOutputStream( connection.getOutputStream() );
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"video\";filename=\"new.mp4\"" + lineEnd);
            outputStream.writeBytes("Content-Type: video/mp4");
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            /*int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            Log.d("ServerCode",""+serverResponseCode);
            Log.d("serverResponseMessage",""+serverResponseMessage);*/
            Log.d("params[0]", urlServer);
            Log.d("params[1]",params[1]);
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            String response_data = "";
            inStream = new DataInputStream( connection.getInputStream() );
            String str;
            while (( str = inStream.readLine()) != null){
                Log.e("Debug","Server Response "+str);
                response_data+=str;
            }
            inStream.close();
            Log.i("response_data", response_data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
