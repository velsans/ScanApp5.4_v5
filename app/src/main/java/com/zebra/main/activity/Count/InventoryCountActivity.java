package com.zebra.main.activity.Count;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.jb.Preference;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.zebra.R;
import com.zebra.adc.decoder.BarCodeReader;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.model.AdvanceSearchModel;
import com.zebra.main.model.InvCount.ScannedResultModel;
import com.zebra.main.sdl.SdlScanListener;
import com.zebra.main.sdl.SdlScanService;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class InventoryCountActivity extends AppCompatActivity implements SdlScanListener {
    static final private boolean saveSnapshot = false;
    private static final String TAG = "InventoryCount";
    AlertDialogManager alert = new AlertDialogManager();
    Intent service;
    String TreeNO, FellingID;
    String ClassName;
    Context context;
    // ui
    private EditText ScanValueETxT = null, treeNoEDT, fellingIDEDT;
    private TextView TotalScannedCount, sbbLabel_TXT, NoValueFoundTxT, LocationNameTXT, VolumeTXT;
    private ImageView image = null, AS_NovalueFound;
    private LinearLayout scannerListLay = null, advanceSearchLAY;
    // BarCodeReader specifics
    private BarCodeReader bcr = null;
    private WakeLock mWakeLock = null;
    private int motionEvents = 0, modechgEvents = 0, snapNum = 0;
    boolean bind = false;
    private SdlScanService scanService;
    private ScannedResultAdapter Scannedadapter;
    private AdvanceSearchResultAdapter advancedSearchadapter;
    private RecyclerView ScannedResultLV, advanceSearchRLV;
    //DatabaseHelper dbBackend;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;

    private static HSSFWorkbook myWorkBook = new HSSFWorkbook();
    private static HSSFSheet mySheet = myWorkBook.createSheet();
    MediaPlayer beepsound, wronBuzzer;
    LinearLayoutManager horizontalLayoutManager;
    AlertDialog.Builder Removebuilder = null;
    AlertDialog RemoveAlert = null;

    @Override
    protected void onStart() {
        super.onStart();
        beepsound = MediaPlayer.create(this, R.raw.beep);
        wronBuzzer = MediaPlayer.create(this, R.raw.wrong_buzzer);
        //ImportDataBaseFromInternalStorage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            //UpdateCountIDList();
            Log.v("", "onStop");
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            //UpdateCountIDList();
            Log.v("", "onPause");
            //scanService.release();
            if (scanService != null)
                scanService.setActivityUp(false);
            releaseWakeLock();
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("", "onDestroy");
        try {
           /* Common.ScannnedResultList = mDBInternalHelper.getScannedResultWithListID(Common.ListID);
            if (Common.ScannnedResultList.size() == 0) {
                // Remove Transfer List If empty
                DeleteTransferCountScannedList(Common.ListID);
            }*/
            if (bind) {
                if (!Preference.getScanSelfopenSupport(this, true)) {
                    this.stopService(service);
                }
                unbindService(serviceConnection);
                scanService = null;
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
        }
    }


    // Called with the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventorycount);
        mDBExternalHelper = new ExternalDataBaseHelperClass(InventoryCountActivity.this);
        mDBInternalHelper = new InternalDataBaseHelperClass(InventoryCountActivity.this);
        Initialization();
        Click_Listener();
        GetScannedResultList();
        //getclassificationAdapter();
        LocationNameTXT.setText(Common.ToLocationName);
        //enableSwipeToDeleteAndUndo();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_F9)) {
            ScanValueETxT.setText("");
            Common.ScanMode = true;
            scanService.doDecode();
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Signout("Are you sure you want to close?");
        }
        return true;
    }

    public boolean AdvaneSearchValidation(String UserName, String UserPassword) {
        if (UserName.equals("")) {
            AlertDialogBox("Tree Number", "please enter Tree number", false);
            treeNoEDT.setError("please enter Tree number");
            treeNoEDT.requestFocus();
            return false;
        } else if (UserPassword.equals("")) {
            AlertDialogBox("Felling ID", "Please enter felling id", false);
            fellingIDEDT.setError("Please enter felling id");
            fellingIDEDT.requestFocus();
            return false;
        }
        return true;
    }

    public void Click_Listener() {
        ScanValueETxT.setOnTouchListener((v, event) -> {
            Common.ScannedEditTXTFlag = true;
            return false;
        });

        ScanValueETxT.addTextChangedListener(new TextWatcher() {
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
                backSpace = lastLength > editable.toString().length();
                if (backSpace) {
                    if (editable.toString().length() > 0) {
                        if (!isValidBarCode(editable.toString())) {
                            ScanValueETxT.setError("Barcode should be like(XX-1111111)");
                        }
                    }
                } else {
                    if (editable.toString().length() > 0) {
                        String last = editable.toString().substring(editable.toString().length() - 1);
                        if (last.equals("-")) {

                        } else {
                            if (editable.toString().length() == 2) {
                                ScanValueETxT.append("-");
                            }
                            if (editable.toString().length() == Common.SBBlenght) {
                                if (!isValidBarCode(editable.toString())) {
                                    ScanValueETxT.setError("Barcode should be like(XX-1111111)");
                                }
                            } else {
                                if (Common.ScannedEditTXTFlag == true) {
                                    ScanValueETxT.setError(CommonMessage(R.string.BarCodeLenghtMsg));
                                }
                            }
                        }
                    }
                }
            }
        });

      /*  spin_classification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Common.classificationID = Common.classificationlist.get(position).getId();
                    Common.ClassificationName = Common.classificationlist.get(position).getCCode();
                } catch (Exception ex) {
                    //ImportDataBaseFromInternalStorage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

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
            Log.v("", "SDLActivity onServiceDisconnected %s" + bind);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bind = true;
            SdlScanService.MyBinder myBinder = (SdlScanService.MyBinder) service;
            scanService = myBinder.getService();
            //
            scanService.setOnScanListener(InventoryCountActivity.this);
            scanService.setActivityUp(true);
            Log.v("", "SDLActivity onServiceConnected  %s" + bind);
        }
    };

    private synchronized static void writeFile(File file, String value) {

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(value.getBytes());
            outputStream.flush();
            outputStream.close();

        } catch (FileNotFoundException ex) {
            CrashAnalytics.CrashReport(ex);
            ex.printStackTrace();
        } catch (IOException ex) {
            CrashAnalytics.CrashReport(ex);
            ex.printStackTrace();
        }
    }

    // Called when the activity is about to start interacting with the user.
    @Override
    protected void onResume() {
        Log.v("", "SDLActivity onResume bind:  %s" + bind);
        try {
            service = new Intent(this, SdlScanService.class);
            bindService(service, serviceConnection, BIND_AUTO_CREATE);
            startService(service);
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            //Log.e("InventoryCountActivity","Exceptionss: "+e+" mBeepManagersdl: "+mBeepManagersdl);
        }
        try {
            ScannedStatus("");//getResources().getString(R.string.app_name) + " v" + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (NotFoundException ex) {
            CrashAnalytics.CrashReport(ex);
            ex.printStackTrace();
        }
        if (scanService != null)
            scanService.setActivityUp(true);
        acquireWakeLock();
        GetScannedResultList();
        super.onResume();
    }

    private void Initialization() {
        // Inflate our UI from its XML layout description.
        findViewById(R.id.countScanBTN).setOnClickListener(mDecodeListener);
        findViewById(R.id.countEnterIMG).setOnClickListener(mScannerListener);
        findViewById(R.id.closeActivity).setOnClickListener(mCloseActivityListener);
        findViewById(R.id.advancedsearchTXT).setOnClickListener(as_OpenrListener);
        findViewById(R.id.as_closeActivity).setOnClickListener(as_mCloseActivityListener);
        findViewById(R.id.as_scannerList).setOnClickListener(as_mScannerListener);
        findViewById(R.id.as_NovalueFound).setOnClickListener(as_mAddListener);
        // spin_classification = findViewById(R.id.spin_classification);
        treeNoEDT = findViewById(R.id.as_treenoEDT);
        fellingIDEDT = findViewById(R.id.as_fellingEDT);

        scannerListLay = findViewById(R.id.scannerListlayout);
        advanceSearchRLV = findViewById(R.id.as_scan_lv);
        ScanValueETxT = findViewById(R.id.textStatus);
        ScannedResultLV = findViewById(R.id.inventorycount_lv);
        advanceSearchLAY = findViewById(R.id.advancesearchLAY);
        advanceSearchLAY.setVisibility(View.GONE);
        sbbLabel_TXT = findViewById(R.id.sbbLabel_TXT);
        NoValueFoundTxT = findViewById(R.id.NovalueFound);
        AS_NovalueFound = findViewById(R.id.as_NovalueFound);
        AS_NovalueFound.setVisibility(View.GONE);
        LocationNameTXT = findViewById(R.id.locationNameTXT);
        TotalScannedCount = findViewById(R.id.TotalScannedCount);
        VolumeTXT = findViewById(R.id.TotalScannedVolume);
    }

    OnClickListener as_mAddListener = new OnClickListener() {
        public void onClick(View v) {
            try {
                if (Common.IsSBBLabelCorrected == true) {
                    Common.BarCode = sbbLabel_TXT.getText().toString();
                    boolean BarcodeValidation = Common.BarCode.contains("-");
                    if (BarcodeValidation == true) {
                        String[] arrOfStr = Common.BarCode.split("-");
                        if (arrOfStr.length > 1) {
                            Common.SbbLabel = arrOfStr[1];
                        }
                    }
                    /*Common.SbbLabel = sbbLabel_TXT.getText().toString();
                    Common.BarCode = "NA-" + Common.SbbLabel;*/
                    Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                    //Common.IMEI = getImeiNumber();
                    Common.IsActive = 1;
                    Common.EntryMode = 3;
                    Common.WoodSpieceID = "";
                    Common.WoodSpieceCode = "";
                    Common.OrgSBBLabel = "";
                    Common.OrgBarCode = "";
                    Common.FellingSectionId = fellingIDEDT.getText().toString();
                    Common.TreeNumber = treeNoEDT.getText().toString();
                    Common.QualityName = "";
                    // Checked Duplicate In internal Tabel
                    boolean EmptyNullFlags = mDBInternalHelper.getScannedResultWithMapListIDCheck(Common.ListID, Common.BarCode, Common.SbbLabel);
                    if (EmptyNullFlags == true) {
                        Common.ScannnedResultList.clear();
                        Common.WoodSpieceID = "";
                        Common.WoodSpieceCode = "";
                        wronBuzzer.start();
                    } else {
                        boolean InisertAdvanceFlag = mDBInternalHelper.insertScannedResultFlag(Common.ToLocationID, Common.ToLocationName, Common.SbbLabel,
                                Common.WoodSpieceID, Common.WoodSpieceCode, Common.DateTime, Common.IMEI, Common.BarCode, Common.EntryMode, Common.IsActive,
                                Common.ListID, booleanToInt(Common.IsSBBLabelCorrected), Common.OrgBarCode, "0.00", Common.FellingSectionId, Common.TreeNumber,
                                Common.QualityName);
                        AS_NovalueFound.setVisibility(View.GONE);
                        advanceSearchLAY.setVisibility(View.GONE);
                    }
                    GetScannedResultList();
                    UpdateCountIDList();
                }
            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
                ex.printStackTrace();
            }
        }
    };

    OnClickListener mDecodeListener = new OnClickListener() {
        public void onClick(View v) {
            ScanValueETxT.setText("");
            Common.ScanMode = true;
            scanService.doDecode();
        }
    };

    OnClickListener as_OpenrListener = new OnClickListener() {
        public void onClick(View v) {
            Common.IsSBBLabelCorrected = false;
            sbbLabel_TXT.setText("");
            AdvanceSearchRefresh();
        }
    };

    OnClickListener as_mScannerListener = new OnClickListener() {
        public void onClick(View v) {
            AdvanceSearch();
        }
    };

    OnClickListener as_mCloseActivityListener = new OnClickListener() {
        public void onClick(View v) {
            advanceSearchLAY.setVisibility(View.GONE);
            GetScannedResultList();
        }
    };
    OnClickListener mScannerListener = v -> {
        try {
            Common.ScanMode = false;
            Common.HideKeyboard(InventoryCountActivity.this);
            String SBBLabel = ScanValueETxT.getText().toString();
            if (!isValidBarCode(SBBLabel)) {
                AlertDialogBox("Barcode Format", CommonMessage(R.string.ValidBarCodeMsg), false);
            } else {
                Common.EntryMode = 2;
                ScannedResult(SBBLabel);
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("Barcode Format", ex.toString(), false);
        }
       /* if (SBBLabel.length() == Common.SBBlenght) {
            Common.EntryMode = 2;
            ScannedResult(SBBLabel);
        } else {
            ScanValueETxT.setError(CommonMessage(R.string.ValidBarCodeMsg));
        }*/
    };

    OnClickListener mCloseActivityListener = new OnClickListener() {
        public void onClick(View v) {
            Signout("Are you sure you want to close?");
        }
    };

    public void AdvanceSearch() {
        try {
            if (AdvaneSearchValidation(treeNoEDT.getText().toString(), fellingIDEDT.getText().toString())) {
                TreeNO = treeNoEDT.getText().toString();
                FellingID = fellingIDEDT.getText().toString();
                AS_NovalueFound.setVisibility(View.GONE);
                Common.AdvancedSearchList.clear();
                Common.AdvancedSearchList = mDBExternalHelper.getAdvanceSearchList(TreeNO, FellingID, "In Stock");
                if (Common.AdvancedSearchList.size() == 0) {
                    Common.AdvancedSearchList = mDBExternalHelper.getAdvanceSearchList(TreeNO, FellingID, "Harvested");
                }
                if (Common.IsSBBLabelCorrected == true) {
                    if (Common.AdvancedSearchList.size() > 0) {
                        StringBuilder AdvanceSearchBarcodeStr = new StringBuilder();
                        StringBuilder AdvanceSearchSBBlabelStr = new StringBuilder();
                        for (int i = 0; i < Common.AdvancedSearchList.size(); i++) {
                            AdvanceSearchBarcodeStr.append("'" + Common.AdvancedSearchList.get(i).getBarCode() + "',");
                            AdvanceSearchSBBlabelStr.append("'" + Common.AdvancedSearchList.get(i).getSBBLabel() + "',");
                        }
                        boolean AdvanceSearchBtnFlag = mDBInternalHelper.getAdvanceSearchCountListCheck(Common.ListID, AdvanceSearchBarcodeStr.substring(0, AdvanceSearchBarcodeStr.toString().length() - 1), Common.AdvancedSearchList.size(), true, AdvanceSearchSBBlabelStr.substring(0, AdvanceSearchSBBlabelStr.toString().length() - 1));
                        if (AdvanceSearchBtnFlag == false) {
                            AS_NovalueFound.setVisibility(View.GONE);
                        } else {
                            AS_NovalueFound.setVisibility(View.VISIBLE);
                        }
                        RefreshAdvancedSearchList();
                    } else {
                        AS_NovalueFound.setVisibility(View.VISIBLE);
                    }
                } else {
                    AS_NovalueFound.setVisibility(View.GONE);
                    RefreshAdvancedSearchList();
                }
                 Common.HideKeyboard(InventoryCountActivity.this);
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            Log.d("", "Exception : %s"+ ex.toString());
        }
    }

    // display status string
    public void ScannedStatus(String s) {
        Log.v("", "ScannedStatus : %s"+ s);
        // tvStat.setText(s);
    }

    // display status resource id
    public void ScannedStatus(int id) {
        Log.v("", "ScannedStatus : %s" + id);
        //tvStat.setText(id);
    }

    // display error msg
    private void dspErr(String s) {
        Log.v("", "ScannedStatus : %s"+ s);
        //tvStat.setText("ERROR" + s);
    }

    // display status string
    public void ScannedResult(String s) {
         Common.HideKeyboard(InventoryCountActivity.this);
        try {
            if (s != null && s.length() > 0) {
                // Common.ScannnedResultListMap.clear();
                //if (Common.ScanMode == true) {
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
               /* } else {
                    Common.SbbLabel = s;
                    Common.BarCode = "NA-" + Common.SbbLabel;
                }*/
                Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                // Common.IMEI = getImeiNumber();
                Common.IsActive = 1;
                // Checked Duplicate In Internal Tabel
                boolean EmptyNullFlags = mDBInternalHelper.getScannedResultWithMapListIDCheck(Common.ListID, Common.BarCode, Common.SbbLabel);
                if (EmptyNullFlags == true) {
                    Common.ScannnedResultList.clear();
                    Common.WoodSpieceID = "";
                    Common.WoodSpieceCode = "";
                    Common.FellingSectionId = "";
                    Common.TreeNumber = "";
                    wronBuzzer.start();
                    GetScannedResultList();
                    UpdateCountIDList();
                    return;
                }
                //Search the master table for SBB label entered or scanned
                Common.SearchedTransLogDetils = mDBExternalHelper.getBarCodeTransferLogDetails(Common.BarCode);
                //Common.SearchedTransLogDetils = Common.AllTransLogDetailsmap.get(Common.BarCode);
                if (Common.SearchedTransLogDetils.size() > 0) {
                    Common.WoodSpieceID = Common.SearchedTransLogDetils.get(0).getWoodSpeciesId();
                    Common.WoodSpieceCode = Common.SearchedTransLogDetils.get(0).getWoodSpeciesCode();
                    Common.Volume = Common.SearchedTransLogDetils.get(0).getVolume();
                    Common.FellingSectionId = Common.SearchedTransLogDetils.get(0).getFellingSectionId();
                    Common.TreeNumber = Common.SearchedTransLogDetils.get(0).getTreeNumber();
                    Common.QualityName = Common.SearchedTransLogDetils.get(0).getQuality();
                    Common.IsSBBLabelCorrected = false;
                    InsertScannedResultTable();
                } else {
                    Common.WoodSpieceID = "";
                    Common.WoodSpieceCode = "";
                    Common.Volume = "0.00";
                    Common.FellingSectionId = "";
                    Common.TreeNumber = "";
                    Common.QualityName = "";
                    Common.IsSBBLabelCorrected = true;
                    sbbLabel_TXT.setText(Common.BarCode);
                    AS_NovalueFound.setVisibility(View.VISIBLE);
                    AdvanceSearchRefresh();
                }
                Common.EntryMode = 1;
                Common.ScannedEditTXTFlag = false;
                ScanValueETxT.setText(s);
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            Log.v("decode failed : %s", ex.toString());
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

    public void Signout(String ErrorMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(InventoryCountActivity.this);
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
                           /* Common.ScannnedResultList = mDBInternalHelper.getScannedResultWithListID(Common.ListID);
                            Log.v(TAG, "setNegativeButton" + Common.ScannnedResultList.size());
                            if (Common.ScannnedResultList.size() == 0) {
                                // Remove Transfer List If empty
                                DeleteTransferCountScannedList(Common.ListID);
                            }*/
                            UpdateCountIDList();
                            InventoryActivty();
                        } catch (Exception ex) {
                            CrashAnalytics.CrashReport(ex);
                            AlertDialogBox("DeleteTransferListandTransferScannedList", ex.toString(), false);
                        }


                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void GetScannedResultList() {
        try {
            Common.ScannnedResultList = mDBInternalHelper.getScannedResultWithListID(Common.ListID);
            Double RemoveVolumeSum = 0.00;
            if (Common.ScannnedResultList.size() > 0) {
                Scannedadapter = new ScannedResultAdapter(Common.ScannnedResultList, Common.QulaityDefaultList, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                ScannedResultLV.setLayoutManager(horizontalLayoutManager);
                Scannedadapter.notifyDataSetChanged();
                ScannedResultLV.setAdapter(Scannedadapter);
                NoValueFoundTxT.setVisibility(View.GONE);
                ScannedResultLV.setVisibility(View.VISIBLE);
                RemoveVolumeSum = TotalVolume(Common.ScannnedResultList);
            } else {
                NoValueFoundTxT.setVisibility(View.VISIBLE);
                ScannedResultLV.setVisibility(View.GONE);
            }
            TotalScannedCount.setText(String.valueOf(Common.ScannnedResultList.size()));
            VolumeTXT.setText(String.valueOf(Common.decimalFormat.format(RemoveVolumeSum)));
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            Log.d("Exception : %s", ex.toString());
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
                // process any other events here
                break;
        }
    }

    private Bitmap rotated(Bitmap bmSnap) {
        Matrix matrix = new Matrix();
        if (matrix != null) {
            matrix.postRotate(90);
            // create new bitmap from orig tranformed by matrix
            Bitmap bmr = Bitmap.createBitmap(bmSnap, 0, 0, bmSnap.getWidth(), bmSnap.getHeight(), matrix, true);
            if (bmr != null)
                return bmr;
        }

        return bmSnap;        //when all else fails
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
            //mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "ZHENGYI.WZY");
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

    public void InsertScannedResultTable() {
        beepsound.start();
        // Insert values into Scanned Result
        mDBInternalHelper.insertScannedResult(Common.ToLocationID, Common.ToLocationName, Common.SbbLabel, Common.WoodSpieceID,
                Common.WoodSpieceCode, Common.DateTime, Common.IMEI, Common.BarCode, Common.EntryMode, Common.IsActive, Common.ListID,
                booleanToInt(Common.IsSBBLabelCorrected), "", Common.Volume, Common.FellingSectionId, Common.TreeNumber,
                Common.QualityName);

        //Scanned Result Refresh
        GetScannedResultList();
        UpdateCountIDList();
    }

    public void AdvanceSearchRefresh() {
        advanceSearchLAY.setVisibility(View.VISIBLE);
        treeNoEDT.setText("");
        fellingIDEDT.setText("");
        Common.AdvancedSearchList.clear();
        RefreshAdvancedSearchList();
    }

    private static void excelLog(int row, int col, String value) {
        HSSFRow myRow = mySheet.getRow(row);
        if (myRow == null)
            myRow = mySheet.createRow(row);
        HSSFCell myCell = myRow.createCell(col);
        myCell.setCellValue(value);
    }

    public class ScannedResultAdapter extends RecyclerView.Adapter<ScannedResultAdapter.GroceryViewHolder> {
        private List<ScannedResultModel> ScannedResultList;
        private List<String> QualitySpinner;
        Context context;


        public ScannedResultAdapter(List<ScannedResultModel> ScannedResultList, ArrayList<String> SpinnerData, Context context) {
            this.QualitySpinner = SpinnerData;
            this.ScannedResultList = ScannedResultList;
            this.context = context;
        }

        public void removeItem(int position) {
            ScannedResultList.remove(position);
            notifyItemRemoved(position);
        }

        public ScannedResultModel getItem(int position) {
            return ScannedResultList.get(position);
        }
        /*   public void restoreItem(String item, int position) {
               ScannedResultList.add(position, item);
               notifyItemInserted(position);
           }
   */
       /* public ArrayList<ScannedResultModel> getData() {
            return ScannedResultList;
        }*/

        @Override
        public GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.scanned_infliator, parent, false);
            GroceryViewHolder gvh = new GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(GroceryViewHolder holder, final int position) {
            holder.Barcode.setText(ScannedResultList.get(position).getBarCode());  //barcode
            holder.WoodSpiceCode.setText(ScannedResultList.get(position).getWoodSpieceCode());//specie
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
            int color = generator.getColor(getItem(position));
            holder.Remove.setBackgroundColor(color);
            holder.Remove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.RemoveSBBLabel = "";
                    Common.RemoveSBBLabel = ScannedResultList.get(position).getBarCode();
                    RemoveMessage(CommonMessage(R.string.Remove_Message));
                }
            });

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, QualitySpinner);
            holder.QualitySpinner.setAdapter(adapter);
            if (ScannedResultList.get(position).getQuality() != null) {
                int spinnerPosition = adapter.getPosition(ScannedResultList.get(position).getQuality());
                holder.QualitySpinner.setSelection(spinnerPosition);
            }
            holder.QualitySpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Common.isSpinnerTouched = true;
                    return false;
                }
            });
            holder.QualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int inside_position, long id) {
                    try {
                        if (Common.isSpinnerTouched == true) {
                            Common.QualityName = QualitySpinner.get(inside_position);
                            String sBBLabel = ScannedResultList.get(position).getSbbLabel();
                            boolean UpdateFlag = mDBInternalHelper.UpdateQualityCountList(Common.QualityName, sBBLabel, Common.ListID);
                            Common.isSpinnerTouched = false;
                        }
                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return ScannedResultList.size();
        }

        public class GroceryViewHolder extends RecyclerView.ViewHolder {
            TextView Barcode, WoodSpiceCode;
            Spinner QualitySpinner;
            LinearLayout Background, Remove;

            public GroceryViewHolder(View view) {
                super(view);
                Barcode = view.findViewById(R.id.count_sbblabel);
                WoodSpiceCode = view.findViewById(R.id.count_speiceCode);
                QualitySpinner = view.findViewById(R.id.count_qualitySpinner);
                Background = view.findViewById(R.id.countlayoutBackground);
                Remove = view.findViewById(R.id.count_removeBarCode);
            }
        }
    }

    public class AdvanceSearchResultAdapter extends RecyclerView.Adapter<AdvanceSearchResultAdapter.GroceryViewHolder> {
        private List<AdvanceSearchModel> AdvanceSearchResultList;
        //private List<AddButtonModel> AddIntabelList;
        Context context;

        public AdvanceSearchResultAdapter(List<AdvanceSearchModel> ScannedResultList, Context context) {
            this.AdvanceSearchResultList = ScannedResultList;
            //this.AddIntabelList = AddInTabel;
            this.context = context;
        }

        @Override
        public GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.advance_search_infliator, parent, false);
            GroceryViewHolder gvh = new GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(GroceryViewHolder holder, final int position) {
            if (position % 2 == 0) {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.Background.setBackgroundColor(context.getResources().getColor(R.color.color_white));
            }
            holder.SBBLabel.setText(AdvanceSearchResultList.get(position).getBarCode());//barcode
            holder.WSpiceCode.setText(AdvanceSearchResultList.get(position).getWoodSpeciesCode());
            holder.Lenght.setText(AdvanceSearchResultList.get(position).getLength_dm());//specie
            holder.Volume.setText(AdvanceSearchResultList.get(position).getVolume());  //specie
            holder.F1.setText(AdvanceSearchResultList.get(position).getF1());
            holder.F2.setText(AdvanceSearchResultList.get(position).getF2());
            holder.T1.setText(AdvanceSearchResultList.get(position).getT1());
            holder.T2.setText(AdvanceSearchResultList.get(position).getT2());
            try {
                boolean AdvanceSearchBtnFlag = mDBInternalHelper.getAdvanceSearchCountListCheck(Common.ListID, AdvanceSearchResultList.get(position).getBarCode(), 0, false, AdvanceSearchResultList.get(position).getSBBLabel());
                if (AdvanceSearchBtnFlag == true) {
                    holder.Select.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    holder.Select.setText("In Table");
                    holder.Select.setTextColor(getResources().getColor(R.color.edittextColor));
                } else {
                    holder.Select.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    holder.Select.setTextColor(getResources().getColor(R.color.color_white));
                    holder.Select.setText("ADD");
                }
            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
                ex.printStackTrace();
            }

            holder.Select.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String SelectTxTvalue = holder.Select.getText().toString();
                        if (SelectTxTvalue.equals("ADD")) {
                            Common.SbbLabel = AdvanceSearchResultList.get(position).getSBBLabel();
                            Common.BarCode = AdvanceSearchResultList.get(position).getBarCode();
                            Common.Volume = AdvanceSearchResultList.get(position).getVolume();
                            boolean EmptyNullFlags = mDBInternalHelper.getScannedResultWithMapListIDCheck(Common.ListID, Common.BarCode, Common.SbbLabel);
                            if (EmptyNullFlags == true) {
                                wronBuzzer.start();
                                return;
                            }
                            Common.DateTime = Common.dateFormat.format(Calendar.getInstance().getTime());
                            //Common.IMEI = getImeiNumber();
                            Common.IsActive = 1;
                            Common.EntryMode = 3;
                            Common.WoodSpieceID = String.valueOf(AdvanceSearchResultList.get(position).getWoodSpeciesId());
                            Common.WoodSpieceCode = AdvanceSearchResultList.get(position).getWoodSpeciesCode();
                            Common.FellingSectionId = AdvanceSearchResultList.get(position).getFellingSectionId();
                            Common.TreeNumber = AdvanceSearchResultList.get(position).getTreeNumber();
                            Common.QualityName = AdvanceSearchResultList.get(position).getClassification();
                            try {
                                boolean InisertAdvanceFlag = false;
                                if (Common.IsSBBLabelCorrected == true) {
                                    Common.BarCode = sbbLabel_TXT.getText().toString();
                                    Common.OrgBarCode = Common.BarCode;
                                    boolean BarcodeValidation = Common.BarCode.contains("-");
                                    if (BarcodeValidation == true) {
                                        String[] arrOfStr = Common.BarCode.split("-");
                                        if (arrOfStr.length > 1) {
                                            Common.OrgSBBLabel = arrOfStr[1];
                                        }
                                    }
                                    InisertAdvanceFlag = mDBInternalHelper.insertScannedResultFlag(Common.ToLocationID, Common.ToLocationName, Common.OrgBarCode,
                                            Common.WoodSpieceID, Common.WoodSpieceCode, Common.DateTime, Common.IMEI, Common.BarCode, Common.EntryMode, Common.IsActive,
                                            Common.ListID, booleanToInt(Common.IsSBBLabelCorrected), Common.SbbLabel, Common.Volume, Common.FellingSectionId,
                                            Common.TreeNumber, Common.QualityName);
                                } else {
                                    Common.OrgSBBLabel = "";
                                    Common.OrgBarCode = "";
                                    Common.BarCode = AdvanceSearchResultList.get(position).getBarCode();
                                    InisertAdvanceFlag = mDBInternalHelper.insertScannedResultFlag(Common.ToLocationID, Common.ToLocationName, Common.SbbLabel,
                                            Common.WoodSpieceID, Common.WoodSpieceCode, Common.DateTime, Common.IMEI, Common.BarCode, Common.EntryMode, Common.IsActive,
                                            Common.ListID, booleanToInt(Common.IsSBBLabelCorrected), Common.OrgBarCode, Common.Volume, Common.FellingSectionId, Common.TreeNumber,
                                            Common.QualityName);
                                }
                                if (InisertAdvanceFlag == true) {
                                    Common.OrgSBBLabel = "";
                                    Common.OrgBarCode = "";
                                    Common.EntryMode = 1;
                                    advanceSearchLAY.setVisibility(View.GONE);
                                    // Scanned Result List Refresh
                                    GetScannedResultList();
                                    UpdateCountIDList();
                                } else {
                                    wronBuzzer.start();
                                }
                            } catch (Exception ex) {
                                CrashAnalytics.CrashReport(ex);
                                AlertDialogBox("Advacne Search", ex.toString(), false);
                            }
                        } else {
                            wronBuzzer.start();
                        }
                    } catch (Exception ex) {
                        CrashAnalytics.CrashReport(ex);
                        AlertDialogBox("Advance Search", ex.toString(), false);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return AdvanceSearchResultList.size();
        }

        public class GroceryViewHolder extends RecyclerView.ViewHolder {
            TextView SBBLabel, WSpiceCode, Lenght, Volume, Select, F1, F2, T1, T2;
            LinearLayout Background;

            public GroceryViewHolder(View view) {
                super(view);
                SBBLabel = view.findViewById(R.id.as_sbblabel);
                WSpiceCode = view.findViewById(R.id.as_woodspiceCode);
                Lenght = view.findViewById(R.id.as_lenght);
                Volume = view.findViewById(R.id.as_volume);
                Background = view.findViewById(R.id.as_scannerlayoutBackground);
                Select = view.findViewById(R.id.as_select);
                F1 = view.findViewById(R.id.as_F1);
                F2 = view.findViewById(R.id.as_F2);
                T1 = view.findViewById(R.id.as_T1);
                T2 = view.findViewById(R.id.as_T2);
            }
        }
    }

    public void RefreshAdvancedSearchList() {
        try {
            if (Common.AdvancedSearchList.size() > 0) {
                advancedSearchadapter = new AdvanceSearchResultAdapter(Common.AdvancedSearchList, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                advanceSearchRLV.setLayoutManager(horizontalLayoutManager);
                advanceSearchRLV.setAdapter(advancedSearchadapter);
                advanceSearchRLV.setVisibility(View.VISIBLE);
            } else {
                advanceSearchRLV.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            ex.printStackTrace();
        }
    }

    public static <T> boolean IsNullOrEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(InventoryCountActivity.this, Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public String CommonMessage(int Common_Msg) {
        return InventoryCountActivity.this.getResources().getString(Common_Msg);
    }

    public void RemoveMessage(String ErrorMessage) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(InventoryCountActivity.this);
        Removebuilder.setMessage(ErrorMessage);
        Removebuilder.setCancelable(true);
        Removebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Removebuilder.setNegativeButton(CommonMessage(R.string.action_Remove),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            boolean Isdelete = mDBInternalHelper.RemoveFromScanneList(Common.RemoveSBBLabel, 0, Common.ListID);
                            if (Isdelete == true) {
                                Toast.makeText(InventoryCountActivity.this, "Successfully Removed from List", Toast.LENGTH_LONG).show();
                                GetScannedResultList();
                                UpdateCountIDList();
                            }
                        } catch (Exception ex) {
                            CrashAnalytics.CrashReport(ex);
                        }
                        dialog.cancel();
                    }
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public void UpdateCountIDList() {
        try {
            Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            Double RemoveVolumeSum = TotalVolume(Common.ScannnedResultList);
            // Update values into ListID
            boolean ListIdFlag = mDBInternalHelper.UpdateInventoryCountListID(Common.EndDate, Common.ScannnedResultList.size(), Common.ListID, String.valueOf(RemoveVolumeSum), 1, Common.CountUniqueID);
            /*if (ListIdFlag == true) {
                Common.InventoryCountList = mDBInternalHelper.getInventoryCountIdList();
            }*/
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("UpdateCountIDList", ex.toString(), false);
        }
    }

    public double TotalVolume(ArrayList<ScannedResultModel> TotalScannedList) {
        double TotVolume = 0.00;
        for (ScannedResultModel inventocountScanModel : TotalScannedList) {
            TotVolume = TotVolume + inventocountScanModel.getVolume();
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
            CrashAnalytics.CrashReport(ex);
            AlertDialogBox("DeleteTransferCountandTransferScannedList", ex.toString(), false);
        }
    }

    public void InventoryActivty() {
        Intent _gwwIntent = new Intent(InventoryCountActivity.this, InventoryCountListActivity.class);
        startActivity(_gwwIntent);
    }

    // BarCode email id
    private boolean isValidBarCode(String barCode) {
        String BarCodeValidation =
                "[a-zA-Z]{2}" +
                        "\\-" +
                        "[0-9]{7}";
        Pattern pattern = Pattern.compile(BarCodeValidation);
        Matcher matcher = pattern.matcher(barCode);
        return matcher.matches();
    }
}
