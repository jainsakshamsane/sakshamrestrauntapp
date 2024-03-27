package com.sakshamrestrauntapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Add_CouponsActivity extends AppCompatActivity {

    ImageView backcoupons;
    EditText couponcode, maxlimit, percentage;
    TextView savedetails;
    FirebaseDatabase database;
    DatabaseReference reference;
    String ownerid, ownerphone, ownername, restro_name, restro_id, codecoupon, couponpercentage, couponlimit;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_coupons_activity);

        couponcode = findViewById(R.id.couponcode);
        maxlimit = findViewById(R.id.maxlimit);
        percentage = findViewById(R.id.percentage);
        savedetails = findViewById(R.id.savedetails);
        backcoupons = findViewById(R.id.backcoupons);

        backcoupons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Add_CouponsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            restro_name = bundle.getString("restaurant_name", "");
            restro_id = bundle.getString("restaurant_id", "");
        }

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("coupons");

        SharedPreferences sharedPreferencess = getSharedPreferences("user_information", MODE_PRIVATE);
        ownerid = sharedPreferencess.getString("userId", "");
        ownerphone = sharedPreferencess.getString("mobile", "");
        ownername = sharedPreferencess.getString("username", "");

        savedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve values from EditText fields
                codecoupon = couponcode.getText().toString();
                couponpercentage = percentage.getText().toString();
                couponlimit = maxlimit.getText().toString();

                // Check if any field is left blank
                if (codecoupon.isEmpty() || couponpercentage.isEmpty() || couponlimit.isEmpty()) {
                    Toast.makeText(Add_CouponsActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    checkcouponcode(codecoupon);
                }
            }
        });
    }

    private void checkcouponcode(String couponcode) {
        DatabaseReference ownersRef = FirebaseDatabase.getInstance().getReference("coupons");
        ownersRef.orderByChild("coupon_code").equalTo(couponcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email already exists in the database, show error message
                    Toast.makeText(Add_CouponsActivity.this, "Coupon Code already exists in the database. Please choose another one.", Toast.LENGTH_SHORT).show();
                } else {
                    insertcoupon();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void insertcoupon() {
        // Proceed with saving data to the database
        DatabaseReference idRef = reference.push();
        String id = idRef.getKey();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Save data to the database
        reference.child(id).child("coupon_id").setValue(id);
        reference.child(id).child("coupon_code").setValue(codecoupon);
        reference.child(id).child("coupon_percentage").setValue(couponpercentage);
        reference.child(id).child("owner_id").setValue(ownerid);
        reference.child(id).child("owner_name").setValue(ownername);
        reference.child(id).child("coupon_limit").setValue(couponlimit);
        reference.child(id).child("timestamp").setValue(timestamp);

        Toast.makeText(Add_CouponsActivity.this, "Saved details successfully!", Toast.LENGTH_SHORT).show();

        // Clear input fields after successful signup
        couponcode.setText("");
        percentage.setText("");
        maxlimit.setText("");

        // Start the activity and pass the Bundle
        Intent intent = new Intent(Add_CouponsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
