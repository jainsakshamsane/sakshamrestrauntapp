package com.sakshamrestrauntapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String ownername;
    ImageView coupons;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home_Fragment()).commit();

        coupons = findViewById(R.id.coupons);

        coupons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyCouponsActivity.class);
                startActivity(intent);
            }
        });

        // Set the login status in shared preferences
        SharedPreferences sharedPreferencessss = getSharedPreferences("my_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencessss.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        SharedPreferences sharedPreferencess = getSharedPreferences("user_information", MODE_PRIVATE);
        ownername = sharedPreferencess.getString("username", "");
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_item_1) {
                selectedFragment = new Home_Fragment();
            } else if (item.getItemId() == R.id.nav_item_2) {
                selectedFragment = new Track_Fragment();
            } else if (item.getItemId() == R.id.nav_item_02) {
                selectedFragment = new Review_Fragment();
            } else if (item.getItemId() == R.id.nav_item_3) {
                selectedFragment = new Profile_Fragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }

            return false;
        }
    };
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}
