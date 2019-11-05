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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tscdll.TSCActivity;
import com.google.gson.Gson;
import com.zebra.R;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.activity.FellingRegistration.FellingRegistrationActivity;
import com.zebra.main.model.ExternalDB.LocationsModel;
import com.zebra.main.model.FellingRegistration.FellingRegisterListModel;
import com.zebra.main.model.FellingRegistration.FellingRegistrationSyncModel;
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

import java.util.Calendar;
import java.util.List;

public class FellingRegistrationFragments extends Fragment {
    FellingReceiver felling_receiver;
    ImageView fellingRegScanListTxT;
    LinearLayout ProgressBarLay;
    FellingRegistrationAdapter fellingRegadapter;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;
    private PrintSlipsClass printSlip;
    RecyclerView FellingRegListView;
    LinearLayoutManager horizontalLayoutManager;
    TextView NoValueFoundTxT;
    AlertDialogManager alert = new AlertDialogManager();
    boolean isInternetPresent;
    FellingRegistrationSyncModel fellingSyncModel;
    SyncStatusModel suncStaModel;
    TSCActivity tsc = new TSCActivity();
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    SwipeRefreshLayout felling_refreshLay;
    View Felling_rootView;


    public FellingRegistrationFragments() {
        // Required empty public constructor
    }

    private class FellingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    }

    public void refresh() {
        if (Common.IsPrintBtnClickFlag == true) {
            if (felling_refreshLay.isRefreshing() == false && ProgressBarLay.getVisibility() == View.GONE) {
                GetFellingRegistrationList();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        felling_receiver = new FellingReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(felling_receiver,
                new IntentFilter("FELLING_REFRESH"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(felling_receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Felling_rootView = inflater.inflate(R.layout.fellingregistrationlist_fragment, container, false);
        Initialization();
        GetFellingRegistrationList();
        felling_refreshLay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                felling_refreshLay.setRefreshing(false);
                GetFellingRegistrationList();
            }
        });
        /*FellingRegListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (PendingToColLV.getChildAt(0) != null) {
                    pendinglist_refreshLay.setEnabled(PendingToColLV.getFirstVisiblePosition() == 0 && PendingToColLV.getChildAt(0).getTop() == 0);
                }
            }
        });*/
        return Felling_rootView;
    }

    public void Initialization() {
        mDBExternalHelper = new ExternalDataBaseHelperClass(getActivity());
        mDBInternalHelper = new InternalDataBaseHelperClass(getActivity());
        printSlip = new PrintSlipsClass(getActivity());
        // Felling Registration
        fellingRegScanListTxT = Felling_rootView.findViewById(R.id.fellingRegScanListTxT);
        Felling_rootView.findViewById(R.id.fellingRegScanListTxT).setOnClickListener(mFellingRegisterCreateScanListen);
        FellingRegListView = Felling_rootView.findViewById(R.id.fellingReg_RecyclerListView);
        ProgressBarLay = Felling_rootView.findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        NoValueFoundTxT = Felling_rootView.findViewById(R.id.NovalueFound);
        felling_refreshLay = Felling_rootView.findViewById(R.id.fellingSwipeRefreshLayout);
        felling_refreshLay.setColorScheme(R.color.red, R.color.green, R.color.teal);
    }

    public void GetFellingRegistrationList() {
        try {
            Common.FellingRegisterList.clear();
            Common.FellingRegisterList = mDBInternalHelper.getFellingRegisterList();
            if (Common.FellingRegisterList.size() > 0) {
                fellingRegadapter = new FellingRegistrationAdapter(Common.FellingRegisterList, getActivity());
                horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                fellingRegadapter.notifyDataSetChanged();
                FellingRegListView.setLayoutManager(horizontalLayoutManager);
                FellingRegListView.setAdapter(fellingRegadapter);
                NoValueFoundTxT.setVisibility(View.GONE);
                FellingRegListView.setVisibility(View.VISIBLE);
            } else {
                NoValueFoundTxT.setVisibility(View.VISIBLE);
                FellingRegListView.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
    }

    View.OnClickListener mFellingRegisterCreateScanListen = new View.OnClickListener() {
        public void onClick(View v) {
            Common.SyncTime = "";
            Common.SyncStatus = 0;
            Common.FellingRegDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            boolean ListIdFlag = mDBInternalHelper.insertFellingRegisterList(Common.FellingRegNo, Common.FellingRegDate, Common.FellingSectionId, Common.ToLocationID, Common.EndDate,
                    0, Common.FellingRegUniqueID, Common.SyncStatus, Common.SyncTime, 1, Common.UserID, Common.IMEI);
            if (ListIdFlag == true) {
                Common.FellingRegisterList = mDBInternalHelper.getFellingRegisterList();
                if (Common.FellingRegisterList.size() > 0) {
                    Common.FellingRegID = Integer.parseInt(mDBInternalHelper.getLastFellingRegD());
                    /*Add Felling Reg Unique ID*/
                    String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                    String DeviceID = "";
                    if (String.valueOf(Common.LDeviceID).length() == 1) {
                        DeviceID = "0" + String.valueOf(Common.LDeviceID);
                    } else {
                        DeviceID = String.valueOf(Common.LDeviceID);
                    }
                    Common.FellingRegUniqueID = String.valueOf(DateUniqueFormat + DeviceID + Common.FellingRegID);
                }
            }
            InventorFellingRegAcivityCall();
            Common.IsFellingRegEditListFlag = true;
            Common.IsEditorViewFlag = true;
        }
    };

    public class FellingRegistrationAdapter extends RecyclerView.Adapter<FellingRegistrationAdapter.FellingRegViewHolder> {
        private List<FellingRegisterListModel> FellingRegisterList;
        Context context;

        public FellingRegistrationAdapter(List<FellingRegisterListModel> FellingRegisterList, Context context) {
            this.FellingRegisterList = FellingRegisterList;
            this.context = context;
        }

        @Override
        public FellingRegistrationAdapter.FellingRegViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.felling_registerlistinfliator, parent, false);
            FellingRegistrationAdapter.FellingRegViewHolder gvh = new FellingRegistrationAdapter.FellingRegViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return FellingRegisterList.size();
        }

        @Override
        public void onBindViewHolder(FellingRegistrationAdapter.FellingRegViewHolder holder, final int position) {
            holder.FellingRegIDTxT.setText(String.valueOf(FellingRegisterList.get(position).getFellingRegID()));
            holder.FellingRegNoTxT.setText(String.valueOf(FellingRegisterList.get(position).getFellingRegistrationNumber()));
            holder.RegisterDateTxT.setText(FellingRegisterList.get(position).getFellingRegistrationDate());
            holder.EndTimeTXT.setText(FellingRegisterList.get(position).getEndDateTime());
            holder.CountTXT.setText(String.valueOf(FellingRegisterList.get(position).getCount()));
            holder.SyncStatusTXT.setText(String.valueOf(FellingRegisterList.get(position).getSyncStatus()));

            if (FellingRegisterList.get(position).getSyncStatus() == 1) {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.red));
            }
            holder.SyncTimeTXT.setText(FellingRegisterList.get(position).getSyncTime());
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            if (FellingRegisterList.get(position).getCount() > 0) {
                holder.syncTXT.setVisibility(View.VISIBLE);
                holder.PrintIMG.setVisibility(View.VISIBLE);
            } else {
                holder.syncTXT.setVisibility(View.INVISIBLE);
                holder.PrintIMG.setVisibility(View.INVISIBLE);
            }
            holder.syncTXT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Sync Date to BackEnd
                    Common.SyncStartDateTime = FellingRegisterList.get(position).getFellingRegistrationDate();
                    Common.SyncEndDateTime = FellingRegisterList.get(position).getEndDateTime();
                    Common.FellingRegID = FellingRegisterList.get(position).getFellingRegID();
                    Common.FellingRegNo = FellingRegisterList.get(position).getFellingRegistrationNumber();
                    Common.SyncBarCodeCount = FellingRegisterList.get(position).getCount();
                    Common.FellingSectionId = FellingRegisterList.get(position).getFellingSectionID();
                    Common.FellingRegUniqueID = FellingRegisterList.get(position).getFellingRegistrationUniqueID();
                    FellingRegistrayionSync(Common.FellingRegID);
                }
            });
            holder.DeleteIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.FellingRegID = FellingRegisterList.get(position).getFellingRegID();
                    //Remove From Transfer Lsit and Scanned list
                    if (FellingRegisterList.get(position).getSyncStatus() == 1) {
                        DeleteFellingListannFellingScannedList(Common.FellingRegID);
                    } else {
                        if (FellingRegisterList.get(position).getCount() < 5) {
                            DeleteFellingListannFellingScannedList(Common.FellingRegID);
                            return;
                        }
                        AlertDialogBox("InventoryTransfer", "This is not Syncked yet", false);
                    }
                }
            });
            holder.Background.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Common.FellingRegUniqueID = FellingRegisterList.get(position).getFellingRegistrationUniqueID();
                    Common.FellingRegID = FellingRegisterList.get(position).getFellingRegID();
                    Common.ToLocationID = FellingRegisterList.get(position).getLocationID();
                    Common.LocationList.clear();
                    Common.LocationList = mDBExternalHelper.getToLocations();
                    for (LocationsModel locationMod : Common.LocationList) {
                        if (Common.ToLocationID == locationMod.getToLocationId()) {
                            Common.ToLocationName = locationMod.getLocation();
                        }
                    }
                    Common.FellingSectionId = FellingRegisterList.get(position).getFellingSectionID();
                    Common.FellingRegDate = FellingRegisterList.get(position).getFellingRegistrationDate();
                    Common.FellingRegNo = FellingRegisterList.get(position).getFellingRegistrationNumber();
                    if (FellingRegisterList.get(position).getSyncStatus() == 0) {
                        Common.IsEditorViewFlag = true;
                        Common.IsFellingRegEditListFlag = false;
                    } else {
                        Common.IsEditorViewFlag = false;
                    }
                    InventorFellingRegAcivityCall();
                    return false;
                }
            });
            holder.PrintIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Common.FellingRegUniqueID = FellingRegisterList.get(position).getFellingRegistrationUniqueID();
                        Common.FellingRegID = FellingRegisterList.get(position).getFellingRegID();
                        Common.ToLocationID = FellingRegisterList.get(position).getLocationID();
                        Common.ToLocationName = mDBExternalHelper.getToLocationName(Common.ToLocationID);
                        Common.FellingSectionId = FellingRegisterList.get(position).getFellingSectionID();
                        Common.FellingRegDate = FellingRegisterList.get(position).getFellingRegistrationDate();
                        Common.FellingRegNo = FellingRegisterList.get(position).getFellingRegistrationNumber();
                        Common.FellingRegisterInputList.clear();
                        Common.FellingRegisterLogsDetails = mDBInternalHelper.getFellingformDetailsID(Common.FellingRegID,Common.FellingSectionId);
                        if (Common.FellingRegisterLogsDetails.size() > 0) {
                            PrintFellingSlipCheck();
                        } else {
                            AlertDialogBox("Transfer PrintSlip", "No values found, try some other item", false);
                        }
                    } catch (Exception ex) {

                    }
                }
            });
        }

        public class FellingRegViewHolder extends RecyclerView.ViewHolder {
            TextView FellingRegIDTxT, FellingRegNoTxT, RegisterDateTxT, EndTimeTXT, CountTXT, SyncStatusTXT, SyncTimeTXT;
            ImageView syncTXT, PrintIMG, DeleteIMG;
            LinearLayout Background;

            public FellingRegViewHolder(View view) {
                super(view);
                Background = view.findViewById(R.id.fellinglayoutBackground);
                FellingRegIDTxT = view.findViewById(R.id.fellingRegidTxT);
                FellingRegNoTxT = view.findViewById(R.id.fellingRegNoTxT);
                RegisterDateTxT = view.findViewById(R.id.fellingRegDateTxT);
                EndTimeTXT = view.findViewById(R.id.fellingRegEndDateTxT);
                CountTXT = view.findViewById(R.id.fellingReg_countTxT);
                SyncStatusTXT = view.findViewById(R.id.fellingReg_StatusTxT);
                SyncTimeTXT = view.findViewById(R.id.fellingReg_syncTimeTxT);
                syncTXT = view.findViewById(R.id.fellingReg_syncBtN);
                DeleteIMG = view.findViewById(R.id.fellingReg_remove);
                PrintIMG = view.findViewById(R.id.fellingReg_PrintBtn);
            }
        }
    }

    public void DeleteFellingListannFellingScannedList(int fellingID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteFellingRegistrationListID(fellingID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteFellingRegistrationScanned(fellingID);
                if (DeleteScannedFlag == true) {
                    GetFellingRegistrationList();
                }
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteReceivedListannReceivedScannedList", ex.toString(), false);
        }
    }

    public void PrintFellingSlipCheck() {
        if (Common.IsPrintBtnClickFlag == true) {
            if (Common.FellingRegisterLogsDetails.size() > 0) {
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
    }

    public void FellingRegistrayionSync(int FellingRegID) {
        Common.FellingRegisterInputList.clear();
        Common.FellingRegisterInputList = mDBInternalHelper.getFellingRegInputWithFellingUniqID(FellingRegID);
        if (Common.FellingRegisterInputList.size() > 0) {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                new GetFellingRegSyncAsynkTask().execute();
            }
        } else {
            AlertDialogBox("InventoryTransfer Sync", "Values are empty", false);
        }
    }

    class GetFellingRegSyncAsynkTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.SyncStatusList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String MethodName = "FellingRegistration/";
            String SyncURL = ServiceURL.getServiceURL(ServiceURL.ControllorName, MethodName);
            fellingSyncModel = new FellingRegistrationSyncModel();
            fellingSyncModel.setFellingRegID(Common.FellingRegID);
            fellingSyncModel.setFellingRegistrationNumber(Common.FellingRegNo);
            fellingSyncModel.setLocationID(Common.ToLocationID);
            fellingSyncModel.setTotalCount(Common.SyncBarCodeCount);
            fellingSyncModel.setIMEI(Common.IMEI);
            fellingSyncModel.setFellingRegisterUniqueID(Common.FellingRegUniqueID);
            fellingSyncModel.setFellingSectionID(Common.FellingSectionId);
            fellingSyncModel.setFellingRegistrationDate(Common.SyncStartDateTime);
            fellingSyncModel.setEndDateTime(Common.SyncEndDateTime);
            fellingSyncModel.setUserID(Common.UserID);
            fellingSyncModel.setFellingReg(Common.FellingRegisterInputList);
            try {
                String SyncURLInfo = new Communicator().POST_Obect(SyncURL, fellingSyncModel);
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
                            boolean ListIdFlag = mDBInternalHelper.UpdateFellingRegSyncStatusFellingRegID(Common.SyncTime, 1, Common.FellingRegID);
                            if (ListIdFlag == true) {
                                //Scanned Result Refresh
                                GetFellingRegistrationList();
                                AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), Common.SyncStatusList.get(0).getMessage(), true);
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), Common.SyncStatusList.get(0).getMessage(), false);
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
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

    public void InventorFellingRegAcivityCall() {
        Intent _gwIntent = new Intent(getActivity(), FellingRegistrationActivity.class);
        startActivity(_gwIntent);
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

    Handler FellingRegPrintHan = new Handler();
    Runnable FellingRegPrintRun = new Runnable() {
        @Override
        public void run() {
            try {
                Common.Count = Common.FellingRegisterLogsDetails.size();
                tsc.clearbuffer();
                tsc.sendcommand(printSlip.FellingRegisterHeader());
                tsc.clearbuffer();
                tsc.sendcommand(printSlip.FellingRegisterBarCodeDetails());
                tsc.clearbuffer();
                PrintoutEnd();
             /*   int TimerforPrint = 0;
                if (Common.Count > 30) {
                    TimerforPrint = ((Common.Count / 30) * 2) * 1000;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PrintoutEnd();
                    }
                }, TimerforPrint);*/
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
}
