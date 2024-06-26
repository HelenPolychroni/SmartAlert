package com.example.smartalert;

import static com.example.smartalert.R.id;
import static com.example.smartalert.R.layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class EmployeeHomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView greetingTextView;

    private static final String topicId = "incidents_near_users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve language preference from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isEnglishSelected = preferences.getBoolean("english", true); // Default value is true if key "english" is not found

        // Change language based on the preference
        String lang = isEnglishSelected ? "en" : "el"; // Change this to the language code you want to switch to
        updateLocale(lang);

        setContentView(layout.activity_employee_home_page);

        mAuth = FirebaseAuth.getInstance();

        greetingTextView = findViewById(R.id.greetingTextView);

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
            greetingTextView.setTextColor(getResources().getColor(R.color.white));

            Button button8 = findViewById(R.id.button8);
            Button button13 = findViewById(id.button13);

            button8.setTextColor(getResources().getColor(R.color.white));
            button13.setTextColor(getResources().getColor(R.color.white));
        }

        // Get the current user's email address
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();

            // Reference to the Firebase database
            DatabaseReference employeesRef = FirebaseDatabase.getInstance().getReference("employees");

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
                                    if (isEnglishSelected)
                                        greetingTextView.setText(String.format("Hello %s,\nchoose the actions you want to perform",
                                            fullName.split(" ")[0]));
                                    else
                                        greetingTextView.setText(String.format("Γεια σου %s,\nεπέλεξε τις ενέργειες που επιθυμείς να εκτελέσεις",
                                                fullName.split(" ")[0]));

                                    // Make the root ConstraintLayout visible
                                    ConstraintLayout rootLayout = findViewById(id.employeeHome);
                                    rootLayout.setVisibility(View.VISIBLE);
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

    private void updateLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
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
}