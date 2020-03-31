package com.example.geolocationapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

public class ScannedQRFragment extends Fragment  {

    private static final String TAG = "StudentNameDetail";

    private FirebaseDatabase myFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseReference;
    private String userID;

    private ListView myListView;
    private TextView moduleResult;
    private TextView lecturerResult;
    private TextView studentName;
    private TextView moduleName;
    private TextView lecturerName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_scanned_qr, container, false);

        myListView = (ListView) view.findViewById(R.id.txtResult1);
        moduleResult = (TextView) view.findViewById(R.id.txtResult2);
        lecturerResult = (TextView) view.findViewById(R.id.txtResult3);
        studentName = (TextView) view.findViewById(R.id.studentTitle);
        moduleName = (TextView) view.findViewById(R.id.moduleTitle);
        lecturerName = (TextView) view.findViewById(R.id.lecturerTitle);

        mAuth = FirebaseAuth.getInstance();
        myFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = myFirebaseDatabase.getReference();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };



        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    StudentInfo studentInfo = new StudentInfo();
                    studentInfo.setuCAS(ds.child("students").child(userID).getValue(StudentInfo.class).getuCAS());

                    //Get QR Code values
                    String moduleCode = getActivity().getIntent().getStringExtra("Module Code");
                    String module = getActivity().getIntent().getStringExtra("Module Name");
                    String lecturer = getActivity().getIntent().getStringExtra("Lecturer Name");
                    String date = getActivity().getIntent().getStringExtra("Date");
                    String time = getActivity().getIntent().getStringExtra("Time");


                    //Get modules children
                    String key = ds.child("students").child(userID).child("modules").getValue().toString();

                    //Get current date and time
                    Date currentDate = Calendar.getInstance().getTime();

                    //Format date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDate = dateFormat.format(currentDate);

                    //Format time (HH:mm)
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

                    //Check student is enrolled in module and timestamp
                    if (key.contains(moduleCode) && date.contains(formattedDate) && timeTest(time)) {
                        showData(dataSnapshot);
                        mDatabaseReference.child("attendance").child("status").child(userID).child(moduleCode).child(formattedDate + time).setValue("present");
                        moduleResult.setText(module);
                        lecturerResult.setText(lecturer);

                    } else if (key.contains(moduleCode) && date.contains(formattedDate) && !timeTest(time)){
                        moduleResult.setText("You have missed the sign in time");
                        studentName.setVisibility(view.INVISIBLE);
                        moduleName.setVisibility(view.INVISIBLE);
                        lecturerName.setVisibility(view.INVISIBLE);

                    } else if (key.contains(moduleCode) && !date.contains(formattedDate)) {
                        moduleResult.setText("You have missed the sign in date");
                        studentName.setVisibility(view.INVISIBLE);
                        moduleName.setVisibility(view.INVISIBLE);
                        lecturerName.setVisibility(view.INVISIBLE);

                    } else {
                        moduleResult.setText("You are not assigned to this class");
                        studentName.setVisibility(view.INVISIBLE);
                        moduleName.setVisibility(view.INVISIBLE);
                        lecturerName.setVisibility(view.INVISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Button button = (Button) view.findViewById(R.id.completeBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                CheckInFragment nextFragment = new CheckInFragment();
                fragmentTransaction.replace(R.id.fragment_container, nextFragment);
                fragmentTransaction.commit();

            }
        });

        return view;
    }

    public static boolean timeTest(String time) {
        boolean result = true;
        try{
            //Format time (HH:mm)
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            //Get current time
            Date currentDate = Calendar.getInstance().getTime();
            String currentTime = timeFormat.format(currentDate);

            //QR Code time
            Date qrTime = timeFormat.parse(time);

            //Get 30 mins after current time
            Calendar cal = Calendar.getInstance();
            cal.setTime(qrTime);
            cal.add(Calendar.MINUTE, 30);
            String endTime = timeFormat.format(cal.getTime());

            Date endClass = timeFormat.parse(endTime);
            Date nowTime = timeFormat.parse(currentTime);
            if(nowTime.after(qrTime) && nowTime.before(endClass))
            {
                result = true;

            }
            else {
                result = false;
            }

        } catch(Exception e){

        }
        return result;

    }


    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setName(ds.child("students").child(userID).getValue(StudentInfo.class).getName()); //set the name

            //display all the information
            Log.d(TAG, "showData: name: " + studentInfo.getName());

            ArrayList<String> array  = new ArrayList<>();
            array.add(studentInfo.getName());
            ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_2, R.id.textItem1, array);
            myListView.setAdapter(adapter);
        }
    }

    //Firebase user instance
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
