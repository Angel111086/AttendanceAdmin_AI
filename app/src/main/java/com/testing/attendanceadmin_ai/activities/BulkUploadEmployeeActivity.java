package com.testing.attendanceadmin_ai.activities;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.testing.attendanceadmin_ai.R;
import com.testing.attendanceadmin_ai.multipart.DownloadTask;
import com.testing.attendanceadmin_ai.multipart.VolleyMultipartRequest;
import com.testing.attendanceadmin_ai.utility.AppGlobalConstants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BulkUploadEmployeeActivity extends AppCompatActivity {

    LinearLayout ln_download,ln_upload;
    TextView txtDownload;
    private RequestQueue rQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_upload_employee);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        init();
        listener();
    }

    public void init()
    {
        ln_download = findViewById(R.id.ln_download);
        ln_upload = findViewById(R.id.ln_upload);
        txtDownload = findViewById(R.id.txtDownload);
    }

    public void listener()
    {
        ln_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadTask(BulkUploadEmployeeActivity.this, "EmployeeTemplate.xlsx");
            }
        });

        ln_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseDocuments();
            }
        });
    }


    public void NotificationPopUp() {
        {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this.getApplicationContext(), "notify_001");
            File file = new File(
                    Environment.getExternalStorageDirectory() + "/Employee Template");
            Intent install = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
// Old Approach
            // install.setDataAndType(Uri.fromFile(file), "*/*");
// End Old approach
// New Approach
            Uri apkURI = FileProvider.getUriForFile(
                    this,
                    this.getApplicationContext()
                            .getPackageName() , file);
            install.setDataAndType(apkURI, "*/*");
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
// End New Approach
            // this.startActivity(install);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 10, install, 0);
            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText(("File Managar/Employee Template"));
            bigText.setBigContentTitle("Template Downloaded");
            bigText.setSummaryText("Employee");
            mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
            mBuilder.setContentTitle("Your Title");
            mBuilder.setContentText("Your text");
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setStyle(bigText);
            mBuilder.setContentIntent(pendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "notify_001";
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Attendance Admin",
                        NotificationManager.IMPORTANCE_HIGH);
                mNotificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channelId);
            }
            mNotificationManager.notify(0, mBuilder.build());
        }

    }

    @SuppressLint("SdCardPath")
    public void alreadycreatedFile()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            openDirt();
        } else {

            Intent intent = new Intent();
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            intent.putExtra("CONTENT_TYPE", "File/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Employee Template/";
            File folder = new File(path);
            intent.setDataAndType(Uri.fromFile(folder),"resourse/folder");
            startActivity(Intent.createChooser(intent, "Open folder"));

        }
    }

    public void openDirt(){
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        Boolean isSDSupportedDevice = Environment.isExternalStorageRemovable();

        if(isSDSupportedDevice && isSDPresent)
        {
            Log.e("sd hai","Sd card hao phone mai");
            // path = theAct.getExternalCacheDir().getAbsolutePath() + "/GrammarFolder";
            File file = new File(
                    getExternalCacheDir().getAbsolutePath() + "/Employee Template/");
            Intent install = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addCategory(Intent.CATEGORY_OPENABLE);

            Uri apkURI = FileProvider.getUriForFile(
                    this,
                    this.getApplicationContext()
                            .getPackageName(), file);
            install.setDataAndType(apkURI, "*/*");
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.startActivity(install);

        }
        else {
            File file = new File(
                    Environment.getExternalStorageDirectory() + "/Employee Template/");
            Intent install = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            install.addCategory(Intent.CATEGORY_OPENABLE);

            // install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Uri apkURI = FileProvider.getUriForFile(
                    this,
                    this.getApplicationContext()
                            .getPackageName(), file);
            install.setDataAndType(apkURI, "*/*");
            this.startActivity(install);

        }
    }

    public void browseDocuments() {
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
//
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("request_code", String.valueOf(requestCode));
        if (requestCode == 1 && resultCode == RESULT_OK) {
            File file = new File(data.getData().getPath());
            String path = file.getAbsolutePath();
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(path));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append("\n");

                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 102 && resultCode == RESULT_OK) {
            try {

                Uri selectedDoc = data.getData();
                String uriString = selectedDoc.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                String displayName = null;
                Log.e("uriString", path);
                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = this.getContentResolver().query(selectedDoc, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            Log.e("nameeeee>>>>  ", displayName);

                            uploadEmployee(displayName, selectedDoc);
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                    Log.d("nameeeee>>>>  ", displayName);
                } else {
                    displayName = myFile.getName();
                    Log.d(">>>nameeeee>  ", displayName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            super.onActivityResult(requestCode, resultCode, data);

        }
        if (requestCode == 120 && resultCode == RESULT_OK){
            Log.e("data10", String.valueOf(data));
        }


    }

    private void uploadEmployee(final String pdfname, Uri pdffile) {

        InputStream iStream = null;
        String url1 = AppGlobalConstants.WEBSERVICE_BASE_URL_NEW + "bulkUploadEmployee";

        try {

            iStream = getContentResolver().openInputStream(pdffile);
            final byte[] inputData = getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url1,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("ressssssoo", new String(response.data));
                            parseResponseEmployee(new String(response.data));
                            rQueue.getCache().clear();
//
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    try {
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return params;
//                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("empfile", new DataPart(pdfname, inputData));
                    return params;
                }

            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(BulkUploadEmployeeActivity.this);
            rQueue.add(volleyMultipartRequest);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void parseResponseEmployee(String response) {
        try {
            Log.e("Update Response", response);
            JSONObject obj = new JSONObject(response);
            String status = obj.getString("status");
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");
            Log.e("response", response);
            if(success == false && message.equals("Employee Code is Active"))
            {
                Toast.makeText(this, "Employee Code is Active", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "SignUp Successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}