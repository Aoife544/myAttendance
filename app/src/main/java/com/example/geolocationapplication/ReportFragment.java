package com.example.geolocationapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class ReportFragment extends Fragment {

    FrameLayout overall_frame, individual_frame;
    View view1, view2;
    TextView tab_overall, tab_individual;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        //INIT VIEWS
        init(view);

        //SET TABS ONCLICK
        overall_frame.setOnClickListener(clik);
        individual_frame.setOnClickListener(clik);

        //LOAD PAGE FOR FIRST
        loadPage(new OverallTabFragment());
        tab_overall.setTextColor(getActivity().getResources().getColor(R.color.colorLightBlue));

        return view;

    }

    public void init(View v){
        overall_frame = v.findViewById(R.id.overall);
        individual_frame = v.findViewById(R.id.individual);
        view1 = v.findViewById(R.id.view1);
        view2 = v.findViewById(R.id.view2);
        tab_overall = v.findViewById(R.id.tab_overall);
        tab_individual = v.findViewById(R.id.tab_individual);
    }


    //ONCLICK LISTENER
    public View.OnClickListener clik = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.overall:


                    loadPage(new OverallTabFragment());
                    //WHEN CLICK TEXT COLOR CHANGED
                    tab_overall.setTextColor(getActivity().getResources().getColor(R.color.colorLightBlue));
                    tab_individual.setTextColor(getActivity().getResources().getColor(R.color.colorBlack));
                    //VIEW VISIBILITY WHEN CLICKED
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.INVISIBLE);
                    break;
                case R.id.individual:


                    loadPage(new IndividualTabFragment());

                    //WHEN CLICK TEXT COLOR CHANGED
                    tab_overall.setTextColor(getActivity().getResources().getColor(R.color.colorBlack));
                    tab_individual.setTextColor(getActivity().getResources().getColor(R.color.colorLightBlue));
                    tab_individual.setTextColor(getActivity().getResources().getColor(R.color.colorLightBlue));
                    //VIEW VISIBILITY WHEN CLICKED
                    view1.setVisibility(View.INVISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    //LOAD PAGE FRAGMENT METHOD
    private boolean loadPage(Fragment fragment) {
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerpage, fragment)
                    .commit();
            return true;
        }
        return false;
    }

}

