package com.action.screenmirror.utils;

import java.io.File;
import java.io.FileInputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Settings;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;

public class DeviceInformation {

    public static String getSerialNumber(Context context) {
        String android_id = null; 
        /*
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.action.screenmirror/setting");
        Cursor query = contentResolver.query(uri , null, null, null, null);
        if (query != null && query.moveToFirst()) {
            android_id = query.getString(query.getColumnIndex("android_id"));
        }else {
            Log.e("DeviceUtils", "--getAndroidId--fail");
        }
        if (query != null) {
            query.close();
        }
        */

        android_id = Settings.Secure.getString(context.getContentResolver(), "android_id");
        if (android_id == null) {
            Log.e("DeviceUtils", "get device id fail");
        }

        return android_id ;
    }

}
