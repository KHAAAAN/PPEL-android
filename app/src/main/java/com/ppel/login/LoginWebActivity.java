package com.ppel.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
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

        @Override
        public void onPageFinished(WebView view, String url){
            CookieManager cookieManager = CookieManager.getInstance();
            String cookie = cookieManager.getCookie("https://debianvm.eecs.wsu.edu/api");
            if(cookie != null) {
                startActivity(new Intent(getApplicationContext(), InfoActivity.class));
            }
        }
    }

    @Override
    protected  void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        CookieManager.getInstance().removeAllCookies(null);

        setContentView(R.layout.activity_login);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WvClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://debianvm.eecs.wsu.edu/api");
        //startActivity(new Intent(getApplicationContext(), InfoActivity.class));
    }
}
