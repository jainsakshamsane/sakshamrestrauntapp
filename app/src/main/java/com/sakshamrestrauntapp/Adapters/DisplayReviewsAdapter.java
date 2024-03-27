package com.sakshamrestrauntapp.Adapters;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sakshamrestrauntapp.Models.ReviewModel;
import com.sakshamrestrauntapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DisplayReviewsAdapter extends RecyclerView.Adapter<DisplayReviewsAdapter.ViewHolder> {
    private List<ReviewModel> reviewModels;
    public DisplayReviewsAdapter(List<ReviewModel> reviewModels) {
        this.reviewModels = reviewModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wrapper_displayreviews, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewModel reviewModel = reviewModels.get(position);
        holder.reviewContentTextView.setText(reviewModel.getReviewText());

        String userkiid = reviewModel.getUserid();

        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("users");
        ratingsRef.orderByChild("userid").equalTo(userkiid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Retrieve data from the "purchase" node
                    String name = ds.child("name").getValue(String.class);
                    String imageurl = ds.child("imageurl").getValue(String.class);

                    holder.reviewUserNameTextView.setText(name);
                    Picasso.get().load(imageurl).into(holder.reviewUserImageView);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reviewUserNameTextView;
        TextView reviewContentTextView;
        ImageView reviewUserImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewUserNameTextView = itemView.findViewById(R.id.reviewUserNameTextView);
            reviewContentTextView = itemView.findViewById(R.id.reviewContentTextView);
            reviewUserImageView = itemView.findViewById(R.id.reviewUserImageView);
        }
    }
}
