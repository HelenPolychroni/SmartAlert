package com.example.smartalert;

import java.util.ArrayList;
import java.util.List;

public class Incident {

    private String userEmail;
    private String type;
    private String comment;
    private String timestamp;
    private String image;
    private String location;

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

    private int subNumber;


    public Incident() {}

    public Incident(List<String> keys, List<String> comments, List<String> locations, List<String> timestamps,
                    List<String> photos, int subNumber){

        this.keys = keys;
        this.comments = comments;
        this.locations = locations;
        this.timestamps = timestamps;
        this.photos = photos;
        this.subNumber = subNumber;

    }

    public Incident(String userEmail, String type, String comment, String timestamp,String image,String location) {
        this.userEmail = userEmail;
        this.type = type;
        this.comment = comment;
        this.timestamp = timestamp;
        this.image=image;
        this.location=location;
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
}
