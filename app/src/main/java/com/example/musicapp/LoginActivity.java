package com.example.musicapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("YOUR_REDIRECT_URI")) {
                    Uri uri = Uri.parse(url);
                    String token = uri.getQueryParameter("token");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("token", token);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        webView.loadUrl("YOUR_CLERK_LOGIN_URL");
    }
}
