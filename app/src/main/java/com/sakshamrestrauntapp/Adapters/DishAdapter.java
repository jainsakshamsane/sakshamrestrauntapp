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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sakshamrestrauntapp.Models.DishModels;
import com.sakshamrestrauntapp.MoreDetailsActivity;
import com.sakshamrestrauntapp.R;
import com.sakshamrestrauntapp.ViewReviewsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.ViewHolder> {

    private List<DishModels> dishModels;
    private List<DishModels> dishModelsFull; // Full list to use for filtering
    private Context context;

    public DishAdapter(List<DishModels> dishModels, Context context) {
        this.dishModels = dishModels;
        this.dishModelsFull = new ArrayList<>(dishModels);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wrapper_dishes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DishModels dishModel = dishModels.get(position);

        holder.dishname.setText(dishModel.getDish_name());
        holder.dishtype.setText(dishModel.getDish_category());
        holder.price.setText(dishModel.getCost() + "/-");
        holder.category.setText(dishModel.getCategory());
        holder.cooktime.setText(dishModel.getCooktime());

        DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference("dishes_images");

        imagesRef.orderByChild("dish_id").equalTo(dishModel.getDish_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Get the image URL
                            String imageUrl = snapshot.child("imageurl").getValue(String.class);

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

        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("dishes");

        statusRef.orderByChild("dish_id").equalTo(dishModel.getDish_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Get the status as a Long
                            Long statusLong = snapshot.child("status").getValue(Long.class);
                            Long outoforderlong = snapshot.child("out_of_order").getValue(Long.class);

                            // Convert statusLong to String if needed
                            String status = String.valueOf(statusLong);
                            String outoforder = String.valueOf(outoforderlong);

                            if ("0".equals(status)) { // Compare strings using equals method, not ==
                                holder.disable.setVisibility(View.VISIBLE);
                            } else {
                                holder.enable.setVisibility(View.VISIBLE);
                            }

                            if ("2".equals(outoforder)) { // Compare strings using equals method, not ==
                                holder.soldout.setVisibility(View.VISIBLE);
                            } else {
                                holder.available.setVisibility(View.VISIBLE);
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                        databaseError.toException().printStackTrace();
                    }
                });

        holder.disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    DishModels clickedItem = dishModels.get(position);
                    String dishId = clickedItem.getDish_id();
                    DatabaseReference dishesRef = FirebaseDatabase.getInstance().getReference("dishes").child(dishId);
                    dishesRef.child("status").setValue(1);
                    Toast.makeText(context, "Disabled successfully", Toast.LENGTH_SHORT).show();
                    holder.enable.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    DishModels clickedItem = dishModels.get(position);
                    String dishId = clickedItem.getDish_id();
                    DatabaseReference dishesRef = FirebaseDatabase.getInstance().getReference("dishes").child(dishId);
                    dishesRef.child("status").setValue(0);
                    Toast.makeText(context, "Enabled successfully", Toast.LENGTH_SHORT).show();
                    holder.disable.setVisibility(View.VISIBLE);
                    holder.enable.setVisibility(View.GONE);
                }
            }
        });

        holder.soldout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    DishModels clickedItem = dishModels.get(position);
                    String dishId = clickedItem.getDish_id();
                    String dishname = clickedItem.getDish_name();
                    DatabaseReference dishesRef = FirebaseDatabase.getInstance().getReference("dishes").child(dishId);
                    dishesRef.child("status").setValue(3);
                    Toast.makeText(context, dishname + " is available now", Toast.LENGTH_SHORT).show();
                    holder.available.setVisibility(View.VISIBLE);
                    holder.soldout.setVisibility(View.GONE);
                }
            }
        });

        holder.available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    DishModels clickedItem = dishModels.get(position);
                    String dishId = clickedItem.getDish_id();
                    String dishname = clickedItem.getDish_name();
                    DatabaseReference dishesRef = FirebaseDatabase.getInstance().getReference("dishes").child(dishId);
                    dishesRef.child("status").setValue(2);
                    Toast.makeText(context, dishname + " is out of order now", Toast.LENGTH_SHORT).show();
                    holder.soldout.setVisibility(View.VISIBLE);
                    holder.available.setVisibility(View.GONE);
                }
            }
        });

        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("nameofdish", dishModel.getDish_name());
                bundle.putString("dishid", dishModel.getDish_id());
                bundle.putString("cooktime", dishModel.getCooktime());
                bundle.putString("cost", dishModel.getCost());
                bundle.putString("dishtype", dishModel.getDishtype());
                bundle.putString("information", dishModel.getInformation());
                bundle.putString("ingredients", dishModel.getIngredients());
                bundle.putString("category", dishModel.getCategory());
                bundle.putString("weight", dishModel.getWeight());
                // Start the activity and pass the Bundle
                Log.e("naaaam", dishModel.getCategory());
                Intent intent = new Intent(context, MoreDetailsActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishModels.size();
    }

        public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cooktime, category, price, dishtype, dishname, disable, enable;
        ImageView photo, available, soldout;
        LinearLayout linear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cooktime = itemView.findViewById(R.id.cooktime);
            category = itemView.findViewById(R.id.category);
            price = itemView.findViewById(R.id.price);
            dishtype = itemView.findViewById(R.id.dishtype);
            dishname = itemView.findViewById(R.id.dishname);
            photo = itemView.findViewById(R.id.leftIcon);
            disable = itemView.findViewById(R.id.disable);
            enable = itemView.findViewById(R.id.enable);
            available = itemView.findViewById(R.id.available);
            soldout = itemView.findViewById(R.id.soldout);
            linear = itemView.findViewById(R.id.linear);
        }
    }
}
