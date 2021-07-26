package com.zebra.main.firebase;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.zebra.utilities.Common;

public class CrashAnalytics {
    public static void CrashReport(Exception exception) {
        //FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseCrashlytics.getInstance().setUserId(Common.IMEI);
        FirebaseCrashlytics.getInstance().setCustomKey("MACADDRESS", Common.IMEI /* string value */);
        FirebaseCrashlytics.getInstance().recordException(exception);
        //FirebaseCrashlytics.getInstance().log(exception.toString());
    }

    public void setKeysBasic(String key) {
        FirebaseCrashlytics.getInstance().setCustomKey(key, "foo" /* string value */);

        FirebaseCrashlytics.getInstance().setCustomKey(key, true /* boolean value */);

        FirebaseCrashlytics.getInstance().setCustomKey(key, 1.0 /* double value */);

        FirebaseCrashlytics.getInstance().setCustomKey(key, 1.0f /* float value */);

        FirebaseCrashlytics.getInstance().setCustomKey(key, 1 /* int value */);
    }


    public static void logReportOnly(String logs) {
        FirebaseCrashlytics.getInstance().log(logs);
        FirebaseCrashlytics.getInstance().setCustomKey("UserID", Common.Username /* string value */);
        FirebaseCrashlytics.getInstance().setCustomKey("MACADDRESS", Common.IMEI /* string value */);
        //throw new RuntimeException("Test Crash");
        //setUserId();
    }

    public static void setUserId() {
        FirebaseCrashlytics.getInstance().setUserId(Common.IMEI);
    }

    public static void methodThatThrows() throws Exception {
        throw new Exception();
    }

    public static void logCaughtEx() {
        try {
            methodThatThrows();
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.toString());
        }
    }
}
