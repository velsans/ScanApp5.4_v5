package com.zebra.utilities;

import android.content.Context;
import android.net.Uri;

import java.net.URL;
import java.net.URLConnection;

public class ServiceURL {

    //public final static String ServiceURLPathLocalNetwork = "http://demo.ysecit.in:8082/GWWHHDeviceApi";
    public final static String ServiceURLPathLocalNetwork = "http://10.200.10.15:8085/GWWHHDeviceAPI";
    /*public final static String ServiceURLPathLocalNetwork = "http://192.162.248.240/RetailerCollection/RetailerCollectionAPI";*/
    public final static String ServiceURLPathLiveNetwork = "http://dv.ysecit.com/GWWHHDeviceAPI";
    public final static String SubServerServiceURLPathLiveNetwork = "http://10.10.13.89";
    public final static String LoginRegContraollerNme = "Verification", DenoControllerName = "ClientInfo", ControllorName = "ScannedResult",ExportControllerName="Export";
    private static Context _context;
    private final static int TIME_OUT = 5000; // Millisecond
    public static boolean CONNECTION_AVAILABILITY = true;
    public final static String[] ServiceURL = new String[]{ServiceURLPathLocalNetwork, ServiceURLPathLiveNetwork};
    //https://dxa.suribet.sr/RetailerCollectionAPI/TestAPI/Test -API TEST API
    public ServiceURL(Context context) {
        _context = context;
    }

    public static boolean IsConnectingToInternet(String ipAddress) {
        try {
            URL myUrl = new URL(ipAddress);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(TIME_OUT);
            connection.connect();
            Common.IsNetworkConnection = true;
            // InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            // inputStream.close();
            return true;
        } catch (Exception e) {
            Common.IsNetworkConnection = false;
            return false;
        }
    }

    public static void ConnectionCheckAvailability() {
        try {
            for (int i = 0; i < ServiceURL.length; i++) {
                if (IsConnectingToInternet(ServiceURL[i])) {
                    Common.ServiceURLPath = ServiceURL[i];
                    CONNECTION_AVAILABILITY = true;
                    return;
                }
            }
            CONNECTION_AVAILABILITY = false;
        } catch (Exception e) {

        }
    }

    public static String getServiceURL(String WebAPIControllerName, String Methodname) {
        Uri.Builder DGUrlPLD = new Uri.Builder();
        DGUrlPLD.appendPath(WebAPIControllerName)// Controllor
                .appendPath(Methodname);// Method name
        String FinalUrlURL = ServiceURLPathLocalNetwork + DGUrlPLD.build().toString();
        return FinalUrlURL;
    }
}
