package com.zebra.main.activity.FellingRegistration;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.example.tscdll.TSCActivity;
import com.google.gson.Gson;
import com.zebra.R;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;

import com.zebra.main.activity.Common.GwwMainActivity;
import com.zebra.main.interfac.FellingFilterInterface;
import com.zebra.main.model.ExternalDB.ConcessionNamesModel;
import com.zebra.main.model.ExternalDB.FellingSectionModel;
import com.zebra.main.model.FellingRegistration.FellingRegisterListModel;
import com.zebra.main.model.FellingRegistration.FellingRegistrationSyncModel;
import com.zebra.main.model.SyncStatusModel;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.BlueTooth;
import com.zebra.utilities.BlutoothCommonClass;
import com.zebra.utilities.Common;
import com.zebra.utilities.Communicator;
import com.zebra.utilities.ConnectionFinder;
import com.zebra.utilities.DeviceIMEIClass;
import com.zebra.utilities.GwwException;
import com.zebra.utilities.PrintSlipsClass;
import com.zebra.utilities.ServiceURL;
import com.zebra.utilities.Utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class
FellingRegistationListActivitiy extends Activity implements FellingFilterInterface {
    private InternalDataBaseHelperClass mDBInternalHelper;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    DeviceIMEIClass device_imei;
    AlertDialog.Builder Signoutbuilder = null;
    AlertDialog SignoutAlert = null;
    PrintSlipsClass printSlip;
    ArrayList<FellingRegisterListModel> FellingRegisterFilteredList = new ArrayList<FellingRegisterListModel>();
    FellingRegistrationAdapter fellingRegadapter;
    LinearLayoutManager horizontalLayoutManager;
    FrameLayout GmailSend_layout;
    RecyclerView FellingRegListView;
    EditText editTextEmail, editTextSubject;
    AutoCompleteTextView FellingSectionSearchATV;
    CheckBox SyncAllFRCheckBox;
    LinearLayout ProgressBarLay;
    TextView NoValueFoundTxT, TotalFilteredCount, TotalFilteredVolume;
    ImageView fellingRegScanListTxT, FellSecExportTOExcel, ButtonClose;
    AlertDialogManager alert = new AlertDialogManager();
    boolean isInternetPresent;
    TSCActivity tsc = new TSCActivity();
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2, columnIndex, PICK_FROM_GALLERY = 101, PICK_FROM_DEVICE = 101;
    FellingRegistrationSyncModel fellingSyncModel;
    SyncStatusModel suncStaModel;
    String directory_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BackUp/", Subject_path = "", email, BCC, subject, message, attachmentFile;
    SQLiteToExcel sqliteToExcel;
    private static HSSFWorkbook myWorkBook = new HSSFWorkbook();
    private static HSSFSheet mySheet = myWorkBook.createSheet();
    ArrayAdapter<String> StringtreeNoadapter;
    Uri URI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fellingregistration_list);
        mDBExternalHelper = new ExternalDataBaseHelperClass(this);
        mDBInternalHelper = new InternalDataBaseHelperClass(this);
        printSlip = new PrintSlipsClass(this);
        device_imei = new DeviceIMEIClass(this);
        Initialization();
        OnClickListener();
        GetFellingRegistrationList();
        getfellingSectionSpinner();
        //editTextSubject.setText(Subject_path);
    }

    @Override
    public void onBackPressed() {
        Intent oneIntent = new Intent(this, GwwMainActivity.class);
        startActivity(oneIntent);
    }

   /* public void GetInventoryTransferDateList() {
        try {
            Common.Filter_FellingRegistrationDate.clear();
            Common.Filter_FellingRegistrationDate = mDBInternalHelper.getFellingRegistrationDate();
            *//*Remove Duplications*//*
            HashSet<String> hashSet = new HashSet<String>();
            hashSet.addAll(Common.Filter_FellingRegistrationDate);
            Common.Filter_FellingRegistrationDate.clear();
            Common.Filter_FellingRegistrationDate.addAll(hashSet);
            if (Common.Filter_FellingRegistrationDate.size() > 0) {
                fellRegDateadapter = new FellingRegistrationDateAdapter(Common.Filter_FellingRegistrationDate, this);
                InveDateLayoutManager = new LinearLayoutManager(this);
                fellRegDateadapter.notifyDataSetChanged();
                InventoryReceivedDateList.setLayoutManager(InveDateLayoutManager);
                InventoryReceivedDateList.setAdapter(fellRegDateadapter);
                NoValueFoundTxT.setVisibility(View.GONE);
                GetFellingRegistrationList(Common.Filter_InventoryReceivedDate.get(Common.FellingRegSelectedIndex));
            } else {
                InventoryReceivedDateList.setVisibility(View.GONE);
                NoValueFoundTxT.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
    }*/

    private void Initialization() {
        ProgressBarLay = findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        NoValueFoundTxT = findViewById(R.id.NovalueFound);
        fellingRegScanListTxT = findViewById(R.id.fellingRegScanListTxT);
        findViewById(R.id.fellingRegScanListTxT).setOnClickListener(mFellingRegisterCreateScanListen);
        TotalFilteredCount = findViewById(R.id.TotalFilteredCount);
        TotalFilteredVolume = findViewById(R.id.TotalFilteredVolume);
        FellingRegListView = findViewById(R.id.fellingReg_RecyclerListView);
        FellingSectionSearchATV = findViewById(R.id.fellingFilter_ATxT);
        FellSecExportTOExcel = findViewById(R.id.FellSecExportTOExcel);
        findViewById(R.id.FellSecExportTOExcel).setOnClickListener(mFellSecExportTOExcel);
        findViewById(R.id.SendExceltoMail_IMG).setOnClickListener(mSendGmailWithExcel);

        findViewById(R.id.buttonAttachment).setOnClickListener(mAttacments);
        findViewById(R.id.buttonSend).setOnClickListener(mSendMail);
        findViewById(R.id.buttonClose).setOnClickListener(mClose);

        SyncAllFRCheckBox = findViewById(R.id.SyncAllFR_checkBox);

        editTextEmail = findViewById(R.id.editTextTo);
        editTextSubject = findViewById(R.id.editTextSubject);
        GmailSend_layout = findViewById(R.id.GmailSend_layout);
        GmailSend_layout.setVisibility(View.GONE);
    }

    public void GetFellingRegistrationList() {
        try {
            Common.FellingRegisterList.clear();
            FellingRegisterFilteredList.clear();
            Common.FellingRegisterList = mDBInternalHelper.getFellingRegisterList();
            FellingRegisterFilteredList.addAll(Common.FellingRegisterList);
            if (Common.FellingRegisterList.size() > 0) {
                fellingRegadapter = new FellingRegistrationAdapter(Common.FellingRegisterList, FellingRegistationListActivitiy.this, FellingRegistationListActivitiy.this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
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
            TotalFilteredCount.setText(String.valueOf(Common.FellingRegisterList.size()));
            TotalFilteredVolume.setText(String.valueOf(mDBInternalHelper.TotalFilteredVolume("")));
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
    }

    private void getfellingSectionSpinner() {
        try {
            Common.FellingSectionList.clear();
            Common.FellingSectionList = mDBExternalHelper.getFellingSectionID(Common.FromLocationID);
            Common.FelllingSectionIDsStringList = new String[Common.FellingSectionList.size()];
            for (int i = 0; i < Common.FellingSectionList.size(); i++) {
                Common.FelllingSectionIDsStringList[i] = String.valueOf(Common.FellingSectionList.get(i).getFellingSectionNumber());
            }
            StringtreeNoadapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, Common.FelllingSectionIDsStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            FellingSectionSearchATV.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
        }
    }

    public void OnClickListener() {
        FellingSectionSearchATV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.e("CharSequence", ">>>>>>>>>>>" + s.toString())
                CharSequence FellingID = "";
                if (s.toString().length() > 0) {
                    for (FellingSectionModel fellingSecMod : Common.FellingSectionList) {
                        if (Integer.parseInt(s.toString()) == fellingSecMod.getFellingSectionNumber()) {
                            FellingID = fellingSecMod.getFellingSectionId();
                        }
                    }
                    if (fellingRegadapter != null) {
                        HideKeyboard();
                        fellingRegadapter.getFilter().filter(FellingID);
                    }
                } else {
                    GetFellingRegistrationList();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        FellingSectionSearchATV.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    FellingSectionSearchATV.showDropDown();
                    FellingSectionSearchATV.requestFocus();
                } catch (Exception ex) {

                }
                return false;
            }
        });
        FellingSectionSearchATV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.e("Item", ">>>>>>>>>>" + Common.FelllingSectionIDsStringList[position]);
                CharSequence FellingID = "";
                Object item = parent.getItemAtPosition(position);
                if (item.toString().length() > 0) {
                    for (FellingSectionModel fellingSecMod : Common.FellingSectionList) {
                        if (Integer.parseInt(item.toString()) == fellingSecMod.getFellingSectionNumber()) {
                            //if (Common.FelllingSectionIDsStringList[position].equals(fellingSecMod.getFellingSectionNumber())) {
                            FellingID = fellingSecMod.getFellingSectionId();
                        }
                    }
                    if (fellingRegadapter != null) {
                        HideKeyboard();
                        fellingRegadapter.getFilter().filter(FellingID);
                    }
                } else {
                    GetFellingRegistrationList();
                }
            }
        });

        SyncAllFRCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SyncAllFRCheckBox.isChecked()) {
                    Common.FellingRegSyncALL = true;
                } else {
                    Common.FellingRegSyncALL = false;
                }
            }
        });

        SyncAllFRCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (SyncAllFRCheckBox.isChecked()) {
                    Common.FellingRegSyncALL = true;
                } else {
                    Common.FellingRegSyncALL = false;
                }
            }
        });
    }

    public class FellingRegistrationAdapter extends RecyclerView.Adapter<FellingRegistrationAdapter.FellingRegViewHolder> implements Filterable {
        private List<FellingRegisterListModel> FellingRegisterList = new ArrayList<>();
        private List<FellingRegisterListModel> FellingRegisterListFilter = new ArrayList<>();
        Context context;
        // private ItemFilter mFilter = new ItemFilter();
        FellingFilterInterface listener;

        public FellingRegistrationAdapter(List<FellingRegisterListModel> FellingRegisterList, Context context, FellingFilterInterface Fe_listener) {
            this.FellingRegisterList = FellingRegisterList;
            this.FellingRegisterListFilter = FellingRegisterList;
            this.context = context;
            this.listener = Fe_listener;
        }

        @Override
        public FellingRegistrationAdapter.FellingRegViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.felling_registerlistinfliator, parent, false);
            FellingRegistrationAdapter.FellingRegViewHolder gvh = new FellingRegistrationAdapter.FellingRegViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return FellingRegisterListFilter.size();
        }

        @Override
        public void onBindViewHolder(FellingRegistrationAdapter.FellingRegViewHolder holder, final int position) {
            try {
                if (FellingRegisterListFilter.size() > 0) {
                    final FellingRegisterListModel FellingRegLstModel = FellingRegisterListFilter.get(position);
                    holder.FellingRegIDTxT.setText(String.valueOf(FellingRegLstModel.getFellingRegistrationUniqueID()));
                    holder.FellingRegNoTxT.setText(String.valueOf(FellingRegLstModel.getFellingRegistrationNumber()));
                    holder.RegisterDateTxT.setText(FellingRegLstModel.getFellingRegistrationDate());
                    holder.EndTimeTXT.setText(FellingRegLstModel.getEndDateTime());
                    holder.CountTXT.setText(String.valueOf(FellingRegLstModel.getCount()));
                    holder.SyncStatusTXT.setText(String.valueOf(FellingRegLstModel.getSyncStatus()));
                    holder.Volume.setText(String.valueOf(Common.decimalFormat.format(FellingRegLstModel.getVolume())));

                    if (FellingRegLstModel.getSyncStatus() == 1) {
                        holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    } else {
                        holder.SyncStatusTXT.setBackgroundColor(context.getResources().getColor(R.color.red));
                    }
                    holder.SyncTimeTXT.setText(FellingRegLstModel.getSyncTime());
                    if (position % 2 == 0) {
                        holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
                    } else {
                        holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
                    }
                    if (FellingRegLstModel.getCount() > 0) {
                        holder.syncTXT.setVisibility(View.VISIBLE);
                        holder.PrintIMG.setVisibility(View.VISIBLE);
                    } else {
                        holder.syncTXT.setVisibility(View.INVISIBLE);
                        holder.PrintIMG.setVisibility(View.INVISIBLE);
                    }
                    holder.syncTXT.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (Common.FellingRegSyncALL == false) {
                                    //Sync Date to BackEnd
                                    Common.SyncStartDateTime = FellingRegLstModel.getFellingRegistrationDate();
                                    Common.SyncEndDateTime = FellingRegLstModel.getEndDateTime();
                                    Common.FellingRegID = FellingRegLstModel.getFellingRegID();
                                    Common.FellingRegNo = FellingRegLstModel.getFellingRegistrationNumber();
                                    Common.SyncBarCodeCount = FellingRegLstModel.getCount();
                                    Common.FellingSectionId = FellingRegLstModel.getFellingSectionID();
                                    Common.FellingRegUniqueID = FellingRegLstModel.getFellingRegistrationUniqueID();
                                    Common.SyncEndDateTime = FellingRegLstModel.getEndDateTime();
                                    Common.TreeFromLocation = FellingRegLstModel.getLocationID();
                                    Common.VolumeSum = FellingRegLstModel.getVolume();
                                    FellingRegistrayionSync(Common.TreeFromLocation, Common.FellingSectionId, Common.FellingRegID, Common.FellingRegUniqueID);
                                } else {
                                    FellingRegSyncALLDialog("Are you sure want to sync all datas?");
                                }
                            } catch (Exception ex) {
                                AlertDialogBox("Felling Sync", ex.toString(), false);
                            }

                        }
                    });
                    holder.DeleteIMG.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Common.FellingRegID = FellingRegLstModel.getFellingRegID();
                                //Remove From Transfer Lsit and Scanned list
                                if (FellingRegLstModel.getSyncStatus() == 1) {
                                    DeleteFellingListannFellingScannedList(Common.FellingRegID);
                                } else {
                                    if (Common.Username.equals("1")) {
                                        FellingDeleteDatasIFUserID_ONE("Are you sure you want delete all datas");
                                    } else {
                                        if (FellingRegLstModel.getCount() < 5) {
                                            DeleteFellingListannFellingScannedList(Common.FellingRegID);
                                            return;
                                        }
                                        AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "This is not Syncked yet", false);
                                    }
                                }
                            } catch (Exception ex) {
                                AlertDialogBox("Felling Delete", ex.toString(), false);
                            }
                        }
                    });
                    holder.Background.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            try {
                                Common.FellingRegUniqueID = FellingRegLstModel.getFellingRegistrationUniqueID();
                                Common.FellingRegID = FellingRegLstModel.getFellingRegID();
                                Common.FromLocationID = FellingRegLstModel.getLocationID();
                                Common.ConcessionList.clear();
                                Common.ConcessionList = mDBExternalHelper.getConcessionList();
                                for (ConcessionNamesModel locationMod : Common.ConcessionList) {
                                    if (Common.FromLocationID == locationMod.getFromLocationId()) {
                                        Common.FromLocationname = locationMod.getConcessionName();
                                    }
                                }
                                Common.FellingSectionId = FellingRegLstModel.getFellingSectionID();
                                Common.FellingRegDate = FellingRegLstModel.getFellingRegistrationDate();
                                Common.FellingRegNo = FellingRegLstModel.getFellingRegistrationNumber();
                                if (FellingRegLstModel.getSyncStatus() == 0) {
                                    Common.IsEditorViewFlag = true;
                                    Common.IsFellingRegEditListFlag = false;
                                } else {
                                    Common.IsEditorViewFlag = false;
                                }
                                InventorFellingRegAcivityCall();
                            } catch (Exception ex) {
                                AlertDialogBox("Felling Background", ex.toString(), false);
                            }
                            return false;
                        }
                    });
                    holder.PrintIMG.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Common.FellingRegUniqueID = FellingRegLstModel.getFellingRegistrationUniqueID();
                                Common.FellingRegID = FellingRegLstModel.getFellingRegID();
                                Common.FromLocationID = FellingRegLstModel.getLocationID();
                                Common.FromLocationname = mDBExternalHelper.getFromLocationName(Common.FromLocationID);
                                Common.FellingSectionId = FellingRegLstModel.getFellingSectionID();
                                Common.FellingRegDate = FellingRegLstModel.getFellingRegistrationDate();
                                Common.FellingRegNo = FellingRegLstModel.getFellingRegistrationNumber();
                                Common.VolumeSum = FellingRegLstModel.getVolume();
                                Common.FellingRegisterInputList.clear();
                                Common.FellingRegisterLogsDetails = mDBInternalHelper.getFellingformDetailsID(Common.FellingRegID, Common.FellingSectionId);

                                Common.PlotNo = mDBInternalHelper.GetPlotNoUsingFellingSecID(Common.FromLocationID, Common.FellingSectionId, Common.FellingRegUniqueID);
                                Common.FellingSectionNumber = mDBExternalHelper.GetFellingSectionNumber(Common.FromLocationID, Common.FellingSectionId);

                                if (Common.FellingRegisterLogsDetails.size() > 0) {
                                    PrintFellingSlipCheck();
                                } else {
                                    AlertDialogBox("Felling PrintSlip", "No values found, try some other item", false);
                                }

                            } catch (Exception ex) {
                                AlertDialogBox("Felling PrintSlip", ex.toString(), false);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("Exception", ">>>>>>>>>>>" + e.toString());
            }
        }

        public class FellingRegViewHolder extends RecyclerView.ViewHolder {
            TextView FellingRegIDTxT, FellingRegNoTxT, RegisterDateTxT, EndTimeTXT, CountTXT, SyncStatusTXT, SyncTimeTXT, Volume;
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
                Volume = view.findViewById(R.id.fellingReg_TotVolumeTxT);


            }
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        FellingRegisterListFilter = FellingRegisterList;
                    } else {
                        ArrayList<FellingRegisterListModel> filteredList = new ArrayList<>();
                        for (FellingRegisterListModel row : FellingRegisterList) {
                            if (row.getFellingSectionID().toLowerCase().equals(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        FellingRegisterListFilter = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = FellingRegisterListFilter;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    FellingRegisterListFilter = (ArrayList<FellingRegisterListModel>) results.values;
                    FellingRegisterFilteredList.clear();
                    FellingRegisterFilteredList.addAll(FellingRegisterListFilter);
                    fellingRegadapter.notifyDataSetChanged();
                    TotalFilteredCount.setText(String.valueOf(FellingRegisterListFilter.size()));
                    TotalFilteredVolume.setText(String.valueOf(mDBInternalHelper.TotalFilteredVolume(constraint.toString())));
                    /*if (FellingRegisterListFilter.size() > 0) {
                        NoValueFoundTxT.setVisibility(View.GONE);
                    } else {
                        NoValueFoundTxT.setVisibility(View.VISIBLE);
                    }*/
                }
            };
        }
    }

    public void HideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onFellingFilterSelected(FellingSectionModel contact) {
        Toast.makeText(getApplicationContext(), "Selected: " + contact.getFellingSectionNumber() + ", " + contact.getFellingSectionId(), Toast.LENGTH_LONG).show();
    }

    View.OnClickListener mFellingRegisterCreateScanListen = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Common.SyncTime = "";
                Common.SyncStatus = 0;
                Common.FellingRegDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                boolean ListIdFlag = mDBInternalHelper.insertFellingRegisterList(Common.FellingRegNo, Common.FellingRegDate, Common.FellingSectionId, Common.FromLocationID, Common.EndDate,
                        0, Common.FellingRegUniqueID, Common.SyncStatus, Common.SyncTime, 1, Common.UserID, Common.IMEI);
                if (ListIdFlag == true) {
                    //Common.FellingRegisterList = mDBInternalHelper.getFellingRegisterList();
                    Common.ConcessionList.clear();
                    Common.ConcessionList = mDBExternalHelper.getConcessionList();
                    for (ConcessionNamesModel locationMod : Common.ConcessionList) {
                        if (Common.FromLocationID == locationMod.getFromLocationId()) {
                            Common.FromLocationname = locationMod.getConcessionName();
                        }
                    }
                    //if (Common.FellingRegisterList.size() > 0) {
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
                    boolean FellingIDFlag = mDBInternalHelper.UpdateFellingUniqueID(Common.FellingRegID, Common.FellingRegUniqueID);
                    Common.IsFellingRegEditListFlag = true;
                    Common.IsEditorViewFlag = true;
                    InventorFellingRegAcivityCall();
                    //}
                } else {
                    AlertDialogBox("IF-AddFellingRegistrationList", "Values are not inserted", false);
                }
            } catch (Exception ex) {
                AlertDialogBox("IF-AddFellingRegistrationList", ex.toString(), false);
            }

        }
    };

    View.OnClickListener mFellSecExportTOExcel = new View.OnClickListener() {
        public void onClick(View v) {
            boolean SyncStatusFlag = mDBInternalHelper.getSyncStatusForExportExcel(FellingSectionSearchATV.getText().toString());
            if (SyncStatusFlag == false) {
                ExportFellingListTable();
            } else {
                AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "please sync all datas", false);
            }
        }
    };

    View.OnClickListener mSendGmailWithExcel = new View.OnClickListener() {
        public void onClick(View v) {
            GmailSend_layout.setVisibility(View.VISIBLE);
        }
    };

    View.OnClickListener mAttacments = new View.OnClickListener() {
        public void onClick(View v) {
            openGallery();
        }
    };

    View.OnClickListener mClose = new View.OnClickListener() {
        public void onClick(View v) {
            GmailSend_layout.setVisibility(View.GONE);
        }
    };

    View.OnClickListener mSendMail = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                email = editTextEmail.getText().toString();
                subject = "Felling Registration Details from - " + editTextSubject.getText().toString();
                message = "-- PFA --";
                BCC = "apps.ysecit1@gmail.com";

                if (isValid(email)) {
                    if (subject.length() > 0) {
                        try {
                            // subject = et_subject.getText().toString();
                            // message = et_message.getText().toString();
                            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                            emailIntent.setType("plain/text");
                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
                            emailIntent.putExtra(Intent.EXTRA_BCC, new String[]{BCC});
                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                            if (URI != null) {
                                emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
                            }
                            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                            startActivity(Intent.createChooser(emailIntent, "Sending email..."));
                        } catch (Throwable t) {
                            //  Toast.makeText(this, "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
                        }
                        GmailSend_layout.setVisibility(View.GONE);
                    } else {
                        AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "please enter subject", false);
                    }
                } else {
                    AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "please enter email", false);
                }
            } catch (Throwable t) {
                Toast.makeText(FellingRegistationListActivitiy.this, "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };

    public void FellingRegistrayionSync(int TreeFromLocation, String FellingSecID, int FellingRegID, String FellingRegUniqueID) {
        Common.FellingRegisterInputList.clear();
        Common.FellingRegisterInputList = mDBInternalHelper.getFellingRegInputWithFellingUniqID(FellingRegID);
        Common.FellingTreeDetailsList = mDBInternalHelper.getFellingRegInputWithTreeDetails(TreeFromLocation, FellingSecID, FellingRegUniqueID);
        if (Common.FellingRegisterInputList.size() > 0) {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                new GetFellingRegSyncAsynkTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else {
            AlertDialogBox("InventoryTransfer Sync", "Values are empty", false);
        }
    }

    public void FellingRegSyncALLDialog(String ErrorMessage) {
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
                        FellingRegistrayionSyncALL();
                        dialog.cancel();
                    }
                });

        SignoutAlert = Signoutbuilder.create();
        SignoutAlert.show();

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
            AlertDialogBox("DeleteFellingListannFellingScannedList", ex.toString(), false);
        }
    }

    public void FellingDeleteDatasIFUserID_ONE(String ErrorMessage) {
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
                        DeleteFellingListannFellingScannedList(Common.FellingRegID);
                        dialog.cancel();
                    }
                });
        SignoutAlert = Signoutbuilder.create();
        SignoutAlert.show();
    }

    public void InventorFellingRegAcivityCall() {
        Intent _gwIntent = new Intent(this, FellingRegistrationActivity.class);
        startActivity(_gwIntent);
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

    class GetFellingRegSyncAsynkTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.SyncStatusList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e("FellingRegistrayion", "FellingRegisterInside---" + Common.FellingRegID);
            String MethodName = "FellingRegistration/";
            String SyncURL = ServiceURL.getServiceURL(ServiceURL.ControllorName, MethodName);
            fellingSyncModel = new FellingRegistrationSyncModel();
            fellingSyncModel.setFellingRegID(Common.FellingRegID);
            fellingSyncModel.setFellingRegistrationNumber(Common.FellingRegNo);
            fellingSyncModel.setLocationID(Common.TreeFromLocation);
            fellingSyncModel.setTotalCount(Common.SyncBarCodeCount);
            fellingSyncModel.setIMEI(Common.IMEI);
            fellingSyncModel.setFellingRegisterUniqueID(Common.FellingRegUniqueID);
            fellingSyncModel.setFellingSectionID(Common.FellingSectionId);
            fellingSyncModel.setFellingRegistrationDate(Common.SyncStartDateTime);
            fellingSyncModel.setEndDateTime(Common.SyncEndDateTime);
            fellingSyncModel.setUserID(Common.UserID);
            //fellingSyncModel.setVolume(Common.VolumeSum);
            fellingSyncModel.setFellingReg(Common.FellingRegisterInputList);
            fellingSyncModel.setFellingTreeDetails(Common.FellingTreeDetailsList);
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Common.IsConnected == true) {
                        if (Common.SyncStatusList.get(0).getStatus() == 1) {
                            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                            Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                            boolean ListIdFlag = mDBInternalHelper.UpdateFellingRegSyncStatusFellingRegID(Common.SyncTime, 1, Common.FellingRegID);
                            if (Common.FellingRegSyncALL == false) {
                                if (ListIdFlag == true) {
                                    //Scanned Result Refresh
                                    GetFellingRegistrationList();
                                    AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "#" + String.valueOf(Common.FellingRegID) + "--" + Common.SyncStatusList.get(0).getMessage(), true);
                                }
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "#" + String.valueOf(Common.FellingRegID) + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "#" + String.valueOf(Common.FellingRegID) + "--" + "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }

    }

    public void FellingRegistrayionSyncALL() {
        try {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                Common.FellingRegSyncALlIndex = 0;
                new GetFellingRegSyncAsynkAllTask().execute();
            }
        } catch (Exception ex) {
            AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), ex.toString(), false);
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
                tsc.sendcommand(printSlip.FellingRegisterDimensions());
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

    class GetFellingRegSyncAsynkAllTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.SyncStatusList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Common.SyncStartDateTime = FellingRegisterFilteredList.get(Common.FellingRegSyncALlIndex).getFellingRegistrationDate();
                Common.SyncEndDateTime = FellingRegisterFilteredList.get(Common.FellingRegSyncALlIndex).getEndDateTime();
                Common.FellingRegID = FellingRegisterFilteredList.get(Common.FellingRegSyncALlIndex).getFellingRegID();
                Common.FellingRegNo = FellingRegisterFilteredList.get(Common.FellingRegSyncALlIndex).getFellingRegistrationNumber();
                Common.SyncBarCodeCount = FellingRegisterFilteredList.get(Common.FellingRegSyncALlIndex).getCount();
                Common.FellingSectionId = FellingRegisterFilteredList.get(Common.FellingRegSyncALlIndex).getFellingSectionID();
                Common.FellingRegUniqueID = FellingRegisterFilteredList.get(Common.FellingRegSyncALlIndex).getFellingRegistrationUniqueID();
                Common.SyncEndDateTime = FellingRegisterFilteredList.get(Common.FellingRegSyncALlIndex).getEndDateTime();
                Common.TreeFromLocation = FellingRegisterFilteredList.get(Common.FellingRegSyncALlIndex).getLocationID();
                Common.VolumeSum = FellingRegisterFilteredList.get(Common.FellingRegSyncALlIndex).getVolume();

                Common.FellingRegisterInputList.clear();
                Common.FellingRegisterInputList = mDBInternalHelper.getFellingRegInputWithFellingUniqID(Common.FellingRegID);
                Common.FellingTreeDetailsList = mDBInternalHelper.getFellingRegInputWithTreeDetails(
                        Common.TreeFromLocation, Common.FellingSectionId, Common.FellingRegUniqueID);
            /*if (Common.FellingRegisterInputList.size() > 0) {
                GetFellingRegSyncAsynkTask FRAsynTask = new GetFellingRegSyncAsynkTask();
                FRAsynTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            } else {
                AlertDialogBox("FellingRegistrayion SyncAll", "Values are empty", false);
            }*/

                String MethodName = "FellingRegistration/";
                String SyncURL = ServiceURL.getServiceURL(ServiceURL.ControllorName, MethodName);
                fellingSyncModel = new FellingRegistrationSyncModel();
                fellingSyncModel.setFellingRegID(Common.FellingRegID);
                fellingSyncModel.setFellingRegistrationNumber(Common.FellingRegNo);
                fellingSyncModel.setLocationID(Common.TreeFromLocation);
                fellingSyncModel.setTotalCount(Common.SyncBarCodeCount);
                fellingSyncModel.setIMEI(Common.IMEI);
                fellingSyncModel.setFellingRegisterUniqueID(Common.FellingRegUniqueID);
                fellingSyncModel.setFellingSectionID(Common.FellingSectionId);
                fellingSyncModel.setFellingRegistrationDate(Common.SyncStartDateTime);
                fellingSyncModel.setEndDateTime(Common.SyncEndDateTime);
                fellingSyncModel.setUserID(Common.UserID);
                //fellingSyncModel.setVolume(Common.VolumeSum);
                fellingSyncModel.setFellingReg(Common.FellingRegisterInputList);
                fellingSyncModel.setFellingTreeDetails(Common.FellingTreeDetailsList);

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Common.IsConnected == true) {
                        if (Common.SyncStatusList.get(0).getStatus() == 1) {
                            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                            Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                            boolean ListIdFlag = mDBInternalHelper.UpdateFellingRegSyncStatusFellingRegID(Common.SyncTime, 1, Common.FellingRegID);
                            if (Common.FellingRegSyncALlIndex == (FellingRegisterFilteredList.size() - 1)) {
                                FellingSectionSearchATV.setText("");
                                GetFellingRegistrationList();
                                AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), Common.SyncStatusList.get(0).getMessage(), true);
                            } else {
                                Common.FellingRegSyncALlIndex++;
                                new GetFellingRegSyncAsynkAllTask().execute();
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "#" + String.valueOf(Common.FellingRegID) + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "#" + String.valueOf(Common.FellingRegID) + "--" + "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }
    }

    public void PrintoutEnd() {
        tsc.closeport();
        TSCConnInt = -1;
        Common.IsPrintBtnClickFlag = true;
    }

    private void ExportFellingListTable() {
        try {
            String FelingSectionID = "00";
            File file = new File(directory_path);
            if (!file.exists()) {
                Log.v("File Created", String.valueOf(file.mkdirs()));
            }
            if (FellingSectionSearchATV.getText().toString().length() > 0) {
                FelingSectionID = FellingSectionSearchATV.getText().toString();
            }
            String dest = directory_path + "FellingRegistration_" + FelingSectionID + "_" + Common.DeviceName + ".xls";
            try {
                for (int i = 0; i < Common.FellingRegExcelExportList.length; i++) {
                    if (i == 0) {
                        int index = myWorkBook.getSheetIndex(Common.FellingRegExcelExportList[i]);
                        if (index == -1) {
                            mySheet = myWorkBook.createSheet(Common.FellingRegExcelExportList[i]);
                        } else {
                            mySheet = myWorkBook.getSheetAt(index);
                            for (int Del_Row = 1; Del_Row <= mySheet.getLastRowNum(); Del_Row++) {
                                Row row = mySheet.getRow(Del_Row);
                                deleteRow(mySheet, row);
                            }
                        }
                        for (int Row = 0; Row < FellingRegisterFilteredList.size() + 1; Row++) {
                            if (Row == 0) {
                                excelLog(Row, 0, "FellingRegID");
                                excelLog(Row, 1, "FellingRegistrationNumber");
                                excelLog(Row, 2, "FellingRegistrationDate");
                                excelLog(Row, 3, "LocationID");
                                excelLog(Row, 4, "FellingSectionId");
                                excelLog(Row, 5, "EndDateTime");
                                excelLog(Row, 6, "TotalCount");
                                excelLog(Row, 7, "FellingRegistrationUniqueID");
                                excelLog(Row, 8, "SyncStatus");
                                excelLog(Row, 9, "SyncTime");
                                excelLog(Row, 10, "IsActive");
                                excelLog(Row, 11, "Volume");
                                excelLog(Row, 12, "UserID");
                                excelLog(Row, 13, "IMEI");
                            } else {
                                excelLog(Row, 0, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getFellingRegID()));
                                excelLog(Row, 1, FellingRegisterFilteredList.get(Row - 1).getFellingRegistrationNumber());
                                excelLog(Row, 2, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getFellingRegistrationDate()));
                                excelLog(Row, 3, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getLocationID()));
                                excelLog(Row, 4, FellingRegisterFilteredList.get(Row - 1).getFellingSectionID());
                                excelLog(Row, 5, FellingRegisterFilteredList.get(Row - 1).getEndDateTime());
                                excelLog(Row, 6, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getCount()));
                                excelLog(Row, 7, FellingRegisterFilteredList.get(Row - 1).getFellingRegistrationUniqueID());
                                excelLog(Row, 8, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getSyncStatus()));
                                excelLog(Row, 9, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getSyncTime()));
                                excelLog(Row, 10, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getIsActive()));
                                excelLog(Row, 11, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getVolume()));
                                excelLog(Row, 12, String.valueOf(FellingRegisterFilteredList.get(Row - 1).getUserID()));
                                excelLog(Row, 13, FellingRegisterFilteredList.get(Row - 1).getIMEI());
                            }
                        }
                    } else if (i == 1) {
                        int index = myWorkBook.getSheetIndex(Common.FellingRegExcelExportList[i]);
                        if (index == -1) {
                            mySheet = myWorkBook.createSheet(Common.FellingRegExcelExportList[i]);
                        } else {
                            mySheet = myWorkBook.getSheetAt(index);
                            for (int Del_Row = 1; Del_Row <= mySheet.getLastRowNum(); Del_Row++) {
                                Row row = mySheet.getRow(Del_Row);
                                deleteRow(mySheet, row);
                            }
                        }
                        Common.FellingRegisterLogsExportDetails.clear();
                        StringBuilder AdvanceSearchStr = new StringBuilder();
                        for (int Row = 0; Row < FellingRegisterFilteredList.size(); Row++) {
                            try {
                                AdvanceSearchStr.append(FellingRegisterFilteredList.get(Row).getFellingRegID() + ",");
                            } catch (Exception ex) {
                                Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
                            }
                        }
                        Common.FellingRegisterLogsExportDetails = mDBInternalHelper.getFellingDetailsExportList(AdvanceSearchStr.toString().substring(0, AdvanceSearchStr.toString().length() - 1));
                        for (int Row = 0; Row < Common.FellingRegisterLogsExportDetails.size() + 1; Row++) {
                            if (Row == 0) {
                                excelLog(Row, 0, "FellingRegID");
                                excelLog(Row, 1, "LocationID");
                                excelLog(Row, 2, "FellingSectionId");
                                excelLog(Row, 3, "SbbLabel");
                                excelLog(Row, 4, "WoodSpieceID");
                                excelLog(Row, 5, "WoodSpieceCode");
                                excelLog(Row, 6, "TreeNumber");
                                excelLog(Row, 7, "Quality");
                                excelLog(Row, 8, "BarCode");
                                excelLog(Row, 9, "FellingRegistrationUniqueID");
                                excelLog(Row, 10, "UserID");
                                excelLog(Row, 11, "EntryMode");
                                excelLog(Row, 12, "IsNewTreeNumber");
                                excelLog(Row, 13, "IsWoodSpieceCode");
                                excelLog(Row, 14, "IsOldWoodSpieceCode");
                                excelLog(Row, 15, "Footer_1");
                                excelLog(Row, 16, "Footer_2");
                                excelLog(Row, 17, "Top_1");
                                excelLog(Row, 18, "Top_2");
                                excelLog(Row, 19, "Length");
                                excelLog(Row, 20, "NoteF");
                                excelLog(Row, 21, "NoteT");
                                excelLog(Row, 22, "NoteL");
                                excelLog(Row, 23, "Volume");
                                excelLog(Row, 24, "TreePartType");
                                excelLog(Row, 25, "IsActive");
                            } else {
                                excelLog(Row, 0, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getFellingRegID()));
                                excelLog(Row, 1, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getLocationID()));
                                excelLog(Row, 2, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getFellingSectionID()));
                                excelLog(Row, 3, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getSbbLabel()));
                                excelLog(Row, 4, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getWoodSpieceID()));
                                excelLog(Row, 5, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getWoodSpieceCode()));
                                excelLog(Row, 6, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getTreeNumber()));
                                excelLog(Row, 7, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getQuality()));
                                excelLog(Row, 8, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getBarCode()));
                                excelLog(Row, 9, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getFellingRegistrationUniqueID()));
                                excelLog(Row, 10, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getUserID()));
                                excelLog(Row, 11, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getEntryMode()));
                                excelLog(Row, 12, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getISNewTree()));
                                excelLog(Row, 13, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getIsWoodSpieceCode()));
                                excelLog(Row, 14, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getIsOldWoodSpieceCode()));
                                excelLog(Row, 15, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getFooter_1()));
                                excelLog(Row, 16, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getFooter_2()));
                                excelLog(Row, 17, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getTop_1()));
                                excelLog(Row, 18, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getTop_2()));
                                excelLog(Row, 19, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getLength()));
                                excelLog(Row, 20, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getNoteF()));
                                excelLog(Row, 21, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getNoteL()));
                                excelLog(Row, 22, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getNoteL()));
                                excelLog(Row, 23, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getVolume()));
                                excelLog(Row, 24, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getTreePartType()));
                                excelLog(Row, 25, String.valueOf(Common.FellingRegisterLogsExportDetails.get(Row - 1).getIsActive()));
                            }
                        }
                    } else if (i == 2) {
                        int index = myWorkBook.getSheetIndex(Common.FellingRegExcelExportList[i]);
                        if (index == -1) {
                            mySheet = myWorkBook.createSheet(Common.FellingRegExcelExportList[i]);
                        } else {
                            mySheet = myWorkBook.getSheetAt(index);
                            for (int Del_Row = 1; Del_Row <= mySheet.getLastRowNum(); Del_Row++) {
                                Row row = mySheet.getRow(Del_Row);
                                deleteRow(mySheet, row);
                            }
                        }
                        Common.FellingExportTreeDetailsList.clear();
                        Common.FellingExportTreeDetailsList = mDBInternalHelper.getExportTreeNumber();
                        for (int Row = 0; Row < Common.FellingExportTreeDetailsList.size() + 1; Row++) {
                            if (Row == 0) {
                                excelLog(Row, 0, "FellingRegID");
                                excelLog(Row, 1, "LocationID");
                                excelLog(Row, 2, "FellingRegistrationUniqueID");
                                excelLog(Row, 3, "FellingSectionId");
                                excelLog(Row, 4, "TreeNumber");
                                excelLog(Row, 5, "Footer_1");
                                excelLog(Row, 6, "Footer_2");
                                excelLog(Row, 7, "Top_1");
                                excelLog(Row, 8, "Top_2");
                                excelLog(Row, 9, "Length");
                                excelLog(Row, 10, "WoodSpieceCode");
                                excelLog(Row, 11, "OldWoodSpieceCode");
                                excelLog(Row, 12, "IsNewTreeNumber");
                                excelLog(Row, 13, "PlotNo");
                                excelLog(Row, 14, "OldPlotNo");
                                excelLog(Row, 15, "PlotId");
                                excelLog(Row, 16, "WoodSpieceID");
                                excelLog(Row, 17, "IsNewPlotNumber");
                                excelLog(Row, 18, "IsWoodSpieceCode");
                            } else {
                                excelLog(Row, 0, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getFellingRegID()));
                                excelLog(Row, 1, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getLocationID()));
                                excelLog(Row, 2, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getFellingRegUniqueID()));
                                excelLog(Row, 3, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getFellingSectionId()));
                                excelLog(Row, 4, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getTreeNumber()));
                                excelLog(Row, 5, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getFooter_1()));
                                excelLog(Row, 6, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getFooter_2()));
                                excelLog(Row, 7, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getTop_1()));
                                excelLog(Row, 8, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getTop_2()));
                                excelLog(Row, 9, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getLength()));
                                excelLog(Row, 10, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getWoodSpieceCode()));
                                excelLog(Row, 11, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getOldWoodSpieceCode()));
                                excelLog(Row, 12, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getIsNewTreeNumber()));
                                excelLog(Row, 13, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getPlotNumber()));
                                excelLog(Row, 14, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getOldPlotNo()));
                                excelLog(Row, 15, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getPlotId()));
                                excelLog(Row, 16, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getWoodSpieceID()));
                                excelLog(Row, 17, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getIsNewPlotNumber()));
                                excelLog(Row, 18, String.valueOf(Common.FellingExportTreeDetailsList.get(Row - 1).getIsWoodSpieceCode()));
                            }
                        }
                    }
                }
                FileOutputStream out = new FileOutputStream(dest);
                myWorkBook.write(out);
                out.close();
                Toast.makeText(this, "Saved Sucessfully - " + directory_path, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                AlertDialogBox("Export DB", e.toString(), false);
            }
        } catch (Exception ex) {
            AlertDialogBox("Export DB", ex.toString(), false);
        }
    }

    public static void deleteRow(HSSFSheet sheet, Row row) {
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum != 0 && lastRowNum > 0) {
            int rowIndex = row.getRowNum();
            Row removingRow = sheet.getRow(rowIndex);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
                System.out.println("Deleting.... ");
            }
        }
    }

    private static void excelLog(int row, int col, String value) {
        HSSFRow myRow = mySheet.getRow(row);
        if (myRow == null)
            myRow = mySheet.createRow(row);
        HSSFCell myCell = myRow.createCell(col);
        myCell.setCellValue(value);
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_DEVICE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_DEVICE && resultCode == RESULT_OK) {
            Uri selectedFile = data.getData();
            assert selectedFile != null;
            if (selectedFile.toString().contains(".xls") || selectedFile.toString().contains(".xlsx")) {
                Uri uri = data.getData();
                File myFile = new File(uri.getPath());
                URI = Uri.parse("file://" + myFile);
                Log.e("Attachment Path:", myFile.toString());
                Subject_path = URI.toString();
                editTextSubject.setText(Subject_path);
            } else {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedFile, filePathColumn, null, null, null);
                cursor.moveToFirst();
                columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                attachmentFile = cursor.getString(columnIndex);
                Log.e("Attachment Path:", attachmentFile);
                URI = Uri.parse("file://" + attachmentFile);
                Subject_path = URI.toString();
                editTextSubject.setText(Subject_path);
                cursor.close();
            }
        }
    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    /*private void ShareViaEmail(String folder_name, String file_name) {
        try {
            File root = Environment.getExternalStorageDirectory();
            String filelocation = root.getAbsolutePath() + folder_name + "/" + file_name;
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            String message = "File to be shared is " + file_name + ".";
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filelocation));
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.setData(Uri.parse("mailto:xyz@gmail.com"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        } catch (Exception e) {
            System.out.println("is exception raises during sending mail" + e);
        }
    }*/

}
