package com.example.geolocationapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.zxing.MultiFormatWriter;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EmailQRFragment extends Fragment {

    private EditText text;
    private Button button;
    private ImageView image;
    private String text2qr;
    private TextView text_view;
    private Bitmap bitmap;

    private OutputStream outputStream;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_email_qr, container, false);

        text = (EditText) view.findViewById(R.id.edit_text);
        button = (Button) view.findViewById(R.id.sendbtn);
        image = (ImageView) view.findViewById(R.id.imageV);
        text_view = (TextView) view.findViewById(R.id.text_view);

        Bundle bundle = getArguments();

        String COM = bundle.getString("COM");
        String module = bundle.getString("Module");
        String lectureName = bundle.getString("LectureName");
        String date = bundle.getString("Date");
        String time = bundle.getString("Time");

        text_view.setText(COM + ", " + module + ", " + lectureName + ", " + date + ", " + time);


        text2qr = text_view.getText().toString().trim();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(text2qr, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            image.setImageBitmap(bitmap);
        }
        catch(WriterException e){
            e.printStackTrace();
        }



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if(ActivityCompat.checkSelfPermission(getContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {

                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission,WRITE_EXTERNAL_STORAGE_CODE);
                    }

                    else {
                        saveimage();
                    }
                }


            }
        });

        return view;
    }



    private void saveimage() {
        bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

        String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path+"/Pictures");
        dir.mkdirs();
        String imagename = time+".PNG";
        File file = new File(dir,imagename);
        Uri imageuri = FileProvider.getUriForFile(getContext(), "com.example.geolocationapplication.provider", file);
        OutputStream out;

        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
            out.flush();
            out.close();

            Toast.makeText(getContext(), "Image Saved In Pictures", Toast.LENGTH_SHORT).show();
            sendEmail(imageuri);




        } catch (Exception e){

            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }



    protected void sendEmail(Uri uri) {
        String editTxt = text.getText().toString();
        Log.i("Send email", "");
        String[] TO = {editTxt};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "QR Code Generator");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Find the generated QR code attached");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_CODE:{

                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){

                }
                else {
                    Toast.makeText(getContext(), "Permission enable", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

