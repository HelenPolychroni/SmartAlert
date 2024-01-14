package com.example.smartalert;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    EditText email, password;
    String role;
    Class<?> page;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
    }

    public void login(View view){


        auth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            role = findRoleFromEmail(email.getText().toString().trim());

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

                            Toast.makeText(LogInActivity.this,"Log in successfully", Toast.LENGTH_SHORT).show();

                            //finish the activity to prevent going back

                            Intent intent = new Intent(LogInActivity.this, page);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            // Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });

    }

    public static String findRoleFromEmail(String email) {
        // Check if the email is not null and contains the '@' symbol
        if (email != null && email.contains("@")) {
            // Split the email using '@' as a delimiter
            String[] parts = email.split("@");

            // Check if there are two parts (local part and domain)
            if (parts.length == 2) {
                // Get the local part (the word before '@')
                String localPart = parts[0];

                // Check if the local part contains the word "employee"
                if (localPart.toLowerCase().contains("employee")) {
                    return "employee";
                } else {
                    return "user";
                }
            }
        }
        // Default to "user" if any conditions are not met
        return "user";
    }
}