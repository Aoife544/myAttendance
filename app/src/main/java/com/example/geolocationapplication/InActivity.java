package com.example.geolocationapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.GeolocationPermissions;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class InActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;

    //Play Services Location
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;//figure can change
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 300193;//figure can change

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000; //5 seconds
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference ref;
    GeoFire geoFire;

    Marker mCurrent;

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        ref = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire = new GeoFire(ref);

        setUpLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices())
                    {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }break;
        }
    }

    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //Request runtime permission
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        }
        else
        {
            if(checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null)
        {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();


            //Update to Firebase
            geoFire.setLocation("You", new GeoLocation(latitude, longitude),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                            //Add Marker
                            if(mCurrent != null)
                                mCurrent.remove(); //remove old marker
                            mCurrent = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude,longitude))
                                    .title("You"));
                            //Move camera to this position
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),12.0f));
                        }
                    });


            Log.d("EDMTDEV", String.format("Your location was changed : %f / %f", latitude, longitude));
        }
        else
            Log.d("EDMTDEV", "Can not get your location");
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else
            {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }



    private void sendNotification(String title, String content) {

        Intent intent = new Intent(this,InActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE);

        NotificationManager manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= 26)
        {
            //When sdk version is larger than26
            String id = "channel_1";
            String description = "143";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(id, description, importance);
//                     channel.enableLights(true);
//                     channel.enableVibration(true);//
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(InActivity.this, id)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            manager.notify(1, notification);
        }
        else
        {
            //When sdk version is less than 26
            Notification builder = new Notification.Builder(this)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            manager.notify(1, builder);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Current location
        LatLng current_area = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        //Create dangerous area
        final LatLng dangerous_area = new LatLng(54.607868, -5.926437);
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(dangerous_area)
                .radius(500) //in meters
                .fillColor(Color.RED)
                .strokeWidth(0f)
        );

        Toast.makeText(getApplicationContext(), "Testing", Toast.LENGTH_SHORT).show();


        float[] distance = new float[2];

        Location.distanceBetween(current_area.latitude, current_area.longitude, circle.getCenter().latitude, circle.getCenter().longitude, distance);


        if ( distance[0] <= circle.getRadius())
        {
            Toast.makeText(getApplicationContext(), "You are in the area", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "You are NOT in the area", Toast.LENGTH_SHORT).show();
        }

/*
        //Add GeoQuery here
        //0.5f = 0.5km
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(dangerous_area.latitude,dangerous_area.longitude),0.5f);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                sendNotification("EDMTDEV", String.format("%s entered the dangerous area", key));
                }

            @Override
            public void onKeyExited(String key) {
                sendNotification("EDMTDEV", String.format("%s is no longer in the dangerous area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d("MOVE",String.format("%s moved within the dangerous area [%f/%f]",key,location.latitude,location.longitude));

            }

            @Override
            public void onGeoQueryReady() {
                //entire query
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("ERROR",""+error);
            }

        });

*/
    }


    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
