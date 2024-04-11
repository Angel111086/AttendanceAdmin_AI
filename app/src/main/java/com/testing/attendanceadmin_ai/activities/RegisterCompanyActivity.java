package com.testing.attendanceadmin_ai.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.testing.attendanceadmin_ai.R;
import com.testing.attendanceadmin_ai.model.CompanyDataModel;
import com.testing.attendanceadmin_ai.utility.AppConstant;
import com.testing.attendanceadmin_ai.utility.AppGlobalConstants;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegisterCompanyActivity extends AppCompatActivity {

    EditText ed_companyName,ed_companyAddress,ed_companyWebsite,ed_companyEmail,ed_companyContactNumber;
    ImageView img_back;
    Button btn_registerCompany;
    CompanyDataModel companyDataModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_company);
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
        ed_companyName = (EditText) findViewById(R.id.ed_companyName);
        ed_companyAddress = (EditText) findViewById(R.id.ed_companyAddress);
        ed_companyWebsite = (EditText) findViewById(R.id.ed_companyWebsite);
        ed_companyEmail = (EditText) findViewById(R.id.ed_companyEmail);
        ed_companyContactNumber = (EditText) findViewById(R.id.ed_companyContactNumber);
        img_back = (ImageView) findViewById(R.id.img_back);
        btn_registerCompany = (Button) findViewById(R.id.btn_registerCompany);
    }

    private void listener() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_registerCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String companyName = ed_companyName.getText().toString();
                String companyAddress = ed_companyAddress.getText().toString();
                String companyWebsite = ed_companyWebsite.getText().toString();
                String companyEmail = ed_companyEmail.getText().toString();
                String companyContactNumber = ed_companyContactNumber.getText().toString();

                if(companyName.isEmpty()){
                    ed_companyName.setError("Please Enter Company's Name");
                    ed_companyName.requestFocus();
                }
                else if(companyAddress.isEmpty()){
                    ed_companyAddress.setError("Please Enter Company's Address");
                    ed_companyAddress.requestFocus();
                }
                else if(companyWebsite.isEmpty()){
                    ed_companyWebsite.setError("Please Enter Company's Website");
                    ed_companyWebsite.requestFocus();
                }
                else if(companyEmail.isEmpty()){
                    ed_companyEmail.setError("Please Enter Company's Email");
                    ed_companyEmail.requestFocus();
                }
                else if(companyContactNumber.isEmpty()){
                    ed_companyContactNumber.setError("Please Enter Company's Contact Number");
                    ed_companyContactNumber.requestFocus();
                }
                else{
                    registerCompany(companyName,companyAddress,companyWebsite,companyEmail,companyContactNumber);
                }
                }

        });
    }

    public void registerCompany(final String cn,final String ca,final String cw,final String ce,final String cc){
        if (!AppConstant.checkConnection(RegisterCompanyActivity.this)) {
            Toast.makeText(RegisterCompanyActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
        final CustomProgressDialogue pd= new CustomProgressDialogue(RegisterCompanyActivity.this);
        pd.setCancelable(false);
        pd.show();

        //String url = "http://192.168.1.14:1234/userapi/registerCompany";
        String url = AppGlobalConstants.WEBSERVICE_BASE_URL_NEW+"registerCompany";
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
                map.put("CompanyName", cn);
                map.put("CompanyAddress", ca);
                map.put("CompanyWebsite", cw);
                map.put("CompanyEmail", ce);
                map.put("CompanyContactNumber", cc);
                companyDataModel = new CompanyDataModel(cn,ca,cw,ce,cc);
                return map;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        sr.setShouldCache(false);
        VolleySingleton.getInstance(RegisterCompanyActivity.this).addToRequestQueue(sr);
    }

    public void parseResponse(String response) {
        Log.e("Reg CompanyResponse",response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            String data = jsonObject.getString("data");
            if(data.equalsIgnoreCase("Company Saved Successfully!")){
                AlertDialog.Builder abuild = new AlertDialog.Builder(RegisterCompanyActivity.this);
                abuild.setTitle("Alert!!!");
                abuild.setIcon(R.drawable.launcher_icon);
                abuild.setMessage("Company Saved Successfully. Please Add Site.");
                abuild.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder abuild = new AlertDialog.Builder(RegisterCompanyActivity.this);
                        abuild.setTitle("Is Your Site Address Same as Company?");
                        abuild.setIcon(R.drawable.launcher_icon);
                        abuild.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent in = new Intent(RegisterCompanyActivity.this,RegisterSiteActivity.class);
                                in.putExtra("Address",companyDataModel);
                                startActivity(in);
                                finish();
                            }
                        });

                        abuild.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(RegisterCompanyActivity.this,RegisterSiteActivity.class));
                                finish();
                            }
                        });

                        AlertDialog ad = abuild.create();
                        ad.show();
                    }
                });
                abuild.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog ad = abuild.create();
                ad.show();
            }else{
                Toast.makeText(this, "Company Details Cannot Be Saved!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
