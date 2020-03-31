package com.example.geolocationapplication;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;

public class Semester1Fragment extends Fragment {

    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private String userID;
    private String course;

    private static final String TAG = "StudentDetails";

    private ImageView mImageView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_semester1, container, false);

        //Call ImageView Instance
        mImageView = (ImageView) view.findViewById(R.id.timetable);

        //call Database
        myFirebaseDatabase = FirebaseDatabase.getInstance();

        //call Database Reference
        mDatabaseReference = myFirebaseDatabase.getReference();

        //call Authentication
        mAuth = FirebaseAuth.getInstance();

        //call Firebase UID
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userID = firebaseUser.getUid();


        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showImage(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;

    }

    //Method to retrieve timetable image
    private void showImage(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            //Retrieve signed in student's course UCAS Code
            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setuCAS(ds.child("students").child(userID).getValue(StudentInfo.class).getuCAS()); //set the uCAS Code

            //Set String course as uCAS Code
            course = studentInfo.getuCAS();

            //Retrieve semester 1 timetable against uCAS Code
            CourseInfo courseInfo = new CourseInfo();
            courseInfo.setS1timetable(ds.child("courses").child(course).getValue(CourseInfo.class).getS1timetable());

            if (courseInfo.getS1timetable().equals("")) {
                mImageView.setImageResource(R.drawable.not_available);
            } else {
                Glide.with(getContext()).load(courseInfo.getS1timetable()).into(mImageView);
            }

        }

    }

}


