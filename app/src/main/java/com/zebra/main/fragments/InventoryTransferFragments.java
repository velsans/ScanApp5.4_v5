package com.zebra.main.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tscdll.TSCActivity;
import com.google.gson.Gson;
import com.zebra.R;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.activity.InventoryTransferActivity;
import com.zebra.main.model.InvCount.InventoryCountSyncModel;
import com.zebra.main.model.InvTransfer.InventoryTransferModel;
import com.zebra.main.model.InvTransfer.InventoryTransferScannedResultModel;
import com.zebra.main.model.InvTransfer.InventoryTransferSyncModel;
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
import java.util.List;

public class InventoryTransferFragments extends Fragment {
    TransferReceiver transfer_receiver;
    Button submitVBBTXT;
    EditText submitVVPEDT;
    LinearLayout ProgressBarLay;
    InventoryTransferAdapter invenTransadapter;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;
    private PrintSlipsClass printSlip;
    RecyclerView InventoryTransList;
    LinearLayoutManager horizontalLayoutManager;
    TextView NoValueFoundTxT;
    AlertDialogManager alert = new AlertDialogManager();
    boolean isInternetPresent;
    InventoryTransferSyncModel transferSyncModel;
    SyncStatusModel suncStaModel;
    TSCActivity tsc = new TSCActivity();
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    View transfer_rootView;
    SwipeRefreshLayout transfer_refreshLay;

    public InventoryTransferFragments() {
        // Required empty public constructor
    }

    private class TransferReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    }

    public void refresh() {
        if (Common.IsPrintBtnClickFlag == true) {
            if (transfer_refreshLay.isRefreshing() == false && ProgressBarLay.getVisibility() == View.GONE) {
                GetInventoryTransferList();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        transfer_receiver = new TransferReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(transfer_receiver,
                new IntentFilter("TRANSFER_REFRESH"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(transfer_receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        transfer_rootView = inflater.inflate(R.layout.inventorytransferlist_fragment, container, false);
        Initialization();
        GetInventoryTransferList();
        Common.QulaityDefaultList.clear();
        Common.QulaityDefaultList.add("A");
        Common.QulaityDefaultList.add("B");
        Common.QulaityDefaultList.add("C");
        Common.QulaityDefaultList.add("U");
        getTransferLocationDetials();
        transfer_refreshLay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                transfer_refreshLay.setRefreshing(false);
                GetInventoryTransferList();
            }
        });
        return transfer_rootView;
    }

    private void getTransferLocationDetials() {
        try {
            Common.AllTransLogDetailsmap.clear();
            Common.AllTransLogDetailsmap = mDBExternalHelper.getAllTransferLogDetails();
            //GetScannedResultListWithIndex();
        } catch (Exception ex) {
            Log.v("TransferLocationDetials", "Retrive Exception: " + ex.toString());
        }
    }

    public void Initialization() {
        mDBExternalHelper = new ExternalDataBaseHelperClass(getActivity());
        mDBInternalHelper = new InternalDataBaseHelperClass(getActivity());
        printSlip = new PrintSlipsClass(getActivity());
        ProgressBarLay = transfer_rootView.findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        NoValueFoundTxT = transfer_rootView.findViewById(R.id.NovalueFound);
        // Inventory Transfer
        submitVBBTXT = transfer_rootView.findViewById(R.id.submitVBBTxT);
        submitVVPEDT = transfer_rootView.findViewById(R.id.submitVVPEdT);
        InventoryTransList = transfer_rootView.findViewById(R.id.inventoryTransListView);
        transfer_rootView.findViewById(R.id.submitVBBTxT).setOnClickListener(mSubmitVVPListen);
        transfer_refreshLay = transfer_rootView.findViewById(R.id.transferSwipeRefreshLayout);
        transfer_refreshLay.setColorScheme(R.color.red, R.color.green, R.color.teal);
    }

    View.OnClickListener mSubmitVVPListen = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Common.SyncTime = "";
                Common.VolumeSum = 0.0;
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
                        Common.FromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TrucklicensePlateNo, Common.UserID, Common.Count,
                        Common.SyncStatus, Common.SyncTime, Common.Volume, 1, Common.TransferUniqueID);
                if (ListIdFlag == true) {
                    Common.InventoryTransferList = mDBInternalHelper.getInventoryTransferIdList();
                    if (Common.InventoryTransferList.size() > 0) {
                        Common.TransferID = Integer.parseInt(mDBInternalHelper.getLastTransferID());
                        String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                        String DeviceID = "";
                        if (String.valueOf(Common.LDeviceID).length() == 1) {
                            DeviceID = "0" + String.valueOf(Common.LDeviceID);
                        } else {
                            DeviceID = String.valueOf(Common.LDeviceID);
                        }
                        Common.TransferUniqueID = String.valueOf(DateUniqueFormat + DeviceID + Common.TransferID);
                        Log.d("TransferID", ">>>>>>" + Common.TransferID + ">>" + Common.TransferUniqueID);

                        InventorTransferAcivityCall();
                    }
                }
                Common.IsTransferEditListFlag = true;
                Common.IsEditorViewFlag = true;
            } catch (Exception ex) {
                AlertDialogBox("IA-AddTransferList", ex.toString(), false);
            }
        }
    };

    public void GetInventoryTransferList() {
        try {
            Common.InventoryTransferList.clear();
            Common.InventoryTransferList = mDBInternalHelper.getInventoryTransferIdList();
            if (Common.InventoryTransferList.size() > 0) {
                invenTransadapter = new InventoryTransferAdapter(Common.InventoryTransferList, getActivity());
                horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
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
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
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
            if (InventoryTransferList.get(position).getCount() > 0) {
                holder.syncTXT.setVisibility(View.VISIBLE);
            } else {
                holder.syncTXT.setVisibility(View.INVISIBLE);
            }
            holder.syncTXT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Sync Date to BackEnd
                    //Common.IMEI = getImeiNumber();
                    Common.VBB_Number = InventoryTransferList.get(position).getVBB_Number();
                    Common.SyncStartDateTime = InventoryTransferList.get(position).getStartDateTime();
                    Common.SyncEndDateTime = InventoryTransferList.get(position).getEndDateTime();
                    Common.TransferID = InventoryTransferList.get(position).getTransferID();
                    Common.SyncBarCodeCount = InventoryTransferList.get(position).getCount();
                    Common.FromLocationID = InventoryTransferList.get(position).getFromLocationID();
                    Common.ToLocaTransID = InventoryTransferList.get(position).getToLocationID();
                    Common.TrucklicensePlateNo = InventoryTransferList.get(position).getTruckPlateNumber();
                    Common.TransportTypeId = InventoryTransferList.get(position).getTransportTypeId();
                    Common.TransferAgencyID = InventoryTransferList.get(position).getTransferAgencyID();
                    Common.DriverID = InventoryTransferList.get(position).getDriverID();
                   /* if (isNullOrEmpty(InventoryTransferList.get(position).getTransUniqueID())) {
                        Common.TransferUniqueID = String.valueOf(Common.TransferID);
                    } else {
                        Common.TransferUniqueID = InventoryTransferList.get(position).getTransUniqueID();
                    }*/
                    Common.TransferUniqueID = InventoryTransferList.get(position).getTransUniqueID();

                    InventoryTransferSync(Common.VBB_Number, Common.TransferID);
                }
            });
            holder.printIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Common.VBB_Number = InventoryTransferList.get(position).getVBB_Number();
                        Common.TransferID = InventoryTransferList.get(position).getTransferID();
                        Common.TransferUniqueID = InventoryTransferList.get(position).getTransUniqueID();
                        Common.InventorytransferScannedResultList.clear();
                        Common.InventorytransferScannedResultList = mDBInternalHelper.getInventoryTransferWithVBBNumber(Common.VBB_Number, Common.TransferID);
                        if (Common.InventorytransferScannedResultList.size() > 0) {
                            Common.DriverID = InventoryTransferList.get(position).getDriverID();
                            Common.DriverName = mDBExternalHelper.getAllDriverName(Common.DriverID);
                            Common.TransportTypeId = InventoryTransferList.get(position).getTransportTypeId();
                            Common.TransportMode = mDBExternalHelper.getAllTransPortMode(Common.TransportTypeId);
                            Common.FromLocationID = InventoryTransferList.get(position).getFromLocationID();
                            Common.FromLocationname = mDBExternalHelper.getFromLocationName(Common.FromLocationID);
                            Common.ToLocaTransID = InventoryTransferList.get(position).getToLocationID();
                            Common.ToLocationName = mDBExternalHelper.getToLocationName(Common.ToLocaTransID);
                            Common.TransferAgencyID = InventoryTransferList.get(position).getTransferAgencyID();
                            Common.AgencyName = mDBExternalHelper.getAgencyName(Common.TransferAgencyID);
                            Common.TrucklicensePlateNo = InventoryTransferList.get(position).getTruckPlateNumber();
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
                            PrintTransferSlipCheck();
                        } else {
                            AlertDialogBox("Transfer PrintSlip", "No values found, try some other item", false);
                        }
                    } catch (Exception ex) {

                    }
                }
            });
            holder.DeleteIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.TransferID = InventoryTransferList.get(position).getTransferID();
                    //Remove From Transfer Lsit and Scanned list
                    if (InventoryTransferList.get(position).getSyncStatus() == 1) {
                        DeleteTransferListandTransferScannedList(Common.TransferID);
                    } else {
                        if (InventoryTransferList.get(position).getCount() < 5) {
                            DeleteTransferListandTransferScannedList(Common.TransferID);
                            return;
                        }
                        AlertDialogBox("InventoryTransfer", "This is not Syncked yet", false);
                    }
                }
            });

            holder.Background.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Common.TransferUniqueID = InventoryTransferList.get(position).getTransUniqueID();
                    Common.TransferID = InventoryTransferList.get(position).getTransferID();
                    Common.ToLocaTransID = InventoryTransferList.get(position).getToLocationID();
                    Common.DriverID = InventoryTransferList.get(position).getDriverID();
                    Common.TransferAgencyID = InventoryTransferList.get(position).getTransferAgencyID();
                    Common.TrucklicensePlateNo = InventoryTransferList.get(position).getTruckPlateNumber();
                    Common.TransportTypeId = InventoryTransferList.get(position).getTransportTypeId();
                    Common.VBB_Number = InventoryTransferList.get(position).getVBB_Number();
                    Common.TruckDeatilsList.clear();
                    Common.TruckDeatilsList = mDBExternalHelper.getAllTruckDetails();
                    for (int i = 0; i < Common.TruckDeatilsList.size(); i++) {
                        if (Common.TrucklicensePlateNo.equals(Common.TruckDeatilsList.get(i).getTruckLicensePlateNo())) {
                            Common.TransportId = Common.TruckDeatilsList.get(i).getTransportId();
                        }
                    }
                    if (InventoryTransferList.get(position).getSyncStatus() == 0) {
                        Common.IsEditorViewFlag = true;
                        Common.IsTransferEditListFlag = false;
                    } else {
                        Common.IsEditorViewFlag = false;
                    }
                    InventorTransferAcivityCall();
                    return false;
                }
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

    public void InventoryTransferSync(String VbbNumber, int TransferId) {
        Common.InventoryTransferInputList.clear();
        Common.InventoryTransferInputList = mDBInternalHelper.getTransferScannedResultInputWithVBBNo(VbbNumber, TransferId);
        if (Common.InventoryTransferInputList.size() > 0) {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                new GetInventoryTransferSyncAsynkTask().execute();
            }
        } else {
            AlertDialogBox("InventoryTransfer Sync", "Values are empty", false);
        }
    }

    class GetInventoryTransferSyncAsynkTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.SyncStatusList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String MethodName = "InsertHHInventoryTransfer_v2";//"InsertHHInventoryTransfer/";
            String SyncURL = ServiceURL.getServiceURL(ServiceURL.ControllorName, MethodName);
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
            transferSyncModel.setHHInventoryTransfer(Common.InventoryTransferInputList);
            try {
                String SyncURLInfo = new Communicator().POST_Obect(SyncURL, transferSyncModel);
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Common.IsConnected == true) {
                        if (Common.SyncStatusList.get(0).getStatus() == 1) {
                            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                            Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                            boolean ListIdFlag = mDBInternalHelper.UpdateInventoryTransferSyncStatusTransID(Common.SyncTime, 1, Common.VBB_Number, Common.TransferID);
                            if (ListIdFlag == true) {
                                //Scanned Result Refresh
                                GetInventoryTransferList();
                                AlertDialogBox("InventoryTransfer", Common.SyncStatusList.get(0).getMessage(), true);
                            }
                        } else {
                            AlertDialogBox("InventoryTransfer", Common.SyncStatusList.get(0).getMessage(), false);
                        }
                    } else {
                        AlertDialogBox("InventoryTransfer", "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
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

    Handler TransferListPrintHan = new Handler();
    Runnable TransferListPrintRun = new Runnable() {
        @Override
        public void run() {
            try {
                Common.VolumeSum = TransferTotalVolume(Common.InventorytransferScannedResultList);
                Common.Count = Common.InventorytransferScannedResultList.size();
                tsc.clearbuffer();
                tsc.sendcommand(printSlip.TransferHeader());
                tsc.sendcommand(printSlip.TransferDetails());
                //tsc.sendcommand(printSlip.TransferBarCodeDetails());
                /*location ----> sabaru to dp*/
                if (Common.Count < (Common.VVBLimitation + 2)) {
                    tsc.sendcommand(printSlip.TransferFooter());
                    tsc.clearbuffer();
                }
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
                Log.e("Exception", ">>>>>>>>>>>" + ex.toString());
            }
        }
    };

    public void PrintoutEnd() {
        tsc.closeport();
        TSCConnInt = -1;
        Common.IsPrintBtnClickFlag = true;
    }

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
                    GetInventoryTransferList();
                }
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
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
        return getActivity().getResources().getString(Common_Msg);
    }

    public boolean CheckisInternetPresent() {
        try {
            isInternetPresent = ConnectionFinder.isInternetOn(getActivity());
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

    public void InventorTransferAcivityCall() {
        Intent _gwIntent = new Intent(getActivity(), InventoryTransferActivity.class);
        startActivity(_gwIntent);
    }

}
