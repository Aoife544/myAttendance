package com.example.geolocationapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

public class OutActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private static int TIME_OUT = 1500;

    //Play Services Location
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;//figure can change
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 300193;//figure can change


    private static int UPDATE_INTERVAL = 10000; //5 seconds
        private static int FASTEST_INTERVAL = 3000;
        private static int DISPLACEMENT = 10;

        DatabaseReference ref;
        GeoFire geoFire;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_out);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            setUpLocation();
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
            fetchLocation();
            createLocationRequest();
            buildGoogleApiClient();
        }
    }

        private void fetchLocation() {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                return;
            }
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        //Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        assert supportMapFragment != null;
                        supportMapFragment.getMapAsync(OutActivity.this);
                    }
                }
            });
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

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
            googleMap.addMarker(markerOptions);
            //Move camera to this position
           // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),12.0f));


            //Create dangerous area

            final LatLng dangerous_area = new LatLng(54.8612, -6.3157);
            Circle circle = googleMap.addCircle(new CircleOptions()
                    .center(dangerous_area)
                    .radius(500) //in meters
                    .fillColor(Color.RED)
                    .strokeWidth(0f)
            );

            float[] distance = new float[2];

            Location.distanceBetween(latLng.latitude, latLng.longitude, circle.getCenter().latitude, circle.getCenter().longitude, distance);


            if ( distance[0] <= circle.getRadius())
            {
                //Toast.makeText(getApplicationContext(), "You are in the area", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(OutActivity.this, ScanQRActivity.class);
                        startActivity(intent);
                    }
                },TIME_OUT);
            }
            else
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(OutActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "You are NOT in the university", Toast.LENGTH_SHORT).show();
                    }
                },TIME_OUT);

            }

        }
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case REQUEST_CODE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        fetchLocation();
                        createLocationRequest();
                        buildGoogleApiClient();
                    }
                    break;
            }
        }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
            fetchLocation();
            startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
            currentLocation = location;
            fetchLocation();


    }
}
