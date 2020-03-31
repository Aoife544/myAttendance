package com.example.geolocationapplication;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;

import java.io.IOException;

public class ScanQRActivity extends FragmentActivity {

    SurfaceView cameraPreview;
    TextView txtResult1;
    TextView txtResult2;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    String intentData = "";
    final int RequestCameraPermissionID = 1001;


    //Request camera permission
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
        txtResult1 = (TextView) findViewById(R.id.txtResult1);
        txtResult2 = (TextView) findViewById(R.id.txtResult2);


        //Call Barcode Detector
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        //Call Camera
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setAutoFocusEnabled(true)
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request permission
                    ActivityCompat.requestPermissions(ScanQRActivity.this,
                            new String[] {Manifest.permission.CAMERA},RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            //Receive QR code and decode array results
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if (qrcodes.size() != 0)
                {
                    txtResult1.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            intentData = qrcodes.valueAt(0).rawValue;

                            String [] items = intentData.split(",");
                            txtResult1.setText(items[0]);

                            Intent intent = new Intent(ScanQRActivity.this, Main2Activity.class);
                            items[0] = txtResult1.getText().toString();
                            intent.putExtra("Module Code",items[0]);
                            intent.putExtra("Module Name",items[1]);
                            intent.putExtra("Lecturer Name", items[2]);
                            intent.putExtra("Date", items[3]);
                            intent.putExtra("Time", items[4]);
                            startActivity(intent);

                            cameraSource.stop();

                        }
                    });
                }
            }
        });
    }



}
