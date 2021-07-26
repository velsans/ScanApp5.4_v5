package com.zebra.main.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tscdll.TSCActivity;
import com.google.gson.Gson;
import com.zebra.R;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.activity.Received.InventoryReceivedActivity;
import com.zebra.main.firebase.CrashAnalytics;
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
import com.zebra.main.api.ServiceURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class InventoryReceivedFragments extends Fragment {
    ReceivedReceiver received_receiver;
    ImageView ReceivedCreateScanListTXT;
    LinearLayout ProgressBarLay;
    InventorReceivedAdapter invenRecievadapter;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;
    private PrintSlipsClass printSlip;
    RecyclerView InventoryReceivedList;
    LinearLayoutManager horizontalLayoutManager;
    TextView NoValueFoundTxT;
    AlertDialogManager alert = new AlertDialogManager();
    boolean isInternetPresent;
    InventoryReceivedSyncModel receiedSyncModel;
    SyncStatusModel suncStaModel;
    TSCActivity tsc = new TSCActivity();
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    View received_rootView;
    SwipeRefreshLayout received_refreshLay;

    public InventoryReceivedFragments() {
        // Required empty public constructor
    }

    private class ReceivedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    }

    public void refresh() {
        if (Common.IsPrintBtnClickFlag == true) {
            if (received_refreshLay.isRefreshing() == false && ProgressBarLay.getVisibility() == View.GONE) {
                GetInventoryReceivedList();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        received_receiver = new ReceivedReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(received_receiver,
                new IntentFilter("RECEIVED_REFRESH"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(received_receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        received_rootView = inflater.inflate(R.layout.inventoryreceivedlist_fragment, container, false);
        Initialization();
        GetInventoryReceivedList();
        received_refreshLay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                received_refreshLay.setRefreshing(false);
                GetInventoryReceivedList();
            }
        });
        return received_rootView;
    }


    public void Initialization() {
        mDBExternalHelper = new ExternalDataBaseHelperClass(getActivity());
        mDBInternalHelper = new InternalDataBaseHelperClass(getActivity());
        printSlip = new PrintSlipsClass(getActivity());
        ProgressBarLay = received_rootView.findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        NoValueFoundTxT = received_rootView.findViewById(R.id.NovalueFound);
        // Inventory Received
        InventoryReceivedList = received_rootView.findViewById(R.id.inventoryReceivedListView);
        ReceivedCreateScanListTXT = received_rootView.findViewById(R.id.receivedCreateScanListTxT);
        received_rootView.findViewById(R.id.receivedCreateScanListTxT).setOnClickListener(mReceivedCreateScanListen);
        received_refreshLay = received_rootView.findViewById(R.id.receivedSwipeRefreshLayout);
        received_refreshLay.setColorScheme(R.color.red, R.color.green, R.color.teal);

    }


    private void GetInventoryReceivedList() {
        try {
            Common.InventoryReceivedList.clear();
            Common.InventoryReceivedList = mDBInternalHelper.getInventoryReceivedIdList(Common.Filter_InventoryReceivedDate.get(Common.InventReceivedDateSelectedIndex));
            if (Common.InventoryReceivedList.size() > 0) {
                invenRecievadapter = new InventorReceivedAdapter(Common.InventoryReceivedList, getActivity());
                horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                invenRecievadapter.notifyDataSetChanged();
                InventoryReceivedList.setLayoutManager(horizontalLayoutManager);
                InventoryReceivedList.setAdapter(invenRecievadapter);
                NoValueFoundTxT.setVisibility(View.GONE);
                InventoryReceivedList.setVisibility(View.VISIBLE);
            } else {
                NoValueFoundTxT.setVisibility(View.VISIBLE);
                InventoryReceivedList.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            Log.d("Exception : %s", ex.toString());        }
    }

    View.OnClickListener mReceivedCreateScanListen = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Common.SyncTime = "";
                Common.VolumeSum = 0.0;
                Common.VBB_Number = "";
                Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                Common.ToLocReceivedID = Common.ToLocationID;
                boolean ListIdFlag = mDBInternalHelper.insertInventoryReceivedIDList(Common.VBB_Number, Common.IMEI, Common.ToLocReceivedID, Common.StartDate, Common.EndDate,
                        Common.FromLocationID, Common.TransportTypeId, Common.TransferAgencyID, Common.DriverID, Common.TransportId, Common.UserID, Common.Count,
                        Common.SyncStatus, Common.SyncTime, Common.Volume, 1, Common.ReceivedUniqueID);
                if (ListIdFlag == true) {
                    Common.InventoryReceivedList = mDBInternalHelper.getInventoryReceivedIdList(Common.Filter_InventoryReceivedDate.get(Common.InventReceivedDateSelectedIndex));
                    if (Common.InventoryReceivedList.size() > 0) {
                        Common.ReceivedID = Integer.parseInt(mDBInternalHelper.getLastReceivedID());
                        String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                        String DeviceID = "";
                        if (String.valueOf(Common.LDeviceID).length() == 1) {
                            DeviceID = "0" + Common.LDeviceID;
                        } else {
                            DeviceID = String.valueOf(Common.LDeviceID);
                        }
                        Common.ReceivedUniqueID = DateUniqueFormat + DeviceID + Common.ReceivedID;
                    }
                    Common.TransferIDsList.clear();
                    Common.IsReceivedEditListFlag = true;
                    Common.IsEditorViewFlag = true;
                    InventorReceivedAcivityCall();
                } else {
                    AlertDialogBox("Add Inventory Received List", "Value not Inserted", false);
                }
            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
                ex.printStackTrace();
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
                }
            });
            holder.DeleteIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.ReceivedID = InventoryReceivedList.get(position).getReceivedID();
                    //Remove From Transfer Lsit and Scanned list
                    if (InventoryReceivedList.get(position).getSyncStatus() == 1) {
                        DeleteReceivedListannReceivedScannedList(Common.ReceivedID);
                    } else {
                        if (InventoryReceivedList.get(position).getCount() < 5) {
                            DeleteReceivedListannReceivedScannedList(Common.ReceivedID);
                            return;
                        }
                        AlertDialogBox("InventoryReceived", "This is not Syncked yet", false);
                    }
                }
            });
            holder.Background.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Common.ReceivedID = InventoryReceivedList.get(position).getReceivedID();
                    Common.VBB_Number = InventoryReceivedList.get(position).getVBB_Number();
                    Common.ReceivedDate = InventoryReceivedList.get(position).getStartDateTime();
                    Common.FromLocationID = InventoryReceivedList.get(position).getFromLocationID();
                    Common.ToLocReceivedID = InventoryReceivedList.get(position).getToLocationID();
                    Common.TransportTypeId = InventoryReceivedList.get(position).getTransportTypeId();
                    Common.TransferAgencyID = InventoryReceivedList.get(position).getTransferAgencyID();
                    Common.DriverID = InventoryReceivedList.get(position).getDriverID();
                    Common.Count = InventoryReceivedList.get(position).getCount();
                    Common.VolumeSum = InventoryReceivedList.get(position).getVolume();
                    Common.TrucklicensePlateNo = InventoryReceivedList.get(position).getTruckPlateNumber();
                    Common.TruckDeatilsList.clear();
                    Common.TruckDeatilsList = mDBExternalHelper.getAllTruckDetails();
                    for (int i = 0; i < Common.TruckDeatilsList.size(); i++) {
                        if (Common.TrucklicensePlateNo.equals(Common.TruckDeatilsList.get(i).getTruckLicensePlateNo())) {
                            Common.TransportId = Common.TruckDeatilsList.get(i).getTransportId();
                        }
                    }
                    if (InventoryReceivedList.get(position).getSyncStatus() == 0) {
                        Common.IsEditorViewFlag = true;
                        Common.IsReceivedEditListFlag = false;
                    } else {
                        Common.IsEditorViewFlag = false;
                    }
                    InventorReceivedAcivityCall();
                    return false;
                }
            });

            holder.printIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Common.VBB_Number = InventoryReceivedList.get(position).getVBB_Number();
                        Common.TransferID = InventoryReceivedList.get(position).getTransferID();
                        Common.ReceivedID = InventoryReceivedList.get(position).getReceivedID();
                        Common.InventoryTransferInputList.clear();
                        Common.InventoryReceivedScannedResultList = mDBInternalHelper.getInventoryReceivedWithVBBNumber(Common.VBB_Number, Common.ReceivedID);
                        if (Common.InventoryReceivedScannedResultList.size() > 0) {
                            Common.DriverID = InventoryReceivedList.get(position).getDriverID();
                            Common.DriverName = mDBExternalHelper.getAllDriverName(Common.DriverID);
                            Common.TransportTypeId = InventoryReceivedList.get(position).getTransportTypeId();
                            Common.TransportMode = mDBExternalHelper.getAllTransPortMode(Common.TransportTypeId);
                            Common.FromLocationID = InventoryReceivedList.get(position).getFromLocationID();
                            Common.FromLocationname = mDBExternalHelper.getFromLocationName(Common.FromLocationID);
                            Common.ToLocaTransID = InventoryReceivedList.get(position).getToLocationID();
                            Common.ToLocationName = mDBExternalHelper.getToLocationName(Common.ToLocaTransID);
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

    public void InventoryReceivedSync(String VbbNumber, int ReceivedID) {
        Common.InventoryReceivedInputList.clear();
        Common.InventoryReceivedInputList = mDBInternalHelper.getReceivedScannedResultInputWithVBBNo(VbbNumber, ReceivedID);
        if (Common.InventoryReceivedInputList.size() > 0) {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                new GetInventoryReceivedSyncAsynkTask().execute();
            }
        } else {
            AlertDialogBox("InventoryTransfer Sync", "Values are empty", false);
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
                        String SyncResponceStr = jsonObj.getString("SyncStatusModel");
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
                            boolean ListIdFlag = mDBInternalHelper.UpdateInventoryReceivedSyncStatusReceivedID(Common.SyncTime, 1, Common.ReceivedID);
                            if (ListIdFlag == true) {
                                //Scanned Result Refresh
                                GetInventoryReceivedList();
                                AlertDialogBox(CommonMessage(R.string.TransferHead), Common.SyncStatusList.get(0).getMessage(), true);
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.TransferHead), Common.SyncStatusList.get(0).getMessage(), false);
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.TransferHead), "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }
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
                tsc.sendcommand(printSlip.ReceivedHeader());
                tsc.sendcommand(printSlip.ReceivedBarCodeSlips());
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

    public void DeleteReceivedListannReceivedScannedList(int receivedID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteInventoryReceivedListID(receivedID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteInventoryReceivedScanned(receivedID);
                if (DeleteScannedFlag == true) {
                    GetInventoryReceivedList();
                }
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteReceivedListannReceivedScannedList", ex.toString(), false);
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

    public double ReceivedTotalVolume(ArrayList<InventoryReceivedModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (InventoryReceivedModel inventoreceScanModel : TotalScannedList) {
            TotVolume = TotVolume + Double.parseDouble(inventoreceScanModel.getVolume());
        }
        return TotVolume;
    }

    public void InventorReceivedAcivityCall() {
        Intent _gwIntent = new Intent(getActivity(), InventoryReceivedActivity.class);
        startActivity(_gwIntent);
    }

}
