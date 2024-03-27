package com.sakshamrestrauntapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sakshamrestrauntapp.Adapters.CouponAdapter;
import com.sakshamrestrauntapp.Adapters.RestaurantAdapter;
import com.sakshamrestrauntapp.Models.CouponModels;
import com.sakshamrestrauntapp.Models.RestaurantModel;

import java.util.ArrayList;
import java.util.List;

public class MyCouponsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<CouponModels> couponModels = new ArrayList<>();
    CouponAdapter adapter;
    ProgressDialog progressDialog;
    TextView text;
    ImageView backcoupons;
    String ownerid, ownerphone, ownername;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycoupons_activity);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyCouponsActivity.this));
        recyclerView.setHasFixedSize(true);

        progressDialog = new ProgressDialog(MyCouponsActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        text = findViewById(R.id.text);
        backcoupons = findViewById(R.id.backcoupons);

        backcoupons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences sharedPreferencess = getSharedPreferences("user_information", MODE_PRIVATE);
        ownerid = sharedPreferencess.getString("userId", "");
        ownerphone = sharedPreferencess.getString("mobile", "");
        ownername = sharedPreferencess.getString("username", "");

        DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference("coupons");

        adapter = new CouponAdapter(couponModels, getApplicationContext());

        couponRef.orderByChild("owner_id").equalTo(ownerid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Retrieve data from the "purchase" node
                    String couponcode = ds.child("coupon_code").getValue(String.class);
                    String couponlimit = ds.child("coupon_limit").getValue(String.class);
                    String couponpercentage = ds.child("coupon_percentage").getValue(String.class);
                    String couponid = ds.child("coupon_id").getValue(String.class);

                    CouponModels couponModels1 = new CouponModels(couponcode, couponlimit, couponpercentage, couponid);
                    couponModels.add(couponModels1);
                }

                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

                // Show the "text" TextView if there are no purchased courses
                if (couponModels.isEmpty()) {
                    text.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                // Dismiss the loading dialog once data is loaded
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                progressDialog.dismiss();
            }
        });
    }
}
