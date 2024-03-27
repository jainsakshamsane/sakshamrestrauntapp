package com.sakshamrestrauntapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AddRestroDetailsActivity extends AppCompatActivity {

    ImageView uploadimage, imgView;
    EditText restroname, address, phone, city, country, pincode, deliverytime;
    TextView savedetails;
    Spinner spinnerType;
    private Uri filePath;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 22;
    LinearLayout checkboxLayout;
    String ownerid, ownerphone, ownername, imageurl, dish_variety_id, resto_type_Id;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addrestrodetails_activity);

        // Initialize your Spinner and CheckBox instances
        spinnerType = findViewById(R.id.spinnerType);
        uploadimage = findViewById(R.id.uploadimage);
        imgView = findViewById(R.id.imgView);
        restroname = findViewById(R.id.restroname);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        city = findViewById(R.id.city);
        country = findViewById(R.id.country);
        pincode = findViewById(R.id.pincode);
        deliverytime = findViewById(R.id.deliverytime);
        savedetails = findViewById(R.id.savedetails);
        checkboxLayout = findViewById(R.id.checkboxLayout);

        // Set up the Spinner with data
        setUpSpinner();

        setUpVarietyCheckboxes();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("restaurant");

        SharedPreferences sharedPreferencess = getSharedPreferences("user_information", MODE_PRIVATE);
        ownerid = sharedPreferencess.getString("userId", "");
        ownerphone = sharedPreferencess.getString("mobile", "");
        ownername = sharedPreferencess.getString("username", "");

        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        savedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve values from EditText fields
                String restaurantname = restroname.getText().toString();
                String restroaddress = address.getText().toString();
                String restrophone = phone.getText().toString();
                String restrocity = city.getText().toString();
                String restrocountry = country.getText().toString();
                String restropincode = pincode.getText().toString();
                String timedelivery = deliverytime.getText().toString();

                // Check if any field is left blank
                if (restaurantname.isEmpty() || restroaddress.isEmpty() || restrophone.isEmpty() || restrocity.isEmpty() || restrocountry.isEmpty() || restropincode.isEmpty() || timedelivery.isEmpty()) {
                    Toast.makeText(AddRestroDetailsActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return; // Stop further processing
                }

                // Check if imageurl is null or empty
                if (imageurl == null || imageurl.isEmpty()) {
                    // If the profile picture is not uploaded, show a message and return
                    Toast.makeText(AddRestroDetailsActivity.this, "Please upload a restaurant picture", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Retrieve the selected Spinner item and CheckBox states
                String selectedType = spinnerType.getSelectedItem().toString();

                // Proceed with saving data to the database
                DatabaseReference idRef = reference.push();
                String id = idRef.getKey();

                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                // Save data to the database
                reference.child(id).child("restaurant_id").setValue(id);
                reference.child(id).child("restaurant_name").setValue(restaurantname);
                reference.child(id).child("address").setValue(restroaddress);
                reference.child(id).child("phone").setValue(restrophone);
                reference.child(id).child("city").setValue(restrocity);
                reference.child(id).child("country").setValue(restrocountry);
                reference.child(id).child("delivery_time").setValue(timedelivery);
                reference.child(id).child("owner_id").setValue(ownerid);
                reference.child(id).child("owner_name").setValue(ownername);
                reference.child(id).child("pincode").setValue(restropincode);
                reference.child(id).child("imageurl").setValue(imageurl);
                reference.child(id).child("resto_type_id").setValue(resto_type_Id);
                reference.child(id).child("type").setValue(selectedType);
                reference.child(id).child("timestamp").setValue(timestamp);

                // Store food types as a list under a single node named "food_types"
                List<String> foodTypesList = new ArrayList<>();

                // Iterate through the dynamically created CheckBoxes
                for (int i = 0; i < checkboxLayout.getChildCount(); i++) {
                    View v = checkboxLayout.getChildAt(i);
                    if (v instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) v;
                        // Check if the CheckBox is checked
                        if (checkBox.isChecked()) {
                            // If checked, add its text (variety name) to the foodTypesList
                            foodTypesList.add(checkBox.getText().toString());
                        }
                    }
                }

                reference.child(id).child("food_types").setValue(foodTypesList);

                Toast.makeText(AddRestroDetailsActivity.this, "Saved details successfully!", Toast.LENGTH_SHORT).show();

                // Clear input fields after successful signup
                restroname.setText("");
                address.setText("");
                phone.setText("");
                city.setText("");
                country.setText("");
                pincode.setText("");
                deliverytime.setText("");

                // Navigate to the desired activity
                Intent intent = new Intent(AddRestroDetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpVarietyCheckboxes() {
        DatabaseReference varietyRef = FirebaseDatabase.getInstance().getReference("variety");

        varietyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Iterate through the dataSnapshot to retrieve variety values
                for (DataSnapshot varietySnapshot : dataSnapshot.getChildren()) {
                    String varietyName = varietySnapshot.child("dish_variety").getValue(String.class);
                    dish_variety_id = varietySnapshot.child("dish_variety_id").getValue(String.class);
                    // Create a new CheckBox
                    CheckBox checkBox = new CheckBox(AddRestroDetailsActivity.this);
                    checkBox.setText(varietyName);
                    // Add the CheckBox to your layout
                    // For example, if you have a LinearLayout named checkboxLayout, you can add it like this:
                    checkboxLayout.addView(checkBox);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    private void setUpSpinner() {
        DatabaseReference dishCategoryRef = FirebaseDatabase.getInstance().getReference("resto_type");

        // Add a listener to fetch data from the "dishcategory" node
        dishCategoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> dishCategories = new ArrayList<>();
                List<String> dishCategoryIds = new ArrayList<>();

                // Iterate through the dataSnapshot to retrieve dish category and id
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String category = categorySnapshot.child("resto_type").getValue(String.class);
                    resto_type_Id = categorySnapshot.child("resto_type_id").getValue(String.class);

                    // Add the fetched values to the lists
                    dishCategories.add(category);
                    dishCategoryIds.add(resto_type_Id);
                }

                // Create an ArrayAdapter using the fetched values and set it to the spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddRestroDetailsActivity.this, android.R.layout.simple_spinner_item, dishCategories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerType.setAdapter(adapter);

                // Save the dish category IDs for later use if needed
                // You can use this list to retrieve the selected category ID when needed
                // Save it as a class variable if you need it in other methods
                // Example: List<String> dishCategoryIds = new ArrayList<>();
                // dishCategoryIds.addAll(categoryIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
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

                                    Toast.makeText(AddRestroDetailsActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddRestroDetailsActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
