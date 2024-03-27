package com.sakshamrestrauntapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sakshamrestrauntapp.Adapters.DishImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class MoreDetailsActivity extends AppCompatActivity {

    ViewPager viewPager;
    TextView heading1, heading2, heading3, heading4, typeofdish, informations, weight, delete;
    String dishid, cooktime, cost, dishtype, information, ingredients, category, weights, name;
    ImageView back, veg, nonveg, upload;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moredetails_activity);

        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        heading1 = findViewById(R.id.dishname);
        heading2 = findViewById(R.id.ingredients);
        heading3 = findViewById(R.id.price);
        heading4 = findViewById(R.id.dishtype);
        typeofdish = findViewById(R.id.typeofdish);
        informations = findViewById(R.id.information);
        back = findViewById(R.id.back);
        veg = findViewById(R.id.veg);
        nonveg = findViewById(R.id.nonveg);
        weight = findViewById(R.id.weight);
        upload = findViewById(R.id.upload);
        delete = findViewById(R.id.delete);

        // Set text for headings
        heading1.setText("Heading 1");
        heading2.setText("Heading 2");
        heading3.setText("Heading 3");
        heading4.setText("Heading 4");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("nameofdish", "");
            dishid = bundle.getString("dishid", "");
            cooktime = bundle.getString("cooktime", "");
            cost = bundle.getString("cost", "");
            dishtype = bundle.getString("dishtype", "");
            information = bundle.getString("information", "");
            ingredients = bundle.getString("ingredients", "");
            category = bundle.getString("category", "");
            weights = bundle.getString("weight", "");

            heading1.setText(name);
            heading2.setText(cooktime);
            heading3.setText(cost + "/-");
            heading4.setText(ingredients);
            typeofdish.setText("Dish Type : " + dishtype);
            informations.setText(information);
            weight.setText(weights);

            if (category.equals("Veg")) {
                veg.setVisibility(View.VISIBLE);
            }

            if (category.equals("Non-Veg")) {
                nonveg.setVisibility(View.VISIBLE);
            }
        }

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("dishname", name);
                bundle.putString("dish_id", dishid);
                // Start the activity and pass the Bundle
                Intent intent = new Intent(MoreDetailsActivity.this, Add_dishimagesActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Fetch image URLs from the database and set up ViewPager adapter
        fetchImageUrlsAndSetupViewPager();
    }

    private void fetchImageUrlsAndSetupViewPager() {
        DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference("dishes_images");
        imagesRef.orderByChild("dish_id").equalTo(dishid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> imageUrls = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Fetch image URLs from database
                    String imageUrl = snapshot.child("imageurl").getValue(String.class);
                    imageUrls.add(imageUrl);
                }
                // Set up ViewPager adapter with fetched image URLs
                DishImageAdapter adapter = new DishImageAdapter(imageUrls, MoreDetailsActivity.this);
                viewPager.setAdapter(adapter);

                // Update the dot indicators
                setupDotIndicators(imageUrls.size());

                if (imageUrls.isEmpty()) {
                    upload.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void setupDotIndicators(int count) {
        LinearLayout dotsLayout = findViewById(R.id.dotsLayout);
        ImageView[] dots = new ImageView[count];

        for (int i = 0; i < count; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.ic_dot_inactive);

            final int position = i;
            dots[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(position);
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dots[i], params);
        }

        // Highlight the first dot initially
        if (dots.length > 0) {
            dots[0].setImageResource(R.drawable.ic_dot_active);
        }

        // Set up ViewPager listener to update dot indicators when page changes
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < count; i++) {
                    dots[i].setImageResource(R.drawable.ic_dot_inactive);
                }
                dots[position].setImageResource(R.drawable.ic_dot_active);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure you want to delete this dish images?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteImages();
                // Finish the activity and go back to the main activity
                Intent intent = new Intent(MoreDetailsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void deleteImages() {
        DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference("dishes_images");
        imagesRef.orderByChild("dish_id").equalTo(dishid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Delete the image from the database
                    snapshot.getRef().removeValue();
                }
                // Optionally, you can delete the dish itself from another node if needed
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
