package com.example.geolocationapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.text.format.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GenerateQRFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "LecturerDetails";

    private FirebaseDatabase myFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference myDatabaseReference;
    private String userID;

    private TextView lecturerName;
    private Button dateBtn;
    private TextView dateTxt;
    private Button timeBtn;
    private TextView timeTxt;

    private Button generateBtn;

    private Spinner comSpinner;
    private Spinner moduleSpinner;
    private String textData = "";
    private ValueEventListener listener;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> spinnerDataList;
    private ArrayAdapter<String> adapter2;
    private ArrayList<String> spinnerDataList2;
    private String item1;
    private String item2;

    //Date Picker intervals
    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_generate_qr, container, false);

        lecturerName = (TextView) view.findViewById(R.id.lecturerNameText);
        comSpinner = (Spinner) view.findViewById(R.id.COMSpinner);
        moduleSpinner = (Spinner) view.findViewById(R.id.ModuleSpinner);

        comSpinner.setOnItemSelectedListener(this);
        moduleSpinner.setOnItemSelectedListener(this);

        //Date and Time
        dateTxt = (TextView) view.findViewById(R.id.datetext);
        dateBtn = (Button) view.findViewById(R.id.dateBtn);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calender = Calendar.getInstance();
                year = calender.get(Calendar.YEAR);
                month = calender.get(Calendar.MONTH);
                day = calender.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), GenerateQRFragment.this,
                        year, month, day);

                datePickerDialog.show();
            }
        });
        timeTxt = (TextView) view.findViewById(R.id.timetext);
        timeBtn = (Button) view.findViewById((R.id.timeBtn));
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), GenerateQRFragment.this,
                        hour, minute, DateFormat.is24HourFormat(getContext()));
                timePickerDialog.show();
            }
        });

        generateBtn = (Button) view.findViewById(R.id.generatebtn);


        //Data List
        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_dropdown_item1, spinnerDataList);
        spinnerDataList2 = new ArrayList<>();
        adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_dropdown_item1, spinnerDataList2);
        comSpinner.setAdapter(adapter);
        moduleSpinner.setAdapter(adapter2);



        //Call in user information
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        myFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = myFirebaseDatabase.getReference();
        myDatabaseReference = myFirebaseDatabase.getReference("attendance").child("lecturers").child(userID).child("modules");


        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lectureName = lecturerName.getText().toString();
                String dateFinal = dateTxt.getText().toString();
                String timeFinal = timeTxt.getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("COM", item1);
                bundle.putString("Module", item2);
                bundle.putString("LectureName", lectureName);
                bundle.putString("Date", dateFinal);
                bundle.putString("Time", timeFinal);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                EmailQRFragment emailQRFragment = new EmailQRFragment();
                emailQRFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container,emailQRFragment);
                fragmentTransaction.commit();
            }
        });

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        retrieveCOM();
        retrieveModule();

        return view;

    }

    //Method to retrieve student name, student course name and profile image
    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            LecturerInfo lecturerInfo = new LecturerInfo();
            lecturerInfo.setName(ds.child("lecturers").child(userID).getValue(LecturerInfo.class).getName()); //set the name

            lecturerName.setText(lecturerInfo.getName());


        }
    }

    public void retrieveCOM(){
        listener = myDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String keys = data.getKey();
                    spinnerDataList.add(keys);
                    //spinnerDataList.add(data.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void retrieveModule(){
        listener = myDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String children = data.getValue().toString();
                    spinnerDataList2.add(children);
                    //spinnerDataList.add(data.getValue().toString());
                }
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearFinal = year;
        monthFinal = month + 1;
        dayFinal = dayOfMonth;

        String monthStr = String.valueOf(monthFinal);
        if (monthStr.length() == 1){
            monthStr = "0" + monthStr;
        }

        dateTxt.setText(dayFinal + "/" + monthStr + "/" + yearFinal);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hourFinal = hourOfDay;
        minuteFinal = minute;

        String minString = String.valueOf(minuteFinal);
        if (minString.length() == 1){
            minString= "0" + minString;
        }

        timeTxt.setText(hourFinal + ":" + minString);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.COMSpinner)
        {
            item1 = parent.getItemAtPosition(position).toString();

        }else if(parent.getId() == R.id.ModuleSpinner) {

            item2 = parent.getItemAtPosition(position).toString();

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }


}
