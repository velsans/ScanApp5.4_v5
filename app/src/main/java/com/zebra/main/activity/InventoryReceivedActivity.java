package com.zebra.main.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.zebra.android.jb.Preference;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CpuUsageInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.tscdll.TSCActivity;
import com.zebra.R;
import com.zebra.adc.decoder.BarCodeReader;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.adapter.AgencyAdapter;
import com.zebra.main.adapter.ArrivalAdapter;
import com.zebra.main.adapter.DriverAdapter;
import com.zebra.main.adapter.FellingSectionAdapter;
import com.zebra.main.adapter.TransportAdapter;
import com.zebra.main.adapter.TruckAdapter;
import com.zebra.main.model.AdvanceSearchModel;
import com.zebra.main.model.ExternalDB.AgencyDetailsModel;
import com.zebra.main.model.ExternalDB.ConcessionNamesModel;
import com.zebra.main.model.ExternalDB.DriverDetailsModel;
import com.zebra.main.model.ExternalDB.LocationsModel;
import com.zebra.main.model.ExternalDB.TransportModesModel;
import com.zebra.main.model.ExternalDB.TruckDetailsModel;
import com.zebra.main.model.ExternalDB.FellingSectionModel;
import com.zebra.main.model.InvReceived.InventoryReceivedModel;
import com.zebra.main.sdl.SdlScanListener;
import com.zebra.main.sdl.SdlScanService;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.BlueTooth;
import com.zebra.utilities.BlutoothCommonClass;
import com.zebra.utilities.Common;
import com.zebra.utilities.GwwException;
import com.zebra.utilities.PrintSlipsClass;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class InventoryReceivedActivity extends Activity implements SdlScanListener {
    static final private boolean saveSnapshot = false;
    private static final String TAG = "InventoryReceived";
    AlertDialogManager alert = new AlertDialogManager();
    Intent service;
    private EditText ScanValueETxT = null, treeNoEDT, fellingIDEDT;
    String TreeNO, FellingID;
    private TextView TotalScannedCount, sbbLabel_TXT, NoValueFoundTxT, vbb_txt, ToLocationTxT, VolumeTXT, FellingSpinnerTxT, scanQRDetails, Received_TotalCheckedCountTXT;
    AutoCompleteTextView FromLocationATXT, DriverATXT, AgencyATXT, TruckDetialsATXT;
    private LinearLayout advanceSearchLAY, EditOptionFlagLay, EditOptionFlagLay2;
    private ImageView image = null, scanQRBarCodeDetails, AS_NovalueFound;
    EditText TransferIDAutoTXT;
    // BarCodeReader specifics
    private BarCodeReader bcr = null;
    private PowerManager.WakeLock mWakeLock = null;
    private int motionEvents = 0, modechgEvents = 0, snapNum = 0;
    boolean bind = false;
    private SdlScanService scanService;
    private InventoryReceivedAdapter receivedadapter;
    private RecyclerView ScannedResultLV, advanceSearchRLV;
    Spinner FromLocationSpin, TransportAgencySpin, TruckPlateNumberSpin, FellingSecSpinner, DriverSpin;
    ListView TransferMode_LV;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;
    private static HSSFWorkbook myWorkBook = new HSSFWorkbook();
    private static HSSFSheet mySheet = myWorkBook.createSheet();
    MediaPlayer beepsound, wronBuzzer;
    LinearLayoutManager horizontalLayoutManager;
    TransportAdapterInside transportAdapter;
    ArrivalAdapter arrivalAdapter;
    TruckAdapter truckAdapter;
    AgencyAdapter agencyAdapter;
    DriverAdapter driverAdapter;
    ArrayAdapter<String> transIDadapter;
    FellingSectionAdapter fellingSectionAdapter;
    AdvanceSearchResultAdapter advancedSearchadapter;
    AlertDialog.Builder Removebuilder = null;
    AlertDialog RemoveAlert = null;
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2, LocationTypeID;
    TSCActivity tsc = new TSCActivity();
    PrintSlipsClass printSlip;
    FloatingActionButton ReceivedScanBtn;
    StringtreeNoadapter StringtreeNoadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventoryreceived);
        Initialization();
        //ImportDataBaseFromInternalStorage();
        mDBExternalHelper = new ExternalDataBaseHelperClass(InventoryReceivedActivity.this);
        mDBInternalHelper = new InternalDataBaseHelperClass(InventoryReceivedActivity.this);
        printSlip = new PrintSlipsClass(InventoryReceivedActivity.this);
        ViewReceivedList(Common.IsEditorViewFlag);
        getToLocationsforSpinner();
        gettruckforSpinner();
        getagencyforSpinner();
        getdriverforSpinner();
        gettransModeforView();
        Click_Listener();
        HideKeyboard();
        //enableSwipeToDeleteAndUndo();
        GetReceivedScannedResultList();
        if (Common.IsReceivedEditListFlag == false) {
            //Common.TransferIDsList = mDBInternalHelper.getTransferIDsList(Common.ReceivedID);
           /* Common.LocationList.clear();
            Common.LocationList = mDBExternalHelper.getToLocations();
            for (LocationsModel locationMod : Common.LocationList) {
                if (Common.ToLocReceivedID == locationMod.getToLocationId()) {
                    ToLocationTxT.setText(locationMod.getLocation());
                }
            }*/
            ToLocationTxT.setText(Common.ToLocationName);
            QrScanDetails();
            TransferIDAutoTXT.setText(Common.TransferReciveUniqueID);
        }
        //TransferIDList(Common.TransferIDsList);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            //UpdateReceivedIDList();
            Log.v(TAG, "onStop");
        } catch (Exception Ex) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            //UpdateReceivedIDList();
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
        Log.v(TAG, "onDestroy");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_F9)) {
            ScanValueETxT.setText("");
            Common.ScanMode = true;
            scanService.doDecode();
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            String Message = "";
            if (Integer.parseInt(TotalScannedCount.getText().toString()) > Integer.parseInt(Received_TotalCheckedCountTXT.getText().toString())) {
                Message = "Still some SBBLabel Is not Checked,Are you sure you want to close? ";
            } else {
                Message = "Are you sure you want to close?";
            }
            Signout(Message);
        }
        return true;
    }

  /*  public void TransferIDList(ArrayList<String> TransferIDSList) {
        TransferIDAutoTXT.setText("");
        if (TransferIDSList.size() > 0) {
            Common.TransferIDsStringList = TransferIDSList.toArray(new String[0]);
            transIDadapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, Common.TransferIDsStringList);
            transIDadapter.notifyDataSetChanged();
            TransferIDAutoTXT.setAdapter(transIDadapter);
            Common.TransferReciveUniqueID = Common.TransferIDsStringList[0];
            TransferIDAutoTXT.setText(Common.TransferReciveUniqueID);

        }
    }*/

    private void Initialization() {
        ReceivedScanBtn = findViewById(R.id.Received_ScanBTN);
        findViewById(R.id.Received_ScanBTN).setOnClickListener(mDecodeListener);
        findViewById(R.id.Received_EnterIMG).setOnClickListener(mScannerListener);
        findViewById(R.id.Received_close).setOnClickListener(mCloseActivityListener);
        findViewById(R.id.ReceivedNXTtxt).setOnClickListener(mAddReceivedIDListener);
        findViewById(R.id.Received_Print).setOnClickListener(mPrintListener);

        ScanValueETxT = findViewById(R.id.Received_scanValueEDTXT);
        TotalScannedCount = findViewById(R.id.Received_TotalScannedCount);
        ScannedResultLV = findViewById(R.id.Received_ListView);

        //ToLocationSpin = findViewById(R.id.tolocation_spinner);
        FromLocationSpin = findViewById(R.id.fromlocation_spinner);
        DriverSpin = findViewById(R.id.driver_sppinner);
        TransportAgencySpin = findViewById(R.id.transagency_spinner);
        TruckPlateNumberSpin = findViewById(R.id.truckplateumber_spinner);
        FellingSecSpinner = findViewById(R.id.fellingsection_spinner);
        TransferMode_LV = findViewById(R.id.ReceivedMode_LV);
        vbb_txt = findViewById(R.id.VBB_numberTXT);
        ToLocationTxT = findViewById(R.id.toLocationTXT);
        VolumeTXT = findViewById(R.id.TotalVolumeReceived);
        FellingSpinnerTxT = findViewById(R.id.fellingSpinnerTxT);

        scanQRDetails = findViewById(R.id.Received_scanQRDetails);
        scanQRBarCodeDetails = findViewById(R.id.Received_scanQRBarCode);

        findViewById(R.id.Received_scanQRDetails).setOnClickListener(mScannerQRCodeDetailListener);
        findViewById(R.id.Received_scanQRBarCode).setOnClickListener(mScannerQRBarCodeDetailListener);

        NoValueFoundTxT = findViewById(R.id.NovalueFound);
        //TransferIDTXT = findViewById(R.id.transferIDTXT);
        Received_TotalCheckedCountTXT = findViewById(R.id.Received_TotalCheckedCount);
        TransferIDAutoTXT = findViewById(R.id.transferIDAutoTXT);
        findViewById(R.id.addTransferIDTXT).setOnClickListener(mAddTransferIDListener);
        EditOptionFlagLay = findViewById(R.id.received_editLayout);
        EditOptionFlagLay2 = findViewById(R.id.transfer_editLayout2);

        findViewById(R.id.advancedsearchTXT).setOnClickListener(as_OpenrListener);
        findViewById(R.id.as_closeActivity).setOnClickListener(as_mCloseActivityListener);
        findViewById(R.id.as_scannerList).setOnClickListener(as_mScannerListener);
        findViewById(R.id.as_NovalueFound).setOnClickListener(as_mAddListener);
        advanceSearchRLV = findViewById(R.id.as_scan_lv);


        treeNoEDT = findViewById(R.id.as_treenoEDT);
        fellingIDEDT = findViewById(R.id.as_fellingEDT);

        advanceSearchLAY = findViewById(R.id.advancesearchLAY);
        advanceSearchLAY.setVisibility(View.GONE);
        sbbLabel_TXT = findViewById(R.id.sbbLabel_TXT);
        NoValueFoundTxT = findViewById(R.id.NovalueFound);
        AS_NovalueFound = findViewById(R.id.as_NovalueFound);
        AS_NovalueFound.setVisibility(View.GONE);

        FromLocationATXT = findViewById(R.id.fromlocation_ATxT);
        DriverATXT = findViewById(R.id.driver_ATxT);
        AgencyATXT = findViewById(R.id.transagency_ATxT);
        TruckDetialsATXT = findViewById(R.id.truckplateumber_ATxT);
    }

    public void ViewReceivedList(boolean EdtFlag) {
        if (EdtFlag == false) {
            EditOptionFlagLay.setVisibility(View.VISIBLE);
            ReceivedScanBtn.setEnabled(false);
            findViewById(R.id.ReceivedNXTtxt).setEnabled(false);
            findViewById(R.id.advancedsearchTXT).setEnabled(false);
            ScanValueETxT.setEnabled(false);
            findViewById(R.id.Received_EnterIMG).setEnabled(false);
            findViewById(R.id.Received_Print).setEnabled(false);
            EditOptionFlagLay2.setVisibility(View.VISIBLE);
        } else {
            EditOptionFlagLay.setVisibility(View.GONE);
            ReceivedScanBtn.setEnabled(true);
            findViewById(R.id.ReceivedNXTtxt).setEnabled(true);
            findViewById(R.id.advancedsearchTXT).setEnabled(true);
            ScanValueETxT.setEnabled(true);
            findViewById(R.id.Received_EnterIMG).setEnabled(true);
            findViewById(R.id.Received_Print).setEnabled(true);
            EditOptionFlagLay2.setVisibility(View.GONE);
        }
    }

    public void Signout(String ErrorMessage) {
        if (Common.IsEditorViewFlag == false) {
            InventoryActivty();
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(ErrorMessage);
            builder1.setCancelable(true);
            builder1.setPositiveButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder1.setNegativeButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                UpdateReceivedIDList();
                                InventoryActivty();
                            } catch (Exception ex) {
                                AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
                            }
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private void getToLocationsforSpinner() {
        try {
            // FromLocation Name from FromLocationID
            Common.ConcessionList.clear();
            Common.ConcessionList = mDBExternalHelper.getConcessionList();
            if (Common.ConcessionList.size() > 0) {
                arrivalAdapter = new ArrivalAdapter(getApplicationContext(), Common.ConcessionList);
                FromLocationSpin.setAdapter(arrivalAdapter);
            }
            Common.LocationList.clear();
            Common.LocationList = mDBExternalHelper.getToLocations();
            for (LocationsModel locationMod : Common.LocationList) {
                if (Common.ToLocReceivedID == locationMod.getToLocationId()) {
                    Common.ToLocationName = locationMod.getLocation();
                    ToLocationTxT.setText(Common.ToLocationName);
                }
            }
            // Felling Section Spinner
            LocationTypeID = Common.ConcessionList.get(0).getLocationType();
            if (LocationTypeID == 1) {
                Common.FellingSectionList.clear();
                Common.FellingSectionList = mDBExternalHelper.getFellingSectionDetails(Common.RecFromLocationID);
                fellingSectionAdapter = new FellingSectionAdapter(getApplicationContext(), Common.FellingSectionList);
                FellingSecSpinner.setAdapter(fellingSectionAdapter);
                FellingSecSpinner.setVisibility(View.VISIBLE);
            } else {
                FellingSecSpinner.setVisibility(View.GONE);
            }

            Common.ConcessionListStringList = new String[Common.ConcessionList.size()];
            for (int i = 0; i < Common.ConcessionList.size(); i++) {
                Common.ConcessionListStringList[i] = Common.ConcessionList.get(i).getConcessionName();
            }
            StringtreeNoadapter = new StringtreeNoadapter(this,
                    android.R.layout.simple_dropdown_item_1line, Common.ConcessionListStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            FromLocationATXT.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
            AlertDialogBox("FromLocation Spinner", ex.toString(), false);
        }
    }

    private void gettruckforSpinner() {
        try {
            Common.TruckDeatilsList.clear();
            Common.TruckDeatilsList = mDBExternalHelper.getAllTruckDetails();
            truckAdapter = new TruckAdapter(getApplicationContext(), Common.TruckDeatilsList);
            TruckPlateNumberSpin.setAdapter(truckAdapter);

            Common.TruckDetialsStringList = new String[Common.TruckDeatilsList.size()];
            for (int i = 0; i < Common.TruckDeatilsList.size(); i++) {
                Common.TruckDetialsStringList[i] = Common.TruckDeatilsList.get(i).getTruckLicensePlateNo();
            }
            //Common.FellingRegTreeNoStringList = Common.FellingRegTreeFilterList.toArray(new String[0]);
            StringtreeNoadapter = new StringtreeNoadapter(this,
                    android.R.layout.simple_dropdown_item_1line, Common.TruckDetialsStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            TruckDetialsATXT.setAdapter(StringtreeNoadapter);

        } catch (Exception ex) {
        }
    }

    private void getagencyforSpinner() {
        try {
            Common.TransportAgencyList.clear();
            Common.TransportAgencyList = mDBExternalHelper.getAllAgencyDetails();
            agencyAdapter = new AgencyAdapter(getApplicationContext(), Common.TransportAgencyList);
            TransportAgencySpin.setAdapter(agencyAdapter);

            Common.AgencyDetailsStringList = new String[Common.TransportAgencyList.size()];
            for (int i = 0; i < Common.TransportAgencyList.size(); i++) {
                Common.AgencyDetailsStringList[i] = Common.TransportAgencyList.get(i).getAgencyName();
            }
            StringtreeNoadapter = new StringtreeNoadapter(this,
                    android.R.layout.simple_dropdown_item_1line, Common.AgencyDetailsStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            AgencyATXT.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
        }
    }

    private void getdriverforSpinner() {
        try {
            Common.DriverList.clear();
            Common.DriverList = mDBExternalHelper.getAllDriverDetails();
            driverAdapter = new DriverAdapter(getApplicationContext(), Common.DriverList);
            DriverSpin.setAdapter(driverAdapter);

            Common.DriverListStringList = new String[Common.DriverList.size()];
            for (int i = 0; i < Common.DriverList.size(); i++) {
                Common.DriverListStringList[i] = Common.DriverList.get(i).getDriverName();
            }
            //Common.FellingRegTreeNoStringList = Common.FellingRegTreeFilterList.toArray(new String[0]);
            StringtreeNoadapter = new StringtreeNoadapter(this,
                    android.R.layout.simple_dropdown_item_1line, Common.DriverListStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            DriverATXT.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
        }
    }

    private void gettransModeforView() {
        try {
            Common.TransportModeList.clear();
            Common.TransportModeList = mDBExternalHelper.getAllTransportModeDetails();
            transportAdapter = new TransportAdapterInside(getApplicationContext(), Common.TransportModeList);
            TransferMode_LV.setAdapter(transportAdapter);
        } catch (Exception ex) {
        }
    }

    public void Click_Listener() {

        ScanValueETxT.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Common.ScannedEditTXTFlag = true;
                return false;
            }
        });

        ScanValueETxT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == Common.SBBlenght) {
                } else {
                    if (Common.ScannedEditTXTFlag == true) {
                        //AlertDialogBox("Barcode Length", CommonMessage(R.string.BarCodeLenghtMsg), false);
                        ScanValueETxT.setError(CommonMessage(R.string.BarCodeLenghtMsg));
                    }
                }
            }
        });

        FromLocationSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Common.RecFromLocationID = Common.ConcessionList.get(position).getFromLocationId();
                    Common.RecFromLocationname = Common.ConcessionList.get(position).getConcessionName();
                } catch (Exception ex) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FellingSecSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Common.FellingSectionId = Common.FellingSectionList.get(position).getFellingSectionId();
                    Common.FellingSectionName = String.valueOf(Common.FellingSectionList.get(position).getFellingSectionNumber());
                } catch (Exception ex) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TransportAgencySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Common.TransferAgencyID = Common.TransportAgencyList.get(position).getAgencyId();
                Common.AgencyName = Common.TransportAgencyList.get(position).getAgencyName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TruckPlateNumberSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Common.TrucklicensePlateNo = Common.TruckDeatilsList.get(position).getTruckLicensePlateNo();
                Common.TransportId = Common.TruckDeatilsList.get(position).getTransportId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DriverSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Common.DriverID = Common.DriverList.get(position).getTruckDriverId();
                Common.DriverName = Common.DriverList.get(position).getDriverName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
     /*  DriverSpin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Common.DriverID = Common.DriverList.get(position).getTruckDriverId();
               Common.DriverName = Common.DriverList.get(position).getDriverName();
           }
       });*/

        TransferMode_LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Common.TransportTypeId = Common.TransportModeList.get(position).getTransportTypeId();
                Common.TransportMode = Common.TransportModeList.get(position).getTransportMode();
            }
        });

        TransferIDAutoTXT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    // Checked List Size For Change Transfer ID
                    if (Common.InventoryReceivedScannedResultList.size() > 0) {
                       /*String str = s.toString();
                        str = str.substring(0, str.length() - 1);
                        TransferIDAutoTXT.setText(str);*/
                        //wronBuzzer.start();
                        AlertDialogBox("Scanned Value", "Donnot Change Transfer ID while Scanning, if you change please give correct Transport Id", false);
                        return;
                    } else {
                      if (s.toString().length() > 10) {
                            Common.TransferReciveUniqueID = s.toString();
                            int TrUniIDInd = (s.toString().length() - 10);
                            Common.ReceivedTransferID = s.toString().substring(s.toString().length() - TrUniIDInd);
                        } else {
                            Common.TransferReciveUniqueID = s.toString();
                            Common.ReceivedTransferID = s.toString();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

     /*   TransferIDAutoTXT.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    TransferIDAutoTXT.showDropDown();
                    TransferIDAutoTXT.requestFocus();
                } catch (Exception ex) {

                }
                return false;
            }
        });

        TransferIDAutoTXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Common.TransferID = Integer.parseInt(Common.TransferIDsStringList[position]);
                Common.TransferReciveUniqueID = Common.TransferIDsStringList[position];
            }
        });
*/
        EditOptionFlagLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogBox(CommonMessage(R.string.ReceivedHead), "Can not edit Or delete after Synced", false);
            }
        });

        EditOptionFlagLay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogBox(CommonMessage(R.string.ReceivedHead), "Can not edit Or delete after Synced", false);
            }
        });

        /*AutoComplete*/
        FromLocationATXT.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FromLocationATXT.requestFocus();
                FromLocationATXT.showDropDown();
                return false;
            }
        });

        FromLocationATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String locModelStr = (String) parent.getItemAtPosition(position);
                for (ConcessionNamesModel locModel : Common.ConcessionList)
                    if (locModelStr.equals(locModel.getConcessionName())) {
                        Common.RecFromLocationID = locModel.getFromLocationId();
                        Common.RecFromLocationname = locModel.getConcessionName();
                    }
            }
        });

        DriverATXT.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DriverATXT.requestFocus();
                DriverATXT.showDropDown();
                return false;
            }
        });

        DriverATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String driverModelStr = (String) parent.getItemAtPosition(position);
                for (DriverDetailsModel driverModel : Common.DriverList)
                    if (driverModelStr.equals(driverModel.getDriverName())) {
                        Common.DriverID = driverModel.getTruckDriverId();
                        Common.DriverName = driverModel.getDriverName();
                    }
            }
        });

        AgencyATXT.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AgencyATXT.requestFocus();
                AgencyATXT.showDropDown();
                return false;
            }
        });

        AgencyATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String agencyModelStr = (String) parent.getItemAtPosition(position);
                for (AgencyDetailsModel agencyModel : Common.TransportAgencyList)
                    if (agencyModelStr.equals(agencyModel.getAgencyName())) {
                        Common.AgencyName = agencyModel.getAgencyName();
                        Common.TransferAgencyID = agencyModel.getAgencyId();
                    }
            }
        });

        TruckDetialsATXT.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TruckDetialsATXT.requestFocus();
                TruckDetialsATXT.showDropDown();
                return false;
            }
        });

        TruckDetialsATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String truckStr = (String) parent.getItemAtPosition(position);
                for (TruckDetailsModel truckModel : Common.TruckDeatilsList)
                    if (truckStr.equals(truckModel.getTruckLicensePlateNo())) {
                        Common.TrucklicensePlateNo = truckModel.getTruckLicensePlateNo();
                        Common.TransportId = truckModel.getTransportId();
                    }
               /* Common.TrucklicensePlateNo = Common.TruckDeatilsList.get(position).getTruckLicensePlateNo();
                Common.TransportId = Common.TruckDeatilsList.get(position).getTransportId();*/
            }
        });
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
            scanService.setOnScanListener(InventoryReceivedActivity.this);
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

    @Override
    protected void onStart() {
        super.onStart();
        beepsound = MediaPlayer.create(this, R.raw.beep);
        wronBuzzer = MediaPlayer.create(this, R.raw.wrong_buzzer);
        //ImportDataBaseFromInternalStorage();
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
        GetReceivedScannedResultList();
        super.onResume();
    }

    View.OnClickListener as_OpenrListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (ReceivedDetailsValidation(TransferIDAutoTXT.getText().toString(), FromLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
                Common.IsSBBLabelCorrected = false;
                sbbLabel_TXT.setText("");
                AdvanceSearchRefresh();
            }
        }
    };
    View.OnClickListener as_mCloseActivityListener = new View.OnClickListener() {
        public void onClick(View v) {
            advanceSearchLAY.setVisibility(View.GONE);
            GetReceivedScannedResultList();
        }
    };
    View.OnClickListener as_mScannerListener = new View.OnClickListener() {
        public void onClick(View v) {
            AdvanceSearch();
        }
    };

    public boolean AdvaneSearchValidation(String UserName, String UserPassword) {
        if (UserName.equals("")) {
            AlertDialogBox("Tree Number", "please enter Tree number", false);
            treeNoEDT.setError("please enter Tree number");
            treeNoEDT.requestFocus();
            return false;
        } else if (UserPassword.equals("")) {
            AlertDialogBox("Felling ID", "Please enter felling id", false);
            fellingIDEDT.setError("Please enter felling id");
            fellingIDEDT.requestFocus();
            return false;
        }
        return true;
    }

    public void AdvanceSearch() {
        try {
            if (AdvaneSearchValidation(treeNoEDT.getText().toString(), fellingIDEDT.getText().toString())) {
                TreeNO = treeNoEDT.getText().toString();
                FellingID = fellingIDEDT.getText().toString();

                Common.AdvancedSearchList.clear();
                Common.AdvancedSearchList = mDBExternalHelper.getAdvanceSearchList(TreeNO, FellingID, "In Stock");
                if (Common.AdvancedSearchList.size() > 0) {
                } else {
                    Common.AdvancedSearchList = mDBExternalHelper.getAdvanceSearchList(TreeNO, FellingID, "Harvested");
                }
                if (Common.IsSBBLabelCorrected == true) {
                    if (Common.AdvancedSearchList.size() > 0) {
                        StringBuilder AdvanceSearchStr = new StringBuilder();
                        for (AdvanceSearchModel advancemodel : Common.AdvancedSearchList) {
                            AdvanceSearchStr.append("'" + advancemodel.getSBBLabel() + "',");
                        }
                        boolean AdvanceSearchBtnFlag = mDBInternalHelper.getAdvanceSearchTransferListCheck(Common.TransferID, AdvanceSearchStr.substring(0, AdvanceSearchStr.toString().length() - 1), Common.AdvancedSearchList.size(), true);
                        if (AdvanceSearchBtnFlag == false) {
                            AS_NovalueFound.setVisibility(View.GONE);
                        } else {
                            AS_NovalueFound.setVisibility(View.VISIBLE);
                        }
                        RefreshAdvancedSearchList();
                    } else {
                        AS_NovalueFound.setVisibility(View.VISIBLE);
                    }
                } else {
                    AS_NovalueFound.setVisibility(View.GONE);
                    RefreshAdvancedSearchList();
                }
                HideKeyboard();
            }
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
    }

    View.OnClickListener as_mAddListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Common.SbbLabel = sbbLabel_TXT.getText().toString();
                Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                Common.WoodSpieceID = "";
                Common.FellingSectionId = fellingIDEDT.getText().toString();
                Common.TreeNumber = treeNoEDT.getText().toString();
                Common.WoodSpieceCode = "";
                Common.QualityName = "";
                Common.BarCode = "NA-" + Common.SbbLabel;
                Common.IsActive = 1;
                Common.EntryMode = 3;
                Common.CheckedFlag = "YES";
                // VVb Limits
                if (Common.ToLocReceivedID == 2) {
                } else {
                    if (Common.InventoryReceivedScannedResultList.size() <= Common.VVBLimitation) {
                    } else {
                        wronBuzzer.start();
                        AlertDialogBox("VBB Limit", "For one VBB Number should have 22 items", false);
                        return;
                    }
                }
                boolean EmptyNullFlags = mDBInternalHelper.getInventoryReceivedduplicateCheck(Common.ReceivedID, Common.SbbLabel);
                if (EmptyNullFlags == true) {
                    Common.InventoryReceivedScannedResultList.clear();
                    wronBuzzer.start();
                } else {
                    boolean ResultFlag = mDBInternalHelper.insertInventoryReceivedItemsFlag(Common.VBB_Number, Common.TransferID, Common.ReceivedID, Common.RecFromLocationname, Common.ToLocationName,
                            Common.SbbLabel, Common.BarCode, "0.00", "0.00", Common.UserID, Common.DateTime, Common.WoodSpieceID, Common.WoodSpieceCode, Common.EntryMode,
                            Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), "", Common.FellingSectionId, Common.TreeNumber,
                            Common.QualityName, Common.CheckedFlag, Common.TransferReciveUniqueID);
                    Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                    AS_NovalueFound.setVisibility(View.GONE);
                    advanceSearchLAY.setVisibility(View.GONE);
                }
                //Scanned Result Refresh
                GetReceivedScannedResultList();
                UpdateReceivedIDList();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
    View.OnClickListener mAddTransferIDListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (TransferIDAutoTXT.getText().toString().length() > 0) {
                Common.TransferReciveUniqueID = TransferIDAutoTXT.getText().toString();
                boolean EmptyNullFlags = mDBInternalHelper.getInventoryReceivedTransferIDDuplication(Common.TransferReciveUniqueID);
                if (EmptyNullFlags == true) {
                    wronBuzzer.start();
                    AlertDialogBox("Transfer ID", "Scanned transfer ID already in table", false);
                    TransferIDAutoTXT.setError("Scanned transfer ID already in table");
                    return;
                } else {
                    for (String trand : Common.TransferIDsList) {
                        if (trand.equals(TransferIDAutoTXT.getText().toString())) {
                            AlertDialogBox("Transfer ID", "Added transfer ID already in List", false);
                            return;
                        }
                    }
                    Common.TransferIDsList.add(Common.TransferReciveUniqueID);
                    //TransferIDList(Common.TransferIDsList);
                }
            }
        }
    };
    View.OnClickListener mScannerQRCodeDetailListener = new View.OnClickListener() {
        public void onClick(View v) {
            Common.QRCodeScan = true;
            Common.QrBarCodeScan = false;
            scanService.doDecode();
        }
    };
    View.OnClickListener mScannerQRBarCodeDetailListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (ReceivedDetailsValidation(TransferIDAutoTXT.getText().toString(), FromLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
                Common.QRCodeScan = true;
                Common.QrBarCodeScan = true;
                Common.EntryMode = 1;
                scanService.doDecode();
            }
        }
    };

    View.OnClickListener mDecodeListener = new View.OnClickListener() {
        public void onClick(View v) {
            ScanValueETxT.setText("");
            Common.QRCodeScan = false;
            Common.ScanMode = true;
            Common.ScannedEditTXTFlag = false;
            if (ReceivedDetailsValidation(TransferIDAutoTXT.getText().toString(), FromLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
                scanService.doDecode();
            }
        }
    };

    View.OnClickListener mScannerListener = new View.OnClickListener() {
        public void onClick(View v) {
            Common.ScanMode = false;
            Common.QRCodeScan = false;
            if (ReceivedDetailsValidation(TransferIDAutoTXT.getText().toString(), FromLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
                HideKeyboard();
                String SBBLabel = ScanValueETxT.getText().toString();
                if (SBBLabel.length() == Common.SBBlenght) {
                    Common.EntryMode = 2;
                    ScannedResult(SBBLabel);
                } else {
                    ScanValueETxT.setError(CommonMessage(R.string.ValidBarCodeMsg));
                }
            }
        }
    };

    public boolean ReceivedDetailsValidation(String TransferID, String FromLoc_ID, String TransAge_ID, String Driver_ID, String Truck_ID) {
        boolean Validattion = true;
    /*    if (FromLoc_ID == -1) {
            Validattion = false;
            AlertDialogBox("From Location", "please select one item from List", false);
        }
        if (ToLoc_ID == -1) {
            Validattion = false;
            AlertDialogBox("To Location", "please select one item from List", false);
        }
        if (TransAge_ID == -1) {
            Validattion = false;
            AlertDialogBox("Transport Agency", "please select one item from List", false);
        }
        if (Driver_ID == -1) {
            Validattion = false;
            AlertDialogBox("Driver Details", "please select one item from List", false);
        }
        if (Truck_ID == -1) {
            Validattion = false;
            AlertDialogBox("Truck Details", "please select one item from List", false);
        }
       */
        if (isNullOrEmpty(TransferID)) {
            Validattion = false;
            AlertDialogBox("From Location", "Add Transfer Id", false);
        }
        //else {
          //  boolean EmptyNullFlags = mDBInternalHelper.getInventoryReceivedTransferIDDuplication(TransferID);
          //  if (EmptyNullFlags == true) {
             //   wronBuzzer.start();
              //  AlertDialogBox("Transfer ID", "Scanned transfer ID already in table", false);
             //   Validattion = false;
            //}
        //}
        if (isNullOrEmpty(String.valueOf(FromLoc_ID))) {
            Validattion = false;
            AlertDialogBox("To Location", "please select one item from List", false);
        }
        if (isNullOrEmpty(String.valueOf(TransAge_ID))) {
            Validattion = false;
            AlertDialogBox("Transport Agency", "please select one item from List", false);
        }
        if (isNullOrEmpty(String.valueOf(Driver_ID))) {
            Validattion = false;
            AlertDialogBox("Driver Details", "please select one item from List", false);
        }
        if (isNullOrEmpty(String.valueOf(Truck_ID))) {
            Validattion = false;
            AlertDialogBox("Truck Details", "please select one item from List", false);
        }

        return Validattion;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str == "null";
    }

    View.OnClickListener mPrintListener = new View.OnClickListener() {
        public void onClick(View v) {
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
                    AlertDialogBox(CommonMessage(R.string.ReceivedHead), CommonMessage(R.string.TransferMessage), false);
                }
            } else {
                AlertDialogBox(CommonMessage(R.string.PrinterHead), CommonMessage(R.string.Printing), false);
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
                try {
                    Common.ReceivedLoadedTypeName = "";
                    Common.LoadedByList.clear();
                    Common.LoadedByList = mDBExternalHelper.getAllLoadedByDetails();
                    Common.ReceivedLoadedTypeName = mDBExternalHelper.getAllLoadedNames(Common.ReceivedLoadedTypeID);
                } catch (Exception ex) {
                }
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

    public void PrintoutEnd() {
        tsc.closeport();
        TSCConnInt = -1;
        Common.IsPrintBtnClickFlag = true;
    }

    View.OnClickListener mAddReceivedIDListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                if (ReceivedDetailsValidation(TransferIDAutoTXT.getText().toString(), FromLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
                    if (Common.InventoryReceivedScannedResultList.size() > 0) {
                        if (Common.InventoryReceivedScannedResultList.size() < Common.MinimumScannedItemSize) {
                            AlertDialogBox(CommonMessage(R.string.ReceivedHead), "add minimum five items ", false);
                            return;
                        }
                        UpdateReceivedIDList();
                        if (Common.InventoryReceivedScannedResultList.size() < (Common.VVBLimitation + 1)) {
                            NextAlertDialog("Received Count is less then 22, Are you sure to create next list?");
                        } else {
                            AddReceivedList();
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.ReceivedHead), "add minimum five items ", false);
                    }

               /* boolean DuplicateFlag = mDBInternalHelper.getInventoryReceivedIdDuplicateCheck(Common.ReceivedID);
                if (DuplicateFlag == true) {
                    if (Common.InventoryReceivedScannedResultList.size() > 0) {
                        if (Common.InventoryReceivedScannedResultList.size() == Common.MinimumScannedItemSize) {
                            NextAlertDialog("Received Count is less then 22, Are you sure to create next list?");
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ReceivedHead), "add minimum five items ", false);
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.ReceivedHead), "add minimum five items ", false);
                    }
                } else {
                    AddReceivedList();
                }*/
                }
            } catch (Exception ex) {
                AlertDialogBox(CommonMessage(R.string.ReceivedHead), ex.toString(), false);
            }

        }
    };
    View.OnClickListener mCloseActivityListener = new View.OnClickListener() {
        public void onClick(View v) {
            String Message = "";
            if (Integer.parseInt(TotalScannedCount.getText().toString()) > Integer.parseInt(Received_TotalCheckedCountTXT.getText().toString())) {
                Message = "Still some SBBLabel Is not Checked,Are you sure you want to close? ";
            } else {
                Message = "Are you sure you want to close?";
            }
            Signout(Message);
        }
    };

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
        HideKeyboard();
        try {
            if (scannedValue != null && scannedValue.length() > 0) {
                Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                Common.IsActive = 1;
                if (Common.QRCodeScan == true) {
                    if (Common.QrBarCodeScan == true) {
                        String[] TransferBarcodeDetails = scannedValue.split("--");
                        String BarCodeValidation = TransferBarcodeDetails[1];
                        //if (BarCodeValidation.indexOf("D") != -1) {
                        if (BarCodeValidation.length() > 10) {
                            ScanedQRBarcodeDetails(TransferBarcodeDetails);
                        } else {
                            wronBuzzer.start();
                            AlertDialogBox("Scanned Value", "Please Scan Valid Barcode Details", false);
                            return;
                        }
                    } else {
                        // Checked List Size For Change Transfer ID
                        /*if (TransferIDAutoTXT.getText().toString().length() > 0) {
                            if (Common.InventoryReceivedScannedResultList.size() > 0) {
                                wronBuzzer.start();
                                AlertDialogBox("Scanned Value", "Cannot Change Transfer ID while Scanning", false);
                                return;
                            }
                        }*/
                        String[] TransferDetails = scannedValue.split("--");
                        String DetailsValidation = TransferDetails[1];
                        if (DetailsValidation.length() == 6 || DetailsValidation.length() == 0) {
                            ScanedQRDetails(TransferDetails);
                        } else {
                            wronBuzzer.start();
                            AlertDialogBox("Scanned Value", "Please Scan Valid Transfer Details", false);
                            return;
                        }
                    }
                } else {
                    Common.CheckedFlag = "YES";
                    if (scannedValue.length() == Common.ScannedValueLenght || scannedValue.length() == Common.SBBlenght) {
                    } else {
                        wronBuzzer.start();
                        AlertDialogBox("Scanned Value", "Please Scan Valid Barcode Details", false);
                        return;
                    }
                    if (Common.ScanMode == true) {
                        Common.BarCode = scannedValue;
                        boolean BarcodeValidation = Common.BarCode.contains("-");
                        if (BarcodeValidation == true) {
                            String[] arrOfStr = Common.BarCode.split("-");
                            if (arrOfStr.length > 1) {
                                Common.SbbLabel = arrOfStr[1];
                            }
                        } else {
                            AlertDialogBox("Scanning Result!", "Enter valid Barcode, please try diff barcode", false);
                            return;
                        }
                    } else {
                        Common.SbbLabel = scannedValue;
                        Common.BarCode = "NA-" + Common.SbbLabel;
                    }
                    // Checked Duplicate In Internal Tabel
                    boolean EmptyNullFlags = mDBInternalHelper.getInventoryReceivedduplicateCheck(Common.ReceivedID, Common.SbbLabel);
                    if (EmptyNullFlags == true) {
                        //Update If Already in ListView
                        boolean ResultFlag = mDBInternalHelper.UpdateInventoryReceivedItems(Common.ReceivedID, Common.SbbLabel, Common.CheckedFlag);
                        if (ResultFlag == true) {
                            //Scanned Result Refresh
                            GetReceivedScannedResultList();
                        }
                        return;
                    }
                    Common.SearchedTransLogDetils = Common.AllTransLogDetailsmap.get(Common.SbbLabel);
                    if (Common.SearchedTransLogDetils != null) {
                        Common.WoodSpieceID = Common.SearchedTransLogDetils.get(0).getWoodSpeciesId();
                        Common.WoodSpieceCode = Common.SearchedTransLogDetils.get(0).getWoodSpeciesCode();
                        Common.Length = Common.SearchedTransLogDetils.get(0).getLength_dm();
                        Common.Volume = Common.SearchedTransLogDetils.get(0).getVolume();
                        Common.FellingSectionId = Common.SearchedTransLogDetils.get(0).getFellingSectionId();
                        Common.TreeNumber = Common.SearchedTransLogDetils.get(0).getTreeNumber();
                        Common.QualityName = Common.SearchedTransLogDetils.get(0).getQuality();
                        //Common.OldQualityName = Common.SearchedTransLogDetils.get(0).getClassification();
                        Common.IsSBBLabelCorrected = false;
                    } else {
                        Common.WoodSpieceID = "";
                        Common.WoodSpieceCode = "";
                        Common.FellingSectionId = "";
                        Common.TreeNumber = "";
                        Common.Length = "0.00";
                        Common.Volume = "0.00";
                        Common.IsSBBLabelCorrected = true;
                        sbbLabel_TXT.setText(Common.SbbLabel);
                        AS_NovalueFound.setVisibility(View.VISIBLE);
                        AdvanceSearchRefresh();
                        return;
                    }
                    if (Common.ToLocReceivedID == 2) {
                        InsertReceivedScannedResultTable();
                    } else {
                        if (Common.InventoryReceivedScannedResultList.size() <= Common.VVBLimitation) {
                            InsertReceivedScannedResultTable();
                        } else {
                            wronBuzzer.start();
                            AlertDialogBox("VBB Limit", "For one VBB Number should have 22 items", false);
                            return;
                        }
                    }
                    Common.EntryMode = 1;
                    Common.ScannedEditTXTFlag = false;
                    ScanValueETxT.setText(scannedValue);
                }
            }
        } catch (Exception ex) {
            Log.v(TAG, "decode failed" + ex.toString());
        }
    }

    public void ScanedQRDetails(String[] ScannedArryValue) {
        try {
            Common.TransferReciveUniqueID = ScannedArryValue[0];
            boolean EmptyNullFlags = mDBInternalHelper.getInventoryReceivedTransferIDDuplication(Common.TransferReciveUniqueID);
            if (EmptyNullFlags == true) {
                wronBuzzer.start();
                AlertDialogBox("Transfer ID", "Scanned transfer ID already in table", false);
                return;
            }/* else {
                for (String trand : Common.TransferIDsList) {
                    if (trand.equals(TransferIDAutoTXT.getText().toString())) {
                        AlertDialogBox("Transfer ID", "Added transfer ID already in List", false);
                        return;
                    }
                }
                //Common.TransferReciveUniqueID = TransferReciveUniqueID;
                Common.TransferIDsList.add(String.valueOf(Common.TransferReciveUniqueID));
                TransferIDList(Common.TransferIDsList);
            }*/
            beepsound.start();
            //Common.TransferID = Integer.parseInt(ScannedArryValue[0]);
            TransferIDAutoTXT.setText(Common.TransferReciveUniqueID);

            Common.VBB_Number = ScannedArryValue[1];
            Common.ReceivedDate = ScannedArryValue[2];
            Common.RecFromLocationID = Integer.parseInt(ScannedArryValue[3]);
            Common.ToLocReceivedID = Integer.parseInt(ScannedArryValue[4]);

            Common.TransferAgencyID = Integer.parseInt(ScannedArryValue[5]);
            Common.DriverID = Integer.parseInt(ScannedArryValue[6]);
            Common.TransportId = Integer.parseInt(ScannedArryValue[7]);
            Common.TransportTypeId = Integer.parseInt(ScannedArryValue[8]);
            Common.FellingSectionId = ScannedArryValue[9];
            Common.Count = Integer.parseInt(ScannedArryValue[10]);
            Common.VolumeSum = Double.parseDouble(ScannedArryValue[11]);
            Common.ReceivedLoadedTypeID = Integer.parseInt(ScannedArryValue[12]);

            Common.RecFromLocationname = ScannedArryValue[13];
            Common.ToLocationName = ScannedArryValue[14];
            Common.AgencyName = ScannedArryValue[15];
            Common.DriverName = ScannedArryValue[16];
            Common.TrucklicensePlateNo = ScannedArryValue[17];
            Common.TransportMode = ScannedArryValue[18];
            Common.LoadedName = ScannedArryValue[19];
            if (ScannedArryValue.length > 20) {
                Common.ReceivedTransferID = ScannedArryValue[20];
            }
         /*   Common.LocationList.clear();
            Common.LocationList = mDBExternalHelper.getToLocations();
            for (LocationsModel locationMod : Common.LocationList) {
                if (Common.ToLocReceivedID == locationMod.getToLocationId()) {
                    Common.ToLocationName = locationMod.getLocation();
                    ToLocationTxT.setText(Common.ToLocationName);
                }
            }*/
            ToLocationTxT.setText(Common.ToLocationName);
            QrScanDetails();
        } catch (Exception ee) {
            AlertDialogBox("ScanQR Details", ee.toString(), false);
        }
    }

    public void QrScanDetails() {
        if (Common.IsReceivedEditListFlag == true) {
            if (MasterDataBaseCheckValidation(Common.RecFromLocationID, Common.TransferAgencyID, Common.DriverID, Common.TransportId, Common.TransportTypeId)) {
            } else {
                return;
            }
        }
        /*18-09-2019-Implemented Values for ID in header QR code*/
        FromLocationATXT.setText(Common.RecFromLocationname);
        AgencyATXT.setText(Common.AgencyName);
        DriverATXT.setText(Common.DriverName);
        TruckDetialsATXT.setText(Common.TrucklicensePlateNo);
        /*for (ConcessionNamesModel item : Common.ConcessionList) {
            if (item.getFromLocationId() == Common.RecFromLocationID) {
                FromLocationSpin.setSelection(item.getID());
                FromLocationATXT.setText(item.getConcessionName());
                Common.RecFromLocationname = item.getConcessionName();
            }
        }

        for (AgencyDetailsModel item : Common.TransportAgencyList) {
            if (item.getAgencyId() == Common.TransferAgencyID) {
                TransportAgencySpin.setSelection(item.getID());
                AgencyATXT.setText(item.getAgencyName());
                Common.AgencyName = item.getAgencyName();
            }
        }
        for (DriverDetailsModel item : Common.DriverList) {
            if (item.getTruckDriverId() == Common.DriverID) {
                DriverSpin.setSelection(item.getID());
                DriverATXT.setText(item.getDriverName());
                Common.DriverName = item.getDriverName();
            }
        }
        for (TruckDetailsModel item : Common.TruckDeatilsList) {
            if (item.getTransportId() == Common.TransportId) {
                TruckPlateNumberSpin.setSelection(item.getID());
                TruckDetialsATXT.setText(item.getTruckLicensePlateNo());
                Common.TrucklicensePlateNo = item.getTruckLicensePlateNo();
            }
        }*/
        //transportAdapter = new TransportAdapterInside(getApplicationContext(), Common.TransportModeList);
        //TransferMode_LV.setAdapter(transportAdapter);
        transportAdapter.notifyDataSetChanged();
        vbb_txt.setText(Common.VBB_Number);
        UpdateReceivedIDList();
    }

    public boolean MasterDataBaseCheckValidation(int RecFromLocationID, int TransferAgencyID, int DriverID, int TransportId, int TransportTypeId) {
        boolean MasterFlg = true;
        if (!mDBExternalHelper.FromLocationMasterCheck(RecFromLocationID)) {
            MasterFlg = false;
            // MasterCheckAlertDialog("Values not found, please sync external data");
        }
        if (!mDBExternalHelper.AgencyMasterCheck(TransferAgencyID)) {
            MasterFlg = false;
            //AlertDialogBox("Agency Details", "Values not found, please sync external data", false);
        }
        if (!mDBExternalHelper.DriverDetailsMasterCheck(DriverID)) {
            MasterFlg = false;
            //AlertDialogBox("Driver Details", "Values not found, please sync external data", false);
        }
        if (!mDBExternalHelper.TruckDetailsMasterCheck(TransportId)) {
            MasterFlg = false;
            //AlertDialogBox("Truck Details", "Values not found, please sync external data", false);
        }
        if (!mDBExternalHelper.TransportTypeMasterCheck(TransportTypeId)) {
            MasterFlg = false;
            //AlertDialogBox("TransPortType Details", "Values not found, please sync external data", false);
        }
        if (MasterFlg == false) {
            MasterCheckAlertDialog("Values not found, please sync external data");
        }
        return MasterFlg;
    }

    public void ScanedQRBarcodeDetails(String[] ScannedArryValue) {
        try {
            Common.CheckedFlag = "NO";
            // Checked Duplicate In Internal Tabel
            for (int i = 0; i < ScannedArryValue.length; i++) {
                //Common.InventoryReceivedScannedResultList = mDBInternalHelper.getInventoryReceivedWithVBBNumber(Common.VBB_Number, Common.ReceivedID);
                //if (Common.InventoryReceivedScannedResultList.size() <= 21) {
                //if (Common.CheckedSize <= Common.TotalReceivedVVBLimitation) {
                if (i == 0) {
                    Common.TransferReciveUniqueID = ScannedArryValue[0];
                } else {
                    String[] BarcodeSplite = ScannedArryValue[i].split("-");
                    Common.QualityName = BarcodeSplite[0];
                    Common.SbbLabel = BarcodeSplite[1];
                    Common.FellingSectionId = BarcodeSplite[2];
                    Common.TreeNumber = BarcodeSplite[3];
                    Common.BarCode = "NA-" + Common.SbbLabel;
                    Common.SearchedTransLogDetils = Common.AllTransLogDetailsmap.get(Common.SbbLabel);
                    boolean EmptyNullFlags = mDBInternalHelper.getInventoryReceivedduplicateCheck(Common.ReceivedID, Common.SbbLabel);
                    if (EmptyNullFlags == true) {
                        wronBuzzer.start();
                    } else {
                        if (Common.SearchedTransLogDetils != null) {
                            Common.WoodSpieceID = Common.SearchedTransLogDetils.get(0).getWoodSpeciesId();
                            Common.Length = Common.SearchedTransLogDetils.get(0).getLength_dm();
                            Common.Volume = Common.SearchedTransLogDetils.get(0).getVolume();
                            Common.WoodSpieceCode = Common.SearchedTransLogDetils.get(0).getWoodSpeciesCode();
                            Common.IsSBBLabelCorrected = false;
                        } else {
                            Common.WoodSpieceID = "";
                            Common.WoodSpieceCode = "";
                            Common.FellingSectionId = "";
                            Common.TreeNumber = "";
                            Common.Length = "0.00";
                            Common.Volume = "0.00";
                            Common.IsSBBLabelCorrected = true;
                        }
                        boolean ResultFlag = mDBInternalHelper.insertInventoryReceivedItemsFlag(Common.VBB_Number, Common.TransferID, Common.ReceivedID, Common.RecFromLocationname, Common.ToLocationName,
                                Common.SbbLabel, Common.BarCode, Common.Length, Common.Volume, Common.UserID, Common.DateTime, Common.WoodSpieceID, Common.WoodSpieceCode, Common.EntryMode,
                                Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), "", Common.FellingSectionId, Common.TreeNumber,
                                Common.QualityName, Common.CheckedFlag, Common.TransferReciveUniqueID);
                    }
                    //}
                }
            }
            beepsound.start();
            GetReceivedScannedResultList();
            UpdateReceivedIDList();
        } catch (Exception ee) {
            AlertDialogBox("ScanQR Barcode Details", ee.toString(), false);
        }
    }

    public void InsertReceivedScannedResultTable() {
        beepsound.start();
        // Insert values into DB
        boolean ResultFlag = mDBInternalHelper.insertInventoryReceivedItemsFlag(Common.VBB_Number, Common.TransferID, Common.ReceivedID, Common.RecFromLocationname, Common.ToLocationName,
                Common.SbbLabel, Common.BarCode, Common.Length, Common.Volume, Common.UserID, Common.DateTime, Common.WoodSpieceID, Common.WoodSpieceCode, Common.EntryMode,
                Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), "", Common.FellingSectionId, Common.TreeNumber,
                Common.QualityName, Common.CheckedFlag, Common.TransferReciveUniqueID);
        //Scanned Result Refresh
        GetReceivedScannedResultList();
        UpdateReceivedIDList();
    }

    public static <T> boolean IsNullOrEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    public static int booleanToInt(boolean value) {
        // Convert true to 1 and false to 0.
        return value ? 1 : 0;
    }

    private void GetReceivedScannedResultList() {
        try {
            Common.InventoryReceivedScannedResultList = mDBInternalHelper.getInventoryReceivedWithVBBNumber(Common.VBB_Number, Common.ReceivedID);
            Common.CheckedSize = mDBInternalHelper.ColculateInventoryReceivedCheckedItems(Common.ReceivedID, Common.SbbLabel, "YES");
            Double RemoveVolumeSum = 0.00;
            if (Common.InventoryReceivedScannedResultList.size() > 0) {
                receivedadapter = new InventoryReceivedAdapter(Common.InventoryReceivedScannedResultList, Common.QulaityDefaultList, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                ScannedResultLV.setLayoutManager(horizontalLayoutManager);
                receivedadapter.notifyDataSetChanged();
                ScannedResultLV.setAdapter(receivedadapter);
                ScannedResultLV.setVisibility(View.VISIBLE);
                RemoveVolumeSum = ReceivedTotalVolume(Common.InventoryReceivedScannedResultList);
            } else {
                ScannedResultLV.setVisibility(View.GONE);
            }
            TotalScannedCount.setText(String.valueOf(Common.InventoryReceivedScannedResultList.size()));
            VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(RemoveVolumeSum)));
            Received_TotalCheckedCountTXT.setText(String.valueOf(Common.CheckedSize));
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
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

    public void HideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void ShowKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    private static void excelLog(int row, int col, String value) {
        HSSFRow myRow = mySheet.getRow(row);
        if (myRow == null)
            myRow = mySheet.createRow(row);
        HSSFCell myCell = myRow.createCell(col);
        myCell.setCellValue(value);
    }

    public class InventoryReceivedAdapter extends RecyclerView.Adapter<InventoryReceivedAdapter.GroceryViewHolder> {
        private List<InventoryReceivedModel> ScannedResultList;
        private List<String> QualitySpinner;
        Context context;

        public InventoryReceivedAdapter(List<InventoryReceivedModel> ScannedResultList, ArrayList<String> SpinnerData, Context context) {
            this.QualitySpinner = SpinnerData;
            this.ScannedResultList = ScannedResultList;
            this.context = context;
        }

        public void removeItem(int position) {
            ScannedResultList.remove(position);
            notifyItemRemoved(position);
        }

      /*  public void restoreItem(InventoryTransferScannedResultModel item, int position) {
            ScannedResultList.add(position, item);
            notifyItemInserted(position);
        }*/

        public InventoryReceivedModel getItem(int position) {
            return ScannedResultList.get(position);
        }

        @Override
        public GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_infliator, parent, false);
            GroceryViewHolder gvh = new GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(GroceryViewHolder holder, final int position) {
            holder.Barcode.setText(ScannedResultList.get(position).getSbbLabel());
            holder.WoodSpiceCode.setText(ScannedResultList.get(position).getWoodSpieceCode());
            holder.ClassifiationTXT.setText(ScannedResultList.get(position).getQuality());
            holder.CheckedTXT.setText(ScannedResultList.get(position).getIsReceived());
            if (ScannedResultList.get(position).getIsReceived().equals("NO")) {
                holder.CheckedTXT.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            } else {
                holder.CheckedTXT.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            }
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
            int color = generator.getColor(getItem(position));
            holder.Remove.setBackgroundColor(color);
            holder.Remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.IsEditorViewFlag == false) {
                        AlertDialogBox(CommonMessage(R.string.ReceivedHead), "Can not edit Or delete after Synced", false);
                    } else {
                        Common.RemoveSBBLabel = "";
                        Common.RemoveSBBLabel = ScannedResultList.get(position).getSbbLabel();
                        RemoveMessage(CommonMessage(R.string.Remove_Message));
                    }
                }
            });

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, QualitySpinner);
            holder.QualitySpinner.setAdapter(adapter);
            if (ScannedResultList.get(position).getQuality() != null) {
                int spinnerPosition = adapter.getPosition(ScannedResultList.get(position).getQuality());
                holder.QualitySpinner.setSelection(spinnerPosition);
            }
            if (Common.IsEditorViewFlag == false) {
                holder.QualitySpinner.setEnabled(false);
            } else {
                holder.QualitySpinner.setEnabled(true);
            }
            holder.QualitySpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Common.isSpinnerTouched = true;
                    return false;
                }
            });
            holder.QualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int inside_position, long id) {
                    try {
                        if (Common.isSpinnerTouched == true) {
                            Common.QualityName = QualitySpinner.get(inside_position);
                            String sBBLabel = ScannedResultList.get(position).getSbbLabel();
                            boolean UpdateFlag = mDBInternalHelper.UpdateQualityScannedList(Common.QualityName, sBBLabel, Common.ReceivedID);
                            Common.isSpinnerTouched = false;
                            //notifyDataSetChanged();

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return ScannedResultList.size();
        }

        public class GroceryViewHolder extends RecyclerView.ViewHolder {
            TextView Barcode, WoodSpiceCode, ClassifiationTXT, CheckedTXT;
            LinearLayout Background, Remove;
            Spinner QualitySpinner;

            public GroceryViewHolder(View view) {
                super(view);
                Barcode = view.findViewById(R.id.received_SbbLabel);
                WoodSpiceCode = view.findViewById(R.id.received_woodspiceCode);
                ClassifiationTXT = view.findViewById(R.id.received_Classifiaction);
                CheckedTXT = view.findViewById(R.id.received_checkBox);
                Background = view.findViewById(R.id.resultlayoutBackground);
                Remove = view.findViewById(R.id.removeBarCode_invenReceied);
                QualitySpinner = view.findViewById(R.id.received_QulaitySpinner);
            }
        }
    }

    public void AdvanceSearchRefresh() {
        advanceSearchLAY.setVisibility(View.VISIBLE);
        treeNoEDT.setText("");
        fellingIDEDT.setText("");
        Common.AdvancedSearchList.clear();
        RefreshAdvancedSearchList();
    }

    public void RefreshAdvancedSearchList() {
        advancedSearchadapter = new AdvanceSearchResultAdapter(Common.AdvancedSearchList, this);
        horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        advanceSearchRLV.setLayoutManager(horizontalLayoutManager);
        advanceSearchRLV.setAdapter(advancedSearchadapter);
    }

    public class AdvanceSearchResultAdapter extends RecyclerView.Adapter<AdvanceSearchResultAdapter.GroceryViewHolder> {
        private List<AdvanceSearchModel> AdvanceSearchResultList;
        Context context;

        public AdvanceSearchResultAdapter(List<AdvanceSearchModel> ScannedResultList, Context context) {
            this.AdvanceSearchResultList = ScannedResultList;
            this.context = context;
        }

        @Override
        public AdvanceSearchResultAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.advance_search_infliator, parent, false);
            AdvanceSearchResultAdapter.GroceryViewHolder gvh = new AdvanceSearchResultAdapter.GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(AdvanceSearchResultAdapter.GroceryViewHolder holder, final int position) {
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            holder.SBBLabel.setText(AdvanceSearchResultList.get(position).getSBBLabel());  //barcode
            holder.WSpiceCode.setText(AdvanceSearchResultList.get(position).getWoodSpeciesCode());
            holder.Lenght.setText(AdvanceSearchResultList.get(position).getLength_dm());  //specie
            holder.Volume.setText(AdvanceSearchResultList.get(position).getVolume());  //specie
            holder.F1.setText(AdvanceSearchResultList.get(position).getF1());
            holder.F2.setText(AdvanceSearchResultList.get(position).getF2());
            holder.T1.setText(AdvanceSearchResultList.get(position).getT1());
            holder.T2.setText(AdvanceSearchResultList.get(position).getT2());
            try {
                boolean AdvanceSearchBtnFlag = mDBInternalHelper.getAdvanceSearchTransferListCheck(Common.TransferID, AdvanceSearchResultList.get(position).getSBBLabel(), 0, false);
                if (AdvanceSearchBtnFlag == true) {
                    holder.Select.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    holder.Select.setText("In Table");
                    holder.Select.setTextColor(getResources().getColor(R.color.edittextColor));
                } else {
                    holder.Select.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    holder.Select.setTextColor(getResources().getColor(R.color.color_white));
                    holder.Select.setText("ADD");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            holder.Select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String SelectTxTvalue = holder.Select.getText().toString();
                        if (SelectTxTvalue.equals("ADD")) {
                            Common.SbbLabel = AdvanceSearchResultList.get(position).getSBBLabel();
                            boolean EmptyNullFlags = mDBInternalHelper.getInventoryReceivedduplicateCheck(Common.ReceivedID, Common.SbbLabel);
                            if (EmptyNullFlags == true) {
                                Common.InventoryReceivedScannedResultList.clear();
                                wronBuzzer.start();
                                //Scanned Result Refresh
                                GetReceivedScannedResultList();
                                return;
                            }
                            if (Common.ToLocReceivedID == 2) {
                            } else {
                                if (Common.InventoryReceivedScannedResultList.size() <= Common.VVBLimitation) {
                                } else {
                                    wronBuzzer.start();
                                    AlertDialogBox("VBB Limit", "For one VBB Number should have 22 items", false);
                                    return;
                                }
                            }
                            Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                            Common.IsActive = 1;
                            Common.EntryMode = 3;
                            Common.WoodSpieceID = String.valueOf(AdvanceSearchResultList.get(position).getWoodSpeciesId());
                            Common.WoodSpieceCode = AdvanceSearchResultList.get(position).getWoodSpeciesCode();
                            Common.FellingSectionId = AdvanceSearchResultList.get(position).getFellingSectionId();
                            Common.TreeNumber = AdvanceSearchResultList.get(0).getTreeNumber();
                            Common.Volume = AdvanceSearchResultList.get(position).getVolume();
                            Common.Length = AdvanceSearchResultList.get(position).getLength_dm();
                            Common.QualityName = AdvanceSearchResultList.get(position).getClassification();
                            Common.CheckedFlag = "YES";
                            try {
                                boolean InisertAdvanceFlag = false;
                                if (Common.IsSBBLabelCorrected == true) {
                                    Common.OrgSBBLabel = sbbLabel_TXT.getText().toString();
                                    Common.BarCode = "NA-" + Common.OrgSBBLabel;
                                    InisertAdvanceFlag = mDBInternalHelper.insertInventoryReceivedItemsFlag(Common.VBB_Number, Common.TransferID, Common.ReceivedID, Common.RecFromLocationname, Common.ToLocationName,
                                            Common.SbbLabel, Common.BarCode, Common.Length, Common.Volume, Common.UserID, Common.DateTime, Common.WoodSpieceID, Common.WoodSpieceCode, Common.EntryMode,
                                            Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), "", Common.FellingSectionId, Common.TreeNumber,
                                            Common.QualityName, Common.CheckedFlag, Common.TransferReciveUniqueID);
                                } else {
                                    Common.OrgSBBLabel = "";
                                    Common.BarCode = "NA-" + AdvanceSearchResultList.get(position).getSBBLabel();
                                    InisertAdvanceFlag = mDBInternalHelper.insertInventoryReceivedItemsFlag(Common.VBB_Number, Common.TransferID, Common.ReceivedID, Common.RecFromLocationname, Common.ToLocationName,
                                            Common.SbbLabel, Common.BarCode, Common.Length, Common.Volume, Common.UserID, Common.DateTime, Common.WoodSpieceID, Common.WoodSpieceCode, Common.EntryMode,
                                            Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), "", Common.FellingSectionId, Common.TreeNumber,
                                            Common.QualityName, Common.CheckedFlag, Common.TransferReciveUniqueID);
                                }
                                if (InisertAdvanceFlag == true) {
                                    Common.OrgSBBLabel = "";
                                    Common.EntryMode = 1;
                                    advanceSearchLAY.setVisibility(View.GONE);
                                    // Scanned Result List Refresh
                                    GetReceivedScannedResultList();
                                    UpdateReceivedIDList();
                                } else {
                                    wronBuzzer.start();
                                }
                            } catch (Exception ex) {
                                AlertDialogBox("Advacne Search", ex.toString(), false);
                            }
                        } else {
                            //AlertDialogBox("Advance Search", "SBBLabel Already in the Table", false);
                            wronBuzzer.start();
                        }
                    } catch (Exception ex) {
                        AlertDialogBox("Advance Search", ex.toString(), false);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return AdvanceSearchResultList.size();
        }

        public class GroceryViewHolder extends RecyclerView.ViewHolder {
            TextView SBBLabel, WSpiceCode, Lenght, Volume, Select, F1, F2, T1, T2;
            LinearLayout Background;

            public GroceryViewHolder(View view) {
                super(view);
                SBBLabel = view.findViewById(R.id.as_sbblabel);
                WSpiceCode = view.findViewById(R.id.as_woodspiceCode);
                Lenght = view.findViewById(R.id.as_lenght);
                Volume = view.findViewById(R.id.as_volume);
                Background = view.findViewById(R.id.as_scannerlayoutBackground);
                Select = view.findViewById(R.id.as_select);
                F1 = view.findViewById(R.id.as_F1);
                F2 = view.findViewById(R.id.as_F2);
                T1 = view.findViewById(R.id.as_T1);
                T2 = view.findViewById(R.id.as_T2);
            }
        }
    }


    public String HeadCenterAlignment(String Value, int Totlenght) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Totlenght; i++) {
            result.append(" ");
        }
        return (result.toString() + Value + result.toString());
    }


    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(InventoryReceivedActivity.this, Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public String CommonMessage(int Common_Msg) {
        return InventoryReceivedActivity.this.getResources().getString(Common_Msg);
    }


    public void RemoveMessage(String ErrorMessage) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(InventoryReceivedActivity.this);
        Removebuilder.setMessage(ErrorMessage);
        Removebuilder.setCancelable(true);
        Removebuilder.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Removebuilder.setNegativeButton(CommonMessage(R.string.action_Remove),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boolean Isdelete = mDBInternalHelper.RemoveFromReceivedlistview(Common.RemoveSBBLabel, 0, Common.ReceivedID);
                        if (Isdelete == true) {
                            Toast.makeText(InventoryReceivedActivity.this, "Successfully Removed from List", Toast.LENGTH_LONG).show();
                            GetReceivedScannedResultList();
                            UpdateReceivedIDList();
                        }
                        dialog.cancel();
                    }
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }


    public void UpdateReceivedIDList() {
        try {
            //Update Received List ID
            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            Double RemoveVolumeSum = ReceivedTotalVolume(Common.InventoryReceivedScannedResultList);
            // Update values into TransferID
            boolean TransferIDFlag = mDBInternalHelper.UpdateInventoryReceivedID(Common.EndDate, Common.ToLocReceivedID, Common.RecFromLocationID, Common.TransportTypeId,
                    Common.TransferAgencyID, Common.DriverID, Common.TrucklicensePlateNo, Common.UserID, Common.InventoryReceivedScannedResultList.size(), Common.ReceivedID,
                    String.valueOf(RemoveVolumeSum), Common.ReceivedID, 1, Common.TransferReciveUniqueID, Common.ReceivedUniqueID, Common.ReceivedLoadedTypeID);
            if (TransferIDFlag == true) {
                Common.InventoryReceivedList = mDBInternalHelper.getInventoryReceivedIdList();
            }
        } catch (Exception ex) {
            AlertDialogBox("UpdateReceivedIDList", ex.toString(), false);

        }
    }

    public void AddReceivedList() {
        try {
            Common.SyncTime = "";
            Common.VolumeSum = 0.0;
            Common.VBB_Number = "";
            Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());
               /* boolean DulpcateFlag = mDBInternalHelper.getInventoryReceivedIdListDuplicateCheck(Common.TransferID);
                if (DulpcateFlag == true) {
                    AlertDialogBox("Inventory Transfer List", "please add diff VBB Number", false);
                    return;
                }*/
           /* boolean ListIdFlag = mDBInternalHelper.insertInventoryReceivedIDList(Common.VBB_Number, Common.IMEI, Common.ToLocReceivedID, Common.StartDate, Common.EndDate,
                    Common.RecFromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TrucklicensePlateNo, Common.UserID, Common.Count,
                    Common.SyncStatus, Common.SyncTime, Common.Volume, 1, Common.ReceivedUniqueID);*/
            boolean ListIdFlag = mDBInternalHelper.insertInventoryReceivedIDList(Common.VBB_Number, Common.IMEI, Common.ToLocReceivedID, Common.StartDate, Common.EndDate,
                    Common.RecFromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TrucklicensePlateNo, Common.UserID, 0,
                    Common.SyncStatus, Common.SyncTime, "0.00", 1, "");
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
                }
                GetReceivedScannedResultList();
            } else {
                AlertDialogBox("Inventory Received", "Value not Inserted", false);
            }
        } catch (
                Exception ex) {
            ex.printStackTrace();
        }
    }

    public void NextAlertDialog(String ErrorMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(InventoryReceivedActivity.this);
        builder1.setMessage(ErrorMessage);
        builder1.setCancelable(true);
        builder1.setPositiveButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            AddReceivedList();
                            dialog.cancel();
                        } catch (Exception ex) {
                            AlertDialogBox("NextAlertDialog", ex.toString(), false);
                        }
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public double ReceivedTotalVolume(ArrayList<InventoryReceivedModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (InventoryReceivedModel inventoreceScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(inventoreceScanModel.getVolume());
        }
        return TotVolume;
    }

    public void InventoryActivty() {
        Intent _gwwIntent = new Intent(InventoryReceivedActivity.this, InventoryActivity.class);
        startActivity(_gwwIntent);
    }

    public void DeleteReceivedListannReceivedScannedList(int receivedID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteInventoryReceivedListID(receivedID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteInventoryReceivedScanned(receivedID);
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteReceivedListannReceivedScannedList", ex.toString(), false);
        }
    }

    public void MasterCheckAlertDialog(String ErrorMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(InventoryReceivedActivity.this);
        builder1.setMessage(ErrorMessage);
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InventoryActivty();
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public class StringtreeNoadapter extends ArrayAdapter<String> {

        public StringtreeNoadapter(Context context, int layout, String[] from) {
            super(context, layout, from);

        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        InputMethodManager imm = (InputMethodManager) getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(
                                view.getApplicationWindowToken(), 0);
                    }
                    return false;
                }
            });
            return view;
        }
    }

    public class TransportAdapterInside extends BaseAdapter {
        Context context;
        ArrayList<TransportModesModel> modename;
        LayoutInflater inflter;

        public TransportAdapterInside(Context applicationContext, ArrayList<TransportModesModel> list) {
            this.context = applicationContext;
            this.modename = list;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return modename.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.transportmode_infliator, null);
            CheckBox TransferMode = view.findViewById(R.id.transMode_infi);
            if (Common.TransportTypeId == modename.get(position).getTransportTypeId()) {
                Common.TransportMode = modename.get(position).getTransportMode();
                TransferMode.setChecked(true);
            } else {
                TransferMode.setChecked(false);
            }
            TransferMode.setText(modename.get(position).getTransportMode());
            TransferMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.TransportTypeId = modename.get(position).getTransportTypeId();
                    Common.TransportMode = modename.get(position).getTransportMode();
                    notifyDataSetChanged();
                }
            });

            TransferMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Common.TransportTypeId = modename.get(position).getTransportTypeId();
                    Common.TransportMode = modename.get(position).getTransportMode();
                    notifyDataSetChanged();
                }
            });
            return view;
        }
    }
}

