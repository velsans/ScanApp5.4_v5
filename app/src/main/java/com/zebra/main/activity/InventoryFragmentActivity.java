package com.zebra.main.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zebra.R;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.adapter.CustomAdapter;
import com.zebra.main.adapter.PagerAdapter;
import com.zebra.main.adapter.TabsPagerAdapter;
import com.zebra.main.model.ExternalDB.ExternalDataBaseTableSizesModel;
import com.zebra.main.model.ExternalDB.TransferLogDetailsExModel;
import com.zebra.main.model.ExternalDB.TransferLogDetailsModel;
import com.zebra.main.model.ExternalDB.TruckDetailsModel;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;
import com.zebra.utilities.Communicator;
import com.zebra.utilities.ConnectionFinder;
import com.zebra.utilities.DeviceIMEIClass;
import com.zebra.utilities.GwwException;
import com.zebra.utilities.NonSwipeableViewPager;
import com.zebra.utilities.PrintSlipsClass;
import com.zebra.utilities.ServiceURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class InventoryFragmentActivity extends AppCompatActivity {

    int ViewPageID;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    CustomAdapter customAdapter;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    LocalBroadcastManager lbm;
    TextView deviceNameIEMITXT;
    LinearLayout ProgressBarLay;
    private ViewPager viewPager;

    AlertDialogManager alert = new AlertDialogManager();
    AlertDialog.Builder Signoutbuilder = null;
    AlertDialog SignoutAlert = null;
    boolean isInternetPresent;
    ExternalDataBaseTableSizesModel externalDBmodel;
    TransferLogDetailsExModel externalTransferLogs;
    TruckDetailsModel externalTruckDetailsLogs;
    DeviceIMEIClass device_imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invenory_fragmain);
        Initialization();
        Common.IMEI = device_imei.GetDeviceIMEI();
        if (isNullOrEmpty(Common.IMEI)) {
            AlertDialogBox("Device IMEI", "Device IMEI not found, please contact adminstrator ", false);
            return;
        }
        ExternetDBtoInternalDB();
        GetLocationDeviceandIMEI();
        /*ToolBar*/
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(null);

        /*Tab layout*/
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        for (String Tabs : Common.Tabs) {
            tabLayout.addTab(tabLayout.newTab().setText(Tabs));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        viewPager = findViewById(R.id.inventory_pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                ViewPageID = tab.getPosition();
               /* if (ViewPageID == 0) {
                    lbm = LocalBroadcastManager.getInstance(InventoryFragmentActivity.this);
                    Intent i = new Intent("FELLING_REFRESH");
                    lbm.sendBroadcast(i);
                } else if (ViewPageID == 1) {
                    lbm = LocalBroadcastManager.getInstance(InventoryFragmentActivity.this);
                    Intent i = new Intent("COUNT_REFRESH");
                    lbm.sendBroadcast(i);
                }
                else if (ViewPageID == 2) {
                    lbm = LocalBroadcastManager.getInstance(InventoryFragmentActivity.this);
                    Intent i = new Intent("TRANSFER_REFRESH");
                    lbm.sendBroadcast(i);
                }
                else if (ViewPageID == 3) {
                    lbm = LocalBroadcastManager.getInstance(InventoryFragmentActivity.this);
                    Intent i = new Intent("RECEIVED_REFRESH");
                    lbm.sendBroadcast(i);
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        /*viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });*/
        ViewPageID = viewPager.getCurrentItem();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str == "null";
    }

    public void Initialization() {
        mDBExternalHelper = new ExternalDataBaseHelperClass(InventoryFragmentActivity.this);
        device_imei = new DeviceIMEIClass(InventoryFragmentActivity.this);

        deviceNameIEMITXT = findViewById(R.id.deviceIEMITxT);
        ProgressBarLay = findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
    }

    public void ExternetDBtoInternalDB() {
        ImportDataBaseFromInternalStorage();
        //RefreshActivity();
    }

    public void ImportDataBaseFromInternalStorage() {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GWW";
        File sd = new File(dir);
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;

        File currentDB = new File(sd, Common.EXTERNAL_MASTER_DB_NAME);
        File backupDB = new File(data, Common.INTERNAL_DB_Path);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        spinner.setPopupBackgroundResource(R.color.deeporange);
        spinner.setGravity(Gravity.CENTER);
        spinner.setBackgroundResource(R.color.orange);
        getToLocationsforSpinner(spinner);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            if (Common.IsPrintBtnClickFlag == true) {
                SignoutforLogin(InventoryFragmentActivity.this.getResources().getString(R.string.SignoutMsg));
            }
        }
        if (id == R.id.spinner) {
            if (Common.IsPrintBtnClickFlag == true) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
                //SignoutforLogin(InventoryActivity.this.getResources().getString(R.string.SignoutMsg));
            }
        }
        if (id == R.id.action_refersh) {
            if (Common.IsPrintBtnClickFlag == true) {
                //ExternetDBtoInternalDB();
                ExternalDataBaseSync();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void SignoutforLogin(String ErrorMessage) {
        if (SignoutAlert != null && SignoutAlert.isShowing()) {
            return;
        }
        Signoutbuilder = new AlertDialog.Builder(InventoryFragmentActivity.this);
        Signoutbuilder.setMessage(ErrorMessage);
        Signoutbuilder.setCancelable(true);
        Signoutbuilder.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Signoutbuilder.setNegativeButton(InventoryFragmentActivity.this.getResources().getString(R.string.action_signout),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SignoutYes(dialog, id);
                        dialog.cancel();
                    }
                });

        SignoutAlert = Signoutbuilder.create();
        SignoutAlert.show();

    }

    private void SignoutYes(DialogInterface dialog, int id) {
        Intent oneIntent = new Intent(InventoryFragmentActivity.this, LoginActivity.class);
        startActivity(oneIntent);

    }

    public void GetLocationDeviceandIMEI() {
        try {
            //Common.IMEI = getImeiNumber();
            Common.LocationDeviceList.clear();
            Common.LocationDeviceList = mDBExternalHelper.getAllLocationDevice(Common.IMEI);
            sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
            if (sharedpreferences.contains(Name)) {
                Common.ToLocationID = sharedpreferences.getInt(Name, 0);
                if (sharedpreferences.contains(Email)) {
                    Common.FromLocationID = sharedpreferences.getInt(Email, 0);
                }
                for (int i = 0; i < Common.LocationDeviceList.size(); i++) {
                    if (Common.LocationDeviceList.get(i).getLocationId() == Common.ToLocationID) {
                        Common.LDeviceID = Common.LocationDeviceList.get(i).getLDevId();
                        Common.DeviceName = Common.LocationDeviceList.get(i).getDeviceName();
                    }
                }
            } else {
                for (int i = 0; i < Common.LocationDeviceList.size(); i++) {
                    if (Common.LocationDeviceList.get(i).getIsDefault() == 1) {
                        Common.ToLocationID = Common.LocationDeviceList.get(i).getLocationId();
                        Common.FromLocationID = Common.LocationDeviceList.get(i).getFromLocationId();
                        Common.LDeviceID = Common.LocationDeviceList.get(i).getLDevId();
                        Common.DeviceName = Common.LocationDeviceList.get(i).getDeviceName();
                        SharedPreferenceSave(Common.ToLocationID, Common.FromLocationID);
                    }
                }
            }
            //getToLocationsforSpinner();
        } catch (Exception ex) {
            AlertDialogBox("GetLocationDeviceandIMEI", ex.toString(), false);
        }
    }

    private void getToLocationsforSpinner(Spinner ToLocSpinner) {
        try {
            StringBuilder AdvanceSearchStr = new StringBuilder();
            for (int i = 0; i < Common.LocationDeviceList.size(); i++) {
                if (Common.LocationDeviceList.get(i).getLocationId() == Common.ToLocationID) {
                    deviceNameIEMITXT.setText(Common.LocationDeviceList.get(i).getDeviceName() + "  - IMEI: " + Common.IMEI);// + " " + Common.LocationDeviceList.get(i).getIMEI().toString());
                }
                AdvanceSearchStr.append(Common.LocationDeviceList.get(i).getLocationId() + ",");
            }
            Common.LocationList.clear();
            Common.LocationList = mDBExternalHelper.getToLocationsWithLocID(AdvanceSearchStr.substring(0, AdvanceSearchStr.toString().length() - 1));
            customAdapter = new CustomAdapter(getApplicationContext(), Common.LocationList);
            ToLocSpinner.setAdapter(customAdapter);
            for (int i = 0; i < Common.LocationList.size(); i++) {
                if (Common.LocationList.get(i).getToLocationId() == Common.ToLocationID) {
                    ToLocSpinner.setSelection(i);
                }
            }
            ToLocSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Common.ToLocationID = Common.LocationList.get(position).getToLocationId();
                    Common.ToLocationName = Common.LocationList.get(position).getLocation();
                    for (int i = 0; i < Common.LocationDeviceList.size(); i++) {
                        if (Common.LocationDeviceList.get(i).getLocationId() == Common.ToLocationID) {
                            Common.LDeviceID = Common.LocationDeviceList.get(i).getLDevId();
                            Common.DeviceName = Common.LocationDeviceList.get(i).getDeviceName();
                        }
                    }
                    for (int i = 0; i < Common.LocationDeviceList.size(); i++) {
                        if (Common.LocationDeviceList.get(i).getLocationId() == Common.ToLocationID) {
                            Common.FromLocationID = Common.LocationDeviceList.get(i).getFromLocationId();
                            SharedPreferenceSave(Common.ToLocationID, Common.FromLocationID);
                        }
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception ex) {

        }
    }

    public void SharedPreferenceSave(int ToLoc_ID, int FromLoc_ID) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(Name, ToLoc_ID);
        editor.putInt(Email, FromLoc_ID);
        editor.commit();
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(InventoryFragmentActivity.this, Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public String CommonMessage(int Common_Msg) {
        return InventoryFragmentActivity.this.getResources().getString(Common_Msg);
    }

    public void ExternalDataBaseSync() {
        if (!CheckisInternetPresent()) {
            AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
        } else {
            try {
                Common.AgencyDetailsIndex = Integer.parseInt(mDBExternalHelper.getLastAgencyD());
                Common.ConcessionNamesIndex = Integer.parseInt(mDBExternalHelper.getLastConsessionID());
                Common.DrivedDetailsIndex = Integer.parseInt(mDBExternalHelper.getLastDriverD());
                Common.FellingRegistrationIndex = Integer.parseInt(mDBExternalHelper.getLastFellingRegisterID());
                Common.FellingSectionIndex = Integer.parseInt(mDBExternalHelper.getLastFellingSectionID());
                Common.LocationDeviceIndex = Integer.parseInt(mDBExternalHelper.getLastLocationDeviceID());
                Common.LocationsIndex = Integer.parseInt(mDBExternalHelper.getLastLocationsID());
                Common.TransferLogDetilsIndex = Integer.parseInt(mDBExternalHelper.getLastTransferLogDetailsID());
                Common.TransportModesIndex = Integer.parseInt(mDBExternalHelper.getLastTransportModesID());
                Common.TruckDetailsIndex = Integer.parseInt(mDBExternalHelper.getLastTruckDetailsID());
                Common.WoodSpicesIndex = Integer.parseInt(mDBExternalHelper.getLastWoodSpieceID());
                new GetExternalDataBaseSyncAsynkTask().execute();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    class GetExternalDataBaseSyncAsynkTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.TransferLogDetailsExSync.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String MethodName = "GetMasterDatavalue/";
            String SyncURL = ServiceURL.getServiceURL(ServiceURL.ControllorName, MethodName);
            externalDBmodel = new ExternalDataBaseTableSizesModel();
            externalDBmodel.setAgencyDetailsIndex(Common.AgencyDetailsIndex);
            externalDBmodel.setConcessionNamesIndex(Common.ConcessionNamesIndex);
            externalDBmodel.setDrivedDetailsIndex(Common.DrivedDetailsIndex);
            externalDBmodel.setFellingRegistrationIndex(Common.FellingRegistrationIndex);
            externalDBmodel.setFellingSectionIndex(Common.FellingSectionIndex);
            externalDBmodel.setLocationDeviceIndex(Common.LocationDeviceIndex);
            externalDBmodel.setLocationsIndex(Common.LocationsIndex);
            externalDBmodel.setTransferLogDetilsIndex(Common.TransferLogDetilsIndex);
            externalDBmodel.setTransportModesIndex(Common.TransportModesIndex);
            externalDBmodel.setTruckDetailsIndex(Common.TruckDetailsIndex);
            externalDBmodel.setWoodSpicesIndex(Common.WoodSpicesIndex);
            try {
                String SyncURLInfo = new Communicator().POST_Obect(SyncURL, externalDBmodel);
                if (GwwException.GwwException(Common.HttpResponceCode) == true) {
                    if (SyncURLInfo != null) {
                        JSONObject jsonObj = new JSONObject(SyncURLInfo);
                        String TruckDetStr = jsonObj.getString("TruckDetails");
                        if (TruckDetStr != null) {
                            JSONArray TruckDetJsonAry = new JSONArray(TruckDetStr);
                            for (int TruckSync_Index = 0; TruckSync_Index < TruckDetJsonAry.length(); TruckSync_Index++) {
                                externalTruckDetailsLogs = new Gson().fromJson(TruckDetJsonAry.getString(TruckSync_Index), TruckDetailsModel.class);
                                Common.TruckDetailsExSync.add(externalTruckDetailsLogs);
                            }
                        }
                        String TransLogResStr = jsonObj.getString("TransferLogDetils");
                        if (TransLogResStr != null) {
                            JSONArray TransLogResJsonAry = new JSONArray(TransLogResStr);
                            for (int TransSync_Index = 0; TransSync_Index < TransLogResJsonAry.length(); TransSync_Index++) {
                                externalTransferLogs = new Gson().fromJson(TransLogResJsonAry.getString(TransSync_Index), TransferLogDetailsExModel.class);
                                Common.TransferLogDetailsExSync.add(externalTransferLogs);
                            }
                        }
                        Common.IsConnected = true;
                    } else {
                        Common.IsConnected = false;
                        Common.InventoryErrorMsg = CommonMessage(R.string.NoValueFound);
                    }
                } else {
                    JSONObject jsonObj = new JSONObject(SyncURLInfo);
                    Common.InventoryErrorMsg = jsonObj.getString("Message");
                    Common.IsConnected = false;
                    Common.AuthorizationFlag = true;
                }
            } catch (Exception e) {
                Common.IsConnected = false;
                Common.InventoryErrorMsg = CommonMessage(R.string.NoValueFound);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Common.IsConnected == true) {
                        InsertValuesInExternalDataBase();
                    } else {
                        AlertDialogBox(CommonMessage(R.string.ExtenalDBHead), "Not Updates", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }
    }

    public void InsertValuesInExternalDataBase() {
        try {
            for (TruckDetailsModel TruckSync : Common.TruckDetailsExSync) {
                mDBExternalHelper.insertTruckDetails(
                        TruckSync.getRowID(),
                        TruckSync.getTransportId(),
                        TruckSync.getTruckLicensePlateNo(),
                        TruckSync.getDescription()
                );
            }
            for (TransferLogDetailsExModel TrsnfsSync : Common.TransferLogDetailsExSync) {
                mDBExternalHelper.insertTransferLogDetails(
                        TrsnfsSync.getRowId(),
                        TrsnfsSync.getLocationId(),
                        TrsnfsSync.getLocationName(),
                        TrsnfsSync.getPlotNo(),
                        TrsnfsSync.getFellingSectionNumber(),
                        TrsnfsSync.getHarvestCropsId(),
                        TrsnfsSync.getInStockId(),
                        TrsnfsSync.getTreeNumber(),
                        TrsnfsSync.getWoodSpeciesCode(),
                        TrsnfsSync.getSBBLabel(),
                        TrsnfsSync.getF1(),
                        TrsnfsSync.getF2(),
                        TrsnfsSync.getT1(),
                        TrsnfsSync.getT2(),
                        TrsnfsSync.getLength_dm(),
                        TrsnfsSync.getVolume(),
                        TrsnfsSync.getWoodSpeciesId(),
                        TrsnfsSync.getFellingSectionId(),
                        TrsnfsSync.getLogQuality(),
                        TrsnfsSync.getLogStatus()
                );
            }
            ExportDatabaseToStorage();
        } catch (Exception ex) {
            Log.d("AgencyCount", "2.1>>>>>" + ex.toString());
        }
    }

    public void ExportDatabaseToStorage() {
        try {
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GWW";
            File sd = new File(dir);
            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + getPackageName() + "/databases/GWW.db";
                String backupDBPath = "GWW.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
/*
        try {
            File download_folder = Environment.getExternalStorageDirectory().getAbsolutePath();//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File data = Environment.getDataDirectory();

            if (download_folder.canWrite()) {
                String currentDBPath = "//data//" + getPackageName() + "//databases//" + "YourWords";
                String backupDBPath = "YourWords";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(download_folder, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }*/
    }

    public boolean CheckisInternetPresent() {
        try {
            isInternetPresent = ConnectionFinder.isInternetOn(getBaseContext());
        } catch (Exception ex) {
            //AlertDialogBox("Internet Connection", ex.toString(), false);
        }
        if (!isInternetPresent) {
            AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            return false;
        } else {
            return true;
        }
    }
}

