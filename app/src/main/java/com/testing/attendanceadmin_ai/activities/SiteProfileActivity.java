package com.testing.attendanceadmin_ai.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class SiteProfileActivity extends AppCompatActivity {

    ImageView img_back;
    EditText site_name,site_address,site_Mob,site_alternativeMob,site_Latitude,site_Longitude;
    TextView txt_userName;
    CircleImageView profile_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_profile);
        checkOrientation();
        init();
        listener();
        getProfileData();
    }
    public void checkOrientation(){
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    public void init()
    {
        img_back = findViewById(R.id.img_back);
        site_name = findViewById(R.id.site_name);
        site_address = findViewById(R.id.site_address);
        site_Mob = findViewById(R.id.site_Mob);
        site_alternativeMob = findViewById(R.id.site_alternativeMob);
        site_Latitude = findViewById(R.id.site_Latitude);
        site_Longitude = findViewById(R.id.site_Longitude);
        txt_userName = findViewById(R.id.txt_userName);
        profile_image = findViewById(R.id.profile_image);
        profile_image.setImageResource(R.drawable.profile);
    }

    public void listener(){
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(SiteProfileActivity.this,RegisteredCompanyActivity.class);
                startActivity(in);
            }
        });
    }

    public void getProfileData()
    {
        if (!AppConstant.checkConnection(SiteProfileActivity.this)) {
            Toast.makeText(SiteProfileActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        final CustomProgressDialogue pd = new CustomProgressDialogue(SiteProfileActivity.this);
        pd.setCancelable(false);
        pd.show();

        //String url = AppGlobalConstants.TEST_URL +"getSiteProfileData";
        String url = AppGlobalConstants.WEBSERVICE_BASE_URL_NEW +"getSiteProfileData";
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseResponseProfile(response);
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

                        parseResponseProfile(response.toString());

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
                map.put("Site_ID", AttendanceAdminSP.loadSiteIdFromPreferences(SiteProfileActivity.this));
                return map;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        sr.setShouldCache(false);
        VolleySingleton.getInstance(SiteProfileActivity.this).addToRequestQueue(sr);
    }
    public void parseResponseProfile(String response)
    {
        try{
            JSONObject jsonObject = new JSONObject(response);
            String data = jsonObject.getString("data");
            if(data.equalsIgnoreCase("Site Details Displayed!")){
                //JSONArray jsonArray = jsonObject.getJSONArray("details");
                JSONObject obj = jsonObject.getJSONObject("details");
                String Site_CompanyID="",SiteName="",SiteAddress="",
                        SiteMobileNumber_One="",SiteMobileNumber_Two="",SiteLatitude="",
                        SiteLongitude="",SiteStatus="",Site_ID="";
               // for(int i=0;i<jsonArray.length();i++)
                //{
                    //JSONObject obj = jsonArray.getJSONObject(i);
                    Site_CompanyID = obj.getString("Site_CompanyID");
                    SiteName = obj.getString("SiteName");
                    SiteAddress = obj.getString("SiteAddress");
                    SiteMobileNumber_One = obj.getString("SiteMobileNumber_One");
                    SiteMobileNumber_Two = obj.getString("SiteMobileNumber_Two");
                    SiteLatitude = obj.getString("SiteLatitude");
                    SiteLongitude = obj.getString("SiteLongitude");
                    SiteStatus = obj.getString("SiteStatus");
                    Site_ID = obj.getString("Site_ID");
                //}
                site_name.setText(SiteName);
                site_address.setText(SiteAddress);
                site_Mob.setText(SiteMobileNumber_One);
                site_alternativeMob.setText(SiteMobileNumber_Two);
                site_Latitude.setText(SiteLatitude);
                site_Longitude.setText(SiteLongitude);
                txt_userName.setText(SiteName);
            }
        }catch (Exception e){e.printStackTrace();}
    }
}
