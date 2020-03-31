package com.example.geolocationapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

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

import java.util.ArrayList;

public class IndividualTabFragment extends Fragment {

    private static final String TAG = "StudentDetails";

    private FirebaseDatabase myFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference moduleDatabaseReference;
    private String userID;


    private ListView listView;
    private ValueEventListener listener;
    private ArrayList<String> arrayList;
    private  ArrayAdapter<String> arrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_tab, container, false);

        //call Authentication
        mAuth = FirebaseAuth.getInstance();

        //call Firebase UID
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        //call Database
        myFirebaseDatabase = FirebaseDatabase.getInstance();


        //call Database Reference
        mDatabaseReference = myFirebaseDatabase.getReference();

        //call Module Database Reference
        moduleDatabaseReference = myFirebaseDatabase.getReference("attendance").child("students").child(userID).child("modules");

        //call ListView
        listView = (ListView) view.findViewById(R.id.listviewtxt);

        //call Array
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);


        //Retrieve data from Database
        retrieveModule();

        return view;
    }


    public void retrieveModule(){
        listener = moduleDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String modules = data.getKey();
                    arrayList.add(modules);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}

