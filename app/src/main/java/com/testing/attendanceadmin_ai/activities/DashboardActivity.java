package com.testing.attendanceadmin_ai.activities;

        import android.annotation.TargetApi;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
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

        import com.android.volley.AuthFailureError;
        import com.android.volley.DefaultRetryPolicy;
        import com.android.volley.NetworkError;
        import com.android.volley.NetworkResponse;
        import com.android.volley.NoConnectionError;
        import com.android.volley.ParseError;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.ServerError;
        import com.android.volley.TimeoutError;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.HttpHeaderParser;
        import com.android.volley.toolbox.StringRequest;
        import com.testing.attendanceadmin_ai.AppPreference.AttendanceAdminSP;
        import com.testing.attendanceadmin_ai.R;
        import com.testing.attendanceadmin_ai.appservice.LocationTrack;
        import com.testing.attendanceadmin_ai.utility.AppConstant;
        import com.testing.attendanceadmin_ai.utility.AppGlobalConstants;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.io.UnsupportedEncodingException;
        import java.text.DecimalFormat;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Locale;
        import java.util.Map;

        import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
        import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DashboardActivity extends AppCompatActivity {

    LinearLayout ln_attendance, ln_logout, layout_home,ln_scan,ln_generate,layout_setting,layout_addEmployee,layout_bulkUpload_drawer;
    DrawerLayout drawer_layout;
    ImageView img_navigation,img_profile;
    TextView username,usernumber,usercompany,userlocation;

    private ArrayList<String> permissionsToRequest = new ArrayList<>();
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    double latitude,longitude;
    String site_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        init();
        listener();
        getLocation();
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
        layout_setting = findViewById(R.id.layout_setting);
        layout_addEmployee = findViewById(R.id.layout_addEmployee_drawer);
        layout_bulkUpload_drawer = findViewById(R.id.layout_bulkUpload_drawer);
        site_id = AttendanceAdminSP.loadSiteIdFromPreferences(DashboardActivity.this);
        //ln_scan = findViewById(R.id.ln_scan);
        //ln_generate = findViewById(R.id.ln_generate);
        username.setText(AttendanceAdminSP.loadCompanyNameFromPreferences(DashboardActivity.this));
        usernumber.setText(AttendanceAdminSP.loadMobileFromPreferences(DashboardActivity.this));
        //usercompany.setText(AttendanceAdminSP.(DashboardActivity.this));
        userlocation.setText(AttendanceAdminSP.loadSiteLocationFromPreferences(DashboardActivity.this));
    }



    private void listener() {

//        ln_scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(DashboardActivity.this,ScanningDetails.class);
//                startActivity(in);
//
//            }
//        });
//
//        ln_generate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(DashboardActivity.this,QRCodeActivity.class);
//                startActivity(in);
//            }
//        });

        layout_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this,FingerScanActivity.class));
            }
        });
        layout_addEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.closeDrawers();
                //Toast.makeText(DashboardActivity.this, "Test", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this,AddEmployeeActivity.class));
            }
        });
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Profile","Test");
                startActivity(new Intent(DashboardActivity.this,SiteProfileActivity.class));
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
            }
        });

        ln_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(DashboardActivity.this, QRCodeActivity.class));
                getCompanyLocation();
            }
        });

        ln_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finishAffinity();
                AttendanceAdminSP.saveCompanyStatusIdToPreferences(DashboardActivity.this, "NA");
                AttendanceAdminSP.saveSiteIdToPreferences(DashboardActivity.this,"NA" );
                AttendanceAdminSP.saveUserLatituteToPreferences(DashboardActivity.this, "NA");
                AttendanceAdminSP.saveUserLongitudeToPreferences(DashboardActivity.this,"NA");
                AttendanceAdminSP.saveCompanyNameToPreferences(DashboardActivity.this,"NA");
                AttendanceAdminSP.saveMobileToPreferences(DashboardActivity.this,"NA");
                AttendanceAdminSP.saveSiteLocationToPreferences(DashboardActivity.this,"NA");
            }
        });

        layout_bulkUpload_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                //Toast.makeText(DashboardActivity.this, "Test", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(DashboardActivity.this, BulkUploadEmployeeActivity.class);
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
        locationTrack = new LocationTrack(DashboardActivity.this);
        if (locationTrack.canGetLocation()) {
            longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();
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
        new AlertDialog.Builder(DashboardActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                //Toast.makeText(DashboardActivity.this, strAdd, Toast.LENGTH_SHORT).show();
                //userlocation.setText(AttendanceAISharedPreference.loadUserLocationFromPreferences(DashboardActivity.this));

                Log.e("My Current loction address", strReturnedAddress.toString());

            } else {
                Log.e("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Cannot get Address!");
        }
        return strAdd;
    }
    public String formatFigureToTwoPlaces(double value) {
        DecimalFormat myFormatter = new DecimalFormat("00.00000");
        return myFormatter.format(value);
    }

    public void getCompanyLocation(){
        if (!AppConstant.checkConnection(DashboardActivity.this)) {
            Toast.makeText(DashboardActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        final CustomProgressDialogue pd= new CustomProgressDialogue(DashboardActivity.this);
        pd.setCancelable(false);
        pd.show();

        //String url = AppGlobalConstants.WEBSERVICE_BASE_URL + "updateLonglat?SiteId="+site_id+"&longitude="+longitude+"&latitude="+latitude;
        //String url = AppGlobalConstants.TEST_URL + "updateSite";
        String url = AppGlobalConstants.WEBSERVICE_BASE_URL_NEW + "updateSite";
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseResponseSite(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                NetworkResponse response = error.networkResponse;

                Log.e("Attendance Admin", "error response " + response);

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.e("mls", "VolleyError TimeoutError error or NoConnectionError");
                } else if (error instanceof AuthFailureError) {                    //TODO
                    Log.e("mls", "VolleyError AuthFailureError");
                } else if (error instanceof ServerError) {
                    Log.e("mls", "VolleyError ServerError");
                } else if (error instanceof NetworkError) {
                    Log.e("mls", "VolleyError NetworkError");
                } else if (error instanceof ParseError) {
                    Log.e("mls", "VolleyError TParseError");
                }
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        Log.e("Attendance Admin", "error response " + res);

                        parseResponseSite(response.toString());

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("SITE_ID",AttendanceAdminSP.loadSiteIdFromPreferences(DashboardActivity.this));
                params.put("SiteLatitude",String.valueOf(latitude));
                params.put("SiteLongitude",String.valueOf(longitude));
                params.put("SiteStatus","2");
                Log.e("SiteID",AttendanceAdminSP.loadSiteIdFromPreferences(DashboardActivity.this));
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        sr.setShouldCache(false);
        VolleySingleton.getInstance(DashboardActivity.this).addToRequestQueue(sr);
    }

    public void parseResponseSite(String response){
        Log.e("Site Response", "response " + response);
        try{
            JSONObject job = new JSONObject(response);
            String message = job.getString("data");
            if(message.equalsIgnoreCase("Site Updated Successfully!")){
                Toast.makeText(DashboardActivity.this, "Site Updated Successfully", Toast.LENGTH_LONG).show();
                String SiteLatitude = job.getString("SiteLatitude");
                String SiteLongitude = job.getString("SiteLongitude");
                String SiteStatus = job.getString("SiteStatus");
                AttendanceAdminSP.saveUserLatituteToPreferences(DashboardActivity.this,SiteLatitude);
                AttendanceAdminSP.saveUserLongitudeToPreferences(DashboardActivity.this,SiteLongitude);
                AttendanceAdminSP.saveCompanyStatusIdToPreferences(DashboardActivity.this, SiteStatus);
                if(SiteStatus.equalsIgnoreCase("2")){
                    startActivity(new Intent(DashboardActivity.this,RegisteredCompanyActivity.class));
                    finishAffinity();
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
