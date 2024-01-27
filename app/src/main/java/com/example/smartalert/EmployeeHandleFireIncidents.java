package com.example.smartalert;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EmployeeHandleFireIncidents {

    FirebaseDatabase database;
    static DatabaseReference incidentsRef;
    static DatabaseReference sortedIncidentsRef;

    public EmployeeHandleFireIncidents() {
        System.out.println("Constructor has been called!");

        database = FirebaseDatabase.getInstance();
        incidentsRef = database.getReference("incidents");
        sortedIncidentsRef = database.getReference("sorted_incidents");

        // method to search and store incidents
        //sortedIncidentsRef.removeValue();
        findAndStoreIncidents();
    }


    private static void findAndStoreIncidents() {
        //sortedIncidentsRef.removeValue();
        incidentsRef.orderByChild("type").equalTo("Fire").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                processAndStoreIncidents(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching data", databaseError.toException());
            }
        });
    }

    private static void processAndStoreIncidents(DataSnapshot dataSnapshot) {
        //Map<String, Integer> userSubmissionCount = new HashMap<>(); // use 4 subNumber
        //sortedIncidentsRef.removeValue();
        DataSnapshot prevIncidentSnapshot = null;
        String prevUserEmail = null;
        String prevLocation = null;

        Incident prevIncident = null;

        /*List<String> comments = new ArrayList<>();
        List<String> locations = new ArrayList<>();
        List<String> timestamps = new ArrayList<>();
        List<String> photos = new ArrayList<>();*/

        String type_ = "Fire";

        /*for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
            System.out.println("Incident: " + incidentSnapshot);

            String type      = incidentSnapshot.child("type").getValue(String.class);
            String userEmail = incidentSnapshot.child("userEmail").getValue(String.class);
            String timestamp = incidentSnapshot.child("timestamp").getValue(String.class);
            String location  = incidentSnapshot.child("location").getValue(String.class);
            String comment   = incidentSnapshot.child("comment").getValue(String.class);
            String image     = incidentSnapshot.child("image").getValue(String.class);




                if (prevIncidentSnapshot == null) { // first examination case
                    System.out.println("Timestamp: " + timestamp);
                    System.out.println("Email" + userEmail);

                    userSubmissionCount.put(userEmail, 1);
                    comments.add(comment);
                    locations.add(location);
                    timestamps.add(timestamp);
                    photos.add(image);
                }


                // Εάν δεν είναι το πρώτο περιστατικό, ελέγξτε τη διαφορά ώρας με το προηγούμενο
                if (prevIncidentSnapshot != null) {
                    String prevTimestamp = prevIncidentSnapshot.child("timestamp").getValue(String.class);
                    if ((isWithin24Hours(prevTimestamp, timestamp) && (isWithin80Km(prevLocation, location)))) {
                        assert userEmail != null;
                        if (!userEmail.equals(prevUserEmail) && !userSubmissionCount.containsKey(userEmail)) {
                            System.out.println("Has fire requirements");

                            userSubmissionCount.put(userEmail, userSubmissionCount.getOrDefault(userEmail, 0) + 1);
                            comments.add(comment);
                            locations.add(location);
                            timestamps.add(timestamp);
                            photos.add(image);

                            // Εφαρμόστε την λογική για τη διαφορά ώρας μεταξύ περιστατικών
                            // Εδώ μπορείτε να προσθέσετε την κατάλληλη λογική
                            // Π.χ., αυξήστε τον μετρητή, αποθηκεύστε δεδομένα κλπ.
                        }
                    }
                }

                prevIncidentSnapshot = incidentSnapshot;
                prevUserEmail = userEmail;
                prevLocation = location;
        }
        // find number of submissions
        int numberOfEntries = userSubmissionCount.size();
        System.out.println("Number of submissions is: " + numberOfEntries);
        System.out.println(userSubmissionCount);

        // save them to firebase
        Incident incident = new Incident(type_, comments, locations, timestamps, photos, numberOfEntries);
        sortedIncidentsRef.removeValue();
        saveDataInSortedIncidents(incident);*/

        for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
            Map<String, Integer> userSubmissionCount = new HashMap<>(); // use 4 subNumber

            List<String> comments = new ArrayList<>();
            List<String> locations = new ArrayList<>();
            List<String> timestamps = new ArrayList<>();
            List<String> photos = new ArrayList<>();

            System.out.println("Incident: " + incidentSnapshot);

            //String type      = incidentSnapshot.child("type").getValue(String.class);
            String userEmail = incidentSnapshot.child("userEmail").getValue(String.class);
            String timestamp = incidentSnapshot.child("timestamp").getValue(String.class);
            String location = incidentSnapshot.child("location").getValue(String.class);
            String comment = incidentSnapshot.child("comment").getValue(String.class);
            String image = incidentSnapshot.child("image").getValue(String.class);

            userSubmissionCount.put(userEmail, 1);
            comments.add(comment);
            locations.add(location);
            timestamps.add(timestamp);
            photos.add(image);

            for (DataSnapshot incidentSnapshotInner : dataSnapshot.getChildren()) {
                String userEmailInner = incidentSnapshotInner.child("userEmail").getValue(String.class);
                String timestampInner = incidentSnapshotInner.child("timestamp").getValue(String.class);
                String locationInner = incidentSnapshotInner.child("location").getValue(String.class);
                String commentInner = incidentSnapshotInner.child("comment").getValue(String.class);
                String imageInner = incidentSnapshotInner.child("image").getValue(String.class);

                if ((isWithin24Hours(timestamp, timestampInner) && (isWithin80Km(location, locationInner)))) {
                    assert userEmail != null;
                    if (!userEmail.equals(userEmailInner) && !userSubmissionCount.containsKey(userEmailInner)) {
                        System.out.println("Has fire requirements");

                        userSubmissionCount.put(userEmailInner, userSubmissionCount.getOrDefault(userEmailInner, 0) + 1);
                        comments.add(commentInner);
                        locations.add(locationInner);
                        timestamps.add(timestampInner);
                        photos.add(imageInner);

                    }

                }
            }
            // find number of submissions
            int numberOfEntries = userSubmissionCount.size();
            System.out.println("Number of submissions is: " + numberOfEntries);
            System.out.println(userSubmissionCount);

            // save them to firebase
            Incident incident = new Incident(comments, comments, locations, timestamps, photos, numberOfEntries);

            if (prevIncident == null) {
                saveDataInSortedIncidents(incident);
                prevIncident = incident;
            }
            else
                saveDataInSortedIncidentsv1(incident);
        }
    }

    private static boolean isWithin24Hours(String prevTimestamp, String timestamp) {
        try {
            // Ορίζουμε το format του timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.getDefault());

            // Μετατρέπουμε τα timestamp σε αντικείμενα Date
            Date incidentDate1 = dateFormat.parse(prevTimestamp);
            Date incidentDate2 = dateFormat.parse(timestamp);

            // Ελέγχουμε αν τα περιστατικά είναι σε διαφορετικές ημερομηνίες
            if (incidentDate1.getDate() != incidentDate2.getDate() || incidentDate1.getMonth() != incidentDate2.getMonth() || incidentDate1.getYear() != incidentDate2.getYear()) {
                return false;
            }

            // Υπολογίζουμε τη διάφορα σε ώρες
            long diffInHours = Math.abs(incidentDate1.getTime() - incidentDate2.getTime()) / (60 * 60 * 1000);

            // Ελέγχουμε αν η διάφορα είναι μικρότερη από 24
            return diffInHours < 24;
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Σε περίπτωση σφάλματος επιστρέφουμε false
        }
    }

    private static boolean isWithin80Km(String prevLocation, String location) {

        double distance = calculateDistance(prevLocation, location);
        return distance <= 80;
    }

    private static void saveDataInSortedIncidents(Incident incident){
        sortedIncidentsRef.push().setValue(incident).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Incident has been saved successfully");
            }
        });
    }

    private static void saveDataInSortedIncidentsv1(Incident incident) {
        System.out.println("\n\n");

        System.out.println("Incidence to examine (comments) " + incident.getComments());


        sortedIncidentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isDuplicate = false;

                for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
                    Incident existingIncident = incidentSnapshot.getValue(Incident.class);

                    //System.out.println("Incident snapshot: " + incidentSnapshot);
                    if (existingIncident != null && isSameIncident(existingIncident, incident)) {
                        System.out.println("Duplicate");
                        System.out.println("Do not save incidence with comments" + incident.getComments());

                        isDuplicate = true;
                        break;
                    }
                }

                if (!isDuplicate) {
                    System.out.println("Not duplicate");
                    System.out.println("Incident to be saved: " + incident.getComments());
                    // Save the new incident if it's not a duplicate
                    sortedIncidentsRef.push().setValue(incident).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            System.out.println("Incident has been saved successfully");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error checking for duplicates", databaseError.toException());
            }
        });
    }

    private static boolean isSameIncident(Incident existingIncident, Incident newIncident) {
        System.out.println("IsSameIncident method");
        // Compare relevant fields such as timestamps, locations, and user emails
        Collections.sort(existingIncident.getTimestamps());
        Collections.sort(newIncident.getTimestamps());
        //System.out.println("Timestamp1" + existingIncident.getTimestamps());
        //System.out.println("Timestamp2" + newIncident.getTimestamps());


        /*Collections.sort(existingIncident.getLocations());
        Collections.sort(newIncident.getLocations());

        Collections.sort(existingIncident.getPhotos());
        Collections.sort(newIncident.getPhotos());

        Collections.sort(existingIncident.getComments());
        Collections.sort(newIncident.getComments());*/

        return existingIncident.getTimestamps().containsAll(newIncident.getTimestamps())
                && existingIncident.getLocations().containsAll(newIncident.getLocations())
                && existingIncident.getPhotos().containsAll(newIncident.getPhotos())
                && existingIncident.getComments().containsAll(newIncident.getComments());
    }

    /*private void saveDataInSortedIncidents(Map<String, Integer> userSubmissionCount) {
        // save date in sorted_incidents
        for (Map.Entry<String, Integer> entry : userSubmissionCount.entrySet()) {
            String userEmail = entry.getKey();
            int submissionCount = entry.getValue();

            Map<String, Object> incidentData = new HashMap<>();
            incidentData.put("type", "fire");
            incidentData.put("number_of_submission", submissionCount);

            // Εδώ μπορείτε να προσθέσετε περισσότερα πεδία όπως "photos" και "comments"

            // Προσθήκη των δεδομένων στην sorted_incidents
            sortedIncidentsRef.push().setValue(incidentData);
        }
    }*/

    private static double calculateDistance(String location1, String location2) {
        // Εξαγωγή των γεωγραφικών συντεταγμένων από τα strings
        double lat1 = extractCoordinate(location1, "Lat");
        //System.out.println("lat1: " + lat1);
        double lon1 = extractCoordinate(location1, "Long");
        //System.out.println("lon1: " + lon1);
        double lat2 = extractCoordinate(location2, "Lat");
        double lon2 = extractCoordinate(location2, "Long");

        // Υπολογισμός της απόστασης με τον τύπο Haversine
        return haversine(lat1, lon1, lat2, lon2);
    }

    // Εξαγωγή των γεωγραφικών συντεταγμένων από τα strings
    private static double extractCoordinate(String location, String coordinateType) {
        //String[] parts = location.split(": ")[1].split(", Long: ");
        //[1].split(",");
        /*double[] parts1 = Arrays.stream(location.replaceAll("[^\\d.,-]", "").split(", "))
                .mapToDouble(Double::parseDouble)
                .toArray();*/

        String[] parts = location.replaceAll("[^\\d.-]+", " ").trim().split("\\s+");
        double lat = 0, lon = 0;

        // Ensure we have at least two parts
        if (parts.length >= 2) {
            lat = Double.parseDouble(parts[0]);
            lon = Double.parseDouble(parts[1]);
        }
        //System.out.println("parts: " + Arrays.toString(parts));
        //System.out.println("paers[0]: " + parts[1].split(",")[0]);
        return coordinateType.equals("Lat") ? lat : lon;
    }

    // Υπολογισμός της απόστασης με τον τύπο Haversine
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Ακτίνα της Γης σε χιλιόμετρα

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Επιστροφή της απόστασης σε χιλιόμετρα
    }
}
