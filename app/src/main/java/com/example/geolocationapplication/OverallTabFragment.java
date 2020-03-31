package com.example.geolocationapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OverallTabFragment extends Fragment {

    private ProgressBar progressBar;
    int attendance = 0;
    double class_total = 0;
    double classes_attended = 0;
    double class_attendance = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overall_tab, container, false);

        return view;
        // classes_attended = 6;
        // class_total = 10;

        // class_attendance = (classes_attended/class_total)*100;

        // attendance = (int) class_attendance;

        //  progressBar.setProgress(attendance);
    }
}
