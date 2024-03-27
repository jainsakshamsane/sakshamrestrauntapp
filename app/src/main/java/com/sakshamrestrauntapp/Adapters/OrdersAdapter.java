package com.sakshamrestrauntapp.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.Hold;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sakshamrestrauntapp.Models.DishModels;
import com.sakshamrestrauntapp.Models.OrdersModel;
import com.sakshamrestrauntapp.Models.ReviewModel;
import com.sakshamrestrauntapp.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private List<OrdersModel.Dish> ordersModels;
    private List<ReviewModel> reviewModels;
    private static Context context;


    public OrdersAdapter(List<OrdersModel.Dish> ordersModels, List<ReviewModel> reviewModels, Context context) {
        this.ordersModels = ordersModels;
        this.reviewModels = reviewModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wrapper_orders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrdersModel.Dish ordersModel = ordersModels.get(position);

        String userkiid = ordersModel.getUserid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        userRef.orderByChild("userid").equalTo(userkiid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Retrieve data from the "purchase" node
                    String names = ds.child("name").getValue(String.class);
                    String imageurl = ds.child("imageurl").getValue(String.class);

                    holder.name.setText(names);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        String dishiddd = ordersModel.getDish_id();
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("ratings");
        ratingsRef.orderByChild("dishId").equalTo(dishiddd).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double totalRating = 0;
                int ratingCount = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Retrieve data from the "purchase" node
                    String ratingStr = ds.child("rating").getValue(String.class);
                    try {
                        double rating = Double.parseDouble(ratingStr);
                        totalRating += rating;
                        ratingCount++;
                    } catch (NumberFormatException e) {
                        // Handle the case where the rating value is not a valid double
                        Log.e("RatingConversionError", "Error converting rating to double: " + ratingStr);
                    }
                }

                // Check if there were valid ratings before calculating the average
                if (ratingCount > 0) {
                    // Calculate the average rating
                    double averageRating = totalRating / ratingCount;

                    // Convert the average rating to an integer
                    int integerRating = (int) averageRating;

                    // Set stars based on the integer rating
                    switch (integerRating) {
                        case 1:
                            holder.rating.setText("★☆☆☆☆"); // One star
                            break;
                        case 2:
                            holder.rating.setText("★★☆☆☆"); // Two stars
                            break;
                        case 3:
                            holder.rating.setText("★★★☆☆"); // Three stars
                            break;
                        case 4:
                            holder.rating.setText("★★★★☆"); // Four stars
                            break;
                        case 5:
                            holder.rating.setText("★★★★★"); // Five stars
                            break;
                        default:
                            holder.rating.setText("☆☆☆☆☆"); // No rating or unknown rating
                            break;
                    }
                } else {
                    // No valid ratings found, set an appropriate default value
                    holder.rating.setText("☆☆☆☆☆");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        holder.dishname.setText(ordersModel.getDish_name());
        holder.dishtype.setText(String.valueOf(ordersModel.getQuantity()));
        holder.price.setText("Total - " + ordersModel.getCost() + "/-");
        Picasso.get().load(ordersModel.getImageurl()).into(holder.photo);
        // Convert timestamp to a readable format
        String timestamp = ordersModel.getTimestamp1();
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy 'at' hh:mma");
        try {
            Date date = inputFormat.parse(timestamp);
            String formattedDate = outputFormat.format(date);
            holder.cooktime.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.recyclerView.setVisibility(View.GONE);
        holder.show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check the current visibility state of the RecyclerView
                int visibility = holder.recyclerView.getVisibility();

                // Toggle the visibility
                if (visibility == View.VISIBLE) {
                    holder.recyclerView.setVisibility(View.GONE); // Hide the RecyclerView
                } else {
                    holder.recyclerView.setVisibility(View.VISIBLE); // Show the RecyclerView
                }
            }
        });

        // Set up RecyclerView for reviews
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviewModels);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(reviewsAdapter);
    }

    @Override
    public int getItemCount() {
        // Return the size of ordersModels
        return ordersModels.size();
    }

    public void setDishes(List<OrdersModel.Dish> dishes) {
        this.ordersModels = dishes;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cooktime, category, price, dishtype, dishname, name, review, rating;
        ImageView photo, show, hide;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cooktime = itemView.findViewById(R.id.cooktime);
            category = itemView.findViewById(R.id.category);
            price = itemView.findViewById(R.id.price);
            dishtype = itemView.findViewById(R.id.dishtype);
            dishname = itemView.findViewById(R.id.dishname);
            photo = itemView.findViewById(R.id.leftIcon);
            name = itemView.findViewById(R.id.name);
            review = itemView.findViewById(R.id.review);
            rating = itemView.findViewById(R.id.ratings);
            recyclerView = itemView.findViewById(R.id.recyclerview);
            show = itemView.findViewById(R.id.show);
        }
    }
}