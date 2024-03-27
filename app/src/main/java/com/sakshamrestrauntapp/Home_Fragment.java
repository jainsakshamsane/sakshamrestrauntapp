package com.sakshamrestrauntapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
import com.sakshamrestrauntapp.Adapters.RestaurantAdapter;
import com.sakshamrestrauntapp.Models.DishModels;
import com.sakshamrestrauntapp.Models.RestaurantModel;

import java.util.ArrayList;
import java.util.List;

public class Home_Fragment extends Fragment {

    RecyclerView recyclerView, recyclerView1;
    List<RestaurantModel> restaurantModels = new ArrayList<>();
    RestaurantAdapter adapter;
    List<DishModels> dishModels = new ArrayList<>();
    DishAdapter adapter1;
    ProgressDialog progressDialog;
    TextView text, text2;
    String ownerid, ownerphone, ownername, restaurantid;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        recyclerView1 = view.findViewById(R.id.recyclerview1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(layoutManager);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        text = view.findViewById(R.id.text);
        text2 = view.findViewById(R.id.text2);

        SharedPreferences sharedPreferencess = getActivity().getSharedPreferences("user_information", MODE_PRIVATE);
        ownerid = sharedPreferencess.getString("userId", "");
        ownerphone = sharedPreferencess.getString("mobile", "");
        ownername = sharedPreferencess.getString("username", "");

        DatabaseReference purchaseRef = FirebaseDatabase.getInstance().getReference("restaurant");

        adapter = new RestaurantAdapter(restaurantModels, getContext());

        purchaseRef.orderByChild("owner_id").equalTo(ownerid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Retrieve data from the "purchase" node
                    String restroname = ds.child("restaurant_name").getValue(String.class);
                    String restrotype = ds.child("type").getValue(String.class);
                    String city = ds.child("city").getValue(String.class);
                    String country = ds.child("country").getValue(String.class);
                    String imageurl = ds.child("imageurl").getValue(String.class);
                    String address = ds.child("address").getValue(String.class);
                    restaurantid = ds.child("restaurant_id").getValue(String.class);
                    Long status = ds.child("status").getValue(Long.class);

                    // Fetch food types from the "foodTypes" node
                    List<String> foodTypes = new ArrayList<>();
                    for (DataSnapshot foodTypeSnapshot : ds.child("food_types").getChildren()) {
                        String foodType = foodTypeSnapshot.getValue(String.class);
                        foodTypes.add(foodType);
                    }

                    RestaurantModel purchaseModel = new RestaurantModel(restroname, restrotype, city, country, foodTypes, imageurl, address, restaurantid, status);
                    restaurantModels.add(purchaseModel);
                }

                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

                SharedPreferences sharedPreferencesss = getActivity().getSharedPreferences("restaurant", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferencesss.edit();
                editor.putString("restaurant", restaurantid);
                editor.apply();

                // Show the "text" TextView if there are no purchased courses
                if (restaurantModels.isEmpty()) {
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

        DatabaseReference dishRef = FirebaseDatabase.getInstance().getReference("dishes");

        adapter1 = new DishAdapter(dishModels, getContext());

        dishRef.orderByChild("owner_id").equalTo(ownerid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Retrieve data from the "purchase" node
                    String dishname = ds.child("dish_name").getValue(String.class);
                    String dishtype = ds.child("dish_type").getValue(String.class);
                    String dishcategory = ds.child("dish_category").getValue(String.class);
                    String category = ds.child("category").getValue(String.class);
                    String cooktime = ds.child("cooktime").getValue(String.class);
                    String cost = ds.child("cost").getValue(String.class);
                    String dish_category_id = ds.child("dish_category_id").getValue(String.class);
                    String dish_id = ds.child("dish_id").getValue(String.class);
                    String ingredients = ds.child("ingredients").getValue(String.class);
                    String weight = ds.child("weight").getValue(String.class);
                    String information = ds.child("information").getValue(String.class);

                    DishModels dishModels1 = new DishModels(dishname, dishtype, dishcategory, category, cooktime, cost, dish_category_id, dish_id, ingredients, weight, information);
                    dishModels.add(dishModels1);
                }

                adapter1.notifyDataSetChanged();
                recyclerView1.setAdapter(adapter1);

                // Show the "text" TextView if there are no purchased courses
                if (dishModels.isEmpty()) {
                    text2.setVisibility(View.VISIBLE);
                    recyclerView1.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }
}
