package com.sakshamrestrauntapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class Dish_CategorywithImageActivity extends AppCompatActivity {
    ImageView backdishimage, uploadimage, imgView;
    Spinner dishtype;
    TextView upload;
    private Uri filePath;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 22;
    String imageurl, restro_id, restro_name, foodtype;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_categorywithimage_activity);

        backdishimage = findViewById(R.id.backdishcategory);
        uploadimage = findViewById(R.id.uploadimage);
        imgView = findViewById(R.id.imgView);
        upload = findViewById(R.id.savedetails);
        dishtype = findViewById(R.id.type);

        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        backdishimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dish_CategorywithImageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            restro_name = bundle.getString("restaurant_name", "");
            restro_id = bundle.getString("restaurant_id", "");
            foodtype = bundle.getString("food_types", "");

            // Set up the spinner with food types
            setUpDishCategorySpinner();
        }

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("dishcategory");

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if imageurl is null or empty
                if (imageurl == null || imageurl.isEmpty()) {
                    // If the profile picture is not uploaded, show a message and return
                    Toast.makeText(Dish_CategorywithImageActivity.this, "Please upload dish image", Toast.LENGTH_SHORT).show();
                    return;
                }

                String type = dishtype.getSelectedItem().toString();

                // Proceed with saving data to the database
                DatabaseReference idRef = reference.push();
                String id = idRef.getKey();

                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                // Save data to the database
                reference.child(id).child("dish_category_id").setValue(id);
                reference.child(id).child("dish_category").setValue(type);
                reference.child(id).child("imageurl").setValue(imageurl);
                reference.child(id).child("timestamp").setValue(timestamp);

                Toast.makeText(Dish_CategorywithImageActivity.this, "Uploaded Dish Category successfully!", Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putString("dish_category", type);
                bundle.putString("dish_category_id", id);
                bundle.putString("restaurant_name", restro_name);
                bundle.putString("restaurant_id", restro_id);
                bundle.putString("foodtype", foodtype);
                bundle.putString("dishtype", type);
                // Start the activity and pass the Bundle
                Intent intent = new Intent(Dish_CategorywithImageActivity.this, Add_DishActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadimage.setImageBitmap(bitmap);
                imgView.setVisibility(View.GONE);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    progressDialog.dismiss();
                                    imageurl = downloadUri.toString();

                                    Toast.makeText(Dish_CategorywithImageActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Dish_CategorywithImageActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void setUpDishCategorySpinner() {
        // Populate the Spinner with data
        List<String> types = new ArrayList<>();
        types.add("Curry");
        types.add("Sweets");
        types.add("Burger");
        types.add("Pizza");
        types.add("Chowmein");
        types.add("Roti");
        types.add("Biryani");
        types.add("Pasta");
        types.add("Chicken");
        types.add("Rolls");
        // Add more types as needed

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dishtype.setAdapter(adapter);
    }
}
