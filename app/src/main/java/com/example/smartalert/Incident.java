package com.example.smartalert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Incident {

    private String userEmail;
    private String type;
    private String comment;
    private String timestamp;
    private String image;
    private String location;
    private String status;

    // for sorted incidents
    // 1. keep incident type
    // 2. comments list
    // 3. locations lit
    // 4. keep timestamp
    // 5. photos list
    // 6. (new) submission number
    private List<String> locations = new ArrayList<>();
    private List<String> comments = new ArrayList<>();
    private List<String> timestamps = new ArrayList<>();
    private List<String> photos = new ArrayList<>();
    private List<String> keys = new ArrayList<>();
    private List<String> usersEmails = new ArrayList<>();

    private int subNumber;


    public Incident() {}

    public Incident(List<String> keys, List<String> comments, List<String> locations, List<String> timestamps,
                    List<String> photos, int subNumber, String status){

        this.keys = keys;
        this.comments = comments;
        this.locations = locations;
        this.timestamps = timestamps;
        this.photos = photos;
        this.subNumber = subNumber;
        this.status = status;

    }

    public Incident(String userEmail, String type, String comment, String timestamp,String image,String location) {
        this.userEmail = userEmail;
        this.type = type;
        this.comment = comment;
        this.timestamp = timestamp;
        this.image=image;
        this.location=location;
    }

    public Incident(List<String> usersEmails, /*String type,*/ String timestamp){
        this.usersEmails = usersEmails;
        //this.type = type;
        this.timestamp = timestamp;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserEmail() {return userEmail;}

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public List<String> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<String> timestamps) {
        this.timestamps = timestamps;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public int getSubNumber() {
        return subNumber;
    }

    public void setSubNumber(int subNumber) {
        this.subNumber = subNumber;
    }

    public List<String> getKeys() {return keys;}

    public void setKeys(List<String> keys) {this.keys = keys;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    public List<String> getUsersEmails() {return usersEmails;}

    public void setUsersEmails(List<String> usersEmails) {this.usersEmails = usersEmails;}

    static boolean isWithinTimeframe(String prevTimestamp, String timestamp, int hours, int minutes) {
        try {
            // Define the timestamp format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.getDefault());

            // Parse the timestamps into Date objects
            Date incidentDate1 = dateFormat.parse(prevTimestamp);
            Date incidentDate2 = dateFormat.parse(timestamp);

            // Check if the incidents are on different dates
            if (!isSameDate(incidentDate1, incidentDate2)) {
                return false;
            }

            // Calculate the difference in minutes
            long diffInMinutes = Math.abs(incidentDate1.getTime() - incidentDate2.getTime()) / (60 * 1000);

            // Check if the difference is less than the specified number of hours and minutes
            return diffInMinutes < (hours * 60 + minutes);
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Return false in case of an error
        }
    }

    // Helper method to check if two Dates are on the same date
    private static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }


    static boolean isWithinDistance(String prevLocation, String location, double distanceKm, double distanceMeters) {
        double distance = calculateDistance(prevLocation, location);
        return distance <= distanceKm || (distance <= (distanceMeters / 1000.0));
    }

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
