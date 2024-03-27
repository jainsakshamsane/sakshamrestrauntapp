package com.sakshamrestrauntapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sakshamrestrauntapp.Adapters.DishAdapter;
import com.sakshamrestrauntapp.Adapters.DishesReviewAdapter;
import com.sakshamrestrauntapp.Adapters.RestaurantAdapter;
import com.sakshamrestrauntapp.Models.DishModels;
import com.sakshamrestrauntapp.Models.RestaurantModel;
import com.sakshamrestrauntapp.Models.ReviewModel;

import java.util.ArrayList;
import java.util.List;

public class Review_Fragment extends Fragment {

    RecyclerView recyclerView;
    List<DishModels> dishModels = new ArrayList<>();
    List<ReviewModel> reviewModels = new ArrayList<>();
    DishesReviewAdapter adapter1;
    ProgressDialog progressDialog;
    String ownerid, ownerphone, ownername, dish_id;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.review_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        SharedPreferences sharedPreferencess = getActivity().getSharedPreferences("user_information", MODE_PRIVATE);
        ownerid = sharedPreferencess.getString("userId", "");
        ownerphone = sharedPreferencess.getString("mobile", "");
        ownername = sharedPreferencess.getString("username", "");

        DatabaseReference dishRef = FirebaseDatabase.getInstance().getReference("dishes");

        adapter1 = new DishesReviewAdapter(dishModels, reviewModels, getContext());

        dishRef.orderByChild("owner_id").equalTo(ownerid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Retrieve data from the "purchase" node
                    String dishname = ds.child("dish_name").getValue(String.class);
                    String dish_category_id = ds.child("dish_category_id").getValue(String.class);
                    dish_id = ds.child("dish_id").getValue(String.class);

                    DishModels dishModels1 = new DishModels(dishname, dish_category_id, dish_id);
                    dishModels.add(dishModels1);
                }

                DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
                reviewsRef.orderByChild("dishId").equalTo(dish_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                            String reviewText = reviewSnapshot.child("reviewText").getValue(String.class);
                            String userId = reviewSnapshot.child("userid").getValue(String.class);

                                ReviewModel review = new ReviewModel(reviewText, userId);
                                reviewModels.add(review);
                        }
                        adapter1.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });

                adapter1.notifyDataSetChanged();
                recyclerView.setAdapter(adapter1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        progressDialog.dismiss();

        return view;
    }
}
