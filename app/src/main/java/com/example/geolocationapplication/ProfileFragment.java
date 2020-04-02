package com.example.geolocationapplication;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "StudentDetails";
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseDatabase myFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseReference;
    private String userID;

    private ListView mListView;
    private ImageView mProfileImage;

    private Uri mImageUri;
    private StorageTask uploadTask;
    private StorageReference mStorage;

    private static final int IMAGE_REQUEST = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //call UI entities
        mListView = (ListView) view.findViewById(R.id.listview);
        mProfileImage = (ImageView) view.findViewById(R.id.profileImage);

        view.findViewById(R.id.logout).setOnClickListener(this);
        view.findViewById(R.id.profileImage).setOnClickListener(this);

        //call Authentication
        mAuth = FirebaseAuth.getInstance();
        myFirebaseDatabase = FirebaseDatabase.getInstance();

        //Database Reference
        mDatabaseReference = myFirebaseDatabase.getReference();

        //Storage Reference
        mStorage = FirebaseStorage.getInstance().getReference("uploads");

        //UID Reference
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        //call Authentication Listener
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
                    toastMessage("See you soon");
                }

            }
        };

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                showData(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;

    }

    //Method to retrieve profile image
    private void showImage(DataSnapshot dataSnapshot) {

        StudentInfo user = dataSnapshot.getValue(StudentInfo.class);
        if (user.getImageURL().equals("default")) {
            mProfileImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(getContext()).load(user.getImageURL()).into(mProfileImage);
        }

    }



    //Method to retrieve student name, student course name and profile image
    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setName(ds.child("students").child(userID).getValue(StudentInfo.class).getName()); //set the name
            studentInfo.setCourseName(ds.child("students").child(userID).getValue(StudentInfo.class).getCourseName()); //set the course
            studentInfo.setImageURL(ds.child("students").child(userID).getValue(StudentInfo.class).getImageURL());


            //display all the information
            Log.d(TAG, "showData: name: " + studentInfo.getName());
            Log.d(TAG, "showData: courseName: " + studentInfo.getCourseName());
            Log.d(TAG, "showData: imageURL: " + studentInfo.getImageURL());

            ArrayList<String> array  = new ArrayList<>();
            array.add(studentInfo.getName());
            array.add(studentInfo.getCourseName());


            ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_1, R.id.textItem1, array);
            mListView.setAdapter(adapter);

            if (studentInfo.getImageURL().equals("default")) {
                mProfileImage.setImageResource(R.mipmap.ic_launcher);
            } else {
                Glide.with(getContext()).load(studentInfo.getImageURL()).into(mProfileImage);
            }

        }
    }

    //Open image library method
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    //Handle file extension of image
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImg() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading Image");
        progressDialog.show();

        if (mImageUri != null) {
            final StorageReference fileReference = mStorage.child(System.currentTimeMillis()
                    +"."+getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("attendance").child("students").child(userID);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        mDatabaseReference.updateChildren(map);

                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImg();
            }
        }
    }

    //Firebase user instance
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //Welcome message
    private void toastMessage(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }


    //Method used to open Test Activity
    private void showDetails() {
        updateUI(null);
        Intent intent = new Intent (getActivity(), LecturerActivity.class);
        startActivity(intent);
    }

    //Sign out method
    private void signOut() {
        mAuth.signOut();
        updateUI(null);
        Intent intent = new Intent (getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    //Update Profile UI
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Button button = (Button) getView().findViewById(R.id.logout);
            button.setVisibility(View.VISIBLE);

        } else {
            Button button = (Button) getView().findViewById(R.id.logout);
            button.setVisibility(View.GONE);
        }
    }



    //OnClick methods (logout and upload image)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                signOut();
                break;
            case R.id.profileImage:
                openImage();
                break;
        }
        //ADD SWITCH CASE
    }

}

//References: https://www.youtube.com/watch?v=gqIWrNitbbk

