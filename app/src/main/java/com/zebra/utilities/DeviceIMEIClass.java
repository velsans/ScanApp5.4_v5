package com.zebra.utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;

public class DeviceIMEIClass {
    private static Context context_ths;

    public DeviceIMEIClass(Context context) {
        context_ths = context;
    }

    public String GetDeviceID() {
        String IMEI = "";
        TelephonyManager telephonyManager = (TelephonyManager) context_ths.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(context_ths, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                IMEI = telephonyManager.getImei();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(context_ths, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (telephonyManager.getDeviceId() != null) {
                    IMEI = telephonyManager.getDeviceId();
                } else {
                    IMEI = Settings.Secure.getString(context_ths.getContentResolver(), Settings.Secure.ANDROID_ID);
                }
            }
        }
        return telephonyManager.getDeviceId();
    }

    public String GetDeviceIMEI() {
        String IMEI = "";
        TelephonyManager telephonyManager = (TelephonyManager) context_ths.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            IMEI= telephonyManager.getImei();
        } else {
            IMEI = telephonyManager.getDeviceId();
        }
        return IMEI;
    }

}
