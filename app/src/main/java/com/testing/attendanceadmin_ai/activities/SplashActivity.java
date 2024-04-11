package com.testing.attendanceadmin_ai.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.testing.attendanceadmin_ai.AppPreference.AttendanceAdminSP;
import com.testing.attendanceadmin_ai.R;

public class SplashActivity extends AppCompatActivity {

    public final int SPLASH_DISPLAY_LENGTH = 5000; //splash screen will be shown for 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkOrientation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                actionNext();
                //startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                //finish();
            }

        }, SPLASH_DISPLAY_LENGTH);
    }
    public void checkOrientation(){
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    private void actionNext(){

        String mob = AttendanceAdminSP.loadMobileFromPreferences(SplashActivity.this);
        String status = AttendanceAdminSP.loadCompanyStatusIdFromPreferences(SplashActivity.this);
        Log.e("Shared Pref",mob);
        if(!mob.equalsIgnoreCase("NA") && status.equalsIgnoreCase("1")){
            Intent intent = new Intent(SplashActivity.this,DashboardActivity.class);
            startActivity(intent);
            finishAffinity();
        }
        else if(!mob.equalsIgnoreCase("NA") && status.equalsIgnoreCase("2")){
            Intent i = new Intent(SplashActivity.this, RegisteredCompanyActivity.class);
            startActivity(i);
            finish();
        }
        else{
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

}
