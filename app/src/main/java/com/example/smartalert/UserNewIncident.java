package com.example.smartalert;

import static android.app.PendingIntent.getActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class UserNewIncident extends AppCompatActivity implements LocationListener{

    LocationManager locationManager;
    double latitude=0;
    double longitude=0;
    String locationString;
    AutoCompleteTextView auto;
    DatabaseReference database;
    StorageReference storage;
    EditText text;
    String type, comment, timestamp, path;
    Bitmap pic;
    ImageView imageView;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    ActivityResultLauncher<Intent> getPhoto;
    // private static final int STORAGE_PERMISSION_CODE = 1;
    Boolean camera;
    Uri image;
    boolean isEnglishSelected;

    static final int PERMISSION_REQUEST_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve language preference from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isEnglishSelected = preferences.getBoolean("english", true); // Default value is true if key "english" is not found

        // Change language based on the preference
        String lang = isEnglishSelected ? "en" : "el"; // Change this to the language code you want to switch to
        updateLocale(lang);

        setContentView(R.layout.activity_user_new_incident);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        Intent intent=getIntent();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (intent != null) {
            locationString = intent.getStringExtra("location");
        }

        database = FirebaseDatabase.getInstance().getReference().child("incidents");
        storage = FirebaseStorage.getInstance().getReference();
        auto = findViewById(R.id.autoCompleteTextView2);
        text = findViewById(R.id.editTextText);

        camera = false;

        imageView=findViewById(R.id.imageView3);
        int color = ContextCompat.getColor(this, R.color.black);
        imageView.setColorFilter(color);

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
            int color1 = ContextCompat.getColor(this, R.color.white);
            imageView.setColorFilter(color1);

            TextView textView8 = findViewById(R.id.textView8);
            textView8.setTextColor(getResources().getColor(R.color.white));

            Button button6 = findViewById(R.id.button6);
            Button button7 = findViewById(R.id.button7);
            Button button10 = findViewById(R.id.button10);

            button6.setTextColor(getResources().getColor(R.color.white));
            button7.setTextColor(getResources().getColor(R.color.white));
            button10.setTextColor(getResources().getColor(R.color.white));
        }
        launcher();
    }

    private void updateLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
    public void getCurrentLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permission again
            getLocationPermission();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                locationString = "Lat: " + latitude + ", Long: " + longitude;
            }
        }
    }


    public void submit(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = isEnglishSelected ? "Submit Incident" : "Υποβολή Περιστατικού";
        String message = isEnglishSelected ? "Are you sure you want to submit this emergency incident?" : "Είστε σίγουροι ότι θέλετε να υποβάλετε αυτό το επείγον περιστατικό;";
        String positiveButtonText = isEnglishSelected ? "Yes" : "Ναι";
        String negativeButtonText = isEnglishSelected ? "Cancel" : "Ακύρωση";
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (locationString != null) {
                            if (auto.getText().toString().length()!=0) {
                                if (camera == true) {
                                    if (pic != null) {
                                        StorageReference storage1 = storage.child(System.currentTimeMillis() + "." + "jpg");
                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                        pic.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                        byte[] data = byteArrayOutputStream.toByteArray();
                                        storage1.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                storage1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        type = auto.getText().toString();
                                                        comment = text.getText().toString();
                                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
                                                        path = uri.toString();
                                                        timestamp = simpleDateFormat.format(new Date());
                                                        String id = database.push().getKey();
                                                        database.child(id).setValue(new Incident(firebaseUser.getEmail(), type, comment, timestamp, path, locationString));

                                                        if (isEnglishSelected)
                                                            Toast.makeText(UserNewIncident.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                                        else
                                                            Toast.makeText(UserNewIncident.this, "Επιτυχής υποβολή", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if (isEnglishSelected)
                                                    Toast.makeText(UserNewIncident.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                                                else
                                                    Toast.makeText(UserNewIncident.this, "Αποτυχία υποβολής", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        if (isEnglishSelected)
                                            Toast.makeText(UserNewIncident.this, "No picture taken", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(UserNewIncident.this, "Δεν έχει επιλεχθεί φωτογραφία", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    if (image != null) {
                                        StorageReference refrence1 = storage.child(System.currentTimeMillis() + "." + getFileExtension(image));
                                        refrence1.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                refrence1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        if (isEnglishSelected)
                                                            Toast.makeText(UserNewIncident.this, "Successful upload", Toast.LENGTH_SHORT).show();
                                                        else
                                                            Toast.makeText(UserNewIncident.this, "Επιτυχής υποβολή", Toast.LENGTH_SHORT).show();

                                                        type = auto.getText().toString();
                                                        comment = text.getText().toString();
                                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
                                                        path = uri.toString();
                                                        timestamp = simpleDateFormat.format(new Date());
                                                        String id = database.push().getKey();
                                                        database.child(id).setValue(new Incident(firebaseUser.getEmail(), type, comment, timestamp, path, locationString));

                                                        finish();
                                                    }
                                                });

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if (isEnglishSelected)
                                                    Toast.makeText(UserNewIncident.this, "Failure in uploading", Toast.LENGTH_SHORT).show();
                                                else
                                                    Toast.makeText(UserNewIncident.this, "Αποτυχία υποβολής", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    } else {
                                        if (isEnglishSelected)
                                            Toast.makeText(UserNewIncident.this, "Please select photo", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(UserNewIncident.this, "Παρακαλώ επιλέξτε φωτογραφία", Toast.LENGTH_SHORT).show();
                                    }

                                }

                            }else {
                                if (isEnglishSelected)
                                    Toast.makeText(UserNewIncident.this, "Please select type of incident"+auto.getText().toString(), Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(UserNewIncident.this, "Παρακαλώ επιλέξτε κατηγορία περιστατικού"+auto.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (isEnglishSelected)
                                Toast.makeText(UserNewIncident.this, "Please turn on location", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(UserNewIncident.this, "Παρακαλώ ενεργοποιήστε την τοποθεσία σας", Toast.LENGTH_SHORT).show();
                            //getLocationPermission();
                        }

                    }
                });
        builder.setNegativeButton(negativeButtonText , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancels the dialog.
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE
            );
        } else {
            getCurrentLocation();
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        locationString = "Lat: " + latitude + ", Long: " + longitude;
    }
    public void openCamera(View view){
        camera=true;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();
        }

    }
    public void startCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getPicture.launch(takePictureIntent);
    }
    private final ActivityResultLauncher<Intent> getPicture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Bundle extras = result.getData().getExtras();
                if (extras != null) {
                    pic= (Bitmap) extras.get("data");
                    imageView.setImageBitmap(pic);
                    imageView.clearColorFilter();

                }
            }
    );
    public void openGallery(View view){
        startGallery();
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    /*
    private void checkPermission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        } else {
            startGallery();
        }
    }
   */
    //open Gallery
    public void startGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        getPhoto.launch(intent);

    }
    private void launcher(){
        getPhoto=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try{
                            image=result.getData().getData();
                            imageView.setImageURI(image);
                            imageView.clearColorFilter();
                        }catch(Exception e){
                            e.printStackTrace();
                            if (isEnglishSelected)
                                Toast.makeText(UserNewIncident.this,"No image",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(UserNewIncident.this,"Δεν βρέθηκε εικόνα",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted for camera
                startCamera();
            } else {
                if (isEnglishSelected)
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Δεν επιτράπηκε η άδεια στην κάμερα", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
            else {
                if (isEnglishSelected)
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Δεν επιτράπηκε η άδεια στην τοποθεσία", Toast.LENGTH_SHORT).show();
            }
        }
    }
}