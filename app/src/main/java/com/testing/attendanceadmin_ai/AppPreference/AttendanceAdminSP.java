package com.testing.attendanceadmin_ai.AppPreference;

import android.content.Context;
import android.content.SharedPreferences;

public class AttendanceAdminSP {

    public static final String PREFS_NAME = "ATTENDANCEADMIN_PREFS";
    public static final String MOBILE = "Mobile";
    public static final String USERNAME = "Name";
    public static final String COMPANYNAME = "CompanyName";
    public static final String LATITIDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String COMPANYLOCATION = "CompanyLocation";
    public static final String SITELOCATION = "SiteLocation";
    public static final String EMPCODE = "EmpCode";
    public static final String COMPANYSTATUSID = "Company_StatusId";
    public static final String SITEID = "Site_Id";
    private Context context;


    public static void saveMobileToPreferences(Context ctx, String mobile) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(MOBILE, mobile);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadMobileFromPreferences(Context ctx) {
        String mobile = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            mobile = prefs.getString(MOBILE, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mobile;
    }


    public static void saveUserNameToPreferences(Context ctx, String username) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(USERNAME, username);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadUserNameFromPreferences(Context ctx) {
        String username = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            username = prefs.getString(USERNAME, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return username;
    }


    public static void saveCompanyNameToPreferences(Context ctx, String companyname) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(COMPANYNAME, companyname);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadCompanyNameFromPreferences(Context ctx) {
        String companyname = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            companyname = prefs.getString(COMPANYNAME, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return companyname;
    }

    public static void saveUserLatituteToPreferences(Context ctx, String latitude) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(LATITIDE, latitude);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadUserLatitudeFromPreferences(Context ctx) {
        String latitude = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            latitude = prefs.getString(LATITIDE, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return latitude;
    }

    public static void saveUserLongitudeToPreferences(Context ctx, String longitude) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(LONGITUDE, longitude);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadUserLongitudeFromPreferences(Context ctx) {
        String longitude = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            longitude = prefs.getString(LONGITUDE, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return longitude;
    }


    public static void saveUserCompanyLocationToPreferences(Context ctx, String companyLocation) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(COMPANYLOCATION, companyLocation);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadUserCompanyLocationFromPreferences(Context ctx) {
        String companyLocation = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            companyLocation = prefs.getString(COMPANYLOCATION, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return companyLocation;
    }

    public static void saveUserEmpCodeToPreferences(Context ctx, String empCode) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(EMPCODE, empCode);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadUserEmpCodeFromPreferences(Context ctx) {
        String empCode = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            empCode = prefs.getString(EMPCODE, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return empCode;
    }


    public static void saveCompanyStatusIdToPreferences(Context ctx, String statusId) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(COMPANYSTATUSID, statusId);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadCompanyStatusIdFromPreferences(Context ctx) {
        String statusId = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            statusId = prefs.getString(COMPANYSTATUSID, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return statusId;
    }

    public static void saveSiteIdToPreferences(Context ctx, String siteId) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(SITEID, siteId);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadSiteIdFromPreferences(Context ctx) {
        String siteId = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            siteId = prefs.getString(SITEID, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return siteId;
    }



    public static void saveSiteLocationToPreferences(Context ctx, String siteLocation) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(SITELOCATION, siteLocation);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadSiteLocationFromPreferences(Context ctx) {
        String siteLocation = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            siteLocation = prefs.getString(SITELOCATION, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return siteLocation;
    }


}
