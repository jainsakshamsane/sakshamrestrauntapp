package com.sakshamrestrauntapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sakshamrestrauntapp.Add_CouponsActivity;
import com.sakshamrestrauntapp.Add_DishActivity;
import com.sakshamrestrauntapp.Dish_CategorywithImageActivity;
import com.sakshamrestrauntapp.Models.RestaurantModel;
import com.sakshamrestrauntapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<RestaurantModel> restaurantModels;
    private Context context;

    public RestaurantAdapter(List<RestaurantModel> restaurantModels, Context context) {
        this.restaurantModels = restaurantModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wrapper_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RestaurantModel restaurantModel = restaurantModels.get(position);

        holder.restaurantName.setText(restaurantModel.getRestaurant_name());
        Picasso.get().load(restaurantModel.getImageurl()).into(holder.restro_image);
        holder.type.setText(restaurantModel.getType());
        holder.location.setText(restaurantModel.getAddress() + " " + restaurantModel.getCity() + ", " + restaurantModel.getCountry());

        // Display food types
        List<String> foodTypes = restaurantModel.getFoodTypes();
        StringBuilder foodTypesString = new StringBuilder("");
        for (String foodType : foodTypes) {
            foodTypesString.append(foodType).append(", ");
        }
        // Remove the trailing comma and space
        if (foodTypesString.length() > 2) {
            foodTypesString.setLength(foodTypesString.length() - 2);
        }

        holder.foodTypes.setText("Food Types : " + foodTypesString.toString());

        Long status = restaurantModel.getStatus();
        if (status == null) {
            holder.dishadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("restaurant_name", restaurantModel.getRestaurant_name());
                    bundle.putString("restaurant_id", restaurantModel.getRestaurant_id());
                    bundle.putString("food_types", foodTypesString.toString());
                    // Start the activity and pass the Bundle
                    Intent intent = new Intent(context, Add_DishActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        } else if (status == 1) {
            holder.dishadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Restaurant Disabled by Admin. You can't add Dish!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.dishadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("restaurant_name", restaurantModel.getRestaurant_name());
                    bundle.putString("restaurant_id", restaurantModel.getRestaurant_id());
                    bundle.putString("food_types", foodTypesString.toString());
                    // Start the activity and pass the Bundle
                    Intent intent = new Intent(context, Add_DishActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }

        if (status == null) {
            holder.couponsadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("restaurant_name", restaurantModel.getRestaurant_name());
                    bundle.putString("restaurant_id", restaurantModel.getRestaurant_id());
                    // Start the activity and pass the Bundle
                    Intent intent = new Intent(context, Add_CouponsActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        } else if (status == 1) {
            holder.couponsadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Restaurant Disabled by Admin. You can't add Coupons!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.couponsadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("restaurant_name", restaurantModel.getRestaurant_name());
                    bundle.putString("restaurant_id", restaurantModel.getRestaurant_id());
                    // Start the activity and pass the Bundle
                    Intent intent = new Intent(context, Add_CouponsActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return restaurantModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName, foodTypes, type, location, couponsadd, dishcategory;
        ImageView restro_image, dishadd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restro_name);
            foodTypes = itemView.findViewById(R.id.restro_type);
            restro_image = itemView.findViewById(R.id.restro_image);
            type = itemView.findViewById(R.id.type);
            location = itemView.findViewById(R.id.location);
            dishadd = itemView.findViewById(R.id.dishadd);
            couponsadd = itemView.findViewById(R.id.couponsadd);
            dishcategory = itemView.findViewById(R.id.dishcategory);
        }
    }
}
