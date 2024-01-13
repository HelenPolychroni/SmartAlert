package com.example.smartalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText fullname, email, password, confirmpassword, phonenumber;
    String fullname_, email_, password_, confirmpassword_, phonenumber_;
    String role;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    SharedPreferences preferences;
    private static final String PREF_NAME = "User";
    private static final String KEY_ROLE = "UserRole";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.password_login);
        phonenumber = findViewById(R.id.email_login);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

    }

    public void SignUpUser(View view){

        if (checkUserInputs()) {
            auth.createUserWithEmailAndPassword(email_, password_)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) { // Firebase Registration Successful

                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {

                                System.out.println("User role is:"+ role );
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(KEY_ROLE,role);
                                editor.apply();

                                try {
                                    saveAllUserInfoToFirebase(firebaseUser.getEmail(), fullname_, phonenumber_, role);
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }

                            } else showToast("Null user");
                        } else {
                            // Firebase Registration Failed
                            showToast("Registration failed!");
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
            Toast.makeText(this, "Please enter your fullname", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        else if (!isValidFullName(fullname_)) {
            Toast.makeText(this,"Invalid fullname", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        // email check
        if (email_.isEmpty()){  // check username
            //showMessage("Error","Username cannot be null.");
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        else if (!isValidEmail(email_)) {
            Toast.makeText(this,"Invalid email address", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            flag = false;

        } else if (!password_.equals(confirmpassword_)){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            flag = false;

        }else if (password_.length() < 6){
            Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        // phonenumber check
        if (phonenumber_.isEmpty()){  // check username
            //showMessage("Error","Username cannot be null.");
            Toast.makeText(this, "Please enter your phonenumber", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        else if (!isValidGreekPhoneNumber(phonenumber_)) {
            Toast.makeText(this,"Invalid phonenumber", Toast.LENGTH_SHORT).show();
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
        // Validate that the phone number is not null and follows the Greek phone number format
        String phonePattern = "^\\+30 \\d{10}$";
        return phoneNumber != null && phoneNumber.matches(phonePattern);
    }

    public void saveAllUserInfoToFirebase(String email, String fullname, String phonenumber, String role) throws ClassNotFoundException {

        // Paradohi tha anagnwrizei ena sigekrimno email ws employee
        String pathString;
        Class<?> page;

        if (role.equals("employee")) {
            pathString = "employees";
            page = Class.forName("com.example.smartalert.EmployeeHomePage");
        } else {
            pathString = "users";
            page = Class.forName("com.example.smartalert.UserHomePage");
        }

        DatabaseReference usersRef = databaseReference.child(pathString).push();
        usersRef.setValue(new User(fullname,email,phonenumber))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        showToast("Data saved successfully");
                        //System.out.println("Nickname in register is: " + receivedUser.getNickname());
                        Intent intent = new Intent(this, page);
                        //intent.putExtra("nickname", receivedUser.getNickname());
                        //intent.putExtra("User", receivedUser);

                        startActivity(intent);
                    }
                    else {showToast("Error saving data");}
                });
    }


    private void showToast(String message) {Toast.makeText(this, message, Toast.LENGTH_SHORT).show();}
}