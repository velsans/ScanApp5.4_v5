package com.zebra.main.activity.purchase;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.jb.Preference;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.tscdll.TSCActivity;
import com.google.android.material.snackbar.Snackbar;
import com.zebra.R;
import com.zebra.adc.decoder.BarCodeReader;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.activity.Common.GwwMainActivity;
import com.zebra.main.activity.FellingRegistration.DimensionCalculation;
import com.zebra.main.activity.purchase.ui.agreement.AgreementAdapter;
import com.zebra.main.activity.purchase.ui.logs.LogsFragment;
import com.zebra.main.activity.purchase.ui.logs.PurchaseLogsModels;
import com.zebra.main.activity.purchase.ui.logs.PurchaseLogsSyncInputModel;
import com.zebra.main.activity.purchase.ui.received.PurchaseReceivedModel;
import com.zebra.main.activity.purchase.ui.transfer.PurchaseTransferModel;
import com.zebra.main.api.ApiClient;
import com.zebra.main.api.ApiInterface;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.model.Export.LogDetailsModel;
import com.zebra.main.model.FellingRegistration.FellingTreeDetailsModel;
import com.zebra.main.model.InvReceived.InventoryReceivedModel;
import com.zebra.main.model.InvReceived.InventoryReceivedSyncModel;
import com.zebra.main.model.InvTransfer.InventoryTransferScannedResultModel;
import com.zebra.main.model.InvTransfer.InventoryTransferSyncModel;
import com.zebra.main.model.externaldb.AgencyDetailsModel;
import com.zebra.main.model.externaldb.ConcessionNamesModel;
import com.zebra.main.model.externaldb.DriverDetailsModel;
import com.zebra.main.model.externaldb.FellingSectionModel;
import com.zebra.main.model.externaldb.LoadedModel;
import com.zebra.main.model.externaldb.LocationsModel;
import com.zebra.main.model.externaldb.PurchaseAgreementInputModel;
import com.zebra.main.model.externaldb.TransportModesModel;
import com.zebra.main.model.externaldb.TruckDetailsModel;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
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

public class ExternalPurchaseBottomBarActivity extends AppCompatActivity implements SdlScanListener {
    static final private boolean saveSnapshot = false;
    AlertDialogManager alert = new AlertDialogManager();
    Intent service;
    private ImageView image = null;
    // BarCodeReader specifics
    private BarCodeReader bcr = null;
    private PowerManager.WakeLock mWakeLock = null;
    private int motionEvents = 0, modechgEvents = 0, snapNum = 0;
    boolean bind = false;
    private SdlScanService scanService;
    MediaPlayer beepsound, wronBuzzer;
    private static final String TAG = "Purcahse";
    AlertDialog.Builder Removebuilder = null;
    AlertDialog RemoveAlert = null;
    String RemarksTypeList, RemarksTypeMessage, RemarksValues;

    //Module declaration
    LinearLayout progessbarLAY, agreementLAY, logsLAY, transferLAY, receivedLAY, SbbLableUpdateLayout, Layout_Note, HideDiamantionLayout, TreeUpdateLayout, Layout_LHValues,
            SL_EditOptionFlagLay, purchaseTransferListLAY, PTD_EditOptionFlagLay, PTD_EditOptionFlagLay2, PRL_snack_barListLAY, PRD_EditOptionFlagLay, PRD_EditOptionFlagLay2,
            PRD_receivedDetails_layout, UpdateLogDetailsLAY, NewMeusermentLAY, RemarksLAY, HeartMesurmentLAY;
    FrameLayout PTD_ListLAY, PRL_ListLAY;
    TextView purchaseNoTXT, agreementTXT, logsTXT, transferTXT, receivedTXT, deviceLocationTXT, SL_dateTxT, TotalCountRegistration, TotalVolumeRegistration, NovalueFound, UpdateSbbLabelSave,
            UpdateTreeNumberSave, PurchaseTransTotalFilteredCount, PurchaseTransTotalFilteredVolume, PTD_TotalScannedCount, PTD_VolumeTXT,
            PTD_FromLocationTxT, PRL_TotalFilteredCount, PRL_TotalFilteredVolume, PRD_ToLocationTxT, PRD_TotalScannedCount, PRD_VolumeTXT, PRD_Received_TotalCheckedCountTXT, PRD_scanQRDetails,
            PRD_TransferIDAutoTXT, PTD_NovalueFound, LD_SBBLabel, LD_Diameter, LD_Volume, HeartMesDiameter, HeartMesVolume, NewMeusermentTxT, RemarksTxT,
            HeartMesurmantTXT;
    ImageView closePurchase, printPurchase, syncPurchase, scanPurchase, PRD_scanQRBarCodeDetails;
    EditText LogsScanValueETxT, PTD_ScanValueETxT, PRD_ScanValueETxT, TreeF1, TreeF2, TreeT1, TreeT2, TreeLenght, SbbLabelF1, SbbLabelF2, SbbLabelT1, SbbLabelT2,
            SbbLabelLenght, UpdateSbbLabelF1, UpdateSbbLabelF2, UpdateSbbLabelT1, UpdateSbbLabelT2, UpdateSbbLabelLenght, UpdateSbbLabelNoteF, UpdateSbbLabelNoteT,
            UpdateSbbLabelNoteL, SL_Sbblabel_NoteF, SL_Sbblabel_NoteT, SL_Sbblabel_NoteL, UpdateTreeNumber, UpdateTreeNumberF1, UpdateTreeNumberF2,
            UpdateTreeNumberT1, UpdateTreeNumberT2, UpdateTreeNumberLenght, SL_SbblabeLHT1, SL_SbblabeLHT2, SL_SbblabeLHF1, SL_SbblabeLHF2,
            SL_SbblabeLHVolume, UpdateSbbLabelLHT1, UpdateSbbLabelLHT2, UpdateSbbLabelLHF1, UpdateSbbLabelLHF2, UpdateSbbLabelLHVolume, LD_F1, LD_F2, LD_T1, LD_T2, LD_Length, LD_Remarks, LengthCutFootETxT, LengthCutTopETxT, HoleF1ETxT, HoleF2ETxT, HoleT1ETxT, HoleT2ETxT, CrackF1ETxT, CrackF2ETxT,
            CrackT1ETxT, CrackT2ETxT, SapDeductionsETxT, HeartMesF1ETxT, HeartMesF2ETxT, HeartMesT1ETxT, HeartMestT2ETxT;
    CheckBox Note_Checked;
    AutoCompleteTextView SL_TreenoAutoTxT, SL_WSpecieAutoTxT, SL_PlotTxT, UpdateTreeNumberWSC, UpdatePlotNumber, SL_TreePartAUTOTXT, UpdateTreePartType,
            PTD_ToLocationATXT, PTD_DriverATXT, PTD_AgencyATXT, PTD_TruckDetialsATXT, PRD_FromLocationATXT, PRD_DriverATXT, PRD_AgencyATXT, PRD_TruckDetialsATXT;
    ImageView UpdateLayoutClose, AddTreeNumberImg, TreeUpdatelayoutClose, PRD_receivedDetails_close, PRD_receivedDetails_open;
    RecyclerView AgreementList, SL_List, PurchaseTransList, PurchaseTransDateList, PTD_ScannedResultLV, PRL_ReceivedList, PRL_ReceivedDateList, PRD_ScannedResultLV, PRD_receivedDetails_RV;
    Spinner SL_QualitySpinner, LD_ContainerSpinner, LD_WoodSpieceCodeSpinner;
    ListView PTD_TransferMode_LV, PTD_LoadedBy_LV, PRD_TransferMode_LV;

    private InternalDataBaseHelperClass mDBInternalHelper;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    ArrayAdapter<String> StringtreeNoadapter;
    ArrayAdapter<String> StringPlotNoadapter;
    ArrayAdapter<String> StringTreePartadapter;
    private PurchaseLogsAdapter purchaseLogsAdapter;
    List<String> TreePartlist = new ArrayList<>();
    AlertDialog.Builder LogsRemovebuilder = null;
    AlertDialog LogsRemoveAlert = null;
    ApiInterface InsertExport = null;
    LoadedAdapterInside loadedAdapter;
    DriverDetailsModel drivermodelArray;
    AgencyDetailsModel agencyModelArray;
    TruckDetailsModel truckModelArray;
    private PurchaseDetailsTransferAdapter ptd_adapter;

    ExternalPurchaseBottomBarViewModel mainViewModel;
    public static List<String> purchaseAgreementWSCLists = new ArrayList<>();
    AgreementAdapter agreementAdapter;
    TransferDateAdapter transferDateadapter;
    TransferListAdapter transListadapter;
    InventoryTransferSyncModel transferSyncModel;
    ReceivedDateAdapter receivedDateAdapter;
    ReceivedListAdapter receivedListAdapter;
    ReceivedLogsAdapter receivedLogsadapter;
    InventoryReceivedSyncModel receiedSyncModel;
    GwwMainActivity gwwMain;
    LinearLayoutManager horizontalLayoutManager, TransDateLayoutManager, ReceivedDateLayoutManager;
    TSCActivity tsc = new TSCActivity();
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    PrintSlipsClass printSlip;
    boolean isInternetPresent;
    AlertDialog alertDialog = null;

    @Override
    protected void onStart() {
        super.onStart();
        beepsound = MediaPlayer.create(this, R.raw.beep);
        wronBuzzer = MediaPlayer.create(this, R.raw.wrong_buzzer);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        try {
            Log.v(TAG, "SDLActivity onpause bind--->: " + bind);
            super.onPause();
            //scanService.release();
            if (scanService != null)
                scanService.setActivityUp(false);
            releaseWakeLock();
            Log.v(TAG, "<----SDLActivity onpause bind: " + bind);
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

    // Called when the activity is about to start interacting with the user.
    @Override
    protected void onResume() {
        Log.v(TAG, "SDLActivity onResume bind: " + bind);
        try {
            service = new Intent(this, SdlScanService.class);
            bindService(service, serviceConnection, BIND_AUTO_CREATE);
            startService(service);
        } catch (Exception e) {
            Log.e(TAG, "Exceptionss: " + e + " mBeepManagersdl: " + e.toString());
        }
        try {
            ScannedStatus(getResources().getString(R.string.app_name) + " v" + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (Resources.NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (scanService != null)
            scanService.setActivityUp(true);
        acquireWakeLock();
        super.onResume();
    }

    // Called with the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_purchase_tab);
        mainViewModel = ViewModelProviders.of(this).get(ExternalPurchaseBottomBarViewModel.class);
        printSlip = new PrintSlipsClass(ExternalPurchaseBottomBarActivity.this);
        mDBExternalHelper = new ExternalDataBaseHelperClass(ExternalPurchaseBottomBarActivity.this);
        mDBInternalHelper = new InternalDataBaseHelperClass(ExternalPurchaseBottomBarActivity.this);
        InsertExport = ApiClient.getInstance().getUserService();
        gwwMain = new GwwMainActivity(ExternalPurchaseBottomBarActivity.this);
        Initialization();
        ScannedClick_Listener();
        InitialView();
        AgreementViews();
        //SL_Views();
        //TransferList();
        Note_Checked.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            if (checked) {
                Layout_Note.setVisibility(View.VISIBLE);
                Layout_LHValues.setVisibility(View.VISIBLE);
            } else {
                Layout_Note.setVisibility(View.GONE);
                Layout_LHValues.setVisibility(View.GONE);
                Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            }
        });
        purchaseNoTXT.setText("PURCHASE NUMBER: " + Common.Purchase.SelectedPurchaseNo);
        Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_F9)) {
            LogsScanValueETxT.setText("");
            //Common.ScanMode = true;
            //scanService.doDecode();
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Signout("Are you sure you want to close?");
        }
        return true;
    }

    private void Initialization() {
        findViewById(R.id.purchaseScanBTN).setOnClickListener(purchaseCommon_ClickListener);
        progessbarLAY = findViewById(R.id.progressbar_layout);
        progessbarLAY.setVisibility(View.GONE);
        agreementLAY = findViewById(R.id.agrementLAY);
        logsLAY = findViewById(R.id.logsLAY);
        transferLAY = findViewById(R.id.transferLAY);
        receivedLAY = findViewById(R.id.receivedLAY);
        findViewById(R.id.agreementTXT).setOnClickListener(purchaseCommon_ClickListener);
        findViewById(R.id.logsTXT).setOnClickListener(purchaseCommon_ClickListener);
        findViewById(R.id.transferTXT).setOnClickListener(purchaseCommon_ClickListener);
        findViewById(R.id.receivedTXT).setOnClickListener(purchaseCommon_ClickListener);
        purchaseNoTXT = findViewById(R.id.purchaseNoTXT);
        agreementTXT = findViewById(R.id.agreementTXT);
        logsTXT = findViewById(R.id.logsTXT);
        transferTXT = findViewById(R.id.transferTXT);
        receivedTXT = findViewById(R.id.receivedTXT);
        findViewById(R.id.closePurchse).setOnClickListener(purchaseCommon_ClickListener);
        findViewById(R.id.syncPurchse).setOnClickListener(purchaseCommon_ClickListener);
        findViewById(R.id.PrintPurchase).setOnClickListener(purchaseCommon_ClickListener);
        AgreementList = findViewById(R.id.agreementRecylcerView);
        closePurchase = findViewById(R.id.closePurchse);
        printPurchase = findViewById(R.id.PrintPurchase);
        syncPurchase = findViewById(R.id.syncPurchse);
        scanPurchase = findViewById(R.id.purchaseScanBTN);
        //Scanned Logs
        LogsScanValueETxT = findViewById(R.id.barcode_TxT);
        findViewById(R.id.scannedlogsEnterIMG).setOnClickListener(scannedLogs_ClickListener);
        deviceLocationTXT = findViewById(R.id.deviceLocationTxT);
        SL_dateTxT = findViewById(R.id.RegistrationdateTxT);
        SL_List = findViewById(R.id.LogsRecylcerView);
        TotalCountRegistration = findViewById(R.id.TotalCountRegistration);
        TotalVolumeRegistration = findViewById(R.id.TotalVolumesRegistration);
        SL_QualitySpinner = findViewById(R.id.ScannedLogsQualitySpinner);

        NovalueFound = findViewById(R.id.NovalueFound);
        SL_WSpecieAutoTxT = findViewById(R.id.scannedlogsWSpecieTxT);
        SL_TreenoAutoTxT = findViewById(R.id.scannedlogsTreenoAutoTxT);
        SL_PlotTxT = findViewById(R.id.scannedlogsPlotTxT);
        SL_EditOptionFlagLay = findViewById(R.id.scannedlogs_editLayout);
        //PrintButton = findViewById(R.id.scannedlogs_Register_Print);
        TreeF1 = findViewById(R.id.Tree_F1);
        TreeF2 = findViewById(R.id.Tree_F2);
        TreeT1 = findViewById(R.id.Tree_T1);
        TreeT2 = findViewById(R.id.Tree_T2);
        TreeLenght = findViewById(R.id.Tree_lenght);
        SbbLabelF1 = findViewById(R.id.Sbblabel_F1);
        SbbLabelF2 = findViewById(R.id.Sbblabel_F2);
        SbbLabelT1 = findViewById(R.id.Sbblabel_T1);
        SbbLabelT2 = findViewById(R.id.Sbblabel_T2);
        SbbLabelLenght = findViewById(R.id.Sbblabel_lenght);
        Note_Checked = findViewById(R.id.logs_note_Checked);
        Layout_Note = findViewById(R.id.layout_Note);
        Layout_Note.setVisibility(View.GONE);
        Layout_LHValues = findViewById(R.id.layout_LH);
        Layout_LHValues.setVisibility(View.GONE);
        SL_Sbblabel_NoteF = findViewById(R.id.scannedlogsSbblabel_NoteF);
        SL_Sbblabel_NoteT = findViewById(R.id.scannedlogsSbblabel_NoteT);
        SL_Sbblabel_NoteL = findViewById(R.id.scannedlogsSbblabel_NoteL);
        SbbLableUpdateLayout = findViewById(R.id.sbblabelUpdate);
        TreeUpdateLayout = findViewById(R.id.treeNumberUpdate);
        UpdateLayoutClose = findViewById(R.id.update_delete);
        TreeUpdatelayoutClose = findViewById(R.id.updateTreeNumber_delete);
        findViewById(R.id.update_delete).setOnClickListener(scannedLogs_ClickListener);
        findViewById(R.id.updateTreeNumber_delete).setOnClickListener(scannedLogs_ClickListener);
        findViewById(R.id.close_scannedlogsform).setOnClickListener(scannedLogs_ClickListener);
        findViewById(R.id.updatesbblable_Save).setOnClickListener(scannedLogs_ClickListener);
        findViewById(R.id.logs_syncIMG).setOnClickListener(scannedLogs_ClickListener);
        UpdateSbbLabelF1 = findViewById(R.id.updatesbblable_F1);
        UpdateSbbLabelF2 = findViewById(R.id.updatesbblable_F2);
        UpdateSbbLabelT1 = findViewById(R.id.updatesbblable_T1);
        UpdateSbbLabelT2 = findViewById(R.id.updatesbblable_T2);
        UpdateSbbLabelLenght = findViewById(R.id.updatesbblable_Length);
        UpdateSbbLabelNoteF = findViewById(R.id.updatesbblable_NoteF);
        UpdateSbbLabelNoteT = findViewById(R.id.updatesbblable_NoteT);
        UpdateSbbLabelNoteL = findViewById(R.id.updatesbblable_NoteL);
        UpdateSbbLabelSave = findViewById(R.id.updatesbblable_Save);
        //27-4-2020
        UpdateSbbLabelLHT1 = findViewById(R.id.updatesbblable_LHT1);
        UpdateSbbLabelLHT2 = findViewById(R.id.updatesbblable_LHT2);
        UpdateSbbLabelLHF1 = findViewById(R.id.updatesbblable_LHF1);
        UpdateSbbLabelLHF2 = findViewById(R.id.updatesbblable_LHF2);
        UpdateSbbLabelLHVolume = findViewById(R.id.updatesbblable_LHVolume);
        findViewById(R.id.updatesbblable_Save).setOnClickListener(scannedLogs_ClickListener);
        HideDiamantionLayout = findViewById(R.id.hideDiamantionLayout);

        AddTreeNumberImg = findViewById(R.id.fellingRegisterTreeNumberAdd);
        findViewById(R.id.scannedlogsTreeNumberAdd).setOnClickListener(scannedLogs_ClickListener);
        UpdateTreeNumber = findViewById(R.id.NewTreeNum);
        UpdateTreeNumberWSC = findViewById(R.id.NewWoodSpeiceCode);
        UpdatePlotNumber = findViewById(R.id.NewPlotNumber);
        UpdateTreeNumberF1 = findViewById(R.id.updateTreeNum_F1);
        UpdateTreeNumberF2 = findViewById(R.id.updateTreeNum_F2);
        UpdateTreeNumberT1 = findViewById(R.id.updateTreeNum_T1);
        UpdateTreeNumberT2 = findViewById(R.id.updateTreeNum_T2);
        UpdateTreeNumberLenght = findViewById(R.id.updateTreeNum_Length);
        UpdateTreeNumberSave = findViewById(R.id.updateTreeNum_Save);
        findViewById(R.id.updateTreeNum_Save).setOnClickListener(scannedLogs_ClickListener);
        SL_TreePartAUTOTXT = findViewById(R.id.scannedlogsTreePartTxT);
        UpdateTreePartType = findViewById(R.id.updateTreePartTxT);
        UpdateTreePartType.setVisibility(View.GONE);
        SL_SbblabeLHT1 = findViewById(R.id.scannedlogsSbblabel_LHT1);
        SL_SbblabeLHT2 = findViewById(R.id.scannedlogsSbblabel_LHT2);
        SL_SbblabeLHF1 = findViewById(R.id.scannedlogsSbblabel_LHF1);
        SL_SbblabeLHF2 = findViewById(R.id.scannedlogsSbblabel_LHF2);
        SL_SbblabeLHVolume = findViewById(R.id.scannedlogsSbblabel_LHVolume);

        // TransferList
        purchaseTransferListLAY = findViewById(R.id.purcahse_snack_barList);
        PTD_ListLAY = findViewById(R.id.ptd_scannedDetailsLAY);
        PTD_ListLAY.setVisibility(View.GONE);
        PurchaseTransList = findViewById(R.id.purcahseTransList);
        PurchaseTransDateList = findViewById(R.id.purchaseTransListDate);
        //NoValueFoundTxT = findViewById(R.id.NovalueFound);
        PurchaseTransTotalFilteredCount = findViewById(R.id.purcahse_TotalFilteredCount);
        PurchaseTransTotalFilteredVolume = findViewById(R.id.purcahse_TotalFilteredVolume);
        findViewById(R.id.purcahse_createNewTransferBTN).setOnClickListener(purchaseTransfer_ClickListener);
        findViewById(R.id.ptd_transferDetailsClose).setOnClickListener(purchaseTransfer_ClickListener);

        // Transfer Details
        findViewById(R.id.ptd_transferEnterIMG).setOnClickListener(purchaseTransfer_ClickListener);
        PTD_ScanValueETxT = findViewById(R.id.ptd_scanValueEDTXT);
        PTD_TotalScannedCount = findViewById(R.id.ptd_TotalScannedCount);
        PTD_ScannedResultLV = findViewById(R.id.ptd_Transfer_lv);

        PTD_ToLocationATXT = findViewById(R.id.ptd_tolocation_ATxT);
        PTD_DriverATXT = findViewById(R.id.ptd_driver_ATxT);
        PTD_AgencyATXT = findViewById(R.id.ptd_transagency_ATxT);
        PTD_TruckDetialsATXT = findViewById(R.id.ptd_truckplateumber_ATxT);

        PTD_TransferMode_LV = findViewById(R.id.ptd_transferMode_LV);
        PTD_LoadedBy_LV = findViewById(R.id.ptd_loadedby_LV);
        PTD_FromLocationTxT = findViewById(R.id.ptd_fromLocationTXT);
        PTD_VolumeTXT = findViewById(R.id.ptd_TotalScannedVolumeTransfer);

        PTD_EditOptionFlagLay = findViewById(R.id.ptd_transfer_editLayout);
        PTD_EditOptionFlagLay2 = findViewById(R.id.ptd_transfer_editLayout2);

        // Received List
        findViewById(R.id.prl_receivedCreateScanListTxT).setOnClickListener(purchaseReceived_ClickListener);
        PRL_TotalFilteredCount = findViewById(R.id.prl_TotalFilteredCount);
        PRL_TotalFilteredVolume = findViewById(R.id.prl_TotalFilteredVolume);
        PRL_ReceivedList = findViewById(R.id.prl_ReceivedListView);
        PRL_ReceivedDateList = findViewById(R.id.prl_ReceivedListViewDate);

        PRL_snack_barListLAY = findViewById(R.id.prl_snack_barListLAY);
        PRL_ListLAY = findViewById(R.id.prd_scannedDetailsLAY);
        PRL_ListLAY.setVisibility(View.GONE);
        findViewById(R.id.prd_receivedDetailsClose).setOnClickListener(purchaseReceived_ClickListener);
        PTD_NovalueFound = findViewById(R.id.PTD_NovalueFound);

        // Received Deatails
        findViewById(R.id.prd_Received_EnterIMG).setOnClickListener(purchaseReceived_ClickListener);
        PRD_ScanValueETxT = findViewById(R.id.prd_Received_scanValueEDTXT);
        PRD_ScannedResultLV = findViewById(R.id.prd_Received_ListView);
        PRD_TransferMode_LV = findViewById(R.id.prd_ReceivedMode_LV);

        PRD_ToLocationTxT = findViewById(R.id.prd_toLocationTXT);
        PRD_TotalScannedCount = findViewById(R.id.prd_Received_TotalScannedCount);
        PRD_VolumeTXT = findViewById(R.id.prd_TotalVolumeReceived);
        PRD_Received_TotalCheckedCountTXT = findViewById(R.id.prd_Received_TotalCheckedCount);
        PRD_scanQRDetails = findViewById(R.id.prd_Received_scanQRDetails);
        PRD_scanQRBarCodeDetails = findViewById(R.id.prd_Received_scanQRBarCode);
        findViewById(R.id.prd_Received_scanQRDetails).setOnClickListener(purchaseReceived_ClickListener);
        findViewById(R.id.prd_Received_scanQRBarCode).setOnClickListener(purchaseReceived_ClickListener);
        PRD_TransferIDAutoTXT = findViewById(R.id.prd_transferIDAutoTXT);

        PRD_EditOptionFlagLay = findViewById(R.id.prd_received_editLayout);
        PRD_EditOptionFlagLay2 = findViewById(R.id.prd_transfer_editLayout2);

        /*  findViewById(R.id.advancedsearchTXT).setOnClickListener(as_OpenrListener);
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
        AS_NovalueFound.setVisibility(View.GONE);*/

        PRD_FromLocationATXT = findViewById(R.id.prd_fromlocation_ATxT);
        PRD_DriverATXT = findViewById(R.id.prd_driver_ATxT);
        PRD_AgencyATXT = findViewById(R.id.prd_transagency_ATxT);
        PRD_TruckDetialsATXT = findViewById(R.id.prd_truckplateumber_ATxT);

        //18-aug-2020
        PRD_receivedDetails_layout = findViewById(R.id.receivedDetails_layout);
        PRD_receivedDetails_layout.setVisibility(View.GONE);
        PRD_receivedDetails_RV = findViewById(R.id.receivedDetails_RV);
        PRD_receivedDetails_close = findViewById(R.id.receivedDetails_close);
        PRD_receivedDetails_open = findViewById(R.id.prd_receivedDetails_open);
        PRD_receivedDetails_open.setVisibility(View.VISIBLE);
        // update details layout
        UpdateLogDetailsLAY = findViewById(R.id.updateLogDetailsLayout);
        UpdateLogDetailsLAY.setVisibility(View.GONE);

        findViewById(R.id.updateLog_close).setOnClickListener(purchaseReceived_ClickListener);
        findViewById(R.id.Update_LogDetails).setOnClickListener(purchaseReceived_ClickListener);
        findViewById(R.id.cancel_LogDetails).setOnClickListener(purchaseReceived_ClickListener);

        LD_WoodSpieceCodeSpinner = findViewById(R.id.logUpdate_WSC);
        LD_SBBLabel = findViewById(R.id.logUpdate_sbblabel);
        LD_Diameter = findViewById(R.id.logUpdate_diameter);
        LD_F1 = findViewById(R.id.logUpdate_footer1);
        LD_F2 = findViewById(R.id.logUpdate_footer2);
        LD_T1 = findViewById(R.id.logUpdate_top1);
        LD_T2 = findViewById(R.id.logUpdate_top2);
        LD_Length = findViewById(R.id.logUpdate_lenght);
        LD_Volume = findViewById(R.id.logUpdate_Volume);
        LD_Remarks = findViewById(R.id.logUpdate_Remarks);
        LD_ContainerSpinner = findViewById(R.id.logUpdate_containerSpinners);
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


        LengthCutFootETxT = findViewById(R.id.LengthCutFootETXT);
        LengthCutTopETxT = findViewById(R.id.LengthCutTopETX);
        HoleF1ETxT = findViewById(R.id.HoleFoot1ETXT);
        HoleF2ETxT = findViewById(R.id.HoleFoot2ETXT);
        HoleT1ETxT = findViewById(R.id.HoleTop1ETXT);
        HoleT2ETxT = findViewById(R.id.HoleTop2ETXT);
        CrackF1ETxT = findViewById(R.id.F1CrackETXT);
        CrackF2ETxT = findViewById(R.id.F2CrackETXT);
        CrackT1ETxT = findViewById(R.id.T1CrackETXT);
        CrackT2ETxT = findViewById(R.id.T2CrackETXT);
        SapDeductionsETxT = findViewById(R.id.SapDeductionsETXT);

        NewMeusermentTxT = findViewById(R.id.NewMeusermentTxT);
        RemarksTxT = findViewById(R.id.RemarksTxT);
        HeartMesurmantTXT = findViewById(R.id.HeartMeusermentTxT);
        findViewById(R.id.NewMeusermentTxT).setOnClickListener(purchaseReceived_ClickListener);
        findViewById(R.id.RemarksTxT).setOnClickListener(purchaseReceived_ClickListener);
        findViewById(R.id.HeartMeusermentTxT).setOnClickListener(purchaseReceived_ClickListener);
        NewMeusermentLAY = findViewById(R.id.NewMeusermentLAY);
        RemarksLAY = findViewById(R.id.RemarksLAY);
        HeartMesurmentLAY = findViewById(R.id.HeartMesurmentLAY);

        HeartMesF1ETxT = findViewById(R.id.heartMes_footer1);
        HeartMesF2ETxT = findViewById(R.id.heartMes_footer2);
        HeartMesT1ETxT = findViewById(R.id.heartMes_top1);
        HeartMestT2ETxT = findViewById(R.id.heartMes_top2);
        HeartMesDiameter = findViewById(R.id.heartMes_diameter);
        HeartMesVolume = findViewById(R.id.heartMes_Volume);

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
                                                    Double.parseDouble(s),
                                                    Double.parseDouble(LDF2),
                                                    Double.parseDouble(LDT1),
                                                    Double.parseDouble(LDT2))));
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
                                            Double.parseDouble(LDF1),
                                            Double.parseDouble(s),
                                            Double.parseDouble(LDT1),
                                            Double.parseDouble(LDT2))));
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
                                            Double.parseDouble(LDF1),
                                            Double.parseDouble(LDF2),
                                            Double.parseDouble(s),
                                            Double.parseDouble(LDT2))));
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
                                            Double.parseDouble(LDF1),
                                            Double.parseDouble(LDF2),
                                            Double.parseDouble(LDT1),
                                            Double.parseDouble(s))));
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
                                            Double.parseDouble(LD_F1.getText().toString()),
                                            Double.parseDouble(LD_F2.getText().toString()),
                                            Double.parseDouble(LD_T1.getText().toString()),
                                            Double.parseDouble(LD_T2.getText().toString()))));
                                }
                            }
                        }
                        break;
                    case R.id.heartMes_footer1:
                        if (Common.IsVolumeCalculationFlag) {
                            if (s.length() > 0) {
                                if (!s.equals("0")) {
                                    if (LogHeartValidation(
                                            s,
                                            HeartMesF2ETxT.getText().toString(),
                                            HeartMesT1ETxT.getText().toString(),
                                            HeartMestT2ETxT.getText().toString())) {
                                        String diameters = String.valueOf(DiameterCalculation(
                                                Double.parseDouble(s),
                                                Double.parseDouble(HeartMesF2ETxT.getText().toString()),
                                                Double.parseDouble(HeartMesT1ETxT.getText().toString()),
                                                Double.parseDouble(HeartMestT2ETxT.getText().toString())));
                                        HeartMesDiameter.setText(diameters);
                                        String Volume = String.valueOf(Common.decimalFormat.format(
                                                DimensionCalculation.HeartVolumeCalculation(s,
                                                        HeartMesF2ETxT.getText().toString(),
                                                        HeartMesT1ETxT.getText().toString(),
                                                        HeartMestT2ETxT.getText().toString(),
                                                        Common.Volume,
                                                        LD_Length.getText().toString(),
                                                        LengthCutTopETxT.getText().toString(),
                                                        LengthCutFootETxT.getText().toString(),
                                                        CrackF1ETxT.getText().toString(),
                                                        CrackF2ETxT.getText().toString(),
                                                        CrackT1ETxT.getText().toString(),
                                                        CrackT2ETxT.getText().toString())));
                                        HeartMesVolume.setText(Volume);
                                    }
                                }
                            }
                        }
                        break;
                    case R.id.heartMes_footer2:
                        if (Common.IsVolumeCalculationFlag) {
                            if (s.length() > 0) {
                                if (!s.equals("0")) {
                                    if (LogHeartValidation(
                                            HeartMesF1ETxT.getText().toString(),
                                            s,
                                            HeartMesT1ETxT.getText().toString(),
                                            HeartMestT2ETxT.getText().toString())) {
                                        HeartMesDiameter.setText(String.valueOf(DiameterCalculation(
                                                Double.parseDouble(s),
                                                Double.parseDouble(HeartMesF1ETxT.getText().toString()),
                                                Double.parseDouble(HeartMesT1ETxT.getText().toString()),
                                                Double.parseDouble(HeartMestT2ETxT.getText().toString()))));
                                        String Volume = String.valueOf(Common.decimalFormat.format(
                                                DimensionCalculation.HeartVolumeCalculation(
                                                        HeartMesF1ETxT.getText().toString(),
                                                        s,
                                                        HeartMesT1ETxT.getText().toString(),
                                                        HeartMestT2ETxT.getText().toString(),
                                                        Common.Volume,
                                                        LD_Length.getText().toString(),
                                                        LengthCutTopETxT.getText().toString(),
                                                        LengthCutFootETxT.getText().toString(),
                                                        CrackF1ETxT.getText().toString(),
                                                        CrackF2ETxT.getText().toString(),
                                                        CrackT1ETxT.getText().toString(),
                                                        CrackT2ETxT.getText().toString())));
                                        HeartMesVolume.setText(Volume);
                                    }
                                }
                            }
                        }
                        break;
                    case R.id.heartMes_top1:
                        if (Common.IsVolumeCalculationFlag) {
                            if (s.length() > 0) {
                                if (!s.equals("0")) {
                                    if (LogHeartValidation(
                                            HeartMesF1ETxT.getText().toString(),
                                            HeartMesF2ETxT.getText().toString(),
                                            s,
                                            HeartMestT2ETxT.getText().toString())) {
                                        HeartMesDiameter.setText(String.valueOf(DiameterCalculation(
                                                Double.parseDouble(HeartMesF1ETxT.getText().toString()),
                                                Double.parseDouble(HeartMesF2ETxT.getText().toString()),
                                                Double.parseDouble(s),
                                                Double.parseDouble(HeartMestT2ETxT.getText().toString()))));
                                        String Volume = String.valueOf(Common.decimalFormat.format(
                                                DimensionCalculation.HeartVolumeCalculation(
                                                        HeartMesF1ETxT.getText().toString(),
                                                        HeartMesF2ETxT.getText().toString(),
                                                        s,
                                                        HeartMestT2ETxT.getText().toString(),
                                                        Common.Volume,
                                                        LD_Length.getText().toString(),
                                                        LengthCutTopETxT.getText().toString(),
                                                        LengthCutFootETxT.getText().toString(),
                                                        CrackF1ETxT.getText().toString(),
                                                        CrackF2ETxT.getText().toString(),
                                                        CrackT1ETxT.getText().toString(),
                                                        CrackT2ETxT.getText().toString())));
                                        HeartMesVolume.setText(Volume);
                                    }
                                }
                            }
                        }
                        break;
                    case R.id.heartMes_top2:
                        if (Common.IsVolumeCalculationFlag) {
                            if (s.length() > 0) {
                                if (!s.equals("0")) {
                                    if (LogHeartValidation(
                                            HeartMesF1ETxT.getText().toString(),
                                            HeartMesF2ETxT.getText().toString(),
                                            HeartMesT1ETxT.getText().toString(),
                                            s)) {
                                        HeartMesDiameter.setText(String.valueOf(DiameterCalculation(
                                                Double.parseDouble(HeartMesF1ETxT.getText().toString()),
                                                Double.parseDouble(HeartMesF2ETxT.getText().toString()),
                                                Double.parseDouble(HeartMesT1ETxT.getText().toString()),
                                                Double.parseDouble(s))));
                                        String Volume = String.valueOf(Common.decimalFormat.format(
                                                DimensionCalculation.HeartVolumeCalculation(
                                                        HeartMesF1ETxT.getText().toString(),
                                                        HeartMesF2ETxT.getText().toString(),
                                                        HeartMesT1ETxT.getText().toString(),
                                                        s,
                                                        Common.Volume,
                                                        LD_Length.getText().toString(),
                                                        LengthCutTopETxT.getText().toString(),
                                                        LengthCutFootETxT.getText().toString(),
                                                        CrackF1ETxT.getText().toString(),
                                                        CrackF2ETxT.getText().toString(),
                                                        CrackT1ETxT.getText().toString(),
                                                        CrackT2ETxT.getText().toString())));
                                        HeartMesVolume.setText(Volume);
                                    }
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
                    //ScanValueETxT.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    //ScanValueETxT.setFocusableInTouchMode(true);
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
    };

    public double DiameterCalculation(double UpdateSbbLabelDF1, double UpdateSbbLabelDF2,
                                      double UpdateSbbLabelDT1, double UpdateSbbLabelDT2) {
        double Diameter = 0.0, Total = 0.0;
        Total = UpdateSbbLabelDF1 + UpdateSbbLabelDF2 + UpdateSbbLabelDT1 + UpdateSbbLabelDT2;
        Diameter = Total / 4;
        return Diameter;
    }

    public void PrintSlip() {
        if (Common.IsPrintBtnClickFlag) {
            if (!BlutoothCommonClass.isBluetoothEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Common.devAddress = BlueTooth.getAddress("BTSPP");
                if (Common.devAddress != null) {
                    //progessbarLAY.setVisibility(View.VISIBLE);
                    Common.Printerstatus = tsc.printerstatus();
                    if (TSCConnInt == 1) {
                        Common.TSCstatus = tsc.status();
                    } else {
                        String BtConnResult = tsc.openport(Common.devAddress);
                        TSCConnInt = Integer.parseInt(BtConnResult);
                    }
                    Common.Printerstatus = tsc.printerstatus();
                    if (Common.Printerstatus.equals("00")) {
                        //progessbarLAY.setVisibility(View.GONE);
                        Common.IsPrintBtnClickFlag = false;
                        if (Common.PurchaseScannedTabs == 0) {
                            if (Common.Purchase.PurchaseLogsDetailsInternal.size() > 0) {
                                ScannedLogsPrintHan.post(ScannedLogsPrintRun);
                            } else {
                                AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), CommonMessage(R.string.TransferMessage), false);
                            }
                        }
                        if (Common.PurchaseScannedTabs == 1) {
                            TransferListPrintHan.post(TransferListPrintRun);
                        }
                        if (Common.PurchaseScannedTabs == 2) {
                            ReceivedListPrintHan.post(ReceivedListPrintRun);
                        }
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
    }

    View.OnClickListener purchaseCommon_ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                    case R.id.purchaseScanBTN:
                        if (Common.PurchaseScannedTabs == 0) {
                            /*if (Common.Purchase.PurchaseScannedIsEditorViewFlag == false) {
                                AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Can not edit Or delete after Synced", false);
                                return;
                            }*/
                            if (FelllingDetailsValidation(SL_WSpecieAutoTxT.getText().toString())) {
                                if (DimensionsValidations(
                                        SbbLabelF1.getText().toString(),
                                        SbbLabelF2.getText().toString(),
                                        SbbLabelT1.getText().toString(),
                                        SbbLabelT2.getText().toString(),
                                        SbbLabelLenght.getText().toString())) {
                                    boolean woodSpeiFlag = mDBExternalHelper.WoodSpeiceCodeCheck(SL_WSpecieAutoTxT.getText().toString());
                                    if (woodSpeiFlag == false) {
                                        Common.IsNewWSCode = 1;
                                    } else {
                                        Common.IsNewWSCode = 0;
                                        Common.OldWSCode = "";
                                    }
                                    LogsScanValueETxT.setText("");
                                    Common.EntryMode = 1;
                                    Common.ScanMode = true;
                                    scanService.doDecode();
                                }
                            }

                        }
                        if (Common.PurchaseScannedTabs == 1) {
                            if (PTD_ListLAY.getVisibility() == View.VISIBLE) {
                                if (DetailsValidation(Common.FromLocationname, PTD_ToLocationATXT.getText().toString(), PTD_AgencyATXT.getText().toString(), PTD_DriverATXT.getText().toString(), PTD_TruckDetialsATXT.getText().toString())) {
                                    if (Common.Purchase.PurchaseTransferIsEditorViewFlag == false) {
                                        AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Can not edit Or delete after Synced", false);
                                        return;
                                    }
                                    LogsScanValueETxT.setText("");
                                    Common.EntryMode = 1;
                                    Common.ScanMode = true;
                                    scanService.doDecode();
                                }
                            }
                        }
                        if (Common.PurchaseScannedTabs == 2) {
                            if (PRL_ListLAY.getVisibility() == View.VISIBLE) {
                                if (Common.Purchase.PurchaseReceivedIsEditorViewFlag == false) {
                                    AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Can not edit Or delete after Synced", false);
                                    return;
                                }
                                Common.QRCodeScan = false;
                                Common.ScanMode = true;
                                Common.ScannedEditTXTFlag = false;
                                if (ReceivedDetailsValidation(PRD_TransferIDAutoTXT.getText().toString(), PRD_FromLocationATXT.getText().toString(), PRD_AgencyATXT.getText().toString(), PRD_DriverATXT.getText().toString(), PRD_TruckDetialsATXT.getText().toString())) {
                                    scanService.doDecode();
                                }
                            }
                        }
                        break;
                    case R.id.closePurchse:
                        Signout("Are you sure want to close?");
                        break;
                    case R.id.syncPurchse:
                        try {
                            if (Common.PurchaseScannedTabs == 0) {
                                if (Common.Purchase.IsScannrdEditorViewFlag == false) {
                                    AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Can not edit Or delete after Synced", false);
                                    return;
                                }
                                Common.Purchase.PurchaseLogsSyncDetails = mDBInternalHelper.getPurchaseLogsSyncDetails(Common.Purchase.SelectedPurchaseId);
                                if ( Common.Purchase.PurchaseLogsSyncDetails.size() > 0) {
                                    ConfirmSyncMessage("Are you sure want to sync data to server?");
                                } else {
                                    AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "please re-measurement any one item", false);
                                }
                            }
                        } catch (Exception ex) {
                        }
                        break;
                    case R.id.PrintPurchase:
                        PrintSlip();
                        break;
                    case R.id.agreementTXT:
                        InitialView();
                        break;
                    case R.id.logsTXT:
                        Common.PurchaseScannedTabs = 0;
                        agreementTXT.setBackgroundColor(getResources().getColor(R.color.blue));
                        logsTXT.setBackgroundColor(getResources().getColor(R.color.TabColor));
                        transferTXT.setBackgroundColor(getResources().getColor(R.color.blue));
                        receivedTXT.setBackgroundColor(getResources().getColor(R.color.blue));
                        agreementLAY.setVisibility(View.GONE);
                        logsLAY.setVisibility(View.VISIBLE);
                        transferLAY.setVisibility(View.GONE);
                        receivedLAY.setVisibility(View.GONE);
                        printPurchase.setVisibility(View.VISIBLE);
                        syncPurchase.setVisibility(View.VISIBLE);
                        scanPurchase.setVisibility(View.VISIBLE);
                        ScannedLogsViews();
                        break;
                    case R.id.transferTXT:
                        Common.PurchaseScannedTabs = 1;
                        agreementTXT.setBackgroundColor(getResources().getColor(R.color.blue));
                        logsTXT.setBackgroundColor(getResources().getColor(R.color.blue));
                        transferTXT.setBackgroundColor(getResources().getColor(R.color.TabColor));
                        receivedTXT.setBackgroundColor(getResources().getColor(R.color.blue));
                        agreementLAY.setVisibility(View.GONE);
                        logsLAY.setVisibility(View.GONE);
                        transferLAY.setVisibility(View.VISIBLE);
                        receivedLAY.setVisibility(View.GONE);
                        printPurchase.setVisibility(View.GONE);
                        syncPurchase.setVisibility(View.GONE);
                        scanPurchase.setVisibility(View.VISIBLE);
                        PTD_ListLAY.setVisibility(View.GONE);
                        TransferList();
                        break;
                    case R.id.receivedTXT:
                        Common.PurchaseScannedTabs = 2;
                        agreementTXT.setBackgroundColor(getResources().getColor(R.color.blue));
                        logsTXT.setBackgroundColor(getResources().getColor(R.color.blue));
                        transferTXT.setBackgroundColor(getResources().getColor(R.color.blue));
                        receivedTXT.setBackgroundColor(getResources().getColor(R.color.TabColor));
                        agreementLAY.setVisibility(View.GONE);
                        logsLAY.setVisibility(View.GONE);
                        transferLAY.setVisibility(View.GONE);
                        receivedLAY.setVisibility(View.VISIBLE);
                        printPurchase.setVisibility(View.GONE);
                        syncPurchase.setVisibility(View.GONE);
                        scanPurchase.setVisibility(View.VISIBLE);
                        PRL_ListLAY.setVisibility(View.GONE);
                        ReceivedList();
                        break;
                }
            } catch (
                    Exception ex) {
                AlertDialogBox("OonClickListener", ex.toString(), false);
            }
        }
    };

    View.OnClickListener scannedLogs_ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                    case R.id.scannedlogsEnterIMG:
                        if (FelllingDetailsValidation(SL_WSpecieAutoTxT.getText().toString())) {
                            if (DimensionsValidations(
                                    SbbLabelF1.getText().toString(),
                                    SbbLabelF2.getText().toString(),
                                    SbbLabelT1.getText().toString(),
                                    SbbLabelT2.getText().toString(),
                                    SbbLabelLenght.getText().toString())) {
                                boolean woodSpeiFlag = mDBExternalHelper.WoodSpeiceCodeCheck(SL_WSpecieAutoTxT.getText().toString());
                                if (woodSpeiFlag == false) {
                                    Common.IsNewWSCode = 1;
                                } else {
                                    Common.IsNewWSCode = 0;
                                    Common.OldWSCode = "";
                                }
                                Common.ScanMode = false;
                                Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
                                String SBBLabel = LogsScanValueETxT.getText().toString();
                                if (!isValidBarCode(SBBLabel)) {
                                    AlertDialogBox("Barcode Format", CommonMessage(R.string.ValidBarCodeMsg), false);
                                } else {
                                    Common.EntryMode = 2;
                                    ScannedResult(SBBLabel);
                                }
                            }
                        }

                        break;
                    case R.id.update_delete:
                        Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
                        SbbLableUpdateLayout.setVisibility(View.GONE);
                        break;
                    case R.id.updateTreeNumber_delete:
                        Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
                        TreeUpdateLayout.setVisibility(View.GONE);
                        break;
                }

            } catch (Exception ex) {
                AlertDialogBox("OonClickListener", ex.toString(), false);
            }
        }
    };

    View.OnClickListener purchaseTransfer_ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                    case R.id.purcahse_createNewTransferBTN:
                        try {
                            Common.LoadedTypeID = 2;
                            Common.SyncTime = "";
                            Common.VolumeSum = 0.0;
                            Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());

                            Common.FromTransLocID = Common.FromLocationID;
                            Common.ToLocaTransID = 0;
                            Common.DriverID = 0;
                            Common.TransferAgencyID = 0;
                            Common.TransportId = 0;
                            boolean ListIdFlag = mDBInternalHelper.insertPurchaseTransferID(Common.IMEI, Common.ToLocaTransID, Common.StartDate, Common.EndDate,
                                    Common.FromTransLocID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TransportId, Common.UserID, Common.Count,
                                    Common.SyncStatus, Common.SyncTime, Common.Volume, 1, "0", Common.LoadedTypeID);
                            if (ListIdFlag == true) {
                                Common.TransferID = Integer.parseInt(mDBInternalHelper.getLastPurchaseTransferID());
                                String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                                String DeviceID = "";
                                if (String.valueOf(Common.LDeviceID).length() == 1) {
                                    DeviceID = "0" + Common.LDeviceID;
                                } else {
                                    DeviceID = String.valueOf(Common.LDeviceID);
                                }
                                Common.TransferUniqueID = DateUniqueFormat + DeviceID + Common.TransferID;
                                boolean TransferIDFlag = mDBInternalHelper.UpdatePurchaseTransferUniqueID(Common.TransferID, Common.TransferUniqueID);
                                Common.Purchase.PurchaseTransferIsEditorViewFlag = true;
                                Common.Purchase.IsTransferEditorViewFlag = true;
                                GetPurchaseTransferDateList();
                                TransferDetails();
                                scanPurchase.setVisibility(View.VISIBLE);
                                PTD_ListLAY.setVisibility(View.VISIBLE);
                            } else {
                                AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Values are not Inserted", false);
                            }
                        } catch (Exception ex) {
                            CrashAnalytics.CrashReport(ex);
                            AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), ex.toString(), false);
                        }
                        break;
                    case R.id.ptd_transferDetailsClose:
                        UpdateTransferIDList();
                        scanPurchase.setVisibility(View.GONE);
                        PTD_ListLAY.setVisibility(View.GONE);
                        GetPurchaseTransferDateList();
                        break;
                    case R.id.ptd_transferEnterIMG:
                        if (DetailsValidation(Common.FromLocationname, PTD_ToLocationATXT.getText().toString(), PTD_AgencyATXT.getText().toString(), PTD_DriverATXT.getText().toString(), PTD_TruckDetialsATXT.getText().toString())) {
                            String SBBLabel = PTD_ScanValueETxT.getText().toString();
                            if (!isValidBarCode(SBBLabel)) {
                                AlertDialogBox("Barcode Format", CommonMessage(R.string.ValidBarCodeMsg), false);
                            } else {
                                Common.EntryMode = 2;
                                ScannedResult(SBBLabel);
                            }
                        }
                        break;
                }
            } catch (Exception ex) {
                AlertDialogBox("OonClickListener", ex.toString(), false);
            }
        }
    };

    View.OnClickListener purchaseReceived_ClickListener = v -> {
        try {
            switch (v.getId()) {
                case R.id.NewMeusermentTxT:
                    NewMeusermentLAY.setVisibility(View.VISIBLE);
                    RemarksLAY.setVisibility(View.GONE);
                    HeartMesurmentLAY.setVisibility(View.GONE);
                    NewMeusermentTxT.setBackgroundColor(getResources().getColor(R.color.amber));
                    RemarksTxT.setBackgroundColor(getResources().getColor(R.color.colorbtnsplash));
                    HeartMesurmantTXT.setBackgroundColor(getResources().getColor(R.color.colorbtnsplash));
                    break;
                case R.id.RemarksTxT:
                    NewMeusermentLAY.setVisibility(View.GONE);
                    RemarksLAY.setVisibility(View.VISIBLE);
                    HeartMesurmentLAY.setVisibility(View.GONE);
                    NewMeusermentTxT.setBackgroundColor(getResources().getColor(R.color.colorbtnsplash));
                    RemarksTxT.setBackgroundColor(getResources().getColor(R.color.amber));
                    HeartMesurmantTXT.setBackgroundColor(getResources().getColor(R.color.colorbtnsplash));
                    break;
                case R.id.HeartMeusermentTxT:
                    NewMeusermentLAY.setVisibility(View.GONE);
                    RemarksLAY.setVisibility(View.GONE);
                    HeartMesurmentLAY.setVisibility(View.VISIBLE);
                    NewMeusermentTxT.setBackgroundColor(getResources().getColor(R.color.colorbtnsplash));
                    RemarksTxT.setBackgroundColor(getResources().getColor(R.color.colorbtnsplash));
                    HeartMesurmantTXT.setBackgroundColor(getResources().getColor(R.color.amber));

                    break;
                case R.id.Update_LogDetails:
                    RemarksTypeList = "";
                    RemarksTypeMessage = "";
                    if (UpdateSbbLabelValidation(LD_F1.getText().toString(), LD_F2.getText().toString(), LD_T1.getText().toString(), LD_T2.getText().toString(), LD_Length.getText().toString())) {
                        if (LogHeartValidation(
                                HeartMesF1ETxT.getText().toString(),
                                HeartMesF2ETxT.getText().toString(),
                                HeartMesT1ETxT.getText().toString(),
                                HeartMestT2ETxT.getText().toString())) {
                            if (LogCrackValidation(
                                    CrackF1ETxT.getText().toString(),
                                    CrackF2ETxT.getText().toString(),
                                    CrackT1ETxT.getText().toString(),
                                    CrackT2ETxT.getText().toString())) {

                                if (LogLengthCutValidation(LengthCutFootETxT.getText().toString(),
                                        LengthCutTopETxT.getText().toString())) {
                                    Common.Volume = String.valueOf(Common.decimalFormat.format(
                                            DimensionCalculation.UpdateVolumeCalculation(
                                                    LD_F1.getText().toString(),
                                                    LD_F2.getText().toString(),
                                                    LD_T1.getText().toString(),
                                                    LD_T2.getText().toString(),
                                                    LD_Length.getText().toString(),
                                                    "0.0",
                                                    "0.0",
                                                    "0.0")));
                                    if (isEditTextEmptyDouble(HeartMesF1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(HeartMesF2ETxT.getText().toString()) > 0.0
                                            || isEditTextEmptyDouble(HeartMesT1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(HeartMestT2ETxT.getText().toString()) > 0.0) {
                                        Common.SbbLabelLHVolume = String.valueOf(Common.decimalFormat.format(DimensionCalculation.HeartVolumeCalculation(
                                                HeartMesT1ETxT.getText().toString(),
                                                HeartMestT2ETxT.getText().toString(),
                                                HeartMesF1ETxT.getText().toString(),
                                                HeartMesF2ETxT.getText().toString(),
                                                Common.Volume,
                                                LD_Length.getText().toString(),
                                                LengthCutTopETxT.getText().toString(),
                                                LengthCutFootETxT.getText().toString(),
                                                CrackF1ETxT.getText().toString(),
                                                CrackF2ETxT.getText().toString(),
                                                CrackT1ETxT.getText().toString(),
                                                CrackT2ETxT.getText().toString())));
                                        RemarksTypeList = "LH:" + Common.SbbLabelLHVolume + ",";
                                        RemarksTypeMessage = "5,";
                                    }
                                    if (isEditTextEmptyDouble(LengthCutFootETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(LengthCutTopETxT.getText().toString()) > 0.0
                                            || isEditTextEmptyDouble(HoleF1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(HoleF2ETxT.getText().toString()) > 0.0
                                            || isEditTextEmptyDouble(HoleT1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(HoleT2ETxT.getText().toString()) > 0.0
                                            || isEditTextEmptyDouble(CrackF1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(CrackF2ETxT.getText().toString()) > 0.0
                                            || isEditTextEmptyDouble(CrackT1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(CrackT2ETxT.getText().toString()) > 0.0
                                            || isEditTextEmptyDouble(SapDeductionsETxT.getText().toString()) > 0.0) {

                                        if (isEditTextEmptyDouble(LengthCutFootETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(LengthCutTopETxT.getText().toString()) > 0.0) {
                                            Common.LengthCutVolume = String.valueOf(Common.decimalFormat.format(DimensionCalculation.LengthCutVolumeCalculation(
                                                    LD_F1.getText().toString(),
                                                    LD_F2.getText().toString(),
                                                    LD_T1.getText().toString(),
                                                    LD_T2.getText().toString(),
                                                    LengthCutTopETxT.getText().toString(),
                                                    LengthCutFootETxT.getText().toString()
                                            )));
                                            RemarksTypeList = RemarksTypeList + "LC:" + Common.LengthCutVolume + ",";
                                            RemarksTypeMessage = RemarksTypeMessage + "1,";
                                        }

                                        if (isEditTextEmptyDouble(HoleF1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(HoleF2ETxT.getText().toString()) > 0.0
                                                || isEditTextEmptyDouble(HoleT1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(HoleT2ETxT.getText().toString()) > 0.0) {
                                            try {
                                                Common.HoleVolume = String.valueOf(Common.decimalFormat.format(
                                                        DimensionCalculation.HoleVolumeCalculation(
                                                                HoleF1ETxT.getText().toString(),
                                                                HoleF2ETxT.getText().toString(),
                                                                HoleT1ETxT.getText().toString(),
                                                                HoleT2ETxT.getText().toString(),
                                                                LengthCutTopETxT.getText().toString(),
                                                                LengthCutFootETxT.getText().toString(),
                                                                LD_Length.getText().toString()
                                                        )));
                                                RemarksTypeList = RemarksTypeList + "HV:" + Common.HoleVolume + ",";
                                                RemarksTypeMessage = RemarksTypeMessage + "2,";
                                            } catch (Exception e) {
                                                Log.v("Exception", e.toString());
                                            }
                                        }

                                        if (isEditTextEmptyDouble(CrackF1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(CrackF2ETxT.getText().toString()) > 0.0
                                                || isEditTextEmptyDouble(CrackT1ETxT.getText().toString()) > 0.0 || isEditTextEmptyDouble(CrackT2ETxT.getText().toString()) > 0.0) {

                                            Common.CrackVolume = String.valueOf(Common.decimalFormat.format(DimensionCalculation.CrackVolumeCalculation(
                                                    CrackF1ETxT.getText().toString(),
                                                    CrackF2ETxT.getText().toString(),
                                                    CrackT1ETxT.getText().toString(),
                                                    CrackT2ETxT.getText().toString(),
                                                    LD_F1.getText().toString(),
                                                    LD_F2.getText().toString(),
                                                    LD_T1.getText().toString(),
                                                    LD_T2.getText().toString(),
                                                    Common.Volume,
                                                    LD_Length.getText().toString())));
                                            RemarksTypeList = RemarksTypeList + "CV:" + Common.CrackVolume + ",";
                                            RemarksTypeMessage = RemarksTypeMessage + "3,";
                                        }

                                        if (isEditTextEmptyDouble(SapDeductionsETxT.getText().toString()) > 0.0) {

                                            Common.SapVolume = String.valueOf(Common.decimalFormat.format(DimensionCalculation.SapVolumeCalculation(
                                                    SapDeductionsETxT.getText().toString(),
                                                    LD_F1.getText().toString(),
                                                    LD_F2.getText().toString(),
                                                    LD_T1.getText().toString(),
                                                    LD_T2.getText().toString(),
                                                    LD_Length.getText().toString(),
                                                    Common.Volume,
                                                    LengthCutTopETxT.getText().toString(),
                                                    LengthCutFootETxT.getText().toString(),
                                                    Common.LengthCutVolume)));
                                            RemarksTypeList = RemarksTypeList + "SA:" + Common.SapVolume + ",";
                                            RemarksTypeMessage = RemarksTypeMessage + "4,";
                                        }
                                    } else {
                                        RemarksTypeList = "";
                                    }
                                    RemarksValues = RemarksTypeMessage + "@" + RemarksTypeList;

                                    boolean barcodevalidation = mDBInternalHelper.externalPurchadeBarcodeValidation(Common.UpdateBarCode);
                                    if (barcodevalidation) {
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
                                                RemarksValues,
                                                LengthCutFootETxT.getText().toString(),
                                                LengthCutTopETxT.getText().toString(),
                                                CrackF1ETxT.getText().toString(),
                                                CrackF2ETxT.getText().toString(),
                                                CrackT1ETxT.getText().toString(),
                                                CrackT2ETxT.getText().toString(),
                                                Common.CrackVolume,
                                                HoleF1ETxT.getText().toString(),
                                                HoleF2ETxT.getText().toString(),
                                                HoleT1ETxT.getText().toString(),
                                                HoleT2ETxT.getText().toString(),
                                                Common.HoleVolume,
                                                SapDeductionsETxT.getText().toString()
                                        );
                                        if (UpdateTree) {
                                            GetPurchaseList();
                                            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
                                            UpdateLogDetailsLAY.setVisibility(View.GONE);
                                        }
                                    } else {
                                        boolean TreeResult = mDBInternalHelper.insertPurchaseLogsDetails(Common.Purchase.SelectedPurchaseId, Common.Purchase.SelectedPurchaseNo, Common.UserID,
                                                Common.UpdatedTreeNumber, Common.FsWoodSpieceCode, Common.FsWoodSpieceID, "", 2, Common.UpdateBarCode, LD_F1.getText().toString(),
                                                LD_F2.getText().toString(),
                                                LD_T1.getText().toString(),
                                                LD_T2.getText().toString(), LD_Length.getText().toString(), Common.SbbLabelNoteF, Common.SbbLabelNoteT, Common.SbbLabelNoteL,
                                                Common.Volume, Common.TreePart, 1, HeartMesT1ETxT.getText().toString(),
                                                HeartMestT2ETxT.getText().toString(),
                                                HeartMesF1ETxT.getText().toString(),
                                                HeartMesF2ETxT.getText().toString(),
                                                Common.SbbLabelLHVolume,
                                                Common.DateTime, Common.Username);
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
                                                RemarksValues,
                                                LengthCutFootETxT.getText().toString(),
                                                LengthCutTopETxT.getText().toString(),
                                                CrackF1ETxT.getText().toString(),
                                                CrackF2ETxT.getText().toString(),
                                                CrackT1ETxT.getText().toString(),
                                                CrackT2ETxT.getText().toString(),
                                                Common.CrackVolume,
                                                HoleF1ETxT.getText().toString(),
                                                HoleF2ETxT.getText().toString(),
                                                HoleT1ETxT.getText().toString(),
                                                HoleT2ETxT.getText().toString(),
                                                Common.HoleVolume,
                                                SapDeductionsETxT.getText().toString()
                                        );
                                    }
                                    //Scanned Result Refresh
                                    GetPurchaseList();
                                    Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
                                    UpdateLogDetailsLAY.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                    break;
                case R.id.updateLog_close:
                    UpdateLogDetailsLAY.setVisibility(View.GONE);
                    Common.IsVolumeCalculationFlag = false;
                    break;
                case R.id.cancel_LogDetails:
                    Common.HideKeyboard(this);
                    UpdateLogDetailsLAY.setVisibility(View.GONE);
                    Common.IsVolumeCalculationFlag = false;
                    //AlertDialogBox("Reject Barcode", "In Future", false);
                    break;
                case R.id.prl_receivedCreateScanListTxT:
                    try {
                        Common.LoadedTypeID = 2;
                        Common.SyncTime = "";
                        Common.VolumeSum = 0.0;
                        Common.VBB_Number = "";
                        Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                        Common.ToLocReceivedID = Common.ToLocationID;
                        Common.FromTransLocID = 0;
                        Common.ToLocaTransID = 0;
                        Common.DriverID = 0;
                        Common.TransferAgencyID = 0;
                        Common.TransportId = 0;
                        Common.RecFromLocationname = "";
                        Common.AgencyName = "";
                        Common.DriverName = "";
                        Common.TrucklicensePlateNo = "";
                        Common.RecFromLocationID = 0;
                        boolean ListIdFlag = mDBInternalHelper.insertPurchaseReceivedID(Common.IMEI, Common.ToLocReceivedID, Common.StartDate, Common.EndDate,
                                Common.FromTransLocID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TransportId, Common.UserID, Common.Count,
                                Common.SyncStatus, Common.SyncTime, Common.Volume, 1, Common.ReceivedUniqueID, Common.LoadedTypeID);
                        if (ListIdFlag == true) {
                            //Common.InventoryReceivedList = mDBInternalHelper.getInventoryReceivedIdList(Common.Filter_InventoryReceivedDate.get(Common.InventReceivedDateSelectedIndex));
                            //if (Common.InventoryReceivedList.size() > 0) {
                            Common.ReceivedID = Integer.parseInt(mDBInternalHelper.getLastPurchaseReceivedID());
                            String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                            String DeviceID = "";
                            if (String.valueOf(Common.LDeviceID).length() == 1) {
                                DeviceID = "0" + Common.LDeviceID;
                            } else {
                                DeviceID = String.valueOf(Common.LDeviceID);
                            }
                            Common.ReceivedUniqueID = DateUniqueFormat + DeviceID + Common.ReceivedID;
                            boolean ReceivedIDFlag = mDBInternalHelper.UpdatePurchaseReceivedUniqueID(Common.ReceivedID, Common.ReceivedUniqueID);
                            //}
                            Common.TransferIDsList.clear();
                            Common.Purchase.PurchaseReceivedIsEditorViewFlag = true;
                            Common.Purchase.IsReceivedEditorViewFlag = true;
                            GetPurchaseReceivedDateList();
                            ReceivedDetails();
                            scanPurchase.setVisibility(View.VISIBLE);
                            PRL_ListLAY.setVisibility(View.VISIBLE);
                        } else {
                            AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Values are not Inserted", false);
                        }
                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        ex.printStackTrace();
                        AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), ex.toString(), false);
                    }
                    break;
                case R.id.prd_receivedDetailsClose:
                    UpdateReceivedIDList();
                    scanPurchase.setVisibility(View.GONE);
                    PRL_ListLAY.setVisibility(View.GONE);
                    GetPurchaseReceivedDateList();
                    break;
                case R.id.prd_Received_scanQRDetails:
                    Common.QRCodeScan = true;
                    Common.QrBarCodeScan = false;
                    scanService.doDecode();
                    break;
                case R.id.prd_Received_scanQRBarCode:
                    if (ReceivedDetailsValidation(PRD_TransferIDAutoTXT.getText().toString(), PRD_FromLocationATXT.getText().toString(), PRD_AgencyATXT.getText().toString(), PRD_DriverATXT.getText().toString(), PRD_TruckDetialsATXT.getText().toString())) {
                        Common.QRCodeScan = true;
                        Common.QrBarCodeScan = true;
                        Common.EntryMode = 1;
                        scanService.doDecode();
                    }
                    break;
                case R.id.prd_Received_EnterIMG:
                    try {
                        Common.ScanMode = false;
                        Common.QRCodeScan = false;
                        if (ReceivedDetailsValidation(PRD_TransferIDAutoTXT.getText().toString(), PRD_FromLocationATXT.getText().toString(), PRD_AgencyATXT.getText().toString(), PRD_DriverATXT.getText().toString(), PRD_TruckDetialsATXT.getText().toString())) {
                            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
                            String SBBLabel = PRD_ScanValueETxT.getText().toString();
                            if (!isValidBarCode(SBBLabel)) {
                                AlertDialogBox("Barcode Format", CommonMessage(R.string.ValidBarCodeMsg), false);
                            } else {
                                Common.EntryMode = 2;
                                ScannedResult(SBBLabel);
                            }
                        }
                    } catch (Exception e) {
                        AlertDialogBox("Barcode Format", e.toString(), false);
                    }
                    break;

            }
        } catch (Exception ex) {
            AlertDialogBox("OonClickListener", ex.toString(), false);
        }
    };

    public static double isEditTextEmptyDouble(String editTxT) {
        double Value = 0.0;
        if (isNullOrEmpty(editTxT)) {
            Value = 0.0;
        } else {
            Value = Double.parseDouble(editTxT.trim());
        }
        return Value;
    }

    public void UpdateContainerLimitMessage(String Title, String ErrorMessage, boolean status) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(this);
        Removebuilder.setTitle(Title);
        Removebuilder.setMessage(ErrorMessage);
        Removebuilder.setIcon((status) ? R.mipmap.success : R.mipmap.fail);
        Removebuilder.setCancelable(true);
        Removebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                (dialog, id) -> dialog.cancel());
        Removebuilder.setNegativeButton("CONTINUE",
                (dialog, id) -> {
                    //InsertExportDetails();
                    /*Common.IsContainerSelectFlag = false;
                    containerSpinners.setSelection(LD_ContainerSpinner.getSelectedItemPosition());*/
                    Common.IsVolumeCalculationFlag = false;
                    dialog.cancel();
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public double SbbLableTotalVolume(ArrayList<LogDetailsModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (LogDetailsModel exportDetailsModel : TotalScannedList) {
            TotVolume = TotVolume + exportDetailsModel.getVolume();
        }
        return TotVolume;
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
        SL_Sbblabel_NoteF.setText("");
        SL_Sbblabel_NoteL.setText("");
        SL_Sbblabel_NoteT.setText("");
        //24 -april
        SL_SbblabeLHT1.setText("");
        SL_SbblabeLHT2.setText("");
        SL_SbblabeLHF1.setText("");
        SL_SbblabeLHF2.setText("");
        SL_SbblabeLHVolume.setText("");
        /*hide scan button*/
        //ScannedLogsistrationScanBTN.hide();
        SL_TreePartAUTOTXT.setText("");
        //SL_WSpecieAutoTxT.setText("");
        Common.TreePart = "";
        SL_TreenoAutoTxT.setText("");
        LogsScanValueETxT.setText("");
        SL_PlotTxT.setText("");
    }

    public void InitialView() {
        Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
        agreementTXT.setBackgroundColor(getResources().getColor(R.color.TabColor));
        logsTXT.setBackgroundColor(getResources().getColor(R.color.blue));
        transferTXT.setBackgroundColor(getResources().getColor(R.color.blue));
        receivedTXT.setBackgroundColor(getResources().getColor(R.color.blue));
        agreementLAY.setVisibility(View.VISIBLE);
        logsLAY.setVisibility(View.GONE);
        transferLAY.setVisibility(View.GONE);
        receivedLAY.setVisibility(View.GONE);
        printPurchase.setVisibility(View.GONE);
        syncPurchase.setVisibility(View.GONE);
        scanPurchase.setVisibility(View.GONE);
    }

    public void ScannedClick_Listener() {

        LogsScanValueETxT.setOnTouchListener((v, event) -> {
            Common.ScannedEditTXTFlag = true;
            return false;
        });

        LogsScanValueETxT.addTextChangedListener(new TextWatcher() {
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
                            LogsScanValueETxT.setError("Barcode should be like(XX-1111111)");
                        }
                    }
                } else {
                    if (editable.toString().length() > 0) {
                        String last = editable.toString().substring(editable.toString().length() - 1);
                        if (last.equals("-")) {

                        } else {
                            if (editable.toString().length() == 2) {
                                LogsScanValueETxT.append("-");
                            }
                            if (editable.toString().length() == Common.SBBlenght) {
                                if (!isValidBarCode(editable.toString())) {
                                    LogsScanValueETxT.setError("Barcode should be like(XX-1111111)");
                                }
                            } else {
                                if (Common.ScannedEditTXTFlag == true) {
                                    LogsScanValueETxT.setError(CommonMessage(R.string.BarCodeLenghtMsg));
                                }
                            }
                        }
                    }
                }
            }
        });


        SL_TreenoAutoTxT.addTextChangedListener(new TextWatcher() {
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

        SL_TreenoAutoTxT.setOnTouchListener((v, event) -> {
            SL_TreenoAutoTxT.requestFocus();
            SL_TreenoAutoTxT.showDropDown();
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SL_TreenoAutoTxT.setOnItemClickListener((parent, view, position, id) -> {
            Common.IsNewTreeNumberAdded = true;
            Common.FsTreeNumber = (String) parent.getItemAtPosition(position);
            GetTreeNumber(Common.FsTreeNumber);
        });

        SL_WSpecieAutoTxT.addTextChangedListener(new TextWatcher() {
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

        SL_WSpecieAutoTxT.setOnTouchListener((v, event) -> {
            SL_WSpecieAutoTxT.requestFocus();
            SL_WSpecieAutoTxT.showDropDown();
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SL_WSpecieAutoTxT.setOnItemClickListener((parent, view, position, id) -> {
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
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
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
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        UpdatePlotNumber.setOnItemClickListener((parent, view, position, id) -> {
            Common.PlotNo = (String) parent.getItemAtPosition(position);
            Common.OldPlotNo = (String) parent.getItemAtPosition(position);
            GetPlotNo(Common.PlotNo, UpdateTreeNumberWSC.getText().toString());
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

        SL_TreePartAUTOTXT.addTextChangedListener(new TextWatcher() {
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
                        SL_TreePartAUTOTXT.setText("");
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

        SL_TreePartAUTOTXT.setOnTouchListener((v, event) -> {
            SL_TreePartAUTOTXT.requestFocus();
            SL_TreePartAUTOTXT.showDropDown();
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SL_TreePartAUTOTXT.setOnItemClickListener((parent, view, position, id) -> Common.TreePart = (String) parent.getItemAtPosition(position));

        UpdateTreePartType.setOnTouchListener((v, event) -> {
            UpdateTreePartType.requestFocus();
            UpdateTreePartType.showDropDown();
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SbbLabelF1.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SbbLabelF2.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SbbLabelT1.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SbbLabelT2.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SbbLabelLenght.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SL_Sbblabel_NoteF.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SL_Sbblabel_NoteT.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SL_Sbblabel_NoteL.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SL_SbblabeLHT1.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SL_SbblabeLHT2.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SL_SbblabeLHF1.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SL_SbblabeLHF2.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SL_SbblabeLHVolume.setOnTouchListener((v, event) -> {
            Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
            return false;
        });

        SL_QualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
    }

    private void getQualitySpinner() {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, Common.QulaityDefaultList);
            SL_QualitySpinner.setAdapter(adapter);
        } catch (Exception ex) {
        }
    }

    public void GetPlotNo(String PlotNuber, String WoodSpiceCode) {
        try {
            if (WoodSpiceCode.length() == 0 || PlotNuber.length() == 0) {
                AlertDialogBox("Validation", "Please check WoodSpiceCode either PlotNumber is empty!", false);
                SL_PlotTxT.setText("");
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

    public void TreeNoList(String fellingSecId) {
        try {
            Common.FellingRegTreeFilterList = mDBExternalHelper.getFellingRegisterFilter(Common.FromTransLocID, fellingSecId);
            ArrayList<String> AddedNewTreeNumbers = mDBInternalHelper.getTreeNumberFromInternal(fellingSecId);
            Common.FellingRegTreeNoStringList = new String[Common.FellingRegTreeFilterList.size() + AddedNewTreeNumbers.size()];
            for (int i = 0; i < Common.FellingRegTreeFilterList.size(); i++) {
                Common.FellingRegTreeNoStringList[i] = String.valueOf(Common.FellingRegTreeFilterList.get(i).getTreeNumber());
            }
            for (int i = 0; i < AddedNewTreeNumbers.size(); i++) {
                Common.FellingRegTreeNoStringList[Common.FellingRegTreeFilterList.size() + i] = AddedNewTreeNumbers.get(i);
            }
            StringtreeNoadapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, Common.FellingRegTreeNoStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            SL_TreenoAutoTxT.setAdapter(StringtreeNoadapter);
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
            StringPlotNoadapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, Common.FellingRegPlotNoStringList);
            StringPlotNoadapter.notifyDataSetChanged();
            SL_PlotTxT.setAdapter(StringPlotNoadapter);
            UpdatePlotNumber.setAdapter(StringPlotNoadapter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void WoodSpeicesID(String WoodSpeicesCode) {
        try {
            if (WoodSpeicesCode.length() == 0) {
                AlertDialogBox("Validation", "WoodSpeicesCode is empty!", false);
                SL_WSpecieAutoTxT.setText("");
                UpdateTreeNumberWSC.setText("");
            } else {
                if (purchaseAgreementWSCLists.size() > 0) {
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

    public void TreeNumberCheck(String treeNumber) {
        try {
            ArrayList<FellingTreeDetailsModel> SingleTreeDetailsList = new ArrayList<>();
            boolean TreeNumberCheck = mDBInternalHelper.getTreeNumberDuplicationCheck(Common.FromTransLocID, Common.FellingSectionId, treeNumber);
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
                Toast.makeText(this, "Treenumber already inserted", Toast.LENGTH_SHORT).show();
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

    public void GetTreeNumber(String EnterTreeNumber) {
        try {
            if (Common.FellingSectionId.equals("Select FellingSectionID")) {
                AlertDialogBox("Felling SectionId", "Please check FellingSectionId is empty!", false);
                SL_TreenoAutoTxT.setText("");
                Common.FsTreeNumber = "";
            } else {
                boolean TreeNumberCheck = mDBInternalHelper.getTreeNumberDuplicationCheck(Common.FromTransLocID, Common.FellingSectionId, EnterTreeNumber);
                Common.FellingTreeDetailsCheckList.clear();
                Common.FellingTreeDetailsCheckList = mDBInternalHelper.getTreeNumberDuplication(Common.FromTransLocID, Common.FellingSectionId, EnterTreeNumber);
                if (Common.FellingTreeDetailsCheckList.size() > 0) {
                    Common.FsWoodSpieceCode = Common.FellingTreeDetailsCheckList.get(0).getWoodSpieceCode();
                    //Common.OldWSCode = Common.FsWoodSpieceCode;
                    Common.PlotNo = Common.FellingTreeDetailsCheckList.get(0).getPlotNumber();
                    SL_WSpecieAutoTxT.setText(Common.FsWoodSpieceCode);
                    SL_PlotTxT.setText(Common.PlotNo);
                    TreeF1.setText(Common.FellingTreeDetailsCheckList.get(0).getFooter_1());
                    TreeF2.setText(Common.FellingTreeDetailsCheckList.get(0).getFooter_2());
                    TreeT1.setText(Common.FellingTreeDetailsCheckList.get(0).getTop_1());
                    TreeT2.setText(Common.FellingTreeDetailsCheckList.get(0).getTop_2());
                    TreeLenght.setText(Common.FellingTreeDetailsCheckList.get(0).getLength());
                    //if (TreeNumberCheck == true) {
                } else {
                    String[] WsCAndPlotNO = mDBExternalHelper.getFellingRegisterWoodSpiceCode(String.valueOf(Common.FromTransLocID), EnterTreeNumber, Common.FellingSectionId);
                    if (WsCAndPlotNO.length > 0) {
                        Common.FsWoodSpieceCode = WsCAndPlotNO[0];
                        Common.PlotNo = WsCAndPlotNO[1];
                        Common.PlotId = Integer.parseInt(WsCAndPlotNO[2]);
                    }
                    SL_WSpecieAutoTxT.setText(Common.FsWoodSpieceCode);
                    SL_PlotTxT.setText(Common.PlotNo);
                    Common.OldWSCode = Common.FsWoodSpieceCode;
                    TreeF1.setText("");
                    TreeF2.setText("");
                    TreeT1.setText("");
                    TreeT2.setText("");
                    TreeLenght.setText("");
                    SL_TreePartAUTOTXT.setText("");
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

    // Agreements
    public void AgreementViews() {
        purchaseAgreementWSCLists = mainViewModel.getPuchaseAgreement(this, Common.Purchase.SelectedPurchaseId);
        agreementAdapter = new AgreementAdapter(this, purchaseAgreementWSCLists);
        horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        AgreementList.setLayoutManager(horizontalLayoutManager);
        agreementAdapter.notifyDataSetChanged();
        AgreementList.setAdapter(agreementAdapter);
    }

    //Scanned Logs
    public void ScannedLogsViews() {
        ViewScannedLogsList(Common.Purchase.IsScannrdEditorViewFlag);
        //getfellingSectionSpinner();
        AgreementWoodSpeicesList();
        getQualitySpinner();
        GetTreePart();
        Common.FellingRegDate = Common.dateFormat.format(Calendar.getInstance().getTime());
        SL_dateTxT.setText(Common.FellingRegDate);
        /*Common.TreeNosList.clear();
        TreeNoList(Common.TreeNosList);*/
        if (Common.IsFellingRegEditListFlag == false) {
            FellingRegEditValues();
        }
        GetPurchaseList();
        LogsScanValueETxT.setText("");
    }

    public void ScannedLogsResult() {
        try {
            // Checked Duplicate In Internal Table
            boolean EmptyNullFlags = mDBInternalHelper.getPurchaseLogsduplicateCheckForPurchaseID(Common.Purchase.SelectedPurchaseId, Common.BarCode);
            if (EmptyNullFlags == true) {
                wronBuzzer.start();
                AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Already exsist please try some other logs", false);
                //Scanned Result Refresh
                GetPurchaseList();
                return;
            }
            Common.FsTreeNumber = SL_TreenoAutoTxT.getText().toString();
            Common.FsWoodSpieceCode = SL_WSpecieAutoTxT.getText().toString();
            //Common.FsWoodSpieceID= SbbLabelF1.getText().toString();
            Common.TreePart = SL_TreePartAUTOTXT.getText().toString();

            Common.SbbLabelDF1 = SbbLabelF1.getText().toString();
            Common.SbbLabelDF2 = SbbLabelF2.getText().toString();
            Common.SbbLabelDT1 = SbbLabelT1.getText().toString();
            Common.SbbLabelDT2 = SbbLabelT2.getText().toString();
            Common.SbbLabelLenght = SbbLabelLenght.getText().toString();

            Common.SbbLabelNoteF = SL_Sbblabel_NoteF.getText().toString();
            Common.SbbLabelNoteT = SL_Sbblabel_NoteT.getText().toString();
            Common.SbbLabelNoteL = SL_Sbblabel_NoteL.getText().toString();

            Common.SbbLabelLHT1 = SL_SbblabeLHT1.getText().toString();
            Common.SbbLabelLHT2 = SL_SbblabeLHT2.getText().toString();
            Common.SbbLabelLHF1 = SL_SbblabeLHF1.getText().toString();
            Common.SbbLabelLHF2 = SL_SbblabeLHF2.getText().toString();
            Common.SbbLabelLHVolume = SL_SbblabeLHVolume.getText().toString();
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
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void ScannedLogsSync() {
        if (checkConnection() == true) {
            try {
                PurchaseLogsSyncInputModel purcahseSyncModel = new PurchaseLogsSyncInputModel();
                purcahseSyncModel.setHHPurchaselist(Common.Purchase.PurchaseLogsSyncDetails);
                InsertExport = ApiClient.getApiInterface();
                InsertExport.InsertPurchaseLogs(purcahseSyncModel).enqueue(new Callback<PurchaseLogsSyncInputModel>() {
                    @Override
                    public void onResponse(Call<PurchaseLogsSyncInputModel> call, Response<PurchaseLogsSyncInputModel> response) {
                        progessbarLAY.setVisibility(View.GONE);
                        if (GwwException.GwwException(response.code())) {
                            if (response.isSuccessful()) {
                                Common.SyncStatusList.clear();
                                assert response.body() != null;
                                Common.SyncStatusList.addAll(response.body().getStatus());
                                if (Common.SyncStatusList.get(0).getStatus() == 1) {
                                    Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                                    Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                                    boolean ListIdFlag = mDBInternalHelper.UpdatePurchaseAgreementStatus(1);
                                    if (ListIdFlag) {
                                        Common.Purchase.IsScannrdEditorViewFlag = false;
                                        ViewScannedLogsList(Common.Purchase.IsScannrdEditorViewFlag);
                                        ExteralSync_showAlertDialog(ExternalPurchaseBottomBarActivity.this, CommonMessage(R.string.purchaseLogsHead), "#" + Common.Purchase.SelectedPurchaseId + "--" + Common.SyncStatusList.get(0).getMessage(), true);
                                        //AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "#" + Common.Purchase.SelectedPurchaseId + "--" + Common.SyncStatusList.get(0).getMessage(), true);
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
                        progessbarLAY.setVisibility(View.GONE);
                        AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), t.getMessage(), false);
                    }
                });
            } catch (Exception ex) {

            }
        }
    }

    Handler ScannedLogsPrintHan = new Handler();
    Runnable ScannedLogsPrintRun = new Runnable() {
        @Override
        public void run() {
            try {
                //Common.PlotNo = mDBInternalHelper.GetPlotNoUsingFellingSecID(Common.FromTransLocID, Common.FellingSectionId, Common.FellingRegUniqueID);
                //Common.FellingSectionNumber = mDBExternalHelper.GetFellingSectionNumber(Common.FromTransLocID, Common.FellingSectionId);
                Common.Count = Common.Purchase.PurchaseLogsDetailsInternal.size();
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

    public boolean LogHeartValidation(String lht1, String lht2, String lhf1, String lhf2) {
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

    public boolean LogCrackValidation(String lct1, String lct2, String lcf1, String lcf2) {
        boolean Validattion = true;
        if (lcf1.equals("") && lcf2.equals("") && lct1.equals("") && lct2.equals("")) {
            Validattion = true;
        } else {
            if (!lcf1.equals("") && !lcf2.equals("") && !lct1.equals("") && !lct2.equals("")) {
                Validattion = true;
            } else {
                Validattion = false;
                AlertDialogBox("Log Crack dimensions", "Please enter remaining dimensions!", false);
            }
        }
        return Validattion;
    }

    public boolean LogLengthCutValidation(String lcf, String lct) {
        boolean Validattion = true;
        if (lcf.equals("") && lct.equals("")) {
            Validattion = true;
        } else {
            if (!lcf.equals("") && !lct.equals("")) {
                Validattion = true;
            } else {
                Validattion = false;
                AlertDialogBox("Log Lengthcut dimensions", "Please enter remaining dimensions!", false);
            }
        }
        return Validattion;
    }

    public void ViewScannedLogsList(boolean EdtFlag) {
        if (EdtFlag == false) {
            HideDiamantionLayout.setVisibility(View.VISIBLE);
            SL_EditOptionFlagLay.setVisibility(View.VISIBLE);
            //FellingRegNoEDTxT.setEnabled(false);
            SL_TreenoAutoTxT.setEnabled(false);
            SL_WSpecieAutoTxT.setEnabled(false);
            LogsScanValueETxT.setEnabled(false);
            SL_QualitySpinner.setEnabled(false);
        } else {
            HideDiamantionLayout.setVisibility(View.GONE);
            SL_EditOptionFlagLay.setVisibility(View.GONE);
            //FellingRegNoEDTxT.setEnabled(true);
            SL_TreenoAutoTxT.setEnabled(true);
            SL_WSpecieAutoTxT.setEnabled(true);
            LogsScanValueETxT.setEnabled(true);
            SL_QualitySpinner.setEnabled(true);
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
            if (purchaseAgreementWSCLists.size() > 0) {
                Common.FellingRegWoodSpeicesStringList = purchaseAgreementWSCLists.toArray(new String[0]);
                StringtreeNoadapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, Common.FellingRegWoodSpeicesStringList);
                StringtreeNoadapter.notifyDataSetChanged();
                SL_WSpecieAutoTxT.setAdapter(StringtreeNoadapter);
                UpdateTreeNumberWSC.setAdapter(StringtreeNoadapter);
            }
        } catch (Exception ex) {

        }
    }

    public void GetTreePart() {
        TreePartlist = Arrays.asList(Common.FellingRegTreePartStringList);
        StringTreePartadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Common.FellingRegTreePartStringList);
        StringTreePartadapter.notifyDataSetChanged();
        SL_TreePartAUTOTXT.setAdapter(StringTreePartadapter);
        UpdateTreePartType.setAdapter(StringTreePartadapter);
    }

    public void FellingRegEditValues() {
        SL_dateTxT.setText(Common.FellingRegDate);
        /*Fellign SectionID*/
        for (FellingSectionModel item : Common.FellingSectionList) {
            if (item.getFellingSectionId().equals(Common.FellingSectionId)) {
                //FellingSecIdSpin.setSelection(item.getID() - 1);
            }
        }
    }

    private void GetPurchaseList() {
        try {
            Common.Purchase.PurchaseLogsDetailsInternal.clear();
            Common.Purchase.PurchaseLogsDetailsInternal = mDBInternalHelper.getPurchaseLogsDetails(Common.Purchase.SelectedPurchaseId);
            Common.Purchase.PurchaseLogsDetailsBarcode.clear();
            for (int i = 0; i < Common.Purchase.PurchaseLogsDetailsInternal.size(); i++) {
                Common.Purchase.PurchaseLogsDetailsBarcode.add(Common.Purchase.PurchaseLogsDetailsInternal.get(i).getBarCode());
            }
            Common.Purchase.PurchaseLogsDetailsExternal = mDBExternalHelper.getBarCodeExternalLogDetailsWithPurchaseCode(Common.Purchase.SelectedPurchaseId, Common.Purchase.PurchaseLogsDetailsBarcode);
            Common.Purchase.PurchaseLogsDetailsExternal.addAll(Common.Purchase.PurchaseLogsDetailsInternal);
            Common.Purchase.PurchaseLogsDetails = Common.Purchase.PurchaseLogsDetailsExternal;

            if (Common.Purchase.PurchaseLogsDetails.size() > 0) {
                purchaseLogsAdapter = new PurchaseLogsAdapter(Common.Purchase.PurchaseLogsDetails, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                SL_List.setLayoutManager(horizontalLayoutManager);
                purchaseLogsAdapter.notifyDataSetChanged();
                SL_List.setAdapter(purchaseLogsAdapter);
                NovalueFound.setVisibility(View.GONE);
                SL_List.setVisibility(View.VISIBLE);
            } else {
                NovalueFound.setVisibility(View.VISIBLE);
                SL_List.setVisibility(View.GONE);
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
            Snackbar snackbar = Snackbar.make(this.findViewById(R.id.snack_barList), message, Snackbar.LENGTH_LONG);

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
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_logs_infliator, parent, false);
            PurchaseLogsAdapter.GroceryViewHolder gvh = new PurchaseLogsAdapter.GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(PurchaseLogsAdapter.GroceryViewHolder holder, final int position) {
            holder.Barcode.setText(PurchaseLogsList.get(position).getBarCode());
            holder.WoodSpiceCode.setText(PurchaseLogsList.get(position).getWoodSpeciesCode());
            holder.QualitySpinner.setText(PurchaseLogsList.get(position).getQuality());
            //holder.TreeNo.setText(PurchaseLogsList.get(position).getTreeNumber());
            //holder.PlotNo.setText(PurchaseLogsList.get(position).getPlotNo());
            holder.DF1.setText(String.valueOf(PurchaseLogsList.get(position).getF1()));
            holder.DF2.setText(String.valueOf(PurchaseLogsList.get(position).getF2()));
            holder.DT1.setText(String.valueOf(PurchaseLogsList.get(position).getT1()));
            holder.DT2.setText(String.valueOf(PurchaseLogsList.get(position).getT2()));
            holder.Lenght.setText(String.valueOf(PurchaseLogsList.get(position).getLength_dm()));

            holder.NoteF.setText(String.valueOf(PurchaseLogsList.get(position).getNoteF()));
            holder.NoteT.setText(String.valueOf(PurchaseLogsList.get(position).getNoteT()));
            holder.NoteL.setText(String.valueOf(PurchaseLogsList.get(position).getNoteL()));
            //holder.TreePart.setText(PurchaseLogsList.get(position).getTreePartType());
            holder.Volume.setText(String.valueOf(Common.decimalFormat.format(PurchaseLogsList.get(position).getVolume())));
            holder.HeartVolume.setText(String.valueOf(Common.decimalFormat.format(PurchaseLogsList.get(position).getHeartVolume())));

            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            if (Common.Purchase.PurchaseLogsDetailsBarcode.contains(PurchaseLogsList.get(position).getBarCode())) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.highlightedyellowColor));
            }
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
            int color = generator.getColor(getItem(position));
            holder.Remove.setBackgroundColor(color);
            holder.Remove.setOnClickListener(v -> {
                if (Common.Purchase.IsScannrdEditorViewFlag == false) {
                    AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Can not edit Or delete after Synced", false);
                } else {
                    Common.RemoveSBBLabel = "";
                    Common.RemoveSBBLabel = PurchaseLogsList.get(position).getBarCode();
                    SL_RemoveMessage(CommonMessage(R.string.Remove_Message));
                }
            });
            holder.Updatelayout.setOnClickListener(v -> {
                try {
                    if (Common.Purchase.IsScannrdEditorViewFlag == false) {
                        AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Can not edit Or delete after Synced", false);
                    } else {
                        Common.IsVolumeCalculationFlag = true;
                        Common.UpdatedTreeNumber = PurchaseLogsList.get(position).getTreeNumber();
                        Common.UpdateBarCode = PurchaseLogsList.get(position).getBarCode();
                        UpdateLogDetailsLAY.setVisibility(View.VISIBLE);
                        // first time show new meusurment layout
                        NewMeusermentLAY.setVisibility(View.VISIBLE);
                        RemarksLAY.setVisibility(View.GONE);
                        HeartMesurmentLAY.setVisibility(View.GONE);
                        NewMeusermentTxT.setBackgroundColor(getResources().getColor(R.color.amber));
                        RemarksTxT.setBackgroundColor(getResources().getColor(R.color.colorbtnsplash));
                        HeartMesurmantTXT.setBackgroundColor(getResources().getColor(R.color.colorbtnsplash));

                        LD_SBBLabel.setText(String.valueOf(PurchaseLogsList.get(position).getBarCode()));
                        LD_F1.setText(String.valueOf(PurchaseLogsList.get(position).getF1()));
                        LD_F2.setText(String.valueOf(PurchaseLogsList.get(position).getF2()));
                        LD_T1.setText(String.valueOf(PurchaseLogsList.get(position).getT1()));
                        LD_T2.setText(String.valueOf(PurchaseLogsList.get(position).getT2()));
                        LD_Diameter.setText(String.valueOf(DimensionCalculation.SBBLableDiameterPurchase(
                                String.valueOf(PurchaseLogsList.get(position).getF1()),
                                String.valueOf(PurchaseLogsList.get(position).getF2()),
                                String.valueOf(PurchaseLogsList.get(position).getT1()),
                                String.valueOf(PurchaseLogsList.get(position).getT2())
                        )));
                        LD_Length.setText(String.valueOf(PurchaseLogsList.get(position).getLength_dm()));
                        LD_Volume.setText(String.valueOf(Common.decimalFormat.format(PurchaseLogsList.get(position).getVolume())));
                        if (isNullOrEmpty(String.valueOf(PurchaseLogsList.get(position).getRemarks()))) {
                            LD_Remarks.setText("");
                        } else {
                            LD_Remarks.setText(String.valueOf(PurchaseLogsList.get(position).getRemarks()));
                        }
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
                        HeartMesDiameter.setText(String.valueOf(DimensionCalculation.SBBLableDiameterPurchase(
                                String.valueOf(PurchaseLogsList.get(position).getLHF1()),
                                String.valueOf(PurchaseLogsList.get(position).getLHF2()),
                                String.valueOf(PurchaseLogsList.get(position).getLHT1()),
                                String.valueOf(PurchaseLogsList.get(position).getLHT2())
                        )));
                        HeartMesVolume.setText(String.valueOf(PurchaseLogsList.get(position).getLHVolume()));

                        //Common.FsTreeNumber = PurchaseLogsList.get(position).getTreeNumber();
                        Common.FsWoodSpieceCode = PurchaseLogsList.get(position).getWoodSpeciesCode();
                        WoodSpeicesID(Common.FsWoodSpieceCode);
                        Common.TreePart = "";
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
            TextView Barcode, WoodSpiceCode, QualitySpinner, DF1, DF2, DT1, DT2, Lenght, NoteF, NoteT, NoteL, Volume, HeartVolume;
            //Spinner QualitySpinner,TreeNo;
            LinearLayout Background, Remove, Updatelayout;

            public GroceryViewHolder(View view) {
                super(view);
                Barcode = view.findViewById(R.id.purchaseLogs_SbblabelTxT);
                WoodSpiceCode = view.findViewById(R.id.purchaseLogs_WSpiceTxT);
                //TreeNo = view.findViewById(R.id.purchaseLogs_TreeNoTxT);
                //PlotNo = view.findViewById(R.id.purchaseLogs_PlotNoTxT);
                QualitySpinner = view.findViewById(R.id.purchaseLogs_QualityTxT);
                Background = view.findViewById(R.id.logslayoutBackground);
                Remove = view.findViewById(R.id.purchaseLogs_DeleteList);
                DF1 = view.findViewById(R.id.purchaseLogs_DF1TxT);
                DF2 = view.findViewById(R.id.purchaseLogs_DF2TxT);
                DT1 = view.findViewById(R.id.purchaseLogs_DT1TxT);
                DT2 = view.findViewById(R.id.purchaseLogs_DT2TxT);
                Lenght = view.findViewById(R.id.purchaseLogs_LenghtTxT);
                NoteF = view.findViewById(R.id.purchaseLogs_NoteFTxT);
                NoteT = view.findViewById(R.id.purchaseLogs_NoteTTxT);
                NoteL = view.findViewById(R.id.purchaseLogs_NoteLTxT);
                Updatelayout = view.findViewById(R.id.purchaseLogs_UpdateList);
                //TreePart = view.findViewById(R.id.purchaseLogs_TreePartTxT);
                Volume = view.findViewById(R.id.purchaseLogs_VolumeTXT);
                HeartVolume = view.findViewById(R.id.purchaseLogs_HeartVolumeTXT);
            }
        }

    }

    public void SL_RemoveMessage(String ErrorMessage) {
        if (LogsRemoveAlert != null && LogsRemoveAlert.isShowing()) {
            return;
        }
        LogsRemovebuilder = new AlertDialog.Builder(this);
        LogsRemovebuilder.setMessage(ErrorMessage);
        LogsRemovebuilder.setCancelable(true);
        LogsRemovebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                (dialog, id) -> dialog.cancel());
        LogsRemovebuilder.setNegativeButton(CommonMessage(R.string.action_Remove),
                (dialog, id) -> {
                    try {
                        boolean Isdelete = mDBInternalHelper.RemovePurchaseLogs(Common.RemoveSBBLabel, 0, Common.Purchase.SelectedPurchaseId);
                        if (Isdelete == true) {
                            GetPurchaseList();
                            Toast.makeText(this, "Successfully Removed from List", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {

                    }
                    dialog.cancel();
                });

        LogsRemoveAlert = LogsRemovebuilder.create();
        LogsRemoveAlert.show();
    }

    // Transfer List and Details
    public void TransferList() {
        Common.InventTransDateSelectedIndex = 0;
        GetPurchaseTransferDateList();
    }

    public void GetPurchaseTransferDateList() {
        try {
            Common.Purchase.Filter_PurchaseTransDate.clear();
            Common.Purchase.Filter_PurchaseTransDate = mDBInternalHelper.getPurchaseTransferDate(Common.Purchase.SelectedPurchaseId);
            if (Common.Purchase.Filter_PurchaseTransDate.size() > 0) {
                transferDateadapter = new TransferDateAdapter(Common.Purchase.Filter_PurchaseTransDate, this);
                TransDateLayoutManager = new LinearLayoutManager(this);
                transferDateadapter.notifyDataSetChanged();
                PurchaseTransDateList.setLayoutManager(TransDateLayoutManager);
                PurchaseTransDateList.setAdapter(transferDateadapter);
                PurchaseTransDateList.setVisibility(View.VISIBLE);
                //NoValueFoundTxT.setVisibility(View.GONE);
                GetPurchaseTransferList(String.valueOf(Common.Purchase.Filter_PurchaseTransDate.get(Common.Purchase.PurcahseTransDateSelectedIndex)), Common.Purchase.SelectedPurchaseId);
            } else {
                PurchaseTransList.setVisibility(View.GONE);
                PurchaseTransDateList.setVisibility(View.GONE);
                //PTD_NovalueFound.setVisibility(View.VISIBLE);
                PurchaseTransTotalFilteredCount.setText("0");
                PurchaseTransTotalFilteredVolume.setText("0.00");
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public class TransferDateAdapter extends RecyclerView.Adapter<TransferDateAdapter.TransferViewHolder> {
        private List<String> PurchaseTransferDateList;
        Context context;

        public TransferDateAdapter(List<String> PurchaseTransferDateList, Context context) {
            this.PurchaseTransferDateList = PurchaseTransferDateList;
            this.context = context;
        }

        @Override
        public TransferDateAdapter.TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_count_dates_infliator, parent, false);
            TransferDateAdapter.TransferViewHolder gvh = new TransferDateAdapter.TransferViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return PurchaseTransferDateList.size();
        }

        @Override
        public void onBindViewHolder(TransferDateAdapter.TransferViewHolder holder, final int position) {
            //holder.TransferIDTXT.setText(String.valueOf(position + 1));
            if (Common.Purchase.PurcahseTransDateSelectedIndex == position) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.orange));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.colorgGreenSmooth));
            }
            holder.TransferDateTXT.setText(String.valueOf(PurchaseTransferDateList.get(position)));

            holder.Background.setOnClickListener(v -> {
                Common.Purchase.PurcahseTransDateSelectedIndex = position;
                notifyDataSetChanged();
                GetPurchaseTransferList(String.valueOf(PurchaseTransferDateList.get(position)), Common.Purchase.SelectedPurchaseId);
            });

        }

        public class TransferViewHolder extends RecyclerView.ViewHolder {
            TextView TransferDateTXT;
            LinearLayout Background;

            public TransferViewHolder(View view) {
                super(view);
                TransferDateTXT = view.findViewById(R.id.textview_dates);
                Background = view.findViewById(R.id.FilterDateBackGround);
            }
        }
    }

    public void GetPurchaseTransferList(String SelectedDate, int purchaseID) {
        try {
            Common.Purchase.PurchaseTransferList.clear();
            Common.Purchase.PurchaseTransferList = mDBInternalHelper.getPurchaseTransferIdList(SelectedDate, purchaseID);
            if (Common.Purchase.PurchaseTransferList.size() > 0) {
                transListadapter = new TransferListAdapter(Common.Purchase.PurchaseTransferList, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                transListadapter.notifyDataSetChanged();
                PurchaseTransList.setLayoutManager(horizontalLayoutManager);
                PurchaseTransList.setAdapter(transListadapter);
                //NoValueFoundTxT.setVisibility(View.GONE);
                PurchaseTransList.setVisibility(View.VISIBLE);
            } else {
                PurchaseTransList.setVisibility(View.GONE);
                //NoValueFoundTxT.setVisibility(View.VISIBLE);
            }
            PurchaseTransTotalFilteredCount.setText(String.valueOf(Common.Purchase.PurchaseTransferList.size()));
            PurchaseTransTotalFilteredVolume.setText(String.valueOf(mDBInternalHelper.TotalVolumeForPurcahseTransfer(SelectedDate)));
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public class TransferListAdapter extends RecyclerView.Adapter<TransferListAdapter.TransferViewHolder> {
        private List<PurchaseTransferModel> PurchaseTransferList;
        Context context;

        public TransferListAdapter(List<PurchaseTransferModel> ScannedResultList, Context context) {
            this.PurchaseTransferList = ScannedResultList;
            this.context = context;
        }

        @Override
        public TransferListAdapter.TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_transferlist_infliator, parent, false);
            TransferListAdapter.TransferViewHolder gvh = new TransferListAdapter.TransferViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return PurchaseTransferList.size();
        }

        @Override
        public void onBindViewHolder(TransferListAdapter.TransferViewHolder holder, final int position) {

            holder.TransferIDTXT.setText(String.valueOf(PurchaseTransferList.get(position).getPurchaseTransferUniqueID()));
            holder.StartTimeTXT.setText(PurchaseTransferList.get(position).getStartDateTime());
            holder.EndTimeTXT.setText(PurchaseTransferList.get(position).getEndDateTime());
            holder.CountTXT.setText(String.valueOf(PurchaseTransferList.get(position).getCount()));

            holder.VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(PurchaseTransferList.get(position).getVolume())));
            holder.SyncStatusTXT.setText(String.valueOf(PurchaseTransferList.get(position).getSyncStatus()));
            if (PurchaseTransferList.get(position).getSyncStatus() == 1) {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.red));
            }
            holder.SyncTimeTXT.setText(PurchaseTransferList.get(position).getSyncTime());
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            if (PurchaseTransferList.get(position).getCount() > 0) {
                holder.syncTXT.setVisibility(View.VISIBLE);
            } else {
                holder.syncTXT.setVisibility(View.INVISIBLE);
            }
            holder.syncTXT.setOnClickListener(v -> {
                //Sync Date to BackEnd
                try {
                    Common.SyncStartDateTime = PurchaseTransferList.get(position).getStartDateTime();
                    Common.SyncEndDateTime = PurchaseTransferList.get(position).getEndDateTime();
                    Common.TransferID = PurchaseTransferList.get(position).getID();
                    Common.SyncBarCodeCount = PurchaseTransferList.get(position).getCount();
                    Common.FromTransLocID = PurchaseTransferList.get(position).getFromLocationID();
                    Common.ToLocaTransID = PurchaseTransferList.get(position).getToLocationID();

                    Common.TransportTypeId = PurchaseTransferList.get(position).getTransportTypeId();
                    Common.LoadedTypeID = PurchaseTransferList.get(position).getLoadedTypeID();
                    Common.TransferAgencyID = PurchaseTransferList.get(position).getTransferAgencyID();
                    Common.DriverID = PurchaseTransferList.get(position).getDriverID();
                    Common.TransferUniqueID = PurchaseTransferList.get(position).getPurchaseTransferUniqueID();
                    Common.TransportId = PurchaseTransferList.get(position).getTruckId();
                    //PurchaseTransferSync(Common.VBB_Number, Common.TransferID);
                    InventoryTransferSync(PurchaseTransferList.get(position));
                } catch (Exception ex) {
                    AlertDialogBox("Transfer Sync", ex.toString(), false);
                }
            });

            holder.DeleteIMG.setOnClickListener(v -> {
                try {
                    Common.TransferID = PurchaseTransferList.get(position).getID();
                    Common.TransferUniqueID = PurchaseTransferList.get(position).getPurchaseTransferUniqueID();
                    //Remove From Transfer Lsit and Scanned list
                    if (PurchaseTransferList.get(position).getSyncStatus() == 1) {
                        DeletePurchaseTransferListandScanned(Common.TransferID, Common.TransferUniqueID);
                    } else {
                        if (PurchaseTransferList.get(position).getCount() < 5) {
                            DeletePurchaseTransferListandScanned(Common.TransferID, Common.TransferUniqueID);
                            return;
                        }
                        AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "This is not Syncked yet", false);
                    }
                } catch (Exception ex) {
                    AlertDialogBox("Transfer DeleteIMG", ex.toString(), false);
                }
            });

            holder.printIMG.setOnClickListener(v -> {
                try {
                    Common.TransferID = PurchaseTransferList.get(position).getID();
                    Common.TransferUniqueID = PurchaseTransferList.get(position).getPurchaseTransferUniqueID();
                    Common.InventorytransferScannedResultList.clear();
                    Common.InventorytransferScannedResultList = mDBInternalHelper.getPurchaseTransferPrintout(Common.TransferUniqueID);
                    if (Common.InventorytransferScannedResultList.size() > 0) {
                        Common.DriverID = PurchaseTransferList.get(position).getDriverID();
                        Common.DriverName = mDBExternalHelper.getAllDriverName(Common.DriverID);
                        Common.TransportTypeId = PurchaseTransferList.get(position).getTransportTypeId();
                        Common.TransportMode = mDBExternalHelper.getAllTransPortMode(Common.TransportTypeId);
                        Common.FromTransLocID = PurchaseTransferList.get(position).getFromLocationID();
                        Common.FromLocationname = mDBExternalHelper.getFromLocationName(Common.FromTransLocID);
                        Common.ToLocaTransID = PurchaseTransferList.get(position).getToLocationID();
                        Common.ToLocationName = mDBExternalHelper.getToLocationName(Common.ToLocaTransID);
                        Common.TransferAgencyID = PurchaseTransferList.get(position).getTransferAgencyID();
                        Common.AgencyName = mDBExternalHelper.getAgencyName(Common.TransferAgencyID);
                        Common.LoadedTypeID = PurchaseTransferList.get(position).getLoadedTypeID();
                        Common.LoadedName = mDBExternalHelper.getLoadedName(Common.LoadedTypeID);
                        Common.TransportId = PurchaseTransferList.get(position).getTruckId();
                        Common.TrucklicensePlateNo = mDBExternalHelper.getAllTruckNames(Common.TransportId);
                        PrintSlip();
                    } else {
                        AlertDialogBox("Transfer PrintSlip", "No values found, try some other item", false);
                    }
                } catch (Exception ex) {
                    AlertDialogBox("Transfer PrintSlip", ex.toString(), false);
                }
            });

            holder.Background.setOnLongClickListener(v -> {
                try {
                    Common.TransferUniqueID = PurchaseTransferList.get(position).getPurchaseTransferUniqueID();
                    Common.TransferID = PurchaseTransferList.get(position).getID();
                    Common.FromTransLocID = PurchaseTransferList.get(position).getFromLocationID();
                    Common.ToLocaTransID = PurchaseTransferList.get(position).getToLocationID();
                    Common.DriverID = PurchaseTransferList.get(position).getDriverID();
                    Common.TransferAgencyID = PurchaseTransferList.get(position).getTransferAgencyID();
                    Common.TransportTypeId = PurchaseTransferList.get(position).getTransportTypeId();
                    Common.TransportId = PurchaseTransferList.get(position).getTruckId();
                    Common.LoadedTypeID = PurchaseTransferList.get(position).getLoadedTypeID();

                    if (PurchaseTransferList.get(position).getSyncStatus() == 0) {
                        Common.Purchase.PurchaseTransferIsEditorViewFlag = true;
                    } else {
                        Common.Purchase.PurchaseTransferIsEditorViewFlag = false;
                    }
                    Common.Purchase.IsTransferEditorViewFlag = false;
                    TransferDetails();
                    scanPurchase.setVisibility(View.VISIBLE);
                    PTD_ListLAY.setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                    AlertDialogBox("Transfer Background", ex.toString(), false);
                }
                return false;
            });
        }

        public class TransferViewHolder extends RecyclerView.ViewHolder {
            TextView TransferIDTXT, StartTimeTXT, EndTimeTXT, CountTXT, SyncStatusTXT, SyncTimeTXT, VolumeTXT;
            ImageView syncTXT, printIMG, DeleteIMG;
            LinearLayout Background;

            public TransferViewHolder(View view) {
                super(view);
                Background = view.findViewById(R.id.purchase_transferlayoutBackground);
                TransferIDTXT = view.findViewById(R.id.purchase_transfer_IDTxT);
                StartTimeTXT = view.findViewById(R.id.purchase_transfer_startTimeTxT);
                EndTimeTXT = view.findViewById(R.id.purchase_transfer_endTimeTxT);
                CountTXT = view.findViewById(R.id.purchase_transfer_countTxT);
                SyncStatusTXT = view.findViewById(R.id.purchase_transfer_syncStatusTxT);
                SyncTimeTXT = view.findViewById(R.id.purchase_transfer_syncTimeTxT);
                syncTXT = view.findViewById(R.id.purchase_transfer_syncIMG);
                printIMG = view.findViewById(R.id.purchase_printSlip_IMG);
                VolumeTXT = view.findViewById(R.id.purchase_transfer_VolumeTxT);
                DeleteIMG = view.findViewById(R.id.purchase_transfer_Delete);
            }
        }
    }

    public void TransferDetails() {
        //Common.HideKeyboard(ExternalPurchaseBottomBarActivity.this);
        ViewTransfeList(Common.Purchase.PurchaseTransferIsEditorViewFlag);
        TransferToLocationsforSpinner();
        driverforSpinner();
        agencyforSpinner();
        truckforSpinner();
        transModeforView();
        PTD_LoadedByView();
        //Purchase Transfer List
        if (Common.Purchase.IsTransferEditorViewFlag == false) {
            TransferEditValues();
        } else {
            PTD_ToLocationATXT.setText("");
            PTD_DriverATXT.setText("");
            PTD_AgencyATXT.setText("");
            PTD_TruckDetialsATXT.setText("");
        }
        PTD_Click_Listener();
        GetTransferScannedResultList();
        PTD_ScanValueETxT.setText("");
    }

    public boolean DetailsValidation(String FromLoc_Str, String ToLoc_ID, String TransAge_ID, String Driver_ID, String Truck_ID) {
        boolean Validattion = true;
        /* if (isNullOrEmpty(FromLoc_Str)) {
            Validattion = false;
            AlertDialogBox("From Location", "Verify from Location", false);
        }*/ /*else {
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

    public void ViewTransfeList(boolean EdtFlag) {
        if (EdtFlag == false) {
            PTD_EditOptionFlagLay.setVisibility(View.VISIBLE);
            PTD_EditOptionFlagLay2.setVisibility(View.VISIBLE);
            PTD_ScanValueETxT.setEnabled(false);
            findViewById(R.id.ptd_transferEnterIMG).setEnabled(false);
        } else {
            PTD_EditOptionFlagLay.setVisibility(View.GONE);
            PTD_EditOptionFlagLay2.setVisibility(View.GONE);
            PTD_ScanValueETxT.setEnabled(true);
            findViewById(R.id.ptd_transferEnterIMG).setEnabled(true);
        }
    }

    private void TransferToLocationsforSpinner() {
        try {
            // FromLocation Name from FromLocationID
            Common.ConcessionList.clear();
            Common.ConcessionList = mDBExternalHelper.getAllConcessionNames(Common.FromLocationID);
            if (Common.ConcessionList.size() > 0) {
                Common.RecFromLocationID = Common.ConcessionList.get(0).getFromLocationId();
                Common.FromLocationname = Common.ConcessionList.get(0).getConcessionName();
            }
            // To Location Spinner
            Common.LocationList.clear();
            Common.LocationList = mDBExternalHelper.getToLocations();
            Common.ConcessionListStringList = new String[Common.LocationList.size()];
            for (int i = 0; i < Common.LocationList.size(); i++) {
                Common.ConcessionListStringList[i] = Common.LocationList.get(i).getLocation();
            }
            StringtreeNoadapter = new StringtreeNoadapter(this,
                    android.R.layout.simple_dropdown_item_1line, Common.ConcessionListStringList);
            StringtreeNoadapter.notifyDataSetChanged();

            PTD_FromLocationTxT.setText(Common.FromLocationname);
            //PTD_FromLocationTxT.setText(Common.ToLocationName);
            PTD_ToLocationATXT.setThreshold(1);
            PTD_ToLocationATXT.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
        }
    }

    private void ReceivedToLocationsforSpinner() {
        try {
            // FromLocation Name from FromLocationID
            Common.ConcessionList.clear();
            Common.ConcessionList = mDBExternalHelper.getConcessionList();

            Common.LocationList.clear();
            Common.LocationList = mDBExternalHelper.getToLocations();
            for (LocationsModel locationMod : Common.LocationList) {
                if (Common.ToLocReceivedID == locationMod.getToLocationId()) {
                    Common.ToLocationName = locationMod.getLocation();
                    PRD_ToLocationTxT.setText(Common.ToLocationName);
                }
            }
            Common.ConcessionListStringList = new String[Common.ConcessionList.size()];
            for (int i = 0; i < Common.ConcessionList.size(); i++) {
                Common.ConcessionListStringList[i] = Common.ConcessionList.get(i).getConcessionName();
            }
            StringtreeNoadapter = new StringtreeNoadapter(this,
                    android.R.layout.simple_dropdown_item_1line, Common.ConcessionListStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            PRD_FromLocationATXT.setThreshold(1);
            PRD_FromLocationATXT.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
            AlertDialogBox("FromLocation Spinner", ex.toString(), false);
        }
    }

    private void truckforSpinner() {
        try {
            Common.TruckDeatilsList.clear();
            Common.TruckDeatilsList = mDBExternalHelper.getAllTruckDetails();
            Common.TruckDetialsStringList = new String[Common.TruckDeatilsList.size()];
            for (int i = 0; i < Common.TruckDeatilsList.size(); i++) {
                Common.TruckDetialsStringList[i] = Common.TruckDeatilsList.get(i).getTruckLicensePlateNo();
            }
            StringtreeNoadapter = new StringtreeNoadapter(this,
                    android.R.layout.simple_dropdown_item_1line, Common.TruckDetialsStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            if (Common.PurchaseScannedTabs == 1) {// Transfer
                PTD_TruckDetialsATXT.setThreshold(1);
                PTD_TruckDetialsATXT.setAdapter(StringtreeNoadapter);
            }
            if (Common.PurchaseScannedTabs == 2) {// Received
                PRD_TruckDetialsATXT.setThreshold(1);
                PRD_TruckDetialsATXT.setAdapter(StringtreeNoadapter);
            }
        } catch (Exception ex) {
        }
    }

    private void agencyforSpinner() {
        try {
            Common.TransportAgencyList.clear();
            Common.TransportAgencyList = mDBExternalHelper.getAllAgencyDetails();
            Common.AgencyDetailsStringList = new String[Common.TransportAgencyList.size()];
            for (int i = 0; i < Common.TransportAgencyList.size(); i++) {
                Common.AgencyDetailsStringList[i] = Common.TransportAgencyList.get(i).getAgencyName();
            }
            StringtreeNoadapter = new StringtreeNoadapter(this,
                    android.R.layout.simple_dropdown_item_1line, Common.AgencyDetailsStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            if (Common.PurchaseScannedTabs == 1) {// Transfer
                PTD_AgencyATXT.setThreshold(1);
                PTD_AgencyATXT.setAdapter(StringtreeNoadapter);
            }
            if (Common.PurchaseScannedTabs == 2) {// Received
                PRD_AgencyATXT.setThreshold(1);
                PRD_AgencyATXT.setAdapter(StringtreeNoadapter);
            }
        } catch (Exception ex) {
        }
    }

    private void driverforSpinner() {
        try {
            Common.DriverList.clear();
            Common.DriverList = mDBExternalHelper.getAllDriverDetails();
            Common.DriverListStringList = new String[Common.DriverList.size()];
            for (int i = 0; i < Common.DriverList.size(); i++) {
                Common.DriverListStringList[i] = Common.DriverList.get(i).getDriverName();
            }
            StringtreeNoadapter = new StringtreeNoadapter(this,
                    android.R.layout.simple_dropdown_item_1line, Common.DriverListStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            if (Common.PurchaseScannedTabs == 1) {// Transfer
                PTD_DriverATXT.setThreshold(1);
                PTD_DriverATXT.setAdapter(StringtreeNoadapter);
            }
            if (Common.PurchaseScannedTabs == 2) {// Received
                PRD_DriverATXT.setThreshold(1);
                PRD_DriverATXT.setAdapter(StringtreeNoadapter);
            }
        } catch (Exception ex) {
        }
    }

    private void transModeforView() {
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    Common.TransportModeList.clear();
                    Common.TransportModeList = mDBExternalHelper.getAllTransportModeDetails();
                    if (Common.TransportModeList.size() > 0) {
                        TransportAdapterInside transportAdapter = new TransportAdapterInside(ExternalPurchaseBottomBarActivity.this, Common.TransportModeList);
                        transportAdapter.notifyDataSetChanged();
                        if (Common.PurchaseScannedTabs == 1) {// Transfer
                            PTD_TransferMode_LV.setAdapter(transportAdapter);
                        }
                        if (Common.PurchaseScannedTabs == 2) {// Received
                            PRD_TransferMode_LV.setAdapter(transportAdapter);
                        }
                    }
                }
            });
        } catch (Exception ex) {
        }
    }

    private void PTD_LoadedByView() {
        try {
            Common.LoadedByList.clear();
            Common.LoadedByList = mDBExternalHelper.getAllLoadedByDetails();
            if (Common.LoadedByList.size() > 0) {
                loadedAdapter = new LoadedAdapterInside(this, Common.LoadedByList);
                loadedAdapter.notifyDataSetChanged();
                PTD_LoadedBy_LV.setAdapter(loadedAdapter);
            }
        } catch (Exception ex) {
        }
    }

    public void TransferEditValues() {
        try {
            // FromLocation Name from FromLocationID
            Common.ConcessionList.clear();
            Common.ConcessionList = mDBExternalHelper.getAllConcessionNames(Common.FromTransLocID);
            if (Common.ConcessionList.size() > 0) {
                Common.FromLocationname = Common.ConcessionList.get(0).getConcessionName();
                PTD_FromLocationTxT.setText(Common.FromLocationname);
            }
            if (Common.ToLocaTransID == 0) {
                PTD_ToLocationATXT.setText("");
            } else {
                for (LocationsModel item : Common.LocationList) {
                    if (item.getToLocationId() == Common.ToLocaTransID) {
                        //ToLocationSpin.setSelection(item.getID());
                        PTD_ToLocationATXT.setText(item.getLocation());
                    }
                }
            }
            if (Common.DriverID == 0) {
                PTD_DriverATXT.setText("");
            } else {
                for (DriverDetailsModel item : Common.DriverList) {
                    if (item.getTruckDriverId() == Common.DriverID) {
                        //DriverSpin.setSelection(item.getID());Common.AgencyName
                        Common.DriverName = item.getDriverName();
                        PTD_DriverATXT.setText(item.getDriverName());
                    }
                }
            }
            if (Common.TransferAgencyID == 0) {
                PTD_AgencyATXT.setText("");
            } else {
                for (AgencyDetailsModel item : Common.TransportAgencyList) {
                    if (item.getAgencyId() == Common.TransferAgencyID) {
                        //TransportAgencySpin.setSelection(item.getID());
                        Common.AgencyName = item.getAgencyName();
                        PTD_AgencyATXT.setText(item.getAgencyName());
                    }
                }
            }
            if (Common.TransportId == 0) {
                PTD_TruckDetialsATXT.setText("");
            } else {
                for (TruckDetailsModel item : Common.TruckDeatilsList) {
                    if (item.getTransportId() == Common.TransportId) {
                        //TruckPlateNumberSpin.setSelection(item.getID());
                        Common.TrucklicensePlateNo = item.getTruckLicensePlateNo();
                        PTD_TruckDetialsATXT.setText(item.getTruckLicensePlateNo());
                    }
                }
            }
        } catch (Exception ex) {

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
                TransferMode.setChecked(true);
                Common.TransportMode = modename.get(position).getTransportMode();
            } else {
                TransferMode.setChecked(false);
            }
            TransferMode.setText(modename.get(position).getTransportMode());
            TransferMode.setOnClickListener(v -> {
                Common.TransportTypeId = modename.get(position).getTransportTypeId();
                Common.TransportMode = modename.get(position).getTransportMode();
                notifyDataSetChanged();
            });

            TransferMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Common.TransportTypeId = modename.get(position).getTransportTypeId();
                Common.TransportMode = modename.get(position).getTransportMode();
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
            if (Common.LoadedTypeID == 0) {
                Common.LoadedTypeID = 2;
            }
            if (Common.LoadedTypeID == modename.get(position).getLoadedid()) {
                LoadedMode.setChecked(true);
                Common.LoadedName = modename.get(position).getName();
            } else {
                LoadedMode.setChecked(false);
            }
            LoadedMode.setText(modename.get(position).getName());
            LoadedMode.setOnClickListener(v -> {
                Common.LoadedTypeID = modename.get(position).getLoadedid();
                Common.LoadedName = modename.get(position).getName();
                notifyDataSetChanged();
            });

            LoadedMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Common.LoadedTypeID = modename.get(position).getLoadedid();
                Common.LoadedName = modename.get(position).getName();
                notifyDataSetChanged();
            });
            return view;
        }
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

    public void PTD_Click_Listener() {

        PTD_ScanValueETxT.setOnTouchListener((v, event) -> {
            Common.ScannedEditTXTFlag = true;
            return false;
        });

        PTD_ScanValueETxT.addTextChangedListener(new TextWatcher() {
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
                            PTD_ScanValueETxT.setError("Barcode should be like(XX-1111111)");
                        }
                    }
                } else {
                    if (editable.toString().length() > 0) {
                        String last = editable.toString().substring(editable.toString().length() - 1);
                        if (last.equals("-")) {

                        } else {
                            if (editable.toString().length() == 2) {
                                PTD_ScanValueETxT.append("-");
                            }
                            if (editable.toString().length() == Common.SBBlenght) {
                                if (!isValidBarCode(editable.toString())) {
                                    PTD_ScanValueETxT.setError("Barcode should be like(XX-1111111)");
                                }
                            } else {
                                if (Common.ScannedEditTXTFlag == true) {
                                    PTD_ScanValueETxT.setError(CommonMessage(R.string.BarCodeLenghtMsg));
                                }
                            }
                        }
                    }
                }
            }
        });

        PTD_EditOptionFlagLay.setOnClickListener(v -> AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Can not edit Or delete after Synced", false));

        PTD_EditOptionFlagLay2.setOnClickListener(v -> AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Can not edit Or delete after Synced", false));

        /*AutoComplete*/
        PTD_ToLocationATXT.setOnTouchListener((v, event) -> {
            PTD_ToLocationATXT.requestFocus();
            PTD_ToLocationATXT.showDropDown();
            return false;
        });

        PTD_ToLocationATXT.setOnItemClickListener((parent, view, position, id) -> {
            String locModelStr = (String) parent.getItemAtPosition(position);
            for (LocationsModel locModel : Common.LocationList)
                if (locModelStr.equals(locModel.getLocation())) {
                    Common.ToLocaTransID = locModel.getToLocationId();
                    Common.ToLocationName = locModel.getLocation();
                }
        });

        PTD_DriverATXT.setOnTouchListener((v, event) -> {
            PTD_DriverATXT.requestFocus();
            PTD_DriverATXT.showDropDown();
            return false;
        });

        PTD_DriverATXT.setOnItemClickListener((parent, view, position, id) -> {
            try {
                String item = (String) parent.getItemAtPosition(position);
                if (!isNullOrEmpty(item)) {
                    drivermodelArray = new DriverDetailsModel();
                    drivermodelArray = mDBExternalHelper.getOneDriverDetails(item);
                    if (drivermodelArray.getIsBlocked() == 1) {
                        AlertDialogBox("Verification", "Selected driver is blocked, please contact admin", false);
                        PTD_DriverATXT.setText("");
                    } else {
                        String driverModelStr = drivermodelArray.getDriverName();
                        Common.DriverName = drivermodelArray.getDriverName();
                        Common.DriverID = drivermodelArray.getTruckDriverId();
                        PTD_DriverATXT.setText(driverModelStr);
                    }
                }
            } catch (Exception ex) {

            }
        });

        PTD_AgencyATXT.setOnTouchListener((v, event) -> {
            PTD_AgencyATXT.requestFocus();
            PTD_AgencyATXT.showDropDown();
            return false;
        });

        PTD_AgencyATXT.setOnItemClickListener((parent, view, position, id) -> {
            try {
                String item = (String) parent.getItemAtPosition(position);
                if (!isNullOrEmpty(item)) {
                    agencyModelArray = new AgencyDetailsModel();
                    agencyModelArray = mDBExternalHelper.getOneAgencyDetails(item);
                    if (agencyModelArray.getIsBlocked() == 1) {
                        AlertDialogBox("Verification", "Selected agent is blocked, please contact admin", false);
                        PTD_AgencyATXT.setText("");
                    } else {
                        String agencyModelStr = agencyModelArray.getAgencyName();
                        Common.AgencyName = agencyModelArray.getAgencyName();
                        Common.TransferAgencyID = agencyModelArray.getAgencyId();
                        PTD_AgencyATXT.setText(agencyModelStr);
                    }
                }
            } catch (Exception ex) {

            }
        });

        PTD_TruckDetialsATXT.setOnTouchListener((v, event) -> {
            PTD_TruckDetialsATXT.requestFocus();
            PTD_TruckDetialsATXT.showDropDown();
            return false;
        });

        PTD_TruckDetialsATXT.setOnItemClickListener((parent, view, position, id) -> {
            try {
                String item = (String) parent.getItemAtPosition(position);
                if (!isNullOrEmpty(item)) {
                    truckModelArray = new TruckDetailsModel();
                    truckModelArray = mDBExternalHelper.getOneTruckDetails(item);
                    if (truckModelArray.getIsBlocked() == 1) {
                        AlertDialogBox("Verification", "Selected truck is blocked, please contact admin", false);
                        PTD_TruckDetialsATXT.setText("");
                    } else {
                        String truckStr = truckModelArray.getTruckLicensePlateNo();
                        Common.TrucklicensePlateNo = truckModelArray.getTruckLicensePlateNo();
                        Common.TransportId = truckModelArray.getTransportId();
                        PTD_TruckDetialsATXT.setText(truckStr);
                    }
                }
            } catch (Exception ex) {

            }
        });
    }

    private void GetTransferScannedResultList() {
        try {
            Common.InventorytransferScannedResultList.clear();
            Common.InventorytransferScannedResultList = mDBInternalHelper.getInventoryTransferWithPurchaseID(Common.Purchase.SelectedPurchaseId);
            //String TotalSannedSize = mDBInternalHelper.ColculateInventoryTransferItems(Common.TransferID);
            Double RemoveVolumeSum = 0.00;
            if (Common.InventorytransferScannedResultList.size() > 0) {
                ptd_adapter = new PurchaseDetailsTransferAdapter(Common.InventorytransferScannedResultList, Common.QulaityDefaultList, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                PTD_ScannedResultLV.setLayoutManager(horizontalLayoutManager);
                ptd_adapter.notifyDataSetChanged();
                PTD_ScannedResultLV.setAdapter(ptd_adapter);
                PTD_ScannedResultLV.setVisibility(View.VISIBLE);
                PTD_NovalueFound.setVisibility(View.GONE);
                RemoveVolumeSum = TotalVolume(Common.InventorytransferScannedResultList);
            } else {
                PTD_NovalueFound.setVisibility(View.VISIBLE);
                PTD_ScannedResultLV.setVisibility(View.GONE);
                // Update StartDate
                Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                mDBInternalHelper.UpdatePurchaseAgreementTransferStartDate(Common.DateTime, Common.Purchase.SelectedPurchaseId);
            }
            PTD_TotalScannedCount.setText(String.valueOf(Common.InventorytransferScannedResultList.size()));
            PTD_VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(RemoveVolumeSum)));
        } catch (Exception ex) {
            Log.d("Exception : %s", ex.toString());
        }
    }

    public double TotalVolume(ArrayList<InventoryTransferScannedResultModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (InventoryTransferScannedResultModel inventoTransScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(inventoTransScanModel.getVolume());
        }
        return TotVolume;
    }

    public class PurchaseDetailsTransferAdapter extends RecyclerView.Adapter<PurchaseDetailsTransferAdapter.GroceryViewHolder> {
        private List<InventoryTransferScannedResultModel> ScannedResultList;
        private List<String> QualitySpinner;
        Context context;

        public PurchaseDetailsTransferAdapter(List<InventoryTransferScannedResultModel> ScannedResultList, ArrayList<String> SpinnerData, Context context) {
            this.QualitySpinner = SpinnerData;
            this.ScannedResultList = ScannedResultList;
            this.context = context;
        }

        public void removeItem(int position) {
            ScannedResultList.remove(position);
            notifyItemRemoved(position);
        }

        public InventoryTransferScannedResultModel getItem(int position) {
            return ScannedResultList.get(position);
        }

        @Override
        public PurchaseDetailsTransferAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transfer_result_infliator, parent, false);
            PurchaseDetailsTransferAdapter.GroceryViewHolder gvh = new PurchaseDetailsTransferAdapter.GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(PurchaseDetailsTransferAdapter.GroceryViewHolder holder, final int position) {
            holder.Barcode.setText(ScannedResultList.get(position).getBarCode());
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
            holder.Remove.setOnClickListener(v -> {
                if (Common.Purchase.PurchaseTransferIsEditorViewFlag == false) {
                    AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Can not edit Or delete after Synced", false);
                } else {
                    Common.RemoveSBBLabel = "";
                    Common.RemoveSBBLabel = ScannedResultList.get(position).getBarCode();
                    PTD_RemoveMessage(CommonMessage(R.string.Remove_Message));
                }
            });
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, QualitySpinner);
            holder.QualitySpinner.setAdapter(adapter);
            if (ScannedResultList.get(position).getQualitiy() != null) {
                int spinnerPosition = adapter.getPosition(ScannedResultList.get(position).getQualitiy());
                holder.QualitySpinner.setSelection(spinnerPosition);
            }
            if (Common.Purchase.PurchaseTransferIsEditorViewFlag == false) {
                holder.QualitySpinner.setEnabled(false);
            } else {
                holder.QualitySpinner.setEnabled(true);
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

    public void PTD_RemoveMessage(String ErrorMessage) {
        if (LogsRemoveAlert != null && LogsRemoveAlert.isShowing()) {
            return;
        }
        LogsRemovebuilder = new AlertDialog.Builder(this);
        LogsRemovebuilder.setMessage(ErrorMessage);
        LogsRemovebuilder.setCancelable(true);
        LogsRemovebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                (dialog, id) -> dialog.cancel());
        LogsRemovebuilder.setNegativeButton(CommonMessage(R.string.action_Remove),
                (dialog, id) -> {
                    try {
                        boolean Isdelete = mDBInternalHelper.RemovePurchasetransferlistview(Common.RemoveSBBLabel, 0, Common.Purchase.SelectedPurchaseId);
                        if (Isdelete == true) {
                            Toast.makeText(this, "Successfully Removed from List", Toast.LENGTH_LONG).show();
                            GetTransferScannedResultList();
                        }
                    } catch (Exception Ex) {

                    }
                    dialog.cancel();
                });

        LogsRemoveAlert = LogsRemovebuilder.create();
        LogsRemoveAlert.show();
    }

    public void PTD_ScannedResult() {
        Common.HideKeyboard(this);
        try {
            Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
            Common.IsActive = 1;
            boolean EmptyNullFlags = mDBInternalHelper.getInventoryTransferduplicateCheckForPurchaseID(Common.Purchase.SelectedPurchaseId, Common.BarCode);
            if (EmptyNullFlags == true) {
                wronBuzzer.start();
                AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Already exsist please try some other logs", false);
                //Scanned Result Refresh
                GetTransferScannedResultList();
                return;
            }
            Common.SearchedExternalLogsDetils = mDBExternalHelper.getBarCodeExternalLogDetails(Common.BarCode);
            if (Common.SearchedExternalLogsDetils.size() > 0) {
                Common.WoodSpieceID = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getWoodSpeciesId());
                Common.WoodSpieceCode = Common.SearchedExternalLogsDetils.get(0).getWoodSpeciesCode();
                Common.Length = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getLength_dm());
                Common.Volume = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getVolume());
                Common.FellingSectionId = Common.SearchedExternalLogsDetils.get(0).getFellingSectionID();
                Common.TreeNumber = Common.SearchedExternalLogsDetils.get(0).getTreeNumber();
                Common.QualityName = Common.SearchedExternalLogsDetils.get(0).getQuality();
                Common.HoleVolume = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getHoleVolume());
                Common.GrossVolume = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getGrossVolume());
                Common.IsSBBLabelCorrected = false;
                // According to purchase id checking wood speice code
                boolean WSCNotEqualFlags = mDBInternalHelper.CheckWSCForPurchaseID(Common.WoodSpieceCode, Common.Purchase.SelectedPurchaseId);
                if (WSCNotEqualFlags == false) {
                    wronBuzzer.start();
                    AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "WSC not match with purchase number", false);
                    return;
                }
            } else {
                wronBuzzer.start();
                AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Barcode not belongs to external purchase, please try another one", false);
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
                return;
            }
            InsertPurchaseTransferScannedTable();
            Common.EntryMode = 1;
            Common.ScannedEditTXTFlag = false;
            PTD_ScanValueETxT.setText("");
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void InsertPurchaseTransferScannedTable() {
        beepsound.start();
        // Insert values into DB
        boolean insertFlag = mDBInternalHelper.insertPurchaseTransferLogsResult(Common.VBB_Number, Common.TransferUniqueID, Common.FromLocationname, Common.ToLocationName,
                Common.SbbLabel, Common.BarCode, Common.Length, Common.Volume, Common.UserID, Common.DateTime, Common.WoodSpieceID,
                Common.WoodSpieceCode, Common.EntryMode, Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), "", Common.FellingSectionId,
                Common.TreeNumber, Common.QualityName, Common.HoleVolume, Common.GrossVolume, Common.Purchase.SelectedPurchaseId);
        if (insertFlag == true) {
            // Update header in purchase table
            /*mDBInternalHelper.UpdatePurchaseAgreementTransferHeader(Common.LoadedTypeID, Common.Purchase.SelectedTransFromLocationID,
                    Common.Purchase.SelectedTransToLocationID, Common.Purchase.SelectedTransAgencyID, Common.Purchase.SelectedTransDriverID,
                    Common.Purchase.SelectedTransTruckID, Common.Purchase.SelectedPurchaseId, Common.DateTime, Common.TransportTypeId);*/
            GetTransferScannedResultList();
            UpdateTransferIDList();
        } else {
            AlertDialogBox("InsertPurchaseTransferScannedTable", "Value Not Inserted", false);
        }
    }

    public void UpdateTransferIDList() {
        try {
            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            Double RemoveVolumeSum = TotalVolume(Common.InventorytransferScannedResultList);
            // Update values into TransferID
            boolean TransferIDFlag = mDBInternalHelper.UpdatePurchaseTransferListByID(Common.EndDate, Common.ToLocaTransID, Common.FromTransLocID, Common.TransportTypeId,
                    Common.TransferAgencyID, Common.DriverID, Common.UserID, Common.InventorytransferScannedResultList.size(),
                    Common.TransferID, String.valueOf(RemoveVolumeSum), 1, Common.TransferUniqueID, Common.LoadedTypeID, Common.TransportId);

        } catch (Exception ex) {
            AlertDialogBox("UpdateTransferIDList", ex.toString(), false);
        }
    }

    public void InventoryTransferSync(PurchaseTransferModel transferSyncList) {
        Common.InventoryTransferInputList.clear();
        Common.InventoryTransferInputList = mDBInternalHelper.getTransferScannedResultInputWithPurchaseID(Common.Purchase.SelectedPurchaseId, transferSyncList.getPurchaseTransferUniqueID());
        if (Common.InventoryTransferInputList.size() > 0) {
            if (checkConnection() == true) {
                getInventoryTransferSyncStatusApi(transferSyncList);
            }
        } else {
            AlertDialogBox(CommonMessage(R.string.PurchaseTransferHead), "Values are empty", false);
        }
    }

    // 2-12-2019 Added InventoryTransferSyncStatusApi impl
    private void getInventoryTransferSyncStatusApi(PurchaseTransferModel transferSyncList) {
        try {
            //ProgressBarLay.setVisibility(View.VISIBLE);
            transferSyncModel = new InventoryTransferSyncModel();
            transferSyncModel.setTransferID(0);
            transferSyncModel.setVBBNumber("0");
            transferSyncModel.setIMEINumber(Common.IMEI);
            transferSyncModel.setStartTime(transferSyncList.getStartDateTime());
            transferSyncModel.setEndTime(transferSyncList.getEndDateTime());
            transferSyncModel.setUserID(Common.UserID);
            transferSyncModel.setTransferUniqueID(transferSyncList.getPurchaseTransferUniqueID());
            transferSyncModel.setLocationID(transferSyncList.getFromLocationID());
            transferSyncModel.setToLocationID(transferSyncList.getToLocationID());

            transferSyncModel.setAgencyName(" ");
            transferSyncModel.setDriverName(" ");
            transferSyncModel.setTruckPlateNumber(mDBExternalHelper.getAllTruckNames(transferSyncList.getTruckId()));

            transferSyncModel.setTransferAgencyId(transferSyncList.getTransferAgencyID());
            transferSyncModel.setDriverId(transferSyncList.getDriverID());
            transferSyncModel.setTruckId(transferSyncList.getTruckId());
            transferSyncModel.setLoadedTypeID(transferSyncList.getLoadedTypeID());
            transferSyncModel.setTransferModeID(transferSyncList.getTransportTypeId());

            transferSyncModel.setHHInventoryTransfer(Common.InventoryTransferInputList);
            transferSyncModel.setTranferredCount(Common.InventoryTransferInputList.size());
            //transferSyncModel.setPurchaseId(transferSyncList.getPurchaseId());

            InsertExport = ApiClient.getApiInterface();
            InsertExport.getPurchaseTransferSync(transferSyncModel).enqueue(new Callback<InventoryTransferSyncModel>() {
                @Override
                public void onResponse(Call<InventoryTransferSyncModel> call, Response<InventoryTransferSyncModel> response) {
                    try {
                        //ProgressBarLay.setVisibility(View.GONE);
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.isSuccessful()) {
                                Common.SyncStatusList.clear();
                                Common.SyncStatusList.addAll(response.body().getStatus());
                                if (Common.SyncStatusList.get(0).getStatus() == 1) {
                                    Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                                    Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                                    boolean ListIdFlag = mDBInternalHelper.UpdatePurchaseTransferSyncStatus(Common.SyncTime, 1, transferSyncList.getPurchaseTransferUniqueID(),
                                            Common.SyncStatusList.get(0).getTransferAgencyId(), Common.SyncStatusList.get(0).getDriverId(), Common.SyncStatusList.get(0).getTruckId());
                                    if (ListIdFlag == true) {
                                        //Scanned Result Refresh
                                        GetPurchaseTransferDateList();
                                        AlertDialogBox(CommonMessage(R.string.PurchaseTransferHead), "#" + String.valueOf(Common.Purchase.SelectedPurchaseId) + "--" + Common.SyncStatusList.get(0).getMessage(), true);
                                        if (Common.SyncStatusList.get(0).getTransferAgencyId() != 0 || Common.SyncStatusList.get(0).getDriverId() != 0 || Common.SyncStatusList.get(0).getTruckId() != 0) {
                                            Common.IsExternalSyncFlag = true;
                                            Common.IsProgressVisible = false;
                                            Common.ExternalSyncFlag = false;
                                            gwwMain.ExternalDataBaseSync();
                                        }
                                    }
                                } else {
                                    AlertDialogBox(CommonMessage(R.string.PurchaseTransferHead), "#" + Common.TransferID + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                                }
                            } else {
                                AlertDialogBox(CommonMessage(R.string.PurchaseTransferHead), "#" + Common.TransferID + "--" + "Not Synced", false);
                            }
                        } else {
                            Common.AuthorizationFlag = true;
                            AlertDialogBox(CommonMessage(R.string.PurchaseTransferHead), response.message(), false);
                        }

                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        AlertDialogBox(CommonMessage(R.string.PurchaseTransferHead), "#" + Common.TransferID + "--" + ex.getMessage(), false);
                    }
                }

                @Override
                public void onFailure(Call<InventoryTransferSyncModel> call, Throwable t) {
                    //ProgressBarLay.setVisibility(View.GONE);
                    AlertDialogBox(CommonMessage(R.string.PurchaseTransferHead), "#" + Common.TransferID + "--" + t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            //ProgressBarLay.setVisibility(View.GONE);
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox(CommonMessage(R.string.PurchaseTransferHead), ex.toString(), false);
        }
    }

    // Received List
    public void ReceivedList() {
        Common.HideKeyboard(this);
        Common.InventReceivedDateSelectedIndex = 0;
        GetPurchaseReceivedDateList();
    }

    public void GetPurchaseReceivedDateList() {
        try {
            Common.Purchase.Filter_PurchaseReceivedDate.clear();
            Common.Purchase.Filter_PurchaseReceivedDate = mDBInternalHelper.getPurchaseReceivedDate(Common.Purchase.SelectedPurchaseId);
            if (Common.Purchase.Filter_PurchaseReceivedDate.size() > 0) {
                receivedDateAdapter = new ReceivedDateAdapter(Common.Purchase.Filter_PurchaseReceivedDate, this);
                ReceivedDateLayoutManager = new LinearLayoutManager(this);
                receivedDateAdapter.notifyDataSetChanged();
                PRL_ReceivedDateList.setLayoutManager(ReceivedDateLayoutManager);
                PRL_ReceivedList.setVisibility(View.VISIBLE);
                PRL_ReceivedDateList.setVisibility(View.VISIBLE);
                PRL_ReceivedDateList.setAdapter(receivedDateAdapter);
                GetPurchaseReceivedList(Common.Purchase.Filter_PurchaseReceivedDate.get(Common.InventReceivedDateSelectedIndex), Common.Purchase.SelectedPurchaseId);
            } else {
                PRL_ReceivedList.setVisibility(View.GONE);
                PRL_ReceivedDateList.setVisibility(View.GONE);
                PRL_TotalFilteredCount.setText("0");
                PRL_TotalFilteredVolume.setText("0.00");
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public class ReceivedDateAdapter extends RecyclerView.Adapter<ReceivedDateAdapter.ReceivedViewHolder> {
        private List<String> InventoryReceivedDateList;
        Context context;

        public ReceivedDateAdapter(List<String> InventoryReceivedDateList, Context context) {
            this.InventoryReceivedDateList = InventoryReceivedDateList;
            this.context = context;
        }

        @Override
        public ReceivedDateAdapter.ReceivedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_count_dates_infliator, parent, false);
            ReceivedDateAdapter.ReceivedViewHolder gvh = new ReceivedDateAdapter.ReceivedViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return InventoryReceivedDateList.size();
        }

        @Override
        public void onBindViewHolder(ReceivedDateAdapter.ReceivedViewHolder holder, final int position) {
            //holder.TransferIDTXT.setText(String.valueOf(position + 1));
            if (Common.Purchase.PurcahseReceivedDateSelectedIndex == position) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.orange));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.colorgGreenSmooth));
            }
            holder.TransferDateTXT.setText(String.valueOf(InventoryReceivedDateList.get(position)));

            holder.Background.setOnClickListener(v -> {
                Common.Purchase.PurcahseReceivedDateSelectedIndex = position;
                notifyDataSetChanged();
                GetPurchaseReceivedList(String.valueOf(InventoryReceivedDateList.get(Common.Purchase.PurcahseReceivedDateSelectedIndex)), Common.Purchase.SelectedPurchaseId);
            });

        }

        public class ReceivedViewHolder extends RecyclerView.ViewHolder {
            TextView TransferDateTXT;
            LinearLayout Background;

            public ReceivedViewHolder(View view) {
                super(view);
                TransferDateTXT = view.findViewById(R.id.textview_dates);
                Background = view.findViewById(R.id.FilterDateBackGround);
            }
        }
    }

    private void GetPurchaseReceivedList(String SelectedDate, int purcahseID) {
        try {
            Common.Purchase.PurchaseReceivedList.clear();
            Common.Purchase.PurchaseReceivedList = mDBInternalHelper.getPurchaseReceivedIdList(SelectedDate, purcahseID);
            if (Common.Purchase.PurchaseReceivedList.size() > 0) {
                receivedListAdapter = new ReceivedListAdapter(Common.Purchase.PurchaseReceivedList, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                receivedListAdapter.notifyDataSetChanged();
                PRL_ReceivedList.setLayoutManager(horizontalLayoutManager);
                PRL_ReceivedList.setAdapter(receivedListAdapter);
                //NoValueFoundTxT.setVisibility(View.GONE);
                PRL_ReceivedList.setVisibility(View.VISIBLE);
            } else {
                PRL_ReceivedList.setVisibility(View.GONE);
                //NoValueFoundTxT.setVisibility(View.VISIBLE);
            }
            PRL_TotalFilteredCount.setText(String.valueOf(Common.Purchase.PurchaseReceivedList.size()));
            PRL_TotalFilteredVolume.setText(String.valueOf(mDBInternalHelper.TotalVolumeForInventoryReceive(SelectedDate)));
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public class ReceivedListAdapter extends RecyclerView.Adapter<ReceivedListAdapter.ReceivedViewHolder> {
        private List<PurchaseReceivedModel> ReceivedList;
        Context context;

        public ReceivedListAdapter(List<PurchaseReceivedModel> ScannedResultList, Context context) {
            this.ReceivedList = ScannedResultList;
            this.context = context;
        }

        @Override
        public ReceivedListAdapter.ReceivedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.puchase_receivedlist_infliator, parent, false);
            ReceivedListAdapter.ReceivedViewHolder gvh = new ReceivedListAdapter.ReceivedViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return ReceivedList.size();
        }

        @Override
        public void onBindViewHolder(ReceivedListAdapter.ReceivedViewHolder holder, final int position) {
            //holder.ReceivedIDTXT.setText(String.valueOf(position + 1));
            holder.ReceivedIDTXT.setText(String.valueOf(ReceivedList.get(position).getID()));
            holder.TransferIDTXT.setText(String.valueOf(ReceivedList.get(position).getID()));
            holder.ImeiTXT.setText(ReceivedList.get(position).getIMEI());
            holder.StartTimeTXT.setText(ReceivedList.get(position).getStartDateTime());
            holder.EndTimeTXT.setText(ReceivedList.get(position).getEndDateTime());
            holder.CountTXT.setText(String.valueOf(ReceivedList.get(position).getCount()));
            holder.VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(ReceivedList.get(position).getVolume())));
            holder.SyncStatusTXT.setText(String.valueOf(ReceivedList.get(position).getSyncStatus()));
            if (ReceivedList.get(position).getSyncStatus() == 1) {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.red));
            }
            holder.SyncTimeTXT.setText(ReceivedList.get(position).getSyncTime());
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            if (ReceivedList.get(position).getCount() > 0) {
                holder.syncTXT.setVisibility(View.VISIBLE);
            } else {
                holder.syncTXT.setVisibility(View.INVISIBLE);
            }
            holder.syncTXT.setOnClickListener(v -> {
                //Sync Date to BackEnd Common.ToLocReceivedID
                try {
                    Common.SyncStartDateTime = ReceivedList.get(position).getStartDateTime();
                    Common.SyncEndDateTime = ReceivedList.get(position).getEndDateTime();
                    Common.ToLocReceivedID = ReceivedList.get(position).getToLocationID();
                    Common.TransferID = ReceivedList.get(position).getTransferID();
                    Common.ReceivedID = ReceivedList.get(position).getReceivedID();
                    Common.SyncBarCodeCount = ReceivedList.get(position).getCount();
                    Common.FromLocationID = ReceivedList.get(position).getFromLocationID();
                    //Common.TrucklicensePlateNo = ReceivedList.get(position).getTruckPlateNumber();
                    Common.TransportTypeId = ReceivedList.get(position).getTransportTypeId();
                    Common.TransferAgencyID = ReceivedList.get(position).getTransferAgencyID();
                    Common.DriverID = ReceivedList.get(position).getDriverID();
                    if (Common.TransferAgencyID == 0) {
                        Common.AgencyName = "";
                    } else {
                        Common.AgencyName = mDBExternalHelper.getAgencyName(Common.TransferAgencyID);
                    }
                    if (Common.DriverID == 0) {
                        Common.DriverName = "";
                    } else {
                        Common.DriverName = mDBExternalHelper.getAllDriverName(Common.DriverID);
                    }
                    if (ReceivedList.get(position).getTruckId() == 0) {
                        Common.TransportId = 0;
                        Common.TrucklicensePlateNo = "";
                    } else {
                        Common.TransportId = ReceivedList.get(position).getTruckId();
                        Common.TrucklicensePlateNo = mDBExternalHelper.getAllTruckNames(Common.TransportId);
                    }
                    InventoryReceivedSync(ReceivedList.get(position));
                } catch (Exception ex) {
                    CrashAnalytics.CrashReport(ex);
                    AlertDialogBox("Received Sync", ex.toString(), false);
                }
            });
            holder.DeleteIMG.setOnClickListener(v -> {
                try {
                    Common.ReceivedID = ReceivedList.get(position).getID();
                    //Remove From Transfer Lsit and Scanned list
                    if (ReceivedList.get(position).getSyncStatus() == 1) {
                        DeleteReceivedListannReceivedScannedList(Common.ReceivedID, Common.Purchase.SelectedPurchaseId);
                    } else {
                        if (ReceivedList.get(position).getCount() < 5) {
                            DeleteReceivedListannReceivedScannedList(Common.ReceivedID, Common.Purchase.SelectedPurchaseId);
                            return;
                        }
                        AlertDialogBox("InventoryReceived", "This is not Syncked yet", false);
                    }
                } catch (Exception ex) {
                    CrashAnalytics.CrashReport(ex);
                    AlertDialogBox("Transfer Sync", ex.toString(), false);
                }
            });

            holder.Background.setOnLongClickListener(v -> {
                try {
                    Common.ReceivedID = ReceivedList.get(position).getID();
                    Common.ReceivedUniqueID = ReceivedList.get(position).getPurchaseReceivedUniqueID();
                    Common.ReceivedDate = ReceivedList.get(position).getStartDateTime();
                    Common.Count = ReceivedList.get(position).getCount();
                    Common.VolumeSum = ReceivedList.get(position).getVolume();
                    Common.ReceivedLoadedTypeID = ReceivedList.get(position).getLoadedTypeID();
                    try {
                        Common.ReceivedLoadedTypeName = "";
                        Common.LoadedByList.clear();
                        Common.LoadedByList = mDBExternalHelper.getAllLoadedByDetails();
                        Common.ReceivedLoadedTypeName = mDBExternalHelper.getAllLoadedNames(Common.ReceivedLoadedTypeID);
                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                    }
                    Common.TransferReciveUniqueID = ReceivedList.get(position).getTransferUniqueID();
                    if (!isNullOrEmpty(Common.TransferReciveUniqueID)) {
                        if (Common.TransferReciveUniqueID.length() > 9) {
                            int TrUniIDInd = (Common.TransferReciveUniqueID.length() - 10);
                            Common.ReceivedTransferID = Common.TransferReciveUniqueID.substring(Common.TransferReciveUniqueID.length() - TrUniIDInd);
                        } else {
                            Common.ReceivedTransferID = Common.TransferReciveUniqueID;
                        }
                    } else {
                        Common.ReceivedTransferID = "";
                    }

                    Common.Purchase.SelectedReceivedFromLocationID = ReceivedList.get(position).getFromLocationID();
                    Common.Purchase.SelectedReceivedToLocationID = ReceivedList.get(position).getToLocationID();
                    Common.Purchase.SelectedReceivedDriverID = ReceivedList.get(position).getDriverID();
                    Common.Purchase.SelectedReceivedAgencyID = ReceivedList.get(position).getTransferAgencyID();
                    Common.Purchase.SelectedReceivedTruckID = ReceivedList.get(position).getTruckId();
                    Common.Purchase.SelectedReceivedTransPortMode = ReceivedList.get(position).getTransportTypeId();

                    Common.RecFromLocationID = ReceivedList.get(position).getFromLocationID();
                    Common.RecFromLocationname = mDBExternalHelper.getFromLocationName(Common.RecFromLocationID);
                    Common.ToLocReceivedID = ReceivedList.get(position).getToLocationID();
                    Common.ToLocationName = mDBExternalHelper.getToLocationName(Common.ToLocReceivedID);

                    Common.DriverID = ReceivedList.get(position).getDriverID();
                    Common.TransportTypeId = ReceivedList.get(position).getTransportTypeId();
                    Common.TransportMode = mDBExternalHelper.getAllTransPortMode(Common.TransportTypeId);
                    Common.TransferAgencyID = ReceivedList.get(position).getTransferAgencyID();
                    if (Common.TransferAgencyID == 0) {
                        Common.AgencyName = "";
                    } else {
                        Common.AgencyName = mDBExternalHelper.getAgencyName(Common.TransferAgencyID);
                    }
                    if (Common.DriverID == 0) {
                        Common.DriverName = "";
                    } else {
                        Common.DriverName = mDBExternalHelper.getAllDriverName(Common.DriverID);
                    }
                    if (ReceivedList.get(position).getTruckId() == 0) {
                        Common.TransportId = 0;
                        Common.TrucklicensePlateNo = "";
                    } else {
                        Common.TransportId = ReceivedList.get(position).getTruckId();
                        Common.TrucklicensePlateNo = mDBExternalHelper.getAllTruckNames(Common.TransportId);
                    }

                    if (ReceivedList.get(position).getSyncStatus() == 0) {
                        Common.Purchase.PurchaseReceivedIsEditorViewFlag = true;
                    } else {
                        Common.Purchase.PurchaseReceivedIsEditorViewFlag = false;
                    }
                    Common.Purchase.IsReceivedEditorViewFlag = false;
                    ReceivedDetails();
                    scanPurchase.setVisibility(View.VISIBLE);
                    PRL_ListLAY.setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                    CrashAnalytics.CrashReport(ex);
                    AlertDialogBox("Received Background", ex.toString(), false);
                }
                return false;
            });

            holder.printIMG.setOnClickListener(v -> {
                try {
                    Common.ReceivedID = ReceivedList.get(position).getID();
                    Common.ReceivedUniqueID = ReceivedList.get(position).getPurchaseReceivedUniqueID();
                    Common.ReceivedLoadedTypeID = ReceivedList.get(position).getLoadedTypeID();
                    try {
                        Common.ReceivedLoadedTypeName = "";
                        Common.LoadedByList.clear();
                        Common.LoadedByList = mDBExternalHelper.getAllLoadedByDetails();
                        Common.ReceivedLoadedTypeName = mDBExternalHelper.getAllLoadedNames(Common.ReceivedLoadedTypeID);
                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                    }
                    Common.InventoryReceivedScannedResultList.clear();
                    Common.InventoryReceivedScannedResultList = mDBInternalHelper.getPurchaseReceivedPrintout(Common.ReceivedID);
                    if (Common.InventoryReceivedScannedResultList.size() > 0) {
                        Common.TransferReciveUniqueID = Common.InventoryReceivedScannedResultList.get(0).getTransferUniqueID();
                        if (!isNullOrEmpty(Common.TransferReciveUniqueID)) {
                            int TrUniIDInd = (Common.TransferReciveUniqueID.length() - 10);
                            Common.ReceivedTransferID = Common.TransferReciveUniqueID.substring(Common.TransferReciveUniqueID.length() - TrUniIDInd);
                        } else {
                            Common.ReceivedTransferID = "";
                        }
                        Common.RecFromLocationID = ReceivedList.get(position).getFromLocationID();
                        Common.RecFromLocationname = mDBExternalHelper.getFromLocationName(Common.RecFromLocationID);
                        Common.ToLocReceivedID = ReceivedList.get(position).getToLocationID();
                        Common.ToLocationName = mDBExternalHelper.getToLocationName(Common.ToLocReceivedID);
                        Common.DriverID = ReceivedList.get(position).getDriverID();
                        Common.TransportTypeId = ReceivedList.get(position).getTransportTypeId();
                        Common.TransportMode = mDBExternalHelper.getAllTransPortMode(Common.TransportTypeId);
                        Common.TransferAgencyID = ReceivedList.get(position).getTransferAgencyID();
                        if (Common.TransferAgencyID == 0) {
                            Common.AgencyName = "";
                        } else {
                            Common.AgencyName = mDBExternalHelper.getAgencyName(Common.TransferAgencyID);
                        }
                        if (Common.DriverID == 0) {
                            Common.DriverName = "";
                        } else {
                            Common.DriverName = mDBExternalHelper.getAllDriverName(Common.DriverID);
                        }
                        if (ReceivedList.get(position).getTruckId() == 0) {
                            Common.TransportId = 0;
                            Common.TrucklicensePlateNo = "";
                        } else {
                            Common.TransportId = ReceivedList.get(position).getTruckId();
                            Common.TrucklicensePlateNo = mDBExternalHelper.getAllTruckNames(Common.TransportId);
                        }
                        PrintSlip();
                    } else {
                        AlertDialogBox("Received PrintSlip", "No values found, try some other item", false);
                    }
                } catch (Exception ex) {
                    CrashAnalytics.CrashReport(ex);
                }
            });
        }

        public class ReceivedViewHolder extends RecyclerView.ViewHolder {
            TextView ReceivedIDTXT, TransferIDTXT, ImeiTXT, StartTimeTXT, EndTimeTXT, CountTXT, SyncStatusTXT, SyncTimeTXT, VolumeTXT;
            ImageView syncTXT, DeleteIMG, viewIMG, printIMG;
            LinearLayout Background;

            public ReceivedViewHolder(View view) {
                super(view);
                Background = view.findViewById(R.id.prl_receivedlayoutBackground);
                ReceivedIDTXT = view.findViewById(R.id.prl_received_IDTxT);
                TransferIDTXT = view.findViewById(R.id.prl_received_TransferTxT);
                ImeiTXT = view.findViewById(R.id.prl_received_imeiTxT);
                StartTimeTXT = view.findViewById(R.id.prl_received_startTimeTxT);
                EndTimeTXT = view.findViewById(R.id.prl_received_endTimeTxT);
                CountTXT = view.findViewById(R.id.prl_received_countTxT);
                SyncStatusTXT = view.findViewById(R.id.prl_received_syncStatusTxT);
                SyncTimeTXT = view.findViewById(R.id.prl_received_syncTimeTxT);
                syncTXT = view.findViewById(R.id.prl_received_syncIMG);
                VolumeTXT = view.findViewById(R.id.prl_received_VolumeTxT);
                DeleteIMG = view.findViewById(R.id.prl_received_Delete);
                viewIMG = view.findViewById(R.id.prl_received_view);
                printIMG = view.findViewById(R.id.prl_printSlip_IMG);
            }
        }
    }

    public void ReceivedDetails() {
        ViewReceivedList(Common.Purchase.PurchaseReceivedIsEditorViewFlag);
        ReceivedToLocationsforSpinner();
        driverforSpinner();
        agencyforSpinner();
        truckforSpinner();
        transModeforView();
        PRD_Click_Listener();
        GetReceivedScannedResultList();
        if (Common.Purchase.IsReceivedEditorViewFlag == false) {
            PRD_ToLocationTxT.setText(Common.ToLocationName);
            QrScanDetails();
            PRD_TransferIDAutoTXT.setText(Common.TransferReciveUniqueID);
        } else {
            PRD_FromLocationATXT.setText("");
            PRD_AgencyATXT.setText("");
            PRD_DriverATXT.setText("");
            PRD_TruckDetialsATXT.setText("");
        }
        Common.HideKeyboard(this);
        PRD_ScanValueETxT.setText("");
    }

    public void ViewReceivedList(boolean EdtFlag) {
        if (EdtFlag == false) {
            PRD_EditOptionFlagLay.setVisibility(View.VISIBLE);
            PRD_ScanValueETxT.setEnabled(false);
            findViewById(R.id.prd_Received_EnterIMG).setEnabled(false);
            PRD_EditOptionFlagLay2.setVisibility(View.VISIBLE);
            PRD_TransferIDAutoTXT.setEnabled(false);
        } else {
            PRD_EditOptionFlagLay.setVisibility(View.GONE);
            PRD_ScanValueETxT.setEnabled(true);
            findViewById(R.id.prd_Received_EnterIMG).setEnabled(true);
            PRD_EditOptionFlagLay2.setVisibility(View.GONE);
            PRD_TransferIDAutoTXT.setEnabled(true);
        }
    }

    public void PRD_Click_Listener() {

        PRD_ScanValueETxT.setOnTouchListener((v, event) -> {
            Common.ScannedEditTXTFlag = true;
            return false;
        });

        PRD_receivedDetails_close.setOnClickListener(v -> {
            PRD_receivedDetails_layout.setVisibility(View.GONE);
        });

        PRD_receivedDetails_open.setOnClickListener(v -> {
            //ReceiveingLogsDetails();
        });

        PRD_ScanValueETxT.addTextChangedListener(new TextWatcher() {
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
                            PRD_ScanValueETxT.setError("Barcode should be like(XX-1111111)");
                        }
                    }
                } else {
                    if (editable.toString().length() > 0) {
                        String last = editable.toString().substring(editable.toString().length() - 1);
                        if (last.equals("-")) {

                        } else {
                            if (editable.toString().length() == 2) {
                                PRD_ScanValueETxT.append("-");
                            }
                            if (editable.toString().length() == Common.SBBlenght) {
                                if (!isValidBarCode(editable.toString())) {
                                    PRD_ScanValueETxT.setError("Barcode should be like(XX-1111111)");
                                }
                            } else {
                                if (Common.ScannedEditTXTFlag == true) {
                                    PRD_ScanValueETxT.setError(CommonMessage(R.string.BarCodeLenghtMsg));
                                }
                            }
                        }
                    }
                }
            }
        });

        PRD_TransferMode_LV.setOnItemClickListener((parent, view, position, id) -> {
            Common.TransportTypeId = Common.TransportModeList.get(position).getTransportTypeId();
            Common.TransportMode = Common.TransportModeList.get(position).getTransportMode();
        });

        PRD_TransferIDAutoTXT.addTextChangedListener(new TextWatcher() {
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
                        AlertDialogBox("Scanned Value", "Do not Change Transfer ID while Scanning, if you change please give correct Transport Id", false);
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

        PRD_EditOptionFlagLay.setOnClickListener(v -> AlertDialogBox(CommonMessage(R.string.purchaseReceivedHead), "Can not edit Or delete after Synced", false));

        PRD_EditOptionFlagLay2.setOnClickListener(v -> AlertDialogBox(CommonMessage(R.string.purchaseReceivedHead), "Can not edit Or delete after Synced", false));

        /*AutoComplete*/
        PRD_FromLocationATXT.setOnTouchListener((v, event) -> {
            //PRD_FromLocationATXT.requestFocus();
            PRD_FromLocationATXT.showDropDown();
            Common.HideKeyboard(this);
            return false;
        });

        PRD_FromLocationATXT.setOnItemClickListener((parent, view, position, id) -> {
            String locModelStr = (String) parent.getItemAtPosition(position);
            if (!isNullOrEmpty(locModelStr)) {
                for (ConcessionNamesModel locModel : Common.ConcessionList)
                    if (locModelStr.equals(locModel.getConcessionName())) {
                        Common.RecFromLocationID = locModel.getFromLocationId();
                        Common.RecFromLocationname = locModel.getConcessionName();
                        PRD_FromLocationATXT.setText(Common.RecFromLocationname);
                    }
            }
        });

        PRD_DriverATXT.setOnTouchListener((v, event) -> {
            PRD_DriverATXT.requestFocus();
            PRD_DriverATXT.showDropDown();
            return false;
        });

        PRD_DriverATXT.setOnItemClickListener((parent, view, position, id) -> {
            //String driverModelStr = (String) parent.getItemAtPosition(position);
            try {
                String item = (String) parent.getItemAtPosition(position);
                if (!isNullOrEmpty(item)) {
                    drivermodelArray = new DriverDetailsModel();
                    drivermodelArray = mDBExternalHelper.getOneDriverDetails(item);
                    if (drivermodelArray.getIsBlocked() == 1) {
                        AlertDialogBox("Verification", "Selected driver is blocked, please contact admin", false);
                        PRD_DriverATXT.setText("");
                    } else {
                        String driverModelStr = drivermodelArray.getDriverName();
                        Common.DriverID = drivermodelArray.getTruckDriverId();
                        Common.DriverName = drivermodelArray.getDriverName();
                        PRD_DriverATXT.setText(driverModelStr);
                    }
                }
            } catch (Exception ex) {

            }
        });

        PRD_AgencyATXT.setOnTouchListener((v, event) -> {
            PRD_AgencyATXT.requestFocus();
            PRD_AgencyATXT.showDropDown();
            return false;
        });

        PRD_AgencyATXT.setOnItemClickListener((parent, view, position, id) -> {
            //String agencyModelStr = (String) parent.getItemAtPosition(position);
            try {
                String item = (String) parent.getItemAtPosition(position);
                if (!isNullOrEmpty(item)) {
                    agencyModelArray = new AgencyDetailsModel();
                    agencyModelArray = mDBExternalHelper.getOneAgencyDetails(item);
                    if (agencyModelArray.getIsBlocked() == 1) {
                        AlertDialogBox("Verification", "Selected agent is blocked, please contact admin", false);
                        PRD_AgencyATXT.setText("");
                    } else {
                        String agencyModelStr = agencyModelArray.getAgencyName();
                        Common.AgencyName = agencyModelArray.getAgencyName();
                        Common.TransferAgencyID = agencyModelArray.getAgencyId();
                        PRD_AgencyATXT.setText(agencyModelStr);
                    }
                }
            } catch (Exception ex) {

            }
        });

        PRD_TruckDetialsATXT.setOnTouchListener((v, event) -> {
            PRD_TruckDetialsATXT.requestFocus();
            PRD_TruckDetialsATXT.showDropDown();
            return false;
        });

        PRD_TruckDetialsATXT.setOnItemClickListener((parent, view, position, id) -> {
            try {
                String item = (String) parent.getItemAtPosition(position);
                if (!isNullOrEmpty(item)) {
                    truckModelArray = new TruckDetailsModel();
                    truckModelArray = mDBExternalHelper.getOneTruckDetails(item);
                    if (truckModelArray.getIsBlocked() == 1) {
                        AlertDialogBox("Verification", "Selected truck is blocked, please contact admin", false);
                        PRD_TruckDetialsATXT.setText("");
                    } else {
                        String truckStr = truckModelArray.getTruckLicensePlateNo();
                        Common.TrucklicensePlateNo = truckModelArray.getTruckLicensePlateNo();
                        Common.TransportId = truckModelArray.getTransportId();
                        PRD_TruckDetialsATXT.setText(truckStr);
                    }
                }
            } catch (Exception ex) {

            }
        });
    }

    public void QrScanDetails() {
        if (Common.Purchase.IsReceivedEditorViewFlag == true) {
            if (MasterDataBaseCheckValidation(Common.RecFromLocationID, Common.TransferAgencyID, Common.DriverID, Common.TransportId, Common.TransportTypeId)) {
            } else {
                return;
            }
        }
        /*18-09-2019-Implemented Values for ID in header QR code*/
        PRD_FromLocationATXT.setText(Common.RecFromLocationname);
        PRD_AgencyATXT.setText(Common.AgencyName);
        PRD_DriverATXT.setText(Common.DriverName);
        PRD_TruckDetialsATXT.setText(Common.TrucklicensePlateNo);
        //transportAdapter.notifyDataSetChanged();
        //vbb_txt.setText(Common.VBB_Number);
        //UpdateReceivedIDList();
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

    public void MasterCheckAlertDialog(String ErrorMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ExternalPurchaseBottomBarActivity.this);
        builder1.setMessage(ErrorMessage);
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK",
                (dialog, id) -> {
                    PRD_receivedDetails_layout.setVisibility(View.GONE);
                    dialog.cancel();
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void GetReceivedScannedResultList() {
        try {
            Common.InventoryReceivedScannedResultList.clear();
            Common.InventoryReceivedScannedResultList = mDBInternalHelper.getInventoryReceivedWithPurchase(Common.Purchase.SelectedPurchaseId);
            Common.CheckedSize = mDBInternalHelper.ColculatePurchaseReceivedCheckedItems(Common.ReceivedUniqueID, "YES", Common.Purchase.SelectedPurchaseId);
            Double RemoveVolumeSum = 0.00;
            if (Common.InventoryReceivedScannedResultList.size() > 0) {
                receivedLogsadapter = new ReceivedLogsAdapter(Common.InventoryReceivedScannedResultList, Common.QulaityDefaultList, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                PRD_ScannedResultLV.setLayoutManager(horizontalLayoutManager);
                receivedLogsadapter.notifyDataSetChanged();
                PRD_ScannedResultLV.setAdapter(receivedLogsadapter);
                PRD_ScannedResultLV.setVisibility(View.VISIBLE);
                RemoveVolumeSum = ReceivedTotalVolume(Common.InventoryReceivedScannedResultList);
                PRD_receivedDetails_open.setVisibility(View.VISIBLE);
            } else {
                PRD_ScannedResultLV.setVisibility(View.GONE);
                PRD_receivedDetails_open.setVisibility(View.GONE);
            }
            PRD_TotalScannedCount.setText(String.valueOf(Common.InventoryReceivedScannedResultList.size()));
            PRD_VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(RemoveVolumeSum)));
            PRD_Received_TotalCheckedCountTXT.setText(String.valueOf(Common.CheckedSize));
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public double ReceivedTotalVolume(ArrayList<InventoryReceivedModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (InventoryReceivedModel inventoreceScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(inventoreceScanModel.getVolume());
        }
        return TotVolume;
    }

    public class ReceivedLogsAdapter extends RecyclerView.Adapter<ReceivedLogsAdapter.GroceryViewHolder> {
        private List<InventoryReceivedModel> ScannedResultList;
        private List<String> QualitySpinner;
        Context context;

        public ReceivedLogsAdapter(List<InventoryReceivedModel> ScannedResultList, ArrayList<String> SpinnerData, Context context) {
            this.QualitySpinner = SpinnerData;
            this.ScannedResultList = ScannedResultList;
            this.context = context;
        }

        public void removeItem(int position) {
            ScannedResultList.remove(position);
            notifyItemRemoved(position);
        }

        public InventoryReceivedModel getItem(int position) {
            return ScannedResultList.get(position);
        }

        @Override
        public ReceivedLogsAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_infliator, parent, false);
            ReceivedLogsAdapter.GroceryViewHolder gvh = new ReceivedLogsAdapter.GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(ReceivedLogsAdapter.GroceryViewHolder holder, final int position) {
            holder.Barcode.setText(ScannedResultList.get(position).getBarCode());
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
            holder.Remove.setOnClickListener(v -> {
                if (!Common.Purchase.IsScannrdEditorViewFlag) {
                    AlertDialogBox(CommonMessage(R.string.purchaseReceivedHead), "Can not edit Or delete after Synced", false);
                } else {
                    Common.RemoveSBBLabel = "";
                    Common.RemoveSBBLabel = ScannedResultList.get(position).getBarCode();
                    PRD_RemoveMessage(CommonMessage(R.string.Remove_Message));
                }
            });

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, QualitySpinner);
            holder.QualitySpinner.setAdapter(adapter);
            if (ScannedResultList.get(position).getQuality() != null) {
                int spinnerPosition = adapter.getPosition(ScannedResultList.get(position).getQuality());
                holder.QualitySpinner.setSelection(spinnerPosition);
            }
            if (!Common.Purchase.IsScannrdEditorViewFlag) {
                holder.QualitySpinner.setEnabled(false);
            } else {
                holder.QualitySpinner.setEnabled(true);
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

    public void PRD_RemoveMessage(String ErrorMessage) {
        if (LogsRemoveAlert != null && LogsRemoveAlert.isShowing()) {
            return;
        }
        LogsRemovebuilder = new AlertDialog.Builder(ExternalPurchaseBottomBarActivity.this);
        LogsRemovebuilder.setMessage(ErrorMessage);
        LogsRemovebuilder.setCancelable(true);
        LogsRemovebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                (dialog, id) -> dialog.cancel());
        LogsRemovebuilder.setNegativeButton(CommonMessage(R.string.action_Remove),
                (dialog, id) -> {
                    boolean Isdelete = mDBInternalHelper.RemovePurchaseReceivedlistview(Common.RemoveSBBLabel, 0, Common.Purchase.SelectedPurchaseId);
                    if (Isdelete == true) {
                        Toast.makeText(ExternalPurchaseBottomBarActivity.this, "Successfully Removed from List", Toast.LENGTH_LONG).show();
                        GetReceivedScannedResultList();
                    }
                    dialog.cancel();
                });

        LogsRemoveAlert = LogsRemovebuilder.create();
        LogsRemoveAlert.show();
    }

    public void PRL_ScannedResult(String scannedValue) {
        try {
            if (scannedValue != null && scannedValue.length() > 0) {
                Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                Common.IsActive = 1;
                if (Common.QRCodeScan == true) {
                    if (Common.QrBarCodeScan == true) {
                        String[] TransferBarcodeDetails = scannedValue.split("--");
                        String BarCodeValidation = TransferBarcodeDetails[1];
                        if (BarCodeValidation.length() > 10) {
                            ScanedQRBarcodeDetails(TransferBarcodeDetails);
                        } else {
                            wronBuzzer.start();
                            AlertDialogBox("Scanned Value", "Please Scan Valid Barcode Details", false);
                            return;
                        }
                    } else {
                        String[] TransferDetails = scannedValue.split("--");
                        String DetailsValidation = TransferDetails[1];
                        //if (DetailsValidation.length() == 6 || DetailsValidation.length() == 0) {
                        if (isNullOrEmpty(DetailsValidation)) {
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
                    // Checked Duplicate In Internal Tabel
                    boolean EmptyNullFlags = mDBInternalHelper.getInventoryReceivedduplicateCheckForPurchaseID(Common.Purchase.SelectedPurchaseId, Common.BarCode);
                    if (EmptyNullFlags == true) {
                        wronBuzzer.start();
                        AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Already exsist please try some other logs", false);
                        //Scanned Result Refresh
                        GetReceivedScannedResultList();
                        return;
                    }
                    Common.SearchedExternalLogsDetils = mDBExternalHelper.getBarCodeExternalLogDetails(Common.BarCode);
                    if (Common.SearchedExternalLogsDetils.size() > 0) {
                        Common.WoodSpieceID = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getWoodSpeciesId());
                        Common.WoodSpieceCode = Common.SearchedExternalLogsDetils.get(0).getWoodSpeciesCode();
                        Common.Length = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getLength_dm());
                        Common.Volume = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getVolume());
                        Common.FellingSectionId = Common.SearchedExternalLogsDetils.get(0).getFellingSectionID();
                        Common.TreeNumber = Common.SearchedExternalLogsDetils.get(0).getTreeNumber();
                        Common.QualityName = Common.SearchedExternalLogsDetils.get(0).getQuality();
                        Common.HoleVolume = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getHoleVolume());
                        Common.GrossVolume = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getGrossVolume());
                        Common.IsSBBLabelCorrected = false;
                        // According to purchase id checking wood speice code
                        boolean WSCNotEqualFlags = mDBInternalHelper.CheckWSCForPurchaseID(Common.WoodSpieceCode, Common.Purchase.SelectedPurchaseId);
                        if (WSCNotEqualFlags == false) {
                            wronBuzzer.start();
                            AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "WSC not match with purchase number", false);
                            return;
                        }
                    } else {
                        wronBuzzer.start();
                        AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Barcode not belongs to external purchase, please try another one", false);
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
                        return;
                    }

                    InsertReceivedScannedResultTable();
                   /* if (Common.ToLocReceivedID == 2) {
                        InsertReceivedScannedResultTable();
                    } else {
                        if (Common.InventoryReceivedScannedResultList.size() <= Common.VVBLimitation) {
                            InsertReceivedScannedResultTable();
                        } else {
                            wronBuzzer.start();
                            AlertDialogBox("VBB Limit", "For one VBB Number should have 22 items", false);
                            return;
                        }
                    }*/
                    Common.EntryMode = 1;
                    Common.ScannedEditTXTFlag = false;
                    PRD_ScanValueETxT.setText(scannedValue);
                }
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void InsertReceivedScannedResultTable() {
        beepsound.start();
        // Insert values into DB
        boolean ResultFlag = mDBInternalHelper.insertInventoryReceivedItemsFlagWithPurchaseID(Common.VBB_Number, Common.TransferID, Common.ReceivedID, Common.RecFromLocationname, Common.ToLocationName,
                Common.SbbLabel, Common.BarCode, Common.Length, Common.Volume, Common.UserID, Common.DateTime, Common.WoodSpieceID, Common.WoodSpieceCode, Common.EntryMode,
                Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), "", Common.FellingSectionId, Common.TreeNumber,
                Common.QualityName, Common.CheckedFlag, Common.TransferReciveUniqueID, Common.Purchase.SelectedPurchaseId);
        //Scanned Result Refresh
        GetReceivedScannedResultList();
        UpdateReceivedIDList();
    }

    public void UpdateReceivedIDList() {
        try {
            //Update Received List ID
            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            Double RemoveVolumeSum = ReceivedTotalVolume(Common.InventoryReceivedScannedResultList);
            // Update values into TransferID
            boolean TransferIDFlag = mDBInternalHelper.UpdatePurchaseReceivedListByID(Common.EndDate, Common.ToLocReceivedID, Common.RecFromLocationID, Common.TransportTypeId,
                    Common.TransferAgencyID, Common.DriverID, Common.UserID, Common.InventoryReceivedScannedResultList.size(), Common.ReceivedID,
                    String.valueOf(RemoveVolumeSum), 1, Common.ReceivedUniqueID, Common.ReceivedLoadedTypeID, Common.TransportId, Common.TransferReciveUniqueID);
            if (TransferIDFlag == true) {
                //Common.InventoryReceivedList = mDBInternalHelper.getInventoryReceivedIdList(Common.Filter_InventoryReceivedDate.get(Common.Purchase.PurcahseReceivedDateSelectedIndex));
            }
        } catch (Exception ex) {
            AlertDialogBox("UpdateReceivedIDList", ex.toString(), false);
        }
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
                   /* Common.SbbLabel = BarcodeSplite[1];
                    Common.BarCode = "NA-" + Common.SbbLabel;*/
                    Common.BarCode = BarcodeSplite[1] + "-" + BarcodeSplite[2];
                    boolean BarcodeValidation = Common.BarCode.contains("-");
                    if (BarcodeValidation == true) {
                        String[] arrOfStr = Common.BarCode.split("-");
                        if (arrOfStr.length > 1) {
                            Common.SbbLabel = arrOfStr[1];
                        }
                    }
                    Common.FellingSectionId = BarcodeSplite[3];
                    Common.TreeNumber = BarcodeSplite[4];

                    // Checked Duplicate In Internal Tabel
                    boolean EmptyNullFlags = mDBInternalHelper.getInventoryReceivedduplicateCheckForPurchaseID(Common.Purchase.SelectedPurchaseId, Common.BarCode);
                    if (EmptyNullFlags == true) {
                        wronBuzzer.start();
                        AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Already exsist please try some other logs", false);
                        //Scanned Result Refresh
                        GetReceivedScannedResultList();
                        return;
                    }
                    /*boolean EmptyNullFlags = mDBInternalHelper.getInventoryReceivedduplicateCheck(Common.ReceivedID, Common.BarCode, Common.SbbLabel);
                    if (EmptyNullFlags == true) {
                        wronBuzzer.start();
                    }*/
                    else {
                        Common.SearchedExternalLogsDetils = mDBExternalHelper.getBarCodeExternalLogDetails(Common.BarCode);
                        if (Common.SearchedExternalLogsDetils.size() > 0) {
                            Common.WoodSpieceID = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getWoodSpeciesId());
                            Common.WoodSpieceCode = Common.SearchedExternalLogsDetils.get(0).getWoodSpeciesCode();
                            Common.Length = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getLength_dm());
                            Common.Volume = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getVolume());
                            Common.FellingSectionId = Common.SearchedExternalLogsDetils.get(0).getFellingSectionID();
                            Common.TreeNumber = Common.SearchedExternalLogsDetils.get(0).getTreeNumber();
                            Common.QualityName = Common.SearchedExternalLogsDetils.get(0).getQuality();
                            Common.HoleVolume = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getHoleVolume());
                            Common.GrossVolume = String.valueOf(Common.SearchedExternalLogsDetils.get(0).getGrossVolume());
                            Common.IsSBBLabelCorrected = false;
                            // According to purchase id checking wood speice code
                            boolean WSCNotEqualFlags = mDBInternalHelper.CheckWSCForPurchaseID(Common.WoodSpieceCode, Common.Purchase.SelectedPurchaseId);
                            if (WSCNotEqualFlags == false) {
                                wronBuzzer.start();
                                AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "WSC not match with purchase number", false);
                                return;
                            }
                        } else {
                            wronBuzzer.start();
                            AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "Barcode not belongs to external purchase, please try another one", false);
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
                            return;
                        }
                    }
                    boolean ResultFlag = mDBInternalHelper.insertInventoryReceivedItemsFlagWithPurchaseID(Common.VBB_Number, Common.TransferID, Common.ReceivedID, Common.RecFromLocationname, Common.ToLocationName,
                            Common.SbbLabel, Common.BarCode, Common.Length, Common.Volume, Common.UserID, Common.DateTime, Common.WoodSpieceID, Common.WoodSpieceCode, Common.EntryMode,
                            Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), "", Common.FellingSectionId, Common.TreeNumber,
                            Common.QualityName, Common.CheckedFlag, Common.TransferReciveUniqueID, Common.Purchase.SelectedPurchaseId);

                }
            }
            beepsound.start();
            GetReceivedScannedResultList();
            UpdateReceivedIDList();
        } catch (Exception ee) {
            AlertDialogBox("ScanQR Barcode Details", ee.toString(), false);
        }
    }

    public void ScanedQRDetails(String[] ScannedArryValue) {
        try {
            Common.TransferReciveUniqueID = ScannedArryValue[0];
            boolean EmptyNullFlags = mDBInternalHelper.getInventoryReceivedTransferIDDuplication(Common.TransferReciveUniqueID);
            beepsound.start();
            //Common.TransferID = Integer.parseInt(ScannedArryValue[0]);
            PRD_TransferIDAutoTXT.setText(Common.TransferReciveUniqueID);

            Common.VBB_Number = ScannedArryValue[1];
            Common.ReceivedDate = ScannedArryValue[2];

            Common.RecFromLocationID = ScanValueCheckInteger(ScannedArryValue[3]);
            Common.ToLocReceivedID = ScanValueCheckInteger(ScannedArryValue[4]);
            Common.TransferAgencyID = ScanValueCheckInteger(ScannedArryValue[5]);
            Common.DriverID = ScanValueCheckInteger(ScannedArryValue[6]);
            Common.TransportId = ScanValueCheckInteger(ScannedArryValue[7]);
            Common.TransportTypeId = ScanValueCheckInteger(ScannedArryValue[8]);
            Common.FellingSectionId = ScannedArryValue[9];
            Common.Count = ScanValueCheckInteger(ScannedArryValue[10]);
            Common.VolumeSum = ScanValueCheckDouble(ScannedArryValue[11]);
            Common.ReceivedLoadedTypeID = ScanValueCheckInteger(ScannedArryValue[12]);

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
            PRD_ToLocationTxT.setText(Common.ToLocationName);
            QrScanDetails();
        } catch (Exception ee) {
            AlertDialogBox("ScanQR Details", ee.toString(), false);
        }
    }

    public int ScanValueCheckInteger(String Str) {
        int Value = 000;
        if (isNullOrEmpty(Str)) {
        } else {
            Value = Integer.parseInt(Str);
        }
        return Value;
    }

    public double ScanValueCheckDouble(String Str) {
        Double Value = 0.0;
        if (isNullOrEmpty(Str)) {
        } else {
            Value = Double.parseDouble(Str);
        }
        return Value;
    }

    public boolean ReceivedDetailsValidation(String TransferID, String FromLoc_ID, String TransAge_ID, String Driver_ID, String Truck_ID) {
        boolean Validattion = true;
        if (isNullOrEmpty(TransferID)) {
            Validattion = false;
            AlertDialogBox("From Location", "Add Transfer Id", false);
        }

        if (isNullOrEmpty(String.valueOf(FromLoc_ID))) {
            Validattion = false;
            AlertDialogBox(CommonMessage(R.string.tolocation), CommonMessage(R.string.pleaseselectoneitem), false);
        } /*else {
            boolean isFromLocValid = mDBExternalHelper.IsValidFromLocation(FromLoc_ID);
            if (isFromLocValid == false) {
                Validattion = false;
                AlertDialogBox(CommonMessage(R.string.tolocation), CommonMessage(R.string.pleaseselectoneitem), false);
            }
        }*/
        if (isNullOrEmpty(String.valueOf(TransAge_ID))) {
            Validattion = false;
            AlertDialogBox("Transport Agency", CommonMessage(R.string.pleaseselectoneitem), false);
        } else {
            boolean isAgencyIDValid = mDBExternalHelper.IsValidAgencyID(TransAge_ID);
            if (isAgencyIDValid == false) {
                Common.AgencyName = PRD_AgencyATXT.getText().toString().trim();
                Common.TransferAgencyID = 0;
            }
        }
        if (isNullOrEmpty(String.valueOf(Driver_ID))) {
            Validattion = false;
            AlertDialogBox("Driver Details", CommonMessage(R.string.pleaseselectoneitem), false);
        } else {
            boolean isAgencyIDValid = mDBExternalHelper.IsValidDriverID(Driver_ID);
            if (isAgencyIDValid == false) {
                Common.DriverID = 0;
                Common.DriverName = PRD_DriverATXT.getText().toString().trim();
            }
        }
        if (isNullOrEmpty(String.valueOf(Truck_ID))) {
            Validattion = false;
            AlertDialogBox("Truck Details", CommonMessage(R.string.pleaseselectoneitem), false);
        } else {
            boolean isAgencyIDValid = mDBExternalHelper.IsValidTruckID(Truck_ID);
            if (isAgencyIDValid == false) {
                Common.TrucklicensePlateNo = PRD_TruckDetialsATXT.getText().toString().trim();
                Common.TransportId = 0;
            }
        }
        return Validattion;
    }

    public void Signout(String ErrorMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ExternalPurchaseBottomBarActivity.this);
        builder1.setMessage(ErrorMessage);
        builder1.setCancelable(true);
        builder1.setPositiveButton("No",
                (dialog, id) -> dialog.cancel());
        builder1.setNegativeButton("Yes",
                (dialog, id) -> {
                    try {
                        PurchaseAgreementActivty();
                    } catch (Exception ex) {
                        AlertDialogBox("Close External Purchase", ex.toString(), false);
                    }
                    dialog.cancel();
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void PurchaseAgreementActivty() {
        Intent _gwwIntent = new Intent(this, ExternalPurchaseAgreementActivity.class);
        startActivity(_gwwIntent);
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(ExternalPurchaseBottomBarActivity.this, Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public String CommonMessage(int Common_Msg) {
        return ExternalPurchaseBottomBarActivity.this.getResources().getString(Common_Msg);
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

    // SDl Listener
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
            scanService.setOnScanListener(ExternalPurchaseBottomBarActivity.this);
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

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

    // display status string
    public void ScannedStatus(String s) {
        //LogsScanValueETxT.setText(s);
    }

    // display status resource id
    public void ScannedStatus(int id) {
        //LogsScanValueETxT.setText(id);
    }

    // display error msg
    private void dspErr(String s) {
        //LogsScanValueETxT.setText("ERROR" + s);
    }

    // display status string☺
    public void ScannedResult(String s) {
        Log.e("ScannedResult", ">>>>>>" + s);
        Common.HideKeyboard(this);
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
                if (Common.PurchaseScannedTabs == 0) {
                    ScannedLogsResult();
                }
                if (Common.PurchaseScannedTabs == 1) {
                    PTD_ScannedResult();
                }
                if (Common.PurchaseScannedTabs == 2) {
                    PRL_ScannedResult(s);
                }
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public static <T> boolean IsNullOrEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
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

    //-----------------------------------------------------
    // SurfaceHolder callbacks
    public void surfaceCreated(SurfaceHolder holder) {
        scanService.startViewFinder();
    }

    //-----------------------------------------------------
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    //-----------------------------------------------------
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "ZHENGYI.WZY");
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

    public void DeletePurchaseTransferListandScanned(int transferID, String TransferUnique) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeletePurchaseTransferListID(transferID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeletePurchaseTransferScanned(TransferUnique);
                if (DeleteScannedFlag == true) {
                    GetPurchaseTransferDateList();
                }
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
        }
    }

    public void DeleteReceivedListannReceivedScannedList(int receivedID, int purchaseID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeletePurchaseReceivedListID(receivedID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeletePurchaseReceivedScanned(receivedID, purchaseID);
                if (DeleteScannedFlag == true) {
                    GetPurchaseReceivedDateList();
                }
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("DeleteReceivedListannReceivedScannedList", ex.toString(), false);
        }
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
                //tsc.sendcommand(printSlip.PurchaseTransferHeader());
                tsc.sendcommand(printSlip.PurchaseTransferDetails12_02_2020());
                tsc.sendcommand(printSlip.PurchaseTransferListDimensions12_02_2020());
                tsc.clearbuffer();
                if (Common.InventorytransferScannedResultList.size() > (Common.VVBLimitation + 2)) {
                } else {
                    tsc.sendcommand(printSlip.TransferFooter());
                    //tsc.sendcommand(printSlip.PurchaseTransferFooter());
                    tsc.clearbuffer();
                }
                tsc.sendcommand(printSlip.TransferReceivedBottom19_09_2019(Common.TransferUniqueID));
                int TimerforPrint = 0;
                if (Common.Count > 50) {
                    TimerforPrint = ((Common.Count / 50) * 2) * 1000;
                }
                new Handler().postDelayed(() -> PrintoutEnd(), TimerforPrint);
            } catch (Exception ex) {
                ex.printStackTrace();
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
                tsc.sendcommand(printSlip.PurchaseReceivedHeader12_03_2020());
                tsc.sendcommand(printSlip.PurchaseReceivedScannedItemsDetails());
                tsc.sendcommand(printSlip.TransferReceivedBottom19_09_2019(Common.TransferReciveUniqueID));
                tsc.clearbuffer();
                int TimerforPrint = 0;
                if (Common.Count > 30) {
                    TimerforPrint = ((Common.Count / 30) * 2) * 1000;
                }
                new Handler().postDelayed(() -> PrintoutEnd(), TimerforPrint);
            } catch (Exception ex) {
                ex.printStackTrace();
                CrashAnalytics.CrashReport(ex);
            }
        }
    };

    public void InventoryReceivedSync(PurchaseReceivedModel receivedSyncModel) {
        Common.InventoryReceivedInputList.clear();
        Common.InventoryReceivedInputList = mDBInternalHelper.getReceivedScannedResultInputWithPurchaseID(Common.Purchase.SelectedPurchaseId, receivedSyncModel.getID());
        if (Common.InventoryReceivedInputList.size() > 0) {
            if (checkConnection() == true) {
                getInventoryReceivedSyncStatusApi(receivedSyncModel);
            }
        } else {
            AlertDialogBox("InventoryReceived Sync", "Values are empty", false);
        }
    }

    private void getInventoryReceivedSyncStatusApi(PurchaseReceivedModel receivedSyncModel) {
        try {
            progessbarLAY.setVisibility(View.VISIBLE);
            receiedSyncModel = new InventoryReceivedSyncModel();
            receiedSyncModel.setReceivedID(receivedSyncModel.getID());
            receiedSyncModel.setTransferID(Common.TransferID);
            receiedSyncModel.setVBBNumber(" ");
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
            receiedSyncModel.setAgencyName(Common.AgencyName);
            receiedSyncModel.setDriverName(Common.DriverName);
            receiedSyncModel.setTruckId(Common.TransportId);
            receiedSyncModel.setHHReceived(Common.InventoryReceivedInputList);

            InsertExport = ApiClient.getApiInterface();
            InsertExport.getInsertExternalPurchaseReceived(receiedSyncModel).enqueue(new Callback<InventoryReceivedSyncModel>() {
                @Override
                public void onResponse(Call<InventoryReceivedSyncModel> call, Response<InventoryReceivedSyncModel> response) {
                    try {
                        progessbarLAY.setVisibility(View.GONE);
                        if (GwwException.GwwException(response.code())) {
                            if (response.isSuccessful()) {
                                Common.SyncStatusList.clear();
                                Common.SyncStatusList.addAll(response.body().getStatus());
                                if (Common.SyncStatusList.get(0).getStatus() == 1) {
                                    Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                                    Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                                    boolean ListIdFlag = mDBInternalHelper.UpdatePurchaseReceivedSyncStatus(Common.SyncTime, 1, receivedSyncModel.getPurchaseReceivedUniqueID(),
                                            Common.SyncStatusList.get(0).getTransferAgencyId(), Common.SyncStatusList.get(0).getDriverId(), Common.SyncStatusList.get(0).getTruckId());
                                    if (ListIdFlag) {
                                        //Scanned Result Refresh
                                        GetPurchaseReceivedDateList();
                                        AlertDialogBox(CommonMessage(R.string.purchaseReceivedHead), "#" + Common.ReceivedID + "--" + Common.SyncStatusList.get(0).getMessage(), true);
                                        if (Common.SyncStatusList.get(0).getTransferAgencyId() != 0 || Common.SyncStatusList.get(0).getDriverId() != 0 || Common.SyncStatusList.get(0).getTruckId() != 0) {
                                            Common.IsExternalSyncFlag = true;
                                            Common.IsProgressVisible = false;
                                            Common.ExternalSyncFlag = false;
                                            gwwMain.ExternalDataBaseSync();
                                        }
                                    }
                                } else {
                                    AlertDialogBox(CommonMessage(R.string.purchaseReceivedHead), "#" + Common.ReceivedID + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                                }
                            } else {
                                AlertDialogBox(CommonMessage(R.string.purchaseReceivedHead), "#" + Common.ReceivedID + "--" + "Not Synced", false);
                            }
                        } else {
                            Common.AuthorizationFlag = true;
                            AlertDialogBox(CommonMessage(R.string.purchaseReceivedHead), response.message(), false);
                        }

                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        AlertDialogBox(CommonMessage(R.string.purchaseReceivedHead), "#" + Common.ReceivedID + "--" + ex.getMessage(), false);
                    }
                }

                @Override
                public void onFailure(Call<InventoryReceivedSyncModel> call, Throwable t) {
                    progessbarLAY.setVisibility(View.GONE);
                    AlertDialogBox(CommonMessage(R.string.purchaseReceivedHead), "#" + Common.ReceivedID + "--" + t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            progessbarLAY.setVisibility(View.GONE);
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox(CommonMessage(R.string.purchaseReceivedHead), ex.toString(), false);
        }
    }

    public boolean FelllingDetailsValidation(String wspieceCode) {
        boolean Validattion = true;
        try {
         /*   if (isNullOrEmpty(FromLoc_Str)) {
                Validattion = false;
                AlertDialogBox("Location", "Please check Location!", false);
            }*/
            if (wspieceCode.equals("")) {
                Validattion = false;
                AlertDialogBox("WoodSpeice Code", "Please enter WoodSpeice Code ", false);
            } else {
                if (wspieceCode.length() < 3 || wspieceCode.length() > 3) {
                    Validattion = false;
                    AlertDialogBox("WoodSpeice Code", "Please select WoodSpeice Code from list!", false);
                }
            }
        } catch (Exception e) {
            AlertDialogBox(CommonMessage(R.string.PurchaseTransferHead), "please select tree number from tree list", false);
            Validattion = false;
        }
     /*   if (treeno.equals("")) {
            Validattion = false;
            AlertDialogBox("Tree No", "please enter PlotNo!", false);
        }*/
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

    public void ConfirmSyncMessage(String ErrorMessage) {
        if (LogsRemoveAlert != null && LogsRemoveAlert.isShowing()) {
            return;
        }
        LogsRemovebuilder = new AlertDialog.Builder(ExternalPurchaseBottomBarActivity.this);
        LogsRemovebuilder.setMessage(ErrorMessage);
        LogsRemovebuilder.setCancelable(true);
        LogsRemovebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                (dialog, id) -> dialog.cancel());
        LogsRemovebuilder.setNegativeButton(CommonMessage(R.string.action_Sync),
                (dialog, id) -> {
                    try {
                        ScannedLogsSync();
                    } catch (Exception e) {

                    }
                    dialog.cancel();
                });

        LogsRemoveAlert = LogsRemovebuilder.create();
        LogsRemoveAlert.show();
    }

    public void RefreshAggrement() {
        try {
            Common.PurchaseLogsIndex = mDBExternalHelper.getPurchaseLogsRowID();
            Log.e("RefreshAggrement", ">>>>>>>>>>");
            PurchaseAgreementInputModel purchaseModel = new PurchaseAgreementInputModel();
            purchaseModel.setExternalLogRowId(Common.PurchaseLogsIndex);
            InsertExport = ApiClient.getApiInterface();
            InsertExport.GetAgreementPurchaseLogs(purchaseModel).enqueue(new Callback<PurchaseAgreementInputModel>() {
                @Override
                public void onResponse(Call<PurchaseAgreementInputModel> call, Response<PurchaseAgreementInputModel> response) {
                    if (GwwException.GwwException(response.code()) == true) {
                        if (response.body() != null) {
                            Common.PurchaseAgreementSync.clear();
                            Common.PurchaseAgreementSync.addAll(response.body().getPurchaseAgreement());
                            if (Common.PurchaseAgreementSync.size() > 0) {
                                mDBInternalHelper.insertInternalPurchaseAgreementREPLACEIB(Common.PurchaseAgreementSync);
                                /*for (PurchaseAgreementModel purchasMod : Common.PurchaseAgreementSync) {
                                    boolean insetFlag = mDBInternalHelper.getPurchasePurcahseListIDCheck(purchasMod.getPurchaseId(), purchasMod.getPurchaseNo(), purchasMod.getDiameterRange(), purchasMod.getWoodSpeciesCode());
                                    if (insetFlag == false) {
                                        mDBInternalHelper.insertInternalPurchaseAgreementREPLACEIB(Common.PurchaseAgreementSync);
                                    }
                                }*/
                            }
                            Common.PurchaseLogsSync.clear();
                            Common.PurchaseLogsSync.addAll(response.body().getExternalPurchaseLogs());
                            if (Common.PurchaseLogsSync.size() > 0) {
                                mDBExternalHelper.insertPurchaseLogsREPLACE(Common.PurchaseLogsSync);
                                ExportDatabaseToStorage();
                            } else {
                                AlertDialogBox(CommonMessage(R.string.purchaseLogsHead), "# No new logs added in external purchase table", false);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<PurchaseAgreementInputModel> call, Throwable t) {
                }
            });
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void ExportDatabaseToStorage() {
        try {
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GWW";
            File sd = new File(dir);
            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + this.getPackageName() + "/databases/GWW.db";
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
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void ExteralSync_showAlertDialog(final Context context, String title, String message,
                                            Boolean status) {
        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }
        alertDialog = new AlertDialog.Builder(context).create();
        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);
        if (status != null)
            // Setting alert dialog collector
            alertDialog.setIcon((status) ? R.mipmap.success : R.mipmap.fail);
        // Setting OK Button
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
            Common.AlertDialogVisibleFlag = true;
            if (Common.AuthorizationFlag == true) {
                Common.AuthorizationFlag = false;
            }
            RefreshAggrement();
        });
        alertDialog.show();
    }
}

