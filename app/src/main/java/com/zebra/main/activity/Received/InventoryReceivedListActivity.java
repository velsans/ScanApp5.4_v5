package com.zebra.main.activity.Received;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tscdll.TSCActivity;
import com.google.gson.Gson;
import com.zebra.R;
import com.zebra.android.jb.Preference;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.activity.Common.GwwMainActivity;
import com.zebra.main.model.InvReceived.InventoryReceivedListModel;
import com.zebra.main.model.InvReceived.InventoryReceivedModel;
import com.zebra.main.model.InvReceived.InventoryReceivedSyncModel;
import com.zebra.main.model.SyncStatusModel;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.BlueTooth;
import com.zebra.utilities.BlutoothCommonClass;
import com.zebra.utilities.Common;
import com.zebra.utilities.Communicator;
import com.zebra.utilities.ConnectionFinder;
import com.zebra.utilities.GwwException;
import com.zebra.utilities.PrintSlipsClass;
import com.zebra.utilities.ServiceURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class InventoryReceivedListActivity extends Activity {
    private static final String TAG = "Inventory Received";
    private InternalDataBaseHelperClass mDBInternalHelper;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    Button inventoryReceivedTXT;
    TextView NoValueFoundTxT, TotalFilteredCount, TotalFilteredVolume;
    LinearLayout inventoryReceivedLAY, ProgressBarLay;
    ImageView ReceivedCreateScanListTXT, ReceivedSyncAllTXT;
    RecyclerView InventoryReceivedList, InventoryReceivedDateList;
    AlertDialog.Builder Signoutbuilder = null;
    AlertDialog SignoutAlert = null;
    AlertDialogManager alert = new AlertDialogManager();
    boolean isInternetPresent;
    MediaPlayer beepsound, wronBuzzer;
    Intent service;
    private ImageView image = null;
    TSCActivity tsc = new TSCActivity();
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    PrintSlipsClass printSlip;
    InventoryReceivedSyncModel receiedSyncModel;
    SyncStatusModel suncStaModel;
    InventorReceivedAdapter invenRecievadapter;
    LinearLayoutManager horizontalLayoutManager;
    InventoryReceivedDateAdapter invenReceivedDateadapter;
    LinearLayoutManager InveDateLayoutManager;
    CheckBox SyncAllTransferCheckBox, SyncAllReceivedCheckBox;

    //DatabaseHelper dbBackend;
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
            Log.v(TAG, "onStop");
        } catch (Exception Ex) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_received_list);
        mDBExternalHelper = new ExternalDataBaseHelperClass(this);
        mDBInternalHelper = new InternalDataBaseHelperClass(this);
        printSlip = new PrintSlipsClass(this);
        Initialization();
        OnclickListener();
        Common.InventReceivedDateSelectedIndex = 0;
        GetInventoryTransferDateList();
    }

    private void OnclickListener() {
        SyncAllReceivedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SyncAllReceivedCheckBox.isChecked()) {
                    Common.InventoryReceivedSyncALL = true;
                } else {
                    Common.InventoryReceivedSyncALL = false;
                }
            }
        });

        SyncAllReceivedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (SyncAllReceivedCheckBox.isChecked()) {
                    Common.InventoryReceivedSyncALL = true;
                } else {
                    Common.InventoryReceivedSyncALL = false;
                }
            }
        });
    }

    public void GetInventoryTransferDateList() {
        try {
            Common.Filter_InventoryReceivedDate.clear();
            Common.Filter_InventoryReceivedDate = mDBInternalHelper.getInventoryReceivedDate();
            /*Remove Duplications*/
            HashSet<String> hashSet = new HashSet<String>();
            hashSet.addAll(Common.Filter_InventoryReceivedDate);
            Common.Filter_InventoryReceivedDate.clear();
            Common.Filter_InventoryReceivedDate.addAll(hashSet);
            if (Common.Filter_InventoryReceivedDate.size() > 0) {
                invenReceivedDateadapter = new InventoryReceivedDateAdapter(Common.Filter_InventoryReceivedDate, this);
                InveDateLayoutManager = new LinearLayoutManager(this);
                invenReceivedDateadapter.notifyDataSetChanged();
                InventoryReceivedDateList.setLayoutManager(InveDateLayoutManager);
                InventoryReceivedDateList.setAdapter(invenReceivedDateadapter);
                NoValueFoundTxT.setVisibility(View.GONE);
                GetInventoryReceivedList(Common.Filter_InventoryReceivedDate.get(Common.InventReceivedDateSelectedIndex));
            } else {
                InventoryReceivedList.setVisibility(View.GONE);
                InventoryReceivedDateList.setVisibility(View.GONE);
                NoValueFoundTxT.setVisibility(View.VISIBLE);
                TotalFilteredCount.setText("0");
                TotalFilteredVolume.setText("0.00");
            }
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
    }

    private void Initialization() {
        ReceivedCreateScanListTXT = findViewById(R.id.receivedCreateScanListTxT);
        findViewById(R.id.receivedCreateScanListTxT).setOnClickListener(mReceivedCreateScanListen);
        TotalFilteredCount = findViewById(R.id.TotalFilteredCount);
        TotalFilteredVolume = findViewById(R.id.TotalFilteredVolume);
        InventoryReceivedList = findViewById(R.id.inventoryReceivedListView);
        InventoryReceivedDateList = findViewById(R.id.inventoryReceivedListViewDate);
        ProgressBarLay = findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        NoValueFoundTxT = findViewById(R.id.NovalueFound);
        ReceivedSyncAllTXT = findViewById(R.id.receivedSyncAll);
        findViewById(R.id.receivedSyncAll).setOnClickListener(mReceivedSyncALL);
        SyncAllReceivedCheckBox = findViewById(R.id.SyncAllReceived_checkBox);
    }

    View.OnClickListener mReceivedSyncALL = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                InventoryReceivedSyncALLDialog("Are you sure want to sync all datas?");
            } catch (Exception ex) {
                ex.printStackTrace();
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
                    //Common.InventoryReceivedList = mDBInternalHelper.getInventoryReceivedIdList(Common.Filter_InventoryReceivedDate.get(Common.InventReceivedDateSelectedIndex));
                    //if (Common.InventoryReceivedList.size() > 0) {
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
                    //}
                    Common.TransferIDsList.clear();
                    Common.IsReceivedEditListFlag = true;
                    Common.IsEditorViewFlag = true;
                    InventorReceivedAcivityCall();
                } else {
                    AlertDialogBox("IR-AddInventoryReceivedList", "Values are not Inserted", false);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                AlertDialogBox("IR-AddInventoryReceivedList", ex.toString(), false);
            }
        }
    };

    public class InventorReceivedAdapter extends RecyclerView.Adapter<InventorReceivedAdapter.ReceivedViewHolder> {
        private List<InventoryReceivedListModel> InventoryReceivedList;
        Context context;

        public InventorReceivedAdapter(List<InventoryReceivedListModel> ScannedResultList, Context context) {
            this.InventoryReceivedList = ScannedResultList;
            this.context = context;
        }

        @Override
        public InventorReceivedAdapter.ReceivedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_receivedlist_infliator, parent, false);
            InventorReceivedAdapter.ReceivedViewHolder gvh = new InventorReceivedAdapter.ReceivedViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return InventoryReceivedList.size();
        }

        @Override
        public void onBindViewHolder(InventorReceivedAdapter.ReceivedViewHolder holder, final int position) {
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
                        if (Common.InventoryReceivedSyncALL == false) {
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
                        } else {
                            InventoryReceivedSyncALLDialog("Are you sure want to sync all datas?");
                        }
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
                        if (Common.TransferReciveUniqueID.length() > 9) {
                            int TrUniIDInd = (Common.TransferReciveUniqueID.length() - 10);
                            Common.ReceivedTransferID = Common.TransferReciveUniqueID.substring(Common.TransferReciveUniqueID.length() - TrUniIDInd);
                        } else {
                            Common.ReceivedTransferID = Common.TransferReciveUniqueID;
                        }
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

    public class InventoryReceivedDateAdapter extends RecyclerView.Adapter<InventoryReceivedDateAdapter.ReceivedViewHolder> {
        private List<String> InventoryReceivedDateList;
        Context context;

        public InventoryReceivedDateAdapter(List<String> InventoryReceivedDateList, Context context) {
            this.InventoryReceivedDateList = InventoryReceivedDateList;
            this.context = context;
        }

        @Override
        public InventoryReceivedDateAdapter.ReceivedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_count_dates_infliator, parent, false);
            InventoryReceivedDateAdapter.ReceivedViewHolder gvh = new InventoryReceivedDateAdapter.ReceivedViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return InventoryReceivedDateList.size();
        }

        @Override
        public void onBindViewHolder(InventoryReceivedDateAdapter.ReceivedViewHolder holder, final int position) {
            //holder.TransferIDTXT.setText(String.valueOf(position + 1));
            if (Common.InventReceivedDateSelectedIndex == position) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.orange));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.colorgGreenSmooth));
            }
            holder.TransferDateTXT.setText(String.valueOf(InventoryReceivedDateList.get(position)));

            holder.Background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.InventReceivedDateSelectedIndex = position;
                    notifyDataSetChanged();
                    GetInventoryReceivedList(Common.Filter_InventoryReceivedDate.get(Common.InventReceivedDateSelectedIndex));
                }
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

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str == "null";
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

    public double ReceivedTotalVolume(ArrayList<InventoryReceivedModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (InventoryReceivedModel inventoreceScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(inventoreceScanModel.getVolume());
        }
        return TotVolume;
    }

    public void PrintoutEnd() {
        tsc.closeport();
        TSCConnInt = -1;
        Common.IsPrintBtnClickFlag = true;
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

    public void InventorReceivedAcivityCall() {
        Intent _gwIntent = new Intent(this, InventoryReceivedActivity.class);
        startActivity(_gwIntent);
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

    private void GetInventoryReceivedList(String SelectedDate) {
        try {
            Common.InventoryReceivedList.clear();
            Common.InventoryReceivedList = mDBInternalHelper.getInventoryReceivedIdList(SelectedDate);
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
            TotalFilteredCount.setText(String.valueOf(Common.InventoryReceivedList.size()));
            TotalFilteredVolume.setText(String.valueOf(mDBInternalHelper.TotalVolumeForInventoryReceive(SelectedDate)));
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
    }

    public void InventoryReceivedSync(String VbbNumber, int ReceivedID) {
        Common.InventoryReceivedInputList.clear();
        Common.InventoryReceivedInputList = mDBInternalHelper.getReceivedScannedResultInputWithVBBNo(VbbNumber, ReceivedID);
        if (Common.InventoryReceivedInputList.size() > 0) {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                new GetInventoryReceivedSyncAsynkTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else {
            AlertDialogBox("InventoryReceived Sync", "Values are empty", false);
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
                                GetInventoryTransferDateList();
                                AlertDialogBox(CommonMessage(R.string.ReceivedHead), "#" + String.valueOf(Common.ReceivedID) + "--" + Common.SyncStatusList.get(0).getMessage(), true);
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ReceivedHead), "#" + String.valueOf(Common.ReceivedID) + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                            return;
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.ReceivedHead), "#" + String.valueOf(Common.ReceivedID) + "--" + "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }
    }

    public void InventoryReceivedSyncALLDialog(String ErrorMessage) {
        if (SignoutAlert != null && SignoutAlert.isShowing()) {
            return;
        }
        Signoutbuilder = new AlertDialog.Builder(this);
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
                        InventoryReceivedSyncALL();
                        dialog.cancel();
                    }
                });

        SignoutAlert = Signoutbuilder.create();
        SignoutAlert.show();

    }

    public void DeleteReceivedListannReceivedScannedList(int receivedID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteInventoryReceivedListID(receivedID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteInventoryReceivedScanned(receivedID);
                if (DeleteScannedFlag == true) {
                    GetInventoryTransferDateList();
                    //GetInventoryReceivedList(Common.Filter_InventoryReceivedDate.get(Common.InventReceivedDateSelectedIndex));
                }
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteReceivedListannReceivedScannedList", ex.toString(), false);
        }
    }

    public void ReceivedDeleteDatasIFUserID_ONE(String ErrorMessage) {
        if (SignoutAlert != null && SignoutAlert.isShowing()) {
            return;
        }
        Signoutbuilder = new AlertDialog.Builder(this);
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

    public void InventoryReceivedSyncALL() {
        try {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                Common.InveReceivedSyncALlIndex = 0;
                new GetInventoryReceivedSyncAllAsynkTask().execute();
            }
        } catch (Exception ex) {
            AlertDialogBox(CommonMessage(R.string.ReceivedHead), ex.toString(), false);
        }
    }

    class GetInventoryReceivedSyncAllAsynkTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.SyncStatusList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Common.VBB_Number = Common.InventoryReceivedList.get(Common.InveReceivedSyncALlIndex).getVBB_Number();
                Common.SyncStartDateTime = Common.InventoryReceivedList.get(Common.InveReceivedSyncALlIndex).getStartDateTime();
                Common.SyncEndDateTime = Common.InventoryReceivedList.get(Common.InveReceivedSyncALlIndex).getEndDateTime();
                Common.ToLocReceivedID = Common.InventoryReceivedList.get(Common.InveReceivedSyncALlIndex).getToLocationID();
                Common.TransferID = Common.InventoryReceivedList.get(Common.InveReceivedSyncALlIndex).getTransferID();
                Common.ReceivedID = Common.InventoryReceivedList.get(Common.InveReceivedSyncALlIndex).getReceivedID();
                Common.SyncBarCodeCount = Common.InventoryReceivedList.get(Common.InveReceivedSyncALlIndex).getCount();
                Common.FromLocationID = Common.InventoryReceivedList.get(Common.InveReceivedSyncALlIndex).getFromLocationID();
                Common.TrucklicensePlateNo = Common.InventoryReceivedList.get(Common.InveReceivedSyncALlIndex).getTruckPlateNumber();
                Common.TransportTypeId = Common.InventoryReceivedList.get(Common.InveReceivedSyncALlIndex).getTransportTypeId();
                Common.TransferAgencyID = Common.InventoryReceivedList.get(Common.InveReceivedSyncALlIndex).getTransferAgencyID();
                Common.DriverID = Common.InventoryReceivedList.get(Common.InveReceivedSyncALlIndex).getDriverID();
                Common.InventoryReceivedInputList.clear();
                Common.InventoryReceivedInputList = mDBInternalHelper.getReceivedScannedResultInputWithVBBNo(Common.VBB_Number, Common.ReceivedID);

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
                            if (Common.InveReceivedSyncALlIndex == (Common.InventoryReceivedList.size() - 1)) {
                                GetInventoryTransferDateList();
                                AlertDialogBox(CommonMessage(R.string.ReceivedHead), Common.SyncStatusList.get(0).getMessage(), true);
                            } else {
                                Common.InveReceivedSyncALlIndex++;
                                new GetInventoryReceivedSyncAllAsynkTask().execute();
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ReceivedHead), "#" + String.valueOf(Common.ReceivedID) + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                            return;
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.ReceivedHead), "#" + String.valueOf(Common.ReceivedID) + "--" + "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }
    }
}
