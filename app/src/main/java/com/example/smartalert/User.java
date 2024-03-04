package com.example.smartalert;

import java.io.Serializable;

public class User implements Serializable {
    protected String fullname, email, password, phonenumber;

    protected String location;
    protected String registrationToken;
    //protected int phonenumber;

    public User(String fullname, String email, String phonenumber, String location,
                String registrationToken) {
        setFullname(fullname);
        setEmail(email);
        //setPassword(password);
       setPhonenumber(phonenumber);
       setLocation(location);

       setRegistrationToken(registrationToken);
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getLocation() {return location;}

    public void setLocation(String location) {this.location = location;}

    public String getRegistrationToken() {return registrationToken;}

    public void setRegistrationToken(String registrationToken) {this.registrationToken = registrationToken;}

    /*
    public String getFullname() {return fullname;}

    public void setFullname(String fullname) {
        if (isValidFullname(fullname)) {this.fullname = fullname;}
        else {
            // Handle invalid full name (throw an exception, log a message, etc.)
            throw new IllegalArgumentException("Invalid full name");
        }
    }

    private boolean isValidFullname(String fullname) {
        // Validate that the full name is not null, contains only letters, hyphens, and has at least one space
        return fullname != null && fullname.matches("[a-zA-Z\\-]+\\s[a-zA-Z\\-]+");
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {
        if (isValidEmail(email)) {this.email = email;}
        else {
            // Handle invalid email (throw an exception, log a message, etc.)
            throw new IllegalArgumentException("Invalid email address");
        }
    }

    private boolean isValidEmail(String email) {
        // Regular expression for a simple email validation
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && Pattern.matches(emailPattern, email);
    }

    public String getPassword() {return password;}

    public void setPassword(String password) {
        if (isValidPassword(password)) {this.password = password;}
        else {
            // Handle invalid password (throw an exception, log a message, etc.)
            throw new IllegalArgumentException("Invalid password. Password must be at least 6 characters");
        }
    }

    private boolean isValidPassword(String password) {
        // Check if the password is not null and has at least 6 characters
        return password != null && password.length() >= 6;
    }

    public int getPhonenumber() {return phonenumber;}

    public void setPhonenumber(int phonenumber) {
        if (isValidGreekPhoneNumber(String.valueOf(phonenumber))) {this.phonenumber = phonenumber;}
        else {
            // Handle invalid phone number (throw an exception, log a message, etc.)
            throw new IllegalArgumentException("Invalid Greek phone number");
        }
    }

    private boolean isValidGreekPhoneNumber(String phoneNumber) {
        // Validate that the phone number is not null and follows the Greek phone number format
        String phonePattern = "^\\+30 \\d{10}$";
        return phoneNumber != null && phoneNumber.matches(phonePattern);
    }*/
}
