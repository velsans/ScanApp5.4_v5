package com.zebra.main.api;

import android.net.Uri;

public class ServiceURL {

    /*URL's*/
    public final static String ServiceURLPathLocalNetwork = "http://10.200.10.15:8085/GWWHHDeviceAPI/";
    /*public final static String ServiceURLPathLocalNetwork = "http://192.162.248.240/RetailerCollection/RetailerCollectionAPI";*/
    public final static String SubServerServiceURLPathLiveNetwork = "http://172.16.15.217:4488/";//"http://10.10.13.72:4488/";
    public final static String LIVE_URL = "http://dv.ysecit.com/GWWHHDeviceAPI/";
    public final static String LOCAL_BASE_URL = "http://demo.ysecit.in:8014/GWWHHDeviceAPI/";
    public final static String LocalExternal = "http://10.10.13.71:8082/GwwDeviceapi/ScannedResult/GetMasterDatavalue_Test";
    //public final static String ExternalDataBaseUrl = "http://demo.ysecit.in:8014/EtopUp/ExternalDB/";// local
    public final static String ExternalDataBaseUrl = "http://dv.ysecit.com/GWWHHDeviceAPI/ExternalDB/";// live

    public final static String ControllorName = "ScannedResult", ExportControllerName = "Export", ExternalPurchase = "ExternalPurchase";
    /*Final Url*/
    public static String Local_live;
    public final static String BASE_URL =Local_server();

    public static String Local_server() {
        Local_live = "Local";
        return LOCAL_BASE_URL;
    }

    public static String Live_server() {
        Local_live = "Live";
        return LIVE_URL;
    }

    /*Service HTTP-URL*/
    public static String getServiceURL(String WebAPIControllerName, String Methodname) {
        Uri.Builder DGUrlPLD = new Uri.Builder();
        DGUrlPLD.appendPath(WebAPIControllerName)// Controllor
                .appendPath(Methodname);// Method name
        /*Test URl*/
        //String FinalUrlURL = Common.ServiceBASE_URLNetwork + DGUrlPLD.build().toString();
        /*Batch URl*/
        String FinalUrlURL = BASE_URL + DGUrlPLD.build().toString();
        /*Live URl*/
        //String FinalUrlURL = ServiceURLPathLocalNetwork + DGUrlPLD.build().toString();
        return FinalUrlURL;
    }

}
