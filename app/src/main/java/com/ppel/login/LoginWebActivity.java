package com.ppel.login;

import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ppel.InfoActivity;
import com.ppel.R;

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

    }

    @Override
    protected  void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_login);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WvClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://debianvm.eecs.wsu.edu/api");

        CookieManager cookieManager = CookieManager.getInstance();
        String cookie = cookieManager.getCookie("https://debianvm.eecs.wsu.edu/api");
        Log.d("cookie", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        if (cookie != null)
        {
            Log.i("cookie", cookie);
        }
        else
        {
            Log.d("cookie", "cookie was null");
        }


        //just out of curiosity how does our app store the cookies

        startActivity(new Intent(getApplicationContext(), InfoActivity.class));
    }
}
