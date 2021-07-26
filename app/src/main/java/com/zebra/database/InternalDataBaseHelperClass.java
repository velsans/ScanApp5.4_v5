package com.zebra.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zebra.main.activity.purchase.ui.logs.PurchaseLogsModels;
import com.zebra.main.activity.purchase.ui.logs.PurchaseLogsSyncModel;
import com.zebra.main.activity.purchase.ui.received.PurchaseReceivedModel;
import com.zebra.main.activity.purchase.ui.transfer.PurchaseTransferModel;
import com.zebra.main.model.Export.BarcodeDetailsInputModel;
import com.zebra.main.model.Export.ContainerListInputModel;
import com.zebra.main.model.Export.ContainerListModel;
import com.zebra.main.model.Export.ContainerDetailsSuncModel;
import com.zebra.main.model.Export.ExportDetailsModel;
import com.zebra.main.model.Export.ExportInputDetailsModel;
import com.zebra.main.model.Export.ExportModel;
import com.zebra.main.model.Export.QuoatationDetailsSyncModel;
import com.zebra.main.model.Export.QuotationInternalModel;
import com.zebra.main.model.Export.QuotationListInputModel;
import com.zebra.main.model.FellingRegistration.FellingTreeDetailsModel;
import com.zebra.main.model.LoginAuthenticationModel;
import com.zebra.main.model.FellingRegistration.FellingRegisterInputListModel;
import com.zebra.main.model.FellingRegistration.FellingRegisterListModel;
import com.zebra.main.model.FellingRegistration.FellingRegisterResultModel;
import com.zebra.main.model.InvCount.InventoryCountInputListModel;
import com.zebra.main.model.InvCount.InventoryCountModel;
import com.zebra.main.model.InvCount.ScannedResultModel;
import com.zebra.main.model.InvReceived.InventoryReceivedInputModel;
import com.zebra.main.model.InvReceived.InventoryReceivedListModel;
import com.zebra.main.model.InvReceived.InventoryReceivedModel;
import com.zebra.main.model.InvTransfer.InventoryTransferInputListModel;
import com.zebra.main.model.InvTransfer.InventoryTransferModel;
import com.zebra.main.model.InvTransfer.InventoryTransferScannedResultModel;
import com.zebra.main.model.externaldb.PurchaseAgreementModel;
import com.zebra.main.model.externaldb.PurchaseNoAgreementModel;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;

import java.util.ArrayList;

import static java.lang.Double.*;

public class InternalDataBaseHelperClass extends SQLiteOpenHelper {
    AlertDialogManager alert = new AlertDialogManager();
    private Context mContext;
    private SQLiteDatabase mDatabase;
    ScannedResultModel scannedModel;
    InventoryCountModel countModel;
    InventoryReceivedListModel ReceivedModel;
    InventoryCountInputListModel inventoryInputModel;
    InventoryTransferScannedResultModel inventoryTransModel;
    InventoryReceivedModel inventoryReceivedModel;
    InventoryTransferModel transferModel;
    InventoryTransferInputListModel inventoryTransferInputListModel;
    InventoryReceivedInputModel inventoryReceivedInputListModel;
    LoginAuthenticationModel loginAuthenticationModel;
    FellingRegisterListModel fellingRegisterListModel;
    FellingRegisterResultModel fellingRegisterResultModel;
    FellingRegisterInputListModel fellingRegInpuModel;
    FellingTreeDetailsModel fellingTreeDetModel;
    InternalDataBaseTabelsClass DbClassObject;
    ExportModel exportListModel;
    ExportDetailsModel exportDetailsModel;
    BarcodeDetailsInputModel barcodeDetailsInputModel;
    ExportInputDetailsModel exportinputDetailsModel;
    QuotationInternalModel quoationModel;
    QuotationListInputModel quotationListInputModel;
    ContainerDetailsSuncModel conmodel;
    QuoatationDetailsSyncModel quoModel;
    PurchaseNoAgreementModel purchaseNoModel;
    private static InternalDataBaseHelperClass instance;
    ContainerListModel containerTableDataModel;
    ContainerListInputModel containerListInputModel;
    PurchaseLogsModels purchaseLogsModel;
    PurchaseLogsSyncModel purchaseLogssyncModel;
    PurchaseAgreementModel purchaseAgreementModel;
    PurchaseTransferModel purchaseTransferModel;
    PurchaseReceivedModel purchaseReceivedModel;

    public InternalDataBaseHelperClass(Context context) {
        super(context, Common.INTERNELDBNAME, null, Common.DATABASE_VERSION);
        DbClassObject = new InternalDataBaseTabelsClass();
        this.mContext = context;
    }
  /*  public static InternalDataBaseHelperClass getInstance(Context context) {
        if (instance == null) {
            instance = new InternalDataBaseHelperClass(context);

        }
        return instance;
    }*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("onCreate : %s", "DATABASE_VERSION" + Common.DATABASE_VERSION);// 21st Nov
        db.execSQL(DbClassObject.CreateLogin());
        db.execSQL(DbClassObject.CountIDList());
        db.execSQL(DbClassObject.CountIDScannedDetails());
        //db.execSQL(DbClassObject.TransferIDScannedDetails());
        /*version 5.9*/
        //db.execSQL(DbClassObject.TransferIDScannedDetails_5_9());
        /*version 6.2.6*/
        db.execSQL(DbClassObject.TransferIDScannedDetails6_2_6());
        db.execSQL(DbClassObject.TransferIDListUpdated6_2_6());
        /*Version 4.9*/
        //db.execSQL(DbClassObject.TransferIDListUpdated4_9());
        /*version 6.2.6*/
        db.execSQL(DbClassObject.ReceivedIDList6_2_6());
        db.execSQL(DbClassObject.ReceivedIDScannedDetails6_2_6());
        //db.execSQL(DbClassObject.ReceivedIDList());
        //db.execSQL(DbClassObject.ReceivedIDList5_3());
        //db.execSQL(DbClassObject.ReceivedIDScannedDetails());
        /*Version 5.2*/
        //db.execSQL(DbClassObject.FellingRegistrationDetails5_2());
        db.execSQL(DbClassObject.FellingIDList5_2());

        // db.execSQL(DbClassObject.FellingTreeDetails5_2());
        //24-4-2020
        // version 6.1
        db.execSQL(DbClassObject.FellingTreeDetails5_2());

        /*Version 5.3*/
        // db.execSQL(DbClassObject.FellingRegistrationDetails5_3());
        // 28-4-2020
        //db.execSQL(DbClassObject.FellingRegistrationDetails5_4());

        /*Version 5.6-New Home Design*/
        /*Version 5.7-Export*/
        //db.execSQL(DbClassObject.ExportList5_7());
        //db.execSQL(DbClassObject.ExportDetails5_7());
        /*version 5.8*/
        //db.execSQL(DbClassObject.ExportQuotationList5_8());
        /*version 5.9*/
        db.execSQL(DbClassObject.ExportList5_9());
        //db.execSQL(DbClassObject.ExportList6_0());
        //db.execSQL(DbClassObject.ExportContainerList5_9());
        db.execSQL(DbClassObject.ExportQuotationList6_0());
        //db.execSQL(DbClassObject.ExportQuotationList5_9());
        db.execSQL(DbClassObject.ExportDetails5_9());
        db.execSQL(DbClassObject.ExportContainerList6_0());
        /*version 6.1*/
        db.execSQL(DbClassObject.FellingRegistrationDetails6_1_4());
        db.execSQL(DbClassObject.PurchaseAgreement());
        //db.execSQL(DbClassObject.PurchaseAgreementScannedLogs());
        // PurchaseList
        db.execSQL(DbClassObject.PurchaseTransferListUpdated6_3_2());
        //db.execSQL(DbClassObject.PurchaseReceivedListUpdated6_3_2());
        // Added TransferUniqueID
        db.execSQL(DbClassObject.PurchaseReceivedListUpdated6_3_3());
        //version 6.4.2
        db.execSQL(DbClassObject.PurchaseAgreementScannedLogs6_4_2());
        //db.execSQL(DbClassObject.TransferIDScannedDetails6_4_2());
        //db.execSQL(DbClassObject.ReceivedIDScannedDetails6_4_2());
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new SQLiteException("Can't downgrade database from version " +
                oldVersion + " to " + newVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d("onUpgrade :%s", "onUpgrade() from " + oldVersion + " to " + newVersion);// 9th may
            if (Common.VersionName.equals("4.9")) {
                Version4_9(db);
            }
            if (Common.VersionName.equals("5.0")) {
                Version5_0(db);
            }
            if (Common.VersionName.equals("5.1")) {
                Version5_1(db);
            }
            if (Common.VersionName.equals("5.2")) {
                Version5_2(db);
            }
            if (Common.VersionName.equals("5.3")) {
                Version5_3(db);
            }
            if (Common.VersionName.equals("5.4")) {
                Version5_3(db);
            }
            if (Common.VersionName.equals("5.7")) {
                Version5_7(db);
            }
            if (Common.VersionName.equals("5.8")) {
                Version5_8(db);
            }
            if (Common.VersionName.equals("5.9")) {
                Version5_9(db);
            }
            if (Common.VersionName.equals("6.0")) {
                Version6_0(db);
            }
            if (Common.VersionName.equals("6.1.4")) {
                Version6_1_4(db);
            }
            if (Common.VersionName.equals("6.2.6")) {
                Version6_2_6(db);
            }
            if (Common.VersionName.equals("6.3.2")) {
                Version6_3_2(db);
            }
            if (Common.VersionName.equals("6.3.3")) {
                Version6_3_3(db);
            }
            if (Common.VersionName.equals("6.4.2")) {
                Version6_4_2(db);
            }

        } catch (Exception ex) {
            AlertDialogBox("DataBase onUpgrade", ex.toString(), false);
        }
    }

    public void Version4_9(SQLiteDatabase db) {
        String upgradeNewTreeQuery = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.ISNEWTREENO + " INTEGER";
        db.execSQL(upgradeNewTreeQuery);
        String upgradeNewWoodCodeQuery = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.ISWOODSPECODE + " INTEGER";
        db.execSQL(upgradeNewWoodCodeQuery);
        String upgradeOldWoodCodeQuery = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.ISOLDWOODSPECODE + " TEXT";
        db.execSQL(upgradeOldWoodCodeQuery);
        String upgradeLoadedQuery = "ALTER TABLE " + Common.TBL_INVENTORYTRANSFERIDLIST + " ADD COLUMN " + Common.LOADEDTYPE + " INTEGER";
        db.execSQL(upgradeLoadedQuery);
    }

    public void Version5_0(SQLiteDatabase db) {
        String DF1Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.DF1 + " TEXT";
        db.execSQL(DF1Query);
        String DF2Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.DF2 + " TEXT";
        db.execSQL(DF2Query);
        String DT1Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.DT1 + " TEXT";
        db.execSQL(DT1Query);
        String DT2Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.DT2 + " TEXT";
        db.execSQL(DT2Query);
        String LenghtQuery = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.LENGTH + " TEXT";
        db.execSQL(LenghtQuery);
        String NoteT1Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.NOTEF + " TEXT";
        db.execSQL(NoteT1Query);
        String NoteF1Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.NOTET + " TEXT";
        db.execSQL(NoteF1Query);
        String NoteLenghtQuery = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.NOTEL + " TEXT";
        db.execSQL(NoteLenghtQuery);
        db.execSQL(DbClassObject.FellingTreeDetails5_0());
    }

    public void Version5_1(SQLiteDatabase db) {
        String TreePARTQuery = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.TREEPARTTYPE + " TEXT";
        db.execSQL(TreePARTQuery);
        db.execSQL(DbClassObject.FellingTreeDetails5_0());
        String PlotIDQuery = "ALTER TABLE " + Common.TBL_FELLINGTREEDETAILS + " ADD COLUMN " + Common.PLOTID + " INTEGER";
        db.execSQL(PlotIDQuery);
        String WSIDQuery = "ALTER TABLE " + Common.TBL_FELLINGTREEDETAILS + " ADD COLUMN " + Common.WoodSpiceID + " INTEGER";
        db.execSQL(WSIDQuery);
        String WSCLenghtQuery = "ALTER TABLE " + Common.TBL_FELLINGTREEDETAILS + " ADD COLUMN " + Common.ISWOODSPECODE + " INTEGER";
        db.execSQL(WSCLenghtQuery);
        String IsPlotNoQuery = "ALTER TABLE " + Common.TBL_FELLINGTREEDETAILS + " ADD COLUMN " + Common.ISNEWPLOTNO + " INTEGER";
        db.execSQL(IsPlotNoQuery);
    }

    public void Version5_2(SQLiteDatabase db) {

        boolean loginTBLFlag = tableExists(db, Common.TBL_LOGINAUTHENTICATION);

        boolean countListTBLFlag = tableExists(db, Common.TBL_INVENTORYCOUNTLIST);
        boolean countDetTBLFlag = tableExists(db, Common.TBL_SCANNEDRESULT);

        boolean tranListTBLFlag = tableExists(db, Common.TBL_INVENTORYTRANSFERIDLIST);
        boolean tranDetTBLFlag = tableExists(db, Common.TBL_INVENTORYTRANSFERSCANNED);

        boolean receListTBLFlag = tableExists(db, Common.TBL_INVENTORYRECEIVEDLIST);
        boolean receDetTBLFlag = tableExists(db, Common.TBL_INVENTORYRECEIVED);

        boolean fellingListTBLFlag = tableExists(db, Common.TBL_FELLINGREGISTRATIONLIST);
        if (fellingListTBLFlag == false) {
            db.execSQL(DbClassObject.FellingIDList5_2());
        } else {
            boolean VOLUME_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONLIST, Common.VOLUME);
            if (VOLUME_Flag == false) {
                String PlotIDQuery = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONLIST + " ADD COLUMN " + Common.VOLUME + " TEXT";
                db.execSQL(PlotIDQuery);
            }
        }
        boolean fellingDetTBLFlag = tableExists(db, Common.TBL_FELLINGREGISTRATIONDETAILS);
        if (fellingDetTBLFlag == false) {
            db.execSQL(DbClassObject.FellingRegistrationDetails5_2());
        } else {
            boolean DF1_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.DF1);
            if (DF1_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.DF1 + " TEXT";
                db.execSQL(DF1Query);
            }
            boolean DF2_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.DF2);
            if (DF2_Flag == false) {
                String DF2Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.DF2 + " TEXT";
                db.execSQL(DF2Query);
            }
            boolean DT1_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.DT1);
            if (DT1_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.DT1 + " TEXT";
                db.execSQL(DF1Query);
            }
            boolean DT2_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.DT2);
            if (DT2_Flag == false) {
                String DT2Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.DT2 + " TEXT";
                db.execSQL(DT2Query);
            }
            boolean LENGTH_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.LENGTH);
            if (LENGTH_Flag == false) {
                String LENGTHQuery = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.LENGTH + " TEXT";
                db.execSQL(LENGTHQuery);
            }
            boolean NOTEF_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.NOTEF);
            if (NOTEF_Flag == false) {
                String NOTEFQuery = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.NOTEF + " TEXT";
                db.execSQL(NOTEFQuery);
            }
            boolean NOTET_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.NOTET);
            if (NOTET_Flag == false) {
                String NOTETQuery = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.NOTET + " TEXT";
                db.execSQL(NOTETQuery);
            }
            boolean NOTEL_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.NOTEL);
            if (NOTEL_Flag == false) {
                String NOTELQuery = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.NOTEL + " TEXT";
                db.execSQL(NOTELQuery);
            }
            boolean TreePart_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.TREEPARTTYPE);
            if (TreePart_Flag == false) {
                String TreePART1Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.TREEPARTTYPE + " TEXT";
                db.execSQL(TreePART1Query);
            }
            boolean VOLUME_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.VOLUME);
            if (VOLUME_Flag == false) {
                String NOTELQuery = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.VOLUME + " TEXT";
                db.execSQL(NOTELQuery);
            }
        }
        boolean fellingTreeTBLFlag = tableExists(db, Common.TBL_FELLINGTREEDETAILS);
        if (fellingTreeTBLFlag == false) {
            db.execSQL(DbClassObject.FellingTreeDetails5_2());
        } else {
            boolean PLOTID_Flag = existsColumnInTable(db, Common.TBL_FELLINGTREEDETAILS, Common.PLOTID);
            if (PLOTID_Flag == false) {
                String PlotIDQuery = "ALTER TABLE " + Common.TBL_FELLINGTREEDETAILS + " ADD COLUMN " + Common.PLOTID + " INTEGER";
                db.execSQL(PlotIDQuery);
            }
            boolean WoodSpiceID_Flag = existsColumnInTable(db, Common.TBL_FELLINGTREEDETAILS, Common.WoodSpiceID);
            if (WoodSpiceID_Flag == false) {
                String WSIDQuery = "ALTER TABLE " + Common.TBL_FELLINGTREEDETAILS + " ADD COLUMN " + Common.WoodSpiceID + " INTEGER";
                db.execSQL(WSIDQuery);
            }
            boolean ISWOODSPECODE_Flag = existsColumnInTable(db, Common.TBL_FELLINGTREEDETAILS, Common.ISWOODSPECODE);
            if (ISWOODSPECODE_Flag == false) {
                String WSCLenghtQuery = "ALTER TABLE " + Common.TBL_FELLINGTREEDETAILS + " ADD COLUMN " + Common.ISWOODSPECODE + " INTEGER";
                db.execSQL(WSCLenghtQuery);
            }
            boolean ISNEWPLOTNO_Flag = existsColumnInTable(db, Common.TBL_FELLINGTREEDETAILS, Common.ISNEWPLOTNO);
            if (ISNEWPLOTNO_Flag == false) {
                String IsPlotNoQuery = "ALTER TABLE " + Common.TBL_FELLINGTREEDETAILS + " ADD COLUMN " + Common.ISNEWPLOTNO + " INTEGER";
                db.execSQL(IsPlotNoQuery);
            }
        }
    }

    public void Version5_3(SQLiteDatabase db) {
        boolean receListTBLFlag = tableExists(db, Common.TBL_INVENTORYRECEIVEDLIST);
        if (receListTBLFlag == false) {
            db.execSQL(DbClassObject.ReceivedIDList5_3());
        } else {
            boolean LTI_Flag = existsColumnInTable(db, Common.TBL_INVENTORYRECEIVEDLIST, Common.LOADEDTYPE);
            if (LTI_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_INVENTORYRECEIVEDLIST + " ADD COLUMN " + Common.LOADEDTYPE + " INTEGER";
                db.execSQL(DF1Query);
            }
            boolean TrUni_Flag = existsColumnInTable(db, Common.TBL_INVENTORYRECEIVEDLIST, Common.TRANSFERUNIQUEID);
            if (TrUni_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_INVENTORYRECEIVEDLIST + " ADD COLUMN " + Common.TRANSFERUNIQUEID + " TEXT";
                db.execSQL(DF1Query);
            }
        }
    }

    public void Version5_7(SQLiteDatabase db) {
        boolean ELPListFlag = tableExists(db, Common.TBL_EXPORTLIST);
        if (ELPListFlag == false) {
            db.execSQL(DbClassObject.ExportList5_7());
        }
        boolean ELPDetailFlag = tableExists(db, Common.TBL_EXPORTDETAILS);
        if (ELPDetailFlag == false) {
            db.execSQL(DbClassObject.ExportDetails5_7());
        }
    }

    public void Version5_8(SQLiteDatabase db) {
        boolean ELPListFlag = tableExists(db, Common.TBL_EXPORTQUOTATIONLIST);
        if (ELPListFlag == false) {
            db.execSQL(DbClassObject.ExportQuotationList5_8());
        }
    }

    public void Version5_9(SQLiteDatabase db) {
        /*Transfer Scanned Volume*/

        boolean TranListTBLFlag = tableExists(db, Common.TBL_INVENTORYTRANSFERSCANNED);
        if (TranListTBLFlag == false) {
            db.execSQL(DbClassObject.TransferIDScannedDetails_5_9());
        } else {
            boolean HoleVol_Flag = existsColumnInTable(db, Common.TBL_INVENTORYTRANSFERSCANNED, Common.HOLEVOLUME);
            if (HoleVol_Flag == false) {
                String HoleQuery = "ALTER TABLE " + Common.TBL_INVENTORYTRANSFERSCANNED + " ADD COLUMN " + Common.HOLEVOLUME + " TEXT";
                db.execSQL(HoleQuery);
            }
            boolean NetVol_Flag = existsColumnInTable(db, Common.TBL_INVENTORYTRANSFERSCANNED, Common.GROSSVOLUME);
            if (NetVol_Flag == false) {
                String NetQuery = "ALTER TABLE " + Common.TBL_INVENTORYTRANSFERSCANNED + " ADD COLUMN " + Common.GROSSVOLUME + " TEXT";
                db.execSQL(NetQuery);
            }
        }
        /*Export List*/
        boolean ExpListTBLFlag = tableExists(db, Common.TBL_EXPORTLIST);
        if (ExpListTBLFlag == false) {
            db.execSQL(DbClassObject.ExportList5_9());
        } else {
            boolean QuoNo_Flag = existsColumnInTable(db, Common.TBL_EXPORTLIST, Common.QUOTATIONNO);
            if (QuoNo_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTLIST + " ADD COLUMN " + Common.QUOTATIONNO + " TEXT";
                db.execSQL(DF1Query);
            }
            boolean QuoFla_Flag = existsColumnInTable(db, Common.TBL_EXPORTLIST, Common.QUOTATIONNOFLAG);
            if (QuoFla_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTLIST + " ADD COLUMN " + Common.QUOTATIONNOFLAG + " INTEGER";
                db.execSQL(DF1Query);
            }
            boolean QuoConCount_Flag = existsColumnInTable(db, Common.TBL_EXPORTLIST, Common.CONTAINERCOUNT);
            if (QuoConCount_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTLIST + " ADD COLUMN " + Common.CONTAINERCOUNT + " INTEGER";
                db.execSQL(DF1Query);
            }
        }
        /*Export Container*/
        boolean ELPListFlag = tableExists(db, Common.TBL_EXPORTCONTAINERLIST);
        if (ELPListFlag == false) {
            db.execSQL(DbClassObject.ExportContainerList5_9());
        }
        /*Export Quotation*/
        boolean QuoListTBLFlag = tableExists(db, Common.TBL_EXPORTQUOTATIONLIST);
        if (QuoListTBLFlag == false) {
            db.execSQL(DbClassObject.ExportQuotationList5_9());
        } else {
            boolean QuNo_Flag = existsColumnInTable(db, Common.TBL_EXPORTQUOTATIONLIST, Common.QUOTATIONNO);
            if (QuNo_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTQUOTATIONLIST + " ADD COLUMN " + Common.QUOTATIONNO + " TEXT";
                db.execSQL(DF1Query);
            }
            boolean LTI_Flag = existsColumnInTable(db, Common.TBL_EXPORTQUOTATIONLIST, Common.QUOTATIONUNIQUENO);
            if (LTI_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTQUOTATIONLIST + " ADD COLUMN " + Common.QUOTATIONUNIQUENO + " TEXT";
                db.execSQL(DF1Query);
            }

        }

        boolean detaListTBLFlag = tableExists(db, Common.TBL_EXPORTDETAILS);
        if (detaListTBLFlag == false) {
            db.execSQL(DbClassObject.ExportDetails5_9());
        } else {
            boolean LTI_Flag = existsColumnInTable(db, Common.TBL_EXPORTDETAILS, Common.QUOTATIONID);
            if (LTI_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTDETAILS + " ADD COLUMN " + Common.QUOTATIONID + " INTEGER";
                db.execSQL(DF1Query);
            }
            boolean Quo_Flag = existsColumnInTable(db, Common.TBL_EXPORTDETAILS, Common.QUOTATIONNO);
            if (Quo_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTDETAILS + " ADD COLUMN " + Common.QUOTATIONNO + " INTEGER";
                db.execSQL(DF1Query);
            }
            boolean QUOTATIONUNIQUENOFLAG = existsColumnInTable(db, Common.TBL_EXPORTDETAILS, Common.QUOTATIONUNIQUENO);
            if (QUOTATIONUNIQUENOFLAG == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTDETAILS + " ADD COLUMN " + Common.QUOTATIONUNIQUENO + " TEXT";
                db.execSQL(DF1Query);
            }
        }
    }

    public void Version6_0(SQLiteDatabase db) {
        boolean TruckPlateNumberVol_Flag = tableExists(db, Common.TBL_INVENTORYTRANSFERIDLIST);
        if (TruckPlateNumberVol_Flag == true) {
            //UpdateTruckPlateNumber();
        }
        boolean ExpListTBLFlag = tableExists(db, Common.TBL_EXPORTQUOTATIONLIST);
        if (ExpListTBLFlag == false) {
            db.execSQL(DbClassObject.ExportQuotationList6_0());
        } else {
            boolean QuoFla_Flag = existsColumnInTable(db, Common.TBL_EXPORTQUOTATIONLIST, Common.QUOTATIONNOFLAG);
            if (QuoFla_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTQUOTATIONLIST + " ADD COLUMN " + Common.QUOTATIONNOFLAG + " INTEGER";
                db.execSQL(DF1Query);
            }
        }
        /*Container Table*/
        boolean ComListTBLFlag = tableExists(db, Common.TBL_EXPORTCONTAINERLIST);
        if (ComListTBLFlag == false) {
            db.execSQL(DbClassObject.ExportContainerList6_0());
        } else {
            boolean QuoFla_Flag = existsColumnInTable(db, Common.TBL_EXPORTCONTAINERLIST, Common.COUNT);
            if (QuoFla_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTCONTAINERLIST + " ADD COLUMN " + Common.COUNT + " INTEGER";
                db.execSQL(DF1Query);
            }
            boolean VolFla_Flag = existsColumnInTable(db, Common.TBL_EXPORTCONTAINERLIST, Common.VOLUME);
            if (VolFla_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTCONTAINERLIST + " ADD COLUMN " + Common.VOLUME + " INTEGER";
                db.execSQL(DF1Query);
            }
        }
        /*   *//*Export List*//*
        boolean ListTBLFlag = tableExists(db, Common.TBL_EXPORTLIST);
        if (ListTBLFlag == false) {
            db.execSQL(DbClassObject.ExportList6_0());
        } else {
            boolean book_Flag = existsColumnInTable(db, Common.TBL_EXPORTLIST, Common.BOOKINGNO);
            if (book_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTLIST + " ADD COLUMN " + Common.BOOKINGNO + " TEXT";
                db.execSQL(DF1Query);
            }
            boolean ship_Flag = existsColumnInTable(db, Common.TBL_EXPORTLIST, Common.SHIPCMPY);
            if (ship_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTLIST + " ADD COLUMN " + Common.SHIPCMPY + " TEXT";
                db.execSQL(DF1Query);
            }
            boolean stuff_Flag = existsColumnInTable(db, Common.TBL_EXPORTLIST, Common.STUFFDATE);
            if (stuff_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTLIST + " ADD COLUMN " + Common.STUFFDATE + " TEXT";
                db.execSQL(DF1Query);
            }
            boolean cutt_Flag = existsColumnInTable(db, Common.TBL_EXPORTLIST, Common.CUTTDATE);
            if (cutt_Flag == false) {
                String DF1Query = "ALTER TABLE " + Common.TBL_EXPORTLIST + " ADD COLUMN " + Common.CUTTDATE + " TEXT";
                db.execSQL(DF1Query);
            }
        }*/
    }

    //24-4-2020

    public void Version6_1_4(SQLiteDatabase db) {
     /*   boolean fellingTreeTBLFlag = tableExists(db, Common.TBL_FELLINGTREEDETAILS);
        if (fellingTreeTBLFlag == false) {
            db.execSQL(DbClassObject.FellingTreeDetails6_1_4());
        } else {
            boolean LHT1_Flag = existsColumnInTable(db, Common.TBL_FELLINGTREEDETAILS, Common.LHT1);
            if (LHT1_Flag == false) {
                String LHT1Query = "ALTER TABLE " + Common.TBL_FELLINGTREEDETAILS + " ADD COLUMN " + Common.LHT1 + " TEXT";
                db.execSQL(LHT1Query);
            }
            boolean LHT2_Flag = existsColumnInTable(db, Common.TBL_FELLINGTREEDETAILS, Common.LHT2);
            if (LHT2_Flag == false) {
                String LHT2Query = "ALTER TABLE " + Common.TBL_FELLINGTREEDETAILS + " ADD COLUMN " + Common.LHT2 + " TEXT";
                db.execSQL(LHT2Query);
            }
            boolean LHF1_Flag = existsColumnInTable(db, Common.TBL_FELLINGTREEDETAILS, Common.LHF1);
            if (LHF1_Flag == false) {
                String LHF1Query = "ALTER TABLE " + Common.TBL_FELLINGTREEDETAILS + " ADD COLUMN " + Common.LHF1 + " TEXT";
                db.execSQL(LHF1Query);
            }
            boolean LHF2_Flag = existsColumnInTable(db, Common.TBL_FELLINGTREEDETAILS, Common.LHF2);
            if (LHF2_Flag == false) {
                String LHF2Query = "ALTER TABLE " + Common.TBL_FELLINGTREEDETAILS + " ADD COLUMN " + Common.LHF2 + " TEXT";
                db.execSQL(LHF2Query);
            }
            boolean LHVOLUME_Flag = existsColumnInTable(db, Common.TBL_FELLINGTREEDETAILS, Common.LHVOLUME);
            if (LHVOLUME_Flag == false) {
                String LHVOLUMEQuery = "ALTER TABLE " + Common.TBL_FELLINGTREEDETAILS + " ADD COLUMN " + Common.LHVOLUME + " TEXT";
                db.execSQL(LHVOLUMEQuery);
            }
        }*/
        boolean fellingdetailsTreeTBLFlag = tableExists(db, Common.TBL_FELLINGREGISTRATIONDETAILS);
        if (fellingdetailsTreeTBLFlag == false) {
            db.execSQL(DbClassObject.FellingRegistrationDetails6_1_4());
        } else {
            boolean fellingdetailsLHT1_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.LHT1);
            if (fellingdetailsLHT1_Flag == false) {
                String fellingdetailsLHT1Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.LHT1 + " TEXT";
                db.execSQL(fellingdetailsLHT1Query);
            }
            boolean fellingdetailsLHT2_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.LHT2);
            if (fellingdetailsLHT2_Flag == false) {
                String fellingdetailsLHT2Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.LHT2 + " TEXT";
                db.execSQL(fellingdetailsLHT2Query);
            }
            boolean fellingdetailsLHF1_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.LHF1);
            if (fellingdetailsLHF1_Flag == false) {
                String fellingdetailsLHF1Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.LHF1 + " TEXT";
                db.execSQL(fellingdetailsLHF1Query);
            }
            boolean fellingdetailsLHF2_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.LHF2);
            if (fellingdetailsLHF2_Flag == false) {
                String fellingdetailsLHF2Query = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.LHF2 + " TEXT";
                db.execSQL(fellingdetailsLHF2Query);
            }
            boolean fellingdetailsLHVOLUME_Flag = existsColumnInTable(db, Common.TBL_FELLINGREGISTRATIONDETAILS, Common.LHVOLUME);
            if (fellingdetailsLHVOLUME_Flag == false) {
                String fellingdetailsLHVOLUMEQuery = "ALTER TABLE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " ADD COLUMN " + Common.LHVOLUME + " TEXT";
                db.execSQL(fellingdetailsLHVOLUMEQuery);
            }
        }
    }

    public void Version6_2_6(SQLiteDatabase db) {
        boolean purchaseAgreementTBLFlag = tableExists(db, Common.TBL_PURCHASEAGREEMENT);
        if (purchaseAgreementTBLFlag == false) {
            db.execSQL(DbClassObject.PurchaseAgreement());
        }
        boolean purchaseScannedTBLFlag = tableExists(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS);
        if (purchaseScannedTBLFlag == false) {
            db.execSQL(DbClassObject.PurchaseAgreementScannedLogs());
        }
        boolean PurchaseId_Flag = existsColumnInTable(db, Common.TBL_INVENTORYTRANSFERSCANNED, Common.Purchase.PurchaseId);
        if (PurchaseId_Flag == false) {
            String PurchaseId_FlagEQuery = "ALTER TABLE " + Common.TBL_INVENTORYTRANSFERSCANNED + " ADD COLUMN " + Common.Purchase.PurchaseId + " TEXT";
            db.execSQL(PurchaseId_FlagEQuery);
        }
        boolean recePurchaseId_Flag = existsColumnInTable(db, Common.TBL_INVENTORYRECEIVED, Common.Purchase.PurchaseId);
        if (recePurchaseId_Flag == false) {
            String PurchaseId_FlagEQuery = "ALTER TABLE " + Common.TBL_INVENTORYRECEIVED + " ADD COLUMN " + Common.Purchase.PurchaseId + " TEXT";
            db.execSQL(PurchaseId_FlagEQuery);
        }
        // Transfer tables
        boolean agency_Flag = existsColumnInTable(db, Common.TBL_INVENTORYTRANSFERIDLIST, Common.ExternalDataBaseClass.AGENCYNAME);
        if (agency_Flag == false) {
            String agency_FlagEQuery = "ALTER TABLE " + Common.TBL_INVENTORYTRANSFERIDLIST + " ADD COLUMN " + Common.ExternalDataBaseClass.AGENCYNAME + " TEXT";
            db.execSQL(agency_FlagEQuery);
        }
        boolean driver_Flag = existsColumnInTable(db, Common.TBL_INVENTORYTRANSFERIDLIST, Common.ExternalDataBaseClass.DRIVERNAME);
        if (driver_Flag == false) {
            String driver_FlagEQuery = "ALTER TABLE " + Common.TBL_INVENTORYTRANSFERIDLIST + " ADD COLUMN " + Common.ExternalDataBaseClass.DRIVERNAME + " TEXT";
            db.execSQL(driver_FlagEQuery);
        }
        boolean truckid_Flag = existsColumnInTable(db, Common.TBL_INVENTORYTRANSFERIDLIST, Common.TRUCKID);
        if (truckid_Flag == false) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_INVENTORYTRANSFERIDLIST + " ADD COLUMN " + Common.TRUCKID + " INTEGER NOT NULL DEFAULT 0";
            db.execSQL(truckid_FlagEQuery);
        }
        // Received tables
        boolean Receagency_Flag = existsColumnInTable(db, Common.TBL_INVENTORYRECEIVEDLIST, Common.ExternalDataBaseClass.AGENCYNAME);
        if (Receagency_Flag == false) {
            String agency_FlagEQuery = "ALTER TABLE " + Common.TBL_INVENTORYRECEIVEDLIST + " ADD COLUMN " + Common.ExternalDataBaseClass.AGENCYNAME + " TEXT";
            db.execSQL(agency_FlagEQuery);
        }
        boolean Recedriver_Flag = existsColumnInTable(db, Common.TBL_INVENTORYRECEIVEDLIST, Common.ExternalDataBaseClass.DRIVERNAME);
        if (Recedriver_Flag == false) {
            String driver_FlagEQuery = "ALTER TABLE " + Common.TBL_INVENTORYRECEIVEDLIST + " ADD COLUMN " + Common.ExternalDataBaseClass.DRIVERNAME + " TEXT";
            db.execSQL(driver_FlagEQuery);
        }
        boolean Recetruckid_Flag = existsColumnInTable(db, Common.TBL_INVENTORYRECEIVEDLIST, Common.TRUCKID);
        if (Recetruckid_Flag == false) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_INVENTORYRECEIVEDLIST + " ADD COLUMN " + Common.TRUCKID + " INTEGER NOT NULL DEFAULT 0";
            db.execSQL(truckid_FlagEQuery);
        }
    }

    public void Version6_3_2(SQLiteDatabase db) {
        boolean purchaseAgreementTBLFlag = tableExists(db, Common.TBL_PURCHASETRANSFER_LIST);
        if (purchaseAgreementTBLFlag == false) {
            db.execSQL(DbClassObject.PurchaseTransferListUpdated6_3_2());
        }
        boolean purchaseScannedTBLFlag = tableExists(db, Common.TBL_PURCHASERECEIVED_LIST);
        if (purchaseScannedTBLFlag == false) {
            db.execSQL(DbClassObject.PurchaseReceivedListUpdated6_3_2());
        }
    }

    public void Version6_3_3(SQLiteDatabase db) {
        boolean purchaseScannedTBLFlag = tableExists(db, Common.TBL_PURCHASERECEIVED_LIST);
        if (purchaseScannedTBLFlag == false) {
            db.execSQL(DbClassObject.PurchaseReceivedListUpdated6_3_3());
        }
        boolean Recetruckid_Flag = existsColumnInTable(db, Common.TBL_PURCHASERECEIVED_LIST, Common.TRANSFERUNIQUEID);
        if (Recetruckid_Flag == false) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASERECEIVED_LIST + " ADD COLUMN " + Common.TRANSFERUNIQUEID + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
    }

    public void Version6_4_2(SQLiteDatabase db) {
        boolean purchaseScannedTBLFlag = tableExists(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS);
        if (!purchaseScannedTBLFlag) {
            db.execSQL(DbClassObject.PurchaseAgreementScannedLogs6_4_2());
        }
        boolean REMARKS_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.REMARKS);
        if (!REMARKS_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.REMARKS + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        boolean REMARKSTYPE_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.REMARKSTYPE);
        if (!REMARKSTYPE_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.REMARKSTYPE + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        // lengthcut
        boolean LENGTHCUTFOOT_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.LENGTHCUTFOOT);
        if (!LENGTHCUTFOOT_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.LENGTHCUTFOOT + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        boolean LENGTHCUTTOP_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.LENGTHCUTTOP);
        if (!LENGTHCUTTOP_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.LENGTHCUTTOP + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        //crack
        boolean CRACKF1_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.CRACKF1);
        if (!CRACKF1_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.CRACKF1 + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        boolean CRACKF2_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.CRACKF2);
        if (!CRACKF2_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.CRACKF2 + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        boolean CRACKT1_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.CRACKT1);
        if (!CRACKT1_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.CRACKT1 + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        boolean CRACKT2_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.CRACKT2);
        if (!CRACKT2_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.CRACKT2 + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        boolean CRACKVOLUME_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.CRACKVOLUME);
        if (!CRACKVOLUME_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.CRACKVOLUME + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        //hole
        boolean HOLEF1_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.HOLEF1);
        if (!HOLEF1_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.HOLEF1 + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        boolean HOLEF2_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.HOLEF2);
        if (!HOLEF2_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.HOLEF2 + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        boolean HOLET1_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.HOLET1);
        if (!HOLET1_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.HOLET1 + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        boolean HOLET2_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.HOLET2);
        if (!HOLET2_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.HOLET2 + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        boolean HOLEVOLUME_Flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.HOLEVOLUME);
        if (!HOLEVOLUME_Flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.HOLEVOLUME + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }
        // sap deductions
        boolean SAPDEDUCTION_flag = existsColumnInTable(db, Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, Common.SAPDEDUCTION);
        if (!SAPDEDUCTION_flag) {
            String truckid_FlagEQuery = "ALTER TABLE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " ADD COLUMN " + Common.SAPDEDUCTION + " TEXT";
            db.execSQL(truckid_FlagEQuery);
        }

    }

    public boolean tableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen()) {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    private boolean existsColumnInTable(SQLiteDatabase inDatabase, String inTable, String columnToCheck) {
        Cursor mCursor = null;
        try {
            // Query 1 row
            mCursor = inDatabase.rawQuery("SELECT * FROM " + inTable + " LIMIT 0", null);

            // getColumnIndex() gives us the index (0 to ...) of the column - otherwise we get a -1
            return mCursor.getColumnIndex(columnToCheck) != -1;

        } catch (Exception Exp) {
            // Something went wrong. Missing the database? The table?
            Log.d("existsColumnInTable :%s", "When checking whether a column exists in the table, an error occurred: " + Exp.getMessage());
            return false;
        } finally {
            if (mCursor != null) mCursor.close();
        }
    }

    /* @Override
     public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.setVersion(oldVersion);
     }*/

    public void openDatabase() {
        String dbPath = mContext.getDatabasePath(Common.INTERNELDBNAME).getPath();
        if (mDatabase != null && mDatabase.isOpen()) {
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void closeDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    /*Inventory Count Details*/
    public void insertScannedResult(int ToLocationID, String ToLocationName, String SbbLabel, String WoodSpieceID, String WoodSpieceCode,
                                    String DateTime, String IMEI, String BarCode, int EntryMode, int IsActive, int ListID, int IsSBBLabelCorrected,
                                    String OrgSBBLabel, String Volume, String FellingSecID, String TreeNO, String Quality) {
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.LOCATION_ID, ToLocationID);
            values.put(Common.LOCATION_NAME, ToLocationName);
            values.put(Common.SBBLabel, SbbLabel);
            values.put(Common.WoodSpiceID, WoodSpieceID);
            values.put(Common.WoodSPiceCode, WoodSpieceCode);
            values.put(Common.DATETIME, DateTime);
            values.put(Common.IMEINumber, IMEI);
            values.put(Common.BARCODE, BarCode);
            values.put(Common.ENTRYMODE, EntryMode);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.LISTID, ListID);
            values.put(Common.VOLUME, Volume);
            values.put(Common.ISSBLABELCORRECT, IsSBBLabelCorrected);
            values.put(Common.ORGSBBLABEL, OrgSBBLabel);
            values.put(Common.FELLING_SECTIONID, FellingSecID);
            values.put(Common.TREENO, TreeNO);
            values.put(Common.QULAITY, Quality);
            mDatabase.insert(Common.TBL_SCANNEDRESULT, null, values);
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertScannedResult", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
    }

    public boolean insertScannedResultFlag(int ToLocationID, String ToLocationName, String SbbLabel, String WoodSpieceID, String WoodSpieceCode,
                                           String DateTime, String IMEI, String BarCode, int EntryMode, int IsActive, int ListID, int IsSBBLabelCorrected,
                                           String OrgSBBLabel, String volume, String FellingSecID, String TreeNO, String quality) {

        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.LOCATION_ID, ToLocationID);
            values.put(Common.LOCATION_NAME, ToLocationName);
            values.put(Common.SBBLabel, SbbLabel);
            values.put(Common.WoodSpiceID, WoodSpieceID);
            values.put(Common.WoodSPiceCode, WoodSpieceCode);
            values.put(Common.DATETIME, DateTime);
            values.put(Common.IMEINumber, IMEI);
            values.put(Common.BARCODE, BarCode);
            values.put(Common.ENTRYMODE, EntryMode);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.LISTID, ListID);
            values.put(Common.VOLUME, volume);
            values.put(Common.ISSBLABELCORRECT, IsSBBLabelCorrected);
            values.put(Common.ORGSBBLABEL, OrgSBBLabel);
            values.put(Common.FELLING_SECTIONID, FellingSecID);
            values.put(Common.TREENO, TreeNO);
            values.put(Common.QULAITY, quality);
            mDatabase.insert(Common.TBL_SCANNEDRESULT, null, values);
            mDatabase.setTransactionSuccessful();
            Result = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertScannedResultFlag", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean insertInventoryCountListID(String IMEI, int LocationID, String startDate, String endDate, int count, int syncStatus, String syncTime, int ISactive, String countUniqueID) {
        boolean Result = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.IMEINumber, IMEI);
            values.put(Common.LOCATION_ID, LocationID);
            values.put(Common.STARTDATETIME, startDate);
            values.put(Common.ENDDATETIME, endDate);
            values.put(Common.ISACTIVE, ISactive);
            values.put(Common.COUNT, count);
            values.put(Common.SYNCSTATUS, syncStatus);
            values.put(Common.SYNCTIME, syncTime);
            values.put(Common.COUNTUNIQUEID, countUniqueID);
            mDatabase.insert(Common.TBL_INVENTORYCOUNTLIST, null, values);
            mDatabase.setTransactionSuccessful();
            Result = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertInventoryCountListID", e.toString(), false);
            Result = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean insertInventoryReceivedIDList(String VBB_Number, String IMEI, int ToLocationID, String StartDate, String EndDate,
                                                 int FromLocationid, int TransportTypeID, int TransferAgencyID, int DriverID, int TrucklicensePlateNo,
                                                 int UserID, int Count, int SyncStatus, String SyncTime, String Volume, int IsActive, String ReceiUniqID) {
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.VBBNUMBER, VBB_Number);
            values.put(Common.IMEINumber, IMEI);
            values.put(Common.LOCATION_ID, ToLocationID);
            values.put(Common.STARTDATETIME, StartDate);
            values.put(Common.ENDDATETIME, EndDate);
            values.put(Common.FROMLOCATIONID, FromLocationid);
            values.put(Common.TRANSPORTTYPEID, TransportTypeID);
            values.put(Common.TRANSFERAGENCYID, TransferAgencyID);
            values.put(Common.DRIVERID, DriverID);
            values.put(Common.TRUCKPLATENUMBER, TrucklicensePlateNo);
            values.put(Common.USERID, UserID);
            values.put(Common.COUNT, Count);
            values.put(Common.VOLUME, Volume);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.SYNCSTATUS, SyncStatus);
            values.put(Common.SYNCTIME, SyncTime);
            values.put(Common.RECEIVEDUNIQUEID, ReceiUniqID);
            mDatabase.insert(Common.TBL_INVENTORYRECEIVEDLIST, null, values);
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertInventoryReceivedIDList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return true;
    }

    public boolean insertInventoryReceivedItemsFlag(String VBB_Number, int TransferID, int receivedID, String FromLocationname, String ToLocationName,
                                                    String SbbLabel, String BarCode, String Length, String Volume, int UserID, String DateTime,
                                                    String WoodSpieceID, String WoodSpieceCode, int EntryMode, int IsActive, int IsSBBLabelCorrected,
                                                    String OldSBBLabel, String FellingSecID, String TreeNumner, String Classfication, String IsChecked,
                                                    String TransReceUniqueID, String purchaseID) {
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.VBBNUMBER, VBB_Number);
            values.put(Common.TRANSFERID, TransferID);
            values.put(Common.RECEIVEDID, receivedID);
            values.put(Common.FROMLOCATION, FromLocationname);
            values.put(Common.TOLOCATION, ToLocationName);
            values.put(Common.SBBLabel, SbbLabel);
            values.put(Common.BARCODE, BarCode);
            values.put(Common.LENGTH, Length);
            values.put(Common.VOLUME, Volume);
            values.put(Common.USERID, UserID);
            values.put(Common.DATETIME, DateTime);
            values.put(Common.WoodSpiceID, WoodSpieceID);
            values.put(Common.WoodSPiceCode, WoodSpieceCode);
            values.put(Common.ENTRYMODE, EntryMode);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.ISSBLABELCORRECT, IsSBBLabelCorrected);
            values.put(Common.ORGSBBLABEL, OldSBBLabel);
            values.put(Common.FELLING_SECTIONID, FellingSecID);
            values.put(Common.QULAITY, Classfication);
            values.put(Common.TRANSFERUNIQUEID, TransReceUniqueID);
            values.put(Common.TREENO, TreeNumner);
            values.put(Common.ISCHECKED, IsChecked);
            values.put(Common.Purchase.PurchaseId, purchaseID);
            mDatabase.insert(Common.TBL_INVENTORYRECEIVED, null, values);
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertInventoryReceivedItemsFlag", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return true;
    }

    public boolean insertInventoryReceivedItemsFlagWithPurchaseID(String VBB_Number, int TransferID, int receivedUniqueID, String FromLocationname, String ToLocationName,
                                                                  String SbbLabel, String BarCode, String Length, String Volume, int UserID, String DateTime,
                                                                  String WoodSpieceID, String WoodSpieceCode, int EntryMode, int IsActive, int IsSBBLabelCorrected,
                                                                  String OldSBBLabel, String FellingSecID, String TreeNumner, String Classfication, String IsChecked,
                                                                  String TransReceUniqueID, int purchaseID) {
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.VBBNUMBER, VBB_Number);
            values.put(Common.TRANSFERID, TransferID);
            values.put(Common.RECEIVEDID, receivedUniqueID);
            values.put(Common.FROMLOCATION, FromLocationname);
            values.put(Common.TOLOCATION, ToLocationName);
            values.put(Common.SBBLabel, SbbLabel);
            values.put(Common.BARCODE, BarCode);
            values.put(Common.LENGTH, Length);
            values.put(Common.VOLUME, Volume);
            values.put(Common.USERID, UserID);
            values.put(Common.DATETIME, DateTime);
            values.put(Common.WoodSpiceID, WoodSpieceID);
            values.put(Common.WoodSPiceCode, WoodSpieceCode);
            values.put(Common.ENTRYMODE, EntryMode);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.ISSBLABELCORRECT, IsSBBLabelCorrected);
            values.put(Common.ORGSBBLABEL, OldSBBLabel);
            values.put(Common.FELLING_SECTIONID, FellingSecID);
            values.put(Common.QULAITY, Classfication);
            values.put(Common.TRANSFERUNIQUEID, TransReceUniqueID);
            values.put(Common.TREENO, TreeNumner);
            values.put(Common.ISCHECKED, IsChecked);
            values.put(Common.Purchase.PurchaseId, purchaseID);
            mDatabase.insert(Common.TBL_INVENTORYRECEIVED, null, values);
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertInventoryReceivedItemsFlag", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return true;
    }

    public boolean UpdateInventoryReceivedItems(int receivedID, String BarCode, String CheckedValue) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYRECEIVED + " SET "
                    + Common.ISCHECKED + " = '" + CheckedValue + "'"
                    + " WHERE " + Common.RECEIVEDID + " = " + receivedID + " and " + Common.BARCODE + " ='" + BarCode + "'";
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedItems", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateInventoryReceivedItemsPuchaseID(int receivedID, String BarCode, String CheckedValue) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYRECEIVED + " SET "
                    + Common.ISCHECKED + " = '" + CheckedValue + "'"
                    + " WHERE " + Common.Purchase.PurchaseId + " = " + receivedID + " and " + Common.BARCODE + " ='" + BarCode + "'";
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedItemsPuchaseID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

/*
    public String ColculateInventoryCountItems(int listID) {
        int TotalCountItems = 0;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT COUNT(ID) as TotalCountItems FROM " + TBL_SCANNEDRESULT +
                    " WHERE " + LISTID + " = " + listID + " and " + ISACTIVE + " = " + 1;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                TotalCountItems = cursor.getInt(cursor.getColumnIndex("TotalCountItems"));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return String.valueOf(TotalCountItems);
    }

    public String ColculateInventoryTransferItems(int transferID) {
        int TotalCountItems = 0;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT COUNT(ID) as TotalTransferItems FROM " + TBL_INVENTORYTRANSFERSCANNED +
                    " WHERE " + TRANSFERID + " = " + transferID + " and " + ISACTIVE + " = " + 1;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                TotalCountItems = cursor.getInt(cursor.getColumnIndex("TotalTransferItems"));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return String.valueOf(TotalCountItems);
    }

    public String ColculateInventoryReceivedItems(int receivedID) {
        int TotalCountItems = 0;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT COUNT(ID) as TotalReceivedItems FROM " + TBL_INVENTORYRECEIVED +
                    " WHERE " + RECEIVEDID + " = " + receivedID + " and " + ISACTIVE + " = " + 1;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                TotalCountItems = cursor.getInt(cursor.getColumnIndex("TotalReceivedItems"));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return String.valueOf(TotalCountItems);
    }*/

    public int ColculateInventoryReceivedCheckedItems(int receivedID, String Sbblabel, String CheckedValue) {
        int TotalCountItems = 0;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            /*String selectQuery = "SELECT COUNT(IsReceived) as TotalReceivedCheckedItems FROM " + TBL_INVENTORYRECEIVED +
                    " WHERE " + RECEIVEDID + " = " + receivedID + " and " + ISCHECKED + " = '" + CheckedValue + "'";*/

            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED +
                    " WHERE " + Common.RECEIVEDID + " = " + receivedID + " and " + Common.ISCHECKED + " = '" + CheckedValue + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotalCountItems = TotalCountItems + 1;
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "ColculateInventoryReceivedCheckedItems", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotalCountItems;
    }

    public boolean UpdateInventoryCountUniqueID(int CountID, String countUniqueID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYCOUNTLIST + " SET "
                    + Common.COUNTUNIQUEID + " = '" + countUniqueID + "'" +
                    " WHERE " + Common.LISTID + " = " + CountID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryTransferUniqueID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateInventoryCountListID(String endDateTime, int ScannedCount, int CountID, String VolumeSum, int Isactive, String CountUniqID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYCOUNTLIST + " SET "
                    + Common.ENDDATETIME + " = '" + endDateTime + "' , "
                    + "[" + Common.COUNT + "]" + "= '" + ScannedCount + "', "
                    + Common.ISACTIVE + " = '" + Isactive + "' , "
                    + Common.COUNTUNIQUEID + " = '" + CountUniqID + "' , "
                    + Common.VOLUME + " = '" + VolumeSum + "'" +
                    " WHERE " + Common.LISTID + " = " + CountID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryCountListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateInventoryCountSyncStatusListID(String SyncTime, int SyncStatus, String DeviceDateTime, int CountID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYCOUNTLIST + " SET "
                    + Common.SYNCTIME + " = '" + SyncTime + "' , "
                    + Common.SYNCSTATUS + " = '" + SyncStatus + "' , "
                    + Common.SYNCDEVICETIME + " = '" + DeviceDateTime + "' WHERE " + Common.LISTID + " = " + CountID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryCountSyncStatusListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public ArrayList<InventoryCountModel> getInventoryCountIdList(String SelectedDate) {
        ArrayList<InventoryCountModel> scannedReusltList = new ArrayList<InventoryCountModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYCOUNTLIST + " WHERE IsActive is null or  IsActive  = 1";
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYCOUNTLIST + " WHERE IsActive  = 1";
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYCOUNTLIST + " WHERE substr(StartDateTime,1,11)='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                countModel = new InventoryCountModel();
                countModel.setListID(cursor.getInt(cursor.getColumnIndex(Common.LISTID)));
                countModel.setImei(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                countModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATION_ID)));
                countModel.setStartTime(cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME)));
                countModel.setEndTime(cursor.getString(cursor.getColumnIndex(Common.ENDDATETIME)));
                countModel.setCount(cursor.getInt(cursor.getColumnIndex(Common.COUNT)));
                countModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                countModel.setISActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                countModel.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Common.SYNCSTATUS)));
                countModel.setSyncTime(cursor.getString(cursor.getColumnIndex(Common.SYNCTIME)));
                countModel.setOLDListID(cursor.getInt(cursor.getColumnIndex("OLDListID")));
                scannedReusltList.add(countModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryCountIdList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public ArrayList<InventoryReceivedListModel> getInventoryReceivedIdList(String SelectedDate) {
        ArrayList<InventoryReceivedListModel> scannedReusltList = new ArrayList<InventoryReceivedListModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            /*String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVEDLIST + " WHERE " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);*/
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVEDLIST + " WHERE substr(StartDateTime,1,11)='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1";
            //+ " and " + Common.TRANSPORTTYPEID + "=" + Common.TransportTypeId;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                ReceivedModel = new InventoryReceivedListModel();
                ReceivedModel.setReceivedID(cursor.getInt(cursor.getColumnIndex(Common.RECEIVEDID)));
                ReceivedModel.setReceivedUniqueID(cursor.getString(cursor.getColumnIndex(Common.RECEIVEDUNIQUEID)));
                ReceivedModel.setVBB_Number(cursor.getString(cursor.getColumnIndex(Common.VBBNUMBER)));
                ReceivedModel.setIMEI(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                ReceivedModel.setToLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATION_ID)));
                ReceivedModel.setStartDateTime(cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME)));
                ReceivedModel.setEndDateTime(cursor.getString(cursor.getColumnIndex(Common.ENDDATETIME)));
                ReceivedModel.setFromLocationID(cursor.getInt(cursor.getColumnIndex(Common.FROMLOCATIONID)));
                ReceivedModel.setTransportTypeId(cursor.getInt(cursor.getColumnIndex(Common.TRANSPORTTYPEID)));
                ReceivedModel.setTransferAgencyID(cursor.getInt(cursor.getColumnIndex(Common.TRANSFERAGENCYID)));
                ReceivedModel.setDriverID(cursor.getInt(cursor.getColumnIndex(Common.DRIVERID)));
                ReceivedModel.setTruckPlateNumber(cursor.getString(cursor.getColumnIndex(Common.TRUCKPLATENUMBER)));
                ReceivedModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                ReceivedModel.setCount(cursor.getInt(cursor.getColumnIndex(Common.COUNT)));
                ReceivedModel.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Common.SYNCSTATUS)));
                ReceivedModel.setSyncTime(cursor.getString(cursor.getColumnIndex(Common.SYNCTIME)));
                ReceivedModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                ReceivedModel.setTransferUniqueID(cursor.getString(cursor.getColumnIndex(Common.TRANSFERUNIQUEID)));
                ReceivedModel.setLoadedTypeID(cursor.getInt(cursor.getColumnIndex(Common.LOADEDTYPE)));
                ReceivedModel.setAgencyName(cursor.getString(cursor.getColumnIndex(Common.ExternalDataBaseClass.AGENCYNAME)));
                ReceivedModel.setDriverName(cursor.getString(cursor.getColumnIndex(Common.ExternalDataBaseClass.DRIVERNAME)));
                ReceivedModel.setTruckId(cursor.getInt(cursor.getColumnIndex(Common.TRUCKID)));
                scannedReusltList.add(ReceivedModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryReceivedIdList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public ArrayList<ScannedResultModel> getScannedResult() {
        ArrayList<ScannedResultModel> scannedReusltList = new ArrayList<ScannedResultModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_SCANNEDRESULT;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                scannedModel = new ScannedResultModel();
                scannedModel.setToLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATION_ID)));
                scannedModel.setToLocationName(cursor.getString(cursor.getColumnIndex(Common.LOCATION_NAME)));
                scannedModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                scannedModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                scannedModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                scannedModel.setDateTime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));
                scannedModel.setIMEI(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                scannedModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                scannedModel.setEntryMode(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                scannedModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                scannedModel.setListID(cursor.getInt(cursor.getColumnIndex(Common.LISTID)));
                scannedModel.setIsSBBLabelCorrected(cursor.getInt(cursor.getColumnIndex(Common.ISSBLABELCORRECT)));
                scannedModel.setOrgSBBLabel(cursor.getString(cursor.getColumnIndex(Common.ORGSBBLABEL)));
                scannedModel.setFellingSectionId(cursor.getString(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                scannedModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                scannedReusltList.add(scannedModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getScannedResult", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public ArrayList<ScannedResultModel> getScannedResultWithListID(int List_ID) {
        ArrayList<ScannedResultModel> scannedReusltList = new ArrayList<ScannedResultModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_SCANNEDRESULT + " where " + Common.LISTID + "=" + List_ID + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                scannedModel = new ScannedResultModel();
                scannedModel.setToLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATION_ID)));
                scannedModel.setToLocationName(cursor.getString(cursor.getColumnIndex(Common.LOCATION_NAME)));
                scannedModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                scannedModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                scannedModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                scannedModel.setDateTime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));
                scannedModel.setIMEI(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                scannedModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                scannedModel.setEntryMode(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                scannedModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                scannedModel.setListID(cursor.getInt(cursor.getColumnIndex(Common.LISTID)));
                scannedModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                scannedModel.setIsSBBLabelCorrected(cursor.getInt(cursor.getColumnIndex(Common.ISSBLABELCORRECT)));
                scannedModel.setOrgSBBLabel(cursor.getString(cursor.getColumnIndex(Common.ORGSBBLABEL)));
                scannedModel.setFellingSectionId(cursor.getString(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                scannedModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                scannedModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                scannedReusltList.add(scannedModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getScannedResultWithListID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public ArrayList<InventoryTransferScannedResultModel> getInventoryTransferWithVBBNumber(String vbb_number, int TransferID) {
        ArrayList<InventoryTransferScannedResultModel> scannedReusltList = new ArrayList<InventoryTransferScannedResultModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + "=" + TransferID + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                inventoryTransModel = new InventoryTransferScannedResultModel();
                inventoryTransModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                inventoryTransModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                inventoryTransModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                inventoryTransModel.setFromLocation(cursor.getString(cursor.getColumnIndex(Common.FROMLOCATION)));
                inventoryTransModel.setToLocation(cursor.getString(cursor.getColumnIndex(Common.TOLOCATION)));
                inventoryTransModel.setVolume(cursor.getString(cursor.getColumnIndex(Common.VOLUME)));
                inventoryTransModel.setVBB_Number(cursor.getString(cursor.getColumnIndex(Common.VBBNUMBER)));
                inventoryTransModel.setUserID(cursor.getString(cursor.getColumnIndex(Common.USERID)));
                inventoryTransModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                inventoryTransModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                inventoryTransModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                inventoryTransModel.setQualitiy(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                inventoryTransModel.setFellingSectionId(cursor.getInt(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                inventoryTransModel.setTreeNumber(cursor.getInt(cursor.getColumnIndex(Common.TREENO)));
                //inventoryTransModel.setTransUniqueID(cursor.getString(cursor.getColumnIndex(TRANSFERUNIQUEID)));
                /*18-11-2019*/
         /*       inventoryTransModel.setTreePartType(cursor.getString(cursor.getColumnIndex(Common.TreePart)));
                inventoryTransModel.setFooter_1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                inventoryTransModel.setFooter_2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                inventoryTransModel.setTop_1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                inventoryTransModel.setTop_2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                inventoryTransModel.setNoteF(cursor.getString(cursor.getColumnIndex(Common.NOTEF)));
                inventoryTransModel.setNoteL(cursor.getString(cursor.getColumnIndex(Common.NOTEL)));
                inventoryTransModel.setNoteT(cursor.getString(cursor.getColumnIndex(Common.NOTET)));*/
                scannedReusltList.add(inventoryTransModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferWithVBBNumber", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public ArrayList<InventoryTransferScannedResultModel> getInventoryTransferWithPurchaseID(int TransferID) {
        ArrayList<InventoryTransferScannedResultModel> scannedReusltList = new ArrayList<InventoryTransferScannedResultModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.Purchase.PurchaseId + "=" + TransferID + " and " + Common.ISACTIVE + " = 1" + " and " + Common.TRANSFERID + " = '" + Common.TransferUniqueID + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                inventoryTransModel = new InventoryTransferScannedResultModel();
                inventoryTransModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                inventoryTransModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                inventoryTransModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                inventoryTransModel.setFromLocation(cursor.getString(cursor.getColumnIndex(Common.FROMLOCATION)));
                inventoryTransModel.setToLocation(cursor.getString(cursor.getColumnIndex(Common.TOLOCATION)));
                inventoryTransModel.setVolume(cursor.getString(cursor.getColumnIndex(Common.VOLUME)));
                inventoryTransModel.setVBB_Number(cursor.getString(cursor.getColumnIndex(Common.VBBNUMBER)));
                inventoryTransModel.setUserID(cursor.getString(cursor.getColumnIndex(Common.USERID)));
                inventoryTransModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                inventoryTransModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                inventoryTransModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                inventoryTransModel.setQualitiy(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                inventoryTransModel.setFellingSectionId(cursor.getInt(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                inventoryTransModel.setTreeNumber(cursor.getInt(cursor.getColumnIndex(Common.TREENO)));
                //inventoryTransModel.setTransUniqueID(cursor.getString(cursor.getColumnIndex(TRANSFERUNIQUEID)));
                /*18-11-2019*/
         /*       inventoryTransModel.setTreePartType(cursor.getString(cursor.getColumnIndex(Common.TreePart)));
                inventoryTransModel.setFooter_1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                inventoryTransModel.setFooter_2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                inventoryTransModel.setTop_1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                inventoryTransModel.setTop_2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                inventoryTransModel.setNoteF(cursor.getString(cursor.getColumnIndex(Common.NOTEF)));
                inventoryTransModel.setNoteL(cursor.getString(cursor.getColumnIndex(Common.NOTEL)));
                inventoryTransModel.setNoteT(cursor.getString(cursor.getColumnIndex(Common.NOTET)));*/
                scannedReusltList.add(inventoryTransModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferWithVBBNumber", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public ArrayList<InventoryReceivedModel> getInventoryReceivedWithVBBNumber(String vbb_number, int ReceivedID) {
        ArrayList<InventoryReceivedModel> scannedReusltList = new ArrayList<InventoryReceivedModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.RECEIVEDID + "=" + ReceivedID + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                inventoryReceivedModel = new InventoryReceivedModel();
                inventoryReceivedModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                inventoryReceivedModel.setLength(cursor.getInt(cursor.getColumnIndex(Common.LENGTH)));
                inventoryReceivedModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                inventoryReceivedModel.setFromLocation(cursor.getString(cursor.getColumnIndex(Common.FROMLOCATION)));
                inventoryReceivedModel.setToLocation(cursor.getString(cursor.getColumnIndex(Common.TOLOCATION)));
                inventoryReceivedModel.setVolume(cursor.getString(cursor.getColumnIndex(Common.VOLUME)));
                inventoryReceivedModel.setVBB_Number(cursor.getString(cursor.getColumnIndex(Common.VBBNUMBER)));
                inventoryReceivedModel.setUserID(cursor.getString(cursor.getColumnIndex(Common.USERID)));
                inventoryReceivedModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                inventoryReceivedModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                inventoryReceivedModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                inventoryReceivedModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                inventoryReceivedModel.setIsReceived(cursor.getString(cursor.getColumnIndex(Common.ISCHECKED)));
                inventoryReceivedModel.setTransferUniqueID(cursor.getString(cursor.getColumnIndex(Common.TRANSFERUNIQUEID)));
                scannedReusltList.add(inventoryReceivedModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryReceivedWithVBBNumber", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public ArrayList<String> getTransferIDsList(int ReceivedID) {
        ArrayList<String> TrnasferIDSList = new ArrayList<String>();
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT " + Common.TRANSFERUNIQUEID + "  FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.RECEIVEDID + "=" + ReceivedID + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TrnasferIDSList.add(cursor.getString(cursor.getColumnIndex(Common.TRANSFERUNIQUEID)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getTransferIDsList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TrnasferIDSList;
    }

    public boolean getScannedResultWithMapListIDCheck(int List_ID, String Barcode, String OrgSBBLabel) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_SCANNEDRESULT + " where " + Common.LISTID + "=" + List_ID + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.SBBLabel + " = " + SbbLabel + " or " + Common.ORGSBBLABEL + " = " + SbbLabel + ")";
            String selectQuery = "SELECT * FROM " + Common.TBL_SCANNEDRESULT + " where " + Common.LISTID + "=" + List_ID + " and " + Common.ISACTIVE + " = " + 1 + " and " + Common.BARCODE + " = '" + Barcode + "' or " + Common.ORGSBBLABEL + " ='" + OrgSBBLabel + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getScannedResultWithMapListIDCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public ArrayList<InventoryCountInputListModel> getScannedResultInputWithListID(int List_ID) {
        ArrayList<InventoryCountInputListModel> scannedReusltList = new ArrayList<InventoryCountInputListModel>();
        int ID = 1;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_SCANNEDRESULT + " where " + Common.LISTID + "=" + List_ID + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                inventoryInputModel = new InventoryCountInputListModel();
                inventoryInputModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                inventoryInputModel.setDatetime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));
                inventoryInputModel.setEntryMode(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.FELLING_SECTIONID)))) {
                    inventoryInputModel.setFellingSectionId(0);
                } else {
                    inventoryInputModel.setFellingSectionId(cursor.getInt(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                }
                inventoryInputModel.setID(ID);
                inventoryInputModel.setIsSBBLabelCorrected(cursor.getInt(cursor.getColumnIndex(Common.ISSBLABELCORRECT)));
                inventoryInputModel.setOrgSBBLabel(cursor.getString(cursor.getColumnIndex(Common.ORGSBBLABEL)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QULAITY)))) {
                    inventoryInputModel.setQuality("A");
                } else {
                    inventoryInputModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                }
                inventoryInputModel.setSBBLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                inventoryInputModel.setToLocation(cursor.getString(cursor.getColumnIndex(Common.LOCATION_NAME)));
                inventoryInputModel.setToLocationId(cursor.getInt(cursor.getColumnIndex(Common.LOCATION_ID)));
                inventoryInputModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                if (cursor.getString(cursor.getColumnIndex(Common.VOLUME)).equals("")) {
                    inventoryInputModel.setVolume(0.00);
                } else {
                    inventoryInputModel.setVolume(parseDouble(cursor.getString(cursor.getColumnIndex(Common.VOLUME))));
                }
                inventoryInputModel.setWoodSpecieCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                inventoryInputModel.setWoodSpecieId(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                scannedReusltList.add(inventoryInputModel);
                ID = ID + 1;
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getScannedResultInputWithListID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    /*Inventory Transfer Detials*/
    public boolean insertInventoryTransferID(String VBB_Number, String IMEI, int ToLocationID, String StartDate, String EndDate,
                                             int FromLocationid, int TransportTypeID, int TransferAgencyID, int DriverID, int TrucklicensePlateNo,
                                             int UserID, int Count, int SyncStatus, String SyncTime, String Volume, int Isactive, String transUniqueID) {
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.VBBNUMBER, VBB_Number);
            values.put(Common.IMEINumber, IMEI);
            values.put(Common.LOCATION_ID, ToLocationID);
            values.put(Common.STARTDATETIME, StartDate);
            values.put(Common.ENDDATETIME, EndDate);
            values.put(Common.FROMLOCATIONID, FromLocationid);
            values.put(Common.TRANSPORTTYPEID, TransportTypeID);
            values.put(Common.TRANSFERAGENCYID, TransferAgencyID);
            values.put(Common.DRIVERID, DriverID);
            values.put(Common.TRUCKPLATENUMBER, TrucklicensePlateNo);
            values.put(Common.USERID, UserID);
            values.put(Common.COUNT, Count);
            values.put(Common.ISACTIVE, Isactive);
            values.put(Common.TRANSFERUNIQUEID, transUniqueID);
            values.put(Common.VOLUME, Volume);
            values.put(Common.SYNCSTATUS, SyncStatus);
            values.put(Common.SYNCTIME, SyncTime);
            mDatabase.insert(Common.TBL_INVENTORYTRANSFERIDLIST, null, values);
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertInventoryTransferID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return true;
    }

    public boolean getInventoryTransferUniqueIdDuplicateCheck(String transferID) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERIDLIST + " where " + Common.TRANSFERUNIQUEID + "='" + transferID + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferIdDuplicateCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getInventoryTransferIdListDuplicateCheck(int vbb_number) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERIDLIST + " where " + Common.VBBNUMBER + "=" + vbb_number;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferIdListDuplicateCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getInventoryTransferIdDuplicateCheck(int transferID) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERIDLIST + " where " + Common.TRANSFERID + "=" + transferID;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferIdDuplicateCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getInventoryReceivedIdDuplicateCheck(int receivedID) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVEDLIST + " where " + Common.RECEIVEDID + "=" + receivedID;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryReceivedIdDuplicateCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getInventoryReceivedIdListDuplicateCheck(int ReceivedID) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVEDLIST + " where " + Common.TRANSFERID + "=" + ReceivedID;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryReceivedIdListDuplicateCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getFellingRegistrationDuplicateCheck(int FellingRegID) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONLIST + " where " + Common.FELLINGREGID + "=" + FellingRegID;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getFellingRegistrationDuplicateCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    /*  public ArrayList<InventoryTransferModel> getInventoryTransferIdListDuplicateCheck(int VVB_Number) {
          ArrayList<InventoryTransferModel> scannedReusltList = new ArrayList<InventoryTransferModel>();
          int ID = 1;
          mDatabase = this.getReadableDatabase();
          mDatabase.beginTransaction();
          try {
              String selectQuery = "SELECT * FROM " + Common.Common.TBL_INVENTORYTRANSFERIDLIST + " where " + Common.VBBNUMBER + "=" + VVB_Number;
              Cursor cursor = mDatabase.rawQuery(selectQuery, null);
              if (cursor.getCount() > 0) {
                  while (cursor.moveToNext()) {
                      transferModel = new InventoryTransferModel();
                      transferModel.setTransferID(ID);
                      transferModel.setVBB_Number(cursor.getString(cursor.getColumnIndex(Common.VBBNUMBER)));
                      transferModel.setIMEI(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                      transferModel.setToLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATION_ID)));
                      transferModel.setStartDateTime(cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME)));
                      transferModel.setEndDateTime(cursor.getString(cursor.getColumnIndex(Common.ENDDATETIME)));
                      transferModel.setFromLocationID(cursor.getInt(cursor.getColumnIndex(FROMLOCATIONID)));
                      transferModel.setTransportTypeId(cursor.getInt(cursor.getColumnIndex(Common.TRANSPORTTYPEID)));
                      transferModel.setTransferAgencyID(cursor.getInt(cursor.getColumnIndex(Common.TRANSFERAGENCYID)));
                      transferModel.setDriverID(cursor.getInt(cursor.getColumnIndex(Common.DRIVERID)));
                      transferModel.setTruckPlateNumber(cursor.getString(cursor.getColumnIndex(Common.TRUCKPLATENUMBER)));
                      transferModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                      transferModel.setCount(cursor.getInt(cursor.getColumnIndex(Common.COUNT)));
                      transferModel.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Common.SYNCSTATUS)));
                      transferModel.setSyncTime(cursor.getString(cursor.getColumnIndex(Common.SYNCTIME)));
                      scannedReusltList.add(transferModel);
                      ID = ID + 1;
                  }
              }
              mDatabase.setTransactionSuccessful();
          } catch (Exception e) {
              e.printStackTrace();
          } finally {
              mDatabase.endTransaction();
              closeDatabase();
          }
          return scannedReusltList;
      }
  */

    public ArrayList<InventoryTransferModel> getInventoryTransferIdList(String SelectedDate) {
        ArrayList<InventoryTransferModel> scannedReusltList = new ArrayList<InventoryTransferModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERIDLIST + " WHERE substr(StartDateTime,1,11)='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1"
                    + " and " + Common.TRANSPORTTYPEID + "=" + Common.TransportTypeId;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                transferModel = new InventoryTransferModel();
                transferModel.setTransferID(cursor.getInt(cursor.getColumnIndex(Common.TRANSFERID)));
                transferModel.setOLDTransferID(cursor.getInt(cursor.getColumnIndex("OLDTransferID")));
                transferModel.setVBB_Number(cursor.getString(cursor.getColumnIndex(Common.VBBNUMBER)));
                transferModel.setIMEI(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                transferModel.setToLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATION_ID)));
                transferModel.setStartDateTime(cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME)));
                transferModel.setEndDateTime(cursor.getString(cursor.getColumnIndex(Common.ENDDATETIME)));
                transferModel.setFromLocationID(cursor.getInt(cursor.getColumnIndex(Common.FROMLOCATIONID)));
                transferModel.setTransportTypeId(cursor.getInt(cursor.getColumnIndex(Common.TRANSPORTTYPEID)));
                transferModel.setTransferAgencyID(cursor.getInt(cursor.getColumnIndex(Common.TRANSFERAGENCYID)));
                transferModel.setDriverID(cursor.getInt(cursor.getColumnIndex(Common.DRIVERID)));
                transferModel.setTruckPlateNumber(cursor.getString(cursor.getColumnIndex(Common.TRUCKPLATENUMBER)));
                transferModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                transferModel.setCount(cursor.getInt(cursor.getColumnIndex(Common.COUNT)));
                transferModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                transferModel.setTransUniqueID(cursor.getString(cursor.getColumnIndex(Common.TRANSFERUNIQUEID)));
                transferModel.setISActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                transferModel.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Common.SYNCSTATUS)));
                transferModel.setSyncTime(cursor.getString(cursor.getColumnIndex(Common.SYNCTIME)));
                transferModel.setLoadedTypeID(cursor.getInt(cursor.getColumnIndex(Common.LOADEDTYPE)));

                transferModel.setAgencyName(cursor.getString(cursor.getColumnIndex(Common.ExternalDataBaseClass.AGENCYNAME)));
                transferModel.setDriverName(cursor.getString(cursor.getColumnIndex(Common.ExternalDataBaseClass.DRIVERNAME)));
                transferModel.setTruckId(cursor.getInt(cursor.getColumnIndex(Common.TRUCKID)));
                //transferModel.setPurchaseID(cursor.getString(cursor.getColumnIndex(Common.Purchase.PurchaseId)));
                scannedReusltList.add(transferModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferIdList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public boolean UpdateInventoryTransferUniqueID(int TransferID, String transUniqueID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYTRANSFERIDLIST + " SET "
                    + Common.TRANSFERUNIQUEID + " = '" + transUniqueID + "'" +
                    " WHERE " + Common.TRANSFERID + " = " + TransferID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryTransferUniqueID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateInventoryTransferID(String endDateTime, int ToLocationID, int FromLocationid, int TransportTypeId, int AgencyID, int DriverID,
                                             int truckId, int UserID, int ScannedCount, int TransferID, String VoulumSum, int Isactive, String transUniqueID,
                                             int LoadedID, String agencyName, String driverName, String TrucklicensePlateNo) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYTRANSFERIDLIST + " SET "
                    + Common.ENDDATETIME + " = '" + endDateTime + "' , "
                    + Common.LOCATION_ID + " = '" + ToLocationID + "' , "
                    + Common.FROMLOCATIONID + " = '" + FromLocationid + "' , "
                    + Common.TRANSPORTTYPEID + " = '" + TransportTypeId + "' , "
                    + Common.TRANSFERAGENCYID + " = '" + AgencyID + "' , "
                    + Common.ExternalDataBaseClass.AGENCYNAME + " = '" + agencyName + "' , "
                    + Common.DRIVERID + " = '" + DriverID + "' , "
                    + Common.ExternalDataBaseClass.DRIVERNAME + " = '" + driverName + "' , "
                    + Common.TRUCKID + " = '" + truckId + "' , "
                    + Common.TRUCKPLATENUMBER + " = '" + TrucklicensePlateNo + "' , "
                    + Common.ISACTIVE + " = '" + Isactive + "' , "
                    + Common.TRANSFERUNIQUEID + " = '" + transUniqueID + "' , "
                    + Common.USERID + " = '" + UserID + "' , "
                    + Common.LOADEDTYPE + "= '" + LoadedID + "' , "
                    + "[" + Common.COUNT + "]" + "= '" + ScannedCount + "' , "
                    + Common.VOLUME + " = '" + VoulumSum + "'" +
                    " WHERE " + Common.TRANSFERID + " = " + TransferID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryTransferID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateOldValuesInventoryTransferID(int TransferID, String transUniqueID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYTRANSFERIDLIST + " SET "
                    + Common.TRANSFERUNIQUEID + " = '" + transUniqueID + "'" +
                    " WHERE " + Common.TRANSFERID + " = " + TransferID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryTransferID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateOldValuesITScannedID(int OldTransferID, int NewTransferID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYTRANSFERSCANNED + " SET "
                    + Common.TRANSFERID + " = '" + NewTransferID + "'" +
                    " WHERE " + Common.TRANSFERID + " = " + OldTransferID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryTransferID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateOldValuesICScannedID(int OldListID, int NewListID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_SCANNEDRESULT + " SET "
                    + Common.LISTID + " = '" + NewListID + "'" +
                    " WHERE " + Common.LISTID + " = " + OldListID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryTransferID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public String getLastFellingRegD() {
        String TransferID = "";
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(FellingRegID) as LastFellingRegID FROM " + Common.TBL_FELLINGREGISTRATIONLIST;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TransferID = (cursor.getString(cursor.getColumnIndex("LastFellingRegID")));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getLastFellingRegD", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TransferID;
    }

    public String getLastCountID() {
        String TransferID = "";
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(ListID) as LastCountID FROM " + Common.TBL_INVENTORYCOUNTLIST;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TransferID = (cursor.getString(cursor.getColumnIndex("LastCountID")));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getLastCountID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TransferID;
    }

    public String getLastTransferID() {
        String TransferID = "";
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(TransferID) as LastTansferID FROM " + Common.TBL_INVENTORYTRANSFERIDLIST;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TransferID = (cursor.getString(cursor.getColumnIndex("LastTansferID")));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getLastTransferID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TransferID;
    }


    public String getLastReceivedID() {
        String TransferID = "";
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(ReceivedID) as LastReceivedID FROM " + Common.TBL_INVENTORYRECEIVEDLIST;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TransferID = (cursor.getString(cursor.getColumnIndex("LastReceivedID")));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getLastReceivedID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TransferID;
    }
/*
    public boolean DeleteInventoryCountListZero(int ListIDs) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYCOUNTLIST + " WHERE " + Common.LISTID + ListIDs;
            //String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYCOUNTLIST + " WHERE " + "[" + Common.COUNT + "]" + " <= 0 ";
            //String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYCOUNTLIST + " WHERE " + Common.COUNT + " <= 0 ";
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteInventoryTransferListZeroCount(int TransID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYTRANSFERIDLIST + " WHERE " + Common.TRANSFERID + TransID;
            //String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYTRANSFERIDLIST + " WHERE " + "[" + Common.COUNT + "]" + " <= 0 ";
            //String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYTRANSFERIDLIST + " WHERE " + Common.COUNT + " <= 0 ";
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteInventoryTransferReceivedZero(int ReceID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYRECEIVEDLIST + " WHERE " + Common.RECEIVEDID + ReceID;
            //String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYRECEIVEDLIST + " WHERE " + "[" + Common.COUNT + "]" + " <= 0 ";
            //String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYRECEIVEDLIST + " WHERE " + Common.COUNT + " <= 0 ";
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }*/

    public boolean DeleteInventoryCountListID(int list_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYCOUNTLIST + " WHERE " + Common.LISTID + " = " + list_ID;
            //Delete from InventoryCountList where ListID=6 and TotalCount=0
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryCountListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteInventoryCountScanned(int list_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_SCANNEDRESULT + " WHERE " + Common.LISTID + " = " + list_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteInventoryTransferListID(int Transfer_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYTRANSFERIDLIST + " WHERE " + Common.TRANSFERID + " = " + Transfer_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteInventoryTransferScanned(int Transfer_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " WHERE " + Common.TRANSFERID + " = " + Transfer_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteInventoryReceivedListID(int received_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYRECEIVEDLIST + " WHERE " + Common.RECEIVEDID + " = " + received_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteInventoryReceivedScanned(int received_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYRECEIVED + " WHERE " + Common.RECEIVEDID + " = " + received_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteFellingRegistrationListID(int FellingReg_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_FELLINGREGISTRATIONLIST + " WHERE " + Common.FELLINGREGID + " = " + FellingReg_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteFellingRegistrationScanned(int FellingReg_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " WHERE " + Common.FELLINGREGID + " = " + FellingReg_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateInventoryReceivedUniqueID(int ReceivedID, String receUniqueID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYRECEIVEDLIST + " SET "
                    + Common.RECEIVEDUNIQUEID + " = '" + receUniqueID + "'" +
                    " WHERE " + Common.RECEIVEDID + " = " + ReceivedID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedUniqueID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateInventoryReceivedID(String endDateTime, int ToLocationID, int FromLocationid, int TransportTypeId, int AgencyID, int DriverID,
                                             int truckId, int UserID, int ScannedCount, int TransferID, String VoulumSum, int ReceivedID,
                                             int ISActive, String TransferUniqueID, String ReceivedUniquID, int ReceivedLoadedTypeID, String agencyName, String driverName, String TrucklicensePlateNo) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
           /* String strSQL = "UPDATE " + Common.TBL_INVENTORYRECEIVEDLIST + " SET "
                    + Common.ENDDATETIME + " = '" + endDateTime + "' , "
                    + Common.LOCATION_ID + " = '" + FromLocationid + "' , "
                    + Common.FROMLOCATIONID + " = '" + ToLocationID + "' , "
                    + Common.TRANSPORTTYPEID + " = '" + TransportTypeId + "' , "
                    + Common.TRANSFERAGENCYID + " = '" + AgencyID + "' , "
                    + Common.ExternalDataBaseClass.AGENCYNAME + " = '" + agencyName + "' , "
                    + Common.DRIVERID + " = '" + DriverID + "' , "
                    + Common.ExternalDataBaseClass.DRIVERNAME + " = '" + driverName + "' , "
                    + Common.TRUCKID + " = '" + truckId + "' , "
                    + Common.ISACTIVE + " = '" + ISActive + "' , "
                    + Common.LOADEDTYPE + " = '" + ReceivedLoadedTypeID + "' , "
                    + Common.TRANSFERUNIQUEID + " = '" + TransferUniqueID + "' , "
                    + Common.RECEIVEDUNIQUEID + " = '" + ReceivedUniquID + "' , "
                    + Common.TRUCKPLATENUMBER + " = '" + TrucklicensePlateNo + "' , "
                    + Common.USERID + " = '" + UserID + "' , "
                    + "[" + Common.COUNT + "]" + "= '" + ScannedCount + "' , "
                    + Common.VOLUME + " = '" + VoulumSum + "'" +
                    " WHERE " + Common.RECEIVEDID + " = " + ReceivedID;*/
            String strSQL = "UPDATE " + Common.TBL_INVENTORYRECEIVEDLIST + " SET "
                    + Common.ENDDATETIME + " = '" + endDateTime + "' , "
                    + Common.LOCATION_ID + " = '" + ToLocationID + "' , "
                    + Common.FROMLOCATIONID + " = '" + FromLocationid + "' , "
                    + Common.TRANSPORTTYPEID + " = '" + TransportTypeId + "' , "
                    + Common.TRANSFERAGENCYID + " = '" + AgencyID + "' , "
                    + Common.ExternalDataBaseClass.AGENCYNAME + " = '" + agencyName + "' , "
                    + Common.DRIVERID + " = '" + DriverID + "' , "
                    + Common.ExternalDataBaseClass.DRIVERNAME + " = '" + driverName + "' , "
                    + Common.TRUCKID + " = '" + truckId + "' , "
                    + Common.ISACTIVE + " = '" + ISActive + "' , "
                    + Common.LOADEDTYPE + " = '" + ReceivedLoadedTypeID + "' , "
                    + Common.TRANSFERUNIQUEID + " = '" + TransferUniqueID + "' , "
                    + Common.RECEIVEDUNIQUEID + " = '" + ReceivedUniquID + "' , "
                    + Common.TRUCKPLATENUMBER + " = '" + TrucklicensePlateNo + "' , "
                    + Common.USERID + " = '" + UserID + "' , "
                    + "[" + Common.COUNT + "]" + "= '" + ScannedCount + "' , "
                    + Common.VOLUME + " = '" + VoulumSum + "'" +
                    " WHERE " + Common.RECEIVEDID + " = " + ReceivedID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

   /* public void insertInventoryTransferResult(String VBB_Number, int TransferID, String FromLocationname, String ToLocationName,
                                              String SbbLabel, String BarCode, String Length, String Volume, int UserID, String DateTime,
                                              int WoodSpieceID, String WoodSpieceCode, int EntryMode, int IsActive, int IsSBBLabelCorrected, String OldSBBLabel,
                                              String FellingSecID, String TreeNumber, String Classfication) {
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.VBBNUMBER, VBB_Number);
            values.put(Common.TRANSFERID, TransferID);
            values.put(Common.FROMLOCATION, FromLocationname);
            values.put(Common.TOLOCATION, ToLocationName);
            values.put(SBBLabel, SbbLabel);
            values.put(Common.BARCODE, BarCode);
            values.put(Common.LENGTH, Length);
            values.put(Common.VOLUME, Volume);
            values.put(Common.USERID, UserID);
            values.put(Common.DATETIME, DateTime);
            values.put(Common.WoodSpiceID, WoodSpieceID);
            values.put(Common.WoodSPiceCode, WoodSpieceCode);
            values.put(Common.ENTRYMODE, EntryMode);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.ISSBLABELCORRECT, IsSBBLabelCorrected);
            values.put(Common.ORGSBBLABEL, OldSBBLabel);
            values.put(Common.TREENO, TreeNumber);
            values.put(Common.FELLING_SECTIONID, FellingSecID);
            values.put(Common.QULAITY, Classfication);
            mDatabase.insert(Common.TBL_INVENTORYTRANSFERSCANNED, null, values);
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
    }
*/

    public boolean insertInventoryTransferResultFlag(String VBB_Number, int TransferID, String FromLocationname, String ToLocationName,
                                                     String SbbLabel, String BarCode, String Length, String Volume, int UserID, String DateTime,
                                                     String WoodSpieceID, String WoodSpieceCode, int EntryMode, int IsActive, int IsSBBLabelCorrected,
                                                     String OldSBBLabel, String FellingSecID, String TreeNumner, String Classfication, String holeVol,
                                                     String GrossVol, String purchaseID) {
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.VBBNUMBER, VBB_Number);
            values.put(Common.TRANSFERID, TransferID);
            values.put(Common.FROMLOCATION, FromLocationname);
            values.put(Common.TOLOCATION, ToLocationName);
            values.put(Common.SBBLabel, SbbLabel);
            values.put(Common.BARCODE, BarCode);
            values.put(Common.LENGTH, Length);
            values.put(Common.VOLUME, Volume);
            values.put(Common.USERID, UserID);
            values.put(Common.DATETIME, DateTime);
            values.put(Common.WoodSpiceID, WoodSpieceID);
            values.put(Common.WoodSPiceCode, WoodSpieceCode);
            values.put(Common.ENTRYMODE, EntryMode);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.ISSBLABELCORRECT, IsSBBLabelCorrected);
            values.put(Common.ORGSBBLABEL, OldSBBLabel);
            values.put(Common.FELLING_SECTIONID, FellingSecID);
            values.put(Common.QULAITY, Classfication);
            //values.put(OLDCommon.QULAITY, OldClassfication);
            values.put(Common.TREENO, TreeNumner);
            values.put(Common.HOLEVOLUME, holeVol);
            values.put(Common.GROSSVOLUME, GrossVol);
            values.put(Common.Purchase.PurchaseId, purchaseID);
            mDatabase.insert(Common.TBL_INVENTORYTRANSFERSCANNED, null, values);
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertInventoryTransferResultFlag", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return true;
    }


    public boolean getInventoryTransferduplicateCheck(String vbb_number, int Trans_ID, String Barcode, String fromLocation, String SbbLabel) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where VBB_Number = '" + vbb_number + "'" + " and " + Common.TRANSFERID + "= '" + Trans_ID + "'" + " and " + SBBLabel + "= '" + SBBLabel + "'";
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.SBBLabel + "= '" + SbbLabel + "'" + " and FromLocation" + "= '" + fromLocation + "' and " + Common.ISACTIVE + "= 1";
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + " FromLocation" + "= '" + fromLocation + "' and " + Common.ISACTIVE + "= 1" + " and (" + Common.BARCODE + " IN (" + Barcode + ")" + " or " + Common.ORGSBBLABEL + "=" + SbbLabel + ")";
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + " FromLocation" + "= '" + fromLocation + "' and " + Common.ISACTIVE + "= 1" + " and (" + Common.BARCODE + "='" + Barcode + "'" + " or " + Common.ORGSBBLABEL + "='" + SbbLabel + "')";
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + " FromLocation" + "= '" + fromLocation + "' and " + Common.ISACTIVE + "= " + 1 + " and " + Common.BARCODE + " = '" + Barcode + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferduplicateCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getPurchaseLogsduplicateCheckForPurchaseID(int Purcahse_ID, String Barcode) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            // String selectQuery = "SELECT * FROM " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " where " + Common.Purchase.PurchaseId + "= " + Purcahse_ID + " and " + Common.ISACTIVE + "= 1" + " and " + Common.BARCODE + "='" + Barcode + "'";
            String selectQuery = "SELECT * FROM " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " where " + Common.ISACTIVE + "= 1" + " and " + Common.BARCODE + "='" + Barcode + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getPurchaseLogsduplicateCheckForPurchaseID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getInventoryTransferduplicateCheckForPurchaseID(int Purcahse_ID, String Barcode) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.Purchase.PurchaseId + "= " + Purcahse_ID + " and " + Common.ISACTIVE + "= 1" + " and " + Common.BARCODE + "='" + Barcode + "'";
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.ISACTIVE + "= 1" + " and " + Common.BARCODE + "='" + Barcode + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferduplicateCheckForPurchaseID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getInventoryReceivedduplicateCheckForPurchaseID(int Purcahse_ID, String Barcode) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.Purchase.PurchaseId + "= " + Purcahse_ID + " and " + Common.ISACTIVE + "= 1" + " and " + Common.BARCODE + "='" + Barcode + "'";
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.ISACTIVE + "= 1" + " and " + Common.BARCODE + "='" + Barcode + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryReceivedduplicateCheckForPurchaseID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean CheckWSCForPurchaseID(String WSCode, int purchaseID) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_PURCHASEAGREEMENT + " where " + Common.WoodSPiceCode + " IN ('" + WSCode + "')" + " and " + Common.Purchase.PurchaseId + "=" + purchaseID;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "CheckWSCForPurchaseID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getInventoryReceivedduplicateCheck(int Received_ID, String Barcode, String SbbLabel) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + " ReceivedID" + "= '" + Received_ID + "' and " + Common.ISACTIVE + "= 1" + " and (" + Common.BARCODE + "='" + Barcode + "'" + " or " + Common.ORGSBBLABEL + "='" + SbbLabel + "')";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryReceivedduplicateCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getInventoryReceivedTransferIDDuplication(String transUnique_ID) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.TRANSFERUNIQUEID + "= '" + transUnique_ID + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryReceivedTransferIDDuplication", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }


    public boolean UpdateInventoryTransferSyncStatusTransID(String SyncTime, int SyncStatus, String VbbNumber, int TransID, int agencyID, int driverID, int TruckID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYTRANSFERIDLIST + " SET "
                    + Common.SYNCTIME + " = '" + SyncTime + "' , ";
            if (agencyID != 0) {
                strSQL = strSQL + Common.TRANSFERAGENCYID + " = '" + agencyID + "' , ";
            }
            if (driverID != 0) {
                strSQL = strSQL + Common.DRIVERID + " = '" + driverID + "' , ";
            }
            if (TruckID != 0) {
                strSQL = strSQL + Common.TRUCKID + " = '" + TruckID + "' , ";
            }
            strSQL = strSQL + Common.SYNCSTATUS + " = '" + SyncStatus + "' WHERE "
                    + Common.VBBNUMBER + " = '" + VbbNumber + "' and "
                    + Common.TRANSFERID + " = " + TransID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryTransferSyncStatusTransID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateExportList(String SyncTime, int SyncStatus, String exportUniqueID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_EXPORTLIST + " SET "
                    + Common.SYNCTIME + " = '" + SyncTime + "' , "
                    + Common.SYNCSTATUS + " = '" + SyncStatus + "' WHERE "
                    + Common.ExportUniqueID + " = " + exportUniqueID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedSyncStatusReceivedID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateInventoryReceivedSyncStatusReceivedID(String SyncTime, int SyncStatus, int ReceivedID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYRECEIVEDLIST + " SET "
                    + Common.SYNCTIME + " = '" + SyncTime + "' , "
                    + Common.SYNCSTATUS + " = '" + SyncStatus + "' WHERE "
                    + Common.RECEIVEDID + " = " + ReceivedID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedSyncStatusReceivedID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateFellingRegSyncStatusFellingRegID(String SyncTime, int SyncStatus, int fellingRegID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_FELLINGREGISTRATIONLIST + " SET "
                    + Common.SYNCTIME + " = '" + SyncTime + "' , "
                    + Common.SYNCSTATUS + " = '" + SyncStatus + "' WHERE "
                    + Common.FELLINGREGID + " = " + fellingRegID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedSyncStatusReceivedID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateExportSavedStatus(String SyncTime, int SyncStatus, String OrderNo, int ExportID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_EXPORTLIST + " SET "
                    + Common.SYNCTIME + " = '" + SyncTime + "' , "
                    + Common.SYNCSTATUS + " = '" + SyncStatus + "' WHERE "
                    + Common.ORDERNO + " = '" + OrderNo + "' and "
                    + Common.EXPORTID + " = " + ExportID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryTransferSyncStatusTransID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }


    public ArrayList<InventoryTransferInputListModel> getTransferScannedResultInputWithVBBNo(String Vbb_Number, int TransID) {
        ArrayList<InventoryTransferInputListModel> InventoryResultList = new ArrayList<InventoryTransferInputListModel>();
        int ID = 1;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + " = " + TransID + " and " + Common.ISACTIVE + "=1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                inventoryTransferInputListModel = new InventoryTransferInputListModel();
                inventoryTransferInputListModel.setID(ID);
                inventoryTransferInputListModel.setSBBLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                inventoryTransferInputListModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                inventoryTransferInputListModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                inventoryTransferInputListModel.setToLocationId(Common.ToLocaTransID);
                inventoryTransferInputListModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                inventoryTransferInputListModel.setDatetime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));
                if (cursor.getString(cursor.getColumnIndex(Common.LENGTH)).equals("")) {
                    inventoryTransferInputListModel.setLength(0.00);
                } else {
                    inventoryTransferInputListModel.setLength(parseDouble(cursor.getString(cursor.getColumnIndex(Common.LENGTH))));
                }
                if (cursor.getString(cursor.getColumnIndex(Common.VOLUME)).equals("")) {
                    inventoryTransferInputListModel.setVolume(0.00);
                } else {
                    inventoryTransferInputListModel.setVolume(parseDouble(cursor.getString(cursor.getColumnIndex(Common.VOLUME))));
                }
                inventoryTransferInputListModel.setEntryModeID(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                inventoryTransferInputListModel.setIsSBBLabelCorrected(cursor.getInt(cursor.getColumnIndex(Common.ISSBLABELCORRECT)));
                inventoryTransferInputListModel.setOrgSBBLabel(cursor.getString(cursor.getColumnIndex(Common.ORGSBBLABEL)));
                inventoryTransferInputListModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.FELLING_SECTIONID)))) {
                    inventoryTransferInputListModel.setFellingSectionId(0);
                } else {
                    inventoryTransferInputListModel.setFellingSectionId(cursor.getInt(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                }

                if (isNullOrEmpty(String.valueOf(cursor.getInt(cursor.getColumnIndex(Common.TREENO))))) {
                    inventoryTransferInputListModel.setTreeNumber(0);
                } else {
                    inventoryTransferInputListModel.setTreeNumber(cursor.getInt(cursor.getColumnIndex(Common.TREENO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QULAITY)))) {
                    inventoryTransferInputListModel.setQuality("A");
                } else {
                    inventoryTransferInputListModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                }
               /* if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.Purchase.PurchaseId)))) {
                    inventoryTransferInputListModel.setPurchaseID("");
                } else {
                    inventoryTransferInputListModel.setPurchaseID(cursor.getString(cursor.getColumnIndex(Common.Purchase.PurchaseId)));
                }*/
                InventoryResultList.add(inventoryTransferInputListModel);
                ID = ID + 1;

            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getTransferScannedResultInputWithVBBNo", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return InventoryResultList;
    }

    public boolean getTransferScannedPurchaseStatus(String Vbb_Number, int TransID) {
        boolean status = false;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT count(PurchaseId) as PurchaseStatus FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + " = " + TransID + " and " + Common.ISACTIVE + "=1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                int statusFlag = (cursor.getInt(cursor.getColumnIndex("PurchaseStatus")));
                if (statusFlag > 0) {
                    status = true;
                }
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getTransferScannedResultInputWithVBBNo", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return status;
    }

    public ArrayList<InventoryTransferInputListModel> getTransferScannedResultInputWithPurchaseID(int purchaseID, String transferID) {
        ArrayList<InventoryTransferInputListModel> InventoryResultList = new ArrayList<InventoryTransferInputListModel>();
        int ID = 1;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.Purchase.PurchaseId + " = " + purchaseID + " and " + Common.ISACTIVE + "=1" + " and " + Common.TRANSFERID + "='" + transferID + "'";

            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                inventoryTransferInputListModel = new InventoryTransferInputListModel();
                inventoryTransferInputListModel.setID(ID);
                inventoryTransferInputListModel.setSBBLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                inventoryTransferInputListModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)))) {
                    //if (cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)).equals("")) {
                    inventoryTransferInputListModel.setWoodSpieceCode(" ");
                } else {
                    inventoryTransferInputListModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                }
                inventoryTransferInputListModel.setToLocationId(Common.ToLocaTransID);
                inventoryTransferInputListModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                inventoryTransferInputListModel.setDatetime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));
                if (cursor.getString(cursor.getColumnIndex(Common.LENGTH)).equals("")) {
                    inventoryTransferInputListModel.setLength(0.00);
                } else {
                    inventoryTransferInputListModel.setLength(parseDouble(cursor.getString(cursor.getColumnIndex(Common.LENGTH))));
                }
                if (cursor.getString(cursor.getColumnIndex(Common.VOLUME)).equals("")) {
                    inventoryTransferInputListModel.setVolume(0.00);
                } else {
                    inventoryTransferInputListModel.setVolume(parseDouble(cursor.getString(cursor.getColumnIndex(Common.VOLUME))));
                }
                inventoryTransferInputListModel.setEntryModeID(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                inventoryTransferInputListModel.setIsSBBLabelCorrected(cursor.getInt(cursor.getColumnIndex(Common.ISSBLABELCORRECT)));
                inventoryTransferInputListModel.setOrgSBBLabel(cursor.getString(cursor.getColumnIndex(Common.ORGSBBLABEL)));
                inventoryTransferInputListModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.FELLING_SECTIONID)))) {
                    inventoryTransferInputListModel.setFellingSectionId(0);
                } else {
                    inventoryTransferInputListModel.setFellingSectionId(cursor.getInt(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                }

                if (isNullOrEmpty(String.valueOf(cursor.getInt(cursor.getColumnIndex(Common.TREENO))))) {
                    inventoryTransferInputListModel.setTreeNumber(0);
                } else {
                    inventoryTransferInputListModel.setTreeNumber(cursor.getInt(cursor.getColumnIndex(Common.TREENO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QULAITY)))) {
                    inventoryTransferInputListModel.setQuality("A");
                } else {
                    inventoryTransferInputListModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                }
                InventoryResultList.add(inventoryTransferInputListModel);
                ID = ID + 1;

            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getTransferScannedResultInputWithVBBNo", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return InventoryResultList;
    }

    public ArrayList<InventoryReceivedInputModel> getReceivedScannedResultInputWithVBBNo(String Vbb_Number, int receivedID) {
        ArrayList<InventoryReceivedInputModel> InventoryResultList = new ArrayList<InventoryReceivedInputModel>();
        int ID = 1;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.RECEIVEDID + " = " + receivedID + " and " + Common.ISACTIVE + "=1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                inventoryReceivedInputListModel = new InventoryReceivedInputModel();
                inventoryReceivedInputListModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                inventoryReceivedInputListModel.setDatetime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));
                inventoryReceivedInputListModel.setEntryModeID(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.FELLING_SECTIONID)))) {
                    inventoryReceivedInputListModel.setFellingSectionId(0);
                } else {
                    inventoryReceivedInputListModel.setFellingSectionId(cursor.getInt(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                }
                inventoryReceivedInputListModel.setFromLocationID(Common.FromLocationID);
                inventoryReceivedInputListModel.setID(ID);
                inventoryReceivedInputListModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                inventoryReceivedInputListModel.setIsSBBLabelCorrected(cursor.getInt(cursor.getColumnIndex(Common.ISSBLABELCORRECT)));
                if ((cursor.getString(cursor.getColumnIndex(Common.ISCHECKED)).equals("YES"))) {
                    inventoryReceivedInputListModel.setIsReceived(booleanToInt(true));
                } else {
                    inventoryReceivedInputListModel.setIsReceived(booleanToInt(false));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LENGTH)))) {
                    inventoryReceivedInputListModel.setLength(0.00);
                } else {
                    inventoryReceivedInputListModel.setLength(parseDouble(cursor.getString(cursor.getColumnIndex(Common.LENGTH))));
                }
                inventoryReceivedInputListModel.setOrgSBBLabel(cursor.getString(cursor.getColumnIndex(Common.ORGSBBLABEL)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QULAITY)))) {
                    inventoryReceivedInputListModel.setQuality("");
                } else {
                    inventoryReceivedInputListModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                }
                inventoryReceivedInputListModel.setReceivedID(cursor.getInt(cursor.getColumnIndex(Common.RECEIVEDID)));
                inventoryReceivedInputListModel.setSBBLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                inventoryReceivedInputListModel.setToLocationId(Common.ToLocReceivedID);
                inventoryReceivedInputListModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.VOLUME)))) {
                    inventoryReceivedInputListModel.setVolume(0.00);
                } else {
                    inventoryReceivedInputListModel.setVolume(parseDouble(cursor.getString(cursor.getColumnIndex(Common.VOLUME))));
                }
                inventoryReceivedInputListModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                inventoryReceivedInputListModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.TRANSFERUNIQUEID)))) {
                    inventoryReceivedInputListModel.setTransferUniqueID(" ");
                } else {
                    inventoryReceivedInputListModel.setTransferUniqueID(cursor.getString(cursor.getColumnIndex(Common.TRANSFERUNIQUEID)));
                }
              /*  if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.Purchase.PurchaseId)))) {
                    inventoryReceivedInputListModel.setPurchaseID("");
                } else {
                    inventoryReceivedInputListModel.setPurchaseID(cursor.getString(cursor.getColumnIndex(Common.Purchase.PurchaseId)));
                }*/
                InventoryResultList.add(inventoryReceivedInputListModel);
                ID = ID + 1;
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getReceivedScannedResultInputWithVBBNo", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return InventoryResultList;
    }

    public boolean getReceivedScannedPurchaseStatus(String Vbb_Number, int receivedID) {
        boolean status = false;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT count(PurchaseId) as PurchaseStatus FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.RECEIVEDID + " = " + receivedID + " and " + Common.ISACTIVE + "=1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                int statusFlag = (cursor.getInt(cursor.getColumnIndex("PurchaseStatus")));
                if (statusFlag > 0) {
                    status = true;
                }
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getTransferScannedResultInputWithVBBNo", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return status;
    }

    public ArrayList<FellingRegisterInputListModel> getFellingRegInputWithFellingUniqID(int FellingRegID) {
        ArrayList<FellingRegisterInputListModel> InventoryResultList = new ArrayList<FellingRegisterInputListModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " where " + Common.FELLINGREGID + " = " + FellingRegID + " and " + Common.ISACTIVE + "=1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingRegInpuModel = new FellingRegisterInputListModel();

                fellingRegInpuModel.setFellingRegID(cursor.getInt(cursor.getColumnIndex(Common.FELLINGREGID)));
                fellingRegInpuModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)))) {
                    fellingRegInpuModel.setSbbLabel("");
                } else {
                    fellingRegInpuModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                }
                fellingRegInpuModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)))) {
                    fellingRegInpuModel.setWoodSpieceCode("");
                } else {
                    fellingRegInpuModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.TREENO)))) {
                    fellingRegInpuModel.setTreeNumber("");
                } else {
                    fellingRegInpuModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                }
                fellingRegInpuModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.BARCODE)))) {
                    fellingRegInpuModel.setBarCode("");
                } else {
                    fellingRegInpuModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                }
                fellingRegInpuModel.setEntryMode(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                fellingRegInpuModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISNEWTREENO)))) {
                    fellingRegInpuModel.setIsNewTree(0);
                } else {
                    fellingRegInpuModel.setIsNewTree(cursor.getInt(cursor.getColumnIndex(Common.ISNEWTREENO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISOLDWOODSPECODE)))) {
                    fellingRegInpuModel.setOldWoodSpieceCode("");
                } else {
                    fellingRegInpuModel.setOldWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.ISOLDWOODSPECODE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISWOODSPECODE)))) {
                    fellingRegInpuModel.setIsWoodSpieceCode(0);
                } else {
                    fellingRegInpuModel.setIsWoodSpieceCode(cursor.getInt(cursor.getColumnIndex(Common.ISWOODSPECODE)));
                }

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF1)))) {
                    fellingRegInpuModel.setFooter_1("0");
                } else {
                    fellingRegInpuModel.setFooter_1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF2)))) {
                    fellingRegInpuModel.setFooter_2("0");
                } else {
                    fellingRegInpuModel.setFooter_2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT1)))) {
                    fellingRegInpuModel.setTop_1("0");
                } else {
                    fellingRegInpuModel.setTop_1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT2)))) {
                    fellingRegInpuModel.setTop_2("0");
                } else {
                    fellingRegInpuModel.setTop_2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LENGTH)))) {
                    fellingRegInpuModel.setLength("0");
                } else {
                    fellingRegInpuModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                }

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.NOTEF)))) {
                    fellingRegInpuModel.setNoteF("0");
                } else {
                    fellingRegInpuModel.setNoteF(cursor.getString(cursor.getColumnIndex(Common.NOTEF)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.NOTET)))) {
                    fellingRegInpuModel.setNoteT("0");
                } else {
                    fellingRegInpuModel.setNoteT(cursor.getString(cursor.getColumnIndex(Common.NOTET)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.NOTEL)))) {
                    fellingRegInpuModel.setNoteL("0");
                } else {
                    fellingRegInpuModel.setNoteL(cursor.getString(cursor.getColumnIndex(Common.NOTEL)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.TREEPARTTYPE)))) {
                    fellingRegInpuModel.setTreePartType("");
                } else {
                    fellingRegInpuModel.setTreePartType(cursor.getString(cursor.getColumnIndex(Common.TREEPARTTYPE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.VOLUME)))) {
                    fellingRegInpuModel.setVolume(0.00);
                } else {
                    fellingRegInpuModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LHF1)))) {
                    fellingRegInpuModel.setLHF1("0");
                } else {
                    fellingRegInpuModel.setLHF1(cursor.getString(cursor.getColumnIndex(Common.LHF1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LHF2)))) {
                    fellingRegInpuModel.setLHF2("0");
                } else {
                    fellingRegInpuModel.setLHF2(cursor.getString(cursor.getColumnIndex(Common.LHF2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LHT1)))) {
                    fellingRegInpuModel.setLHT1("0");
                } else {
                    fellingRegInpuModel.setLHT1(cursor.getString(cursor.getColumnIndex(Common.LHT1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LHT2)))) {
                    fellingRegInpuModel.setLHT2("0");
                } else {
                    fellingRegInpuModel.setLHT2(cursor.getString(cursor.getColumnIndex(Common.LHT2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LHVOLUME)))) {
                    fellingRegInpuModel.setLHVolume(0.00);
                } else {
                    fellingRegInpuModel.setLHVolume(cursor.getDouble(cursor.getColumnIndex(Common.LHVOLUME)));
                }
                InventoryResultList.add(fellingRegInpuModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getReceivedScannedResultInputWithVBBNo", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return InventoryResultList;
    }

    public String GetPlotNoUsingFellingSecID(int LocaID, String FellingSecID, String FellingUniqueID) {
        String PlotNumber = "";
        StringBuilder PlotNumberBuilder = new StringBuilder();

        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT PlotNo as FirstPlotNo FROM " + Common.TBL_FELLINGTREEDETAILS + " where " + Common.LOCATIONID + " = " + LocaID
                    + " and " + Common.FELLING_SECTIONID + " = " + FellingSecID
                    + " and " + Common.TREENO + " in (select TreeNumber from FellingRegistrationDetails where FellingRegistrationUniqueID = '" + FellingUniqueID + "')";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                PlotNumber = (cursor.getString(cursor.getColumnIndex("FirstPlotNo")));
                PlotNumberBuilder.append(PlotNumber + " , ");
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "GetPlotNoUsingFellingSecID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return PlotNumberBuilder.toString();
    }

    public String GetFellingSecNumbersFromINTransfer(int Trans_ID) {
        String FellingNumber = "";
        StringBuilder FellingNumberBuilder = new StringBuilder();

        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT FellingSectionId as FellingNos FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + "=" + Trans_ID + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                FellingNumber = (cursor.getString(cursor.getColumnIndex("FellingNos")));
                FellingNumberBuilder.append(FellingNumber + " , ");
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "GetFellingSecNumbersFromINTransfer", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return FellingNumberBuilder.toString();
    }

    public String GetFellingSecNumbersFromINReceived(int Receiv_ID) {
        String FellingNumber = "";
        StringBuilder FellingNumberBuilder = new StringBuilder();

        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT FellingSectionId as FellingNos FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.RECEIVEDID + "=" + Receiv_ID + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                FellingNumber = (cursor.getString(cursor.getColumnIndex("FellingNos")));
                FellingNumberBuilder.append(FellingNumber + " , ");
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "GetFellingSecNumbersFromINReceived", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return FellingNumberBuilder.toString();
    }

    public ArrayList<FellingTreeDetailsModel> getFellingRegInputWithTreeDetails(int LocaID, String FellingSecID, String FellingUniqueID) {
        ArrayList<FellingTreeDetailsModel> TreeDetailsList = new ArrayList<FellingTreeDetailsModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGTREEDETAILS + " where " + Common.LOCATIONID + " = " + LocaID + " and " + Common.FELLING_SECTIONID + " = " + FellingSecID;
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGTREEDETAILS + " where " + Common.LOCATIONID + " = " + LocaID
                    + " and " + Common.FELLING_SECTIONID + " = " + FellingSecID
                    + " and " + Common.TREENO + " in (select TreeNumber from FellingRegistrationDetails where FellingRegistrationUniqueID = '" + FellingUniqueID + "')";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingTreeDetModel = new FellingTreeDetailsModel();

                fellingTreeDetModel.setFellingRegID(cursor.getInt(cursor.getColumnIndex(Common.FELLINGREGID)));
                fellingTreeDetModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                fellingTreeDetModel.setFellingRegUniqueID(cursor.getString(cursor.getColumnIndex(Common.FELLIINGREGUNIQUEID)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.TREENO)))) {
                    fellingTreeDetModel.setTreeNumber("");
                } else {
                    fellingTreeDetModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF1)))) {
                    fellingTreeDetModel.setFooter_1("0");
                } else {
                    fellingTreeDetModel.setFooter_1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF2)))) {
                    fellingTreeDetModel.setFooter_2("0");
                } else {
                    fellingTreeDetModel.setFooter_2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT1)))) {
                    fellingTreeDetModel.setTop_1("0");
                } else {
                    fellingTreeDetModel.setTop_1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT2)))) {
                    fellingTreeDetModel.setTop_2("0");
                } else {
                    fellingTreeDetModel.setTop_2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LENGTH)))) {
                    fellingTreeDetModel.setLength("0");
                } else {
                    fellingTreeDetModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                }

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.PLOTID)))) {
                    fellingTreeDetModel.setPlotId(0);
                } else {
                    fellingTreeDetModel.setPlotId(cursor.getInt(cursor.getColumnIndex(Common.PLOTID)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.WoodSpiceID)))) {
                    fellingTreeDetModel.setWoodSpieceID(0);
                } else {
                    fellingTreeDetModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISWOODSPECODE)))) {
                    fellingTreeDetModel.setIsWoodSpieceCode(0);
                } else {
                    fellingTreeDetModel.setIsWoodSpieceCode(cursor.getInt(cursor.getColumnIndex(Common.ISWOODSPECODE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISNEWPLOTNO)))) {
                    fellingTreeDetModel.setIsNewPlotNumber(0);
                } else {
                    fellingTreeDetModel.setIsNewPlotNumber(cursor.getInt(cursor.getColumnIndex(Common.ISNEWPLOTNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISNEWTREENO)))) {
                    fellingTreeDetModel.setIsNewTreeNumber("0");
                } else {
                    fellingTreeDetModel.setIsNewTreeNumber(cursor.getString(cursor.getColumnIndex(Common.ISNEWTREENO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.PLOTNO)))) {
                    fellingTreeDetModel.setPlotNumber("");
                } else {
                    fellingTreeDetModel.setPlotNumber(cursor.getString(cursor.getColumnIndex(Common.PLOTNO)));
                }
                TreeDetailsList.add(fellingTreeDetModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getFellingRegInputWithTreeDetails", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TreeDetailsList;
    }

    public ArrayList<FellingTreeDetailsModel> getFellingRegWithTreeDetails(String TreeNumber) {
        ArrayList<FellingTreeDetailsModel> TreeDetailsList = new ArrayList<FellingTreeDetailsModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGTREEDETAILS + " where " + Common.TREENO + " = '" + TreeNumber + "' ";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingTreeDetModel = new FellingTreeDetailsModel();

                fellingTreeDetModel.setFellingRegID(cursor.getInt(cursor.getColumnIndex(Common.FELLINGREGID)));
                fellingTreeDetModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                fellingTreeDetModel.setFellingRegUniqueID(cursor.getString(cursor.getColumnIndex(Common.FELLIINGREGUNIQUEID)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.TREENO)))) {
                    fellingTreeDetModel.setTreeNumber("");
                } else {
                    fellingTreeDetModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF1)))) {
                    fellingTreeDetModel.setFooter_1("");
                } else {
                    fellingTreeDetModel.setFooter_1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF2)))) {
                    fellingTreeDetModel.setFooter_2("");
                } else {
                    fellingTreeDetModel.setFooter_2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT1)))) {
                    fellingTreeDetModel.setTop_1("");
                } else {
                    fellingTreeDetModel.setTop_1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT2)))) {
                    fellingTreeDetModel.setTop_2("");
                } else {
                    fellingTreeDetModel.setTop_2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LENGTH)))) {
                    fellingTreeDetModel.setLength("");
                } else {
                    fellingTreeDetModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)))) {
                    fellingTreeDetModel.setWoodSpieceCode("");
                } else {
                    fellingTreeDetModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.PLOTNO)))) {
                    fellingTreeDetModel.setPlotNumber("");
                } else {
                    fellingTreeDetModel.setPlotNumber(cursor.getString(cursor.getColumnIndex(Common.PLOTNO)));
                }
                TreeDetailsList.add(fellingTreeDetModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getFellingRegInputWithTreeDetails", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TreeDetailsList;
    }

    public boolean RemoveFromScanneList(String Sbb_Label, int IsActiveValue, int ListID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_SCANNEDRESULT + " SET " + Common.ISACTIVE + " = '" + IsActiveValue + "'where " + Common.BARCODE + "='" + Sbb_Label + "' and " + Common.LISTID + "=" + ListID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "RemoveFromScanneList", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean RemoveFromtransferlistview(String Sbb_Label, int IsActiveValue, int TrnsFerID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYTRANSFERSCANNED + " SET " + Common.ISACTIVE + " = '" + IsActiveValue + "' where " + Common.BARCODE + "='" + Sbb_Label + "' and " + Common.TRANSFERID + "=" + TrnsFerID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "RemoveFromtransferlistview", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean RemovePurchasetransferlistview(String Sbb_Label, int IsActiveValue, int purchaseID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYTRANSFERSCANNED + " SET " + Common.ISACTIVE + " = '" + IsActiveValue + "' where " + Common.BARCODE + "='" + Sbb_Label + "' and " + Common.Purchase.PurchaseId + "=" + purchaseID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "RemovePurchasetransferlistview", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean RemoveFromReceivedlistview(String Sbb_Label, int IsActiveValue, int receivedID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYRECEIVED + " SET " + Common.ISACTIVE + " = '" + IsActiveValue + "' where " + Common.BARCODE + "='" + Sbb_Label + "' and " + Common.RECEIVEDID + "=" + receivedID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "RemoveFromReceivedlistview", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean RemovePurchaseReceivedlistview(String Sbb_Label, int IsActiveValue, int purchaseID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYRECEIVED + " SET " + Common.ISACTIVE + " = '" + IsActiveValue + "' where " + Common.BARCODE + "='" + Sbb_Label + "' and " + Common.Purchase.PurchaseId + "=" + purchaseID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "RemovePurchaseReceivedlistview", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean RemoveFromExportDetailsList(String Sbb_Label, int IsActiveValue, int ListID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_EXPORTDETAILS + " SET " + Common.ISACTIVE + " = '" + IsActiveValue + "'where " + Common.SBBLabel + "=" + Sbb_Label + " and " + Common.EXPORTID + "=" + ListID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "RemoveFromScanneList", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

  /*  public boolean RemoveInventoryCountListID(String endDateTime, int ScannedCount, int CountID, String VolumeSum) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYCOUNTLIST + " SET "
                    + Common.ENDDATETIME + " = '" + endDateTime + "' ,"
                    + Common.VOLUME + " = '" + VolumeSum + "' , " +
                    " Count = Count -1" +
                    " WHERE " + Common.LISTID + " = " + CountID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }*/

    public boolean RemoveInventoryTransferID(String endDateTime, int ToLocationID, int FromLocationID, int TransportTypeId, int AgencyID, int DriverID,
                                             String TrucklicensePlateNo, int UserID, int ScannedCount, int TransferID, String VolumeSum) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYTRANSFERIDLIST + " SET "
                    + Common.ENDDATETIME + " = '" + endDateTime + "' , "
                    + Common.LOCATION_ID + " = '" + ToLocationID + "' , "
                    + Common.FROMLOCATIONID + " = '" + FromLocationID + "' , "
                    + Common.TRANSPORTTYPEID + " = '" + TransportTypeId + "' , "
                    + Common.TRANSFERAGENCYID + " = '" + AgencyID + "' , "
                    + Common.DRIVERID + " = '" + DriverID + "' , "
                    + Common.TRUCKPLATENUMBER + " = '" + TrucklicensePlateNo + "' , "
                    + Common.VOLUME + " = '" + VolumeSum + "' , "
                    + Common.USERID + " = '" + UserID + "' , " +
                    "Count = Count -1" +
                    " WHERE " + Common.TRANSFERID + " = " + TransferID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "RemoveInventoryTransferID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean getAdvanceSearchCountListCheck(int List_IDs, String Barcodes, int size, boolean Flag, String OrgSbbLabel) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "";
            if (Flag == true) {
                selectQuery = "SELECT * FROM " + Common.TBL_SCANNEDRESULT + " where " + Common.LISTID + "=" + List_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.BARCODE + " IN (" + Barcodes + ")" + " or " + Common.ORGSBBLABEL + " IN (" + OrgSbbLabel + "))";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_SCANNEDRESULT + " where " + Common.LISTID + "=" + List_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.BARCODE + "='" + Barcodes + "'" + " or " + Common.ORGSBBLABEL + "='" + OrgSbbLabel + "')";
            }

            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                if (Flag == true) {
                    Result = size == cursor.getCount();
                } else {
                    Result = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getAdvanceSearchCountListCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getAdvanceSearchTransferListCheck(int transfer_IDs, String Barcodes, int size, boolean Flag, String SbbLabels) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "";
            if (Flag == true) {
                selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + "=" + transfer_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.BARCODE + " IN (" + SbbLabels + ")" + " or " + Common.ORGSBBLABEL + " IN (" + SbbLabels + "))";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + "=" + transfer_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.BARCODE + " ='" + SbbLabels + "'" + " or " + Common.ORGSBBLABEL + "='" + SbbLabels + "')";
            }
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                if (Flag == true) {
                    Result = size == cursor.getCount();
                } else {
                    Result = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getAdvanceSearchTransferListCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getAdvanceSearchReceivedListCheck(int received_IDs, String Barcodes, int size, boolean Flag, String SbbLabels) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "";
            if (Flag == true) {
                selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.RECEIVEDID + "=" + received_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.BARCODE + " IN (" + Barcodes + ")" + " or " + Common.ORGSBBLABEL + " IN (" + SbbLabels + "))";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.RECEIVEDID + "=" + received_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.BARCODE + "='" + Barcodes + "'" + " or " + Common.ORGSBBLABEL + "='" + SbbLabels + "')";
            }
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                if (Flag == true) {
                    Result = size == cursor.getCount();
                } else {
                    Result = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getAdvanceSearchReceivedListCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean insertLoginDetails(String username, String password, String IMEI) {
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.USER_NAME, username);
            values.put(Common.PASSWORD, password);
            values.put(Common.IMEINumber, IMEI);
            mDatabase.insert(Common.TBL_LOGINAUTHENTICATION, null, values);
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertLoginDetails", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return true;
    }

    public ArrayList<LoginAuthenticationModel> getLoginAuthentication(String UserName) {
        ArrayList<LoginAuthenticationModel> getLogindetails = new ArrayList<LoginAuthenticationModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            //+ " where " + Common.USER_NAME + "=" + Username + " and " + PASSWORD + " = " + Password
            String selectQuery = "SELECT * FROM " + Common.TBL_LOGINAUTHENTICATION + " Where " + Common.USER_NAME + " = '" + UserName + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                loginAuthenticationModel = new LoginAuthenticationModel();
                loginAuthenticationModel.setUsername(cursor.getString(cursor.getColumnIndex(Common.USER_NAME)));
                loginAuthenticationModel.setPassword(cursor.getString(cursor.getColumnIndex(Common.PASSWORD)));
                loginAuthenticationModel.setIMEI(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                getLogindetails.add(loginAuthenticationModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getLoginAuthentication", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return getLogindetails;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str == "null";
    }

    public boolean UpdateQualityScannedList(String Quality_Name, String Sbb_Label, int received_Id) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYRECEIVED + " SET " + Common.QULAITY + " = '" + Quality_Name + "'where " + Common.SBBLabel + "=" + Sbb_Label + " and " + Common.RECEIVEDID + "=" + received_Id;

            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateQualityScannedList", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateQualityCountList(String Quality_Name, String Sbb_Label, int list_id) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_SCANNEDRESULT + " SET " + Common.QULAITY + " = '" + Quality_Name + "'where " + Common.SBBLabel + "=" + Sbb_Label + " and " + Common.LISTID + "=" + list_id;

            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateQualityCountList", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateQualityTansferList(String Quality_Name, String Sbb_Label, int transfer_id) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYTRANSFERSCANNED + " SET " + Common.QULAITY + " = '" + Quality_Name + "'where " + Common.SBBLabel + "=" + Sbb_Label + " and " + Common.TRANSFERID + "=" + transfer_id;

            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateQualityTansferList", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    //-----FellingRegistrationFormMethods------//
    public boolean insertfellingformdetails(int FellingRegID, int LocationID, String FellingSecID, String SbbLabel, String WoodSpieceID, String WoodSpieceCode,
                                            String TreeNO, String Quality, String BarCode, String uniqueID, int UserId, int EntryMode, int IsActive,
                                            int IsNewTree, int IsWsCode, String OldWSCode, String Df1, String Df2, String Dt1, String Dt2, String SbbLabelLenght,
                                            String sbbLabelNoteF, String sbbLabelNoteT, String sbbLabelNoteL, String TreePartType, String Volume,
                                            String LHT1, String LHT2, String LHF1, String LHF2, String LHVOLUMN) {
        boolean Result = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.FELLINGREGID, FellingRegID);
            values.put(Common.LOCATIONID, LocationID);
            values.put(Common.FELLING_SECTIONID, FellingSecID);
            values.put(Common.SBBLabel, SbbLabel);
            values.put(Common.WoodSpiceID, WoodSpieceID);
            values.put(Common.WoodSPiceCode, WoodSpieceCode);
            values.put(Common.TREENO, TreeNO);
            values.put(Common.QULAITY, Quality);
            values.put(Common.BARCODE, BarCode);
            values.put(Common.FELLIINGREGUNIQUEID, uniqueID);
            values.put(Common.USERID, UserId);
            values.put(Common.ENTRYMODE, EntryMode);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.ISNEWTREENO, IsNewTree);
            values.put(Common.ISWOODSPECODE, IsWsCode);
            values.put(Common.ISOLDWOODSPECODE, OldWSCode);
            values.put(Common.DF1, Df1);
            values.put(Common.DF2, Df2);
            values.put(Common.DT1, Dt1);
            values.put(Common.DT2, Dt2);
            values.put(Common.LENGTH, SbbLabelLenght);
            values.put(Common.NOTEF, sbbLabelNoteF);
            values.put(Common.NOTET, sbbLabelNoteT);
            values.put(Common.NOTEL, sbbLabelNoteL);
            values.put(Common.TREEPARTTYPE, TreePartType);
            values.put(Common.VOLUME, Volume);
            values.put(Common.LHT1, LHT1);
            values.put(Common.LHT2, LHT2);
            values.put(Common.LHF1, LHF1);
            values.put(Common.LHF2, LHF2);
            values.put(Common.LHVOLUME, LHVOLUMN);
            mDatabase.insert(Common.TBL_FELLINGREGISTRATIONDETAILS, null, values);
            mDatabase.setTransactionSuccessful();
            Result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
            Result = false;

        }
        return Result;
    }

    public boolean insertFellingTreeDetails(int FellingRegID, String FellinUniquID, String FellingsectionID, String TreeNumber, String Df1, String Df2, String Dt1, String Dt2,
                                            String TreeLenght, int FromLoc, String WSpecCode, String OldWSpecCode, int IsnewTree, String PlotNo, String OldPlotNo,
                                            int PlotID, String WSC, int IsWSC, int IsNewPlotNo) {
        boolean Result = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.FELLINGREGID, FellingRegID);
            values.put(Common.LOCATIONID, FromLoc);
            values.put(Common.FELLIINGREGUNIQUEID, FellinUniquID);
            values.put(Common.FELLING_SECTIONID, FellingsectionID);
            values.put(Common.TREENO, TreeNumber);
            values.put(Common.DF1, Df1);
            values.put(Common.DF2, Df2);
            values.put(Common.DT1, Dt1);
            values.put(Common.DT2, Dt2);
            values.put(Common.LENGTH, TreeLenght);
            values.put(Common.WoodSPiceCode, WSpecCode);
            values.put(Common.ISNEWTREENO, IsnewTree);
            values.put(Common.PLOTNO, PlotNo);
            values.put(Common.OLDPLOTNO, OldPlotNo);
            values.put(Common.OLDWOODSPECODE, OldWSpecCode);
            values.put(Common.PLOTID, PlotID);
            values.put(Common.WoodSpiceID, WSC);
            values.put(Common.ISWOODSPECODE, IsWSC);
            values.put(Common.ISNEWPLOTNO, IsNewPlotNo);
         /*   values.put(Common.LHT1, LHT1);
            values.put(Common.LHT2, LHT2);
            values.put(Common.LHF1, LHF1);
            values.put(Common.LHF2, LHF2);
            values.put(Common.LHVOLUME, LHVOLUMN);*/
            mDatabase.insert(Common.TBL_FELLINGTREEDETAILS, null, values);
            mDatabase.setTransactionSuccessful();
            Result = true;
        } catch (Exception e) {
            e.printStackTrace();
            Result = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean updateFellingTreeDetails(String FellingsectionID, String TreeNumber, String Df1, String Df2, String Dt1, String Dt2,
                                            String TreeLenght, int FromLoc, String WSpecCode, String OldWSpecCode, int IsnewTree, String PlotNo, String OldPlotNo,
                                            int PlotID, String WSC, int IsWSC, int IsNewPlotNo) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_FELLINGTREEDETAILS + " SET "
                    + Common.DF1 + " = '" + Df1 + "' , "
                    + Common.DF2 + " = '" + Df2 + "' , "
                    + Common.DT1 + " = '" + Dt1 + "' , "
                    + Common.DT2 + " = '" + Dt2 + "' , "
                    + Common.WoodSPiceCode + " = '" + WSpecCode + "' , "
                    + Common.OLDWOODSPECODE + " = '" + OldWSpecCode + "' , "
                    + Common.PLOTNO + " = '" + PlotNo + "' , "
                    + Common.OLDPLOTNO + " = '" + OldPlotNo + "' , "
                    + Common.PLOTID + " = '" + PlotID + "' , "
                    + Common.WoodSpiceID + " = '" + WSC + "' , "
                    + Common.ISWOODSPECODE + " = '" + IsWSC + "' , "
                    + Common.ISNEWPLOTNO + " = '" + IsNewPlotNo + "' , "
                    + Common.LENGTH + " = '" + TreeLenght + "' "
                    /*+ Common.LHT1 + " = '" + LHT1 + "' , "
                    + Common.LHT2 + " = '" + LHT2 + "' , "
                    + Common.LHF1 + " = '" + LHF1 + "' , "
                    + Common.LHF2 + " = '" + LHF2 + "' , "
                    + Common.LHVOLUME + " = '" + LHVOLUMN + "' "*/ +
                    " WHERE " + Common.FELLING_SECTIONID + " = " + FellingsectionID + " and " + Common.TREENO + "=" + TreeNumber + " and " + Common.LOCATIONID + "=" + FromLoc;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateQualityTansferList", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean updateFellRegMasterDetails(String BarCode, String FellingRegUniqueID, String Df1, String Df2, String Dt1, String Dt2,
                                              String TreeLenght, String NoteF, String NoteT, String NoteL, String Volume, String TreePartType) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " SET "
                    + Common.DF1 + " = '" + Df1 + "' , "
                    + Common.DF2 + " = '" + Df2 + "' , "
                    + Common.DT1 + " = '" + Dt1 + "' , "
                    + Common.DT2 + " = '" + Dt2 + "' , "
                    + Common.LENGTH + " = '" + TreeLenght + "' , "
                    + Common.TREEPARTTYPE + " = '" + TreePartType + "' , "
                    + Common.NOTEF + " = '" + NoteF + "' , "
                    + Common.NOTET + " = '" + NoteT + "' , "
                    + Common.VOLUME + " = '" + Volume + "' , "
                    + Common.NOTEL + " = '" + NoteL + "'"
                    + " WHERE " + Common.FELLIINGREGUNIQUEID + " = '" + FellingRegUniqueID + "' and " + Common.BARCODE + "= '" + BarCode + "'";
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "updateFellRegMasterDetails", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean updateFellRegSbbLabel(String BarCode, String FellingRegUniqueID, String Df1, String Df2, String Dt1, String Dt2,
                                         String TreeLenght, String NoteF, String NoteT, String NoteL, String Volume, String TreePartType) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " SET "
                    + Common.DF1 + " = '" + Df1 + "' , "
                    + Common.DF2 + " = '" + Df2 + "' , "
                    + Common.DT1 + " = '" + Dt1 + "' , "
                    + Common.DT2 + " = '" + Dt2 + "' , "
                    + Common.LENGTH + " = '" + TreeLenght + "' , "
                    + Common.TREEPARTTYPE + " = '" + TreePartType + "' , "
                    + Common.NOTEF + " = '" + NoteF + "' , "
                    + Common.NOTET + " = '" + NoteT + "' , "
                    + Common.VOLUME + " = '" + Volume + "' , "
                    + Common.NOTEL + " = '" + NoteL + "'"
                    + " WHERE " + Common.FELLIINGREGUNIQUEID + " = '" + FellingRegUniqueID + "' and " + Common.BARCODE + "= '" + BarCode + "'";
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "updateFellRegSbbLabel", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    // 28-4-2020

    public boolean updateFellRegSbbLabel6_1_4(String BarCode, String FellingRegUniqueID, String Df1, String Df2, String Dt1, String Dt2,
                                              String TreeLenght, String NoteF, String NoteT, String NoteL, String Volume, String TreePartType, String LHT1, String LHT2, String LHF1, String LHF2, String LHVOLUMN) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " SET "
                    + Common.DF1 + " = '" + Df1 + "' , "
                    + Common.DF2 + " = '" + Df2 + "' , "
                    + Common.DT1 + " = '" + Dt1 + "' , "
                    + Common.DT2 + " = '" + Dt2 + "' , "
                    + Common.LENGTH + " = '" + TreeLenght + "' , "
                    + Common.TREEPARTTYPE + " = '" + TreePartType + "' , "
                    + Common.NOTEF + " = '" + NoteF + "' , "
                    + Common.NOTET + " = '" + NoteT + "' , "
                    + Common.VOLUME + " = '" + Volume + "' , "
                    + Common.NOTEL + " = '" + NoteL + "' , "
                    + Common.LHT1 + " = '" + LHT1 + "' , "
                    + Common.LHT2 + " = '" + LHT2 + "' , "
                    + Common.LHF1 + " = '" + LHF1 + "' , "
                    + Common.LHF2 + " = '" + LHF2 + "' , "
                    + Common.LHVOLUME + " = '" + LHVOLUMN + "' "
                    + " WHERE " + Common.FELLIINGREGUNIQUEID + " = '" + FellingRegUniqueID + "' and " + Common.BARCODE + "= '" + BarCode + "'";
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "updateFellRegSbbLabel", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }


    public ArrayList<FellingRegisterListModel> getFellingRegisterList() {
        ArrayList<FellingRegisterListModel> fellingRegisterList = new ArrayList<FellingRegisterListModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONLIST;
            //String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONLIST + " WHERE substr(StartDateTime,1,11)='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1";

            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            //if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                fellingRegisterListModel = new FellingRegisterListModel();
                fellingRegisterListModel.setFellingRegID(cursor.getInt(cursor.getColumnIndex(Common.FELLINGREGID)));
                fellingRegisterListModel.setFellingRegistrationNumber(cursor.getString(cursor.getColumnIndex(Common.FELLINGREGNO)));
                fellingRegisterListModel.setFellingRegistrationDate(cursor.getString(cursor.getColumnIndex(Common.FELLINGREGDATE)));
                fellingRegisterListModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                fellingRegisterListModel.setFellingSectionID(cursor.getString(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                fellingRegisterListModel.setEndDateTime(cursor.getString(cursor.getColumnIndex(Common.ENDDATETIME)));
                fellingRegisterListModel.setCount(cursor.getInt(cursor.getColumnIndex(Common.COUNT)));
                fellingRegisterListModel.setFellingRegistrationUniqueID(cursor.getString(cursor.getColumnIndex(Common.FELLIINGREGUNIQUEID)));
                fellingRegisterListModel.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Common.SYNCSTATUS)));
                fellingRegisterListModel.setSyncTime(cursor.getString(cursor.getColumnIndex(Common.SYNCTIME)));
                fellingRegisterListModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                fellingRegisterListModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                fellingRegisterListModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                fellingRegisterListModel.setIMEI(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                fellingRegisterList.add(fellingRegisterListModel);
            }
            //}
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return fellingRegisterList;
    }

    public boolean insertFellingRegisterList(String FellingReg_No, String FellingReg_Date, String FellingSection_id, int LocationID, String endDate, int count,
                                             String uniqueid, int syncStatus, String syncTime, int IsActive, int Userid, String Imei) {
        boolean Result = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.FELLINGREGNO, FellingReg_No);
            values.put(Common.FELLINGREGDATE, FellingReg_Date);
            values.put(Common.FELLING_SECTIONID, FellingSection_id);
            values.put(Common.LOCATIONID, LocationID);
            values.put(Common.ENDDATETIME, endDate);
            values.put(Common.COUNT, count);
            values.put(Common.FELLIINGREGUNIQUEID, uniqueid);
            values.put(Common.SYNCSTATUS, syncStatus);
            values.put(Common.SYNCTIME, syncTime);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.USERID, Userid);
            values.put(Common.IMEINumber, Imei);
            values.put(Common.VOLUME, 0);
            mDatabase.insert(Common.TBL_FELLINGREGISTRATIONLIST, null, values);
            mDatabase.setTransactionSuccessful();
            Result = true;
        } catch (Exception e) {
            e.printStackTrace();
            Result = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean UpdateQualityFellingformList(String Quality_Name, String Sbb_Label, int fellingReg_id) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " SET " + Common.QULAITY + " = '" + Quality_Name + "'where " + Common.SBBLabel + "=" + Sbb_Label + " and " + Common.FELLINGREGID + "=" + fellingReg_id;

            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean RemoveFromfellingform(String Barcode, int IsActiveValue, int FellingReg_id) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_FELLINGREGISTRATIONDETAILS + " SET " + Common.ISACTIVE + " = '" + IsActiveValue + "'where " + Common.BARCODE + "='" + Barcode + "' and " + Common.FELLINGREGID + "=" + FellingReg_id;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public ArrayList<FellingRegisterResultModel> getFellingRegistrationDetails() {
        ArrayList<FellingRegisterResultModel> scannedReusltList = new ArrayList<FellingRegisterResultModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingRegisterResultModel = new FellingRegisterResultModel();
                //fellingRegisterResultModel.setPlotNo(cursor.getString(cursor.getColumnIndex(Common.PLOTNO)));
                fellingRegisterResultModel.setFellingRegID(cursor.getInt(cursor.getColumnIndex(Common.FELLINGREGID)));
                fellingRegisterResultModel.setLocationID(cursor.getString(cursor.getColumnIndex(Common.LOCATIONID)));
                fellingRegisterResultModel.setFellingSectionID(cursor.getString(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                fellingRegisterResultModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                fellingRegisterResultModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                fellingRegisterResultModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                fellingRegisterResultModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                fellingRegisterResultModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                fellingRegisterResultModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                fellingRegisterResultModel.setFellingRegistrationUniqueID(cursor.getString(cursor.getColumnIndex(Common.FELLIINGREGUNIQUEID)));
                fellingRegisterResultModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                fellingRegisterResultModel.setEntryMode(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                fellingRegisterResultModel.setISNewTree(cursor.getInt(cursor.getColumnIndex(Common.ISNEWTREENO)));
                fellingRegisterResultModel.setIsWoodSpieceCode(cursor.getInt(cursor.getColumnIndex(Common.ISWOODSPECODE)));
                fellingRegisterResultModel.setIsOldWoodSpieceCode(cursor.getInt(cursor.getColumnIndex(Common.ISOLDWOODSPECODE)));
                fellingRegisterResultModel.setFooter_1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                fellingRegisterResultModel.setFooter_2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                fellingRegisterResultModel.setTop_1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                fellingRegisterResultModel.setTop_2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                fellingRegisterResultModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                fellingRegisterResultModel.setNoteF(cursor.getString(cursor.getColumnIndex(Common.NOTEF)));
                fellingRegisterResultModel.setNoteT(cursor.getString(cursor.getColumnIndex(Common.NOTET)));
                fellingRegisterResultModel.setNoteL(cursor.getString(cursor.getColumnIndex(Common.NOTEL)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.TREEPARTTYPE)))) {
                    fellingRegisterResultModel.setTreePartType("");
                } else {
                    fellingRegisterResultModel.setTreePartType(cursor.getString(cursor.getColumnIndex(Common.TREEPARTTYPE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISNEWTREENO)))) {
                    fellingRegisterResultModel.setISNewTree(0);
                } else {
                    fellingRegisterResultModel.setISNewTree(cursor.getInt(cursor.getColumnIndex(Common.ISNEWTREENO)));
                }
                fellingRegisterResultModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                fellingRegisterResultModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                //fellingRegisterResultModel.setFellingRegistrationNumber(cursor.getInt(cursor.getColumnIndex(Common.FELLINGREGNO)));
                scannedReusltList.add(fellingRegisterResultModel);
            }
            //}
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public ArrayList<FellingRegisterResultModel> getFellingformDetailsID(int FellingReg_ID, String FellingSecID) {
        ArrayList<FellingRegisterResultModel> scannedReusltList = new ArrayList<FellingRegisterResultModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " where " + Common.FELLINGREGID + "=" + FellingReg_ID + " and " + Common.ISACTIVE + " = " + 1;
            String selectQuery = "SELECT  FT.PlotNo as PlotNo, FRD.* FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " as FRD INNER JOIN "
                    + Common.TBL_FELLINGTREEDETAILS + " as FT on FRD.TreeNumber=FT.TreeNumber  WHERE FRD." + Common.FELLINGREGID + "=" + FellingReg_ID
                    + " and FRD." + Common.ISACTIVE + " = " + 1 + " and FT." + Common.FELLING_SECTIONID + "='" + FellingSecID + "'";

            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingRegisterResultModel = new FellingRegisterResultModel();
                fellingRegisterResultModel.setPlotNo(cursor.getString(cursor.getColumnIndex(Common.PLOTNO)));
                fellingRegisterResultModel.setFellingRegID(cursor.getInt(cursor.getColumnIndex(Common.FELLINGREGID)));
                fellingRegisterResultModel.setLocationID(cursor.getString(cursor.getColumnIndex(Common.LOCATIONID)));
                fellingRegisterResultModel.setFellingSectionID(cursor.getString(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                fellingRegisterResultModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                fellingRegisterResultModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                fellingRegisterResultModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                fellingRegisterResultModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                fellingRegisterResultModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                fellingRegisterResultModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                fellingRegisterResultModel.setFellingRegistrationUniqueID(cursor.getString(cursor.getColumnIndex(Common.FELLIINGREGUNIQUEID)));
                fellingRegisterResultModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                fellingRegisterResultModel.setEntryMode(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                fellingRegisterResultModel.setISNewTree(cursor.getInt(cursor.getColumnIndex(Common.ISNEWTREENO)));
                fellingRegisterResultModel.setIsWoodSpieceCode(cursor.getInt(cursor.getColumnIndex(Common.ISWOODSPECODE)));
                fellingRegisterResultModel.setIsOldWoodSpieceCode(cursor.getInt(cursor.getColumnIndex(Common.ISOLDWOODSPECODE)));
                fellingRegisterResultModel.setFooter_1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                fellingRegisterResultModel.setFooter_2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                fellingRegisterResultModel.setTop_1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                fellingRegisterResultModel.setTop_2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                fellingRegisterResultModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                fellingRegisterResultModel.setNoteF(cursor.getString(cursor.getColumnIndex(Common.NOTEF)));
                fellingRegisterResultModel.setNoteT(cursor.getString(cursor.getColumnIndex(Common.NOTET)));
                fellingRegisterResultModel.setNoteL(cursor.getString(cursor.getColumnIndex(Common.NOTEL)));
                fellingRegisterResultModel.setLHF1(cursor.getString(cursor.getColumnIndex(Common.LHF1)));
                fellingRegisterResultModel.setLHF2(cursor.getString(cursor.getColumnIndex(Common.LHF2)));
                fellingRegisterResultModel.setLHT1(cursor.getString(cursor.getColumnIndex(Common.LHT1)));
                fellingRegisterResultModel.setLHT2(cursor.getString(cursor.getColumnIndex(Common.LHT2)));
                fellingRegisterResultModel.setLHVolume(cursor.getString(cursor.getColumnIndex(Common.LHVOLUME)));

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.TREEPARTTYPE)))) {
                    fellingRegisterResultModel.setTreePartType("");
                } else {
                    fellingRegisterResultModel.setTreePartType(cursor.getString(cursor.getColumnIndex(Common.TREEPARTTYPE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISNEWTREENO)))) {
                    fellingRegisterResultModel.setISNewTree(0);
                } else {
                    fellingRegisterResultModel.setISNewTree(cursor.getInt(cursor.getColumnIndex(Common.ISNEWTREENO)));
                }
                fellingRegisterResultModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                fellingRegisterResultModel.setHeartVolume(cursor.getDouble(cursor.getColumnIndex(Common.LHVOLUME)));
                fellingRegisterResultModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                //fellingRegisterResultModel.setFellingRegistrationNumber(cursor.getInt(cursor.getColumnIndex(Common.FELLINGREGNO)));
                scannedReusltList.add(fellingRegisterResultModel);
            }
            //}
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public ArrayList<FellingRegisterResultModel> getFellingDetailsExportList(String FellingReg_IDStr) {
        ArrayList<FellingRegisterResultModel> scannedReusltList = new ArrayList<FellingRegisterResultModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT  * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " WHERE " + Common.FELLINGREGID + " IN (" + FellingReg_IDStr + ")";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingRegisterResultModel = new FellingRegisterResultModel();
                fellingRegisterResultModel.setFellingRegID(cursor.getInt(cursor.getColumnIndex(Common.FELLINGREGID)));
                fellingRegisterResultModel.setLocationID(cursor.getString(cursor.getColumnIndex(Common.LOCATIONID)));
                fellingRegisterResultModel.setFellingSectionID(cursor.getString(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                fellingRegisterResultModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                fellingRegisterResultModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                fellingRegisterResultModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                fellingRegisterResultModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                fellingRegisterResultModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                fellingRegisterResultModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                fellingRegisterResultModel.setFellingRegistrationUniqueID(cursor.getString(cursor.getColumnIndex(Common.FELLIINGREGUNIQUEID)));
                fellingRegisterResultModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                fellingRegisterResultModel.setEntryMode(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                fellingRegisterResultModel.setISNewTree(cursor.getInt(cursor.getColumnIndex(Common.ISNEWTREENO)));
                fellingRegisterResultModel.setIsWoodSpieceCode(cursor.getInt(cursor.getColumnIndex(Common.ISWOODSPECODE)));
                fellingRegisterResultModel.setIsOldWoodSpieceCode(cursor.getInt(cursor.getColumnIndex(Common.ISOLDWOODSPECODE)));
                fellingRegisterResultModel.setFooter_1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                fellingRegisterResultModel.setFooter_2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                fellingRegisterResultModel.setTop_1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                fellingRegisterResultModel.setTop_2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                fellingRegisterResultModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                fellingRegisterResultModel.setNoteF(cursor.getString(cursor.getColumnIndex(Common.NOTEF)));
                fellingRegisterResultModel.setNoteT(cursor.getString(cursor.getColumnIndex(Common.NOTET)));
                fellingRegisterResultModel.setNoteL(cursor.getString(cursor.getColumnIndex(Common.NOTEL)));

                fellingRegisterResultModel.setLHF1(cursor.getString(cursor.getColumnIndex(Common.LHF1)));
                fellingRegisterResultModel.setLHF2(cursor.getString(cursor.getColumnIndex(Common.LHF2)));
                fellingRegisterResultModel.setLHT1(cursor.getString(cursor.getColumnIndex(Common.LHT1)));
                fellingRegisterResultModel.setLHT2(cursor.getString(cursor.getColumnIndex(Common.LHT2)));
                fellingRegisterResultModel.setLHVolume(cursor.getString(cursor.getColumnIndex(Common.LHVOLUME)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.TREEPARTTYPE)))) {
                    fellingRegisterResultModel.setTreePartType("");
                } else {
                    fellingRegisterResultModel.setTreePartType(cursor.getString(cursor.getColumnIndex(Common.TREEPARTTYPE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISNEWTREENO)))) {
                    fellingRegisterResultModel.setISNewTree(0);
                } else {
                    fellingRegisterResultModel.setISNewTree(cursor.getInt(cursor.getColumnIndex(Common.ISNEWTREENO)));
                }
                fellingRegisterResultModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                fellingRegisterResultModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                //fellingRegisterResultModel.setFellingRegistrationNumber(cursor.getInt(cursor.getColumnIndex(Common.FELLINGREGNO)));
                scannedReusltList.add(fellingRegisterResultModel);
            }
            //}
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public boolean UpdateFellingUniqueID(int fellingID, String fellingUniqueID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_FELLINGREGISTRATIONLIST + " SET "
                    + Common.FELLIINGREGUNIQUEID + " = '" + fellingUniqueID + "'" +
                    " WHERE " + Common.FELLINGREGID + " = " + fellingID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedUniqueID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateFellingRegisterID(String endDateTime, int ToLocationID, String FellingSection_id, String FellingReg_No, int ScannedCount, int FellingReg_Id, String FellingUniqueID, double TotVolume) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_FELLINGREGISTRATIONLIST + " SET "
                    + Common.ENDDATETIME + " = '" + endDateTime + "' , "
                    + Common.LOCATIONID + " = '" + ToLocationID + "' , "
                    + Common.FELLINGREGNO + " = '" + FellingReg_No + "' , "
                    + Common.FELLIINGREGUNIQUEID + " = '" + FellingUniqueID + "' , "
                    + Common.FELLING_SECTIONID + " = '" + FellingSection_id + "' , "
                    + Common.VOLUME + " = '" + TotVolume + "' , "
                    + Common.COUNT + " = '" + ScannedCount + "'" +
                    " WHERE " + Common.FELLINGREGID + " = " + FellingReg_Id;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public double TotalFilteredVolume(String FellingSectionID) {
        double TotVolume = 0.00;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "";
            if (FellingSectionID.isEmpty()) {
                selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " where " + Common.ISACTIVE + " = " + 1;
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " where " + Common.ISACTIVE + " = " + 1 + " and " + Common.FELLING_SECTIONID + " = " + FellingSectionID;
            }
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotVolume = TotVolume + parseDouble(cursor.getString(cursor.getColumnIndex(Common.VOLUME)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "TotalFilteredVolume", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotVolume;
    }

    public boolean getFellingformDetailswithIDCheck(String Barcode) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " where " + Common.FELLINGREGID + "=" + FellingReg_ID + " and " + Common.ISACTIVE + " = " + 1 + " and " + Common.SBBLabel + " = " + SbbLabel;
            //String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " where " + Common.ISACTIVE + " = " + 1 + " and " + Common.SBBLabel + " = " + SbbLabel;
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " where " + Common.ISACTIVE + " = " + 1 + " and " + Common.BARCODE + " ='" + Barcode + "'";
            if (Barcode.length() > 0) {
                Cursor cursor = mDatabase.rawQuery(selectQuery, null);
                while (cursor.moveToNext()) {
                    Result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Result = true;
            AlertDialogBox("I-DB-" + "getFellingformDetailswithIDCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getTreeNumberDuplicationCheck(int fromLocationID, String FellingSecID, String treeNUm) {
        boolean Result = false;
        if (isNullOrEmpty(FellingSecID)) {
            FellingSecID = "0";
        }
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGTREEDETAILS + " where " + Common.LOCATIONID + "=" + fromLocationID + " and " + Common.FELLING_SECTIONID + " = " + FellingSecID + " and " + Common.TREENO + " = " + treeNUm;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getTreeNumberDuplicationCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean getTreeNumberTreepartCheck(int fromLocationID, String FellingSecID, String treeNUm) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " where " + Common.LOCATIONID + "=" + fromLocationID
                    + " and " + Common.FELLING_SECTIONID + " = " + FellingSecID
                    + " and " + Common.ISACTIVE + " = " + 1
                    + " and " + Common.TREENO + " = " + treeNUm
                    + " and ifnull(" + Common.TREEPARTTYPE + ", '') = ''";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getTreeNumberTreepartCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public String getTreeNumberTreepartVlaueCheck(int fromLocationID, String FellingSecID, String treeNUm) {
        String Result = "";
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT TreePartType FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " where " + Common.LOCATIONID + "=" + fromLocationID
                    + " and " + Common.FELLING_SECTIONID + " = " + FellingSecID
                    + " and " + Common.ISACTIVE + " = " + 1
                    + " and " + Common.TREENO + " = " + treeNUm;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = (cursor.getString(cursor.getColumnIndex("TreePartType")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getTreeNumberTreepartVlaueCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }


    public boolean getTreeNumberTreepartDuplicationCheck(int fromLocationID, String FellingSecID, String treeNUm, String TreePartType) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " where " + Common.LOCATIONID + "=" + fromLocationID
                    + " and " + Common.FELLING_SECTIONID + " = " + FellingSecID
                    + " and " + Common.ISACTIVE + " = " + 1
                    + " and " + Common.TREENO + " = " + treeNUm
                    + " and " + Common.TREEPARTTYPE + "= '" + TreePartType + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getTreeNumberTreepartDuplicationCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean UpdategetTreeNumberTreepartDuplicationCheck(int fromLocationID, String FellingSecID, String treeNUm, String TreePartType, String BarCode) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            if (TreePartType.equals("")) {
                String selectQueryCount = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " where " + Common.LOCATIONID + "=" + fromLocationID
                        + " and " + Common.FELLING_SECTIONID + " = " + FellingSecID
                        + " and " + Common.ISACTIVE + " = " + 1
                        + " and " + Common.TREENO + " = " + treeNUm
                        + " and " + Common.BARCODE + " ='" + BarCode + "'"
                        + " and " + Common.TREEPARTTYPE + "= '" + TreePartType + "'";
                Cursor cursorCount = mDatabase.rawQuery(selectQueryCount, null);
                while (cursorCount.moveToNext()) {
                    return false;
                }
                return true;
            }
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " where " + Common.LOCATIONID + "=" + fromLocationID
                    + " and " + Common.FELLING_SECTIONID + " = " + FellingSecID
                    + " and " + Common.ISACTIVE + " = " + 1
                    + " and " + Common.TREENO + " = " + treeNUm
                    + " and " + Common.BARCODE + " !='" + BarCode + "'"
                    + " and " + Common.TREEPARTTYPE + "= '" + TreePartType + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getTreeNumberTreepartDuplicationCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public ArrayList<FellingTreeDetailsModel> getTreeNumberDuplication(
            int fromLocationID, String FellingSecID, String treeNUm) {
        ArrayList<FellingTreeDetailsModel> fellingTreeList = new ArrayList<FellingTreeDetailsModel>();
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGTREEDETAILS + " where " + Common.LOCATIONID + "=" + fromLocationID + " and " + Common.FELLING_SECTIONID + " = " + FellingSecID + " and " + Common.TREENO + " = " + treeNUm;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingTreeDetModel = new FellingTreeDetailsModel();
                fellingTreeDetModel.setFellingRegID(cursor.getInt(cursor.getColumnIndex(Common.FELLINGREGID)));
                fellingTreeDetModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                fellingTreeDetModel.setFellingRegUniqueID(cursor.getString(cursor.getColumnIndex(Common.FELLIINGREGUNIQUEID)));
                fellingTreeDetModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                fellingTreeDetModel.setFooter_1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                fellingTreeDetModel.setFooter_2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                fellingTreeDetModel.setTop_1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                fellingTreeDetModel.setTop_2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                fellingTreeDetModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                fellingTreeDetModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                fellingTreeDetModel.setIsNewTreeNumber(cursor.getString(cursor.getColumnIndex(Common.ISNEWTREENO)));
                fellingTreeDetModel.setPlotNumber(cursor.getString(cursor.getColumnIndex(Common.PLOTNO)));
                fellingTreeDetModel.setOldPlotNo(cursor.getString(cursor.getColumnIndex(Common.OLDPLOTNO)));
                fellingTreeDetModel.setOldWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.OLDWOODSPECODE)));
                fellingTreeList.add(fellingTreeDetModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferduplicateCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return fellingTreeList;
    }

    public ArrayList<FellingTreeDetailsModel> getExportTreeNumber() {
        ArrayList<FellingTreeDetailsModel> fellingTreeList = new ArrayList<FellingTreeDetailsModel>();
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGTREEDETAILS;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingTreeDetModel = new FellingTreeDetailsModel();
                fellingTreeDetModel.setFellingRegID(cursor.getInt(cursor.getColumnIndex(Common.FELLINGREGID)));
                fellingTreeDetModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                fellingTreeDetModel.setFellingRegUniqueID(cursor.getString(cursor.getColumnIndex(Common.FELLIINGREGUNIQUEID)));
                fellingTreeDetModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                fellingTreeDetModel.setFooter_1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                fellingTreeDetModel.setFooter_2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                fellingTreeDetModel.setTop_1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                fellingTreeDetModel.setTop_2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                fellingTreeDetModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                fellingTreeDetModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                fellingTreeDetModel.setIsNewTreeNumber(cursor.getString(cursor.getColumnIndex(Common.ISNEWTREENO)));
                fellingTreeDetModel.setPlotNumber(cursor.getString(cursor.getColumnIndex(Common.PLOTNO)));
                fellingTreeDetModel.setOldPlotNo(cursor.getString(cursor.getColumnIndex(Common.OLDPLOTNO)));
                fellingTreeDetModel.setOldWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.OLDWOODSPECODE)));
                fellingTreeList.add(fellingTreeDetModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getExportTreeNumber", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return fellingTreeList;
    }

    public ArrayList<String> getTreeNumber(int FellingRegID) {
        ArrayList<String> fellingTreeList = new ArrayList<String>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT TreeNumber FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " Where " + Common.FELLINGREGID + " = '" + FellingRegID + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingTreeList.add(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return fellingTreeList;
    }


    public ArrayList<String> getTreeNumberFromInternal(String FellingSecID) {
        ArrayList<String> fellingTreeList = new ArrayList<String>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_FELLINGTREEDETAILS + " Where " + Common.ISNEWTREENO + " = " + 1 + " and " + Common.FELLING_SECTIONID + "='" + FellingSecID + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingTreeList.add(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return fellingTreeList;
    }


    public int getMaxTreeNumberInter(int FromLoca, String FellingSec) {
        String TreeNumber;
        int InternalNewTreeNumber = 0;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT MAX(TreeNumber) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_FELLINGREGISTER + " where " + Common.ExternalDataBaseClass.ConcessionID + "=" + FromLoca + " and " + Common.ExternalDataBaseClass.FELLINGSECTIONID + "='" + FellingSec + "'";
            String selectQuery = "SELECT MAX(CAST(TreeNumber as INTEGER)) as LastIndex FROM " + Common.TBL_FELLINGTREEDETAILS + " where " + Common.LOCATIONID + "=" + FromLoca + " and " + Common.FELLING_SECTIONID + "='" + FellingSec + "'";
            Cursor Extercursor = mDatabase.rawQuery(selectQuery, null);
            while (Extercursor.moveToNext()) {
                TreeNumber = (Extercursor.getString(Extercursor.getColumnIndex("LastIndex")));
                if (isNullOrEmpty(TreeNumber)) {
                    InternalNewTreeNumber = 1;
                } else {
                    InternalNewTreeNumber = Integer.parseInt(TreeNumber);
                }
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLastAgencyD", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return InternalNewTreeNumber;
    }

    // 27 - 9 -19
    public double TotalVolumeForInventoryCount(String SelectedDate) {
        double TotVolume = 0.00;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYCOUNTLIST + " where " + Common.ISACTIVE + " = " + 1;
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYCOUNTLIST + " WHERE substr(StartDateTime,1,11)='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotVolume = TotVolume + cursor.getDouble(cursor.getColumnIndex(Common.VOLUME));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "TotalVolumeForInventoryCount", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotVolume;
    }

    public double TotalVolumeForInventoryTransfer(String SelectedDate) {
        double TotVolume = 0.00;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.ISACTIVE + " = " + 1;
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERIDLIST + " WHERE substr(StartDateTime,1,11)='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1"
                    + " and " + Common.TRANSPORTTYPEID + "=" + Common.TransportTypeId;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotVolume = TotVolume + cursor.getDouble(cursor.getColumnIndex(Common.VOLUME));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "TotalVolumeForInventoryTransfer", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotVolume;
    }

    public double TotalVolumeForInventoryReceive(String SelectedDate) {
        double TotVolume = 0.00;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.ISACTIVE + " = " + 1;
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVEDLIST + " WHERE substr(StartDateTime,1,11)='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotVolume = TotVolume + cursor.getDouble(cursor.getColumnIndex(Common.VOLUME));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "TotalVolumeForInventoryReceive", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotVolume;
    }

    /*5.6 Version*/
    public ArrayList<String> getInventoryCountDate() {
        ArrayList<String> fellingTreeList = new ArrayList<String>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * from " + Common.TBL_INVENTORYCOUNTLIST;
            String selectQuery = "SELECT DISTINCT trim(substr(StartDateTime,1,11))as StartDateTime from " + Common.TBL_INVENTORYCOUNTLIST
                    + " ORDER by " + Common.LISTID + " DESC";

            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                String SdateTime = cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME));
                String[] parts = SdateTime.split(" ");
                fellingTreeList.add(parts[0]);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return fellingTreeList;
    }

    public ArrayList<String> getInventoryTransferDate() {
        ArrayList<String> fellingTreeList = new ArrayList<String>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT trim(substr(StartDateTime,1,11))as StartDateTime from " + Common.TBL_INVENTORYTRANSFERIDLIST
                    + " Where " + Common.TRANSPORTTYPEID + "='" + Common.TransportTypeId
                    + "' ORDER by " + Common.TRANSFERID + " DESC";

            //"select DISTINCT trim(substr(StartDateTime,1,11)) from InventoryTransferList Where TransportTypeId='1' ORDER by TransferID DESC;"

            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                String SdateTime = cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME));
                String[] parts = SdateTime.split(" ");
                //Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(parts[0]);
                fellingTreeList.add(parts[0]);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return fellingTreeList;
    }

    public ArrayList<String> getInventoryReceivedDate() {
        ArrayList<String> fellingTreeList = new ArrayList<String>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * from " + Common.TBL_INVENTORYRECEIVEDLIST;
            String selectQuery = "SELECT DISTINCT trim(substr(StartDateTime,1,11))as StartDateTime from " + Common.TBL_INVENTORYRECEIVEDLIST
                    + " ORDER by " + Common.RECEIVEDID + " DESC";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                String SdateTime = cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME));
                String[] parts = SdateTime.split(" ");
                fellingTreeList.add(parts[0]);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return fellingTreeList;
    }

    public ArrayList<String> getFellingRegistrationDate() {
        ArrayList<String> fellingTreeList = new ArrayList<String>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * from " + Common.TBL_FELLINGREGISTRATIONLIST;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                String SdateTime = cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME));
                String[] parts = SdateTime.split(" ");
                fellingTreeList.add(parts[0]);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return fellingTreeList;
    }

    public boolean getSyncStatusForExportExcel(String FellingSecID) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "";
            if (FellingSecID.length() == 0) {
                selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONLIST + " where " + Common.SYNCSTATUS + " = " + 0;
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONLIST + " where " + Common.FELLING_SECTIONID + " = " + FellingSecID + " and " + Common.SYNCSTATUS + " = " + 0;
            }
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getSyncStatusForExportExcel", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    /*Export Load Plan*/
    public ArrayList<String> getExportDate() {
        ArrayList<String> fellingTreeList = new ArrayList<String>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * from " + Common.TBL_EXPORTLIST + " Where " + Common.ISACTIVE + "=1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                /*String SdateTime = cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME));
                String[] parts = SdateTime.split(" ");
                fellingTreeList.add(parts[0]);*/
                String OrderNumber = cursor.getString(cursor.getColumnIndex(Common.QUOTATIONNO));
              /*  if (isNullOrEmpty(OrderNumber)) {
                    OrderNumber = Common.QuatationDefaultNo;
                }*/
                fellingTreeList.add(OrderNumber);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return fellingTreeList;
    }

    public ArrayList<ExportModel> getExportList(String QuotationNo) {
        ArrayList<ExportModel> ExportContainerListList = new ArrayList<ExportModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        String selectQuery = "";
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYCOUNTLIST + " WHERE IsActive is null or  IsActive  = 1";
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYCOUNTLIST + " WHERE IsActive  = 1";
            //String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTLOADPLANLIST + " WHERE substr(StartDateTime,1,11)='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1";
            /*if (isNullOrEmpty(SelectedDate) || SelectedDate.equals(Common.Order_Number)) {
                selectQuery = "SELECT * FROM " + Common.TBL_EXPORTLIST + " WHERE ifnull(OrderNo, '') = '' and " + Common.ISACTIVE + " = 1";
            } else {*/
            //selectQuery = "SELECT * FROM " + Common.TBL_EXPORTLIST + " WHERE " + Common.ORDERNO + " = '" + SelectedDate + "' and " + Common.ISACTIVE + " = 1";
            selectQuery = "SELECT * FROM " + Common.TBL_EXPORTLIST + " WHERE " + Common.ISACTIVE + " = 1";// and " + Common.QUOTATIONNO + "'" + QuotationNo + "'";
            //}
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                exportListModel = new ExportModel();
                exportListModel.setExportID(cursor.getInt(cursor.getColumnIndex(Common.EXPORTID)));
                exportListModel.setExportUniqueID(cursor.getString(cursor.getColumnIndex(Common.EXPORTUNIQUEID)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)))) {
                    exportListModel.setOrderNo(" ");
                } else {
                    exportListModel.setOrderNo(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)));
                }
                exportListModel.setQuotationNo(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONNO)));
                exportListModel.setQuotationEntryFlag(cursor.getInt(cursor.getColumnIndex(Common.QUOTATIONNOFLAG)));
                exportListModel.setContainerCount(cursor.getInt(cursor.getColumnIndex(Common.CONTAINERCOUNT)));
                exportListModel.setIMEI(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                exportListModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                exportListModel.setStartDateTime(cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME)));
                exportListModel.setEndDateTime(cursor.getString(cursor.getColumnIndex(Common.ENDDATETIME)));
                exportListModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                exportListModel.setTotalCount(cursor.getInt(cursor.getColumnIndex(Common.COUNT)));
                exportListModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));

                /*exportListModel.setBookingNo(cursor.getString(cursor.getColumnIndex(Common.BOOKINGNO)));
                exportListModel.setShippingCompany(cursor.getString(cursor.getColumnIndex(Common.SHIPCMPY)));
                exportListModel.setStuffingDateTime(cursor.getString(cursor.getColumnIndex(Common.STUFFDATE)));
                exportListModel.setCuttingDateTime(cursor.getString(cursor.getColumnIndex(Common.CUTTDATE)));*/
                exportListModel.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Common.SYNCSTATUS)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.SYNCTIME)))) {
                    exportListModel.setSyncTime("");
                } else {
                    exportListModel.setSyncTime(cursor.getString(cursor.getColumnIndex(Common.SYNCTIME)));
                }
                exportListModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                ExportContainerListList.add(exportListModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getExportLoadPlanList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return ExportContainerListList;
    }

    // 10-12-20

    public double TotalVolumeForExport(String ExportId) {
        double TotVolume = 0.00;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        String selectQuery = "";
        try {
            selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " WHERE " + Common.EXPORTID + " ='" + ExportId + "' and " + Common.ISACTIVE + " = 1";
            //selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " WHERE " + Common.ORDERNO + " ='" + OrderNO + "' and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotVolume = TotVolume + parseDouble(cursor.getString(cursor.getColumnIndex(Common.VOLUME)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "TotalVolumeForExport", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotVolume;
    }

    public int TotalCountForExport(String OrderNO) {
        int TotCount = 0;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        String selectQuery = "";
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYCOUNTLIST + " where " + Common.ISACTIVE + " = " + 1;
            //String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTLOADPLANLIST + " WHERE substr(StartDateTime,1,11)='" + OrderNO + "' and " + Common.ISACTIVE + " = 1";
          /*  if (isNullOrEmpty(OrderNO) || OrderNO.equals("N/A")) {
                selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " WHERE ifnull(OrderNo, '') = '' and " + Common.ISACTIVE + " = 1";
            } else {*/
            selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " WHERE " + Common.ORDERNO + " ='" + OrderNO + "' and " + Common.ISACTIVE + " = 1";
            //}
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotCount = TotCount + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "TotalCountForExport", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotCount;
    }

    /*Delete Total Export Details*/
    public boolean DeleteExportListID(int ExportID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_EXPORTLIST + " WHERE " + Common.EXPORTID + " = " + ExportID;
            //Delete from InventoryCountList where ListID=6 and TotalCount=0
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteExportContainerDetails(int export_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_EXPORTCONTAINERLIST + " WHERE " + Common.EXPORTID + " = " + export_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteExportScannedFromList(int ExportID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "DELETE FROM " + Common.TBL_EXPORTDETAILS + " WHERE " + Common.EXPORTID + " =" + ExportID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteExportScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteExportFromQuotationScanned(String QutWSCode, String ExportID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_EXPORTDETAILS + " WHERE "
                    + Common.EXPORTID + " ='" + ExportID
                    + "' and " + Common.ISACTIVE + "= 1"
                    + " and (" + Common.QUTWOODSPECODE + "='" + QutWSCode + "'" + " or " + Common.WoodSPiceCode + "='" + QutWSCode + "')";
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteExportScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean getExportOrderNoDuplicateCheck(String container_number) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.CONTAINERNO + "='" + container_number + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getExportOrderNoDuplicateCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    /*Quotation Number Duplication*/
    public boolean getExportQuotationNoDuplicateCheck(String quotation_number) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTLIST + " where " + Common.QUOTATIONNO + "='" + quotation_number + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getExportOrderNoDuplicateCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    /*Inventory Transfer Detials*/
    public boolean insertExportIDList(String order_Number, String IMEI, int LocationID, String StartDate, String EndDate,
                                      int UserID, int Count, String Volume, int Isactive, String ExporUniqueID, String QoNO) {
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.ORDERNO, order_Number);
            //values.put(Common.CONTAINERNO, container_Number);
            values.put(Common.IMEINumber, IMEI);
            values.put(Common.LOCATIONID, LocationID);
            values.put(Common.STARTDATETIME, StartDate);
            values.put(Common.ENDDATETIME, EndDate);
            values.put(Common.USERID, UserID);
            values.put(Common.COUNT, Count);
            values.put(Common.VOLUME, Volume);
            values.put(Common.ISACTIVE, Isactive);
            values.put(Common.EXPORTUNIQUEID, ExporUniqueID);
            values.put(Common.QUOTATIONNO, QoNO);
            mDatabase.insert(Common.TBL_EXPORTLIST, null, values);
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertExportIDList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return true;
    }

    public boolean UpdateExportUniqueID(int exportID, String exportUniqueID, String OrderNo) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_EXPORTLIST + " SET "
                    + Common.EXPORTUNIQUEID + " = '" + exportUniqueID + "' , "
                    + Common.ORDERNO + " = '" + OrderNo + "' " +
                    " WHERE " + Common.EXPORTID + " = " + exportID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryTransferUniqueID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdateExportEndDate(String exportuniqueID, String endDate, int containerCount) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_EXPORTLIST + " SET "
                    + Common.ENDDATETIME + " = '" + endDate + "' , "
                    + Common.CONTAINERCOUNT + " = " + containerCount
                    + " WHERE " + Common.EXPORTUNIQUEID + " ='" + exportuniqueID + "'";
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryTransferUniqueID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public ArrayList<ContainerDetailsSuncModel> getContainerDetails(String Order_NO) {
        ArrayList<ContainerDetailsSuncModel> containerList = new ArrayList<ContainerDetailsSuncModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTCONTAINERLIST + " where " + Common.ORDERNO + "='" + Order_NO + "' and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                conmodel = new ContainerDetailsSuncModel();
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.EXPORTID)))) {
                    conmodel.setExportID("");
                } else {
                    conmodel.setExportID(cursor.getString(cursor.getColumnIndex(Common.EXPORTID)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.EXPORTUNIQUEID)))) {
                    conmodel.setExportUniqueID("");
                } else {
                    conmodel.setExportUniqueID(cursor.getString(cursor.getColumnIndex(Common.EXPORTUNIQUEID)));
                }

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)))) {
                    conmodel.setOrderNo("");
                } else {
                    conmodel.setOrderNo(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)))) {
                    conmodel.setContainerNo("");
                } else {
                    conmodel.setContainerNo(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)));
                }
                conmodel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                containerList.add(conmodel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getContainerNumber", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return containerList;
    }

    public ArrayList<String> getContainerNumber(String Order_NO) {
        ArrayList<String> containerList = new ArrayList<String>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT ContainerNo FROM " + Common.TBL_EXPORTCONTAINERLIST + " where " + Common.ORDERNO + "='" + Order_NO + "' and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)))) {
                } else {
                    containerList.add(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getContainerNumber", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return containerList;
    }

    public String getWoodSpecieCode(int Export_ID, String ContainerNo) {
        StringBuilder WoodSpecieCode = new StringBuilder();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT WoodSpieceCode FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.EXPORTID + "=" + Export_ID
                    + " and " + Common.ISACTIVE + " = 1"
                    + " and " + Common.CONTAINERNO + " ='" + ContainerNo + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                WoodSpecieCode.append(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)) + ",");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getWoodSpecieCode", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return WoodSpecieCode.toString();
    }

    public int TotalCountForContainer(int Export_ID, String ContainerNo) {
        int TotCount = 0;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.EXPORTID + "=" + Export_ID
                    + " and " + Common.ISACTIVE + " = 1"
                    + " and " + Common.CONTAINERNO + " ='" + ContainerNo + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotCount = TotCount + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "TotalVolumeForInventoryCount", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotCount;
    }

    public double TotalVolumeForContainer(int Export_ID, String ContainerNo) {
        double TotVolume = 0.00;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.EXPORTID + "=" + Export_ID
                    + " and " + Common.ISACTIVE + " = 1"
                    + " and " + Common.CONTAINERNO + " ='" + ContainerNo + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotVolume = TotVolume + parseDouble(cursor.getString(cursor.getColumnIndex(Common.VOLUME)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "TotalVolumeForInventoryCount", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotVolume;
    }

    public ArrayList<QuoatationDetailsSyncModel> getQuationListSync(int Export_ID) {
        ArrayList<QuoatationDetailsSyncModel> exportList = new ArrayList<QuoatationDetailsSyncModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT DISTINCT QutWoodSpieceCode,QutDiameter,QutTotalCBM FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.EXPORTID + "=" + Export_ID + " and " + Common.ISACTIVE + " = 1";
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTQUOTATIONLIST + " where " + Common.EXPORTID + "=" + Export_ID + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                quoModel = new QuoatationDetailsSyncModel();
                quoModel.setExportID(cursor.getString(cursor.getColumnIndex(Common.EXPORTID)));
                quoModel.setExportUniqueID(cursor.getString(cursor.getColumnIndex(Common.EXPORTUNIQUEID)));
                quoModel.setOrderNo(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)));
                quoModel.setIMEI(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                quoModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                quoModel.setDateTime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));
                quoModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)))) {
                    quoModel.setContainerNo("");
                } else {
                    quoModel.setContainerNo(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)))) {
                    quoModel.setQutWoodSpieceCode("");
                } else {
                    quoModel.setQutWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTDIAMETER)))) {
                    quoModel.setQutDiameter("0.00");
                } else {
                    quoModel.setQutDiameter(cursor.getString(cursor.getColumnIndex(Common.QUTDIAMETER)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTTOTALCBM)))) {
                    quoModel.setQutTotalCBM("0.00");
                } else {
                    quoModel.setQutTotalCBM(cursor.getString(cursor.getColumnIndex(Common.QUTTOTALCBM)));
                }
                quoModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                quoModel.setQuotationUniqueNo(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONUNIQUENO)));
                exportList.add(quoModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInternalQuationList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return exportList;
    }

    public ArrayList<QuotationInternalModel> getInternalQuationList(String QuoNo, int ExportID) {
        ArrayList<QuotationInternalModel> exportList = new ArrayList<QuotationInternalModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTQUOTATIONLIST + " where " + Common.QUOTATIONNO + "='" + QuoNo + "' and " + Common.ISACTIVE + " = 1" + " and " + Common.EXPORTID + "=" + ExportID;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                quoationModel = new QuotationInternalModel();
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONID)))) {
                    quoationModel.setQuotationID(0);
                } else {
                    quoationModel.setQuotationID(cursor.getInt(cursor.getColumnIndex(Common.QUOTATIONID)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONUNIQUENO)))) {
                    quoationModel.setQuotationUniqueNo("");
                } else {
                    quoationModel.setQuotationUniqueNo(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONUNIQUENO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONNO)))) {
                    quoationModel.setQuotationNo("");
                } else {
                    quoationModel.setQuotationNo(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONNOFLAG)))) {
                    quoationModel.setQuotationEntryFlag(0);
                } else {
                    quoationModel.setQuotationEntryFlag(cursor.getInt(cursor.getColumnIndex(Common.QUOTATIONNOFLAG)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.EXPORTID)))) {
                    quoationModel.setExportID("");
                } else {
                    quoationModel.setExportID(cursor.getString(cursor.getColumnIndex(Common.EXPORTID)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.EXPORTUNIQUEID)))) {
                    quoationModel.setExportUniqueID("");
                } else {
                    quoationModel.setExportUniqueID(cursor.getString(cursor.getColumnIndex(Common.EXPORTUNIQUEID)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)))) {
                    quoationModel.setOrderNo("");
                } else {
                    quoationModel.setOrderNo(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)))) {
                    quoationModel.setContainerNo("");
                } else {
                    quoationModel.setContainerNo(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)))) {
                    quoationModel.setIMEI("");
                } else {
                    quoationModel.setIMEI(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LOCATIONID)))) {
                    quoationModel.setLocationID("");
                } else {
                    quoationModel.setLocationID(cursor.getString(cursor.getColumnIndex(Common.LOCATIONID)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DATETIME)))) {
                    quoationModel.setDateTime("");
                } else {
                    quoationModel.setDateTime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.USERID)))) {
                    quoationModel.setUserID(0);
                } else {
                    quoationModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)))) {
                    quoationModel.setQutWoodSpieceCode("");
                } else {

                    quoationModel.setQutWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTTOTALCBM)))) {
                    quoationModel.setQutTotalCBM(0.00);
                } else {
                    quoationModel.setQutTotalCBM(cursor.getDouble(cursor.getColumnIndex(Common.QUTTOTALCBM)));
                }
                quoationModel.setQutDiameter(cursor.getString(cursor.getColumnIndex(Common.QUTDIAMETER)));
                quoationModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                exportList.add(quoationModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInternalQuationList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return exportList;
    }

    public ArrayList<QuotationListInputModel> getInternalQuationListInput(String QuoNo, int ExportID) {
        ArrayList<QuotationListInputModel> exportList = new ArrayList<QuotationListInputModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTQUOTATIONLIST + " where " + Common.QUOTATIONNO + "='" + QuoNo + "' and " + Common.ISACTIVE + " = 1" + " and " + Common.EXPORTID + "=" + ExportID;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                quotationListInputModel = new QuotationListInputModel();
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONNO)))) {
                    quotationListInputModel.setQuotationNo("");
                } else {
                    quotationListInputModel.setQuotationNo(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONNO)));
                }

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONUNIQUENO)))) {
                    quotationListInputModel.setQuotationUniqueNo("");
                } else {
                    quotationListInputModel.setQuotationUniqueNo(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONUNIQUENO)));
                }

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)))) {
                    quotationListInputModel.setWoodSpieceCode("");
                } else {

                    quotationListInputModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTTOTALCBM)))) {
                    quotationListInputModel.setQutTotalCBM(0.00);
                } else {
                    quotationListInputModel.setQutTotalCBM(cursor.getDouble(cursor.getColumnIndex(Common.QUTTOTALCBM)));
                }
                quotationListInputModel.setDiameterRange(cursor.getString(cursor.getColumnIndex(Common.QUTDIAMETER)));
                exportList.add(quotationListInputModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInternalQuationList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return exportList;
    }


    public boolean getInternalQuationListCheck(int ExportID, String WSC, String DIAMETER, String TOTCBM, String QuotaNo) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTQUOTATIONLIST + " where " + Common.ISACTIVE + " = " + 1
                    + " and " + Common.QUOTATIONNO + " ='" + QuotaNo + "'"
                    + " and " + Common.EXPORTID + " ='" + ExportID + "'"
                    + " and " + Common.QUTWOODSPECODE + " ='" + WSC + "'"
                    + " and " + Common.QUTDIAMETER + " ='" + DIAMETER + "'"
                    + " and " + Common.QUTTOTALCBM + " ='" + TOTCBM + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInternalQuationListCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public ArrayList<ExportDetailsModel> getExportBarcodeDetailsSync(int Export_ID) {
        int ID = 1;
        ArrayList<ExportDetailsModel> exportList = new ArrayList<ExportDetailsModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.EXPORTID + "=" + Export_ID
                    + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                exportDetailsModel = new ExportDetailsModel();
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.AGEOFLOG)))) {
                    exportDetailsModel.setAgeOfLog("");
                } else {
                    exportDetailsModel.setAgeOfLog(cursor.getString(cursor.getColumnIndex(Common.AGEOFLOG)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.BARCODE)))) {
                    exportDetailsModel.setBarCode("");
                } else {
                    exportDetailsModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                }
                String ContainerNO = cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO));
                if (isNullOrEmpty(ContainerNO)) {
                    exportDetailsModel.setContainerNo("");
                } else {
                    exportDetailsModel.setContainerNo(ContainerNO);
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DATETIME)))) {
                    exportDetailsModel.setDateTime("");
                } else {
                    exportDetailsModel.setDateTime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));
                }
                exportDetailsModel.setDiameter(cursor.getString(cursor.getColumnIndex(Common.DIAMETER)));
                exportDetailsModel.setEntryMode(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                exportDetailsModel.setExportID(cursor.getInt(cursor.getColumnIndex(Common.EXPORTID)));
                exportDetailsModel.setExportUniqueID(cursor.getString(cursor.getColumnIndex(Common.EXPORTUNIQUEID)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF1)))) {
                    exportDetailsModel.setFooter_1("");
                } else {
                    exportDetailsModel.setFooter_1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF2)))) {
                    exportDetailsModel.setFooter_2("");
                } else {
                    exportDetailsModel.setFooter_2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                }
                exportDetailsModel.setID(ID);
                exportDetailsModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDDIAMETER)))) {
                    exportDetailsModel.setIsValidDiameter(0);
                } else {
                    exportDetailsModel.setIsValidDiameter(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDDIAMETER)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDPVNO)))) {
                    exportDetailsModel.setIsValidPvNo(0);
                } else {
                    exportDetailsModel.setIsValidPvNo(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDPVNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDVOLUME)))) {
                    exportDetailsModel.setIsValidVolume(0);
                } else {
                    exportDetailsModel.setIsValidVolume(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDVOLUME)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDWSCODE)))) {
                    exportDetailsModel.setIsValidWSCode(0);
                } else {
                    exportDetailsModel.setIsValidWSCode(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDWSCODE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LENGTH)))) {
                    exportDetailsModel.setLength("");
                } else {
                    exportDetailsModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                }
                exportDetailsModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)))) {
                    exportDetailsModel.setOrderNo("");
                } else {
                    exportDetailsModel.setOrderNo(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.PVDATE)))) {
                    exportDetailsModel.setPvDate("");
                } else {
                    exportDetailsModel.setPvDate(cursor.getString(cursor.getColumnIndex(Common.PVDATE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.PVNO)))) {
                    exportDetailsModel.setPvNo("");
                } else {
                    exportDetailsModel.setPvNo(cursor.getString(cursor.getColumnIndex(Common.PVNO)));
                }
                exportDetailsModel.setQutDiameter(cursor.getString(cursor.getColumnIndex(Common.QUTDIAMETER)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTTOTALCBM)))) {
                    exportDetailsModel.setQutTotalCBM(0.00);
                } else {
                    exportDetailsModel.setQutTotalCBM(cursor.getDouble(cursor.getColumnIndex(Common.QUTTOTALCBM)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)))) {
                    exportDetailsModel.setQutWoodSpieceCode("");
                } else {
                    exportDetailsModel.setQutWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)));
                }
                exportDetailsModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT1)))) {
                    exportDetailsModel.setTop_1("");
                } else {
                    exportDetailsModel.setTop_1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT2)))) {
                    exportDetailsModel.setTop_2("");
                } else {
                    exportDetailsModel.setTop_2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                }
                exportDetailsModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.VOLUME)))) {
                    exportDetailsModel.setVolume(0.00);
                } else {
                    exportDetailsModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)))) {
                    exportDetailsModel.setWoodSpieceCode("");
                } else {
                    exportDetailsModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                }
                exportDetailsModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));

                exportList.add(exportDetailsModel);
                ID = ID + 1;
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getExportDetailsList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return exportList;
    }

    public ArrayList<ExportDetailsModel> getExportDetailsInternalList(int Export_ID, boolean ExpoDetailsFlag, String ContainerNO) {
        int ID = 1;
        ArrayList<ExportDetailsModel> exportList = new ArrayList<ExportDetailsModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.EXPORTID + "=" + Export_ID
                    + " and " + Common.CONTAINERNO + " ='" + ContainerNO + "'"
                    + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                exportDetailsModel = new ExportDetailsModel();
                exportDetailsModel.setID(ID);
                exportDetailsModel.setExportID(cursor.getInt(cursor.getColumnIndex(Common.EXPORTID)));
                exportDetailsModel.setExportUniqueID(cursor.getString(cursor.getColumnIndex(Common.EXPORTUNIQUEID)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)))) {
                    exportDetailsModel.setContainerNo("");
                } else {
                    exportDetailsModel.setContainerNo(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.BARCODE)))) {
                    exportDetailsModel.setBarCode("");
                } else {
                    exportDetailsModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DATETIME)))) {
                    exportDetailsModel.setDateTime("");
                } else {
                    exportDetailsModel.setDateTime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)))) {
                    exportDetailsModel.setOrderNo("");
                } else {
                    exportDetailsModel.setOrderNo(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.PVNO)))) {
                    exportDetailsModel.setPvNo("");
                } else {
                    exportDetailsModel.setPvNo(cursor.getString(cursor.getColumnIndex(Common.PVNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.PVDATE)))) {
                    exportDetailsModel.setPvDate("");
                } else {
                    exportDetailsModel.setPvDate(cursor.getString(cursor.getColumnIndex(Common.PVDATE)));
                }
                exportDetailsModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                exportDetailsModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                exportDetailsModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                exportDetailsModel.setEntryMode(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                exportDetailsModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.AGEOFLOG)))) {
                    exportDetailsModel.setAgeOfLog("");
                } else {
                    exportDetailsModel.setAgeOfLog(cursor.getString(cursor.getColumnIndex(Common.AGEOFLOG)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)))) {
                    exportDetailsModel.setWoodSpieceCode("");
                } else {
                    exportDetailsModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                }

                exportDetailsModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                exportDetailsModel.setDiameter(cursor.getString(cursor.getColumnIndex(Common.DIAMETER)));
                exportDetailsModel.setQutDiameter(cursor.getString(cursor.getColumnIndex(Common.QUTDIAMETER)));

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF1)))) {
                    exportDetailsModel.setFooter_1("");
                } else {
                    exportDetailsModel.setFooter_1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF2)))) {
                    exportDetailsModel.setFooter_2("");
                } else {
                    exportDetailsModel.setFooter_2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT1)))) {
                    exportDetailsModel.setTop_1("");
                } else {
                    exportDetailsModel.setTop_1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT2)))) {
                    exportDetailsModel.setTop_2("");
                } else {
                    exportDetailsModel.setTop_2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LENGTH)))) {
                    exportDetailsModel.setLength("");
                } else {
                    exportDetailsModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                }
                exportDetailsModel.setDateTime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.PVDATE)))) {
                    exportDetailsModel.setPvDate("");
                } else {
                    exportDetailsModel.setPvDate(cursor.getString(cursor.getColumnIndex(Common.PVDATE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)))) {
                    exportDetailsModel.setQutWoodSpieceCode("");
                } else {
                    exportDetailsModel.setQutWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTTOTALCBM)))) {
                    exportDetailsModel.setQutTotalCBM(0.00);
                } else {
                    exportDetailsModel.setQutTotalCBM(cursor.getDouble(cursor.getColumnIndex(Common.QUTTOTALCBM)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.VOLUME)))) {
                    exportDetailsModel.setVolume(0.00);
                } else {
                    exportDetailsModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDPVNO)))) {
                    exportDetailsModel.setIsValidPvNo(0);
                } else {
                    exportDetailsModel.setIsValidPvNo(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDPVNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDVOLUME)))) {
                    exportDetailsModel.setIsValidVolume(0);
                } else {
                    exportDetailsModel.setIsValidVolume(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDVOLUME)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDWSCODE)))) {
                    exportDetailsModel.setIsValidWSCode(0);
                } else {
                    exportDetailsModel.setIsValidWSCode(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDWSCODE)));
                }
                exportList.add(exportDetailsModel);
                ID = ID + 1;
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getExportDetailsList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return exportList;
    }

    public ArrayList<ExportDetailsModel> getExportDetailsList(int Export_ID, String ContainerNo) {
        int ID = 1;
        ArrayList<ExportDetailsModel> exportList = new ArrayList<ExportDetailsModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        String selectQuery = "";
        try {
            if (isNullOrEmpty(ContainerNo)) {
                ContainerNo = "ALL";
            }
            if (ContainerNo.equals("ALL")) {
                selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.EXPORTID + "=" + Export_ID
                        + " and " + Common.ISACTIVE + " = 1";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.EXPORTID + "=" + Export_ID
                        + " and " + Common.CONTAINERNO + "='" + ContainerNo + "'"
                        + " and " + Common.ISACTIVE + " = 1";
            }

            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                exportDetailsModel = new ExportDetailsModel();
                exportDetailsModel.setID(ID);
                exportDetailsModel.setExportID(cursor.getInt(cursor.getColumnIndex(Common.EXPORTID)));
                exportDetailsModel.setExportUniqueID(cursor.getString(cursor.getColumnIndex(Common.EXPORTUNIQUEID)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)))) {
                    exportDetailsModel.setContainerNo("");
                } else {
                    exportDetailsModel.setContainerNo(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.BARCODE)))) {
                    exportDetailsModel.setBarCode("");
                } else {
                    exportDetailsModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DATETIME)))) {
                    exportDetailsModel.setDateTime("");
                } else {
                    exportDetailsModel.setDateTime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)))) {
                    exportDetailsModel.setOrderNo("");
                } else {
                    exportDetailsModel.setOrderNo(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.PVNO)))) {
                    exportDetailsModel.setPvNo("");
                } else {
                    exportDetailsModel.setPvNo(cursor.getString(cursor.getColumnIndex(Common.PVNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.PVDATE)))) {
                    exportDetailsModel.setPvDate("");
                } else {
                    exportDetailsModel.setPvDate(cursor.getString(cursor.getColumnIndex(Common.PVDATE)));
                }
                exportDetailsModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                exportDetailsModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                exportDetailsModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                exportDetailsModel.setEntryMode(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                exportDetailsModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.AGEOFLOG)))) {
                    exportDetailsModel.setAgeOfLog("");
                } else {
                    exportDetailsModel.setAgeOfLog(cursor.getString(cursor.getColumnIndex(Common.AGEOFLOG)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)))) {
                    exportDetailsModel.setWoodSpieceCode("");
                } else {
                    exportDetailsModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                }

                exportDetailsModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                exportDetailsModel.setDiameter(cursor.getString(cursor.getColumnIndex(Common.DIAMETER)));
                exportDetailsModel.setQutDiameter(cursor.getString(cursor.getColumnIndex(Common.QUTDIAMETER)));

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF1)))) {
                    exportDetailsModel.setFooter_1("");
                } else {
                    exportDetailsModel.setFooter_1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF2)))) {
                    exportDetailsModel.setFooter_2("");
                } else {
                    exportDetailsModel.setFooter_2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT1)))) {
                    exportDetailsModel.setTop_1("");
                } else {
                    exportDetailsModel.setTop_1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT2)))) {
                    exportDetailsModel.setTop_2("");
                } else {
                    exportDetailsModel.setTop_2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LENGTH)))) {
                    exportDetailsModel.setLength("");
                } else {
                    exportDetailsModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                }
                exportDetailsModel.setDateTime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.PVDATE)))) {
                    exportDetailsModel.setPvDate("");
                } else {
                    exportDetailsModel.setPvDate(cursor.getString(cursor.getColumnIndex(Common.PVDATE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)))) {
                    exportDetailsModel.setQutWoodSpieceCode("");
                } else {
                    exportDetailsModel.setQutWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTTOTALCBM)))) {
                    exportDetailsModel.setQutTotalCBM(0.00);
                } else {
                    exportDetailsModel.setQutTotalCBM(cursor.getDouble(cursor.getColumnIndex(Common.QUTTOTALCBM)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.VOLUME)))) {
                    exportDetailsModel.setVolume(0.00);
                } else {
                    exportDetailsModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDPVNO)))) {
                    exportDetailsModel.setIsValidPvNo(0);
                } else {
                    exportDetailsModel.setIsValidPvNo(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDPVNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDVOLUME)))) {
                    exportDetailsModel.setIsValidVolume(0);
                } else {
                    exportDetailsModel.setIsValidVolume(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDVOLUME)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDWSCODE)))) {
                    exportDetailsModel.setIsValidWSCode(0);
                } else {
                    exportDetailsModel.setIsValidWSCode(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDWSCODE)));
                }
                exportList.add(exportDetailsModel);
                ID = ID + 1;
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getExportDetailsList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return exportList;
    }

    public ArrayList<BarcodeDetailsInputModel> getExportDetailsListInput(int Export_ID, String ContainerNo) {
        int ID = 1;
        ArrayList<BarcodeDetailsInputModel> exportListinput = new ArrayList<BarcodeDetailsInputModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        String selectQuery = "";
        try {
            if (isNullOrEmpty(ContainerNo)) {
                ContainerNo = "ALL";
            }
            if (ContainerNo.equals("ALL")) {
                selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.EXPORTID + "=" + Export_ID
                        + " and " + Common.ISACTIVE + " = 1";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.EXPORTID + "=" + Export_ID
                        + " and " + Common.CONTAINERNO + "='" + ContainerNo + "'"
                        + " and " + Common.ISACTIVE + " = 1";
            }

            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                barcodeDetailsInputModel = new BarcodeDetailsInputModel();

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONNO)))) {
                    barcodeDetailsInputModel.setQuotationNo("");
                } else {
                    barcodeDetailsInputModel.setQuotationNo(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONNO)));
                }


                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONUNIQUENO)))) {
                    barcodeDetailsInputModel.setQuotationUniqueNo("");
                } else {
                    barcodeDetailsInputModel.setQuotationUniqueNo(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONUNIQUENO)));
                }


                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)))) {
                    barcodeDetailsInputModel.setContainerNo("");
                } else {
                    barcodeDetailsInputModel.setContainerNo(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.BARCODE)))) {
                    barcodeDetailsInputModel.setBarcode("");
                } else {
                    barcodeDetailsInputModel.setBarcode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                }


                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.PVNO)))) {
                    barcodeDetailsInputModel.setPVNo("");
                } else {
                    barcodeDetailsInputModel.setPVNo(cursor.getString(cursor.getColumnIndex(Common.PVNO)));
                }

                barcodeDetailsInputModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                barcodeDetailsInputModel.setWoodspieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));

                barcodeDetailsInputModel.setEntryModeId(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.AGEOFLOG)))) {
                    barcodeDetailsInputModel.setAgeOflog("");
                } else {
                    barcodeDetailsInputModel.setAgeOflog(cursor.getString(cursor.getColumnIndex(Common.AGEOFLOG)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)))) {
                    barcodeDetailsInputModel.setWoodspieceCode("");
                } else {
                    barcodeDetailsInputModel.setWoodspieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                }

                barcodeDetailsInputModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF1)))) {
                    barcodeDetailsInputModel.setDF1("");
                } else {
                    barcodeDetailsInputModel.setDF1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF2)))) {
                    barcodeDetailsInputModel.setDF2("");
                } else {
                    barcodeDetailsInputModel.setDF2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT1)))) {
                    barcodeDetailsInputModel.setDT1("");
                } else {
                    barcodeDetailsInputModel.setDT1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT2)))) {
                    barcodeDetailsInputModel.setDT2("");
                } else {
                    barcodeDetailsInputModel.setDT2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LENGTH)))) {
                    barcodeDetailsInputModel.setLength("");
                } else {
                    barcodeDetailsInputModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                }


                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.NOTEF)))) {
                    barcodeDetailsInputModel.setNoteF("");
                } else {
                    barcodeDetailsInputModel.setNoteF(cursor.getString(cursor.getColumnIndex(Common.NOTEF)));
                }

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.NOTET)))) {
                    barcodeDetailsInputModel.setNoteT("");
                } else {
                    barcodeDetailsInputModel.setNoteT(cursor.getString(cursor.getColumnIndex(Common.NOTET)));
                }

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.NOTEL)))) {
                    barcodeDetailsInputModel.setNoteL("");
                } else {
                    barcodeDetailsInputModel.setNoteL(cursor.getString(cursor.getColumnIndex(Common.NOTEL)));
                }

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.VOLUME)))) {
                    barcodeDetailsInputModel.setVolume(0.00);
                } else {
                    barcodeDetailsInputModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDPVNO)))) {
                    barcodeDetailsInputModel.setIsvalidPvNo(0);
                } else {
                    barcodeDetailsInputModel.setIsvalidPvNo(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDPVNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDVOLUME)))) {
                    barcodeDetailsInputModel.setValidVolume(0);
                } else {
                    barcodeDetailsInputModel.setValidVolume(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDVOLUME)));
                }

                exportListinput.add(barcodeDetailsInputModel);
                ID = ID + 1;
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getExportDetailsList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return exportListinput;
    }

    public ArrayList<ExportInputDetailsModel> getExportInputDetailsList(int Export_ID, boolean ExpoDetailsFlag) {
        int ID = 1;
        ArrayList<ExportInputDetailsModel> exportInputList = new ArrayList<ExportInputDetailsModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        String selectQuery = "";
        try {
            if (ExpoDetailsFlag == true) {
                selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.EXPORTID + "=" + Export_ID + " and " + Common.ISACTIVE + " = 1";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.EXPORTID + "=" + Export_ID + " and " + Common.ISACTIVE + " = 1";
            }
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                exportinputDetailsModel = new ExportInputDetailsModel();
                exportinputDetailsModel.setID(ID);
                exportinputDetailsModel.setExportId(cursor.getInt(cursor.getColumnIndex(Common.EXPORTID)));
                exportinputDetailsModel.setExportUniqueID(cursor.getInt(cursor.getColumnIndex(Common.EXPORTUNIQUEID)));
                exportinputDetailsModel.setContainerNo(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)));
                exportinputDetailsModel.setOrderNo(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)));
                exportinputDetailsModel.setPvNo(cursor.getString(cursor.getColumnIndex(Common.PVNO)));
                exportinputDetailsModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                exportinputDetailsModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                exportinputDetailsModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                exportinputDetailsModel.setEntryMode(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                exportinputDetailsModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                exportinputDetailsModel.setPvNo(cursor.getString(cursor.getColumnIndex(Common.PVNO)));
                exportinputDetailsModel.setAgeOfLog(cursor.getString(cursor.getColumnIndex(Common.AGEOFLOG)));
                exportinputDetailsModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                exportinputDetailsModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                exportinputDetailsModel.setDiameter(cursor.getString(cursor.getColumnIndex(Common.DIAMETER)));
                exportinputDetailsModel.setQutDiameter(cursor.getString(cursor.getColumnIndex(Common.QUTDIAMETER)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF1)))) {
                    exportinputDetailsModel.setDF1("");
                } else {
                    exportinputDetailsModel.setDF1(cursor.getString(cursor.getColumnIndex(Common.DF1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DF2)))) {
                    exportinputDetailsModel.setDF2("");
                } else {
                    exportinputDetailsModel.setDF2(cursor.getString(cursor.getColumnIndex(Common.DF2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT1)))) {
                    exportinputDetailsModel.setDT1("");
                } else {
                    exportinputDetailsModel.setDT1(cursor.getString(cursor.getColumnIndex(Common.DT1)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.DT2)))) {
                    exportinputDetailsModel.setDT2("");
                } else {
                    exportinputDetailsModel.setDT2(cursor.getString(cursor.getColumnIndex(Common.DT2)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.LENGTH)))) {
                    exportinputDetailsModel.setLength("");
                } else {
                    exportinputDetailsModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                }
                exportinputDetailsModel.setDateTime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.PVDATE)))) {
                    exportinputDetailsModel.setPvDate("");
                } else {
                    exportinputDetailsModel.setPvDate(cursor.getString(cursor.getColumnIndex(Common.PVDATE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)))) {
                    exportinputDetailsModel.setQutWoodSpieceCode("");
                } else {
                    exportinputDetailsModel.setQutWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.QUTWOODSPECODE)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QUTTOTALCBM)))) {
                    exportinputDetailsModel.setQutTotalCBM(0.00);
                } else {
                    exportinputDetailsModel.setQutTotalCBM(cursor.getDouble(cursor.getColumnIndex(Common.QUTTOTALCBM)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.VOLUME)))) {
                    exportinputDetailsModel.setVolume(0.00);
                } else {
                    exportinputDetailsModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDPVNO)))) {
                    exportinputDetailsModel.setIsValidPvNo(0);
                } else {
                    exportinputDetailsModel.setIsValidPvNo(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDPVNO)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDVOLUME)))) {
                    exportinputDetailsModel.setIsValidVolume(0);
                } else {
                    exportinputDetailsModel.setIsValidVolume(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDVOLUME)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ISVALIDWSCODE)))) {
                    exportinputDetailsModel.setIsValidWSCode(0);
                } else {
                    exportinputDetailsModel.setIsValidWSCode(cursor.getInt(cursor.getColumnIndex(Common.ISVALIDWSCODE)));
                }
                exportInputList.add(exportinputDetailsModel);
                ID = ID + 1;
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getExportDetailsList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return exportInputList;
    }

    public String getLastExportID() {
        String ExportID = "";
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(ExportID) as LastExportID FROM " + Common.TBL_EXPORTLIST;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                ExportID = (cursor.getString(cursor.getColumnIndex("LastExportID")));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferListID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return ExportID;
    }

    public double getWoodSpeiceCodeQuotationList(int exportID, String quoUniqeID) {
        double WSCVolume = 0.00;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTQUOTATIONLIST + " where "
                    + Common.EXPORTID + "=" + exportID + " and "
                    + Common.QUOTATIONUNIQUENO + "='" + quoUniqeID + "' and "
                    + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                WSCVolume = WSCVolume + cursor.getDouble(cursor.getColumnIndex(Common.QUTTOTALCBM));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferWithVBBNumber", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return WSCVolume;
    }

    public double getWoodSpeiceCodeLogsDetails(int exportID, String quoUniqeID) {
        double WSCVolume = 0.00;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where "
                    + Common.EXPORTID + "=" + exportID + " and "
                    + Common.QUOTATIONUNIQUENO + "='" + quoUniqeID + "' and "
                    + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                WSCVolume = WSCVolume + cursor.getDouble(cursor.getColumnIndex(Common.VOLUME));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferWithVBBNumber", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return WSCVolume;
    }

    public ArrayList<ExportDetailsModel> getExportLoadPlanContainerDetails(String vbb_number, int TransferID) {
        ArrayList<ExportDetailsModel> scannedReusltList = new ArrayList<ExportDetailsModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + "=" + TransferID + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
               /* exportDetailsModel = new ExportDetailsModel();
                exportDetailsModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                exportDetailsModel.setLength(cursor.getInt(cursor.getColumnIndex(Common.LENGTH)));
                exportDetailsModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                exportDetailsModel.setFromLocation(cursor.getString(cursor.getColumnIndex(Common.FROMLOCATION)));
                exportDetailsModel.setToLocation(cursor.getString(cursor.getColumnIndex(Common.TOLOCATION)));
                exportDetailsModel.setVolume(cursor.getString(cursor.getColumnIndex(Common.VOLUME)));
                exportDetailsModel.setVBB_Number(cursor.getString(cursor.getColumnIndex(Common.VBBNUMBER)));
                exportDetailsModel.setUserID(cursor.getString(cursor.getColumnIndex(Common.USERID)));
                exportDetailsModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                exportDetailsModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                exportDetailsModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                exportDetailsModel.setQualitiy(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                exportDetailsModel.setFellingSectionId(cursor.getInt(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                exportDetailsModel.setTreeNumber(cursor.getInt(cursor.getColumnIndex(Common.TREENO)));*/
                //inventoryTransModel.setTransUniqueID(cursor.getString(cursor.getColumnIndex(TRANSFERUNIQUEID)));
                scannedReusltList.add(exportDetailsModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferWithVBBNumber", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public boolean getExportListIDCheck(String BarCode) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.ISACTIVE + " = " + 1 + " and " + Common.BARCODE + " ='" + BarCode + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getExportLoadPlanListIDCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean UpdateExportListID(String endDateTime, int ScannedCount, int ExportID, String VolumeSum, int Isactive, String CountUniqID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_EXPORTLIST + " SET "
                    + Common.ENDDATETIME + " = '" + endDateTime + "' , "
                    + Common.COUNT + "= '" + ScannedCount + "', "
                    + Common.ISACTIVE + " = '" + Isactive + "' , "
                    + Common.EXPORTUNIQUEID + " = '" + CountUniqID + "' , "
                    + Common.VOLUME + " = '" + VolumeSum + "'" +
                    " WHERE " + Common.EXPORTID + " = " + ExportID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryCountListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean insertExportDetailsResult(int ExportID, String ExportUniqueID, String OrderNo, int QuotID, String QuotUniqueNo, String ContainerNo, String QuoNo, int ToLocationID, String IMEI, String PvNo, String PvDate, String SbbLabel, String BarCode,
                                             String WoodSpieceID, String QutWoodSpieceCode, String WoodSpieceCode, String AgeofLOG, String Df1, String Df2, String Dt1, String Dt2, String Length,
                                             int UserID, int EntryMode, String TotCBM, String Volume, String DateTime, int IsActive, String NoteF, String NoteT, String NoteL, int IsValidVolume,
                                             int IsValidWSCode, int IsValidDiameter, String Export_Diameter, String Export_TotDiameter) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.EXPORTID, ExportID);
            values.put(Common.EXPORTUNIQUEID, ExportUniqueID);
            values.put(Common.ORDERNO, OrderNo);
            values.put(Common.QUOTATIONID, QuotID);
            values.put(Common.QUOTATIONUNIQUENO, QuotUniqueNo);
            values.put(Common.CONTAINERNO, ContainerNo);
            values.put(Common.QUOTATIONNO, QuoNo);
            values.put(Common.PVNO, PvNo);
            values.put(Common.PVDATE, PvDate);
            values.put(Common.LOCATIONID, ToLocationID);
            values.put(Common.IMEINumber, IMEI);
            values.put(Common.SBBLabel, SbbLabel);
            values.put(Common.BARCODE, BarCode);
            values.put(Common.WoodSpiceID, WoodSpieceID);
            values.put(Common.QUTWOODSPECODE, QutWoodSpieceCode);
            values.put(Common.WoodSPiceCode, WoodSpieceCode);
            values.put(Common.AGEOFLOG, AgeofLOG);
            values.put(Common.DF1, Df1);
            values.put(Common.DF2, Df2);
            values.put(Common.DT1, Dt1);
            values.put(Common.DT2, Dt2);
            values.put(Common.LENGTH, Length);
            values.put(Common.USERID, UserID);
            values.put(Common.ENTRYMODE, EntryMode);
            values.put(Common.QUTTOTALCBM, TotCBM);
            values.put(Common.VOLUME, Volume);
            values.put(Common.DATETIME, DateTime);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.ISVALIDWSCODE, IsValidWSCode);
            values.put(Common.ISVALIDDIAMETER, IsValidDiameter);
            values.put(Common.DIAMETER, Export_Diameter);
            values.put(Common.QUTDIAMETER, Export_TotDiameter);
            mDatabase.insert(Common.TBL_EXPORTDETAILS, null, values);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertExportDetailsResult", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }


    public boolean UpdateExportDetailsWithBarcode(String Barcode, String ContainerNO) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_EXPORTDETAILS + " SET "
                    + Common.CONTAINERNO + " = '" + ContainerNO + "'" +
                    " WHERE " + Common.BARCODE + " = '" + Barcode + "'";
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryCountListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }


    /* Container  operations*/
    public boolean insertExportContainerList(int ExportID, String ExportUniqueID, String OrderNo, String ContainerNo, String QouNo, int IsActive, int Count, double TotVol) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.EXPORTID, ExportID);
            values.put(Common.EXPORTUNIQUEID, ExportUniqueID);
            values.put(Common.ORDERNO, OrderNo);
            values.put(Common.CONTAINERNO, ContainerNo);
            values.put(Common.QUOTATIONNO, QouNo);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.COUNT, Count);
            values.put(Common.VOLUME, TotVol);
            mDatabase.insert(Common.TBL_EXPORTCONTAINERLIST, null, values);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertExportDetailsResult", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public String getLastExportContainerList() {
        String QuotattionID = "";
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(ID) as ContainerID FROM " + Common.TBL_EXPORTCONTAINERLIST;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                QuotattionID = (cursor.getString(cursor.getColumnIndex("ContainerID")));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getLastExportQuotationList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return QuotattionID;
    }

    public boolean UpdateExportContainerList(String ContainerUniqueID, String Volume, int Count) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_EXPORTCONTAINERLIST + " SET "
                    + Common.VOLUME + " = '" + Volume + "' , "
                    + Common.COUNT + " = '" + Count + "'"
                    + " WHERE " + Common.CONTAINERNO + " = '" + ContainerUniqueID + "'";
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateExportQuotationList", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }


    /* Quotation  operations*/
    public boolean insertExportQuotationList(int ExportID, String ExportUniqueID, String OrderNo, String ContainerNo, int ToLocationID, String IMEI, String QutWoodSpieceCode, int UserID,
                                             String TotCBM, String DateTime, int IsActive, String Export_TotDiameter, String QuoNo, int QuotatioEntryFlag) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.EXPORTID, ExportID);
            values.put(Common.EXPORTUNIQUEID, ExportUniqueID);
            values.put(Common.ORDERNO, OrderNo);
            values.put(Common.CONTAINERNO, ContainerNo);
            values.put(Common.LOCATIONID, ToLocationID);
            values.put(Common.IMEINumber, IMEI);
            values.put(Common.QUTWOODSPECODE, QutWoodSpieceCode);
            values.put(Common.USERID, UserID);
            values.put(Common.QUTTOTALCBM, TotCBM);
            values.put(Common.DATETIME, DateTime);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.QUTDIAMETER, Export_TotDiameter);
            values.put(Common.QUOTATIONNO, QuoNo);
            /*version 6.0*/
            values.put(Common.QUOTATIONNOFLAG, QuotatioEntryFlag);
            mDatabase.insert(Common.TBL_EXPORTQUOTATIONLIST, null, values);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertExportDetailsResult", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public String getLastExportQuotationList() {
        String QuotattionID = "";
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(QuotationID) as QuotationID FROM " + Common.TBL_EXPORTQUOTATIONLIST;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                QuotattionID = (cursor.getString(cursor.getColumnIndex("QuotationID")));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getLastExportQuotationList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return QuotattionID;
    }

    /*Unique ID Update*/
    public boolean UpdateExportQuotationList(String QuotationID, String QuotationUniqueID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_EXPORTQUOTATIONLIST + " SET "
                    + Common.QUOTATIONUNIQUENO + " = '" + QuotationUniqueID + "'"
                    + " WHERE " + Common.QUOTATIONID + " = '" + QuotationID + "'";
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateExportQuotationList", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    /*Quotationlist Update*/
    public boolean UpdateQuotationList(String QUOTATIONUNIQUENO, String Selected_QutWsCode, String Selected_QutDiameter, String Selected_QutTotCBM) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_EXPORTQUOTATIONLIST + " SET "
                    + Common.QUTWOODSPECODE + " = '" + Selected_QutWsCode + "' ,"
                    + Common.QUTDIAMETER + " = '" + Selected_QutDiameter + "' ,"
                    + Common.QUTTOTALCBM + " = '" + Selected_QutTotCBM + "' "
                    + " WHERE " + Common.QUOTATIONUNIQUENO + " = '" + QUOTATIONUNIQUENO + "'";
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateExportQuotationList", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteQuotationListFromList(String QuotationUniqueID, int QuoID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "DELETE FROM " + Common.TBL_EXPORTQUOTATIONLIST + " WHERE " + Common.QUOTATIONID + " ='" + QuoID
                    + "' and " + Common.QUOTATIONUNIQUENO + " = '" + QuotationUniqueID + "'";
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateExportQuotationList", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeleteQuotationListFromExportList(int ExportID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "DELETE FROM " + Common.TBL_EXPORTQUOTATIONLIST + " WHERE " + Common.QUOTATIONID + " =" + ExportID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateExportQuotationList", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    // 7-12-2019 Added
    public ArrayList<ContainerListModel> getContainerList(String quotationNo, int exportID) {
        ArrayList<ContainerListModel> containerListModels = new ArrayList<ContainerListModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTCONTAINERLIST
                    + " WHERE " + Common.QUOTATIONNO + " = '" + quotationNo
                    + "' and " + Common.EXPORTID + "=" + exportID
                    + " and " + Common.ISACTIVE + "=1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                // ContainerListModel(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)),cursor.getInt(cursor.getColumnIndex(Common.EXPORTID)))
                containerTableDataModel = new ContainerListModel();
                containerTableDataModel.setContainerNumber(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)));
                containerTableDataModel.setExportID(cursor.getInt(cursor.getColumnIndex(Common.EXPORTID)));
                containerTableDataModel.setTotalVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                containerTableDataModel.setTotalCount(cursor.getInt(cursor.getColumnIndex(Common.COUNT)));
                containerListModels.add(containerTableDataModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getContainerData===", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return containerListModels;
    }

    public ArrayList<ContainerListModel> getContainerListBeforeCreate(String quotationNo, int exportID) {
        ArrayList<ContainerListModel> containerListModels = new ArrayList<ContainerListModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTCONTAINERLIST
                    + " WHERE " + Common.QUOTATIONNO + " = '" + quotationNo
                    + "' and " + Common.EXPORTID + "=" + exportID;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                // ContainerListModel(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)),cursor.getInt(cursor.getColumnIndex(Common.EXPORTID)))
                containerTableDataModel = new ContainerListModel();
                containerTableDataModel.setContainerNumber(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)));
                containerTableDataModel.setExportID(cursor.getInt(cursor.getColumnIndex(Common.EXPORTID)));
                containerTableDataModel.setTotalVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                containerTableDataModel.setTotalCount(cursor.getInt(cursor.getColumnIndex(Common.COUNT)));
                containerListModels.add(containerTableDataModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getContainerListBeforeCreate", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return containerListModels;
    }

    public ArrayList<ContainerListInputModel> getContainerListInput(String quotationNo, int exportID) {
        ArrayList<ContainerListInputModel> containerListModels = new ArrayList<ContainerListInputModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTCONTAINERLIST + " WHERE " + Common.QUOTATIONNO + " = '" + quotationNo + "' and " + Common.EXPORTID + "=" + exportID;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                // ContainerListModel(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)),cursor.getInt(cursor.getColumnIndex(Common.EXPORTID)))
                containerListInputModel = new ContainerListInputModel();
                if (cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)) > 0) {
                    containerListInputModel.setContainerNo(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)));
                    containerListInputModel.setQuotationNo(cursor.getString(cursor.getColumnIndex(Common.QUOTATIONNO)));
                    containerListInputModel.setTotalVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                    containerListInputModel.setTotalLogCount(cursor.getInt(cursor.getColumnIndex(Common.COUNT)));
                    containerListModels.add(containerListInputModel);
                }
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getContainerListInput", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return containerListModels;

    }

    public boolean RemoveContainerList(String containerNO, int exportID) {
        boolean UpdateFlag = false;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "UPDATE " + Common.TBL_EXPORTCONTAINERLIST
                    + " SET " + Common.ISACTIVE + "=0"
                    + " WHERE " + Common.CONTAINERNO + "='" + containerNO
                    + "' and " + Common.EXPORTID + "=" + exportID;

            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
            }
            UpdateFlag = true;
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateContainerList===", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return UpdateFlag;
    }

    // 19-12-2019 added
    public int getTotalCountCBMForExportQuotation(String ExportID, String QuotaNo, String QuotUniqueID) {
        int TotCount = 0;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        String selectQuery = "";
        try {
            /*selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where "
                    + Common.QUOTATIONNO + " ='" + QuotaNo + "'"
                    + " and " + Common.EXPORTID + " ='" + ExportID + "'"
                    + " and (" + Common.QUTWOODSPECODE + " = '" + WSC + "' or " + Common.WoodSPiceCode + " = '" + WSC + "')"
                    + " and " + Common.QUTDIAMETER + " ='" + DIAMETER + "'"
                    + " and " + Common.QUTTOTALCBM + " ='" + TOTCBM + "'"
                    + " and " + Common.ISACTIVE + " = " + 1;*/
            selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where "
                    + Common.QUOTATIONNO + " ='" + QuotaNo + "'"
                    + " and " + Common.EXPORTID + " ='" + ExportID + "'"
                    + " and " + Common.QUOTATIONUNIQUENO + " ='" + QuotUniqueID + "'"
                    + " and " + Common.ISACTIVE + " = " + 1;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotCount = TotCount + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "TotalScannedCBMForExportQuotation", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotCount;
    }

    public double getTotalScannedCBMForExportQuotation(String ExportID, String QuotaNo, String QuotUniqueID) {
        double TotScannedCBM = 0.00;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        String selectQuery = "";
        try {
           /* selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where "
                    + Common.QUOTATIONNO + " ='" + QuotaNo + "'"
                    + " and " + Common.EXPORTID + " ='" + ExportID + "'"
                    + " and (" + Common.QUTWOODSPECODE + " = '" + WSC + "' or " + Common.WoodSPiceCode + " = '" + WSC + "')"
                    + " and " + Common.QUTDIAMETER + " ='" + DIAMETER + "'"
                    + " and " + Common.QUTTOTALCBM + " ='" + TOTCBM + "'"
                    + " and " + Common.ISACTIVE + " = " + 1;*/
            selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where "
                    + Common.QUOTATIONNO + " ='" + QuotaNo + "'"
                    + " and " + Common.EXPORTID + " ='" + ExportID + "'"
                    + " and " + Common.QUOTATIONUNIQUENO + " ='" + QuotUniqueID + "'"
                    + " and " + Common.ISACTIVE + " = " + 1;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotScannedCBM = TotScannedCBM + parseDouble(cursor.getString(cursor.getColumnIndex(Common.VOLUME)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "TotalScannedCBMForExportQuotation", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotScannedCBM;
    }

    public static int booleanToInt(boolean value) {
        // Convert true to 1 and false to 0.
        return value ? 1 : 0;
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(mContext, Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    /*Update Table*/
    public boolean UpdateTruckPlateNumber(int receivedID, String BarCode, String CheckedValue) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYRECEIVED + " SET "
                    + Common.ISCHECKED + " = '" + CheckedValue + "'"
                    + " WHERE " + Common.RECEIVEDID + " = " + receivedID + " and " + Common.BARCODE + " ='" + BarCode + "'";
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedItems", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public double TotalVolumeForExportForContainer(int ExportId, String CotainerNo) {
        double TotVolume = 0.00;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        String selectQuery = "";
        try {
            selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " WHERE " + Common.EXPORTID + " =" + ExportId
                    + " and " + Common.CONTAINERNO + " ='" + CotainerNo + "'"
                    + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotVolume = TotVolume + parseDouble(cursor.getString(cursor.getColumnIndex(Common.VOLUME)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "TotalVolumeForExportForContainer", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotVolume;
    }

    public int TotalCountForExportForContainer(int ExportId, String CotainerNo) {
        int TotCount = 0;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        String selectQuery = "";
        try {
            selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " WHERE " + Common.EXPORTID + " =" + ExportId
                    + " and " + Common.CONTAINERNO + " ='" + CotainerNo + "'"
                    + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotCount = TotCount + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "TotalCountForExportForContainer", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotCount;
    }

    /*External Purchase*/
    public int getPurchaseLogsRowID() {
        int LastIndex = 0;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLastWoodSpieceID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }


    public boolean getPurchasePurcahseListIDCheck(int purchaseID, String PurchaseNo, String DaimeteRan, String WSC) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_EXTERNALDBPURCHASEAGREEMENT + " where " + Common.Purchase.PurchaseListid + "=" + PurchaseListid;
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_EXTERNALDBPURCHASEAGREEMENT + " where "
                    + Common.Purchase.PurchaseId + "=" + purchaseID + " AND "
                    + Common.Purchase.PurchaseNo + "='" + PurchaseNo + "' AND "
                    + "WoodSpieceCode" + "='" + WSC + "' AND "
                    + Common.Purchase.DiameterRange + "='" + DaimeteRan + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getPurchasePurcahseListIDCheck", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public void insertInternalPurchaseAgreementREPLACEIB(ArrayList<PurchaseAgreementModel> agreementSync) {
        mDatabase = this.getWritableDatabase();
        if (null != mDatabase) {
            try {
                for (int i = 0; i < agreementSync.size(); i++) {
                    boolean Result = false;
                    String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_EXTERNALDBPURCHASEAGREEMENT + " where "
                            + Common.Purchase.PurchaseId + "=" + agreementSync.get(i).getPurchaseId() + " AND "
                            + Common.Purchase.PurchaseNo + "='" + agreementSync.get(i).getPurchaseNo() + "' AND "
                            + "WoodSpieceCode" + "='" + agreementSync.get(i).getWoodSpeciesCode() + "' AND "
                            + Common.Purchase.DiameterRange + "='" + agreementSync.get(i).getDiameterRange() + "'";
                    Cursor cursor = mDatabase.rawQuery(selectQuery, null);
                    while (cursor.moveToNext()) {
                        Result = true;
                    }
                    if (Result == false) {
                        ContentValues values = new ContentValues();
                        values.put(Common.Purchase.PurchaseId, agreementSync.get(i).getPurchaseId());
                        values.put(Common.Purchase.PurchaseNo, agreementSync.get(i).getPurchaseNo());
                        values.put(Common.Purchase.DiameterRange, agreementSync.get(i).getDiameterRange());
                        values.put("WoodSpieceCode", agreementSync.get(i).getWoodSpeciesCode());
                        values.put(Common.WoodSpiceID, agreementSync.get(i).getWoodSpecieId());
                        values.put(Common.Purchase.ValidUntil, agreementSync.get(i).getValidUntil());
                        values.put(Common.Purchase.StatusID, agreementSync.get(i).getStatusId());
                        values.put(Common.Purchase.PurchaseListid, agreementSync.get(i).getPurchaseListid());
                        mDatabase.insert(Common.ExternalDataBaseClass.TBL_EXTERNALDBPURCHASEAGREEMENT, null, values);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertPurchaseAgreementREPLACE", e.toString(), false);
            } finally {
                mDatabase.close();
            }
        }
    }

    public ArrayList<PurchaseNoAgreementModel> getPurchaseNoAgreement() {
        ArrayList<PurchaseNoAgreementModel> labels = new ArrayList<PurchaseNoAgreementModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT PurchaseId,PurchaseNo,SyncStatus FROM "
                    + Common.ExternalDataBaseClass.TBL_EXTERNALDBPURCHASEAGREEMENT + " where " + Common.Purchase.StatusID + "='" + 1 + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                purchaseNoModel = new PurchaseNoAgreementModel();
                purchaseNoModel.setPurchaseId(cursor.getInt(cursor.getColumnIndex(Common.Purchase.PurchaseId)));
                purchaseNoModel.setPurchaseNo(cursor.getString(cursor.getColumnIndex(Common.Purchase.PurchaseNo)));
                purchaseNoModel.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Common.SYNCSTATUS)));
                /*purchaseNoModel.setTSyncStatus(cursor.getInt(cursor.getColumnIndex(Common.Purchase.TransferStatus)));
                purchaseNoModel.setRSyncStatus(cursor.getInt(cursor.getColumnIndex(Common.Purchase.ReceivedStatus)));
                purchaseNoModel.setTransferAgencyID(cursor.getInt(cursor.getColumnIndex(Common.TRANSFERAGENCY)));
                purchaseNoModel.setTDriverID(cursor.getInt(cursor.getColumnIndex(Common.TRANSFERDRIVERID)));
                purchaseNoModel.setTTruckId(cursor.getInt(cursor.getColumnIndex(Common.TRANSFERTRUCKID)));
                purchaseNoModel.setRAgencyID(cursor.getInt(cursor.getColumnIndex(Common.RECEIVEDAGENCYID)));
                purchaseNoModel.setRDriverID(cursor.getInt(cursor.getColumnIndex(Common.RECEIVEDDRIVERID)));
                purchaseNoModel.setRTruckId(cursor.getInt(cursor.getColumnIndex(Common.RECEIVEDTRUCKID)));
                purchaseNoModel.setTToLocationID(cursor.getInt(cursor.getColumnIndex(Common.Purchase.TTOLOCATIONID)));
                purchaseNoModel.setTFromLocationID(cursor.getInt(cursor.getColumnIndex(Common.Purchase.TFROMLOCATIONID)));
                purchaseNoModel.setRToLocationID(cursor.getInt(cursor.getColumnIndex(Common.Purchase.RTOLOCATIONID)));
                purchaseNoModel.setRFromLocationID(cursor.getInt(cursor.getColumnIndex(Common.Purchase.RFROMLOCATIONID)));
                purchaseNoModel.setTTransportMode(cursor.getInt(cursor.getColumnIndex(Common.Purchase.TTRANSPORTMODE)));
                purchaseNoModel.setRTransportMode(cursor.getInt(cursor.getColumnIndex(Common.Purchase.RTRANSPORTMODE)));
                purchaseNoModel.setTLoadedby(cursor.getInt(cursor.getColumnIndex(Common.Purchase.TLOADEDBY)));
                purchaseNoModel.setRLoadedby(cursor.getInt(cursor.getColumnIndex(Common.Purchase.RLOADEDBY)));

                purchaseNoModel.setTStartDateTime(cursor.getString(cursor.getColumnIndex(Common.Purchase.TSTARTDATETIME)));
                purchaseNoModel.setTEndDateTime(cursor.getString(cursor.getColumnIndex(Common.Purchase.TENDDATETIME)));
                purchaseNoModel.setRStartDateTime(cursor.getString(cursor.getColumnIndex(Common.Purchase.RSTARTDATETIME)));
                purchaseNoModel.setREndDateTime(cursor.getString(cursor.getColumnIndex(Common.Purchase.RENDDATETIME)));*/
                labels.add(purchaseNoModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getPurchaseNoAgreement", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            mDatabase.close();
        }
        return labels;
    }

    public ArrayList<String> getExternalPurchaseAgreementWSC(int SelectedPurchaseID) {
        ArrayList<String> labels = new ArrayList<>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT WoodSpieceCode FROM " + Common.ExternalDataBaseClass.TBL_EXTERNALDBPURCHASEAGREEMENT + " where " + Common.Purchase.PurchaseId + "=" + SelectedPurchaseID;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                labels.add(cursor.getString(cursor.getColumnIndex("WoodSpieceCode")));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getExternalPurchaseAgreement", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            mDatabase.close();
        }
        return labels;
    }

    public ArrayList<PurchaseAgreementModel> getExternalPurchaseAgreement(int SelectedPurchaseID) {
        ArrayList<PurchaseAgreementModel> labels = new ArrayList<>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT WoodSpieceCode,DiameterRange FROM " + Common.ExternalDataBaseClass.TBL_EXTERNALDBPURCHASEAGREEMENT + " where " + Common.Purchase.PurchaseId + "=" + SelectedPurchaseID;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                purchaseAgreementModel = new PurchaseAgreementModel();
                purchaseAgreementModel.setWoodSpeciesCode(cursor.getString(cursor.getColumnIndex("WoodSpieceCode")));
                purchaseAgreementModel.setDiameterRange(cursor.getString(cursor.getColumnIndex("DiameterRange")));
                labels.add(purchaseAgreementModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getExternalPurchaseAgreement", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            mDatabase.close();
        }
        return labels;
    }

    public boolean UpdateExternalPurchaseAgreementCount(int SelectedPurchaseID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "";// "UPDATE" + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " SET " + Common.ISACTIVE + " = '" + IsActiveValue + "'where " + Common.BARCODE + "='" + Barcode + "' and " + Common.Purchase.PurchaseId + "=" + PurchaseId;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public ArrayList<PurchaseLogsModels> getPurchaseLogsDetails(int purchaseIDStr) {
        ArrayList<PurchaseLogsModels> scannedReusltList = new ArrayList<>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        int Rowid = 1;
        try {
            String selectQuery = "SELECT  * FROM " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " WHERE " + Common.Purchase.PurchaseId + " = " + purchaseIDStr + " and " + Common.ISACTIVE + "=" + 1;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                purchaseLogsModel = new PurchaseLogsModels();
                purchaseLogsModel.setRowId(Rowid);
                purchaseLogsModel.setWoodSpeciesId(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                purchaseLogsModel.setWoodSpeciesCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                purchaseLogsModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QULAITY)))) {
                    purchaseLogsModel.setQuality("");
                } else {
                    purchaseLogsModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                }
                purchaseLogsModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                purchaseLogsModel.setPurchaseId(cursor.getInt(cursor.getColumnIndex(Common.Purchase.PurchaseId)));
                purchaseLogsModel.setPurchaseNo(cursor.getString(cursor.getColumnIndex(Common.Purchase.PurchaseNo)));
                purchaseLogsModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                purchaseLogsModel.setEntryMode(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                purchaseLogsModel.setF1(cursor.getDouble(cursor.getColumnIndex(Common.DF1)));
                purchaseLogsModel.setF2(cursor.getDouble(cursor.getColumnIndex(Common.DF2)));
                purchaseLogsModel.setT1(cursor.getDouble(cursor.getColumnIndex(Common.DT1)));
                purchaseLogsModel.setT2(cursor.getDouble(cursor.getColumnIndex(Common.DT2)));
                purchaseLogsModel.setLength_dm(cursor.getDouble(cursor.getColumnIndex(Common.LENGTH)));
                purchaseLogsModel.setNoteF(cursor.getDouble(cursor.getColumnIndex(Common.NOTEF)));
                purchaseLogsModel.setNoteT(cursor.getDouble(cursor.getColumnIndex(Common.NOTET)));
                purchaseLogsModel.setNoteL(cursor.getDouble(cursor.getColumnIndex(Common.NOTEL)));

                purchaseLogsModel.setLHF1(cursor.getDouble(cursor.getColumnIndex(Common.LHF1)));
                purchaseLogsModel.setLHF2(cursor.getDouble(cursor.getColumnIndex(Common.LHF2)));
                purchaseLogsModel.setLHT1(cursor.getDouble(cursor.getColumnIndex(Common.LHT1)));
                purchaseLogsModel.setLHT2(cursor.getDouble(cursor.getColumnIndex(Common.LHT2)));
                purchaseLogsModel.setLHVolume(cursor.getDouble(cursor.getColumnIndex(Common.LHVOLUME)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.TREEPARTTYPE)))) {
                    purchaseLogsModel.setTreePartType("");
                } else {
                    purchaseLogsModel.setTreePartType(cursor.getString(cursor.getColumnIndex(Common.TREEPARTTYPE)));
                }
                purchaseLogsModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                purchaseLogsModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                // remesurements
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.REMARKS)))) {
                    purchaseLogsModel.setRemarks("");
                } else {
                    purchaseLogsModel.setRemarks(cursor.getString(cursor.getColumnIndex(Common.REMARKS)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.REMARKSTYPE)))) {
                    purchaseLogsModel.setRemarksType("");
                } else {
                    purchaseLogsModel.setRemarksType(cursor.getString(cursor.getColumnIndex(Common.REMARKSTYPE)));
                }

                purchaseLogsModel.setLengthCutFoot(cursor.getDouble(cursor.getColumnIndex(Common.LENGTHCUTFOOT)));
                purchaseLogsModel.setLengthCutTop(cursor.getDouble(cursor.getColumnIndex(Common.LENGTHCUTTOP)));

                purchaseLogsModel.setCrackFoot1(cursor.getDouble(cursor.getColumnIndex(Common.CRACKF1)));
                purchaseLogsModel.setCrackFoot2(cursor.getDouble(cursor.getColumnIndex(Common.CRACKF2)));
                purchaseLogsModel.setCrackTop1(cursor.getDouble(cursor.getColumnIndex(Common.CRACKT1)));
                purchaseLogsModel.setCrackTop2(cursor.getDouble(cursor.getColumnIndex(Common.CRACKT2)));
                purchaseLogsModel.setCrackVolume(cursor.getDouble(cursor.getColumnIndex(Common.CRACKVOLUME)));
                purchaseLogsModel.setHoleFoot1(cursor.getDouble(cursor.getColumnIndex(Common.HOLEF1)));
                purchaseLogsModel.setHoleFoot2(cursor.getDouble(cursor.getColumnIndex(Common.HOLEF2)));
                purchaseLogsModel.setHoleTop1(cursor.getDouble(cursor.getColumnIndex(Common.HOLET1)));
                purchaseLogsModel.setHoleTop2(cursor.getDouble(cursor.getColumnIndex(Common.HOLET2)));
                purchaseLogsModel.setHoleVolume(cursor.getDouble(cursor.getColumnIndex(Common.HOLEVOLUME)));
                purchaseLogsModel.setSapDeduction(cursor.getDouble(cursor.getColumnIndex(Common.SAPDEDUCTION)));
                scannedReusltList.add(purchaseLogsModel);
                Rowid = Rowid + 1;
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public ArrayList<PurchaseLogsSyncModel> getPurchaseLogsSyncDetails(int purchaseIDStr) {
        ArrayList<PurchaseLogsSyncModel> scannedReusltList = new ArrayList<>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        int Rowid = 1;
        try {
            String selectQuery = "SELECT  * FROM " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " WHERE " + Common.Purchase.PurchaseId + " = " + purchaseIDStr + " and " + Common.ISACTIVE + "=" + 1;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                purchaseLogssyncModel = new PurchaseLogsSyncModel();
                purchaseLogssyncModel.setRowId(Rowid);
                purchaseLogssyncModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                purchaseLogssyncModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                purchaseLogssyncModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QULAITY)))) {
                    purchaseLogssyncModel.setQuality(" ");
                } else {
                    purchaseLogssyncModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                }
                purchaseLogssyncModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                purchaseLogssyncModel.setPurchaseId(cursor.getInt(cursor.getColumnIndex(Common.Purchase.PurchaseId)));
                purchaseLogssyncModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                purchaseLogssyncModel.setEntryMode(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                purchaseLogssyncModel.setFooter_1(cursor.getDouble(cursor.getColumnIndex(Common.DF1)));
                purchaseLogssyncModel.setFooter_2(cursor.getDouble(cursor.getColumnIndex(Common.DF2)));
                purchaseLogssyncModel.setTop_1(cursor.getDouble(cursor.getColumnIndex(Common.DT1)));
                purchaseLogssyncModel.setTop_2(cursor.getDouble(cursor.getColumnIndex(Common.DT2)));
                purchaseLogssyncModel.setLength(cursor.getDouble(cursor.getColumnIndex(Common.LENGTH)));
                purchaseLogssyncModel.setNoteF(cursor.getDouble(cursor.getColumnIndex(Common.NOTEF)));
                purchaseLogssyncModel.setNoteT(cursor.getDouble(cursor.getColumnIndex(Common.NOTET)));
                purchaseLogssyncModel.setNoteL(cursor.getDouble(cursor.getColumnIndex(Common.NOTEL)));

                purchaseLogssyncModel.setLHF1(cursor.getDouble(cursor.getColumnIndex(Common.LHF1)));
                purchaseLogssyncModel.setLHF2(cursor.getDouble(cursor.getColumnIndex(Common.LHF2)));
                purchaseLogssyncModel.setLHT1(cursor.getDouble(cursor.getColumnIndex(Common.LHT1)));
                purchaseLogssyncModel.setLHT2(cursor.getDouble(cursor.getColumnIndex(Common.LHT2)));
                purchaseLogssyncModel.setLHVolume(cursor.getDouble(cursor.getColumnIndex(Common.LHVOLUME)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.TREEPARTTYPE)))) {
                    purchaseLogssyncModel.setTreePartType(" ");
                } else {
                    purchaseLogssyncModel.setTreePartType(cursor.getString(cursor.getColumnIndex(Common.TREEPARTTYPE)));
                }
                purchaseLogssyncModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                purchaseLogssyncModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));

                // remesurements
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.REMARKS)))) {
                    purchaseLogssyncModel.setRemarks("");
                } else {
                    purchaseLogssyncModel.setRemarks(cursor.getString(cursor.getColumnIndex(Common.REMARKS)));
                }
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.REMARKSTYPE)))) {
                    purchaseLogssyncModel.setRemarksType("");
                } else {
                    if (cursor.getString(cursor.getColumnIndex(Common.REMARKSTYPE)).contains("@")) {
                        String[] parts = cursor.getString(cursor.getColumnIndex(Common.REMARKSTYPE)).split("@");
                        if (parts.length > 0) {
                            String part1 = parts[0];
                            purchaseLogssyncModel.setRemarksType(part1);
                        }else{
                            purchaseLogssyncModel.setRemarksType("");
                        }
                    } else {
                        purchaseLogssyncModel.setRemarksType("");
                    }
                }
                purchaseLogssyncModel.setLengthCutFoot(cursor.getDouble(cursor.getColumnIndex(Common.LENGTHCUTFOOT)));
                purchaseLogssyncModel.setLengthCutTop(cursor.getDouble(cursor.getColumnIndex(Common.LENGTHCUTTOP)));

                purchaseLogssyncModel.setCrackFoot1(cursor.getDouble(cursor.getColumnIndex(Common.CRACKF1)));
                purchaseLogssyncModel.setCrackFoot2(cursor.getDouble(cursor.getColumnIndex(Common.CRACKF2)));
                purchaseLogssyncModel.setCrackTop1(cursor.getDouble(cursor.getColumnIndex(Common.CRACKT1)));
                purchaseLogssyncModel.setCrackTop2(cursor.getDouble(cursor.getColumnIndex(Common.CRACKT2)));
                purchaseLogssyncModel.setCrackVolume(cursor.getDouble(cursor.getColumnIndex(Common.CRACKVOLUME)));
                purchaseLogssyncModel.setHoleFoot1(cursor.getDouble(cursor.getColumnIndex(Common.HOLEF1)));
                purchaseLogssyncModel.setHoleFoot2(cursor.getDouble(cursor.getColumnIndex(Common.HOLEF2)));
                purchaseLogssyncModel.setHoleTop1(cursor.getDouble(cursor.getColumnIndex(Common.HOLET1)));
                purchaseLogssyncModel.setHoleTop2(cursor.getDouble(cursor.getColumnIndex(Common.HOLET2)));
                purchaseLogssyncModel.setHoleVolume(cursor.getDouble(cursor.getColumnIndex(Common.HOLEVOLUME)));
                purchaseLogssyncModel.setSapDeduction(cursor.getDouble(cursor.getColumnIndex(Common.SAPDEDUCTION)));
                scannedReusltList.add(purchaseLogssyncModel);
                Rowid = Rowid + 1;
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public boolean insertPurchaseLogsDetails(int PurcahseID, String PurcahseNo, int UserID, String TreeNumber, String WSpecCode, String WSCID,
                                             String Quality, int entryMode, String barcode, String Df1, String Df2, String Dt1, String Dt2, String length, String noteF,
                                             String noteT, String noteL, String volume, String treePartType, int IsActive, String lhf1, String lhf2, String lht1, String lht2,
                                             String lhVolume, String updatedDate, String updatedBy) {
        boolean Result = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.Purchase.PurchaseId, PurcahseID);
            values.put(Common.Purchase.PurchaseNo, PurcahseNo);
            values.put(Common.USERID, UserID);
            values.put(Common.TREENO, TreeNumber);
            values.put(Common.WoodSPiceCode, WSpecCode);
            values.put(Common.WoodSpiceID, WSCID);
            values.put(Common.QULAITY, Quality);
            values.put(Common.ENTRYMODE, entryMode);
            values.put(Common.BARCODE, barcode);
            values.put(Common.DF1, Df1);
            values.put(Common.DF2, Df2);
            values.put(Common.DT1, Dt1);
            values.put(Common.DT2, Dt2);
            values.put(Common.LENGTH, length);
            values.put(Common.NOTEF, noteF);
            values.put(Common.NOTET, noteT);
            values.put(Common.NOTEL, noteL);
            values.put(Common.VOLUME, volume);
            values.put(Common.TREEPARTTYPE, treePartType);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.LHF1, lhf1);
            values.put(Common.LHF2, lhf2);
            values.put(Common.LHT1, lht1);
            values.put(Common.LHT2, lht2);
            values.put(Common.LHVOLUME, lhVolume);
            values.put(Common.Purchase.UpdatedDate, updatedDate);
            values.put(Common.Purchase.UpdatedBy, updatedBy);

            mDatabase.insert(Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, null, values);
            mDatabase.setTransactionSuccessful();
            Result = true;
        } catch (Exception e) {
            e.printStackTrace();
            Result = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Result;
    }

    public boolean externalPurchadeBarcodeValidation(String Barcode) {
        boolean status = false;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT count(*) as BarcodeValidation FROM " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " where " + Common.BARCODE + " = " + "'" + Barcode + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                int statusFlag = (cursor.getInt(cursor.getColumnIndex("BarcodeValidation")));
                if (statusFlag > 0) {
                    status = true;
                }
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getTransferScannedResultInputWithVBBNo", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return status;
    }

    public boolean updatePurchaseLogsDetails(int PurcahseID, String barcode, String Df1, String Df2, String Dt1, String Dt2, String length, String noteF,
                                             String noteT, String noteL, String volume, String lhf1, String lhf2, String lht1, String lht2,
                                             String lhVolume, String remarks, String remarksType, String lengthcutfoot, String lengthcuttop,
                                             String crackF1, String crackF2, String crackT1, String crackT2, String crackvolume,
                                             String holeF1, String holeF2, String holeT1, String holeT2, String holevolume, String sapdeduction) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " SET "
                    + Common.DF1 + " = '" + Df1 + "' , "
                    + Common.DF2 + " = '" + Df2 + "' , "
                    + Common.DT1 + " = '" + Dt1 + "' , "
                    + Common.DT2 + " = '" + Dt2 + "' , "
                    + Common.LENGTH + " = '" + length + "' , "
                    + Common.NOTEF + " = '" + noteF + "' , "
                    + Common.NOTET + " = '" + noteT + "' , "
                    + Common.NOTEL + " = '" + noteL + "' , "
                    + Common.VOLUME + " = '" + volume + "' , "
                    + Common.LHT1 + " = '" + lhf1 + "' , "
                    + Common.LHT2 + " = '" + lhf2 + "' , "
                    + Common.LHF1 + " = '" + lht1 + "' , "
                    + Common.LHF2 + " = '" + lht2 + "' , "
                    + Common.LHVOLUME + " = '" + lhVolume + "' , "
                    + Common.REMARKS + " = '" + remarks + "' , "
                    + Common.REMARKSTYPE + " = '" + remarksType + "' , "
                    + Common.LENGTHCUTFOOT + " = '" + lengthcutfoot + "' , "
                    + Common.LENGTHCUTTOP + " = '" + lengthcuttop + "' , "
                    + Common.CRACKF1 + " = '" + crackF1 + "' , "
                    + Common.CRACKF2 + " = '" + crackF2 + "' , "
                    + Common.CRACKT1 + " = '" + crackT1 + "' , "
                    + Common.CRACKT2 + " = '" + crackT2 + "' , "
                    + Common.CRACKVOLUME + " = '" + crackvolume + "' , "
                    + Common.HOLEF1 + " = '" + holeF1 + "' , "
                    + Common.HOLEF2 + " = '" + holeF2 + "' , "
                    + Common.HOLET1 + " = '" + holeT1 + "' , "
                    + Common.HOLET2 + " = '" + holeT2 + "' , "
                    + Common.HOLEVOLUME + " = '" + holevolume + "' , "
                    + Common.SAPDEDUCTION + " = '" + sapdeduction + "' " +
                    " WHERE " + Common.BARCODE + " = '" + barcode + "' and " + Common.Purchase.PurchaseId + "=" + PurcahseID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateQualityTansferList", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean RemovePurchaseLogs(String Barcode, int IsActiveValue, int PurchaseId) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " SET " + Common.ISACTIVE + " = '" + IsActiveValue + "' where " + Common.BARCODE + "='" + Barcode + "' and " + Common.Purchase.PurchaseId + "=" + PurchaseId;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdatePurchaseAgreementStatus(int SyncStatus) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASEAGREEMENT + " SET "
                    + Common.SYNCSTATUS + " = " + SyncStatus + " WHERE "
                    + Common.Purchase.PurchaseId + " = " + Common.Purchase.SelectedPurchaseId;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedSyncStatusReceivedID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdatePurchaseAgreementTransferStatus(int SyncStatus) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASEAGREEMENT + " SET "
                    + " TSyncStatus " + SyncStatus + " WHERE "
                    + Common.Purchase.PurchaseId + " = " + Common.Purchase.SelectedPurchaseId;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdatePurchaseAgreementTransferStatus", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdatePurchaseAgreementReceivedStatus(int SyncStatus) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASEAGREEMENT + " SET "
                    + " RSyncStatus " + SyncStatus + " WHERE "
                    + Common.Purchase.PurchaseId + " = " + Common.Purchase.SelectedPurchaseId;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdatePurchaseAgreementReceivedStatus", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdatePurchaseAgreementTransferHeader(int loadedID, int FromLocID, int ToLocID, int agencyID, int driverID, int truckID, int purchaseID, String endDate, int TransportType) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASEAGREEMENT + " SET "
                    + Common.Purchase.TLOADEDBY + " = '" + loadedID + "' , "
                    + Common.Purchase.TFROMLOCATIONID + " = '" + FromLocID + "' , "
                    + Common.Purchase.TTOLOCATIONID + " = '" + ToLocID + "' , "
                    + Common.Purchase.TENDDATETIME + " = '" + endDate + "' , "
                    + Common.Purchase.TTRANSPORTMODE + " = '" + TransportType + "' , "
                    + Common.TRANSFERAGENCY + " = '" + agencyID + "' , "
                    + Common.TRANSFERDRIVERID + " = '" + driverID + "' , "
                    + Common.TRANSFERTRUCKID + " = '" + truckID + "' WHERE "
                    + Common.Purchase.PurchaseId + " = " + purchaseID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedSyncStatusReceivedID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdatePurchaseAgreementTransferStartDate(String StartDate, int purchaseID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASEAGREEMENT + " SET "
                    + Common.Purchase.TSTARTDATETIME + " = '" + StartDate + "' WHERE "
                    + Common.Purchase.PurchaseId + " = " + purchaseID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedSyncStatusReceivedID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdatePurchaseAgreementReceivedStartDate(String StartDate, int purchaseID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASEAGREEMENT + " SET "
                    + Common.Purchase.RSTARTDATETIME + " = '" + StartDate + "' WHERE "
                    + Common.Purchase.PurchaseId + " = " + purchaseID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedSyncStatusReceivedID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdatePurchaseAgreementReceivedHeader(int loadedID, int FromLocID, int ToLocID, int agencyID, int driverID, int truckID, int purchaseID, String endDate, int TransportType) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASEAGREEMENT + " SET "
                    + Common.Purchase.RLOADEDBY + " = '" + loadedID + "' , "
                    + Common.Purchase.RFROMLOCATIONID + " = '" + FromLocID + "' , "
                    + Common.Purchase.RTOLOCATIONID + " = '" + ToLocID + "' , "
                    + Common.Purchase.RENDDATETIME + " = '" + endDate + "' , "
                    + Common.Purchase.RTRANSPORTMODE + " = '" + TransportType + "' , "
                    + Common.RECEIVEDAGENCYID + " = '" + agencyID + "' , "
                    + Common.RECEIVEDDRIVERID + " = '" + driverID + "' , "
                    + Common.RECEIVEDTRUCKID + " = '" + truckID + "' WHERE "
                    + Common.Purchase.PurchaseId + " = " + purchaseID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryReceivedSyncStatusReceivedID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdatePurchaseTransferSyncStatus(String SyncTime, int SyncStatus, String purchaseTransUniqueID, int agencyID, int driverID, int TruckID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASETRANSFER_LIST + " SET "
                    + Common.SYNCTIME + " = '" + SyncTime + "' ,";
            if (agencyID != 0) {
                strSQL = strSQL + Common.TRANSFERAGENCYID + " = '" + agencyID + "' ,";
            }
            if (driverID != 0) {
                strSQL = strSQL + Common.DRIVERID + " = '" + driverID + "' ,";
            }
            if (TruckID != 0) {
                strSQL = strSQL + Common.TRUCKID + " = '" + TruckID + "' ,";
            }
            strSQL = strSQL + " SyncStatus = '" + SyncStatus + "' WHERE "
                    + Common.PURCHASETRANSFERUNIQUEID + " = " + purchaseTransUniqueID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryTransferSyncStatusTransID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdatePurchaseReceivedSyncStatus(String SyncTime, int SyncStatus, String purchaseReceiveUniqID, int agencyID, int driverID, int TruckID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASERECEIVED_LIST + " SET "
                    + Common.SYNCTIME + " = '" + SyncTime + "' ,";
            if (agencyID != 0) {
                strSQL = strSQL + Common.TRANSFERAGENCYID + " = '" + agencyID + "' ,";
            }
            if (driverID != 0) {
                strSQL = strSQL + Common.DRIVERID + " = '" + driverID + "' ,";
            }
            if (TruckID != 0) {
                strSQL = strSQL + Common.TRUCKID + " = '" + TruckID + "' ,";
            }
            strSQL = strSQL + " SyncStatus = '" + SyncStatus + "' WHERE "
                    + Common.PURCHASERECEIVEDUNIQUEID + " = " + purchaseReceiveUniqID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdatePurchaseReceivedSyncStatus", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    // Purchase Transfer
    public String getLastPurchaseTransferID() {
        String TransferID = "";
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(ID) as LastTansferID FROM " + Common.TBL_PURCHASETRANSFER_LIST;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TransferID = (cursor.getString(cursor.getColumnIndex("LastTansferID")));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getLastPurchaseTransferID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TransferID;
    }

    public boolean insertPurchaseTransferID(String IMEI, int ToLocationID, String StartDate, String EndDate,
                                            int FromLocationid, int TransportTypeID, int TransferAgencyID, int DriverID, int TruckID,
                                            int UserID, int Count, int SyncStatus, String SyncTime, String Volume, int Isactive, String transUniqueID, int LoadedBy) {
        boolean InsertFlag = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.Purchase.PurchaseId, Common.Purchase.SelectedPurchaseId);
            values.put(Common.Purchase.PurchaseNo, Common.Purchase.SelectedPurchaseNo);
            values.put(Common.IMEINumber, IMEI);
            values.put(Common.STARTDATETIME, StartDate);
            values.put(Common.ENDDATETIME, EndDate);
            values.put(Common.LOCATION_ID, ToLocationID);
            values.put(Common.FROMLOCATIONID, FromLocationid);
            values.put(Common.TRANSPORTTYPEID, TransportTypeID);
            values.put(Common.TRANSFERAGENCYID, TransferAgencyID);
            values.put(Common.DRIVERID, DriverID);
            values.put(Common.TRUCKID, TruckID);
            values.put(Common.USERID, UserID);
            values.put(Common.COUNT, Count);
            values.put(Common.ISACTIVE, Isactive);
            values.put(Common.PURCHASETRANSFERUNIQUEID, transUniqueID);
            values.put(Common.VOLUME, Volume);
            values.put(Common.LOADEDTYPE, LoadedBy);
            values.put(Common.SYNCSTATUS, SyncStatus);
            values.put(Common.SYNCTIME, SyncTime);
            long result = mDatabase.insert(Common.TBL_PURCHASETRANSFER_LIST, null, values);
            if (result == -1)
                InsertFlag = false;
            else
                InsertFlag = true;
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertInventoryTransferID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return InsertFlag;
    }

    public boolean UpdatePurchaseTransferUniqueID(int TransferID, String transUniqueID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASETRANSFER_LIST + " SET "
                    + Common.PURCHASETRANSFERUNIQUEID + " = '" + transUniqueID + "'" +
                    " WHERE ID = " + TransferID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdatePurchaseTransferUniqueID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public ArrayList<String> getPurchaseTransferDate(int purchaseID) {
        ArrayList<String> purchaseDateList = new ArrayList<>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT trim(substr(StartDateTime,1,11))as StartDateTime from " + Common.TBL_PURCHASETRANSFER_LIST
                    + " Where " + Common.Purchase.PurchaseId + "=" + purchaseID
                    + " ORDER by ID DESC";

            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                String SdateTime = cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME));
                String[] parts = SdateTime.split(" ");
                //Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(parts[0]);
                purchaseDateList.add(parts[0]);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return purchaseDateList;
    }

    public ArrayList<PurchaseTransferModel> getPurchaseTransferIdList(String SelectedDate, int PurchaseId) {
        ArrayList<PurchaseTransferModel> scannedReusltList = new ArrayList<>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_PURCHASETRANSFER_LIST + " WHERE substr(StartDateTime,1,11)='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1" + " and " + Common.Purchase.PurchaseId + " = " + PurchaseId;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                purchaseTransferModel = new PurchaseTransferModel();
                purchaseTransferModel.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                purchaseTransferModel.setIMEI(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                purchaseTransferModel.setToLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATION_ID)));
                purchaseTransferModel.setStartDateTime(cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME)));
                purchaseTransferModel.setEndDateTime(cursor.getString(cursor.getColumnIndex(Common.ENDDATETIME)));
                purchaseTransferModel.setFromLocationID(cursor.getInt(cursor.getColumnIndex(Common.FROMLOCATIONID)));
                purchaseTransferModel.setTransportTypeId(cursor.getInt(cursor.getColumnIndex(Common.TRANSPORTTYPEID)));
                purchaseTransferModel.setTransferAgencyID(cursor.getInt(cursor.getColumnIndex(Common.TRANSFERAGENCYID)));
                purchaseTransferModel.setDriverID(cursor.getInt(cursor.getColumnIndex(Common.DRIVERID)));
                purchaseTransferModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                purchaseTransferModel.setCount(cursor.getInt(cursor.getColumnIndex(Common.COUNT)));
                purchaseTransferModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                purchaseTransferModel.setPurchaseTransferUniqueID(cursor.getString(cursor.getColumnIndex(Common.PURCHASETRANSFERUNIQUEID)));
                purchaseTransferModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                purchaseTransferModel.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Common.SYNCSTATUS)));
                purchaseTransferModel.setSyncTime(cursor.getString(cursor.getColumnIndex(Common.SYNCTIME)));
                purchaseTransferModel.setLoadedTypeID(cursor.getInt(cursor.getColumnIndex(Common.LOADEDTYPE)));
                purchaseTransferModel.setTruckId(cursor.getInt(cursor.getColumnIndex(Common.TRUCKID)));
                purchaseTransferModel.setPurchaseId(cursor.getInt(cursor.getColumnIndex(Common.Purchase.PurchaseId)));
                purchaseTransferModel.setPurchaseNo(cursor.getString(cursor.getColumnIndex(Common.Purchase.PurchaseNo)));
                scannedReusltList.add(purchaseTransferModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferIdList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public boolean DeletePurchaseTransferListID(int TransferID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_PURCHASETRANSFER_LIST + " WHERE ID=" + TransferID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeletePurchaseTransferListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeletePurchaseTransferScanned(String TransferUnique_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " WHERE " + Common.TRANSFERID + " = '" + TransferUnique_ID + "'";
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean insertPurchaseTransferLogsResult(String VBB_Number, String TransferID, String FromLocationname, String ToLocationName,
                                                    String SbbLabel, String BarCode, String Length, String Volume, int UserID, String DateTime,
                                                    String WoodSpieceID, String WoodSpieceCode, int EntryMode, int IsActive, int IsSBBLabelCorrected,
                                                    String OldSBBLabel, String FellingSecID, String TreeNumner, String Classfication, String holeVol,
                                                    String GrossVol, int purchaseID) {
        boolean InsertFlag = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.VBBNUMBER, VBB_Number);
            values.put(Common.TRANSFERID, TransferID);
            values.put(Common.FROMLOCATION, FromLocationname);
            values.put(Common.TOLOCATION, ToLocationName);
            values.put(Common.SBBLabel, SbbLabel);
            values.put(Common.BARCODE, BarCode);
            values.put(Common.LENGTH, Length);
            values.put(Common.VOLUME, Volume);
            values.put(Common.USERID, UserID);
            values.put(Common.DATETIME, DateTime);
            values.put(Common.WoodSpiceID, WoodSpieceID);
            values.put(Common.WoodSPiceCode, WoodSpieceCode);
            values.put(Common.ENTRYMODE, EntryMode);
            values.put(Common.ISACTIVE, IsActive);
            values.put(Common.ISSBLABELCORRECT, IsSBBLabelCorrected);
            values.put(Common.ORGSBBLABEL, OldSBBLabel);
            values.put(Common.FELLING_SECTIONID, FellingSecID);
            values.put(Common.QULAITY, Classfication);
            //values.put(OLDCommon.QULAITY, OldClassfication);
            values.put(Common.TREENO, TreeNumner);
            values.put(Common.HOLEVOLUME, holeVol);
            values.put(Common.GROSSVOLUME, GrossVol);
            values.put(Common.Purchase.PurchaseId, purchaseID);
            long result = mDatabase.insert(Common.TBL_INVENTORYTRANSFERSCANNED, null, values);
            if (result == -1)
                InsertFlag = false;
            else
                InsertFlag = true;
            mDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertInventoryTransferResultFlag", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return InsertFlag;
    }

    public boolean UpdatePurchaseTransferListByID(String endDateTime, int ToLocationID, int FromLocationid, int TransportTypeId, int AgencyID, int DriverID,
                                                  int UserID, int ScannedCount, int TransferID, String VoulumSum, int Isactive, String transUniqueID,
                                                  int LoadedID, int truckId) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASETRANSFER_LIST + " SET "
                    + Common.ENDDATETIME + " = '" + endDateTime + "' , "
                    + Common.LOCATION_ID + " = '" + ToLocationID + "' , "
                    + Common.FROMLOCATIONID + " = '" + FromLocationid + "' , "
                    + Common.TRANSPORTTYPEID + " = '" + TransportTypeId + "' , "
                    + Common.TRANSFERAGENCYID + " = '" + AgencyID + "' , "
                    + Common.DRIVERID + " = '" + DriverID + "' , "
                    + Common.TRUCKID + " = '" + truckId + "' , "
                    + Common.ISACTIVE + " = '" + Isactive + "' , "
                    + Common.PURCHASETRANSFERUNIQUEID + " = '" + transUniqueID + "' , "
                    + Common.USERID + " = '" + UserID + "' , "
                    + Common.LOADEDTYPE + "= '" + LoadedID + "' , "
                    + "[" + Common.COUNT + "]" + "= '" + ScannedCount + "' , "
                    + Common.VOLUME + " = '" + VoulumSum + "'" +
                    " WHERE ID = " + TransferID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryTransferID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public ArrayList<InventoryTransferScannedResultModel> getPurchaseTransferPrintout(String TransferUniqueID) {
        ArrayList<InventoryTransferScannedResultModel> scannedReusltList = new ArrayList<InventoryTransferScannedResultModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + "=" + TransferUniqueID + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                inventoryTransModel = new InventoryTransferScannedResultModel();
                inventoryTransModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                inventoryTransModel.setLength(cursor.getString(cursor.getColumnIndex(Common.LENGTH)));
                inventoryTransModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                inventoryTransModel.setFromLocation(cursor.getString(cursor.getColumnIndex(Common.FROMLOCATION)));
                inventoryTransModel.setToLocation(cursor.getString(cursor.getColumnIndex(Common.TOLOCATION)));
                inventoryTransModel.setVolume(cursor.getString(cursor.getColumnIndex(Common.VOLUME)));
                inventoryTransModel.setVBB_Number(cursor.getString(cursor.getColumnIndex(Common.VBBNUMBER)));
                inventoryTransModel.setUserID(cursor.getString(cursor.getColumnIndex(Common.USERID)));
                inventoryTransModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                inventoryTransModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                inventoryTransModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                inventoryTransModel.setQualitiy(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                inventoryTransModel.setFellingSectionId(cursor.getInt(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                inventoryTransModel.setTreeNumber(cursor.getInt(cursor.getColumnIndex(Common.TREENO)));
                scannedReusltList.add(inventoryTransModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferWithVBBNumber", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }


    public double TotalVolumeForPurcahseTransfer(String SelectedDate) {
        double TotVolume = 0.00;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_PURCHASETRANSFER_LIST + " WHERE substr(StartDateTime,1,11)='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1";
            // + " and " + Common.TRANSPORTTYPEID + "=" + Common.TransportTypeId;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotVolume = TotVolume + cursor.getDouble(cursor.getColumnIndex(Common.VOLUME));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "TotalVolumeForInventoryTransfer", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotVolume;
    }

    // Purchase Received
    public String getLastPurchaseReceivedID() {
        String TransferID = "";
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(ID) as LastTansferID FROM " + Common.TBL_PURCHASERECEIVED_LIST;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TransferID = (cursor.getString(cursor.getColumnIndex("LastTansferID")));
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getLastPurchaseTransferID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TransferID;
    }

    public boolean insertPurchaseReceivedID(String IMEI, int ToLocationID, String StartDate, String EndDate,
                                            int FromLocationid, int TransportTypeID, int TransferAgencyID, int DriverID, int TruckID,
                                            int UserID, int Count, int SyncStatus, String SyncTime, String Volume, int Isactive, String transUniqueID, int LoadedBy) {
        boolean InsertFlag = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Common.Purchase.PurchaseId, Common.Purchase.SelectedPurchaseId);
            values.put(Common.Purchase.PurchaseNo, Common.Purchase.SelectedPurchaseNo);
            values.put(Common.IMEINumber, IMEI);
            values.put(Common.STARTDATETIME, StartDate);
            values.put(Common.ENDDATETIME, EndDate);
            values.put(Common.LOCATION_ID, ToLocationID);
            values.put(Common.FROMLOCATIONID, FromLocationid);
            values.put(Common.TRANSPORTTYPEID, TransportTypeID);
            values.put(Common.TRANSFERAGENCYID, TransferAgencyID);
            values.put(Common.DRIVERID, DriverID);
            values.put(Common.TRUCKID, TruckID);
            values.put(Common.USERID, UserID);
            values.put(Common.COUNT, Count);
            values.put(Common.ISACTIVE, Isactive);
            values.put(Common.PURCHASERECEIVEDUNIQUEID, transUniqueID);
            values.put(Common.VOLUME, Volume);
            values.put(Common.LOADEDTYPE, LoadedBy);
            values.put(Common.SYNCSTATUS, SyncStatus);
            values.put(Common.SYNCTIME, SyncTime);
            long result = mDatabase.insert(Common.TBL_PURCHASERECEIVED_LIST, null, values);
            if (result == -1)
                InsertFlag = false;
            else
                InsertFlag = true;
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "insertInventoryTransferID", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return InsertFlag;
    }

    public boolean UpdatePurchaseReceivedUniqueID(int ReceivadID, String ReceivedUniqueID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASERECEIVED_LIST + " SET "
                    + Common.PURCHASERECEIVEDUNIQUEID + " = '" + ReceivedUniqueID + "'" +
                    " WHERE ID = " + ReceivadID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdatePurchaseTransferUniqueID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public ArrayList<String> getPurchaseReceivedDate(int purchaseID) {
        ArrayList<String> purchaseDateList = new ArrayList<>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT trim(substr(StartDateTime,1,11))as StartDateTime from " + Common.TBL_PURCHASERECEIVED_LIST
                    + " Where " + Common.Purchase.PurchaseId + "=" + purchaseID
                    + " ORDER by ID DESC";

            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                String SdateTime = cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME));
                String[] parts = SdateTime.split(" ");
                //Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(parts[0]);
                purchaseDateList.add(parts[0]);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return purchaseDateList;
    }

    public ArrayList<PurchaseReceivedModel> getPurchaseReceivedIdList(String SelectedDate, int PurchaseId) {
        ArrayList<PurchaseReceivedModel> scannedReusltList = new ArrayList<>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_PURCHASERECEIVED_LIST + " WHERE substr(StartDateTime,1,11)='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1" + " and " + Common.Purchase.PurchaseId + " = " + PurchaseId;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                purchaseReceivedModel = new PurchaseReceivedModel();
                purchaseReceivedModel.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                purchaseReceivedModel.setIMEI(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                purchaseReceivedModel.setToLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATION_ID)));
                purchaseReceivedModel.setStartDateTime(cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME)));
                purchaseReceivedModel.setEndDateTime(cursor.getString(cursor.getColumnIndex(Common.ENDDATETIME)));
                purchaseReceivedModel.setFromLocationID(cursor.getInt(cursor.getColumnIndex(Common.FROMLOCATIONID)));
                purchaseReceivedModel.setTransportTypeId(cursor.getInt(cursor.getColumnIndex(Common.TRANSPORTTYPEID)));
                purchaseReceivedModel.setTransferAgencyID(cursor.getInt(cursor.getColumnIndex(Common.TRANSFERAGENCYID)));
                purchaseReceivedModel.setDriverID(cursor.getInt(cursor.getColumnIndex(Common.DRIVERID)));
                purchaseReceivedModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                purchaseReceivedModel.setCount(cursor.getInt(cursor.getColumnIndex(Common.COUNT)));
                purchaseReceivedModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                purchaseReceivedModel.setPurchaseReceivedUniqueID(cursor.getString(cursor.getColumnIndex(Common.PURCHASERECEIVEDUNIQUEID)));
                purchaseReceivedModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                purchaseReceivedModel.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Common.SYNCSTATUS)));
                purchaseReceivedModel.setSyncTime(cursor.getString(cursor.getColumnIndex(Common.SYNCTIME)));
                purchaseReceivedModel.setLoadedTypeID(cursor.getInt(cursor.getColumnIndex(Common.LOADEDTYPE)));
                purchaseReceivedModel.setTruckId(cursor.getInt(cursor.getColumnIndex(Common.TRUCKID)));
                purchaseReceivedModel.setPurchaseId(cursor.getInt(cursor.getColumnIndex(Common.Purchase.PurchaseId)));
                purchaseReceivedModel.setTransferUniqueID(cursor.getString(cursor.getColumnIndex(Common.TRANSFERUNIQUEID)));
                purchaseReceivedModel.setPurchaseNo(cursor.getString(cursor.getColumnIndex(Common.Purchase.PurchaseNo)));
                scannedReusltList.add(purchaseReceivedModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryTransferIdList", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public boolean DeletePurchaseReceivedListID(int ReceivedID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_PURCHASERECEIVED_LIST + " WHERE ID=" + ReceivedID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeletePurchaseTransferListID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeletePurchaseReceivedScanned(int ReceivedUniueID, int purchaseID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYRECEIVED + " WHERE " + Common.Purchase.PurchaseId + " = " + purchaseID + " and " + Common.RECEIVEDID + " = '" + ReceivedUniueID + "'";
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean UpdatePurchaseReceivedListByID(String endDateTime, int ToLocationID, int FromLocationid, int TransportTypeId, int AgencyID, int DriverID,
                                                  int UserID, int ScannedCount, int ReceivedID, String VoulumSum, int Isactive, String receivedUniqueID,
                                                  int LoadedID, int truckId, String TransferUniqeID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_PURCHASERECEIVED_LIST + " SET "
                    + Common.ENDDATETIME + " = '" + endDateTime + "' , "
                    + Common.LOCATION_ID + " = '" + ToLocationID + "' , "
                    + Common.TRANSFERUNIQUEID + " = '" + TransferUniqeID + "' , "
                    + Common.FROMLOCATIONID + " = '" + FromLocationid + "' , "
                    + Common.TRANSPORTTYPEID + " = '" + TransportTypeId + "' , "
                    + Common.TRANSFERAGENCYID + " = '" + AgencyID + "' , "
                    + Common.DRIVERID + " = '" + DriverID + "' , "
                    + Common.TRUCKID + " = '" + truckId + "' , "
                    + Common.ISACTIVE + " = '" + Isactive + "' , "
                    + Common.PURCHASERECEIVEDUNIQUEID + " = '" + receivedUniqueID + "' , "
                    + Common.USERID + " = '" + UserID + "' , "
                    + Common.LOADEDTYPE + "= '" + LoadedID + "' , "
                    + "[" + Common.COUNT + "]" + "= '" + ScannedCount + "' , "
                    + Common.VOLUME + " = '" + VoulumSum + "'" +
                    " WHERE ID = " + ReceivedID;
            mDatabase.execSQL(strSQL);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "UpdateInventoryTransferID", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public ArrayList<InventoryReceivedModel> getPurchaseReceivedPrintout(int ReceivedUniqueID) {
        ArrayList<InventoryReceivedModel> scannedReusltList = new ArrayList<InventoryReceivedModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.RECEIVEDID + "=" + ReceivedUniqueID + " and " + Common.ISACTIVE + " = 1";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                inventoryReceivedModel = new InventoryReceivedModel();
                inventoryReceivedModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                inventoryReceivedModel.setLength(cursor.getInt(cursor.getColumnIndex(Common.LENGTH)));
                inventoryReceivedModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                inventoryReceivedModel.setFromLocation(cursor.getString(cursor.getColumnIndex(Common.FROMLOCATION)));
                inventoryReceivedModel.setToLocation(cursor.getString(cursor.getColumnIndex(Common.TOLOCATION)));
                inventoryReceivedModel.setVolume(cursor.getString(cursor.getColumnIndex(Common.VOLUME)));
                inventoryReceivedModel.setVBB_Number(cursor.getString(cursor.getColumnIndex(Common.VBBNUMBER)));
                inventoryReceivedModel.setUserID(cursor.getString(cursor.getColumnIndex(Common.USERID)));
                inventoryReceivedModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                inventoryReceivedModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                inventoryReceivedModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                inventoryReceivedModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                inventoryReceivedModel.setIsReceived(cursor.getString(cursor.getColumnIndex(Common.ISCHECKED)));
                inventoryReceivedModel.setTransferUniqueID(cursor.getString(cursor.getColumnIndex(Common.TRANSFERUNIQUEID)));
                scannedReusltList.add(inventoryReceivedModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryReceivedWithVBBNumber", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public ArrayList<InventoryReceivedInputModel> getReceivedScannedResultInputWithPurchaseID(int purchaseID, int receivedID) {
        ArrayList<InventoryReceivedInputModel> InventoryResultList = new ArrayList<InventoryReceivedInputModel>();
        int ID = 1;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.Purchase.PurchaseId + " = " + purchaseID + " and " + Common.ISACTIVE + "=1" + " and " + Common.RECEIVEDID + "=" + receivedID;
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                inventoryReceivedInputListModel = new InventoryReceivedInputModel();
                inventoryReceivedInputListModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                inventoryReceivedInputListModel.setDatetime(cursor.getString(cursor.getColumnIndex(Common.DATETIME)));
                inventoryReceivedInputListModel.setEntryModeID(cursor.getInt(cursor.getColumnIndex(Common.ENTRYMODE)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.FELLING_SECTIONID)))) {
                    inventoryReceivedInputListModel.setFellingSectionId(0);
                } else {
                    inventoryReceivedInputListModel.setFellingSectionId(cursor.getInt(cursor.getColumnIndex(Common.FELLING_SECTIONID)));
                }
                inventoryReceivedInputListModel.setFromLocationID(Common.FromLocationID);
                inventoryReceivedInputListModel.setID(ID);
                inventoryReceivedInputListModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                inventoryReceivedInputListModel.setIsSBBLabelCorrected(cursor.getInt(cursor.getColumnIndex(Common.ISSBLABELCORRECT)));
                if ((cursor.getString(cursor.getColumnIndex(Common.ISCHECKED)).equals("YES"))) {
                    inventoryReceivedInputListModel.setIsReceived(booleanToInt(true));
                } else {
                    inventoryReceivedInputListModel.setIsReceived(booleanToInt(false));
                }
                if (cursor.getString(cursor.getColumnIndex(Common.LENGTH)).equals("")) {
                    inventoryReceivedInputListModel.setLength(0.00);
                } else {
                    inventoryReceivedInputListModel.setLength(parseDouble(cursor.getString(cursor.getColumnIndex(Common.LENGTH))));
                }
                inventoryReceivedInputListModel.setOrgSBBLabel(cursor.getString(cursor.getColumnIndex(Common.ORGSBBLABEL)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.QULAITY)))) {
                    inventoryReceivedInputListModel.setQuality(" ");
                } else {
                    inventoryReceivedInputListModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                }
                inventoryReceivedInputListModel.setReceivedID(cursor.getInt(cursor.getColumnIndex(Common.RECEIVEDID)));
                inventoryReceivedInputListModel.setSBBLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                inventoryReceivedInputListModel.setToLocationId(Common.ToLocReceivedID);
                inventoryReceivedInputListModel.setTreeNumber(cursor.getString(cursor.getColumnIndex(Common.TREENO)));
                if (cursor.getString(cursor.getColumnIndex(Common.VOLUME)).equals("")) {
                    inventoryReceivedInputListModel.setVolume(0.00);
                } else {
                    inventoryReceivedInputListModel.setVolume(parseDouble(cursor.getString(cursor.getColumnIndex(Common.VOLUME))));
                }
                inventoryReceivedInputListModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                inventoryReceivedInputListModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));

                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.TRANSFERUNIQUEID)))) {
                    inventoryReceivedInputListModel.setTransferUniqueID(" ");
                } else {
                    inventoryReceivedInputListModel.setTransferUniqueID(cursor.getString(cursor.getColumnIndex(Common.TRANSFERUNIQUEID)));
                }
                InventoryResultList.add(inventoryReceivedInputListModel);
                ID = ID + 1;
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getReceivedScannedResultInputWithVBBNo", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return InventoryResultList;
    }

    public ArrayList<InventoryReceivedModel> getInventoryReceivedWithPurchase(int purchaseID) {
        ArrayList<InventoryReceivedModel> scannedReusltList = new ArrayList<InventoryReceivedModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.Purchase.PurchaseId + "=" + purchaseID + " and " + Common.ISACTIVE + " = 1" + " and " + Common.RECEIVEDID + " = '" + Common.ReceivedID + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                inventoryReceivedModel = new InventoryReceivedModel();
                inventoryReceivedModel.setSbbLabel(cursor.getString(cursor.getColumnIndex(Common.SBBLabel)));
                inventoryReceivedModel.setLength(cursor.getInt(cursor.getColumnIndex(Common.LENGTH)));
                inventoryReceivedModel.setBarCode(cursor.getString(cursor.getColumnIndex(Common.BARCODE)));
                inventoryReceivedModel.setFromLocation(cursor.getString(cursor.getColumnIndex(Common.FROMLOCATION)));
                inventoryReceivedModel.setToLocation(cursor.getString(cursor.getColumnIndex(Common.TOLOCATION)));
                inventoryReceivedModel.setVolume(cursor.getString(cursor.getColumnIndex(Common.VOLUME)));
                inventoryReceivedModel.setVBB_Number(cursor.getString(cursor.getColumnIndex(Common.VBBNUMBER)));
                inventoryReceivedModel.setUserID(cursor.getString(cursor.getColumnIndex(Common.USERID)));
                inventoryReceivedModel.setWoodSpieceID(cursor.getInt(cursor.getColumnIndex(Common.WoodSpiceID)));
                inventoryReceivedModel.setWoodSpieceCode(cursor.getString(cursor.getColumnIndex(Common.WoodSPiceCode)));
                inventoryReceivedModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                inventoryReceivedModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
                inventoryReceivedModel.setIsReceived(cursor.getString(cursor.getColumnIndex(Common.ISCHECKED)));
                inventoryReceivedModel.setTransferUniqueID(cursor.getString(cursor.getColumnIndex(Common.TRANSFERUNIQUEID)));
                scannedReusltList.add(inventoryReceivedModel);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getInventoryReceivedWithVBBNumber", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return scannedReusltList;
    }

    public int ColculatePurchaseReceivedCheckedItems(String receivedID, String CheckedValue, int purchaseID) {
        int TotalCountItems = 0;
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED +
                    " WHERE " + Common.Purchase.PurchaseId + "=" + purchaseID + " and " + Common.RECEIVEDID + " = '" + receivedID + "' and " + Common.ISCHECKED + " = '" + CheckedValue + "'";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                TotalCountItems = TotalCountItems + 1;
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "CalculateInventoryReceivedCheckedItems", e.toString(), false);
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return TotalCountItems;
    }

    // Purchase Delete

    public boolean DeletePurchaseAgreements(int Purchase_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_PURCHASEAGREEMENT + " WHERE " + Common.Purchase.PurchaseId + " = " + Purchase_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeletePurchaseExternalLogDetails(int Purchase_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " WHERE " + Common.Purchase.PurchaseId + " = " + Purchase_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeletePurchaseInventoryTransferScanned(int Purchase_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " WHERE " + Common.Purchase.PurchaseId + " = " + Purchase_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeletePurchaseInventoryReceivedScanned(int Purchase_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_INVENTORYRECEIVED + " WHERE " + Common.Purchase.PurchaseId + " = " + Purchase_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeletePurchaseTransferList(int Purchase_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_PURCHASETRANSFER_LIST + " WHERE " + Common.Purchase.PurchaseId + " = " + Purchase_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeletePurchaseReceivedList(int Purchase_ID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_PURCHASERECEIVED_LIST + " WHERE " + Common.Purchase.PurchaseId + " = " + Purchase_ID;
            mDatabase.execSQL(selectQuery);
            mDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

}