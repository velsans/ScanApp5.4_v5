package com.zebra.main.activity.purchase.ui.received;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import androidx.annotation.NonNull;
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
import com.zebra.main.adapter.FellingSectionAdapter;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.interfac.BaseApplication;
import com.zebra.main.model.InvReceived.InventoryReceivedModel;
import com.zebra.main.model.InvReceived.ReceivedLogsModel;
import com.zebra.main.model.InvTransfer.InventoryTransferScannedResultModel;
import com.zebra.main.model.externaldb.AgencyDetailsModel;
import com.zebra.main.model.externaldb.ConcessionNamesModel;
import com.zebra.main.model.externaldb.DriverDetailsModel;
import com.zebra.main.model.externaldb.LoadedModel;
import com.zebra.main.model.externaldb.LocationsModel;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationsFragment extends Fragment implements SdlScanListener, ConnectivityReceiver.ConnectivityReceiverListener {

    View ReceivedView;
    static final private boolean saveSnapshot = false;
    MediaPlayer beepsound, wronBuzzer;
    private BarCodeReader bcr = null;
    private PowerManager.WakeLock mWakeLock = null;
    private int motionEvents = 0, modechgEvents = 0, snapNum = 0;
    boolean bind = false;
    private SdlScanService scanService;
    AlertDialogManager alert = new AlertDialogManager();
    Intent service;
    Context FragContext;
    BarCodeReceiver barcode_receiver;
    boolean isInternetPresent;
    LinearLayoutManager horizontalLayoutManager;
    private RecyclerView ScannedResultLV, receivedDetails_RV;
    Spinner FromLocationSpin, ToLocationSpin, TransportAgencySpin, TruckPlateNumberSpin, FellingSecSpinner, DriverSpin;
    AutoCompleteTextView FromLocationATXT, ToLocationATXT, DriverATXT, AgencyATXT, TruckDetialsATXT;
    FloatingActionButton ReceivedScanBtn;
    ListView ReceivedMode_LV, LoadedBy_LV;
    private EditText ScanValueETxT = null, treeNoEDT, fellingIDEDT, TransferIDAutoTXT;
    private TextView TotalScannedCount, sbbLabel_TXT, NoValueFoundTxT, vbb_txt, ToLocationTxT, VolumeTXT, FellingSpinnerTxT, scanQRDetails, Received_TotalCheckedCountTXT, ReceivedTopicsTxT;
    private LinearLayout advanceSearchLAY, EditOptionFlagLay, EditOptionFlagLay2, receivedDetails_layout;
    private ImageView image = null, scanQRBarCodeDetails, AS_NovalueFound, receivedDetails_close, receivedDetails_open;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;
    String TreeNO, FellingID;
    AlertDialog.Builder Removebuilder = null;
    AlertDialog RemoveAlert = null;
    PrintSlipsClass printSlip;
    FellingSectionAdapter fellingSectionAdapter;
    TransportAdapterInside transportAdapter;
    LoadedAdapterInside loadedAdapter;
    StringtreeNoadapter StringtreeNoadapter;
    DriverDetailsModel drivermodelArray;
    AgencyDetailsModel agencyModelArray;
    TruckDetailsModel truckModelArray;
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    TSCActivity tsc = new TSCActivity();
    private InventoryReceivedAdapter receivedadapter;
    InventoryReceivedDetailsAdapter adapter;

    private class BarCodeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    public NotificationsFragment() {
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
                new IntentFilter("TRANSFER_REFRESH"));
        BaseApplication.getInstance().setConnectivityListener(this);
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //transferViewModel = ViewModelProviders.of(this).get(TransferViewModel.class);
        ReceivedView = inflater.inflate(R.layout.fragment_notifications, container, false);
        Initialization();
        //ImportDataBaseFromInternalStorage();
        mDBExternalHelper = new ExternalDataBaseHelperClass(getActivity());
        mDBInternalHelper = new InternalDataBaseHelperClass(getActivity());
        printSlip = new PrintSlipsClass(getActivity());
        beepsound = MediaPlayer.create(getActivity(), R.raw.beep);
        wronBuzzer = MediaPlayer.create(getActivity(), R.raw.wrong_buzzer);
        //View existing ListView
        ViewReceivedList(Common.Purchase.IsReceivedEditorViewFlag);
        getToLocationsforSpinner();
        getdriverforSpinner();
        getagencyforSpinner();
        gettruckforSpinner();
        gettransModeforView();
        getLoadedByView();
        Common.HideKeyboard(getActivity());
        //enableSwipeToDeleteAndUndo();
        vbb_txt.setText(Common.VBB_Number);
        if (Common.Purchase.IsReceivedEditorViewFlag == false) {
            ToLocationTxT.setText(Common.ToLocationName);
            QrScanDetails();
            TransferIDAutoTXT.setText(Common.TransferReciveUniqueID);
        }
        Click_Listener();
        GetReceivedScannedResultList();
        return ReceivedView;
    }

    public void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
        }
    }

    private void Initialization() {
        ReceivedScanBtn = ReceivedView.findViewById(R.id.Received_ScanBTN);
        ReceivedView.findViewById(R.id.Received_ScanBTN).setOnClickListener(mDecodeListener);
        ReceivedView.findViewById(R.id.Received_EnterIMG).setOnClickListener(mScannerListener);
        ReceivedView.findViewById(R.id.Received_close).setOnClickListener(mCloseActivityListener);
        ReceivedView.findViewById(R.id.Received_Print).setOnClickListener(mPrintListener);

        ScanValueETxT = ReceivedView.findViewById(R.id.Received_scanValueEDTXT);
        TotalScannedCount = ReceivedView.findViewById(R.id.Received_TotalScannedCount);
        ScannedResultLV = ReceivedView.findViewById(R.id.Received_ListView);

        //ToLocationSpin = ReceivedView.findViewById(R.id.tolocation_spinner);
        FromLocationSpin = ReceivedView.findViewById(R.id.fromlocation_spinner);
        DriverSpin = ReceivedView.findViewById(R.id.driver_sppinner);
        TransportAgencySpin = ReceivedView.findViewById(R.id.transagency_spinner);
        TruckPlateNumberSpin = ReceivedView.findViewById(R.id.truckplateumber_spinner);
        FellingSecSpinner = ReceivedView.findViewById(R.id.fellingsection_spinner);
        ReceivedMode_LV = ReceivedView.findViewById(R.id.ReceivedMode_LV);
        vbb_txt = ReceivedView.findViewById(R.id.VBB_numberTXT);
        ToLocationTxT = ReceivedView.findViewById(R.id.toLocationTXT);
        VolumeTXT = ReceivedView.findViewById(R.id.TotalVolumeReceived);
        FellingSpinnerTxT = ReceivedView.findViewById(R.id.fellingSpinnerTxT);

        scanQRDetails = ReceivedView.findViewById(R.id.Received_scanQRDetails);
        scanQRBarCodeDetails = ReceivedView.findViewById(R.id.Received_scanQRBarCode);

        ReceivedView.findViewById(R.id.Received_scanQRDetails).setOnClickListener(mScannerQRCodeDetailListener);
        ReceivedView.findViewById(R.id.Received_scanQRBarCode).setOnClickListener(mScannerQRBarCodeDetailListener);

        NoValueFoundTxT = ReceivedView.findViewById(R.id.NovalueFound);
        //TransferIDTXT = ReceivedView.findViewById(R.id.transferIDTXT);
        Received_TotalCheckedCountTXT = ReceivedView.findViewById(R.id.Received_TotalCheckedCount);
        TransferIDAutoTXT = ReceivedView.findViewById(R.id.transferIDAutoTXT);
        ReceivedView.findViewById(R.id.addTransferIDTXT).setOnClickListener(mAddTransferIDListener);
        EditOptionFlagLay = ReceivedView.findViewById(R.id.received_editLayout);
        EditOptionFlagLay2 = ReceivedView.findViewById(R.id.transfer_editLayout2);

        treeNoEDT = ReceivedView.findViewById(R.id.as_treenoEDT);
        fellingIDEDT = ReceivedView.findViewById(R.id.as_fellingEDT);

        //advanceSearchLAY = ReceivedView.findViewById(R.id.advancesearchLAY);
        //advanceSearchLAY.setVisibility(View.GONE);
        sbbLabel_TXT = ReceivedView.findViewById(R.id.sbbLabel_TXT);
        NoValueFoundTxT = ReceivedView.findViewById(R.id.NovalueFound);
        //AS_NovalueFound = ReceivedView.findViewById(R.id.as_NovalueFound);
        //AS_NovalueFound.setVisibility(View.GONE);

        FromLocationATXT = ReceivedView.findViewById(R.id.fromlocation_ATxT);
        DriverATXT = ReceivedView.findViewById(R.id.driver_ATxT);
        AgencyATXT = ReceivedView.findViewById(R.id.transagency_ATxT);
        TruckDetialsATXT = ReceivedView.findViewById(R.id.truckplateumber_ATxT);

        //18-aug-2020
        receivedDetails_layout = ReceivedView.findViewById(R.id.receivedDetails_layout);
        receivedDetails_layout.setVisibility(View.GONE);
        receivedDetails_RV = ReceivedView.findViewById(R.id.receivedDetails_RV);
        receivedDetails_close = ReceivedView.findViewById(R.id.receivedDetails_close);
        receivedDetails_open = ReceivedView.findViewById(R.id.receivedDetails_open);
        receivedDetails_open.setVisibility(View.VISIBLE);
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
            scanService.setOnScanListener(NotificationsFragment.this);
            scanService.setActivityUp(true);
        }
    };

    @Override
    public void result(String content) {

    }

    @Override
    public void henResult(String codeType, String context) {

    }

    public void ScannedStatus(String s) {
        // tvStat.setText(s);
    }

    public void ScannedStatus(int id) {
        //tvStat.setText(id);
    }

    private void dspErr(String s) {
        //tvStat.setText("ERROR" + s);
    }

    public void ScannedResult(String scannedValue) {
        Common.HideKeyboard(getActivity());
        try {
            if (scannedValue != null && scannedValue.length() > 0) {
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
                    //if (Common.ScanMode == true) {
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
                    // Checked Duplicate In Internal Tabel
                    boolean EmptyNullFlags = mDBInternalHelper.getInventoryReceivedduplicateCheckForPurchaseID(Common.Purchase.SelectedPurchaseId, Common.BarCode);
                    if (EmptyNullFlags == true) {
                        //Update If Already in ListView
                        boolean ResultFlag = mDBInternalHelper.UpdateInventoryReceivedItemsPuchaseID(Common.Purchase.SelectedPurchaseId, Common.BarCode, Common.CheckedFlag);
                        if (ResultFlag == true) {
                            //Scanned Result Refresh
                            GetReceivedScannedResultList();
                        }
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
                        if (WSCNotEqualFlags == true) {
                            wronBuzzer.start();
                            AlertDialogBox(CommonMessage(R.string.TransferHead), "WSC not match with purchase number", false);
                            return;
                        }
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
                        sbbLabel_TXT.setText(Common.BarCode);
                        AlertDialogBox(CommonMessage(R.string.TransferHead), "Barcode not belongs to external purchase, please try another one", false);
                        return;
                    }
                    InsertReceivedScannedResultTable();
                    Common.EntryMode = 1;
                    Common.ScannedEditTXTFlag = false;
                    ScanValueETxT.setText(scannedValue);
                }
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void ScanedQRDetails(String[] ScannedArryValue) {
        try {
            Common.TransferReciveUniqueID = ScannedArryValue[0];
            boolean EmptyNullFlags = mDBInternalHelper.getInventoryReceivedTransferIDDuplication(Common.TransferReciveUniqueID);
            beepsound.start();
            //Common.TransferID = Integer.parseInt(ScannedArryValue[0]);
            TransferIDAutoTXT.setText(Common.TransferReciveUniqueID);

            Common.VBB_Number = ScannedArryValue[1];
            Common.ReceivedDate = ScannedArryValue[2];

            Common.RecFromLocationID = ScanValueCheckInteger(ScannedArryValue[3]);
            Common.ToLocReceivedID = ScanValueCheckInteger(ScannedArryValue[4]);
            Common.Purchase.SelectedReceivedAgencyID = ScanValueCheckInteger(ScannedArryValue[5]);
            Common.Purchase.SelectedReceivedDriverID = ScanValueCheckInteger(ScannedArryValue[6]);
            Common.Purchase.SelectedReceivedTruckID = ScanValueCheckInteger(ScannedArryValue[7]);
            Common.Purchase.SelectedReceivedTransPortMode = ScanValueCheckInteger(ScannedArryValue[8]);
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
            ToLocationTxT.setText(Common.ToLocationName);
            QrScanDetails();
        } catch (Exception ee) {
            AlertDialogBox("ScanQR Details", ee.toString(), false);
        }
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

    public void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        }
        if (mWakeLock != null) {
            mWakeLock.acquire();
        }
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

    private void GetReceivedScannedResultList() {
        try {
            Common.InventoryReceivedScannedResultList.clear();
            Common.InventoryReceivedScannedResultList = mDBInternalHelper.getInventoryReceivedWithVBBNumber(Common.VBB_Number, Common.ReceivedID);
            Common.CheckedSize = mDBInternalHelper.ColculateInventoryReceivedCheckedItems(Common.ReceivedID, Common.SbbLabel, "YES");
            Double RemoveVolumeSum = 0.00;
            if (Common.InventoryReceivedScannedResultList.size() > 0) {
                receivedadapter = new InventoryReceivedAdapter(Common.InventoryReceivedScannedResultList, Common.QulaityDefaultList, getActivity());
                horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                ScannedResultLV.setLayoutManager(horizontalLayoutManager);
                receivedadapter.notifyDataSetChanged();
                ScannedResultLV.setAdapter(receivedadapter);
                ScannedResultLV.setVisibility(View.VISIBLE);
                RemoveVolumeSum = ReceivedTotalVolume(Common.InventoryReceivedScannedResultList);
                receivedDetails_open.setVisibility(View.VISIBLE);
            } else {
                ScannedResultLV.setVisibility(View.GONE);
                receivedDetails_open.setVisibility(View.GONE);
                // Update StartDate in Purchase Agreement
                Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                mDBInternalHelper.UpdatePurchaseAgreementReceivedStartDate(Common.DateTime,Common.Purchase.SelectedPurchaseId);
            }
            TotalScannedCount.setText(String.valueOf(Common.InventoryReceivedScannedResultList.size()));
            VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(RemoveVolumeSum)));
            Received_TotalCheckedCountTXT.setText(String.valueOf(Common.CheckedSize));
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public double TotalVolume(ArrayList<InventoryTransferScannedResultModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (InventoryTransferScannedResultModel inventoTransScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(inventoTransScanModel.getVolume());
        }
        return TotVolume;
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
        public InventoryReceivedAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_infliator, parent, false);
            InventoryReceivedAdapter.GroceryViewHolder gvh = new InventoryReceivedAdapter.GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(InventoryReceivedAdapter.GroceryViewHolder holder, final int position) {
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
            holder.Remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.IsEditorViewFlag == false) {
                        AlertDialogBox(CommonMessage(R.string.ReceivedHead), "Can not edit Or delete after Synced", false);
                    } else {
                        Common.RemoveSBBLabel = "";
                        Common.RemoveSBBLabel = ScannedResultList.get(position).getBarCode();
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
                        boolean Isdelete = mDBInternalHelper.RemovePurchaseReceivedlistview(Common.RemoveSBBLabel, 0, Common.Purchase.SelectedPurchaseId);
                        if (Isdelete == true) {
                            Toast.makeText(getActivity(), "Successfully Removed from List", Toast.LENGTH_LONG).show();
                            GetReceivedScannedResultList();
                        }
                    } catch (Exception Ex) {

                    }
                    dialog.cancel();
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public void InsertReceivedScannedResultTable() {
        beepsound.start();
        // Insert values into DB
        boolean ResultFlag = mDBInternalHelper.insertInventoryReceivedItemsFlagWithPurchaseID(Common.VBB_Number, Common.TransferID, Common.ReceivedID, Common.RecFromLocationname, Common.ToLocationName,
                Common.SbbLabel, Common.BarCode, Common.Length, Common.Volume, Common.UserID, Common.DateTime, Common.WoodSpieceID, Common.WoodSpieceCode, Common.EntryMode,
                Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), "", Common.FellingSectionId, Common.TreeNumber,
                Common.QualityName, Common.CheckedFlag, Common.TransferReciveUniqueID, Common.Purchase.SelectedPurchaseId);
        // Update header in purchase table
        UpdateReceivedIDList();
        //Scanned Result Refresh
        GetReceivedScannedResultList();
    }

    public static int booleanToInt(boolean value) {
        // Convert true to 1 and false to 0.
        return value ? 1 : 0;
    }

    public void ViewReceivedList(boolean EdtFlag) {
        if (EdtFlag == false) {
            EditOptionFlagLay.setVisibility(View.VISIBLE);
            ReceivedScanBtn.setEnabled(false);
            //ReceivedView.findViewById(R.id.ReceivedNXTtxt).setEnabled(false);
            //ReceivedView.findViewById(R.id.advancedsearchTXT).setEnabled(false);
            ScanValueETxT.setEnabled(false);
            ReceivedView.findViewById(R.id.Received_EnterIMG).setEnabled(false);
            ReceivedView.findViewById(R.id.Received_Print).setEnabled(false);
            EditOptionFlagLay2.setVisibility(View.VISIBLE);
            //TransferIDAutoTXT.setEnabled(false);
        } else {
            EditOptionFlagLay.setVisibility(View.GONE);
            ReceivedScanBtn.setEnabled(true);
            //ReceivedView.findViewById(R.id.ReceivedNXTtxt).setEnabled(true);
            //ReceivedView.findViewById(R.id.advancedsearchTXT).setEnabled(true);
            ScanValueETxT.setEnabled(true);
            ReceivedView.findViewById(R.id.Received_EnterIMG).setEnabled(true);
            ReceivedView.findViewById(R.id.Received_Print).setEnabled(true);
            EditOptionFlagLay2.setVisibility(View.GONE);
            //TransferIDAutoTXT.setEnabled(true);
        }
    }

    private void getToLocationsforSpinner() {
        try {
            // FromLocation Name from FromLocationID
            Common.ConcessionList.clear();
            Common.ConcessionList = mDBExternalHelper.getConcessionList();

            Common.LocationList.clear();
            Common.LocationList = mDBExternalHelper.getToLocations();
            for (LocationsModel locationMod : Common.LocationList) {
                if (Common.ToLocReceivedID == locationMod.getToLocationId()) {
                    Common.ToLocationName = locationMod.getLocation();
                    ToLocationTxT.setText(Common.ToLocationName);
                }
            }
            // Felling Section Spinner
            int LocationTypeID = Common.ConcessionList.get(0).getLocationType();
            if (LocationTypeID == 1) {
                Common.FellingSectionList.clear();
                Common.FellingSectionList = mDBExternalHelper.getFellingSectionDetails(Common.RecFromLocationID);
                fellingSectionAdapter = new FellingSectionAdapter(getActivity(), Common.FellingSectionList);
                FellingSecSpinner.setAdapter(fellingSectionAdapter);
                FellingSecSpinner.setVisibility(View.VISIBLE);
            } else {
                FellingSecSpinner.setVisibility(View.GONE);
            }

            Common.ConcessionListStringList = new String[Common.ConcessionList.size()];
            for (int i = 0; i < Common.ConcessionList.size(); i++) {
                Common.ConcessionListStringList[i] = Common.ConcessionList.get(i).getConcessionName();
            }
            StringtreeNoadapter = new StringtreeNoadapter(getActivity(),
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

            Common.TruckDetialsStringList = new String[Common.TruckDeatilsList.size()];
            for (int i = 0; i < Common.TruckDeatilsList.size(); i++) {
                Common.TruckDetialsStringList[i] = Common.TruckDeatilsList.get(i).getTruckLicensePlateNo();
            }
            //Common.FellingRegTreeNoStringList = Common.FellingRegTreeFilterList.toArray(new String[0]);
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

            Common.DriverListStringList = new String[Common.DriverList.size()];
            for (int i = 0; i < Common.DriverList.size(); i++) {
                Common.DriverListStringList[i] = Common.DriverList.get(i).getDriverName();
            }
            //Common.FellingRegTreeNoStringList = Common.FellingRegTreeFilterList.toArray(new String[0]);
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
            ReceivedMode_LV.setAdapter(transportAdapter);
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

    public void ReceiveingLogsDetails() {
        if (Common.InventoryReceivedScannedResultList.size() > 0) {
            Common.ReceivingLogsList.clear();
            Common.ReceivingLogsList = mDBExternalHelper.getRecevingLogsTransferLogDetails(Common.InventoryReceivedScannedResultList);
            horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
            horizontalLayoutManager.setStackFromEnd(true);
            receivedDetails_RV.setLayoutManager(horizontalLayoutManager);
            adapter = new InventoryReceivedDetailsAdapter(getActivity(), Common.ReceivingLogsList);
            receivedDetails_RV.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            receivedDetails_layout.setVisibility(View.VISIBLE);
        } else {
            AlertDialogBox("Receiving Logs", "No value found", false);
            receivedDetails_layout.setVisibility(View.VISIBLE);
        }
    }

    public void Click_Listener() {

        ScanValueETxT.setOnTouchListener((v, event) -> {
            Common.ScannedEditTXTFlag = true;
            return false;
        });

        receivedDetails_close.setOnClickListener(v -> {
            receivedDetails_layout.setVisibility(View.GONE);
        });

        receivedDetails_open.setOnClickListener(v -> {
            ReceiveingLogsDetails();
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

        ReceivedMode_LV.setOnItemClickListener((parent, view, position, id) -> {
            Common.Purchase.SelectedReceivedTransPortMode = Common.TransportModeList.get(position).getTransportTypeId();
            Common.TransportMode = Common.TransportModeList.get(position).getTransportMode();
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

        EditOptionFlagLay.setOnClickListener(v -> AlertDialogBox(CommonMessage(R.string.ReceivedHead), "Can not edit Or delete after Synced", false));

        EditOptionFlagLay2.setOnClickListener(v -> AlertDialogBox(CommonMessage(R.string.ReceivedHead), "Can not edit Or delete after Synced", false));

        /*AutoComplete*/
        FromLocationATXT.setOnTouchListener((v, event) -> {
            FromLocationATXT.requestFocus();
            FromLocationATXT.showDropDown();
            return false;
        });

        FromLocationATXT.setOnItemClickListener((parent, view, position, id) -> {
            String locModelStr = (String) parent.getItemAtPosition(position);
            for (ConcessionNamesModel locModel : Common.ConcessionList)
                if (locModelStr.equals(locModel.getConcessionName())) {
                    Common.RecFromLocationID = locModel.getFromLocationId();
                    Common.RecFromLocationname = locModel.getConcessionName();
                }
        });

        DriverATXT.setOnTouchListener((v, event) -> {
            DriverATXT.requestFocus();
            DriverATXT.showDropDown();
            return false;
        });

        DriverATXT.setOnItemClickListener((parent, view, position, id) -> {
            //String driverModelStr = (String) parent.getItemAtPosition(position);
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
                        Common.Purchase.SelectedReceivedDriverID = drivermodelArray.getTruckDriverId();
                        Common.DriverName = drivermodelArray.getDriverName();
                        DriverATXT.setText(driverModelStr);
                    }
                }
            } catch (Exception ex) {

            }
        });

        AgencyATXT.setOnTouchListener((v, event) -> {
            AgencyATXT.requestFocus();
            AgencyATXT.showDropDown();
            return false;
        });

        AgencyATXT.setOnItemClickListener((parent, view, position, id) -> {
            //String agencyModelStr = (String) parent.getItemAtPosition(position);
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
                        Common.AgencyName = agencyModelArray.getAgencyName();
                        Common.Purchase.SelectedReceivedAgencyID = agencyModelArray.getAgencyId();
                        AgencyATXT.setText(agencyModelStr);
                    }
                }
            } catch (Exception ex) {

            }
        });

        TruckDetialsATXT.setOnTouchListener((v, event) -> {
            TruckDetialsATXT.requestFocus();
            TruckDetialsATXT.showDropDown();
            return false;
        });

        TruckDetialsATXT.setOnItemClickListener((parent, view, position, id) -> {
            try {
                String item = (String) parent.getItemAtPosition(position);
                if (!isNullOrEmpty(item)) {
                    truckModelArray = new TruckDetailsModel();
                    truckModelArray = mDBExternalHelper.getOneTruckDetails(item);
                    if (truckModelArray.getIsBlocked() == 1) {
                        AlertDialogBox("Verification", "Selected truck is blocked, please contact admin", false);
                        TruckDetialsATXT.setText("");
                    } else {
                        String truckStr = truckModelArray.getTruckLicensePlateNo();
                        Common.TrucklicensePlateNo = truckModelArray.getTruckLicensePlateNo();
                        Common.Purchase.SelectedReceivedTruckID = truckModelArray.getTransportId();
                        TruckDetialsATXT.setText(truckStr);
                    }
                }
            } catch (Exception ex) {

            }
        });
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
            if (Common.Purchase.SelectedReceivedTransPortMode == modename.get(position).getTransportTypeId()) {
                TransferMode.setChecked(true);
                Common.TransportMode = modename.get(position).getTransportMode();
            } else {
                TransferMode.setChecked(false);
            }
            TransferMode.setText(modename.get(position).getTransportMode());
            TransferMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.Purchase.SelectedReceivedTransPortMode = modename.get(position).getTransportTypeId();
                    Common.TransportMode = modename.get(position).getTransportMode();
                    notifyDataSetChanged();
                }
            });

            TransferMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Common.Purchase.SelectedReceivedTransPortMode = modename.get(position).getTransportTypeId();
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
            CheckBox LoadedMode = view.findViewById(R.id.loadedMode_infi);
            if (modename.get(position).getIsActive() == 1) {
                LoadedMode.setVisibility(View.VISIBLE);
            } else {
                LoadedMode.setVisibility(View.GONE);
            }
            if (Common.Purchase.SelectedReceivedLoadedby == modename.get(position).getLoadedid()) {
                LoadedMode.setChecked(true);
                Common.LoadedName = modename.get(position).getName();
            } else {
                LoadedMode.setChecked(false);
            }
            LoadedMode.setText(modename.get(position).getName());
            LoadedMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.Purchase.SelectedReceivedLoadedby = modename.get(position).getLoadedid();
                    Common.Purchase.SelectedReceivedLoadedby = Common.Purchase.SelectedReceivedLoadedby;
                    Common.LoadedName = modename.get(position).getName();
                    notifyDataSetChanged();
                }
            });

            LoadedMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Common.Purchase.SelectedReceivedLoadedby = modename.get(position).getLoadedid();
                    Common.Purchase.SelectedReceivedLoadedby = Common.Purchase.SelectedReceivedLoadedby;
                    Common.LoadedName = modename.get(position).getName();
                    notifyDataSetChanged();
                }
            });
            return view;
        }
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

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
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
        } else {
            boolean isFromLocValid = mDBExternalHelper.IsValidFromLocation(FromLoc_ID);
            if (isFromLocValid == false) {
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
                Common.AgencyName = AgencyATXT.getText().toString().trim();
                Common.Purchase.SelectedReceivedAgencyID = 0;
            }
        }
        if (isNullOrEmpty(String.valueOf(Driver_ID))) {
            Validattion = false;
            AlertDialogBox("Driver Details", CommonMessage(R.string.pleaseselectoneitem), false);
        } else {
            boolean isAgencyIDValid = mDBExternalHelper.IsValidDriverID(Driver_ID);
            if (isAgencyIDValid == false) {
                Common.Purchase.SelectedReceivedDriverID = 0;
                Common.DriverName = DriverATXT.getText().toString().trim();
            }
        }
        if (isNullOrEmpty(String.valueOf(Truck_ID))) {
            Validattion = false;
            AlertDialogBox("Truck Details", CommonMessage(R.string.pleaseselectoneitem), false);
        } else {
            boolean isAgencyIDValid = mDBExternalHelper.IsValidTruckID(Truck_ID);
            if (isAgencyIDValid == false) {
                Common.TrucklicensePlateNo = TruckDetialsATXT.getText().toString().trim();
                Common.Purchase.SelectedReceivedTruckID = 0;
            }
        }
        return Validattion;
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

    public void Signout(String ErrorMessage) {
        if (Common.IsEditorViewFlag == false) {
            //InventoryActivty();
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage(ErrorMessage);
            builder1.setCancelable(true);
            builder1.setPositiveButton("No",
                    (dialog, id) -> dialog.cancel());
            builder1.setNegativeButton("Yes",
                    (dialog, id) -> {
                        try {
                            //UpdateReceivedIDList();
                            //InventoryActivty();
                        } catch (Exception ex) {
                            AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
                        }
                        dialog.cancel();
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

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
            try {
                Common.ScanMode = false;
                Common.QRCodeScan = false;
                if (ReceivedDetailsValidation(TransferIDAutoTXT.getText().toString(), FromLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
                    Common.HideKeyboard(getActivity());
                    String SBBLabel = ScanValueETxT.getText().toString();
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

    public double ReceivedTotalVolume(ArrayList<InventoryReceivedModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (InventoryReceivedModel inventoreceScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(inventoreceScanModel.getVolume());
        }
        return TotVolume;
    }

    public class InventoryReceivedDetailsAdapter extends RecyclerView.Adapter<InventoryReceivedDetailsAdapter.InventoryReceivedDetailsViewHolder> {

        private Context mCtx;
        private List<ReceivedLogsModel> ReceivedLogsModelList;

        InventoryReceivedDetailsAdapter(Context mCtx, List<ReceivedLogsModel> ReceivedLogsModelList) {
            this.mCtx = mCtx;
            this.ReceivedLogsModelList = ReceivedLogsModelList;
        }

        public ReceivedLogsModel getItem(int position) {
            return ReceivedLogsModelList.get(position);
        }

        @NonNull
        @Override
        public InventoryReceivedDetailsAdapter.InventoryReceivedDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mCtx).inflate(R.layout.received_details_layout_infliator, parent, false);
            return new InventoryReceivedDetailsAdapter.InventoryReceivedDetailsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(InventoryReceivedDetailsAdapter.InventoryReceivedDetailsViewHolder holder, int position) {
            ReceivedLogsModel ReceivedLogsListModel = ReceivedLogsModelList.get(position);
            holder.TxTSbbLabel.setText(ReceivedLogsListModel.getBarCode());
            holder.TxTwsc.setText(ReceivedLogsListModel.getWoodSpecieCode());
            holder.TxTfs.setText(String.valueOf(ReceivedLogsListModel.getFellingSection()));
            holder.TxTtn.setText(String.valueOf(ReceivedLogsListModel.getTreeNumber()));
            holder.TxTrs.setText(String.valueOf(ReceivedLogsListModel.getRetributionStatus()));
            holder.TxTee.setText(String.valueOf(ReceivedLogsListModel.getExportExamination()));

            if (position % 2 == 0) {
                holder.backgroundLayout.setBackgroundColor(mCtx.getResources().getColor(R.color.green));
            } else {
                holder.backgroundLayout.setBackgroundColor(mCtx.getResources().getColor(R.color.color_white));
            }
        }

        @Override
        public int getItemCount() {
            return ReceivedLogsModelList.size();
        }

        class InventoryReceivedDetailsViewHolder extends RecyclerView.ViewHolder {

            TextView TxTSbbLabel, TxTwsc, TxTfs, TxTtn, TxTrs, TxTee;
            LinearLayout backgroundLayout;

            InventoryReceivedDetailsViewHolder(View itemView) {
                super(itemView);
                TxTSbbLabel = itemView.findViewById(R.id.receivedLogs_label);
                TxTwsc = itemView.findViewById(R.id.receivedLogs_wsc);
                TxTfs = itemView.findViewById(R.id.receivedLogs_fs);
                TxTtn = itemView.findViewById(R.id.receivedLogs_tn);
                TxTrs = itemView.findViewById(R.id.receivedLogs_rs);
                TxTee = itemView.findViewById(R.id.receivedLogs_ee);
                backgroundLayout = itemView.findViewById(R.id.rl_backgroundLayout);
            }
        }
    }

    public void QrScanDetails() {
        if (Common.Purchase.IsReceivedEditorViewFlag == true) {
            if (MasterDataBaseCheckValidation(Common.RecFromLocationID, Common.Purchase.SelectedReceivedAgencyID, Common.Purchase.SelectedReceivedDriverID, Common.Purchase.SelectedReceivedTruckID, Common.Purchase.SelectedReceivedTransPortMode)) {
            } else {
                return;
            }
        }
        /*18-09-2019-Implemented Values for ID in header QR code*/
        FromLocationATXT.setText(Common.RecFromLocationname);
        AgencyATXT.setText(Common.AgencyName);
        DriverATXT.setText(Common.DriverName);
        TruckDetialsATXT.setText(Common.TrucklicensePlateNo);
        transportAdapter.notifyDataSetChanged();
        vbb_txt.setText(Common.VBB_Number);
        UpdateReceivedIDList();
    }

    private void UpdateReceivedIDList() {
        Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
        mDBInternalHelper.UpdatePurchaseAgreementReceivedHeader(Common.Purchase.SelectedReceivedLoadedby, Common.Purchase.SelectedReceivedFromLocationID, Common.Purchase.SelectedReceivedToLocationID, Common.Purchase.SelectedReceivedAgencyID, Common.Purchase.SelectedReceivedDriverID, Common.Purchase.SelectedReceivedTruckID,
                Common.Purchase.SelectedPurchaseId,Common.DateTime,Common.Purchase.SelectedReceivedTransPortMode);
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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(ErrorMessage);
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK",
                (dialog, id) -> {
                    //InventoryActivty();
                    dialog.cancel();
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void ScanedQRBarcodeDetails(String[] ScannedArryValue) {
        try {
            Common.CheckedFlag = "NO";
            // Checked Duplicate In Internal Tabel
            for (int i = 0; i < ScannedArryValue.length; i++) {
                if (i == 0) {
                    Common.TransferReciveUniqueID = ScannedArryValue[0];
                } else {
                    String[] BarcodeSplite = ScannedArryValue[i].split("-");
                    Common.QualityName = BarcodeSplite[0];
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
                        //Update If Already in ListView
                        boolean ResultFlag = mDBInternalHelper.UpdateInventoryReceivedItemsPuchaseID(Common.Purchase.SelectedPurchaseId, Common.BarCode, Common.CheckedFlag);
                        if (ResultFlag == true) {
                            //Scanned Result Refresh
                            GetReceivedScannedResultList();
                        }
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
                        if (WSCNotEqualFlags == true) {
                            wronBuzzer.start();
                            AlertDialogBox(CommonMessage(R.string.TransferHead), "WSC not match with purchase number", false);
                            return;
                        }
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
                        //sbbLabel_TXT.setText(Common.BarCode);
                        //return;
                    }
                    boolean ResultFlag = mDBInternalHelper.insertInventoryReceivedItemsFlagWithPurchaseID(Common.VBB_Number, Common.TransferID, Common.ReceivedID, Common.RecFromLocationname, Common.ToLocationName,
                            Common.SbbLabel, Common.BarCode, Common.Length, Common.Volume, Common.UserID, Common.DateTime, Common.WoodSpieceID, Common.WoodSpieceCode, Common.EntryMode,
                            Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), "", Common.FellingSectionId, Common.TreeNumber,
                            Common.QualityName, Common.CheckedFlag, Common.TransferReciveUniqueID, Common.Purchase.SelectedPurchaseId);
                }
                //}
            }
            beepsound.start();
            // Update header in purchase table
            UpdateReceivedIDList();
            //Scanned Result Refresh
            GetReceivedScannedResultList();
        } catch (
                Exception ee) {
            AlertDialogBox("ScanQR Barcode Details", ee.toString(), false);
        }
    }
}