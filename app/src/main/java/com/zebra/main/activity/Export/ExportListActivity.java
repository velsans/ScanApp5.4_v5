package com.zebra.main.activity.Export;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zebra.R;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.activity.Common.GwwMainActivity;
import com.zebra.main.adapter.ExportCodeListAdapter;
import com.zebra.main.api.ApiClient;
import com.zebra.main.api.ApiInterface;
import com.zebra.main.api.ServiceURL;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.interfac.BaseApplication;
import com.zebra.main.model.Export.BarcodeDetailsInputModel;
import com.zebra.main.model.Export.ContainerListInputModel;
import com.zebra.main.model.Export.ExportCodeModel;
import com.zebra.main.model.Export.ExportContainerDetailsModel;
import com.zebra.main.model.Export.ExportDetailsInputSavedListModel;
import com.zebra.main.model.Export.ExportDetailsModel;
import com.zebra.main.model.Export.ExportDetailstTotalSyncModel;
import com.zebra.main.model.Export.ExportListInputModel;
import com.zebra.main.model.Export.ExportListModel;
import com.zebra.main.model.Export.ExportListTotalInputModel;
import com.zebra.main.model.Export.ExportModel;
import com.zebra.main.model.Export.ExportOrderDetailsModel;
import com.zebra.main.model.Export.QuotationExternalModel;
import com.zebra.main.model.Export.QuotationInternalModel;
import com.zebra.main.model.Export.QuotationListInputModel;
import com.zebra.main.model.SyncStatusModel;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;
import com.zebra.utilities.Communicator;
import com.zebra.utilities.ConnectivityReceiver;
import com.zebra.utilities.GwwException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zebra.utilities.Common.dateFormat;

public class ExportListActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private InternalDataBaseHelperClass mDBInternalHelper;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    ExpendableExportAdapter exportContaineradapter;
    LinearLayoutManager horizontalLayoutManager, InveDateLayoutManager;
    RecyclerView ExportList, ExportListDate, export_details_recyclerview;
    FrameLayout frame_quatation_details_layout;
    TextView NoValueFoundTxT, Export_orderNoTxT, Export_UniqueIDTxT, OrderDetailsTxT, TotalFilteredCountTxT, TotalFilteredVolumeTxT, NovalueFoundContainerlistTxT,
            ContainerNoTxT, OrderTotalCBMTxT, OrderRemainingCBMTxT, YesOptionTxT, NoOptionTxT;
    LinearLayout ProgressBarLay, CreateContainerNumberLayout;
    AlertDialogManager alert = new AlertDialogManager();
    boolean isInternetPresent;
    ExportDateAdapter exportDateadapter;
    AlertDialog.Builder Signoutbuilder = null;
    AlertDialog SignoutAlert = null;
    EditText submitOrderNoValue, submitContainerValue, CreateContainerNumberEDT;
    Button submitOrderNo;
    ExportContainerDetailsModel expContainerDetailsModel;
    ExportDetailstTotalSyncModel exportTotalSyncModel;
    ExportDetailsInputSavedListModel exportSyncModel;
    QuotationExternalModel QuotationExterModel;
    ExportOrderDetailsModel exportOrdermodel;
    SyncStatusModel suncStaModel;
    ImageView containerDetailsLAYClose, RemoveIMG, createContainerNoLayoutClose, CreateContainerBtn;
    ApiInterface ExportSync = null;
    ExportListTotalInputModel exportListTotalInputModel;
    ExportListModel exportListModel;
    String directory_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BackUp/";
    private static HSSFWorkbook myWorkBook = new HSSFWorkbook();
    private static HSSFSheet mySheet = myWorkBook.createSheet();
    ArrayList<ExportCodeModel> exportCodeList;
    Spinner exportCodeSpin;
    ExportCodeListAdapter exportCodeAdapter;
    SwipeRefreshLayout exportlist_swipeLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exportlist);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mDBExternalHelper = new ExternalDataBaseHelperClass(this);
        mDBInternalHelper = new InternalDataBaseHelperClass(this);
        ExportSync = ApiClient.getInstance().getUserService();
        Initialization();
        //GetExportDateList();
        OnclickeListener();
        getExportGrid();
    }

    @Override
    protected void onResume() {
        //  Common.HideKeyboard(ExportListActivity.this);
        BaseApplication.getInstance().setConnectivityListener(this);
        super.onResume();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void OnclickeListener() {
        ExportList.setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return false;
        });

        containerDetailsLAYClose.setOnClickListener(v -> frame_quatation_details_layout.setVisibility(View.GONE));
        exportCodeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (exportCodeList.size() > 0) {
                   /* Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                            Toast.LENGTH_SHORT).show();*/
                    Common.ExportID = exportCodeList.get(position).getExportId();
                    Common.ExportCode = exportCodeList.get(position).getExportCode();
                    Common.Order_Number = String.valueOf(exportCodeList.get(position).getOrderId());
                    Common.Export_ContainerCount = String.valueOf(exportCodeList.get(position).getContainerQuantity());
                    Common.StuffingDate = exportCodeList.get(position).getStuffingDate();
                    Common.BookingNumber = exportCodeList.get(position).getBookingNumber();
                    Common.CuttOffDateTime = exportCodeList.get(position).getCuttOffDateTime();
                    Common.ShippingAgentId = String.valueOf(exportCodeList.get(position).getShippingAgentId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        exportlist_swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
                getExportGrid();
                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Stop animation (This will be after 3 seconds)
                        exportlist_swipeLayout.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), "Refreshed !", Toast.LENGTH_LONG).show();
                    }
                }, 2000); // Delay in millis
            }
        });

    }

    public void onBackPressed() {
        if (frame_quatation_details_layout.getVisibility() == View.VISIBLE) {
            frame_quatation_details_layout.setVisibility(View.GONE);
            return;
        }
        Intent oneIntent = new Intent(this, GwwMainActivity.class);
        startActivity(oneIntent);

    }

    public void Initialization() {
        ProgressBarLay = findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        ExportList = findViewById(R.id.exportloadplanListView);
        NoValueFoundTxT = findViewById(R.id.NovalueFound);
        ExportListDate = findViewById(R.id.exportloadplanListViewDate);
        submitOrderNoValue = findViewById(R.id.exportOrderEditTXT);
        submitContainerValue = findViewById(R.id.exportContainerEditTXT);
        submitOrderNo = findViewById(R.id.exportLoadPlancreateListTxT);
        findViewById(R.id.exportLoadPlancreateListTxT).setOnClickListener(Obj_clickListener);
        frame_quatation_details_layout = findViewById(R.id.containerDetailsLAY);
        export_details_recyclerview = findViewById(R.id.recyclerview_Container_list_details);
        containerDetailsLAYClose = findViewById(R.id.containerDetailsLAYClose);
        Export_orderNoTxT = findViewById(R.id.export_orderNoTxT);
        Export_UniqueIDTxT = findViewById(R.id.export_UniqueIDTxT);
        CreateContainerBtn = findViewById(R.id.create_containerBTN);
        findViewById(R.id.create_containerBTN).setOnClickListener(Obj_clickListener);

        OrderDetailsTxT = findViewById(R.id.OrderDetailsTXT);
        TotalFilteredCountTxT = findViewById(R.id.containerTotalCountTXT);
        TotalFilteredVolumeTxT = findViewById(R.id.containerTotalVolumeTXT);
        NovalueFoundContainerlistTxT = findViewById(R.id.txt_NovalueFound_inner_list);
        ContainerNoTxT = findViewById(R.id.containerNoTXT);
        OrderTotalCBMTxT = findViewById(R.id.OrderTotalCBMTXT);
        OrderRemainingCBMTxT = findViewById(R.id.OrderRemainingCBMTXT);
        //26-11-2019
        CreateContainerNumberLayout = findViewById(R.id.createContainerNumberLayout);
        CreateContainerNumberEDT = findViewById(R.id.createContainerNoEDT);
        NoOptionTxT = findViewById(R.id.NoOptionTxT);
        findViewById(R.id.NoOptionTxT).setOnClickListener(Obj_clickListener);
        YesOptionTxT = findViewById(R.id.YesOptionTxT);
        findViewById(R.id.YesOptionTxT).setOnClickListener(Obj_clickListener);
        createContainerNoLayoutClose = findViewById(R.id.create_containerNo_close);
        findViewById(R.id.create_containerNo_close).setOnClickListener(Obj_clickListener);
        findViewById(R.id.export_sync).setOnClickListener(Obj_clickListener);
        exportCodeSpin = findViewById(R.id.exportOrderSpinner);
        exportlist_swipeLayout = findViewById(R.id.exportlist_swipe_refresh_layout);
        // Scheme colors for animation
        exportlist_swipeLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
    }

    private void GetExportDateList() {
        try {
            Common.Filter_ExportDate.clear();
            Common.Filter_ExportDate = mDBInternalHelper.getExportDate();
            if (Common.Filter_ExportDate.size() > 0) {
                if (Common.IsExportSelectFlag == true) {
                    Common.ExportDateSelectedIndex = Common.Filter_ExportDate.size() - 1;
                    Common.IsExportSelectFlag = false;
                }
                exportDateadapter = new ExportDateAdapter(Common.Filter_ExportDate, this);
                InveDateLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                InveDateLayoutManager.setStackFromEnd(true);
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
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    View.OnClickListener Obj_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                    case R.id.exportLoadPlancreateListTxT:
                        if (exportCodeList.size() > 0) {
                            if (checkConnection() == true) {
                                GetOrderDetailsApi();
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ExportHead), "Export number not found ", false);
                        }
                        break;
                    case R.id.NoOptionTxT:
                        break;
                    case R.id.YesOptionTxT:
                        if (CreateContainerNumValidation(CreateContainerNumberEDT.getText().toString())) {
                            //CreateNewContainer(false);
                        }
                        break;
                    case R.id.create_containerBTN:
                        CreateContainerNumberLayout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.create_containerNo_close:
                        CreateContainerNumberLayout.setVisibility(View.GONE);
                        break;
                    case R.id.export_sync:
                        ProgressBarLay.setVisibility(View.VISIBLE);
                        GetExportDetailsSync(Common.Filter_ExportDate.get(Common.ExportDateSelectedIndex));
                        break;
                }
            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
                AlertDialogBox("OonClickListener", ex.toString(), false);
            }
        }
    };

    private void GetExportDetailsSync(String OrderNo) {
        try {
            Common.ExportList.clear();
            Common.ExportList = mDBInternalHelper.getExportList(OrderNo);
            Common.Export.ContainersList.clear();
            Common.Export.ContainersList = mDBInternalHelper.getContainerNumber(OrderNo);
            if (Common.ExportList.size() > 0) {
                /*Expendable List Values*/
                exportTotalSyncModel = new ExportDetailstTotalSyncModel();
                exportTotalSyncModel.setExportID(Common.ExportList.get(0).getExportID());
                exportTotalSyncModel.setExportUniqueID(Common.ExportList.get(0).getExportUniqueID());
                exportTotalSyncModel.setOrderNo(Common.ExportList.get(0).getOrderNo());
                exportTotalSyncModel.setIMEI(Common.ExportList.get(0).getIMEI());
                exportTotalSyncModel.setLocationID(Common.ExportList.get(0).getLocationID());
                exportTotalSyncModel.setUserID(Common.ExportList.get(0).getUserID());
                exportTotalSyncModel.setStartTime(Common.ExportList.get(0).getStartDateTime());
                exportTotalSyncModel.setEndTime(Common.ExportList.get(0).getEndDateTime());
                exportTotalSyncModel.setTotalLogCount(mDBInternalHelper.TotalCountForExport(OrderNo));
                exportTotalSyncModel.setTotalLogVolume(String.valueOf(mDBInternalHelper.TotalVolumeForExport(OrderNo)));
                exportTotalSyncModel.setTotalContainerCount(Common.Export.ContainersList.size());
                exportTotalSyncModel.setHHConDetails(mDBInternalHelper.getContainerDetails(OrderNo));
                exportTotalSyncModel.setHHQuoDetails(mDBInternalHelper.getQuationListSync(Common.ExportList.get(0).getExportID()));
                exportTotalSyncModel.setHHBarCodeDetails(mDBInternalHelper.getExportBarcodeDetailsSync(Common.ExportList.get(0).getExportID()));
                String stringInput = new GsonBuilder().create().toJson(exportTotalSyncModel);
                /*Export Sync*/
                ExportSync = ApiClient.getApiInterface();
                ExportSync.getExportDetailsSync(exportTotalSyncModel).enqueue(new Callback<ExportDetailstTotalSyncModel>() {
                    @Override
                    public void onResponse(Call<ExportDetailstTotalSyncModel> call, Response<ExportDetailstTotalSyncModel> response) {
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.isSuccessful()) {
                                Common.SyncStatusList.clear();
                                Common.SyncStatusList.addAll(response.body().getStatus());
                                if (Common.SyncStatusList.get(0).getStatus() == 1) {
                                    Common.EndDate = dateFormat.format(Calendar.getInstance().getTime());
                                    Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                                    boolean ListIdFlag = mDBInternalHelper.UpdateExportSavedStatus(Common.SyncTime, 1, OrderNo, Common.ExportList.get(0).getExportID());
                                    if (Common.FellingRegSyncALL == false) {
                                        if (ListIdFlag == true) {
                                            AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.ExportList.get(0).getExportID() + "--" + Common.SyncStatusList.get(0).getMessage(), true);
                                            //Scanned Result Refresh
                                            GetExportList(Common.Filter_ExportDate.get(Common.ExportDateSelectedIndex));
                                        }
                                    }
                                } else {
                                    AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.ExportList.get(0).getExportID() + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                                }
                            } else {
                                AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.ExportList.get(0).getExportID() + "" + response.message(), false);
                            }
                        } else {
                            Common.AuthorizationFlag = true;
                            AlertDialogBox(CommonMessage(R.string.ExportHead), response.message(), false);
                        }
                        ProgressBarLay.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<ExportDetailstTotalSyncModel> call, Throwable t) {
                        ProgressBarLay.setVisibility(View.GONE);
                        AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.TransferID + "--" + t.getMessage(), false);

                    }
                });
            } else {
                AlertDialogBox(CommonMessage(R.string.ExportHead), "Add at-least one container and one log", false);
            }
        } catch (Exception ex) {
            ProgressBarLay.setVisibility(View.GONE);
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox(CommonMessage(R.string.ExportHead), ex.toString(), false);
        }
    }

    private void GetExportList(String QuotationNo) {
        try {
            //OrderDetailsTxT.setText("QUOTATION NO# " + QuotationNo);
            Common.ExportList.clear();
            Common.ExportList = mDBInternalHelper.getExportList(QuotationNo);
            if (Common.ExportList.size() > 0) {
                exportContaineradapter = new ExpendableExportAdapter(Common.ExportList, this);
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

        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
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

    public void ExportAcivityCall() {
        Common.BarcodeScannerFlagForExport=false;
        Common.SelectedTabPosition = 1;
        Intent _gwIntent = new Intent(this, ExportDetailsActivity.class);
        startActivity(_gwIntent);
    }

    public class ExpendableExportAdapter extends RecyclerView.Adapter<ExpendableExportAdapter.ViewHolder> {

        ArrayList<ExportModel> HeadList = new ArrayList<ExportModel>();
        ArrayList<Integer> counter = new ArrayList<Integer>();
        Context context;

        public ExpendableExportAdapter(ArrayList<ExportModel> nameList, Context context) {
            this.HeadList = nameList;
            this.context = context;
            for (int i = 0; i < nameList.size(); i++) {
                counter.add(0);
            }
        }

        public ExportModel getItem(int position) {
            return HeadList.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView CardViewbackgroundLayout;
            TextView OrderNoTXT, ExportNumTXT, QuotationTXT, BookingNoTXT, ShipCmpyTXT, ContainerCountTXT, StuffingDateTXT, CutOffEndDateTimeTXT, StartTimeTXT, EndTimeTXT, CountTXT, VolumeTXT, SyncStatus, ExportSyncDateTxT;
            ImageView StatusIMG, SyncIMG, DeleteIMG, ExportListToExcelOption;
            LinearLayout Background, cardview_background;

            public ViewHolder(View view) {
                super(view);
                OrderNoTXT = view.findViewById(R.id.exportOrderNumTxT);
                ExportNumTXT = view.findViewById(R.id.exportNumTxT);
                QuotationTXT = view.findViewById(R.id.exportQuatN0TxT);
                BookingNoTXT = view.findViewById(R.id.exportBookingNumTxT);
                ShipCmpyTXT = view.findViewById(R.id.exportShippingCompanyTxT);
                ContainerCountTXT = view.findViewById(R.id.exportContainerCountTxT);
                StuffingDateTXT = view.findViewById(R.id.exportStuffingDateTxT);
                CutOffEndDateTimeTXT = view.findViewById(R.id.exportCutOffEndDateTimeTxT);
                StartTimeTXT = view.findViewById(R.id.export_startTimeTxT);
                EndTimeTXT = view.findViewById(R.id.export_endTimeTxT);
                CountTXT = view.findViewById(R.id.export_TotalCountTxT);
                VolumeTXT = view.findViewById(R.id.export_VolumeTxT);

                // 9-12-2019 added
                Background = view.findViewById(R.id.exportlayoutBackground_1);
                cardview_background = view.findViewById(R.id.cardview_background);
                DeleteIMG = view.findViewById(R.id.ImgExportDelete);
                SyncIMG = view.findViewById(R.id.exportSave);
                StatusIMG = view.findViewById(R.id.exportSyncStatusIcon);
                CardViewbackgroundLayout = view.findViewById(R.id.export_layout_background);
                SyncStatus = view.findViewById(R.id.exportSyncStatus);
                ExportSyncDateTxT = view.findViewById(R.id.exportSyncDate);
                ExportListToExcelOption = view.findViewById(R.id.ExportListExportTOExcel);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.exportlist_infliator, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            try {
                //9-12-2019 added
                holder.OrderNoTXT.setText(String.valueOf(HeadList.get(position).getOrderNo()));
                holder.ExportNumTXT.setText(String.valueOf(HeadList.get(position).getExportUniqueID()));
                holder.QuotationTXT.setText(HeadList.get(position).getQuotationNo());
                holder.ContainerCountTXT.setText(String.valueOf(HeadList.get(position).getContainerCount()));
                holder.BookingNoTXT.setText(HeadList.get(position).getBookingNo());
                holder.ShipCmpyTXT.setText(HeadList.get(position).getShippingCompany());
                holder.StuffingDateTXT.setText(HeadList.get(position).getStuffingDateTime());
                holder.CutOffEndDateTimeTXT.setText(HeadList.get(position).getCuttingDateTime());

                holder.StartTimeTXT.setText(HeadList.get(position).getStartDateTime());
                holder.EndTimeTXT.setText(HeadList.get(position).getEndDateTime());
                holder.CountTXT.setText(String.valueOf(HeadList.get(position).getTotalCount()));
                holder.VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(HeadList.get(position).getVolume())));
                holder.ExportSyncDateTxT.setText(HeadList.get(position).getSyncTime());
                if (HeadList.get(position).getSyncStatus() == 1) {
                    //holder.SyncIMG.setBackgroundResource(R.mipmap.success);
                    holder.SyncStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    holder.SyncStatus.setText("Synced Already");
                    holder.StatusIMG.setImageDrawable(getResources().getDrawable(R.mipmap.success));

                } else {
                    //holder.SyncIMG.setBackgroundResource(R.mipmap.fail);
                    holder.SyncStatus.setTextColor(context.getResources().getColor(R.color.red));
                    holder.SyncStatus.setText("Not Synced");
                    holder.StatusIMG.setImageDrawable(getResources().getDrawable(R.mipmap.fail));
                }
                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
                int color = generator.getColor(getItem(position));
                holder.Background.setBackgroundColor(color);
                holder.cardview_background.setBackgroundColor(color);
                // holder.cardView.
                holder.CardViewbackgroundLayout.setOnLongClickListener(v -> {
                    try {
                        Common.ExportID = HeadList.get(position).getExportID();
                        Common.Order_Number = HeadList.get(position).getOrderNo();
                        // Common.ContainerNo = HeadList.get(position).getContainerNo();
                        Common.Export_Count = HeadList.get(position).getTotalCount();
                        Common.ExportUniqueID = HeadList.get(position).getExportUniqueID();
                        Common.QuotationNo = HeadList.get(position).getQuotationNo();

                        Common.IsExportEditListFlag = HeadList.get(position).getSyncStatus() != 0;
                        Common.IsQuotationNew = false;
                        ExportAcivityCall();
                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        AlertDialogBox("cardView Background", ex.toString(), false);
                    }
                    return false;
                });

                holder.CardViewbackgroundLayout.setOnClickListener(v -> {
                    Common.ExportID = HeadList.get(position).getExportID();
                    Common.QuotationNo = HeadList.get(position).getQuotationNo();
                    //Common.ContainerNo = HeadList.get(position).getContainerNo();

                    Common.Export.QuotationDetailsTotalVolumeRCBMInternalList.clear();
                    Common.Export.QuotationDetailsTotalVolumeRCBMInternalList = mDBInternalHelper.getInternalQuationList(Common.QuotationNo, Common.ExportID);
                    OrderTotalCBMTxT.setText("ToT CBM : " + TotalVolume(Common.Export.QuotationDetailsTotalVolumeRCBMInternalList));

                    Double QuotationTotalCBM = 0.00, RemainingCBM = 0.00, TotalScannedVolume = 0.00;
                    Common.Export.LogSummaryInternalWSCList.clear();
                    //Common.QuotationInternalDiameterList.clear();

                    for (QuotationInternalModel QuoaModel : Common.Export.QuotationDetailsTotalVolumeRCBMInternalList) {
                        QuotationTotalCBM = QuotationTotalCBM + QuoaModel.getQutTotalCBM();
                        Common.Export.LogSummaryInternalWSCList.add(QuoaModel.getQutWoodSpieceCode());
                        //Common.QuotationInternalDiameterList.add(QuoaModel.getQutDiameter());
                    }
                    /*Remainng CBM*/
                    TotalScannedVolume = mDBInternalHelper.TotalVolumeForExport(String.valueOf(Common.ExportID));
                    /*Remaining Volume*/
                    RemainingCBM = QuotationTotalCBM - TotalScannedVolume;
                    OrderRemainingCBMTxT.setText("Rem CBM: " + Common.decimalFormat.format(RemainingCBM));
                    OrderDetailsTxT.setText("QUOTATION NO# " + Common.QuotationNo);
                });

                holder.SyncIMG.setOnClickListener(v -> {
                    //Sync Date to BackEnd
                    try {
                        Common.ExportID = HeadList.get(position).getExportID();
                        Common.Order_Number = HeadList.get(position).getOrderNo();
                        Common.IMEI = HeadList.get(position).getIMEI();
                        Common.SyncStartDateTime = HeadList.get(position).getStartDateTime();
                        Common.SyncEndDateTime = HeadList.get(position).getEndDateTime();
                        Common.LocationID = HeadList.get(position).getLocationID();
                        Common.Export_Count = HeadList.get(position).getTotalCount();
                        Common.QuotationNo = HeadList.get(position).getQuotationNo();
                        // 21-11-2019 volume added
                        Common.Export_Volume = String.valueOf(Common.decimalFormat.format(HeadList.get(position).getVolume()));
                        Common.ExportUniqueID = HeadList.get(position).getExportUniqueID();
                        Common.Export.ExportDetailsInputList.clear();
                        Common.Export.ExportDetailsInputList = mDBInternalHelper.getExportDetailsList(HeadList.get(position).getExportID(), "ALL");
                        //Common.ExportDetailsInputList = mDBInternalHelper.getExportInputDetailsList(ExportID, true);
                        if (Common.Export.ExportDetailsInputList.size() > 0) {
                            if (checkConnection() == true) {
                                ExportListInputModel exportListInput = new ExportListInputModel();
                                exportListInput.setExportUniqueID(HeadList.get(position).getExportUniqueID());
                                exportListInput.setActive(HeadList.get(position).getIsActive());
                                exportListInput.setContainerCount(HeadList.get(position).getContainerCount());
                                exportListInput.setEndDateTime(HeadList.get(position).getEndDateTime());
                                exportListInput.setIMEI(HeadList.get(position).getIMEI());
                                exportListInput.setLocationID(HeadList.get(position).getLocationID());
                                exportListInput.setOrderNo(HeadList.get(position).getOrderNo());
                                exportListInput.setQuotationNo(HeadList.get(position).getQuotationNo());
                                exportListInput.setStartDateTime(HeadList.get(position).getStartDateTime());
                                exportListInput.setSyncDate(HeadList.get(position).getSyncTime());
                                exportListInput.setSyncStatus(HeadList.get(position).getSyncStatus());
                                exportListInput.setTotalCount(HeadList.get(position).getTotalCount());
                                exportListInput.setUserID(HeadList.get(position).getUserID());
                                exportListInput.setVolume(HeadList.get(position).getVolume());
                                ArrayList<ExportListInputModel> listExportListInputModels = new ArrayList<ExportListInputModel>();
                                listExportListInputModels.add(exportListInput);

                                if (Double.parseDouble(Common.Export_Volume) >= TotalVolume(mDBInternalHelper.getInternalQuationList(Common.QuotationNo, Common.ExportID))) {
                                    getExportListInputAPI(Common.ExportID, Common.QuotationNo, listExportListInputModels);
                                } else {
                                    QuotationVolumeDialog(CommonMessage(R.string.ExportHead), "Export is not completed, Can you continue?", Common.ExportID, Common.QuotationNo, listExportListInputModels, true);
                                }
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ExportHead), "Logs are empty", false);
                        }
                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        AlertDialogBox("Transfer Sync", ex.toString(), false);
                    }
                });

                holder.ExportListToExcelOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Common.ExportID = HeadList.get(position).getExportID();
                        ExportListInputModel exportListInput = new ExportListInputModel();
                        exportListInput.setExportUniqueID(HeadList.get(position).getExportUniqueID());
                        exportListInput.setActive(HeadList.get(position).getIsActive());
                        exportListInput.setContainerCount(HeadList.get(position).getContainerCount());
                        exportListInput.setEndDateTime(HeadList.get(position).getEndDateTime());
                        exportListInput.setIMEI(HeadList.get(position).getIMEI());
                        exportListInput.setLocationID(HeadList.get(position).getLocationID());
                        exportListInput.setOrderNo(HeadList.get(position).getOrderNo());
                        exportListInput.setQuotationNo(HeadList.get(position).getQuotationNo());
                        exportListInput.setStartDateTime(HeadList.get(position).getStartDateTime());
                        exportListInput.setSyncDate(HeadList.get(position).getSyncTime());
                        exportListInput.setSyncStatus(HeadList.get(position).getSyncStatus());
                        exportListInput.setTotalCount(HeadList.get(position).getTotalCount());
                        exportListInput.setUserID(HeadList.get(position).getUserID());
                        exportListInput.setVolume(HeadList.get(position).getVolume());
                        ArrayList<ExportListInputModel> listExportListInputModels = new ArrayList<ExportListInputModel>();
                        listExportListInputModels.add(exportListInput);

                        ExportListRecordsToExcel(listExportListInputModels, Common.ExportID);

                    }
                });


                holder.DeleteIMG.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Common.ExportID = HeadList.get(position).getExportID();
                            if (HeadList.size() > 0) {
                                ExportOrdetDeleteDatasIFUserID_ONE("Are you sure want delete all data's");
                            }
                        } catch (Exception ex) {
                            CrashAnalytics.CrashReport(ex);
                            AlertDialogBox("IE-mRemoveExportNo", ex.toString(), false);
                        }
                    }
                });
            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
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
                //WoodSpiceCode = view.findViewById(R.id.export_speiceCode);
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
           /* if (ChildList.size() > 0) {
                holder.Remove.setVisibility(View.VISIBLE);
                NovalueFound_inner_list_Txt.setVisibility(View.INVISIBLE);
            } else {
                holder.Remove.setVisibility(View.INVISIBLE);
                NovalueFound_inner_list_Txt.setVisibility(View.VISIBLE);
            }*/
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
            exportSyncModel.setHHExportListandDetailsObj(Common.Export.ExportDetailsInputList);
            try {
                String SyncURLInfo = new Communicator().POST_Obect(SyncURL, exportSyncModel);
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
            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
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
                            Common.EndDate = dateFormat.format(Calendar.getInstance().getTime());
                            Common.SyncTime = Common.SyncStatusList.get(0).getSyncTime();
                            boolean ListIdFlag = mDBInternalHelper.UpdateExportSavedStatus(Common.SyncTime, 1, Common.Order_Number, Common.ExportID);
                            if (ListIdFlag == true) {
                                //Scanned Result Refresh
                                GetExportDateList();
                                AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.ExportID + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.ExportID + "--" + Common.SyncStatusList.get(0).getMessage(), false);
                            return;
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.ExportID + "--" + "Not Synced", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }
    }

    public void ExportOrdetDeleteDatasIFUserID_ONE(String ErrorMessage) {
        if (SignoutAlert != null && SignoutAlert.isShowing()) {
            return;
        }
        Signoutbuilder = new AlertDialog.Builder(this);
        Signoutbuilder.setMessage(ErrorMessage);
        Signoutbuilder.setCancelable(true);
        Signoutbuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Signoutbuilder.setNegativeButton(CommonMessage(R.string.action_delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeleteTotalExportDetails(Common.ExportID);
                        dialog.cancel();
                    }
                });

        SignoutAlert = Signoutbuilder.create();
        SignoutAlert.show();

    }

    public void DeleteTotalExportDetails(int ExportID) {
        try {
            boolean DeleteExportFlag = mDBInternalHelper.DeleteExportListID(ExportID);
            if (DeleteExportFlag) {
                boolean DeleteListFlag = mDBInternalHelper.DeleteExportContainerDetails(ExportID);
                if (DeleteListFlag == true) {
                    boolean DeleteScannedFlag = mDBInternalHelper.DeleteExportScannedFromList(ExportID);
                    if (DeleteScannedFlag == true) {
                        boolean DeleteQuotaFlag = mDBInternalHelper.DeleteQuotationListFromExportList(ExportID);
                        if (DeleteQuotaFlag == true) {
                            Common.IsExportSelectFlag = true;
                            GetExportDateList();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("DeleteExportScannedList", ex.toString(), false);
        }
    }

    public boolean CreateContainerNumValidation(String CreateContainerNum) {
        if (CreateContainerNum.equals("")) {
            AlertDialogBox("Create New Container", "Please Enter Container Number", false);
            CreateContainerNumberEDT.requestFocus();
            return false;
        }
        return true;
    }

    public void GetOrderDetailsApi() {
        try {
            Common.Export.LogSummaryList.clear();
            Common.Export.ContainerList.clear();
            Common.Export.ExportDetailsList.clear();
            Common.Export.TotalCBMDetailsList.clear();
            ProgressBarLay.setVisibility(View.VISIBLE);
            exportOrdermodel = new ExportOrderDetailsModel();
            exportOrdermodel.setExportID(Common.ExportID);

            ExportSync = ApiClient.getApiInterface();
            ExportSync.GetExportOrderDetails(exportOrdermodel).enqueue(new Callback<ExportOrderDetailsModel>() {
                @Override
                public void onResponse(Call<ExportOrderDetailsModel> call, Response<ExportOrderDetailsModel> response) {
                    if (GwwException.GwwException(response.code()) == true) {
                        if (response.isSuccessful()) {
                            Common.Export.LogSummaryList.addAll(response.body().getLogSummaryDetails());
                            Common.Export.ContainerList.addAll(response.body().getContainersDetails());
                            Common.Export.ExportDetailsList.addAll(response.body().getLogDetails());
                            Common.Export.TotalCBMDetailsList.addAll(response.body().getTotalCBMDetails());
                            if (Common.Export.LogSummaryList.size() > 0) {
                                InsertQuotationDetails();
                                //ExportDetailsIntent();
                            } else {
                                AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.ExportID + "--" + CommonMessage(R.string.NoValueFound) + ", Please try some other export number", false);
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.ExportID + "--" + response.message(), false);
                        }
                    } else {
                        Common.AuthorizationFlag = true;
                        AlertDialogBox(CommonMessage(R.string.ExportHead), response.message(), false);
                    }
                    ProgressBarLay.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ExportOrderDetailsModel> call, Throwable t) {
                    ProgressBarLay.setVisibility(View.GONE);
                    AlertDialogBox(CommonMessage(R.string.ExportHead), t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            ProgressBarLay.setVisibility(View.GONE);
            AlertDialogBox(CommonMessage(R.string.ExportHead), ex.toString(), false);
        }
    }

    public void InsertQuotationDetails() {
        try {
            Common.IsExportEditListFlag = false;
            Common.IsQuotationNew = true;
            ExportAcivityCall();
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox(CommonMessage(R.string.ExportHead), ex.toString(), false);
        }
    }

    public void ExportDetailsIntent() {
        try {
            Common.VolumeSum = 0.0;
            Common.Count = 0;
            Common.StartDate = dateFormat.format(Calendar.getInstance().getTime());
            boolean ListIdFlag = mDBInternalHelper.insertExportIDList(Common.Order_Number, Common.IMEI, Common.ToLocationID, Common.StartDate, Common.EndDate,
                    Common.UserID, Common.Count, String.valueOf(Common.VolumeSum), 1, Common.ExportUniqueID, Common.QuotationNo);
            if (ListIdFlag == true) {
                Common.ExportID = Integer.parseInt(mDBInternalHelper.getLastExportID());
                String DateUniqueFormat = Common.UniqueIDdateFormat.format(Calendar.getInstance().getTime());
                String DeviceID = "";
                if (String.valueOf(Common.LDeviceID).length() == 1) {
                    DeviceID = "0" + Common.LDeviceID;
                } else {
                    DeviceID = String.valueOf(Common.LDeviceID);
                }
                Common.ExportUniqueID = DateUniqueFormat + DeviceID + Common.ExportID;
                if (isNullOrEmpty(Common.Order_Number)) {
                    Common.Order_Number = "DO-" + Common.ExportUniqueID;
                }
                // Update values into ExportID
                boolean TransferIDFlag = mDBInternalHelper.UpdateExportUniqueID(Common.ExportID, Common.ExportUniqueID, Common.Order_Number);
                Common.IsExportEditListFlag = true;
                if (isNullOrEmpty(Common.ContainerNo)) {
                    Common.ContainerNo = "DC-" + Common.ExportUniqueID + "1";// String.valueOf(Common.ContainerNoInt);
                }

                InsertQuotationDetails();
            } else {
                AlertDialogBox("IE-AddCreateExportList", "Values are not inserted", false);
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("IE-AddCreateExportList", ex.toString(), false);
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

    public double TotalVolume(ArrayList<QuotationInternalModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (QuotationInternalModel exportDetailsModel : TotalScannedList) {
            TotVolume = TotVolume + exportDetailsModel.getQutTotalCBM();
        }
        return TotVolume;
    }

    public void getExportListInputAPI(int exportId, String exportNum, ArrayList<ExportListInputModel> exportListInput) {
        try {
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.Export.AllExportDetails.clear();
            Common.SyncStatusExportInputList.clear();
            exportListTotalInputModel = new ExportListTotalInputModel();
            exportListTotalInputModel.setHHLoadPlan(exportListInput);
            exportListTotalInputModel.setHHLoadPlanQuotation(mDBInternalHelper.getInternalQuationListInput(exportNum, exportId));
            exportListTotalInputModel.setHHLoadPlanContainer(mDBInternalHelper.getContainerListInput(Common.QuotationNo, Common.ExportID));
            exportListTotalInputModel.setHHLoadPlanDetails(mDBInternalHelper.getExportDetailsListInput(exportId, "ALL"));

            ExportSync = ApiClient.getApiInterface();
            ExportSync.getInsertLoadPlan(exportListTotalInputModel).enqueue(new Callback<ExportListTotalInputModel>() {
                @Override
                public void onResponse(Call<ExportListTotalInputModel> call, Response<ExportListTotalInputModel> response) {
                    try {
                        ProgressBarLay.setVisibility(View.GONE);
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.isSuccessful()) {
                                Common.SyncStatusExportInputList.addAll(response.body().getStatus());
                                if (Common.SyncStatusExportInputList.get(0).getStatus() == 1) {
                                    Common.EndDate = dateFormat.format(Calendar.getInstance().getTime());
                                    Common.SyncTime = Common.SyncStatusExportInputList.get(0).getSyncTime();
                                    boolean ListIdFlag = mDBInternalHelper.UpdateExportSavedStatus(Common.SyncTime, 1, Common.Order_Number, Common.ExportID);
                                    if (ListIdFlag == true) {
                                        //Scanned Result Refresh
                                        GetExportDateList();
                                        AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.ExportID + "--" + Common.SyncStatusExportInputList.get(0).getMessage(), true);
                                    }
                                } else {
                                    AlertDialogBox(CommonMessage(R.string.ExportHead), "#" + Common.ExportID + "--" + Common.SyncStatusExportInputList.get(0).getMessage(), false);
                                }
                            } else {
                                AlertDialogBox(CommonMessage(R.string.ExportHead), response.message(), false);
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ExportHead), response.message(), false);
                        }
                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        AlertDialogBox(CommonMessage(R.string.ExportHead), ex.getMessage(), false);
                    }
                }

                @Override
                public void onFailure(Call<ExportListTotalInputModel> call, Throwable t) {
                    ProgressBarLay.setVisibility(View.GONE);
                    AlertDialogBox(CommonMessage(R.string.ExportHead), t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            ProgressBarLay.setVisibility(View.GONE);
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox(CommonMessage(R.string.ExportHead), ex.toString(), false);
        }
    }

    public void QuotationVolumeDialog(String title, CharSequence message, int exportId, String exportNum,
                                      ArrayList<ExportListInputModel> exportListInput, boolean status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message)
                .setIcon((status) ? R.mipmap.success : R.mipmap.fail)
                .setCancelable(false)
                .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getExportListInputAPI(exportId, exportNum, exportListInput);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(CommonMessage(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void ExportListRecordsToExcel(ArrayList<ExportListInputModel> exportlistInputData, int exportId) {

        try {
            String QuotationNum = exportlistInputData.get(0).getQuotationNo();
            String dateTime = dateFormat.format(Calendar.getInstance().getTime());
            File file = new File(directory_path);
            if (!file.exists()) {
            }
            String dest = directory_path + "ExportList_" + QuotationNum + "_" + Common.DeviceName + "_" + dateTime + ".xls";
            try {

                for (int i = 0; i < Common.ExportListExcelExportList.length; i++) {

                    if (i == 0) {
                        int index = myWorkBook.getSheetIndex(Common.ExportListExcelExportList[i]);
                        if (index == -1) {
                            mySheet = myWorkBook.createSheet(Common.ExportListExcelExportList[i]);
                        } else {
                            mySheet = myWorkBook.getSheetAt(index);
                            for (int Del_Row = 1; Del_Row <= mySheet.getLastRowNum(); Del_Row++) {
                                Row row = mySheet.getRow(Del_Row);
                                deleteRow(mySheet, row);
                            }
                        }

                        for (int Row = 0; Row < exportlistInputData.size() + 1; Row++) {
                            if (Row == 0) {
                                excelLog(Row, 0, "QuotationNo");
                                excelLog(Row, 1, "OrderNo");
                                excelLog(Row, 2, "StartDateTime");
                                excelLog(Row, 3, "EndDateTime");
                                excelLog(Row, 4, "SyncTime");
                                excelLog(Row, 5, "IMEI");
                                excelLog(Row, 6, "ExportUniqueID");
                                excelLog(Row, 7, "LocationID");
                                excelLog(Row, 8, "UserID");
                                excelLog(Row, 9, "IsActive");
                                excelLog(Row, 10, "ContainerCount");
                                excelLog(Row, 11, "SyncStatus");
                                excelLog(Row, 12, "TotalCount");
                                excelLog(Row, 13, "Volume");

                            } else {
                                excelLog(Row, 0, exportlistInputData.get(Row - 1).getQuotationNo());
                                excelLog(Row, 1, String.valueOf(exportlistInputData.get(Row - 1).getOrderNo()));
                                excelLog(Row, 2, String.valueOf(exportlistInputData.get(Row - 1).getStartDateTime()));
                                excelLog(Row, 3, String.valueOf(exportlistInputData.get(Row - 1).getEndDateTime()));
                                excelLog(Row, 4, String.valueOf(exportlistInputData.get(Row - 1).getSyncDate()));
                                excelLog(Row, 5, String.valueOf(exportlistInputData.get(Row - 1).getIMEI()));
                                excelLog(Row, 6, exportlistInputData.get(Row - 1).getExportUniqueID());
                                excelLog(Row, 7, String.valueOf(exportlistInputData.get(Row - 1).getLocationID()));
                                excelLog(Row, 8, String.valueOf(exportlistInputData.get(Row - 1).getUserID()));
                                excelLog(Row, 9, String.valueOf(exportlistInputData.get(Row - 1).isActive()));
                                excelLog(Row, 10, String.valueOf(exportlistInputData.get(Row - 1).getContainerCount()));
                                excelLog(Row, 11, String.valueOf(exportlistInputData.get(Row - 1).getSyncStatus()));
                                excelLog(Row, 12, String.valueOf(exportlistInputData.get(Row - 1).getTotalCount()));
                                excelLog(Row, 13, String.valueOf(exportlistInputData.get(Row - 1).getVolume()));

                            }
                        }
                    }

                    if (i == 1) {
                        int index = myWorkBook.getSheetIndex(Common.ExportListExcelExportList[i]);
                        if (index == -1) {
                            mySheet = myWorkBook.createSheet(Common.ExportListExcelExportList[i]);
                        } else {
                            mySheet = myWorkBook.getSheetAt(index);
                            for (int Del_Row = 1; Del_Row <= mySheet.getLastRowNum(); Del_Row++) {
                                Row row = mySheet.getRow(Del_Row);
                                deleteRow(mySheet, row);
                            }
                        }
                        ArrayList<QuotationListInputModel> exportQuotationList = mDBInternalHelper.getInternalQuationListInput(QuotationNum, exportId);

                        for (int Row = 0; Row < exportQuotationList.size() + 1; Row++) {
                            if (Row == 0) {
                                excelLog(Row, 0, "QuotationUniqueNo");
                                excelLog(Row, 1, "QuotationNo");
                                excelLog(Row, 2, "QutWoodSpieceCode");
                                excelLog(Row, 3, "QutDiameter");
                                excelLog(Row, 4, "QutTotalCBM");

                            } else {
                                excelLog(Row, 0, String.valueOf(exportQuotationList.get(Row - 1).getQuotationUniqueNo()));
                                excelLog(Row, 1, exportQuotationList.get(Row - 1).getQuotationNo());
                                excelLog(Row, 2, String.valueOf(exportQuotationList.get(Row - 1).getWoodSpieceCode()));
                                excelLog(Row, 3, String.valueOf(exportQuotationList.get(Row - 1).getDiameterRange()));
                                excelLog(Row, 4, String.valueOf(exportQuotationList.get(Row - 1).getQutTotalCBM()));
                            }
                        }
                    } else if (i == 2) {
                        int index = myWorkBook.getSheetIndex(Common.ExportListExcelExportList[i]);
                        if (index == -1) {
                            mySheet = myWorkBook.createSheet(Common.ExportListExcelExportList[i]);
                        } else {
                            mySheet = myWorkBook.getSheetAt(index);
                            for (int Del_Row = 1; Del_Row <= mySheet.getLastRowNum(); Del_Row++) {
                                Row row = mySheet.getRow(Del_Row);
                                deleteRow(mySheet, row);
                            }
                        }

                        ArrayList<ContainerListInputModel> exportContainerListModels = mDBInternalHelper.getContainerListInput(QuotationNum, exportId);


                        for (int Row = 0; Row < exportContainerListModels.size() + 1; Row++) {
                            if (Row == 0) {
                                excelLog(Row, 0, "ContainerNo");
                                excelLog(Row, 1, "QuotationNo");
                                excelLog(Row, 2, "Volume");
                                excelLog(Row, 3, "TotalCount");
                            } else {
                                excelLog(Row, 0, String.valueOf(exportContainerListModels.get(Row - 1).getContainerNo()));
                                excelLog(Row, 1, String.valueOf(exportContainerListModels.get(Row - 1).getQuotationNo()));
                                excelLog(Row, 2, String.valueOf(exportContainerListModels.get(Row - 1).getTotalVolume()));
                                excelLog(Row, 3, String.valueOf(exportContainerListModels.get(Row - 1).getTotalLogCount()));
                            }
                        }
                    } else if (i == 3) {
                        int index = myWorkBook.getSheetIndex(Common.ExportListExcelExportList[i]);
                        if (index == -1) {
                            mySheet = myWorkBook.createSheet(Common.ExportListExcelExportList[i]);
                        } else {
                            mySheet = myWorkBook.getSheetAt(index);
                            for (int Del_Row = 1; Del_Row <= mySheet.getLastRowNum(); Del_Row++) {
                                Row row = mySheet.getRow(Del_Row);
                                deleteRow(mySheet, row);
                            }
                        }

                        ArrayList<BarcodeDetailsInputModel> barcodeDetailsInput = mDBInternalHelper.getExportDetailsListInput(exportId, "ALL");
                        for (int Row = 0; Row < barcodeDetailsInput.size() + 1; Row++) {
                            if (Row == 0) {
                                excelLog(Row, 0, "QuotationNo");
                                excelLog(Row, 1, "QuotationUniqueNo");
                                excelLog(Row, 2, "ContainerNo");
                                excelLog(Row, 3, "SbbLabel");
                                excelLog(Row, 4, "Barcode");
                                excelLog(Row, 5, "WoodspieceCode");
                                excelLog(Row, 6, "PVNo");
                                excelLog(Row, 7, "DF1");
                                excelLog(Row, 8, "DF2");
                                excelLog(Row, 9, "DT1");
                                excelLog(Row, 10, "DT2");
                                excelLog(Row, 11, "Length");
                                excelLog(Row, 12, "NoteT");
                                excelLog(Row, 13, "NoteF");
                                excelLog(Row, 14, "NoteL");
                                excelLog(Row, 15, "AgeOflog");
                                excelLog(Row, 16, "LocationID");
                                excelLog(Row, 17, "WoodspieceID");
                                excelLog(Row, 18, "EntryModeId");
                                excelLog(Row, 19, "IsvalidPvNo");
                                excelLog(Row, 20, "IsValidVolume");
                                excelLog(Row, 21, "Volume");

                            } else {
                                excelLog(Row, 0, String.valueOf(barcodeDetailsInput.get(Row - 1).getQuotationNo()));
                                excelLog(Row, 1, String.valueOf(barcodeDetailsInput.get(Row - 1).getQuotationUniqueNo()));
                                excelLog(Row, 2, String.valueOf(barcodeDetailsInput.get(Row - 1).getContainerNo()));
                                excelLog(Row, 3, String.valueOf(barcodeDetailsInput.get(Row - 1).getSbbLabel()));
                                excelLog(Row, 4, String.valueOf(barcodeDetailsInput.get(Row - 1).getBarcode()));
                                excelLog(Row, 5, String.valueOf(barcodeDetailsInput.get(Row - 1).getWoodspieceCode()));
                                excelLog(Row, 6, String.valueOf(barcodeDetailsInput.get(Row - 1).getPVNo()));
                                excelLog(Row, 7, String.valueOf(barcodeDetailsInput.get(Row - 1).getDF1()));
                                excelLog(Row, 8, String.valueOf(barcodeDetailsInput.get(Row - 1).getDF2()));
                                excelLog(Row, 9, String.valueOf(barcodeDetailsInput.get(Row - 1).getDT1()));
                                excelLog(Row, 10, String.valueOf(barcodeDetailsInput.get(Row - 1).getDT2()));
                                excelLog(Row, 11, String.valueOf(barcodeDetailsInput.get(Row - 1).getLength()));
                                excelLog(Row, 12, String.valueOf(barcodeDetailsInput.get(Row - 1).getNoteT()));
                                excelLog(Row, 13, String.valueOf(barcodeDetailsInput.get(Row - 1).getNoteF()));
                                excelLog(Row, 14, String.valueOf(barcodeDetailsInput.get(Row - 1).getNoteL()));
                                excelLog(Row, 15, String.valueOf(barcodeDetailsInput.get(Row - 1).getAgeOflog()));
                                excelLog(Row, 16, String.valueOf(barcodeDetailsInput.get(Row - 1).getLocationID()));
                                excelLog(Row, 17, String.valueOf(barcodeDetailsInput.get(Row - 1).getWoodspieceID()));
                                excelLog(Row, 18, String.valueOf(barcodeDetailsInput.get(Row - 1).getEntryModeId()));
                                excelLog(Row, 19, String.valueOf(barcodeDetailsInput.get(Row - 1).isIsvalidPvNo()));
                                excelLog(Row, 20, String.valueOf(barcodeDetailsInput.get(Row - 1).isValidVolume()));
                                excelLog(Row, 21, String.valueOf(barcodeDetailsInput.get(Row - 1).getVolume()));

                            }
                        }
                    }
                }
                FileOutputStream out = new FileOutputStream(dest);
                myWorkBook.write(out);
                out.close();
                Toast.makeText(this, "Saved Sucessfully - " + directory_path, Toast.LENGTH_LONG).show();

            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
                AlertDialogBox("Export DB", ex.toString(), false);
            }

        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
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

    public void getExportGrid() {
        try {
            exportCodeList = new ArrayList<>();
            if (exportlist_swipeLayout.isRefreshing() == false) {
                ProgressBarLay.setVisibility(View.VISIBLE);
            }
            Common.Export.AllExportDetails.clear();
            exportListModel = new ExportListModel();
            ExportSync = ApiClient.getApiInterface();
            ExportSync.getExportList().enqueue(new Callback<ExportListModel>() {
                @Override
                public void onResponse(Call<ExportListModel> call, Response<ExportListModel> response) {
                    try {
                        ProgressBarLay.setVisibility(View.GONE);
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.isSuccessful()) {
                                exportCodeList.addAll(response.body().getExportList());
                                if (exportCodeList.size() > 0) {
                                    ExportCodeList(exportCodeList);
                                } else {
                                    AlertDialogBox(CommonMessage(R.string.ExportHead), "Export number not found", false);
                                }
                            } else {
                                AlertDialogBox(CommonMessage(R.string.ExportHead), response.message(), false);
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ExportHead), response.message(), false);
                        }
                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        AlertDialogBox(CommonMessage(R.string.ExportHead), ex.getMessage(), false);
                    }
                }

                @Override
                public void onFailure(Call<ExportListModel> call, Throwable t) {
                    ProgressBarLay.setVisibility(View.GONE);
                    AlertDialogBox(CommonMessage(R.string.ExportHead), t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            ProgressBarLay.setVisibility(View.GONE);
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox(CommonMessage(R.string.ExportHead), ex.toString(), false);
        }
    }

    public void ExportCodeList(ArrayList<ExportCodeModel> exportQuotationList) {
        try {
            ArrayList<String> exportQuotNumber = new ArrayList<>();
            for (int i = 0; i < exportQuotationList.size(); i++) {
                exportQuotNumber.add(exportQuotationList.get(i).getExportCode());
            }
            exportCodeAdapter = new ExportCodeListAdapter(ExportListActivity.this, exportQuotNumber);
            exportCodeSpin.setAdapter(exportCodeAdapter);
            // set a GridLayoutManager with default vertical orientation and 2 number of columns
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
            ExportList.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
            //  call the constructor of CustomAdapter to send the reference and data to Adapter
            ExportNumGrideAdapter exportNumGrideAdapter = new ExportNumGrideAdapter(ExportListActivity.this, exportQuotNumber);
            ExportList.setAdapter(exportNumGrideAdapter); // set the Adapter to RecyclerView
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }


    public class ExportNumGrideAdapter extends RecyclerView.Adapter<QuotationGrideHolder> {

        private Context mContext;
        private List<String> exportExportNumber;

        public ExportNumGrideAdapter(Context mContext, List<String> exportExportNumber) {
            this.mContext = mContext;
            this.exportExportNumber = exportExportNumber;
        }

        @Override
        public QuotationGrideHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exportlist_exportnum_grid, parent, false);
            return new QuotationGrideHolder(mView);
        }

        public String getItem(int position) {
            return exportExportNumber.get(position);
        }

        @Override
        public void onBindViewHolder(final QuotationGrideHolder holder, int position) {
            holder.mExportNumTxT.setText(exportExportNumber.get(position));

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
            int color = generator.getColor(getItem(position));
            //holder.cardView.setBackgroundColor(color);

            if (position % 2 == 0) {
                holder.cardView.setBackgroundColor(color);
            } else {
                holder.cardView.setBackgroundColor(color);
            }
            holder.cardView.setOnClickListener(v -> {
                Common.ExportID = exportCodeList.get(position).getExportId();
                Common.ExportCode = exportCodeList.get(position).getExportCode();
                Common.Order_Number = String.valueOf(exportCodeList.get(position).getOrderId());
                Common.Export_ContainerCount = String.valueOf(exportCodeList.get(position).getContainerQuantity());
                Common.BookingNumber = String.valueOf(exportCodeList.get(position).getBookingNumber());
                Common.StuffingDate = String.valueOf(exportCodeList.get(position).getStuffingDate());
                Common.CuttOffDateTime = String.valueOf(exportCodeList.get(position).getCuttOffDateTime());
                Common.ShippingAgentId = String.valueOf(exportCodeList.get(position).getShippingAgentId());
                try {
                    if (exportCodeList.size() > 0) {
                        if (checkConnection() == true) {
                            GetOrderDetailsApi();
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.ExportHead), "Export number not found", false);
                    }
                } catch (Exception ex) {
                    CrashAnalytics.CrashReport(ex);
                }
            });

        }

        @Override
        public int getItemCount() {
            return exportExportNumber.size();
        }
    }

    class QuotationGrideHolder extends RecyclerView.ViewHolder {

        TextView mExportNumTxT;
        CardView cardView;

        QuotationGrideHolder(View itemView) {
            super(itemView);
            mExportNumTxT = itemView.findViewById(R.id.exportNumber);
            cardView = itemView.findViewById(R.id.cardview1);
        }
    }

}