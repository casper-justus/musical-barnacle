package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTextView = findViewById(R.id.name_text_view);
        emailTextView = findViewById(R.id.email_text_view);
        logoutButton = findViewById(R.id.logout_button);

        // TODO: Get user data from Clerk and display it
        nameTextView.setText("John Doe");
        emailTextView.setText("john.doe@example.com");

        logoutButton.setOnClickListener(v -> {
            // TODO: Implement Clerk logout
            Toast.makeText(ProfileActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
