package com.zebra.utilities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.widget.ArrayAdapter;

import com.zebra.R;

import java.util.Set;

public class BlutoothCommonClass {
    static Activity activity;
    static BluetoothAdapter bluetoothAdapter;

    public BlutoothCommonClass(Activity activity2) {
        activity = activity2;
    }

    /*Bluetooth Printer*/
    public static boolean CheckBlueToothState() {
        boolean support = false;
        if (bluetoothAdapter == null) {
            support = false;
            //stateBluetooth.setText("Bluetooth NOT support");
            System.out.println("Bluetooth NOT support");
        } else {
            support = true;
            if (bluetoothAdapter.isEnabled()) {
                if (bluetoothAdapter.isDiscovering()) {
                    //stateBluetooth.setText("Bluetooth is currently in device discovery process.");
                    System.out.println("Bluetooth is currently in device discovery process.");
                } else {
                    //stateBluetooth.setText("Bluetooth is Enabled.");
                    System.out.println("Bluetooth is Enabled.");
                    Common.mPairedDevicesArrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1);

                    BluetoothAdapter bluetoothAdapter
                            = BluetoothAdapter.getDefaultAdapter();
                    Set<BluetoothDevice> pairedDevices
                            = bluetoothAdapter.getBondedDevices();

                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            String deviceBTName = device.getName();
                            String deviceBTIP = device.getAddress();
                            String deviceBTMajorClass = getBTMajorDeviceClass(device.getBluetoothClass().getMajorDeviceClass());
                            Common.mPairedDevicesArrayAdapter.add(deviceBTName + "\n" + deviceBTIP);
                        }
                    } else {
                        Common.mPairedDevicesArrayAdapter.add(activity.getResources().getText(R.string.none_paired).toString());
                    }
                }
            } else {
                //stateBluetooth.setText("Bluetooth is NOT Enabled!");
                /*Intent enableBtIntent = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult (enableBtIntent, REQUEST_ENABLE_BT);*/
            }
        }
        return support;
    }

    public static String getBTMajorDeviceClass(int major) {
        switch (major) {
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                return "AUDIO_VIDEO";
            case BluetoothClass.Device.Major.COMPUTER:
                return "COMPUTER";
            case BluetoothClass.Device.Major.HEALTH:
                return "HEALTH";
            case BluetoothClass.Device.Major.IMAGING:
                return "IMAGING";
            case BluetoothClass.Device.Major.MISC:
                return "MISC";
            case BluetoothClass.Device.Major.NETWORKING:
                return "NETWORKING";
            case BluetoothClass.Device.Major.PERIPHERAL:
                return "PERIPHERAL";
            case BluetoothClass.Device.Major.PHONE:
                return "PHONE";
            case BluetoothClass.Device.Major.TOY:
                return "TOY";
            case BluetoothClass.Device.Major.UNCATEGORIZED:
                return "UNCATEGORIZED";
            case BluetoothClass.Device.Major.WEARABLE:
                return "AUDIO_VIDEO";
            default:
                return "unknown!";
        }
    }

    public static boolean isBluetoothEnabled() {
        boolean ret = false;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled() == true) {
            ret = true;
        }
        return ret;
    }
}
