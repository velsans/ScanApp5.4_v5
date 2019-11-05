package com.zebra.main.activity.Export;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.gson.Gson;
import com.zebra.R;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.activity.Common.GwwMainActivity;
import com.zebra.main.model.Export.ExportContainerDetailsModel;
import com.zebra.main.model.Export.ExportDetailsInputSavedListModel;
import com.zebra.main.model.Export.ExportDetailsModel;
import com.zebra.main.model.Export.ExportModel;
import com.zebra.main.model.SyncStatusModel;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;
import com.zebra.utilities.Communicator;
import com.zebra.utilities.ConnectionFinder;
import com.zebra.utilities.GwwException;
import com.zebra.utilities.ServiceURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class ExportListActivity extends Activity {
    private InternalDataBaseHelperClass mDBInternalHelper;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    /*Inventory Export*/
    //ExportAdapter exportadapter;
    ExpendableExportAdapter exportContaineradapter;
    LinearLayoutManager horizontalLayoutManager, InveDateLayoutManager;
    RecyclerView ExportList, ExportListDate;
    TextView TotalFilteredExport, TotalFilteredVolume, NoValueFoundTxT;
    LinearLayout ProgressBarLay;
    AlertDialogManager alert = new AlertDialogManager();
    boolean isInternetPresent;
    ExportDateAdapter exportDateadapter;
    AlertDialog.Builder Signoutbuilder = null;
    AlertDialog SignoutAlert = null;
    EditText submitOrderNoValue, submitContainerValue;
    Button submitOrderNo;
    ExportContainerDetailsModel expContainerDetailsModel;
    ExportDetailsInputSavedListModel exportSyncModel;
    SyncStatusModel suncStaModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exportlist);
        mDBExternalHelper = new ExternalDataBaseHelperClass(this);
        mDBInternalHelper = new InternalDataBaseHelperClass(this);
        Initialization();
        GetExportDateList();
        OnclickeListener();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void OnclickeListener() {
        ExportList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
    }

    public void onBackPressed() {
        Intent oneIntent = new Intent(this, GwwMainActivity.class);
        startActivity(oneIntent);
    }

    public void Initialization() {
        ProgressBarLay = findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        ExportList = findViewById(R.id.exportloadplanListView);
        NoValueFoundTxT = findViewById(R.id.NovalueFound);
        TotalFilteredExport = findViewById(R.id.TotalFilteredCount);
        TotalFilteredVolume = findViewById(R.id.TotalFilteredVolume);
        ExportListDate = findViewById(R.id.exportloadplanListViewDate);
        submitOrderNoValue = findViewById(R.id.exportOrderEditTXT);
        submitContainerValue = findViewById(R.id.exportContainerEditTXT);
        submitOrderNo = findViewById(R.id.exportLoadPlancreateListTxT);
        findViewById(R.id.exportLoadPlancreateListTxT).setOnClickListener(mSubmitCreateExport);
    }

    private void GetExportDateList() {
        try {
            Common.Filter_ExportDate.clear();
            Common.Filter_ExportDate = mDBInternalHelper.getExportDate();
            /*Remove Duplications*/
            HashSet<String> hashSet = new HashSet<String>();
            hashSet.addAll(Common.Filter_ExportDate);
            Common.Filter_ExportDate.clear();
            Common.Filter_ExportDate.addAll(hashSet);
            if (Common.Filter_ExportDate.size() > 0) {
                exportDateadapter = new ExportDateAdapter(Common.Filter_ExportDate, this);
                InveDateLayoutManager = new LinearLayoutManager(this);
                exportDateadapter.notifyDataSetChanged();
                ExportListDate.setLayoutManager(InveDateLayoutManager);
                ExportListDate.setAdapter(exportDateadapter);
                ExportListDate.setVisibility(View.VISIBLE);
                NoValueFoundTxT.setVisibility(View.GONE);
                GetExportList(Common.Filter_ExportDate.get(Common.ExportDateSelectedIndex));
            } else {
                ExportList.setVisibility(View.GONE);
                ExportListDate.setVisibility(View.GONE);
                NoValueFoundTxT.setVisibility(View.VISIBLE);
                TotalFilteredExport.setText("0");
                TotalFilteredVolume.setText("0.00");
            }
        } catch (Exception ex) {
            Log.d("ExportDate", ">>>>>>" + ex.toString());
        }
    }

    View.OnClickListener mSubmitCreateExport = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Common.VolumeSum = 0.0;
                Common.Count = 0;
                Common.Order_Number = submitOrderNoValue.getText().toString();
                Common.ContainerNo = submitContainerValue.getText().toString();
                Common.StartDate = Common.dateFormat.format(Calendar.getInstance().getTime());
                if (Common.ContainerNo.length() > 0) {
                    boolean DuplicateFlag = mDBInternalHelper.getExportOrderNoDuplicateCheck(Common.ContainerNo);
                    if (DuplicateFlag == true) {
                        AlertDialogBox("Export List", "Container Number Already Exsist", false);
                        return;
                    }
                }
                boolean ListIdFlag = mDBInternalHelper.insertExportIDList(Common.Order_Number, Common.IMEI, Common.LocationID, Common.StartDate, Common.EndDate,
                        Common.UserID, Common.Count, String.valueOf(Common.VolumeSum), 1, Common.ExportUniqueID);

                if (ListIdFlag == true) {
                    Common.ExportID = Integer.parseInt(mDBInternalHelper.getLastExportID());
                    String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                    String DeviceID = "";
                    if (String.valueOf(Common.LDeviceID).length() == 1) {
                        DeviceID = "0" + String.valueOf(Common.LDeviceID);
                    } else {
                        DeviceID = String.valueOf(Common.LDeviceID);
                    }
                    Common.ExportUniqueID = String.valueOf(DateUniqueFormat + DeviceID + Common.ExportID);
                    Log.d("ExportLoadID", ">>>>>>" + Common.ExportID + ">>" + Common.ExportUniqueID);
                    // Update values into ExportID
                    boolean TransferIDFlag = mDBInternalHelper.UpdateExportUniqueID(Common.ExportID, Common.ExportUniqueID);
                    Common.IsExportEditListFlag = true;
                    Common.IsEditorViewFlag = true;
                    ExportAcivityCall();
                } else {
                    AlertDialogBox("IE-AddCreateExportList", "Values are not inserted", false);
                }
            } catch (Exception ex) {
                AlertDialogBox("IE-AddCreateExportList", ex.toString(), false);
            }
        }
    };

    private void GetExportList(String SelectedDate) {
        try {
            Common.ExportList.clear();
            Common.ExportList = mDBInternalHelper.getExportList(SelectedDate);
            Common.ExportHeaderList.clear();
            if (Common.ExportList.size() > 0) {
                for (int i = 0; i < Common.ExportList.size(); i++) {
                    /*Expendable List Values*/
                    expContainerDetailsModel = new ExportContainerDetailsModel();
                    expContainerDetailsModel.setExportID(Common.ExportList.get(i).getExportID());
                    expContainerDetailsModel.setExportUniqueID(Common.ExportList.get(i).getExportUniqueID());
                    //expContainerDetailsModel.setContainerNo(Common.ExportList.get(i).getContainerNo());
                    expContainerDetailsModel.setOrderNo(Common.ExportList.get(i).getOrderNo());
                    expContainerDetailsModel.setIMEI(Common.ExportList.get(i).getIMEI());
                    expContainerDetailsModel.setLocationID(Common.ExportList.get(i).getLocationID());
                    expContainerDetailsModel.setStartDateTime(Common.ExportList.get(i).getStartDateTime());
                    expContainerDetailsModel.setEndDateTime(Common.ExportList.get(i).getEndDateTime());
                    expContainerDetailsModel.setUserID(Common.ExportList.get(i).getUserID());
                    expContainerDetailsModel.setTotalCount(Common.ExportList.get(i).getTotalCount());
                    expContainerDetailsModel.setVolume(Common.ExportList.get(i).getVolume());
                    expContainerDetailsModel.setIsActive(Common.ExportList.get(i).getIsActive());
                    expContainerDetailsModel.setSyncStatus(Common.ExportList.get(i).getSyncStatus());
                    expContainerDetailsModel.setSyncDate(Common.ExportList.get(i).getSyncDate());

                    expContainerDetailsModel.setContainterDetailsItems(mDBInternalHelper.getExportDetailsList(Common.ExportList.get(i).getExportID(), false));
                    Common.ExportHeaderList.add(expContainerDetailsModel);
                }
                exportContaineradapter = new ExpendableExportAdapter(Common.ExportHeaderList, this);
                //exportadapter = new ExportAdapter(Common.ExportList, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                exportContaineradapter.notifyDataSetChanged();
                ExportList.setLayoutManager(horizontalLayoutManager);
                ExportList.setAdapter(exportContaineradapter);
                NoValueFoundTxT.setVisibility(View.GONE);
                ExportList.setVisibility(View.VISIBLE);
            } else {
                NoValueFoundTxT.setVisibility(View.VISIBLE);
                ExportList.setVisibility(View.GONE);
            }
            TotalFilteredExport.setText(String.valueOf(Common.ExportHeaderList.size()));
            TotalFilteredVolume.setText(String.valueOf(mDBInternalHelper.TotalVolumeForExport(SelectedDate)));
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
    }

    public class ExportDateAdapter extends RecyclerView.Adapter<ExportDateAdapter.TransferViewHolder> {
        private List<String> ExportDateList;
        Context context;

        public ExportDateAdapter(List<String> ExportDateList, Context context) {
            this.ExportDateList = ExportDateList;
            this.context = context;
        }

        @Override
        public ExportDateAdapter.TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_count_dates_infliator, parent, false);
            ExportDateAdapter.TransferViewHolder gvh = new ExportDateAdapter.TransferViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public int getItemCount() {
            return ExportDateList.size();
        }

        @Override
        public void onBindViewHolder(ExportDateAdapter.TransferViewHolder holder, final int position) {
            //holder.TransferIDTXT.setText(String.valueOf(position + 1));
            if (Common.ExportDateSelectedIndex == position) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.orange));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.colorgGreenSmooth));
            }
            holder.TransferDateTXT.setText(String.valueOf(ExportDateList.get(position)));
            holder.Background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.ExportDateSelectedIndex = position;
                    notifyDataSetChanged();
                    GetExportList(String.valueOf(ExportDateList.get(position)));
                }
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

    public class ExportAdapter extends RecyclerView.Adapter<ExportAdapter.ExportViewHolder> {
        private List<ExportModel> ExportList;
        Context context;

        public ExportAdapter(List<ExportModel> ScannedResultList, Context context) {
            this.ExportList = ScannedResultList;
            this.context = context;
        }

        @Override
        public ExportAdapter.ExportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exportlist_infliator, parent, false);
            ExportAdapter.ExportViewHolder gvh = new ExportAdapter.ExportViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(ExportAdapter.ExportViewHolder holder, final int position) {
            //holder.ListIDTXT.setText(String.valueOf(position + 1));
            holder.ExportUniqueIDTXT.setText(String.valueOf(ExportList.get(position).getExportUniqueID()));
            holder.OrderNoTXT.setText(String.valueOf(ExportList.get(position).getOrderNo()));
            holder.StartTimeTXT.setText(ExportList.get(position).getStartDateTime());
            holder.EndTimeTXT.setText(ExportList.get(position).getEndDateTime());
            holder.CountTXT.setText(String.valueOf(ExportList.get(position).getTotalCount()));
            holder.VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(ExportList.get(position).getVolume())));

            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            holder.DeleteIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Common.ExportID = ExportList.get(position).getExportID();
                        //Remove From Export List and Scanned list
                        ExportDeleteDatasIFUserID_ONE("Are you sure you want delete all datas");
                    } catch (Exception ex) {
                        AlertDialogBox("Export DeleteIMG", ex.toString(), false);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return ExportList.size();
        }

        public class ExportViewHolder extends RecyclerView.ViewHolder {
            TextView ExportUniqueIDTXT, containerIDTXT, OrderNoTXT, StartTimeTXT, EndTimeTXT, CountTXT, VolumeTXT;
            ImageView DeleteIMG;
            LinearLayout Background;

            public ExportViewHolder(View view) {
                super(view);
                Background = view.findViewById(R.id.exportlayoutBackground);
                ExportUniqueIDTXT = view.findViewById(R.id.export_UniqueIDTxT);
                OrderNoTXT = view.findViewById(R.id.export_orderNoTxT);
                containerIDTXT = view.findViewById(R.id.exportContainerEditTXT);
                StartTimeTXT = view.findViewById(R.id.export_startTimeTxT);
                EndTimeTXT = view.findViewById(R.id.export_endTimeTxT);
                CountTXT = view.findViewById(R.id.export_TotalCountTxT);
                DeleteIMG = view.findViewById(R.id.export_delete);
                VolumeTXT = view.findViewById(R.id.export_VolumeTxT);
            }
        }
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str == "null";
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

    public void DeleteExportScannedList(int ExportID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteExportListID(ExportID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteExportScanned(ExportID);
                if (DeleteScannedFlag == true) {
                    //GetExportList();
                    GetExportDateList();
                }
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteExportScannedList", ex.toString(), false);
        }
    }

    public void ExportDeleteDatasIFUserID_ONE(String ErrorMessage) {
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
                        DeleteExportScannedList(Common.ExportID);
                        dialog.cancel();
                    }
                });

        SignoutAlert = Signoutbuilder.create();
        SignoutAlert.show();

    }

    public void ExportAcivityCall() {
        Intent _gwIntent = new Intent(this, ExportDetailsActivity.class);
        startActivity(_gwIntent);
    }

    public class ExpendableExportAdapter extends RecyclerView.Adapter<ExpendableExportAdapter.ViewHolder> {

        ArrayList<ExportContainerDetailsModel> HeadList = new ArrayList<ExportContainerDetailsModel>();
        ArrayList<Integer> counter = new ArrayList<Integer>();
        Context context;

        public ExpendableExportAdapter(ArrayList<ExportContainerDetailsModel> nameList, Context context) {
            this.HeadList = nameList;
            this.context = context;
            for (int i = 0; i < nameList.size(); i++) {
                counter.add(0);
            }
        }

        public ExportContainerDetailsModel getItem(int position) {
            return HeadList.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerView cardRecyclerView;
            CardView cardView;
            TextView ExportUniqueIDTXT, OrderNoTXT, StartTimeTXT, EndTimeTXT, CountTXT, VolumeTXT, SyncStatus, SyncDate;
            ImageView DeleteIMG, SaveIMG, SyncIMG;
            LinearLayout Background, exportInnerLayoutRecycler;

            public ViewHolder(View view) {
                super(view);
                cardRecyclerView = itemView.findViewById(R.id.innerRecyclerView);
                exportInnerLayoutRecycler = itemView.findViewById(R.id.exportInnerLayoutRecycler);
                cardView = itemView.findViewById(R.id.cardView);
                Background = view.findViewById(R.id.exportlayoutBackground);
                ExportUniqueIDTXT = view.findViewById(R.id.export_UniqueIDTxT);
                OrderNoTXT = view.findViewById(R.id.export_orderNoTxT);
                SyncIMG = view.findViewById(R.id.export_syncStatusIcon);
                StartTimeTXT = view.findViewById(R.id.export_startTimeTxT);
                EndTimeTXT = view.findViewById(R.id.export_endTimeTxT);
                CountTXT = view.findViewById(R.id.export_TotalCountTxT);
                DeleteIMG = view.findViewById(R.id.export_delete);
                VolumeTXT = view.findViewById(R.id.export_VolumeTxT);
                SaveIMG = view.findViewById(R.id.export_save);
                SyncStatus = view.findViewById(R.id.export_syncStatus);
                SyncDate = view.findViewById(R.id.export_syncDate);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.exportlist_infliator, parent, false);
            ExpendableExportAdapter.ViewHolder vh = new ExpendableExportAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            try {
                //holder.containerIDTXT.setText(String.valueOf(HeadList.get(position).getContainerNo()));
                holder.ExportUniqueIDTXT.setText(String.valueOf(HeadList.get(position).getExportUniqueID()));
                holder.OrderNoTXT.setText(String.valueOf(HeadList.get(position).getOrderNo()));
                holder.StartTimeTXT.setText(HeadList.get(position).getStartDateTime());
                holder.EndTimeTXT.setText(HeadList.get(position).getEndDateTime());
                holder.CountTXT.setText(String.valueOf(HeadList.get(position).getTotalCount()));
                holder.VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(HeadList.get(position).getVolume())));
                if (HeadList.get(position).getSyncStatus() == 1) {
                    //holder.SyncIMG.setBackgroundResource(R.mipmap.success);
                    holder.SyncStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    holder.SyncStatus.setText("Synced Already");
                } else {
                    //holder.SyncIMG.setBackgroundResource(R.mipmap.fail);
                    holder.SyncStatus.setTextColor(context.getResources().getColor(R.color.red));
                    holder.SyncStatus.setText("Not Synced");
                }
                holder.SyncDate.setText(String.valueOf(HeadList.get(position).getSyncDate()));
                /*if (position % 2 == 0) {
                    holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else {
                    holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
                }*/
                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
                int color = generator.getColor(getItem(position));
                holder.Background.setBackgroundColor(color);
                holder.SaveIMG.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Sync Date to BackEnd
                        try {
                            // if (Common.FellingRegSyncALL == false) {
                            Common.ExportID = HeadList.get(position).getExportID();
                            Common.Order_Number = HeadList.get(position).getOrderNo();
                            Common.IMEI = HeadList.get(position).getIMEI();
                            Common.SyncStartDateTime = HeadList.get(position).getStartDateTime();
                            Common.SyncEndDateTime = HeadList.get(position).getEndDateTime();
                            Common.LocationID = HeadList.get(position).getLocationID();
                            Common.Export_Count = HeadList.get(position).getTotalCount();
                            Common.ExportUniqueID = HeadList.get(position).getExportUniqueID();
                            ExportSaveDatas(Common.Order_Number, Common.ExportID);
                          /*  } else {
                                InventoryTransferSyncALLDialog("Are you sure want to sync all datas?");
                            }*/
                        } catch (Exception ex) {
                            AlertDialogBox("Transfer Sync", ex.toString(), false);
                        }
                    }
                });
                holder.DeleteIMG.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Common.ExportID = HeadList.get(position).getExportID();
                            //Remove From Export List and Scanned list
                            ExportDeleteDatasIFUserID_ONE("Are you sure you want delete all datas");
                        } catch (Exception ex) {
                            AlertDialogBox("Export DeleteIMG", ex.toString(), false);
                        }
                    }
                });

                holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        try {
                            Common.ExportID = HeadList.get(position).getExportID();
                            Common.Order_Number = HeadList.get(position).getOrderNo();
                            Common.ExportUniqueID = HeadList.get(position).getExportUniqueID();
                            Common.IsEditorViewFlag = false;
                            ExportAcivityCall();
                        } catch (Exception ex) {
                            AlertDialogBox("Transfer Background", ex.toString(), false);
                        }
                        return false;
                    }

                });

                InnerRecyclerViewAdapter itemInnerRecyclerView = new InnerRecyclerViewAdapter(HeadList.get(position).getContainterDetailsItems(), context);
                //holder.cardRecyclerView.setLayoutManager(new LinearLayoutManager(context,  LinearLayoutManager.VERTICAL, true));
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (counter.get(position) % 2 == 0) {
                            holder.cardRecyclerView.setVisibility(View.VISIBLE);
                            holder.exportInnerLayoutRecycler.setVisibility(View.VISIBLE);

                        } else {
                            holder.cardRecyclerView.setVisibility(View.GONE);
                            holder.exportInnerLayoutRecycler.setVisibility(View.GONE);
                        }
                        counter.set(position, counter.get(position) + 1);
                    }
                });
                horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                holder.cardRecyclerView.setLayoutManager(horizontalLayoutManager);
                holder.cardRecyclerView.setAdapter(itemInnerRecyclerView);
            } catch (Exception ex) {
                Log.e("Exception", ">>>>>>>>" + ex.toString());
            }
        }

        @Override
        public int getItemCount() {
            return HeadList.size();
        }

    }

    public class InnerRecyclerViewAdapter extends RecyclerView.Adapter<InnerRecyclerViewAdapter.ViewHolder> {
        public ArrayList<ExportDetailsModel> ChildList = new ArrayList<ExportDetailsModel>();
        Context context;

        public InnerRecyclerViewAdapter(ArrayList<ExportDetailsModel> nameList, Context _context) {
            this.ChildList = nameList;
            this.context = _context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView Barcode, WoodSpiceCode, DiamterSize, CBM;
            LinearLayout Background, Remove;

            public ViewHolder(View view) {
                super(view);
                Barcode = view.findViewById(R.id.export_sbblabel);
                WoodSpiceCode = view.findViewById(R.id.export_speiceCode);
                Background = view.findViewById(R.id.exportlayoutBackground);
                Remove = view.findViewById(R.id.export_removeBarCode);
                DiamterSize = view.findViewById(R.id.export_DiameterSizes);
                CBM = view.findViewById(R.id.export_TotCBM);
            }
        }

        public ExportDetailsModel getItem(int position) {
            return ChildList.get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.exportdetails_infliator, parent, false);
            InnerRecyclerViewAdapter.ViewHolder vh = new InnerRecyclerViewAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.Barcode.setText(ChildList.get(position).getSbbLabel()); //barcode
            holder.WoodSpiceCode.setText(ChildList.get(position).getWoodSpieceCode());//specie
            holder.WoodSpiceCode.setText(ChildList.get(position).getWoodSpieceCode());//specie
            holder.DiamterSize.setText(ChildList.get(position).getDiameter());//Diameter
            holder.CBM.setText(String.valueOf(ChildList.get(position).getVolume()));//Diameter
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
            int color = generator.getColor(getItem(position));
            holder.Remove.setBackgroundColor(color);
            holder.Remove.setVisibility(View.INVISIBLE);
        }

        @Override
        public int getItemCount() {
            return ChildList.size();
        }
    }

    public void ExportSaveDatas(String OrderNo, int ExportID) {
        Common.ExportDetailsInputList.clear();
        Common.ExportDetailsInputList = mDBInternalHelper.getExportDetailsList(ExportID, true);
        //Common.ExportDetailsInputList = mDBInternalHelper.getExportInputDetailsList(ExportID, true);
        if (Common.ExportDetailsInputList.size() > 0) {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                new GetExportSaveDetailsAsynkTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else {
            AlertDialogBox(CommonMessage(R.string.ExportHead), "Values are empty", false);
        }
    }

    class GetExportSaveDetailsAsynkTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.SyncStatusList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String MethodName = "InsertExport";//"InsertHHInventoryTransfer/";
            String SyncURL = ServiceURL.getServiceURL(ServiceURL.ExportControllerName, MethodName);
            SyncURL = SyncURL.replaceAll("%20", "");
            exportSyncModel = new ExportDetailsInputSavedListModel();
            exportSyncModel.setExportID(Common.ExportID);
            exportSyncModel.setOrderNo(Common.Order_Number);
            exportSyncModel.setIMEINumber(Common.IMEI);
            exportSyncModel.setLocationID(Common.LocationID);
            exportSyncModel.setStartTime(Common.SyncStartDateTime);
            exportSyncModel.setEndTime(Common.SyncEndDateTime);
            exportSyncModel.setUserID(Common.UserID);
            exportSyncModel.setTotalCount(Common.Export_Count);
            exportSyncModel.setExportUniqueID(Common.ExportUniqueID);
            exportSyncModel.setHHExportListandDetailsObj(Common.ExportDetailsInputList);
            try {
                String SyncURLInfo = new Communicator().POST_Obect(SyncURL, exportSyncModel);
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
                            boolean ListIdFlag = mDBInternalHelper.UpdateExportSavedStatus(Common.SyncTime, 1, Common.Order_Number, Common.ExportID);
                            if (ListIdFlag == true) {
                                //Scanned Result Refresh
                                GetExportDateList();
                                AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + String.valueOf(Common.ExportID) + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + String.valueOf(Common.ExportID) + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                            return;
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + String.valueOf(Common.ExportID) + "--" + "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }
    }

}

