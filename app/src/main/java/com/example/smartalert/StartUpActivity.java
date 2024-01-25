package com.example.smartalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        change_lang_btn = findViewById(R.id.change_lang_btn);

        preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);



        /*
        boolean isEnglishSelected = change_lang_btn.isChecked();
        lang = isEnglishSelected ? "en" : "el"; // Change this to the language code you want to switch to
        System.out.println("Lang:::" + lang);*/


        /*change_lang_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setAppLanguage("en");
                } else {
                    setAppLanguage("el");
                }
                //updateContent();
            }
        });*/

        /*Spinner languageSpinner = findViewById(R.id.languageSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.language_choices,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedLanguage = parentView.getItemAtPosition(position).toString();
                String lang;

                setAppLanguage(selectedLanguage);

                //if (selectedLanguage.equals("GR")) lang = "el"; // Change this to the language code you want to switch to
                //else lang = "en";

                //updateLocale(lang);
                //recreate();


                // Handle language selection (e.g., change app language)
                // You can use a switch statement or an if-else block here
                // For simplicity, let's just print the selected language to the log
                System.out.println("Selected Language: " + selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });*/


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
                    page = Class.forName("com.example.smartalert.UserOptions");
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

    private void setAppLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // You may want to restart the activity or reload resources
        recreate();
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

        // Print the value to the console or log it
        System.out.println("ToggleButton value: " + isEnglishSelected);
        System.out.println("English is " + isEnglishSelected);

        // Change language logic here
        lang = isEnglishSelected ? "en" : "el"; // Change this to the language code you want to switch to

        updateLocale(lang);
        recreate(); // Restart the activity to apply the new configuration

        //SharedPreferences.Editor editor = preferences.edit();
        editor = preferences.edit();
        editor.putBoolean("english", isEnglishSelected);
        editor.apply();
    }

    private void updateLocale(String lang) {
        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(new Locale(lang));
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}