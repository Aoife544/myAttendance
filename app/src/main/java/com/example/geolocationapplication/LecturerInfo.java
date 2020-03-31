package com.example.geolocationapplication;


import android.content.Intent;

public class LecturerInfo {

    public String name;
    public String title;
    public String imageURL;


    public LecturerInfo(){

    }

    public LecturerInfo(String name, String title, String imageURL) {
        this.name = name;
        this.title = title;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}
