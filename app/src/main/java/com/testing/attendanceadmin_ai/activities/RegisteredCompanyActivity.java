package com.testing.attendanceadmin_ai.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.testing.attendanceadmin_ai.AppPreference.AttendanceAdminSP;
import com.testing.attendanceadmin_ai.R;
import com.testing.attendanceadmin_ai.appservice.LocationTrack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RegisteredCompanyActivity extends AppCompatActivity {

    LinearLayout ln_attendance, ln_logout, layout_home,ln_scan,ln_generate,layout_addEmployee,layout_bulkUpload_drawer;
    DrawerLayout drawer_layout;
    ImageView img_navigation,img_profile;
    TextView username,usernumber,usercompany,userlocation;

    private ArrayList<String> permissionsToRequest = new ArrayList<>();
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_company);
        checkOrientation();
        init();
        listener();
        //getLocation();
    }
    public void checkOrientation(){
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    private void init() {
        ln_attendance=(LinearLayout)findViewById(R.id.ln_attendance);
        drawer_layout=(DrawerLayout)findViewById(R.id.drawer_layout);
        img_navigation=(ImageView)findViewById(R.id.img_navigation);
        ln_logout=(LinearLayout)findViewById(R.id.ln_logout);
        layout_home=(LinearLayout)findViewById(R.id.layout_home);
        img_profile = findViewById(R.id.img_profile);
        username = findViewById(R.id.username);
        usernumber = findViewById(R.id.usernumber);
        usercompany = findViewById(R.id.usercompany);
        userlocation = findViewById(R.id.userlocation);
        ln_scan = findViewById(R.id.ln_scan);
        ln_generate = findViewById(R.id.ln_generate);
        layout_addEmployee = findViewById(R.id.layout_addEmployee_drawer);
        layout_bulkUpload_drawer = findViewById(R.id.layout_bulkUpload_drawer);
        username.setText(AttendanceAdminSP.loadCompanyNameFromPreferences(RegisteredCompanyActivity.this));
        usernumber.setText(AttendanceAdminSP.loadMobileFromPreferences(RegisteredCompanyActivity.this));
        //usercompany.setText(AttendanceAdminSP.(RegisteredCompanyActivity.this));
        userlocation.setText(AttendanceAdminSP.loadSiteLocationFromPreferences(RegisteredCompanyActivity.this));
    }

    private void listener() {

        ln_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(RegisteredCompanyActivity.this,ScanningDetails.class);
                startActivity(in);

            }
        });

        ln_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(RegisteredCompanyActivity.this,QRCodeActivity.class);
                startActivity(in);
            }
        });
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Profile","Test");
                startActivity(new Intent(RegisteredCompanyActivity.this,SiteProfileActivity.class));
            }
        });

        img_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(GravityCompat.START);
                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromInputMethod(v.getWindowToken(),0);
            }
        });

        layout_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.closeDrawers();
                Toast.makeText(RegisteredCompanyActivity.this, "Home", Toast.LENGTH_SHORT).show();
            }
        });

//        ln_attendance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //startActivity(new Intent(DashboardActivity.this, QRCodeActivity.class));
//            }
//        });

        layout_addEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.closeDrawers();
                //Toast.makeText(RegisteredCompanyActivity.this, "Test", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisteredCompanyActivity.this,AddEmployeeActivity.class));
            }
        });
        ln_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisteredCompanyActivity.this, LoginActivity.class));
                finishAffinity();

                AttendanceAdminSP.saveCompanyStatusIdToPreferences(RegisteredCompanyActivity.this, "NA");
                AttendanceAdminSP.saveSiteIdToPreferences(RegisteredCompanyActivity.this,"NA" );
                AttendanceAdminSP.saveUserLatituteToPreferences(RegisteredCompanyActivity.this, "NA");
                AttendanceAdminSP.saveUserLongitudeToPreferences(RegisteredCompanyActivity.this,"NA");
                AttendanceAdminSP.saveCompanyNameToPreferences(RegisteredCompanyActivity.this,"NA");
                AttendanceAdminSP.saveMobileToPreferences(RegisteredCompanyActivity.this,"NA");
                AttendanceAdminSP.saveSiteLocationToPreferences(RegisteredCompanyActivity.this,"NA");
            }
        });

        layout_bulkUpload_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                Intent in = new Intent(RegisteredCompanyActivity.this, BulkUploadEmployeeActivity.class);
                startActivity(in);
            }
        });
    }
    private void getLocation(){
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        locationTrack = new LocationTrack(RegisteredCompanyActivity.this);
        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            Log.e("LatLong",""+latitude+" : "+longitude);
            //getCompleteAddressString(latitude,longitude);
            //AttendanceAISharedPreference.saveUserLatituteToPreferences(DashboardActivity.this,latitude+"");
            //AttendanceAISharedPreference.saveUserLongitudeToPreferences(DashboardActivity.this,longitude+"");
            Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {
            locationTrack.showSettingsAlert();
        }

    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
            new AlertDialog.Builder(RegisteredCompanyActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       // locationTrack.stopListener();
    }

}
