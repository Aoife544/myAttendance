package com.example.geolocationapplication;


import android.content.Intent;

public class StudentInfo {

    public String name;
    public String courseName;
    public String imageURL;
    public String uCAS;


    public StudentInfo(){

    }

    public StudentInfo(String name, String courseName, String imageURL, String uCAS) {
        this.name = name;
        this.courseName = courseName;
        this.imageURL = imageURL;
        this.uCAS = uCAS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getuCAS() {
        return uCAS;
    }

    public void setuCAS(String uCAS) {
        this.uCAS = uCAS;
    }
}
