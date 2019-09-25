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
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
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
import android.widget.Filter;
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
import com.zebra.main.adapter.ArrivalAdapter;
import com.zebra.main.adapter.AgencyAdapter;
import com.zebra.main.adapter.CustomAdapter;
import com.zebra.main.adapter.DriverAdapter;
import com.zebra.main.adapter.FellingSectionAdapter;
import com.zebra.main.adapter.TruckAdapter;
import com.zebra.main.model.AdvanceSearchModel;
import com.zebra.main.model.ExternalDB.AgencyDetailsModel;
import com.zebra.main.model.ExternalDB.DriverDetailsModel;
import com.zebra.main.model.ExternalDB.LoadedModel;
import com.zebra.main.model.ExternalDB.LocationsModel;
import com.zebra.main.model.ExternalDB.TransportModesModel;
import com.zebra.main.model.ExternalDB.TruckDetailsModel;
import com.zebra.main.model.InvTransfer.InventoryTransferScannedResultModel;
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

public class InventoryTransferActivity extends Activity implements SdlScanListener {
    static final private boolean saveSnapshot = false;
    private static final String TAG = "InventoryTransfer";
    AlertDialogManager alert = new AlertDialogManager();
    String TreeNO, FellingID;
    Intent service;
    private EditText ScanValueETxT = null, treeNoEDT, fellingIDEDT;
    private TextView TotalScannedCount, sbbLabel_TXT, NoValueFoundTxT, vbb_txt, FromLocationTxT, VolumeTXT;
    private LinearLayout scannerListLay = null, advanceSearchLAY;
    private ImageView image = null, AS_NovalueFound;
    // BarCodeReader specifics
    private BarCodeReader bcr = null;
    private PowerManager.WakeLock mWakeLock = null;
    private int motionEvents = 0, modechgEvents = 0, snapNum = 0;
    boolean bind = false;
    private SdlScanService scanService;
    private InventoryTransferAdapter transferadapter;
    private RecyclerView ScannedResultLV, advanceSearchRLV;
    Spinner ToLocationSpin, TransportAgencySpin, TruckPlateNumberSpin, FellingSecSpinner, DriverSpin;
    AutoCompleteTextView ToLocationATXT, DriverATXT, AgencyATXT, TruckDetialsATXT;
    FloatingActionButton TransferScanBtn;
    ListView TransferMode_LV, LoadedBy_LV;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;
    private static HSSFWorkbook myWorkBook = new HSSFWorkbook();
    private static HSSFSheet mySheet = myWorkBook.createSheet();
    MediaPlayer beepsound, wronBuzzer;
    LinearLayoutManager horizontalLayoutManager;
    TransportAdapterInside transportAdapter;
    LoadedAdapterInside loadedAdapter;
    ArrivalAdapter arrivalAdapter;
    CustomAdapter customAdapter;
    TruckAdapter truckAdapter;
    AgencyAdapter agencyAdapter;
    DriverAdapter driverAdapter;
    FellingSectionAdapter fellingSectionAdapter;
    AlertDialog.Builder Removebuilder = null;
    AlertDialog RemoveAlert = null;
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    private AdvanceSearchResultAdapter advancedSearchadapter;
    TSCActivity tsc = new TSCActivity();
    PrintSlipsClass printSlip;
    LinearLayout EditOptionFlagLay, EditOptionFlagLay2;
    StringtreeNoadapter StringtreeNoadapter;


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

    // Called with the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventorytransfer);
        Initialization();
        //ImportDataBaseFromInternalStorage();
        mDBExternalHelper = new ExternalDataBaseHelperClass(InventoryTransferActivity.this);
        mDBInternalHelper = new InternalDataBaseHelperClass(InventoryTransferActivity.this);
        printSlip = new PrintSlipsClass(InventoryTransferActivity.this);
        //View existing ListView
        ViewTransfeList(Common.IsEditorViewFlag);
        getToLocationsforSpinner();
        getdriverforSpinner();
        getagencyforSpinner();
        gettruckforSpinner();
        gettransModeforView();
        getLoadedByView();

        HideKeyboard();
        //enableSwipeToDeleteAndUndo();
        vbb_txt.setText(Common.VBB_Number);
        if (Common.IsTransferEditListFlag == false) {
            TransferEditValues();
        } /*else {
            Common.ToLocaTransID = -1;
            Common.TransferAgencyID = -1;
            Common.DriverID = -1;
            Common.TransportId = -1;
        }*/
        Click_Listener();
        GetTransferScannedResultList();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_F9)) {
            ScanValueETxT.setText("");
            Common.ScanMode = true;
            scanService.doDecode();
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Signout("Are you sure you want to close?");
        }
        return true;
    }

    public void ViewTransfeList(boolean EdtFlag) {
        if (EdtFlag == false) {
            EditOptionFlagLay.setVisibility(View.VISIBLE);
            EditOptionFlagLay2.setVisibility(View.VISIBLE);
            TransferScanBtn.setEnabled(false);
            findViewById(R.id.transferNXTtxt).setEnabled(false);
            findViewById(R.id.advancedsearchTXT).setEnabled(false);
            ScanValueETxT.setEnabled(false);
            findViewById(R.id.transferEnterIMG).setEnabled(false);
            findViewById(R.id.transfer_printTXT).setEnabled(false);
        } else {
            EditOptionFlagLay.setVisibility(View.GONE);
            //if (EditOptionFlagLay2.getVisibility() == View.VISIBLE) {
            EditOptionFlagLay2.setVisibility(View.GONE);
            // }
            TransferScanBtn.setEnabled(true);
            findViewById(R.id.transferNXTtxt).setEnabled(true);
            findViewById(R.id.advancedsearchTXT).setEnabled(true);
            ScanValueETxT.setEnabled(true);
            findViewById(R.id.transferEnterIMG).setEnabled(true);
            findViewById(R.id.transfer_printTXT).setEnabled(true);
        }
    }

    public void TransferEditValues() {
        try {
            // FromLocation Name from FromLocationID
            Common.ConcessionList.clear();
            Common.ConcessionList = mDBExternalHelper.getAllConcessionNames(Common.FromTransLocID);
            Common.FromLocationname = Common.ConcessionList.get(0).getConcessionName();
            FromLocationTxT.setText(Common.FromLocationname);

            for (LocationsModel item : Common.LocationList) {
                if (item.getToLocationId() == Common.ToLocaTransID) {
                    ToLocationSpin.setSelection(item.getID());
                    Log.v(TAG, "To>>>>>LocationSpin " + item.getLocation() + item.getToLocationId());
                    ToLocationATXT.setText(item.getLocation());
                }
            }
            for (DriverDetailsModel item : Common.DriverList) {
                if (item.getTruckDriverId() == Common.DriverID) {
                    DriverSpin.setSelection(item.getID());
                    Log.v(TAG, "To>>>>>DriverSpin " + item.getDriverName() + item.getTruckDriverId());
                    DriverATXT.setText(item.getDriverName());
                }
            }
            for (AgencyDetailsModel item : Common.TransportAgencyList) {
                if (item.getAgencyId() == Common.TransferAgencyID) {
                    Log.v(TAG, "To>>>>>TransportAgencySpin " + item.getAgencyName() + item.getAgencyId());
                    TransportAgencySpin.setSelection(item.getID());
                    AgencyATXT.setText(item.getAgencyName());
                }
            }
            for (TruckDetailsModel item : Common.TruckDeatilsList) {
                if (item.getTransportId() == Common.TransportId) {
                    Log.v(TAG, "To>>>>>TruckDetialsATXT " + item.getTruckLicensePlateNo() + item.getTransportId());
                    TruckPlateNumberSpin.setSelection(item.getID());
                    Common.TrucklicensePlateNo = item.getTruckLicensePlateNo();
                    TruckDetialsATXT.setText(item.getTruckLicensePlateNo());

                }
            }
        /*for (int i = 0; i < Common.TransportModeList.size(); i++) {
            if (Common.TransportModeList.get(i).getTransportTypeId() == Common.TransportTypeId) {
                TransferMode_LV.setSelection(i);
            }
        }*/
        } catch (Exception ex) {

        }
    }

    private void Initialization() {
        TransferScanBtn = findViewById(R.id.TransferScanBTN);
        findViewById(R.id.TransferScanBTN).setOnClickListener(mDecodeListener);
        findViewById(R.id.transferEnterIMG).setOnClickListener(mScannerListener);
        findViewById(R.id.closeInventoryTransfer).setOnClickListener(mCloseActivityListener);
        findViewById(R.id.transfer_printTXT).setOnClickListener(mPrintListener);
        scannerListLay = findViewById(R.id.scannerListlayout);
        ScanValueETxT = findViewById(R.id.scanValueEDTXT);
        TotalScannedCount = findViewById(R.id.TotalScannedCount);
        ScannedResultLV = findViewById(R.id.inventorytransfer_lv);
        advanceSearchRLV = findViewById(R.id.as_scan_lv);

        ToLocationSpin = findViewById(R.id.tolocation_spinner);
        DriverSpin = findViewById(R.id.driver_sppinner);
        TransportAgencySpin = findViewById(R.id.transagency_spinner);
        TruckPlateNumberSpin = findViewById(R.id.truckplateumber_spinner);
        FellingSecSpinner = findViewById(R.id.fellingsection_spinner);

        ToLocationATXT = findViewById(R.id.tolocation_ATxT);
        DriverATXT = findViewById(R.id.driver_ATxT);
        AgencyATXT = findViewById(R.id.transagency_ATxT);
        TruckDetialsATXT = findViewById(R.id.truckplateumber_ATxT);

        TransferMode_LV = findViewById(R.id.transferMode_LV);
        LoadedBy_LV = findViewById(R.id.loadedby_LV);
        vbb_txt = findViewById(R.id.VBB_numberTXT);
        FromLocationTxT = findViewById(R.id.fromLocationTXT);
        VolumeTXT = findViewById(R.id.TotalScannedVolumeTransfer);

        //FellingSpinnerTxT = findViewById(R.id.fellingSpinnerTxT);

        findViewById(R.id.advancedsearchTXT).setOnClickListener(as_OpenrListener);
        findViewById(R.id.as_closeActivity).setOnClickListener(as_mCloseActivityListener);
        findViewById(R.id.as_scannerList).setOnClickListener(as_mScannerListener);
        findViewById(R.id.as_NovalueFound).setOnClickListener(as_mAddListener);

        findViewById(R.id.transferNXTtxt).setOnClickListener(mAddTransferIDListener);

        treeNoEDT = findViewById(R.id.as_treenoEDT);
        fellingIDEDT = findViewById(R.id.as_fellingEDT);

        advanceSearchLAY = findViewById(R.id.advancesearchLAY);
        advanceSearchLAY.setVisibility(View.GONE);
        sbbLabel_TXT = findViewById(R.id.sbbLabel_TXT);
        NoValueFoundTxT = findViewById(R.id.NovalueFound);
        AS_NovalueFound = findViewById(R.id.as_NovalueFound);
        AS_NovalueFound.setVisibility(View.GONE);
        EditOptionFlagLay = findViewById(R.id.transfer_editLayout);
        EditOptionFlagLay2 = findViewById(R.id.transfer_editLayout2);
    }


    public void NextAlertDialog(String ErrorMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(InventoryTransferActivity.this);
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
                            AddTransferList();
                            dialog.cancel();
                        } catch (Exception ex) {
                            AlertDialogBox("NextAlertDialog", ex.toString(), false);
                        }
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void Signout(String ErrorMessage) {
        if (Common.IsEditorViewFlag == false) {
            InventoryActivty();
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(InventoryTransferActivity.this);
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
                               /* Common.InventorytransferScannedResultList = mDBInternalHelper.getInventoryTransferWithVBBNumber(Common.VBB_Number, Common.TransferID);
                                Log.v(TAG, "setNegativeButton" + Common.InventorytransferScannedResultList.size());
                                if (Common.InventorytransferScannedResultList.size() == 0) {
                                    // Remove Transfer List If empty
                                    DeleteTransferListandTransferScannedList(Common.TransferID);
                                }*/
                                UpdateTransferIDList();
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

    View.OnClickListener as_mAddListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Common.SbbLabel = sbbLabel_TXT.getText().toString();
                Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                //Common.IMEI = getImeiNumber();
                Common.WoodSpieceID = "";
                Common.FellingSectionId = fellingIDEDT.getText().toString();
                Common.TreeNumber = treeNoEDT.getText().toString();
                Common.WoodSpieceCode = "";
                Common.QualityName = "";
                //Common.OldQualityName = "";
                Common.BarCode = "NA-" + Common.SbbLabel;
                Common.IsActive = 1;
                Common.EntryMode = 3;
                // VVb Limits
                if (Common.FromLocationID == 6000 || Common.FromLocationID == 2) {//22-july-2019
                    //if (Common.FromLocationID == 6000 && Common.ToLocaTransID == 2 && Common.TransportTypeId == 2) {
                } else {
                    if (Common.InventorytransferScannedResultList.size() <= Common.VVBLimitation) {
                    } else {
                        wronBuzzer.start();
                        AlertDialogBox("VBB Limit", "For one VBB Number should have 22 items", false);
                        return;
                    }
                }
                boolean EmptyNullFlags = mDBInternalHelper.getInventoryTransferduplicateCheck(Common.VBB_Number, Common.TransferID, Common.SbbLabel, Common.FromLocationname);
                if (EmptyNullFlags == true) {
                    Common.InventorytransferScannedResultList.clear();
                    wronBuzzer.start();
                    GetTransferScannedResultList();
                    return;
                } else {
                    boolean InisertAdvanceFlag = mDBInternalHelper.insertInventoryTransferResultFlag(Common.VBB_Number, Common.TransferID, Common.FromLocationname, Common.ToLocationName,
                            Common.SbbLabel, Common.BarCode, "0.00", "0.00", Common.UserID, Common.DateTime, Common.WoodSpieceID, Common.WoodSpieceCode,
                            Common.EntryMode, Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), "", Common.FellingSectionId, Common.TreeNumber,
                            Common.QualityName);
                    Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                    AS_NovalueFound.setVisibility(View.GONE);
                    advanceSearchLAY.setVisibility(View.GONE);
                }
                //Scanned Result Refresh
                GetTransferScannedResultList();
                UpdateTransferIDList();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    View.OnClickListener mPrintListener = new View.OnClickListener() {
        public void onClick(View v) {
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
    };

    View.OnClickListener as_OpenrListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (DetailsValidation(Common.FromLocationname, ToLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
                Common.IsSBBLabelCorrected = false;
                sbbLabel_TXT.setText("");
                AdvanceSearchRefresh();
            }
        }
    };
    View.OnClickListener mAddTransferIDListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                if (DetailsValidation(Common.FromLocationname, ToLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
              /*  boolean DuplicateFlag = mDBInternalHelper.getInventoryTransferIdDuplicateCheck(Common.TransferID);
                if (DuplicateFlag == true) {*/
                    if (Common.InventorytransferScannedResultList.size() > 0) {
                        if (Common.InventorytransferScannedResultList.size() < Common.MinimumScannedItemSize) {
                            AlertDialogBox(CommonMessage(R.string.TransferHead), "add minimum five items ", false);
                            return;
                        }
                        UpdateTransferIDList();
                        if (Common.InventorytransferScannedResultList.size() < (Common.VVBLimitation + 1)) {
                            NextAlertDialog("Transfer Count is less then 22, Are you sure to create next list?");
                        } else {
                            AddTransferList();
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.TransferHead), "add minimum five items ", false);
                    }
               /* } else {
                    AddTransferList();
                }*/
                }
            } catch (Exception ex) {
                AlertDialogBox(CommonMessage(R.string.TransferHead), ex.toString(), false);
            }
        }
    };

    View.OnClickListener as_mScannerListener = new View.OnClickListener() {
        public void onClick(View v) {
            AdvanceSearch();
        }
    };

    View.OnClickListener as_mCloseActivityListener = new View.OnClickListener() {
        public void onClick(View v) {
            advanceSearchLAY.setVisibility(View.GONE);
            GetTransferScannedResultList();
        }
    };

    private void getToLocationsforSpinner() {
        try {
            // FromLocation Name from FromLocationID
            Common.ConcessionList.clear();
            Common.ConcessionList = mDBExternalHelper.getAllConcessionNames(Common.FromLocationID);
            Common.FromLocationname = Common.ConcessionList.get(0).getConcessionName();
            FromLocationTxT.setText(Common.FromLocationname);
            // Felling Section Spinner
            int LocationTypeID = Common.ConcessionList.get(0).getLocationType();
            //int LocationTypeID = mDBExternalHelper.getLocationType(Common.FromLocationID);
            if (LocationTypeID == 1) {
                Common.FellingSectionList.clear();
                Common.FellingSectionList = mDBExternalHelper.getFellingSectionDetails(Common.FromLocationID);
                fellingSectionAdapter = new FellingSectionAdapter(getApplicationContext(), Common.FellingSectionList);
                FellingSecSpinner.setAdapter(fellingSectionAdapter);
                FellingSecSpinner.setVisibility(View.GONE);
                //FellingSpinnerTxT.setVisibility(View.VISIBLE);
            } else {
                FellingSecSpinner.setVisibility(View.GONE);
                //FellingSpinnerTxT.setVisibility(View.GONE);
            }
            // To Location Spinner
            Common.LocationList.clear();
            Common.LocationList = mDBExternalHelper.getToLocations();
            // Common.ToLocaTransID = Common.LocationList.get(0).getToLocationId();
            customAdapter = new CustomAdapter(getApplicationContext(), Common.LocationList);
            ToLocationSpin.setAdapter(customAdapter);
            //ToLocationSpin.setSelection(Common.LocationList.size() - 1);

            Common.ConcessionListStringList = new String[Common.LocationList.size()];
            for (int i = 0; i < Common.LocationList.size(); i++) {
                Common.ConcessionListStringList[i] = Common.LocationList.get(i).getLocation();
            }
            StringtreeNoadapter = new StringtreeNoadapter(this,
                    android.R.layout.simple_dropdown_item_1line, Common.ConcessionListStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            ToLocationATXT.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
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
            //Common.FellingRegTreeNoStringList = Common.FellingRegTreeFilterList.toArray(new String[0]);
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

    private void getLoadedByView() {
        try {
            Common.LoadedByList.clear();
            Common.LoadedByList = mDBExternalHelper.getAllLoadedByDetails();
            loadedAdapter = new LoadedAdapterInside(getApplicationContext(), Common.LoadedByList);
            LoadedBy_LV.setAdapter(loadedAdapter);
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
                    //AlertDialogBox("Barcode Length", CommonMessage(R.string.BarCodeLenghtMsg), false);
                    if (Common.ScannedEditTXTFlag == true) {
                        ScanValueETxT.setError(CommonMessage(R.string.BarCodeLenghtMsg));
                    }
                }
            }
        });
        ToLocationSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Common.ToLocaTransID = Common.LocationList.get(position).getToLocationId();
                    Common.ToLocationName = Common.LocationList.get(position).getLocation();
                } catch (Exception ex) {
                    //ImportDataBaseFromInternalStorage();
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

   /*     FromLocationSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Common.FromLocationID = Common.ConcessionList.get(position).getFromLocationId();
                Common.FromLocationname = Common.ConcessionList.get(position).getConcessionName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

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
       /* TransferMode_LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Common.TransportTypeId = Common.TransportModeList.get(position).getTransportTypeId();
                Common.TransportMode = Common.TransportModeList.get(position).getTransportMode();
            }
        });*/

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
        ToLocationATXT.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ToLocationATXT.requestFocus();
                ToLocationATXT.showDropDown();
                return false;
            }
        });

        ToLocationATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String locModelStr = (String) parent.getItemAtPosition(position);
                for (LocationsModel locModel : Common.LocationList)
                    if (locModelStr.equals(locModel.getLocation())) {
                        Common.ToLocaTransID = locModel.getToLocationId();
                        Common.ToLocationName = locModel.getLocation();
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
            scanService.setOnScanListener(InventoryTransferActivity.this);
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
        GetTransferScannedResultList();
        super.onResume();
    }

    View.OnClickListener mDecodeListener = new View.OnClickListener() {
        public void onClick(View v) {
            ScanValueETxT.setText("");
            Common.ScanMode = true;
            Common.ScannedEditTXTFlag = false;
            Common.EntryMode = 1;
            if (DetailsValidation(Common.FromLocationname, ToLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
                scanService.doDecode();
            }
        }
    };

    View.OnClickListener mScannerListener = new View.OnClickListener() {
        public void onClick(View v) {
            Common.ScanMode = false;
            if (DetailsValidation(Common.FromLocationname, ToLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
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

    public boolean DetailsValidation(String FromLoc_Str, String ToLoc_ID, String TransAge_ID, String Driver_ID, String Truck_ID) {
        boolean Validattion = true;
        if (isNullOrEmpty(FromLoc_Str)) {
            Validattion = false;
            AlertDialogBox("From Location", "Verify from Location", false);
        }
        if (isNullOrEmpty(String.valueOf(ToLoc_ID))) {
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
       /* if (ToLoc_ID == -1) {
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
        }*/
        return Validattion;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str == "null";
    }

    View.OnClickListener mCloseActivityListener = new View.OnClickListener() {
        public void onClick(View v) {
            Signout("Are you sure you want to close?");
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
    public void ScannedResult(String s) {
        HideKeyboard();
        try {
            if (s != null && s.length() > 0) {
                if (Common.ScanMode == true) {
                    Common.BarCode = s;
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
                    Common.SbbLabel = s;
                    Common.BarCode = "NA-" + Common.SbbLabel;
                }
                Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                Common.IsActive = 1;
                // Checked Duplicate In Internal Tabel
                boolean EmptyNullFlags = mDBInternalHelper.getInventoryTransferduplicateCheck(Common.VBB_Number, Common.TransferID, Common.SbbLabel, Common.FromLocationname);
                if (EmptyNullFlags == true) {
                    Common.InventorytransferScannedResultList.clear();
                    wronBuzzer.start();
                    //Scanned Result Refresh
                    GetTransferScannedResultList();
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
                    Common.IsSBBLabelCorrected = false;
                } else {
                    Common.WoodSpieceID = "";
                    Common.WoodSpieceCode = "";
                    Common.FellingSectionId = "";
                    Common.QualityName = "";
                    Common.TreeNumber = "";
                    Common.Length = "0.00";
                    Common.Volume = "0.00";
                    Common.IsSBBLabelCorrected = true;
                    sbbLabel_TXT.setText(Common.SbbLabel);
                    AS_NovalueFound.setVisibility(View.VISIBLE);
                    AdvanceSearchRefresh();
                    return;
                }
                //Condition implemented 12-june-2019-(sabaru-dp-ponton)
                //Sabaru and Exotic no limits
                if (Common.FromLocationID == 6000 || Common.FromLocationID == 2) {
                    //if (Common.FromLocationID == 6000 && Common.ToLocaTransID == 2 && Common.TransportTypeId == 2) {
                    InsertTransferScannedResultTable();
                } else {
                    if (Common.InventorytransferScannedResultList.size() <= Common.VVBLimitation) {
                        InsertTransferScannedResultTable();
                    } else {
                        wronBuzzer.start();
                        AlertDialogBox("VBB Limit", "For one VBB Number should have 22 items", false);
                    }
                }
                Common.EntryMode = 1;
                Common.ScannedEditTXTFlag = false;
                ScanValueETxT.setText(s);
            }
        } catch (Exception ex) {
            Log.v(TAG, "decode failed" + ex.toString());
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
                            boolean EmptyNullFlags = mDBInternalHelper.getInventoryTransferduplicateCheck(Common.VBB_Number, Common.TransferID, Common.SbbLabel, Common.FromLocationname);
                            if (EmptyNullFlags == true) {
                                Common.InventorytransferScannedResultList.clear();
                                wronBuzzer.start();
                                //Scanned Result Refresh
                                GetTransferScannedResultList();
                                return;
                            }
                            if (Common.FromLocationID == 6000 || Common.FromLocationID == 2) {
                                //if (Common.FromLocationID == 6000 && Common.ToLocaTransID == 2 && Common.TransportTypeId == 2) {

                            } else {
                                if (Common.InventorytransferScannedResultList.size() <= Common.VVBLimitation) {
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
                            try {
                                boolean InisertAdvanceFlag = false;
                                if (Common.IsSBBLabelCorrected == true) {
                                    Common.OrgSBBLabel = sbbLabel_TXT.getText().toString();
                                    Common.BarCode = "NA-" + Common.OrgSBBLabel;
                                    InisertAdvanceFlag = mDBInternalHelper.insertInventoryTransferResultFlag(Common.VBB_Number, Common.TransferID, Common.FromLocationname,
                                            Common.ToLocationName, Common.OrgSBBLabel, Common.BarCode, Common.Length, Common.Volume, Common.UserID, Common.DateTime, Common.WoodSpieceID,
                                            Common.WoodSpieceCode, Common.EntryMode, Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), Common.SbbLabel, Common.FellingSectionId,
                                            Common.TreeNumber, Common.QualityName);
                                } else {
                                    Common.OrgSBBLabel = "";
                                    Common.BarCode = "NA-" + AdvanceSearchResultList.get(position).getSBBLabel();
                                    InisertAdvanceFlag = mDBInternalHelper.insertInventoryTransferResultFlag(Common.VBB_Number, Common.TransferID, Common.FromLocationname, Common.ToLocationName,
                                            Common.SbbLabel, Common.BarCode, Common.Length, Common.Volume, Common.UserID, Common.DateTime, Common.WoodSpieceID, Common.WoodSpieceCode,
                                            Common.EntryMode, Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), Common.OrgSBBLabel, Common.FellingSectionId, Common.TreeNumber, Common.QualityName);
                                }
                                if (InisertAdvanceFlag == true) {
                                    Common.OrgSBBLabel = "";
                                    Common.EntryMode = 1;
                                    advanceSearchLAY.setVisibility(View.GONE);
                                    // Scanned Result List Refresh
                                    GetTransferScannedResultList();
                                    UpdateTransferIDList();
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

    public void InsertTransferScannedResultTable() {
        beepsound.start();
        // Insert values into DB
        mDBInternalHelper.insertInventoryTransferResultFlag(Common.VBB_Number, Common.TransferID, Common.FromLocationname, Common.ToLocationName,
                Common.SbbLabel, Common.BarCode, Common.Length, Common.Volume, Common.UserID, Common.DateTime, Common.WoodSpieceID,
                Common.WoodSpieceCode, Common.EntryMode, Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), "", Common.FellingSectionId,
                Common.TreeNumber, Common.QualityName);
        //Scanned Result Refresh
        GetTransferScannedResultList();
        UpdateTransferIDList();
    }

    public static <T> boolean IsNullOrEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    public static int booleanToInt(boolean value) {
        // Convert true to 1 and false to 0.
        return value ? 1 : 0;
    }

    private void GetTransferScannedResultList() {
        try {
            Common.InventorytransferScannedResultList = mDBInternalHelper.getInventoryTransferWithVBBNumber(Common.VBB_Number, Common.TransferID);
            //String TotalSannedSize = mDBInternalHelper.ColculateInventoryTransferItems(Common.TransferID);
            Double RemoveVolumeSum = 0.00;
            if (Common.InventorytransferScannedResultList.size() > 0) {
                transferadapter = new InventoryTransferAdapter(Common.InventorytransferScannedResultList, Common.QulaityDefaultList, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                ScannedResultLV.setLayoutManager(horizontalLayoutManager);
                transferadapter.notifyDataSetChanged();
                ScannedResultLV.setAdapter(transferadapter);
                ScannedResultLV.setVisibility(View.VISIBLE);
                RemoveVolumeSum = TotalVolume(Common.InventorytransferScannedResultList);
            } else {
                ScannedResultLV.setVisibility(View.GONE);
            }
            TotalScannedCount.setText(String.valueOf(Common.InventorytransferScannedResultList.size()));
            VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(RemoveVolumeSum)));
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

    public class InventoryTransferAdapter extends RecyclerView.Adapter<InventoryTransferAdapter.GroceryViewHolder> {
        private List<InventoryTransferScannedResultModel> ScannedResultList;
        private List<String> QualitySpinner;
        Context context;

        public InventoryTransferAdapter(List<InventoryTransferScannedResultModel> ScannedResultList, ArrayList<String> SpinnerData, Context context) {
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

        public InventoryTransferScannedResultModel getItem(int position) {
            return ScannedResultList.get(position);
        }

        @Override
        public InventoryTransferAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transfer_result_infliator, parent, false);
            InventoryTransferAdapter.GroceryViewHolder gvh = new InventoryTransferAdapter.GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(InventoryTransferAdapter.GroceryViewHolder holder, final int position) {
            holder.Barcode.setText(ScannedResultList.get(position).getSbbLabel());
            holder.WoodSpiceCode.setText(ScannedResultList.get(position).getWoodSpieceCode());
            holder.ClassificationTXT.setText(ScannedResultList.get(position).getQualitiy());
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
            if (ScannedResultList.get(position).getQualitiy() != null) {
                int spinnerPosition = adapter.getPosition(ScannedResultList.get(position).getQualitiy());
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
                            boolean UpdateFlag = mDBInternalHelper.UpdateQualityTansferList(Common.QualityName, sBBLabel, Common.TransferID);
                            Common.isSpinnerTouched = false;
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
            TextView Barcode, WoodSpiceCode, ClassificationTXT;
            Spinner QualitySpinner;
            LinearLayout Background, Remove;

            public GroceryViewHolder(View view) {
                super(view);
                Barcode = view.findViewById(R.id.transfer_SbbLabel);
                WoodSpiceCode = view.findViewById(R.id.transfer_woodspiceCode);
                ClassificationTXT = view.findViewById(R.id.transfer_Quality);
                QualitySpinner = view.findViewById(R.id.transfer_QualitySpinner);
                Background = view.findViewById(R.id.resultlayoutBackground);
                Remove = view.findViewById(R.id.removeBarCode_inven);
            }
        }

    }


    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(InventoryTransferActivity.this, Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public String CommonMessage(int Common_Msg) {
        return InventoryTransferActivity.this.getResources().getString(Common_Msg);
    }


    public void RemoveMessage(String ErrorMessage) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(InventoryTransferActivity.this);
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
                        try {
                            boolean Isdelete = mDBInternalHelper.RemoveFromtransferlistview(Common.RemoveSBBLabel, 0, Common.TransferID);
                            if (Isdelete == true) {
                                Toast.makeText(InventoryTransferActivity.this, "Successfully Removed from List", Toast.LENGTH_LONG).show();
                                GetTransferScannedResultList();
                                UpdateTransferIDList();
                            }
                        } catch (Exception Ex) {

                        }
                        dialog.cancel();
                    }
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    Handler TransferListPrintHan = new Handler();
    Runnable TransferListPrintRun = new Runnable() {
        @Override
        public void run() {
            try {
                Common.VolumeSum = TotalVolume(Common.InventorytransferScannedResultList);
                Common.Count = Common.InventorytransferScannedResultList.size();
                tsc.clearbuffer();
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
            }
        }
    };

    public void PrintoutEnd() {
        tsc.closeport();
        TSCConnInt = -1;
        Common.IsPrintBtnClickFlag = true;
    }

    public void UpdateTransferIDList() {
        try {
            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            Double RemoveVolumeSum = TotalVolume(Common.InventorytransferScannedResultList);
            // Update values into TransferID
            boolean TransferIDFlag = mDBInternalHelper.UpdateInventoryTransferID(Common.EndDate, Common.ToLocaTransID, Common.FromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TrucklicensePlateNo, Common.UserID, Common.InventorytransferScannedResultList.size(), Common.TransferID, String.valueOf(RemoveVolumeSum), 1, Common.TransferUniqueID, Common.LoadedTypeID);
        } catch (Exception ex) {
            AlertDialogBox("UpdateTransferIDList", ex.toString(), false);
        }
    }

    public double TotalVolume(ArrayList<InventoryTransferScannedResultModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (InventoryTransferScannedResultModel inventoTransScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(inventoTransScanModel.getVolume());
        }
        return TotVolume;
    }

    public void AddTransferList() {
        try {
            Common.IsActive = 0;
            Common.SyncTime = "";
            Common.VolumeSum = 0.0;
            Common.Count = 0;
            Common.VBB_Number = "";//vbb_txt.getText().toString();
            Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            boolean ListIdFlag = mDBInternalHelper.insertInventoryTransferID(Common.VBB_Number, Common.IMEI, Common.ToLocaTransID, Common.StartDate, Common.EndDate,
                    Common.FromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TrucklicensePlateNo, Common.UserID, 0,
                    Common.SyncStatus, Common.SyncTime, "0.00", 1, "");
            /*boolean ListIdFlag = mDBInternalHelper.insertInventoryTransferID(Common.VBB_Number, Common.IMEI, Common.ToLocaTransID, Common.StartDate, Common.EndDate,
                    Common.FromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TrucklicensePlateNo, Common.UserID, Common.Count,
                    Common.SyncStatus, Common.SyncTime, Common.Volume, 1, Common.TransferUniqueID);*/
            if (ListIdFlag == true) {
                Common.InventoryTransferList = mDBInternalHelper.getInventoryTransferIdList();
                if (Common.InventoryTransferList.size() > 0) {
                    Common.TransferID = Integer.parseInt(mDBInternalHelper.getLastTransferID());
                    String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                    //Common.TransferID = Common.InventoryTransferList.get(Common.InventoryTransferList.size() - 1).getTransferID();
                    String DeviceID = "";
                    if (String.valueOf(Common.LDeviceID).length() == 1) {
                        DeviceID = "0" + String.valueOf(Common.LDeviceID);
                    } else {
                        DeviceID = String.valueOf(Common.LDeviceID);
                    }
                    Common.TransferUniqueID = String.valueOf(DateUniqueFormat + DeviceID + Common.TransferID);
                    GetTransferScannedResultList();
                }
            }
            // Refresh Page
            //RefreshActivity();
        } catch (Exception ex) {
            AlertDialogBox("TC-AddTransferList", ex.toString(), false);
        }
    }

    private void RefreshActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
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
                TransferMode.setChecked(true);
                Common.TransportMode = modename.get(position).getTransportMode();
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

    public class LoadedAdapterInside extends BaseAdapter {
        Context context;
        ArrayList<LoadedModel> modename;
        LayoutInflater inflter;

        public LoadedAdapterInside(Context applicationContext, ArrayList<LoadedModel> list) {
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
            view = inflter.inflate(R.layout.loaded_infliator, null);
            Log.d("loadedid", ">>>" + modename.get(position).getLoadedid());
            CheckBox LoadedMode = view.findViewById(R.id.loadedMode_infi);
            if (modename.get(position).getIsActive() == 1) {
                LoadedMode.setVisibility(View.VISIBLE);
            } else {
                LoadedMode.setVisibility(View.GONE);
            }
            if (Common.LoadedTypeID == modename.get(position).getLoadedid()) {
                LoadedMode.setChecked(true);
                Common.LoadedName = modename.get(position).getName();
            } else {
                LoadedMode.setChecked(false);
            }
            LoadedMode.setText(modename.get(position).getName());
            LoadedMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.LoadedTypeID = modename.get(position).getLoadedid();
                    Common.LoadedName = modename.get(position).getName();
                    notifyDataSetChanged();
                }
            });

            LoadedMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Common.LoadedTypeID = modename.get(position).getLoadedid();
                    Common.LoadedName = modename.get(position).getName();
                    notifyDataSetChanged();
                }
            });
            return view;
        }
    }

    public void InventoryActivty() {
        Intent _gwwIntent = new Intent(InventoryTransferActivity.this, InventoryActivity.class);
        startActivity(_gwwIntent);
    }

    /*Transfer List and Scanned Items - Remove */
    public void DeleteTransferListandTransferScannedList(int transferID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteInventoryTransferListID(transferID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteInventoryTransferScanned(transferID);
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
        }
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
}

