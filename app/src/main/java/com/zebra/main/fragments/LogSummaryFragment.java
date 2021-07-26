package com.zebra.main.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.zebra.R;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.model.Export.LogSummaryModel;
import com.zebra.main.model.externaldb.WoodSpeciesModel;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LogSummaryFragment extends Fragment {

    View LogSummaryView;
    private RecyclerView LogSummaryInternalRV;
    LogSummaryInternalListAdapter LogSummaryInternalListAdapter;
    LogSummaryModel logSummarymodel;
    //DatabaseHelper dbBackend;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;
    AlertDialogManager alert = new AlertDialogManager();
    AutoCompleteTextView WSpecieAutoTxT;
    EditText DiameterEDT, TotalCBMEDT;
    LinearLayout ProgressBarLay;
    ImageView quationListIMG, QuotatioUpdateClose;
    ArrayAdapter<String> StringWSCNoadapter;
    boolean AutoOREnterFlag = true;
    FrameLayout LogSummaryDetailsUpdateLayout;
    AppCompatAutoCompleteTextView LogSummaryWoodSpiceAutoTxT;
    EditText LogSummaryDaimeterEditTxT, LogSummaryTotalCBMEditTxT;
    Button LogSummaryUpdate;
    TextView export_LogSummaryTotCBM, TotScannedVolumeTxT;
    AlertDialog.Builder Signoutbuilder = null;
    AlertDialog SignoutAlert = null;
    LogSummaryReceiver quota_receiver;
    double TotalScannedCBM = 0.00;
    Double TotalScannedVolume = 0.00;
    LinearLayoutManager horizontalLayoutManager;


    private class LogSummaryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            TotalLogSummary();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        quota_receiver = new LogSummaryReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(quota_receiver,
                new IntentFilter("LOGSUMMARY_REFRESH"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(quota_receiver);
    }

    public LogSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogSummaryView = inflater.inflate(R.layout.fragment_quotation, container, false);

        mDBExternalHelper = new ExternalDataBaseHelperClass(getActivity());
        mDBInternalHelper = new InternalDataBaseHelperClass(getActivity());
        Inizilaization();
        TotalLogSummary();
        WoodSpeicesList();
        // Inflate the layout for this fragment
        return LogSummaryView;
    }

    public void WoodSpeicesList() {
        try {
            Common.WoodSpeicesDeatilsList.clear();
            Common.WoodSpeicesDeatilsList = mDBExternalHelper.getWoodSpicesTabel();
            if (Common.WoodSpeicesDeatilsList.size() > 0) {
                ArrayList<String> WoodSpiceArrray = new ArrayList<>();
                for (WoodSpeciesModel WsModel : Common.WoodSpeicesDeatilsList) {
                    WoodSpiceArrray.add(WsModel.getWoodSpeciesCode());
                }
                Common.FellingRegWoodSpeicesStringList = WoodSpiceArrray.toArray(new String[0]);
                StringWSCNoadapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, Common.FellingRegWoodSpeicesStringList);
                StringWSCNoadapter.notifyDataSetChanged();
                WSpecieAutoTxT.setAdapter(StringWSCNoadapter);
                WSpecieAutoTxT.isPerformingCompletion();
                LogSummaryWoodSpiceAutoTxT.setAdapter(StringWSCNoadapter);
                LogSummaryWoodSpiceAutoTxT.isPerformingCompletion();
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void Inizilaization() {
        LogSummaryInternalRV = LogSummaryView.findViewById(R.id.recyclerview_Quotationlist);
        LogSummaryView.findViewById(R.id.quationListIMG).setOnClickListener(mLogSummaryListListener);
        quationListIMG = LogSummaryView.findViewById(R.id.quationListIMG);
        WSpecieAutoTxT = LogSummaryView.findViewById(R.id.export_WSpecieTxT);
        DiameterEDT = LogSummaryView.findViewById(R.id.diameterEDT);
        TotalCBMEDT = LogSummaryView.findViewById(R.id.TotCBMEDT);
        ProgressBarLay = LogSummaryView.findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        LogSummaryDetailsUpdateLayout = LogSummaryView.findViewById(R.id.quotationDetailsUpdateLAY);
        LogSummaryWoodSpiceAutoTxT = LogSummaryView.findViewById(R.id.Quotation_Edit_WSpecieTxT);
        LogSummaryDaimeterEditTxT = LogSummaryView.findViewById(R.id.Quotation_Edit_diameterEDT);
        LogSummaryTotalCBMEditTxT = LogSummaryView.findViewById(R.id.Quotation_Edit_TotCBMEDT);
        LogSummaryUpdate = LogSummaryView.findViewById(R.id.Quotation_updateBtn);
        QuotatioUpdateClose = LogSummaryView.findViewById(R.id.Quotation_closeIcon);
        export_LogSummaryTotCBM = LogSummaryView.findViewById(R.id.export_QuotationTotCBM);
        TotScannedVolumeTxT = LogSummaryView.findViewById(R.id.TotalScannedLogsCountVolumeTxt);
    }

    View.OnClickListener mLogSummaryListListener = new View.OnClickListener() {
        public void onClick(View v) {
            Common.QuotationEntryFlag = 2;
            InsertLogSummary();
        }
    };

    public void TotalLogSummary() {
        try {
            if (Common.Export.LogSummaryList.size() > 0) {

                ArrayList<LogSummaryModel> quotationWoodCodeNoRepeat = new ArrayList<LogSummaryModel>();

                for (LogSummaryModel logSummaryModel : Common.Export.LogSummaryList) {
                    boolean isFound = false;
                    // check if the WoodSpeciesCode name exists in quotationWoodCodeNoRepeat
                    for (LogSummaryModel e : quotationWoodCodeNoRepeat) {
                        if (e.getWoodSpeciesCode().equals(logSummaryModel.getWoodSpeciesCode()) || (e.equals(logSummaryModel))) {
                            isFound = true;
                            break;
                        }
                    }
                    if (!isFound) quotationWoodCodeNoRepeat.add(logSummaryModel);
                }

                LogSummaryInternalRV.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                LogSummaryInternalRV.setLayoutManager(linearLayoutManager);
                LogSummaryInternalListAdapter = new LogSummaryInternalListAdapter(getActivity(), quotationWoodCodeNoRepeat);
                LogSummaryInternalListAdapter.notifyDataSetChanged();
                LogSummaryInternalRV.setAdapter(LogSummaryInternalListAdapter);

                export_LogSummaryTotCBM.setText(String.valueOf(Common.decimalFormat.format(TotalQuantityVolume(Common.Export.LogSummaryList))));
                TotScannedVolumeTxT.setText(String.valueOf(Common.decimalFormat.format(TotalScannedVolume(Common.Export.LogSummaryList))));
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public double TotalQuantityVolume(ArrayList<LogSummaryModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (LogSummaryModel exportDetailsModel : TotalScannedList) {
            TotVolume = TotVolume + exportDetailsModel.getQuantityCBM();
        }
        return TotVolume;
    }

    public double TotalScannedVolume(ArrayList<LogSummaryModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (LogSummaryModel exportDetailsModel : TotalScannedList) {
            TotVolume = TotVolume + exportDetailsModel.getScannedCBM();
        }
        return TotVolume;
    }

    public class LogSummaryInternalListAdapter extends RecyclerView.Adapter<LogSummaryInternalListAdapter.RecyclerViewHolder> {

        private ArrayList<LogSummaryModel> LogSummaryList;
        private Context context;
        private int selectedPosition = -1;
        ArrayList<Integer> counter = new ArrayList<Integer>();

        public LogSummaryInternalListAdapter(Context context, ArrayList<LogSummaryModel> arrayList) {
            this.LogSummaryList = arrayList;
            this.context = context;
            for (int i = 0; i < arrayList.size(); i++) {
                counter.add(0);
            }
        }

        public LogSummaryModel getItem(int position) {
            return LogSummaryList.get(position);
        }

        @Override
        public LogSummaryInternalListAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.export_device_quotation_inflator, viewGroup, false);
            return new LogSummaryInternalListAdapter.RecyclerViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final LogSummaryInternalListAdapter.RecyclerViewHolder holder, final int position) {

            ArrayList<LogSummaryModel> logSummaryChild = new ArrayList<LogSummaryModel>();
            for (LogSummaryModel logSummaryModel : Common.Export.LogSummaryList) {
                if (logSummaryModel.getWoodSpeciesCode().equals(LogSummaryList.get(position).getWoodSpeciesCode())) {
                    logSummaryChild.add(logSummaryModel);
                }
            }

            int logCount = 0;
            int daimetermax = 0;
            int daimetermin = 0;
            double quantitycbm = 0;
            double scannedcbm = 0;

            for (LogSummaryModel logSummaryModel : logSummaryChild) {
                daimetermax = daimetermax + logSummaryModel.getDiameterMax();
                daimetermin = daimetermin + logSummaryModel.getDiameterMin();
                quantitycbm = quantitycbm + logSummaryModel.getQuantityCBM();
                scannedcbm = scannedcbm + logSummaryModel.getScannedCBM();
                logCount = logCount + logSummaryModel.getScannedLogCount();
            }

            logSummarymodel = LogSummaryList.get(position);
            holder.speciesTxT.setText(String.valueOf(logSummarymodel.getWoodSpeciesCode()));
            holder.daimeterminTxT.setText("");
            holder.daimetermaxTxT.setText("");
            holder.totalCBMTxT.setText(String.valueOf(Common.decimalFormat.format(quantitycbm)));
            holder.totalScanCBM.setText(String.valueOf(Common.decimalFormat.format(scannedcbm)));
            holder.SpeciesCountTxT.setText(String.valueOf(logCount));
            //check the radio button if both position and selectedPosition matches
            holder.selctionRadio.setChecked(position == selectedPosition);
            //Set the position tag to both radio button and label
            holder.selctionRadio.setTag(position);
            //holder.speciesTxT.setTag(position);
            holder.selctionRadio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemCheckChanged(v);
                }
            });

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
            int color = generator.getColor(getItem(position));
            holder.RemoveLAY.setBackgroundColor(color);

            if (position % 2 == 0) {
                holder.headerLayout.setBackgroundColor(color);
            } else {
                holder.headerLayout.setBackgroundColor(color);
            }

            InnerRecyclerViewAdapter itemInnerRecyclerView = new InnerRecyclerViewAdapter(logSummaryChild, context);
            holder.innerRecyclerView.setVisibility(View.VISIBLE);
            horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true);
            horizontalLayoutManager.setStackFromEnd(true);
            holder.innerRecyclerView.setLayoutManager(horizontalLayoutManager);
            holder.innerRecyclerView.setAdapter(itemInnerRecyclerView);

        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder {
            LinearLayout headerLayout;
            TextView speciesTxT, daimeterminTxT, daimetermaxTxT, totalCBMTxT, totalScanCBM, TotalCount, SpeciesCountTxT;
            ImageView RemoveLogSummary;
            RadioButton selctionRadio;
            LinearLayout Background, RemoveLAY;
            RecyclerView innerRecyclerView;

            RecyclerViewHolder(View view) {
                super(view);
                headerLayout = itemView.findViewById(R.id.headerlayout);
                selctionRadio = itemView.findViewById(R.id.radio_button);
                speciesTxT = itemView.findViewById(R.id.species_txt);
                daimeterminTxT = itemView.findViewById(R.id.daimetermin_txt);
                totalCBMTxT = itemView.findViewById(R.id.total_cbm_txt);
                //selctedCBMTxT = itemView.findViewById(R.id.selected_cbm_txt);
                Background = itemView.findViewById(R.id.quotation_background);
                RemoveLogSummary = itemView.findViewById(R.id.RemoveQuotations);
                totalScanCBM = itemView.findViewById(R.id.total_scanCBM_txt);
                TotalCount = itemView.findViewById(R.id.total_scannedCount);
                RemoveLAY = itemView.findViewById(R.id.quotation_removeBarCode);
                daimetermaxTxT = itemView.findViewById(R.id.daimetermax_txt);
                innerRecyclerView = itemView.findViewById(R.id.innerRecyclerView);
                SpeciesCountTxT = itemView.findViewById(R.id.species_Count_txt);
            }
        }

        //On selecting any view set the current position to selectedPositon and notify adapter
        private void itemCheckChanged(View v) {
            selectedPosition = (Integer) v.getTag();
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return (null != LogSummaryList ? LogSummaryList.size() : 0);
        }

    }

    public void WoodSpeicesID(String WoodSpeicesCode) {
        try {
            if (Common.WoodSpeicesDeatilsList.size() > 0) {
                Common.WoodSpeicesFilterDeatilsList.clear();
                Common.WoodSpeicesFilterDeatilsList = mDBExternalHelper.getWoodSpiceID(WoodSpeicesCode);
                if (Common.WoodSpeicesFilterDeatilsList.size() > 0) {
                    Common.WoodSpieceID = Common.WoodSpeicesFilterDeatilsList.get(0).getWoodSpeciesId();
                }
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void InsertLogSummary() {
        try {
            if (QuotationINsertValidation(WSpecieAutoTxT.getText().toString(), DiameterEDT.getText().toString(), TotalCBMEDT.getText().toString())) {
                boolean QuationCheck = mDBInternalHelper.getInternalQuationListCheck(Common.ExportID, WSpecieAutoTxT.getText().toString(), DiameterEDT.getText().toString(),
                        TotalCBMEDT.getText().toString(), Common.QuotationNo);
                if (QuationCheck == false) {
                    Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                    boolean exportInserFlag = mDBInternalHelper.insertExportQuotationList(
                            Common.ExportID, Common.ExportUniqueID, Common.Order_Number, " ", Common.ToLocationID, Common.IMEI,
                            WSpecieAutoTxT.getText().toString().trim(), Common.UserID, TotalCBMEDT.getText().toString().trim(), Common.DateTime, 1,
                            DiameterEDT.getText().toString().trim(), Common.QuotationNo, Common.QuotationEntryFlag);

                    if (exportInserFlag == true) {
                        String LSTQuotationID = mDBInternalHelper.getLastExportQuotationList();
                        boolean exportUpdateFlag = mDBInternalHelper.UpdateExportQuotationList(LSTQuotationID, "DQ-" + Common.ExportUniqueID + LSTQuotationID);
                    }
                    TotalLogSummary();
                } else {
                    AlertDialogBox("LogSummary", "Add Different export number because it is already Exists", false);
                }
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("LogSummary", ex.toString(), false);
        }
    }

    public boolean QuotationINsertValidation(String WSC, String Diamter, String TotalCBM) {
        boolean Validattion = true;
        try {
            if (isNullOrEmpty(WSC)) {
                Validattion = false;
                AlertDialogBox("Location", "Please enter WSCode!", false);
            }
            if (WSC.length() > 3 || WSC.length() < 3) {
                Validattion = false;
                AlertDialogBox("WoodSpiece code", "WoodSpiece code length should be 3 ", false);
            }
            if (isNullOrEmpty(Diamter)) {
                Validattion = false;
                AlertDialogBox("Diameter", "Please enter Diameter!", false);
            }

            if (TotalCBM.equals("")) {
                Validattion = false;
                AlertDialogBox("Total CBM", "Please enter Total CBM", false);
            }

        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox(CommonMessage(R.string.ExportHead), ex.toString(), false);
            Validattion = false;
        }
        return Validattion;
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            TotalLogSummary();
            LogSummaryDetailsUpdateLayout.setVisibility(View.GONE);
            alert.showAlertDialog(getActivity(), Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

    public String CommonMessage(int Common_Msg) {
        return this.getResources().getString(Common_Msg);
    }

    public void DeleteQuotationList(String ErrorMessage) {
        if (SignoutAlert != null && SignoutAlert.isShowing()) {
            return;
        }
        Signoutbuilder = new AlertDialog.Builder(getActivity());
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
                        //DeleteQuotationList();
                        dialog.cancel();
                    }
                });

        SignoutAlert = Signoutbuilder.create();
        SignoutAlert.show();
    }
/*
    public void DeleteQuotationList() {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteQuotationListFromList(Common.QuotationDetailsInternalList.get(Common.ExportInterQuoSelectedIndex).getQuotationUniqueNo(),
                    Common.QuotationDetailsInternalList.get(Common.ExportInterQuoSelectedIndex).getQuotationID());
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteExportFromQuotationScanned(Common.QuotationDetailsInternalList.get(Common.ExportInterQuoSelectedIndex).getQutWoodSpieceCode(),
                        Common.QuotationDetailsInternalList.get(Common.ExportInterQuoSelectedIndex).getExportID());
                if (DeleteScannedFlag == true) {
                    TotalLogSummary();
                }
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteExportScannedList", ex.toString(), false);
        }
    }*/

    public class InnerRecyclerViewAdapter extends RecyclerView.Adapter<InnerRecyclerViewAdapter.ViewHolder> {
        public ArrayList<LogSummaryModel> ChildList = new ArrayList<LogSummaryModel>();
        Context context;

        public InnerRecyclerViewAdapter(ArrayList<LogSummaryModel> LogSummaryList, Context _context) {
            this.ChildList = LogSummaryList;
            this.context = _context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView speciesTxT, daimeterminTxT, daimetermaxTxT, totalCBMTxT, totalScanCBM, TotalCount, WSCCountTxT;
            ImageView RemoveLogSummary;
            RadioButton selctionRadio;
            LinearLayout Background, RemoveLAY;

            public ViewHolder(View view) {
                super(view);
                selctionRadio = itemView.findViewById(R.id.radio_button);
                speciesTxT = itemView.findViewById(R.id.species_txt);
                daimeterminTxT = itemView.findViewById(R.id.daimetermin_txt);
                totalCBMTxT = itemView.findViewById(R.id.total_cbm_txt);
                //selctedCBMTxT = itemView.findViewById(R.id.selected_cbm_txt);
                Background = itemView.findViewById(R.id.quotation_background);
                RemoveLogSummary = itemView.findViewById(R.id.RemoveQuotations);
                totalScanCBM = itemView.findViewById(R.id.total_scanCBM_txt);
                TotalCount = itemView.findViewById(R.id.total_scannedCount);
                RemoveLAY = itemView.findViewById(R.id.quotation_removeBarCode);
                daimetermaxTxT = itemView.findViewById(R.id.daimetermax_txt);
                WSCCountTxT = itemView.findViewById(R.id.logcount_txt);
            }
        }

        public LogSummaryModel getItem(int position) {
            return ChildList.get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.export_device_quotation_details_inflator, parent, false);
            InnerRecyclerViewAdapter.ViewHolder vh = new InnerRecyclerViewAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            LogSummaryModel logSummaryChild = ChildList.get(position);

            holder.speciesTxT.setText("");
            if (logSummaryChild.getDiameterMin() == 0 && logSummaryChild.getDiameterMax() == 0) {
                holder.daimeterminTxT.setText("-");
                holder.daimetermaxTxT.setText("-");
            } else {
                holder.daimeterminTxT.setText(String.valueOf(logSummaryChild.getDiameterMin()));
                holder.daimetermaxTxT.setText(String.valueOf(logSummaryChild.getDiameterMax()));
            }
            holder.totalCBMTxT.setText(String.valueOf(logSummaryChild.getQuantityCBM()));
            holder.totalScanCBM.setText(String.valueOf(Common.decimalFormat.format(logSummaryChild.getScannedCBM())));
            holder.WSCCountTxT.setText(String.valueOf(Common.decimalFormat.format(logSummaryChild.getScannedLogCount())));
        }

        @Override
        public int getItemCount() {
            return ChildList.size();
        }
    }

}
