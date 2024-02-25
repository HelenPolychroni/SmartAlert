package com.example.smartalert;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class StartUpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ToggleButton change_lang_btn;
    private static final String PREF_NAME = "User";
    private static final String KEY_ROLE = "UserRole";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String role, email;
    String lang = "en";
    Class<?> page;
    boolean isEnglishSelected = true;
    private TextView textView2, textView3;
    private Button signIn_btn3, signIn_btn4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        change_lang_btn = findViewById(R.id.change_lang_btn);

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode

            System.out.println("Dark mode on");
            textView2 = findViewById(R.id.textView2);
            textView3 = findViewById(R.id.textView3);

            // Change text view colors for dark mode
            textView2.setTextColor(getResources().getColor(R.color.white));
            textView3.setTextColor(getResources().getColor(R.color.white));

            signIn_btn3 = findViewById(R.id.signIn_btn3);
            signIn_btn4 = findViewById(R.id.signIn_btn4);

            signIn_btn3.setTextColor(getResources().getColor(R.color.white));
            signIn_btn4.setTextColor(getResources().getColor(R.color.white));

            change_lang_btn.setBackgroundColor(getResources().getColor(R.color.primary_color_dark));


        }else System.out.println("Light mode on");


        preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);


        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {

            //preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            //role = preferences.getString(KEY_ROLE, "");
            role = firebaseUser.getDisplayName();
            email = firebaseUser.getEmail();

            System.out.println("Role is: " + role);
            System.out.println("Email is: " + email);

            // depending on role
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

            //finish the activity to prevent going back
            finish();
            Intent intent = new Intent(this, page);
            intent.putExtra("lang", lang);
            System.out.println("Lang is:" + lang);
            startActivity(intent);
        }
    }

    // login btn
    public void login(View view){
        Intent intent = new Intent(this, LogInActivity.class);
        //intent.putExtra("lang", isEnglishSelected);
        //System.out.println("Login lang english: " + isEnglishSelected);
        startActivity(intent);
    }

    // signup btn
    public void signup(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        //intent.putExtra("lang", isEnglishSelected);
        //System.out.println("Signup lang english: " + isEnglishSelected);
        startActivity(intent);
    }

    public void changeLanguage(View view) {

        // Retrieve the state of the ToggleButton
        isEnglishSelected = change_lang_btn.isChecked();

        // Save the state in SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("english", isEnglishSelected);
        editor.apply();

        // Print the value to the console or log it
        Log.d("ToggleButton value: ", String.valueOf(isEnglishSelected));
        Log.d("English is ", String.valueOf(isEnglishSelected));

        // Change language logic here
        String lang = isEnglishSelected ? "en" : "el"; // Change this to the language code you want to switch to

        updateLocale(lang);
        recreate(); // Restart the activity to apply the new configuration
    }

    private void updateLocale(String lang) {
        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(new Locale(lang));
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}