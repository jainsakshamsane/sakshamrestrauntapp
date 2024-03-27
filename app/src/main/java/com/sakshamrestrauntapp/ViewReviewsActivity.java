package com.sakshamrestrauntapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.sakshamrestrauntapp.Adapters.ViewReviewsAdapter;
import com.sakshamrestrauntapp.Models.ReviewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewReviewsActivity extends AppCompatActivity {

    TextView text, text1;
    RecyclerView recyclerView;
    ImageView back;
    String dishid;
    ProgressDialog progressDialog;
    List<ReviewModel> reviewModels = new ArrayList<>();
    ViewReviewsAdapter adapter;
    String imageurl;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewreviews_activity);

        back = findViewById(R.id.back);
        text = findViewById(R.id.text);
        text1 = findViewById(R.id.text1);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewReviewsActivity.this));
        recyclerView.setHasFixedSize(true);

        progressDialog = new ProgressDialog(ViewReviewsActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String name = bundle.getString("nameofdish", "");
            dishid = bundle.getString("dishid", "");
            imageurl = bundle.getString("imageurl", "");

            text1.setText("Reviews ( "+ name + " )");
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new ViewReviewsAdapter(reviewModels);

        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
        reviewsRef.orderByChild("dishId").equalTo(dishid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    String reviewText = reviewSnapshot.child("reviewText").getValue(String.class);
                    String userId = reviewSnapshot.child("userid").getValue(String.class);
                    String timestamp = reviewSnapshot.child("timestamp").getValue(String.class);

                        ReviewModel review = new ReviewModel(reviewText, userId, timestamp);
                        reviewModels.add(review);
                }

                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter); // Set the adapter to the RecyclerView here

                // Show the "text" TextView if there are no purchased courses
                if (reviewModels.isEmpty()) {
                    text.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        progressDialog.dismiss();

    }
}
