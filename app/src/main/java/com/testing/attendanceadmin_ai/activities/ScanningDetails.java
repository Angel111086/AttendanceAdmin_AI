package com.testing.attendanceadmin_ai.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CameraPreview;
import com.testing.attendanceadmin_ai.R;
import com.testing.attendanceadmin_ai.cam.CamActivity;
import com.testing.attendanceadmin_ai.cam.CameraController;
import com.testing.attendanceadmin_ai.cam.CameraService;
import com.testing.attendanceadmin_ai.cam.MyCamService;
import com.testing.attendanceadmin_ai.cam.TestCamera;
import com.testing.attendanceadmin_ai.newCam.CameraActivity;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class ScanningDetails extends AppCompatActivity {

    LinearLayout ln_scanall, ln_scanmanually;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning_details);
        checkOrientation();;
        init();
        listener();
    }
    public void checkOrientation(){
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    public void init() {
        ln_scanall = findViewById(R.id.ln_scanall);
        ln_scanmanually = findViewById(R.id.ln_scanmanually);
    }

    public void listener() {
        ln_scanall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ScanningDetails.this, QRCodeTestActivity.class);
                startActivity(in);

            }
        });
        ln_scanmanually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(ScanningDetails.this);
                integrator.setCameraId(1);
                integrator.initiateScan();
            }

        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {

            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned:" + result.getContents(), Toast.LENGTH_LONG).show();
                openFrontCamera();
//                String test = result.getContents();
//                Log.e("TestData",test);
//                String COMPANY="";
//
//                try {
//                    JSONObject obj = new JSONObject(test);
//                    COMPANY = obj.getString("COMPANY");
//
//                    if (COMPANY.equals("CONNEKT")) {
//                        Intent in = new Intent(DashboardActivity.this, VehicleDetailsActivity.class);
//                        in.putExtra("ScannedData", result.getContents());
//                        startActivity(in);
//                        //Toast.makeText(this, "Scanned:"+result.getContents(), Toast.LENGTH_LONG).show();
//                        Log.e("Scanned Data",result.getContents());
//
//                    }else{
//                        Toast.makeText(this, "Invalid Data" , Toast.LENGTH_LONG).show();
//                    }
//                }catch (Exception e){e.printStackTrace();}
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void openFrontCamera() {
        Intent in = new Intent(ScanningDetails.this, CameraActivity.class);
        startActivity(in);
    }

}


