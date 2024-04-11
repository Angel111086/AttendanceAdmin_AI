package com.testing.attendanceadmin_ai.activities;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class VerifyOTPActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {

    ImageView img_back;
    Button btn_submit;
    EditText pin_hidden_edittext,pin_first_edittext,pin_second_edittext,pin_third_edittext,pin_forth_edittext;
    int generateOtp;
    String mobile="";
    String getHiddenData="",strOtp="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        checkOrientation();
        init();
        listener();
        setPINListeners();
        try {
            generateOtp = getIntent().getIntExtra("GETOTP",0);
            strOtp = String.valueOf(generateOtp);
            //strOtp = "1234";
        }catch (Exception e){e.printStackTrace();}
        //mobile = AttendanceAISharedPreference.loadMobileFromPreferences(VerifyOTPActivity.this);
    }
    public void checkOrientation(){
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
    private void init() {
        img_back=(ImageView)findViewById(R.id.img_back);
        btn_submit=(Button)findViewById(R.id.btn_submit);
        pin_first_edittext = (EditText) findViewById(R.id.pin_first_edittext);
        pin_second_edittext = (EditText) findViewById(R.id.pin_second_edittext);
        pin_third_edittext = (EditText) findViewById(R.id.pin_third_edittext);
        pin_forth_edittext = (EditText) findViewById(R.id.pin_forth_edittext);
        pin_hidden_edittext = (EditText) findViewById(R.id.pin_hidden_edittext);
    }
    private void setPINListeners() {
        pin_hidden_edittext.addTextChangedListener(this);
        pin_first_edittext.setOnFocusChangeListener(this);
        pin_second_edittext.setOnFocusChangeListener(this);
        pin_third_edittext.setOnFocusChangeListener(this);
        pin_forth_edittext.setOnFocusChangeListener(this);

        pin_first_edittext.setOnKeyListener(this);
        pin_second_edittext.setOnKeyListener(this);
        pin_third_edittext.setOnKeyListener(this);
        pin_forth_edittext.setOnKeyListener(this);
        pin_hidden_edittext.setOnKeyListener(this);
    }

    private void listener() {

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TestOTPinVerify",generateOtp+"");

                getHiddenData = pin_hidden_edittext.getText().toString();
                Log.e("Hidden::",getHiddenData);
                //To Check
                String fp = pin_first_edittext.getText().toString().trim();
                String sp = pin_second_edittext.getText().toString().trim();
                String tp = pin_third_edittext.getText().toString().trim();
                String fop = pin_forth_edittext.getText().toString().trim();
                if (fp.equalsIgnoreCase("") || sp.equalsIgnoreCase("") || tp.equalsIgnoreCase("") || fop.equalsIgnoreCase("")) {
                    //mPinFirstDigitEditText.setError("");
                    //mPinSecondDigitEditText.setError("");
                    //mPinThirdDigitEditText.setError("");
                    //mPinForthDigitEditText.setError("");
                    //return;
                }
                else{
                    Log.e("Condition",getHiddenData.equals(strOtp)+"");
                    if(getHiddenData.equals(strOtp)){
                        Log.e("OTP","Checking");
                        Toast.makeText(VerifyOTPActivity.this, "OTP Verified", Toast.LENGTH_SHORT).show();
                        String status = AttendanceAdminSP.loadCompanyStatusIdFromPreferences(VerifyOTPActivity.this);
                        if (status.equals("1")) {
                            startActivity(new Intent(VerifyOTPActivity.this, DashboardActivity.class));
                        }else{
                            startActivity(new Intent(VerifyOTPActivity.this, RegisteredCompanyActivity.class));
                        }

                        finishAffinity();
                    }
                    else{
                        Toast.makeText(VerifyOTPActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setDefaultPinBackground(pin_first_edittext);
        setDefaultPinBackground(pin_second_edittext);
        setDefaultPinBackground(pin_third_edittext);
        setDefaultPinBackground(pin_forth_edittext);

        if (s.length() == 0) {
            setFocusedPinBackground(pin_first_edittext);
            pin_first_edittext.setText("");
        } else if (s.length() == 1) {
            setFocusedPinBackground(pin_second_edittext);
            pin_first_edittext.setText(s.charAt(0) + "");
            pin_second_edittext.setText("");
            pin_third_edittext.setText("");
            pin_forth_edittext.setText("");
        } else if (s.length() == 2) {
            setFocusedPinBackground(pin_third_edittext);
            pin_second_edittext.setText(s.charAt(1) + "");
            pin_third_edittext.setText("");
            pin_forth_edittext.setText("");
        } else if (s.length() == 3) {
            setFocusedPinBackground(pin_forth_edittext);
            pin_third_edittext.setText(s.charAt(2) + "");
            pin_forth_edittext.setText("");
        }  else if (s.length() == 4) {
            setDefaultPinBackground(pin_forth_edittext);
            pin_forth_edittext.setText(s.charAt(3) + "");
            hideSoftKeyboard(pin_forth_edittext);
        }

    }

    private void setDefaultPinBackground(EditText editText) {
//        setViewBackground(editText, getResources().getDrawable(R.drawable.textfield_default_holo_light));
    }

    private void setFocusedPinBackground(EditText editText) {
//        setViewBackground(editText, getResources().getDrawable(R.drawable.textfield_focused_holo_light));
    }

    public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.pin_first_edittext:
                if (hasFocus) {
                    setFocus(pin_hidden_edittext);
                    showSoftKeyboard(pin_hidden_edittext);
                }
                break;

            case R.id.pin_second_edittext:
                if (hasFocus) {
                    setFocus(pin_hidden_edittext);
                    showSoftKeyboard(pin_hidden_edittext);
                }
                break;

            case R.id.pin_third_edittext:
                if (hasFocus) {
                    setFocus(pin_hidden_edittext);
                    showSoftKeyboard(pin_hidden_edittext);
                }
                break;

            case R.id.pin_forth_edittext:
                if (hasFocus) {
                    setFocus(pin_hidden_edittext);
                    showSoftKeyboard(pin_hidden_edittext);
                }
                break;
            default:
                break;
        }

    }

    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    private void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (pin_hidden_edittext.getText().length() == 4)
                            pin_forth_edittext.setText("");
                        else if (pin_hidden_edittext.getText().length() == 3)
                            pin_third_edittext.setText("");
                        else if (pin_hidden_edittext.getText().length() == 2)
                            pin_second_edittext.setText("");
                        else if (pin_hidden_edittext.getText().length() == 1)
                            pin_second_edittext.setText("");

                        if (pin_hidden_edittext.length() > 0)
                            pin_hidden_edittext.setText(pin_hidden_edittext.getText().subSequence(0, pin_hidden_edittext.length() - 1));

                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }
        return false;
    }

//    public void verifyAPI(){
//
//        if (!AppConstant.checkConnection(VerifyOTPActivity.this)) {
//            Toast.makeText(VerifyOTPActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        final ProgressDialog pd = new ProgressDialog(VerifyOTPActivity.this);
//        pd.setTitle("Please Wait");
//        pd.setCancelable(false);
//        pd.show();
//
//        String url = "https://control.msg91.com/api/sendhttp.php?authkey=6110Aw4HLHYba593675f9&mobiles="+mobile+"&message="+generateOtp+"&sender=CKTPOS&route=4&country=91&response=json";
//
//        StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                pd.dismiss();
//                parseResponse(response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                pd.dismiss();
//                NetworkResponse response = error.networkResponse;
//
//                Log.e("com.curryout", "error response " + response);
//
//                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Log.e("mls", "VolleyError TimeoutError error or NoConnectionError");
//                } else if (error instanceof AuthFailureError) {                    //TODO
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
//                        Log.e("com.curryout", "error response " + res);
//
//                        parseResponse(response.toString());
//
//                    } catch (UnsupportedEncodingException e1) {
//                        // Couldn't properly decode data to string
//                        e1.printStackTrace();
//                    }
//                }
//
//            }
//
//        });
//        sr.setRetryPolicy(new DefaultRetryPolicy(
//                60000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        ));
//        sr.setShouldCache(false);
//        VolleySingleton.getInstance(VerifyOTPActivity.this).addToRequestQueue(sr);
//    }
//    public void parseResponse(String response){
//        Log.e("Verify OTP Response","response "+response);
//        try {
//            JSONObject object = new JSONObject(response);
//            String type = object.getString("type");
//            if(type.equalsIgnoreCase("success")){
//                //Intent in = new Intent(VerifyOTPActivity.this,DashboardActivity.class);
//                //startActivity(in);
//                //finishAffinity();
//            }
//            else {
//
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
}
