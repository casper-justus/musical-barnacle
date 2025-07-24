package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton; // The button to initiate the login process

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Your existing layout for login

        loginButton = findViewById(R.id.login_button); // Assuming you have a button with this ID

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the login button is clicked, launch the AuthWebViewActivity
                Intent intent = new Intent(LoginActivity.this, AuthWebViewActivity.class);
                startActivity(intent);
                // Optionally, finish LoginActivity so the user cannot navigate back to it
                // after the WebView handles authentication.
                // finish();
            }
        });
    }

    // You can remove any old Volley or direct API call related code from here
    // as it's no longer used for the WebView authentication flow.
}