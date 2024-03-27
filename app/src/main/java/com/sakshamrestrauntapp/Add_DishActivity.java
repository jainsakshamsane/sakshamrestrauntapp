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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Add_DishActivity extends AppCompatActivity {

    ImageView backdish;
    EditText dishname, ingredients, cooktime, weight, costdish, information;
    TextView savedetails;
    Spinner spinnerType, dishcategory, dishpopular, dishtype;
    FirebaseDatabase database;
    DatabaseReference reference;
    String ownerid, ownerphone, ownername, restro_name, restro_id, foodtype, dish_categoryId;

    List<String> dishCategoryIds = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dish_activity);

        spinnerType = findViewById(R.id.spinnerType);
        backdish = findViewById(R.id.backdish);
        dishname = findViewById(R.id.dishname);
        ingredients = findViewById(R.id.ingredients);
        cooktime = findViewById(R.id.cooktime);
        weight = findViewById(R.id.weight);
        dishtype = findViewById(R.id.type);
        costdish = findViewById(R.id.cost);
        dishpopular = findViewById(R.id.dishpopular);
        dishcategory = findViewById(R.id.dishcategory);
        information = findViewById(R.id.information);
        savedetails = findViewById(R.id.savedetails);

        backdish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Add_DishActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            restro_name = bundle.getString("restaurant_name", "");
            restro_id = bundle.getString("restaurant_id", "");
            foodtype = bundle.getString("food_types", "");

            setUpFoodTypeSpinner(foodtype);
            setUpDishCategorySpinner();
        }

        // Set up the Spinner with data
        setUpSpinner();
        setUpPopularDishSpinner();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("dishes");

        SharedPreferences sharedPreferencess = getSharedPreferences("user_information", MODE_PRIVATE);
        ownerid = sharedPreferencess.getString("userId", "");
        ownerphone = sharedPreferencess.getString("mobile", "");
        ownername = sharedPreferencess.getString("username", "");

        savedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve values from EditText fields
                String nameofdish = dishname.getText().toString();
                String dishingredients = ingredients.getText().toString();
                String timetocook = cooktime.getText().toString();
                String dishweight = weight.getText().toString();
                String cost = costdish.getText().toString();
                String info = information.getText().toString();

                // Check if any field is left blank
                if (nameofdish.isEmpty() || dishingredients.isEmpty() || timetocook.isEmpty() || dishweight.isEmpty() || cost.isEmpty() || info.isEmpty()) {
                    Toast.makeText(Add_DishActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return; // Stop further processing
                }

                // Retrieve the selected Spinner item and CheckBox states
                String selectedType = spinnerType.getSelectedItem().toString();
                String selectedcategory = dishcategory.getSelectedItem().toString();
                String selectedpopular = dishpopular.getSelectedItem().toString();
                String type = dishtype.getSelectedItem().toString();

                // Retrieve the selected category ID from the Spinner
                String selectedCategoryId = dishCategoryIds.get(dishtype.getSelectedItemPosition());

                // Proceed with saving data to the database
                DatabaseReference idRef = reference.push();
                String id = idRef.getKey();

                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                // Save data to the database
                reference.child(id).child("dish_id").setValue(id);
                reference.child(id).child("restaurant_id").setValue(restro_id);
                reference.child(id).child("restaurant_name").setValue(restro_name);
                reference.child(id).child("dish_name").setValue(nameofdish);
                reference.child(id).child("ingredients").setValue(dishingredients);
                reference.child(id).child("cooktime").setValue(timetocook);
                reference.child(id).child("weight").setValue(dishweight + "grams");
                reference.child(id).child("dish_category").setValue(type);
                reference.child(id).child("dish_type").setValue(selectedcategory);
                reference.child(id).child("dish_category_id").setValue(selectedCategoryId); // Use the selected category ID
                reference.child(id).child("dish_popular").setValue(selectedpopular);
                reference.child(id).child("owner_id").setValue(ownerid);
                reference.child(id).child("owner_name").setValue(ownername);
                reference.child(id).child("cost").setValue("Rs. " + cost);
                reference.child(id).child("category").setValue(selectedType);
                reference.child(id).child("information").setValue(info);
                reference.child(id).child("timestamp").setValue(timestamp);
                reference.child(id).child("status").setValue(0);

                Toast.makeText(Add_DishActivity.this, "Saved details successfully!", Toast.LENGTH_SHORT).show();

                // Clear input fields after successful signup
                dishname.setText("");
                ingredients.setText("");
                cooktime.setText("");
                weight.setText("");
                costdish.setText("");
                information.setText("");

                Bundle bundle = new Bundle();
                bundle.putString("restaurant_name", restro_name);
                bundle.putString("restaurant_id", restro_id);
                bundle.putString("dishname", nameofdish);
                bundle.putString("dish_id", id);
                // Start the activity and pass the Bundle
                Intent intent = new Intent(Add_DishActivity.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void setUpSpinner() {
        // Populate the Spinner with data
        List<String> types = new ArrayList<>();
        types.add("Veg");
        types.add("Non-Veg");
        // Add more types as needed

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
    }

    private void setUpFoodTypeSpinner(String foodTypesString) {
        // Split the comma-separated string into a list of food types
        String[] foodTypesArray = foodTypesString.split(", ");
        List<String> foodTypesList = Arrays.asList(foodTypesArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, foodTypesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dishcategory.setAdapter(adapter);
    }

    private void setUpPopularDishSpinner() {
        // Populate the Spinner with data
        List<String> types = new ArrayList<>();
        types.add("Yes");
        types.add("No");
        // Add more types as needed

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dishpopular.setAdapter(adapter);
    }

    private void setUpDishCategorySpinner() {
        DatabaseReference dishCategoryRef = FirebaseDatabase.getInstance().getReference("dishcategory");

        // Add a listener to fetch data from the "dishcategory" node
        dishCategoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> dishCategories = new ArrayList<>();

                // Clear the existing list to avoid duplicates
                dishCategoryIds.clear();

                // Iterate through the dataSnapshot to retrieve dish category and id
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String category = categorySnapshot.child("dish_category").getValue(String.class);
                    dish_categoryId = categorySnapshot.child("dish_category_id").getValue(String.class);

                    // Add the fetched values to the lists
                    dishCategories.add(category);
                    dishCategoryIds.add(dish_categoryId);
                }

                // Create an ArrayAdapter using the fetched values and set it to the spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Add_DishActivity.this, android.R.layout.simple_spinner_item, dishCategories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dishtype.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

}
