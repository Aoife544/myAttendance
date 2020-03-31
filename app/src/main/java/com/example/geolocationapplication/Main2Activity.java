package com.example.geolocationapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        bottomNavigationView.setSelectedItemId(R.id.nav_sign_in);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new ScannedQRFragment()).commit();



    }

    //set Fragments against bottom nav icons
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch (menuItem.getItemId()) {
                case R.id.nav_profile:
                    selectedFragment = new ProfileFragment();
                    break;
                case R.id.nav_timetable:
                    selectedFragment = new TimetableFragment();
                    break;
                case R.id.nav_sign_in:
                    selectedFragment = new CheckInFragment();
                    break;
                case R.id.nav_report:
                    selectedFragment = new ReportFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();

            return true;
        }
    };

}



