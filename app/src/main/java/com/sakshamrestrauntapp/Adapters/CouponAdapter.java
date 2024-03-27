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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sakshamrestrauntapp.Add_CouponsActivity;
import com.sakshamrestrauntapp.Add_DishActivity;
import com.sakshamrestrauntapp.Dish_CategorywithImageActivity;
import com.sakshamrestrauntapp.Models.CouponModels;
import com.sakshamrestrauntapp.Models.RestaurantModel;
import com.sakshamrestrauntapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {

    private List<CouponModels> couponModels;
    private Context context;

    public CouponAdapter(List<CouponModels> couponModels, Context context) {
        this.couponModels = couponModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wrapper_coupons, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CouponModels couponModels1 = couponModels.get(position);

        holder.code.setText(couponModels1.getCoupon_code());
        holder.limit.setText("Rs. " + couponModels1.getCoupon_limit());
        holder.discount.setText(couponModels1.getCoupon_percentage() + "%");

        // Set click listener for the delete button
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Get the coupon ID to be deleted
                    String couponId = couponModels.get(adapterPosition).getCoupon_id();

                    // Remove the coupon from the database
                    DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference().child("coupons").child(couponId);
                    couponRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Coupon deleted from the database, now remove it from the RecyclerView
                                couponModels.remove(adapterPosition);
                                notifyItemRemoved(adapterPosition);
                                Toast.makeText(context, "Coupon deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to delete coupon", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return couponModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView code, discount, limit;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.code);
            discount = itemView.findViewById(R.id.discount);
            limit = itemView.findViewById(R.id.limit);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
