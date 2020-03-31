package com.example.geolocationapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class LecturerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_lecturer);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new Profile2Fragment()).commit();
    }

    //set Fragments against bottom nav icons
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch (menuItem.getItemId()) {
                case R.id.nav_profile:
                    selectedFragment = new Profile2Fragment();
                    break;
                case R.id.nav_qr:
                    selectedFragment = new GenerateQRFragment();
                    break;
                case R.id.nav_class:
                    selectedFragment = new CheckInFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();

            return true;
        }
    };

}

