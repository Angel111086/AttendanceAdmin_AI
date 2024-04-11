package com.testing.attendanceadmin_ai.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.testing.attendanceadmin_ai.R;
import com.testing.attendanceadmin_ai.multipart.AppHelper;
import com.testing.attendanceadmin_ai.multipart.VolleyMultipartRequest;
import com.testing.attendanceadmin_ai.utility.AppConstant;
import com.testing.attendanceadmin_ai.utility.AppGlobalConstants;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AddEmployeeActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    ImageView profile_image,img_back;
    TextView txt_uploadImage;
    EditText ed_user_name,ed_user_mobile,ed_user_bloodGroup,ed_user_bankDetails,ed_user_companyName,
            ed_user_companyAddress,ed_user_designation,ed_user_DOB,ed_user_email,ed_user_experience,
            ed_user_fatherName,ed_user_motherName,ed_user_joiningDate,ed_user_localAddress,
            ed_user_notice,ed_user_password,ed_user_permanentAddress,
            ed_user_salary,ed_user_status,ed_user_guardianNumber,ed_user_bankName,ed_user_bankAccNum,
            ed_user_branchName,ed_user_ifscCode;
    RadioButton rbMale,rbFemale,rbPassport_Yes,rbPassport_No;
    Spinner sp_user_status;
    Button btn_add_employee;
    String img_name="";
    Bitmap bitmap;
    ArrayList<String> spinList;
    ArrayAdapter<String> spinAdap;
    String userStatus="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
        init();
        listener();
        if (checkPermission()) {
        } else {
            requestPermission();
        }
        isStoragePermissionGranted();
    }

    public void init(){
        img_back = findViewById(R.id.img_back);
        profile_image = findViewById(R.id.profile_image);
        txt_uploadImage = findViewById(R.id.txt_uploadImage);
        ed_user_name = findViewById(R.id.ed_user_name);
        ed_user_mobile = findViewById(R.id.ed_user_mobile);
        ed_user_bloodGroup = findViewById(R.id.ed_user_bloodGroup);
        //ed_user_bankDetails = findViewById(R.id.ed_user_bankDetails);
        ed_user_bankName = findViewById(R.id.ed_user_bankName);
        ed_user_bankAccNum = findViewById(R.id.ed_user_bankAccNum);
        ed_user_branchName = findViewById(R.id.ed_user_branchName);
        ed_user_ifscCode = findViewById(R.id.ed_user_ifscCode);

        ed_user_companyName = findViewById(R.id.ed_user_companyName);
        ed_user_companyAddress = findViewById(R.id.ed_user_companyAddress);
        ed_user_designation = findViewById(R.id.ed_user_designation);
        ed_user_DOB = findViewById(R.id.ed_user_DOB);
        ed_user_email = findViewById(R.id.ed_user_email);
        ed_user_experience = findViewById(R.id.ed_user_experience);
        ed_user_fatherName = findViewById(R.id.ed_user_fatherName);
        ed_user_motherName = findViewById(R.id.ed_user_motherName);
        ed_user_joiningDate = findViewById(R.id.ed_user_joiningDate);
        ed_user_localAddress = findViewById(R.id.ed_user_localAddress);
        ed_user_notice = findViewById(R.id.ed_user_notice);
        ed_user_password = findViewById(R.id.ed_user_password);
        ed_user_permanentAddress = findViewById(R.id.ed_user_permanentAddress);
        ed_user_salary = findViewById(R.id.ed_user_salary);
        //ed_user_status = findViewById(R.id.ed_user_status);
        ed_user_guardianNumber = findViewById(R.id.ed_user_guardianNumber);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbPassport_Yes = findViewById(R.id.rbPassport_Yes);
        rbPassport_No = findViewById(R.id.rbPassport_No);
        btn_add_employee = findViewById(R.id.btn_add_employee);
        sp_user_status = findViewById(R.id.sp_user_status);
        spinList = new ArrayList<>();
        spinList.add("Select");
        spinList.add("New");
        spinList.add("Active");
        spinList.add("Suspended");
        spinAdap = new ArrayAdapter<>(AddEmployeeActivity.this,android.R.layout.simple_list_item_1,spinList);
        sp_user_status.setAdapter(spinAdap);
    }

    public void listener(){
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(AddEmployeeActivity.this,RegisteredCompanyActivity.class);
                startActivity(in);
            }
        });
        sp_user_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = spinList.get(i);
                Log.e("Spin",s);
                if(s.equalsIgnoreCase("Active")){
                    userStatus = "1";
                }
                if(s.equalsIgnoreCase("New")){
                    userStatus = "0";
                }
                if(s.equalsIgnoreCase("Suspended")){
                    userStatus = "-1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        txt_uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPicType();
            }
        });
        ed_user_DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEmployeeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        Log.e("Month", month + "");
                        Log.e("Year", year + "");
                        ed_user_DOB.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                        ed_user_DOB.setError(null);
                        ed_user_DOB.clearFocus();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.setCancelable(false);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            }

        });

        ed_user_joiningDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEmployeeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        Log.e("Month", month + "");
                        Log.e("Year", year + "");
                        ed_user_joiningDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                        ed_user_joiningDate.setError(null);
                        ed_user_joiningDate.clearFocus();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.setCancelable(false);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            }
        });
        btn_add_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = ed_user_name.getText().toString();
                String userMobile = ed_user_mobile.getText().toString();
                String userBloodGroup = ed_user_bloodGroup.getText().toString();
                String userBankName = ed_user_bankName.getText().toString();
                String userBankAccNum = ed_user_bankAccNum.getText().toString();
                String userBranchName = ed_user_branchName.getText().toString();
                String userifscCode = ed_user_ifscCode.getText().toString();
                String userCompanyName = ed_user_companyName.getText().toString();
                String userCompanyAddress = ed_user_companyAddress.getText().toString();
                String userDesignation = ed_user_designation.getText().toString();
                String userDOB = ed_user_DOB.getText().toString();
                String userEmail = ed_user_email.getText().toString();
                String userExperience = ed_user_experience.getText().toString();
                String userFatherName = ed_user_fatherName.getText().toString();
                String userMotherName = ed_user_motherName.getText().toString();
                String userJoiningDate = ed_user_joiningDate.getText().toString();
                String userLocalAddress = ed_user_localAddress.getText().toString();
                String userNotice = ed_user_notice.getText().toString();
                String userPassword = ed_user_password.getText().toString();
                String userPermanentAddress = ed_user_permanentAddress.getText().toString();
                String userSalary = ed_user_salary.getText().toString();
                //String userStatus = ed_user_status.getText().toString();
                String userGuardianNumber = ed_user_guardianNumber.getText().toString();
                String gender="";
                if (rbMale.isChecked()){
                    gender = rbMale.getText().toString();
                }
                else{gender = rbFemale.getText().toString();}

                String passport="";
                if (rbPassport_Yes.isChecked()){passport = rbPassport_Yes.getText().toString();}
                else{passport = rbPassport_No.getText().toString();}

                Log.e("Data",userName+","+userMobile+","+userBloodGroup+","+
                        userBankName+","+userBankAccNum+","+userBranchName+","+
                        userifscCode+","+userCompanyName+","+userCompanyAddress+","+
                        userDesignation+","+userDOB+","+userEmail+","+userExperience+","+userFatherName
                        +","+userMotherName+","+userJoiningDate+","+userLocalAddress+","+userNotice
                        +","+userPassword+","+userPermanentAddress+","+userSalary+","+userStatus+","+
                        userGuardianNumber+","+gender+","+passport);
                if(profile_image.getDrawable()==null){
                    ((LinearLayout) findViewById(R.id.ll_profileimagenotuploaded)).setVisibility(View.VISIBLE);
                    TextView textView=findViewById(R.id.tv_profileimagenotuploaded);
                    textView.setError("Please Upload Profile Image");
                }
                else if(userName.isEmpty()){
                    ed_user_name.setError("Please Enter User Name");
                    ed_user_name.requestFocus();
                }
                else if(userMobile.isEmpty()){
                    ed_user_mobile.setError("Please Enter Mobile Number");
                    ed_user_mobile.requestFocus();
                }
                else if(userBloodGroup.isEmpty()){
                    ed_user_bloodGroup.setError("Please Enter Blood Group");
                    ed_user_bloodGroup.requestFocus();
                }
                else if(userBankName.isEmpty()){
                    ed_user_bankName.setError("Please Enter Bank Name");
                    ed_user_bankName.requestFocus();
                }
                else if(userBankAccNum.isEmpty()){
                    ed_user_bankAccNum.setError("Please Enter Bank Account Number");
                    ed_user_bankAccNum.requestFocus();
                }
                else if(userBranchName.isEmpty()){
                    ed_user_branchName.setError("Please Enter Branch Name");
                    ed_user_branchName.requestFocus();
                }
                else if(userifscCode.isEmpty()){
                    ed_user_ifscCode.setError("Please Enter IFSC Code");
                    ed_user_ifscCode.requestFocus();
                }
                else if(userCompanyName.isEmpty()){
                    ed_user_companyName.setError("Please Enter Company's Name");
                    ed_user_companyName.requestFocus();
                }
                else if(userCompanyAddress.isEmpty()){
                    ed_user_companyAddress.setError("Please Enter Company Address");
                    ed_user_companyAddress.requestFocus();
                }
                else if(userDesignation.isEmpty()){
                    ed_user_designation.setError("Please Enter User Designation");
                    ed_user_designation.requestFocus();
                }
                else if(userDOB.isEmpty()){
                    ed_user_DOB.setError("Please Enter Company's Name");
                    ed_user_DOB.requestFocus();
                }
                else if(userEmail.isEmpty()){
                    ed_user_email.setError("Please Enter User Email");
                    ed_user_email.requestFocus();
                }
                else if(userExperience.isEmpty()){
                    ed_user_experience.setError("Please Enter User Experience");
                    ed_user_experience.requestFocus();
                }
                else if(userFatherName.isEmpty()){
                    ed_user_fatherName.setError("Please Enter User Father's Name");
                    ed_user_fatherName.requestFocus();
                }
                else if(userMotherName.isEmpty()){
                    ed_user_motherName.setError("Please Enter User Mother's Name");
                    ed_user_motherName.requestFocus();
                }
                else if(userJoiningDate.isEmpty()){
                    ed_user_joiningDate.setError("Please Enter User Joining Date");
                    ed_user_joiningDate.requestFocus();
                }
                else if(userLocalAddress.isEmpty()){
                    ed_user_localAddress.setError("Please Enter Local Address");
                    ed_user_localAddress.requestFocus();
                }
                else if(userNotice.isEmpty()){
                    ed_user_notice.setError("Please Enter Notice Period");
                    ed_user_notice.requestFocus();
                }
                else if(userPassword.isEmpty()){
                    ed_user_password.setError("Please Enter User Password");
                    ed_user_password.requestFocus();
                }
                else if(userPermanentAddress.isEmpty()){
                    ed_user_permanentAddress.setError("Please Enter User Permanent Address");
                    ed_user_permanentAddress.requestFocus();
                }
                else if (sp_user_status.getSelectedItem().toString().trim().equalsIgnoreCase("Select")) {
                    Toast.makeText(AddEmployeeActivity.this, "Please Select User Status", Toast.LENGTH_SHORT).show();
                }
                else{
                  saveEmployeeDetails(userName,userMobile,userBloodGroup,userBankName,userBankAccNum,
                          userBranchName,userifscCode,userCompanyName,
                          userCompanyAddress,userDesignation,userDOB,userEmail,userExperience,userFatherName,
                          userMotherName,userJoiningDate,userLocalAddress,userNotice,userPassword,
                          userPermanentAddress,userSalary,userStatus,userGuardianNumber,gender,passport);
                }

            }
        });

    }

    public void selectPicType() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        TextView title = new TextView(AddEmployeeActivity.this);
        title.setText("Add Photo!");
        title.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        title.setPadding(10, 15, 15, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                AddEmployeeActivity.this);
        builder.setCustomTitle(title);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    openCamera();
                } else if (items[item].equals("Choose from Library")) {
                    openGallery();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }
    public void openCamera(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 101);
        ((LinearLayout) findViewById(R.id.ll_profileimagenotuploaded)).setVisibility(View.INVISIBLE);

    }

    public void openGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 102);
        ((LinearLayout) findViewById(R.id.ll_profileimagenotuploaded)).setVisibility(View.INVISIBLE);

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 101:
                if(resultCode == RESULT_OK){
                    try {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        Uri uri = getImageUri(AddEmployeeActivity.this, bitmap);
                        img_name = getFileName(uri);
                        Log.e("Bitmap Uri", img_name);
                        profile_image.setImageBitmap(bitmap);
                    }catch (Exception e){e.printStackTrace();}
                }

                break;
            case 102:
                if(resultCode == RESULT_OK){
                    try {
                        Uri selectedImage = data.getData();
                        img_name = getFileName(selectedImage);
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        Log.e("Gallery Name", img_name);
                        profile_image.setImageURI(selectedImage);
                    }catch (Exception e){e.printStackTrace();}
                }
                break;
        }

    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
            {
                Log.e("TAG","Permission is granted");
                return true;
            }
            else if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            {
                Log.e("Read","Permission");
                return true;
            }
            else {
                Log.e("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.e("TAG","Permission is granted");
            return true;
        }
    }
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.e("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(AddEmployeeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



    public void saveEmployeeDetails(final String userN,
    final String userM, final String userBG,final String userBN,
    final String userBAN,final String userBranch,final String userIFSC,
    final String userCN,final String userCA,final String userD,
    final String userDOB,final String userEm,final String userEx,
    final String userFN,final String userMN,final String userJD,
    final String userLA,final String userNo,final String userPass,final String userPA,final String userSal,
    final String userSt,final String userGN,final String userGender,final String userPassport){
        if (!AppConstant.checkConnection(AddEmployeeActivity.this)) {
            Toast.makeText(AddEmployeeActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
        final CustomProgressDialogue pd= new CustomProgressDialogue(AddEmployeeActivity.this);
        pd.setCancelable(false);
        pd.show();

        //String url = "http://192.168.1.14:1234/userapi/signUp";
        String url = AppGlobalConstants.WEBSERVICE_BASE_URL_NEW +"signUpNew";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                try {
                    pd.dismiss();
                    String resultResponse = new String(response.data);
                    parseResponse(resultResponse);
                    Log.e("Response",resultResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    try {
                        String result = new String(networkResponse.data);
                        Log.e("Result",result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("BankName",userBN);
                map.put("BankAccountNumber",userBAN);
                map.put("BranchName",userBranch);
                map.put("IFSCCode",userIFSC);
                map.put("BloodGroup",userBG);
                map.put("CompanyName",userCN);
                map.put("CompanyAddress",userCA);
                map.put("CreationDate","");
                map.put("Designation",userD);
                map.put("DOB",userDOB);
                map.put("Email",userEm);
                //map.put("EmpCode","1");
                map.put("Experience",userEx);
                map.put("FatherName",userFN);
                map.put("Gender",userGender);
                map.put("JoiningDate",userJD);
                map.put("LocalAddress",userLA);
                map.put("Mobile",userM);
                map.put("ModificationDate","");
                map.put("MothersName",userMN);
                map.put("Notice_Period",userNo);
                map.put("Passport",userPassport);
                map.put("Password",userPass);
                map.put("PermanentAddress",userPA);
                map.put("Salary",userSal);
                map.put("Status",userSt);
                map.put("Username",userN);
                map.put("GurdianNumber",userGN);

                return map;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("Photo", new DataPart(img_name, AppHelper.getBitmap(AddEmployeeActivity.this,bitmap),"image/jpeg"));


                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        multipartRequest.setShouldCache(false);
        VolleySingletonImage.getInstance(AddEmployeeActivity.this).addToRequestQueue(multipartRequest);
    }

    public void parseResponse(String response){
        Log.e("Attendance Response", "Response " + response);
        try {
            JSONObject obj = new JSONObject(response);
            String msg = obj.getString("data");
            if(msg.equals("SignUp Successfully!")){
                Toast.makeText(AddEmployeeActivity.this, "SignUp Successfully!", Toast.LENGTH_LONG).show();
                        finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(AddEmployeeActivity.this, "Getting Null From Server.", Toast.LENGTH_LONG).show();
        }
    }

}

