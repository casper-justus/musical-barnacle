package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.musicapp.fragments.LibraryFragment;
import com.example.musicapp.fragments.LikedSongsFragment;
import com.example.musicapp.fragments.ProfileFragment;
import com.example.musicapp.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // as soon as the application opens the first fragment should
        // be displayed to the user
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LibraryFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();
                    if (itemId == R.id.navigation_library) {
                        selectedFragment = new LibraryFragment();
                    } else if (itemId == R.id.navigation_liked) {
                        selectedFragment = new LikedSongsFragment();
                    } else if (itemId == R.id.navigation_search) {
                        selectedFragment = new SearchFragment();
                    } else if (itemId == R.id.navigation_profile) {
                        selectedFragment = new ProfileFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }
                    return true;
                }
            };
}
