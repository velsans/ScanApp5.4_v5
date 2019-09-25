
package com.zebra.main.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.tscdll.TSCActivity;
import com.zebra.R;
import com.zebra.android.jb.Preference;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.adapter.FellingSectionAdapter;
import com.zebra.main.interfac.FellingFilterInterface;
import com.zebra.main.model.ExternalDB.ConcessionNamesModel;
import com.zebra.main.model.ExternalDB.FellingSectionModel;
import com.zebra.main.model.ExternalDB.WoodSpeciesModel;
import com.zebra.main.model.FellingRegistration.FellingRegisterResultModel;
import com.zebra.main.model.FellingRegistration.FellingTreeDetailsModel;
import com.zebra.main.sdl.SdlScanListener;
import com.zebra.main.sdl.SdlScanService;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.BlueTooth;
import com.zebra.utilities.BlutoothCommonClass;
import com.zebra.utilities.Common;
import com.zebra.utilities.GwwException;
import com.zebra.utilities.PrintSlipsClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FellingRegistrationActivity extends Activity implements SdlScanListener {
    private static final String TAG = "FellingRegistration";
    AlertDialogManager alert = new AlertDialogManager();
    Intent service;
    EditText FellingRegNoEDTxT, fellingRegtext, TreeF1, TreeF2, TreeT1, TreeT2, TreeLenght, SbbLabelF1, SbbLabelF2, SbbLabelT1, SbbLabelT2,
            SbbLabelLenght, UpdateSbbLabelF1, UpdateSbbLabelF2, UpdateSbbLabelT1, UpdateSbbLabelT2, UpdateSbbLabelLenght, UpdateSbbLabelNoteF, UpdateSbbLabelNoteT, UpdateSbbLabelNoteL,
            FellingRegSbblabel_NoteF, FellingRegSbblabel_NoteT, FellingRegSbblabel_NoteL, UpdateTreeNumber, UpdateTreeNumberF1, UpdateTreeNumberF2, UpdateTreeNumberT1, UpdateTreeNumberT2,
            UpdateTreeNumberLenght;
    Spinner FellingSecIdSpin, fellingTreenoSpinner, fellingQualitySpinner;
    TextView deviceLocationTXT, RegistrationdateTxT, TotalCountRegistration, TotalVolumeRegistration, NovalueFound, AddTreeNoTxT, UpdateSbbLabelSave, UpdateTreeNumberSave, SbbLabelVolume;

    LinearLayout SbbLableUpdateLayout, Layout_Note, HideDiamantionLayout, TreeUpdateLayout;
    CheckBox Note_Checked;
    RecyclerView FellingRegistrationList;
    FloatingActionButton fellingRegistrationScanBTN;
    ImageView fellingRegEnterManual, NextButton, closeButton, PrintButton, UpdateLayoutClose, AddTreeNumberImg, TreeUpdatelayoutClose;
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;
    private SdlScanService scanService;
    AlertDialog.Builder Removebuilder = null;
    AlertDialog RemoveAlert = null;
    private PowerManager.WakeLock mWakeLock = null;
    boolean bind = false;
    MediaPlayer beepsound, wronBuzzer;
    private FellingRegisterAdapter fellingRegisterAdapter;
    LinearLayoutManager horizontalLayoutManager;
    FellingSectionAdapter fellingSectionAdapter;
    private List<String> QualitySpinner;
    AutoCompleteTextView fellingTreenoAutoTxT, FellingWSpecieAutoTxT, FellingPlotTxT, UpdateTreeNumberWSC, UpdatePlotNumber, TreePartAUTOTXT, UpdateTreePartType;
    ArrayAdapter<String> StringtreeNoadapter;
    ArrayAdapter<String> StringPlotNoadapter;
    ArrayAdapter<String> StringTreePartadapter;
    List<String> TreePartlist = new ArrayList<>();
    LinearLayout EditOptionFlagLay;
    TSCActivity tsc = new TSCActivity();
    int TSCConnInt = -1, REQUEST_ENABLE_BT = 2;
    PrintSlipsClass printSlip;
    int FinalTreeNumber;

    @Override
    protected void onStart() {
        super.onStart();
        beepsound = MediaPlayer.create(this, R.raw.beep);
        wronBuzzer = MediaPlayer.create(this, R.raw.wrong_buzzer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fellingregister);
        mDBExternalHelper = new ExternalDataBaseHelperClass(FellingRegistrationActivity.this);
        mDBInternalHelper = new InternalDataBaseHelperClass(FellingRegistrationActivity.this);
        printSlip = new PrintSlipsClass(FellingRegistrationActivity.this);
        Initialization();
        ViewFellingList(Common.IsEditorViewFlag);
        Click_Listener();
        getfellingSectionSpinner();
        getQualitySpinner();
        GetTreePart();
        deviceLocationTXT.setText(Common.FromLocationname);
        Common.FellingRegDate = Common.dateFormat.format(Calendar.getInstance().getTime());
        RegistrationdateTxT.setText(Common.FellingRegDate);
        /*Common.TreeNosList.clear();
        TreeNoList(Common.TreeNosList);*/
        if (Common.IsFellingRegEditListFlag == false) {
            FellingRegNoEDTxT.setEnabled(false);
            FellingRegEditValues();
        }
        FellingRegistrationList.setVisibility(View.VISIBLE);
        GetFellingRegResultList();

        Note_Checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    Layout_Note.setVisibility(View.VISIBLE);
                } else {
                    Layout_Note.setVisibility(View.GONE);
                    HideKeyboard();
                }
            }
        });
    }

    public void GetTreePart() {
        TreePartlist = Arrays.asList(Common.FellingRegTreePartStringList);
        StringTreePartadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Common.FellingRegTreePartStringList);
        StringTreePartadapter.notifyDataSetChanged();
        TreePartAUTOTXT.setAdapter(StringTreePartadapter);
        UpdateTreePartType.setAdapter(StringTreePartadapter);
    }

    public void TreeNoList(String fellingSecId) {
        try {
            Common.FellingRegTreeFilterList = mDBExternalHelper.getFellingRegisterFilter(Common.FromLocationID, fellingSecId);
            ArrayList<String> AddedNewTreeNumbers = mDBInternalHelper.getTreeNumberFromInternal(fellingSecId);
            Common.FellingRegTreeNoStringList = new String[Common.FellingRegTreeFilterList.size() + AddedNewTreeNumbers.size()];
            for (int i = 0; i < Common.FellingRegTreeFilterList.size(); i++) {
                Common.FellingRegTreeNoStringList[i] = String.valueOf(Common.FellingRegTreeFilterList.get(i).getTreeNumber());
            }
            for (int i = 0; i < AddedNewTreeNumbers.size(); i++) {
                Common.FellingRegTreeNoStringList[Common.FellingRegTreeFilterList.size() + i] = AddedNewTreeNumbers.get(i);
            }
            StringtreeNoadapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, Common.FellingRegTreeNoStringList);
            StringtreeNoadapter.notifyDataSetChanged();
            fellingTreenoAutoTxT.setAdapter(StringtreeNoadapter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void PlotNumberList(String fellingSecId) {
        try {
            Common.FellingRegPlotNumberFilterList = mDBExternalHelper.getFellingRegisterFilterWithDistinct(fellingSecId);
            Common.FellingRegPlotNoStringList = new String[Common.FellingRegPlotNumberFilterList.size()];
            for (int i = 0; i < Common.FellingRegPlotNumberFilterList.size(); i++) {
                Common.FellingRegPlotNoStringList[i] = String.valueOf(Common.FellingRegPlotNumberFilterList.get(i).getPlotNumber());
            }
            StringPlotNoadapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, Common.FellingRegPlotNoStringList);
            StringPlotNoadapter.notifyDataSetChanged();
            FellingPlotTxT.setAdapter(StringPlotNoadapter);
            UpdatePlotNumber.setAdapter(StringPlotNoadapter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void WoodSpeicesList(ArrayList<WoodSpeciesModel> WoodSpeicesSList) {
        try {
            if (WoodSpeicesSList.size() > 0) {
                ArrayList<String> WoodSpiceArrray = new ArrayList<>();
                for (WoodSpeciesModel WsModel : WoodSpeicesSList) {
                    WoodSpiceArrray.add(WsModel.getWoodSpeciesCode());
                }
                Common.FellingRegWoodSpeicesStringList = WoodSpiceArrray.toArray(new String[0]);
                StringtreeNoadapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_dropdown_item_1line, Common.FellingRegWoodSpeicesStringList);
                StringtreeNoadapter.notifyDataSetChanged();
                FellingWSpecieAutoTxT.setAdapter(StringtreeNoadapter);
                UpdateTreeNumberWSC.setAdapter(StringtreeNoadapter);
            }
        } catch (Exception ex) {

        }
    }

    public void GetTreeNumber(String EnterTreeNumber) {
        try {
            if (Common.FellingSectionId.equals("Select FellingSectionID")) {
                AlertDialogBox("Felling SectionId", "Please check FellingSectionId is empty!", false);
                fellingTreenoAutoTxT.setText("");
                Common.FsTreeNumber = "";
            } else {
                boolean TreeNumberCheck = mDBInternalHelper.getTreeNumberDuplicationCheck(Common.FromLocationID, Common.FellingSectionId, EnterTreeNumber);
                Common.FellingTreeDetailsCheckList.clear();
                Common.FellingTreeDetailsCheckList = mDBInternalHelper.getTreeNumberDuplication(Common.FromLocationID, Common.FellingSectionId, EnterTreeNumber);
                if (Common.FellingTreeDetailsCheckList.size() > 0) {
                    Common.FsWoodSpieceCode = Common.FellingTreeDetailsCheckList.get(0).getWoodSpieceCode();
                    //Common.OldWSCode = Common.FsWoodSpieceCode;
                    Common.PlotNo = Common.FellingTreeDetailsCheckList.get(0).getPlotNumber();
                    FellingWSpecieAutoTxT.setText(Common.FsWoodSpieceCode);
                    FellingPlotTxT.setText(Common.PlotNo);
                    TreeF1.setText(Common.FellingTreeDetailsCheckList.get(0).getFooter_1());
                    TreeF2.setText(Common.FellingTreeDetailsCheckList.get(0).getFooter_2());
                    TreeT1.setText(Common.FellingTreeDetailsCheckList.get(0).getTop_1());
                    TreeT2.setText(Common.FellingTreeDetailsCheckList.get(0).getTop_2());
                    TreeLenght.setText(Common.FellingTreeDetailsCheckList.get(0).getLength());
                    //if (TreeNumberCheck == true) {
                } else {
                    String[] WsCAndPlotNO = mDBExternalHelper.getFellingRegisterWoodSpiceCode(String.valueOf(Common.FromLocationID), EnterTreeNumber, Common.FellingSectionId);
                    if (WsCAndPlotNO.length > 0) {
                        Common.FsWoodSpieceCode = WsCAndPlotNO[0].toString();
                        Common.PlotNo = WsCAndPlotNO[1].toString();
                        Common.PlotId = Integer.parseInt(WsCAndPlotNO[2].toString());
                    }
                    FellingWSpecieAutoTxT.setText(Common.FsWoodSpieceCode);
                    FellingPlotTxT.setText(Common.PlotNo);
                    Common.OldWSCode = Common.FsWoodSpieceCode;
                    TreeF1.setText("");
                    TreeF2.setText("");
                    TreeT1.setText("");
                    TreeT2.setText("");
                    TreeLenght.setText("");
                    TreePartAUTOTXT.setText("");
                    if (Common.FsWoodSpieceCode.length() == 0) {
                        //AddTreeNumber();
                        Common.IsNewTreeNumber = 1;
                    } else {
                        Common.IsNewTreeNumber = 0;
                    }
                }
            }
          /*  Common.FellingTreeDetailsCheckList.clear();
            Common.FellingTreeDetailsCheckList = mDBInternalHelper.getTreeNumberDuplication(Common.FromLocationID, Common.FellingSectionId, EnterTreeNumber);
            if (Common.FellingTreeDetailsCheckList.size() > 0) {
                Common.FsWoodSpieceCode = Common.FellingTreeDetailsCheckList.get(0).getWoodSpieceCode();
                //Common.OldWSCode = Common.FsWoodSpieceCode;
                Common.PlotNo = Common.FellingTreeDetailsCheckList.get(0).getPlotNumber();
                FellingWSpecieAutoTxT.setText(Common.FsWoodSpieceCode);
                FellingPlotTxT.setText(Common.PlotNo);
                TreeF1.setText(Common.FellingTreeDetailsCheckList.get(0).getFooter_1());
                TreeF2.setText(Common.FellingTreeDetailsCheckList.get(0).getFooter_2());
                TreeT1.setText(Common.FellingTreeDetailsCheckList.get(0).getTop_1());
                TreeT2.setText(Common.FellingTreeDetailsCheckList.get(0).getTop_2());
                TreeLenght.setText(Common.FellingTreeDetailsCheckList.get(0).getLength());
            } else {
                FellingWSpecieAutoTxT.setText("");
                FellingPlotTxT.setText("");
                TreeF1.setText("");
                TreeF2.setText("");
                TreeT1.setText("");
                TreeT2.setText("");
                TreeLenght.setText("");
            }*/
        } catch (Exception ex) {

        }
    }

    public double VolumeCalculation() {
        double TotalVolume = 0.00, LogVolume = 0.00, CavityVolume = 0.00;
        if (!SbbLabelF1.getText().toString().equals("") && !SbbLabelF2.getText().toString().equals("") && !SbbLabelT1.getText().toString().equals("") && !SbbLabelT2.getText().toString().equals("") && !SbbLabelLenght.getText().toString().equals("")) {
            double F1 = Double.parseDouble(SbbLabelF1.getText().toString());
            double F2 = Double.parseDouble(SbbLabelF2.getText().toString());
            double T1 = Double.parseDouble(SbbLabelT1.getText().toString());
            double T2 = Double.parseDouble(SbbLabelT2.getText().toString());
            double LENGTH = Double.parseDouble(SbbLabelLenght.getText().toString());
            double Diameter = ((F1 + F2 + T1 + T2) / 4);
            LogVolume = (Diameter * Diameter * Common.DensityOFWood * LENGTH) / 10000;
        }
        /*Diameter Calculation*/
        if (!FellingRegSbblabel_NoteF.getText().toString().equals("") || !FellingRegSbblabel_NoteT.getText().toString().equals("") && !FellingRegSbblabel_NoteL.getText().toString().equals("")) {
            double F = 0.00, T = 0.00, L = 0.00, CavityAvergDiameter = 0.00;
            if (FellingRegSbblabel_NoteF.getText().toString().length() > 0) {
                F = Double.parseDouble(FellingRegSbblabel_NoteF.getText().toString());
            }
            if (FellingRegSbblabel_NoteT.getText().toString().length() > 0) {
                T = Double.parseDouble(FellingRegSbblabel_NoteT.getText().toString());
            }
            if (FellingRegSbblabel_NoteL.getText().toString().length() > 0) {
                L = Double.parseDouble(FellingRegSbblabel_NoteL.getText().toString());
            }
            if (!FellingRegSbblabel_NoteF.getText().toString().equals("") && !FellingRegSbblabel_NoteT.getText().toString().equals("")) {
                CavityAvergDiameter = ((F + T) / 2);
            } else if (!FellingRegSbblabel_NoteT.getText().toString().equals("") || !FellingRegSbblabel_NoteT.getText().toString().equals("")) {
                CavityAvergDiameter = ((F + T) / 1);
            }

            CavityVolume = (CavityAvergDiameter * CavityAvergDiameter * Common.DensityOFWood * L) / 10000;
        }
        TotalVolume = LogVolume - CavityVolume;
        return TotalVolume;
    }

    public double UpdateVolumeCalculation() {
        double TotalVolume = 0.00, LogVolume = 0.00, CavityVolume = 0.00;
        if (!UpdateSbbLabelF1.getText().toString().equals("") && !UpdateSbbLabelF2.getText().toString().equals("") && !UpdateSbbLabelT1.getText().toString().equals("") && !UpdateSbbLabelT2.getText().toString().equals("") && !UpdateSbbLabelLenght.getText().toString().equals("")) {
            double F1 = Double.parseDouble(UpdateSbbLabelF1.getText().toString());
            double F2 = Double.parseDouble(UpdateSbbLabelF2.getText().toString());
            double T1 = Double.parseDouble(UpdateSbbLabelT1.getText().toString());
            double T2 = Double.parseDouble(UpdateSbbLabelT2.getText().toString());
            double LENGTH = Double.parseDouble(UpdateSbbLabelLenght.getText().toString());
            double Diameter = ((F1 + F2 + T1 + T2) / 4);
            LogVolume = (Diameter * Diameter * Common.DensityOFWood * LENGTH) / 10000;
        }
        /*Diameter Calculation*/
        if (!UpdateSbbLabelNoteF.getText().toString().equals("") || !UpdateSbbLabelNoteT.getText().toString().equals("") && !UpdateSbbLabelNoteL.getText().toString().equals("")) {
            double F = 0.00, T = 0.00, L = 0.00, CavityAvergDiameter = 0.00;
            if (UpdateSbbLabelNoteF.getText().toString().length() > 0) {
                F = Double.parseDouble(UpdateSbbLabelNoteF.getText().toString());
            }
            if (UpdateSbbLabelNoteT.getText().toString().length() > 0) {
                T = Double.parseDouble(UpdateSbbLabelNoteT.getText().toString());
            }
            if (UpdateSbbLabelNoteL.getText().toString().length() > 0) {
                L = Double.parseDouble(UpdateSbbLabelNoteL.getText().toString());
            }
            if (!UpdateSbbLabelNoteF.getText().toString().equals("") && !UpdateSbbLabelNoteT.getText().toString().equals("")) {
                CavityAvergDiameter = ((F + T) / 2);
            } else if (!UpdateSbbLabelNoteF.getText().toString().equals("") || !UpdateSbbLabelNoteT.getText().toString().equals("")) {
                CavityAvergDiameter = ((F + T) / 1);
            }
            CavityVolume = (CavityAvergDiameter * CavityAvergDiameter * Common.DensityOFWood * L) / 10000;
        }
        TotalVolume = LogVolume - CavityVolume;
        return TotalVolume;
    }

    public void WoodSpeicesID(String WoodSpeicesCode, String TreeNumber) {
        try {
            if (TreeNumber.length() == 0) {
                AlertDialogBox("Validation", "Please check FellingSectionId either TreeNumber is empty!", false);
                FellingWSpecieAutoTxT.setText("");
                UpdateTreeNumberWSC.setText("");
            } else {
                if (Common.WoodSpeicesDeatilsList.size() > 0) {
                    Common.WoodSpeicesFilterDeatilsList.clear();
                    Common.WoodSpeicesFilterDeatilsList = mDBExternalHelper.getWoodSpiceID(WoodSpeicesCode);
                    if (Common.WoodSpeicesFilterDeatilsList.size() > 0) {
                        Common.FsWoodSpieceID = Common.WoodSpeicesFilterDeatilsList.get(0).getWoodSpeciesId();
                        Common.IsNewWoodSpiceCode = 0;
                    } else {
                        Common.IsNewWoodSpiceCode = 1;
                    }
                }
            }
        } catch (Exception ex) {

        }
    }

    public void GetPlotNo(String PlotNuber, String WoodSpiceCode) {
        try {
            if (WoodSpiceCode.length() == 0 || PlotNuber.length() == 0) {
                AlertDialogBox("Validation", "Please check WoodSpiceCode either PlotNumber is empty!", false);
                FellingPlotTxT.setText("");
                UpdatePlotNumber.setText("");
            } else {
                if (Common.FellingRegPlotNumberFilterList.size() > 0) {
                    Common.FellingRegPlotNumberFilterSinglrList.clear();
                    Common.FellingRegPlotNumberFilterSinglrList = mDBExternalHelper.getPlotIDCheck(PlotNuber);
                    if (Common.FellingRegPlotNumberFilterSinglrList.size() > 0) {
                        Common.PlotId = Common.FellingRegPlotNumberFilterSinglrList.get(0).getPlotId();
                        Common.IsNewPlotNumber = 0;
                    } else {
                        Common.IsNewPlotNumber = 1;
                    }
                }
            }
        } catch (Exception ex) {

        }
    }


    public void FellingRegEditValues() {
        RegistrationdateTxT.setText(Common.FellingRegDate);
        FellingRegNoEDTxT.setText(String.valueOf(Common.FellingRegNo));
        /*Fellign SectionID*/
      /*  Common.FellingSectionList.clear();
        Common.FellingSectionList = mDBExternalHelper.getFellingSectionDetails(Common.FromLocationID);*/
        for (FellingSectionModel item : Common.FellingSectionList) {
            if (item.getFellingSectionId().equals(Common.FellingSectionId)) {
                FellingSecIdSpin.setSelection(item.getID() - 1);
            }
        }
       /* Common.TreeNosList = mDBInternalHelper.getTreeNumber(Common.FellingRegID);
        TreeNoList(Common.TreeNosList);*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_F9)) {
            fellingRegtext.setText("");
            Common.ScanMode = true;
            scanService.doDecode();
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Signout("Are you sure you want to close?");
        }
        return true;
    }

    public void Initialization() {
        deviceLocationTXT = findViewById(R.id.deviceLocationTxT);
        FellingRegNoEDTxT = findViewById(R.id.fellingRegistrationNoEDTxT);
        FellingSecIdSpin = findViewById(R.id.fellingSectionIdSpinner);
        FellingSecIdSpin.setEnabled(true);
        FellingRegNoEDTxT.setEnabled(true);
        RegistrationdateTxT = findViewById(R.id.RegistrationdateTxT);
        FellingRegistrationList = findViewById(R.id.fellingRegistrationList);
        TotalCountRegistration = findViewById(R.id.TotalCountRegistration);
        TotalVolumeRegistration = findViewById(R.id.TotalVolumesRegistration);
        fellingRegistrationScanBTN = findViewById(R.id.fellingRegistrationScanBTN);
        fellingRegistrationScanBTN.setVisibility(View.GONE);
        findViewById(R.id.fellingRegistrationScanBTN).setOnClickListener(mDecodeListener);
        fellingRegEnterManual = findViewById(R.id.fellingReg_enterIMG);
        findViewById(R.id.fellingReg_enterIMG).setOnClickListener(mScannerListener);
        NextButton = findViewById(R.id.nextbutton);
        findViewById(R.id.nextbutton).setOnClickListener(mNextclickListener);
        fellingRegtext = findViewById(R.id.fellingRegtextStatus);
        closeButton = findViewById(R.id.close_fellingform);
        findViewById(R.id.close_fellingform).setOnClickListener(mCloseActivityListener);
        NovalueFound = findViewById(R.id.NovalueFound);
        //fellingTreenoSpinner = findViewById(R.id.fellingTreenoSpinner);
        FellingWSpecieAutoTxT = findViewById(R.id.fellingWSpecieTxT);
        fellingQualitySpinner = findViewById(R.id.fellingQualitySpinner);
        fellingTreenoAutoTxT = findViewById(R.id.fellingTreenoAutoTxT);
        FellingPlotTxT = findViewById(R.id.fellingPlotTxT);
        AddTreeNoTxT = findViewById(R.id.addTreeNoTXT);
        findViewById(R.id.addTreeNoTXT).setOnClickListener(maddTreeNoTXTListener);
        EditOptionFlagLay = findViewById(R.id.fellingReg_editLayout);
        PrintButton = findViewById(R.id.fellingRegister_Print);
        findViewById(R.id.fellingRegister_Print).setOnClickListener(mfellingRegisterPrintListener);
        TreeF1 = findViewById(R.id.Tree_F1);
        TreeF2 = findViewById(R.id.Tree_F2);
        TreeT1 = findViewById(R.id.Tree_T1);
        TreeT2 = findViewById(R.id.Tree_T2);
        TreeLenght = findViewById(R.id.Tree_lenght);
        SbbLabelF1 = findViewById(R.id.Sbblabel_F1);
        SbbLabelF2 = findViewById(R.id.Sbblabel_F2);
        SbbLabelT1 = findViewById(R.id.Sbblabel_T1);
        SbbLabelT2 = findViewById(R.id.Sbblabel_T2);
        SbbLabelLenght = findViewById(R.id.Sbblabel_lenght);
        SbbLabelVolume = findViewById(R.id.Sbblabel_Volume);
        Note_Checked = findViewById(R.id.Note_Checked);
        Layout_Note = findViewById(R.id.layout_Note);
        Layout_Note.setVisibility(View.GONE);
        FellingRegSbblabel_NoteF = findViewById(R.id.fellingRegSbblabel_NoteF);
        FellingRegSbblabel_NoteT = findViewById(R.id.fellingRegSbblabel_NoteT);
        FellingRegSbblabel_NoteL = findViewById(R.id.fellingRegSbblabel_NoteL);
        SbbLableUpdateLayout = findViewById(R.id.sbblabelUpdate);
        TreeUpdateLayout = findViewById(R.id.treeNumberUpdate);
        UpdateLayoutClose = findViewById(R.id.update_delete);
        TreeUpdatelayoutClose = findViewById(R.id.updateTreeNumber_delete);
        findViewById(R.id.update_delete).setOnClickListener(mCloseSbLabelUpdateListener);
        findViewById(R.id.updateTreeNumber_delete).setOnClickListener(mCloseTreeNumberUpdateListener);

        UpdateSbbLabelF1 = findViewById(R.id.updatesbblable_F1);
        UpdateSbbLabelF2 = findViewById(R.id.updatesbblable_F2);
        UpdateSbbLabelT1 = findViewById(R.id.updatesbblable_T1);
        UpdateSbbLabelT2 = findViewById(R.id.updatesbblable_T2);
        UpdateSbbLabelLenght = findViewById(R.id.updatesbblable_Length);
        UpdateSbbLabelNoteF = findViewById(R.id.updatesbblable_NoteF);
        UpdateSbbLabelNoteT = findViewById(R.id.updatesbblable_NoteT);
        UpdateSbbLabelNoteL = findViewById(R.id.updatesbblable_NoteL);
        UpdateSbbLabelSave = findViewById(R.id.updatesbblable_Save);
        findViewById(R.id.updatesbblable_Save).setOnClickListener(mSaveUpdateListener);
        HideDiamantionLayout = findViewById(R.id.hideDiamantionLayout);

        AddTreeNumberImg = findViewById(R.id.fellingRegisterTreeNumberAdd);
        findViewById(R.id.fellingRegisterTreeNumberAdd).setOnClickListener(mTreeNumberUpdateListener);
        UpdateTreeNumber = findViewById(R.id.NewTreeNum);
        UpdateTreeNumberWSC = findViewById(R.id.NewWoodSpeiceCode);
        UpdatePlotNumber = findViewById(R.id.NewPlotNumber);
        UpdateTreeNumberF1 = findViewById(R.id.updateTreeNum_F1);
        UpdateTreeNumberF2 = findViewById(R.id.updateTreeNum_F2);
        UpdateTreeNumberT1 = findViewById(R.id.updateTreeNum_T1);
        UpdateTreeNumberT2 = findViewById(R.id.updateTreeNum_T2);
        UpdateTreeNumberLenght = findViewById(R.id.updateTreeNum_Length);
        UpdateTreeNumberSave = findViewById(R.id.updateTreeNum_Save);
        findViewById(R.id.updateTreeNum_Save).setOnClickListener(mSaveUpdateTreeNumberListener);
        TreePartAUTOTXT = findViewById(R.id.TreePartTxT);
        UpdateTreePartType = findViewById(R.id.updateTreePartTxT);



       /* UpdateSbbLabelF1.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 4)});
        UpdateSbbLabelF2.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 4)});
        UpdateSbbLabelT1.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 4)});
        UpdateSbbLabelT2.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 4)});
        UpdateSbbLabelLenght.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 4)});
        UpdateSbbLabelNoteF.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 4)});
        UpdateSbbLabelNoteT.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 4)});
        UpdateSbbLabelNoteL.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 4)});
        */
    }

    public void HideScan(boolean HideOpenFlag) {

        if (HideOpenFlag == true) {
            fellingRegistrationScanBTN.setVisibility(View.VISIBLE);
        } else {
            fellingRegistrationScanBTN.setVisibility(View.GONE);
        }

    }

    public void ViewFellingList(boolean EdtFlag) {
        if (EdtFlag == false) {
            HideDiamantionLayout.setVisibility(View.VISIBLE);
            EditOptionFlagLay.setVisibility(View.VISIBLE);
            fellingRegistrationScanBTN.setEnabled(false);
            FellingRegNoEDTxT.setEnabled(false);
            fellingTreenoAutoTxT.setEnabled(false);
            FellingWSpecieAutoTxT.setEnabled(false);
            AddTreeNoTxT.setEnabled(false);
            fellingRegtext.setEnabled(false);
            /*FellingSecIdSpin.setEnabled(false);*/
            fellingQualitySpinner.setEnabled(false);
            fellingRegEnterManual.setEnabled(false);
            NextButton.setEnabled(false);
        } else {
            HideDiamantionLayout.setVisibility(View.GONE);
            EditOptionFlagLay.setVisibility(View.GONE);
            fellingRegistrationScanBTN.setEnabled(true);
            FellingRegNoEDTxT.setEnabled(true);
            fellingTreenoAutoTxT.setEnabled(true);
            FellingWSpecieAutoTxT.setEnabled(true);
            AddTreeNoTxT.setEnabled(true);
            fellingRegtext.setEnabled(true);
            /*FellingSecIdSpin.setEnabled(true);*/
            fellingQualitySpinner.setEnabled(true);
            fellingRegEnterManual.setEnabled(true);
            NextButton.setEnabled(true);
        }
    }

    public void Click_Listener() {

        FellingRegNoEDTxT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Common.FellingRegNo = s.toString();
                } else {
                    Common.FellingRegNo = "";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fellingTreenoAutoTxT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Common.FsTreeNumber = s.toString();
                    GetTreeNumber(Common.FsTreeNumber);
                } else {
                    //AlertDialogBox("Tree No", "please enter Tree No", false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        fellingTreenoAutoTxT.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fellingTreenoAutoTxT.requestFocus();
                fellingTreenoAutoTxT.showDropDown();
                HideScan(false);
                return false;
            }
        });

        fellingTreenoAutoTxT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Common.FsTreeNumber = (String) parent.getItemAtPosition(position);
                GetTreeNumber(Common.FsTreeNumber);
            }
        });

        FellingWSpecieAutoTxT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Common.FsWoodSpieceCode = s.toString();
                    WoodSpeicesID(Common.FsWoodSpieceCode, fellingTreenoAutoTxT.getText().toString());
                } else {
                    // AlertDialogBox("WoodSpiceCode", "please enter WoodSpiceCode", false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        FellingWSpecieAutoTxT.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FellingWSpecieAutoTxT.requestFocus();
                FellingWSpecieAutoTxT.showDropDown();
                HideScan(false);
                return false;
            }
        });

        FellingWSpecieAutoTxT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Common.FsWoodSpieceCode = (String) parent.getItemAtPosition(position);
                Common.OldWSCode = (String) parent.getItemAtPosition(position);
                WoodSpeicesID(Common.FsWoodSpieceCode, fellingTreenoAutoTxT.getText().toString());
            }
        });

        UpdateTreeNumberWSC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Common.FsWoodSpieceCode = s.toString();
                    WoodSpeicesID(Common.FsWoodSpieceCode, UpdateTreeNumber.getText().toString());
                } else {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        UpdateTreeNumberWSC.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                UpdateTreeNumberWSC.requestFocus();
                UpdateTreeNumberWSC.showDropDown();
                HideScan(false);
                return false;
            }
        });

        UpdateTreeNumberWSC.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Common.FsWoodSpieceCode = (String) parent.getItemAtPosition(position);
                Common.OldWSCode = (String) parent.getItemAtPosition(position);
                WoodSpeicesID(Common.FsWoodSpieceCode, UpdateTreeNumber.getText().toString());
            }
        });

       /* FellingPlotTxT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Common.PlotNo = s.toString();
                    GetPlotNo(Common.PlotNo, FellingWSpecieAutoTxT.getText().toString());
                } else {
                    // AlertDialogBox("WoodSpiceCode", "please enter WoodSpiceCode", false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        FellingPlotTxT.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FellingPlotTxT.requestFocus();
                FellingPlotTxT.showDropDown();
                return false;
            }
        });

        FellingPlotTxT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Common.PlotNo = (String) parent.getItemAtPosition(position);
                Common.OldPlotNo = (String) parent.getItemAtPosition(position);
                GetPlotNo(Common.PlotNo, FellingWSpecieAutoTxT.getText().toString());
            }
        });*/


        UpdatePlotNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Common.PlotNo = s.toString();
                    GetPlotNo(Common.PlotNo, UpdateTreeNumberWSC.getText().toString());
                } else {
                    // AlertDialogBox("WoodSpiceCode", "please enter WoodSpiceCode", false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        UpdatePlotNumber.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                UpdatePlotNumber.requestFocus();
                UpdatePlotNumber.showDropDown();
                HideScan(false);
                return false;
            }
        });

        UpdatePlotNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Common.PlotNo = (String) parent.getItemAtPosition(position);
                Common.OldPlotNo = (String) parent.getItemAtPosition(position);
                GetPlotNo(Common.PlotNo, UpdateTreeNumberWSC.getText().toString());
            }
        });

        fellingRegtext.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Common.ScannedEditTXTFlag = true;
                HideScan(true);
                return false;
            }
        });

        fellingRegtext.addTextChangedListener(new TextWatcher() {
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
                        fellingRegtext.setError(CommonMessage(R.string.BarCodeLenghtMsg));
                    }
                }
            }
        });

        fellingQualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Common.QualityName = Common.QulaityDefaultList.get(position);
                } catch (Exception ex) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FellingSecIdSpin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Common.FellingRegisterLogsDetails.size() > 0) {
                    AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "You can not select different felling section ID in same page", false);
                    if (Common.IsEditorViewFlag == false) {
                        AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "Can not edit Or delete after Synced", false);
                    }
                    FellingSecIdSpin.setEnabled(false);
                    HideScan(false);
                }
                return false;
            }
        });

        FellingSecIdSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Common.FellingSectionId = String.valueOf(Common.FellingSectionList.get(position).getFellingSectionId());
                    //Common.FellingSectionName = Common.FellingSectionList.get(position).getFellingSectionId();
                    fellingTreenoAutoTxT.setText("");
                    FellingWSpecieAutoTxT.setText("");
                    TreeNoList(Common.FellingSectionId);
                    PlotNumberList(Common.FellingSectionId);
                } catch (Exception ex) {
                    Log.e("Exception", "FellingSecIdSpin" + ex.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        HideDiamantionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "Can not edit Or delete after Synced", false);
            }
        });

        UpdateTreeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    TreeNumberCheck(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        TreePartAUTOTXT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Common.TreePart = s.toString();
                    if (TreePartlist.contains(Common.TreePart)) {
                    } else {
                        AlertDialogBox("Tree Part", "Please enter correct value", false);
                        TreePartAUTOTXT.setText("");
                        Common.TreePart = "";
                    }
                } else {
                    Common.TreePart = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        TreePartAUTOTXT.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TreePartAUTOTXT.requestFocus();
                TreePartAUTOTXT.showDropDown();
                HideScan(false);
                return false;
            }
        });

        TreePartAUTOTXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Common.TreePart = (String) parent.getItemAtPosition(position);
            }
        });

        UpdateTreePartType.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                UpdateTreePartType.requestFocus();
                UpdateTreePartType.showDropDown();
                HideScan(false);
                return false;
            }
        });

        SbbLabelF1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HideScan(false);
                return false;
            }
        });
        SbbLabelF2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HideScan(false);
                return false;
            }
        });
        SbbLabelT1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HideScan(false);
                return false;
            }
        });
        SbbLabelT2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HideScan(false);
                return false;
            }
        });
        SbbLabelLenght.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HideScan(false);
                return false;
            }
        });
        FellingRegSbblabel_NoteF.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HideScan(false);
                return false;
            }
        });
        FellingRegSbblabel_NoteT.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HideScan(false);
                return false;
            }
        });
        FellingRegSbblabel_NoteL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HideScan(false);
                return false;
            }
        });
    }

    public void TreeNumberCheck(String treeNumber) {
        try {
            ArrayList<FellingTreeDetailsModel> SingleTreeDetailsList = new ArrayList<>();
            boolean TreeNumberCheck = mDBInternalHelper.getTreeNumberDuplicationCheck(Common.FromLocationID, Common.FellingSectionId, treeNumber);
            if (TreeNumberCheck == true) {
                SingleTreeDetailsList = mDBInternalHelper.getFellingRegWithTreeDetails(treeNumber);
                UpdateTreeNumberWSC.setText(SingleTreeDetailsList.get(0).getWoodSpieceCode());
                UpdatePlotNumber.setText(SingleTreeDetailsList.get(0).getPlotNumber());
                UpdateTreeNumberF1.setText(SingleTreeDetailsList.get(0).getFooter_1());
                UpdateTreeNumberF2.setText(SingleTreeDetailsList.get(0).getFooter_2());
                UpdateTreeNumberT1.setText(SingleTreeDetailsList.get(0).getTop_1());
                UpdateTreeNumberT2.setText(SingleTreeDetailsList.get(0).getTop_2());
                UpdateTreeNumberLenght.setText(SingleTreeDetailsList.get(0).getLength());
                UpdateTreeNumberSave.setText("Update");
                Toast.makeText(FellingRegistrationActivity.this, "Treenumber already inserted", Toast.LENGTH_SHORT).show();
            } else {
                UpdateTreeNumberWSC.setText("");
                UpdatePlotNumber.setText("");
                UpdateTreeNumberF1.setText("");
                UpdateTreeNumberF2.setText("");
                UpdateTreeNumberT1.setText("");
                UpdateTreeNumberT2.setText("");
                UpdateTreeNumberLenght.setText("");
                UpdateTreeNumberSave.setText("Save");
            }

        } catch (Exception ex) {

        }
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str == "null";
    }

    private void getfellingSectionSpinner() {
        try {
            Common.FellingSectionList.clear();
            Common.FellingSectionList = mDBExternalHelper.getFellingSectionID(Common.FromLocationID);
            fellingSectionAdapter = new FellingSectionAdapter(getApplicationContext(), Common.FellingSectionList);
            FellingSecIdSpin.setAdapter(fellingSectionAdapter);
            Common.WoodSpeicesDeatilsList.clear();
            Common.WoodSpeicesDeatilsList = mDBExternalHelper.getWoodSpicesTabel();
            WoodSpeicesList(Common.WoodSpeicesDeatilsList);
        } catch (Exception ex) {
        }
    }

    private void getQualitySpinner() {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, Common.QulaityDefaultList);
            fellingQualitySpinner.setAdapter(adapter);
        } catch (Exception ex) {
        }
    }

    View.OnClickListener mDecodeListener = new View.OnClickListener() {
        public void onClick(View v) {
            Common.ScannedEditTXTFlag = false;
            if (FelllingDetailsValidation(Common.FromLocationname, fellingTreenoAutoTxT.getText().toString(), FellingWSpecieAutoTxT.getText().toString(),
                    Common.FellingSectionId, RegistrationdateTxT.toString())) {
                if (DimensionsValidations(
                        SbbLabelF1.getText().toString(),
                        SbbLabelF2.getText().toString(),
                        SbbLabelT1.getText().toString(),
                        SbbLabelT2.getText().toString(),
                        SbbLabelLenght.getText().toString())) {
                    boolean woodSpeiFlag = mDBExternalHelper.WoodSpeiceCodeCheck(FellingWSpecieAutoTxT.getText().toString());
                    if (woodSpeiFlag == false) {
                        Common.IsNewWSCode = 1;
                    } else {
                        Common.IsNewWSCode = 0;
                        Common.OldWSCode = "";
                    }
                    fellingRegtext.setText("");
                    Common.ScanMode = true;
                    scanService.doDecode();
                }
            }
        }
    };

    View.OnClickListener mScannerListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (FelllingDetailsValidation(Common.FromLocationname, fellingTreenoAutoTxT.getText().toString(), FellingWSpecieAutoTxT.getText().toString(), Common.FellingSectionId, RegistrationdateTxT.toString())) {
                if (DimensionsValidations(
                        SbbLabelF1.getText().toString(),
                        SbbLabelF2.getText().toString(),
                        SbbLabelT1.getText().toString(),
                        SbbLabelT2.getText().toString(),
                        SbbLabelLenght.getText().toString())) {
                    boolean woodSpeiFlag = mDBExternalHelper.WoodSpeiceCodeCheck(FellingWSpecieAutoTxT.getText().toString());
                    if (woodSpeiFlag == false) {
                        Common.IsNewWSCode = 1;
                    } else {
                        Common.IsNewWSCode = 0;
                        Common.OldWSCode = "";
                    }
                    Common.ScanMode = false;
                    HideKeyboard();
                    String SBBLabel = fellingRegtext.getText().toString();
                    if (SBBLabel.length() == Common.SBBlenght) {
                        Common.EntryMode = 2;
                        ScannedResult(SBBLabel);
                    } else {
                        fellingRegtext.setError(CommonMessage(R.string.ValidBarCodeMsg));
                    }
                }
            }
        }
    };

    View.OnClickListener mCloseActivityListener = new View.OnClickListener() {
        public void onClick(View v) {
            Signout("Are you sure you want to close?");
        }
    };
    View.OnClickListener mCloseSbLabelUpdateListener = new View.OnClickListener() {
        public void onClick(View v) {
            HideKeyboard();
            SbbLableUpdateLayout.setVisibility(View.GONE);
        }
    };
    View.OnClickListener mCloseTreeNumberUpdateListener = new View.OnClickListener() {
        public void onClick(View v) {
            HideKeyboard();
            TreeUpdateLayout.setVisibility(View.GONE);
        }
    };
    View.OnClickListener mTreeNumberUpdateListener = new View.OnClickListener() {
        public void onClick(View v) {
            FinalTreeNumber = 0;
            HideKeyboard();
            TreeUpdateLayout.setVisibility(View.VISIBLE);
            int ExternalnewTreeNUmber = mDBExternalHelper.getMaxTreeNumberExter(Common.FromLocationID, Common.FellingSectionId);
            int InternalnewTreeNUmber = mDBInternalHelper.getMaxTreeNumberInter(Common.FromLocationID, Common.FellingSectionId);
            if (ExternalnewTreeNUmber > InternalnewTreeNUmber) {
                FinalTreeNumber = ExternalnewTreeNUmber + 1;
            } else {
                FinalTreeNumber = InternalnewTreeNUmber + 1;
            }
            UpdateTreeNumber.setText(String.valueOf(FinalTreeNumber));
            UpdateTreeNumberSave.setText("Save");
            UpdateTreeNumberWSC.setText("");
            UpdatePlotNumber.setText("");
            UpdateTreeNumberF1.setText("");
            UpdateTreeNumberF2.setText("");
            UpdateTreeNumberT1.setText("");
            UpdateTreeNumberT2.setText("");
            UpdateTreeNumberLenght.setText("");
        }
    };
  /*  View.OnClickListener mSaveUpdateListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Common.Volume = String.valueOf(UpdateVolumeCalculation());
                boolean treePartTypeDuplicationFlag = mDBInternalHelper.UpdategetTreeNumberTreepartDuplicationCheck(Common.FromLocationID, Common.FellingSectionId, Common.UpdatedTreeNumber, UpdateTreePartType.getText().toString(), Common.UpdateSBBLabel);
                if (treePartTypeDuplicationFlag == true) {
                    AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "please add diff tree part type", false);
                    return;
                } else {
                    boolean UpdateTree = mDBInternalHelper.updateFellRegSbbLabel(
                            Common.UpdateSBBLabel,
                            Common.UpdateFellRegUnique,
                            UpdateSbbLabelF1.getText().toString(),
                            UpdateSbbLabelF2.getText().toString(),
                            UpdateSbbLabelT1.getText().toString(),
                            UpdateSbbLabelT2.getText().toString(),
                            UpdateSbbLabelLenght.getText().toString(),
                            UpdateSbbLabelNoteF.getText().toString(),
                            UpdateSbbLabelNoteT.getText().toString(),
                            UpdateSbbLabelNoteL.getText().toString(),
                            Common.Volume,
                            UpdateTreePartType.getText().toString()
                    );
                    if (UpdateTree == true) {
                        GetFellingRegResultList();
                        HideKeyboard();
                        SbbLableUpdateLayout.setVisibility(View.GONE);

                    }
                }
            } catch (Exception ex) {

            }
        }
    };*/

    View.OnClickListener mSaveUpdateListener = new View.OnClickListener() {
        public void onClick(View v) {
            String noteF = UpdateSbbLabelNoteF.getText().toString();
            String noteT = UpdateSbbLabelNoteT.getText().toString();
            String noteL = UpdateSbbLabelNoteL.getText().toString();
            if (!noteF.isEmpty() || !noteT.isEmpty()) {
                if (noteL.isEmpty()) {
                    AlertDialogBox("Update","Please enter NOTE L value",false);
                } else {
                    try {
                        Common.Volume = String.valueOf(UpdateVolumeCalculation());
                        boolean treePartTypeDuplicationFlag = mDBInternalHelper.UpdategetTreeNumberTreepartDuplicationCheck(Common.FromLocationID, Common.FellingSectionId, Common.UpdatedTreeNumber, UpdateTreePartType.getText().toString(), Common.UpdateSBBLabel);
                        if (treePartTypeDuplicationFlag == true) {
                            AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "please add diff tree part type", false);
                            return;
                        } else {

                            boolean UpdateTree = mDBInternalHelper.updateFellRegSbbLabel(
                                    Common.UpdateSBBLabel,
                                    Common.UpdateFellRegUnique,
                                    UpdateSbbLabelF1.getText().toString(),
                                    UpdateSbbLabelF2.getText().toString(),
                                    UpdateSbbLabelT1.getText().toString(),
                                    UpdateSbbLabelT2.getText().toString(),
                                    UpdateSbbLabelLenght.getText().toString(),
                                    UpdateSbbLabelNoteF.getText().toString(),
                                    UpdateSbbLabelNoteT.getText().toString(),
                                    UpdateSbbLabelNoteL.getText().toString(),
                                    Common.Volume,
                                    UpdateTreePartType.getText().toString()
                            );
                            if (UpdateTree == true) {
                                GetFellingRegResultList();
                                HideKeyboard();
                                SbbLableUpdateLayout.setVisibility(View.GONE);

                            }
                        }
                    } catch (Exception ex) {

                    }

                }

            } else {
/*Validation added by Sreeni 23-09-19*/
                if (!UpdateSbbLabelNoteL.getText().toString().isEmpty()) {
                    if (UpdateSbbLabelNoteF.getText().toString().isEmpty() || UpdateSbbLabelNoteT.getText().toString().isEmpty()) {
                        AlertDialogBox("Update","Please enter NOTE F or Note T value",false);
                    }
                } else {
                    try {
                        Common.Volume = String.valueOf(UpdateVolumeCalculation());
                        boolean treePartTypeDuplicationFlag = mDBInternalHelper.UpdategetTreeNumberTreepartDuplicationCheck(Common.FromLocationID, Common.FellingSectionId, Common.UpdatedTreeNumber, UpdateTreePartType.getText().toString(), Common.UpdateSBBLabel);
                        if (treePartTypeDuplicationFlag == true) {
                            AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "please add diff tree part type", false);
                            return;
                        } else {

                            boolean UpdateTree = mDBInternalHelper.updateFellRegSbbLabel(
                                    Common.UpdateSBBLabel,
                                    Common.UpdateFellRegUnique,
                                    UpdateSbbLabelF1.getText().toString(),
                                    UpdateSbbLabelF2.getText().toString(),
                                    UpdateSbbLabelT1.getText().toString(),
                                    UpdateSbbLabelT2.getText().toString(),
                                    UpdateSbbLabelLenght.getText().toString(),
                                    UpdateSbbLabelNoteF.getText().toString(),
                                    UpdateSbbLabelNoteT.getText().toString(),
                                    UpdateSbbLabelNoteL.getText().toString(),
                                    Common.Volume,
                                    UpdateTreePartType.getText().toString()
                            );
                            if (UpdateTree == true) {
                                GetFellingRegResultList();
                                HideKeyboard();
                                SbbLableUpdateLayout.setVisibility(View.GONE);

                            }
                        }
                    } catch (Exception ex) {

                    }
                }

            }

        }
    };

    View.OnClickListener mSaveUpdateTreeNumberListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                //Common.FsWoodSpieceCode = UpdateTreeNumberWSC.getText().toString();
                //Common.PlotNo = UpdatePlotNumber.getText().toString();
                Common.TreeDF1 = UpdateTreeNumberF1.getText().toString();
                Common.TreeDF2 = UpdateTreeNumberF2.getText().toString();
                Common.TreeDT1 = UpdateTreeNumberT1.getText().toString();
                Common.TreeDT2 = UpdateTreeNumberT2.getText().toString();
                Common.TreeLenght = UpdateTreeNumberLenght.getText().toString();
                boolean UpdateTree = GetTreeNumber(Common.FromLocationID, Common.FellingSectionId, UpdateTreeNumber.getText().toString());
                if (UpdateTree == true) {
                    fellingTreenoAutoTxT.setText(UpdateTreeNumber.getText().toString());
                    FellingWSpecieAutoTxT.setText(Common.FsWoodSpieceCode);
                    FellingPlotTxT.setText(Common.PlotNo);
                    TreeF1.setText(Common.TreeDF1);
                    TreeF2.setText(Common.TreeDF2);
                    TreeT1.setText(Common.TreeDT1);
                    TreeT2.setText(Common.TreeDT2);
                    TreeLenght.setText(Common.TreeLenght);
                    HideKeyboard();
                    TreeUpdateLayout.setVisibility(View.GONE);
                }
                TreeNoList(Common.FellingSectionId);
            } catch (Exception ex) {

            }
        }
    };

    View.OnClickListener mNextclickListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                if (Common.FellingRegisterLogsDetails.size() > 0) {
                        if (Common.FellingRegisterLogsDetails.size() < Common.MinimumScannedItemSize) {
                            AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "Add  minimum five items ", false);
                            return;
                        }
                        UpdateRegistrationID();
                        if (Common.FellingRegisterLogsDetails.size() < (Common.FellingRegLimit + 1)) {
                            NextAlertDialog("Felling Registration Count is less then 18, Are you sure to create next list?");
                        } else {
                            if (FelllingDetailsValidation(Common.FromLocationname, fellingTreenoAutoTxT.getText().toString(), FellingWSpecieAutoTxT.getText().toString(), Common.FellingSectionId, RegistrationdateTxT.toString())) {
                                AddFellingRegisterList();
                            }
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "Add  minimum five items ", false);
                    }

            } catch (Exception ex) {
                AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), ex.toString(), false);
            }
        }
    };

    View.OnClickListener maddTreeNoTXTListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    View.OnClickListener mfellingRegisterPrintListener = new View.OnClickListener() {
        public void onClick(View v) {
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
    };

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
            scanService.setOnScanListener(FellingRegistrationActivity.this);
            scanService.setActivityUp(true);
            Log.v(TAG, "SDLActivity onServiceConnected " + bind);
        }
    };

    @Override
    protected void onPause() {
        Log.v(TAG, "SDLActivity onpause bind--->: " + bind);
        super.onPause();
        //scanService.release();
        if (scanService != null)
            scanService.setActivityUp(false);
        releaseWakeLock();
        Log.v(TAG, "<----SDLActivity onpause bind: " + bind);
    }

    @Override
    protected void onResume() {
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
        GetFellingRegResultList();
        super.onResume();
    }

    @Override
    public void result(String content) {

    }

    @Override
    public void henResult(String codeType, String context) {

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        try {
            /*Common.FellingRegisterLogsDetails = mDBInternalHelper.getFellingformDetailsID(Common.FellingRegID);
            Log.v(TAG, "onDestroy" + Common.FellingRegisterLogsDetails.size());
            if (Common.FellingRegisterLogsDetails.size() == 0) {
                // Remove Felling Registration List If empty
                DeleteFellingListannFellingScannedList(Common.FellingRegID);
            }*/
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
                fellingRegtext.setText(Common.SbbLabel);
                /*Tree Validation*/
                Common.FellingRegNo = FellingRegNoEDTxT.getText().toString();
                Common.TreeDF1 = TreeF1.getText().toString();
                Common.TreeDF2 = TreeF2.getText().toString();
                Common.TreeDT1 = TreeT1.getText().toString();
                Common.TreeDT2 = TreeT2.getText().toString();
                Common.TreeLenght = TreeLenght.getText().toString();

                Common.SbbLabelDF1 = SbbLabelF1.getText().toString();
                Common.SbbLabelDF2 = SbbLabelF2.getText().toString();
                Common.SbbLabelDT1 = SbbLabelT1.getText().toString();
                Common.SbbLabelDT2 = SbbLabelT2.getText().toString();
                Common.SbbLabelLenght = SbbLabelLenght.getText().toString();

                Common.SbbLabelNoteF = FellingRegSbblabel_NoteF.getText().toString();
                Common.SbbLabelNoteT = FellingRegSbblabel_NoteT.getText().toString();
                Common.SbbLabelNoteL = FellingRegSbblabel_NoteL.getText().toString();
                Common.Volume = String.valueOf(VolumeCalculation());


                /*Tree Details Validations*/
                if (Common.TreeDF1.length() == 0) {
                    Common.TreeDF1 = Common.SbbLabelDF1;
                }
                if (Common.TreeDF2.length() == 0) {
                    Common.TreeDF2 = Common.SbbLabelDF2;
                }
                if (Common.TreeDT1.length() == 0) {
                    Common.TreeDT1 = Common.SbbLabelDT1;
                }
                if (Common.TreeDT2.length() == 0) {
                    Common.TreeDT2 = Common.SbbLabelDT2;
                }
                if (Common.TreeLenght.length() == 0) {
                    Common.TreeLenght = Common.SbbLabelLenght;
                }
                /*Tree part Type check exsits value with empty*/
                boolean treePartTypeFlag = mDBInternalHelper.getTreeNumberTreepartCheck(Common.FromLocationID, Common.FellingSectionId, Common.FsTreeNumber);
                if (treePartTypeFlag == true) {
                    AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "please add diff tree number cause already exsist with no tree part type", false);
                    return;
                }
                /*Tree part Type Empty*/
                String TreeTypeValues = mDBInternalHelper.getTreeNumberTreepartVlaueCheck(Common.FromLocationID, Common.FellingSectionId, Common.FsTreeNumber);
                if (TreeTypeValues.length() > 0) {
                    if (isNullOrEmpty(Common.TreePart)) {
                        AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "Cannot add empty Tree Part Type if already exists", false);
                        return;
                    }
                }
                /*Tree Part Type if exsist*/
                //if (isNullOrEmpty(Common.TreePart)) {
                boolean treePartTypeDuplicationFlag = mDBInternalHelper.getTreeNumberTreepartDuplicationCheck(Common.FromLocationID, Common.FellingSectionId, Common.FsTreeNumber, Common.TreePart);
                if (treePartTypeDuplicationFlag == true) {
                    AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "please add diff tree part type", false);
                    return;
                }
                // }
                Common.IsActive = 1;
                // Checked Duplicate In Internal Tabel
                boolean EmptyNullFlags = mDBInternalHelper.getFellingformDetailswithIDCheck(Common.SbbLabel);
                if (EmptyNullFlags == true) {
                    wronBuzzer.start();
                    return;
                }
                if (Common.FellingRegisterLogsDetails.size() <= Common.FellingRegLimit) {
                    Log.e("FellingRegisterLogs", ">>>" + Common.FellingRegisterLogsDetails.size());
                    fellingRegtext.setText("");
                    InsertFellingFormDetails();
                } else {
                    wronBuzzer.start();
                    AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "For one Felling Registration form only 18 items allows.", false);
                    return;
                }
                Common.EntryMode = 1;
                Common.ScannedEditTXTFlag = false;
            }
        } catch (Exception ex) {
            Log.v(TAG, "decode failed" + ex.toString());
        }
    }

    public boolean FelllingDetailsValidation(String FromLoc_Str, String treeno, String wspieceCode, String fellingsecId, String RegisterDate) {
        boolean Validattion = true;
        if (isNullOrEmpty(FromLoc_Str)) {
            Validattion = false;
            AlertDialogBox("Location", "Please check Location!", false);
        }
        if (fellingsecId.equals("Select FellingSectionID")) {
            Validattion = false;
            AlertDialogBox("Felling SectionId", "Please check FellingSectionId is empty!", false);
        }

        if (RegisterDate.equals("")) {
            Validattion = false;
            AlertDialogBox("Felling Register Date", "Please enter FellingRegiter Date is empty!", false);
        }

        if (treeno.equals("")) {
            Validattion = false;
            AlertDialogBox("Tree No", "Please enter TreeNo is empty!", false);
        }

        if (wspieceCode.equals("")) {
            Validattion = false;
            AlertDialogBox("WoodSpeice Code", "Please enter WoodSpeice Code ", false);
        } else {
            if (wspieceCode.length() < 3 || wspieceCode.length() > 3) {
                Validattion = false;
                AlertDialogBox("WoodSpeice Code", "Please select WoodSpeice Code from list!", false);
            }
        }
     /*   if (treeno.equals("")) {
            Validattion = false;
            AlertDialogBox("Tree No", "please enter PlotNo!", false);
        }*/
        return Validattion;
    }

    public boolean DimensionsValidations(String F1_Str, String F2_Str, String T1_Str, String T2_Str, String L_Str) {
        boolean Validattion = true;
        if (F1_Str.equals("") && F2_Str.equals("") && T1_Str.equals("") && T2_Str.equals("") && L_Str.equals("")) {
            Validattion = true;
        } else {
            if (!F1_Str.equals("") && !F2_Str.equals("") && !T1_Str.equals("") && !T2_Str.equals("") && !L_Str.equals("")) {
                Validattion = true;
            } else {
                Validattion = false;
                AlertDialogBox("Dimensions", "Please enter remaining dimensions!", false);
            }
        }
        return Validattion;
    }

    public void InsertFellingFormDetails() {
        beepsound.start();
        /*Insert Tree Detials*/
        if (Common.FsTreeNumber.length() > 0) {
            boolean TreeNumberCheck = mDBInternalHelper.getTreeNumberDuplicationCheck(Common.FromLocationID, Common.FellingSectionId, Common.FsTreeNumber);
            if (TreeNumberCheck == true) {
                boolean UpdateTree = mDBInternalHelper.updateFellingTreeDetails(Common.FellingSectionId, Common.FsTreeNumber, Common.TreeDF1, Common.TreeDF2,
                        Common.TreeDT1, Common.TreeDT2, Common.TreeLenght, Common.FromLocationID, Common.FsWoodSpieceCode, Common.OldWSCode, Common.IsNewTreeNumber,
                        Common.PlotNo, Common.OldPlotNo, Common.PlotId, Common.FsWoodSpieceID, Common.IsNewWoodSpiceCode, Common.IsNewPlotNumber);
            } else {
                boolean TreeResult = mDBInternalHelper.insertFellingTreeDetails(Common.FellingRegID, Common.FellingRegUniqueID, Common.FellingSectionId, Common.FsTreeNumber,
                        Common.TreeDF1, Common.TreeDF2, Common.TreeDT1, Common.TreeDT2, Common.TreeLenght, Common.FromLocationID, Common.FsWoodSpieceCode, Common.OldWSCode,
                        Common.IsNewTreeNumber, Common.PlotNo, Common.OldPlotNo, Common.PlotId, Common.FsWoodSpieceID, Common.IsNewWoodSpiceCode, Common.IsNewPlotNumber);
            }
        }
        // Insert values into Scanned Result
        boolean result = mDBInternalHelper.insertfellingformdetails(Common.FellingRegID, Common.FromLocationID, Common.FellingSectionId, Common.SbbLabel, Common.FsWoodSpieceID,
                Common.FsWoodSpieceCode, Common.FsTreeNumber, Common.QualityName, Common.BarCode, Common.FellingRegUniqueID, Common.UserID, Common.EntryMode, Common.IsActive,
                Common.IsNewTreeNumber, Common.IsNewWSCode, Common.OldWSCode, Common.SbbLabelDF1, Common.SbbLabelDF2, Common.SbbLabelDT1, Common.SbbLabelDT2, Common.SbbLabelLenght,
                Common.SbbLabelNoteF, Common.SbbLabelNoteT, Common.SbbLabelNoteL, Common.TreePart, Common.Volume);
        //Scanned Result Refresh
        GetFellingRegResultList();


        UpdateRegistrationID();
        SbbLabelF1.setText("");
        SbbLabelF2.setText("");
        SbbLabelT1.setText("");
        SbbLabelT2.setText("");
        SbbLabelLenght.setText("");
        FellingRegSbblabel_NoteF.setText("");
        FellingRegSbblabel_NoteL.setText("");
        FellingRegSbblabel_NoteT.setText("");
        /*hide scan button*/
        fellingRegistrationScanBTN.setVisibility(View.GONE);
        TreePartAUTOTXT.setText("");
        Common.TreePart = "";
    }

    public boolean GetTreeNumber(int fromLocationID, String FellingSecID, String treeNUm) {
        boolean Result = true;
        try {
            if (treeNUm.length() > 0) {
                boolean TreeNumberCheck = mDBInternalHelper.getTreeNumberDuplicationCheck(fromLocationID, FellingSecID, treeNUm);
                if (TreeNumberCheck == true) {
                    boolean UpdateTree = mDBInternalHelper.updateFellingTreeDetails(Common.FellingSectionId, treeNUm, Common.TreeDF1, Common.TreeDF2,
                            Common.TreeDT1, Common.TreeDT2, Common.TreeLenght, Common.FromLocationID, Common.FsWoodSpieceCode, Common.OldWSCode, Common.IsNewTreeNumber,
                            Common.PlotNo, Common.OldPlotNo, Common.PlotId, Common.FsWoodSpieceID, Common.IsNewWoodSpiceCode, Common.IsNewPlotNumber);
                    Result = true;
                } else {
                    int CurrentTreeNo = Integer.parseInt(UpdateTreeNumber.getText().toString());
                    if (CurrentTreeNo >= FinalTreeNumber) {
                        int TreeNumberLimit = FinalTreeNumber + 10;
                        if (CurrentTreeNo > TreeNumberLimit) {
                            AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "Entered tree number not more then 10 with showed tree number #" + FinalTreeNumber, false);
                            Result = false;
                        } else {
                            Common.IsNewTreeNumber = 1;
                            boolean TreeResult = mDBInternalHelper.insertFellingTreeDetails(Common.FellingRegID, Common.FellingRegUniqueID, Common.FellingSectionId, treeNUm,
                                    Common.TreeDF1, Common.TreeDF2, Common.TreeDT1, Common.TreeDT2, Common.TreeLenght, Common.FromLocationID, Common.FsWoodSpieceCode, Common.OldWSCode,
                                    Common.IsNewTreeNumber, Common.PlotNo, Common.OldPlotNo, Common.PlotId, Common.FsWoodSpieceID, Common.IsNewWoodSpiceCode, Common.IsNewPlotNumber);
                            Result = true;
                        }
                    } else {
                        AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "Entered tree number should be same or above showed tree number #" + FinalTreeNumber, false);
                        Result = false;
                    }
                }

            }
        } catch (Exception ex) {
            Result = false;
        }
        return Result;
    }

    private void GetFellingRegResultList() {
        try {
            Common.FellingRegisterLogsDetails = mDBInternalHelper.getFellingformDetailsID(Common.FellingRegID,Common.FellingSectionId);
            if (Common.FellingRegisterLogsDetails.size() > 0) {
                fellingRegisterAdapter = new FellingRegisterAdapter(Common.FellingRegisterLogsDetails, this);
                horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
                horizontalLayoutManager.setStackFromEnd(true);
                FellingRegistrationList.setLayoutManager(horizontalLayoutManager);
                fellingRegisterAdapter.notifyDataSetChanged();
                FellingRegistrationList.setAdapter(fellingRegisterAdapter);
                NovalueFound.setVisibility(View.GONE);
                FellingRegistrationList.setVisibility(View.VISIBLE);
            } else {
                NovalueFound.setVisibility(View.VISIBLE);
                FellingRegistrationList.setVisibility(View.GONE);
            }
            TotalCountRegistration.setText(String.valueOf(Common.FellingRegisterLogsDetails.size()));

            /*Sum of Volume*/
            Common.VolumeSum = FellingRegisTotalVolume(Common.FellingRegisterLogsDetails);
            TotalVolumeRegistration.setText(String.valueOf(Common.decimalFormat.format(Common.VolumeSum)));
        } catch (Exception ex) {
            Log.d(">>>>>>>>", ">>>>>>" + ex.toString());
        }
    }

    public void UpdateRegistrationID() {
        /*UpdateRegistrationID*/
        Common.EndDate = Common.dateFormat.format(Calendar.getInstance().getTime());
        boolean IDFlag = mDBInternalHelper.UpdateFellingRegisterID(Common.EndDate, Common.FromLocationID, Common.FellingSectionId, Common.FellingRegNo,
                Common.FellingRegisterLogsDetails.size(), Common.FellingRegID, Common.FellingRegUniqueID, Common.VolumeSum);
    }

    public class FellingRegisterAdapter extends RecyclerView.Adapter<FellingRegisterAdapter.GroceryViewHolder> {
        private List<FellingRegisterResultModel> FellingRegisterList;
        Context context;

        public FellingRegisterAdapter(List<FellingRegisterResultModel> ScannedResultList, Context context) {
            this.FellingRegisterList = ScannedResultList;
            this.context = context;
        }

        public void removeItem(int position) {
            FellingRegisterList.remove(position);
            notifyItemRemoved(position);
        }

        public void restoreItem(FellingRegisterResultModel item, int position) {
            FellingRegisterList.add(position, item);
            notifyItemInserted(position);
        }

        public FellingRegisterResultModel getItem(int position) {
            return FellingRegisterList.get(position);
        }

        @Override
        public FellingRegisterAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.felling_registration_infliator, parent, false);
            FellingRegisterAdapter.GroceryViewHolder gvh = new FellingRegisterAdapter.GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(FellingRegisterAdapter.GroceryViewHolder holder, final int position) {
            holder.Barcode.setText(FellingRegisterList.get(position).getSbbLabel());
            holder.WoodSpiceCode.setText(FellingRegisterList.get(position).getWoodSpieceCode());
            holder.QualitySpinner.setText(FellingRegisterList.get(position).getQuality());
            holder.TreeNo.setText(FellingRegisterList.get(position).getTreeNumber());
            holder.PlotNo.setText(FellingRegisterList.get(position).getPlotNo());
            holder.DF1.setText(FellingRegisterList.get(position).getFooter_1());
            holder.DF2.setText(FellingRegisterList.get(position).getFooter_2());
            holder.DT1.setText(FellingRegisterList.get(position).getTop_1());
            holder.DT2.setText(FellingRegisterList.get(position).getTop_2());
            holder.Lenght.setText(FellingRegisterList.get(position).getLength());

            holder.NoteF.setText(FellingRegisterList.get(position).getNoteF());
            holder.NoteT.setText(FellingRegisterList.get(position).getNoteT());
            holder.NoteL.setText(FellingRegisterList.get(position).getNoteL());
            holder.TreePart.setText(FellingRegisterList.get(position).getTreePartType());
            holder.Volume.setText(String.valueOf(Common.decimalFormat.format(FellingRegisterList.get(position).getVolume())));

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
                    if (Common.IsEditorViewFlag == false) {
                        AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "Can not edit Or delete after Synced", false);
                    } else {
                        Common.RemoveSBBLabel = "";
                        Common.RemoveSBBLabel = FellingRegisterList.get(position).getSbbLabel();
                        RemoveMessage(CommonMessage(R.string.Remove_Message));
                    }
                }
            });
            holder.Updatelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (Common.IsEditorViewFlag == false) {
                            AlertDialogBox(CommonMessage(R.string.FellingRegisterHead), "Can not edit Or delete after Synced", false);
                        } else {
                            Common.UpdatedTreeNumber = FellingRegisterList.get(position).getTreeNumber();
                            Common.UpdateSBBLabel = FellingRegisterList.get(position).getSbbLabel();
                            Common.UpdateFellRegUnique = FellingRegisterList.get(position).getFellingRegistrationUniqueID();
                            SbbLableUpdateLayout.setVisibility(View.VISIBLE);
                            UpdateTreePartType.setText(FellingRegisterList.get(position).getTreePartType());
                            UpdateSbbLabelF1.setText(FellingRegisterList.get(position).getFooter_1());
                            UpdateSbbLabelF2.setText(FellingRegisterList.get(position).getFooter_2());
                            UpdateSbbLabelT1.setText(FellingRegisterList.get(position).getTop_1());
                            UpdateSbbLabelT2.setText(FellingRegisterList.get(position).getTop_2());
                            UpdateSbbLabelLenght.setText(FellingRegisterList.get(position).getLength());
                            UpdateSbbLabelNoteF.setText(FellingRegisterList.get(position).getNoteF());
                            UpdateSbbLabelNoteT.setText(FellingRegisterList.get(position).getNoteT());
                            UpdateSbbLabelNoteL.setText(FellingRegisterList.get(position).getNoteL());
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return FellingRegisterList.size();
        }

        public class GroceryViewHolder extends RecyclerView.ViewHolder {
            TextView Barcode, WoodSpiceCode, QualitySpinner, TreeNo, PlotNo, DF1, DF2, DT1, DT2, Lenght, NoteF, NoteT, NoteL, TreePart, Volume;
            //Spinner QualitySpinner,TreeNo;
            LinearLayout Background, Remove, Updatelayout;

            public GroceryViewHolder(View view) {
                super(view);
                Barcode = view.findViewById(R.id.fellingReg_SbblabelTxT);
                WoodSpiceCode = view.findViewById(R.id.fellingReg_WSpiceTxT);
                TreeNo = view.findViewById(R.id.fellingReg_TreeNoTxT);
                PlotNo = view.findViewById(R.id.fellingReg_PlotNoTxT);
                QualitySpinner = view.findViewById(R.id.fellingReg_QualityTxT);
                Background = view.findViewById(R.id.registrationlayoutBackground);
                Remove = view.findViewById(R.id.fellingReg_DeleteList);
                DF1 = view.findViewById(R.id.fellingReg_DF1TxT);
                DF2 = view.findViewById(R.id.fellingReg_DF2TxT);
                DT1 = view.findViewById(R.id.fellingReg_DT1TxT);
                DT2 = view.findViewById(R.id.fellingReg_DT2TxT);
                Lenght = view.findViewById(R.id.fellingReg_LenghtTxT);
                NoteF = view.findViewById(R.id.fellingReg_NoteFTxT);
                NoteT = view.findViewById(R.id.fellingReg_NoteTTxT);
                NoteL = view.findViewById(R.id.fellingReg_NoteLTxT);
                Updatelayout = view.findViewById(R.id.fellingReg_UpdateList);
                TreePart = view.findViewById(R.id.fellingReg_TreePartTxT);
                Volume = view.findViewById(R.id.fellingReg_VolumeTXT);
            }
        }
    }

    public void AddFellingRegisterList() {
        try {
            Common.SyncTime = "";
            Common.SyncStatus = 0;
            Common.FellingRegDate = Common.dateFormat.format(Calendar.getInstance().getTime());
            boolean ListIdFlag = mDBInternalHelper.insertFellingRegisterList(Common.FellingRegNo, Common.FellingRegDate, Common.FellingSectionId, Common.FromLocationID, Common.EndDate,
                    0, Common.FellingRegUniqueID, Common.SyncStatus, Common.SyncTime, 1, Common.UserID, Common.IMEI);
            if (ListIdFlag == true) {
                Common.FellingRegisterList = mDBInternalHelper.getFellingRegisterList();
                Common.ConcessionList.clear();
                Common.ConcessionList = mDBExternalHelper.getConcessionList();
                for (ConcessionNamesModel locationMod : Common.ConcessionList) {
                    if (Common.FromLocationID == locationMod.getFromLocationId()) {
                        Common.FromLocationname = locationMod.getConcessionName();
                    }
                }
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
                    boolean FellingIDFlag = mDBInternalHelper.UpdateFellingUniqueID(Common.FellingRegID, Common.FellingRegUniqueID);
                    GetFellingRegResultList();
                }
            }
            FellingSecIdSpin.setEnabled(true);
            FellingRegNoEDTxT.setEnabled(true);
        } catch (Exception ex) {
            AlertDialogBox("AddTransferList", ex.toString(), false);
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

    public String CommonMessage(int Common_Msg) {
        return FellingRegistrationActivity.this.getResources().getString(Common_Msg);
    }

    public void RemoveMessage(String ErrorMessage) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(FellingRegistrationActivity.this);
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
                            boolean Isdelete = mDBInternalHelper.RemoveFromfellingform(Common.RemoveSBBLabel, 0, Common.FellingRegID);
                            if (Isdelete == true) {
                                Toast.makeText(FellingRegistrationActivity.this, "Successfully Removed from List", Toast.LENGTH_LONG).show();
                                GetFellingRegResultList();
                                UpdateRegistrationID();
                            }
                        } catch (Exception e) {

                        }
                        dialog.cancel();
                    }
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public void Signout(String ErrorMessage) {
        if (Common.IsEditorViewFlag == false) {
            InventoryActivty();
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(FellingRegistrationActivity.this);
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
                               /* Common.FellingRegisterLogsDetails = mDBInternalHelper.getFellingformDetailsID(Common.FellingRegID);
                                Log.v(TAG, "setNegativeButton" + Common.FellingRegisterLogsDetails.size());
                                if (Common.FellingRegisterLogsDetails.size() == 0) {
                                    // Remove Felling Registration List If empty
                                    DeleteFellingListannFellingScannedList(Common.FellingRegID);
                                }*/
                                UpdateRegistrationID();
                                InventoryActivty();
                                dialog.cancel();
                            } catch (Exception ex) {

                            }
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    public void AddTreeNumber() {
        if (Common.IsEditorViewFlag == false) {
            InventoryActivty();
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(FellingRegistrationActivity.this);
            builder1.setMessage("Entered tree number is not there, Allow to add as new tree number?");
            builder1.setCancelable(true);
            builder1.setPositiveButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //String treeNumRemovelast = Common.FsTreeNumber.substring(0, Common.FsTreeNumber.length() - 1);
                            //fellingTreenoAutoTxT.setText(treeNumRemovelast);
                            dialog.cancel();
                        }
                    });
            builder1.setNegativeButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                Common.IsNewTreeNumber = 1;
                                dialog.cancel();
                            } catch (Exception ex) {

                            }
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    public void NextAlertDialog(String ErrorMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(FellingRegistrationActivity.this);
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
                            AddFellingRegisterList();
                            dialog.cancel();
                        } catch (Exception ex) {
                            AlertDialogBox("NextAlertDialog", ex.toString(), false);
                        }
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    Handler FellingRegPrintHan = new Handler();
    Runnable FellingRegPrintRun = new Runnable() {
        @Override
        public void run() {
            try {
                Common.PlotNo = mDBInternalHelper.GetPlotNoUsingFellingSecID(Common.FromLocationID, Common.FellingSectionId, Common.FellingRegUniqueID);
                Common.FellingSectionNumber = mDBExternalHelper.GetFellingSectionNumber(Common.FromLocationID, Common.FellingSectionId);
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

    public void PrintoutEnd() {
        tsc.closeport();
        TSCConnInt = -1;
        Common.IsPrintBtnClickFlag = true;
    }


    public static int booleanToInt(boolean value) {
        // Convert true to 1 and false to 0.
        return value ? 1 : 0;
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(FellingRegistrationActivity.this, Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public void InventoryActivty() {
        Intent _gwwIntent = new Intent(FellingRegistrationActivity.this, InventoryActivity.class);
        startActivity(_gwwIntent);
    }

    public void DeleteFellingListannFellingScannedList(int fellingID) {
        try {
            boolean DeleteListFlag = mDBInternalHelper.DeleteFellingRegistrationListID(fellingID);
            if (DeleteListFlag == true) {
                boolean DeleteScannedFlag = mDBInternalHelper.DeleteFellingRegistrationScanned(fellingID);
            }
        } catch (Exception ex) {
            AlertDialogBox("DeleteReceivedListannReceivedScannedList", ex.toString(), false);
        }
    }

    public double FellingRegisTotalVolume(ArrayList<FellingRegisterResultModel> TotalFRScannedList) {
        double TotVolume = 0.00;
        for (FellingRegisterResultModel inventoTransScanModel : TotalFRScannedList) {
            TotVolume = TotVolume + inventoTransScanModel.getVolume();
        }
        return TotVolume;
    }


}

