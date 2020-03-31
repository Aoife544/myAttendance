package com.example.geolocationapplication;

import android.content.Intent;

public class CourseInfo {

    public String courseName;
    public String s1timetable;
    public String s2timetable;


    public CourseInfo(){

    }

    public CourseInfo (String courseName, String s1timetable, String s2timetable) {
        this.courseName = courseName;
        this.s1timetable = s1timetable;
        this.s2timetable = s2timetable;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getS1timetable() {
        return s1timetable;
    }

    public void setS1timetable(String s1timetable) {
        this.s1timetable = s1timetable;
    }

    public String getS2timetable() {
        return s2timetable;
    }

    public void setS2timetable(String s2timetable) {
        this.s2timetable = s2timetable;
    }
}

