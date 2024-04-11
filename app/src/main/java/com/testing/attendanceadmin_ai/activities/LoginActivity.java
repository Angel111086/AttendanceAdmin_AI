package com.testing.attendanceadmin_ai.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.testing.attendanceadmin_ai.utility.AppConstant;
import com.testing.attendanceadmin_ai.utility.AppGlobalConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    Button btn_next;
    EditText et_mobile;
    String empMobile;
    public static int OTP;
    TextView txt_registerCompany;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkOrientation();
        init();
        listener();
    }
    public void checkOrientation(){
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    private void init() {

        btn_next=(Button)findViewById(R.id.btn_next);
        et_mobile=(EditText) findViewById(R.id.et_mobile);
        txt_registerCompany = findViewById(R.id.txt_registerCompany);
        //pd = new ProgressDialog(LoginActivity.this);
    }
    private void listener() {

        txt_registerCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterCompanyActivity.class));
                //startActivity(new Intent(LoginActivity.this,RegisterSiteActivity.class));
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                empMobile = et_mobile.getText().toString();

                if(empMobile.isEmpty()){
                    et_mobile.setError("Please enter mobile number");
                    et_mobile.requestFocus();
                }else if(empMobile.length()!=10){
                    et_mobile.setError("Please enter valid mobile number");
                    et_mobile.requestFocus();
                }else {
                    OTP = generateRandomNumber();
                    getSignUpData(empMobile);
                    //in.putExtra("GETOTP",OTP);
                    //startActivity(in);

                }
            }
        });
    }

    public int generateRandomNumber()
    {
        Random rnd = new Random();
        //int otp = rnd.nextInt(10000);
        int randomPIN = (int)(Math.random()*9000)+1000;
        return randomPIN;
    }

    public void getSignUpData(final String mob) {
        if (!AppConstant.checkConnection(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        final CustomProgressDialogue pd = new CustomProgressDialogue(LoginActivity.this);
        pd.setCancelable(false);
        pd.show();

        //String url = "http://192.168.1.14:1234/userapi/adminLogin";
        String url = AppGlobalConstants.WEBSERVICE_BASE_URL_NEW +"adminLogin";
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
                        Log.e("p.parking", "error response " + res);

                        parseResponse(response.toString());

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
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/x-www-form-urlencoded");
                return map;
            }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Mobile",mob);
                return map;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        sr.setShouldCache(false);
        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(sr);
    }

    public void parseResponse(String response) {
        Log.e("Login Response", "response " + response);
        try{
            JSONObject job = new JSONObject(response);
            JSONArray datares = job.getJSONArray("Data");
            String SiteName="",SiteLatitude="",SiteLongitude="",SiteMobileNumber_One="",SiteMobileNumber_Two="",Site_ID="",SiteStatus="",SiteAddress="";
            for(int i = 0;i<datares.length();i++){
                JSONObject json = datares.getJSONObject(i);
                String _id = json.getString("_id");
                String Site_CompanyID = json.getString("Site_CompanyID");
                SiteName = json.getString("SiteName");
                SiteAddress = json.getString("SiteAddress");
                SiteMobileNumber_One = json.getString("SiteMobileNumber_One");
                SiteMobileNumber_Two = json.getString("SiteMobileNumber_Two");
                SiteLatitude = json.getString("SiteLatitude");
                SiteLongitude = json.getString("SiteLongitude");
                Site_ID = json.getString("Site_ID");
                SiteStatus = json.getString("SiteStatus");

            }
            String message = job.getString("Msg");
            if(message.equalsIgnoreCase("Login Successfull!"))
            {
                if (SiteMobileNumber_One.equalsIgnoreCase(empMobile) || SiteMobileNumber_Two.equalsIgnoreCase(empMobile)) {
                    callSendOTP(empMobile,OTP+"");
                    Intent in = new Intent(LoginActivity.this, VerifyOTPActivity.class);
                    //in.putExtra("");
                    startActivity(in);
                    AttendanceAdminSP.saveCompanyStatusIdToPreferences(LoginActivity.this, SiteStatus);
                    AttendanceAdminSP.saveSiteIdToPreferences(LoginActivity.this, Site_ID);
                    AttendanceAdminSP.saveUserLatituteToPreferences(LoginActivity.this,SiteLatitude);
                    AttendanceAdminSP.saveUserLongitudeToPreferences(LoginActivity.this,SiteLongitude);
                    AttendanceAdminSP.saveCompanyNameToPreferences(LoginActivity.this,SiteName);
                    AttendanceAdminSP.saveMobileToPreferences(LoginActivity.this,empMobile);
                    AttendanceAdminSP.saveSiteLocationToPreferences(LoginActivity.this,SiteAddress);
                    Toast.makeText(LoginActivity.this, "Login Successfull.", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(LoginActivity.this, "Invalid Credentials.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public void callSendOTP(final String mobileOTP,final String pin){
//        if (!AppConstant.checkConnection(LoginActivity.this)) {
//            Toast.makeText(LoginActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        String url =  AppGlobalConstants.WEBSERVICE_BASE_URL + "Sendotp?mobileno="+mobileOTP+"&OTP="+pin;
//
//        Log.e("URLTest","Test:--"+url);
//
//        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                //pd.dismiss();
//                parseResponseOTP(response.toString());
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //pd.dismiss();
//                NetworkResponse response = error.networkResponse;
//
//                Log.e("com.toppers", "error response " + response);
//
//                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Log.e("mls", "VolleyError TimeoutError error or NoConnectionError");
//                } else if (error instanceof AuthFailureError) {
//                    //TODO
//                    Log.e("mls", "VolleyError AuthFailureError");
//                } else if (error instanceof ServerError) {
//                    Log.e("mls", "VolleyError ServerError");
//                } else if (error instanceof NetworkError) {
//                    Log.e("mls", "VolleyError NetworkError");
//                } else if (error instanceof ParseError) {
//                    Log.e("mls", "VolleyError TParseError");
//                }
//                if (error instanceof ServerError && response != null) {
//                    try {
//                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
//                        // Now you can use any deserializer to make sense of data
//                        Log.e("com.toppers", "error response " + res);
//
//                        parseResponseOTP(response.toString());
//
//                    } catch (UnsupportedEncodingException e1) {
//                        // Couldn't properly decode data to string
//                        e1.printStackTrace();
//                    }
//                }
//
//            }
//        });
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
//                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS * 2,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        ));
//        jsonObjectRequest.setShouldCache(false);
//        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);
//    }
//
//    public void parseResponseOTP(String response){
//        Log.e("AA OTP Response", "response " + response);
//        if(response.equalsIgnoreCase("true")){
//            Intent in = new Intent(LoginActivity.this,VerifyOTPActivity.class);
//            in.putExtra("OTP",OTP);
//            startActivity(in);
//            finish();
//        }
//        else{
//
//        }
//
//    }


    public void callSendOTP(final String mob,final String otp){

        if (!AppConstant.checkConnection(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setTitle("Please Wait");
        pd.setCancelable(false);
        pd.show();

        String url = "https://control.msg91.com/api/sendhttp.php?authkey=6110Aw4HLHYba593675f9&mobiles="+mob+"&message="+OTP+"&sender=CKTPOS&route=4&country=91&response=json";
        //String url="";
        StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseResponse(response,otp);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                NetworkResponse response = error.networkResponse;

                Log.e("com.curryout", "error response " + response);

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
                        Log.e("com.curryout", "error response " + res);

                        parseResponse(response.toString(),otp);

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }

            }

        });
        sr.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        sr.setShouldCache(false);
        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(sr);
    }
    public void parseResponse(String response, String otp){
        Log.e("Verify OTP Response","response "+response);
        try {
            JSONObject object = new JSONObject(response);
            String type = object.getString("type");
            if(type.equalsIgnoreCase("success")) {
                Intent in = new Intent(LoginActivity.this, VerifyOTPActivity.class);
                in.putExtra("GETOTP",OTP);
                startActivity(in);
            }
            else {
                Toast.makeText(this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

