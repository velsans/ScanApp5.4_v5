package com.zebra.main.activity.purchase.ui.transfer;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
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
import com.zebra.main.activity.Common.GwwMainActivity;
import com.zebra.main.activity.Transfer.InventoryTransferActivity;
import com.zebra.main.adapter.CustomAdapter;
import com.zebra.main.adapter.DriverAdapter;
import com.zebra.main.adapter.FellingSectionAdapter;
import com.zebra.main.adapter.TruckAdapter;
import com.zebra.main.api.ApiClient;
import com.zebra.main.api.ApiInterface;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.model.InvTransfer.InventoryTransferScannedResultModel;
import com.zebra.main.model.InvTransfer.InventoryTransferSyncModel;
import com.zebra.main.model.externaldb.AgencyDetailsModel;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TransferFragment extends Fragment implements SdlScanListener, ConnectivityReceiver.ConnectivityReceiverListener {

    View TransferView;
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
    //BarCodeReceiver barcode_receiver;
    boolean isInternetPresent;
    private InventoryTransferAdapter transferadapter;
    LinearLayoutManager horizontalLayoutManager;
    private RecyclerView ScannedResultLV;
    Spinner ToLocationSpin, TransportAgencySpin, TruckPlateNumberSpin, FellingSecSpinner, DriverSpin;
    AutoCompleteTextView ToLocationATXT, DriverATXT, AgencyATXT, TruckDetialsATXT;
    FloatingActionButton TransferScanBtn;
    ListView TransferMode_LV, LoadedBy_LV;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;
    String TreeNO, FellingID;
    private EditText ScanValueETxT = null, treeNoEDT, fellingIDEDT;
    private TextView TotalScannedCount, sbbLabel_TXT, NoValueFoundTxT, vbb_txt, FromLocationTxT, VolumeTXT, TransportTypeIDTXT, TransferTopicsTxT;
    private LinearLayout scannerListLay = null, EditOptionFlagLay, EditOptionFlagLay2;
    private ImageView image = null;
    AlertDialog.Builder Removebuilder = null;
    AlertDialog RemoveAlert = null;
    PrintSlipsClass printSlip;
    FellingSectionAdapter fellingSectionAdapter;
    TransportAdapterInside transportAdapter;
    LoadedAdapterInside loadedAdapter;
    CustomAdapter customAdapter;
    TruckAdapter truckAdapter;
    DriverAdapter driverAdapter;
    StringtreeNoadapter StringtreeNoadapter;
    DriverDetailsModel drivermodelArray;
    AgencyDetailsModel agencyModelArray;
    TruckDetailsModel truckModelArray;
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    TSCActivity tsc = new TSCActivity();
    InventoryTransferSyncModel transferSyncModel;
    ApiInterface InvenTransAPI = null;
    GwwMainActivity gwwMain;

    private class BarCodeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            GetTransferScannedResultList();
        }
    }

    public TransferFragment() {
        FragContext = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        beepsound = MediaPlayer.create(getActivity(), R.raw.beep);
        wronBuzzer = MediaPlayer.create(getActivity(), R.raw.wrong_buzzer);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (scanService != null)
                scanService.setActivityUp(false);
            releaseWakeLock();
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
        GetTransferScannedResultList();
        super.onResume();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //transferViewModel = ViewModelProviders.of(this).get(TransferViewModel.class);
        TransferView = inflater.inflate(R.layout.fragment_transfer, container, false);
        mDBExternalHelper = new ExternalDataBaseHelperClass(getActivity());
        mDBInternalHelper = new InternalDataBaseHelperClass(getActivity());
        printSlip = new PrintSlipsClass(getActivity());
        InvenTransAPI = ApiClient.getInstance().getUserService();
        gwwMain = new GwwMainActivity(getActivity());
        Initialization();
        //View existing ListView
        ViewTransfeList(Common.Purchase.IsTransferEditorViewFlag);
        getToLocationsforSpinner();
        getdriverforSpinner();
        getagencyforSpinner();
        gettruckforSpinner();
        gettransModeforView();
        getLoadedByView();
        Common.HideKeyboard(getActivity());
        //enableSwipeToDeleteAndUndo();
        vbb_txt.setText(Common.VBB_Number);
        if (Common.Purchase.IsTransferEditorViewFlag == false) {
            TransferEditValues();
        }
        Click_Listener();
        GetTransferScannedResultList();
        TransferTopicsTxT.setText("EXTERNAL PURCHASE TRANSFER");
        return TransferView;
    }

    public void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
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
            scanService.setOnScanListener(TransferFragment.this);
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
                boolean EmptyNullFlags = mDBInternalHelper.getInventoryTransferduplicateCheckForPurchaseID(Common.Purchase.SelectedPurchaseId, Common.BarCode);
                if (EmptyNullFlags == true) {
                    wronBuzzer.start();
                    AlertDialogBox(CommonMessage(R.string.TransferHead), "Already exsist please try some other logs", false);
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
                    if (WSCNotEqualFlags == true) {
                        wronBuzzer.start();
                        AlertDialogBox(CommonMessage(R.string.TransferHead), "WSC not match with purchase number", false);
                        return;
                    }
                } else {
                    wronBuzzer.start();
                    AlertDialogBox(CommonMessage(R.string.TransferHead), "Barcode not belongs to external purchase, please try another one", false);
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
                InsertTransferScannedResultTable();
                Common.EntryMode = 1;
                Common.ScannedEditTXTFlag = false;
                ScanValueETxT.setText(s);
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
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

    private void GetTransferScannedResultList() {
        try {
            Common.InventorytransferScannedResultList = mDBInternalHelper.getInventoryTransferWithPurchaseID(Common.Purchase.SelectedPurchaseId);
            //String TotalSannedSize = mDBInternalHelper.ColculateInventoryTransferItems(Common.TransferID);
            Double RemoveVolumeSum = 0.00;
            if (Common.InventorytransferScannedResultList.size() > 0) {
                transferadapter = new InventoryTransferAdapter(Common.InventorytransferScannedResultList, Common.QulaityDefaultList, getActivity());
                horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                ScannedResultLV.setLayoutManager(horizontalLayoutManager);
                transferadapter.notifyDataSetChanged();
                ScannedResultLV.setAdapter(transferadapter);
                ScannedResultLV.setVisibility(View.VISIBLE);
                RemoveVolumeSum = TotalVolume(Common.InventorytransferScannedResultList);
            } else {
                ScannedResultLV.setVisibility(View.GONE);
                // Update StartDate
                Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                mDBInternalHelper.UpdatePurchaseAgreementTransferStartDate(Common.DateTime, Common.Purchase.SelectedPurchaseId);
            }
            TotalScannedCount.setText(String.valueOf(Common.InventorytransferScannedResultList.size()));
            VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(RemoveVolumeSum)));
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
            holder.Remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.IsEditorViewFlag == false) {
                        AlertDialogBox(CommonMessage(R.string.TransferHead), "Can not edit Or delete after Synced", false);
                    } else {
                        Common.RemoveSBBLabel = "";
                        Common.RemoveSBBLabel = ScannedResultList.get(position).getBarCode();
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
                        boolean Isdelete = mDBInternalHelper.RemovePurchasetransferlistview(Common.RemoveSBBLabel, 0, Common.Purchase.SelectedPurchaseId);
                        if (Isdelete == true) {
                            Toast.makeText(getActivity(), "Successfully Removed from List", Toast.LENGTH_LONG).show();
                            GetTransferScannedResultList();
                        }
                    } catch (Exception Ex) {

                    }
                    dialog.cancel();
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public void InsertTransferScannedResultTable() {
        beepsound.start();
        // Insert values into DB
        mDBInternalHelper.insertInventoryTransferResultFlag(Common.VBB_Number, Common.TransferID, Common.FromLocationname, Common.ToLocationName,
                Common.SbbLabel, Common.BarCode, Common.Length, Common.Volume, Common.UserID, Common.DateTime, Common.WoodSpieceID,
                Common.WoodSpieceCode, Common.EntryMode, Common.IsActive, booleanToInt(Common.IsSBBLabelCorrected), "", Common.FellingSectionId,
                Common.TreeNumber, Common.QualityName, Common.HoleVolume, Common.GrossVolume, Common.Purchase.PurchaseID);
        // Update header in purchase table
        mDBInternalHelper.UpdatePurchaseAgreementTransferHeader(Common.Purchase.SelectedTransLoadedby, Common.Purchase.SelectedTransFromLocationID,
                Common.Purchase.SelectedTransToLocationID, Common.Purchase.SelectedTransAgencyID, Common.Purchase.SelectedTransDriverID,
                Common.Purchase.SelectedTransTruckID, Common.Purchase.SelectedPurchaseId, Common.DateTime, Common.Purchase.SelectedTransTransPortMode);
        GetTransferScannedResultList();
    }

    public static int booleanToInt(boolean value) {
        // Convert true to 1 and false to 0.
        return value ? 1 : 0;
    }

    private void Initialization() {
        TransferScanBtn = TransferView.findViewById(R.id.TransferScanBTN);
        TransferView.findViewById(R.id.TransferScanBTN).setOnClickListener(onClickListener);
        TransferView.findViewById(R.id.transferEnterIMG).setOnClickListener(onClickListener);
        //TransferView.findViewById(R.id.closeInventoryTransfer).setOnClickListener(onClickListener);
        TransferView.findViewById(R.id.transfer_printTXT).setOnClickListener(onClickListener);
        scannerListLay = TransferView.findViewById(R.id.scannerListlayout);
        ScanValueETxT = TransferView.findViewById(R.id.scanValueEDTXT);
        TotalScannedCount = TransferView.findViewById(R.id.TotalScannedCount);
        ScannedResultLV = TransferView.findViewById(R.id.ptd_Transfer_lv);

        ToLocationSpin = TransferView.findViewById(R.id.tolocation_spinner);
        DriverSpin = TransferView.findViewById(R.id.driver_sppinner);
        TransportAgencySpin = TransferView.findViewById(R.id.transagency_spinner);
        TruckPlateNumberSpin = TransferView.findViewById(R.id.truckplateumber_spinner);
        FellingSecSpinner = TransferView.findViewById(R.id.fellingsection_spinner);

        ToLocationATXT = TransferView.findViewById(R.id.tolocation_ATxT);
        DriverATXT = TransferView.findViewById(R.id.driver_ATxT);
        AgencyATXT = TransferView.findViewById(R.id.transagency_ATxT);
        TruckDetialsATXT = TransferView.findViewById(R.id.truckplateumber_ATxT);

        TransferMode_LV = TransferView.findViewById(R.id.transferMode_LV);
        LoadedBy_LV = TransferView.findViewById(R.id.loadedby_LV);
        vbb_txt = TransferView.findViewById(R.id.VBB_numberTXT);
        FromLocationTxT = TransferView.findViewById(R.id.fromLocationTXT);
        VolumeTXT = TransferView.findViewById(R.id.TotalScannedVolumeTransfer);

        //TransferView.findViewById(R.id.transferNXTtxt).setOnClickListener(onClickListener);

        treeNoEDT = TransferView.findViewById(R.id.as_treenoEDT);
        fellingIDEDT = TransferView.findViewById(R.id.as_fellingEDT);
        sbbLabel_TXT = TransferView.findViewById(R.id.sbbLabel_TXT);
        NoValueFoundTxT = TransferView.findViewById(R.id.NovalueFound);
        EditOptionFlagLay = TransferView.findViewById(R.id.transfer_editLayout);
        EditOptionFlagLay2 = TransferView.findViewById(R.id.transfer_editLayout2);
        TransportTypeIDTXT = TransferView.findViewById(R.id.TransportTypeIDTXT);
        TransferTopicsTxT = TransferView.findViewById(R.id.TransferTopicsTXT);
        //TransferView.findViewById(R.id.transfer_syncTXT).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = v -> {
        try {
            switch (v.getId()) {
                case R.id.TransferScanBTN:
                    ScanValueETxT.setText("");
                    Common.ScanMode = true;
                    Common.ScannedEditTXTFlag = false;
                    Common.EntryMode = 1;
                    if (DetailsValidation(Common.FromLocationname, ToLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
                        scanService.doDecode();
                    }
                    break;
                case R.id.transferEnterIMG:
                    try {
                        Common.ScanMode = false;
                        if (DetailsValidation(Common.FromLocationname, ToLocationATXT.getText().toString(), AgencyATXT.getText().toString(), DriverATXT.getText().toString(), TruckDetialsATXT.getText().toString())) {
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
                    break;
                case R.id.transfer_printTXT:
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
                                        //TransferListPrintHan.post(TransferListPrintRun);
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

                /*case R.id.transfer_syncTXT:
                    InventoryTransferSync();
                    break;*/
            }
        } catch (Exception ex) {
            AlertDialogBox("OonClickListener", ex.toString(), false);
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

    public void ViewTransfeList(boolean EdtFlag) {
        if (EdtFlag == false) {
            EditOptionFlagLay.setVisibility(View.VISIBLE);
            EditOptionFlagLay2.setVisibility(View.VISIBLE);
            TransferScanBtn.setEnabled(false);
            //TransferView.findViewById(R.id.transferNXTtxt).setEnabled(false);
            //TransferView.findViewById(R.id.advancedsearchTXT).setEnabled(false);
            ScanValueETxT.setEnabled(false);
            TransferView.findViewById(R.id.transferEnterIMG).setEnabled(false);
            TransferView.findViewById(R.id.transfer_printTXT).setEnabled(false);
        } else {
            EditOptionFlagLay.setVisibility(View.GONE);
            EditOptionFlagLay2.setVisibility(View.GONE);
            TransferScanBtn.setEnabled(true);
            //TransferView.findViewById(R.id.transferNXTtxt).setEnabled(true);
            //TransferView.findViewById(R.id.advancedsearchTXT).setEnabled(true);
            ScanValueETxT.setEnabled(true);
            TransferView.findViewById(R.id.transferEnterIMG).setEnabled(true);
            TransferView.findViewById(R.id.transfer_printTXT).setEnabled(true);
        }
    }

    private void getToLocationsforSpinner() {
        try {
            // FromLocation Name from FromLocationID
            Common.ConcessionList.clear();
            Common.ConcessionList = mDBExternalHelper.getAllConcessionNames(Common.FromLocationID);
            if (Common.ConcessionList.size() > 0) {
                Common.FromLocationname = Common.ConcessionList.get(0).getConcessionName();
                FromLocationTxT.setText(Common.FromLocationname);
            }
            // Felling Section Spinner
            int LocationTypeID = Common.ConcessionList.get(0).getLocationType();
            //int LocationTypeID = mDBExternalHelper.getLocationType(Common.FromLocationID);
            if (LocationTypeID == 1) {
                Common.FellingSectionList.clear();
                Common.FellingSectionList = mDBExternalHelper.getFellingSectionDetails(Common.FromLocationID);
                fellingSectionAdapter = new FellingSectionAdapter(getActivity(), Common.FellingSectionList);
                FellingSecSpinner.setAdapter(fellingSectionAdapter);
                FellingSecSpinner.setVisibility(View.GONE);
            } else {
                FellingSecSpinner.setVisibility(View.GONE);
            }
            // To Location Spinner
            Common.LocationList.clear();
            Common.LocationList = mDBExternalHelper.getToLocations();
            customAdapter = new CustomAdapter(getActivity(), Common.LocationList);
            ToLocationSpin.setAdapter(customAdapter);

            Common.ConcessionListStringList = new String[Common.LocationList.size()];
            for (int i = 0; i < Common.LocationList.size(); i++) {
                Common.ConcessionListStringList[i] = Common.LocationList.get(i).getLocation();
            }
            StringtreeNoadapter = new StringtreeNoadapter(getActivity(),
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
            TransportTypeIDTXT.setText(Common.TransportMode);
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

        EditOptionFlagLay.setOnClickListener(v -> AlertDialogBox(CommonMessage(R.string.TransferHead), "Can not edit Or delete after Synced", false));

        EditOptionFlagLay2.setOnClickListener(v -> AlertDialogBox(CommonMessage(R.string.TransferHead), "Can not edit Or delete after Synced", false));

        /*AutoComplete*/
        ToLocationATXT.setOnTouchListener((v, event) -> {
            ToLocationATXT.requestFocus();
            ToLocationATXT.showDropDown();
            return false;
        });

        ToLocationATXT.setOnItemClickListener((parent, view, position, id) -> {
            String locModelStr = (String) parent.getItemAtPosition(position);
            for (LocationsModel locModel : Common.LocationList)
                if (locModelStr.equals(locModel.getLocation())) {
                    Common.Purchase.SelectedTransToLocationID = locModel.getToLocationId();
                    Common.ToLocationName = locModel.getLocation();
                }
        });

        DriverATXT.setOnTouchListener((v, event) -> {
            DriverATXT.requestFocus();
            DriverATXT.showDropDown();
            return false;
        });

        DriverATXT.setOnItemClickListener((parent, view, position, id) -> {
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
                        Common.Purchase.SelectedTransDriverID = drivermodelArray.getTruckDriverId();
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
                        Common.Purchase.SelectedTransAgencyID = agencyModelArray.getAgencyId();
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
                        Common.Purchase.SelectedTransTruckID = truckModelArray.getTransportId();
                        TruckDetialsATXT.setText(truckStr);
                    }
                }
            } catch (Exception ex) {

            }
        });
    }

    public void TransferEditValues() {
        try {
            // FromLocation Name from FromLocationID
            Common.ConcessionList.clear();
            Common.ConcessionList = mDBExternalHelper.getAllConcessionNames(Common.Purchase.SelectedTransFromLocationID);
            if (Common.ConcessionList.size() > 0) {
                Common.FromLocationname = Common.ConcessionList.get(0).getConcessionName();
                FromLocationTxT.setText(Common.FromLocationname);
            }
            for (LocationsModel item : Common.LocationList) {
                if (item.getToLocationId() == Common.Purchase.SelectedTransToLocationID) {
                    //ToLocationSpin.setSelection(item.getID());
                    ToLocationATXT.setText(item.getLocation());
                }
            }
            for (DriverDetailsModel item : Common.DriverList) {
                if (item.getTruckDriverId() == Common.Purchase.SelectedTransDriverID) {
                    //DriverSpin.setSelection(item.getID());Common.AgencyName
                    Common.DriverName = item.getDriverName();
                    DriverATXT.setText(item.getDriverName());
                }
            }
            for (AgencyDetailsModel item : Common.TransportAgencyList) {
                if (item.getAgencyId() == Common.Purchase.SelectedTransAgencyID) {
                    //TransportAgencySpin.setSelection(item.getID());
                    Common.AgencyName = item.getAgencyName();
                    AgencyATXT.setText(item.getAgencyName());
                }
            }
            for (TruckDetailsModel item : Common.TruckDeatilsList) {
                if (item.getTransportId() == Common.Purchase.SelectedTransTruckID) {
                    //TruckPlateNumberSpin.setSelection(item.getID());
                    Common.TrucklicensePlateNo = item.getTruckLicensePlateNo();
                    TruckDetialsATXT.setText(item.getTruckLicensePlateNo());
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
            if (Common.Purchase.SelectedTransLoadedby == modename.get(position).getTransportTypeId()) {
                TransferMode.setChecked(true);
                Common.TransportMode = modename.get(position).getTransportMode();
            } else {
                TransferMode.setChecked(false);
            }
            TransferMode.setText(modename.get(position).getTransportMode());
            TransferMode.setOnClickListener(v -> {
                Common.Purchase.SelectedTransTransPortMode = modename.get(position).getTransportTypeId();
                Common.TransportMode = modename.get(position).getTransportMode();
                notifyDataSetChanged();
            });

            TransferMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Common.Purchase.SelectedTransTransPortMode = modename.get(position).getTransportTypeId();
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
            if (Common.Purchase.SelectedTransLoadedby == 0) {
                Common.Purchase.SelectedTransLoadedby = 2;
            }
            if (Common.Purchase.SelectedTransLoadedby == modename.get(position).getLoadedid()) {
                LoadedMode.setChecked(true);
                Common.LoadedName = modename.get(position).getName();
            } else {
                LoadedMode.setChecked(false);
            }
            LoadedMode.setText(modename.get(position).getName());
            LoadedMode.setOnClickListener(v -> {
                Common.Purchase.SelectedTransLoadedby = modename.get(position).getLoadedid();
                Common.LoadedName = modename.get(position).getName();
                notifyDataSetChanged();
            });

            LoadedMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Common.Purchase.SelectedTransLoadedby = modename.get(position).getLoadedid();
                Common.LoadedName = modename.get(position).getName();
                notifyDataSetChanged();
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
                    LayoutInflater inflater = ((InventoryTransferActivity) mContext).getLayoutInflater();
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
                    LayoutInflater inflater = ((InventoryTransferActivity) mContext).getLayoutInflater();
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
                    LayoutInflater inflater = ((InventoryTransferActivity) mContext).getLayoutInflater();
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

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

    public void PrintoutEnd() {
        tsc.closeport();
        TSCConnInt = -1;
        Common.IsPrintBtnClickFlag = true;
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
                tsc.sendcommand(printSlip.TransferDetails19_09_2019());
                tsc.sendcommand(printSlip.TransferListDimensions18_Nov_2019());
                tsc.clearbuffer();
                if (Common.InventorytransferScannedResultList.size() > (Common.VVBLimitation + 2)) {
                } else {
                    tsc.sendcommand(printSlip.TransferFooter());
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

    public void InventoryTransferSync() {
        Common.InventoryTransferInputList.clear();
       // Common.InventoryTransferInputList = mDBInternalHelper.getTransferScannedResultInputWithPurchaseID(Common.Purchase.SelectedPurchaseId);
        if (Common.InventoryTransferInputList.size() > 0) {
            if (checkConnection() == true) {
                getInventoryTransferSyncStatusApi();
            }
        } else {
            AlertDialogBox("InventoryTransfer Sync", "Values are empty", false);
        }
    }

    // 2-12-2019 Added InventoryTransferSyncStatusApi impl
    private void getInventoryTransferSyncStatusApi() {
        try {
            //ProgressBarLay.setVisibility(View.VISIBLE);
            transferSyncModel = new InventoryTransferSyncModel();
            transferSyncModel.setTransferID(0);
            transferSyncModel.setVBBNumber("0");
            transferSyncModel.setIMEINumber(Common.IMEI);
            transferSyncModel.setStartTime(Common.Purchase.SelectedTransStatrDate);
            transferSyncModel.setEndTime(Common.Purchase.SelectedTransStatrDate);
            transferSyncModel.setUserID(Common.UserID);
            transferSyncModel.setTransferUniqueID("0");
            transferSyncModel.setLocationID(Common.Purchase.SelectedTransFromLocationID);
            transferSyncModel.setToLocationID(Common.Purchase.SelectedTransToLocationID);//ToLocaTransID
            transferSyncModel.setAgencyName(Common.AgencyName);
            transferSyncModel.setDriverName(Common.DriverName);
            transferSyncModel.setTruckPlateNumber(Common.TrucklicensePlateNo);

            transferSyncModel.setTransferAgencyId(Common.Purchase.SelectedTransAgencyID);
            transferSyncModel.setDriverId(Common.Purchase.SelectedTransDriverID);
            transferSyncModel.setTruckId(Common.Purchase.SelectedTransTruckID);
            transferSyncModel.setLoadedTypeID(Common.Purchase.SelectedTransLoadedby);
            transferSyncModel.setTransferModeID(Common.Purchase.SelectedTransTransPortMode);

            transferSyncModel.setHHInventoryTransfer(Common.InventoryTransferInputList);
            transferSyncModel.setTranferredCount(Common.InventoryTransferInputList.size());
            //transferSyncModel.setPurchaseId(Common.Purchase.SelectedPurchaseId);

            InvenTransAPI = ApiClient.getApiInterface();
            InvenTransAPI.getPurchaseTransferSync(transferSyncModel).enqueue(new Callback<InventoryTransferSyncModel>() {
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
                                    boolean ListIdFlag = mDBInternalHelper.UpdatePurchaseTransferSyncStatus(Common.SyncTime, 1, Common.TransferUniqueID,
                                            Common.SyncStatusList.get(0).getTransferAgencyId(), Common.SyncStatusList.get(0).getDriverId(), Common.SyncStatusList.get(0).getTruckId());
                                    if (ListIdFlag == true) {
                                        //Scanned Result Refresh
                                        GetTransferScannedResultList();
                                        AlertDialogBox(CommonMessage(R.string.TransferHead), "#" + String.valueOf(Common.Purchase.SelectedPurchaseId) + "--" + Common.SyncStatusList.get(0).getMessage(), true);
                                        if (Common.SyncStatusList.get(0).getTransferAgencyId() != 0 || Common.SyncStatusList.get(0).getDriverId() != 0 || Common.SyncStatusList.get(0).getTruckId() != 0) {
                                            Common.IsExternalSyncFlag = true;
                                            Common.IsProgressVisible = false;
                                            Common.ExternalSyncFlag = false;
                                            gwwMain.ExternalDataBaseSync();
                                        }
                                    }
                                } else {
                                    AlertDialogBox(CommonMessage(R.string.TransferHead), "#" + Common.TransferID + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                                }
                            } else {
                                AlertDialogBox(CommonMessage(R.string.TransferHead), "#" + Common.TransferID + "--" + "Not Synced", false);
                            }
                        } else {
                            Common.AuthorizationFlag = true;
                            AlertDialogBox(CommonMessage(R.string.TransferHead), response.message(), false);
                        }

                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        AlertDialogBox(CommonMessage(R.string.TransferHead), "#" + Common.TransferID + "--" + ex.getMessage(), false);
                    }
                }

                @Override
                public void onFailure(Call<InventoryTransferSyncModel> call, Throwable t) {
                    //ProgressBarLay.setVisibility(View.GONE);
                    AlertDialogBox(CommonMessage(R.string.TransferHead), "#" + Common.TransferID + "--" + t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            //ProgressBarLay.setVisibility(View.GONE);
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox(CommonMessage(R.string.TransferHead), ex.toString(), false);
        }
    }
}