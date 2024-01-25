package com.example.smartalert;

public class Incident {
    private String type;
    private String comment;
    private String timestamp;
    private String image;
    private String location;

    public Incident() {
    }


    public Incident(String type, String comment, String timestamp,String image,String location) {
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
}
