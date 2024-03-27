package com.sakshamrestrauntapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sakshamrestrauntapp.Models.DishModels;
import com.sakshamrestrauntapp.Models.ReviewModel;
import com.sakshamrestrauntapp.R;
import com.sakshamrestrauntapp.ViewReviewsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DishesReviewAdapter extends RecyclerView.Adapter<DishesReviewAdapter.ViewHolder> {

    private List<DishModels> dishModels;
    private List<ReviewModel> reviewModels;
    private Context context;
    String imageUrl;

    public DishesReviewAdapter(List<DishModels> dishModels, List<ReviewModel> reviewModels, Context context) {
        this.dishModels = dishModels;
        this.reviewModels = reviewModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wrapper_reviewdish, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DishModels dishModel = dishModels.get(position);

        holder.dishname.setText(dishModel.getDish_name());

        DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference("dishes_images");

        imagesRef.orderByChild("dish_id").equalTo(dishModel.getDish_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Get the image URL
                            imageUrl = snapshot.child("imageurl").getValue(String.class);

                            // Load the image using Picasso
                            Picasso.get().load(imageUrl).into(holder.photo);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                        databaseError.toException().printStackTrace();
                    }
                });

        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("nameofdish", dishModel.getDish_name());
                bundle.putString("dishid", dishModel.getDish_id());
                bundle.putString("imageurl", imageUrl);
                // Start the activity and pass the Bundle
                Log.e("naaaam", dishModel.getDish_name());
                Intent intent = new Intent(context, ViewReviewsActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

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
        DisplayReviewsAdapter reviewsAdapter = new DisplayReviewsAdapter(reviewModels);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(reviewsAdapter);
    }

    @Override
    public int getItemCount() {
        return dishModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dishname;
        ImageView photo, show;
        RecyclerView recyclerView;
        LinearLayout linear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dishname = itemView.findViewById(R.id.dishname);
            photo = itemView.findViewById(R.id.leftIcon);
            show = itemView.findViewById(R.id.show);
            recyclerView = itemView.findViewById(R.id.recyclerview);
            linear = itemView.findViewById(R.id.linear);
        }
    }
}
