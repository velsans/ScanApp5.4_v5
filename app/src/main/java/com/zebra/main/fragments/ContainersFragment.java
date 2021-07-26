package com.zebra.main.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;
import com.google.gson.GsonBuilder;
import com.zebra.R;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.api.ApiClient;
import com.zebra.main.api.ApiInterface;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.model.Export.AgencyNameModel;
import com.zebra.main.model.Export.ContainerDetailsMainModel;
import com.zebra.main.model.Export.ContainerDetailsModel;
import com.zebra.main.model.Export.ContainerTypeModel;
import com.zebra.main.model.Export.ContainersModel;
import com.zebra.main.model.Export.DriverDetailsModel;
import com.zebra.main.model.Export.TransportDetailsModel;
import com.zebra.main.model.Export.TruckLicensePlateNoDetailsModel;
import com.zebra.main.model.Export.UpdateContainerModel;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;
import com.zebra.utilities.GwwException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContainersFragment extends Fragment {

    View containerView;
    private RecyclerView ContainerRecyclerView;
    LinearLayoutManager horizontalLayoutManager;
    private InternalDataBaseHelperClass mDBInternalHelper;
    List<ContainersModel> containerModelList;
    ContainerReceiver contain_receiver;
    TextView containerTotScanLogsCountVolumeTxt, ContainerTotalScanedLogsCount, OrderNoTxt, ExportNoTxt, ContainersNoTxt, StuffStartTimeETxt,
            StuffEndTimeETxt, StuffingDateETxt, BookingNoETxt;
    EditText ClosedByETxt, AgentSealNoETxt, CheckedByETxt, CustomsSealNoETxt, CustomsOfficerETxt, ExcavatorETxt, SBBOfficerETxt,
            LoaderETxt, LVVOfficerETxt, PointerNameETxt, WiredSealETxT, LabelSealETxT, SealKitPackageNumberETxt, ContainerSerailNoETxT,
            MaxGrossWeightContainerETxt, MaxPayloadContainerEETxt, VerifiedGrsMassContinerETxt;
    ContainerFragmentAdapter adapter;
    AlertDialogManager alert = new AlertDialogManager();
    AlertDialog.Builder Removebuilder = null;
    AlertDialog RemoveAlert = null;
    LinearLayout containerLayout;
    ApiInterface ContaierExport = null;
    ContainerDetailsMainModel containerDetModel;
    ArrayList<ContainerDetailsModel> ContainerDetailsList = new ArrayList<>();
    ArrayList<AgencyNameModel> AgencyNameList = new ArrayList<>();
    ArrayList<TruckLicensePlateNoDetailsModel> TruckLicensePlateNoList = new ArrayList<>();
    ArrayList<DriverDetailsModel> DriverNameList = new ArrayList<>();
    ArrayList<ContainerTypeModel> ContainerTypeList = new ArrayList<>();
    TransportDetailsModel transPortModel;
    Spinner AgencySpin, TruckPlateSpin, DriverSpin, ContainerSpin, ContainerTypeSpin;
    AutoCompleteTextView AgencyAuto, TruckPlateAuto, DriverAuto;
    int AgencyID, TruckID, DriverID, ContainerTypeID;
    String AgencyName, TruckName, DriverName;
    AutoCompletedadapter StringtreeNoadapter;
    ArrayList<String> TruckPlateArray = new ArrayList<>();
    ArrayList<String> DriverArray = new ArrayList<>();
    ArrayList<String> AgencyNameArray = new ArrayList<>();
    /* ArrayList<Integer> AgencyIDArray = new ArrayList<>();
     ArrayList<Integer> TruckPlateIDArray = new ArrayList<>();
     ArrayList<Integer> DriverIDArray = new ArrayList<>();*/
    boolean ContainerTypeIdFlag = false;
    HashMap<Integer, String> AgencyNameMapList = new HashMap<>();
    HashMap<Integer, String> TruckLicensePlateNoMapList = new HashMap<>();
    HashMap<Integer, String> DriverNameMapList = new HashMap<>();


    private class ContainerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //ContainerList();
            ContainerListAdapter();
            showLogsCountVolume();
            Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        contain_receiver = new ContainerReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(contain_receiver,
                new IntentFilter("CONTAINERS_REFRESH"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(contain_receiver);
    }

    public ContainersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_container, container, false);
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mDBInternalHelper = new InternalDataBaseHelperClass(getActivity());
        ContaierExport = ApiClient.getInstance().getUserService();
        containerModelList = new ArrayList<ContainersModel>();
        Initilization();
        ContainerListAdapter();
        showLogsCountVolume();
        GetTransportDetails();
        return containerView;
    }

    private void OnItemSelectListener() {
        ContainerTypeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ContainerTypeIdFlag = true) {
                    ContainerTypeIdFlag = false;
                    ContainerTypeID = ContainerTypeList.get(position).getContainerTypeId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*AutoComplete*/
        AgencyAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AgencyName = (String) parent.getItemAtPosition(position);
                AgencyID = (Integer) getKeyFromValue(AgencyNameMapList, AgencyName);
                //AgencyID = AgencyNameList.get(position).getAgencyId();
            }
        });
        TruckPlateAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TruckName = (String) parent.getItemAtPosition(position);
                TruckID = (Integer) getKeyFromValue(TruckLicensePlateNoMapList, TruckName);
                //TruckID = TruckLicensePlateNoList.get(position).getTruckId();
            }
        });
        DriverAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DriverName = (String) parent.getItemAtPosition(position);
                DriverID = (Integer) getKeyFromValue(DriverNameMapList, DriverName);
                //DriverID = DriverNameList.get(position).getTruckDriverId();
            }
        });
        ContainerSerailNoETxT.addTextChangedListener(new TextWatcher() {
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
                backSpace = lastLength > editable.toString().trim().length();
                if (editable.toString().trim().length() > 0) {
                    if (backSpace) {
                        if (!isValidContainerSealNo(editable.toString().trim())) {
                            ContainerSerailNoETxT.setError(CommonMessage(R.string.containerNOFormatMsg));
                        }
                    } else {
                        //if (editable.toString().trim().length() > 0) {
                        if (editable.toString().trim().length() == 4) {
                            ContainerSerailNoETxT.append(" ");
                        }
                        if (editable.toString().trim().length() == 11) {
                            ContainerSerailNoETxT.append("-");
                        }
                        if (editable.toString().trim().length() == Common.ContainerSealNolenght) {
                            if (!isValidContainerSealNo(editable.toString().trim())) {
                                ContainerSerailNoETxT.setError(CommonMessage(R.string.containerNOFormatMsg));
                            }
                        } else {
                            ContainerSerailNoETxT.setError(CommonMessage(R.string.containerNOFormatMsg));
                        }
                    }
                }
            }
        });
    }

    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    private void showLogsCountVolume() {
        containerTotScanLogsCountVolumeTxt.setText(String.valueOf(Common.decimalFormat.format(Common.Export.TotalCBMDetailsList.get(0).getScannedVolum())));
        int totoalLogCount = 0;
        for (ContainersModel containersModel : Common.Export.ContainerList) {
            totoalLogCount = totoalLogCount + containersModel.getLogCount();
        }
        ContainerTotalScanedLogsCount.setText(String.valueOf(totoalLogCount));
    }

    public void Initilization() {
        ContainerRecyclerView = containerView.findViewById(R.id.RV_ContainerFrg);
        containerTotScanLogsCountVolumeTxt = containerView.findViewById(R.id.ContainerTotScanLogsCountVolumeTxt);
        ContainerTotalScanedLogsCount = containerView.findViewById(R.id.ContainerTotalScanLogs);
        containerView.findViewById(R.id.add_containerIMG).setOnClickListener(ContainerOnclickListener);
        containerLayout = containerView.findViewById(R.id.containerDetailsLAYOUT);
        containerView.findViewById(R.id.updateContainerDetails).setOnClickListener(ContainerOnclickListener);
        containerView.findViewById(R.id.cancelContainerDetails).setOnClickListener(ContainerOnclickListener);

        AgencySpin = containerView.findViewById(R.id.cd_transportAgencySpin);
        TruckPlateSpin = containerView.findViewById(R.id.cd_transportTruckPlateSpin);
        DriverSpin = containerView.findViewById(R.id.cd_transportDriverSpin);
        ContainerSpin = containerView.findViewById(R.id.cd_containersSpin);

        OrderNoTxt = containerView.findViewById(R.id.OrderNoTxT);
        ExportNoTxt = containerView.findViewById(R.id.ExportNoTxT);
        MaxGrossWeightContainerETxt = containerView.findViewById(R.id.MaxGrossWeightContainerETxT);
        ClosedByETxt = containerView.findViewById(R.id.ClosedByETxT);
        AgentSealNoETxt = containerView.findViewById(R.id.AgentSealNoETxT);
        MaxPayloadContainerEETxt = containerView.findViewById(R.id.MaxPayloadContainerEETxT);
        CheckedByETxt = containerView.findViewById(R.id.CheckedByETxT);
        CustomsSealNoETxt = containerView.findViewById(R.id.CustomsSealNoETxT);
        VerifiedGrsMassContinerETxt = containerView.findViewById(R.id.VerifiedGrsMassContinerETxT);
        CustomsOfficerETxt = containerView.findViewById(R.id.CustomsOfficerETxT);
        BookingNoETxt = containerView.findViewById(R.id.BookingNoETxT);
        StuffingDateETxt = containerView.findViewById(R.id.StuffingDateETxT);
        ExcavatorETxt = containerView.findViewById(R.id.ExcavatorETxT);
        SBBOfficerETxt = containerView.findViewById(R.id.SBBOfficerETxT);
        StuffStartTimeETxt = containerView.findViewById(R.id.StuffStartTimeETxT);
        StuffEndTimeETxt = containerView.findViewById(R.id.StuffEndTimeETxT);
        LoaderETxt = containerView.findViewById(R.id.LoaderETxT);
        LVVOfficerETxt = containerView.findViewById(R.id.LVVOfficerETxT);
        PointerNameETxt = containerView.findViewById(R.id.PointerNameETxT);
        ContainerTypeSpin = containerView.findViewById(R.id.cd_containerTypeSpin);
        ContainersNoTxt = containerView.findViewById(R.id.cd_containersNo);


        containerView.findViewById(R.id.StuffStartTimeETxT).setOnClickListener(ContainerOnclickListener);
        containerView.findViewById(R.id.StuffingDateETxT).setOnClickListener(ContainerOnclickListener);
        containerView.findViewById(R.id.StuffEndTimeETxT).setOnClickListener(ContainerOnclickListener);

        WiredSealETxT = containerView.findViewById(R.id.WiredSealETxT);
        LabelSealETxT = containerView.findViewById(R.id.LabelSeal2ETxT);
        SealKitPackageNumberETxt = containerView.findViewById(R.id.SealKitPackageNumberETxT);
        ContainerSerailNoETxT = containerView.findViewById(R.id.containerSealNoETxT);

        AgencyAuto = containerView.findViewById(R.id.cd_transportAgencyAuto);
        TruckPlateAuto = containerView.findViewById(R.id.cd_transportTruckPlateAuto);
        DriverAuto = containerView.findViewById(R.id.cd_transportDriverAuto);

        containerView.findViewById(R.id.StuffingDateELAY).setOnClickListener(ContainerOnclickListener);
        containerView.findViewById(R.id.StuffStartTimeELAY).setOnClickListener(ContainerOnclickListener);
        containerView.findViewById(R.id.StuffEndTimeELAY).setOnClickListener(ContainerOnclickListener);
        containerView.findViewById(R.id.SecuritySeal1ELAY).setOnClickListener(ContainerOnclickListener);
        containerView.findViewById(R.id.SecuritySeal2ELAY).setOnClickListener(ContainerOnclickListener);

        containerView.findViewById(R.id.cd_transportAgencyAuto).setOnTouchListener(ContainerOnTouchListener);
        containerView.findViewById(R.id.cd_transportTruckPlateAuto).setOnTouchListener(ContainerOnTouchListener);
        containerView.findViewById(R.id.cd_transportDriverAuto).setOnTouchListener(ContainerOnTouchListener);

    }

    View.OnTouchListener ContainerOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                switch (v.getId()) {
                    case R.id.cd_transportAgencyAuto:
                        AgencyAuto.requestFocus();
                        AgencyAuto.showDropDown();
                        break;
                    case R.id.cd_transportTruckPlateAuto:
                        TruckPlateAuto.requestFocus();
                        TruckPlateAuto.showDropDown();
                        break;
                    case R.id.cd_transportDriverAuto:
                        DriverAuto.requestFocus();
                        DriverAuto.showDropDown();
                        break;
                    case R.id.cd_containerTypeSpin:
                        ContainerTypeIdFlag = true;
                        break;
                }
            } catch (Exception ex) {

            }
            return false;
        }
    };

    View.OnClickListener ContainerOnclickListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                    case R.id.add_containerIMG:
                        /*Get Container Count*/
                        ContainerListAdapter();
                        AlertDialogBox("Add Container", "Container Added Successfully", true);
                        break;
                    case R.id.updateContainerDetails:
                        updateContainerDetailsApi();
                        break;
                    case R.id.cancelContainerDetails:
                        containerLayout.setVisibility(View.GONE);
                        break;
                    case R.id.StuffingDateETxT:
                        StuffingdatePicker();
                        break;
                    case R.id.StuffStartTimeETxT:
                        StuffingTimePicker(true);
                        break;
                    case R.id.StuffEndTimeETxT:
                        StuffingTimePicker(false);
                        break;
                    case R.id.StuffingDateELAY:
                        StuffingdatePicker();
                        break;
                    case R.id.StuffStartTimeELAY:
                        StuffingTimePicker(true);
                        break;
                    case R.id.StuffEndTimeELAY:
                        StuffingTimePicker(false);
                        break;
                    case R.id.SecuritySeal1ELAY:
                        StuffingTimePicker(false);
                        break;
                    case R.id.SecuritySeal2ELAY:
                        StuffingTimePicker(false);
                        break;
                }
            } catch (Exception ex) {

            }

        }
    };

    public class ContainerFragmentAdapter extends RecyclerView.Adapter<ContainerFragmentAdapter.ContainerFragmentViewHolder> {

        private Context mCtx;
        private List<ContainersModel> containerModelList;

        ContainerFragmentAdapter(Context mCtx, List<ContainersModel> containerModelList) {
            this.mCtx = mCtx;
            this.containerModelList = containerModelList;
        }

        public ContainersModel getItem(int position) {
            return containerModelList.get(position);
        }

        @NonNull
        @Override
        public ContainerFragmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mCtx).inflate(R.layout.fragment_container_inflator, parent, false);
            return new ContainerFragmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ContainerFragmentViewHolder holder, int position) {
            ContainersModel containerListModel = containerModelList.get(position);
            holder.TxTContainerNum.setText(containerListModel.getContainerNumber());
            holder.TxTLogCount.setText(String.valueOf(containerListModel.getLogCount()));
            holder.TotalVolumeTxT.setText(String.valueOf(Common.decimalFormat.format(containerListModel.getScannedCBM())));
            holder.TotalNetVolumeTxT.setText(String.valueOf(Common.decimalFormat.format(containerListModel.getGrossVolume())));
            /*ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
            int color = generator.getColor(getItem(position));
            holder.backgroundLayout.setBackgroundColor(color);*/

            if (position % 2 == 0) {
                holder.backgroundLayout.setBackgroundColor(mCtx.getResources().getColor(R.color.green));
            } else {
                holder.backgroundLayout.setBackgroundColor(mCtx.getResources().getColor(R.color.color_white));
            }
            if (containerListModel.getScannedCBM() > Common.ExportSingleContainerTotValue) {
                holder.backgroundLayout.setBackgroundColor(mCtx.getResources().getColor(R.color.highlightedColor));
            }
            //if Layout Background color yellow or not
            int backgroundColor = ((ColorDrawable) holder.backgroundLayout.getBackground()).getColor();
            if (backgroundColor == getResources().getColor(R.color.highlightedColor)) {
                holder.TxTContainerNum.setTextColor(getResources().getColor(R.color.red));
                holder.TxTLogCount.setTextColor(getResources().getColor(R.color.red));
                holder.TotalVolumeTxT.setTextColor(getResources().getColor(R.color.red));
            } else {
                holder.TxTContainerNum.setTextColor(getResources().getColor(R.color.color_black));
                holder.TxTLogCount.setTextColor(getResources().getColor(R.color.color_black));
                holder.TotalVolumeTxT.setTextColor(getResources().getColor(R.color.color_black));
            }
            if (containerListModel.getContainerId() == 0 || containerListModel.getContainerId() == -1) {
                holder.EditIMG.setVisibility(View.INVISIBLE);
            } else {
                holder.EditIMG.setVisibility(View.VISIBLE);

            }
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
            int color = generator.getColor(getItem(position));
            holder.Remove.setBackgroundColor(color);
            //holder.EditIMG.setBackgroundColor(color);
            holder.Remove.setOnClickListener(v -> RemoveMessage(CommonMessage(R.string.Remove_Message), position));
            holder.EditIMG.setOnClickListener(v -> {
                Common.ContainerID = containerListModel.getContainerId();
                Common.ContainerNo = containerListModel.getContainerNumber();
                GetContainerDetails();
            });
        }

        @Override
        public int getItemCount() {
            return containerModelList.size();
        }

        class ContainerFragmentViewHolder extends RecyclerView.ViewHolder {

            TextView TxTContainerNum, TxTLogCount, TotalVolumeTxT, TotalNetVolumeTxT;
            LinearLayout backgroundLayout;
            ImageView Remove, EditIMG;

            ContainerFragmentViewHolder(View itemView) {
                super(itemView);
                TxTContainerNum = itemView.findViewById(R.id.TxT_ContainerNum);
                TxTLogCount = itemView.findViewById(R.id.TxT_LogCount);
                TotalVolumeTxT = itemView.findViewById(R.id.TxT_TotalVolume);
                TotalNetVolumeTxT = itemView.findViewById(R.id.TxT_TotalNetVolume);
                backgroundLayout = itemView.findViewById(R.id.container_layout_background);
                Remove = itemView.findViewById(R.id.remove_containerIMG);
                EditIMG = itemView.findViewById(R.id.edit_containerIMG);
            }
        }
    }

    public void ContainerList() {
        try {
            hideKeyBoard((AppCompatActivity) getActivity());
            ContainerListAdapter();
        } catch (Exception ex) {
            Log.d("Exception : %s", ex.toString().trim());
        }
    }

    public void ContainerListAdapter() {
        //Common.ContainerList = mDBInternalHelper.getContainerList(Common.QuotationNo, Common.ExportID);
        if (Common.Export.ContainerList.size() > 0) {
            horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
            horizontalLayoutManager.setStackFromEnd(true);
            ContainerRecyclerView.setLayoutManager(horizontalLayoutManager);
            adapter = new ContainerFragmentAdapter(getActivity(), Common.Export.ContainerList);
            ContainerRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public static void hideKeyBoard(AppCompatActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(getActivity(), Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public void RemoveMessage(String ErrorMessage, int position) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(getActivity());
        Removebuilder.setMessage(ErrorMessage);
        Removebuilder.setCancelable(true);

        Removebuilder.setPositiveButton(CommonMessage(R.string.action_Remove),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                        } catch (Exception e) {

                        }
                        dialog.cancel();
                    }
                });
        Removebuilder.setNegativeButton(CommonMessage(R.string.action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public String CommonMessage(int Common_Msg) {
        return this.getResources().getString(Common_Msg);
    }

    public void GetContainerDetails() {
        try {
            ContinerDetailsClear();
            //Scanned Result Refresh
            containerDetModel = new ContainerDetailsMainModel();
            containerDetModel.setExportId(Common.ExportID);
            containerDetModel.setContainerId(Common.ContainerID);

            Log.e("GsonBuilder", ">>>>>>>" + new GsonBuilder().create().toJson(containerDetModel));
            ContaierExport = ApiClient.getApiInterface();
            ContaierExport.GetContainerDetails(containerDetModel).enqueue(new Callback<ContainerDetailsMainModel>() {
                @Override
                public void onResponse(Call<ContainerDetailsMainModel> call, Response<ContainerDetailsMainModel> response) {
                    if (GwwException.GwwException(response.code()) == true) {
                        if (response.isSuccessful()) {
                            ContainerDetailsList.clear();
                            ContainerDetailsList = response.body().getContainerDetails();
                            if (ContainerDetailsList.size() > 0) {
                                //if (ContainerDetailsList.get(0).getUpdateStatus() == 1) {
                                ContainerDetailsLayout();
                                containerLayout.setVisibility(View.VISIBLE);
                            } else {
                                //AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), ContainerDetailsList.get(0).getMessage(), false);
                                AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), response.body().toString(), false);
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), "#" + Common.Export_Sbblabel + "-" + response.message(), false);
                        }
                    } else {
                        Common.AuthorizationFlag = true;
                        AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), response.message(), false);
                    }
                }

                @Override
                public void onFailure(Call<ContainerDetailsMainModel> call, Throwable t) {
                    AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), t.getMessage(), false);
                }
            });

        } catch (Exception ex) {
            AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), ex.toString().trim(), false);
        }
    }

    public void GetTransportDetails() {
        try {
            AgencyNameList.clear();
            TruckLicensePlateNoList.clear();
            DriverNameList.clear();
            ContainerTypeList.clear();
            transPortModel = new TransportDetailsModel();
            ContaierExport = ApiClient.getApiInterface();
            ContaierExport.GetTransportDetails().enqueue(new Callback<TransportDetailsModel>() {
                @Override
                public void onResponse(Call<TransportDetailsModel> call, Response<TransportDetailsModel> response) {
                    try {
                        if (GwwException.GwwException(response.code()) == true) {
                            if (response.isSuccessful()) {
                                AgencyNameList = response.body().getAgencyName();
                                TruckLicensePlateNoList = response.body().getTruckLicensePlateNo();
                                DriverNameList = response.body().getDriverName();
                                ContainerTypeList = response.body().getContainerType();
                                TransportDetails();
                            } else {
                                AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), response.message(), false);
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), response.message(), false);
                        }
                    } catch (Exception e) {
                        AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), e.getMessage(), false);
                    }
                }

                @Override
                public void onFailure(Call<TransportDetailsModel> call, Throwable t) {
                    AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), ex.toString().trim(), false);
        }
    }

    public void TransportDetails() {
        try {
            if (Common.Export.ContainerList.size() > 0) {
                ContainersAdapter containerAdapter = new ContainersAdapter(getActivity(), Common.Export.ContainerList);
                ContainerSpin.setAdapter(containerAdapter);
            }
            if (AgencyNameList.size() > 0) {
                for (int i = 0; i < AgencyNameList.size(); i++) {
                    AgencyNameArray.add(AgencyNameList.get(i).getAgencyName());
                    //AgencyIDArray.add(AgencyNameList.get(i).getAgencyId());
                    AgencyNameMapList.put(AgencyNameList.get(i).getAgencyId(), AgencyNameList.get(i).getAgencyName());
                }
                StringtreeNoadapter = new AutoCompletedadapter(getActivity(), android.R.layout.simple_dropdown_item_1line, AgencyNameArray);
                StringtreeNoadapter.notifyDataSetChanged();
                AgencyAuto.setAdapter(StringtreeNoadapter);
            }
            if (TruckLicensePlateNoList.size() > 0) {
                for (int i = 0; i < TruckLicensePlateNoList.size(); i++) {
                    TruckPlateArray.add(TruckLicensePlateNoList.get(i).getTruckLicensePlateNo());
                    //TruckPlateIDArray.add(TruckLicensePlateNoList.get(i).getTruckId());
                    TruckLicensePlateNoMapList.put(TruckLicensePlateNoList.get(i).getTruckId(), TruckLicensePlateNoList.get(i).getTruckLicensePlateNo());
                }
                StringtreeNoadapter = new AutoCompletedadapter(getActivity(), android.R.layout.simple_dropdown_item_1line, TruckPlateArray);
                StringtreeNoadapter.notifyDataSetChanged();
                TruckPlateAuto.setAdapter(StringtreeNoadapter);
            }
            if (DriverNameList.size() > 0) {
                for (int i = 0; i < DriverNameList.size(); i++) {
                    DriverArray.add(DriverNameList.get(i).getDriverName());
                    //DriverIDArray.add(DriverNameList.get(i).getTruckDriverId());
                    DriverNameMapList.put(DriverNameList.get(i).getTruckDriverId(), DriverNameList.get(i).getDriverName());
                }
                StringtreeNoadapter = new AutoCompletedadapter(getActivity(), android.R.layout.simple_dropdown_item_1line, DriverArray);
                StringtreeNoadapter.notifyDataSetChanged();
                DriverAuto.setAdapter(StringtreeNoadapter);
            }
            if (ContainerTypeList.size() > 0) {
                ContainerTypeAdapter containerTypeAdapter = new ContainerTypeAdapter(getActivity(), ContainerTypeList);
                ContainerTypeSpin.setAdapter(containerTypeAdapter);
            }
            OnItemSelectListener();
        } catch (Exception ex) {

        }
    }

    public void ContinerDetailsClear() {
        OrderNoTxt.setText(" ");
        ExportNoTxt.setText(" ");
        ContainersNoTxt.setText(" ");
        MaxGrossWeightContainerETxt.setText(" ");
        ClosedByETxt.setText(" ");
        AgentSealNoETxt.setText(" ");
        MaxPayloadContainerEETxt.setText(" ");
        CheckedByETxt.setText(" ");
        CustomsSealNoETxt.setText(" ");
        VerifiedGrsMassContinerETxt.setText(" ");
        CustomsOfficerETxt.setText(" ");
        BookingNoETxt.setText(" ");
        StuffingDateETxt.setText(" ");
        ExcavatorETxt.setText(" ");
        SBBOfficerETxt.setText(" ");
        StuffStartTimeETxt.setText(" ");
        StuffEndTimeETxt.setText(" ");
        LoaderETxt.setText(" ");
        LVVOfficerETxt.setText(" ");
        PointerNameETxt.setText(" ");
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getCurrentTime() {
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);
        return String.valueOf(hour + ":" + minute);
    }

    public void ContainerDetailsLayout() {
        try {
            OrderNoTxt.setText(Common.Order_Number);
            ExportNoTxt.setText(Common.ExportCode);
            ContainersNoTxt.setText(Common.ContainerNo);
            BookingNoETxt.setText(Common.BookingNumber);
            if (ContainerDetailsList.size() > 0) {
                ContainerDetailsModel detailsModel = ContainerDetailsList.get(0);

                MaxGrossWeightContainerETxt.setText(String.valueOf(detailsModel.getMaxGrossweight()));
                ClosedByETxt.setText(detailsModel.getClosedBy());
                AgentSealNoETxt.setText(detailsModel.getAgencySealNo());
                MaxPayloadContainerEETxt.setText(String.valueOf(detailsModel.getMaxPayLoad()));
                CheckedByETxt.setText(detailsModel.getCheckedBy());
                CustomsSealNoETxt.setText(detailsModel.getCustomSearNo());
                ContainerSerailNoETxT.setText(detailsModel.getContainerSerialNo());
                VerifiedGrsMassContinerETxt.setText(String.valueOf(detailsModel.getVerifiedGrossWeight()));
                CustomsOfficerETxt.setText(detailsModel.getCustomerOfficer());
                ExcavatorETxt.setText(detailsModel.getExcavator());
                SBBOfficerETxt.setText(detailsModel.getSBBOfficer());
                StuffingDateETxt.setText(getDateTime());
                if (isNullOrEmpty(detailsModel.getStartTime())) {
                    StuffingDateETxt.setText(getDateTime());
                } else {
                    StuffingDateETxt.setText(detailsModel.getStuffingDate());
                }
                if (isNullOrEmpty(detailsModel.getStartTime())) {
                    StuffStartTimeETxt.setText(getCurrentTime());
                } else {
                    StuffStartTimeETxt.setText(detailsModel.getStartTime());
                }
                if (isNullOrEmpty(detailsModel.getEndTime())) {
                    StuffEndTimeETxt.setText(getCurrentTime());
                } else {
                    StuffEndTimeETxt.setText(detailsModel.getEndTime());
                }
                LoaderETxt.setText(detailsModel.getLoader());
                LVVOfficerETxt.setText(detailsModel.getLVVOfficer());
                PointerNameETxt.setText(detailsModel.getPointerName());
                WiredSealETxT.setText(detailsModel.getSecuritySeal1());
                LabelSealETxT.setText(detailsModel.getSecuritySeal2());
                SealKitPackageNumberETxt.setText(detailsModel.getSealKitPackageNumber());

                if (AgencyNameMapList.containsKey(detailsModel.getAgencyId())) {
                    AgencyID = detailsModel.getAgencyId();
                    AgencyAuto.setText(AgencyNameMapList.get(detailsModel.getAgencyId()));
                } else {
                    AgencyID = 0;
                    AgencyAuto.setText("");
                }
                if (TruckLicensePlateNoMapList.containsKey(detailsModel.getTruckId())) {
                    TruckID = detailsModel.getTruckId();
                    TruckPlateAuto.setText(TruckLicensePlateNoMapList.get(detailsModel.getTruckId()));
                } else {
                    TruckID = 0;
                    TruckPlateAuto.setText("");
                }
                if (DriverNameMapList.containsKey(detailsModel.getTruckDriverId())) {
                    DriverID = detailsModel.getTruckDriverId();
                    DriverAuto.setText(DriverNameMapList.get(detailsModel.getTruckDriverId()));
                } else {
                    DriverID = 0;
                    DriverAuto.setText("");
                }
                for (int i = 0; i < ContainerTypeList.size(); i++) {
                    if (ContainerTypeList.get(i).getContainerTypeId() == detailsModel.getContainerTypeId()) {
                        ContainerTypeID = detailsModel.getContainerTypeId();
                        ContainerTypeSpin.setSelection(i);
                    }
                }
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public class ContainersAdapter extends BaseAdapter {
        Context context;
        ArrayList<ContainersModel> locname;
        LayoutInflater inflter;

        public ContainersAdapter(Context applicationContext, ArrayList<ContainersModel> list) {
            this.context = applicationContext;
            this.locname = list;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return locname.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.containerdetails_infliator, null);

            TextView fromname = view.findViewById(R.id.txt_continerDetails);
            fromname.setText(locname.get(i).getContainerNumber());
            return view;
        }
    }

    public class AgencyNameAdapter extends BaseAdapter {
        Context context;
        ArrayList<AgencyNameModel> locname;
        LayoutInflater inflter;

        public AgencyNameAdapter(Context applicationContext, ArrayList<AgencyNameModel> list) {
            this.context = applicationContext;
            this.locname = list;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return locname.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.containerdetails_infliator, null);

            TextView fromname = view.findViewById(R.id.txt_continerDetails);
            fromname.setText(locname.get(i).getAgencyName());
            return view;
        }
    }

    public class TruckLicensePlateAdapter extends BaseAdapter {
        Context context;
        ArrayList<TruckLicensePlateNoDetailsModel> locname;
        LayoutInflater inflter;

        public TruckLicensePlateAdapter(Context applicationContext, ArrayList<TruckLicensePlateNoDetailsModel> list) {
            this.context = applicationContext;
            this.locname = list;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return locname.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.containerdetails_infliator, null);

            TextView fromname = view.findViewById(R.id.txt_continerDetails);
            fromname.setText(locname.get(i).getTruckLicensePlateNo());
            return view;
        }
    }

    public class DriverNameAdapter extends BaseAdapter {
        Context context;
        ArrayList<DriverDetailsModel> locname;
        LayoutInflater inflter;

        public DriverNameAdapter(Context applicationContext, ArrayList<DriverDetailsModel> list) {
            this.context = applicationContext;
            this.locname = list;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return locname.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.containerdetails_infliator, null);

            TextView fromname = view.findViewById(R.id.txt_continerDetails);
            fromname.setText(locname.get(i).getDriverName());
            return view;
        }
    }

    public class ContainerTypeAdapter extends BaseAdapter {
        Context context;
        ArrayList<ContainerTypeModel> containerTypeModels;
        LayoutInflater inflter;

        public ContainerTypeAdapter(Context applicationContext, ArrayList<ContainerTypeModel> list) {
            this.context = applicationContext;
            this.containerTypeModels = list;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return containerTypeModels.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.containerdetails_infliator, null);

            TextView fromname = view.findViewById(R.id.txt_continerDetails);
            fromname.setText(containerTypeModels.get(i).getContainerType());
            return view;
        }

    }

    //Auto Completed
    public class AutoCompletedadapter extends ArrayAdapter<String> {

        public AutoCompletedadapter(Context context, int layout, ArrayList<String> from) {
            super(context, layout, from);

        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        InputMethodManager imm = (InputMethodManager) getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(
                                view.getApplicationWindowToken(), 0);
                    }
                    return false;
                }
            });
            return view;
        }
    }

    public void updateContainerDetailsApi() {
        try {
            UpdateContainerModel updateContainerModel = new UpdateContainerModel();
            if (isNullOrEmpty(ContainerSerailNoETxT.getText().toString().trim())) {
                updateContainerModel.setContainerSerialNo(isEditTextEmpty(ContainerSerailNoETxT.getText().toString().trim()));
            } else {
                if (!isValidContainerSealNo(ContainerSerailNoETxT.getText().toString().trim())) {
                    ContainerSerailNoETxT.setError(CommonMessage(R.string.containerNOFormatMsg));
                    AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), CommonMessage(R.string.containerNOFormatMsg), false);
                    return;
                } else {
                    updateContainerModel.setContainerSerialNo(isEditTextEmpty(ContainerSerailNoETxT.getText().toString().trim()));
                }
            }
            updateContainerModel.setOrderId(Integer.parseInt(OrderNoTxt.getText().toString().trim()));
            updateContainerModel.setExportId(Common.ExportID);
            updateContainerModel.setContainerId(Common.ContainerID);

            updateContainerModel.setAgentName(AgencyAuto.getText().toString().trim());
            updateContainerModel.setTruckPlateNo(TruckPlateAuto.getText().toString().trim());
            updateContainerModel.setDriverName(DriverAuto.getText().toString().trim());

            if (!isNullOrEmpty(AgencyAuto.getText().toString().trim())) {
                updateContainerModel.setAgentId(AgencyID);
            } else {
                AgencyID = 0;
                updateContainerModel.setAgentId(AgencyID);
            }
            if (!isNullOrEmpty(TruckPlateAuto.getText().toString().trim())) {
                updateContainerModel.setTruckId(TruckID);
            } else {
                TruckID = 0;
                updateContainerModel.setTruckId(TruckID);
            }
            if (!isNullOrEmpty(DriverAuto.getText().toString().trim())) {
                updateContainerModel.setTruckDriverId(DriverID);
            } else {
                DriverID = 0;
                updateContainerModel.setTruckDriverId(DriverID);
            }
            updateContainerModel.setExcavator(isEditTextEmpty(ExcavatorETxt.getText().toString().trim()));
            updateContainerModel.setLoader(isEditTextEmpty(LoaderETxt.getText().toString().trim()));
            updateContainerModel.setPointerName(isEditTextEmpty(PointerNameETxt.getText().toString().trim()));
            updateContainerModel.setClosedBy(isEditTextEmpty(ClosedByETxt.getText().toString().trim()));
            updateContainerModel.setCheckedBy(isEditTextEmpty(CheckedByETxt.getText().toString().trim()));
            updateContainerModel.setCustomerOfficer(isEditTextEmpty(CustomsOfficerETxt.getText().toString().trim()));
            updateContainerModel.setSBBOfficer(isEditTextEmpty(SBBOfficerETxt.getText().toString().trim()));
            updateContainerModel.setLVVOfficer(isEditTextEmpty(LVVOfficerETxt.getText().toString().trim()));
            updateContainerModel.setCustomSearNo(isEditTextEmpty(CustomsSealNoETxt.getText().toString().trim()));
            updateContainerModel.setAgencySealNo(isEditTextEmpty(AgentSealNoETxt.getText().toString().trim()));
            updateContainerModel.setContainerSerialNo(isEditTextEmpty(ContainerSerailNoETxT.getText().toString().trim()));
            updateContainerModel.setMaxGrossweight(isEditTextEmptyDouble(MaxGrossWeightContainerETxt.getText().toString().trim()));
            updateContainerModel.setMaxPayLoad(isEditTextEmptyDouble(MaxPayloadContainerEETxt.getText().toString().trim()));
            updateContainerModel.setVerifiedGrossWeight(isEditTextEmptyDouble(VerifiedGrsMassContinerETxt.getText().toString().trim()));
            updateContainerModel.setSecuritySeal1(isEditTextEmpty(WiredSealETxT.getText().toString().trim()));
            updateContainerModel.setSecuritySeal2(isEditTextEmpty(LabelSealETxT.getText().toString().trim()));
            updateContainerModel.setContainerTypeId(ContainerTypeID);
            updateContainerModel.setStuffingDate(StuffingDateETxt.getText().toString().trim());
            updateContainerModel.setStartTime(StuffStartTimeETxt.getText().toString().trim());
            updateContainerModel.setEndTime(StuffEndTimeETxt.getText().toString().trim());
            updateContainerModel.setSealKitPackageNumber(isEditTextEmpty(SealKitPackageNumberETxt.getText().toString().trim()));

            Log.e("GsonBuilder", ">>>>>>>" + new GsonBuilder().create().toJson(updateContainerModel));
            ContaierExport = ApiClient.getApiInterface();
            ContaierExport.getUpdateContainer(updateContainerModel).enqueue(new Callback<UpdateContainerModel>() {
                @Override
                public void onResponse(Call<UpdateContainerModel> call, Response<UpdateContainerModel> response) {
                    if (GwwException.GwwException(response.code()) == true) {
                        if (response.isSuccessful()) {
                            Common.StatusVerificationList.clear();
                            Common.StatusVerificationList = response.body().getUpdateStatus();
                            if (Common.StatusVerificationList.size() > 0) {
                                if (Common.StatusVerificationList.get(0).getStatus() == 1) {
                                    AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), Common.StatusVerificationList.get(0).getMessage(), true);
                                    containerLayout.setVisibility(View.GONE);
                                    hideKeyBoard((AppCompatActivity) getActivity());
                                    //GetTransportDetails();
                                }
                            } else {
                                AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), response.message(), false);
                            }
                        } else {
                            AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), response.message(), false);
                        }
                    } else {
                        Common.AuthorizationFlag = true;
                        AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), response.message(), false);
                    }
                }

                @Override
                public void onFailure(Call<UpdateContainerModel> call, Throwable t) {
                    AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), t.getMessage(), false);
                }
            });
        } catch (Exception ex) {
            AlertDialogBox(CommonMessage(R.string.ContainerDetailsHead), ex.toString().trim(), false);
        }
    }

    public void StuffingTimePicker(boolean startFlag) {
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    //myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    //myCalender.set(Calendar.MINUTE, minute);
                    String curTime = String.format("%02d:%02d", hourOfDay, minute);
                    if (startFlag == true) {
                        StuffStartTimeETxt.setText(curTime);
                    } else {
                        StuffEndTimeETxt.setText(curTime);
                    }
                }
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        timePickerDialog.setTitle("Choose hour:");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    private void StuffingdatePicker() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
// date picker dialog
        DatePickerDialog picker = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        StuffingDateETxt.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                        //StuffingDateETxt.setText(year + (monthOfYear + 1) + dayOfMonth);
                    }
                }, year, month, day);
        picker.show();
    }

    public static String isEditTextEmpty(String editTxT) {
        String Value = "";
        boolean dafaf = isNullOrEmpty(editTxT);
        if (isNullOrEmpty(editTxT)) {
            Value = " ";
        } else {
            Value = editTxT.trim();
        }
        return Value;
    }

    public static double isEditTextEmptyDouble(String editTxT) {
        double Value = 0.0;
        if (isNullOrEmpty(editTxT)) {
            Value = 0.0;
        } else {
            Value = Double.parseDouble(editTxT.trim());
        }
        return Value;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

    // BarCode
    private boolean isValidContainerSealNo(String barCode) {
        String BarCodeValidation =
                "[a-zA-Z]{4}" +
                        "\\s" +
                        "[0-9]{6}" +
                        "\\-" +
                        "[0-9]{1}";
        Pattern pattern = Pattern.compile(BarCodeValidation);
        Matcher matcher = pattern.matcher(barCode);
        return matcher.matches();
    }
}