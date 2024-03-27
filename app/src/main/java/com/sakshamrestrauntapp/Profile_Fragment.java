package com.sakshamrestrauntapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class Profile_Fragment extends Fragment {

    TextView logout, name, phone, email;
    String ownerid, ownerphone, ownername, owneremail;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        logout = view.findViewById(R.id.logout);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.email);

        SharedPreferences sharedPreferencess = getActivity().getSharedPreferences("user_information", MODE_PRIVATE);
        ownerid = sharedPreferencess.getString("userId", "");
        ownerphone = sharedPreferencess.getString("mobile", "");
        ownername = sharedPreferencess.getString("username", "");
        owneremail = sharedPreferencess.getString("email", "");

        name.setText(ownername);
        phone.setText(ownerphone);
        email.setText(owneremail);

        logout.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void logoutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> logout());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clear all preferences, including the switch state
        editor.clear();
        editor.apply();

        // Navigate to the login page
        Intent intent = new Intent(getContext(), LoginActivity.class);
        Toast.makeText(getContext(), "Signed out", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}
