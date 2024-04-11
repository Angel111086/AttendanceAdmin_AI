package com.testing.attendanceadmin_ai.activities;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.testing.attendanceadmin_ai.R;
import com.testing.attendanceadmin_ai.appservice.LocationTrack;
import com.testing.attendanceadmin_ai.model.CompanyDataModel;
import com.testing.attendanceadmin_ai.utility.AppConstant;
import com.testing.attendanceadmin_ai.utility.AppGlobalConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RegisterSiteActivity extends AppCompatActivity {

    EditText ed_siteName,ed_siteAddress,ed_siteMobileNumberOne,ed_alternativeNumber,ed_siteLatitude,ed_siteLongitude;
    ImageView img_back;
    Spinner spin_selectCompany;
    Button btn_registerSite;

    ArrayList<CompanyDataModel> companyList;
    ArrayAdapter<CompanyDataModel> companyadapter;

    String company_id="";
    CompanyDataModel companyDataModel;

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
        setContentView(R.layout.activity_register_site);
        checkOrientation();
        init();
        getData();
        listener();
        getCompanyData();
        getLocation();
    }
    public void checkOrientation(){
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    private void init() {
        img_back = (ImageView) findViewById(R.id.img_back);
        ed_siteName = (EditText) findViewById(R.id.ed_siteName);
        ed_siteAddress = (EditText) findViewById(R.id.ed_siteAddress);
        ed_siteMobileNumberOne = (EditText) findViewById(R.id.ed_siteMobileNumberOne);
        ed_alternativeNumber = (EditText) findViewById(R.id.ed_alternativeNumber);
        ed_siteLatitude = (EditText) findViewById(R.id.ed_siteLatitude);
        ed_siteLongitude = (EditText) findViewById(R.id.ed_siteLongitude);
        spin_selectCompany = (Spinner) findViewById(R.id.spin_selectCompany);
        btn_registerSite = (Button) findViewById(R.id.btn_registerSite);
        companyList = new ArrayList<>();
    }
    public void getData(){
        companyDataModel = (CompanyDataModel) getIntent().getSerializableExtra("Address");
        String com_address = companyDataModel.getCompanyAddress();
        if(com_address.isEmpty()){

        }else{
            ed_siteAddress.setText(com_address);
        }
    }
    public void listener(){
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        spin_selectCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CompanyDataModel cm = companyList.get(position);
                company_id = cm.get_id();
                Log.e("Company_Id",company_id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_registerSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String siteName = ed_siteName.getText().toString();
                String siteAddress = ed_siteAddress.getText().toString();
                String siteMobile = ed_siteMobileNumberOne.getText().toString();
                String siteAlternative = ed_alternativeNumber.getText().toString();
                String siteLatitude = ed_siteLatitude.getText().toString();
                String siteLongitude = ed_siteLongitude.getText().toString();
                if(siteName.isEmpty()){
                    ed_siteName.setError("Please Enter Site Name");
                    ed_siteName.requestFocus();
                }
                else if(siteAddress.isEmpty()){
                    ed_siteAddress.setError("Please Enter Site Address");
                    ed_siteAddress.requestFocus();
                }
                else if(siteMobile.isEmpty()){
                    ed_siteMobileNumberOne.setError("Please Enter Site Mobile Number");
                    ed_siteMobileNumberOne.requestFocus();
                }
                else if(siteAlternative.isEmpty()){
                    ed_alternativeNumber.setError("Please Enter Company's Email");
                    ed_alternativeNumber.requestFocus();
                }
                else{
                    registerSite(company_id,siteName,siteAddress,siteMobile,siteAlternative,siteLatitude,siteLongitude);
                }
            }

        });
    }

    public void getCompanyData(){
        if (!AppConstant.checkConnection(RegisterSiteActivity.this)) {
            Toast.makeText(RegisterSiteActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
        final CustomProgressDialogue pd= new CustomProgressDialogue(RegisterSiteActivity.this);
        pd.setCancelable(false);
        pd.show();

        //String url = "http://192.168.1.14:1234/userapi/getCompanyData";
        String url = AppGlobalConstants.WEBSERVICE_BASE_URL_NEW + "getCompanyData";
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseResponseCompany(response);
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

                        parseResponseCompany(response.toString());

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/x-www-form-urlencoded");
                return map;
            }


        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        sr.setShouldCache(false);
        VolleySingleton.getInstance(RegisterSiteActivity.this).addToRequestQueue(sr);
    }

    public void parseResponseCompany(String response) {
        Log.e("Company Data Response",response);
        try {
            JSONObject object = new JSONObject(response);
            JSONArray jsonArray = object.getJSONArray("Data");
            CompanyDataModel cm;
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String _id= jsonObject.getString("_id");
                String CompanyName= jsonObject.getString("CompanyName");
                String CompanyAddress= jsonObject.getString("CompanyAddress");
                String CompanyWebsite= jsonObject.getString("CompanyWebsite");
                String CompanyEmail= jsonObject.getString("CompanyEmail");
                String CompanyContactNumber= jsonObject.getString("CompanyContactNumber");
                cm = new CompanyDataModel(_id,CompanyName);
                companyList.add(cm);
            }
            companyadapter = new ArrayAdapter<>(RegisterSiteActivity.this,android.R.layout.simple_list_item_1,companyList);
            spin_selectCompany.setAdapter(companyadapter);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void registerSite(final String Id,final String sn,final String sa,final String sm,final String am,final String slatitude,final String slongitude){
        if (!AppConstant.checkConnection(RegisterSiteActivity.this)) {
            Toast.makeText(RegisterSiteActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
        final CustomProgressDialogue pd= new CustomProgressDialogue(RegisterSiteActivity.this);
        pd.setCancelable(false);
        pd.show();

        //String url = "http://192.168.1.14:1234/userapi/registerSite";
        String url = AppGlobalConstants.WEBSERVICE_BASE_URL_NEW +"registerSite";
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseResponse(response);
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

                        parseResponse(response.toString());

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/x-www-form-urlencoded");
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Site_CompanyID", Id);
                map.put("SiteName", sn);
                map.put("SiteAddress", sa);
                map.put("SiteMobileNumber_One", sm);
                map.put("SiteMobileNumber_Two", am);
                map.put("SiteLatitude", slatitude);
                map.put("SiteLongitude", slongitude);
                map.put("SiteStatus", "1");
                return map;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        sr.setShouldCache(false);
        VolleySingleton.getInstance(RegisterSiteActivity.this).addToRequestQueue(sr);
    }

    public void parseResponse(String response) {
        Log.e("Register Site Response",response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            String data = jsonObject.getString("data");
            if (data.equalsIgnoreCase("Site Saved Successfully!")) {
                //String site_Id = jsonObject.getString("Site_ID");
                //String siteStatus = jsonObject.getString("SiteStatus");
                AlertDialog.Builder abuild = new AlertDialog.Builder(RegisterSiteActivity.this);
                abuild.setTitle("Site Saved");
                abuild.setIcon(R.drawable.launcher_icon);
                abuild.setMessage("Please Login With Site's Number");
                abuild.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                abuild.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog ad = abuild.create();
                ad.show();
            }
            else{
                Toast.makeText(this, "Site Details cannot be Saved!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getLocation(){
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        locationTrack = new LocationTrack(RegisterSiteActivity.this);
        if (locationTrack.canGetLocation()) {
            longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();
            Log.e("LatLong",""+latitude+" : "+longitude);
            //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
            ed_siteLatitude.setText(latitude+"");
            ed_siteLongitude.setText(longitude+"");
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
        new AlertDialog.Builder(RegisterSiteActivity.this)
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
}
