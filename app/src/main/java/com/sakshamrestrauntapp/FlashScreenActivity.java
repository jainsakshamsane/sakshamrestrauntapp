package com.sakshamrestrauntapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class FlashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashscreen_activity);

        // Check if the user is already logged in
        if (isLoggedIn()) {
                Intent intent = new Intent(FlashScreenActivity.this, MainActivity.class);
                startActivity(intent);
            }  else {
            // If not logged in, go to LoginRegisterActivity
            Intent intent = new Intent(FlashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private boolean isLoggedIn() {
        // Check your login status logic using shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
}
