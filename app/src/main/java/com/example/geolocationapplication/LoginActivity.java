package com.example.geolocationapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity  {

    private static final String TAG = "EmailPassword";

    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText emailField;
    private EditText passwordField;
    private FirebaseAuth mAuth;
    private String userID;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Call Input Fields
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);

        //Call Database Reference
        myFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = myFirebaseDatabase.getReference();

        //Call Firebase Authentication Instance
        mAuth = FirebaseAuth.getInstance();

        //Call Firebase Authentication Listener Instance
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        //On Database call, carry out task corresponding with database reference
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    //Get student
                    final String students = ds.child("students").getValue().toString();

                    //Get lecturers
                    final String lecturers = ds.child("lecturers").getValue().toString();

                final Button login = (Button) findViewById(R.id.loginBtn);
                login.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String email = emailField.getText().toString();
                        String password = passwordField.getText().toString();

                        Log.d(TAG, "signIn:" + email);
                        if(!validateForm()){
                            return;
                        }

                        //Email and Password verification
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            //Sign in success
                                            Log.d(TAG, "signInWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if(students.contains(user.getUid())) {
                                                updateUIStudent(user);
                                            }
                                            else if(lecturers.contains(user.getUid())) {
                                                updateUILecturer(user);
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Not registered as student or lecturer",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            //Sign in fails
                                            Log.w(TAG, "signInWithEmail: failure", task.getException());
                                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            updateUIStudent(null);
                                        }

                                        if (!task.isSuccessful()){
                                            Toast.makeText(LoginActivity.this, "Failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }


    //Apply Authentication listener on program start
    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    //Validate email and password fields
    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if(TextUtils.isEmpty(email)){
            emailField.setError("Required.");
            valid = false;
        } else{
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if(TextUtils.isEmpty(password)){
            passwordField.setError("Required.");
            valid = false;
        } else{
            passwordField.setError(null);
        }

        return valid;
    }

    //Update the UI with the Firebase User
    private void updateUIStudent(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent (LoginActivity.this, MainActivity.class);
            startActivity(intent);

        } else {

        }

    }

    //Update the UI with the Firebase User
    private void updateUILecturer(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent (LoginActivity.this, LecturerActivity.class);
            startActivity(intent);

        } else {

        }

    }
}

