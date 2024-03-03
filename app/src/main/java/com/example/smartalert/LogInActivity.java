package com.example.smartalert;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class LogInActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    EditText email, password;
    String role;
    Class<?> page;
    SharedPreferences preferences;

    private TextView welcomeBack_msg;
    private Button LogIn_btn;
    boolean isEnglishSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve language preference from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isEnglishSelected = preferences.getBoolean("english", true); // Default value is true if key "english" is not found

        // Change language based on the preference
        String lang = isEnglishSelected ? "en" : "el"; // Change this to the language code you want to switch to
        updateLocale(lang);

        setContentView(R.layout.activity_log_in);

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
            welcomeBack_msg = findViewById(R.id.welcomeBack_msg);
            welcomeBack_msg.setTextColor(getResources().getColor(R.color.white));

            LogIn_btn = findViewById(R.id.LogIn_btn);
            LogIn_btn.setTextColor(getResources().getColor(R.color.white));
        }

        System.out.println("English is: " + isEnglishSelected);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.confpassword);
    }

    private void updateLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
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
                                    //page = Class.forName("com.example.smartalert.UserOptions");
                                    page = Class.forName("com.example.smartalert.UserHomePage");
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            if (isEnglishSelected)
                                showToast("Log in successfully", LogInActivity.this);
                            else {
                                showToast("Επιτυχής σύνδεση", LogInActivity.this);
                            }
                            //Toast.makeText(LogInActivity.this,"Log in successfully", Toast.LENGTH_SHORT).show();

                            //finish the activity to prevent going back

                            Intent intent = new Intent(LogInActivity.this, page);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            if (isEnglishSelected)
                                showToast("Failed to log in", LogInActivity.this);
                            else {
                                showToast("Αποτυχία σύνδεσης", LogInActivity.this);
                            }
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

    private void showToast(String message, Context context) {

        // Show the Toast using the passed context
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}