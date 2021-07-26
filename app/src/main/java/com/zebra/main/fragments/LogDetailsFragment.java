package com.zebra.main.fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.IBinder;
import android.os.PowerManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.GsonBuilder;
import com.zebra.R;
import com.zebra.adc.decoder.BarCodeReader;
import com.zebra.android.jb.Preference;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.activity.FellingRegistration.DimensionCalculation;
import com.zebra.main.adapter.ContainernoAdapter;
import com.zebra.main.api.ApiClient;
import com.zebra.main.api.ApiInterface;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.model.AdvanceSearchOutputModel;
import com.zebra.main.model.Export.ContainersModel;
import com.zebra.main.model.Export.DeleteExportLogModel;
import com.zebra.main.model.Export.ExportSbbLabelInputModel;
import com.zebra.main.model.Export.ExportSbblabelOutputModel;
import com.zebra.main.model.Export.InsertExportLogsModel;
import com.zebra.main.model.Export.LogDetailsModel;
import com.zebra.main.model.Export.LogSummaryModel;
import com.zebra.main.model.Export.WoodSpeiceModel;
import com.zebra.main.model.InputAdvanceSearchOutputModel;
import com.zebra.main.sdl.SdlScanListener;
import com.zebra.main.sdl.SdlScanService;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;
import com.zebra.utilities.ConnectivityReceiver;
import com.zebra.utilities.GwwException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LogDetailsFragment extends Fragment implements SdlScanListener {
    protected String TAG = getClass().getSimpleName();
    View ScannedView;
    static final private boolean saveSnapshot = false;
    MediaPlayer beepsound, wronBuzzer;
    private BarCodeReader bcr = null;
    private PowerManager.WakeLock mWakeLock = null;
    private int motionEvents = 0, modechgEvents = 0, snapNum = 0;
    boolean bind = false;
    private SdlScanService scanService;
    AlertDialogManager alert = new AlertDialogManager();
    Intent service;
    //DatabaseHelper dbBackend;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;
    TextView TotalScannedLogsCountTXT, OrderRemainingCBMTxT, TotalScannedCount, VolumeTXT, Diameter_check, TreeNo_check, SBBL_CheckTxT, WSC_CheckTxT, AgeOfLog_CheckTxT, NoteF_CheckTxT, NoteT_CheckTxT,
            NoteL_CheckTxT, Footer1_CheckTxT, Footer2_CheckTxT, Top1_CheckTxT, Top2_CheckTxT, Lenght_CheckTxT, Volume_CheckTxT, Save_CheckTxT, Reject_checkTxT, Sbblabel_details_TXT, NoValueFoundTxT,
            ValidBarcode_TXT, LD_SBBLabel, LD_Diameter, LD_Volume, as_NovalueFound, NewMeusermentTxT, RemarksTxT, HeartMesurmantTXT, HeartMesDiameter, HeartMesVolume;
    EditText ScanValueETxT = null, LD_F1, LD_F2, LD_T1, LD_T2, LD_Length, LD_Remarks, LengthCutFootETxT, LengthCutTopETxT, HoleF1ETxT, HoleF2ETxT, HoleT1ETxT, HoleT2ETxT, CrackF1ETxT, CrackF2ETxT,
            CrackT1ETxT, CrackT2ETxT, SapDeductionsETxT, HeartMesF1ETxT, HeartMesF2ETxT, HeartMesT1ETxT,
            HeartMestT2ETxT;
    ImageView BarCodeScanBTN;
    private FloatingActionButton image = null;
    LinearLayout SBBLabel_Layout, ProgressBarLay, UpdateLogDetailsLAY, advanceSearchLAY, SbblabelChildDetailsLAY, NewMeusermentLAY, RemarksLAY, HeartMesurmentLAY;

    private RecyclerView ExportDetailsLV, advanceSearchRLV, SbblabelChildDetailsLv;
    Spinner containerSpinners, quotationSpinners, LD_ContainerSpinner, LD_WoodSpieceCodeSpinner;
    private ExportDetailsAdapter Exportadapter;
    LinearLayoutManager horizontalLayoutManager;
    AlertDialog.Builder Removebuilder = null;
    AlertDialog RemoveAlert = null;
    ExportSbbLabelInputModel exportInputmodel;
    Context FragContext;
    ApiInterface BarCodeCheck = null, InsertExport = null;
    BarCodeReceiver barcode_receiver;
    List<String> ContainerNo_List;
    List<Integer> ContainerID_List;
    HashMap<Integer, String> ContainerMap = new HashMap<Integer, String>();
    ContainernoAdapter containerAdapter;
    boolean isInternetPresent, isWhiteRedlayout, IsInsertFag;
    RelativeLayout rootView;
    InsertExportLogsModel insertexportmodel;
    DeleteExportLogModel deleteexportmodel;
    ArrayAdapter StringWSCNoadapter;
    AdvanceSearchResultAdapter advancedSearchadapter;
    SbblabelChildDetailsAdapter sbblabelChildDetailsAdapter;
    String TreeNO, FellingID;
    EditText treeNoEDT, fellingIDEDT;
    InputAdvanceSearchOutputModel outputModel;
    double spliteRemoveVolumeSum = 0.00, spliteQuantityCBMVolume = 0.00, spliteScannedCBMVolume = 0.00;
    ArrayList<LogDetailsModel> ExportDetailsFilter = new ArrayList<>();
    String[] SelectedBarcode;
    StringBuilder RemarksType;
    ArrayList<String> RemarksTypeList = new ArrayList<>();

    private class BarCodeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            GetExportDetailsList(Common.Export.ExportDetailsList);
            GetContainerlist();
            Common.HideKeyboard(getActivity());
        }
    }

    public LogDetailsFragment() {
        // Required empty public constructor
        FragContext = getActivity();

    }

    @Override
    public void onStart() {
        super.onStart();
        beepsound = MediaPlayer.create(getActivity(), R.raw.beep);
        wronBuzzer = MediaPlayer.create(getActivity(), R.raw.wrong_buzzer);
        //ImportDataBaseFromInternalStorage();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "OnPause");
        LogDetailsPauseANDResume();
    }

    public void LogDetailsPauseANDResume() {
        Log.i(TAG, "setUserVisibleHint-OnPause");
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(barcode_receiver);
            //scanService.release();
            if (scanService != null)
                scanService.setActivityUp(false);
            releaseWakeLock();
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
        Log.i(TAG, "setUserVisibleHint-onDestroy");
        try {
            if (bind) {
                if (!Preference.getScanSelfopenSupport(getActivity(), true)) {
                    getActivity().stopService(service);
                }
                getActivity().unbindService(serviceConnection);
                scanService = null;
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            //AlertDialogBox("onDestroy-setUserVisibleHint", ex.toString(), false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser == false) {
            LogDetailsPauseANDResume();
        } else {
            if (Common.BarcodeScannerFlagForExport == true) {
                Log.i(TAG, "setUserVisibleHint-onResume");
                try {
                    service = new Intent(getActivity(), SdlScanService.class);
                    getActivity().bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);
                    getActivity().startService(service);
                } catch (Exception ex) {
                    CrashAnalytics.CrashReport(ex);
                }
                try {
                    ScannedStatus("");//getResources().getString(R.string.app_name) + " v" + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
                    if (scanService != null)
                        scanService.setActivityUp(true);
                    acquireWakeLock();
                    barcode_receiver = new BarCodeReceiver();
                    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(barcode_receiver, new IntentFilter("LOGDETAILS_REFRESH"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (bind) {
                if (!Preference.getScanSelfopenSupport(getActivity(), true)) {
                    getActivity().stopService(service);
                }
                getActivity().unbindService(serviceConnection);
                scanService = null;
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("onDestroy-setUserVisibleHint", ex.toString(), false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        try {
            service = new Intent(getActivity(), SdlScanService.class);
            getActivity().bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(service);
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
        try {
            ScannedStatus("");//getResources().getString(R.string.app_name) + " v" + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
            if (scanService != null)
                scanService.setActivityUp(true);
            acquireWakeLock();
            barcode_receiver = new BarCodeReceiver();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(barcode_receiver,
                    new IntentFilter("LOGDETAILS_REFRESH"));
        } catch (EnumConstantNotPresentException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScannedView = inflater.inflate(R.layout.fragment_scanedbarcodes, container, false);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mDBExternalHelper = new ExternalDataBaseHelperClass(getActivity());
        mDBInternalHelper = new InternalDataBaseHelperClass(getActivity());
        InsertExport = ApiClient.getInstance().getUserService();
        // Inflate the layout for this fragment
        Initialization();
        Click_Listener();
        GetContainerlist();
        FilterWIthCOntainerNo(Common.Export.ExportDetailsList, Common.ContainerID);
        //WoodSpeicesList();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightView = rootView.getHeight();
            int widthView = rootView.getWidth();
            if (1.0 * widthView / heightView > 3) {
                HideScan(false);
            } else {
                HideScan(true);
            }
        });
        Common.HideKeyboard(getActivity());
        return ScannedView;
    }

    public void Initialization() {
        TotalScannedLogsCountTXT = ScannedView.findViewById(R.id.TotalScannedLogsCount);
        OrderRemainingCBMTxT = ScannedView.findViewById(R.id.OrderRemainingCBMTXT);
        ScanValueETxT = ScannedView.findViewById(R.id.textStatus);
        ScanValueETxT.setOnTouchListener(GeneralOnTouchListener);
        TotalScannedCount = ScannedView.findViewById(R.id.TotalScannedCount);
        VolumeTXT = ScannedView.findViewById(R.id.TotalScannedVolume);
        BarCodeScanBTN = ScannedView.findViewById(R.id.exportScanBTN);
        ScannedView.findViewById(R.id.exportScanBTN).setOnClickListener(Obj_clickListener);
        ScannedView.findViewById(R.id.exportPlantEnterIMG).setOnClickListener(Obj_clickListener);
        SBBLabel_Layout = ScannedView.findViewById(R.id.SbbLabelCheck_layout);
        SBBLabel_Layout.setVisibility(View.GONE);
        ProgressBarLay = ScannedView.findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        Diameter_check = ScannedView.findViewById(R.id.diameter_check);
        TreeNo_check = ScannedView.findViewById(R.id.treeNo_check);
        SBBL_CheckTxT = ScannedView.findViewById(R.id.sbblabel_check);
        WSC_CheckTxT = ScannedView.findViewById(R.id.wsc_check);
        AgeOfLog_CheckTxT = ScannedView.findViewById(R.id.ageoflog_check);
        NoteF_CheckTxT = ScannedView.findViewById(R.id.noteF_check);
        NoteT_CheckTxT = ScannedView.findViewById(R.id.noteT_check);
        NoteL_CheckTxT = ScannedView.findViewById(R.id.noteL_check);
        Footer1_CheckTxT = ScannedView.findViewById(R.id.footer1_check);
        Footer2_CheckTxT = ScannedView.findViewById(R.id.footer2_check);
        Top1_CheckTxT = ScannedView.findViewById(R.id.top1_check);
        Top2_CheckTxT = ScannedView.findViewById(R.id.top2_check);
        Lenght_CheckTxT = ScannedView.findViewById(R.id.lenght_check);
        Volume_CheckTxT = ScannedView.findViewById(R.id.volume_check);
        Sbblabel_details_TXT = ScannedView.findViewById(R.id.sbblabel_detailsTXT);
        Save_CheckTxT = ScannedView.findViewById(R.id.Save_check);
        Reject_checkTxT = ScannedView.findViewById(R.id.Reject_check);
        ScannedView.findViewById(R.id.Save_check).setOnClickListener(Obj_clickListener);
        ScannedView.findViewById(R.id.Reject_check).setOnClickListener(Obj_clickListener);
        ExportDetailsLV = ScannedView.findViewById(R.id.exportPlan_lv);
        NoValueFoundTxT = ScannedView.findViewById(R.id.NovalueFound);
        ScannedView.findViewById(R.id.closeActivity_check).setOnClickListener(Obj_clickListener);
        containerSpinners = ScannedView.findViewById(R.id.containerSpinners);
        containerSpinners.setOnTouchListener(GeneralOnTouchListener);
        quotationSpinners = ScannedView.findViewById(R.id.quotationSpinners);
        ValidBarcode_TXT = ScannedView.findViewById(R.id.isValidLog_TxT);
        rootView = ScannedView.findViewById(R.id.addresses_confirm_root_view);
        // Advcance Search layout
        advanceSearchLAY = ScannedView.findViewById(R.id.advancesearchLAY);
        advanceSearchLAY.setVisibility(View.GONE);
        advanceSearchRLV = ScannedView.findViewById(R.id.as_scan_lv);
        as_NovalueFound = ScannedView.findViewById(R.id.as_NovalueFound);
        SbblabelChildDetailsLAY = ScannedView.findViewById(R.id.sbblabelChildDetailsLAY);
        SbblabelChildDetailsLAY.setVisibility(View.GONE);
        SbblabelChildDetailsLv = ScannedView.findViewById(R.id.sbblabel_child_details_lv);
        ScannedView.findViewById(R.id.advancedsearchIMG).setOnClickListener(Obj_clickListener);
        ScannedView.findViewById(R.id.as_closeActivity).setOnClickListener(Obj_clickListener);
        ScannedView.findViewById(R.id.as_scannerList).setOnClickListener(Obj_clickListener);
        treeNoEDT = ScannedView.findViewById(R.id.as_treenoEDT);
        fellingIDEDT = ScannedView.findViewById(R.id.as_fellingEDT);
        ScannedView.findViewById(R.id.child_SBBLabel_closeActivity).setOnClickListener(Obj_clickListener);

        // update details layout
        UpdateLogDetailsLAY = ScannedView.findViewById(R.id.updateLogDetailsLayout);
        UpdateLogDetailsLAY.setVisibility(View.GONE);

        ScannedView.findViewById(R.id.updateLog_close).setOnClickListener(Obj_clickListener);
        ScannedView.findViewById(R.id.Update_LogDetails).setOnClickListener(Obj_clickListener);
        ScannedView.findViewById(R.id.cancel_LogDetails).setOnClickListener(Obj_clickListener);

        LD_WoodSpieceCodeSpinner = ScannedView.findViewById(R.id.logUpdate_WSC);
        LD_SBBLabel = ScannedView.findViewById(R.id.logUpdate_sbblabel);
        LD_Diameter = ScannedView.findViewById(R.id.logUpdate_diameter);
        LD_F1 = ScannedView.findViewById(R.id.logUpdate_footer1);
        LD_F2 = ScannedView.findViewById(R.id.logUpdate_footer2);
        LD_T1 = ScannedView.findViewById(R.id.logUpdate_top1);
        LD_T2 = ScannedView.findViewById(R.id.logUpdate_top2);
        LD_Length = ScannedView.findViewById(R.id.logUpdate_lenght);
        LD_Volume = ScannedView.findViewById(R.id.logUpdate_Volume);
        LD_Remarks = ScannedView.findViewById(R.id.logUpdate_Remarks);
        LD_ContainerSpinner = ScannedView.findViewById(R.id.logUpdate_containerSpinners);
        LD_ContainerSpinner.setOnTouchListener(GeneralOnTouchListener);

        LD_F1.addTextChangedListener(new GenericTextWatcher(LD_F1));
        LD_F2.addTextChangedListener(new GenericTextWatcher(LD_F2));
        LD_T1.addTextChangedListener(new GenericTextWatcher(LD_T1));
        LD_T2.addTextChangedListener(new GenericTextWatcher(LD_T2));
        LD_Length.addTextChangedListener(new GenericTextWatcher(LD_Length));

        LD_F1.setOnTouchListener(GeneralOnTouchListener);
        LD_F2.setOnTouchListener(GeneralOnTouchListener);
        LD_T1.setOnTouchListener(GeneralOnTouchListener);
        LD_T2.setOnTouchListener(GeneralOnTouchListener);
        LD_Length.setOnTouchListener(GeneralOnTouchListener);


        LengthCutFootETxT = ScannedView.findViewById(R.id.LengthCutFootETXT);
        LengthCutTopETxT = ScannedView.findViewById(R.id.LengthCutTopETX);
        HoleF1ETxT = ScannedView.findViewById(R.id.HoleFoot1ETXT);
        HoleF2ETxT = ScannedView.findViewById(R.id.HoleFoot2ETXT);
        HoleT1ETxT = ScannedView.findViewById(R.id.HoleTop1ETXT);
        HoleT2ETxT = ScannedView.findViewById(R.id.HoleTop2ETXT);
        CrackF1ETxT = ScannedView.findViewById(R.id.F1CrackETXT);
        CrackF2ETxT = ScannedView.findViewById(R.id.F2CrackETXT);
        CrackT1ETxT = ScannedView.findViewById(R.id.T1CrackETXT);
        CrackT2ETxT = ScannedView.findViewById(R.id.T2CrackETXT);
        SapDeductionsETxT = ScannedView.findViewById(R.id.SapDeductionsETXT);

        NewMeusermentTxT = ScannedView.findViewById(R.id.NewMeusermentTxT);
        RemarksTxT = ScannedView.findViewById(R.id.RemarksTxT);
        HeartMesurmantTXT = ScannedView.findViewById(R.id.HeartMeusermentTxT);
        ScannedView.findViewById(R.id.NewMeusermentTxT).setOnClickListener(Obj_clickListener);
        ScannedView.findViewById(R.id.RemarksTxT).setOnClickListener(Obj_clickListener);
        ScannedView.findViewById(R.id.HeartMeusermentTxT).setOnClickListener(Obj_clickListener);
        NewMeusermentLAY = ScannedView.findViewById(R.id.NewMeusermentLAY);
        RemarksLAY = ScannedView.findViewById(R.id.RemarksLAY);
        HeartMesurmentLAY = ScannedView.findViewById(R.id.HeartMesurmentLAY);

        HeartMesF1ETxT = ScannedView.findViewById(R.id.heartMes_footer1);
        HeartMesF2ETxT = ScannedView.findViewById(R.id.heartMes_footer2);
        HeartMesT1ETxT = ScannedView.findViewById(R.id.heartMes_top1);
        HeartMestT2ETxT = ScannedView.findViewById(R.id.heartMes_top2);
        HeartMesDiameter = ScannedView.findViewById(R.id.heartMes_diameter);
        HeartMesVolume = ScannedView.findViewById(R.id.heartMes_Volume);

        HeartMesF1ETxT.addTextChangedListener(new GenericTextWatcher(HeartMesF1ETxT));
        HeartMesF2ETxT.addTextChangedListener(new GenericTextWatcher(HeartMesF2ETxT));
        HeartMesT1ETxT.addTextChangedListener(new GenericTextWatcher(HeartMesT1ETxT));
        HeartMestT2ETxT.addTextChangedListener(new GenericTextWatcher(HeartMestT2ETxT));

        HeartMesF1ETxT.setOnTouchListener(GeneralOnTouchListener);
        HeartMesF2ETxT.setOnTouchListener(GeneralOnTouchListener);
        HeartMesT1ETxT.setOnTouchListener(GeneralOnTouchListener);
        HeartMestT2ETxT.setOnTouchListener(GeneralOnTouchListener);
    }

    public void WoodSpeicesList() {
        try {
            Common.Export.LogSummaryWSCList.clear();
            Common.Export.LogSummaryWSCcodeList.clear();
            for (int i = 0; i < Common.Export.LogSummaryList.size(); i++) {
                WoodSpeiceModel WSCode = new WoodSpeiceModel();
                WSCode.setSpecieId(Common.Export.LogSummaryList.get(i).getSpecieId());
                WSCode.setWoodSpeciesCode(Common.Export.LogSummaryList.get(i).getWoodSpeciesCode());
                Common.Export.LogSummaryWSCList.add(WSCode);
                Common.Export.LogSummaryWSCcodeList.add(Common.Export.LogSummaryList.get(i).getWoodSpeciesCode());
            }
            StringWSCNoadapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, Common.Export.LogSummaryWSCcodeList);
            StringWSCNoadapter.notifyDataSetChanged();
            LD_WoodSpieceCodeSpinner.setAdapter(StringWSCNoadapter);
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void GetContainerlist() {
        try {
            ContainerNo_List = new ArrayList<String>();
            ContainerID_List = new ArrayList<Integer>();
            for (ContainersModel contaiLstModel : Common.Export.ContainerList) {
                ContainerNo_List.add(String.valueOf(contaiLstModel.getContainerNumber()));
                ContainerID_List.add(contaiLstModel.getContainerId());
                ContainerMap.put(contaiLstModel.getContainerId(), String.valueOf(contaiLstModel.getContainerNumber()));
            }
            containerAdapter = new ContainernoAdapter(getActivity(), ContainerNo_List);
            containerSpinners.setAdapter(containerAdapter);
        } catch (Exception e) {

        }
    }

    public boolean onBackPressed() {
        return false;
    }

    public void HideScan(boolean HideOpenFlag) {
        BarCodeScanBTN = ScannedView.findViewById(R.id.exportScanBTN);
        if (HideOpenFlag == true) {
            //BarCodeScanBTN.show();
        } else {
            //BarCodeScanBTN.hide();
        }

    }

    public void Click_Listener() {

        ScanValueETxT.addTextChangedListener(new TextWatcher() {
            private int lastLength;
            private boolean backSpace;

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
                            ScanValueETxT.setError("Barcode should be like(XX-1111111)");
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
                                    ScanValueETxT.setError("Barcode should be like(XX-1111111)");
                                }
                            } else {
                                if (Common.ScannedEditTXTFlag == true) {
                                    ScanValueETxT.setError(CommonMessage(R.string.BarCodeLenghtMsg));
                                }
                            }
                        }
                    }
                }
            }
        });

        containerSpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int main_pos, long id) {
                Common.ContainerFilterpostion = main_pos;
              /*  if (Common.IsContainerSelectFilterFlag == true) {
                    Common.IsContainerSelectFilterFlag = false;
                    Common.IsContainerSelectFlag = false;*/
                try {
                    /*if (ContainerNo_List.equals("ALL") || ContainerNo_List.equals("Un Assigned")) {
                        Common.ContainerID = 0;
                    }*/

                    if (ContainerNo_List.equals("ALL")) {
                        Common.ContainerID = ContainerID_List.get(main_pos);
                    } else if (ContainerNo_List.equals("Un Assigned")) {
                        Common.ContainerID = 0;
                    } else {
                        Common.ContainerID = ContainerID_List.get(main_pos);
                    }
                    FilterWIthCOntainerNo(Common.Export.ExportDetailsList, Common.ContainerID);
                } catch (Exception ex) {
                    CrashAnalytics.CrashReport(ex);
                }
                // }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LD_ContainerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int main_pos, long id) {
                Common.ContainerFilterpostion = main_pos;
                if (Common.IsContainerSelectFilterFlag == true) {
                    Common.IsContainerSelectFilterFlag = false;
                    Common.IsContainerSelectFlag = false;
                    try {
                        if (ContainerNo_List.equals("ALL") || ContainerNo_List.equals("N/A")) {
                            Common.ContainerID = 0;
                        } else {
                            Common.ContainerID = ContainerID_List.get(Common.ContainerFilterpostion);
                        }
                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    View.OnTouchListener GeneralOnTouchListener = (v, event) -> {
        try {
            switch (v.getId()) {
                case R.id.logUpdate_footer1:
                    Common.IsVolumeCalculationFlag = true;
                    break;
                case R.id.logUpdate_footer2:
                    Common.IsVolumeCalculationFlag = true;
                    break;
                case R.id.logUpdate_top1:
                    Common.IsVolumeCalculationFlag = true;
                    break;
                case R.id.logUpdate_top2:
                    Common.IsVolumeCalculationFlag = true;
                    break;
                case R.id.textStatus:
                    Common.ScannedEditTXTFlag = true;
                    ScanValueETxT.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    ScanValueETxT.setFocusableInTouchMode(true);
                    break;
                case R.id.containerSpinners:
                    Common.IsContainerSelectFilterFlag = true;
                    break;
                case R.id.logUpdate_containerSpinners:
                    Common.IsContainerSelectFilterFlag = true;
                    break;
                case R.id.heartMes_footer1:
                    Common.IsVolumeCalculationFlag = true;
                    break;
                case R.id.heartMes_footer2:
                    Common.IsVolumeCalculationFlag = true;
                    break;
                case R.id.heartMes_top1:
                    Common.IsVolumeCalculationFlag = true;
                    break;
                case R.id.heartMes_top2:
                    Common.IsVolumeCalculationFlag = true;
                    break;
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
        return false;
    };

    private class GenericTextWatcher implements TextWatcher {

        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void afterTextChanged(Editable editable) {
            try {
                String s = editable.toString();
                switch (view.getId()) {
                    case R.id.logUpdate_footer1:
                        if (Common.IsVolumeCalculationFlag == true) {
                            if (s.length() > 0) {
                                if (!s.equals("0")) {
                                    String LDF2 = LD_F2.getText().toString();
                                    String LDT1 = LD_T1.getText().toString();
                                    String LDT2 = LD_T2.getText().toString();
                                    String LDLength = LD_Length.getText().toString();
                                    String Volume = String.valueOf(Common.decimalFormat.format(DimensionCalculation.UpdateVolumeCalculation(s, LDF2, LDT1, LDT2, LDLength, "0", "0", "0")));
                                    LD_Volume.setText(Volume);
                                    LD_Diameter.setText(String.valueOf(
                                            DiameterCalculation(
                                                    Integer.parseInt(s),
                                                    Integer.parseInt(LDF2),
                                                    Integer.parseInt(LDT1),
                                                    Integer.parseInt(LDT2))));
                                }
                            }
                        }
                        break;
                    case R.id.logUpdate_footer2:
                        if (Common.IsVolumeCalculationFlag == true) {
                            if (s.length() > 0) {
                                if (!s.equals("0")) {
                                    String LDF1 = LD_F1.getText().toString();
                                    String LDT1 = LD_T1.getText().toString();
                                    String LDT2 = LD_T2.getText().toString();
                                    String LDLength = LD_Length.getText().toString();
                                    String Volume = String.valueOf(Common.decimalFormat.format(DimensionCalculation.UpdateVolumeCalculation(LDF1, s, LDT1, LDT2, LDLength, "0", "0", "0")));
                                    LD_Volume.setText(Volume);
                                    LD_Diameter.setText(String.valueOf(DiameterCalculation(
                                            Integer.parseInt(LDF1),
                                            Integer.parseInt(s),
                                            Integer.parseInt(LDT1),
                                            Integer.parseInt(LDT2))));
                                }
                            }
                        }
                        break;
                    case R.id.logUpdate_top1:
                        if (Common.IsVolumeCalculationFlag == true) {
                            if (s.length() > 0) {
                                if (!s.equals("0")) {
                                    String LDF1 = LD_F1.getText().toString();
                                    String LDF2 = LD_F2.getText().toString();
                                    String LDT2 = LD_T2.getText().toString();
                                    String LDLength = LD_Length.getText().toString();
                                    String Volume = String.valueOf(Common.decimalFormat.format(DimensionCalculation.UpdateVolumeCalculation(LDF1, LDF2, s, LDT2, LDLength, "0", "0", "0")));
                                    LD_Volume.setText(Volume);
                                    LD_Diameter.setText(String.valueOf(DiameterCalculation(
                                            Integer.parseInt(LDF1),
                                            Integer.parseInt(LDF2),
                                            Integer.parseInt(s),
                                            Integer.parseInt(LDT2))));
                                }
                            }
                        }
                        break;
                    case R.id.logUpdate_top2:
                        if (Common.IsVolumeCalculationFlag == true) {
                            if (s.length() > 0) {
                                if (!s.equals("0")) {
                                    String LDF1 = LD_F1.getText().toString();
                                    String LDF2 = LD_F2.getText().toString();
                                    String LDT1 = LD_T1.getText().toString();
                                    String LDLength = LD_Length.getText().toString();
                                    String Volume = String.valueOf(Common.decimalFormat.format(DimensionCalculation.UpdateVolumeCalculation(LDF1, LDF2, LDT1, s, LDLength, "0", "0", "0")));
                                    LD_Volume.setText(Volume);
                                    LD_Diameter.setText(String.valueOf(DiameterCalculation(
                                            Integer.parseInt(LDF1),
                                            Integer.parseInt(LDF2),
                                            Integer.parseInt(LDT1),
                                            Integer.parseInt(s))));
                                }
                            }
                        }
                        break;
                    case R.id.logUpdate_lenght:
                        if (Common.IsVolumeCalculationFlag == true) {
                            if (s.length() > 0) {
                                if (!s.equals("0")) {
                                    String LDF1 = LD_F1.getText().toString();
                                    String LDF2 = LD_F2.getText().toString();
                                    String LDT1 = LD_T1.getText().toString();
                                    String LDT2 = LD_T2.getText().toString();
                                    String Volume = String.valueOf(Common.decimalFormat.format(DimensionCalculation.UpdateVolumeCalculation(LDF1, LDF2, LDT1, LDT2, s, "0", "0", "0")));
                                    LD_Volume.setText(Volume);
                                    LD_Diameter.setText(String.valueOf(DiameterCalculation(
                                            Integer.parseInt(LD_F1.getText().toString()),
                                            Integer.parseInt(LD_F2.getText().toString()),
                                            Integer.parseInt(LD_T1.getText().toString()),
                                            Integer.parseInt(LD_T2.getText().toString()))));
                                }
                            }
                        }
                        break;
                    case R.id.heartMes_footer1:
                        if (Common.IsVolumeCalculationFlag == true) {
                            if (s.length() > 0) {
                                if (!s.equals("0")) {
                                    String HF2 = HeartMesF2ETxT.getText().toString();
                                    String HT1 = HeartMesT1ETxT.getText().toString();
                                    String HT2 = HeartMestT2ETxT.getText().toString();
                                    String HLength = LD_Length.getText().toString();
                                    String Volume = String.valueOf(Common.decimalFormat.format(DimensionCalculation.UpdateVolumeCalculation(s, HF2, HT1, HT2, HLength, "0", "0", "0")));
                                    HeartMesVolume.setText(Volume);
                                    HeartMesDiameter.setText(String.valueOf(DiameterCalculation(
                                            Integer.parseInt(s),
                                            Integer.parseInt(HF2),
                                            Integer.parseInt(HT1),
                                            Integer.parseInt(HT2))));
                                }
                            }
                        }
                        break;
                    case R.id.heartMes_footer2:
                        if (Common.IsVolumeCalculationFlag == true) {
                            if (s.length() > 0) {
                                if (!s.equals("0")) {
                                    String HF1 = HeartMesF1ETxT.getText().toString();
                                    String HT1 = HeartMesT1ETxT.getText().toString();
                                    String HT2 = HeartMestT2ETxT.getText().toString();
                                    String HLength = LD_Length.getText().toString();
                                    String Volume = String.valueOf(Common.decimalFormat.format(DimensionCalculation.UpdateVolumeCalculation(HF1, s, HT1, HT2, HLength, "0", "0", "0")));
                                    HeartMesVolume.setText(Volume);
                                    HeartMesDiameter.setText(String.valueOf(
                                            DiameterCalculation(
                                                    Integer.parseInt(HF1),
                                                    Integer.parseInt(s),
                                                    Integer.parseInt(HT1),
                                                    Integer.parseInt(HT2))));
                                }
                            }
                        }
                        break;
                    case R.id.heartMes_top2:
                        if (Common.IsVolumeCalculationFlag == true) {
                            if (s.length() > 0) {
                                if (!s.equals("0")) {
                                    String HF1 = HeartMesF1ETxT.getText().toString();
                                    String HF2 = HeartMesF2ETxT.getText().toString();
                                    String HT1 = HeartMesT1ETxT.getText().toString();
                                    String HLength = LD_Length.getText().toString();
                                    String Volume = String.valueOf(Common.decimalFormat.format(DimensionCalculation.UpdateVolumeCalculation(HF1, HF2, HT1, s, HLength, "0", "0", "0")));
                                    HeartMesVolume.setText(Volume);
                                    HeartMesDiameter.setText(String.valueOf(
                                            DiameterCalculation(
                                                    Integer.parseInt(HF1),
                                                    Integer.parseInt(HF2),
                                                    Integer.parseInt(HT1),
                                                    Integer.parseInt(s))));
                                }
                            }
                        }
                        break;
                    case R.id.heartMes_top1:
                        if (Common.IsVolumeCalculationFlag == true) {
                            if (s.length() > 0) {
                                if (!s.equals("0")) {
                                    String HF1 = HeartMesF1ETxT.getText().toString();
                                    String HF2 = HeartMesF2ETxT.getText().toString();
                                    String HT2 = HeartMestT2ETxT.getText().toString();
                                    String HLength = LD_Length.getText().toString();
                                    String Volume = String.valueOf(Common.decimalFormat.format(DimensionCalculation.UpdateVolumeCalculation(HF1, HF2, s, HT2, HLength, "0", "0", "0")));
                                    HeartMesVolume.setText(Volume);
                                    HeartMesDiameter.setText(String.valueOf(
                                            DiameterCalculation(
                                                    Integer.parseInt(HF1),
                                                    Integer.parseInt(HF2),
                                                    Integer.parseInt(s),
                                                    Integer.parseInt(HT2))));
                                }
                            }
                        }
                        break;
                }
            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
            }
        }
    }

    View.OnClickListener Obj_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                    case R.id.as_scannerList:
                         Common.HideKeyboard(getActivity());
                        advanceSearchApi();
                        break;
                    case R.id.as_closeActivity:
                        advanceSearchLAY.setVisibility(View.GONE);
                        break;
                    case R.id.child_SBBLabel_closeActivity:
                        SbblabelChildDetailsLAY.setVisibility(View.GONE);
                        break;
                    case R.id.advancedsearchIMG:
                        advanceSearchLAY.setVisibility(View.VISIBLE);
                        treeNoEDT.setText("");
                        fellingIDEDT.setText("");
                        advanceSearchRLV.setVisibility(View.GONE);
                        //as_NovalueFound.setVisibility(View.VISIBLE);
                        break;
                    case R.id.exportScanBTN:
                        ScanValueETxT.setText("");
                        Common.EntryMode = 1;
                        Common.ScanMode = true;
                        if (Common.BarcodeScannerforLogdetails == true) {
                            scanService.doDecode();
                        }
                        break;
                    case R.id.exportPlantEnterIMG:
                        if (Common.Export.LogSummaryList.size() > 0) {
                            Common.ScanMode = false;
                            String SBBLabel = ScanValueETxT.getText().toString();
                            if (SBBLabel.length() == Common.SBBlenght) {
                                Common.EntryMode = 2;
                                ScannedResult(SBBLabel);
                            } else {
                                ScanValueETxT.setError(CommonMessage(R.string.ValidBarCodeMsg));
                            }
                        } else {
                            AlertDialogBox("LogSummary", "please add one quotation", false);
                        }
                        break;
                    case R.id.Update_LogDetails:
                        if (UpdateSbbLabelValidation(LD_F1.getText().toString(), LD_F2.getText().toString(), LD_T1.getText().toString(), LD_T2.getText().toString(), LD_Length.getText().toString())) {
                            if (LogHeartValidation(
                                    HeartMesF1ETxT.getText().toString(),
                                    HeartMesF2ETxT.getText().toString(),
                                    HeartMesT1ETxT.getText().toString(),
                                    HeartMestT2ETxT.getText().toString())) {

                                Common.Export_Diameter = LD_Diameter.getText().toString();
                                Common.Export_Footer_1 = (int) Math.round(Double.parseDouble(LD_F1.getText().toString()));
                                Common.Export_Footer_2 = (int) Math.round(Double.parseDouble(LD_F2.getText().toString()));
                                Common.Export_Top_1 = (int) Math.round(Double.parseDouble(LD_T1.getText().toString()));
                                Common.Export_Top_2 = (int) Math.round(Double.parseDouble(LD_T2.getText().toString()));
                                Common.Export_Length = LD_Length.getText().toString();
                                Common.Export_Volume = LD_Volume.getText().toString();
                                if (LD_Remarks.getText().toString().length() > 0) {
                                    Common.Export_Remarks = LD_Remarks.getText().toString();
                                } else {
                                    Common.Export_Remarks = "";
                                }
                                Common.Export_UpdateFlag = 0;
                                Common.Export.IsSplite = 0;
                                ArrayList<LogDetailsModel> ExportDetailsUpdaFilter = new ArrayList<>();
                                for (LogDetailsModel LogDetailsM : Common.Export.ExportDetailsList) {
                                    if (LogDetailsM.getContainerId() == Common.ContainerID) {
                                        ExportDetailsUpdaFilter.add(LogDetailsM);
                                    }
                                }
                                spliteRemoveVolumeSum = SbbLableTotalVolume(ExportDetailsUpdaFilter);
                                spliteRemoveVolumeSum = spliteRemoveVolumeSum + Double.parseDouble(Common.Export_Volume);
                                if (Common.ContainerID == -1 || Common.ContainerID == 0) {
                                } else {
                                    if (spliteRemoveVolumeSum > Common.ExportSingleContainerTotValue) {
                                        UpdateContainerLimitMessage("Container CBM Limit", "Container cannot allow to add more then " + Common.ExportSingleContainerTotValue + " Volume", true);
                                        return;
                                    }
                                }
                                IsInsertFag = true;
                                InsertExportDetails();
                                Common.IsContainerSelectFlag = false;
                                containerSpinners.setSelection(LD_ContainerSpinner.getSelectedItemPosition());
                                Common.IsVolumeCalculationFlag = false;
                            }
                        }
                        break;
                    case R.id.updateLog_close:
                        UpdateLogDetailsLAY.setVisibility(View.GONE);
                        Common.IsVolumeCalculationFlag = false;
                        break;
                    case R.id.cancel_LogDetails:
                        Common.HideKeyboard(getActivity());
                        UpdateLogDetailsLAY.setVisibility(View.GONE);
                        Common.IsVolumeCalculationFlag = false;
                        //AlertDialogBox("Reject Barcode", "In Future", false);
                        break;
                    case R.id.Save_check:
                        Common.Export_Remarks = "";
                        Common.Export_UpdateFlag = 1;
                        Common.Export.IsSplite = 0;
                        IsInsertFag = false;
                        InsertExportDetails();
                        break;
                    case R.id.Reject_check:
                        SBBLabel_Layout.setVisibility(View.GONE);
                        break;
                    case R.id.closeActivity_check:
                        SBBLabel_Layout.setVisibility(View.GONE);
                        break;
                    case R.id.NewMeusermentTxT:
                        NewMeusermentLAY.setVisibility(View.VISIBLE);
                        RemarksLAY.setVisibility(View.GONE);
                        HeartMesurmentLAY.setVisibility(View.GONE);
                        NewMeusermentTxT.setBackgroundColor(getActivity().getResources().getColor(R.color.amber));
                        RemarksTxT.setBackgroundColor(getActivity().getResources().getColor(R.color.colorbtnsplash));
                        HeartMesurmantTXT.setBackgroundColor(getActivity().getResources().getColor(R.color.colorbtnsplash));
                        break;
                    case R.id.RemarksTxT:
                        NewMeusermentLAY.setVisibility(View.GONE);
                        RemarksLAY.setVisibility(View.VISIBLE);
                        HeartMesurmentLAY.setVisibility(View.GONE);
                        NewMeusermentTxT.setBackgroundColor(getActivity().getResources().getColor(R.color.colorbtnsplash));
                        RemarksTxT.setBackgroundColor(getActivity().getResources().getColor(R.color.amber));
                        HeartMesurmantTXT.setBackgroundColor(getActivity().getResources().getColor(R.color.colorbtnsplash));
                        break;
                    case R.id.HeartMeusermentTxT:
                        NewMeusermentLAY.setVisibility(View.GONE);
                        RemarksLAY.setVisibility(View.GONE);
                        HeartMesurmentLAY.setVisibility(View.VISIBLE);
                        NewMeusermentTxT.setBackgroundColor(getActivity().getResources().getColor(R.color.colorbtnsplash));
                        RemarksTxT.setBackgroundColor(getActivity().getResources().getColor(R.color.colorbtnsplash));
                        HeartMesurmantTXT.setBackgroundColor(getActivity().getResources().getColor(R.color.amber));

                        break;

                }

            } catch (Exception ex) {
                AlertDialogBox("OonClickListener", ex.toString(), false);
            }
        }
    };

    public boolean LogHeartValidation(String lht1, String lht2, String lhf1, String lhf2) {
        boolean Validattion = true;
        if (lhf1.equals("") && lhf2.equals("") && lht1.equals("") && lht2.equals("")) {
            Validattion = true;
        } else {
            if (!lhf1.equals("") && !lhf2.equals("") && !lht1.equals("") && !lht2.equals("")) {
                Validattion = true;
            } else {
                Validattion = false;
                AlertDialogBox("Heart Dimensions", "Please enter remaining dimensions!", false);
            }
        }
        return Validattion;
    }

    private void FilterWIthCOntainerNo(ArrayList<LogDetailsModel> ExportDetails, int ContainerID) {
        if (ExportDetailsFilter.size() > 0) {
            ExportDetailsFilter.clear();
        }
        if (ContainerID == -1) {
            ExportDetailsFilter.addAll(ExportDetails);
        } else {
            for (LogDetailsModel LogDetailsM : ExportDetails) {
                if (LogDetailsM.getContainerId() == ContainerID) {
                    ExportDetailsFilter.add(LogDetailsM);
                }
            }
        }
        GetExportDetailsList(ExportDetailsFilter);
    }

    // BarCode
    private boolean isValidBarCode(String barCode) {
        String BarCodeValidation =
                "[a-zA-Z]{2}" +
                        "\\-" +
                        "[0-9]{7}";
        Pattern pattern = Pattern.compile(BarCodeValidation);
        Matcher matcher = pattern.matcher(barCode);
        return matcher.matches();
    }

    public boolean UpdateSbbLabelValidation(String F1_Str, String F2_Str, String T1_Str, String T2_Str, String L_Str) {
        boolean Validattion = true;
        if (F1_Str.equals("") && F2_Str.equals("") && T1_Str.equals("") && T2_Str.equals("") && L_Str.equals("")) {
            Validattion = true;
        } else {
            if (!F1_Str.equals("") && !F2_Str.equals("") && !T1_Str.equals("") && !T2_Str.equals("") && !L_Str.equals("")) {
                Validattion = true;
            } else {
                Validattion = false;
                AlertDialogBox("Dimensions", "Please enter remaining dimensions!", false);
            }
        }
        if (!isNullOrEmpty(L_Str)) {
            if (Double.valueOf(L_Str) > Common.TreelenghtLimit) {
                AlertDialogBox("Update", "Invalid Tree Length - It should not more then " + Common.TreelenghtLimit, false);
                Validattion = false;
            }
        }
        return Validattion;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

    public void InsertExportDetails() {
        try {
            RemarksTypeList.clear();
            RemarksType = new StringBuilder();
            ProgressBarLay.setVisibility(View.VISIBLE);
            //Scanned Result Refresh
            insertexportmodel = new InsertExportLogsModel();
            insertexportmodel.setExportId(Common.ExportID);
            insertexportmodel.setContainerId(Common.ContainerID);
            insertexportmodel.setSBBLabel(Common.Export_Sbblabel);

            insertexportmodel.setDiameterF1(Common.Export_Footer_1);
            insertexportmodel.setDiameterF2(Common.Export_Footer_2);
            insertexportmodel.setDiameterT1(Common.Export_Top_1);
            insertexportmodel.setDiameterT2(Common.Export_Top_2);

            insertexportmodel.setDiameterAverage(Common.Export_Diameter);
            insertexportmodel.setLength(Common.Export_Length);
            insertexportmodel.setVolume(Common.Export_Volume);
            insertexportmodel.setSpecieId(Integer.parseInt(Common.WoodSpieceID));
            insertexportmodel.setRemarks(Common.Export_Remarks);
            insertexportmodel.setUpdateFlag(Common.Export_UpdateFlag);
            insertexportmodel.setUserID(1);
            insertexportmodel.setIsSplit(Common.Export.IsSplite);

            //deductions

            insertexportmodel.setLengthCutFoot(isEditTextEmptyDouble(LengthCutFootETxT.getText().toString()));
            insertexportmodel.setLengthCutTop(isEditTextEmptyDouble(LengthCutTopETxT.getText().toString()));
            insertexportmodel.setHoleFoot1(isEditTextEmptyDouble(HoleF1ETxT.getText().toString()));
            insertexportmodel.setHoleFoot2(isEditTextEmptyDouble(HoleF2ETxT.getText().toString()));
            insertexportmodel.setHoleTop1(isEditTextEmptyDouble(HoleT1ETxT.getText().toString()));
            insertexportmodel.setHoleTop2(isEditTextEmptyDouble(HoleT2ETxT.getText().toString()));
            insertexportmodel.setCrackFoot1(isEditTextEmptyDouble(CrackF1ETxT.getText().toString()));
            insertexportmodel.setCrackFoot2(isEditTextEmptyDouble(CrackF2ETxT.getText().toString()));
            insertexportmodel.setCrackTop1(isEditTextEmptyDouble(CrackT1ETxT.getText().toString()));
            insertexportmodel.setCrackTop2(isEditTextEmptyDouble(CrackT2ETxT.getText().toString()));
            insertexportmodel.setSapDeduction(isEditTextEmptyDouble(SapDeductionsETxT.getText().toString()));

            insertexportmodel.setHF1(isEditTextEmptyDouble(HeartMesF1ETxT.getText().toString()));
            insertexportmodel.setHF2(isEditTextEmptyDouble(HeartMesF2ETxT.getText().toString()));
            insertexportmodel.setHT1(isEditTextEmptyDouble(HeartMesT1ETxT.getText().toString()));
            insertexportmodel.setHT2(isEditTextEmptyDouble(HeartMestT2ETxT.getText().toString()));
            insertexportmodel.setHAvg(isEditTextEmptyDouble(HeartMesDiameter.getText().toString()));
            insertexportmodel.setHvolume(isEditTextEmptyDouble(HeartMesVolume.getText().toString()));

            if (IsInsertFag) {
                if (isEditTextEmptyDouble(LengthCutFootETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(LengthCutTopETxT.getText().toString()) > 0.0
                        || isEditTextEmptyDouble(HoleF1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(HoleF2ETxT.getText().toString()) > 0.0
                        || isEditTextEmptyDouble(HoleT1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(HoleT2ETxT.getText().toString()) > 0.0
                        || isEditTextEmptyDouble(CrackF1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(CrackF2ETxT.getText().toString()) > 0.0
                        || isEditTextEmptyDouble(CrackT1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(CrackT2ETxT.getText().toString()) > 0.0
                        || isEditTextEmptyDouble(SapDeductionsETxT.getText().toString()) > 0.0) {

                    if (isEditTextEmptyDouble(LengthCutFootETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(LengthCutTopETxT.getText().toString()) > 0.0) {
                        RemarksTypeList.add("1,");
                    } else {
                        if (RemarksTypeList.contains("1,")) {
                            RemarksTypeList.remove("1,");
                        }
                    }
                    if (isEditTextEmptyDouble(HoleF1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(HoleF2ETxT.getText().toString()) > 0.0
                            || isEditTextEmptyDouble(HoleT1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(HoleT2ETxT.getText().toString()) > 0.0) {
                        RemarksTypeList.add("2,");
                    } else {
                        if (RemarksTypeList.contains("2,")) {
                            RemarksTypeList.remove("2,");
                        }
                    }
                    if (isEditTextEmptyDouble(CrackF1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(CrackF2ETxT.getText().toString()) > 0.0
                            || isEditTextEmptyDouble(CrackT1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(CrackT2ETxT.getText().toString()) > 0.0) {
                        RemarksTypeList.add("3,");
                    } else {
                        if (RemarksTypeList.contains("3,")) {
                            RemarksTypeList.remove("3,");
                        }
                    }
                    if (isEditTextEmptyDouble(SapDeductionsETxT.getText().toString()) > 0.0) {
                        RemarksTypeList.add("4,");
                    } else {
                        if (RemarksTypeList.contains("4,")) {
                            RemarksTypeList.remove("4,");
                        }
                    }
                    if (isEditTextEmptyDouble(HeartMesF1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(HeartMesF2ETxT.getText().toString()) > 0.0
                            || isEditTextEmptyDouble(HeartMesT1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(HeartMestT2ETxT.getText().toString()) > 0.0) {
                        RemarksTypeList.add("5,");
                    } else {
                        if (RemarksTypeList.contains("5,")) {
                            RemarksTypeList.remove("5,");
                        }
                    }
                } else {
                    RemarksTypeList.add("0,");
                }
                for (String Remarks : RemarksTypeList) {
                    RemarksType.append(Remarks);
                }
                insertexportmodel.setRemarksType(RemarksType.toString());
            } else {
                insertexportmodel.setRemarksType("0,");
            }

            Log.e("GsonBuilder", ">>>>>>>" + new GsonBuilder().create().toJson(insertexportmodel));
            InsertExport = ApiClient.getApiInterface();
            InsertExport.InsertExportLogs(insertexportmodel).enqueue(new Callback<InsertExportLogsModel>() {
                @Override
                public void onResponse(Call<InsertExportLogsModel> call, Response<InsertExportLogsModel> response) {
                    if (GwwException.GwwException(response.code()) == true) {
                        if (response.isSuccessful()) {
                            Common.StatusVerificationList.clear();
                            Common.StatusVerificationList = response.body().getInsertLogStatus();
                            if (Common.StatusVerificationList.get(0).getStatus() == 1) {
                                beepsound.start();
                                Common.Export.LogSummaryList.clear();
                                Common.Export.ContainerList.clear();
                                Common.Export.ExportDetailsList.clear();
                                Common.Export.TotalCBMDetailsList.clear();
                                Common.Export.LogSummaryList.addAll(response.body().getLogSummaryDetails());
                                Common.Export.ContainerList.addAll(response.body().getContainersDetails());
                                Common.Export.ExportDetailsList.addAll(response.body().getLogDetails());
                                Common.Export.TotalCBMDetailsList.addAll(response.body().getTotalCBMDetails());
                                //Scanned Result Refresh
                                FilterWIthCOntainerNo(Common.Export.ExportDetailsList, Common.ContainerID);
                                ClearSbbLayout();
                                ScanValueETxT.setText("");
                                AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.Export_Sbblabel + " " + Common.StatusVerificationList.get(0).getMessage(), true);
                                if (SBBLabel_Layout.getVisibility() == View.VISIBLE) {
                                    SBBLabel_Layout.setVisibility(View.GONE);
                                }
                                if (UpdateLogDetailsLAY.getVisibility() == View.VISIBLE) {
                                    UpdateLogDetailsLAY.setVisibility(View.GONE);
                                }
                            } else {
                                AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.Export_Sbblabel + "-" + Common.StatusVerificationList.get(0).getMessage(), true);
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.Export_Sbblabel + "-" + response.message(), false);
                        }
                    } else {
                        Common.AuthorizationFlag = true;
                        AlertDialogBox(CommonMessage(R.string.ExportHead), response.message(), false);
                    }
                    ProgressBarLay.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<InsertExportLogsModel> call, Throwable t) {
                    ProgressBarLay.setVisibility(View.GONE);
                    AlertDialogBox(CommonMessage(R.string.ExportHead), t.getMessage(), false);
                }
            });
           /* boolean exportInserFlag = mDBInternalHelper.insertExportDetailsResult(
                    Common.ExportID, Common.ExportUniqueID, Common.Order_Number, Common.QuotationID, Common.QuotationUniqueNo, Common.ContainerNo, Common.QuotationNo, Common.ToLocationID, Common.IMEI,
                    Common.Export_PvNo, Common.Export_PvDate, Common.SbbLabel, Common.BarCode, Common.WoodSpieceID, Common.Selected_QutWsCode, Common.Export_WSC, Common.Export_AgeOFLog, Common.Export_Footer_1,
                    Common.Export_Footer_2, Common.Export_Top_1, Common.Export_Top_2, Common.Export_Length, Common.UserID, Common.EntryMode, Common.Selected_QutTotCBM, Common.Export_Volume, Common.DateTime,
                    Common.IsActive, Common.Export_NoteF, Common.Export_NoteT, Common.Export_NoteL, Common.IsValidVolume, Common.IsValidWSCode, Common.IsValidDiameter, Common.Export_Diameter, Common.Selected_QutDiameter);
            */
        } catch (Exception ex) {
            ProgressBarLay.setVisibility(View.GONE);
            AlertDialogBox(CommonMessage(R.string.ExportHead), ex.toString(), false);
        }
    }

    public void DeleteBarcodeAPI(int ExpId, String BarCode) {
        try {
            ProgressBarLay.setVisibility(View.VISIBLE);
            deleteexportmodel = new DeleteExportLogModel();
            deleteexportmodel.setExportId(ExpId);
            deleteexportmodel.setSBBLabel(BarCode);
            deleteexportmodel.setUserID(1);

            String stringInput = new GsonBuilder().create().toJson(deleteexportmodel);
            Log.e("DeleteBarcodeAPI", ">>>>>>" + stringInput);
            InsertExport = ApiClient.getApiInterface();
            InsertExport.DeleteExportLogs(deleteexportmodel).enqueue(new Callback<DeleteExportLogModel>() {
                @Override
                public void onResponse(Call<DeleteExportLogModel> call, Response<DeleteExportLogModel> response) {
                    try {
                        ProgressBarLay.setVisibility(View.GONE);
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.isSuccessful()) {
                                Common.StatusVerificationList.clear();
                                Common.StatusVerificationList = response.body().getDeleteLogStatus();
                                if (Common.StatusVerificationList.get(0).getStatus() == 1) {
                                    Common.Export.LogSummaryList.clear();
                                    Common.Export.ContainerList.clear();
                                    Common.Export.ExportDetailsList.clear();
                                    Common.Export.TotalCBMDetailsList.clear();
                                    Common.Export.LogSummaryList.addAll(response.body().getLogSummaryDetails());
                                    Common.Export.ContainerList.addAll(response.body().getContainersDetails());
                                    Common.Export.ExportDetailsList.addAll(response.body().getLogDetails());
                                    Common.Export.TotalCBMDetailsList.addAll(response.body().getTotalCBMDetails());
                                    FilterWIthCOntainerNo(Common.Export.ExportDetailsList, Common.ContainerID);
                                    AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + BarCode + " " + Common.StatusVerificationList.get(0).getMessage(), true);
                                } else {
                                    AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + BarCode + " " + Common.StatusVerificationList.get(0).getMessage(), true);
                                }
                            } else {
                                AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + BarCode + " " + response.message(), false);
                            }
                        } else {
                            Common.AuthorizationFlag = true;
                            AlertDialogBox(CommonMessage(R.string.ExportHead), response.message(), false);
                        }
                    } catch (Exception ex) {

                    }
                }

                @Override
                public void onFailure(Call<DeleteExportLogModel> call, Throwable t) {

                }
            });


        } catch (Exception ex) {

        }
    }

    private void GetExportDetailsList(ArrayList<LogDetailsModel> ExportDetailsList) {
        try {
            // Common.HideKeyboard(this);
            SelectedBarcode = new String[ExportDetailsList.size()];
            Common.Export.LogSummaryInternalWSCList.clear();
            if (Common.Export.ExportDetailsList.size() > 0) {
                Exportadapter = new ExportDetailsAdapter(ExportDetailsList, getActivity());
                horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                ExportDetailsLV.setLayoutManager(horizontalLayoutManager);
                Exportadapter.notifyDataSetChanged();
                ExportDetailsLV.setAdapter(Exportadapter);
                NoValueFoundTxT.setVisibility(View.GONE);
                ExportDetailsLV.setVisibility(View.VISIBLE);
            } else {
                NoValueFoundTxT.setVisibility(View.VISIBLE);
                ExportDetailsLV.setVisibility(View.GONE);
            }
            for (LogSummaryModel QuoaModel : Common.Export.LogSummaryList) {
                Common.Export.LogSummaryInternalWSCList.add(QuoaModel.getWoodSpeciesCode());
            }
            OrderRemainingCBMTxT.setText("Rem CBM: " + Common.decimalFormat.format(Common.Export.TotalCBMDetailsList.get(0).getRemainingVolum()));
            TotalScannedLogsCountTXT.setText("Scanned CBM: " + Common.decimalFormat.format(Common.Export.TotalCBMDetailsList.get(0).getScannedVolum()));
            //Remainng CBM
            TotalScannedCount.setText(String.valueOf(ExportDetailsList.size()));
            double LogDetailsVolumeSum = TotalVolume(ExportDetailsList);
            VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(LogDetailsVolumeSum)));
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public double TotalVolume(ArrayList<LogDetailsModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (LogDetailsModel exportDetailsModel : TotalScannedList) {
            TotVolume = TotVolume + exportDetailsModel.getVolume();
        }
        return TotVolume;
    }

    public double SbbLableTotalVolume(ArrayList<LogDetailsModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (LogDetailsModel exportDetailsModel : TotalScannedList) {
            TotVolume = TotVolume + exportDetailsModel.getVolume();
        }
        return TotVolume;
    }

    private void UpdateContainerList() {
        try {
            for (ContainersModel containModel : Common.Export.ContainerList) {
                String Volume = String.valueOf(mDBInternalHelper.TotalVolumeForExportForContainer(Common.ExportID, containModel.getContainerNumber()));
                int Count = mDBInternalHelper.TotalCountForExportForContainer(Common.ExportID, containModel.getContainerNumber());
                /*Update Volume and Count*/
                boolean ContVolUpdate = mDBInternalHelper.UpdateExportContainerList(containModel.getContainerNumber(), Volume, Count);
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bind = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bind = true;
            SdlScanService.MyBinder myBinder = (SdlScanService.MyBinder) service;
            scanService = myBinder.getService();

            //SdlScanListener Sample = null;
            scanService.setOnScanListener(LogDetailsFragment.this);
            scanService.setActivityUp(true);
        }
    };

    // display status string
    public void ScannedStatus(String s) {
        // tvStat.setText(s);
    }

    // display status resource id
    public void ScannedStatus(int id) {
        //tvStat.setText(id);
    }

    // display error msg
    private void dspErr(String s) {
        //tvStat.setText("ERROR" + s);
    }

    // display status string
    public void ScannedResult(String s) {
        //  Common.HideKeyboard(this);
        try {
            if (s != null && s.length() > 0) {
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
                // Common.IMEI = getImeiNumber();
                Common.IsActive = 1;
                if (checkConnection() == true) {
                    GetExportBarCodeCheckAPI();
                }
                Common.ScannedEditTXTFlag = false;
                ScanValueETxT.setText(s);
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public static int booleanToInt(boolean value) {
        // Convert true to 1 and false to 0.
        return value ? 1 : 0;
    }

    public void HideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void onPictureTaken(int format, int width, int height, byte[] abData, BarCodeReader
            reader) {
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
                break;
        }
    }

    private Bitmap rotated(Bitmap bmSnap) {
        Matrix matrix = new Matrix();
        if (matrix != null) {
            matrix.postRotate(90);
            Bitmap bmr = Bitmap.createBitmap(bmSnap, 0, 0, bmSnap.getWidth(), bmSnap.getHeight(), matrix, true);
            if (bmr != null)
                return bmr;
        }

        return bmSnap;
    }

    @Override
    public void henResult(String codeType, String context) {
    }

    @Override
    public void result(String content) {
    }

    public void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
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

    public static <T> boolean IsNullOrEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(getActivity(), Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public String CommonMessage(int Common_Msg) {
        return this.getResources().getString(Common_Msg);
    }

    public void RemoveMessage(String ErrorMessage, int ExportId, String Barcode) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(getActivity());
        Removebuilder.setMessage(ErrorMessage);
        Removebuilder.setCancelable(true);
        Removebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Removebuilder.setNegativeButton(CommonMessage(R.string.action_Remove),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            DeleteBarcodeAPI(ExportId, Barcode);
                        } catch (Exception e) {

                        }
                        dialog.cancel();
                    }
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public void UpdateExportList() {
        try {
            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            Double RemoveVolumeSum = TotalVolume(Common.Export.ExportDetailsList);
            // Update values into ListID
            boolean ListIdFlag = mDBInternalHelper.UpdateExportListID(Common.EndDate, Common.Export.ExportDetailsList.size(), Common.ExportID, String.valueOf(RemoveVolumeSum), 1, Common.ExportUniqueID);
            /*if (ListIdFlag == true) {
                Common.InventoryCountList = mDBInternalHelper.getInventoryCountIdList();
            }*/
        } catch (Exception ex) {
            AlertDialogBox("UpdateExportList", ex.toString(), false);
        }
    }

    public void GetExportBarCodeCheckAPI() {
        try {
             Common.HideKeyboard(getActivity());
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.Export.AllExportDetails.clear();
            exportInputmodel = new ExportSbbLabelInputModel();
            exportInputmodel.setSbbLabel(Common.BarCode);
            exportInputmodel.setExportid(Common.ExportID);

            BarCodeCheck = ApiClient.getApiInterface();
            BarCodeCheck.getBarCodeCheck(exportInputmodel).enqueue(new Callback<ExportSbbLabelInputModel>() {
                @Override
                public void onResponse(Call<ExportSbbLabelInputModel> call, Response<ExportSbbLabelInputModel> response) {
                    try {
                        ProgressBarLay.setVisibility(View.GONE);
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.isSuccessful()) {
                                Common.Export.AllExportDetails.addAll(response.body().getSbbLabelDetails());
                                if (Common.Export.AllExportDetails.size() > 0) {
                                    if (Common.Export.AllExportDetails.size() > 1) {
                                        //Splitted logs showing
                                        if (Common.Export.AllExportDetails.get(0).getIsValidLog().equals("Active")) {
                                            SbblabelChildDetailsLAY.setVisibility(View.VISIBLE);
                                            sbblabelChildDetailsAdapter = new SbblabelChildDetailsAdapter(Common.Export.AllExportDetails, getActivity());
                                            horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                            SbblabelChildDetailsLv.setLayoutManager(horizontalLayoutManager);
                                            SbblabelChildDetailsLv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                                            SbblabelChildDetailsLv.setAdapter(sbblabelChildDetailsAdapter);
                                        } else {
                                            AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.BarCode + "-" + Common.Export.AllExportDetails.get(0).getErrorMessage(), true);
                                        }
                                    } else {
                                        // Checked active or In-active
                                        if (Common.Export.AllExportDetails.get(0).getIsValidLog().equals("Active")) {
                                            GetSbbLabelDetails();
                                        } else {
                                            if (Common.Export.AllExportDetails.get(0).getIsValidLog().equals("In-Active")) {
                                                GetSbbLabelDetails();
                                                AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.BarCode + "-" + Common.Export.AllExportDetails.get(0).getErrorMessage(), true);
                                            } else {
                                                AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.BarCode + "-" + Common.Export.AllExportDetails.get(0).getErrorMessage(), true);
                                            }
                                        }
                                    }
                                }
                            } else {
                                AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.BarCode + "-" + response.message(), false);
                            }
                        } else {
                            Common.AuthorizationFlag = true;
                            AlertDialogBox(CommonMessage(R.string.ExportHead), response.message(), false);
                        }
                    } catch (Exception e) {
                        AlertDialogBox(CommonMessage(R.string.ExportHead), e.getMessage(), false);
                    }
                }

                @Override
                public void onFailure(Call<ExportSbbLabelInputModel> call, Throwable t) {
                    ProgressBarLay.setVisibility(View.GONE);
                    AlertDialogBox(CommonMessage(R.string.ExportHead), t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            ProgressBarLay.setVisibility(View.GONE);
            AlertDialogBox(CommonMessage(R.string.ExportHead), ex.toString(), false);
        }
    }

    public void GetSbbLabelDetails() {
        try {
            isWhiteRedlayout = false;
            //Wood Species Code Check
            Common.Export_WSC = Common.Export.AllExportDetails.get(0).getWoodSpeciesCode();
            if (Common.Export.LogSummaryInternalWSCList.contains(Common.Export_WSC)) {
                WSC_CheckTxT.setBackgroundColor(getResources().getColor(R.color.color_white));
                Save_CheckTxT.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
                Common.IsValidWSCode = 0;
            } else {
                AlertDialogBox("LOGS", "cannot allow to add this WoodSpecieCode #" + Common.Export_WSC + ", Because It is not belongs to quotation specie List", false);
                return;
            }

            Double RemoveVolumeSum = SbbLableTotalVolume(ExportDetailsFilter);
            Double QuantityCBMVolume = 0.00, ScannedCBMVolume = 0.00;
            RemoveVolumeSum = RemoveVolumeSum + Common.Export.AllExportDetails.get(0).getVolume();
            if (Common.ContainerID == -1 || Common.ContainerID == 0) {
            } else {
                if (RemoveVolumeSum > Common.ExportSingleContainerTotValue) {
                    ContainerLimitMessage("Container CBM Limit", "Container cannot allow to add more then " + Common.ExportSingleContainerTotValue + " Volume", true);
                }
            }
            Common.Export_Sbblabel = Common.Export.AllExportDetails.get(0).getSBBLabel();
            Common.Export_PvNo = Common.Export.AllExportDetails.get(0).getExaminationNo();
            Common.Export_PvDate = Common.Export.AllExportDetails.get(0).getExaminationDate();
            Common.Export_AgeOFLog = Common.Export.AllExportDetails.get(0).getStockAge();
            Common.Export_Footer_1 = (int) Math.round(Common.Export.AllExportDetails.get(0).getFoot1_cm());
            Common.Export_Footer_2 = (int) Math.round(Common.Export.AllExportDetails.get(0).getFoot2_cm());
            Common.Export_Top_1 = (int) Math.round(Common.Export.AllExportDetails.get(0).getTop1_cm());
            Common.Export_Top_2 = (int) Math.round(Common.Export.AllExportDetails.get(0).getTop2_cm());
            Common.Export_Diameter = String.valueOf(Common.Export.AllExportDetails.get(0).getDiameter());

            Common.Export_Length = String.valueOf(Common.Export.AllExportDetails.get(0).getLength_dm());
            Common.Export_Volume = String.valueOf(Common.Export.AllExportDetails.get(0).getVolume());
            Common.WoodSpieceID = String.valueOf(Common.Export.AllExportDetails.get(0).getWoodSpeciesId());

            Diameter_check.setText("DR-" + Common.Export_Diameter);
            TreeNo_check.setText("");
            SBBL_CheckTxT.setText(Common.Export_Sbblabel);
            WSC_CheckTxT.setText(Common.Export_WSC);
            AgeOfLog_CheckTxT.setText(Common.Export_AgeOFLog);
            NoteF_CheckTxT.setText(Common.Export_NoteF);
            NoteT_CheckTxT.setText(Common.Export_NoteT);
            NoteL_CheckTxT.setText(Common.Export_NoteL);
            Footer1_CheckTxT.setText(String.valueOf(Common.Export_Footer_1));
            Footer2_CheckTxT.setText(String.valueOf(Common.Export_Footer_2));
            Top1_CheckTxT.setText(String.valueOf(Common.Export_Top_1));
            Top2_CheckTxT.setText(String.valueOf(Common.Export_Top_2));
            Lenght_CheckTxT.setText(Common.Export_Length);
            Volume_CheckTxT.setText(Common.Export_Volume);

            //Check Diameter Range
            ArrayList<LogSummaryModel> LogSummaryDiameterList = new ArrayList<LogSummaryModel>();
            for (LogSummaryModel QuoaModel : Common.Export.LogSummaryList) {
                if (QuoaModel.getWoodSpeciesCode().equals(Common.Export_WSC)) {
                    LogSummaryDiameterList.add(QuoaModel);
                }
            }
            //As per martin ask to remove the  conditon
            for (int i = 0; i < LogSummaryDiameterList.size(); i++) {
                int FirstValue = 0, SecondValue = 0, DiameterValue = 0;
                FirstValue = LogSummaryDiameterList.get(i).getDiameterMin();
                SecondValue = LogSummaryDiameterList.get(i).getDiameterMax();

                DiameterValue = Integer.parseInt(Common.Export_Diameter);
                if (FirstValue <= DiameterValue && SecondValue >= DiameterValue) {
                    WhiteLayout();
                    //Get QuotationID
                    QuantityCBMVolume = LogSummaryDiameterList.get(i).getQuantityCBM();
                    ScannedCBMVolume = LogSummaryDiameterList.get(i).getScannedCBM();
                    isWhiteRedlayout = true;
                    break;
                }
            }
            if (isWhiteRedlayout == false) {
                RedLayout();
            }
            //Check WoodSpeiceCode(Each Quotations from list) Volume
            if (ScannedCBMVolume >= QuantityCBMVolume) {
                WSCLimitMessage("WSCode Total CBM", "This WSC-" + Common.Export_WSC + " and Diameter- " + Common.Export_Diameter + " reached the volume of" + QuantityCBMVolume + ".", true);
            }
            SBBLabel_Layout.setVisibility(View.VISIBLE);
            if (Common.Export.AllExportDetails.get(0).getIsValidLog().equals("Active")) {
                ValidBarcode_TXT.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
                ValidBarcode_TXT.setText("Approved");
            } else {
                ValidBarcode_TXT.setBackgroundColor(getResources().getColor(R.color.red));
                ValidBarcode_TXT.setText("Not Approved");
            }
             Common.HideKeyboard(getActivity());
        } catch (Exception ex) {
            AlertDialogBox("SbbLabel Validation", "#" + Common.BarCode + "--" + ex.toString(), false);
        }
    }

    public void ClearSbbLayout() {
        SBBL_CheckTxT.setText("");
        WSC_CheckTxT.setText("");
        AgeOfLog_CheckTxT.setText("");
        NoteF_CheckTxT.setText("");
        NoteT_CheckTxT.setText("");
        NoteL_CheckTxT.setText("");
        Footer1_CheckTxT.setText("");
        Footer2_CheckTxT.setText("");
        Top1_CheckTxT.setText("");
        Top2_CheckTxT.setText("");
        Lenght_CheckTxT.setText("");
        Diameter_check.setText("");
        TreeNo_check.setText("");
        /*Clear Values*/
        Common.Export_Diameter = "";
        Common.Export_PvNo = "";
        Common.Export_PvDate = "";
        Common.WoodSpieceID = "";
        Common.OLDWoodSpieceCode = "";
        Common.WoodSpieceCode = "";
        Common.Export_AgeOFLog = "";
        Common.Export_Footer_1 = 0;
        Common.Export_Footer_2 = 0;
        Common.Export_Top_1 = 0;
        Common.Export_Top_2 = 0;
        Common.Length = "";
        Common.OLDVolume = "0.00";
        Common.Volume = "0.00";
        Common.Export_NoteF = "";
        Common.Export_NoteT = "";
        Common.Export_NoteL = "";
    }

    public void WhiteLayout() {
        Diameter_check.setBackgroundColor(getResources().getColor(R.color.color_white));
        if (Common.IsValidWSCode == 1) {
            Save_CheckTxT.setBackgroundColor(getResources().getColor(R.color.red));
        } else {
            Save_CheckTxT.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
        }
        return;
    }

    public void RedLayout() {
        Diameter_check.setBackgroundColor(getResources().getColor(R.color.red));
        Save_CheckTxT.setBackgroundColor(getResources().getColor(R.color.red));
    }

    private boolean checkConnection() {
        isInternetPresent = ConnectivityReceiver.isConnected();
        showSnack(isInternetPresent);
        return isInternetPresent;
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
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.snack_barList), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        } catch (Exception e) {

        }
    }

    public void ContainerLimitMessage(String Title, String ErrorMessage, boolean status) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(getActivity());
        Removebuilder.setTitle(Title);
        Removebuilder.setMessage(ErrorMessage);
        Removebuilder.setIcon((status) ? R.mipmap.success : R.mipmap.fail);
        Removebuilder.setCancelable(true);
        Removebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                (dialog, id) -> {
                    SBBLabel_Layout.setVisibility(View.GONE);
                    dialog.cancel();
                });
        Removebuilder.setNegativeButton("CONTINUE",
                (dialog, id) -> dialog.cancel());

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public void WSCLimitMessage(String Title, String ErrorMessage, boolean status) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(getActivity());
        Removebuilder.setTitle(Title);
        Removebuilder.setMessage(ErrorMessage);
        Removebuilder.setIcon((status) ? R.mipmap.success : R.mipmap.fail);
        Removebuilder.setCancelable(true);
        Removebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SBBLabel_Layout.setVisibility(View.GONE);
                        dialog.cancel();
                    }
                });
        Removebuilder.setNegativeButton("CONTINUE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public void SpliteContainerLimitMessage(String Title, String ErrorMessage, boolean status) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(getActivity());
        Removebuilder.setTitle(Title);
        Removebuilder.setMessage(ErrorMessage);
        Removebuilder.setIcon((status) ? R.mipmap.success : R.mipmap.fail);
        Removebuilder.setCancelable(true);
        Removebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SBBLabel_Layout.setVisibility(View.GONE);
                        dialog.cancel();
                    }
                });
        Removebuilder.setNegativeButton("CONTINUE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        SbblabelChildDetailsLAY.setVisibility(View.GONE);
                        //Check Diameter Range
                        ArrayList<LogSummaryModel> LogSummaryDiameterList = new ArrayList<LogSummaryModel>();
                        for (LogSummaryModel QuoaModel : Common.Export.LogSummaryList) {
                            if (QuoaModel.getWoodSpeciesCode().equals(Common.Export_WSC)) {
                                LogSummaryDiameterList.add(QuoaModel);
                            }
                        }
                        //As per martin ask to remove the  conditon
                        for (int i = 0; i < LogSummaryDiameterList.size(); i++) {
                            int FirstValue = 0, SecondValue = 0, DiameterValue = 0;
                            FirstValue = LogSummaryDiameterList.get(i).getDiameterMin();
                            SecondValue = LogSummaryDiameterList.get(i).getDiameterMax();

                            DiameterValue = Integer.parseInt(Common.Export_Diameter);
                            if (FirstValue <= DiameterValue && SecondValue >= DiameterValue) {
                                //Get QuotationID
                                spliteQuantityCBMVolume = LogSummaryDiameterList.get(i).getQuantityCBM();
                                spliteScannedCBMVolume = LogSummaryDiameterList.get(i).getScannedCBM();
                                break;
                            }
                        }
                        //Check WoodSpeiceCode(Each Quotations from list) Volume
                        if (spliteScannedCBMVolume >= spliteQuantityCBMVolume) {
                            SpliteWSCLimitMessage("WSCode Total CBM", "This WSC-" + Common.Export_WSC + " and Diameter- " + Common.Export_Diameter + " reached the volume of" + spliteQuantityCBMVolume + ".", true);
                            return;
                        }
                        SbblabelChildDetailsLAY.setVisibility(View.GONE);
                        Common.Export_Remarks = "";
                        Common.Export_UpdateFlag = 1;
                        Common.Export.IsSplite = 1;
                        InsertExportDetails();
                    }
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public void SpliteWSCLimitMessage(String Title, String ErrorMessage, boolean status) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(getActivity());
        Removebuilder.setTitle(Title);
        Removebuilder.setMessage(ErrorMessage);
        Removebuilder.setIcon((status) ? R.mipmap.success : R.mipmap.fail);
        Removebuilder.setCancelable(true);
        Removebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                (dialog, id) -> {
                    SBBLabel_Layout.setVisibility(View.GONE);
                    dialog.cancel();
                });
        Removebuilder.setNegativeButton("CONTINUE",
                (dialog, id) -> {
                    dialog.cancel();
                    Common.Export_Remarks = "";
                    Common.Export_UpdateFlag = 1;
                    Common.Export.IsSplite = 1;
                    InsertExportDetails();
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public void UpdateContainerLimitMessage(String Title, String ErrorMessage, boolean status) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(getActivity());
        Removebuilder.setTitle(Title);
        Removebuilder.setMessage(ErrorMessage);
        Removebuilder.setIcon((status) ? R.mipmap.success : R.mipmap.fail);
        Removebuilder.setCancelable(true);
        Removebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                (dialog, id) -> dialog.cancel());
        Removebuilder.setNegativeButton("CONTINUE",
                (dialog, id) -> {
                    InsertExportDetails();
                    Common.IsContainerSelectFlag = false;
                    containerSpinners.setSelection(LD_ContainerSpinner.getSelectedItemPosition());
                    Common.IsVolumeCalculationFlag = false;
                    dialog.cancel();
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    private void advanceSearchApi() {
        try {
//            ProgressBarLay.setVisibility(View.VISIBLE);
            if (AdvaneSearchValidation(treeNoEDT.getText().toString(), fellingIDEDT.getText().toString())) {
                TreeNO = treeNoEDT.getText().toString();
                FellingID = fellingIDEDT.getText().toString();
                int treenumber = Integer.parseInt(TreeNO);
                int fsecnum = Integer.parseInt(FellingID);
                outputModel = new InputAdvanceSearchOutputModel();
                outputModel.setFSnumber(fsecnum);
                outputModel.setTreeNumber(TreeNO);
                Log.e("GsonBuilder", ">>>>>>>" + new GsonBuilder().create().toJson(outputModel));
                InsertExport = ApiClient.getApiInterface();

                InsertExport.getExportAdvanceSearch(outputModel).enqueue(new Callback<InputAdvanceSearchOutputModel>() {
                    @Override
                    public void onResponse(Call<InputAdvanceSearchOutputModel> call, Response<InputAdvanceSearchOutputModel> response) {
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.isSuccessful()) {
                                ArrayList<AdvanceSearchOutputModel> advanceSearchOutputModels = response.body().getLogDetails();
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
                                //advanceSearchRLV.setVisibility(View.VISIBLE);
                            } else {
                                AlertDialogBox("AdvanceSearched", "#" + "" + "--" + "Failed AdvancedSearched", false);
                            }
                        } else {
                            Common.AuthorizationFlag = true;
                            AlertDialogBox(CommonMessage(R.string.ExportHead), response.message(), false);
                        }
                        ProgressBarLay.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<InputAdvanceSearchOutputModel> call, Throwable t) {
                        ProgressBarLay.setVisibility(View.GONE);
                        AlertDialogBox(CommonMessage(R.string.ExportHead), t.getMessage(), false);
                    }
                });
            }
        } catch (Exception e) {

        }
    }

    public class ExportDetailsAdapter extends RecyclerView.Adapter<ExportDetailsAdapter.GroceryViewHolder> {
        //private ArrayList<LogDetailsModel> ExportList;
        private ArrayList<LogDetailsModel> ExportListFiltered;
        Context context;

        public ExportDetailsAdapter(ArrayList<LogDetailsModel> exportList, Context context) {
            //this.ExportList = exportList;
            this.context = context;
            this.ExportListFiltered = exportList;
        }

        public void removeItem(int position) {
            //ExportList.remove(position);
            notifyItemRemoved(position);
        }

        public LogDetailsModel getItem(int position) {
            return ExportListFiltered.get(position);
        }

        @Override
        public ExportDetailsAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exportdetails_infliator, parent, false);
            ExportDetailsAdapter.GroceryViewHolder gvh = new ExportDetailsAdapter.GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(ExportDetailsAdapter.GroceryViewHolder holder, final int position) {
            try {
                holder.Barcode.setText(ExportListFiltered.get(position).getSBBLabel());  //barcode
                holder.WoodSpiceCode.setText(ExportListFiltered.get(position).getWoodSpeciesCode());//specie
                //holder.DiamterSize.setText(ExportListFiltered.get(position).getDiameter());//Diameter
                holder.DF1.setText(String.valueOf(ExportListFiltered.get(position).getDiameterF1()));
                holder.DF2.setText(String.valueOf(ExportListFiltered.get(position).getDiameterF2()));
                holder.DT1.setText(String.valueOf(ExportListFiltered.get(position).getDiameterT1()));
                holder.DT2.setText(String.valueOf(ExportListFiltered.get(position).getDiameterT2()));
                holder.DiamterSize.setText(String.valueOf(ExportListFiltered.get(position).getDiameterAverage()));
                holder.CBM.setText(String.valueOf(Common.decimalFormat.format(ExportListFiltered.get(position).getVolume())));//Volume
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
                        try {
                            RemoveMessage(CommonMessage(R.string.Remove_Message), ExportListFiltered.get(position).getExportId(), ExportListFiltered.get(position).getSBBLabel());
                        } catch (Exception ex) {

                        }
                    }
                });

                String ConNo = ContainerMap.get(ExportListFiltered.get(position).getContainerId());
                holder.ContainerTxT.setText(ConNo);

                /*Container Spinner*/
                ContainernoAdapter containerAdapter = new ContainernoAdapter(getActivity(), ContainerNo_List);
                holder.ContainerSpinner.setAdapter(containerAdapter);

                for (int i = 0; i < ContainerNo_List.size(); i++) {
                    if (ContainerNo_List.get(i).equals(ConNo)) {
                        //holder.ContainerTxT.setText(ExportListFiltered.get(position).getContainerId());
                        holder.ContainerSpinner.setSelection(i);
                    }
                }
                holder.ContainerSpinner.setOnTouchListener((v, event) -> {
                    Common.IsContainerSelectFlag = true;
                    return false;
                });

                holder.ContainerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        try {
                            if (Common.IsContainerSelectFlag == true) {
                                Common.IsContainerSelectFlag = false;
                                //Common.ContainerNo = ContainerNo_List.get(pos);
                                if (ContainerNo_List.get(pos).equals("ALL") || ContainerNo_List.get(pos).equals("Un Assigned")) {
                                    Common.ContainerID = 0;
                                } else {
                                    Common.ContainerID = ContainerID_List.get(pos);
                                }
                                Common.ExportID = ExportListFiltered.get(position).getExportId();
                                Common.Export_Sbblabel = ExportListFiltered.get(position).getSBBLabel();
                                Common.Export_Footer_1 = Math.round(ExportListFiltered.get(position).getDiameterF1());
                                Common.Export_Footer_2 = Math.round(ExportListFiltered.get(position).getDiameterF2());
                                Common.Export_Top_1 = Math.round(ExportListFiltered.get(position).getDiameterT1());
                                Common.Export_Top_2 = Math.round(ExportListFiltered.get(position).getDiameterT2());
                                Common.Export_Length = String.valueOf(ExportListFiltered.get(position).getLength());
                                Common.Export_Volume = String.valueOf(ExportListFiltered.get(position).getVolume());
                                Common.Export_Diameter = String.valueOf(ExportListFiltered.get(position).getDiameterAverage());
                                Common.WoodSpieceID = String.valueOf(ExportListFiltered.get(position).getSpecieId());
                                Common.Export_Barcode = String.valueOf(ExportListFiltered.get(position).getBarcode());
                                Common.Export_UpdateFlag = 0;
                                Common.Export.IsSplite = 0;
                                InsertExportDetails();
                                containerSpinners.setSelection(pos);
                            }
                        } catch (Exception ex) {
                            CrashAnalytics.CrashReport(ex);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                holder.EditBarcode.setOnClickListener(v -> {
                    try {
                        Common.IsVolumeCalculationFlag = false;
                        UpdateLogDetailsLAY.setVisibility(View.VISIBLE);
                        // first time show new meusurment layout
                        NewMeusermentLAY.setVisibility(View.VISIBLE);
                        RemarksLAY.setVisibility(View.GONE);
                        HeartMesurmentLAY.setVisibility(View.GONE);
                        NewMeusermentTxT.setBackgroundColor(getActivity().getResources().getColor(R.color.amber));
                        RemarksTxT.setBackgroundColor(getActivity().getResources().getColor(R.color.colorbtnsplash));
                        HeartMesurmantTXT.setBackgroundColor(getActivity().getResources().getColor(R.color.colorbtnsplash));
                 /*       //LD_F1.setText("0");f
                        //LD_F2.setText("0");
                        //LD_T1.setText("0");
                        //LD_T2.setText("0");
                        //.setText("");
                        //LD_Remarks.setHint("Remarks");*/
                        LD_SBBLabel.setText(String.valueOf(ExportListFiltered.get(position).getSBBLabel()));
                        LD_Diameter.setText(String.valueOf(ExportListFiltered.get(position).getDiameterAverage()));
                        LD_F1.setText(String.valueOf(ExportListFiltered.get(position).getDiameterF1()));
                        LD_F2.setText(String.valueOf(ExportListFiltered.get(position).getDiameterF2()));
                        LD_T1.setText(String.valueOf(ExportListFiltered.get(position).getDiameterT1()));
                        LD_T2.setText(String.valueOf(ExportListFiltered.get(position).getDiameterT2()));
                        LD_Length.setText(String.valueOf(ExportListFiltered.get(position).getLength()));
                        LD_Volume.setText(String.valueOf(Common.decimalFormat.format(ExportListFiltered.get(position).getVolume())));
                        LD_Remarks.setText(String.valueOf(ExportListFiltered.get(position).getRemarks()));
                        LengthCutFootETxT.setText(String.valueOf(ExportListFiltered.get(position).getLengthCutFoot()));
                        LengthCutTopETxT.setText(String.valueOf(ExportListFiltered.get(position).getLengthCutTop()));
                        HoleF1ETxT.setText(String.valueOf(ExportListFiltered.get(position).getHoleFoot1()));
                        HoleF2ETxT.setText(String.valueOf(ExportListFiltered.get(position).getHoleFoot2()));
                        HoleT1ETxT.setText(String.valueOf(ExportListFiltered.get(position).getHoleTop1()));
                        HoleT2ETxT.setText(String.valueOf(ExportListFiltered.get(position).getHoleTop2()));
                        CrackF1ETxT.setText(String.valueOf(ExportListFiltered.get(position).getCrackFoot1()));
                        CrackF2ETxT.setText(String.valueOf(ExportListFiltered.get(position).getCrackFoot2()));
                        CrackT1ETxT.setText(String.valueOf(ExportListFiltered.get(position).getCrackTop1()));
                        CrackT2ETxT.setText(String.valueOf(ExportListFiltered.get(position).getCrackTop2()));
                        SapDeductionsETxT.setText(String.valueOf(ExportListFiltered.get(position).getSapDeduction()));

                        HeartMesF1ETxT.setText(String.valueOf(ExportListFiltered.get(position).getHF1()));
                        HeartMesF2ETxT.setText(String.valueOf(ExportListFiltered.get(position).getHF2()));
                        HeartMesT1ETxT.setText(String.valueOf(ExportListFiltered.get(position).getHT1()));
                        HeartMestT2ETxT.setText(String.valueOf(ExportListFiltered.get(position).getHT2()));
                        HeartMesDiameter.setText(String.valueOf(ExportListFiltered.get(position).getHAvg()));
                        HeartMesVolume.setText(String.valueOf(ExportListFiltered.get(position).getHvolume()));

                        Common.Export_Sbblabel = String.valueOf(ExportListFiltered.get(position).getSBBLabel());
                        Common.WoodSpieceID = String.valueOf(ExportListFiltered.get(position).getSpecieId());
                        Common.Export_Barcode = String.valueOf(ExportListFiltered.get(position).getSBBLabel());

                        ContainernoAdapter containerAdapter1 = new ContainernoAdapter(getActivity(), ContainerNo_List);
                        LD_ContainerSpinner.setAdapter(containerAdapter1);
                        int pos = holder.ContainerSpinner.getSelectedItemPosition();
                        LD_ContainerSpinner.setSelection(pos);
                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                    }
                });
            } catch (Exception ex) {

            }
        }

        @Override
        public int getItemCount() {
            return ExportListFiltered.size();
        }

        public class GroceryViewHolder extends RecyclerView.ViewHolder {
            TextView Barcode, DF1, DF2, DT1, DT2, DiamterSize, CBM, WoodSpiceCode, ContainerTxT;
            LinearLayout Background, Remove, EditBarcode;
            Spinner ContainerSpinner;
            CheckBox SelectCheckBOX;

            public GroceryViewHolder(View view) {
                super(view);
                Barcode = view.findViewById(R.id.export_sbblabel);
                WoodSpiceCode = view.findViewById(R.id.export_wsc);
                DF1 = view.findViewById(R.id.export_DiaF1);
                DF2 = view.findViewById(R.id.export_DiaF2);
                DT1 = view.findViewById(R.id.export_DiaT1);
                DT2 = view.findViewById(R.id.export_DiaT2);
                Background = view.findViewById(R.id.exportlayoutBackground);
                Remove = view.findViewById(R.id.export_removeBarCode);
                DiamterSize = view.findViewById(R.id.export_Diameter);
                CBM = view.findViewById(R.id.export_TotCBM);
                ContainerSpinner = view.findViewById(R.id.export_containerSpinners);
                EditBarcode = view.findViewById(R.id.export_EditSbbLable);
                ContainerTxT = view.findViewById(R.id.export_containerText);
                SelectCheckBOX = view.findViewById(R.id.barcode_Checkbox);
            }
        }

    /*    @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    //ExportListFiltered.clear();
                    String charString = charSequence.toString();
                    if (charString.equals("ALL")) {
                        ExportListFiltered = ExportList;
                    } else {
                        ArrayList<LogDetailsModel> filteredList = new ArrayList<>();
                        for (LogDetailsModel row : ExportList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getContainerId() == Common.ContainerID) {
                                filteredList.add(row);
                            }
                        }
                        ExportListFiltered = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = ExportListFiltered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    try {
                        ExportListFilteredList.clear();
                        ExportListFiltered = (ArrayList<LogDetailsModel>) filterResults.values;
                        ExportListFilteredList.addAll(ExportListFiltered);

                        notifyDataSetChanged();
                        Double LogDetailsVolumeSum = 0.00;
                        *//*Remainng CBM*//*
                        TotalScannedCount.setText(String.valueOf(ExportListFiltered.size()));
                        LogDetailsVolumeSum = TotalVolume(ExportListFiltered);
                        VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(LogDetailsVolumeSum)));
                    } catch (Exception e) {
                        Log.e("publishResults: %s", e.toString());
                    }
                }
            };
        }*/
    }

    public class AdvanceSearchResultAdapter extends RecyclerView.Adapter<AdvanceSearchResultAdapter.GroceryViewHolder> {
        private List<AdvanceSearchOutputModel> AdvanceSearchResultList;
        Context context;

        public AdvanceSearchResultAdapter(List<AdvanceSearchOutputModel> ScannedResultList, Context context) {
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
            holder.SBBLabel.setText(AdvanceSearchResultList.get(position).getBarcode());  //barcode
            holder.WSpiceCode.setText(AdvanceSearchResultList.get(position).getWoodSpeciesCode());
            holder.Lenght.setText(AdvanceSearchResultList.get(position).getLength_dm());  //specie
            holder.Volume.setText(String.valueOf(Common.decimalFormat.format(AdvanceSearchResultList.get(position).getVolume())));  //specie
            holder.F1.setText(String.valueOf(AdvanceSearchResultList.get(position).getFoot1_cm()));
            holder.F2.setText(String.valueOf(AdvanceSearchResultList.get(position).getFoot2_cm()));
            holder.T1.setText(String.valueOf(AdvanceSearchResultList.get(position).getTop1_cm()));
            holder.T2.setText(String.valueOf(AdvanceSearchResultList.get(position).getTop2_cm()));
            holder.Diameter.setText(String.valueOf(AdvanceSearchResultList.get(position).getDiameter()));
            holder.Select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    advanceSearchLAY.setVisibility(View.GONE);
                    ScannedResult(AdvanceSearchResultList.get(position).getBarcode());
                }
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

    public class SbblabelChildDetailsAdapter extends RecyclerView.Adapter<SbblabelChildDetailsAdapter.SbbLabelChildViewHolder> {
        private List<ExportSbblabelOutputModel> exportChildSbbLabelList;
        Context context;

        public SbblabelChildDetailsAdapter(List<ExportSbblabelOutputModel> exportChildSbbLabelLists, Context context) {
            this.exportChildSbbLabelList = exportChildSbbLabelLists;
            this.context = context;
        }

        @Override
        public SbblabelChildDetailsAdapter.SbbLabelChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View sbblabelChildView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sbblabel_childdetails_infliator, parent, false);
            SbbLabelChildViewHolder gvh = new SbbLabelChildViewHolder(sbblabelChildView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(SbbLabelChildViewHolder holder, final int position) {
            ExportSbblabelOutputModel sbblabelOutputModel = exportChildSbbLabelList.get(position);

            holder.SBBLabel_TxT.setText(sbblabelOutputModel.getSBBLabel());
            holder.WSpiceCode_TxT.setText(String.valueOf(sbblabelOutputModel.getWoodSpeciesCode()));
            //holder.TreeNum_TxT.setText(sbblabelOutputModel.getTreeNumber());
            holder.AgeOfLog_TxT.setText(sbblabelOutputModel.getStockAge());
            holder.Diameter_TxT.setText(String.valueOf(sbblabelOutputModel.getDiameter()));
            int foot1 = (int) Math.round(sbblabelOutputModel.getFoot1_cm());
            int foot2 = (int) Math.round(sbblabelOutputModel.getFoot2_cm());
            holder.Footer1_TxT.setText(String.valueOf(foot1));
            holder.Footer2_TxT.setText(String.valueOf(foot2));
            int top1 = (int) Math.round(sbblabelOutputModel.getTop1_cm());
            int top2 = (int) Math.round(sbblabelOutputModel.getTop2_cm());
            holder.Top1_TxT.setText(String.valueOf(top1));
            holder.Top2_TxT.setText(String.valueOf(top2));
            holder.Lenght_TxT.setText(String.valueOf(sbblabelOutputModel.getLength_dm()));
            holder.Volume_TxT.setText(String.valueOf(sbblabelOutputModel.getVolume()));
            if (sbblabelOutputModel.getIsValidLog().equals("Active")) {
                holder.ValidBarcode_TXT.setText("Approved");
                holder.ValidBarcode_TXT.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary_light));
                holder.Save_TxT.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary_light));
            } else {
                holder.ValidBarcode_TXT.setText("Not Approved");
                holder.ValidBarcode_TXT.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                holder.Save_TxT.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
            }
            holder.Save_TxT.setOnClickListener(v -> {
                try {
                    Common.Export_WSC = sbblabelOutputModel.getWoodSpeciesCode();
                    Common.Export_Sbblabel = sbblabelOutputModel.getSBBLabel();
                    Common.Export_PvNo = sbblabelOutputModel.getExaminationNo();
                    Common.Export_PvDate = sbblabelOutputModel.getExaminationDate();
                    Common.Export_AgeOFLog = sbblabelOutputModel.getStockAge();
                    Common.Export_Footer_1 = (int) Math.round(sbblabelOutputModel.getFoot1_cm());
                    Common.Export_Footer_2 = (int) Math.round(sbblabelOutputModel.getFoot2_cm());
                    Common.Export_Top_1 = (int) Math.round(sbblabelOutputModel.getTop1_cm());
                    Common.Export_Top_2 = (int) Math.round(sbblabelOutputModel.getTop2_cm());
                    Common.Export_Diameter = String.valueOf(sbblabelOutputModel.getDiameter());
                    Common.Export_Length = String.valueOf(sbblabelOutputModel.getLength_dm());
                    Common.Export_Volume = String.valueOf(sbblabelOutputModel.getVolume());
                    Common.WoodSpieceID = String.valueOf(sbblabelOutputModel.getWoodSpeciesId());
                    if (Common.Export.LogSummaryInternalWSCList.contains(Common.Export_WSC)) {
                        WSC_CheckTxT.setBackgroundColor(getResources().getColor(R.color.color_white));
                        Save_CheckTxT.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
                        Common.IsValidWSCode = 0;
                    } else {
                        AlertDialogBox("LOGS", "cannot allow to add this WoodSpecieCode #" + Common.Export_WSC + ", Because It is not belongs to quotation specie List", false);
                        return;
                    }
                    spliteRemoveVolumeSum = SbbLableTotalVolume(ExportDetailsFilter);
                    spliteRemoveVolumeSum = spliteRemoveVolumeSum + sbblabelOutputModel.getVolume();
                    if (Common.ContainerID == -1 || Common.ContainerID == 0) {
                    } else {
                        if (spliteRemoveVolumeSum > Common.ExportSingleContainerTotValue) {
                            SpliteContainerLimitMessage("Container CBM Limit", "Container cannot allow to add more then " + Common.ExportSingleContainerTotValue + " Volume", true);
                            return;
                        }
                    }
                    //Check Diameter Range
                    ArrayList<LogSummaryModel> LogSummaryDiameterList = new ArrayList<LogSummaryModel>();
                    for (LogSummaryModel QuoaModel : Common.Export.LogSummaryList) {
                        if (QuoaModel.getWoodSpeciesCode().equals(Common.Export_WSC)) {
                            LogSummaryDiameterList.add(QuoaModel);
                        }
                    }
                    //As per martin ask to remove the  conditon
                    for (int i = 0; i < LogSummaryDiameterList.size(); i++) {
                        int FirstValue = 0, SecondValue = 0, DiameterValue = 0;
                        FirstValue = LogSummaryDiameterList.get(i).getDiameterMin();
                        SecondValue = LogSummaryDiameterList.get(i).getDiameterMax();

                        DiameterValue = Integer.parseInt(Common.Export_Diameter);
                        if (FirstValue <= DiameterValue && SecondValue >= DiameterValue) {
                            //Get QuotationID
                            spliteQuantityCBMVolume = LogSummaryDiameterList.get(i).getQuantityCBM();
                            spliteScannedCBMVolume = LogSummaryDiameterList.get(i).getScannedCBM();
                            break;
                        }
                    }
                    //Check WoodSpeiceCode(Each Quotations from list) Volume
                    if (spliteScannedCBMVolume >= spliteQuantityCBMVolume) {
                        SpliteWSCLimitMessage("WSCode Total CBM", "This WSC-" + Common.Export_WSC + " and Diameter- " + Common.Export_Diameter + " reached the volume of" + spliteQuantityCBMVolume + ".", true);
                        return;
                    }
                    SbblabelChildDetailsLAY.setVisibility(View.GONE);
                    Common.Export_Remarks = "";
                    Common.Export_UpdateFlag = 1;
                    Common.Export.IsSplite = 1;
                    InsertExportDetails();
                } catch (Exception ex) {
                }
            });
        }

        @Override
        public int getItemCount() {
            return exportChildSbbLabelList.size();
        }

        public class SbbLabelChildViewHolder extends RecyclerView.ViewHolder {
            TextView SBBLabel_TxT, WSpiceCode_TxT, AgeOfLog_TxT, Diameter_TxT, Footer1_TxT, Footer2_TxT, Top1_TxT, Top2_TxT,
                    Lenght_TxT, Volume_TxT, ValidBarcode_TXT;
            Button Save_TxT;

            public SbbLabelChildViewHolder(View view) {
                super(view);
                Diameter_TxT = view.findViewById(R.id.child_diameter_check);
                SBBLabel_TxT = view.findViewById(R.id.child_sbblabel_check);
                WSpiceCode_TxT = view.findViewById(R.id.child_wsc_check);
                AgeOfLog_TxT = view.findViewById(R.id.child_ageoflog_check);
                Footer1_TxT = view.findViewById(R.id.child_footer1_check);
                Footer2_TxT = view.findViewById(R.id.child_footer2_check);
                Top1_TxT = view.findViewById(R.id.child_top1_check);
                Top2_TxT = view.findViewById(R.id.child_top2_check);
                Lenght_TxT = view.findViewById(R.id.child_lenght_check);
                Volume_TxT = view.findViewById(R.id.child_volume_check);
                Save_TxT = view.findViewById(R.id.child_Save_check);
                ValidBarcode_TXT = view.findViewById(R.id.child_isValidLog_TxT);
            }
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

    public double DiameterCalculation(int UpdateSbbLabelDF1, int UpdateSbbLabelDF2, int UpdateSbbLabelDT1, int UpdateSbbLabelDT2) {
        double Diameter = 0.0, Total = 0.0;
        Total = UpdateSbbLabelDF1 + UpdateSbbLabelDF2 + UpdateSbbLabelDT1 + UpdateSbbLabelDT2;
        Diameter = Total / 4;
        return Diameter;
    }

    public static double isEditTextEmptyDouble(String editTxT) {
        double Value = 0.0;
        if (isNullOrEmpty(editTxT)) {
            Value = 0.0;
        } else {
            Value = Double.parseDouble(editTxT.trim());
        }
        return Value;
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
}
