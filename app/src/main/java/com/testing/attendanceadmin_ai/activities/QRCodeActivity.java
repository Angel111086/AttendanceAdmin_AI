package com.testing.attendanceadmin_ai.activities;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.testing.attendanceadmin_ai.AppPreference.AttendanceAdminSP;
import com.testing.attendanceadmin_ai.R;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

public class QRCodeActivity extends AppCompatActivity {

    ImageView img_back,qrscan_img;
    Button btn_ok;
    TextView txt_compName,txt_siteAddress;
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;
    //DatabaseReference database;
    ProgressDialog pd;
    String empMobile,empLocation,empCode;
    Date currentTime;
    Handler mHandler;
    private volatile boolean flag = true;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        checkOrientation();
        init();
        listener();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (flag) {
                    try {
                        Thread.sleep(60000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                // Write your code here to update the UI.
                                try {
                                    JSONObject object = new JSONObject();
                                    object.accumulate("SITE_ID", AttendanceAdminSP.loadSiteIdFromPreferences(QRCodeActivity.this));
                                    //object.accumulate("EMPCODE","2002");
                                    object.accumulate("EMPDATE", DateFormat.getDateTimeInstance().format(new Date()));
                                    object.accumulate("EMPLATITUDE",AttendanceAdminSP.loadUserLatitudeFromPreferences(QRCodeActivity.this));
                                    object.accumulate("EMPLONGITUDE",AttendanceAdminSP.loadUserLongitudeFromPreferences(QRCodeActivity.this));
                                    object.accumulate("Refreshed",i);
                                    bitmap = TextToImageEncode(object.toString());
                                    qrscan_img.setImageBitmap(bitmap);
                                    i++;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();
    }
    public void checkOrientation(){
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    private void init() {
        mHandler = new Handler();
        img_back=(ImageView)findViewById(R.id.img_back);
        //btn_ok=(Button)findViewById(R.id.btn_ok);
        qrscan_img = (ImageView) findViewById(R.id.qrscan_img);
        txt_compName = findViewById(R.id.txt_compName);
        txt_siteAddress = findViewById(R.id.txt_siteAddress);
        txt_compName.setText(AttendanceAdminSP.loadCompanyNameFromPreferences(QRCodeActivity.this));
        txt_siteAddress.setText(AttendanceAdminSP.loadSiteLocationFromPreferences(QRCodeActivity.this));
        //Log.e("LoginModel",empCode);
        try {
            JSONObject object = new JSONObject();
            object.accumulate("SITE_ID",AttendanceAdminSP.loadSiteIdFromPreferences(QRCodeActivity.this));
            object.accumulate("EMPDATE",DateFormat.getDateTimeInstance().format(new Date()));
            object.accumulate("EMPLATITUDE",AttendanceAdminSP.loadUserLatitudeFromPreferences(QRCodeActivity.this));
            object.accumulate("EMPLONGITUDE",AttendanceAdminSP.loadUserLongitudeFromPreferences(QRCodeActivity.this));
            bitmap = TextToImageEncode(object.toString());
            qrscan_img.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void listener() {

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(QRCodeActivity.this, DetailsActivity.class));
//            }
//        });

    }


    public Bitmap TextToImageEncode(String value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            Log.e("Value",value);
            bitMatrix = new MultiFormatWriter().encode(
                    value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, 500, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public void stopRunning()
    {
        flag = false;
    }
    @Override
    protected void onDestroy() {
        stopRunning();
        super.onDestroy();

    }
}
