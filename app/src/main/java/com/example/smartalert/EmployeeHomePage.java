package com.example.smartalert;

import static com.example.smartalert.R.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmployeeHomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;

    private TextView greetingTextView;
    private DatabaseReference employeesRef;
    private Button button13, button8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_employee_home_page);

        mAuth = FirebaseAuth.getInstance();

        greetingTextView = findViewById(R.id.greetingTextView);

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
            greetingTextView.setTextColor(getResources().getColor(R.color.white));

            button8 = findViewById(R.id.button8);
            button13 = findViewById(id.button13);

            button8.setTextColor(getResources().getColor(R.color.white));
            button13.setTextColor(getResources().getColor(R.color.white));
        }

        // Get the current user's email address
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();

            // Reference to the Firebase database
            employeesRef = FirebaseDatabase.getInstance().getReference("employees");

            // Query the database to find the user's full name by email
            employeesRef.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Retrieve the full name from the database
                                String fullName = dataSnapshot.getChildren().iterator().next().child("fullname").getValue(String.class);
                                if (fullName != null) {
                                    // Update the greeting text view with the full name
                                    greetingTextView.setText(String.format("Hello %s,\nchoose the actions you want to perform",
                                            fullName.split(" ")[0]));
                                } else {
                                    // Handle the case when the full name is not found
                                    //Toast.makeText(YourActivity.this, "Full name not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle the case when the user is not found
                                //Toast.makeText(YourActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle the error
                            //Toast.makeText(YourActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle the case when the current user is null (not authenticated)
            //Toast.makeText(this, "No authenticated user", Toast.LENGTH_SHORT).show();
        }
    }

    public void logout(View view){
        mAuth.signOut();

        Intent intent = new Intent(this, StartUpActivity.class);
        startActivity(intent);
    }

    public void got2Incidents(View view){
        Intent intent = new Intent(this, EmployeeIncidentsActivity.class);
        startActivity(intent);
    }

    public void FireIncidents(View view){
        Intent intent = new Intent(this, EmployeeAllFireIncidentsActivity.class);
        startActivity(intent);
    }

    public void EarthquakeIncidents(View view){
        Intent intent = new Intent(this, EmployeeAllEarthquakeIncidentsActivity.class);
        startActivity(intent);
    }

    public void FloodIncidents(View view){
        Intent intent = new Intent(this, EmployeeAllFloodIncidentsActivity.class);
        startActivity(intent);
    }
}