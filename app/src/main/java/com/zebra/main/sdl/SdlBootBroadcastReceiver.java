package com.zebra.main.sdl;

import java.io.File;

import com.zebra.main.interfac.BaseApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zebra.android.jb.Preference;

import android.os.Handler;
import android.os.Message;

public class SdlBootBroadcastReceiver extends BroadcastReceiver {

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000:
                    Context arg0 = (Context) msg.obj;
                    if (Preference.getScanSelfopenSupport(
                            BaseApplication.getAppContext(), true)) {
                        Intent service = new Intent(arg0, SdlScanService.class);
                        arg0.startService(service);
                        System.out.println("SdlBootBroadcastReceiver serviceup");
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        if (
                "SdlScanServiceDestroy".equals(arg1.getAction())) {
            if (Preference.getScanSelfopenSupport(
                    BaseApplication.getAppContext(), true)) {
                Intent service = new Intent(arg0, SdlScanService.class);
                arg0.startService(service);
                System.out.println("SdlBootBroadcastReceiver SdlScanServiceDestroy");
            }
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(arg1.getAction()) && (exist("/dev/moto_sdl"))) {
            if (Preference.getScanSelfopenSupport(
                    BaseApplication.getAppContext(), true)) {
                Intent service = new Intent(arg0, SdlScanService.class);
                arg0.startService(service);
                System.out.println("SdlBootBroadcastReceiver ACTION_BOOT_COMPLETED");
            }
        }
    }

    private boolean exist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        } else
            return true;
    }
}
