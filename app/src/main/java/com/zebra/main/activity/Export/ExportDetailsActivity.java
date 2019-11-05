package com.zebra.main.activity.Export;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.gson.Gson;
import com.zebra.R;
import com.zebra.adc.decoder.BarCodeReader;
import com.zebra.android.jb.Preference;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.model.Export.ExportDetailsModel;
import com.zebra.main.model.Export.ExportSbbLabelInputModel;
import com.zebra.main.model.Export.ExportOrderDetailsModel;
import com.zebra.main.model.Export.ExportSbblabelOutputModel;
import com.zebra.main.model.Export.QuotationModel;
import com.zebra.main.model.ExternalDB.WoodSpeciesModel;
import com.zebra.main.sdl.SdlScanListener;
import com.zebra.main.sdl.SdlScanService;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;
import com.zebra.utilities.Communicator;
import com.zebra.utilities.GwwException;
import com.zebra.utilities.ServiceURL;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class ExportDetailsActivity extends Activity implements SdlScanListener {
    static final private boolean saveSnapshot = false;
    private static final String TAG = "ExportLoadPlanDetails";
    AlertDialogManager alert = new AlertDialogManager();
    Intent service;
    // ui
    private EditText ScanValueETxT = null, DiameterEDT, TotalCBMEDT;
    private TextView TotalScannedCount, NoValueFoundTxT, LocationNameTXT, VolumeTXT, Diameter_check, TreeNo_check, SBBL_CheckTxt, WSC_CheckTxt, AgeOfLog_CheckTxt, NoteF_CheckTxt, NoteT_CheckTxt,
            NoteL_CheckTxt, Footer1_CheckTxt, Footer2_CheckTxt, Top1_CheckTxt, Top2_CheckTxt, Lenght_CheckTxt, Volume_CheckTxt, Save_CheckTxt;
    private ImageView image = null, Close_SbbLabelIMG, QuotationListIMG;
    LinearLayout SBBLabel_Layout, Quotation_Layout;
    // BarCodeReader specifics
    private BarCodeReader bcr = null;
    private PowerManager.WakeLock mWakeLock = null;
    private int motionEvents = 0, modechgEvents = 0, snapNum = 0;
    boolean bind = false;
    private SdlScanService scanService;
    private ExportLoadPlanDetailsAdapter Exportadapter;

    private RecyclerView ExportDetailsLV, QuotationRecyclerView;
    //DatabaseHelper dbBackend;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;

    private static HSSFWorkbook myWorkBook = new HSSFWorkbook();
    private static HSSFSheet mySheet = myWorkBook.createSheet();
    MediaPlayer beepsound, wronBuzzer;
    LinearLayoutManager horizontalLayoutManager;
    AlertDialog.Builder Removebuilder = null;
    AlertDialog RemoveAlert = null;
    ExportSbbLabelInputModel exportInputmodel;
    ExportDetailsModel exportPlanDetailsModel;
    ExportSbblabelOutputModel exportSbblabeOutModel;
    QuotationModel QuotationModel;
    ExportOrderDetailsModel exportOrdermodel;
    QuotationListAdapter QuotationListAdapter;
    LinearLayout ProgressBarLay;
    AutoCompleteTextView WSpecieAutoTxT;
    ArrayAdapter<String> StringWSCNoadapter;

    @Override
    protected void onStart() {
        super.onStart();
        beepsound = MediaPlayer.create(ExportDetailsActivity.this, R.raw.beep);
        wronBuzzer = MediaPlayer.create(ExportDetailsActivity.this, R.raw.wrong_buzzer);
        //ImportDataBaseFromInternalStorage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            //UpdateCountIDList();
            Log.v(TAG, "onStop");
        } catch (Exception Ex) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            //UpdateCountIDList();
            Log.v(TAG, "onPause");
            //scanService.release();
            if (scanService != null)
                scanService.setActivityUp(false);
            releaseWakeLock();
        } catch (Exception Ex) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        try {
            if (bind) {
                if (!Preference.getScanSelfopenSupport(this, true)) {
                    this.stopService(service);
                }
                unbindService(serviceConnection);
                scanService = null;
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
        }
    }

    // Called with the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exportdetails);
        mDBExternalHelper = new ExternalDataBaseHelperClass(this);
        mDBInternalHelper = new InternalDataBaseHelperClass(this);
        Initialization();
        Click_Listener();
        WoodSpeicesList();
        GetExportDetailsList();
        LocationNameTXT.setText(Common.ToLocationName);
        try {
            new GetQuotationDetails().execute();
        } catch (Exception ex) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_F9)) {
            ScanValueETxT.setText("");
            Common.ScanMode = true;
            scanService.doDecode();
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Signout("Are you sure want to close?");
        }
        return true;
    }

    public void Click_Listener() {

        ScanValueETxT.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Common.ScannedEditTXTFlag = true;
                return false;
            }
        });

        ScanValueETxT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == Common.SBBlenght) {
                } else {
                    if (Common.ScannedEditTXTFlag == true) {
                        //AlertDialogBox("Barcode Length", CommonMessage(R.string.BarCodeLenghtMsg), false);
                        ScanValueETxT.setError(CommonMessage(R.string.BarCodeLenghtMsg));
                    }
                }
            }
        });

        WSpecieAutoTxT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Common.FsWoodSpieceCode = s.toString();
                    WoodSpeicesID(Common.FsWoodSpieceCode);
                } else {
                    // AlertDialogBox("WoodSpiceCode", "please enter WoodSpiceCode", false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        WSpecieAutoTxT.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WSpecieAutoTxT.requestFocus();
                WSpecieAutoTxT.showDropDown();
                hideKeyBoard();
                return false;
            }
        });

        WSpecieAutoTxT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Common.FsWoodSpieceCode = (String) parent.getItemAtPosition(position);
                WoodSpeicesID(Common.FsWoodSpieceCode);
            }
        });

    }
   /* public void HideScan(boolean HideOpenFlag) {

        if (HideOpenFlag == true) {
            fellingRegistrationScanBTN.setVisibility(View.VISIBLE);
        } else {
            fellingRegistrationScanBTN.setVisibility(View.GONE);
        }

    }*/
   /* private void getclassificationAdapter() {
        try {
            Common.classificationlist.clear();
            Common.classificationlist = mDBExternalHelper.getAllclassifications();
            classificationAdapter = new QualitiyAdapter(getApplicationContext(), Common.classificationlist);
            spin_classification.setAdapter(classificationAdapter);
        } catch (Exception ex) {
        }
    }*/

    ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bind = false;
            Log.v(TAG, "SDLActivity onServiceDisconnected " + bind);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bind = true;
            SdlScanService.MyBinder myBinder = (SdlScanService.MyBinder) service;
            scanService = myBinder.getService();
            //
            scanService.setOnScanListener(ExportDetailsActivity.this);
            scanService.setActivityUp(true);
            Log.v(TAG, "SDLActivity onServiceConnected " + bind);
        }
    };

    private synchronized static void writeFile(File file, String value) {

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(value.getBytes());
            outputStream.flush();
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void WoodSpeicesID(String WoodSpeicesCode) {
        try {
            if (Common.WoodSpeicesDeatilsList.size() > 0) {
                Common.WoodSpeicesFilterDeatilsList.clear();
                Common.WoodSpeicesFilterDeatilsList = mDBExternalHelper.getWoodSpiceID(WoodSpeicesCode);
                if (Common.WoodSpeicesFilterDeatilsList.size() > 0) {
                    Common.FsWoodSpieceID = Common.WoodSpeicesFilterDeatilsList.get(0).getWoodSpeciesId();
                }
            }
        } catch (Exception ex) {

        }
    }

    // Called when the activity is about to start interacting with the user.
    @Override
    protected void onResume() {
        Log.v(TAG, "SDLActivity onResume bind: " + bind);
        try {
            service = new Intent(this, SdlScanService.class);
            bindService(service, serviceConnection, BIND_AUTO_CREATE);
            startService(service);
        } catch (Exception e) {
            //Log.e("InventoryCountActivity","Exceptionss: "+e+" mBeepManagersdl: "+mBeepManagersdl);
        }
        try {
            ScannedStatus("");//getResources().getString(R.string.app_name) + " v" + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (scanService != null)
            scanService.setActivityUp(true);
        acquireWakeLock();
        GetExportDetailsList();
        super.onResume();
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
                StringWSCNoadapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_dropdown_item_1line, Common.FellingRegWoodSpeicesStringList);
                StringWSCNoadapter.notifyDataSetChanged();
                WSpecieAutoTxT.setAdapter(StringWSCNoadapter);
            }
        } catch (Exception ex) {

        }
    }

    private void Initialization() {
        // Inflate our UI from its XML layout description.
        findViewById(R.id.exportScanBTN).setOnClickListener(mDecodeListener);
        findViewById(R.id.exportPlantEnterIMG).setOnClickListener(mScannerListener);
        findViewById(R.id.closeActivity).setOnClickListener(mCloseActivityListener);
        ScanValueETxT = findViewById(R.id.textStatus);
        TotalScannedCount = findViewById(R.id.TotalScannedCount);
        VolumeTXT = findViewById(R.id.TotalScannedVolume);
        ExportDetailsLV = findViewById(R.id.exportPlan_lv);
        NoValueFoundTxT = findViewById(R.id.NovalueFound);
        LocationNameTXT = findViewById(R.id.locationNameTXT);
        ProgressBarLay = findViewById(R.id.progressbar_layout);
        ProgressBarLay.setVisibility(View.GONE);
        Diameter_check = findViewById(R.id.diameter_check);
        TreeNo_check = findViewById(R.id.treeNo_check);
        SBBL_CheckTxt = findViewById(R.id.sbblabel_check);
        WSC_CheckTxt = findViewById(R.id.wsc_check);
        AgeOfLog_CheckTxt = findViewById(R.id.ageoflog_check);
        NoteF_CheckTxt = findViewById(R.id.noteF_check);
        NoteT_CheckTxt = findViewById(R.id.noteT_check);
        NoteL_CheckTxt = findViewById(R.id.noteL_check);
        Footer1_CheckTxt = findViewById(R.id.footer1_check);
        Footer2_CheckTxt = findViewById(R.id.footer2_check);
        Top1_CheckTxt = findViewById(R.id.top1_check);
        Top2_CheckTxt = findViewById(R.id.top2_check);
        Lenght_CheckTxt = findViewById(R.id.lenght_check);
        Volume_CheckTxt = findViewById(R.id.volume_check);
        Save_CheckTxt = findViewById(R.id.Save_check);
        findViewById(R.id.Save_check).setOnClickListener(mSaveActivityListener);

        Close_SbbLabelIMG = findViewById(R.id.closeActivity_check);
        findViewById(R.id.closeActivity_check).setOnClickListener(mCloseActivityCheckListener);
        findViewById(R.id.quotation_closeActivity).setOnClickListener(mCloseQuptationActivityCheckListener);
        SBBLabel_Layout = findViewById(R.id.SbbLabelCheck_layout);
        SBBLabel_Layout.setVisibility(View.GONE);
        Quotation_Layout = findViewById(R.id.quationList_layout);
        Quotation_Layout.setVisibility(View.GONE);
        WSpecieAutoTxT = findViewById(R.id.export_WSpecieTxT);
        DiameterEDT = findViewById(R.id.diameterEDT);
        TotalCBMEDT = findViewById(R.id.TotCBMEDT);
        QuotationRecyclerView = findViewById(R.id.recyclerview_Quotationlist);
        QuotationListIMG = findViewById(R.id.quationListIMG);
        findViewById(R.id.quationListIMG).setOnClickListener(mQuotationListListener);
    }

    View.OnClickListener mDecodeListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (SbbLableValidation(WSpecieAutoTxT.getText().toString(), DiameterEDT.getText().toString(), TotalCBMEDT.getText().toString())) {
                ScanValueETxT.setText("");
                Common.EntryMode = 1;
                Common.ScanMode = true;
                scanService.doDecode();
            }
        }
    };

    View.OnClickListener mScannerListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (SbbLableValidation(WSpecieAutoTxT.getText().toString(), DiameterEDT.getText().toString(), TotalCBMEDT.getText().toString())) {
                Common.ScanMode = false;
                HideKeyboard();
                String SBBLabel = ScanValueETxT.getText().toString();
                if (SBBLabel.length() == Common.SBBlenght) {
                    Common.EntryMode = 2;
                    ScannedResult(SBBLabel);
                } else {
                    ScanValueETxT.setError(CommonMessage(R.string.ValidBarCodeMsg));
                }
            }
        }
    };

    View.OnClickListener mCloseActivityListener = new View.OnClickListener() {
        public void onClick(View v) {
            Signout("Are you sure you want to close?");
        }
    };
    View.OnClickListener mQuotationListListener = new View.OnClickListener() {
        public void onClick(View v) {
            hideKeyBoard();
            if (Common.QuotationDetailsList.size() > 0) {
                Quotation_Layout.setVisibility(View.VISIBLE);
            } else {
                AlertDialogBox(CommonMessage(R.string.ExportHead), "OrderNo did not have Quotation List!", false);
            }
        }
    };
    View.OnClickListener mCloseActivityCheckListener = new View.OnClickListener() {
        public void onClick(View v) {
            SBBLabel_Layout.setVisibility(View.GONE);
        }
    };
    View.OnClickListener mCloseQuptationActivityCheckListener = new View.OnClickListener() {
        public void onClick(View v) {
            Quotation_Layout.setVisibility(View.GONE);
        }
    };
    View.OnClickListener mSaveActivityListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                SBBLabel_Layout.setVisibility(View.GONE);
                InsertExportLoadPlanDetails();
            } catch (Exception ex) {

            }
        }
    };

    public boolean SbbLableValidation(String WSC, String Diamter, String TotalCBM) {
        boolean Validattion = true;
        try {
            if (isNullOrEmpty(WSC)) {
                Validattion = false;
                AlertDialogBox("Location", "Please enter WSCode!", false);
            }
            if (isNullOrEmpty(Diamter)) {
                Validattion = false;
                AlertDialogBox("Felling SectionId", "Please enter Diameter!", false);
            }

            if (TotalCBM.equals("")) {
                Validattion = false;
                AlertDialogBox("Felling Register Date", "Please enter Total CBM", false);
            }

        } catch (Exception e) {
            AlertDialogBox(CommonMessage(R.string.ExportHead), e.toString(), false);
            Validattion = false;
        }
        return Validattion;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str == "null";
    }

    public void GetSbbLabelDetails() {
        try {
            if (Common.AllExportLoadPlanDetailsmap.size() > 0) {
                Common.ExportDetailsList = mDBInternalHelper.getExportDetailsList(Common.ExportID, true);
                Double RemoveVolumeSum = TotalVolume(Common.ExportDetailsList);
                RemoveVolumeSum = RemoveVolumeSum + Common.AllExportLoadPlanDetailsmap.get(0).getVolume();
                if (Common.ExportSingleContainerTotValue < RemoveVolumeSum) {
                    AlertDialogBox("Container Total CBM", "Container cannot allow to add more then " + String.valueOf(Common.ExportSingleContainerTotValue) + "Volume", false);
                    return;
                } else {
                    SBBLabel_Layout.setVisibility(View.VISIBLE);
                    Common.Export_Sbblabel = Common.AllExportLoadPlanDetailsmap.get(0).getChildSBBLabel();
                    Common.Export_WSC = Common.AllExportLoadPlanDetailsmap.get(0).getWoodSpeciesCode();
                    Common.Export_PvNo = Common.AllExportLoadPlanDetailsmap.get(0).getExaminationNo();
                    Common.Export_PvDate = Common.AllExportLoadPlanDetailsmap.get(0).getExaminationDate();
                    Common.Export_AgeOFLog = Common.AllExportLoadPlanDetailsmap.get(0).getStockAge();
                    Common.Export_Footer_1 = String.valueOf(Common.AllExportLoadPlanDetailsmap.get(0).getFoot1_cm());
                    Common.Export_Footer_2 = String.valueOf(Common.AllExportLoadPlanDetailsmap.get(0).getFoot2_cm());
                    Common.Export_Top_1 = String.valueOf(Common.AllExportLoadPlanDetailsmap.get(0).getTop1_cm());
                    Common.Export_Top_2 = String.valueOf(Common.AllExportLoadPlanDetailsmap.get(0).getTop2_cm());
                    Common.Export_Diameter = String.valueOf(Common.AllExportLoadPlanDetailsmap.get(0).getDiameter());
                    Common.Export_treeNo = String.valueOf(Common.AllExportLoadPlanDetailsmap.get(0).getTreeNumber());
                                   /* Common.Export_NoteT = Common.AllExportLoadPlanDetailsmap.get(0).getNoteT();
                                    Common.Export_NoteF = Common.AllExportLoadPlanDetailsmap.get(0).getNoteF();
                                    Common.Export_NoteL = Common.AllExportLoadPlanDetailsmap.get(0).getNoteL();*/
                    Common.Export_Length = String.valueOf(Common.AllExportLoadPlanDetailsmap.get(0).getLength_dm());
                    Common.Export_Volume = String.valueOf(Common.AllExportLoadPlanDetailsmap.get(0).getVolume());
                    Common.WoodSpieceID = String.valueOf(Common.AllExportLoadPlanDetailsmap.get(0).getWoodSpeciesId());
                    //Common.OLDWoodSpieceCode = Common.AllExportLoadPlanDetailsmap.get(0).getOldWoodSpieceCode();

                    if (WSpecieAutoTxT.getText().toString().equals(Common.Export_WSC)) {
                        WSC_CheckTxt.setBackgroundColor(getResources().getColor(R.color.color_white));
                    } else {
                        WSC_CheckTxt.setBackgroundColor(getResources().getColor(R.color.red));
                        Save_CheckTxt.setBackgroundColor(getResources().getColor(R.color.red));
                    }
                    String[] DiameterSplite = DiameterEDT.getText().toString().split("-");
                    if (DiameterSplite.length == 2) {
                        if (Integer.parseInt(DiameterSplite[0].trim()) >= Integer.parseInt(Common.Export_Diameter) && Integer.parseInt(DiameterSplite[1].trim()) <= Integer.parseInt(Common.Export_Diameter)) {
                            Diameter_check.setBackgroundColor(getResources().getColor(R.color.color_white));
                            Save_CheckTxt.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
                            Common.IsValidWSCode = 0;
                        } else {
                            Diameter_check.setBackgroundColor(getResources().getColor(R.color.red));
                            Save_CheckTxt.setBackgroundColor(getResources().getColor(R.color.red));
                            Common.IsValidWSCode = 1;
                        }
                    } else {
                        String DiaMeter2 = DiameterSplite[0];
                        String DiameterLstValue = DiaMeter2.substring(DiaMeter2.length() - 1);
                        String DiaMeter = DiaMeter2.substring(0, DiaMeter2.length() - 1).trim();
                        if (DiameterLstValue.equals("+")) {
                            if (Integer.parseInt(DiaMeter) <= Integer.parseInt(Common.Export_Diameter)) {
                                Diameter_check.setBackgroundColor(getResources().getColor(R.color.color_white));
                                Save_CheckTxt.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
                                Common.IsValidDiameter = 0;
                            } else {
                                Diameter_check.setBackgroundColor(getResources().getColor(R.color.red));
                                Save_CheckTxt.setBackgroundColor(getResources().getColor(R.color.red));
                                Common.IsValidDiameter = 1;
                            }
                        } else if (DiameterLstValue.equals("-")) {
                            if (Integer.parseInt(DiaMeter) >= Integer.parseInt(Common.Export_Diameter)) {
                                Diameter_check.setBackgroundColor(getResources().getColor(R.color.color_white));
                                Save_CheckTxt.setBackgroundColor(getResources().getColor(R.color.colorPrimary_light));
                                Common.IsValidDiameter = 0;
                            } else {
                                Diameter_check.setBackgroundColor(getResources().getColor(R.color.red));
                                Save_CheckTxt.setBackgroundColor(getResources().getColor(R.color.red));
                                Common.IsValidDiameter = 1;
                            }
                        }
                    }
                    Diameter_check.setText(Common.Export_Diameter);
                    TreeNo_check.setText(Common.Export_treeNo);
                    SBBL_CheckTxt.setText(Common.Export_Sbblabel);
                    WSC_CheckTxt.setText(Common.Export_WSC);
                    AgeOfLog_CheckTxt.setText(Common.Export_AgeOFLog);
                    NoteF_CheckTxt.setText(Common.Export_NoteF);
                    NoteT_CheckTxt.setText(Common.Export_NoteT);
                    NoteL_CheckTxt.setText(Common.Export_NoteL);
                    Footer1_CheckTxt.setText(Common.Export_Footer_1);
                    Footer2_CheckTxt.setText(Common.Export_Footer_2);
                    Top1_CheckTxt.setText(Common.Export_Top_1);
                    Top2_CheckTxt.setText(Common.Export_Top_2);
                    Lenght_CheckTxt.setText(Common.Export_Length);
                    Volume_CheckTxt.setText(Common.Export_Volume);
                }
            } else {
                AlertDialogBox("SbbLabel Validation", "#" + String.valueOf(Common.SbbLabel) + "--" + "No values Found", false);
            }
        } catch (Exception ex) {
            AlertDialogBox("SbbLabel Validation", "#" + String.valueOf(Common.SbbLabel) + "--" + ex.toString(), false);
        }
    }

    public void ClearSbbLayout() {
        SBBL_CheckTxt.setText("");
        WSC_CheckTxt.setText("");
        AgeOfLog_CheckTxt.setText("");
        NoteF_CheckTxt.setText("");
        NoteT_CheckTxt.setText("");
        NoteL_CheckTxt.setText("");
        Footer1_CheckTxt.setText("");
        Footer2_CheckTxt.setText("");
        Top1_CheckTxt.setText("");
        Top2_CheckTxt.setText("");
        Lenght_CheckTxt.setText("");
        Diameter_check.setText("");
        TreeNo_check.setText("");
        /*Clear Values*/
        Common.Export_Diameter = "";
        Common.Export_treeNo = "";
        Common.Export_PvNo = "";
        Common.Export_PvDate = "";
        Common.WoodSpieceID = "";
        Common.OLDWoodSpieceCode = "";
        Common.WoodSpieceCode = "";
        Common.Export_AgeOFLog = "";
        Common.Export_Footer_1 = "";
        Common.Export_Footer_2 = "";
        Common.Export_Top_1 = "";
        Common.Export_Top_2 = "";
        Common.Length = "";
        Common.OLDVolume = "0.00";
        Common.Volume = "0.00";
        Common.Export_NoteF = "";
        Common.Export_NoteT = "";
        Common.Export_NoteL = "";
    }

    // display status string
    public void ScannedStatus(String s) {
        Log.v(TAG, "ScannedStatus " + s);
        // tvStat.setText(s);
    }

    // display status resource id
    public void ScannedStatus(int id) {
        Log.v(TAG, "ScannedStatus " + id);
        //tvStat.setText(id);
    }

    // display error msg
    private void dspErr(String s) {
        Log.v(TAG, "ScannedStatus " + s);
        //tvStat.setText("ERROR" + s);
    }

    // display status string
    public void ScannedResult(String s) {
        HideKeyboard();
        try {
            if (s != null && s.length() > 0) {
                // Common.ScannnedResultListMap.clear();
                if (Common.ScanMode == true) {
                    Common.BarCode = s;
                    boolean BarcodeValidation = Common.BarCode.contains("-");
                    if (BarcodeValidation == true) {
                        String[] arrOfStr = Common.BarCode.split("-");
                        if (arrOfStr.length > 1) {
                            Common.SbbLabel = arrOfStr[1];
                        }
                    } else {
                        AlertDialogBox("Scanning Result!", "enter valid Barcode, please try diff barcode", false);
                        return;
                    }
                } else {
                    Common.SbbLabel = s;
                    Common.BarCode = "NA-" + Common.SbbLabel;
                }
                Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                // Common.IMEI = getImeiNumber();
                Common.IsActive = 1;

                // Checked Duplicate In Internal Tabel
                boolean EmptyNullFlags = mDBInternalHelper.getExportLoadPlanListIDCheck(Common.SbbLabel);
                if (EmptyNullFlags == true) {
                    wronBuzzer.start();
                    GetExportDetailsList();
                    UpdateExportList();
                    return;
                }
                new GetExportSBBLabelSyncAsynkTask().execute();
                Common.ScannedEditTXTFlag = false;
                ScanValueETxT.setText(s);
            }
        } catch (Exception ex) {
            Log.v(TAG, "decode failed" + ex.toString());
        }
    }

    public static int booleanToInt(boolean value) {
        // Convert true to 1 and false to 0.
        return value ? 1 : 0;
    }

 /*   private String getImeiNumber() {
        final TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return telephonyManager.getImei();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return telephonyManager.getDeviceId();
            }
        }
        return telephonyManager.getDeviceId();
    }*/

    public boolean SbblabelValidation(String WSC, String PlotNO, String F1, String F2, String T1, String T2, String Length) {
        boolean Validattion = true;
        if (WSC.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter WSC!", false);
        }
        if (PlotNO.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter PlotNO!", false);
        }
        if (F1.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter F1!", false);
        }
        if (F2.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter F2!", false);
        }
        if (T1.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter T1!", false);
        }
        if (T2.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter T2!", false);
        }
        if (Length.equals("")) {
            Validattion = false;
            AlertDialogBox("New Tree No", "Please enter Length!", false);
        }
        return Validattion;
    }

    public void Signout(String ErrorMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(ErrorMessage);
        builder1.setCancelable(true);
        builder1.setPositiveButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            UpdateExportList();
                            InventoryActivty();
                        } catch (Exception ex) {
                            AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
                        }


                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void GetExportDetailsList() {
        try {
            Common.ExportDetailsList = mDBInternalHelper.getExportDetailsList(Common.ExportID, true);
            Double RemoveVolumeSum = 0.00;
            if (Common.ExportDetailsList.size() > 0) {
                Exportadapter = new ExportLoadPlanDetailsAdapter(Common.ExportDetailsList, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                ExportDetailsLV.setLayoutManager(horizontalLayoutManager);
                Exportadapter.notifyDataSetChanged();
                ExportDetailsLV.setAdapter(Exportadapter);
                NoValueFoundTxT.setVisibility(View.GONE);
                ExportDetailsLV.setVisibility(View.VISIBLE);
                RemoveVolumeSum = TotalVolume(Common.ExportDetailsList);
            } else {
                NoValueFoundTxT.setVisibility(View.VISIBLE);
                ExportDetailsLV.setVisibility(View.GONE);
            }
            TotalScannedCount.setText(String.valueOf(Common.ExportDetailsList.size()));
            VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(RemoveVolumeSum)));
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
    }

    public void onPictureTaken(int format, int width, int height, byte[] abData, BarCodeReader reader) {
        if (image == null)
            return;

        // display snapshot
        Bitmap bmSnap = BitmapFactory.decodeByteArray(abData, 0, abData.length);
        if (bmSnap == null) {
            dspErr("OnPictureTaken: no bitmap");
            return;
        }
        image.setImageBitmap(rotated(bmSnap));

        // Save snapshot to the SD card
        if (saveSnapshot) {
            String snapFmt = "bin";
            switch (bcr.getNumParameter(BarCodeReader.ParamNum.IMG_FILE_FORMAT)) {
                case BarCodeReader.ParamVal.IMG_FORMAT_BMP:
                    snapFmt = "bmp";
                    break;

                case BarCodeReader.ParamVal.IMG_FORMAT_JPEG:
                    snapFmt = "jpg";
                    break;

                case BarCodeReader.ParamVal.IMG_FORMAT_TIFF:
                    snapFmt = "tif";
                    break;
            }

            File filFSpec = null;
            try {
                String strFile = String.format("se4500_img_%d.%s", snapNum, snapFmt);
                File filRoot = Environment.getExternalStorageDirectory();
                File filPath = new File(filRoot.getAbsolutePath() + "/DCIM/Camera");
                filPath.mkdirs();
                filFSpec = new File(filPath, strFile);
                FileOutputStream fos = new FileOutputStream(filFSpec);
                fos.write(abData);
                fos.close();

                ++snapNum;
            } catch (Throwable thrw) {
                dspErr("Create '" + filFSpec.getAbsolutePath() + "' failed");
                dspErr("Error=" + thrw.getMessage());
            }
        }
    }

    public void onPreviewFrame(byte[] data, BarCodeReader bcreader) {
    }

    public void onEvent(int event, int info, byte[] data, BarCodeReader reader) {
        switch (event) {
            case BarCodeReader.BCRDR_EVENT_SCAN_MODE_CHANGED:
                ++modechgEvents;
                ScannedStatus("Scan Mode Changed Event (#" + modechgEvents + ")");
                break;

            case BarCodeReader.BCRDR_EVENT_MOTION_DETECTED:
                ++motionEvents;
                ScannedStatus("Motion Detect Event (#" + motionEvents + ")");
                break;

            case BarCodeReader.BCRDR_EVENT_SCANNER_RESET:
                ScannedStatus("Reset Event");
                break;

            default:
                break;
        }
    }

    private Bitmap rotated(Bitmap bmSnap) {
        Matrix matrix = new Matrix();
        if (matrix != null) {
            matrix.postRotate(90);
            Bitmap bmr = Bitmap.createBitmap(bmSnap, 0, 0, bmSnap.getWidth(), bmSnap.getHeight(), matrix, true);
            if (bmr != null)
                return bmr;
        }

        return bmSnap;
    }

    @Override
    public void henResult(String codeType, String context) {
    }

    @Override
    public void result(String content) {
    }

    public void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        }
        if (mWakeLock != null) {
            mWakeLock.acquire();
        }
    }

    public void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
        }
    }


    public void HideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void ShowKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    public void InsertExportLoadPlanDetails() {
        beepsound.start();
        // Insert values into Scanned Result
        boolean exportInserFlag = mDBInternalHelper.insertExportDetailsResult(
                Common.ExportID, Common.ExportUniqueID, Common.Order_Number, Common.ContainerNo, Common.ToLocationID, Common.IMEI, Common.Export_PvNo, Common.Export_PvDate, Common.SbbLabel, Common.BarCode,
                Common.WoodSpieceID, WSpecieAutoTxT.getText().toString(), Common.Export_WSC, Common.Export_AgeOFLog, Common.Export_Footer_1, Common.Export_Footer_2, Common.Export_Top_1,
                Common.Export_Top_2, Common.Export_Length, Common.UserID, Common.EntryMode, TotalCBMEDT.getText().toString(), Common.Export_Volume, Common.DateTime, Common.IsActive, Common.Export_NoteF,
                Common.Export_NoteT, Common.Export_NoteL, Common.IsValidVolume, Common.IsValidWSCode, Common.IsValidDiameter, Common.Export_Diameter, DiameterEDT.getText().toString());
        //Scanned Result Refresh
        GetExportDetailsList();
        UpdateExportList();
        ClearSbbLayout();
    }

    public class ExportLoadPlanDetailsAdapter extends RecyclerView.Adapter<ExportLoadPlanDetailsAdapter.GroceryViewHolder> {
        private List<ExportDetailsModel> ExportLoadPlanList;
        Context context;


        public ExportLoadPlanDetailsAdapter(List<ExportDetailsModel> exportLoadPlanList, Context context) {
            this.ExportLoadPlanList = exportLoadPlanList;
            this.context = context;
        }

        public void removeItem(int position) {
            ExportLoadPlanList.remove(position);
            notifyItemRemoved(position);
        }

        public ExportDetailsModel getItem(int position) {
            return ExportLoadPlanList.get(position);
        }

        @Override
        public ExportLoadPlanDetailsAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exportdetails_infliator, parent, false);
            ExportLoadPlanDetailsAdapter.GroceryViewHolder gvh = new ExportLoadPlanDetailsAdapter.GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(ExportLoadPlanDetailsAdapter.GroceryViewHolder holder, final int position) {
            holder.Barcode.setText(ExportLoadPlanList.get(position).getSbbLabel());  //barcode
            holder.WoodSpiceCode.setText(ExportLoadPlanList.get(position).getWoodSpieceCode());//specie
            holder.DiamterSize.setText(ExportLoadPlanList.get(position).getDiameter());//Diameter
            holder.CBM.setText(String.valueOf(ExportLoadPlanList.get(position).getVolume()));//Diameter
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
            int color = generator.getColor(getItem(position));
            holder.Remove.setBackgroundColor(color);
            holder.Remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.RemoveSBBLabel = "";
                    Common.RemoveSBBLabel = ExportLoadPlanList.get(position).getSbbLabel();
                    RemoveMessage(CommonMessage(R.string.Remove_Message));
                }
            });
        }

        @Override
        public int getItemCount() {
            return ExportLoadPlanList.size();
        }

        public class GroceryViewHolder extends RecyclerView.ViewHolder {
            TextView Barcode, WoodSpiceCode, DiamterSize, CBM;
            LinearLayout Background, Remove;

            public GroceryViewHolder(View view) {
                super(view);
                Barcode = view.findViewById(R.id.export_sbblabel);
                WoodSpiceCode = view.findViewById(R.id.export_speiceCode);
                Background = view.findViewById(R.id.exportlayoutBackground);
                Remove = view.findViewById(R.id.export_removeBarCode);
                DiamterSize = view.findViewById(R.id.export_DiameterSizes);
                CBM = view.findViewById(R.id.export_TotCBM);

            }
        }
    }

    public static <T> boolean IsNullOrEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
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

    public void RemoveMessage(String ErrorMessage) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(this);
        Removebuilder.setMessage(ErrorMessage);
        Removebuilder.setCancelable(true);
        Removebuilder.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Removebuilder.setNegativeButton(CommonMessage(R.string.action_Remove),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            boolean Isdelete = mDBInternalHelper.RemoveFromExportDetailsList(Common.RemoveSBBLabel, 0, Common.ExportID);
                            if (Isdelete == true) {
                                Toast.makeText(ExportDetailsActivity.this, "Successfully Removed from List", Toast.LENGTH_LONG).show();
                                GetExportDetailsList();
                                UpdateExportList();
                            }
                        } catch (Exception e) {

                        }
                        dialog.cancel();
                    }
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public void UpdateExportList() {
        try {
            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            Double RemoveVolumeSum = TotalVolume(Common.ExportDetailsList);
            // Update values into ListID
            boolean ListIdFlag = mDBInternalHelper.UpdateExportLoadPlanListID(Common.EndDate, Common.ExportDetailsList.size(), Common.ExportID, String.valueOf(RemoveVolumeSum), 1, Common.ExportUniqueID);
            /*if (ListIdFlag == true) {
                Common.InventoryCountList = mDBInternalHelper.getInventoryCountIdList();
            }*/
        } catch (Exception ex) {
            AlertDialogBox("UpdateExportList", ex.toString(), false);
        }
    }

    public double TotalVolume(ArrayList<ExportDetailsModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (ExportDetailsModel exportDetailsModel : TotalScannedList) {
            TotVolume = TotVolume + exportDetailsModel.getVolume();
        }
        return TotVolume;
    }

    public void DeleteTransferCountScannedList(int ListID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteInventoryCountListID(ListID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteInventoryCountScanned(ListID);
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteTransferCountandTransferScannedList", ex.toString(), false);
        }
    }

    public void InventoryActivty() {
        Intent _gwwIntent = new Intent(this, ExportListActivity.class);
        startActivity(_gwwIntent);
    }

    // Method for QuationList API
    class GetQuotationDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.QuotationDetailsList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String MethodName = "GetQuotationDetails ";//"InsertHHInventoryCount/";
            String SyncURL = ServiceURL.getServiceURL(ServiceURL.ExportControllerName, MethodName);
            SyncURL = SyncURL.replaceAll("%20", "");
            exportOrdermodel = new ExportOrderDetailsModel();
            exportOrdermodel.setQuotNum(Common.Order_Number);
            try {
                String SyncURLInfo = new Communicator().POST_Obect(SyncURL, exportOrdermodel);
                if (GwwException.GwwException(Common.HttpResponceCode) == true) {
                    if (SyncURLInfo != null) {
                        JSONObject jsonObj = new JSONObject(SyncURLInfo);
                        String SyncResponceStr = jsonObj.getString("QuotationDetails");
                        if (SyncResponceStr != null) {
                            JSONArray SyncJsonAry = new JSONArray(SyncResponceStr);
                            for (int Sync_Index = 0; Sync_Index < SyncJsonAry.length(); Sync_Index++) {
                                QuotationModel = new Gson().fromJson(SyncJsonAry.getString(Sync_Index), QuotationModel.class);
                                Common.QuotationDetailsList.add(QuotationModel);
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
                        //Search the master table for SBB label entered or scanned
                        try {
                            if (Common.QuotationDetailsList.size() > 0) {
                                Quotation_Layout.setVisibility(View.VISIBLE);
                                GetQuotationRecyclerView();
                            } else {
                                Quotation_Layout.setVisibility(View.GONE);
                                AlertDialogBox("Quotation Details", "#" + String.valueOf(Common.Quotation_NO) + "--" + "No values Found", false);
                            }
                        } catch (Exception ex) {
                            Quotation_Layout.setVisibility(View.GONE);
                        }
                    } else {
                        Quotation_Layout.setVisibility(View.GONE);
                        AlertDialogBox("Quotation Details", "#" + String.valueOf(Common.Quotation_NO) + "--" + "No values Found - Please contact adminstrator", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }
    }

    // Method for SyncAPI
    class GetExportSBBLabelSyncAsynkTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarLay.setVisibility(View.VISIBLE);
            Common.AllExportLoadPlanDetailsmap.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String MethodName = "ExportMasterDetails ";//"InsertHHInventoryCount/";
            String SyncURL = ServiceURL.getServiceURL(ServiceURL.ExportControllerName, MethodName);
            SyncURL = SyncURL.replaceAll("%20", "");
            exportInputmodel = new ExportSbbLabelInputModel();
            exportInputmodel.setSbbLabel(Common.SbbLabel);
            try {
                String SyncURLInfo = new Communicator().POST_Obect(SyncURL, exportInputmodel);
                if (GwwException.GwwException(Common.HttpResponceCode) == true) {
                    if (SyncURLInfo != null) {
                        JSONObject jsonObj = new JSONObject(SyncURLInfo);
                        String SyncResponceStr = jsonObj.getString("SbbLabelDetails");
                        if (SyncResponceStr != null) {
                            JSONArray SyncJsonAry = new JSONArray(SyncResponceStr);
                            for (int Sync_Index = 0; Sync_Index < SyncJsonAry.length(); Sync_Index++) {
                                exportSbblabeOutModel = new Gson().fromJson(SyncJsonAry.getString(Sync_Index), ExportSbblabelOutputModel.class);
                                Common.AllExportLoadPlanDetailsmap.add(exportSbblabeOutModel);
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
                        //Search the master table for SBB label entered or scanned
                        GetSbbLabelDetails();
                    } else {
                        AlertDialogBox("SbbLabel Validation", "#" + String.valueOf(Common.SbbLabel) + "--" + "No values Found - Please contact adminstrator", false);
                    }
                }
            });
            ProgressBarLay.setVisibility(View.GONE);
        }
    }

    private void GetQuotationRecyclerView() {
        QuotationRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        QuotationRecyclerView.setLayoutManager(linearLayoutManager);
        QuotationListAdapter = new QuotationListAdapter(this, Common.QuotationDetailsList);
        QuotationListAdapter.notifyDataSetChanged();
        QuotationRecyclerView.setAdapter(QuotationListAdapter);
    }

    public class QuotationListAdapter extends RecyclerView.Adapter<QuotationListAdapter.RecyclerViewHolder> {

        private ArrayList<QuotationModel> QuotationModelsList;
        private Context context;
        private int selectedPosition = -1;

        public QuotationListAdapter(Context context, ArrayList<QuotationModel> arrayList) {
            this.QuotationModelsList = arrayList;
            this.context = context;
        }

        @Override
        public QuotationListAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.quatation_list_adapter_row_layout, viewGroup, false);
            return new QuotationListAdapter.RecyclerViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final QuotationListAdapter.RecyclerViewHolder holder, final int position) {
            QuotationModel QuotationModel = QuotationModelsList.get(position);
            holder.speciesTxT.setText(QuotationModel.getWoodSpeciesCode().trim());
            String DiameterS = QuotationModel.getDiameterSizes().trim();
            holder.daimeterSizesTxT.setText(DiameterS);
            holder.totalCBMTxT.setText(String.valueOf(QuotationModel.getTotalVolume()).trim());
            holder.selctedCBMTxT.setText(String.valueOf(QuotationModel.getTotalVolume()).trim());

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
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            holder.Background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Quotation_Layout.setVisibility(View.GONE);
                    Common.FsWoodSpieceCode = QuotationModel.getWoodSpeciesCode();
                    WoodSpeicesID(Common.FsWoodSpieceCode);
                    String DiameterS = QuotationModel.getDiameterSizes();
                    WSpecieAutoTxT.setText(Common.FsWoodSpieceCode);
                    DiameterEDT.setText(DiameterS);
                    TotalCBMEDT.setText(String.valueOf(QuotationModel.getTotalVolume()));
                }
            });
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView speciesTxT, daimeterSizesTxT, totalCBMTxT, selctedCBMTxT;
            RadioButton selctionRadio;
            LinearLayout Background;

            RecyclerViewHolder(View view) {
                super(view);
                selctionRadio = (RadioButton) itemView.findViewById(R.id.radio_button);
                speciesTxT = (TextView) itemView.findViewById(R.id.species_txt);
                daimeterSizesTxT = (TextView) itemView.findViewById(R.id.daimeter_txt);
                totalCBMTxT = (TextView) itemView.findViewById(R.id.total_cbm_txt);
                selctedCBMTxT = (TextView) itemView.findViewById(R.id.selected_cbm_txt);
                Background = itemView.findViewById(R.id.quotation_background);
            }

        }

        //On selecting any view set the current position to selectedPositon and notify adapter
        private void itemCheckChanged(View v) {
            selectedPosition = (Integer) v.getTag();
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return (null != QuotationModelsList ? QuotationModelsList.size() : 0);
        }

    }
    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}

