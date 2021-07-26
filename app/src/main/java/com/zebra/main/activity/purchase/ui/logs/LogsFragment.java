package com.zebra.main.activity.purchase.ui.logs;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
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
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.tscdll.TSCActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zebra.R;
import com.zebra.adc.decoder.BarCodeReader;
import com.zebra.android.jb.Preference;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.activity.FellingRegistration.DimensionCalculation;
import com.zebra.main.activity.purchase.ui.agreement.AgreementFragment;
import com.zebra.main.adapter.ContainernoAdapter;
import com.zebra.main.adapter.FellingSectionAdapter;
import com.zebra.main.api.ApiClient;
import com.zebra.main.api.ApiInterface;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.fragments.LogDetailsFragment;
import com.zebra.main.interfac.BaseApplication;
import com.zebra.main.model.FellingRegistration.FellingTreeDetailsModel;
import com.zebra.main.model.externaldb.FellingSectionModel;
import com.zebra.main.model.externaldb.WoodSpeciesModel;
import com.zebra.main.sdl.SdlScanListener;
import com.zebra.main.sdl.SdlScanService;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.BlueTooth;
import com.zebra.utilities.BlutoothCommonClass;
import com.zebra.utilities.Common;
import com.zebra.utilities.ConnectivityReceiver;
import com.zebra.utilities.GwwException;
import com.zebra.utilities.PrintSlipsClass;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LogsFragment extends Fragment implements SdlScanListener, ConnectivityReceiver.ConnectivityReceiverListener {
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
    private FloatingActionButton image = null;
    LinearLayout ProgressBarLay;
    Context FragContext;
    ApiInterface InsertExport = null;
    BarCodeReceiver barcode_receiver;
    boolean isInternetPresent;
    EditText ScanValueETxT, TreeF1, TreeF2, TreeT1, TreeT2, TreeLenght, SbbLabelF1, SbbLabelF2, SbbLabelT1, SbbLabelT2,
            SbbLabelLenght, UpdateSbbLabelF1, UpdateSbbLabelF2, UpdateSbbLabelT1, UpdateSbbLabelT2, UpdateSbbLabelLenght, UpdateSbbLabelNoteF, UpdateSbbLabelNoteT,
            UpdateSbbLabelNoteL, ScannedLogsSbblabel_NoteF, ScannedLogsSbblabel_NoteT, ScannedLogsSbblabel_NoteL, UpdateTreeNumber, UpdateTreeNumberF1, UpdateTreeNumberF2,
            UpdateTreeNumberT1, UpdateTreeNumberT2, UpdateTreeNumberLenght, ScannedLogsSbblabeLHT1, ScannedLogsSbblabeLHT2, ScannedLogsSbblabeLHF1, ScannedLogsSbblabeLHF2,
            ScannedLogsSbblabeLHVolume, UpdateSbbLabelLHT1, UpdateSbbLabelLHT2, UpdateSbbLabelLHF1, UpdateSbbLabelLHF2, UpdateSbbLabelLHVolume, LD_F1, LD_F2, LD_T1, LD_T2, LD_Length, LD_Remarks, LengthCutFootETxT, LengthCutTopETxT, HoleF1ETxT, HoleF2ETxT, HoleT1ETxT, HoleT2ETxT, CrackF1ETxT, CrackF2ETxT,
            CrackT1ETxT, CrackT2ETxT, SapDeductionsETxT, HeartMesF1ETxT, HeartMesF2ETxT, HeartMesT1ETxT, HeartMestT2ETxT, NewMeusermentTxT, RemarksTxT,
            HeartMesurmantTXT, HeartMesDiameter, HeartMesVolume;
    Spinner FellingSecIdSpin, ScannedLogsQualitySpinner, LD_ContainerSpinner, LD_WoodSpieceCodeSpinner;
    TextView deviceLocationTXT, ScannedLogsdateTxT, TotalCountRegistration, TotalVolumeRegistration, NovalueFound,
            UpdateSbbLabelSave, UpdateTreeNumberSave, LD_SBBLabel, LD_Diameter, LD_Volume;
    ToggleButton scannerToggleBtn;
    LinearLayoutManager horizontalLayoutManager;
    LinearLayout SbbLableUpdateLayout, Layout_Note, HideDiamantionLayout, TreeUpdateLayout, Layout_LHValues,
            EditOptionFlagLay, UpdateLogDetailsLAY, NewMeusermentLAY, RemarksLAY, HeartMesurmentLAY;
    CheckBox Note_Checked;
    RecyclerView ScannedLogsList;
    FloatingActionButton ScannedLogsScanBTN;
    ImageView PrintButton, UpdateLayoutClose, AddTreeNumberImg, TreeUpdatelayoutClose;
    AlertDialog.Builder Removebuilder = null;
    AlertDialog RemoveAlert = null;
    private PurchaseLogsAdapter purchaseLogsAdapter;
    FellingSectionAdapter fellingSectionAdapter;
    AutoCompleteTextView fellingTreenoAutoTxT, FellingWSpecieAutoTxT, FellingPlotTxT, UpdateTreeNumberWSC, UpdatePlotNumber, TreePartAUTOTXT, UpdateTreePartType;
    ArrayAdapter<String> StringtreeNoadapter;
    ArrayAdapter<String> StringPlotNoadapter;
    ArrayAdapter<String> StringTreePartadapter;
    List<String> TreePartlist = new ArrayList<>();
    TSCActivity tsc = new TSCActivity();
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    PrintSlipsClass printSlip;

    private class BarCodeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    public LogsFragment() {
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
        try {
            //UpdateCountIDList();
            //scanService.release();
            if (scanService != null)
                scanService.setActivityUp(false);
            releaseWakeLock();
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(barcode_receiver);
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
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
            AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
        }
    }

    @Override
    public void onResume() {
        try {
            service = new Intent(getActivity(), SdlScanService.class);
            getActivity().bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(service);
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
        try {
            ScannedStatus("");//getResources().getString(R.string.app_name) + " v" + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (scanService != null)
            scanService.setActivityUp(true);
        acquireWakeLock();
        barcode_receiver = new BarCodeReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(barcode_receiver,
                new IntentFilter("LOGDETAILS_REFRESH"));
        BaseApplication.getInstance().setConnectivityListener(this);
        super.onResume();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScannedView = inflater.inflate(R.layout.fragment_logs, container, false);
        mDBExternalHelper = new ExternalDataBaseHelperClass(getActivity());
        mDBInternalHelper = new InternalDataBaseHelperClass(getActivity());
        InsertExport = ApiClient.getInstance().getUserService();
        printSlip = new PrintSlipsClass(getActivity());
        // Inflate the layout for this fragment
        Initialization();
        Click_Listener();
        ViewFellingList(Common.IsEditorViewFlag);
        //getfellingSectionSpinner();
        AgreementWoodSpeicesList();
        getQualitySpinner();
        GetTreePart();
        Common.FellingRegDate = Common.dateFormat.format(Calendar.getInstance().getTime());
        ScannedLogsdateTxT.setText(Common.FellingRegDate);
        /*Common.TreeNosList.clear();
        TreeNoList(Common.TreeNosList);*/
        if (!Common.IsFellingRegEditListFlag) {
            FellingRegEditValues();
        }
        GetPurchaseList();
        Note_Checked.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            if (checked) {
                Layout_Note.setVisibility(View.VISIBLE);
                Layout_LHValues.setVisibility(View.VISIBLE);
            } else {
                Layout_Note.setVisibility(View.GONE);
                Layout_LHValues.setVisibility(View.GONE);
                Common.HideKeyboard(getActivity());
            }
        });
        return ScannedView;
    }

    public void ViewFellingList(boolean EdtFlag) {
        if (!EdtFlag) {
            HideDiamantionLayout.setVisibility(View.VISIBLE);
            EditOptionFlagLay.setVisibility(View.VISIBLE);
            //FellingRegNoEDTxT.setEnabled(false);
            fellingTreenoAutoTxT.setEnabled(false);
            FellingWSpecieAutoTxT.setEnabled(false);
            ScanValueETxT.setEnabled(false);
            ScannedLogsQualitySpinner.setEnabled(false);
        } else {
            HideDiamantionLayout.setVisibility(View.GONE);
            EditOptionFlagLay.setVisibility(View.GONE);
            //FellingRegNoEDTxT.setEnabled(true);
            fellingTreenoAutoTxT.setEnabled(true);
            FellingWSpecieAutoTxT.setEnabled(true);
            ScanValueETxT.setEnabled(true);
            ScannedLogsQualitySpinner.setEnabled(true);
        }
    }

    private void getfellingSectionSpinner() {
        try {
            Common.FellingSectionList.clear();
            Common.FellingSectionList = mDBExternalHelper.getFellingSectionID(Common.FromLocationID);
            fellingSectionAdapter = new FellingSectionAdapter(getActivity(), Common.FellingSectionList);
            FellingSecIdSpin.setAdapter(fellingSectionAdapter);
            Common.WoodSpeicesDeatilsList.clear();
            Common.WoodSpeicesDeatilsList = mDBExternalHelper.getWoodSpicesTabel();
            WoodSpeicesList(Common.WoodSpeicesDeatilsList);
        } catch (Exception ex) {
        }
    }

    public void TreeNoList(String fellingSecId) {
        try {
            Common.FellingRegTreeFilterList = mDBExternalHelper.getFellingRegisterFilter(Common.FromLocationID, fellingSecId);
            ArrayList<String> AddedNewTreeNumbers = mDBInternalHelper.getTreeNumberFromInternal(fellingSecId);
            Common.FellingRegTreeNoStringList = new String[Common.FellingRegTreeFilterList.size() + AddedNewTreeNumbers.size()];
            for (int i = 0; i < Common.FellingRegTreeFilterList.size(); i++) {
                Common.FellingRegTreeNoStringList[i] = String.valueOf(Common.FellingRegTreeFilterList.get(i).getTreeNumber());
            }
            for (int i = 0; i < AddedNewTreeNumbers.size(); i++) {
                Common.FellingRegTreeNoStringList[Common.FellingRegTreeFilterList.size() + i] = AddedNewTreeNumbers.get(i);
            }
            StringtreeNoadapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, Common.FellingRegTreeNoStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            fellingTreenoAutoTxT.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void PlotNumberList(String fellingSecId) {
        try {
            Common.FellingRegPlotNumberFilterList = mDBExternalHelper.getFellingRegisterFilterWithDistinct(fellingSecId);
            Common.FellingRegPlotNoStringList = new String[Common.FellingRegPlotNumberFilterList.size()];
            for (int i = 0; i < Common.FellingRegPlotNumberFilterList.size(); i++) {
                Common.FellingRegPlotNoStringList[i] = String.valueOf(Common.FellingRegPlotNumberFilterList.get(i).getPlotNumber());
            }
            StringPlotNoadapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, Common.FellingRegPlotNoStringList);
            StringPlotNoadapter.notifyDataSetChanged();
            FellingPlotTxT.setAdapter(StringPlotNoadapter);
            UpdatePlotNumber.setAdapter(StringPlotNoadapter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void WoodSpeicesList(ArrayList<WoodSpeciesModel> WoodSpeicesSList) {
        try {
            if (WoodSpeicesSList.size() > 0) {
                ArrayList<String> WoodSpiceArrray = new ArrayList<>();
                for (WoodSpeciesModel WsModel : WoodSpeicesSList) {
                    WoodSpiceArrray.add(WsModel.getWoodSpeciesCode());
                }
                Common.FellingRegWoodSpeicesStringList = WoodSpiceArrray.toArray(new String[0]);
                StringtreeNoadapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, Common.FellingRegWoodSpeicesStringList);
                StringtreeNoadapter.notifyDataSetChanged();
                FellingWSpecieAutoTxT.setAdapter(StringtreeNoadapter);
                UpdateTreeNumberWSC.setAdapter(StringtreeNoadapter);
            }
        } catch (Exception ex) {

        }
    }

    public void AgreementWoodSpeicesList() {
        try {
            Common.ConcessionList.clear();
            Common.ConcessionList = mDBExternalHelper.getAllConcessionNames(Common.FromLocationID);
            if (Common.ConcessionList.size() > 0) {
                Common.FromLocationname = Common.ConcessionList.get(0).getConcessionName();
                deviceLocationTXT.setText(Common.FromLocationname);
            }
            if (AgreementFragment.purchaseAgreementWSCLists.size() > 0) {
                Common.FellingRegWoodSpeicesStringList = AgreementFragment.purchaseAgreementWSCLists.toArray(new String[0]);
                StringtreeNoadapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, Common.FellingRegWoodSpeicesStringList);
                StringtreeNoadapter.notifyDataSetChanged();
                FellingWSpecieAutoTxT.setAdapter(StringtreeNoadapter);
                UpdateTreeNumberWSC.setAdapter(StringtreeNoadapter);
            }
        } catch (Exception ex) {

        }
    }

    private void getQualitySpinner() {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Common.QulaityDefaultList);
            ScannedLogsQualitySpinner.setAdapter(adapter);
        } catch (Exception ex) {
        }
    }

    public void GetTreePart() {
        TreePartlist = Arrays.asList(Common.FellingRegTreePartStringList);
        StringTreePartadapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, Common.FellingRegTreePartStringList);
        StringTreePartadapter.notifyDataSetChanged();
        TreePartAUTOTXT.setAdapter(StringTreePartadapter);
        UpdateTreePartType.setAdapter(StringTreePartadapter);
    }

    public void FellingRegEditValues() {
        ScannedLogsdateTxT.setText(Common.FellingRegDate);
        /*Fellign SectionID*/
        for (FellingSectionModel item : Common.FellingSectionList) {
            if (item.getFellingSectionId().equals(Common.FellingSectionId)) {
                FellingSecIdSpin.setSelection(item.getID() - 1);
            }
        }
    }

    public void Initialization() {
        ScanValueETxT = ScannedView.findViewById(R.id.barcode_TxT);
        ScannedView.findViewById(R.id.scannedlogsBTN).setOnClickListener(Obj_clickListener);
        ScannedView.findViewById(R.id.scannedlogsEnterIMG).setOnClickListener(Obj_clickListener);
        ProgressBarLay = ScannedView.findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);

        deviceLocationTXT = ScannedView.findViewById(R.id.deviceLocationTxT);
        //FellingSecIdSpin = ScannedView.findViewById(R.id.scannedlogsSectionIdSpinner);
        FellingSecIdSpin.setEnabled(true);
        ScannedLogsdateTxT = ScannedView.findViewById(R.id.RegistrationdateTxT);
        ScannedLogsList = ScannedView.findViewById(R.id.LogsRecylcerView);
        TotalCountRegistration = ScannedView.findViewById(R.id.TotalCountRegistration);
        TotalVolumeRegistration = ScannedView.findViewById(R.id.TotalVolumesRegistration);
        ScannedLogsScanBTN = ScannedView.findViewById(R.id.fellingRegistrationScanBTN);

        NovalueFound = ScannedView.findViewById(R.id.NovalueFound);
        FellingWSpecieAutoTxT = ScannedView.findViewById(R.id.scannedlogsWSpecieTxT);
        //ScannedLogsQualitySpinner = ScannedView.findViewById(R.id.scannedlogsQualitySpinner);
        fellingTreenoAutoTxT = ScannedView.findViewById(R.id.scannedlogsTreenoAutoTxT);
        FellingPlotTxT = ScannedView.findViewById(R.id.scannedlogsPlotTxT);
        EditOptionFlagLay = ScannedView.findViewById(R.id.scannedlogs_editLayout);
        PrintButton = ScannedView.findViewById(R.id.fellingRegister_Print);
        ScannedView.findViewById(R.id.print_IMG).setOnClickListener(Obj_clickListener);
        TreeF1 = ScannedView.findViewById(R.id.Tree_F1);
        TreeF2 = ScannedView.findViewById(R.id.Tree_F2);
        TreeT1 = ScannedView.findViewById(R.id.Tree_T1);
        TreeT2 = ScannedView.findViewById(R.id.Tree_T2);
        TreeLenght = ScannedView.findViewById(R.id.Tree_lenght);
        SbbLabelF1 = ScannedView.findViewById(R.id.Sbblabel_F1);
        SbbLabelF2 = ScannedView.findViewById(R.id.Sbblabel_F2);
        SbbLabelT1 = ScannedView.findViewById(R.id.Sbblabel_T1);
        SbbLabelT2 = ScannedView.findViewById(R.id.Sbblabel_T2);
        SbbLabelLenght = ScannedView.findViewById(R.id.Sbblabel_lenght);
        Note_Checked = ScannedView.findViewById(R.id.logs_note_Checked);
        Layout_Note = ScannedView.findViewById(R.id.layout_Note);
        Layout_Note.setVisibility(View.GONE);
        Layout_LHValues = ScannedView.findViewById(R.id.layout_LH);
        Layout_LHValues.setVisibility(View.GONE);
        ScannedLogsSbblabel_NoteF = ScannedView.findViewById(R.id.scannedlogsSbblabel_NoteF);
        ScannedLogsSbblabel_NoteT = ScannedView.findViewById(R.id.scannedlogsSbblabel_NoteT);
        ScannedLogsSbblabel_NoteL = ScannedView.findViewById(R.id.scannedlogsSbblabel_NoteL);
        SbbLableUpdateLayout = ScannedView.findViewById(R.id.sbblabelUpdate);
        TreeUpdateLayout = ScannedView.findViewById(R.id.treeNumberUpdate);
        UpdateLayoutClose = ScannedView.findViewById(R.id.update_delete);
        TreeUpdatelayoutClose = ScannedView.findViewById(R.id.updateTreeNumber_delete);
        ScannedView.findViewById(R.id.update_delete).setOnClickListener(Obj_clickListener);
        ScannedView.findViewById(R.id.updateTreeNumber_delete).setOnClickListener(Obj_clickListener);
        ScannedView.findViewById(R.id.close_scannedlogsform).setOnClickListener(Obj_clickListener);
        ScannedView.findViewById(R.id.updatesbblable_Save).setOnClickListener(Obj_clickListener);
        ScannedView.findViewById(R.id.logs_syncIMG).setOnClickListener(Obj_clickListener);
        UpdateSbbLabelF1 = ScannedView.findViewById(R.id.updatesbblable_F1);
        UpdateSbbLabelF2 = ScannedView.findViewById(R.id.updatesbblable_F2);
        UpdateSbbLabelT1 = ScannedView.findViewById(R.id.updatesbblable_T1);
        UpdateSbbLabelT2 = ScannedView.findViewById(R.id.updatesbblable_T2);
        UpdateSbbLabelLenght = ScannedView.findViewById(R.id.updatesbblable_Length);
        UpdateSbbLabelNoteF = ScannedView.findViewById(R.id.updatesbblable_NoteF);
        UpdateSbbLabelNoteT = ScannedView.findViewById(R.id.updatesbblable_NoteT);
        UpdateSbbLabelNoteL = ScannedView.findViewById(R.id.updatesbblable_NoteL);
        UpdateSbbLabelSave = ScannedView.findViewById(R.id.updatesbblable_Save);
        //27-4-2020
        UpdateSbbLabelLHT1 = ScannedView.findViewById(R.id.updatesbblable_LHT1);
        UpdateSbbLabelLHT2 = ScannedView.findViewById(R.id.updatesbblable_LHT2);
        UpdateSbbLabelLHF1 = ScannedView.findViewById(R.id.updatesbblable_LHF1);
        UpdateSbbLabelLHF2 = ScannedView.findViewById(R.id.updatesbblable_LHF2);
        UpdateSbbLabelLHVolume = ScannedView.findViewById(R.id.updatesbblable_LHVolume);
        ScannedView.findViewById(R.id.updatesbblable_Save).setOnClickListener(Obj_clickListener);
        HideDiamantionLayout = ScannedView.findViewById(R.id.hideDiamantionLayout);

        AddTreeNumberImg = ScannedView.findViewById(R.id.fellingRegisterTreeNumberAdd);
        ScannedView.findViewById(R.id.scannedlogsTreeNumberAdd).setOnClickListener(Obj_clickListener);
        UpdateTreeNumber = ScannedView.findViewById(R.id.NewTreeNum);
        UpdateTreeNumberWSC = ScannedView.findViewById(R.id.NewWoodSpeiceCode);
        UpdatePlotNumber = ScannedView.findViewById(R.id.NewPlotNumber);
        UpdateTreeNumberF1 = ScannedView.findViewById(R.id.updateTreeNum_F1);
        UpdateTreeNumberF2 = ScannedView.findViewById(R.id.updateTreeNum_F2);
        UpdateTreeNumberT1 = ScannedView.findViewById(R.id.updateTreeNum_T1);
        UpdateTreeNumberT2 = ScannedView.findViewById(R.id.updateTreeNum_T2);
        UpdateTreeNumberLenght = ScannedView.findViewById(R.id.updateTreeNum_Length);
        UpdateTreeNumberSave = ScannedView.findViewById(R.id.updateTreeNum_Save);
        ScannedView.findViewById(R.id.updateTreeNum_Save).setOnClickListener(Obj_clickListener);
        TreePartAUTOTXT = ScannedView.findViewById(R.id.TreePartTxT);
        UpdateTreePartType = ScannedView.findViewById(R.id.updateTreePartTxT);
        ScannedLogsSbblabeLHT1 = ScannedView.findViewById(R.id.scannedlogsSbblabel_LHT1);
        ScannedLogsSbblabeLHT2 = ScannedView.findViewById(R.id.scannedlogsSbblabel_LHT2);
        ScannedLogsSbblabeLHF1 = ScannedView.findViewById(R.id.scannedlogsSbblabel_LHF1);
        ScannedLogsSbblabeLHF2 = ScannedView.findViewById(R.id.scannedlogsSbblabel_LHF2);
        ScannedLogsSbblabeLHVolume = ScannedView.findViewById(R.id.scannedlogsSbblabel_LHVolume);
        scannerToggleBtn = ScannedView.findViewById(R.id.scannerToggleButton);
        scannerToggleBtn.setChecked(false);

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

    public double DiameterCalculation(int UpdateSbbLabelDF1, int UpdateSbbLabelDF2, int UpdateSbbLabelDT1, int UpdateSbbLabelDT2) {
        double Diameter = 0.0, Total = 0.0;
        Total = UpdateSbbLabelDF1 + UpdateSbbLabelDF2 + UpdateSbbLabelDT1 + UpdateSbbLabelDT2;
        Diameter = Total / 4;
        return Diameter;
    }

    View.OnClickListener Obj_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                switch (v.getId()) {
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
                    case R.id.scannedlogsBTN:
                        ScanValueETxT.setText("");
                        Common.EntryMode = 1;
                        Common.ScanMode = true;
                        scanService.doDecode();
                        break;
                    case R.id.scannedlogsEnterIMG:
                        if (AgreementFragment.purchaseAgreementWSCLists.size() > 0) {
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
                    case R.id.update_delete:
                        Common.HideKeyboard(getActivity());
                        SbbLableUpdateLayout.setVisibility(View.GONE);
                        break;
                    case R.id.updateTreeNumber_delete:
                        Common.HideKeyboard(getActivity());
                        TreeUpdateLayout.setVisibility(View.GONE);
                        break;
                    case R.id.updatesbblable_Save:
                        if (UpdateSbbLabelValidation(
                                UpdateTreePartType.getText().toString(),
                                UpdateSbbLabelF1.getText().toString(),
                                UpdateSbbLabelF2.getText().toString(),
                                UpdateSbbLabelT1.getText().toString(),
                                UpdateSbbLabelT2.getText().toString(),
                                UpdateSbbLabelLenght.getText().toString())) {
                            String noteF = UpdateSbbLabelNoteF.getText().toString();
                            String noteT = UpdateSbbLabelNoteT.getText().toString();
                            String noteL = UpdateSbbLabelNoteL.getText().toString();

                            if (NoteValidation(noteF, noteT, noteL)) {
                                if (LogHeartValidation(
                                        UpdateSbbLabelLHT1.getText().toString(),
                                        UpdateSbbLabelLHT2.getText().toString(),
                                        UpdateSbbLabelLHF1.getText().toString(),
                                        UpdateSbbLabelLHF2.getText().toString(),
                                        UpdateSbbLabelLHVolume.getText().toString())) {
                                    try {
                                        /*Volume Calculation*/
                                        /*5.9 version*/
                                        Common.Volume = String.valueOf(DimensionCalculation.UpdateVolumeCalculation(
                                                UpdateSbbLabelF1.getText().toString(),
                                                UpdateSbbLabelF2.getText().toString(),
                                                UpdateSbbLabelT1.getText().toString(),
                                                UpdateSbbLabelT2.getText().toString(),
                                                UpdateSbbLabelLenght.getText().toString(),
                                                UpdateSbbLabelNoteF.getText().toString(),
                                                UpdateSbbLabelNoteT.getText().toString(),
                                                UpdateSbbLabelNoteL.getText().toString()));

                                        Common.SbbLabelLHVolume = String.valueOf(DimensionCalculation.UpdateVolumeCalculation(
                                                UpdateSbbLabelLHT1.getText().toString(),
                                                UpdateSbbLabelLHT2.getText().toString(),
                                                UpdateSbbLabelLHF1.getText().toString(),
                                                UpdateSbbLabelLHF2.getText().toString(),
                                                UpdateSbbLabelLenght.getText().toString(),
                                                "", "", ""));
                                        boolean UpdateTree = mDBInternalHelper.updatePurchaseLogsDetails(
                                                Common.Purchase.SelectedPurchaseId,
                                                Common.UpdateBarCode,
                                                LD_F1.getText().toString(),
                                                LD_F2.getText().toString(),
                                                LD_T1.getText().toString(),
                                                LD_T2.getText().toString(),
                                                LD_Length.getText().toString(),
                                                "",
                                                "",
                                                "",
                                                Common.Volume,
                                                HeartMesT1ETxT.getText().toString(),
                                                HeartMestT2ETxT.getText().toString(),
                                                HeartMesF1ETxT.getText().toString(),
                                                HeartMesF2ETxT.getText().toString(),
                                                Common.SbbLabelLHVolume,
                                                LD_Remarks.getText().toString(),
                                                "",
                                                LengthCutFootETxT.getText().toString(),
                                                LengthCutTopETxT.getText().toString(),
                                                CrackF1ETxT.getText().toString(),
                                                CrackF2ETxT.getText().toString(),
                                                CrackT1ETxT.getText().toString(),
                                                CrackT2ETxT.getText().toString(),
                                                "",
                                                HoleF1ETxT.getText().toString(),
                                                HoleF2ETxT.getText().toString(),
                                                HoleT1ETxT.getText().toString(),
                                                HoleT2ETxT.getText().toString(),
                                                "",
                                                SapDeductionsETxT.getText().toString()
                                        );
                                        if (UpdateTree == true) {
                                            GetPurchaseList();
                                            Common.HideKeyboard(getActivity());
                                            SbbLableUpdateLayout.setVisibility(View.GONE);
                                        }

                                    } catch (Exception ex) {

                                    }
                                }
                            }
                        }
                        break;
                    case R.id.logs_syncIMG:
                        if (Common.Purchase.PurchaseLogsDetails.size() > 0) {
                            if (checkConnection() == true) {
                                try {
                                    Common.Purchase.PurchaseLogsSyncDetails = mDBInternalHelper.getPurchaseLogsSyncDetails(Common.Purchase.SelectedPurchaseId);
                                    PurchaseLogsSyncInputModel purcahseSyncModel = new PurchaseLogsSyncInputModel();
                                    purcahseSyncModel.setHHPurchaselist(Common.Purchase.PurchaseLogsSyncDetails);
                                    InsertExport = ApiClient.getApiInterface();
                                    InsertExport.InsertPurchaseLogs(purcahseSyncModel).enqueue(new Callback<PurchaseLogsSyncInputModel>() {
                                        @Override
                                        public void onResponse(Call<PurchaseLogsSyncInputModel> call, Response<PurchaseLogsSyncInputModel> response) {
                                            ProgressBarLay.setVisibility(View.GONE);
                                            if (GwwException.GwwException(response.code()) == true) {
                                                if (response.isSuccessful()) {
                                                    Common.SyncStatusList.clear();
                                                    Common.SyncStatusList.addAll(response.body().getStatus());
                                                    if (Common.SyncStatusList.get(0).getStatus() == 1) {
                                                        Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                                                        Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                                                        boolean ListIdFlag = mDBInternalHelper.UpdatePurchaseAgreementStatus(1);
                                                        if (ListIdFlag == true) {
                                                            AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "#" + Common.Purchase.SelectedPurchaseId + "--" + Common.SyncStatusList.get(0).getMessage(), true);
                                                        }
                                                    } else {
                                                        AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "#" + Common.Purchase.SelectedPurchaseId + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                                                    }
                                                } else {
                                                    AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "#" + Common.Purchase.SelectedPurchaseId + "--" + "Not Synced", false);
                                                }
                                            } else {
                                                Common.AuthorizationFlag = true;
                                                AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), response.message(), false);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<PurchaseLogsSyncInputModel> call, Throwable t) {
                                            ProgressBarLay.setVisibility(View.GONE);
                                            AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), t.getMessage(), false);
                                        }
                                    });
                                } catch (Exception ex) {

                                }
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Values are empty", false);
                        }
                        break;

                    case R.id.print_IMG:
                        if (Common.IsPrintBtnClickFlag == true) {
                            if (Common.Purchase.PurchaseLogsDetails.size() > 0) {
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
                        break;

                    case R.id.Save_check:
                        Common.Export_Remarks = "";
                        Common.Export_UpdateFlag = 1;
                        Common.Export.IsSplite = 0;
                        //IsInsertFag = false;
                        //InsertExportDetails();
                        break;
                    case R.id.Reject_check:
                        //SBBLabel_Layout.setVisibility(View.GONE);
                        break;
                    case R.id.closeActivity_check:
                        //SBBLabel_Layout.setVisibility(View.GONE);
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

    Handler FellingRegPrintHan = new Handler();
    Runnable FellingRegPrintRun = new Runnable() {
        @Override
        public void run() {
            try {
                //Common.PlotNo = mDBInternalHelper.GetPlotNoUsingFellingSecID(Common.FromLocationID, Common.FellingSectionId, Common.FellingRegUniqueID);
                //Common.FellingSectionNumber = mDBExternalHelper.GetFellingSectionNumber(Common.FromLocationID, Common.FellingSectionId);
                Common.Count = Common.Purchase.PurchaseLogsDetails.size();
                tsc.clearbuffer();
                tsc.sendcommand(printSlip.PurchaseLogsHeader());
                tsc.clearbuffer();
                tsc.sendcommand(printSlip.PurchaseLogsDimensions());
                tsc.clearbuffer();
                PrintoutEnd();
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

    public boolean onBackPressed() {
        return false;
    }

    private void GetPurchaseList() {
        try {
            Common.Purchase.PurchaseLogsDetails = mDBInternalHelper.getPurchaseLogsDetails(Common.Purchase.SelectedPurchaseId);
            if (Common.Purchase.PurchaseLogsDetails.size() > 0) {
                purchaseLogsAdapter = new PurchaseLogsAdapter(Common.Purchase.PurchaseLogsDetails, getActivity());
                horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                ScannedLogsList.setLayoutManager(horizontalLayoutManager);
                purchaseLogsAdapter.notifyDataSetChanged();
                ScannedLogsList.setAdapter(purchaseLogsAdapter);
                NovalueFound.setVisibility(View.GONE);
                ScannedLogsList.setVisibility(View.VISIBLE);
            } else {
                NovalueFound.setVisibility(View.VISIBLE);
                ScannedLogsList.setVisibility(View.GONE);
            }
            TotalCountRegistration.setText(String.valueOf(Common.Purchase.PurchaseLogsDetails.size()));
            /*Sum of Volume*/
            Common.VolumeSum = PurchaseLogsTotalVolume(Common.Purchase.PurchaseLogsDetails);
            TotalVolumeRegistration.setText(String.valueOf(Common.decimalFormat.format(Common.VolumeSum)));
        } catch (Exception ex) {
            Log.d("Exception : %s", ex.toString());
        }
    }

    public double PurchaseLogsTotalVolume(ArrayList<PurchaseLogsModels> TotalFRScannedList) {
        double TotVolume = 0.00;
        for (PurchaseLogsModels inventoTransScanModel : TotalFRScannedList) {
            TotVolume = TotVolume + inventoTransScanModel.getVolume();
        }
        return TotVolume;
    }

    public void GetTreeNumber(String EnterTreeNumber) {
        try {
            if (Common.FellingSectionId.equals("Select FellingSectionID")) {
                AlertDialogBox("Felling SectionId", "Please check FellingSectionId is empty!", false);
                fellingTreenoAutoTxT.setText("");
                Common.FsTreeNumber = "";
            } else {
                boolean TreeNumberCheck = mDBInternalHelper.getTreeNumberDuplicationCheck(Common.FromLocationID, Common.FellingSectionId, EnterTreeNumber);
                Common.FellingTreeDetailsCheckList.clear();
                Common.FellingTreeDetailsCheckList = mDBInternalHelper.getTreeNumberDuplication(Common.FromLocationID, Common.FellingSectionId, EnterTreeNumber);
                if (Common.FellingTreeDetailsCheckList.size() > 0) {
                    Common.FsWoodSpieceCode = Common.FellingTreeDetailsCheckList.get(0).getWoodSpieceCode();
                    //Common.OldWSCode = Common.FsWoodSpieceCode;
                    Common.PlotNo = Common.FellingTreeDetailsCheckList.get(0).getPlotNumber();
                    FellingWSpecieAutoTxT.setText(Common.FsWoodSpieceCode);
                    FellingPlotTxT.setText(Common.PlotNo);
                    TreeF1.setText(Common.FellingTreeDetailsCheckList.get(0).getFooter_1());
                    TreeF2.setText(Common.FellingTreeDetailsCheckList.get(0).getFooter_2());
                    TreeT1.setText(Common.FellingTreeDetailsCheckList.get(0).getTop_1());
                    TreeT2.setText(Common.FellingTreeDetailsCheckList.get(0).getTop_2());
                    TreeLenght.setText(Common.FellingTreeDetailsCheckList.get(0).getLength());
                    //if (TreeNumberCheck == true) {
                } else {
                    String[] WsCAndPlotNO = mDBExternalHelper.getFellingRegisterWoodSpiceCode(String.valueOf(Common.FromLocationID), EnterTreeNumber, Common.FellingSectionId);
                    if (WsCAndPlotNO.length > 0) {
                        Common.FsWoodSpieceCode = WsCAndPlotNO[0];
                        Common.PlotNo = WsCAndPlotNO[1];
                        Common.PlotId = Integer.parseInt(WsCAndPlotNO[2]);
                    }
                    FellingWSpecieAutoTxT.setText(Common.FsWoodSpieceCode);
                    FellingPlotTxT.setText(Common.PlotNo);
                    Common.OldWSCode = Common.FsWoodSpieceCode;
                    TreeF1.setText("");
                    TreeF2.setText("");
                    TreeT1.setText("");
                    TreeT2.setText("");
                    TreeLenght.setText("");
                    TreePartAUTOTXT.setText("");
                    if (Common.FsWoodSpieceCode.length() == 0) {
                        //AddTreeNumber();
                        Common.IsNewTreeNumber = 1;
                    } else {
                        Common.IsNewTreeNumber = 0;
                    }
                }
            }
        } catch (Exception ex) {

        }
    }

    public boolean NoteValidation(String noteF, String noteT, String noteL) {
        boolean NoteFlag = true;
        if (!noteF.isEmpty() || !noteT.isEmpty()) {
            if (noteL.isEmpty()) {
                AlertDialogBox("Update", "Please enter NOTE L value", false);
                NoteFlag = false;
            }
            if (!isNullOrEmpty(noteL)) {
                if (Double.valueOf(noteL) > Common.TreelenghtLimit) {
                    AlertDialogBox("Update", "Invalid Note Length - It should not more then " + Common.TreelenghtLimit, false);
                    NoteFlag = false;
                }
            }
        } else {
            if (!noteL.isEmpty()) {
                if (!isNullOrEmpty(noteL)) {
                    if (Double.valueOf(noteL) > Common.TreelenghtLimit) {
                        AlertDialogBox("Update", "Invalid Note Length - It should not more then " + Common.TreelenghtLimit, false);
                        NoteFlag = false;
                    }
                }
                if (noteF.isEmpty() || noteF.isEmpty()) {
                    AlertDialogBox("Update", "Please enter NOTE F or Note T value", false);
                    NoteFlag = false;
                }
            }
        }
        return NoteFlag;
    }

    public boolean UpdateSbbLabelValidation(String TreeType, String F1_Str, String F2_Str, String T1_Str, String T2_Str, String L_Str) {
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

    public boolean LogHeartValidation(String lht1, String lht2, String lhf1, String lhf2, String lhVolume) {
        boolean Validattion = true;
        if (lhf1.equals("") && lhf2.equals("") && lht1.equals("") && lht2.equals("")) {
            Validattion = true;
        } else {
            if (!lhf1.equals("") && !lhf2.equals("") && !lht1.equals("") && !lht2.equals("")) {
                Validattion = true;
            } else {
                Validattion = false;
                AlertDialogBox("Log Heart dimensions", "Please enter remaining dimensions!", false);
            }
        }
        return Validattion;
    }

    public boolean NewTreeNumberValidation(String WSC, String PlotNO, String LenghtLimit) {
        boolean Validattion = true;
        if (WSC.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter WSC!", false);
        }
        if (PlotNO.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter PlotNO!", false);
        }
        if (!isNullOrEmpty(LenghtLimit)) {
            if (Double.valueOf(LenghtLimit) > Common.TreelenghtLimit) {
                Validattion = false;
                AlertDialogBox("New Tree No", "Invalid Tree Length - It should not more then " + Common.TreelenghtLimit, false);
            }
        }
        return Validattion;
    }

    public void TreeNumberCheck(String treeNumber) {
        try {
            ArrayList<FellingTreeDetailsModel> SingleTreeDetailsList = new ArrayList<>();
            boolean TreeNumberCheck = mDBInternalHelper.getTreeNumberDuplicationCheck(Common.FromLocationID, Common.FellingSectionId, treeNumber);
            if (TreeNumberCheck == true) {
                SingleTreeDetailsList = mDBInternalHelper.getFellingRegWithTreeDetails(treeNumber);
                UpdateTreeNumberWSC.setText(SingleTreeDetailsList.get(0).getWoodSpieceCode());
                UpdatePlotNumber.setText(SingleTreeDetailsList.get(0).getPlotNumber());
                UpdateTreeNumberF1.setText(SingleTreeDetailsList.get(0).getFooter_1());
                UpdateTreeNumberF2.setText(SingleTreeDetailsList.get(0).getFooter_2());
                UpdateTreeNumberT1.setText(SingleTreeDetailsList.get(0).getTop_1());
                UpdateTreeNumberT2.setText(SingleTreeDetailsList.get(0).getTop_2());
                UpdateTreeNumberLenght.setText(SingleTreeDetailsList.get(0).getLength());
                UpdateTreeNumberSave.setText("Update");
                Toast.makeText(getActivity(), "Treenumber already inserted", Toast.LENGTH_SHORT).show();
            } else {
                UpdateTreeNumberWSC.setText("");
                UpdatePlotNumber.setText("");
                UpdateTreeNumberF1.setText("");
                UpdateTreeNumberF2.setText("");
                UpdateTreeNumberT1.setText("");
                UpdateTreeNumberT2.setText("");
                UpdateTreeNumberLenght.setText("");
                UpdateTreeNumberSave.setText("Save");
            }

        } catch (Exception ex) {

        }
    }

    public boolean UpdateTreeNumberValidation(String WSC, String PlotNO, String F1, String F2, String T1, String T2, String Length) {
        boolean Validattion = true;
        if (WSC.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter WSC!", false);
        }
        if (PlotNO.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter PlotNO!", false);
        }
        if (F1.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter F1!", false);
        }
        if (F2.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter F2!", false);
        }
        if (T1.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter T1!", false);
        }
        if (T2.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter T2!", false);
        }
        if (Length.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter Length!", false);
        }
        return Validattion;
    }

    public boolean DimensionsValidations(String F1_Str, String F2_Str, String T1_Str, String T2_Str, String L_Str) {
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
                Validattion = false;
                AlertDialogBox("Dimensions", "Invalid Tree Length - It should not more then " + Common.TreelenghtLimit, false);
            }
        }
        return Validattion;
    }

    public void WoodSpeicesID(String WoodSpeicesCode) {
        try {
            if (WoodSpeicesCode.length() == 0) {
                AlertDialogBox("Validation", "WoodSpeicesCode is empty!", false);
                FellingWSpecieAutoTxT.setText("");
                UpdateTreeNumberWSC.setText("");
            } else {
                if (AgreementFragment.purchaseAgreementWSCLists.size() > 0) {
                    Common.WoodSpeicesFilterDeatilsList.clear();
                    Common.WoodSpeicesFilterDeatilsList = mDBExternalHelper.getWoodSpiceID(WoodSpeicesCode);
                    if (Common.WoodSpeicesFilterDeatilsList.size() > 0) {
                        Common.FsWoodSpieceID = Common.WoodSpeicesFilterDeatilsList.get(0).getWoodSpeciesId();
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    public void GetPlotNo(String PlotNuber, String WoodSpiceCode) {
        try {
            if (WoodSpiceCode.length() == 0 || PlotNuber.length() == 0) {
                AlertDialogBox("Validation", "Please check WoodSpiceCode either PlotNumber is empty!", false);
                FellingPlotTxT.setText("");
                UpdatePlotNumber.setText("");
            } else {
                if (Common.FellingRegPlotNumberFilterList.size() > 0) {
                    Common.FellingRegPlotNumberFilterSinglrList.clear();
                    Common.FellingRegPlotNumberFilterSinglrList = mDBExternalHelper.getPlotIDCheck(PlotNuber);
                    if (Common.FellingRegPlotNumberFilterSinglrList.size() > 0) {
                        Common.PlotId = Common.FellingRegPlotNumberFilterSinglrList.get(0).getPlotId();
                        Common.IsNewPlotNumber = 0;
                    } else {
                        Common.IsNewPlotNumber = 1;
                    }
                }
            }
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


        fellingTreenoAutoTxT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Common.IsNewTreeNumberAdded = false;
                    Common.FsTreeNumber = s.toString();
                    GetTreeNumber(Common.FsTreeNumber);
                } else {
                    //AlertDialogBox("Tree No", "please enter Tree No", false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fellingTreenoAutoTxT.setOnTouchListener((v, event) -> {
            fellingTreenoAutoTxT.requestFocus();
            fellingTreenoAutoTxT.showDropDown();
            Common.HideKeyboard(getActivity());
            return false;
        });

        fellingTreenoAutoTxT.setOnItemClickListener((parent, view, position, id) -> {
            Common.IsNewTreeNumberAdded = true;
            Common.FsTreeNumber = (String) parent.getItemAtPosition(position);
            GetTreeNumber(Common.FsTreeNumber);
        });

        FellingWSpecieAutoTxT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Common.FsWoodSpieceCode = s.toString();
                    WoodSpeicesID(Common.FsWoodSpieceCode);
                } else {
                    // AlertDialogBox("WoodSpiceCode", "please enter WoodSpiceCode", false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        FellingWSpecieAutoTxT.setOnTouchListener((v, event) -> {
            FellingWSpecieAutoTxT.requestFocus();
            FellingWSpecieAutoTxT.showDropDown();
            Common.HideKeyboard(getActivity());
            return false;
        });

        FellingWSpecieAutoTxT.setOnItemClickListener((parent, view, position, id) -> {
            Common.FsWoodSpieceCode = (String) parent.getItemAtPosition(position);
            Common.OldWSCode = (String) parent.getItemAtPosition(position);
            WoodSpeicesID(Common.FsWoodSpieceCode);
        });

        UpdateTreeNumberWSC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Common.FsWoodSpieceCode = s.toString();
                    WoodSpeicesID(Common.FsWoodSpieceCode);
                } else {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        UpdateTreeNumberWSC.setOnTouchListener((v, event) -> {
            UpdateTreeNumberWSC.requestFocus();
            UpdateTreeNumberWSC.showDropDown();
            Common.HideKeyboard(getActivity());
            return false;
        });

        UpdateTreeNumberWSC.setOnItemClickListener((parent, view, position, id) -> {
            Common.FsWoodSpieceCode = (String) parent.getItemAtPosition(position);
            Common.OldWSCode = (String) parent.getItemAtPosition(position);
            WoodSpeicesID(Common.FsWoodSpieceCode
            );
        });

        UpdatePlotNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Common.PlotNo = s.toString();
                    GetPlotNo(Common.PlotNo, UpdateTreeNumberWSC.getText().toString());
                } else {
                    // AlertDialogBox("WoodSpiceCode", "please enter WoodSpiceCode", false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        UpdatePlotNumber.setOnTouchListener((v, event) -> {
            UpdatePlotNumber.requestFocus();
            UpdatePlotNumber.showDropDown();
            Common.HideKeyboard(getActivity());
            return false;
        });

        UpdatePlotNumber.setOnItemClickListener((parent, view, position, id) -> {
            Common.PlotNo = (String) parent.getItemAtPosition(position);
            Common.OldPlotNo = (String) parent.getItemAtPosition(position);
            GetPlotNo(Common.PlotNo, UpdateTreeNumberWSC.getText().toString());
        });

        ScannedLogsQualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Common.QualityName = Common.QulaityDefaultList.get(position);
                } catch (Exception ex) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FellingSecIdSpin.setOnTouchListener((v, event) -> {
            if (Common.Purchase.PurchaseLogsDetails.size() > 0) {
                AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "You can not select different felling section ID in same page", false);
                if (Common.IsEditorViewFlag == false) {
                    AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Can not edit Or delete after Synced", false);
                }
                FellingSecIdSpin.setEnabled(false);
                Common.HideKeyboard(getActivity());
            }
            return false;
        });

        FellingSecIdSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Common.FellingSectionId = String.valueOf(Common.FellingSectionList.get(position).getFellingSectionId());
                    //Common.FellingSectionName = Common.FellingSectionList.get(position).getFellingSectionId();
                    fellingTreenoAutoTxT.setText("");
                    FellingWSpecieAutoTxT.setText("");
                    TreeNoList(Common.FellingSectionId);
                    PlotNumberList(Common.FellingSectionId);
                } catch (Exception ex) {
                    Log.e("", "Exception-FellingSecIdSpin : %s" + ex.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        HideDiamantionLayout.setOnClickListener(v -> AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Can not edit Or delete after Synced", false));

        UpdateTreeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    TreeNumberCheck(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        TreePartAUTOTXT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Common.TreePart = s.toString();
                    if (TreePartlist.contains(Common.TreePart)) {
                    } else {
                        AlertDialogBox("Tree Part", "Please enter correct value", false);
                        TreePartAUTOTXT.setText("");
                        Common.TreePart = "";
                    }
                } else {
                    Common.TreePart = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        TreePartAUTOTXT.setOnTouchListener((v, event) -> {
            TreePartAUTOTXT.requestFocus();
            TreePartAUTOTXT.showDropDown();
            Common.HideKeyboard(getActivity());
            return false;
        });

        TreePartAUTOTXT.setOnItemClickListener((parent, view, position, id) -> Common.TreePart = (String) parent.getItemAtPosition(position));

        UpdateTreePartType.setOnTouchListener((v, event) -> {
            UpdateTreePartType.requestFocus();
            UpdateTreePartType.showDropDown();
            Common.HideKeyboard(getActivity());
            return false;
        });

        SbbLabelF1.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(getActivity());
            return false;
        });

        SbbLabelF2.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(getActivity());
            return false;
        });

        SbbLabelT1.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(getActivity());
            return false;
        });

        SbbLabelT2.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(getActivity());
            return false;
        });

        SbbLabelLenght.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(getActivity());
            return false;
        });

        ScannedLogsSbblabel_NoteF.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(getActivity());
            return false;
        });

        ScannedLogsSbblabel_NoteT.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(getActivity());
            return false;
        });

        ScannedLogsSbblabel_NoteL.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(getActivity());
            return false;
        });

        ScannedLogsSbblabeLHT1.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(getActivity());
            return false;
        });

        ScannedLogsSbblabeLHT2.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(getActivity());
            return false;
        });

        ScannedLogsSbblabeLHF1.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(getActivity());
            return false;
        });

        ScannedLogsSbblabeLHF2.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(getActivity());
            return false;
        });

        ScannedLogsSbblabeLHVolume.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(getActivity());
            return false;
        });
    }

    View.OnTouchListener GeneralOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
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
                }
            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
            }
            return false;
        }
    };

    private boolean isValidBarCode(String barCode) {
        String BarCodeValidation =
                "[a-zA-Z]{2}" +
                        "\\-" +
                        "[0-9]{7}";
        Pattern pattern = Pattern.compile(BarCodeValidation);
        Matcher matcher = pattern.matcher(barCode);
        return matcher.matches();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
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
            scanService.setOnScanListener(LogsFragment.this);
            scanService.setActivityUp(true);
        }
    };

    public void ScannedStatus(String s) {
        // tvStat.setText(s);
    }

    public void ScannedStatus(int id) {
        //tvStat.setText(id);
    }

    private void dspErr(String s) {
        //tvStat.setText("ERROR" + s);
    }

    public void ScannedResult(String s) {
        //  Common.HideKeyboard(getActivity());
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

                // Checked Duplicate In Internal Table
                boolean EmptyNullFlags = mDBInternalHelper.getPurchaseLogsduplicateCheckForPurchaseID(Common.Purchase.SelectedPurchaseId, Common.BarCode);
                if (EmptyNullFlags == true) {
                    wronBuzzer.start();
                    AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Already exsist please try some other logs", false);
                    //Scanned Result Refresh
                    GetPurchaseList();
                    return;
                }
                Common.SbbLabelDF1 = SbbLabelF1.getText().toString();
                Common.SbbLabelDF2 = SbbLabelF2.getText().toString();
                Common.SbbLabelDT1 = SbbLabelT1.getText().toString();
                Common.SbbLabelDT2 = SbbLabelT2.getText().toString();
                Common.SbbLabelLenght = SbbLabelLenght.getText().toString();

                Common.SbbLabelNoteF = ScannedLogsSbblabel_NoteF.getText().toString();
                Common.SbbLabelNoteT = ScannedLogsSbblabel_NoteT.getText().toString();
                Common.SbbLabelNoteL = ScannedLogsSbblabel_NoteL.getText().toString();

                Common.SbbLabelLHT1 = ScannedLogsSbblabeLHT1.getText().toString();
                Common.SbbLabelLHT2 = ScannedLogsSbblabeLHT2.getText().toString();
                Common.SbbLabelLHF1 = ScannedLogsSbblabeLHF1.getText().toString();
                Common.SbbLabelLHF2 = ScannedLogsSbblabeLHF2.getText().toString();
                Common.SbbLabelLHVolume = ScannedLogsSbblabeLHVolume.getText().toString();
                Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                /*Volume Calculation*/
                Common.Volume = String.valueOf(DimensionCalculation.VolumeCalculation(Common.SbbLabelDF1, Common.SbbLabelDF2, Common.SbbLabelDT1,
                        Common.SbbLabelDT2, Common.SbbLabelLenght,
                        Common.SbbLabelNoteF, Common.SbbLabelNoteT, Common.SbbLabelNoteL));
                // Common.IMEI = getImeiNumber();
                Common.IsActive = 1;
                if (checkConnection() == true) {
                    InsertPurchaseLogsDetails();
                }
                Common.ScannedEditTXTFlag = false;

            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void InsertPurchaseLogsDetails() {
        beepsound.start();
        boolean TreeNumberCheck = mDBInternalHelper.externalPurchadeBarcodeValidation(Common.BarCode);
        if (TreeNumberCheck == true) {
            AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Enter valid Barcode, please try diff barcode", false);
        } else {
            boolean TreeResult = mDBInternalHelper.insertPurchaseLogsDetails(Common.Purchase.SelectedPurchaseId, Common.Purchase.SelectedPurchaseNo, Common.UserID,
                    Common.FsTreeNumber, Common.FsWoodSpieceCode, Common.FsWoodSpieceID, Common.QualityName, Common.EntryMode, Common.BarCode, Common.SbbLabelDF1,
                    Common.SbbLabelDF2, Common.SbbLabelDT1, Common.SbbLabelDT2, Common.SbbLabelLenght, Common.SbbLabelNoteF, Common.SbbLabelNoteT, Common.SbbLabelNoteL,
                    Common.Volume, Common.TreePart, Common.IsActive, Common.SbbLabelLHT1, Common.SbbLabelLHT2, Common.SbbLabelLHF1, Common.SbbLabelLHF2, Common.SbbLabelLHVolume,
                    Common.DateTime, Common.Username);
        }
        //Scanned Result Refresh
        GetPurchaseList();
        SbbLabelF1.setText("");
        SbbLabelF2.setText("");
        SbbLabelT1.setText("");
        SbbLabelT2.setText("");
        SbbLabelLenght.setText("");
        ScannedLogsSbblabel_NoteF.setText("");
        ScannedLogsSbblabel_NoteL.setText("");
        ScannedLogsSbblabel_NoteT.setText("");
        //24 -april
        ScannedLogsSbblabeLHT1.setText("");
        ScannedLogsSbblabeLHT2.setText("");
        ScannedLogsSbblabeLHF1.setText("");
        ScannedLogsSbblabeLHF2.setText("");
        ScannedLogsSbblabeLHVolume.setText("");
        /*hide scan button*/
        //ScannedLogsistrationScanBTN.hide();
        TreePartAUTOTXT.setText("");
        FellingWSpecieAutoTxT.setText("");
        Common.TreePart = "";
        fellingTreenoAutoTxT.setText("");
        ScanValueETxT.setText("");
        FellingPlotTxT.setText("");
    }

    public static int booleanToInt(boolean value) {
        // Convert true to 1 and false to 0.
        return value ? 1 : 0;
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

    public void henResult(String codeType, String context) {
    }

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

    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
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

    public class PurchaseLogsAdapter extends RecyclerView.Adapter<PurchaseLogsAdapter.GroceryViewHolder> {
        private List<PurchaseLogsModels> PurchaseLogsList;
        Context context;

        public PurchaseLogsAdapter(List<PurchaseLogsModels> ScannedResultList, Context context) {
            this.PurchaseLogsList = ScannedResultList;
            this.context = context;
        }

        public void removeItem(int position) {
            PurchaseLogsList.remove(position);
            notifyItemRemoved(position);
        }

        public void restoreItem(PurchaseLogsModels item, int position) {
            PurchaseLogsList.add(position, item);
            notifyItemInserted(position);
        }

        public PurchaseLogsModels getItem(int position) {
            return PurchaseLogsList.get(position);
        }

        @Override
        public PurchaseLogsAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.felling_registration_infliator, parent, false);
            PurchaseLogsAdapter.GroceryViewHolder gvh = new PurchaseLogsAdapter.GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(PurchaseLogsAdapter.GroceryViewHolder holder, final int position) {
            holder.Barcode.setText(PurchaseLogsList.get(position).getBarCode());
            holder.WoodSpiceCode.setText(PurchaseLogsList.get(position).getWoodSpeciesCode());
            holder.QualitySpinner.setText(PurchaseLogsList.get(position).getQuality());
            holder.TreeNo.setText(PurchaseLogsList.get(position).getTreeNumber());
            holder.PlotNo.setText(PurchaseLogsList.get(position).getPlotNo());
            holder.DF1.setText(String.valueOf(PurchaseLogsList.get(position).getF1()));
            holder.DF2.setText(String.valueOf(PurchaseLogsList.get(position).getF2()));
            holder.DT1.setText(String.valueOf(PurchaseLogsList.get(position).getT1()));
            holder.DT2.setText(String.valueOf(PurchaseLogsList.get(position).getT2()));
            holder.Lenght.setText(String.valueOf(PurchaseLogsList.get(position).getLength_dm()));

            holder.NoteF.setText(String.valueOf(PurchaseLogsList.get(position).getNoteF()));
            holder.NoteT.setText(String.valueOf(PurchaseLogsList.get(position).getNoteT()));
            holder.NoteL.setText(String.valueOf(PurchaseLogsList.get(position).getNoteL()));
            holder.TreePart.setText(PurchaseLogsList.get(position).getTreePartType());
            holder.Volume.setText(String.valueOf(Common.decimalFormat.format(PurchaseLogsList.get(position).getVolume())));
            holder.HeartVolume.setText(String.valueOf(Common.decimalFormat.format(PurchaseLogsList.get(position).getHeartVolume())));

            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
            int color = generator.getColor(getItem(position));
            holder.Remove.setBackgroundColor(color);
            holder.Remove.setOnClickListener(v -> {
                if (Common.IsEditorViewFlag == false) {
                    AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Can not edit Or delete after Synced", false);
                } else {
                    Common.RemoveSBBLabel = "";
                    Common.RemoveSBBLabel = PurchaseLogsList.get(position).getBarCode();
                    RemoveMessage(CommonMessage(R.string.Remove_Message));
                }
            });
            holder.Updatelayout.setOnClickListener(v -> {
                try {
                    if (Common.IsEditorViewFlag == false) {
                        AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Can not edit Or delete after Synced", false);
                    } else {
                        /*  Common.UpdatedTreeNumber = PurchaseLogsList.get(position).getTreeNumber();
                        Common.UpdateBarCode = PurchaseLogsList.get(position).getBarCode();
                        //Common.UpdateFellRegUnique = PurchaseLogsList.get(position).getFellingRegistrationUniqueID();
                        SbbLableUpdateLayout.setVisibility(View.VISIBLE);
                      UpdateTreePartType.setText(PurchaseLogsList.get(position).getTreePartType());
                        UpdateSbbLabelF1.setText(String.valueOf(PurchaseLogsList.get(position).getF1()));
                        UpdateSbbLabelF2.setText(String.valueOf(PurchaseLogsList.get(position).getF2()));
                        UpdateSbbLabelT1.setText(String.valueOf(PurchaseLogsList.get(position).getT1()));
                        UpdateSbbLabelT2.setText(String.valueOf(PurchaseLogsList.get(position).getT2()));
                        UpdateSbbLabelLenght.setText(String.valueOf(PurchaseLogsList.get(position).getLength_dm()));
                        UpdateSbbLabelNoteF.setText(String.valueOf(PurchaseLogsList.get(position).getNoteF()));
                        UpdateSbbLabelNoteT.setText(String.valueOf(PurchaseLogsList.get(position).getNoteT()));
                        UpdateSbbLabelNoteL.setText(String.valueOf(PurchaseLogsList.get(position).getNoteL()));

                        UpdateSbbLabelLHT1.setText(String.valueOf(PurchaseLogsList.get(position).getLHT1()));
                        UpdateSbbLabelLHT2.setText(String.valueOf(PurchaseLogsList.get(position).getLHT2()));
                        UpdateSbbLabelLHF1.setText(String.valueOf(PurchaseLogsList.get(position).getLHF1()));
                        UpdateSbbLabelLHF2.setText(String.valueOf(PurchaseLogsList.get(position).getLHF2()));
                        UpdateSbbLabelLHVolume.setText(String.valueOf(PurchaseLogsList.get(position).getLHVolume()));*/

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
                        LD_SBBLabel.setText(String.valueOf(PurchaseLogsList.get(position).getSbbLabel()));
                        LD_F1.setText(String.valueOf(PurchaseLogsList.get(position).getF1()));
                        LD_F2.setText(String.valueOf(PurchaseLogsList.get(position).getF2()));
                        LD_T1.setText(String.valueOf(PurchaseLogsList.get(position).getT1()));
                        LD_T2.setText(String.valueOf(PurchaseLogsList.get(position).getT2()));
                        LD_Length.setText(String.valueOf(PurchaseLogsList.get(position).getLength_dm()));
                        LD_Volume.setText(String.valueOf(Common.decimalFormat.format(PurchaseLogsList.get(position).getVolume())));
                        LD_Remarks.setText(String.valueOf(PurchaseLogsList.get(position).getRemarks()));

                        LengthCutFootETxT.setText(String.valueOf(PurchaseLogsList.get(position).getLengthCutFoot()));
                        LengthCutTopETxT.setText(String.valueOf(PurchaseLogsList.get(position).getLengthCutTop()));
                        HoleF1ETxT.setText(String.valueOf(PurchaseLogsList.get(position).getHoleFoot1()));
                        HoleF2ETxT.setText(String.valueOf(PurchaseLogsList.get(position).getHoleFoot2()));
                        HoleT1ETxT.setText(String.valueOf(PurchaseLogsList.get(position).getHoleTop1()));
                        HoleT2ETxT.setText(String.valueOf(PurchaseLogsList.get(position).getHoleTop2()));
                        CrackF1ETxT.setText(String.valueOf(PurchaseLogsList.get(position).getCrackFoot1()));
                        CrackF2ETxT.setText(String.valueOf(PurchaseLogsList.get(position).getCrackFoot2()));
                        CrackT1ETxT.setText(String.valueOf(PurchaseLogsList.get(position).getCrackTop1()));
                        CrackT2ETxT.setText(String.valueOf(PurchaseLogsList.get(position).getCrackTop2()));
                        SapDeductionsETxT.setText(String.valueOf(PurchaseLogsList.get(position).getSapDeduction()));

                        HeartMesF1ETxT.setText(String.valueOf(PurchaseLogsList.get(position).getLHF1()));
                        HeartMesF2ETxT.setText(String.valueOf(PurchaseLogsList.get(position).getLHF2()));
                        HeartMesT1ETxT.setText(String.valueOf(PurchaseLogsList.get(position).getLHT1()));
                        HeartMestT2ETxT.setText(String.valueOf(PurchaseLogsList.get(position).getLHT2()));
                        HeartMesDiameter.setText(String.valueOf(PurchaseLogsList.get(position).getLHAvg()));
                        HeartMesVolume.setText(String.valueOf(PurchaseLogsList.get(position).getLHVolume()));

                        /*Common.Export_Sbblabel = String.valueOf(PurchaseLogsList.get(position).getSBBLabel());
                        Common.WoodSpieceID = String.valueOf(PurchaseLogsList.get(position).getSpecieId());
                        Common.Export_Barcode = String.valueOf(PurchaseLogsList.get(position).getSBBLabel());

                        ContainernoAdapter containerAdapter1 = new ContainernoAdapter(getActivity(), ContainerNo_List);
                        LD_ContainerSpinner.setAdapter(containerAdapter1);
                        int pos = holder.ContainerSpinner.getSelectedItemPosition();
                        LD_ContainerSpinner.setSelection(pos);*/
                    }
                } catch (Exception e) {
                }
            });
        }

        @Override
        public int getItemCount() {
            return PurchaseLogsList.size();
        }

        public class GroceryViewHolder extends RecyclerView.ViewHolder {
            TextView Barcode, WoodSpiceCode, QualitySpinner, TreeNo, PlotNo, DF1, DF2, DT1, DT2, Lenght, NoteF, NoteT, NoteL, TreePart, Volume, HeartVolume;
            //Spinner QualitySpinner,TreeNo;
            LinearLayout Background, Remove, Updatelayout;

            public GroceryViewHolder(View view) {
                super(view);
                Barcode = view.findViewById(R.id.fellingReg_SbblabelTxT);
                WoodSpiceCode = view.findViewById(R.id.fellingReg_WSpiceTxT);
                TreeNo = view.findViewById(R.id.fellingReg_TreeNoTxT);
                PlotNo = view.findViewById(R.id.fellingReg_PlotNoTxT);
                QualitySpinner = view.findViewById(R.id.fellingReg_QualityTxT);
                Background = view.findViewById(R.id.registrationlayoutBackground);
                Remove = view.findViewById(R.id.fellingReg_DeleteList);
                DF1 = view.findViewById(R.id.fellingReg_DF1TxT);
                DF2 = view.findViewById(R.id.fellingReg_DF2TxT);
                DT1 = view.findViewById(R.id.fellingReg_DT1TxT);
                DT2 = view.findViewById(R.id.fellingReg_DT2TxT);
                Lenght = view.findViewById(R.id.fellingReg_LenghtTxT);
                NoteF = view.findViewById(R.id.fellingReg_NoteFTxT);
                NoteT = view.findViewById(R.id.fellingReg_NoteTTxT);
                NoteL = view.findViewById(R.id.fellingReg_NoteLTxT);
                Updatelayout = view.findViewById(R.id.fellingReg_UpdateList);
                TreePart = view.findViewById(R.id.fellingReg_TreePartTxT);
                Volume = view.findViewById(R.id.fellingReg_VolumeTXT);
                HeartVolume = view.findViewById(R.id.fellingReg_HeartVolumeTXT);
            }
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
                    try {
                        boolean Isdelete = mDBInternalHelper.RemovePurchaseLogs(Common.RemoveSBBLabel, 0, Common.Purchase.SelectedPurchaseId);
                        if (Isdelete == true) {
                            Toast.makeText(getActivity(), "Successfully Removed from List", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {

                    }
                    dialog.cancel();
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }
}
