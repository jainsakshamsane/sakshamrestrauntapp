package com.sakshamrestrauntapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sakshamrestrauntapp.Adapters.OrdersAdapter;
import com.sakshamrestrauntapp.Models.OrdersModel;
import com.sakshamrestrauntapp.Models.ReviewModel;

import java.util.ArrayList;
import java.util.List;

public class Track_Fragment extends Fragment {

    RecyclerView recyclerView;
    List<OrdersModel.Dish> ordersModels = new ArrayList<>();
    List<ReviewModel> reviewModels = new ArrayList<>();
    OrdersAdapter adapter;
    TextView text, total;
    ProgressDialog progressDialog;
    String ownerid, ownerphone, ownername, restaurant;
    double totalPrice = 0; // Total price for all dishes

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.track_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        text = view.findViewById(R.id.text);
        total = view.findViewById(R.id.total);

        SharedPreferences sharedPreferencess = getActivity().getSharedPreferences("user_information", MODE_PRIVATE);
        ownerid = sharedPreferencess.getString("userId", "");
        ownerphone = sharedPreferencess.getString("mobile", "");
        ownername = sharedPreferencess.getString("username", "");

        SharedPreferences sharedPreferencesss = getActivity().getSharedPreferences("restaurant", MODE_PRIVATE);
        restaurant = sharedPreferencesss.getString("restaurant", "");
        Log.e("restaurant ki id", restaurant);

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders");

        adapter = new OrdersAdapter(ordersModels, reviewModels, getContext());

        // Set the adapter to the recyclerView
        recyclerView.setAdapter(adapter);

                    orderRef.orderByChild("status").equalTo(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot orderSnapshot) {
                            for (DataSnapshot order : orderSnapshot.getChildren()) {
                                for (DataSnapshot dishSnapshot : order.child("dishes").getChildren()) {
                                    String cost = dishSnapshot.child("cost").getValue(String.class);
                                    String dish_id = dishSnapshot.child("dish_id").getValue(String.class);
                                    String dish_name = dishSnapshot.child("dish_name").getValue(String.class);
                                    long quantity = dishSnapshot.child("quantity").getValue(long.class);
                                    String imageurl = dishSnapshot.child("imageurl").getValue(String.class);
                                    String restaurant_id = dishSnapshot.child("restaurant_id").getValue(String.class);
                                    String timestamp = dishSnapshot.child("timestamp1").getValue(String.class);
                                    String userid = dishSnapshot.child("userid").getValue(String.class);

                                    if (restaurant.equals(restaurant_id)) {
                                        // Split the cost string based on the space character
                                        String[] parts = cost.split(" ");

                                        // Extract the numeric part of the cost string
                                        String numericCost = parts[1]; // Assuming the numeric part is always at index 1

                                        // Parse the numeric cost to a double value
                                        double dishTotalPrice = Double.parseDouble(numericCost) * quantity;
                                        totalPrice += dishTotalPrice;

                                        OrdersModel.Dish dish = new OrdersModel.Dish(cost, dish_id, dish_name, imageurl, quantity, restaurant_id, timestamp, userid);
                                        ordersModels.add(dish);
                                    }
                                }
                            }

                            // Update the adapter with the ordersModels list
                            adapter.setDishes(ordersModels);
                            recyclerView.setAdapter(adapter);

                            total.setText("Rs. " + String.valueOf(totalPrice)); // Convert double to String
                            progressDialog.dismiss();

                            if (ordersModels.isEmpty()) {
                                text.setVisibility(View.VISIBLE);
                            } else {
                                text.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle error
                        }
                    });

        return view;
    }
}
