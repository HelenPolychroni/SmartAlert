package com.example.smartalert;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText fullname, email, password, confirmpassword, phonenumber;
    String fullname_, email_, password_, confirmpassword_, phonenumber_;
    String role;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    SharedPreferences preferences, sharedPreferences;
    boolean isEnglishSelected;

    // Define a constant for requesting location permission
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private FusedLocationProviderClient fusedLocationClient;
    private String locationString;
    private TextView textView;
    private Button signIn_btn2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve language preference from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isEnglishSelected = preferences.getBoolean("english", true); // Default value is true if key "english" is not found

        // Change language based on the preference
        String lang = isEnglishSelected ? "en" : "el"; // Change this to the language code you want to switch to
        updateLocale(lang);

        setContentView(R.layout.activity_sign_up);

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
            textView = findViewById(R.id.textView);
            textView.setTextColor(getResources().getColor(R.color.white));

            signIn_btn2 = findViewById(R.id.signIn_btn2);
            signIn_btn2.setTextColor(getResources().getColor(R.color.white));
        }

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confpassword);
        phonenumber = findViewById(R.id.phonenum);

        // firebase stuff
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        // preferences
        //preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Retrieve the data from SharedPreferences
        isEnglishSelected = sharedPreferences.getBoolean("english", true);
        System.out.println("SignUp english is: " + isEnglishSelected);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check and request location permissions if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions already granted, get user's location
            getUserLocation();
        }
    }

    private void updateLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    public void SignUpUser(View view){

        if (checkUserInputs()) {
            auth.createUserWithEmailAndPassword(email_, password_)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) { // Firebase Registration Successful

                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {

                                System.out.println("User role is:"+ role );
                                /*SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(KEY_ROLE,role);
                                editor.apply();*/

                                updateUser(firebaseUser, role);

                                try {
                                    saveAllUserInfoToFirebase(firebaseUser.getEmail(), fullname_, phonenumber_, role);
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                if (isEnglishSelected) showToast("Null user");
                                else showToast("Κενός χρήστης");
                            }
                        } else {
                            // Firebase Registration Failed
                            if (isEnglishSelected) showToast("Registration failed!");
                            else showToast("Αποτυχία εγγραφής χρήστη.");
                        }
                    });
        }
    }

    public boolean checkUserInputs(){
        boolean flag = true;

        fullname_ = fullname.getText().toString().trim();
        email_ = email.getText().toString().trim();
        password_ = password.getText().toString().trim();
        confirmpassword_ = confirmpassword.getText().toString().trim();
        phonenumber_ = phonenumber.getText().toString().trim();


        // fullname check
        if (fullname_.isEmpty()){  // check username
            //showMessage("Error","Username cannot be null.");
            if (isEnglishSelected) Toast.makeText(this, "Please enter your fullname", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Παρακαλώ συμπληρώστε το ονοματεπώνυμό σας", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        else if (!isValidFullName(fullname_)) {
            if (isEnglishSelected) Toast.makeText(this,"Invalid fullname", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this,"Μη έγκυρο ονοματεπώνυμο", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        // email check
        if (email_.isEmpty()){  // check username
            //showMessage("Error","Username cannot be null.");
            if (isEnglishSelected) Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Παρακαλώ συμπληρώστε την διεύθυνση ηλ. ταχ. σας", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        else if (!isValidEmail(email_)) {
            if (isEnglishSelected) Toast.makeText(this,"Invalid email address", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this,"Μη έγκυρη διεύθυνση ηλ. ταχ. ", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        else{
            boolean employee = findRoleFromEmail(email_);
            if (employee) role = "employee";
            else role = "user";
        }

        // password check
        if (password_.isEmpty() && confirmpassword_.isEmpty()){
            //showMessage("Error","Password cannot be null.");
            if (isEnglishSelected) Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Παρακαλώ εισάγετε τον κωδικό σας ", Toast.LENGTH_SHORT).show();
            flag = false;

        } else if (!password_.equals(confirmpassword_)){
            if (isEnglishSelected) Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Οι κωδικοί δεν ταιριαζουν", Toast.LENGTH_SHORT).show();
            flag = false;

        }else if (password_.length() < 6){
            if (isEnglishSelected) Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Ο κωδικός πρέπει να αποτελείται από τουλάχιστον 6 χαρακτήρες", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        // phonenumber check
        if (phonenumber_.isEmpty()){  // check username
            //showMessage("Error","Username cannot be null.");
            if (isEnglishSelected) Toast.makeText(this, "Please enter your phonenumber", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Παρακαλώ εισάγετε τον αριθμό κινητού σας", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        else if (!isValidGreekPhoneNumber(phonenumber_)) {
            if (isEnglishSelected) Toast.makeText(this,"Invalid phonenumber", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this,"Μη έγκυρος αριθμός κινητού", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        return  flag;
    }

    private boolean isValidEmail(String email) {
        // Validate that the email is not null and follows a simple email pattern
        // Allowing "admin" as an optional prefix
        String emailPattern = "^(employee)?[\\w\\.-]*@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailPattern);
    }

    private boolean findRoleFromEmail(String email){
        // Check if the word "admin" is present before '@'
        String[] parts = email.split("@");
        if (parts.length > 1) {
            String localPart = parts[0];
            return localPart.toLowerCase().contains("employee");
        }
        return false;
    }

    private boolean isValidFullName(String fullname) {
        // Validate that the full name is not null and contains only letters, hyphens, and at least one space
        String fullNamePattern = "^[a-zA-Z\\-]+( [a-zA-Z\\-]+)+$";
        return fullname != null && fullname.matches(fullNamePattern);
    }

    private boolean isValidGreekPhoneNumber(String phoneNumber) {
        // Define the regular expression pattern
        String regex = "^69\\d{8}$";

        // Compile the pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher with the input phone number
        Matcher matcher = pattern.matcher(phoneNumber);

        // Check if the phone number matches the pattern
        return matcher.matches();
    }

    public void saveAllUserInfoToFirebase(String email, String fullname, String phonenumber, String role) throws ClassNotFoundException {

        // Paradohi tha anagnwrizei ena sigekrimno email ws employee
        String pathString;
        Class<?> page;
        String location = "";

        if (role.equals("employee")) {
            pathString = "employees";
            page = Class.forName("com.example.smartalert.EmployeeHomePage");
        } else {
            pathString = "users";
            location = locationString;
            page = Class.forName("com.example.smartalert.UserHomePage");
        }


        DatabaseReference usersRef = databaseReference.child(pathString).push();
        usersRef.setValue(new User(fullname,email,phonenumber, location))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        if (isEnglishSelected) showToast("Data saved successfully");
                        else showToast("Επιτυχής εγγραφή");
                        //System.out.println("Nickname in register is: " + receivedUser.getNickname());

                        finish();
                        Intent intent = new Intent(this, page);
                        //intent.putExtra("nickname", receivedUser.getNickname());
                        //intent.putExtra("User", receivedUser);

                        startActivity(intent);
                    }
                    else {
                         if (isEnglishSelected) showToast("Error saving data");
                         else showToast("Σφάλμα κατά την αποθήκευση των στοιχείων");
                    }
                });
    }

    // Request location permissions result handler
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permissions granted, get user's location
                getUserLocation();
            } else {
                // Location permissions denied, handle accordingly
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to get user's location
    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Request user's last known location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            // Handle user's location here
                            // For example, you can save it to Firebase
                            locationString = "Lat: " + latitude + ", Long: " + longitude;

                            //Toast.makeText(SignUpActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(SignUpActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to save user's location to Firebase
    private void saveUserLocationToFirebase(String locationString) {
        // Your code to save user's location to Firebase database goes here
    }

    private void updateUser(FirebaseUser user, String role){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(role)
                .build();
        user.updateProfile(request);
    }

    private void showToast(String message) {
        //Context customContext = createConfigurationContext(new Configuration());
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();}
}