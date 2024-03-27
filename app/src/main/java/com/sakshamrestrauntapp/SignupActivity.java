package com.sakshamrestrauntapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sakshamrestrauntapp.Models.SignupModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    TextView username, password, email, phone, signup, login;
    FirebaseDatabase database;
    DatabaseReference reference;

    String usernames, passwords, emails, phones;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.logintext);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("owners");

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernames = username.getText().toString();
                emails = email.getText().toString();
                passwords = password.getText().toString();
                phones = phone.getText().toString();

                if (usernames.isEmpty() || emails.isEmpty() || passwords.isEmpty() || phones.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill all details", Toast.LENGTH_SHORT).show();
                } else if (!emails.toLowerCase().endsWith("@gmail.com")) {
                    Toast.makeText(SignupActivity.this, "Email must end with @gmail.com", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the email already exists in the database
                    checkEmailExistence(emails);
                }
            }
        });
    }

        private void checkEmailExistence(final String email) {
            DatabaseReference ownersRef = FirebaseDatabase.getInstance().getReference("owners");
            ownersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Email already exists in the database, show error message
                        Toast.makeText(SignupActivity.this, "Email already exists in the database. Please choose another email.", Toast.LENGTH_SHORT).show();
                    } else {
                        checkPhoneExistence(phones);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
        }

    private void checkPhoneExistence(final String phone) {
        DatabaseReference ownersRef = FirebaseDatabase.getInstance().getReference("owners");
        ownersRef.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email already exists in the database, show error message
                    Toast.makeText(SignupActivity.this, "Phone already exists in the database. Please choose another.", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

        private void registerUser() {
            DatabaseReference idRef = reference.push();
            String id = idRef.getKey();

            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            SignupModel registerModel = new SignupModel(id, usernames, emails, passwords, phones, timestamp);
            reference.child(id).setValue(registerModel);

            Toast.makeText(SignupActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);

            username.setText("");
            email.setText("");
            password.setText("");
            phone.setText("");
        }
    }
