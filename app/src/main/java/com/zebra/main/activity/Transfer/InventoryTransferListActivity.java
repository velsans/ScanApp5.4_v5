package com.zebra.main.activity.Transfer;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tscdll.TSCActivity;
import com.google.android.material.snackbar.Snackbar;
import com.zebra.R;
import com.zebra.adc.decoder.BarCodeReader;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.activity.Common.GwwMainActivity;
import com.zebra.main.api.ApiClient;
import com.zebra.main.api.ApiInterface;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.model.InvTransfer.InventoryTransferModel;
import com.zebra.main.model.InvTransfer.InventoryTransferScannedResultModel;
import com.zebra.main.model.InvTransfer.InventoryTransferSyncModel;
import com.zebra.main.model.SyncStatusModel;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryTransferListActivity extends AppCompatActivity implements SdlScanListener, ConnectivityReceiver.ConnectivityReceiverListener {
    private static final String TAG = "Inventory Transfer";
    private InternalDataBaseHelperClass mDBInternalHelper;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    /*Inventory Count*/
    InventoryTransferAdapter invenTransadapter;
    InventoryTransferDateAdapter invenTransferDateadapter;
    LinearLayoutManager horizontalLayoutManager, InveDateLayoutManager;
    RecyclerView InventoryTransList, InventoryTransDateList;
    CheckBox SyncAllTransferCheckBox;
    InventoryTransferSyncModel transferSyncModel;
    SyncStatusModel suncStaModel;
    TextView TotalFilteredCount, TotalFilteredVolume, NoValueFoundTxT, TransferTopicTxT;
    LinearLayout ProgressBarLay;
    AlertDialogManager alert = new AlertDialogManager();
    boolean isInternetPresent;
    AlertDialog.Builder Signoutbuilder = null;
    AlertDialog SignoutAlert = null;
    private BarCodeReader bcr = null;
    private PowerManager.WakeLock mWakeLock = null;
    private int motionEvents = 0, modechgEvents = 0, snapNum = 0;
    boolean bind = false;
    private SdlScanService scanService;
    MediaPlayer beepsound, wronBuzzer;
    Intent service;
    private ImageView image = null;
    TSCActivity tsc = new TSCActivity();
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    PrintSlipsClass printSlip;
    EditText submitVVPEDT, submitOldTransEDT;
    Button submitVBBTXT;
    ApiInterface InvenTransAPI = null;
    GwwMainActivity gwwMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_trans_list);
        mDBExternalHelper = new ExternalDataBaseHelperClass(this);
        mDBInternalHelper = new InternalDataBaseHelperClass(this);
        gwwMain = new GwwMainActivity(this);
        printSlip = new PrintSlipsClass(this);
        Initialization();
        OnClickListener();
        Common.InventTransDateSelectedIndex = 0;
        GetInventoryTransferDateList();
        TransferTopicTxT.setText("INVENTORY TRANSFER - " + Common.TransportMode);
        InvenTransAPI = ApiClient.getInstance().getUserService();
    }

    @Override
    public void onBackPressed() {
        Intent oneIntent = new Intent(this, GwwMainActivity.class);
        startActivity(oneIntent);
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
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            //UpdateTransferIDList();
            //scanService.release();
            if (scanService != null)
                scanService.setActivityUp(false);
            releaseWakeLock();
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void OnClickListener() {
        SyncAllTransferCheckBox.setOnClickListener(v -> Common.InventoryTransferSyncALL = SyncAllTransferCheckBox.isChecked());

        SyncAllTransferCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> Common.InventoryTransferSyncALL = SyncAllTransferCheckBox.isChecked());
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
            //
            scanService.setOnScanListener(InventoryTransferListActivity.this);
            scanService.setActivityUp(true);
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
    protected void onResume() {
        try {
            service = new Intent(this, SdlScanService.class);
            bindService(service, serviceConnection, BIND_AUTO_CREATE);
            startService(service);
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
        try {
            ScannedStatus("");
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (scanService != null)
            scanService.setActivityUp(true);
        acquireWakeLock();
        GetInventoryTransferDateList();
        super.onResume();
    }

    public void Initialization() {
        ProgressBarLay = findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        InventoryTransList = findViewById(R.id.inventoryTransListView);
        InventoryTransDateList = findViewById(R.id.inventoryTransListViewDate);
        NoValueFoundTxT = findViewById(R.id.NovalueFound);
        TotalFilteredCount = findViewById(R.id.TotalFilteredCount);
        TotalFilteredVolume = findViewById(R.id.TotalFilteredVolume);
        SyncAllTransferCheckBox = findViewById(R.id.SyncAllTransfer_checkBox);
        submitVBBTXT = findViewById(R.id.submitVBBTxT);
        submitVVPEDT = findViewById(R.id.submitVVPEdT);
        submitOldTransEDT = findViewById(R.id.submitTrabsferEDT);
        findViewById(R.id.submitVBBTxT).setOnClickListener(mSubmitVVPListen);
        findViewById(R.id.submitTransBtn).setOnClickListener(mSubmitOldTransferListen);
        findViewById(R.id.OldTrans_scanQROCde).setOnClickListener(mOldBarCodeScanListen);
        findViewById(R.id.transfer_syncAllIMG).setOnClickListener(mTransfer_SyncAll);
        TransferTopicTxT = findViewById(R.id.TransferTopicTXT);
    }

    View.OnClickListener mTransfer_SyncAll = v -> {
        try {
            InventoryTransferSyncALLDialog("Are you sure want to sync all datas?");
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("IA-mTransfer_SyncAll", ex.toString(), false);
        }
    };

    View.OnClickListener mSubmitVVPListen = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Common.LoadedTypeID = 2;
                Common.SyncTime = "";
                Common.VolumeSum = 0.0;
                Common.VBB_Number = submitVVPEDT.getText().toString();
                Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                Common.FromTransLocID = Common.FromLocationID;
                if (Common.VBB_Number.length() > 0) {
                    boolean DuplicateFlag = mDBInternalHelper.getInventoryTransferIdListDuplicateCheck(Integer.parseInt(Common.VBB_Number));
                    if (DuplicateFlag == true) {
                        AlertDialogBox("Inventory Transfer List", "please add diff VBB Number", false);
                        return;
                    }
                }
                boolean ListIdFlag = mDBInternalHelper.insertInventoryTransferID(Common.VBB_Number, Common.IMEI, Common.ToLocaTransID, Common.StartDate, Common.EndDate,
                        Common.FromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TransportId, Common.UserID, Common.Count,
                        Common.SyncStatus, Common.SyncTime, Common.Volume, 1, Common.TransferUniqueID);
                if (ListIdFlag == true) {
                    //Common.InventoryTransferList = mDBInternalHelper.getInventoryTransferIdList("null");
                    //if (Common.InventoryTransferList.size() > 0) {
                    Common.TransferID = Integer.parseInt(mDBInternalHelper.getLastTransferID());
                    String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                    String DeviceID = "";
                    if (String.valueOf(Common.LDeviceID).length() == 1) {
                        DeviceID = "0" + Common.LDeviceID;
                    } else {
                        DeviceID = String.valueOf(Common.LDeviceID);
                    }
                    Common.TransferUniqueID = DateUniqueFormat + DeviceID + Common.TransferID;
                    // Update values into TransferID
                    boolean TransferIDFlag = mDBInternalHelper.UpdateInventoryTransferUniqueID(Common.TransferID, Common.TransferUniqueID);
                    //}
                    Common.IsTransferEditListFlag = true;
                    Common.IsEditorViewFlag = true;
                    InventorTransferAcivityCall();
                } else {
                    AlertDialogBox("IT-AddTransferList", "Values are not Inserted", false);
                }
            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
                AlertDialogBox("IT-AddTransferList", ex.toString(), false);
            }
        }
    };

    View.OnClickListener mSubmitOldTransferListen = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                if (OldTransferIDValidation(submitOldTransEDT.getText().toString())) {
                    Common.LoadedTypeID = 2;
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
                            Common.FromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TransportId, Common.UserID, Common.Count,
                            Common.SyncStatus, Common.SyncTime, Common.Volume, 1, Common.TransferUniqueID);

                    if (ListIdFlag == true) {
                        //
                        //if (Common.InventoryTransferList.size() > 0) {
                        Common.TransferID = Integer.parseInt(mDBInternalHelper.getLastTransferID());
                        // Update values into TransferID
                        boolean TransferIDFlag = mDBInternalHelper.UpdateInventoryTransferUniqueID(Common.TransferID, Common.TransferUniqueID);
                        //}
                    }
                    Common.IsTransferEditListFlag = true;
                    Common.IsEditorViewFlag = true;
                    InventorTransferAcivityCall();
                }
            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
                AlertDialogBox("IA-AddTransferList", ex.toString(), false);
            }
        }
    };

    View.OnClickListener mOldBarCodeScanListen = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                scanService.doDecode();
            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
                AlertDialogBox("IA-AddTransferList", ex.toString(), false);
            }
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

    public void GetInventoryTransferDateList() {
        try {
            Common.Filter_InventoryTransDate.clear();
            Common.Filter_InventoryTransDate = mDBInternalHelper.getInventoryTransferDate();
            if (Common.Filter_InventoryTransDate.size() > 0) {
                invenTransferDateadapter = new InventoryTransferDateAdapter(Common.Filter_InventoryTransDate, this);
                InveDateLayoutManager = new LinearLayoutManager(this);
                invenTransferDateadapter.notifyDataSetChanged();
                InventoryTransDateList.setLayoutManager(InveDateLayoutManager);
                InventoryTransDateList.setAdapter(invenTransferDateadapter);
                InventoryTransDateList.setVisibility(View.VISIBLE);
                NoValueFoundTxT.setVisibility(View.GONE);
                GetInventoryTransferList(String.valueOf(Common.Filter_InventoryTransDate.get(Common.InventTransDateSelectedIndex)));
            } else {
                InventoryTransList.setVisibility(View.GONE);
                InventoryTransDateList.setVisibility(View.GONE);
                NoValueFoundTxT.setVisibility(View.VISIBLE);
                TotalFilteredCount.setText("0");
                TotalFilteredVolume.setText("0.00");
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void GetInventoryTransferList(String SelectedDate) {
        try {
            Common.InventoryTransferList.clear();
            Common.InventoryTransferList = mDBInternalHelper.getInventoryTransferIdList(SelectedDate);
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
            TotalFilteredCount.setText(String.valueOf(Common.InventoryTransferList.size()));
            TotalFilteredVolume.setText(String.valueOf(mDBInternalHelper.TotalVolumeForInventoryTransfer(SelectedDate)));
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    @Override
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

            Snackbar snackbar = Snackbar.make(findViewById(R.id.snack_barList), message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
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
        public InventoryTransferAdapter.TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_transferlist_infliator, parent, false);
            InventoryTransferAdapter.TransferViewHolder gvh = new InventoryTransferAdapter.TransferViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return InventoryTransferList.size();
        }

        @Override
        public void onBindViewHolder(InventoryTransferAdapter.TransferViewHolder holder, final int position) {
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
           /* if (InventoryTransferList.get(position).getCount() > 0) {
                holder.syncTXT.setVisibility(View.VISIBLE);
            } else {
                holder.syncTXT.setVisibility(View.INVISIBLE);
            }*/
            holder.syncTXT.setOnClickListener(v -> {
                //Sync Date to BackEnd
                try {
                    if (Common.InventoryTransferSyncALL == false) {
                        Common.VBB_Number = InventoryTransferList.get(position).getVBB_Number();
                        Common.SyncStartDateTime = InventoryTransferList.get(position).getStartDateTime();
                        Common.SyncEndDateTime = InventoryTransferList.get(position).getEndDateTime();
                        Common.TransferID = InventoryTransferList.get(position).getTransferID();
                        Common.SyncBarCodeCount = InventoryTransferList.get(position).getCount();
                        Common.FromLocationID = InventoryTransferList.get(position).getFromLocationID();
                        Common.ToLocaTransID = InventoryTransferList.get(position).getToLocationID();

                        Common.TransportTypeId = InventoryTransferList.get(position).getTransportTypeId();
                        Common.LoadedTypeID = InventoryTransferList.get(position).getLoadedTypeID();
                        Common.TransferAgencyID = InventoryTransferList.get(position).getTransferAgencyID();
                        Common.DriverID = InventoryTransferList.get(position).getDriverID();
                        Common.TransferUniqueID = InventoryTransferList.get(position).getTransUniqueID();

                        if (Common.TransferAgencyID == 0) {
                            Common.AgencyName = InventoryTransferList.get(position).getAgencyName();
                        } else {
                            Common.AgencyName = mDBExternalHelper.getAgencyName(Common.TransferAgencyID);
                        }
                        if (Common.DriverID == 0) {
                            Common.DriverName = InventoryTransferList.get(position).getDriverName();
                        } else {
                            Common.DriverName = mDBExternalHelper.getAllDriverName(Common.DriverID);
                        }
                        if (InventoryTransferList.get(position).getTruckId() == 0) {
                            Common.TransportId = 0;
                            Common.TrucklicensePlateNo = InventoryTransferList.get(position).getTruckPlateNumber();
                        } else {
                            Common.TransportId = InventoryTransferList.get(position).getTruckId();
                            Common.TrucklicensePlateNo = mDBExternalHelper.getAllTruckNames(Common.TransportId);
                        }
                        InventoryTransferSync(Common.VBB_Number, Common.TransferID);
                    } else {
                        InventoryTransferSyncALLDialog("Are you sure want to sync all datas?");
                    }
                } catch (Exception ex) {
                    AlertDialogBox("Transfer Sync", ex.toString(), false);
                }
            });

            holder.DeleteIMG.setOnClickListener(v -> {
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
                            AlertDialogBox(CommonMessage(R.string.TransferHead), "This is not Syncked yet", false);
                        }
                    }
                } catch (Exception ex) {
                    AlertDialogBox("Transfer DeleteIMG", ex.toString(), false);
                }
            });

            holder.printIMG.setOnClickListener(v -> {
                try {
                    Common.VBB_Number = InventoryTransferList.get(position).getVBB_Number();
                    Common.TransferID = InventoryTransferList.get(position).getTransferID();
                    Common.TransferUniqueID = InventoryTransferList.get(position).getTransUniqueID();
                    Common.InventorytransferScannedResultList.clear();
                    Common.InventorytransferScannedResultList = mDBInternalHelper.getInventoryTransferWithVBBNumber(Common.VBB_Number, Common.TransferID);
                    if (Common.InventorytransferScannedResultList.size() > 0) {
                        Common.DriverID = InventoryTransferList.get(position).getDriverID();
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
                        if (Common.TransferAgencyID == 0) {
                            Common.AgencyName = InventoryTransferList.get(position).getAgencyName();
                        } else {
                            Common.AgencyName = mDBExternalHelper.getAgencyName(Common.TransferAgencyID);
                        }
                        if (Common.DriverID == 0) {
                            Common.DriverName = InventoryTransferList.get(position).getDriverName();
                        } else {
                            Common.DriverName = mDBExternalHelper.getAllDriverName(Common.DriverID);
                        }
                        if (InventoryTransferList.get(position).getTruckId() == 0) {
                            Common.TransportId = 0;
                            Common.TrucklicensePlateNo = InventoryTransferList.get(position).getTruckPlateNumber();
                        } else {
                            Common.TransportId = InventoryTransferList.get(position).getTruckId();
                            Common.TrucklicensePlateNo = mDBExternalHelper.getAllTruckNames(Common.TransportId);
                        }
                        PrintTransferSlipCheck();
                    } else {
                        AlertDialogBox("Transfer PrintSlip", "No values found, try some other item", false);
                    }
                } catch (Exception ex) {
                    AlertDialogBox("Transfer PrintSlip", ex.toString(), false);
                }
            });

            holder.Background.setOnLongClickListener(v -> {
                try {
                    Common.TransferUniqueID = InventoryTransferList.get(position).getTransUniqueID();
                    Common.TransferID = InventoryTransferList.get(position).getTransferID();

                    Common.FromTransLocID = InventoryTransferList.get(position).getFromLocationID();
                    Common.FromLocationname = mDBExternalHelper.getFromLocationName(Common.FromTransLocID);

                    Common.ToLocaTransID = InventoryTransferList.get(position).getToLocationID();
                    Common.ToLocationName = mDBExternalHelper.getToLocationName(Common.ToLocaTransID);

                    Common.DriverID = InventoryTransferList.get(position).getDriverID();

                    Common.TransferAgencyID = InventoryTransferList.get(position).getTransferAgencyID();

                    Common.TransportTypeId = InventoryTransferList.get(position).getTransportTypeId();
                    Common.TransportMode = mDBExternalHelper.getAllTransPortMode(Common.TransportTypeId);

                    if (isNullOrEmpty(String.valueOf(InventoryTransferList.get(position).getLoadedTypeID()))) {
                        Common.LoadedName = "";
                    } else {
                        Common.LoadedTypeID = InventoryTransferList.get(position).getLoadedTypeID();
                        Common.LoadedName = mDBExternalHelper.getLoadedName(Common.LoadedTypeID);
                    }

                    Common.VBB_Number = InventoryTransferList.get(position).getVBB_Number();

                    if (Common.TransferAgencyID == 0) {
                        Common.AgencyName = InventoryTransferList.get(position).getAgencyName();
                    } else {
                        Common.AgencyName = mDBExternalHelper.getAgencyName(Common.TransferAgencyID);
                    }
                    if (Common.DriverID == 0) {
                        Common.DriverName = InventoryTransferList.get(position).getDriverName();
                    } else {
                        Common.DriverName = mDBExternalHelper.getAllDriverName(Common.DriverID);
                    }
                    if (InventoryTransferList.get(position).getTruckId() == 0) {
                        Common.TransportId = 0;
                        Common.TrucklicensePlateNo = InventoryTransferList.get(position).getTruckPlateNumber();
                    } else {
                        Common.TransportId = InventoryTransferList.get(position).getTruckId();
                        Common.TrucklicensePlateNo = mDBExternalHelper.getAllTruckNames(Common.TransportId);
                    }
                    if (InventoryTransferList.get(position).getSyncStatus() == 0) {
                        Common.IsEditorViewFlag = true;
                        Common.IsTransferEditListFlag = false;
                        //if (Common.FromLocationID != Common.FromTransLocID) {
                            //Common.IsEditorViewFlag = false;
                        //}
                    } else {
                        Common.IsEditorViewFlag = false;
                    }
                    InventorTransferAcivityCall();
                } catch (Exception ex) {
                    AlertDialogBox("Transfer Background", ex.toString(), false);
                }
                return false;
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

    public class InventoryTransferDateAdapter extends RecyclerView.Adapter<InventoryTransferDateAdapter.TransferViewHolder> {
        private List<String> InventoryTransferDateList;
        Context context;

        public InventoryTransferDateAdapter(List<String> inventoryTransferDateList, Context context) {
            this.InventoryTransferDateList = inventoryTransferDateList;
            this.context = context;
        }

        @Override
        public InventoryTransferDateAdapter.TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_count_dates_infliator, parent, false);
            InventoryTransferDateAdapter.TransferViewHolder gvh = new InventoryTransferDateAdapter.TransferViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return InventoryTransferDateList.size();
        }

        @Override
        public void onBindViewHolder(InventoryTransferDateAdapter.TransferViewHolder holder, final int position) {
            //holder.TransferIDTXT.setText(String.valueOf(position + 1));
            if (Common.InventTransDateSelectedIndex == position) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.orange));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.colorgGreenSmooth));
            }
            holder.TransferDateTXT.setText(String.valueOf(InventoryTransferDateList.get(position)));

            holder.Background.setOnClickListener(v -> {
                Common.InventTransDateSelectedIndex = position;
                notifyDataSetChanged();
                GetInventoryTransferList(String.valueOf(InventoryTransferDateList.get(position)));
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

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(this, Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public String CommonMessage(int Common_Msg) {
        return this.getResources().getString(Common_Msg);
    }

    // Method for SyncAPI
    public void InventoryTransferSyncALLDialog(String ErrorMessage) {
        if (SignoutAlert != null && SignoutAlert.isShowing()) {
            return;
        }
        Signoutbuilder = new AlertDialog.Builder(this);
        Signoutbuilder.setMessage(ErrorMessage);
        Signoutbuilder.setCancelable(true);
        Signoutbuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                (dialog, id) -> dialog.cancel());
        Signoutbuilder.setNegativeButton("SyncAll",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InventoryTransferSyncALL();
                        dialog.cancel();
                    }
                });

        SignoutAlert = Signoutbuilder.create();
        SignoutAlert.show();
    }

    public void InventoryTransferSyncALL() {
        try {
            if (checkConnection() == true) {
                Common.InveTransferSyncALlIndex = 0;
                getInventoryTransferAllSyncStatusApi();
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), ex.toString(), false);
        }
    }


    public void TransferDeleteDatasIFUserID_ONE(String ErrorMessage) {
        if (SignoutAlert != null && SignoutAlert.isShowing()) {
            return;
        }
        Signoutbuilder = new AlertDialog.Builder(this);
        Signoutbuilder.setMessage(ErrorMessage);
        Signoutbuilder.setCancelable(true);
        Signoutbuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                (dialog, id) -> dialog.cancel());
        Signoutbuilder.setNegativeButton(CommonMessage(R.string.action_delete),
                (dialog, id) -> {
                    DeleteTransferListandTransferScannedList(Common.TransferID);
                    dialog.cancel();
                });

        SignoutAlert = Signoutbuilder.create();
        SignoutAlert.show();

    }


    public void InventoryTransferSync(String VbbNumber, int TransferId) {
        Common.InventoryTransferInputList.clear();
        Common.InventoryTransferInputList = mDBInternalHelper.getTransferScannedResultInputWithVBBNo(VbbNumber, TransferId);
        Common.Purchase.purchaseStatus = mDBInternalHelper.getTransferScannedPurchaseStatus(VbbNumber, TransferId);
        if (Common.InventoryTransferInputList.size() > 0) {
            if (checkConnection()) {
                getInventoryTransferSyncStatusApi();
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
                Common.VolumeSum = TransferTotalVolume(Common.InventorytransferScannedResultList);
                Common.Count = Common.InventorytransferScannedResultList.size();
                tsc.clearbuffer();
                /*hided in version 5.0*/
                tsc.sendcommand(printSlip.TransferHeader());
                //tsc.sendcommand(printSlip.TransferDetails());
                tsc.sendcommand(printSlip.TransferDetails19_09_2019());
                //tsc.sendcommand(printSlip.TransferScannedItemsDetails());
                //18-11-2019
                tsc.sendcommand(printSlip.TransferListDimensions18_Nov_2019());
                tsc.clearbuffer();
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
                CrashAnalytics.CrashReport(ex);
            }
        }
    };

    public double TransferTotalVolume(ArrayList<InventoryTransferScannedResultModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (InventoryTransferScannedResultModel inventoTransScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(inventoTransScanModel.getVolume());
        }
        return TotVolume;
    }

    public void DeleteTransferListandTransferScannedList(int transferID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteInventoryTransferListID(transferID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteInventoryTransferScanned(transferID);
                if (DeleteScannedFlag == true) {
                    GetInventoryTransferDateList();
                    //GetInventoryTransferList(Common.Filter_InventoryTransDate.get(Common.InventTransDateSelectedIndex));
                }
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
        }
    }

    public void InventorTransferAcivityCall() {
        Intent _gwIntent = new Intent(this, InventoryTransferActivity.class);
        startActivity(_gwIntent);
    }

    @Override
    public void ScannedStatus(String string) {
    }

    public void ScannedStatus(int id) {
    }

    private void dspErr(String s) {
    }

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
            submitOldTransEDT.setText(ScannedArryValue[0]);
            Common.VBB_Number = ScannedArryValue[1];
            //Common.TrucklicensePlateNo = ScannedArryValue[2];
            Common.FromTransLocID = Integer.parseInt(ScannedArryValue[3]);
            Common.ToLocaTransID = Integer.parseInt(ScannedArryValue[4]);
            Common.TransferAgencyID = Integer.parseInt(ScannedArryValue[5]);
            Common.DriverID = Integer.parseInt(ScannedArryValue[6]);
            Common.TransportId = Integer.parseInt(ScannedArryValue[7]);
            Common.TransportTypeId = Integer.parseInt(ScannedArryValue[8]);
            Common.LoadedTypeID = Integer.parseInt(ScannedArryValue[12]);
            Common.SyncTime = "";
            Common.VolumeSum = 0.0;
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
                    Common.FromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TransportId, Common.UserID, Common.Count,
                    Common.SyncStatus, Common.SyncTime, Common.Volume, 1, Common.TransferUniqueID);

            if (ListIdFlag == true) {
                Common.InventoryTransferList = mDBInternalHelper.getInventoryTransferIdList(Common.Filter_InventoryTransDate.get(Common.InventTransDateSelectedIndex));
                if (Common.InventoryTransferList.size() > 0) {
                    Common.TransferID = Integer.parseInt(mDBInternalHelper.getLastTransferID());
                    // Update values into TransferID
                    boolean TransferIDFlag = mDBInternalHelper.UpdateInventoryTransferUniqueID(Common.TransferID, Common.TransferUniqueID);
                }
            }
            Common.IsTransferEditListFlag = false;
            Common.IsEditorViewFlag = true;
            InventorTransferAcivityCall();
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("ScanQR Details", ex.toString(), false);
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

    // 2-12-2019 Added InventoryTransferSyncStatusApi impl
    private void getInventoryTransferSyncStatusApi() {
        try {
            ProgressBarLay.setVisibility(View.VISIBLE);
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
            transferSyncModel.setAgencyName(Common.AgencyName);
            transferSyncModel.setDriverName(Common.DriverName);
            transferSyncModel.setTruckId(Common.TransportId);
            transferSyncModel.setPurchaseStatus(Common.Purchase.purchaseStatus);
            transferSyncModel.setHHInventoryTransfer(Common.InventoryTransferInputList);

            InvenTransAPI = ApiClient.getApiInterface();
            InvenTransAPI.getInventoryTransferSync(transferSyncModel).enqueue(new Callback<InventoryTransferSyncModel>() {
                @Override
                public void onResponse(Call<InventoryTransferSyncModel> call, Response<InventoryTransferSyncModel> response) {
                    try {
                        ProgressBarLay.setVisibility(View.GONE);
                        if (GwwException.GwwException(response.code())) {
                            if (response.isSuccessful()) {
                                Common.SyncStatusList.clear();
                                Common.SyncStatusList.addAll(response.body().getStatus());
                                if (Common.SyncStatusList.get(0).getStatus() == 1) {
                                    Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                                    Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                                    boolean ListIdFlag = mDBInternalHelper.UpdateInventoryTransferSyncStatusTransID(Common.SyncTime, 1, Common.VBB_Number, Common.TransferID,
                                            Common.SyncStatusList.get(0).getTransferAgencyId(), Common.SyncStatusList.get(0).getDriverId(), Common.SyncStatusList.get(0).getTruckId());
                                    if (ListIdFlag) {
                                        //Scanned Result Refresh
                                        GetInventoryTransferDateList();
                                        AlertDialogBox(CommonMessage(R.string.TransferHead), "#" + Common.TransferID + "--" + Common.SyncStatusList.get(0).getMessage(), true);
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
                    ProgressBarLay.setVisibility(View.GONE);
                    AlertDialogBox(CommonMessage(R.string.TransferHead), "#" + Common.TransferID + "--" + t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            ProgressBarLay.setVisibility(View.GONE);
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox(CommonMessage(R.string.TransferHead), ex.toString(), false);
        }
    }

    // 2-12-2019 Added InventoryTransferAllSyncStatusApi impl
    private void getInventoryTransferAllSyncStatusApi() {
        try {
            ProgressBarLay.setVisibility(View.VISIBLE);

            Common.VBB_Number = Common.InventoryTransferList.get(Common.InveTransferSyncALlIndex).getVBB_Number();
            Common.SyncStartDateTime = Common.InventoryTransferList.get(Common.InveTransferSyncALlIndex).getStartDateTime();
            Common.SyncEndDateTime = Common.InventoryTransferList.get(Common.InveTransferSyncALlIndex).getEndDateTime();
            Common.TransferID = Common.InventoryTransferList.get(Common.InveTransferSyncALlIndex).getTransferID();
            Common.SyncBarCodeCount = Common.InventoryTransferList.get(Common.InveTransferSyncALlIndex).getCount();
            Common.FromLocationID = Common.InventoryTransferList.get(Common.InveTransferSyncALlIndex).getFromLocationID();
            Common.ToLocaTransID = Common.InventoryTransferList.get(Common.InveTransferSyncALlIndex).getToLocationID();
            Common.TrucklicensePlateNo = Common.InventoryTransferList.get(Common.InveTransferSyncALlIndex).getTruckPlateNumber();
            Common.TransportTypeId = Common.InventoryTransferList.get(Common.InveTransferSyncALlIndex).getTransportTypeId();
            Common.LoadedTypeID = Common.InventoryTransferList.get(Common.InveTransferSyncALlIndex).getLoadedTypeID();
            Common.TransferAgencyID = Common.InventoryTransferList.get(Common.InveTransferSyncALlIndex).getTransferAgencyID();
            Common.DriverID = Common.InventoryTransferList.get(Common.InveTransferSyncALlIndex).getDriverID();
            Common.TransferUniqueID = Common.InventoryTransferList.get(Common.InveTransferSyncALlIndex).getTransUniqueID();

            Common.InventoryTransferInputList.clear();
            Common.InventoryTransferInputList = mDBInternalHelper.getTransferScannedResultInputWithVBBNo(Common.VBB_Number, Common.TransferID);

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

            InvenTransAPI = ApiClient.getApiInterface();
            InvenTransAPI.getInventoryTransferSync(transferSyncModel).enqueue(new Callback<InventoryTransferSyncModel>() {
                @Override
                public void onResponse(Call<InventoryTransferSyncModel> call, Response<InventoryTransferSyncModel> response) {
                    try {
                        ProgressBarLay.setVisibility(View.GONE);
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.isSuccessful()) {
                                Common.SyncStatusList.clear();
                                Common.SyncStatusList.addAll(response.body().getStatus());
                                if (Common.SyncStatusList.get(0).getStatus() == 1) {
                                    Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                                    Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                                    boolean ListIdFlag = mDBInternalHelper.UpdateInventoryTransferSyncStatusTransID(Common.SyncTime, 1, Common.VBB_Number, Common.TransferID, 0, 0, 0);
                                    if (Common.InveTransferSyncALlIndex == (Common.InventoryTransferList.size() - 1)) {
                                        GetInventoryTransferDateList();
                                        AlertDialogBox(CommonMessage(R.string.CountHead), Common.SyncStatusList.get(0).getMessage(), true);
                                    } else {
                                        Common.InveTransferSyncALlIndex++;
                                        getInventoryTransferAllSyncStatusApi();
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
                    ProgressBarLay.setVisibility(View.GONE);
                    AlertDialogBox(CommonMessage(R.string.TransferHead), "#" + Common.TransferID + "--" + t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            ProgressBarLay.setVisibility(View.GONE);
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox(CommonMessage(R.string.TransferHead), ex.toString(), false);
        }
    }
}
