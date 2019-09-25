package com.zebra.main.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.zebra.android.jb.Preference;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tscdll.TSCActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zebra.R;
import com.zebra.adc.decoder.BarCodeReader;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.adapter.ArrivalAdapter;
import com.zebra.main.adapter.CustomAdapter;
import com.zebra.main.adapter.FellingSectionAdapter;
import com.zebra.main.interfac.FellingFilterInterface;
import com.zebra.main.model.ExternalDB.AgencyDetailsModel;
import com.zebra.main.model.ExternalDB.ConcessionNamesModel;
import com.zebra.main.model.ExternalDB.DriverDetailsModel;
import com.zebra.main.model.ExternalDB.FellingRegisterModel;
import com.zebra.main.model.ExternalDB.FellingSectionModel;
import com.zebra.main.model.ExternalDB.LocationsModel;
import com.zebra.main.model.ExternalDB.ExternalDataBaseTableSizesModel;
import com.zebra.main.model.ExternalDB.TransferLogDetailsExModel;
import com.zebra.main.model.ExternalDB.TransferLogDetailsModel;
import com.zebra.main.model.ExternalDB.TruckDetailsModel;
import com.zebra.main.model.FellingRegistration.FellingRegisterResultModel;
import com.zebra.main.model.SyncStatusModel;
import com.zebra.main.model.FellingRegistration.FellingRegisterListModel;
import com.zebra.main.model.FellingRegistration.FellingRegistrationSyncModel;
import com.zebra.main.model.InvCount.InventoryCountInputListModel;
import com.zebra.main.model.InvCount.InventoryCountModel;
import com.zebra.main.model.InvCount.InventoryCountSyncModel;
import com.zebra.main.model.InvReceived.InventoryReceivedListModel;
import com.zebra.main.model.InvReceived.InventoryReceivedModel;
import com.zebra.main.model.InvReceived.InventoryReceivedSyncModel;
import com.zebra.main.model.InvTransfer.InventoryTransferModel;
import com.zebra.main.model.InvTransfer.InventoryTransferScannedResultModel;
import com.zebra.main.model.InvTransfer.InventoryTransferSyncModel;
import com.zebra.main.sdl.SdlScanListener;
import com.zebra.main.sdl.SdlScanService;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.BlueTooth;
import com.zebra.utilities.BlutoothCommonClass;
import com.zebra.utilities.Common;
import com.zebra.utilities.Communicator;
import com.zebra.utilities.ConnectionFinder;
import com.zebra.utilities.DeviceIMEIClass;
import com.zebra.utilities.GwwException;
import com.zebra.utilities.PrintSlipsClass;
import com.zebra.utilities.ServiceURL;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class InventoryActivity extends AppCompatActivity implements SdlScanListener, FellingFilterInterface {
    private static final String TAG = "Inventory";
    Button inventoryCountTXT, inventoryTransferTXT, inventoryReceivedTXT, submitVBBTXT, submitTransBTN, exportExcelTXT, fellingRegistrationButton;
    TextView deviceNameIEMITXT, NoValueFoundTxT, TotalFilteredCount, TotalFilteredVolume;
    LinearLayout inventoryCountLAY, inventorytransferLAY, inventoryReceivedLAY, ProgressBarLay, fellingRegisterLayout, FellingTOTLayout;
    EditText submitVVPEDT, submitOldTransEDT;
    ImageView CountcreateScanListTXT, ReceivedCreateScanListTXT, fellingRegScanListTxT, OldBarCodeScanIMG, FellSecExportTOExcel;
    RecyclerView InventoryCountList, InventoryTransList, InventoryReceivedList, FellingRegListView;
    AutoCompleteTextView FellingSectionSearchATV;
    CheckBox SyncAllCheckBox;
    private static HSSFWorkbook myWorkBook = new HSSFWorkbook();
    private static HSSFSheet mySheet = myWorkBook.createSheet();
    private InternalDataBaseHelperClass mDBInternalHelper;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    InventoryCountAdapter invenCountadapter;
    InventoryTransferAdapter invenTransadapter;
    InventorReceivedAdapter invenRecievadapter;
    FellingRegistrationAdapter fellingRegadapter;
    ArrayAdapter<String> StringtreeNoadapter;
    ExternalDataBaseTableSizesModel externalDBmodel;
    CustomAdapter customAdapter;
    LinearLayoutManager horizontalLayoutManager;
    InventoryCountSyncModel countSyncModel;
    AlertDialogManager alert = new AlertDialogManager();
    boolean isInternetPresent;
    SyncStatusModel suncStaModel;
    AgencyDetailsModel externalagencyModel;
    DriverDetailsModel externalDrivertModel;
    TransferLogDetailsExModel externalTransferLogs;
    TruckDetailsModel externalTruckDetailsLogs;
    FellingRegisterModel externalfellingRegisterDetails;
    FellingSectionModel externalfellingSectionDetails;
    InventoryTransferSyncModel transferSyncModel;
    InventoryReceivedSyncModel receiedSyncModel;
    FellingRegistrationSyncModel fellingSyncModel;
    TSCActivity tsc = new TSCActivity();
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    ArrivalAdapter arrivalAdapter;
    Spinner FromLocationSpin;//ToLocationSpin;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";

    AlertDialog.Builder Signoutbuilder = null;
    AlertDialog SignoutAlert = null;
    PrintSlipsClass printSlip;
    DeviceIMEIClass device_imei;
    // BarCodeReader specifics
    private BarCodeReader bcr = null;
    private PowerManager.WakeLock mWakeLock = null;
    private int motionEvents = 0, modechgEvents = 0, snapNum = 0;
    boolean bind = false;
    private SdlScanService scanService;
    MediaPlayer beepsound, wronBuzzer;
    Intent service;
    private ImageView image = null;
    static final private boolean saveSnapshot = false;
    String directory_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BackUp/";
    SQLiteToExcel sqliteToExcel;
    ArrayList<FellingRegisterListModel> FellingRegisterFilteredList = new ArrayList<FellingRegisterListModel>();

    //DatabaseHelper dbBackend;
    @Override
    public void onBackPressed() {
        SignoutforLogin(InventoryActivity.this.getResources().getString(R.string.SignoutMsg));
    }

    @Override
    protected void onStart() {
        super.onStart();
        beepsound = MediaPlayer.create(this, R.raw.beep);
        wronBuzzer = MediaPlayer.create(this, R.raw.wrong_buzzer);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            //UpdateTransferIDList();
            Log.v(TAG, "onStop");
        } catch (Exception Ex) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            //UpdateTransferIDList();
            Log.v(TAG, "onPause");
            //scanService.release();
            if (scanService != null)
                scanService.setActivityUp(false);
            releaseWakeLock();
        } catch (Exception Ex) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (bind) {
                if (!Preference.getScanSelfopenSupport(this, true)) {
                    this.stopService(service);
                }
                unbindService(serviceConnection);
                scanService = null;
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bind = false;
            Log.v(TAG, "SDLActivity onServiceDisconnected " + bind);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bind = true;
            SdlScanService.MyBinder myBinder = (SdlScanService.MyBinder) service;
            scanService = myBinder.getService();
            //
            scanService.setOnScanListener(InventoryActivity.this);
            scanService.setActivityUp(true);
            Log.v(TAG, "SDLActivity onServiceConnected " + bind);
        }
    };

    private synchronized static void writeFile(File file, String value) {

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(value.getBytes());
            outputStream.flush();
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Called when the activity is about to start interacting with the user.
    @Override
    protected void onResume() {
        Log.v(TAG, "SDLActivity onResume bind: " + bind);
        try {
            service = new Intent(this, SdlScanService.class);
            bindService(service, serviceConnection, BIND_AUTO_CREATE);
            startService(service);
        } catch (Exception e) {
            //Log.e("InventoryTransferActivity","Exceptionss: "+e+" mBeepManagersdl: "+mBeepManagersdl);
        }
        try {
            ScannedStatus("");
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (scanService != null)
            scanService.setActivityUp(true);
        acquireWakeLock();
        InventoryCount_Transfer(Common.InventoryPageID);
        device_imei = new DeviceIMEIClass(InventoryActivity.this);
        Common.IMEI = device_imei.GetDeviceIMEI();
        if (isNullOrEmpty(Common.IMEI)) {
            AlertDialogBox("Device IMEI", "Device IMEI not found, please contact adminstrator ", false);
            return;
        }
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        mDBExternalHelper = new ExternalDataBaseHelperClass(InventoryActivity.this);
        mDBInternalHelper = new InternalDataBaseHelperClass(InventoryActivity.this);
        printSlip = new PrintSlipsClass(InventoryActivity.this);
        device_imei = new DeviceIMEIClass(InventoryActivity.this);
        Common.IMEI = device_imei.GetDeviceIMEI();
        if (isNullOrEmpty(Common.IMEI)) {
            AlertDialogBox("Device IMEI", "Device IMEI not found, please contact adminstrator ", false);
            return;
        }
        Initialization();
        /*if (Common.VersionCode == 4) {
            UpdatedOldValuesinTable();
        }*/
        InventoryCount_Transfer(Common.InventoryPageID);
        Common.QulaityDefaultList.clear();
        Common.QulaityDefaultList.add("A");
        Common.QulaityDefaultList.add("B");
        Common.QulaityDefaultList.add("C");
        Common.QulaityDefaultList.add("U");
        ExternetDBtoInternalDB();
        GetLocationDeviceandIMEI();
        getTransferLocationDetials();
        getfellingSectionSpinner();

        FromLocationSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Common.FromLocationID = Common.ConcessionList.get(position).getFromLocationId();
                Common.FromLocationname = Common.ConcessionList.get(position).getConcessionName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        FellingSectionSearchATV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.e("CharSequence", ">>>>>>>>>>>" + s.toString())
                CharSequence FellingID = "";
                if (s.toString().length() > 0) {
                    for (FellingSectionModel fellingSecMod : Common.FellingSectionList) {
                        if (Integer.parseInt(s.toString()) == fellingSecMod.getFellingSectionNumber()) {
                            FellingID = fellingSecMod.getFellingSectionId();
                        }
                    }
                    if (fellingRegadapter != null) {
                        HideKeyboard();
                        fellingRegadapter.getFilter().filter(FellingID);
                    }
                } else {
                    GetFellingRegistrationList();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        FellingSectionSearchATV.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    FellingSectionSearchATV.showDropDown();
                    FellingSectionSearchATV.requestFocus();
                } catch (Exception ex) {

                }
                return false;
            }
        });
        FellingSectionSearchATV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.e("Item", ">>>>>>>>>>" + Common.FelllingSectionIDsStringList[position]);
                CharSequence FellingID = "";
                Object item = parent.getItemAtPosition(position);
                if (item.toString().length() > 0) {
                    for (FellingSectionModel fellingSecMod : Common.FellingSectionList) {
                        if (Integer.parseInt(item.toString()) == fellingSecMod.getFellingSectionNumber()) {
                            //if (Common.FelllingSectionIDsStringList[position].equals(fellingSecMod.getFellingSectionNumber())) {
                            FellingID = fellingSecMod.getFellingSectionId();
                        }
                    }
                    if (fellingRegadapter != null) {
                        HideKeyboard();
                        fellingRegadapter.getFilter().filter(FellingID);
                    }
                } else {
                    GetFellingRegistrationList();
                }
            }
        });
        SyncAllCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SyncAllCheckBox.isChecked()) {
                    Common.FellingRegSyncALL = true;
                } else {
                    Common.FellingRegSyncALL = false;
                }
            }
        });

        SyncAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (SyncAllCheckBox.isChecked()) {
                    Common.FellingRegSyncALL = true;
                } else {
                    Common.FellingRegSyncALL = false;
                }
            }
        });
        /*ToLocationSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });*/
        //DeleteTransferListIFzeroCount();
        //DeleteCountListIFzero();
        //DeleteReceivedListIFzero();
    }

    private void getfellingSectionSpinner() {
        try {
            Common.FellingSectionList.clear();
            Common.FellingSectionList = mDBExternalHelper.getFellingSectionID(Common.FromLocationID);
            Common.FelllingSectionIDsStringList = new String[Common.FellingSectionList.size()];
            for (int i = 0; i < Common.FellingSectionList.size(); i++) {
                Common.FelllingSectionIDsStringList[i] = String.valueOf(Common.FellingSectionList.get(i).getFellingSectionNumber());
            }
            StringtreeNoadapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, Common.FelllingSectionIDsStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            FellingSectionSearchATV.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
        }
    }

/*    public void Signout(String ErrorMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(InventoryActivity.this);
        builder1.setMessage(ErrorMessage);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton("Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }*/

    public void SharedPreferenceSave(int ToLoc_ID, int FromLoc_ID) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(Name, ToLoc_ID);
        editor.putInt(Email, FromLoc_ID);
        editor.commit();
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

    public void ExternetDBtoInternalDB() {
        ImportDataBaseFromInternalStorage();
        //RefreshActivity();
    }

    public void Initialization() {
        deviceNameIEMITXT = findViewById(R.id.deviceIEMITxT);
        exportExcelTXT = findViewById(R.id.exportExcelTxT);
        findViewById(R.id.exportExcelTxT).setOnClickListener(mExportExcelListen);
        findViewById(R.id.refreshExternalDataBase).setOnClickListener(mRefreshListener);
        ProgressBarLay = findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        NoValueFoundTxT = findViewById(R.id.NovalueFound);
        // Inventory Count
        inventoryCountTXT = findViewById(R.id.inventoryCountTxT);
        inventoryCountLAY = findViewById(R.id.inventorycountLAY);
        CountcreateScanListTXT = findViewById(R.id.inventoryCountcreateListTxT);
        InventoryCountList = findViewById(R.id.inventoryCountListView);
        findViewById(R.id.inventoryCountTxT).setOnClickListener(mInvenCountListen);
        findViewById(R.id.inventoryCountcreateListTxT).setOnClickListener(mCountCreateScanListen);
        FromLocationSpin = findViewById(R.id.fromlocaton_sppinner);
        // Inventory Transfer
        inventoryTransferTXT = findViewById(R.id.inventoryTranserTxT);
        inventorytransferLAY = findViewById(R.id.inventorytransferLAY);
        submitVBBTXT = findViewById(R.id.submitVBBTxT);
        submitVVPEDT = findViewById(R.id.submitVVPEdT);
        submitTransBTN = findViewById(R.id.submitTransBtn);
        OldBarCodeScanIMG = findViewById(R.id.OldTrans_scanQROCde);
        submitOldTransEDT = findViewById(R.id.submitTrabsferEDT);
        InventoryTransList = findViewById(R.id.inventoryTransListView);
        findViewById(R.id.inventoryTranserTxT).setOnClickListener(mInvenTransferListen);
        findViewById(R.id.submitVBBTxT).setOnClickListener(mSubmitVVPListen);
        findViewById(R.id.submitTransBtn).setOnClickListener(mSubmitOldTransferListen);
        findViewById(R.id.OldTrans_scanQROCde).setOnClickListener(mOldBarCodeScanListen);

        //ToLocationSpin = findViewById(R.id.inventoryCountToLocation_spinner);
        // Inventory Received
        inventoryReceivedTXT = findViewById(R.id.inventoryReceivedTxT);
        inventoryReceivedLAY = findViewById(R.id.inventoryReceivedLAY);
        InventoryReceivedList = findViewById(R.id.inventoryReceivedListView);
        findViewById(R.id.inventoryReceivedTxT).setOnClickListener(mInvenReceivedListen);
        ReceivedCreateScanListTXT = findViewById(R.id.receivedCreateScanListTxT);
        findViewById(R.id.receivedCreateScanListTxT).setOnClickListener(mReceivedCreateScanListen);

        // Felling Registration
        fellingRegistrationButton = findViewById(R.id.fellingRegistrationTxT);
        findViewById(R.id.fellingRegistrationTxT).setOnClickListener(mFellingRegisterListen);
        fellingRegScanListTxT = findViewById(R.id.fellingRegScanListTxT);
        findViewById(R.id.fellingRegScanListTxT).setOnClickListener(mFellingRegisterCreateScanListen);
        fellingRegisterLayout = findViewById(R.id.fellingRegistrationLAY);
        FellingRegListView = findViewById(R.id.fellingReg_RecyclerListView);
        FellingSectionSearchATV = findViewById(R.id.fellingFilter_ATxT);
        TotalFilteredCount = findViewById(R.id.TotalFilteredCount);
        TotalFilteredVolume = findViewById(R.id.TotalFilteredVolume);
        FellingTOTLayout = findViewById(R.id.FellingTotalLayout);
        findViewById(R.id.FellSecExportTOExcel).setOnClickListener(mFellSecExportTOExcel);
        SyncAllCheckBox = findViewById(R.id.SyncAll_checkBox);
    }

    View.OnClickListener mRefreshListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Import External DataBase
            /*Common.TransportAgencyList.clear();
            Common.TransportAgencyList = mDBExternalHelper.getAllAgencyDetails();
            Log.d("AgencyCount", "1>>>>>" + Common.TransportAgencyList.size());
            ExternalDataBasSync();*/
            ExternetDBtoInternalDB();
        }
    };

    View.OnClickListener mFellingRegisterListen = new View.OnClickListener() {
        public void onClick(View v) {
            Common.InventoryPageID = 1;
            InventoryCount_Transfer(Common.InventoryPageID);
        }
    };

    View.OnClickListener mInvenCountListen = new View.OnClickListener() {
        public void onClick(View v) {
            Common.InventoryPageID = 2;
            InventoryCount_Transfer(Common.InventoryPageID);
        }
    };

    View.OnClickListener mInvenTransferListen = new View.OnClickListener() {
        public void onClick(View v) {
            Common.InventoryPageID = 3;
            InventoryCount_Transfer(Common.InventoryPageID);
        }
    };

    View.OnClickListener mInvenReceivedListen = new View.OnClickListener() {
        public void onClick(View v) {
            Common.InventoryPageID = 4;
            InventoryCount_Transfer(Common.InventoryPageID);
        }
    };

    View.OnClickListener mCountCreateScanListen = new View.OnClickListener() {
        public void onClick(View v) {
            Common.SyncTime = "";
            Common.SyncStatus = 0;
            Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            boolean ListIdFlag = mDBInternalHelper.insertInventoryCountListID(Common.IMEI, Common.ToLocationID, Common.StartDate, Common.EndDate, 0, Common.SyncStatus,
                    Common.SyncTime, 1, Common.CountUniqueID);
            if (ListIdFlag == true) {
                Common.InventoryCountList = mDBInternalHelper.getInventoryCountIdList();
                if (Common.InventoryCountList.size() > 0) {
                    Common.ListID = Integer.parseInt(mDBInternalHelper.getLastCountID());
                    String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                    String DeviceID = "";
                    if (String.valueOf(Common.LDeviceID).length() == 1) {
                        DeviceID = "0" + String.valueOf(Common.LDeviceID);
                    } else {
                        DeviceID = String.valueOf(Common.LDeviceID);
                    }
                    Common.CountUniqueID = String.valueOf(DateUniqueFormat + DeviceID + Common.ListID);
                    Log.d("CountUniqueID", ">>>>>>" + Common.ListID + ">>" + Common.CountUniqueID);
                    boolean CountIDFlag = mDBInternalHelper.UpdateInventoryCountUniqueID(Common.ListID, Common.CountUniqueID);
                    InventorCountAcivityCall();
                }
            }
        }
    };

    View.OnClickListener mSubmitOldTransferListen = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                if (OldTransferIDValidation(submitOldTransEDT.getText().toString())) {
                    Common.SyncTime = "";
                    Common.VolumeSum = 0.0;
                    Common.TransferUniqueID = submitOldTransEDT.getText().toString();
                    Common.VBB_Number = submitVVPEDT.getText().toString();
                    Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                    if (Common.VBB_Number.length() > 0) {
                        boolean DuplicateFlag = mDBInternalHelper.getInventoryTransferIdListDuplicateCheck(Integer.parseInt(Common.VBB_Number));
                        if (DuplicateFlag == true) {
                            AlertDialogBox("Inventory Transfer List", "please add diff VBB Number", false);
                            return;
                        }
                    }
                    boolean ListIdFlag = mDBInternalHelper.insertInventoryTransferID(Common.VBB_Number, Common.IMEI, Common.ToLocaTransID, Common.StartDate, Common.EndDate,
                            Common.FromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TrucklicensePlateNo, Common.UserID, Common.Count,
                            Common.SyncStatus, Common.SyncTime, Common.Volume, 1, Common.TransferUniqueID);

                    if (ListIdFlag == true) {
                        Common.InventoryTransferList = mDBInternalHelper.getInventoryTransferIdList();
                        if (Common.InventoryTransferList.size() > 0) {
                            Common.TransferID = Integer.parseInt(mDBInternalHelper.getLastTransferID());
                            // Update values into TransferID
                            boolean TransferIDFlag = mDBInternalHelper.UpdateInventoryTransferUniqueID(Common.TransferID, Common.TransferUniqueID);
                        }
                    }
                    Common.IsTransferEditListFlag = true;
                    Common.IsEditorViewFlag = true;
                    InventorTransferAcivityCall();
                }
            } catch (Exception ex) {
                AlertDialogBox("IA-AddTransferList", ex.toString(), false);
            }
        }
    };

    View.OnClickListener mOldBarCodeScanListen = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                scanService.doDecode();
            } catch (Exception ex) {
                AlertDialogBox("IA-AddTransferList", ex.toString(), false);
            }
        }
    };

    View.OnClickListener mSubmitVVPListen = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Common.SyncTime = "";
                Common.VolumeSum = 0.0;
                Common.VBB_Number = submitVVPEDT.getText().toString();
                Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                if (Common.VBB_Number.length() > 0) {
                    boolean DuplicateFlag = mDBInternalHelper.getInventoryTransferIdListDuplicateCheck(Integer.parseInt(Common.VBB_Number));
                    if (DuplicateFlag == true) {
                        AlertDialogBox("Inventory Transfer List", "please add diff VBB Number", false);
                        return;
                    }
                }
                boolean ListIdFlag = mDBInternalHelper.insertInventoryTransferID(Common.VBB_Number, Common.IMEI, Common.ToLocaTransID, Common.StartDate, Common.EndDate,
                        Common.FromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TrucklicensePlateNo, Common.UserID, Common.Count,
                        Common.SyncStatus, Common.SyncTime, Common.Volume, 1, Common.TransferUniqueID);
                if (ListIdFlag == true) {
                    Common.InventoryTransferList = mDBInternalHelper.getInventoryTransferIdList();
                    if (Common.InventoryTransferList.size() > 0) {
                        Common.TransferID = Integer.parseInt(mDBInternalHelper.getLastTransferID());
                        String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                        String DeviceID = "";
                        if (String.valueOf(Common.LDeviceID).length() == 1) {
                            DeviceID = "0" + String.valueOf(Common.LDeviceID);
                        } else {
                            DeviceID = String.valueOf(Common.LDeviceID);
                        }
                        Common.TransferUniqueID = String.valueOf(DateUniqueFormat + DeviceID + Common.TransferID);
                        Log.d("TransferID", ">>>>>>" + Common.TransferID + ">>" + Common.TransferUniqueID);
                        // Update values into TransferID
                        boolean TransferIDFlag = mDBInternalHelper.UpdateInventoryTransferUniqueID(Common.TransferID, Common.TransferUniqueID);
                    }
                }
                Common.IsTransferEditListFlag = true;
                Common.IsEditorViewFlag = true;
                InventorTransferAcivityCall();
            } catch (Exception ex) {
                AlertDialogBox("IA-AddTransferList", ex.toString(), false);
            }
        }
    };

    View.OnClickListener mReceivedCreateScanListen = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Common.SyncTime = "";
                Common.VolumeSum = 0.0;
                Common.VBB_Number = "";
                Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                Common.ToLocReceivedID = Common.ToLocationID;
                boolean ListIdFlag = mDBInternalHelper.insertInventoryReceivedIDList(Common.VBB_Number, Common.IMEI, Common.ToLocReceivedID, Common.StartDate, Common.EndDate,
                        Common.FromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TrucklicensePlateNo, Common.UserID, Common.Count,
                        Common.SyncStatus, Common.SyncTime, Common.Volume, 1, Common.ReceivedUniqueID);
                if (ListIdFlag == true) {
                    Common.InventoryReceivedList = mDBInternalHelper.getInventoryReceivedIdList();
                    if (Common.InventoryReceivedList.size() > 0) {
                        Common.ReceivedID = Integer.parseInt(mDBInternalHelper.getLastReceivedID());
                        String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                        String DeviceID = "";
                        if (String.valueOf(Common.LDeviceID).length() == 1) {
                            DeviceID = "0" + String.valueOf(Common.LDeviceID);
                        } else {
                            DeviceID = String.valueOf(Common.LDeviceID);
                        }
                        Common.ReceivedUniqueID = String.valueOf(DateUniqueFormat + DeviceID + Common.ReceivedID);
                        boolean ReceivedIDFlag = mDBInternalHelper.UpdateInventoryReceivedUniqueID(Common.ReceivedID, Common.ReceivedUniqueID);
                    }
                    Common.TransferIDsList.clear();
                    Common.IsReceivedEditListFlag = true;
                    Common.IsEditorViewFlag = true;
                    InventorReceivedAcivityCall();
                } else {
                    AlertDialogBox("Add Inventory Received List", "Value not Inserted", false);
                }
            } catch (
                    Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    View.OnClickListener mFellSecExportTOExcel = new View.OnClickListener() {
        public void onClick(View v) {
            ExportFellingListTable();
        }
    };

    View.OnClickListener mFellingRegisterCreateScanListen = new View.OnClickListener() {
        public void onClick(View v) {
            Common.SyncTime = "";
            Common.SyncStatus = 0;
            Common.FellingRegDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            boolean ListIdFlag = mDBInternalHelper.insertFellingRegisterList(Common.FellingRegNo, Common.FellingRegDate, Common.FellingSectionId, Common.FromLocationID, Common.EndDate,
                    0, Common.FellingRegUniqueID, Common.SyncStatus, Common.SyncTime, 1, Common.UserID, Common.IMEI);
            if (ListIdFlag == true) {
                //Common.FellingRegisterList = mDBInternalHelper.getFellingRegisterList();
                Common.ConcessionList.clear();
                Common.ConcessionList = mDBExternalHelper.getConcessionList();
                for (ConcessionNamesModel locationMod : Common.ConcessionList) {
                    if (Common.FromLocationID == locationMod.getFromLocationId()) {
                        Common.FromLocationname = locationMod.getConcessionName();
                    }
                }
                if (Common.FellingRegisterList.size() > 0) {
                    Common.FellingRegID = Integer.parseInt(mDBInternalHelper.getLastFellingRegD());
                    /*Add Felling Reg Unique ID*/
                    String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                    String DeviceID = "";
                    if (String.valueOf(Common.LDeviceID).length() == 1) {
                        DeviceID = "0" + String.valueOf(Common.LDeviceID);
                    } else {
                        DeviceID = String.valueOf(Common.LDeviceID);
                    }
                    Common.FellingRegUniqueID = String.valueOf(DateUniqueFormat + DeviceID + Common.FellingRegID);
                    boolean FellingIDFlag = mDBInternalHelper.UpdateFellingUniqueID(Common.FellingRegID, Common.FellingRegUniqueID);
                }
            }
            Common.IsFellingRegEditListFlag = true;
            Common.IsEditorViewFlag = true;
            InventorFellingRegAcivityCall();
        }
    };

    View.OnClickListener mExportExcelListen = new View.OnClickListener() {
        public void onClick(View v) {
            //ExportExcelFileFromDeviceDB();
            ExportAllTables();
        }
    };

    public boolean OldTransferIDValidation(String TransferUniqueID) {
        boolean Validattion = true;
        if (TransferUniqueID.equals("")) {
            AlertDialogBox(CommonMessage(R.string.OldTransferHead), CommonMessage(R.string.OldTransferMsg), false);
            submitOldTransEDT.requestFocus();
            Validattion = false;
        }
        if (TransferUniqueID.length() < 9) {
            AlertDialogBox(CommonMessage(R.string.OldTransferHead), CommonMessage(R.string.OldTransferIDLenghtMsg), false);
            submitOldTransEDT.requestFocus();
            Validattion = false;
        }
        boolean DuplicateFlag = mDBInternalHelper.getInventoryTransferUniqueIdDuplicateCheck(TransferUniqueID);
        if (DuplicateFlag == true) {
            AlertDialogBox("Inventory Transfer List", "This transfer id already exists", false);
            Validattion = false;
        }
        return Validattion;
    }

    public void InventoryCount_Transfer(int Count_TranferFLAG) {
        FellingSectionSearchATV.setText("");
        //HideKeyboard();
        if (Count_TranferFLAG == 1) {
            fellingRegisterLayout.setVisibility(View.VISIBLE);
            inventoryCountLAY.setVisibility(View.GONE);
            inventorytransferLAY.setVisibility(View.GONE);
            inventoryReceivedLAY.setVisibility(View.GONE);

            fellingRegistrationButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            inventoryCountTXT.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
            inventoryTransferTXT.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
            inventoryReceivedTXT.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));

            fellingRegistrationButton.setTypeface(null, Typeface.BOLD);
            inventoryCountTXT.setTypeface(null, Typeface.NORMAL);
            inventoryTransferTXT.setTypeface(null, Typeface.NORMAL);
            inventoryReceivedTXT.setTypeface(null, Typeface.NORMAL);
            FellingTOTLayout.setVisibility(View.VISIBLE);
            GetFellingRegistrationList();
        } else if (Count_TranferFLAG == 2) {
            inventoryCountLAY.setVisibility(View.VISIBLE);
            inventorytransferLAY.setVisibility(View.GONE);
            inventoryReceivedLAY.setVisibility(View.GONE);
            fellingRegisterLayout.setVisibility(View.GONE);

            inventoryCountTXT.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            inventoryTransferTXT.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
            inventoryReceivedTXT.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
            fellingRegistrationButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));

            inventoryCountTXT.setTypeface(null, Typeface.BOLD);
            inventoryTransferTXT.setTypeface(null, Typeface.NORMAL);
            inventoryReceivedTXT.setTypeface(null, Typeface.NORMAL);
            fellingRegistrationButton.setTypeface(null, Typeface.NORMAL);
            FellingTOTLayout.setVisibility(View.GONE);
            GetInventoryCountList();
        } else if (Count_TranferFLAG == 3) {
            inventoryCountLAY.setVisibility(View.GONE);
            inventorytransferLAY.setVisibility(View.VISIBLE);
            inventoryReceivedLAY.setVisibility(View.GONE);
            fellingRegisterLayout.setVisibility(View.GONE);

            inventoryCountTXT.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
            inventoryTransferTXT.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            inventoryReceivedTXT.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
            fellingRegistrationButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));

            inventoryCountTXT.setTypeface(null, Typeface.NORMAL);
            inventoryTransferTXT.setTypeface(null, Typeface.BOLD);
            inventoryReceivedTXT.setTypeface(null, Typeface.NORMAL);
            fellingRegistrationButton.setTypeface(null, Typeface.NORMAL);
            FellingTOTLayout.setVisibility(View.GONE);
            GetInventoryTransferList();
        } else if (Count_TranferFLAG == 4) {
            inventoryCountLAY.setVisibility(View.GONE);
            inventorytransferLAY.setVisibility(View.GONE);
            inventoryReceivedLAY.setVisibility(View.VISIBLE);
            fellingRegisterLayout.setVisibility(View.GONE);

            inventoryCountTXT.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
            inventoryTransferTXT.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
            inventoryReceivedTXT.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            fellingRegistrationButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));

            inventoryCountTXT.setTypeface(null, Typeface.NORMAL);
            inventoryTransferTXT.setTypeface(null, Typeface.NORMAL);
            inventoryReceivedTXT.setTypeface(null, Typeface.BOLD);
            fellingRegistrationButton.setTypeface(null, Typeface.NORMAL);
            FellingTOTLayout.setVisibility(View.GONE);
            GetInventoryReceivedList();
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
                    getfellingSectionSpinner();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception ex) {

        }
    }

    /* private void getFromLocationsforSpinner() {
         try {
             Common.ConcessionList.clear();
             Common.ConcessionList = mDBExternalHelper.getAllConcessionNames();
             Common.FromLocationID = Common.ConcessionList.get(0).getFromLocationId();
             arrivalAdapter = new ArrivalAdapter(getApplicationContext(), Common.ConcessionList);
             FromLocationSpin.setAdapter(arrivalAdapter);
         } catch (Exception ex) {
         }
     }
 */
    public boolean ToLocationValidation(int ToLocaID) {
        boolean Validattion = true;
        if (ToLocaID == -1) {
            Validattion = false;
            AlertDialogBox("From Location", "please select one item from List", false);
        }
        return Validattion;
    }

    public boolean FromLocationValidation(int FromLoc_ID) {
        boolean Validattion = true;
        if (FromLoc_ID == -1) {
            Validattion = false;
            AlertDialogBox("From Location", "please select one item from List", false);
        }
        return Validattion;
    }

    public void GetFellingRegistrationList() {
        try {
            Common.FellingRegisterList.clear();
            FellingRegisterFilteredList.clear();
            Common.FellingRegisterList = mDBInternalHelper.getFellingRegisterList();
            FellingRegisterFilteredList.addAll(Common.FellingRegisterList);
            if (Common.FellingRegisterList.size() > 0) {
                fellingRegadapter = new FellingRegistrationAdapter(Common.FellingRegisterList, InventoryActivity.this, InventoryActivity.this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                fellingRegadapter.notifyDataSetChanged();
                FellingRegListView.setLayoutManager(horizontalLayoutManager);
                FellingRegListView.setAdapter(fellingRegadapter);
                NoValueFoundTxT.setVisibility(View.GONE);
                FellingRegListView.setVisibility(View.VISIBLE);
            } else {
                NoValueFoundTxT.setVisibility(View.VISIBLE);
                FellingRegListView.setVisibility(View.GONE);
            }
            TotalFilteredCount.setText(String.valueOf(Common.FellingRegisterList.size()));
            TotalFilteredVolume.setText(String.valueOf(mDBInternalHelper.TotalFilteredVolume("")));
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
    }

    private void GetInventoryCountList() {
        try {
            Common.InventoryCountList.clear();
            Common.InventoryCountList = mDBInternalHelper.getInventoryCountIdList();
            if (Common.InventoryCountList.size() > 0) {
                invenCountadapter = new InventoryCountAdapter(Common.InventoryCountList, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                invenCountadapter.notifyDataSetChanged();
                InventoryCountList.setLayoutManager(horizontalLayoutManager);
                InventoryCountList.setAdapter(invenCountadapter);
                NoValueFoundTxT.setVisibility(View.GONE);
                InventoryCountList.setVisibility(View.VISIBLE);
            } else {
                NoValueFoundTxT.setVisibility(View.VISIBLE);
                InventoryCountList.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
    }

    public void UpdatedOldValuesinTable() {
        try {
            Common.InventoryTransferList = mDBInternalHelper.getInventoryTransferIdList();
            for (InventoryTransferModel invenTransMode : Common.InventoryTransferList) {
                if (isNullOrEmpty(invenTransMode.getTransUniqueID())) {
                    Date date = Common.dateFormat.parse(invenTransMode.getStartDateTime());
                    String StartDateFormat = Common.UniqueIDdateFormat.format(date);
                    String DeviceID = "";
                    if (String.valueOf(Common.LDeviceID).length() == 1) {
                        DeviceID = "0" + String.valueOf(Common.LDeviceID);
                    } else {
                        DeviceID = String.valueOf(Common.LDeviceID);
                    }
                    String transferUniqueCreation = StartDateFormat + DeviceID + invenTransMode.getTransferID();
                    mDBInternalHelper.UpdateOldValuesInventoryTransferID(invenTransMode.getTransferID(), transferUniqueCreation);
                    mDBInternalHelper.UpdateOldValuesITScannedID(invenTransMode.getOLDTransferID(), invenTransMode.getTransferID());

                }
            }
            Common.InventoryCountList = mDBInternalHelper.getInventoryCountIdList();
            for (InventoryCountModel invCountMod : Common.InventoryCountList) {
                mDBInternalHelper.UpdateOldValuesICScannedID(invCountMod.getOLDListID(), invCountMod.getListID());
            }
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
    }

    public void GetInventoryTransferList() {
        try {
            Common.InventoryTransferList.clear();
            Common.InventoryTransferList = mDBInternalHelper.getInventoryTransferIdList();
            if (Common.InventoryTransferList.size() > 0) {
                invenTransadapter = new InventoryTransferAdapter(Common.InventoryTransferList, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                invenTransadapter.notifyDataSetChanged();
                InventoryTransList.setLayoutManager(horizontalLayoutManager);
                InventoryTransList.setAdapter(invenTransadapter);
                NoValueFoundTxT.setVisibility(View.GONE);
                InventoryTransList.setVisibility(View.VISIBLE);
            } else {
                InventoryTransList.setVisibility(View.GONE);
                NoValueFoundTxT.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
    }

    private void GetInventoryReceivedList() {
        try {
            Common.InventoryReceivedList.clear();
            Common.InventoryReceivedList = mDBInternalHelper.getInventoryReceivedIdList();
            if (Common.InventoryReceivedList.size() > 0) {
                invenRecievadapter = new InventorReceivedAdapter(Common.InventoryReceivedList, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                invenRecievadapter.notifyDataSetChanged();
                InventoryReceivedList.setLayoutManager(horizontalLayoutManager);
                InventoryReceivedList.setAdapter(invenRecievadapter);
                NoValueFoundTxT.setVisibility(View.GONE);
                InventoryReceivedList.setVisibility(View.VISIBLE);
            } else {
                InventoryReceivedList.setVisibility(View.GONE);
                NoValueFoundTxT.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
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

    public class InventoryCountAdapter extends RecyclerView.Adapter<InventoryCountAdapter.CountViewHolder> {
        private List<InventoryCountModel> InventoryCountList;
        Context context;

        public InventoryCountAdapter(List<InventoryCountModel> ScannedResultList, Context context) {
            this.InventoryCountList = ScannedResultList;
            this.context = context;
        }

        @Override
        public CountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_countlist_infliator, parent, false);
            CountViewHolder gvh = new CountViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(CountViewHolder holder, final int position) {
            //holder.ListIDTXT.setText(String.valueOf(position + 1));
            holder.ListIDTXT.setText(String.valueOf(InventoryCountList.get(position).getListID()));
            holder.ImeiTXT.setText(InventoryCountList.get(position).getImei());
            holder.LocationIDTXT.setText(String.valueOf(InventoryCountList.get(position).getLocationID()));
            holder.StartTimeTXT.setText(InventoryCountList.get(position).getStartTime());
            holder.EndTimeTXT.setText(InventoryCountList.get(position).getEndTime());
            holder.CountTXT.setText(String.valueOf(InventoryCountList.get(position).getCount()));
            holder.VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(InventoryCountList.get(position).getVolume())));
            holder.SyncStatusTXT.setText(String.valueOf(InventoryCountList.get(position).getSyncStatus()));

            if (InventoryCountList.get(position).getSyncStatus() == 1) {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.red));
            }
           /* if (InventoryCountList.get(position).getSyncTime().isEmpty()) {
                holder.SyncTimeTXT.setText("Not yet");
            } else {*/
            holder.SyncTimeTXT.setText(InventoryCountList.get(position).getSyncTime());
            //}
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            if (InventoryCountList.get(position).getCount() > 0) {
                holder.syncTXT.setVisibility(View.VISIBLE);
            } else {
                holder.syncTXT.setVisibility(View.INVISIBLE);
            }
            holder.syncTXT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Sync Date to BackEnd
                    //Common.IMEI = getImeiNumber();
                    Common.SyncStartDateTime = InventoryCountList.get(position).getStartTime();
                    Common.SyncEndDateTime = InventoryCountList.get(position).getEndTime();
                    Common.SyncListID = InventoryCountList.get(position).getListID();
                    Common.SyncBarCodeCount = InventoryCountList.get(position).getCount();
                    Common.LocationID = InventoryCountList.get(position).getLocationID();
                    InventoryCountSync(Common.SyncListID);
                }
            });
            holder.DeleteIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Remove From Transfer Lsit and Scanned list
                    Common.ListID = InventoryCountList.get(position).getListID();
                    if (InventoryCountList.get(position).getSyncStatus() == 1) {
                        DeleteTransferCountScannedList(Common.ListID);
                    } else {
                        if (InventoryCountList.get(position).getCount() < 5) {
                            DeleteTransferCountScannedList(Common.ListID);
                            return;
                        }
                        AlertDialogBox("InventoryTransfer", "This is not Syncked yet", false);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return InventoryCountList.size();
        }

        public class CountViewHolder extends RecyclerView.ViewHolder {
            TextView ListIDTXT, ImeiTXT, LocationIDTXT, StartTimeTXT, EndTimeTXT, CountTXT, SyncStatusTXT, SyncTimeTXT, VolumeTXT;
            ImageView syncTXT, DeleteIMG;
            LinearLayout Background;

            public CountViewHolder(View view) {
                super(view);
                Background = view.findViewById(R.id.countlayoutBackground);
                ListIDTXT = view.findViewById(R.id.count_listidTxT);
                ImeiTXT = view.findViewById(R.id.count_imeiTxT);
                LocationIDTXT = view.findViewById(R.id.count_locationIDTxT);
                StartTimeTXT = view.findViewById(R.id.count_startTimeTxT);
                EndTimeTXT = view.findViewById(R.id.count_endTimeTxT);
                CountTXT = view.findViewById(R.id.count_countTxT);
                SyncStatusTXT = view.findViewById(R.id.count_syncStatusTxT);
                SyncTimeTXT = view.findViewById(R.id.count_syncTimeTxT);
                syncTXT = view.findViewById(R.id.count_syncBtN);
                DeleteIMG = view.findViewById(R.id.count_delete);
                VolumeTXT = view.findViewById(R.id.count_VolumeTxT);
            }
        }
    }

    public class InventoryTransferAdapter extends RecyclerView.Adapter<InventoryTransferAdapter.TransferViewHolder> {
        private List<InventoryTransferModel> InventoryTransferList;
        Context context;

        public InventoryTransferAdapter(List<InventoryTransferModel> ScannedResultList, Context context) {
            this.InventoryTransferList = ScannedResultList;
            this.context = context;
        }

        @Override
        public TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_transferlist_infliator, parent, false);
            TransferViewHolder gvh = new TransferViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return InventoryTransferList.size();
        }

        @Override
        public void onBindViewHolder(TransferViewHolder holder, final int position) {
            //holder.TransferIDTXT.setText(String.valueOf(position + 1));
            holder.TransferIDTXT.setText(String.valueOf(InventoryTransferList.get(position).getTransUniqueID()));
            holder.ImeiTXT.setText(InventoryTransferList.get(position).getIMEI());
            holder.VBB_NumberTXT.setText(String.valueOf(InventoryTransferList.get(position).getVBB_Number()));
            holder.StartTimeTXT.setText(InventoryTransferList.get(position).getStartDateTime());
            holder.EndTimeTXT.setText(InventoryTransferList.get(position).getEndDateTime());
            holder.CountTXT.setText(String.valueOf(InventoryTransferList.get(position).getCount()));

            holder.VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(InventoryTransferList.get(position).getVolume())));
            holder.SyncStatusTXT.setText(String.valueOf(InventoryTransferList.get(position).getSyncStatus()));
            if (InventoryTransferList.get(position).getSyncStatus() == 1) {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.red));
            }
            holder.SyncTimeTXT.setText(InventoryTransferList.get(position).getSyncTime());
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            if (InventoryTransferList.get(position).getCount() > 0) {
                holder.syncTXT.setVisibility(View.VISIBLE);
            } else {
                holder.syncTXT.setVisibility(View.INVISIBLE);
            }
            holder.syncTXT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Sync Date to BackEnd
                    //Common.IMEI = getImeiNumber();
                    try {
                        Common.VBB_Number = InventoryTransferList.get(position).getVBB_Number();
                        Common.SyncStartDateTime = InventoryTransferList.get(position).getStartDateTime();
                        Common.SyncEndDateTime = InventoryTransferList.get(position).getEndDateTime();
                        Common.TransferID = InventoryTransferList.get(position).getTransferID();
                        Common.SyncBarCodeCount = InventoryTransferList.get(position).getCount();
                        Common.FromLocationID = InventoryTransferList.get(position).getFromLocationID();
                        Common.ToLocaTransID = InventoryTransferList.get(position).getToLocationID();
                        Common.TrucklicensePlateNo = InventoryTransferList.get(position).getTruckPlateNumber();
                        Common.TransportTypeId = InventoryTransferList.get(position).getTransportTypeId();
                        Common.LoadedTypeID = InventoryTransferList.get(position).getLoadedTypeID();
                        Common.TransferAgencyID = InventoryTransferList.get(position).getTransferAgencyID();
                        Common.DriverID = InventoryTransferList.get(position).getDriverID();
                   /* if (isNullOrEmpty(InventoryTransferList.get(position).getTransUniqueID())) {
                        Common.TransferUniqueID = String.valueOf(Common.TransferID);
                    } else {
                        Common.TransferUniqueID = InventoryTransferList.get(position).getTransUniqueID();
                    }*/
                        Common.TransferUniqueID = InventoryTransferList.get(position).getTransUniqueID();

                        InventoryTransferSync(Common.VBB_Number, Common.TransferID);
                    } catch (Exception ex) {
                        AlertDialogBox("Transfer Sync", ex.toString(), false);
                    }
                }
            });

            holder.DeleteIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Common.TransferID = InventoryTransferList.get(position).getTransferID();
                        //Remove From Transfer Lsit and Scanned list
                        if (InventoryTransferList.get(position).getSyncStatus() == 1) {
                            DeleteTransferListandTransferScannedList(Common.TransferID);
                        } else {
                            if (Common.Username.equals("1")) {
                                TransferDeleteDatasIFUserID_ONE("Are you sure you want delete all datas");
                            } else {
                                if (InventoryTransferList.get(position).getCount() < 5) {
                                    DeleteTransferListandTransferScannedList(Common.TransferID);
                                    return;
                                }
                                AlertDialogBox("InventoryTransfer", "This is not Syncked yet", false);
                            }
                        }
                    } catch (Exception ex) {
                        AlertDialogBox("Transfer DeleteIMG", ex.toString(), false);
                    }
                }
            });

            holder.printIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Common.VBB_Number = InventoryTransferList.get(position).getVBB_Number();
                        Common.TransferID = InventoryTransferList.get(position).getTransferID();
                        Common.TransferUniqueID = InventoryTransferList.get(position).getTransUniqueID();
                        Common.InventorytransferScannedResultList.clear();
                        Common.InventorytransferScannedResultList = mDBInternalHelper.getInventoryTransferWithVBBNumber(Common.VBB_Number, Common.TransferID);
                        if (Common.InventorytransferScannedResultList.size() > 0) {
                            Common.DriverID = InventoryTransferList.get(position).getDriverID();
                            Common.DriverName = mDBExternalHelper.getAllDriverName(Common.DriverID);
                            Common.TransportTypeId = InventoryTransferList.get(position).getTransportTypeId();
                            Common.TransportMode = mDBExternalHelper.getAllTransPortMode(Common.TransportTypeId);
                            Common.FromLocationID = InventoryTransferList.get(position).getFromLocationID();
                            Common.FromLocationname = mDBExternalHelper.getFromLocationName(Common.FromLocationID);
                            Common.ToLocaTransID = InventoryTransferList.get(position).getToLocationID();
                            Common.ToLocationName = mDBExternalHelper.getToLocationName(Common.ToLocaTransID);
                            Common.TransferAgencyID = InventoryTransferList.get(position).getTransferAgencyID();
                            if (isNullOrEmpty(String.valueOf(InventoryTransferList.get(position).getLoadedTypeID()))) {
                                Common.LoadedName = "";
                            } else {
                                Common.LoadedTypeID = InventoryTransferList.get(position).getLoadedTypeID();
                                Common.LoadedName = mDBExternalHelper.getLoadedName(Common.LoadedTypeID);
                            }
                            Common.AgencyName = mDBExternalHelper.getAgencyName(Common.TransferAgencyID);
                            Common.TrucklicensePlateNo = InventoryTransferList.get(position).getTruckPlateNumber();
                            try {
                                Common.TruckDeatilsList.clear();
                                Common.TruckDeatilsList = mDBExternalHelper.getAllTruckDetails();
                                for (int i = 0; i < Common.TruckDeatilsList.size(); i++) {
                                    if (Common.TrucklicensePlateNo.equals(Common.TruckDeatilsList.get(i).getTruckLicensePlateNo())) {
                                        Common.TransportId = Common.TruckDeatilsList.get(i).getTransportId();
                                    }
                                }
                            } catch (Exception ex) {
                            }
                            PrintTransferSlipCheck();
                        } else {
                            AlertDialogBox("Transfer PrintSlip", "No values found, try some other item", false);
                        }
                    } catch (Exception ex) {
                        AlertDialogBox("Transfer PrintSlip", ex.toString(), false);
                    }
                }
            });

            holder.Background.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        Common.TransferUniqueID = InventoryTransferList.get(position).getTransUniqueID();
                        Common.TransferID = InventoryTransferList.get(position).getTransferID();

                        Common.FromTransLocID = InventoryTransferList.get(position).getFromLocationID();
                        Common.FromLocationname = mDBExternalHelper.getFromLocationName(Common.FromTransLocID);

                        Common.ToLocaTransID = InventoryTransferList.get(position).getToLocationID();
                        Common.ToLocationName = mDBExternalHelper.getToLocationName(Common.ToLocaTransID);

                        Common.DriverID = InventoryTransferList.get(position).getDriverID();
                        Common.DriverName = mDBExternalHelper.getAllDriverName(Common.DriverID);

                        Common.TransferAgencyID = InventoryTransferList.get(position).getTransferAgencyID();
                        Common.AgencyName = mDBExternalHelper.getAgencyName(Common.TransferAgencyID);

                        Common.TransportTypeId = InventoryTransferList.get(position).getTransportTypeId();
                        Common.TransportMode = mDBExternalHelper.getAllTransPortMode(Common.TransportTypeId);

                        if (isNullOrEmpty(String.valueOf(InventoryTransferList.get(position).getLoadedTypeID()))) {
                            Common.LoadedName = "";
                        } else {
                            Common.LoadedTypeID = InventoryTransferList.get(position).getLoadedTypeID();
                            Common.LoadedName = mDBExternalHelper.getLoadedName(Common.LoadedTypeID);
                        }

                        Common.VBB_Number = InventoryTransferList.get(position).getVBB_Number();
                        Common.TrucklicensePlateNo = InventoryTransferList.get(position).getTruckPlateNumber();
                        try {
                            Common.TruckDeatilsList.clear();
                            Common.TruckDeatilsList = mDBExternalHelper.getAllTruckDetails();
                            for (int i = 0; i < Common.TruckDeatilsList.size(); i++) {
                                if (Common.TrucklicensePlateNo.equals(Common.TruckDeatilsList.get(i).getTruckLicensePlateNo())) {
                                    Common.TransportId = Common.TruckDeatilsList.get(i).getTransportId();
                                }
                            }
                        } catch (Exception ex) {
                        }
                        if (InventoryTransferList.get(position).getSyncStatus() == 0) {
                            Common.IsEditorViewFlag = true;
                            Common.IsTransferEditListFlag = false;
                            if (Common.FromLocationID != Common.FromTransLocID) {
                                Common.IsEditorViewFlag = false;
                            }
                        } else {
                            Common.IsEditorViewFlag = false;
                        }
                        InventorTransferAcivityCall();
                    } catch (Exception ex) {
                        AlertDialogBox("Transfer Background", ex.toString(), false);
                    }
                    return false;
                }

            });
        }

        public class TransferViewHolder extends RecyclerView.ViewHolder {
            TextView TransferIDTXT, ImeiTXT, VBB_NumberTXT, StartTimeTXT, EndTimeTXT, CountTXT, SyncStatusTXT, SyncTimeTXT, VolumeTXT;
            ImageView syncTXT, printIMG, DeleteIMG;
            LinearLayout Background;

            public TransferViewHolder(View view) {
                super(view);
                Background = view.findViewById(R.id.transferlayoutBackground);
                TransferIDTXT = view.findViewById(R.id.transfer_IDTxT);
                ImeiTXT = view.findViewById(R.id.transfer_imeiTxT);
                VBB_NumberTXT = view.findViewById(R.id.transfer_VVBNumberTxT);
                StartTimeTXT = view.findViewById(R.id.transfer_startTimeTxT);
                EndTimeTXT = view.findViewById(R.id.transfer_endTimeTxT);
                CountTXT = view.findViewById(R.id.transfer_countTxT);
                SyncStatusTXT = view.findViewById(R.id.transfer_syncStatusTxT);
                SyncTimeTXT = view.findViewById(R.id.transfer_syncTimeTxT);
                syncTXT = view.findViewById(R.id.transfer_syncIMG);
                printIMG = view.findViewById(R.id.printSlip_IMG);
                VolumeTXT = view.findViewById(R.id.transfer_VolumeTxT);
                DeleteIMG = view.findViewById(R.id.transfer_Delete);
            }
        }
    }

    public class InventorReceivedAdapter extends RecyclerView.Adapter<InventorReceivedAdapter.ReceivedViewHolder> {
        private List<InventoryReceivedListModel> InventoryReceivedList;
        Context context;

        public InventorReceivedAdapter(List<InventoryReceivedListModel> ScannedResultList, Context context) {
            this.InventoryReceivedList = ScannedResultList;
            this.context = context;
        }

        @Override
        public ReceivedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_receivedlist_infliator, parent, false);
            ReceivedViewHolder gvh = new ReceivedViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return InventoryReceivedList.size();
        }

        @Override
        public void onBindViewHolder(ReceivedViewHolder holder, final int position) {
            //holder.ReceivedIDTXT.setText(String.valueOf(position + 1));
            holder.ReceivedIDTXT.setText(String.valueOf(InventoryReceivedList.get(position).getReceivedID()));

            holder.TransferIDTXT.setText(String.valueOf(InventoryReceivedList.get(position).getTransferID()));

            holder.ImeiTXT.setText(InventoryReceivedList.get(position).getIMEI());

            holder.VBB_NumberTXT.setText(String.valueOf(InventoryReceivedList.get(position).getVBB_Number()));

            holder.StartTimeTXT.setText(InventoryReceivedList.get(position).getStartDateTime());

            holder.EndTimeTXT.setText(InventoryReceivedList.get(position).getEndDateTime());

            holder.CountTXT.setText(String.valueOf(InventoryReceivedList.get(position).getCount()));

            holder.VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(InventoryReceivedList.get(position).getVolume())));

            holder.SyncStatusTXT.setText(String.valueOf(InventoryReceivedList.get(position).getSyncStatus()));

            if (InventoryReceivedList.get(position).getSyncStatus() == 1) {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.red));
            }

            holder.SyncTimeTXT.setText(InventoryReceivedList.get(position).getSyncTime());

            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }

            if (InventoryReceivedList.get(position).getCount() > 0) {
                holder.syncTXT.setVisibility(View.VISIBLE);
            } else {
                holder.syncTXT.setVisibility(View.INVISIBLE);
            }

            holder.syncTXT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Sync Date to BackEnd Common.ToLocReceivedID
                    try {
                        Common.VBB_Number = InventoryReceivedList.get(position).getVBB_Number();
                        Common.SyncStartDateTime = InventoryReceivedList.get(position).getStartDateTime();
                        Common.SyncEndDateTime = InventoryReceivedList.get(position).getEndDateTime();
                        Common.ToLocReceivedID = InventoryReceivedList.get(position).getToLocationID();
                        Common.TransferID = InventoryReceivedList.get(position).getTransferID();
                        Common.ReceivedID = InventoryReceivedList.get(position).getReceivedID();
                        Common.SyncBarCodeCount = InventoryReceivedList.get(position).getCount();
                        Common.FromLocationID = InventoryReceivedList.get(position).getFromLocationID();
                        Common.TrucklicensePlateNo = InventoryReceivedList.get(position).getTruckPlateNumber();
                        Common.TransportTypeId = InventoryReceivedList.get(position).getTransportTypeId();
                        Common.TransferAgencyID = InventoryReceivedList.get(position).getTransferAgencyID();
                        Common.DriverID = InventoryReceivedList.get(position).getDriverID();
                        InventoryReceivedSync(Common.VBB_Number, Common.ReceivedID);
                    } catch (Exception ex) {
                        AlertDialogBox("Received Sync", ex.toString(), false);
                    }
                }
            });

            holder.DeleteIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Common.ReceivedID = InventoryReceivedList.get(position).getReceivedID();
                        //Remove From Transfer Lsit and Scanned list
                        if (InventoryReceivedList.get(position).getSyncStatus() == 1) {
                            DeleteReceivedListannReceivedScannedList(Common.ReceivedID);
                        } else {
                            if (Common.Username.equals("1")) {
                                ReceivedDeleteDatasIFUserID_ONE("Are you sure you want delete all datas");
                            } else {
                                if (InventoryReceivedList.get(position).getCount() < 5) {
                                    DeleteReceivedListannReceivedScannedList(Common.ReceivedID);
                                    return;
                                }
                                AlertDialogBox("InventoryReceived", "This is not Syncked yet", false);
                            }
                        }
                    } catch (Exception ex) {
                        AlertDialogBox("Transfer Sync", ex.toString(), false);
                    }
                }
            });

            holder.Background.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        Common.ReceivedID = InventoryReceivedList.get(position).getReceivedID();
                        Common.VBB_Number = InventoryReceivedList.get(position).getVBB_Number();
                        Common.ReceivedDate = InventoryReceivedList.get(position).getStartDateTime();
                        Common.Count = InventoryReceivedList.get(position).getCount();
                        Common.VolumeSum = InventoryReceivedList.get(position).getVolume();
                        Common.ReceivedLoadedTypeID = InventoryReceivedList.get(position).getLoadedTypeID();
                        try {
                            Common.ReceivedLoadedTypeName = "";
                            Common.LoadedByList.clear();
                            Common.LoadedByList = mDBExternalHelper.getAllLoadedByDetails();
                            Common.ReceivedLoadedTypeName = mDBExternalHelper.getAllLoadedNames(Common.ReceivedLoadedTypeID);
                        } catch (Exception ex) {
                        }
                        Common.TransferReciveUniqueID = Common.InventoryReceivedList.get(position).getTransferUniqueID();
                        int TrUniIDInd = (Common.TransferReciveUniqueID.length() - 10);
                        Common.ReceivedTransferID = Common.TransferReciveUniqueID.substring(Common.TransferReciveUniqueID.length() - TrUniIDInd);
                        Common.RecFromLocationID = InventoryReceivedList.get(position).getFromLocationID();
                        Common.RecFromLocationname = mDBExternalHelper.getFromLocationName(Common.RecFromLocationID);
                        Common.ToLocReceivedID = InventoryReceivedList.get(position).getToLocationID();
                        Common.ToLocationName = mDBExternalHelper.getToLocationName(Common.ToLocReceivedID);

                        Common.DriverID = InventoryReceivedList.get(position).getDriverID();
                        Common.DriverName = mDBExternalHelper.getAllDriverName(Common.DriverID);
                        Common.TransportTypeId = InventoryReceivedList.get(position).getTransportTypeId();
                        Common.TransportMode = mDBExternalHelper.getAllTransPortMode(Common.TransportTypeId);
                        Common.TransferAgencyID = InventoryReceivedList.get(position).getTransferAgencyID();
                        Common.AgencyName = mDBExternalHelper.getAgencyName(Common.TransferAgencyID);
                        Common.TrucklicensePlateNo = InventoryReceivedList.get(position).getTruckPlateNumber();

                        if (!isNullOrEmpty(Common.TrucklicensePlateNo)) {
                            Common.TruckDeatilsList.clear();
                            Common.TruckDeatilsList = mDBExternalHelper.getAllTruckDetails();
                            for (int i = 0; i < Common.TruckDeatilsList.size(); i++) {
                                if (Common.TrucklicensePlateNo.equals(Common.TruckDeatilsList.get(i).getTruckLicensePlateNo())) {
                                    Common.TransportId = Common.TruckDeatilsList.get(i).getTransportId();
                                }
                            }
                        }
                        if (InventoryReceivedList.get(position).getSyncStatus() == 0) {
                            Common.IsEditorViewFlag = true;
                            Common.IsReceivedEditListFlag = false;
                        } else {
                            Common.IsEditorViewFlag = false;
                        }
                        InventorReceivedAcivityCall();
                    } catch (Exception ex) {
                        AlertDialogBox("Received Sync", ex.toString(), false);
                    }
                    return false;
                }
            });

            holder.printIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Common.VBB_Number = InventoryReceivedList.get(position).getVBB_Number();
                        Common.ReceivedID = InventoryReceivedList.get(position).getReceivedID();
                        Common.ReceivedUniqueID = InventoryReceivedList.get(position).getReceivedUniqueID();
                        Common.ReceivedLoadedTypeID = InventoryReceivedList.get(position).getLoadedTypeID();
                        try {
                            Common.ReceivedLoadedTypeName = "";
                            Common.LoadedByList.clear();
                            Common.LoadedByList = mDBExternalHelper.getAllLoadedByDetails();
                            Common.ReceivedLoadedTypeName = mDBExternalHelper.getAllLoadedNames(Common.ReceivedLoadedTypeID);
                        } catch (Exception ex) {
                        }
                        Common.InventoryReceivedScannedResultList.clear();
                        Common.InventoryReceivedScannedResultList = mDBInternalHelper.getInventoryReceivedWithVBBNumber(Common.VBB_Number, Common.ReceivedID);
                        if (Common.InventoryReceivedScannedResultList.size() > 0) {
                            Common.TransferReciveUniqueID = Common.InventoryReceivedScannedResultList.get(0).getTransferUniqueID();
                            int TrUniIDInd = (Common.TransferReciveUniqueID.length() - 10);
                            Common.ReceivedTransferID = Common.TransferReciveUniqueID.substring(Common.TransferReciveUniqueID.length() - TrUniIDInd);
                            Common.RecFromLocationID = InventoryReceivedList.get(position).getFromLocationID();
                            Common.RecFromLocationname = mDBExternalHelper.getFromLocationName(Common.RecFromLocationID);
                            Common.ToLocReceivedID = InventoryReceivedList.get(position).getToLocationID();
                            Common.ToLocationName = mDBExternalHelper.getToLocationName(Common.ToLocReceivedID);
                            Common.DriverID = InventoryReceivedList.get(position).getDriverID();
                            Common.DriverName = mDBExternalHelper.getAllDriverName(Common.DriverID);
                            Common.TransportTypeId = InventoryReceivedList.get(position).getTransportTypeId();
                            Common.TransportMode = mDBExternalHelper.getAllTransPortMode(Common.TransportTypeId);
                            Common.TransferAgencyID = InventoryReceivedList.get(position).getTransferAgencyID();
                            Common.AgencyName = mDBExternalHelper.getAgencyName(Common.TransferAgencyID);
                            Common.TrucklicensePlateNo = InventoryReceivedList.get(position).getTruckPlateNumber();
                            try {
                                Common.TruckDeatilsList.clear();
                                Common.TruckDeatilsList = mDBExternalHelper.getAllTruckDetails();
                                for (int i = 0; i < Common.TruckDeatilsList.size(); i++) {
                                    if (Common.TrucklicensePlateNo.equals(Common.TruckDeatilsList.get(i).getTruckLicensePlateNo())) {
                                        Common.TransportId = Common.TruckDeatilsList.get(i).getTransportId();
                                    }
                                }
                            } catch (Exception ex) {
                            }
                            PrintReceivedSlipCheck();
                        } else {
                            AlertDialogBox("Transfer PrintSlip", "No values found, try some other item", false);
                        }
                    } catch (Exception ex) {

                    }
                }
            });
        }

        public class ReceivedViewHolder extends RecyclerView.ViewHolder {
            TextView ReceivedIDTXT, TransferIDTXT, ImeiTXT, VBB_NumberTXT, StartTimeTXT, EndTimeTXT, CountTXT, SyncStatusTXT, SyncTimeTXT, VolumeTXT;
            ImageView syncTXT, DeleteIMG, viewIMG, printIMG;
            LinearLayout Background;

            public ReceivedViewHolder(View view) {
                super(view);
                Background = view.findViewById(R.id.receivedlayoutBackground);
                ReceivedIDTXT = view.findViewById(R.id.received_IDTxT);
                TransferIDTXT = view.findViewById(R.id.received_TransferTxT);
                ImeiTXT = view.findViewById(R.id.received_imeiTxT);
                VBB_NumberTXT = view.findViewById(R.id.received_VVBNumberTxT);
                StartTimeTXT = view.findViewById(R.id.received_startTimeTxT);
                EndTimeTXT = view.findViewById(R.id.received_endTimeTxT);
                CountTXT = view.findViewById(R.id.received_countTxT);
                SyncStatusTXT = view.findViewById(R.id.received_syncStatusTxT);
                SyncTimeTXT = view.findViewById(R.id.received_syncTimeTxT);
                syncTXT = view.findViewById(R.id.received_syncIMG);
                VolumeTXT = view.findViewById(R.id.received_VolumeTxT);
                DeleteIMG = view.findViewById(R.id.received_Delete);
                viewIMG = view.findViewById(R.id.received_view);
                printIMG = view.findViewById(R.id.printSlip_IMG);
            }
        }
    }

    public class FellingRegistrationAdapter extends RecyclerView.Adapter<FellingRegistrationAdapter.FellingRegViewHolder> implements Filterable {
        private List<FellingRegisterListModel> FellingRegisterList = new ArrayList<>();
        private List<FellingRegisterListModel> FellingRegisterListFilter = new ArrayList<>();
        Context context;
        // private ItemFilter mFilter = new ItemFilter();
        FellingFilterInterface listener;

        public FellingRegistrationAdapter(List<FellingRegisterListModel> FellingRegisterList, Context context, FellingFilterInterface Fe_listener) {
            this.FellingRegisterList = FellingRegisterList;
            this.FellingRegisterListFilter = FellingRegisterList;
            this.context = context;
            this.listener = Fe_listener;
        }

        @Override
        public FellingRegViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.felling_registerlistinfliator, parent, false);
            FellingRegViewHolder gvh = new FellingRegViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return FellingRegisterListFilter.size();
        }

        @Override
        public void onBindViewHolder(FellingRegViewHolder holder, final int position) {
            try {
                if (FellingRegisterListFilter.size() > 0) {
                    final FellingRegisterListModel FellingRegLstModel = FellingRegisterListFilter.get(position);
                    holder.FellingRegIDTxT.setText(String.valueOf(FellingRegLstModel.getFellingRegistrationUniqueID()));
                    holder.FellingRegNoTxT.setText(String.valueOf(FellingRegLstModel.getFellingRegistrationNumber()));
                    holder.RegisterDateTxT.setText(FellingRegLstModel.getFellingRegistrationDate());
                    holder.EndTimeTXT.setText(FellingRegLstModel.getEndDateTime());
                    holder.CountTXT.setText(String.valueOf(FellingRegLstModel.getCount()));
                    holder.SyncStatusTXT.setText(String.valueOf(FellingRegLstModel.getSyncStatus()));
                    holder.Volume.setText(String.valueOf(Common.decimalFormat.format(FellingRegLstModel.getVolume())));

                    if (FellingRegLstModel.getSyncStatus() == 1) {
                        holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    } else {
                        holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.red));
                    }
                    holder.SyncTimeTXT.setText(FellingRegLstModel.getSyncTime());
                    if (position % 2 == 0) {
                        holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
                    } else {
                        holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
                    }
                    if (FellingRegLstModel.getCount() > 0) {
                        holder.syncTXT.setVisibility(View.VISIBLE);
                        holder.PrintIMG.setVisibility(View.VISIBLE);
                    } else {
                        holder.syncTXT.setVisibility(View.INVISIBLE);
                        holder.PrintIMG.setVisibility(View.INVISIBLE);
                    }
                    holder.syncTXT.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (Common.FellingRegSyncALL == false) {
                                    //Sync Date to BackEnd
                                    Common.SyncStartDateTime = FellingRegLstModel.getFellingRegistrationDate();
                                    Common.SyncEndDateTime = FellingRegLstModel.getEndDateTime();
                                    Common.FellingRegID = FellingRegLstModel.getFellingRegID();
                                    Common.FellingRegNo = FellingRegLstModel.getFellingRegistrationNumber();
                                    Common.SyncBarCodeCount = FellingRegLstModel.getCount();
                                    Common.FellingSectionId = FellingRegLstModel.getFellingSectionID();
                                    Common.FellingRegUniqueID = FellingRegLstModel.getFellingRegistrationUniqueID();
                                    Common.SyncEndDateTime = FellingRegLstModel.getEndDateTime();
                                    Common.TreeFromLocation = FellingRegLstModel.getLocationID();
                                    Common.VolumeSum = FellingRegLstModel.getVolume();
                                    FellingRegistrayionSync(Common.TreeFromLocation, Common.FellingSectionId, Common.FellingRegID, Common.FellingRegUniqueID);
                                } else {
                                    FellingRegSyncALL("Are you sure want to sync all datas?");
                                }
                            } catch (Exception ex) {
                                AlertDialogBox("Felling Sync", ex.toString(), false);
                            }

                        }
                    });
                    holder.DeleteIMG.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Common.FellingRegID = FellingRegLstModel.getFellingRegID();
                                //Remove From Transfer Lsit and Scanned list
                                if (FellingRegLstModel.getSyncStatus() == 1) {
                                    DeleteFellingListannFellingScannedList(Common.FellingRegID);
                                } else {
                                    if (FellingRegLstModel.getCount() < 5) {
                                        DeleteFellingListannFellingScannedList(Common.FellingRegID);
                                        return;
                                    }
                                    AlertDialogBox("InventoryTransfer", "This is not Syncked yet", false);
                                }
                            } catch (Exception ex) {
                                AlertDialogBox("Felling Delete", ex.toString(), false);
                            }
                        }
                    });
                    holder.Background.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            try {
                                Common.FellingRegUniqueID = FellingRegLstModel.getFellingRegistrationUniqueID();
                                Common.FellingRegID = FellingRegLstModel.getFellingRegID();
                                Common.FromLocationID = FellingRegLstModel.getLocationID();
                                Common.ConcessionList.clear();
                                Common.ConcessionList = mDBExternalHelper.getConcessionList();
                                for (ConcessionNamesModel locationMod : Common.ConcessionList) {
                                    if (Common.FromLocationID == locationMod.getFromLocationId()) {
                                        Common.FromLocationname = locationMod.getConcessionName();
                                    }
                                }
                                Common.FellingSectionId = FellingRegLstModel.getFellingSectionID();
                                Common.FellingRegDate = FellingRegLstModel.getFellingRegistrationDate();
                                Common.FellingRegNo = FellingRegLstModel.getFellingRegistrationNumber();
                                if (FellingRegLstModel.getSyncStatus() == 0) {
                                    Common.IsEditorViewFlag = true;
                                    Common.IsFellingRegEditListFlag = false;
                                } else {
                                    Common.IsEditorViewFlag = false;
                                }
                                InventorFellingRegAcivityCall();
                            } catch (Exception ex) {
                                AlertDialogBox("Felling Background", ex.toString(), false);
                            }
                            return false;
                        }
                    });
                    holder.PrintIMG.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Common.FellingRegUniqueID = FellingRegLstModel.getFellingRegistrationUniqueID();
                                Common.FellingRegID = FellingRegLstModel.getFellingRegID();
                                Common.FromLocationID = FellingRegLstModel.getLocationID();
                                Common.FromLocationname = mDBExternalHelper.getFromLocationName(Common.FromLocationID);
                                Common.FellingSectionId = FellingRegLstModel.getFellingSectionID();
                                Common.FellingRegDate = FellingRegLstModel.getFellingRegistrationDate();
                                Common.FellingRegNo = FellingRegLstModel.getFellingRegistrationNumber();
                                Common.VolumeSum = FellingRegLstModel.getVolume();
                                Common.FellingRegisterInputList.clear();
                                Common.FellingRegisterLogsDetails = mDBInternalHelper.getFellingformDetailsID(Common.FellingRegID, Common.FellingSectionId);

                                Common.PlotNo = mDBInternalHelper.GetPlotNoUsingFellingSecID(Common.FromLocationID, Common.FellingSectionId, Common.FellingRegUniqueID);
                                Common.FellingSectionNumber = mDBExternalHelper.GetFellingSectionNumber(Common.FromLocationID, Common.FellingSectionId);

                                if (Common.FellingRegisterLogsDetails.size() > 0) {
                                    PrintFellingSlipCheck();
                                } else {
                                    AlertDialogBox("Felling PrintSlip", "No values found, try some other item", false);
                                }

                            } catch (Exception ex) {
                                AlertDialogBox("Felling PrintSlip", ex.toString(), false);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("Exception", ">>>>>>>>>>>" + e.toString());
            }
        }

        public class FellingRegViewHolder extends RecyclerView.ViewHolder {
            TextView FellingRegIDTxT, FellingRegNoTxT, RegisterDateTxT, EndTimeTXT, CountTXT, SyncStatusTXT, SyncTimeTXT, Volume;
            ImageView syncTXT, PrintIMG, DeleteIMG;
            LinearLayout Background;

            public FellingRegViewHolder(View view) {
                super(view);
                Background = view.findViewById(R.id.fellinglayoutBackground);
                FellingRegIDTxT = view.findViewById(R.id.fellingRegidTxT);
                FellingRegNoTxT = view.findViewById(R.id.fellingRegNoTxT);
                RegisterDateTxT = view.findViewById(R.id.fellingRegDateTxT);
                EndTimeTXT = view.findViewById(R.id.fellingRegEndDateTxT);
                CountTXT = view.findViewById(R.id.fellingReg_countTxT);
                SyncStatusTXT = view.findViewById(R.id.fellingReg_StatusTxT);
                SyncTimeTXT = view.findViewById(R.id.fellingReg_syncTimeTxT);
                syncTXT = view.findViewById(R.id.fellingReg_syncBtN);
                DeleteIMG = view.findViewById(R.id.fellingReg_remove);
                PrintIMG = view.findViewById(R.id.fellingReg_PrintBtn);
                Volume = view.findViewById(R.id.fellingReg_TotVolumeTxT);

            }
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        FellingRegisterListFilter = FellingRegisterList;
                    } else {
                        ArrayList<FellingRegisterListModel> filteredList = new ArrayList<>();
                        for (FellingRegisterListModel row : FellingRegisterList) {
                            if (row.getFellingSectionID().toLowerCase().equals(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        FellingRegisterListFilter = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = FellingRegisterListFilter;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    FellingRegisterListFilter = (ArrayList<FellingRegisterListModel>) results.values;
                    FellingRegisterFilteredList.clear();
                    FellingRegisterFilteredList.addAll(FellingRegisterListFilter);
                    fellingRegadapter.notifyDataSetChanged();
                    TotalFilteredCount.setText(String.valueOf(FellingRegisterListFilter.size()));
                    TotalFilteredVolume.setText(String.valueOf(mDBInternalHelper.TotalFilteredVolume(constraint.toString())));
                    /*if (FellingRegisterListFilter.size() > 0) {
                        NoValueFoundTxT.setVisibility(View.GONE);
                    } else {
                        NoValueFoundTxT.setVisibility(View.VISIBLE);
                    }*/
                }
            };
        }


    }


/*
    private String getImeiNumber() {
        final TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return telephonyManager.getImei();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return telephonyManager.getDeviceId();
            }
        }
        return telephonyManager.getDeviceId();
    }
*/

    public void HideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void InventoryCountSync(int ListID) {
        Common.InventoryCountInputList.clear();
        Common.InventoryCountInputList = mDBInternalHelper.getScannedResultInputWithListID(ListID);
        if (Common.InventoryCountInputList.size() > 0) {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                new GetInventoryCountSyncAsynkTask().execute();
            }
        } else {
            AlertDialogBox("InventoryCount Sync", "List id is empty", false);
        }

    }

    // Method for SyncAPI
    class GetInventoryCountSyncAsynkTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.SyncStatusList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String MethodName = "InsertHHInventoryCount_V2";//"InsertHHInventoryCount/";
            String SyncURL = ServiceURL.getServiceURL(ServiceURL.ControllorName, MethodName);
            countSyncModel = new InventoryCountSyncModel();
            countSyncModel.setIMEI(Common.IMEI);
            countSyncModel.setStartedTime(Common.SyncStartDateTime);
            countSyncModel.setEndDateTime(Common.SyncEndDateTime);
            countSyncModel.setListID(Common.SyncListID);
            countSyncModel.setBarCodeCount(Common.SyncBarCodeCount);
            countSyncModel.setLocationID(Common.LocationID);
            countSyncModel.setUserId(Common.UserID);
            countSyncModel.setHHScannedResult(Common.InventoryCountInputList);
            try {
                String SyncURLInfo = new Communicator().POST_Obect(SyncURL, countSyncModel);
                if (GwwException.GwwException(Common.HttpResponceCode) == true) {
                    if (SyncURLInfo != null) {
                        JSONObject jsonObj = new JSONObject(SyncURLInfo);
                        String SyncResponceStr = jsonObj.getString("Status");
                        if (SyncResponceStr != null) {
                            JSONArray SyncJsonAry = new JSONArray(SyncResponceStr);
                            for (int Sync_Index = 0; Sync_Index < SyncJsonAry.length(); Sync_Index++) {
                                suncStaModel = new Gson().fromJson(SyncJsonAry.getString(Sync_Index), SyncStatusModel.class);
                                Common.SyncStatusList.add(suncStaModel);
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
                        if (Common.SyncStatusList.get(0).getStatus() == 1) {
                            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                            Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                            boolean ListIdFlag = mDBInternalHelper.UpdateInventoryCountSyncStatusListID(Common.SyncTime, 1, Common.EndDate, Common.SyncListID);
                            if (ListIdFlag == true) {
                                //Scanned Result Refresh
                                GetInventoryCountList();
                                AlertDialogBox("InventoryCount", Common.SyncStatusList.get(0).getMessage(), true);
                            }
                        } else {
                            AlertDialogBox("InventoryCount", Common.SyncStatusList.get(0).getMessage(), false);
                        }
                    } else {
                        AlertDialogBox("InventoryCount", "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(InventoryActivity.this, Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public String CommonMessage(int Common_Msg) {
        return InventoryActivity.this.getResources().getString(Common_Msg);
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

    private void getTransferLocationDetials() {
        try {
            Common.AllTransLogDetailsmap.clear();
            Common.AllTransLogDetailsmap = mDBExternalHelper.getAllTransferLogDetails();
            //GetScannedResultListWithIndex();
        } catch (Exception ex) {
            Log.v("TransferLocationDetials", "Retrive Exception: " + ex.toString());
        }
    }

    class GetInventoryTransferSyncAsynkTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.SyncStatusList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String MethodName = "InsertHHInventoryTransfer_v2";//"InsertHHInventoryTransfer/";
            String SyncURL = ServiceURL.getServiceURL(ServiceURL.ControllorName, MethodName);
            transferSyncModel = new InventoryTransferSyncModel();
            transferSyncModel.setTransferID(Common.TransferID);
            transferSyncModel.setVBBNumber(Common.VBB_Number);
            transferSyncModel.setIMEINumber(Common.IMEI);
            transferSyncModel.setStartTime(Common.SyncStartDateTime);
            transferSyncModel.setEndTime(Common.SyncEndDateTime);
            transferSyncModel.setTruckPlateNumber(Common.TrucklicensePlateNo);
            transferSyncModel.setLocationID(Common.FromLocationID);
            transferSyncModel.setTransferModeID(Common.TransportTypeId);
            transferSyncModel.setTransferAgencyId(Common.TransferAgencyID);
            transferSyncModel.setDriverId(Common.DriverID);
            transferSyncModel.setTranferredCount(Common.SyncBarCodeCount);
            transferSyncModel.setToLocationID(Common.ToLocaTransID);//ToLocaTransID
            transferSyncModel.setUserID(Common.UserID);
            transferSyncModel.setTransferUniqueID(Common.TransferUniqueID);
            transferSyncModel.setLoadedTypeID(Common.LoadedTypeID);
            transferSyncModel.setHHInventoryTransfer(Common.InventoryTransferInputList);
            try {
                String SyncURLInfo = new Communicator().POST_Obect(SyncURL, transferSyncModel);
                if (GwwException.GwwException(Common.HttpResponceCode) == true) {
                    if (SyncURLInfo != null) {
                        JSONObject jsonObj = new JSONObject(SyncURLInfo);
                        String SyncResponceStr = jsonObj.getString("Status");
                        if (SyncResponceStr != null) {
                            JSONArray SyncJsonAry = new JSONArray(SyncResponceStr);
                            for (int Sync_Index = 0; Sync_Index < SyncJsonAry.length(); Sync_Index++) {
                                suncStaModel = new Gson().fromJson(SyncJsonAry.getString(Sync_Index), SyncStatusModel.class);
                                Common.SyncStatusList.add(suncStaModel);
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
                        if (Common.SyncStatusList.get(0).getStatus() == 1) {
                            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                            Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                            boolean ListIdFlag = mDBInternalHelper.UpdateInventoryTransferSyncStatusTransID(Common.SyncTime, 1, Common.VBB_Number, Common.TransferID);
                            if (ListIdFlag == true) {
                                //Scanned Result Refresh
                                GetInventoryTransferList();
                                AlertDialogBox("InventoryTransfer", Common.SyncStatusList.get(0).getMessage(), true);
                            }
                        } else {
                            AlertDialogBox("InventoryTransfer", Common.SyncStatusList.get(0).getMessage(), false);
                        }
                    } else {
                        AlertDialogBox("InventoryTransfer", "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }

    }

    public void InventoryTransferSync(String VbbNumber, int TransferId) {
        Common.InventoryTransferInputList.clear();
        Common.InventoryTransferInputList = mDBInternalHelper.getTransferScannedResultInputWithVBBNo(VbbNumber, TransferId);
        if (Common.InventoryTransferInputList.size() > 0) {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                new GetInventoryTransferSyncAsynkTask().execute();
            }
        } else {
            AlertDialogBox("InventoryTransfer Sync", "Values are empty", false);
        }
    }

    public void PrintTransferSlipCheck() {
        if (Common.IsPrintBtnClickFlag == true) {
            if (Common.InventorytransferScannedResultList.size() > 0) {
                if (BlutoothCommonClass.isBluetoothEnabled() == false) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    Common.devAddress = BlueTooth.getAddress("BTSPP");
                    if (Common.devAddress != null) {
                        //ProgressBarLay.setVisibility(View.VISIBLE);
                        Common.Printerstatus = tsc.printerstatus();
                        if (TSCConnInt == 1) {
                            Common.TSCstatus = tsc.status();
                            Common.Printerstatus = tsc.printerstatus();
                        } else {
                            String BtConnResult = tsc.openport(Common.devAddress);
                            TSCConnInt = Integer.parseInt(BtConnResult);
                            Common.Printerstatus = tsc.printerstatus();
                        }
                        if (Common.Printerstatus.equals("00")) {
                            //ProgressBarLay.setVisibility(View.GONE);
                            Common.IsPrintBtnClickFlag = false;
                            TransferListPrintHan.post(TransferListPrintRun);
                        } else {
                            AlertDialogBox(CommonMessage(R.string.TSC_Conn), GwwException.PrinterCurrentStatus(Common.Printerstatus), false);
                            TSCConnInt = -1;
                            tsc.closeport();
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.PrinterHead), CommonMessage(R.string.PrinterHDmsg), false);
                    }
                }
            } else {
                AlertDialogBox(CommonMessage(R.string.TransferHead), CommonMessage(R.string.TransferMessage), false);
            }
        } else {
            AlertDialogBox(CommonMessage(R.string.PrinterHead), CommonMessage(R.string.Printing), false);
        }
    }

    public void PrintReceivedSlipCheck() {
        if (Common.IsPrintBtnClickFlag == true) {
            if (Common.InventoryReceivedScannedResultList.size() > 0) {
                if (BlutoothCommonClass.isBluetoothEnabled() == false) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    Common.devAddress = BlueTooth.getAddress("BTSPP");
                    if (Common.devAddress != null) {
                        //ProgressBarLay.setVisibility(View.VISIBLE);
                        Common.Printerstatus = tsc.printerstatus();
                        if (TSCConnInt == 1) {
                            Common.TSCstatus = tsc.status();
                            Common.Printerstatus = tsc.printerstatus();
                        } else {
                            String BtConnResult = tsc.openport(Common.devAddress);
                            TSCConnInt = Integer.parseInt(BtConnResult);
                            Common.Printerstatus = tsc.printerstatus();
                        }
                        if (Common.Printerstatus.equals("00")) {
                            //ProgressBarLay.setVisibility(View.GONE);
                            Common.IsPrintBtnClickFlag = false;
                            ReceivedListPrintHan.post(ReceivedListPrintRun);
                        } else {
                            AlertDialogBox(CommonMessage(R.string.TSC_Conn), GwwException.PrinterCurrentStatus(Common.Printerstatus), false);
                            TSCConnInt = -1;
                            tsc.closeport();
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.PrinterHead), CommonMessage(R.string.PrinterHDmsg), false);
                    }
                }
            } else {
                AlertDialogBox(CommonMessage(R.string.TransferHead), CommonMessage(R.string.TransferMessage), false);
            }
        } else {
            AlertDialogBox(CommonMessage(R.string.PrinterHead), CommonMessage(R.string.Printing), false);
        }
    }

    public void PrintFellingSlipCheck() {
        if (Common.IsPrintBtnClickFlag == true) {
            if (Common.FellingRegisterLogsDetails.size() > 0) {
                if (BlutoothCommonClass.isBluetoothEnabled() == false) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    Common.devAddress = BlueTooth.getAddress("BTSPP");
                    if (Common.devAddress != null) {
                        //ProgressBarLay.setVisibility(View.VISIBLE);
                        Common.Printerstatus = tsc.printerstatus();
                        if (TSCConnInt == 1) {
                            Common.TSCstatus = tsc.status();
                            Common.Printerstatus = tsc.printerstatus();
                        } else {
                            String BtConnResult = tsc.openport(Common.devAddress);
                            TSCConnInt = Integer.parseInt(BtConnResult);
                            Common.Printerstatus = tsc.printerstatus();
                        }
                        if (Common.Printerstatus.equals("00")) {
                            //ProgressBarLay.setVisibility(View.GONE);
                            Common.IsPrintBtnClickFlag = false;
                            FellingRegPrintHan.post(FellingRegPrintRun);
                        } else {
                            AlertDialogBox(CommonMessage(R.string.TSC_Conn), GwwException.PrinterCurrentStatus(Common.Printerstatus), false);
                            TSCConnInt = -1;
                            tsc.closeport();
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.PrinterHead), CommonMessage(R.string.PrinterHDmsg), false);
                    }
                }
            } else {
                AlertDialogBox(CommonMessage(R.string.TransferHead), CommonMessage(R.string.TransferMessage), false);
            }
        } else {
            AlertDialogBox(CommonMessage(R.string.PrinterHead), CommonMessage(R.string.Printing), false);
        }
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str == "null";
    }

    Handler TransferListPrintHan = new Handler();
    Runnable TransferListPrintRun = new Runnable() {
        @Override
        public void run() {
            try {
                Common.VolumeSum = TransferTotalVolume(Common.InventorytransferScannedResultList);
                Common.Count = Common.InventorytransferScannedResultList.size();
                tsc.clearbuffer();
                /*hided in version 5.0*/
                tsc.sendcommand(printSlip.TransferHeader());
                //tsc.sendcommand(printSlip.TransferDetails());
                tsc.sendcommand(printSlip.TransferDetails19_09_2019());
                tsc.sendcommand(printSlip.TransferScannedItemsDetails());
                if (Common.InventorytransferScannedResultList.size() > (Common.VVBLimitation + 2)) {
                } else {
                    tsc.sendcommand(printSlip.TransferFooter());
                    tsc.clearbuffer();
                }
                tsc.sendcommand(printSlip.TransferReceivedBottom19_09_2019(Common.TransferUniqueID));
                /*location ----> sabaru to dp*/
                /*hided in version 5.0*/
                int TimerforPrint = 0;
                if (Common.Count > 50) {
                    TimerforPrint = ((Common.Count / 50) * 2) * 1000;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PrintoutEnd();
                    }
                }, TimerforPrint);
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e("Exception", ">>>>>>>>>>>" + ex.toString());
            }
        }
    };

    Handler ReceivedListPrintHan = new Handler();
    Runnable ReceivedListPrintRun = new Runnable() {
        @Override
        public void run() {
            try {
                Common.VolumeSum = ReceivedTotalVolume(Common.InventoryReceivedScannedResultList);
                Common.Count = Common.InventoryReceivedScannedResultList.size();
                tsc.clearbuffer();
                //tsc.sendcommand(printSlip.ReceivedHeader());
                tsc.sendcommand(printSlip.ReceivedHeader19_09_2019());
                //tsc.sendcommand(printSlip.ReceivedBarCodeSlips());
                tsc.sendcommand(printSlip.ReceivedScannedItemsDetails());
                tsc.sendcommand(printSlip.TransferReceivedBottom19_09_2019(Common.TransferReciveUniqueID));
                tsc.clearbuffer();
                int TimerforPrint = 0;
                if (Common.Count > 30) {
                    TimerforPrint = ((Common.Count / 30) * 2) * 1000;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PrintoutEnd();
                    }
                }, TimerforPrint);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    Handler FellingRegPrintHan = new Handler();
    Runnable FellingRegPrintRun = new Runnable() {
        @Override
        public void run() {
            try {
                Common.Count = Common.FellingRegisterLogsDetails.size();
                tsc.clearbuffer();
                tsc.sendcommand(printSlip.FellingRegisterHeader());
                tsc.clearbuffer();
                tsc.sendcommand(printSlip.FellingRegisterDimensions());
                tsc.clearbuffer();
                PrintoutEnd();
             /*   int TimerforPrint = 0;
                if (Common.Count > 30) {
                    TimerforPrint = ((Common.Count / 30) * 2) * 1000;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PrintoutEnd();
                    }
                }, TimerforPrint);*/
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    public void PrintoutEnd() {
        tsc.closeport();
        TSCConnInt = -1;
        Common.IsPrintBtnClickFlag = true;
    }

    public void SignoutforLogin(String ErrorMessage) {
        if (SignoutAlert != null && SignoutAlert.isShowing()) {
            return;
        }
        Signoutbuilder = new AlertDialog.Builder(InventoryActivity.this);
        Signoutbuilder.setMessage(ErrorMessage);
        Signoutbuilder.setCancelable(true);
        Signoutbuilder.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Signoutbuilder.setNegativeButton(InventoryActivity.this.getResources().getString(R.string.action_signout),
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
        Intent oneIntent = new Intent(InventoryActivity.this, LoginActivity.class);
        startActivity(oneIntent);

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
                SignoutforLogin(InventoryActivity.this.getResources().getString(R.string.SignoutMsg));
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
        if (id == R.id.action_export) {
            if (Common.IsPrintBtnClickFlag == true) {
                AlertDialogBox("Internal Database", "Exported database successfully!", true);
                PushDatabaseFromAppToInternalStorage();
            }
        }
        if (id == R.id.action_exportXecel) {
            if (Common.IsPrintBtnClickFlag == true) {
                //ExportExcelFileFromDeviceDB();
                ExportAllTables();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void InventoryReceivedSync(String VbbNumber, int ReceivedID) {
        Common.InventoryReceivedInputList.clear();
        Common.InventoryReceivedInputList = mDBInternalHelper.getReceivedScannedResultInputWithVBBNo(VbbNumber, ReceivedID);
        if (Common.InventoryReceivedInputList.size() > 0) {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                new GetInventoryReceivedSyncAsynkTask().execute();
            }
        } else {
            AlertDialogBox("InventoryTransfer Sync", "Values are empty", false);
        }
    }

    class GetInventoryReceivedSyncAsynkTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.SyncStatusList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String MethodName = "InsertHHInventoryReceived/";
            String SyncURL = ServiceURL.getServiceURL(ServiceURL.ControllorName, MethodName);
            receiedSyncModel = new InventoryReceivedSyncModel();
            receiedSyncModel.setReceivedID(Common.ReceivedID);
            receiedSyncModel.setTransferID(Common.TransferID);
            receiedSyncModel.setVBBNumber(Common.VBB_Number);
            receiedSyncModel.setIMEINumber(Common.IMEI);
            receiedSyncModel.setStartTime(Common.SyncStartDateTime);
            receiedSyncModel.setEndTime(Common.SyncEndDateTime);
            receiedSyncModel.setTruckPlateNumber(Common.TrucklicensePlateNo);
            receiedSyncModel.setLocationID(Common.FromLocationID);
            receiedSyncModel.setTransferModeID(Common.TransportTypeId);
            receiedSyncModel.setTransferAgencyId(Common.TransferAgencyID);
            receiedSyncModel.setDriverId(Common.DriverID);
            receiedSyncModel.setTranferredCount(Common.SyncBarCodeCount);
            receiedSyncModel.setToLocationID(Common.ToLocReceivedID);
            receiedSyncModel.setUserID(Common.UserID);
            receiedSyncModel.setHHReceived(Common.InventoryReceivedInputList);
            try {
                String SyncURLInfo = new Communicator().POST_Obect(SyncURL, receiedSyncModel);
                if (GwwException.GwwException(Common.HttpResponceCode) == true) {
                    if (SyncURLInfo != null) {
                        JSONObject jsonObj = new JSONObject(SyncURLInfo);
                        String SyncResponceStr = jsonObj.getString("Status");
                        if (SyncResponceStr != null) {
                            JSONArray SyncJsonAry = new JSONArray(SyncResponceStr);
                            for (int Sync_Index = 0; Sync_Index < SyncJsonAry.length(); Sync_Index++) {
                                suncStaModel = new Gson().fromJson(SyncJsonAry.getString(Sync_Index), SyncStatusModel.class);
                                Common.SyncStatusList.add(suncStaModel);
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
                        if (Common.SyncStatusList.get(0).getStatus() == 1) {
                            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                            Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                            boolean ListIdFlag = mDBInternalHelper.UpdateInventoryReceivedSyncStatusReceivedID(Common.SyncTime, 1, Common.ReceivedID);
                            if (ListIdFlag == true) {
                                //Scanned Result Refresh
                                GetInventoryReceivedList();
                                AlertDialogBox("InventoryTransfer", Common.SyncStatusList.get(0).getMessage(), true);
                            }
                        } else {
                            AlertDialogBox("InventoryTransfer", Common.SyncStatusList.get(0).getMessage(), false);
                        }
                    } else {
                        AlertDialogBox("InventoryTransfer", "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }

    }

    public void FellingRegistrayionSync(int TreeFromLocation, String FellingSecID, int FellingRegID, String FellingRegUniqueID) {
        Common.FellingRegisterInputList.clear();
        Common.FellingRegisterInputList = mDBInternalHelper.getFellingRegInputWithFellingUniqID(FellingRegID);
        Common.FellingTreeDetailsList = mDBInternalHelper.getFellingRegInputWithTreeDetails(TreeFromLocation, FellingSecID, FellingRegUniqueID);
        if (Common.FellingRegisterInputList.size() > 0) {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                new GetFellingRegSyncAsynkTask().execute();
            }
        } else {
            AlertDialogBox("InventoryTransfer Sync", "Values are empty", false);
        }
    }

    public void FellingRegistrayionSyncALL() {
        try {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                for (int Row = 0; Row < FellingRegisterFilteredList.size(); Row++) {
                    if (FellingRegisterFilteredList.get(Row).getSyncStatus() == 0) {
                        Common.FellingRegisterInputList.clear();
                        Common.FellingRegisterInputList = mDBInternalHelper.getFellingRegInputWithFellingUniqID(FellingRegisterFilteredList.get(Row).getFellingRegID());
                        Common.FellingTreeDetailsList = mDBInternalHelper.getFellingRegInputWithTreeDetails(FellingRegisterFilteredList.get(Row).getLocationID(), FellingRegisterFilteredList.get(Row).getFellingSectionID(), FellingRegisterFilteredList.get(Row).getFellingRegistrationUniqueID());
                        if (Common.FellingRegisterInputList.size() > 0) {
                            new GetFellingRegSyncAsynkTask().execute();
                        } else {
                            AlertDialogBox("InventoryTransfer Sync", "Values are empty", false);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), ex.toString(), false);
        }
    }

    class GetFellingRegSyncAsynkTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.SyncStatusList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String MethodName = "FellingRegistration/";
            String SyncURL = ServiceURL.getServiceURL(ServiceURL.ControllorName, MethodName);
            fellingSyncModel = new FellingRegistrationSyncModel();
            fellingSyncModel.setFellingRegID(Common.FellingRegID);
            fellingSyncModel.setFellingRegistrationNumber(Common.FellingRegNo);
            fellingSyncModel.setLocationID(Common.TreeFromLocation);
            fellingSyncModel.setTotalCount(Common.SyncBarCodeCount);
            fellingSyncModel.setIMEI(Common.IMEI);
            fellingSyncModel.setFellingRegisterUniqueID(Common.FellingRegUniqueID);
            fellingSyncModel.setFellingSectionID(Common.FellingSectionId);
            fellingSyncModel.setFellingRegistrationDate(Common.SyncStartDateTime);
            fellingSyncModel.setEndDateTime(Common.SyncEndDateTime);
            fellingSyncModel.setUserID(Common.UserID);
            //fellingSyncModel.setVolume(Common.VolumeSum);
            fellingSyncModel.setFellingReg(Common.FellingRegisterInputList);
            fellingSyncModel.setFellingTreeDetails(Common.FellingTreeDetailsList);
            try {
                String SyncURLInfo = new Communicator().POST_Obect(SyncURL, fellingSyncModel);
                if (GwwException.GwwException(Common.HttpResponceCode) == true) {
                    if (SyncURLInfo != null) {
                        JSONObject jsonObj = new JSONObject(SyncURLInfo);
                        String SyncResponceStr = jsonObj.getString("Status");
                        if (SyncResponceStr != null) {
                            JSONArray SyncJsonAry = new JSONArray(SyncResponceStr);
                            for (int Sync_Index = 0; Sync_Index < SyncJsonAry.length(); Sync_Index++) {
                                suncStaModel = new Gson().fromJson(SyncJsonAry.getString(Sync_Index), SyncStatusModel.class);
                                Common.SyncStatusList.add(suncStaModel);
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
                        if (Common.SyncStatusList.get(0).getStatus() == 1) {
                            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                            Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                            boolean ListIdFlag = mDBInternalHelper.UpdateFellingRegSyncStatusFellingRegID(Common.SyncTime, 1, Common.FellingRegID);
                            if (Common.FellingRegSyncALL = false) {
                                if (ListIdFlag == true) {
                                    //Scanned Result Refresh
                                    GetFellingRegistrationList();
                                    AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), Common.SyncStatusList.get(0).getMessage(), true);
                                }
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), Common.SyncStatusList.get(0).getMessage(), false);
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }

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
                Common.LoadedIndex = Integer.parseInt(mDBExternalHelper.getLoadedRowID());
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
            Common.AgencyDetailsExSync.clear();
            Common.DriverDetailsExSync.clear();
            Common.TruckDetailsExSync.clear();
            Common.TransferLogDetailsExSync.clear();
            Common.fellingRegisterSync.clear();
            Common.fellingSectionSync.clear();
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
            externalDBmodel.setLoadedIndex(Common.LoadedIndex);
            try {
                String SyncURLInfo = new Communicator().POST_Obect(SyncURL, externalDBmodel);
                if (GwwException.GwwException(Common.HttpResponceCode) == true) {
                    if (SyncURLInfo != null) {
                        JSONObject jsonObj = new JSONObject(SyncURLInfo);
                        String AgencyResStr = jsonObj.getString("AgencyDetails");
                        if (AgencyResStr != null) {
                            JSONArray TransLogResJsonAry = new JSONArray(AgencyResStr);
                            for (int AgencySync_Index = 0; AgencySync_Index < TransLogResJsonAry.length(); AgencySync_Index++) {
                                externalagencyModel = new Gson().fromJson(TransLogResJsonAry.getString(AgencySync_Index), AgencyDetailsModel.class);
                                Common.AgencyDetailsExSync.add(externalagencyModel);
                                Common.IsExternalSync = true;
                            }
                        }
                        String DriverLogResStr = jsonObj.getString("DriverDetails");
                        if (DriverLogResStr != null) {
                            JSONArray DriverLogResJsonAry = new JSONArray(DriverLogResStr);
                            for (int DriverSync_Index = 0; DriverSync_Index < DriverLogResJsonAry.length(); DriverSync_Index++) {
                                externalDrivertModel = new Gson().fromJson(DriverLogResJsonAry.getString(DriverSync_Index), DriverDetailsModel.class);
                                Common.DriverDetailsExSync.add(externalDrivertModel);
                                Common.IsExternalSync = true;
                            }
                        }

                        String TransLogResStr = jsonObj.getString("TransferLogDetils");
                        if (TransLogResStr != null) {
                            JSONArray TransLogResJsonAry = new JSONArray(TransLogResStr);
                            for (int TransSync_Index = 0; TransSync_Index < TransLogResJsonAry.length(); TransSync_Index++) {
                                externalTransferLogs = new Gson().fromJson(TransLogResJsonAry.getString(TransSync_Index), TransferLogDetailsExModel.class);
                                Common.TransferLogDetailsExSync.add(externalTransferLogs);
                                Common.IsExternalSync = true;
                            }

                        }
                        String TruckDetStr = jsonObj.getString("TruckDetails");
                        if (TruckDetStr != null) {
                            JSONArray TruckDetJsonAry = new JSONArray(TruckDetStr);
                            for (int TruckSync_Index = 0; TruckSync_Index < TruckDetJsonAry.length(); TruckSync_Index++) {
                                externalTruckDetailsLogs = new Gson().fromJson(TruckDetJsonAry.getString(TruckSync_Index), TruckDetailsModel.class);
                                Common.TruckDetailsExSync.add(externalTruckDetailsLogs);
                                Common.IsExternalSync = true;
                            }

                        }
                        //  3-9-19
                        //  felling Registration....
                        String fellingRegisterStr = jsonObj.getString("FellingRegister");
                        if (fellingRegisterStr != null) {
                            JSONArray fellingRegisterJsonAry = new JSONArray(fellingRegisterStr);
                            for (int fellingRegisterSync_Index = 0; fellingRegisterSync_Index < fellingRegisterJsonAry.length(); fellingRegisterSync_Index++) {
                                externalfellingRegisterDetails = new Gson().fromJson(fellingRegisterJsonAry.getString(fellingRegisterSync_Index), FellingRegisterModel.class);
                                Common.fellingRegisterSync.add(externalfellingRegisterDetails);
                                Common.IsExternalSync = true;
                            }

                        }

                        //  fellingSection
                        String fellingSectionStr = jsonObj.getString("FellingSection");
                        if (fellingSectionStr != null) {
                            JSONArray fellingSectionJsonAry = new JSONArray(fellingSectionStr);
                            for (int fellingSectionSync_Index = 0; fellingSectionSync_Index < fellingSectionJsonAry.length(); fellingSectionSync_Index++) {
                                externalfellingSectionDetails = new Gson().fromJson(fellingSectionJsonAry.getString(fellingSectionSync_Index), FellingSectionModel.class);
                                Common.fellingSectionSync.add(externalfellingSectionDetails);
                                Common.IsExternalSync = true;
                            }
                        }
                        Common.IsConnected = true;
                    } else {
                        Common.IsConnected = false;
                        Common.InventoryErrorMsg = "Values are updated already";
                    }
                } else {
                    JSONObject jsonObj = new JSONObject(SyncURLInfo);
                    Common.InventoryErrorMsg = jsonObj.getString("Message");
                    Common.IsConnected = false;
                    Common.AuthorizationFlag = true;
                }
            } catch (Exception e) {
                Common.IsConnected = false;
                Common.InventoryErrorMsg = CommonMessage(R.string.NoValueFound) + "--" + e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Common.IsConnected == true) {
                        if (Common.IsExternalSync == true) {
                            Common.IsExternalSync = false;

                            InsertValuesInExternalDataBase();
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ExtenalDBHead), "No values for Update", true);
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.ExtenalDBHead), Common.InventoryErrorMsg, false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }

    }

    public void InsertValuesInExternalDataBase() {
        try {
            for (AgencyDetailsModel agencySync : Common.AgencyDetailsExSync) {
                mDBExternalHelper.insertAgencyDetails(
                        agencySync.getRowID(),
                        agencySync.getAgencyId(),
                        agencySync.getAgencyName(),
                        agencySync.getAddress()
                );
            }

            for (DriverDetailsModel DriverSync : Common.DriverDetailsExSync) {
                mDBExternalHelper.insertDriverDetails(
                        DriverSync.getRowID(),
                        DriverSync.getTruckDriverId(),
                        DriverSync.getDriverLicenseNo(),
                        DriverSync.getDriverName()
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
            for (TruckDetailsModel TruckSync : Common.TruckDetailsExSync) {
                mDBExternalHelper.insertTruckDetails(
                        TruckSync.getRowID(),
                        TruckSync.getTransportId(),
                        TruckSync.getTruckLicensePlateNo(),
                        TruckSync.getDescription()
                );
            }
            //4-9-19
            //FellingRegister
            for (FellingRegisterModel fellingRegisterSync : Common.fellingRegisterSync) {
                mDBExternalHelper.insertFellingRegister(
                        fellingRegisterSync.getRowID(),
                        fellingRegisterSync.getConcessionId(),
                        fellingRegisterSync.getFellingSectionId(),
                        fellingRegisterSync.getFellingSectionNumber(),
                        fellingRegisterSync.getPlotId(),
                        fellingRegisterSync.getTreeNumber(),
                        fellingRegisterSync.getWoodSpeciesId(),
                        fellingRegisterSync.getConcessionName(),
                        fellingRegisterSync.getFellingCode(),
                        fellingRegisterSync.getPlotNumber(),
                        fellingRegisterSync.getWoodSpeciesCode());
            }
            //FellingSection
            for (FellingSectionModel fellingSectionSync : Common.fellingSectionSync) {
                mDBExternalHelper.insertFellingSection(
                        fellingSectionSync.getRowID(),
                        fellingSectionSync.getConcessionId(),
                        fellingSectionSync.getFellingSectionNumber(),
                        fellingSectionSync.getLocationType(),
                        fellingSectionSync.getConcessionName(),
                        fellingSectionSync.getFellingSectionId(),
                        fellingSectionSync.getFellingCode()
                );
            }
            ExportDatabaseToStorage();
            AlertDialogBox(CommonMessage(R.string.ExtenalDBHead), "External DataBase Updated Sucessfully", true);
        } catch (Exception ex) {
            Log.d("AgencyCount", "2.1>>>>>" + ex.toString());
        }
    }

    private void RefreshActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    public void DeleteTransferCountScannedList(int ListID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteInventoryCountListID(ListID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteInventoryCountScanned(ListID);
                if (DeleteScannedFlag == true) {
                    GetInventoryCountList();
                }
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteTransferCountandTransferScannedList", ex.toString(), false);
        }
    }

    public void DeleteTransferListandTransferScannedList(int transferID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteInventoryTransferListID(transferID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteInventoryTransferScanned(transferID);
                if (DeleteScannedFlag == true) {
                    GetInventoryTransferList();
                }
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
        }
    }

    public void DeleteReceivedListannReceivedScannedList(int receivedID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteInventoryReceivedListID(receivedID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteInventoryReceivedScanned(receivedID);
                if (DeleteScannedFlag == true) {
                    GetInventoryReceivedList();
                }
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteReceivedListannReceivedScannedList", ex.toString(), false);
        }
    }

    public void DeleteFellingListannFellingScannedList(int fellingID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteFellingRegistrationListID(fellingID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteFellingRegistrationScanned(fellingID);
                if (DeleteScannedFlag == true) {
                    GetFellingRegistrationList();
                }
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteReceivedListannReceivedScannedList", ex.toString(), false);
        }
    }

    public double FellingRegisTotalVolume(ArrayList<FellingRegisterResultModel> TotalFRScannedList) {
        double TotVolume = 0.00;
        for (FellingRegisterResultModel inventoTransScanModel : TotalFRScannedList) {
            TotVolume = TotVolume + inventoTransScanModel.getVolume();
        }
        return TotVolume;
    }

    public double TransferTotalVolume(ArrayList<InventoryTransferScannedResultModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (InventoryTransferScannedResultModel inventoTransScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(inventoTransScanModel.getVolume());
        }
        return TotVolume;
    }

    public double ReceivedTotalVolume(ArrayList<InventoryReceivedModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (InventoryReceivedModel inventoreceScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(inventoreceScanModel.getVolume());
        }
        return TotVolume;
    }

    public void InventorCountAcivityCall() {
        Intent _gwwIntent = new Intent(InventoryActivity.this, InventoryCountActivity.class);
        startActivity(_gwwIntent);
    }

    public void InventorReceivedAcivityCall() {
        Intent _gwIntent = new Intent(InventoryActivity.this, InventoryReceivedActivity.class);
        startActivity(_gwIntent);
    }

    public void InventorTransferAcivityCall() {
        Intent _gwIntent = new Intent(InventoryActivity.this, InventoryTransferActivity.class);
        startActivity(_gwIntent);
    }

    public void InventorFellingRegAcivityCall() {
        Intent _gwIntent = new Intent(InventoryActivity.this, FellingRegistrationActivity.class);
        startActivity(_gwIntent);
    }

    public void onPictureTaken(int format, int width, int height, byte[] abData, BarCodeReader reader) {
        if (image == null)
            return;

        // display snapshot
        Bitmap bmSnap = BitmapFactory.decodeByteArray(abData, 0, abData.length);
        if (bmSnap == null) {
            dspErr("OnPictureTaken: no bitmap");
            return;
        }
        image.setImageBitmap(rotated(bmSnap));

        // Save snapshot to the SD card
        if (saveSnapshot) {
            String snapFmt = "bin";
            switch (bcr.getNumParameter(BarCodeReader.ParamNum.IMG_FILE_FORMAT)) {
                case BarCodeReader.ParamVal.IMG_FORMAT_BMP:
                    snapFmt = "bmp";
                    break;

                case BarCodeReader.ParamVal.IMG_FORMAT_JPEG:
                    snapFmt = "jpg";
                    break;

                case BarCodeReader.ParamVal.IMG_FORMAT_TIFF:
                    snapFmt = "tif";
                    break;
            }

            File filFSpec = null;
            try {
                String strFile = String.format("se4500_img_%d.%s", snapNum, snapFmt);
                File filRoot = Environment.getExternalStorageDirectory();
                File filPath = new File(filRoot.getAbsolutePath() + "/DCIM/Camera");
                filPath.mkdirs();
                filFSpec = new File(filPath, strFile);
                FileOutputStream fos = new FileOutputStream(filFSpec);
                fos.write(abData);
                fos.close();

                ++snapNum;
            } catch (Throwable thrw) {
                dspErr("Create '" + filFSpec.getAbsolutePath() + "' failed");
                dspErr("Error=" + thrw.getMessage());
            }
        }
    }

    // display status string
    public void ScannedStatus(String s) {
        Log.v(TAG, "ScannedStatus " + s);
        //ScanValueETxT.setText(s);
    }

    // display status resource id
    public void ScannedStatus(int id) {
        //ScanValueETxT.setText(id);
    }

    // display error msg
    private void dspErr(String s) {
        //ScanValueETxT.setText("ERROR" + s);
    }

    // display status string☺
    public void ScannedResult(String scannedValue) {
        String[] TransferDetails = scannedValue.split("--");
        if (TransferDetails.length > 9) {
            ScanedQRDetails(TransferDetails);
        }
    }

    public void ScanedQRDetails(String[] ScannedArryValue) {
        try {
            beepsound.start();
            Common.TransferUniqueID = ScannedArryValue[0];
            //Common.TransferID = Integer.parseInt(ScannedArryValue[0]);
            submitOldTransEDT.setText(ScannedArryValue[0]);
            Common.VBB_Number = ScannedArryValue[1];
            //Common.ReceivedDate = ScannedArryValue[2];
            Common.FromTransLocID = Integer.parseInt(ScannedArryValue[3]);
            Common.ToLocaTransID = Integer.parseInt(ScannedArryValue[4]);
            /*Common.LocationList.clear();
            Common.LocationList = mDBExternalHelper.getToLocations();
            for (LocationsModel locationMod : Common.LocationList) {
                if (Common.ToLocReceivedID == locationMod.getToLocationId()) {
                    Common.ToLocationName = locationMod.getLocation();
                    ToLocationTxT.setText(Common.ToLocationName);
                }
            }*/
            Common.TransferAgencyID = Integer.parseInt(ScannedArryValue[5]);
            Common.DriverID = Integer.parseInt(ScannedArryValue[6]);
            Common.TransportId = Integer.parseInt(ScannedArryValue[7]);
            Common.TransportTypeId = Integer.parseInt(ScannedArryValue[8]);
            //Common.FellingSectionId = ScannedArryValue[9];
            //Common.Count = Integer.parseInt(ScannedArryValue[10]);
            //Common.VolumeSum = Double.parseDouble(ScannedArryValue[11]);
            Common.LoadedTypeID = Integer.parseInt(ScannedArryValue[12]);
            Common.SyncTime = "";
            Common.VolumeSum = 0.0;
            //Common.TransferUniqueID = submitOldTransEDT.getText().toString();
            //Common.VBB_Number = submitVVPEDT.getText().toString();
            Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            boolean DuplicateFlag = mDBInternalHelper.getInventoryTransferUniqueIdDuplicateCheck(Common.TransferUniqueID);
            if (DuplicateFlag == true) {
                AlertDialogBox("Inventory Transfer List", "This transfer id already exists", false);
                return;
            }
            if (Common.VBB_Number.length() > 0) {
                boolean VBBDuplicateFlag = mDBInternalHelper.getInventoryTransferIdListDuplicateCheck(Integer.parseInt(Common.VBB_Number));
                if (VBBDuplicateFlag == true) {
                    AlertDialogBox("Inventory Transfer List", "please add diff VBB Number", false);
                    return;
                }
            }
            boolean ListIdFlag = mDBInternalHelper.insertInventoryTransferID(Common.VBB_Number, Common.IMEI, Common.ToLocaTransID, Common.StartDate, Common.EndDate,
                    Common.FromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TrucklicensePlateNo, Common.UserID, Common.Count,
                    Common.SyncStatus, Common.SyncTime, Common.Volume, 1, Common.TransferUniqueID);

            if (ListIdFlag == true) {
                Common.InventoryTransferList = mDBInternalHelper.getInventoryTransferIdList();
                if (Common.InventoryTransferList.size() > 0) {
                    Common.TransferID = Integer.parseInt(mDBInternalHelper.getLastTransferID());
                    // Update values into TransferID
                    boolean TransferIDFlag = mDBInternalHelper.UpdateInventoryTransferUniqueID(Common.TransferID, Common.TransferUniqueID);
                }
            }
            Common.IsTransferEditListFlag = false;
            Common.IsEditorViewFlag = true;
            InventorTransferAcivityCall();
        } catch (Exception ee) {
            AlertDialogBox("ScanQR Details", ee.toString(), false);
        }
    }

    public void onPreviewFrame(byte[] data, BarCodeReader bcreader) {
    }

    public void onEvent(int event, int info, byte[] data, BarCodeReader reader) {
        switch (event) {
            case BarCodeReader.BCRDR_EVENT_SCAN_MODE_CHANGED:
                ++modechgEvents;
                ScannedStatus("Scan Mode Changed Event (#" + modechgEvents + ")");
                break;

            case BarCodeReader.BCRDR_EVENT_MOTION_DETECTED:
                ++motionEvents;
                ScannedStatus("Motion Detect Event (#" + motionEvents + ")");
                break;

            case BarCodeReader.BCRDR_EVENT_SCANNER_RESET:
                ScannedStatus("Reset Event");
                break;

            default:
                // process any other events here
                break;
        }
    }

    private Bitmap rotated(Bitmap bmSnap) {
        Matrix matrix = new Matrix();
        if (matrix != null) {
            matrix.postRotate(90);
            // create new bitmap from orig tranformed by matrix
            Bitmap bmr = Bitmap.createBitmap(bmSnap, 0, 0, bmSnap.getWidth(), bmSnap.getHeight(), matrix, true);
            if (bmr != null)
                return bmr;
        }

        return bmSnap;        //when all else fails
    }

    @Override
    public void henResult(String codeType, String context) {
    }

    @Override
    public void result(String content) {
    }

    // SurfaceHolder callbacks
    public void surfaceCreated(SurfaceHolder holder) {
        scanService.startViewFinder();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            //mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "ZHENGYI.WZY");
        }
        if (mWakeLock != null) {
            mWakeLock.acquire();
        }
    }

    public void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
        }
    }

/*    public void ExportDatabaseToStorage() {
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
    }*/

    public void PushDatabaseFromAppToInternalStorage() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyyHH:mm:ss");
            String dateTime = dateFormat.format(Calendar.getInstance().getTime());
            File sd = new File(directory_path);
            if (sd.exists() && sd.isDirectory()) {
                if (sd.canWrite()) {
                    String currentDBPath = "/data/data/" + getPackageName() + "/databases/GWWINTERNAL.db";
                    String backupDBPath = "GWWINTERNAL" + "_" + Common.DeviceName + "_" + dateTime + ".db";
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
            } else {
                Log.v("File Created", String.valueOf(sd.mkdirs()));
                AlertDialogBox("Internal Database", "No Folder!", false);
            }

        } catch (Exception e) {

        }
    }

    public void TransferDeleteDatasIFUserID_ONE(String ErrorMessage) {
        if (SignoutAlert != null && SignoutAlert.isShowing()) {
            return;
        }
        Signoutbuilder = new AlertDialog.Builder(InventoryActivity.this);
        Signoutbuilder.setMessage(ErrorMessage);
        Signoutbuilder.setCancelable(true);
        Signoutbuilder.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Signoutbuilder.setNegativeButton("Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeleteTransferListandTransferScannedList(Common.TransferID);
                        dialog.cancel();
                    }
                });

        SignoutAlert = Signoutbuilder.create();
        SignoutAlert.show();

    }

    public void ReceivedDeleteDatasIFUserID_ONE(String ErrorMessage) {
        if (SignoutAlert != null && SignoutAlert.isShowing()) {
            return;
        }
        Signoutbuilder = new AlertDialog.Builder(InventoryActivity.this);
        Signoutbuilder.setMessage(ErrorMessage);
        Signoutbuilder.setCancelable(true);
        Signoutbuilder.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Signoutbuilder.setNegativeButton("Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeleteReceivedListannReceivedScannedList(Common.ReceivedID);
                        dialog.cancel();
                    }
                });

        SignoutAlert = Signoutbuilder.create();
        SignoutAlert.show();

    }

    public void FellingRegSyncALL(String ErrorMessage) {
        if (SignoutAlert != null && SignoutAlert.isShowing()) {
            return;
        }
        Signoutbuilder = new AlertDialog.Builder(InventoryActivity.this);
        Signoutbuilder.setMessage(ErrorMessage);
        Signoutbuilder.setCancelable(true);
        Signoutbuilder.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Signoutbuilder.setNegativeButton("SyncAll",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FellingRegistrayionSyncALL();
                        dialog.cancel();
                    }
                });

        SignoutAlert = Signoutbuilder.create();
        SignoutAlert.show();

    }


    /* @Override
     public void onSelected(String item) {
         Toast.makeText(this, "Selected: " + item, Toast.LENGTH_LONG).show();
     }*/
    @Override
    public void onFellingFilterSelected(FellingSectionModel contact) {
        Toast.makeText(getApplicationContext(), "Selected: " + contact.getFellingSectionNumber() + ", " + contact.getFellingSectionId(), Toast.LENGTH_LONG).show();

    }

    private void ExportAllTables() {
        try {
            File file = new File(directory_path);
            if (!file.exists()) {
                Log.v("File Created", String.valueOf(file.mkdirs()));
            }
            sqliteToExcel = new SQLiteToExcel(getApplicationContext(), "GWWINTERNAL.db", directory_path);
            sqliteToExcel.exportAllTables("GWWINTERNAL_" + Common.DeviceName + ".xls", new SQLiteToExcel.ExportListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onCompleted(String filePath) {
                    AlertDialogBox("Export DB", "Successfully Exported!", true);
                }

                @Override
                public void onError(Exception e) {
                    AlertDialogBox("Export DB-Exce", e.toString(), false);
                }
            });
        } catch (Exception ex) {
            AlertDialogBox("Export DB", ex.toString(), false);
        }
    }

    private void ExportFellingListTable() {
        try {
            String FelingSectionID = "00";
            File file = new File(directory_path);
            if (!file.exists()) {
                Log.v("File Created", String.valueOf(file.mkdirs()));
            }
            if (FellingSectionSearchATV.getText().toString().length() > 0) {
                FelingSectionID = FellingSectionSearchATV.getText().toString();
            }
            String dest = directory_path + "FellingRegistration_" + FelingSectionID + "_" + Common.DeviceName + ".xls";
            try {
                for (int i = 0; i < Common.FellingRegExcelExportList.length; i++) {
                    if (i == 0) {
                        int index = myWorkBook.getSheetIndex(Common.FellingRegExcelExportList[i]);
                        if (index == -1) {
                            mySheet = myWorkBook.createSheet(Common.FellingRegExcelExportList[i]);
                        } else {
                            mySheet = myWorkBook.getSheetAt(index);
                            for (int Del_Row = 1; Del_Row <= mySheet.getLastRowNum(); Del_Row++) {
                                Row row = mySheet.getRow(Del_Row);
                                deleteRow(mySheet, row);
                            }
                        }
                        for (int Row = 0; Row < FellingRegisterFilteredList.size() + 1; Row++) {
                            if (Row == 0) {
                                excelLog(Row, 0, "FellingRegID");
                                excelLog(Row, 1, "FellingRegistrationNumber");
                                excelLog(Row, 2, "FellingRegistrationDate");
                                excelLog(Row, 3, "LocationID");
                                excelLog(Row, 4, "FellingSectionId");
                                excelLog(Row, 5, "EndDateTime");
                                excelLog(Row, 6, "TotalCount");
                                excelLog(Row, 7, "FellingRegistrationUniqueID");
                                excelLog(Row, 8, "SyncStatus");
                                excelLog(Row, 9, "SyncTime");
                                excelLog(Row, 10, "IsActive");
                                excelLog(Row, 11, "Volume");
                                excelLog(Row, 12, "UserID");
                                excelLog(Row, 13, "IMEI");
                            } else {
                                excelLog(Row, 0, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getFellingRegID()));
                                excelLog(Row, 1, FellingRegisterFilteredList.get(Row - 1).getFellingRegistrationNumber());
                                excelLog(Row, 2, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getFellingRegistrationDate()));
                                excelLog(Row, 3, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getLocationID()));
                                excelLog(Row, 4, FellingRegisterFilteredList.get(Row - 1).getFellingSectionID());
                                excelLog(Row, 5, FellingRegisterFilteredList.get(Row - 1).getEndDateTime());
                                excelLog(Row, 6, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getCount()));
                                excelLog(Row, 7, FellingRegisterFilteredList.get(Row - 1).getFellingRegistrationUniqueID());
                                excelLog(Row, 8, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getSyncStatus()));
                                excelLog(Row, 9, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getSyncTime()));
                                excelLog(Row, 10, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getIsActive()));
                                excelLog(Row, 11, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getVolume()));
                                excelLog(Row, 12, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getUserID()));
                                excelLog(Row, 13, FellingRegisterFilteredList.get(Row - 1).getIMEI());
                            }
                        }
                    } else if (i == 1) {
                        int index = myWorkBook.getSheetIndex(Common.FellingRegExcelExportList[i]);
                        if (index == -1) {
                            mySheet = myWorkBook.createSheet(Common.FellingRegExcelExportList[i]);
                        } else {
                            mySheet = myWorkBook.getSheetAt(index);
                            for (int Del_Row = 1; Del_Row <= mySheet.getLastRowNum(); Del_Row++) {
                                Row row = mySheet.getRow(Del_Row);
                                deleteRow(mySheet, row);
                            }
                        }
                        Common.FellingRegisterLogsExportDetails.clear();
                        StringBuilder AdvanceSearchStr = new StringBuilder();
                        for (int Row = 0; Row < FellingRegisterFilteredList.size(); Row++) {
                            try {
                                AdvanceSearchStr.append(FellingRegisterFilteredList.get(Row).getFellingRegID() + ",");
                            } catch (Exception ex) {
                                Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
                            }
                        }
                        Common.FellingRegisterLogsExportDetails = mDBInternalHelper.getFellingDetailsExportList(AdvanceSearchStr.toString().substring(0, AdvanceSearchStr.toString().length() - 1));
                        for (int Row = 0; Row < Common.FellingRegisterLogsExportDetails.size() + 1; Row++) {
                            if (Row == 0) {
                                excelLog(Row, 0, "FellingRegID");
                                excelLog(Row, 1, "LocationID");
                                excelLog(Row, 2, "FellingSectionId");
                                excelLog(Row, 3, "SbbLabel");
                                excelLog(Row, 4, "WoodSpieceID");
                                excelLog(Row, 5, "WoodSpieceCode");
                                excelLog(Row, 6, "TreeNumber");
                                excelLog(Row, 7, "Quality");
                                excelLog(Row, 8, "BarCode");
                                excelLog(Row, 9, "FellingRegistrationUniqueID");
                                excelLog(Row, 10, "UserID");
                                excelLog(Row, 11, "EntryMode");
                                excelLog(Row, 12, "IsNewTreeNumber");
                                excelLog(Row, 13, "IsWoodSpieceCode");
                                excelLog(Row, 14, "IsOldWoodSpieceCode");
                                excelLog(Row, 15, "Footer_1");
                                excelLog(Row, 16, "Footer_2");
                                excelLog(Row, 17, "Top_1");
                                excelLog(Row, 18, "Top_2");
                                excelLog(Row, 19, "Length");
                                excelLog(Row, 20, "NoteF");
                                excelLog(Row, 21, "NoteT");
                                excelLog(Row, 22, "NoteL");
                                excelLog(Row, 23, "Volume");
                                excelLog(Row, 24, "TreePartType");
                                excelLog(Row, 25, "IsActive");
                            } else {
                                excelLog(Row, 0, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getFellingRegID()));
                                excelLog(Row, 1, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getLocationID()));
                                excelLog(Row, 2, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getFellingSectionID()));
                                excelLog(Row, 3, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getSbbLabel()));
                                excelLog(Row, 4, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getWoodSpieceID()));
                                excelLog(Row, 5, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getWoodSpieceCode()));
                                excelLog(Row, 6, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getTreeNumber()));
                                excelLog(Row, 7, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getQuality()));
                                excelLog(Row, 8, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getBarCode()));
                                excelLog(Row, 9, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getFellingRegistrationUniqueID()));
                                excelLog(Row, 10, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getUserID()));
                                excelLog(Row, 11, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getEntryMode()));
                                excelLog(Row, 12, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getISNewTree()));
                                excelLog(Row, 13, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getIsWoodSpieceCode()));
                                excelLog(Row, 14, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getIsOldWoodSpieceCode()));
                                excelLog(Row, 15, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getFooter_1()));
                                excelLog(Row, 16, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getFooter_2()));
                                excelLog(Row, 17, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getTop_1()));
                                excelLog(Row, 18, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getTop_2()));
                                excelLog(Row, 19, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getLength()));
                                excelLog(Row, 20, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getNoteF()));
                                excelLog(Row, 21, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getNoteL()));
                                excelLog(Row, 22, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getNoteL()));
                                excelLog(Row, 23, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getVolume()));
                                excelLog(Row, 24, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getTreePartType()));
                                excelLog(Row, 25, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getIsActive()));
                            }
                        }
                    }
                }
                FileOutputStream out = new FileOutputStream(dest);
                myWorkBook.write(out);
                out.close();
                Toast.makeText(this, "Saved Sucessfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                AlertDialogBox("Export DB", e.toString(), false);
            }
        } catch (Exception ex) {
            AlertDialogBox("Export DB", ex.toString(), false);
        }
    }

    public static void deleteRow(HSSFSheet sheet, Row row) {
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum != 0 && lastRowNum > 0) {
            int rowIndex = row.getRowNum();
            Row removingRow = sheet.getRow(rowIndex);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
                System.out.println("Deleting.... ");
            }
        }
    }

/*
    private void createSheet(String table, HSSFSheet sheet) {
        HSSFRow rowA = sheet.createRow(0);
        ArrayList<String> columns = getColumns(table);
        int cellIndex = 0;
        for (int i = 0; i < columns.size(); i++) {
            String columnName = prettyNameMapping("" + columns.get(i));
            if (!excludeColumn(columnName)) {
                HSSFCell cellA = rowA.createCell(cellIndex);
                cellA.setCellValue(new HSSFRichTextString(columnName));
                cellIndex++;
            }
        }
        insertItemToSheet(table, sheet, columns);
    }
*/


    private static void excelLog(int row, int col, String value) {
        HSSFRow myRow = mySheet.getRow(row);
        if (myRow == null)
            myRow = mySheet.createRow(row);
        HSSFCell myCell = myRow.createCell(col);
        myCell.setCellValue(value);
    }
}
