package com.testing.attendanceadmin_ai.cam;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.testing.attendanceadmin_ai.R;
import com.testing.attendanceadmin_ai.activities.VolleySingletonImage;
import com.testing.attendanceadmin_ai.multipart.AppHelper;
import com.testing.attendanceadmin_ai.multipart.VolleyMultipartRequest;
import com.testing.attendanceadmin_ai.utility.AppConstant;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CamActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.ShutterCallback,Camera.PictureCallback
{
    Camera mCamera;
    SurfaceView mPreview;
    String filePath ;
    int currentCameraId = 0;
    String stringImage = "";
    byte picdata[];
    Bitmap bitmap;
    String imgName="";
    //Button btn_Capture;
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera_preview);
        mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCamera = Camera.open(currentCameraId);
        isStoragePermissionGranted();
        onCancelClick();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    onSnapClick();
                {}}},5000);
    }

//    public void init(){
//        btn_Capture = findViewById(R.id.btn_Capture);
//    }
//    public void listener(){
//        btn_Capture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //onSnapClick(v);
//            }
//        });
//    }



    @Override
    public void onPause() {
        super.onPause();

        mCamera.stopPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
        Log.e("CAMERA","Destroy");
    }

    public void onCancelClick() {

        //mCamera.stopPreview();
        //mCamera.release();
        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        mCamera = Camera.open(currentCameraId);
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
            case Surface.ROTATION_90: degrees = 90; break; //Landscape left
            case Surface.ROTATION_180: degrees = 180; break;//Upside down
            case Surface.ROTATION_270: degrees = 270; break;//Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;

        //STEP #2: Set the 'rotation' parameter
        Camera.Parameters params = mCamera.getParameters();
        //params.setRotation(rotate);
        //params.setRotation(getCorrectCameraOrientation(info,mCamera));
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mCamera.setParameters(params);
        //mCamera.setDisplayOrientation(90);
        mCamera.setDisplayOrientation(getCorrectCameraOrientation(info,mCamera));
        mCamera.startPreview();
    }

    public void onSnapClick() {
        mCamera.takePicture(this, null, null, (Camera.PictureCallback) this);
    }

    @Override
    public void onShutter() {
        Toast.makeText(this, "Click!", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        //Here, we chose internal storage
        assign(data);
        Log.e("picData",picdata.length+"");
        File directory = new File(Environment.getExternalStorageDirectory() + "/EmpDir");

        if (!directory.exists()) {
            File EmployeeDirectory = new File("/sdcard/EmpDir/");
            EmployeeDirectory.mkdirs();
        }

        FileOutputStream fos = null;
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String img_name = "Employee " + currentDateTimeString;
        imgName = img_name;
        File file = new File(new File("/sdcard/EmpDir/"), img_name);
        if (file.exists()) {
            //file.delete();
            Log.e("Img","Exists");
        }
        try {


            fos = new FileOutputStream(
                    filePath);

            bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
            //flip(bitmap,Direction.HORIZONTAL);
            filePath = "/sdcard/EmpDir/"+img_name+".jpg";
            byte da[] = AppHelper.getBitmap(CamActivity.this,bitmap);
            Log.e("AppHelper","test"+da.length);
            fos.write(data);
            fos.close();

            saveImageToDB();
            Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
        } finally {
            Intent i = getIntent();
            i.putExtra("Path",filePath);
            setResult(RESULT_OK, i);
            finish();
        }
        camera.startPreview();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
            case Surface.ROTATION_90: degrees = 90; break; //Landscape left
            case Surface.ROTATION_180: degrees = 180; break;//Upside down
            case Surface.ROTATION_270: degrees = 270; break;//Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;

        //STEP #2: Set the 'rotation' parameter
        Camera.Parameters params = mCamera.getParameters();
        //params.setRotation(rotate);
        params.setRotation(getCorrectCameraOrientation(info,mCamera));
        mCamera.setParameters(params);
        //mCamera.setDisplayOrientation(90);
        mCamera.setDisplayOrientation(getCorrectCameraOrientation(info,mCamera));
        mCamera.startPreview();


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("PREVIEW","surfaceDestroyed");
    }

    public void assign(byte[] b){

        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.write(b);
            dos.close();
            picdata = baos.toByteArray();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //Change Direction
    public enum Direction { VERTICAL, HORIZONTAL };

    /**
     Creates a new bitmap by flipping the specified bitmap
     vertically or horizontally.
     @param src        Bitmap to flip
     @param type       Flip direction (horizontal or vertical)
     @return           New bitmap created by flipping the given one
     vertically or horizontally as specified by
     the <code>type</code> parameter or
     the original bitmap if an unknown type
     is specified.
     **/
    public static Bitmap flip(Bitmap src, Direction type) {
        Matrix matrix = new Matrix();

        if(type == Direction.VERTICAL) {
            matrix.preScale(1.0f, -1.0f);
        }
        else if(type == Direction.HORIZONTAL) {
            matrix.preScale(-1.0f, 1.0f);
        } else {
            return src;
        }

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }


    public int getCorrectCameraOrientation(Camera.CameraInfo info, Camera camera) {

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch(rotation){
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;

        }
        int result;
        if(info.facing==Camera.CameraInfo.CAMERA_FACING_FRONT){
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        }else{
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

    protected Bitmap decodeFileUpgaded(Bitmap bitmap,String pathName) {
        int orientation;
        try {
            if (bitmap == null) {
                return null;
            }

            ExifInterface exif = new ExifInterface(pathName);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.e("ExifInteface .........", "rotation =" + orientation);
            // exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);
            Log.e("orientation", "" + orientation);
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                Log.i("orientation in", "" + orientation);
                bitmap = rotateImage(bitmap, 180);
                //RotateBitmap rotateBitmap = new RotateBitmap(bitmap);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                Log.i("orientation in", "" + orientation);
                bitmap = rotateImage(bitmap, 90);
                //RotateBitmap rotateBitmap = new RotateBitmap(bitmap);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                Log.i("orientation in", "" + orientation);
                bitmap = rotateImage(bitmap, 270);
                //RotateBitmap rotateBitmap = new RotateBitmap(bitmap);

            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(CropImage.this,"Memory low! please try again.",Toast.LENGTH_LONG).show();
            return null;
        }
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.e("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    public void saveImageToDB(){
        if (!AppConstant.checkConnection(CamActivity.this)) {
            Toast.makeText(CamActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

//        final CustomProgressDialogue pd= new CustomProgressDialogue(CamActivity.this);
//        pd.setCancelable(false);
//        pd.show();

        //String url = AppGlobalConstants.WEBSERVICE_BASE_URL + "AppLoginSignUp?mobileno="+mob;
        String url = "http://attendanceai.pythonanywhere.com/api/uploadimage/attendance/v2";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                try {
                    String resultResponse = new String(response.data);
                    Log.e("Response",resultResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("file", new DataPart(imgName, AppHelper.getBitmap(CamActivity.this,bitmap),"image/jpeg"));


                return params;
            }
        };

        VolleySingletonImage.getInstance(CamActivity.this).addToRequestQueue(multipartRequest);
    }


}

