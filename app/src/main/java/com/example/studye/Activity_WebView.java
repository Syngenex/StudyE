package com.example.studye;

import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class Activity_WebView extends AppCompatActivity {
    private WebView webView;
    private String url="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        init();
    }

    private void init() {
        webView=findViewById(R.id.webview_webview);
        new Thread(){
            @Override
            public void run() {
                try {
                    url=EzrevenueClient.ezrevenueClient(adapter.num,adapter.name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        while(url.equals("")){
            SystemClock.sleep(500);
            Log.e("123","123" );
            if (!url.equals("")){
                webView.loadUrl(url);
                break;
            }
        }
    }
}
