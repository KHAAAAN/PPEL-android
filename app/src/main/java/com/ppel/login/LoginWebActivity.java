package com.ppel.login;

import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ppel.InfoActivity;
import com.ppel.R;
import com.ppel.RetrieveEmailTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by jay on 11/15/16.
 */

public class LoginWebActivity extends Activity{

    private class WvClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

            handler.proceed();
            // Ignore SSL certificate errors
        }

        @Override
        public void onPageFinished(WebView view, String url){

            String email;
            try {
                email = new RetrieveEmailTask().execute("https://debianvm.eecs.wsu.edu/api/users/email").get(10000, TimeUnit.MILLISECONDS);

            } catch (InterruptedException e) {
                email = null;
                e.printStackTrace();
            } catch (ExecutionException e) {
                email = null;
                e.printStackTrace();
            } catch (TimeoutException e) {
                email = null;
                e.printStackTrace();
            }

            if(email != null) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        }
    }

    @Override
    protected  void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_login);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WvClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://debianvm.eecs.wsu.edu/api");
    }
}
