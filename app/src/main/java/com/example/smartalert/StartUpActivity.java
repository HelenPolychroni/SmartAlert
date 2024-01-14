package com.example.smartalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartUpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    private static final String PREF_NAME = "User";
    private static final String KEY_ROLE = "UserRole";
    SharedPreferences preferences;

    String role;
    Class<?> page;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();



        if (firebaseUser != null) {

            //preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            //role = preferences.getString(KEY_ROLE, "");
            role = firebaseUser.getDisplayName();
            System.out.println("Role is: "+ role);

            // depending on role
            if (role.equals("employee")) {
                try {
                    page = Class.forName("com.example.smartalert.EmployeeHomePage");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (role.equals("user")) {
                try {
                    page = Class.forName("com.example.smartalert.UserOptions");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            //finish the activity to prevent going back
            finish();
            Intent intent = new Intent(this, page);
            startActivity(intent);
        }
    }

    // login btn
    public void login(View view){
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    // signup btn
    public void signup(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}