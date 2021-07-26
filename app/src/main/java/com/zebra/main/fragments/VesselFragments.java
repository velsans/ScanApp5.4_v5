package com.zebra.main.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.tscdll.TSCActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.GsonBuilder;
import com.zebra.R;
import com.zebra.adc.decoder.BarCodeReader;
import com.zebra.android.jb.Preference;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.main.adapter.AgencyAdapter;
import com.zebra.main.adapter.CustomAdapter;
import com.zebra.main.adapter.DriverAdapter;
import com.zebra.main.adapter.FellingSectionAdapter;
import com.zebra.main.adapter.TruckAdapter;
import com.zebra.main.api.ApiClient;
import com.zebra.main.api.ApiInterface;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.model.externaldb.AgencyDetailsModel;
import com.zebra.main.model.externaldb.DriverDetailsModel;
import com.zebra.main.model.externaldb.LoadedModel;
import com.zebra.main.model.externaldb.LocationsModel;
import com.zebra.main.model.externaldb.TransportModesModel;
import com.zebra.main.model.externaldb.TruckDetailsModel;
import com.zebra.main.model.vessel.VesselAdvanceSearchModel;
import com.zebra.main.model.vessel.VesselCancelTransferModel;
import com.zebra.main.model.vessel.VesselCreateModel;
import com.zebra.main.model.vessel.VesselInputAdvanceSearchModel;
import com.zebra.main.model.vessel.VesselInputList;
import com.zebra.main.model.vessel.VesselListModel;
import com.zebra.main.model.vessel.VesselLogDetailsPrintModel;
import com.zebra.main.model.vessel.VesselLogsDetailsModel;
import com.zebra.main.model.vessel.VesselPrintOutputModel;
import com.zebra.main.model.vessel.VesselScannedDetailsModel;
import com.zebra.main.model.vessel.VesselScannedLogModel;
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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VesselFragments extends Fragment implements SdlScanListener {

    View vesselView;
    VesselReceiver vessel_reciver;
    VesselTransportAdapter vesselTransportadapter;
    LinearLayoutManager horizontalLayoutManager;
    AlertDialogManager alert = new AlertDialogManager();
    TSCActivity tsc = new TSCActivity();
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    PrintSlipsClass printSlip;
    Button submitVBBTXT;
    ApiInterface vesselTransportAPI = null;

    static final private boolean saveSnapshot = false;
    Intent vessel_service;
    private EditText ScanValueETxT = null, treeNoEDT, fellingIDEDT, submitVVPEDT;
    private TextView TotalScannedCount, vbb_txt, FromLocationTxT, VolumeTXT, TransportTypeIDTXT, TransferTopicsTxT, TotalFilteredCount,
            TotalFilteredVolume, NoValueFoundTxT, vessel_NoValueFoundTxT, TransferTopicTxT, as_NovalueFound;
    private LinearLayout scannerListLay = null, advanceSearchLAY, ProgressBarLay, vesselDetailsLAY;
    private ImageView image = null, TransferScanBtn;
    // BarCodeReader specifics
    private BarCodeReader vessel_bcr = null;
    private PowerManager.WakeLock vessel_mWakeLock = null;
    private int motionEvents = 0, modechgEvents = 0, snapNum = 0;
    boolean vessel_bind = false;
    private SdlScanService vessel_scanService;
    private VesselTransferAdapter vessellogsadapter;
    private RecyclerView ScannedResultLV, VesselTransportList, VesselTransportDateList, advanceSearchRLV;
    Spinner ToLocationSpin, TransportAgencySpin, TruckPlateNumberSpin, FellingSecSpinner, DriverSpin;
    AutoCompleteTextView ToLocationATXT, DriverATXT, AgencyATXT, TruckDetialsATXT;
    //FloatingActionButton TransferScanBtn;
    ListView TransferMode_LV, LoadedBy_LV;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private static HSSFWorkbook myWorkBook = new HSSFWorkbook();
    private static HSSFSheet mySheet = myWorkBook.createSheet();
    MediaPlayer beepsound, wronBuzzer;
    TransportAdapterInside transportAdapter;
    LoadedAdapterInside loadedAdapter;
    CustomAdapter customAdapter;
    TruckAdapter truckAdapter;
    AgencyAdapter agencyAdapter;
    DriverAdapter driverAdapter;
    FellingSectionAdapter fellingSectionAdapter;
    AlertDialog.Builder Removebuilder = null;
    AlertDialog RemoveAlert = null;
    LinearLayout EditOptionFlagLay, EditOptionFlagLay2;
    StringtreeNoadapter StringtreeNoadapter;
    DriverDetailsModel drivermodelArray;
    AgencyDetailsModel agencyModelArray;
    TruckDetailsModel truckModelArray;
    protected String TAG = getClass().getSimpleName();
    VesselInputAdvanceSearchModel veesel_searchModel;
    AdvanceSearchResultAdapter advancedSearchadapter;

    private class VesselReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            GetVesselTransportList();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        vesselPauseANDResume();
    }

    public void vesselPauseANDResume() {
        Log.i(TAG, "setUserVisibleHint-onPause");
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(vessel_reciver);
            if (vessel_scanService != null)
                vessel_scanService.setActivityUp(false);
            releaseWakeLock();
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
        Log.i(TAG, "setUserVisibleHint-onDestroy");
        try {
            if (vessel_bind) {
                if (!Preference.getScanSelfopenSupport(getActivity(), true)) {
                    getActivity().stopService(vessel_service);
                }
                getActivity().unbindService(vessel_serviceConnection);
                vessel_scanService = null;
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser == false) {
            vesselPauseANDResume();
        } else {
            if (Common.BarcodeScannerFlagForExport == true) {
                Log.i(TAG, "setUserVisibleHint-onResume");
                try {
                    vessel_service = new Intent(getActivity(), SdlScanService.class);
                    getActivity().bindService(vessel_service, vessel_serviceConnection, Context.BIND_AUTO_CREATE);
                    getActivity().startService(vessel_service);
                } catch (Exception ex) {
                    CrashAnalytics.CrashReport(ex);
                }
                try {
                    ScannedStatus("");//getResources().getString(R.string.app_name) + " v" + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
                    if (vessel_scanService != null)
                        vessel_scanService.setActivityUp(true);
                    acquireWakeLock();
                    vessel_reciver = new VesselReceiver();
                    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(vessel_reciver, new IntentFilter("VESSEL_REFRESH"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        try {
            if (vessel_bind) {
                if (!Preference.getScanSelfopenSupport(getActivity(), true)) {
                    getActivity().stopService(vessel_service);
                }
                getActivity().unbindService(vessel_serviceConnection);
                vessel_scanService = null;
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("Exception", ex.toString(), false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        try {
            vessel_service = new Intent(getActivity(), SdlScanService.class);
            getActivity().bindService(vessel_service, vessel_serviceConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(vessel_service);
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
        try {
            ScannedStatus("");//getResources().getString(R.string.app_name) + " v" + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
            if (vessel_scanService != null)
                vessel_scanService.setActivityUp(true);
            acquireWakeLock();
            vessel_reciver = new VesselReceiver();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(vessel_reciver,
                    new IntentFilter("VESSEL_REFRESH"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void Initialization() {
        ProgressBarLay = vesselView.findViewById(R.id.vessel_progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        VesselTransportList = vesselView.findViewById(R.id.vessel_TransportListView);
        VesselTransportDateList = vesselView.findViewById(R.id.vessel_TransportListViewDate);
        vessel_NoValueFoundTxT = vesselView.findViewById(R.id.vesselLogs_NovalueFound);
        NoValueFoundTxT = vesselView.findViewById(R.id.vesselTransport_NovalueFound);
        TotalFilteredCount = vesselView.findViewById(R.id.vessel_TotalFilteredCount);
        TotalFilteredVolume = vesselView.findViewById(R.id.vessel_TotalFilteredVolume);
        submitVBBTXT = vesselView.findViewById(R.id.vessel_submitVBBTxT);
        submitVVPEDT = vesselView.findViewById(R.id.vessel_submitVVPEdT);
        vesselView.findViewById(R.id.vessel_submitVBBTxT).setOnClickListener(mSubmitVVPListen);
        TransferTopicTxT = vesselView.findViewById(R.id.vessel_TopicTXT);

        // Vessel Transportation
        vesselDetailsLAY = vesselView.findViewById(R.id.vessel_DetailsLAY);
        vesselDetailsLAY.setVisibility(View.GONE);
        TransferScanBtn = vesselView.findViewById(R.id.vessel_ScanBTN);
        vesselView.findViewById(R.id.vessel_ScanBTN).setOnClickListener(mDecodeListener);
        vesselView.findViewById(R.id.vessel_EnterIMG).setOnClickListener(mScannerListener);
        vesselView.findViewById(R.id.vessel_closeInventoryTransfer).setOnClickListener(mCloseActivityListener);

        vesselView.findViewById(R.id.vessel_Printtxt).setOnClickListener(mPrintListener);
        scannerListLay = vesselView.findViewById(R.id.vessel_scannerListlayout);
        ScanValueETxT = vesselView.findViewById(R.id.vessel_scanValueEDTXT);
        TotalScannedCount = vesselView.findViewById(R.id.vessel_TotalScannedCount);
        ScannedResultLV = vesselView.findViewById(R.id.vessel_Details_lv);

        ToLocationSpin = vesselView.findViewById(R.id.vessel_tolocation_spinner);
        DriverSpin = vesselView.findViewById(R.id.vessel_driver_sppinner);
        TransportAgencySpin = vesselView.findViewById(R.id.vessel_transagency_spinner);
        TruckPlateNumberSpin = vesselView.findViewById(R.id.vessel_truckplateumber_spinner);
        FellingSecSpinner = vesselView.findViewById(R.id.vessel_fellingsection_spinner);

        ToLocationATXT = vesselView.findViewById(R.id.vessel_tolocation_ATxT);
        DriverATXT = vesselView.findViewById(R.id.vessel_driver_ATxT);
        AgencyATXT = vesselView.findViewById(R.id.vessel_transagency_ATxT);
        TruckDetialsATXT = vesselView.findViewById(R.id.vessel_truckplateumber_ATxT);

        TransferMode_LV = vesselView.findViewById(R.id.vessel_transportMode_LV);
        LoadedBy_LV = vesselView.findViewById(R.id.vessel_loadedby_LV);
        vbb_txt = vesselView.findViewById(R.id.vessel_VBB_numberTXT);
        FromLocationTxT = vesselView.findViewById(R.id.vessel_fromLocationTXT);
        VolumeTXT = vesselView.findViewById(R.id.vessel_TotalScannedVolume);

        EditOptionFlagLay = vesselView.findViewById(R.id.vessel_editLayout);
        EditOptionFlagLay2 = vesselView.findViewById(R.id.vessel_editLayout2);
        TransportTypeIDTXT = vesselView.findViewById(R.id.vessel_TransportTypeIDTXT);

        vesselView.findViewById(R.id.vessel_advancedsearchIMG).setOnClickListener(advanceSearchVisibleListener);
        vesselView.findViewById(R.id.as_closeActivity).setOnClickListener(advanceSearchCloseListener);
        vesselView.findViewById(R.id.as_scannerList).setOnClickListener(advanceSearchListener);
        treeNoEDT = vesselView.findViewById(R.id.as_treenoEDT);
        fellingIDEDT = vesselView.findViewById(R.id.as_fellingEDT);
        advanceSearchLAY = vesselView.findViewById(R.id.vessel_advancesearchLAY);
        advanceSearchLAY.setVisibility(View.GONE);
        advanceSearchRLV = vesselView.findViewById(R.id.as_scan_lv);
        as_NovalueFound = vesselView.findViewById(R.id.as_NovalueFound);
    }

    View.OnClickListener mAddTransferIDListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                if (DetailsValidation(Common.Export.Vessel_FromLocationName, ToLocationATXT.getText().toString(), AgencyATXT.getText().toString(),
                        DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
                    if (Common.VesselScannedResultList.size() > 0) {
                        if (Common.VesselScannedResultList.size() < Common.MinimumScannedItemSize) {
                            AlertDialogBox(CommonMessage(R.string.TransferHead), "add minimum five items ", false);
                            return;
                        }
                        if (Common.VesselScannedResultList.size() < (Common.VVBLimitation + 1)) {
                            NextAlertDialog("Vessel Count is less then 22, Are you sure to create next list?");
                        } else {
                            AddTransferList();
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.TransferHead), "add minimum five items ", false);
                    }
                }
            } catch (Exception ex) {
                AlertDialogBox(CommonMessage(R.string.TransferHead), ex.toString(), false);
            }
        }
    };

    private void getToLocationsforSpinner() {
        try {
            // FromLocation Name from FromLocationID
            Common.Export.Vessel_FromLocationID = Common.FromLocationID;// Device Location
            int vesselLocation;
            if (String.valueOf(Common.Export.Vessel_FromLocationID).length() == 1) {
                vesselLocation = Common.Export.Vessel_FromLocationID * 1000;
            } else {
                vesselLocation = Common.Export.Vessel_FromLocationID;
            }
            Common.ConcessionList.clear();
            Common.ConcessionList = mDBExternalHelper.getAllConcessionNames(vesselLocation);
            if (Common.ConcessionList.size() > 0) {
                Common.Export.Vessel_FromLocationName = Common.ConcessionList.get(0).getConcessionName();
                FromLocationTxT.setText(Common.Export.Vessel_FromLocationName);
            }
            // Felling Section Spinner
            /*int LocationTypeID = Common.ConcessionList.get(0).getLocationType();
            if (LocationTypeID == 1) {
                Common.FellingSectionList.clear();
                Common.FellingSectionList = mDBExternalHelper.getFellingSectionDetails(Common.FromLocationID);// Device Location
                fellingSectionAdapter = new FellingSectionAdapter(getActivity(), Common.FellingSectionList);
                FellingSecSpinner.setAdapter(fellingSectionAdapter);
                FellingSecSpinner.setVisibility(View.GONE);
            } else {
                FellingSecSpinner.setVisibility(View.GONE);
            }*/
            // To Location Spinner
            Common.LocationList.clear();
            Common.LocationList = mDBExternalHelper.getToLocations();
            //customAdapter = new CustomAdapter(getActivity(), Common.LocationList);
            //ToLocationSpin.setAdapter(customAdapter);

            Common.ConcessionListStringList = new String[Common.LocationList.size()];
            for (int i = 0; i < Common.LocationList.size(); i++) {
                Common.ConcessionListStringList[i] = Common.LocationList.get(i).getLocation();
            }
            StringtreeNoadapter = new StringtreeNoadapter(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, Common.ConcessionListStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            ToLocationATXT.setThreshold(1);
            ToLocationATXT.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
        }
    }

    private void gettruckforSpinner() {
        try {
            Common.TruckDeatilsList.clear();
            Common.TruckDeatilsList = mDBExternalHelper.getAllTruckDetails();
            truckAdapter = new TruckAdapter(getActivity(), Common.TruckDeatilsList);
            TruckPlateNumberSpin.setAdapter(truckAdapter);
            Common.TruckDetialsStringList = new String[Common.TruckDeatilsList.size()];
            for (int i = 0; i < Common.TruckDeatilsList.size(); i++) {
                Common.TruckDetialsStringList[i] = Common.TruckDeatilsList.get(i).getTruckLicensePlateNo();
            }
            StringtreeNoadapter = new StringtreeNoadapter(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, Common.TruckDetialsStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            TruckDetialsATXT.setThreshold(1);
            TruckDetialsATXT.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
        }
    }

    private void getagencyforSpinner() {
        try {
            Common.TransportAgencyList.clear();
            Common.TransportAgencyList = mDBExternalHelper.getAllAgencyDetails();
            Common.AgencyDetailsStringList = new String[Common.TransportAgencyList.size()];
            for (int i = 0; i < Common.TransportAgencyList.size(); i++) {
                Common.AgencyDetailsStringList[i] = Common.TransportAgencyList.get(i).getAgencyName();
            }

            StringtreeNoadapter = new StringtreeNoadapter(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, Common.AgencyDetailsStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            AgencyATXT.setThreshold(1);
            AgencyATXT.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
        }
    }

    private void getdriverforSpinner() {
        try {
            Common.DriverList.clear();
            Common.DriverList = mDBExternalHelper.getAllDriverDetails();
            driverAdapter = new DriverAdapter(getActivity(), Common.DriverList);
            DriverSpin.setAdapter(driverAdapter);
            Common.DriverListStringList = new String[Common.DriverList.size()];
            for (int i = 0; i < Common.DriverList.size(); i++) {
                Common.DriverListStringList[i] = Common.DriverList.get(i).getDriverName();
            }
            StringtreeNoadapter = new StringtreeNoadapter(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, Common.DriverListStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            DriverATXT.setThreshold(1);
            DriverATXT.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
        }
    }

    private void gettransModeforView() {
        try {
            Common.TransportModeList.clear();
            Common.TransportModeList = mDBExternalHelper.getAllTransportModeDetails();
            transportAdapter = new TransportAdapterInside(getActivity(), Common.TransportModeList);
            transportAdapter.notifyDataSetChanged();
            TransferMode_LV.setAdapter(transportAdapter);
            TransportTypeIDTXT.setText(Common.Export.Vessel_TransportMode);
        } catch (Exception ex) {
        }
    }

    private void getLoadedByView() {
        try {
            Common.LoadedByList.clear();
            Common.LoadedByList = mDBExternalHelper.getAllLoadedByDetails();
            loadedAdapter = new LoadedAdapterInside(getActivity(), Common.LoadedByList);
            loadedAdapter.notifyDataSetChanged();
            LoadedBy_LV.setAdapter(loadedAdapter);
        } catch (Exception ex) {
        }
    }

    public void Click_Listener() {

        ScanValueETxT.setOnTouchListener((v, event) -> {
            Common.ScannedEditTXTFlag = true;
            return false;
        });

        ScanValueETxT.addTextChangedListener(new TextWatcher() {
            private int lastLength;
            private boolean backSpace;
            String LastRemoved;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lastLength = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                backSpace = lastLength > editable.toString().length();
                if (backSpace) {
                    if (editable.toString().length() > 0) {
                        if (!isValidBarCode(editable.toString())) {
                            ScanValueETxT.setError("Barcode should be like(XX-1111111 or XX-1111111 X )");
                        }
                    }
                } else {
                    if (editable.toString().length() > 0) {
                        String last = editable.toString().substring(editable.toString().length() - 1);
                        if (last.equals("-")) {

                        } else {
                            if (editable.toString().length() == 2) {
                                ScanValueETxT.append("-");
                            }
                            if (editable.toString().length() == Common.SBBlenght) {
                                if (!isValidBarCode(editable.toString())) {
                                    ScanValueETxT.setError("Barcode should be like(XX-1111111 or XX-1111111 X )");
                                }
                            } else {
                                if (editable.toString().length() == Common.SBBlenghtVessel) {
                                    char laste = editable.toString().charAt(editable.toString().length() - 1);
                                    boolean check1 = Character.isWhitespace(laste);
                                    if (check1 == false) {
                                        LastRemoved = editable.toString().substring(0, editable.toString().length() - 1);
                                        LastRemoved = LastRemoved + " " + last;
                                        ScanValueETxT.setText(LastRemoved);
                                        ScanValueETxT.setSelection(ScanValueETxT.getText().length());
                                    } else {
                                        LastRemoved = editable.toString();
                                    }
                                    if (!isValidBarCode12(LastRemoved)) {
                                        ScanValueETxT.setError("Barcode should be like(XX-1111111 or XX-1111111 X )");
                                    } else {
                                        Common.HideKeyboard(getActivity());
                                    }
                                }
                                if (editable.toString().length() > Common.SBBlenghtVessel) {
                                    char laste = editable.toString().charAt(editable.toString().length() - 1);
                                    boolean check1 = Character.isWhitespace(laste);
                                    if (check1 == true) {
                                        String lastTwo = editable.toString().substring(0, editable.toString().length() - 1);
                                        ScanValueETxT.setText(lastTwo.substring(0, lastTwo.length() - 1));
                                        ScanValueETxT.setSelection(ScanValueETxT.getText().length());
                                    }
                                    if (!isValidBarCode12(editable.toString())) {
                                        ScanValueETxT.setError("Barcode should be like(XX-1111111 or XX-1111111 X )");
                                    } else {
                                        Common.HideKeyboard(getActivity());
                                    }
                                } /*else {
                                    if (Common.ScannedEditTXTFlag == true) {
                                        ScanValueETxT.setError(CommonMessage(R.string.BarCodeLenghtMsgVessel));
                                    }
                                }*/
                            }
                        }
                    }
                }
            }
        });

       /* TransferMode_LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Common.TransportTypeId = Common.Export.Vessel_TransportModeList.get(position).getTransportTypeId();
                Common.Export.Vessel_TransportMode = Common.Export.Vessel_TransportModeList.get(position).getTransportMode();
            }
        });*/

        EditOptionFlagLay.setOnClickListener(v ->

                AlertDialogBox(CommonMessage(R.string.ReceivedHead), "Can not edit Or delete after Synced", false));

        EditOptionFlagLay2.setOnClickListener(v ->

                AlertDialogBox(CommonMessage(R.string.ReceivedHead), "Can not edit Or delete after Synced", false));

        /*AutoComplete*/
        ToLocationATXT.setOnTouchListener((v, event) ->

        {
            ToLocationATXT.requestFocus();
            ToLocationATXT.showDropDown();
            return false;
        });

        ToLocationATXT.setOnItemClickListener((parent, view, position, id) ->

        {
            String locModelStr = (String) parent.getItemAtPosition(position);
            for (LocationsModel locModel : Common.LocationList)
                if (locModelStr.equals(locModel.getLocation())) {
                    Common.Export.Vessel_ToLocationID = locModel.getToLocationId();
                    Common.Export.Vessel_ToLocationName = locModel.getLocation();
                }
        });

        DriverATXT.setOnTouchListener((v, event) ->

        {
            DriverATXT.requestFocus();
            DriverATXT.showDropDown();
            return false;
        });

        DriverATXT.setOnItemClickListener((parent, view, position, id) ->

        {
            try {
                String item = (String) parent.getItemAtPosition(position);
                if (!isNullOrEmpty(item)) {
                    drivermodelArray = new DriverDetailsModel();
                    drivermodelArray = mDBExternalHelper.getOneDriverDetails(item);
                    if (drivermodelArray.getIsBlocked() == 1) {
                        AlertDialogBox("Verification", "Selected driver is blocked, please contact admin", false);
                        DriverATXT.setText("");
                    } else {
                        String driverModelStr = drivermodelArray.getDriverName();
                        Common.Export.Vessel_DriverID = drivermodelArray.getTruckDriverId();
                        Common.Export.Vessel_DriverName = drivermodelArray.getDriverName();
                        DriverATXT.setText(driverModelStr);
                    }
                }
            } catch (Exception ex) {

            }
        });

        AgencyATXT.setOnTouchListener((v, event) ->

        {
            AgencyATXT.requestFocus();
            AgencyATXT.showDropDown();
            return false;
        });

        AgencyATXT.setOnItemClickListener((parent, view, position, id) ->

        {
            try {
                String item = (String) parent.getItemAtPosition(position);
                if (!isNullOrEmpty(item)) {
                    agencyModelArray = new AgencyDetailsModel();
                    agencyModelArray = mDBExternalHelper.getOneAgencyDetails(item);
                    if (agencyModelArray.getIsBlocked() == 1) {
                        AlertDialogBox("Verification", "Selected agent is blocked, please contact admin", false);
                        AgencyATXT.setText("");
                    } else {
                        String agencyModelStr = agencyModelArray.getAgencyName();
                        Common.Export.Vessel_AgencyName = agencyModelArray.getAgencyName();
                        Common.Export.Vessel_TransferAgencyID = agencyModelArray.getAgencyId();
                        AgencyATXT.setText(agencyModelStr);
                    }
                }
            } catch (Exception ex) {

            }
        });

        TruckDetialsATXT.setOnTouchListener((v, event) ->

        {
            TruckDetialsATXT.requestFocus();
            TruckDetialsATXT.showDropDown();
            return false;
        });

        TruckDetialsATXT.setOnItemClickListener((parent, view, position, id) ->

        {
            try {
                String item = (String) parent.getItemAtPosition(position);
                if (!isNullOrEmpty(item)) {
                    truckModelArray = new TruckDetailsModel();
                    truckModelArray = mDBExternalHelper.getOneTruckDetails(item);
                    if (truckModelArray.getIsBlocked() == 1) {
                        AlertDialogBox("Verification", "Selected truck is blocked, please contact admin", false);
                        TruckDetialsATXT.setText("");
                    } else {
                        Common.Export.Vessel_TrucklicensePlateNo = truckModelArray.getTruckLicensePlateNo();
                        Common.Export.Vessel_TransportID = truckModelArray.getTransportId();
                        TruckDetialsATXT.setText(Common.Export.Vessel_TrucklicensePlateNo);
                    }
                }
            } catch (Exception ex) {

            }
        });
    }

    ServiceConnection vessel_serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            vessel_bind = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder vessel_service) {
            vessel_bind = true;
            SdlScanService.MyBinder myBinder = (SdlScanService.MyBinder) vessel_service;
            vessel_scanService = myBinder.getService();
            //
            vessel_scanService.setOnScanListener(VesselFragments.this);
            vessel_scanService.setActivityUp(true);
        }
    };

    public void acquireWakeLock() {
        if (vessel_mWakeLock == null) {
            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            //vessel_mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "ZHENGYI.WZY");
        }
        if (vessel_mWakeLock != null) {
            vessel_mWakeLock.acquire();
        }
    }

    public void releaseWakeLock() {
        if (vessel_mWakeLock != null) {
            vessel_mWakeLock.release();
        }
    }

    public VesselFragments() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vesselView = inflater.inflate(R.layout.activity_vessel_list, container, false);
        mDBExternalHelper = new ExternalDataBaseHelperClass(getActivity());
        printSlip = new PrintSlipsClass(getActivity());
        beepsound = MediaPlayer.create(getActivity(), R.raw.beep);
        wronBuzzer = MediaPlayer.create(getActivity(), R.raw.wrong_buzzer);
        vesselTransportAPI = ApiClient.getInstance().getUserService();
        Initialization();
        getToLocationsforSpinner();
        getdriverforSpinner();
        getagencyforSpinner();
        gettruckforSpinner();
        gettransModeforView();
        getLoadedByView();
        Common.HideKeyboard(getActivity());
        vbb_txt.setText(Common.VBB_Number);
        Click_Listener();
        return vesselView;
    }

    public void TransferEditValues() {
        try {
            // FromLocation Name from FromLocationID
            int vesselLocation;
            if (String.valueOf(Common.Export.Vessel_FromLocationID).length() == 1) {
                vesselLocation = Common.Export.Vessel_FromLocationID * 1000;
            } else {
                vesselLocation = Common.Export.Vessel_FromLocationID;
            }
            Common.ConcessionList.clear();
            Common.ConcessionList = mDBExternalHelper.getAllConcessionNames(vesselLocation);
            if (Common.ConcessionList.size() > 0) {
                Common.Export.Vessel_FromLocationName = Common.ConcessionList.get(0).getConcessionName();
                FromLocationTxT.setText(Common.Export.Vessel_FromLocationName);
            }
            if (Common.Export.Vessel_ToLocationID == 0) {
                ToLocationATXT.setText("");
            } else {
                for (LocationsModel item : Common.LocationList) {
                    if (item.getToLocationId() == Common.Export.Vessel_ToLocationID) {
                        ToLocationSpin.setSelection(item.getID());
                        ToLocationATXT.setText(item.getLocation());
                    }
                }
            }
            if (Common.Export.Vessel_DriverID == 0) {
                DriverATXT.setText("");
            } else {
                for (DriverDetailsModel item : Common.DriverList) {
                    if (item.getTruckDriverId() == Common.Export.Vessel_DriverID) {
                        DriverSpin.setSelection(item.getID());
                        DriverATXT.setText(item.getDriverName());
                    }
                }
            }
            if (Common.Export.Vessel_TransferAgencyID == 0) {
                AgencyATXT.setText("");
            } else {
                for (AgencyDetailsModel item : Common.TransportAgencyList) {
                    if (item.getAgencyId() == Common.Export.Vessel_TransferAgencyID) {
                        TransportAgencySpin.setSelection(item.getID());
                        AgencyATXT.setText(item.getAgencyName());
                    }
                }
            }
            if (Common.Export.Vessel_TransportID == 0) {
                TruckDetialsATXT.setText("");
            } else {
                for (TruckDetailsModel item : Common.TruckDeatilsList) {
                    if (item.getTransportId() == Common.Export.Vessel_TransportID) {
                        TruckPlateNumberSpin.setSelection(item.getID());
                        Common.Export.Vessel_TrucklicensePlateNo = item.getTruckLicensePlateNo();
                        TruckDetialsATXT.setText(item.getTruckLicensePlateNo());
                    }
                }
            }
            getLoadedByView();
        } catch (Exception ex) {

        }
    }

    View.OnClickListener mSubmitVVPListen = new View.OnClickListener() {
        public void onClick(View v) {
            ProgressBarLay.setVisibility(View.VISIBLE);
            try {
                Common.Export.Vessel_LoadedTypeID = 1;  // for vessel loded by GWW
                Common.VolumeSum = 0.0;
                Common.VBB_Number = submitVVPEDT.getText().toString();
                Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                VesselCreateModel inputObj = new VesselCreateModel();
                inputObj.setIMEINumber(Common.IMEI);
                inputObj.setUserID(Common.UserID);
                inputObj.setDeviceID(Common.LDeviceID);
                inputObj.setExportID(Common.ExportID);
                inputObj.setExportCode(Common.ExportCode);
                vesselTransportAPI = ApiClient.getApiInterface();
                vesselTransportAPI.getVesselCreate(inputObj).enqueue(new Callback<VesselCreateModel>() {
                    @Override
                    public void onResponse(Call<VesselCreateModel> call, Response<VesselCreateModel> response) {
                        try {
                            ProgressBarLay.setVisibility(View.GONE);
                            if (GwwException.GwwException(response.code()) == true) {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        Common.SyncStatusList.clear();
                                        Common.SyncStatusList.addAll(response.body().getStatus());
                                        if (Common.SyncStatusList.get(0).getStatus() == 1) {
                                            Common.Export.VesselID = response.body().getCreateVesselTransport().get(0).getVesselListId();
                                            Common.Export.VesselUniqueID = response.body().getCreateVesselTransport().get(0).getVesselUniqueID();
                                            Common.IsVesselEditListFlag = false;
                                            // Empty Details for First time
                                            Common.Export.Vessel_ToLocationID = 0;
                                            Common.Export.Vessel_DriverID = 0;
                                            Common.Export.Vessel_TransferAgencyID = 0;
                                            Common.Export.Vessel_TransportID = 0;
                                            VesselTransportDetailsActivityCall();
                                        } else {
                                            AlertDialogBox(CommonMessage(R.string.Vesselhead), "#" + Common.Export.VesselID + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                                        }
                                    } else {
                                        AlertDialogBox(CommonMessage(R.string.Vesselhead), "#" + Common.Export.VesselID + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                                    }
                                } else {
                                    AlertDialogBox(CommonMessage(R.string.Vesselhead), "#" + Common.Export.VesselID + "--" + "No Value Found", false);
                                }
                            } else {
                                Common.AuthorizationFlag = true;
                                AlertDialogBox(CommonMessage(R.string.Vesselhead), response.message(), false);
                            }

                        } catch (Exception ex) {
                            CrashAnalytics.CrashReport(ex);
                            AlertDialogBox(CommonMessage(R.string.Vesselhead), "#" + Common.Export.VesselID + "--" + ex.getMessage(), false);
                        }
                    }

                    @Override
                    public void onFailure(Call<VesselCreateModel> call, Throwable t) {
                        ProgressBarLay.setVisibility(View.GONE);
                        AlertDialogBox(CommonMessage(R.string.Vesselhead), t.getMessage(), false);
                    }
                });
            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
                AlertDialogBox("IT-AddTransferList", ex.toString(), false);
            }
        }
    };

    public void GetVesselTransportList() {
        try {
            ProgressBarLay.setVisibility(View.VISIBLE);
            VesselInputList inputObj = new VesselInputList();
            inputObj.setIMEI(Common.IMEI);
            inputObj.setUserID(Common.UserID);
            inputObj.setExportID(Common.ExportID);
            vesselTransportAPI = ApiClient.getApiInterface();
            vesselTransportAPI.getVesselList(inputObj).enqueue(new Callback<VesselInputList>() {
                @Override
                public void onResponse(Call<VesselInputList> call, Response<VesselInputList> response) {
                    try {
                        ProgressBarLay.setVisibility(View.GONE);
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.isSuccessful()) {
                                Common.VesselLists.clear();
                                Common.VesselLists.addAll(response.body().getCreateVesselTransportList());
                                if (Common.VesselLists.size() > 0) {
                                    vesselTransportadapter = new VesselTransportAdapter(Common.VesselLists, getActivity());
                                    horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
                                    horizontalLayoutManager.setStackFromEnd(true);
                                    vesselTransportadapter.notifyDataSetChanged();
                                    VesselTransportList.setLayoutManager(horizontalLayoutManager);
                                    VesselTransportList.setAdapter(vesselTransportadapter);
                                    NoValueFoundTxT.setVisibility(View.GONE);
                                    VesselTransportList.setVisibility(View.VISIBLE);
                                } else {
                                    VesselTransportList.setVisibility(View.GONE);
                                    NoValueFoundTxT.setVisibility(View.VISIBLE);
                                    //AlertDialogBox(CommonMessage(R.string.Vesselhead), CommonMessage(R.string.NoValueFound), false);
                                }
                                TotalFilteredCount.setText(String.valueOf(Common.VesselLists.size()));
                                TotalFilteredVolume.setText(String.valueOf(TotalVolumeList(Common.VesselLists)));
                            } else {
                                AlertDialogBox(CommonMessage(R.string.Vesselhead), "Not Synced", false);
                            }
                        } else {
                            Common.AuthorizationFlag = true;
                            AlertDialogBox(CommonMessage(R.string.Vesselhead), response.message(), false);
                        }

                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        AlertDialogBox(CommonMessage(R.string.Vesselhead), ex.getMessage(), false);
                    }
                }

                @Override
                public void onFailure(Call<VesselInputList> call, Throwable t) {
                    ProgressBarLay.setVisibility(View.GONE);
                    AlertDialogBox(CommonMessage(R.string.Vesselhead), t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public double TotalVolumeList(ArrayList<VesselListModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (VesselListModel vesselTransScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(vesselTransScanModel.getTotalVolume().trim());
        }
        return TotVolume;
    }

    private void showSnack(boolean isConnected) {
        try {
            String message;
            int color;
            if (isConnected) {
                message = "Good! Connected to Internet";
                color = Color.WHITE;
            } else {
                message = "Sorry! Not connected to internet";
                color = Color.RED;
            }

            Snackbar snackbar = Snackbar.make(vesselView.findViewById(R.id.snack_barList), message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public class VesselTransportAdapter extends RecyclerView.Adapter<VesselTransportAdapter.TransferViewHolder> {
        private List<VesselListModel> VesselTransportList;
        Context context;

        public VesselTransportAdapter(List<VesselListModel> ScannedResultList, Context context) {
            this.VesselTransportList = ScannedResultList;
            this.context = context;
        }

        @Override
        public TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vessel_list_infliator, parent, false);
            TransferViewHolder gvh = new TransferViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return VesselTransportList.size();
        }

        @Override
        public void onBindViewHolder(VesselTransportAdapter.TransferViewHolder holder, final int position) {
            //holder.TransferIDTXT.setText(String.valueOf(position + 1));
            holder.TransferIDTXT.setText(VesselTransportList.get(position).getTransferDetailsId());
            holder.Vessel_NumberTXT.setText(String.valueOf(VesselTransportList.get(position).getVesselListID()));
            holder.ImeiTXT.setText(VesselTransportList.get(position).getIMEI());
            holder.StartTimeTXT.setText(VesselTransportList.get(position).getStartDateTime());
            holder.EndTimeTXT.setText(VesselTransportList.get(position).getEndDateTime());
            holder.CountTXT.setText(String.valueOf(VesselTransportList.get(position).getCount()));

            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            holder.Background.setOnClickListener(v -> {
                Common.Export.VesselID = VesselTransportList.get(position).getVesselListID();
                Common.Export.VesselUniqueID = VesselTransportList.get(position).getVesselUniqueID();
                Common.Export.Vessel_FromLocationID = VesselTransportList.get(position).getFromLocationID();
                Common.Export.Vessel_ToLocationID = VesselTransportList.get(position).getToLocationID();
                Common.Export.Vessel_DriverID = VesselTransportList.get(position).getDriverID();
                Common.Export.Vessel_TransferAgencyID = VesselTransportList.get(position).getTransferAgencyID();
                Common.Export.Vessel_TransportID = VesselTransportList.get(position).getTransportTypeId();
                Common.Export.Vessel_LoadedTypeID = VesselTransportList.get(position).getLoadedTypeID();
                Common.IsVesselEditListFlag = VesselTransportList.get(position).getIsDeliveryConfirmed();
                if (Common.Export.Vessel_LoadedTypeID == 0) {
                    Common.Export.Vessel_LoadedTypeID = 1;
                }
                VesselTransportDetailsActivityCall();
            });
            if (VesselTransportList.get(position).getCount() > 0) {
                holder.printIMG.setVisibility(View.VISIBLE);
            } else {
                holder.printIMG.setVisibility(View.INVISIBLE);
            }
            holder.printIMG.setOnClickListener(v -> {
                Common.Export.VesselID = VesselTransportList.get(position).getVesselListID();
                Common.Export.VesselUniqueID = VesselTransportList.get(position).getVesselUniqueID();
                TransferListPrintHan.post(TransferListPrintRun);
            });
            holder.DeleteIMG.setOnClickListener(v -> {
                try {
                    Common.Export.VesselUniqueID = VesselTransportList.get(position).getVesselUniqueID();
                    GetVesselTransportListDelete(Common.Export.VesselUniqueID);
                } catch (Exception ex) {
                    AlertDialogBox("Transfer DeleteIMG", ex.toString(), false);
                }
            });
        }

        public class TransferViewHolder extends RecyclerView.ViewHolder {
            TextView TransferIDTXT, ImeiTXT, Vessel_NumberTXT, StartTimeTXT, EndTimeTXT, CountTXT;
            ImageView printIMG, DeleteIMG;
            LinearLayout Background;

            public TransferViewHolder(View view) {
                super(view);
                Background = view.findViewById(R.id.vessel_layoutBackground);
                TransferIDTXT = view.findViewById(R.id.vessel_IDTxT);
                Vessel_NumberTXT = view.findViewById(R.id.vessel_VesselTxT);
                ImeiTXT = view.findViewById(R.id.vessel_imeiTxT);
                StartTimeTXT = view.findViewById(R.id.vessel_startTimeTxT);
                EndTimeTXT = view.findViewById(R.id.vessel_endTimeTxT);
                CountTXT = view.findViewById(R.id.vessel_countTxT);
                printIMG = view.findViewById(R.id.printSlip_IMG);
                DeleteIMG = view.findViewById(R.id.vessel_Delete);
            }
        }
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(getActivity(), Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public String CommonMessage(int Common_Msg) {
        return getActivity().getResources().getString(Common_Msg);
    }

    public void PrintoutEnd() {
        tsc.closeport();
        TSCConnInt = -1;
        Common.IsPrintBtnClickFlag = true;
    }

    public void VesselTransportDetailsActivityCall() {
        ViewTransfeList(Common.IsVesselEditListFlag);
        TransferEditValues();
        GetTransferScannedResultList();
        vesselDetailsLAY.setVisibility(View.VISIBLE);
    }

    public void ViewTransfeList(boolean EdtFlag) {
        if (EdtFlag == true) {
            EditOptionFlagLay.setVisibility(View.VISIBLE);
            EditOptionFlagLay2.setVisibility(View.VISIBLE);
            TransferScanBtn.setEnabled(false);
            ScanValueETxT.setEnabled(false);
            vesselView.findViewById(R.id.vessel_EnterIMG).setEnabled(false);
            vesselView.findViewById(R.id.vessel_ScanBTN).setEnabled(false);
        } else {
            EditOptionFlagLay.setVisibility(View.GONE);
            EditOptionFlagLay2.setVisibility(View.GONE);
            TransferScanBtn.setEnabled(true);
            ScanValueETxT.setEnabled(true);
            vesselView.findViewById(R.id.vessel_EnterIMG).setEnabled(true);
            vesselView.findViewById(R.id.vessel_ScanBTN).setEnabled(true);
        }
    }

    public void GetVesselTransportListDelete(String VesselUniqueID) {
        try {
            VesselCancelTransferModel inputObj = new VesselCancelTransferModel();
            inputObj.setBarcode(Common.BarCode);
            inputObj.setUserId(Common.UserID);
            inputObj.setVesselTrUniqueID(VesselUniqueID);
            vesselTransportAPI = ApiClient.getApiInterface();
            vesselTransportAPI.GetVesselCancelledTransferList(inputObj).enqueue(new Callback<VesselCancelTransferModel>() {
                @Override
                public void onResponse(Call<VesselCancelTransferModel> call, Response<VesselCancelTransferModel> response) {
                    try {
                        ProgressBarLay.setVisibility(View.GONE);
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.body() != null) {
                                GetVesselTransportList();
                            } else {
                                AlertDialogBox(CommonMessage(R.string.Vesselhead), "Not Synced", false);
                            }
                        } else {
                            Common.AuthorizationFlag = true;
                            AlertDialogBox(CommonMessage(R.string.Vesselhead), response.message(), false);
                        }

                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        AlertDialogBox(CommonMessage(R.string.Vesselhead), ex.getMessage(), false);
                    }
                }

                @Override
                public void onFailure(Call<VesselCancelTransferModel> call, Throwable t) {
                    ProgressBarLay.setVisibility(View.GONE);
                    AlertDialogBox(CommonMessage(R.string.Vesselhead), t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    Handler TransferListPrintHan = new Handler();
    Runnable TransferListPrintRun = new Runnable() {
        @Override
        public void run() {
            try {
                VesselPrintOutputModel intObj = new VesselPrintOutputModel();
                intObj.setVSTransferUniqueID(Common.Export.VesselUniqueID);
                intObj.setUserID(Common.UserID);
                vesselTransportAPI = ApiClient.getApiInterface();
                vesselTransportAPI.GetVesselTransportPrintValue(intObj).enqueue(new Callback<VesselPrintOutputModel>() {
                    @Override
                    public void onResponse(Call<VesselPrintOutputModel> call, Response<VesselPrintOutputModel> response) {
                        try {
                            ProgressBarLay.setVisibility(View.GONE);
                            if (GwwException.GwwException(response.code()) == true) {
                                if (response.body() != null) {
                                    Common.VesselLogsPrintOutList.clear();
                                    Common.VesselLogsPrintOutList.addAll(response.body().getGetVesselTransportPrintValue());
                                    if (Common.VesselLogsPrintOutList.size() > 0) {
                                        Common.VBB_Number = Common.VesselLogsPrintOutList.get(0).getVBB_Number();
                                        Common.TransferID = Common.VesselLogsPrintOutList.get(0).getTransferID();
                                        Common.TransferUniqueID = Common.VesselLogsPrintOutList.get(0).getTransUniqueID();
                                        Common.Export.Vessel_DriverID = Common.VesselLogsPrintOutList.get(0).getDriverID();
                                        Common.Export.Vessel_DriverName = Common.VesselLogsPrintOutList.get(0).getDriverName();
                                        Common.Export.Vessel_TransportTypeId = Common.VesselLogsPrintOutList.get(0).getTransportTypeId();
                                        Common.Export.Vessel_TransportMode = Common.VesselLogsPrintOutList.get(0).getTransportMode();
                                        Common.Export.Vessel_FromLocationID = Common.VesselLogsPrintOutList.get(0).getFromLocationID();
                                        Common.Export.Vessel_FromLocationName = Common.VesselLogsPrintOutList.get(0).getFromLocation();
                                        Common.Export.Vessel_ToLocationID = Common.VesselLogsPrintOutList.get(0).getToLocationID();// added hardcore values
                                        Common.Export.Vessel_ToLocationName = Common.VesselLogsPrintOutList.get(0).getTolocation();// added hardcore values
                                        Common.Export.Vessel_TransferAgencyID = Common.VesselLogsPrintOutList.get(0).getTransferAgencyID();
                                        Common.Export.Vessel_AgencyName = Common.VesselLogsPrintOutList.get(0).getAgencyName();
                                        Common.Export.Vessel_LoadedName = Common.VesselLogsPrintOutList.get(0).getLoadedName();
                                        Common.Export.Vessel_TrucklicensePlateNo = Common.VesselLogsPrintOutList.get(0).getTruckPlateNumber();
                                        Common.FellingSectionId = String.valueOf(Common.VesselLogsPrintOutList.get(0).getFellingSectionId());

                                        //Common.VolumeSum = Common.VesselLogsPrintOutList.get(0).getTotalVolume();
                                        //  Vessel  Printout
                                        if (Common.IsPrintBtnClickFlag == true) {
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
                                                        Common.IsPrintBtnClickFlag = false;
                                                        Common.VolumeSum = print_TotalVolume(Common.VesselLogsPrintOutList);
                                                        Common.Count = Common.VesselLogsPrintOutList.size();
                                                        tsc.clearbuffer();
                                                        tsc.sendcommand(printSlip.VesselHeader());
                                                        tsc.sendcommand(printSlip.VesselDetails());
                                                        tsc.sendcommand(printSlip.VesselListDimensions());
                                                        tsc.clearbuffer();
                                                        if (Common.VesselLogsPrintOutList.size() > (Common.VVBLimitation + 2)) {
                                                        } else {
                                                            tsc.sendcommand(printSlip.VesselFooter());
                                                            tsc.clearbuffer();
                                                        }
                                                        tsc.sendcommand(printSlip.TransferReceivedBottom19_09_2019(Common.Export.VesselUniqueID));
                                                        int TimerforPrint = 0;
                                                        if (Common.Count > 50) {
                                                            TimerforPrint = ((Common.Count / 50) * 2) * 1000;
                                                        }
                                                        new Handler().postDelayed(() -> PrintoutEnd(), TimerforPrint);
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
                                            AlertDialogBox(CommonMessage(R.string.PrinterHead), CommonMessage(R.string.Printing), false);
                                        }
                                    } else {
                                        AlertDialogBox(CommonMessage(R.string.Vesselhead), "#" + Common.Export.VesselID + "-- No Value Found", false);
                                    }
                                } else {
                                    AlertDialogBox(CommonMessage(R.string.Vesselhead), "#" + Common.Export.VesselID + "--" + "Not Synced", false);
                                }
                            } else {
                                Common.AuthorizationFlag = true;
                                AlertDialogBox(CommonMessage(R.string.Vesselhead), response.message(), false);
                            }

                        } catch (Exception ex) {
                            CrashAnalytics.CrashReport(ex);
                            AlertDialogBox(CommonMessage(R.string.Vesselhead), "#" + Common.Export.VesselID + "--" + ex.getMessage(), false);
                        }
                    }

                    @Override
                    public void onFailure(Call<VesselPrintOutputModel> call, Throwable t) {
                        ProgressBarLay.setVisibility(View.GONE);
                        AlertDialogBox(CommonMessage(R.string.Vesselhead), t.getMessage(), false);
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    public double print_TotalVolume(ArrayList<VesselLogDetailsPrintModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (VesselLogDetailsPrintModel inventoTransScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(inventoTransScanModel.getVolume());
        }
        return TotVolume;
    }

    // vessel transportation
    View.OnClickListener mDecodeListener = new View.OnClickListener() {
        public void onClick(View v) {
            ScanValueETxT.setText("");
            Common.ScanMode = true;
            Common.ScannedEditTXTFlag = false;
            Common.EntryMode = 1;
            if (DetailsValidation(Common.Export.Vessel_FromLocationName, ToLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
                if (Common.BarcodeScannerforVessel == true) {
                    vessel_scanService.doDecode();
                }
            }
        }
    };

    View.OnClickListener advanceSearchVisibleListener = v -> {
        advanceSearchLAY.setVisibility(View.VISIBLE);
        treeNoEDT.setText("");
        fellingIDEDT.setText("");
        advanceSearchRLV.setVisibility(View.GONE);
    };

    View.OnClickListener advanceSearchListener = v -> {
        Common.HideKeyboard(getActivity());
        advanceSearchApi();
    };

    View.OnClickListener advanceSearchCloseListener = v -> {
        advanceSearchLAY.setVisibility(View.GONE);
    };

    View.OnClickListener mScannerListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Common.ScanMode = false;
                if (DetailsValidation(Common.Export.Vessel_FromLocationName, ToLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
                    Common.HideKeyboard(getActivity());
                    String SBBLabel = ScanValueETxT.getText().toString();
                    if (SBBLabel.length() == Common.SBBlenght || SBBLabel.length() > Common.SBBlenght) {
                        if (SBBLabel.length() == Common.SBBlenght) {
                            if (!isValidBarCode(SBBLabel)) {
                                AlertDialogBox("Barcode Format", CommonMessage(R.string.ValidBarCodeMsg), false);
                                return;
                            }
                        }
                        if (SBBLabel.length() == 12) {
                            if (!isValidBarCode12(SBBLabel)) {
                                AlertDialogBox("Barcode Format", CommonMessage(R.string.ValidBarCodeMsg), false);
                                return;
                            }
                        }
                        Common.EntryMode = 2;
                        ScannedResult(SBBLabel);
                    } else {
                        AlertDialogBox("Barcode Format", CommonMessage(R.string.ValidBarCodeMsg), false);
                    }
                }
            } catch (Exception e) {
                AlertDialogBox("Barcode Format", e.toString(), false);
            }
        }
    };

    public boolean DetailsValidation(String FromLoc_Str, String ToLoc_ID, String TransAge_ID, String Driver_ID, String Truck_ID) {
        boolean Validattion = true;
        if (isNullOrEmpty(FromLoc_Str)) {
            Validattion = false;
            AlertDialogBox("From Location", "Verify from Location", false);
        } /*else {
            boolean isFromLocValid = mDBExternalHelper.IsValidFromLocation(FromLoc_Str);
            if (isFromLocValid == false) {
                Validattion = false;
                AlertDialogBox("From Location", "Verify from Location", false);
            }
        }*/
        if (isNullOrEmpty(String.valueOf(ToLoc_ID))) {
            Validattion = false;
            AlertDialogBox(CommonMessage(R.string.tolocation), CommonMessage(R.string.pleaseselectoneitem), false);
        } else {
            boolean isToLocValid = mDBExternalHelper.IsValidToLocation(ToLoc_ID);
            if (isToLocValid == false) {
                Validattion = false;
                AlertDialogBox(CommonMessage(R.string.tolocation), CommonMessage(R.string.pleaseselectoneitem), false);
            }
        }
        if (isNullOrEmpty(String.valueOf(TransAge_ID))) {
            Validattion = false;
            AlertDialogBox("Transport Agency", CommonMessage(R.string.pleaseselectoneitem), false);
        } else {
            boolean isAgencyIDValid = mDBExternalHelper.IsValidAgencyID(TransAge_ID);
            if (isAgencyIDValid == false) {
                Validattion = false;
                AlertDialogBox("Transport Agency", CommonMessage(R.string.pleaseselectoneitem), false);
            }
        }
        if (isNullOrEmpty(String.valueOf(Driver_ID))) {
            Validattion = false;
            AlertDialogBox("Driver Details", CommonMessage(R.string.pleaseselectoneitem), false);
        } else {
            boolean isAgencyIDValid = mDBExternalHelper.IsValidDriverID(Driver_ID);
            if (isAgencyIDValid == false) {
                Validattion = false;
                AlertDialogBox("Driver Details", CommonMessage(R.string.pleaseselectoneitem), false);
            }
        }
        if (isNullOrEmpty(String.valueOf(Truck_ID))) {
            Validattion = false;
            AlertDialogBox("Truck Details", CommonMessage(R.string.pleaseselectoneitem), false);
        } else {
            boolean isAgencyIDValid = mDBExternalHelper.IsValidTruckID(Truck_ID);
            if (isAgencyIDValid == false) {
                Validattion = false;
                AlertDialogBox("Truck Details", CommonMessage(R.string.pleaseselectoneitem), false);
            }
        }
        return Validattion;
    }

    View.OnClickListener mCloseActivityListener = v -> Signout("Are you sure want to close?");

    View.OnClickListener mPrintListener = v -> TransferListPrintHan.post(TransferListPrintRun);

    // display status string
    public void ScannedStatus(String s) {
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
        Common.HideKeyboard(getActivity());
        try {
            if (s != null && s.length() > 0) {
                // if (Common.ScanMode == true) {
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
                Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                Common.IsActive = 1;
                Common.SearchedTransLogDetils = mDBExternalHelper.getBarCodeTransferLogDetails(Common.BarCode);
                //Common.SearchedTransLogDetils = Common.AllTransLogDetailsmap.get(Common.BarCode);
                if (Common.SearchedTransLogDetils.size() > 0) {
                    Common.WoodSpieceID = Common.SearchedTransLogDetils.get(0).getWoodSpeciesId();
                    Common.WoodSpieceCode = Common.SearchedTransLogDetils.get(0).getWoodSpeciesCode();
                    Common.Length = Common.SearchedTransLogDetils.get(0).getLength_dm();
                    Common.Volume = Common.SearchedTransLogDetils.get(0).getVolume();
                    Common.FellingSectionId = Common.SearchedTransLogDetils.get(0).getFellingSectionId();
                    Common.TreeNumber = Common.SearchedTransLogDetils.get(0).getTreeNumber();
                    Common.QualityName = Common.SearchedTransLogDetils.get(0).getQuality();
                    Common.HoleVolume = Common.SearchedTransLogDetils.get(0).getHoleVolume();
                    Common.GrossVolume = Common.SearchedTransLogDetils.get(0).getGrossVolume();
                    Common.IsSBBLabelCorrected = false;
                } else {
                    Common.WoodSpieceID = "";
                    Common.WoodSpieceCode = "";
                    Common.FellingSectionId = "";
                    Common.QualityName = "";
                    Common.TreeNumber = "";
                    Common.Length = "0.00";
                    Common.Volume = "0.00";
                    Common.HoleVolume = "0.00";
                    Common.GrossVolume = "0.00";
                    Common.IsSBBLabelCorrected = true;
                }
                Common.EntryMode = 1;
                Common.ScannedEditTXTFlag = false;
                beepsound.start();
                ScanValueETxT.setText(s);
                try {
                    VesselScannedLogModel inputObj = new VesselScannedLogModel();
                    inputObj.setSBBlBarcode(Common.BarCode);
                    inputObj.setVesselTrsnID(Common.Export.VesselID);
                    if (Common.VBB_Number == null) {
                        inputObj.setVBBNumber(" ");
                    } else {
                        inputObj.setVBBNumber(Common.VBB_Number);
                    }
                    inputObj.setIMEINumber(Common.IMEI);
                    inputObj.setEndTime(Common.EndDate);
                    inputObj.setLocationID(Common.Export.Vessel_FromLocationID);
                    inputObj.setToLocationID(Common.Export.Vessel_ToLocationID);
                    //inputObj.setTransferModeID(Common.Export.Vessel_TransportMode);
                    inputObj.setTransferModeID(3);// for vessel transport
                    inputObj.setTransferAgencyId(Common.Export.Vessel_TransferAgencyID);
                    inputObj.setDriverId(Common.Export.Vessel_DriverID);
                    inputObj.setTruckPlateNumber(Common.Export.Vessel_TrucklicensePlateNo);
                    //inputObj.setTransportId(Common.Export.Vessel_TransportID);
                    inputObj.setTranferredCount(Common.Count);
                    inputObj.setUserID(Common.UserID);
                    inputObj.setVSTransferUniqueID(Common.Export.VesselUniqueID);
                    inputObj.setLoadedTypeID(Common.Export.Vessel_LoadedTypeID);
                    inputObj.setPortID(0);
                    inputObj.setEntryMode(Common.EntryMode);
                    inputObj.setQuality(Common.QualityName);
                    inputObj.setExportID(Common.ExportID);
                    vesselTransportAPI = ApiClient.getApiInterface();
                    vesselTransportAPI.getVesselTransportLogs(inputObj).enqueue(new Callback<VesselScannedLogModel>() {
                        @Override
                        public void onResponse(Call<VesselScannedLogModel> call, Response<VesselScannedLogModel> response) {
                            if (GwwException.GwwException(response.code()) == true) {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        Common.SyncStatusList.clear();
                                        Common.SyncStatusList.addAll(response.body().getStatus());
                                        if (Common.SyncStatusList.get(0).getStatus() == 1) {
                                            Common.VesselScannedResultList.clear();
                                            Common.VesselScannedResultList.addAll(response.body().getExportLogDetails());
                                            Double RemoveVolumeSum = 0.00;
                                            if (Common.VesselScannedResultList.size() > 0) {
                                                vessellogsadapter = new VesselTransferAdapter(Common.VesselScannedResultList, Common.QulaityDefaultList, getActivity());
                                                horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
                                                horizontalLayoutManager.setStackFromEnd(true);
                                                ScannedResultLV.setLayoutManager(horizontalLayoutManager);
                                                vessellogsadapter.notifyDataSetChanged();
                                                ScannedResultLV.setAdapter(vessellogsadapter);
                                                ScannedResultLV.setVisibility(View.VISIBLE);
                                                RemoveVolumeSum = TotalVolume(Common.VesselScannedResultList);
                                                ScanValueETxT.setText("");
                                                vessel_NoValueFoundTxT.setVisibility(View.GONE);
                                            } else {
                                                ScannedResultLV.setVisibility(View.GONE);
                                                vessel_NoValueFoundTxT.setVisibility(View.VISIBLE);
                                            }
                                            TotalScannedCount.setText(String.valueOf(Common.VesselScannedResultList.size()));
                                            VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(RemoveVolumeSum)));
                                            AlertDialogBox(CommonMessage(R.string.Vesselhead), "#" + Common.BarCode + "--" + Common.SyncStatusList.get(0).getMessage(), true);

                                        } else {
                                            AlertDialogBox(CommonMessage(R.string.Vesselhead), Common.SyncStatusList.get(0).getMessage(), false);
                                        }
                                    } else {
                                        AlertDialogBox(CommonMessage(R.string.Vesselhead), Common.SyncStatusList.get(0).getMessage(), false);
                                    }
                                } else {
                                    AlertDialogBox(CommonMessage(R.string.Vesselhead), "#" + Common.BarCode + "--" + "Not Synced", false);
                                }
                            } else {
                                Common.AuthorizationFlag = true;
                                AlertDialogBox(CommonMessage(R.string.Vesselhead), response.message(), false);
                            }
                        }

                        @Override
                        public void onFailure(Call<VesselScannedLogModel> call, Throwable t) {
                            AlertDialogBox(CommonMessage(R.string.Vesselhead), t.getMessage(), false);
                        }
                    });
                } catch (Exception ex) {
                    Common.AuthorizationFlag = true;
                    AlertDialogBox(CommonMessage(R.string.Vesselhead), ex.toString(), false);
                }
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public static int booleanToInt(boolean value) {
        // Convert true to 1 and false to 0.
        return value ? 1 : 0;
    }

    private void GetTransferScannedResultList() {
        try {
            VesselScannedDetailsModel inputObj = new VesselScannedDetailsModel();
            inputObj.setIMEI(Common.IMEI);
            inputObj.setVesselTrUniqueID(Common.Export.VesselUniqueID);
            vesselTransportAPI = ApiClient.getApiInterface();
            vesselTransportAPI.getVesselTransportLogsDetails(inputObj).enqueue(new Callback<VesselScannedDetailsModel>() {
                @Override
                public void onResponse(Call<VesselScannedDetailsModel> call, Response<VesselScannedDetailsModel> response) {
                    try {
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.isSuccessful()) {
                                Common.VesselScannedResultList.clear();
                                Common.VesselScannedResultList.addAll(response.body().getGetVesselScannedLogs());
                                Double RemoveVolumeSum = 0.00;
                                if (Common.VesselScannedResultList.size() > 0) {
                                    vessellogsadapter = new VesselTransferAdapter(Common.VesselScannedResultList, Common.QulaityDefaultList, getActivity());
                                    horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
                                    horizontalLayoutManager.setStackFromEnd(true);
                                    ScannedResultLV.setLayoutManager(horizontalLayoutManager);
                                    vessellogsadapter.notifyDataSetChanged();
                                    ScannedResultLV.setAdapter(vessellogsadapter);
                                    ScannedResultLV.setVisibility(View.VISIBLE);
                                    RemoveVolumeSum = TotalVolume(Common.VesselScannedResultList);
                                    vessel_NoValueFoundTxT.setVisibility(View.GONE);
                                } else {
                                    ScannedResultLV.setVisibility(View.GONE);
                                    vessel_NoValueFoundTxT.setVisibility(View.VISIBLE);
                                }
                                TotalScannedCount.setText(String.valueOf(Common.VesselScannedResultList.size()));
                                VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(RemoveVolumeSum)));
                            } else {
                                AlertDialogBox(CommonMessage(R.string.Vesselhead), "Not Synced", false);
                            }
                        } else {
                            Common.AuthorizationFlag = true;
                            AlertDialogBox(CommonMessage(R.string.Vesselhead), response.message(), false);
                        }

                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        AlertDialogBox(CommonMessage(R.string.Vesselhead), ex.getMessage(), false);
                    }
                }

                @Override
                public void onFailure(Call<VesselScannedDetailsModel> call, Throwable t) {
                    AlertDialogBox(CommonMessage(R.string.Vesselhead), t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
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
            switch (vessel_bcr.getNumParameter(BarCodeReader.ParamNum.IMG_FILE_FORMAT)) {
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
        vessel_scanService.startViewFinder();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private static void excelLog(int row, int col, String value) {
        HSSFRow myRow = mySheet.getRow(row);
        if (myRow == null)
            myRow = mySheet.createRow(row);
        HSSFCell myCell = myRow.createCell(col);
        myCell.setCellValue(value);
    }

    public class VesselTransferAdapter extends RecyclerView.Adapter<VesselTransferAdapter.GroceryViewHolder> {
        private List<VesselLogsDetailsModel> ScannedResultList;
        private List<String> QualitySpinner;
        Context context;

        public VesselTransferAdapter(List<VesselLogsDetailsModel> ScannedResultList, ArrayList<String> SpinnerData, Context context) {
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

        public VesselLogsDetailsModel getItem(int position) {
            return ScannedResultList.get(position);
        }

        @Override
        public VesselTransferAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vessel_logs_infliator, parent, false);
            VesselTransferAdapter.GroceryViewHolder gvh = new VesselTransferAdapter.GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(VesselTransferAdapter.GroceryViewHolder holder, final int position) {
            holder.Barcode.setText(ScannedResultList.get(position).getBarCode());
            holder.WoodSpiceCode.setText(ScannedResultList.get(position).getWoodSpieceCode());
            holder.ClassificationTXT.setText(ScannedResultList.get(position).getQuality());
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
            int color = generator.getColor(getItem(position));
            holder.Remove.setBackgroundColor(color);
            holder.Remove.setOnClickListener(v -> {
                if (Common.IsVesselEditListFlag == true) {
                    AlertDialogBox(CommonMessage(R.string.Vesselhead), "Can not edit Or delete after Synced", false);
                } else {
                    Common.RemoveSBBLabel = "";
                    Common.RemoveSBBLabel = ScannedResultList.get(position).getBarCode();
                    RemoveMessage(CommonMessage(R.string.Remove_Message));
                }

            });
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, QualitySpinner);
            holder.QualitySpinner.setAdapter(adapter);

            if (ScannedResultList.get(position).getQuality() != null) {
                int spinnerPosition = adapter.getPosition(ScannedResultList.get(position).getQuality());
                holder.QualitySpinner.setSelection(spinnerPosition);
            }

            holder.QualitySpinner.setOnTouchListener((v, event) -> {
                Common.isSpinnerTouched = true;
                return false;
            });

            holder.QualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int inside_position, long id) {
                    try {
                        if (Common.isSpinnerTouched == true) {
                            Common.QualityName = QualitySpinner.get(inside_position);
                            String sBBLabel = ScannedResultList.get(position).getBarCode();
                            //boolean UpdateFlag = mDBInternalHelper.UpdateQualityTansferList(Common.QualityName, sBBLabel, Common.TransferID);
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
            TextView Barcode, WoodSpiceCode, ClassificationTXT, Quality;
            Spinner QualitySpinner;
            LinearLayout Background, Remove;

            public GroceryViewHolder(View view) {
                super(view);
                Barcode = view.findViewById(R.id.vessel_Barcode);
                WoodSpiceCode = view.findViewById(R.id.vessel_woodspiceCode);
                ClassificationTXT = view.findViewById(R.id.vessel_Quality);
                QualitySpinner = view.findViewById(R.id.vessel_QualitySpinner);
                Background = view.findViewById(R.id.resultlayoutBackground);
                Remove = view.findViewById(R.id.removeBarCode_inven);

            }
        }

    }

    public void GetVesselTransportListDelete() {
        try {
            VesselCancelTransferModel inputObj = new VesselCancelTransferModel();
            inputObj.setBarcode(Common.RemoveSBBLabel);
            inputObj.setUserId(Common.UserID);
            inputObj.setVesselTrUniqueID(Common.Export.VesselUniqueID);
            vesselTransportAPI = ApiClient.getApiInterface();
            vesselTransportAPI.GetVesselCancelledTransferList(inputObj).enqueue(new Callback<VesselCancelTransferModel>() {
                @Override
                public void onResponse(Call<VesselCancelTransferModel> call, Response<VesselCancelTransferModel> response) {
                    try {
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.body() != null) {
                                Common.SyncStatusList.clear();
                                Common.SyncStatusList.addAll(response.body().getStatus());
                                if (Common.SyncStatusList.get(0).getStatus() == 1) {
                                    AlertDialogBox(CommonMessage(R.string.Vesselhead), "#" + Common.RemoveSBBLabel + "--" + Common.SyncStatusList.get(0).getMessage(), true);
                                    GetTransferScannedResultList();
                                } else {
                                    AlertDialogBox(CommonMessage(R.string.Vesselhead), "#" + Common.RemoveSBBLabel + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                                }
                            } else {
                                AlertDialogBox(CommonMessage(R.string.Vesselhead), "Not Synced", false);
                            }
                        } else {
                            Common.AuthorizationFlag = true;
                            AlertDialogBox(CommonMessage(R.string.Vesselhead), response.message(), false);
                        }

                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        AlertDialogBox(CommonMessage(R.string.Vesselhead), ex.getMessage(), false);
                    }
                }

                @Override
                public void onFailure(Call<VesselCancelTransferModel> call, Throwable t) {
                    AlertDialogBox(CommonMessage(R.string.Vesselhead), t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void RemoveMessage(String ErrorMessage) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(getActivity());
        Removebuilder.setMessage(ErrorMessage);
        Removebuilder.setCancelable(true);
        Removebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                (dialog, id) -> dialog.cancel());
        Removebuilder.setNegativeButton(CommonMessage(R.string.action_Remove),
                (dialog, id) -> {
                    GetVesselTransportListDelete();
                    dialog.cancel();
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public double TotalVolume(ArrayList<VesselLogsDetailsModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (VesselLogsDetailsModel vesselTransScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(vesselTransScanModel.getVolume().trim());
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

        } catch (Exception ex) {
            AlertDialogBox("TC-AddTransferList", ex.toString(), false);
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
            if (Common.Export.Vessel_TransportTypeId == modename.get(position).getTransportTypeId()) {
                TransferMode.setChecked(true);
                Common.Export.Vessel_TransportMode = modename.get(position).getTransportMode();
            } else {
                TransferMode.setChecked(false);
            }
            TransferMode.setText(modename.get(position).getTransportMode());
            TransferMode.setOnClickListener(v -> {
                Common.Export.Vessel_TransportTypeId = modename.get(position).getTransportTypeId();
                Common.Export.Vessel_TransportMode = modename.get(position).getTransportMode();
                notifyDataSetChanged();
            });

            TransferMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Common.Export.Vessel_TransportTypeId = modename.get(position).getTransportTypeId();
                Common.Export.Vessel_TransportMode = modename.get(position).getTransportMode();
                notifyDataSetChanged();
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
            CheckBox LoadedMode = view.findViewById(R.id.loadedMode_infi);
            if (modename.get(position).getIsActive() == 1) {
                LoadedMode.setVisibility(View.VISIBLE);
            } else {
                LoadedMode.setVisibility(View.GONE);
            }
            if (Common.Export.Vessel_LoadedTypeID == modename.get(position).getLoadedid()) {
                LoadedMode.setChecked(true);
                Common.Export.Vessel_LoadedName = modename.get(position).getName();
            } else {
                LoadedMode.setChecked(false);
            }
            LoadedMode.setText(modename.get(position).getName());
            LoadedMode.setOnClickListener(v -> {
                Common.Export.Vessel_LoadedTypeID = modename.get(position).getLoadedid();
                Common.Export.Vessel_LoadedName = modename.get(position).getName();
                notifyDataSetChanged();
            });

            LoadedMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Common.Export.Vessel_LoadedTypeID = modename.get(position).getLoadedid();
                Common.Export.Vessel_LoadedName = modename.get(position).getName();
                notifyDataSetChanged();
            });
            return view;
        }
    }

    public void InventoryTransferListActivty() {
        vesselDetailsLAY.setVisibility(View.GONE);
        GetVesselTransportList();
    }

    public class StringtreeNoadapter extends ArrayAdapter<String> {

        public StringtreeNoadapter(Context context, int layout, String[] from) {
            super(context, layout, from);

        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            view.setOnTouchListener((view1, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            view1.getApplicationWindowToken(), 0);
                }
                return false;
            });
            return view;
        }
    }

    public class AgencyAutoArrayAdapter extends ArrayAdapter<AgencyDetailsModel> {

        Context mContext;
        int layoutResourceId;
        AgencyDetailsModel data[] = null;

        public AgencyAutoArrayAdapter(Context mContext, int layoutResourceId, AgencyDetailsModel[] data) {
            super(mContext, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    convertView = inflater.inflate(layoutResourceId, parent, false);
                }
                AgencyDetailsModel objectItem = data[position];
                TextView textViewItem = convertView.findViewById(R.id.txt_agency);
                textViewItem.setText(objectItem.getAgencyName());
                if (objectItem.getIsBlocked() == 1) {
                    textViewItem.setBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    textViewItem.setBackgroundColor(getResources().getColor(R.color.amber));
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }

    public class DriverAutoArrayAdapter extends ArrayAdapter<DriverDetailsModel> {

        Context mContext;
        int layoutResourceId;
        DriverDetailsModel data[] = null;

        public DriverAutoArrayAdapter(Context mContext, int layoutResourceId, DriverDetailsModel[] data) {
            super(mContext, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    convertView = inflater.inflate(layoutResourceId, parent, false);
                }
                DriverDetailsModel objectItem = data[position];
                TextView textViewItem = convertView.findViewById(R.id.txt_agency);
                textViewItem.setText(objectItem.getDriverName());
                if (objectItem.getIsBlocked() == 1) {
                    textViewItem.setBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    textViewItem.setBackgroundColor(getResources().getColor(R.color.lime));
                }
            } catch (NullPointerException ex) {
                ex.printStackTrace();
                CrashAnalytics.CrashReport(ex);
            } catch (Exception ex) {
                ex.printStackTrace();
                CrashAnalytics.CrashReport(ex);
            }
            return convertView;
        }
    }

    public class TruckAutoArrayAdapter extends ArrayAdapter<TruckDetailsModel> {

        Context mContext;
        int layoutResourceId;
        TruckDetailsModel data[] = null;

        public TruckAutoArrayAdapter(Context mContext, int layoutResourceId, TruckDetailsModel[] data) {
            super(mContext, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    convertView = inflater.inflate(layoutResourceId, parent, false);
                }
                TruckDetailsModel objectItem = data[position];
                TextView textViewItem = convertView.findViewById(R.id.txt_agency);
                textViewItem.setText(objectItem.getTruckLicensePlateNo());
                if (objectItem.getIsBlocked() == 1) {
                    textViewItem.setBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    textViewItem.setBackgroundColor(getResources().getColor(R.color.orange));
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }

    // BarCode email id
    private boolean isValidBarCode(String barCode) {
        String BarCodeValidation =
                "[a-zA-Z]{2}" +
                        "\\-" +
                        "[0-9]{7}";
        Pattern pattern = Pattern.compile(BarCodeValidation);
        Matcher matcher = pattern.matcher(barCode);
        return matcher.matches();
    }

    private boolean isValidBarCode12(String barCode) {
        String BarCodeValidation =
                "[a-zA-Z]{2}" +
                        "\\-" +
                        "[0-9]{7}" +
                        "\\s" +
                        "[a-zA-Z]{1}";
        Pattern pattern = Pattern.compile(BarCodeValidation);
        Matcher matcher = pattern.matcher(barCode);
        return matcher.matches();
    }

    public void NextAlertDialog(String ErrorMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(ErrorMessage);
        builder1.setCancelable(true);
        builder1.setPositiveButton("No",
                (dialog, id) -> dialog.cancel());
        builder1.setNegativeButton("Yes",
                (dialog, id) -> {
                    try {
                        AddTransferList();
                        dialog.cancel();
                    } catch (Exception ex) {
                        AlertDialogBox("NextAlertDialog", ex.toString(), false);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void Signout(String ErrorMessage) {
        if (Common.IsEditorViewFlag == false) {
            InventoryTransferListActivty();
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage(ErrorMessage);
            builder1.setCancelable(true);
            builder1.setPositiveButton("No",
                    (dialog, id) -> dialog.cancel());
            builder1.setNegativeButton("Yes",
                    (dialog, id) -> {
                        try {
                            InventoryTransferListActivty();
                        } catch (Exception ex) {
                            AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
                        }
                        dialog.cancel();
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Log.i(TAG, "onCreateAnimation");
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    }

    private void advanceSearchApi() {
        try {
            if (AdvaneSearchValidation(treeNoEDT.getText().toString(), fellingIDEDT.getText().toString())) {
                ProgressBarLay.setVisibility(View.VISIBLE);
                veesel_searchModel = new VesselInputAdvanceSearchModel();
                veesel_searchModel.setFSnumber(Integer.parseInt(fellingIDEDT.getText().toString()));
                veesel_searchModel.setTreeNumber(Integer.parseInt(treeNoEDT.getText().toString()));
                veesel_searchModel.setExportID(Common.ExportID);
                Log.e("GsonBuilder", ">>>>>>>" + new GsonBuilder().create().toJson(veesel_searchModel));
                vesselTransportAPI = ApiClient.getApiInterface();
                vesselTransportAPI.GetVesselAdvanceSearch(veesel_searchModel).enqueue(new Callback<VesselInputAdvanceSearchModel>() {
                    @Override
                    public void onResponse(Call<VesselInputAdvanceSearchModel> call, Response<VesselInputAdvanceSearchModel> response) {
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.isSuccessful()) {
                                ArrayList<VesselAdvanceSearchModel> advanceSearchOutputModels = response.body().getGetVesselAdvanceSearch();
                                if (advanceSearchOutputModels.size() > 0) {
                                    advancedSearchadapter = new AdvanceSearchResultAdapter(advanceSearchOutputModels, getActivity());
                                    horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                    advanceSearchRLV.setLayoutManager(horizontalLayoutManager);
                                    advanceSearchRLV.setAdapter(advancedSearchadapter);
                                    advanceSearchRLV.setVisibility(View.VISIBLE);
                                    as_NovalueFound.setVisibility(View.GONE);
                                } else {
                                    advanceSearchRLV.setVisibility(View.GONE);
                                    as_NovalueFound.setVisibility(View.VISIBLE);
                                }
                            } else {
                                AlertDialogBox("AdvanceSearched", "#" + "" + "--" + "Failed AdvancedSearched", false);
                            }
                        } else {
                            Common.AuthorizationFlag = true;
                            AlertDialogBox(CommonMessage(R.string.Vesselhead), response.message(), false);
                        }
                        ProgressBarLay.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<VesselInputAdvanceSearchModel> call, Throwable t) {
                        ProgressBarLay.setVisibility(View.GONE);
                        AlertDialogBox(CommonMessage(R.string.Vesselhead), t.getMessage(), false);
                    }
                });
            }
        } catch (Exception e) {

        }
    }

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

    public class AdvanceSearchResultAdapter extends RecyclerView.Adapter<AdvanceSearchResultAdapter.GroceryViewHolder> {
        private List<VesselAdvanceSearchModel> AdvanceSearchResultList;
        Context context;

        public AdvanceSearchResultAdapter(List<VesselAdvanceSearchModel> ScannedResultList, Context context) {
            this.AdvanceSearchResultList = ScannedResultList;
            this.context = context;
        }

        @Override
        public AdvanceSearchResultAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.export_advancedsearch_infliator, parent, false);
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
            holder.Lenght.setText(String.valueOf(AdvanceSearchResultList.get(position).getLength()));
            holder.Volume.setText(String.valueOf(Common.decimalFormat.format(AdvanceSearchResultList.get(position).getVolume())));  //specie
            holder.F1.setText(String.valueOf(AdvanceSearchResultList.get(position).getF1()));
            holder.F2.setText(String.valueOf(AdvanceSearchResultList.get(position).getF2()));
            holder.T1.setText(String.valueOf(AdvanceSearchResultList.get(position).getT1()));
            holder.T2.setText(String.valueOf(AdvanceSearchResultList.get(position).getT2()));
            holder.Diameter.setText(String.valueOf(AdvanceSearchResultList.get(position).getDiameter()));
            holder.Select.setOnClickListener(v -> {
                advanceSearchLAY.setVisibility(View.GONE);
                ScannedResult(AdvanceSearchResultList.get(position).getSBBLabel());
            });

        }

        @Override
        public int getItemCount() {
            return AdvanceSearchResultList.size();
        }

        public class GroceryViewHolder extends RecyclerView.ViewHolder {
            TextView SBBLabel, WSpiceCode, Lenght, Volume, Select, F1, F2, T1, T2, Diameter;
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
                Diameter = view.findViewById(R.id.as_diameter);
            }
        }
    }
}
