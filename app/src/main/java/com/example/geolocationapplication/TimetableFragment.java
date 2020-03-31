package com.example.geolocationapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class TimetableFragment extends Fragment {

    private Button s1Button;
    private Button s2Button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_timetable, container, false);

        s1Button = (Button) view.findViewById(R.id.semester1);
        s1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Semester1Fragment nextFragment = new Semester1Fragment();
                fragmentTransaction.replace(R.id.fragment_container, nextFragment);
                fragmentTransaction.commit();

            }
        });

        s2Button = (Button) view.findViewById(R.id.semester2);
        s2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Semester2Fragment nextFragment = new Semester2Fragment();
                fragmentTransaction.replace(R.id.fragment_container, nextFragment);
                fragmentTransaction.commit();

            }
        });
        return view;

    }
}

