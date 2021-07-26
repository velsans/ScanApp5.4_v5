package com.zebra.main.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.zebra.main.activity.Count.InventoryCountActivity;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.model.InvCount.InventoryCountModel;
import com.zebra.main.model.InvCount.InventoryCountSyncModel;
import com.zebra.main.model.SyncStatusModel;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;
import com.zebra.utilities.Communicator;
import com.zebra.utilities.ConnectionFinder;
import com.zebra.utilities.GwwException;
import com.zebra.utilities.PrintSlipsClass;
import com.zebra.main.api.ServiceURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;


public class InventoryCountFragment extends Fragment {
    CountReceiver count_receiver;
    ImageView CountcreateScanListTXT;
    LinearLayout ProgressBarLay;
    InventoryCountAdapter invenCountadapter;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;
    private PrintSlipsClass printSlip;
    RecyclerView InventoryCountList;
    LinearLayoutManager horizontalLayoutManager;
    TextView NoValueFoundTxT;
    AlertDialogManager alert = new AlertDialogManager();
    boolean isInternetPresent;
    InventoryCountSyncModel countSyncModel;
    SyncStatusModel suncStaModel;
    TSCActivity tsc = new TSCActivity();
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    View count_rootView;
    SwipeRefreshLayout count_refreshLay;


    public InventoryCountFragment() {
        // Required empty public constructor
    }

    private class CountReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    }

    public void refresh() {
        if (Common.IsPrintBtnClickFlag == true) {
            if (count_refreshLay.isRefreshing() == false && ProgressBarLay.getVisibility() == View.GONE) {
                GetInventoryCountList();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        count_receiver = new CountReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(count_receiver,
                new IntentFilter("COUNT_REFRESH"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(count_receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        count_rootView = inflater.inflate(R.layout.inventorycountlist_fragment, container, false);
        Initialization();
        GetInventoryCountList();
        count_refreshLay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                count_refreshLay.setRefreshing(false);
                GetInventoryCountList();
            }
        });
        return count_rootView;
    }

    public void Initialization() {
        mDBExternalHelper = new ExternalDataBaseHelperClass(getActivity());
        mDBInternalHelper = new InternalDataBaseHelperClass(getActivity());
        printSlip = new PrintSlipsClass(getActivity());
        ProgressBarLay = count_rootView.findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        NoValueFoundTxT = count_rootView.findViewById(R.id.NovalueFound);
        CountcreateScanListTXT = count_rootView.findViewById(R.id.inventoryCountcreateListTxT);
        InventoryCountList = count_rootView.findViewById(R.id.inventoryCountListView);
        count_rootView.findViewById(R.id.inventoryCountcreateListTxT).setOnClickListener(mCountCreateScanListen);
        count_refreshLay = count_rootView.findViewById(R.id.countSwipeRefreshLayout);
        count_refreshLay.setColorScheme(R.color.red, R.color.green, R.color.teal);
    }

    View.OnClickListener mCountCreateScanListen = new View.OnClickListener() {
        public void onClick(View v) {
            Common.SyncTime = "";
            Common.SyncStatus = 0;
            Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            boolean ListIdFlag = mDBInternalHelper.insertInventoryCountListID(Common.IMEI, Common.ToLocationID, Common.StartDate, Common.EndDate, 0, Common.SyncStatus,
                    Common.SyncTime, 1, Common.CountUniqueID);
            if (ListIdFlag == true) {
                Common.InventoryCountList = mDBInternalHelper.getInventoryCountIdList(Common.Filter_InventoryCountDate.get(Common.InventCountDateSelectedIndex));
                if (Common.InventoryCountList.size() > 0) {
                    Common.ListID = Integer.parseInt(mDBInternalHelper.getLastCountID());
                    String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                    String DeviceID = "";
                    if (String.valueOf(Common.LDeviceID).length() == 1) {
                        DeviceID = "0" + Common.LDeviceID;
                    } else {
                        DeviceID = String.valueOf(Common.LDeviceID);
                    }
                    Common.CountUniqueID = DateUniqueFormat + DeviceID + Common.ListID;
                    Intent _gwwIntent = new Intent(getActivity(), InventoryCountActivity.class);
                    startActivity(_gwwIntent);
                }
            }
        }
    };

    private void GetInventoryCountList() {
        try {
            Common.InventoryCountList.clear();
            Common.InventoryCountList = mDBInternalHelper.getInventoryCountIdList(Common.Filter_InventoryCountDate.get(Common.InventCountDateSelectedIndex));
            if (Common.InventoryCountList.size() > 0) {
                invenCountadapter = new InventoryCountAdapter(Common.InventoryCountList, getActivity());
                horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                invenCountadapter.notifyDataSetChanged();
                InventoryCountList.setLayoutManager(horizontalLayoutManager);
                InventoryCountList.setAdapter(invenCountadapter);
                NoValueFoundTxT.setVisibility(View.GONE);
                InventoryCountList.setVisibility(View.VISIBLE);
            } else {
                NoValueFoundTxT.setVisibility(View.VISIBLE);
                InventoryCountList.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public class InventoryCountAdapter extends RecyclerView.Adapter<InventoryCountAdapter.CountViewHolder> {
        private List<InventoryCountModel> InventoryCountList;
        Context context;

        public InventoryCountAdapter(List<InventoryCountModel> ScannedResultList, Context context) {
            this.InventoryCountList = ScannedResultList;
            this.context = context;
        }

        @Override
        public InventoryCountAdapter.CountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_countlist_infliator, parent, false);
            InventoryCountAdapter.CountViewHolder gvh = new InventoryCountAdapter.CountViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(InventoryCountAdapter.CountViewHolder holder, final int position) {
            //holder.ListIDTXT.setText(String.valueOf(position + 1));
            holder.ListIDTXT.setText(String.valueOf(InventoryCountList.get(position).getListID()));
            holder.ImeiTXT.setText(InventoryCountList.get(position).getImei());
            holder.LocationIDTXT.setText(String.valueOf(InventoryCountList.get(position).getLocationID()));
            holder.StartTimeTXT.setText(InventoryCountList.get(position).getStartTime());
            holder.EndTimeTXT.setText(InventoryCountList.get(position).getEndTime());
            holder.CountTXT.setText(String.valueOf(InventoryCountList.get(position).getCount()));
            holder.VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(InventoryCountList.get(position).getVolume())));
            holder.SyncStatusTXT.setText(String.valueOf(InventoryCountList.get(position).getSyncStatus()));

            if (InventoryCountList.get(position).getSyncStatus() == 1) {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.red));
            }
           /* if (InventoryCountList.get(position).getSyncTime().isEmpty()) {
                holder.SyncTimeTXT.setText("Not yet");
            } else {*/
            holder.SyncTimeTXT.setText(InventoryCountList.get(position).getSyncTime());
            //}
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            if (InventoryCountList.get(position).getCount() > 0) {
                holder.syncTXT.setVisibility(View.VISIBLE);
            } else {
                holder.syncTXT.setVisibility(View.INVISIBLE);
            }
            holder.syncTXT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Sync Date to BackEnd
                    //Common.IMEI = getImeiNumber();
                    Common.SyncStartDateTime = InventoryCountList.get(position).getStartTime();
                    Common.SyncEndDateTime = InventoryCountList.get(position).getEndTime();
                    Common.SyncListID = InventoryCountList.get(position).getListID();
                    Common.SyncBarCodeCount = InventoryCountList.get(position).getCount();
                    Common.LocationID = InventoryCountList.get(position).getLocationID();
                    InventoryCountSync(Common.SyncListID);
                }
            });
            holder.DeleteIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Remove From Transfer Lsit and Scanned list
                    Common.ListID = InventoryCountList.get(position).getListID();
                    if (InventoryCountList.get(position).getSyncStatus() == 1) {
                        DeleteTransferCountScannedList(Common.ListID);
                    } else {
                        if (InventoryCountList.get(position).getCount() < 5) {
                            DeleteTransferCountScannedList(Common.ListID);
                            return;
                        }
                        AlertDialogBox(CommonMessage(R.string.TransferHead), "This is not Syncked yet", false);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return InventoryCountList.size();
        }

        public class CountViewHolder extends RecyclerView.ViewHolder {
            TextView ListIDTXT, ImeiTXT, LocationIDTXT, StartTimeTXT, EndTimeTXT, CountTXT, SyncStatusTXT, SyncTimeTXT, VolumeTXT;
            ImageView syncTXT, DeleteIMG;
            LinearLayout Background;

            public CountViewHolder(View view) {
                super(view);
                Background = view.findViewById(R.id.countlayoutBackground);
                ListIDTXT = view.findViewById(R.id.count_listidTxT);
                ImeiTXT = view.findViewById(R.id.count_imeiTxT);
                LocationIDTXT = view.findViewById(R.id.count_locationIDTxT);
                StartTimeTXT = view.findViewById(R.id.count_startTimeTxT);
                EndTimeTXT = view.findViewById(R.id.count_endTimeTxT);
                CountTXT = view.findViewById(R.id.count_countTxT);
                SyncStatusTXT = view.findViewById(R.id.count_syncStatusTxT);
                SyncTimeTXT = view.findViewById(R.id.count_syncTimeTxT);
                syncTXT = view.findViewById(R.id.count_syncBtN);
                DeleteIMG = view.findViewById(R.id.count_delete);
                VolumeTXT = view.findViewById(R.id.count_VolumeTxT);
            }
        }
    }

    public void InventoryCountSync(int ListID) {
        Common.InventoryCountInputList.clear();
        Common.InventoryCountInputList = mDBInternalHelper.getScannedResultInputWithListID(ListID);
        if (Common.InventoryCountInputList.size() > 0) {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                new GetInventoryCountSyncAsynkTask().execute();
            }
        } else {
            AlertDialogBox("InventoryCount Sync", "List id is empty", false);
        }

    }

    // Method for SyncAPI
    class GetInventoryCountSyncAsynkTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.SyncStatusList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String MethodName = "InsertHHInventoryCount_V2";//"InsertHHInventoryCount/";
            String SyncURL = ServiceURL.getServiceURL(ServiceURL.ControllorName, MethodName);
            countSyncModel = new InventoryCountSyncModel();
            countSyncModel.setIMEI(Common.IMEI);
            countSyncModel.setStartedTime(Common.SyncStartDateTime);
            countSyncModel.setEndDateTime(Common.SyncEndDateTime);
            countSyncModel.setListID(Common.SyncListID);
            countSyncModel.setBarCodeCount(Common.SyncBarCodeCount);
            countSyncModel.setLocationID(Common.LocationID);
            countSyncModel.setUserId(Common.UserID);
            countSyncModel.setHHScannedResult(Common.InventoryCountInputList);
            try {
                String SyncURLInfo = new Communicator().POST_Obect(SyncURL, countSyncModel);
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
                            boolean ListIdFlag = mDBInternalHelper.UpdateInventoryCountSyncStatusListID(Common.SyncTime, 1, Common.EndDate, Common.SyncListID);
                            if (ListIdFlag == true) {
                                //Scanned Result Refresh
                                GetInventoryCountList();
                                AlertDialogBox("InventoryCount", Common.SyncStatusList.get(0).getMessage(), true);
                            }
                        } else {
                            AlertDialogBox("InventoryCount", Common.SyncStatusList.get(0).getMessage(), false);
                        }
                    } else {
                        AlertDialogBox("InventoryCount", "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }
    }

    public void DeleteTransferCountScannedList(int ListID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteInventoryCountListID(ListID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteInventoryCountScanned(ListID);
                if (DeleteScannedFlag == true) {
                    GetInventoryCountList();
                }
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("DeleteTransferCountandTransferScannedList", ex.toString(), false);
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
            CrashAnalytics.CrashReport(ex);
        }
        if (!isInternetPresent) {
            AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            return false;
        } else {
            return true;
        }
    }
}
