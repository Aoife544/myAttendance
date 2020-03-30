package com.example.geolocationapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class CheckInFragment extends Fragment {


    public CheckInFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);


    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_check_in, container, false);

        //Button click opens new activity
        Button button = (Button) view.findViewById(R.id.scanBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openNextActivity();
            }
        });
        return view;

    }

    //Open Scan QR Code Activity Method
    public void openNextActivity() {
        Intent intent = new Intent(getActivity(), OutActivity.class);
        startActivity(intent);
    }


}


