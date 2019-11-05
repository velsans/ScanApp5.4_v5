package com.zebra.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zebra.main.model.Export.ExportDetailsModel;
import com.zebra.main.model.Export.ExportInputDetailsModel;
import com.zebra.main.model.Export.ExportModel;
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
    ExportInputDetailsModel exportinputDetailsModel;
    private static InternalDataBaseHelperClass instance;

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
        db.execSQL(DbClassObject.CreateLogin());
        db.execSQL(DbClassObject.CountIDList());
        db.execSQL(DbClassObject.CountIDScannedDetails());
        db.execSQL(DbClassObject.TransferIDScannedDetails());
        /*Version 4.9*/
        db.execSQL(DbClassObject.TransferIDListUpdated4_9());
        //db.execSQL(DbClassObject.ReceivedIDList());
        db.execSQL(DbClassObject.ReceivedIDScannedDetails());
        /*Version 5.2*/
        //db.execSQL(DbClassObject.FellingRegistrationDetails5_2());
        db.execSQL(DbClassObject.FellingIDList5_2());
        db.execSQL(DbClassObject.FellingTreeDetails5_2());
        /*Version 5.3*/
        db.execSQL(DbClassObject.ReceivedIDList5_3());
        db.execSQL(DbClassObject.FellingRegistrationDetails5_3());
        /*Version 5.6-New Home Design*/
        /*Version 5.7-Export*/
        db.execSQL(DbClassObject.ExportLoadPlanList5_7());
        db.execSQL(DbClassObject.ExportLoadPlanDetails5_7());
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new SQLiteException("Can't downgrade database from version " +
                oldVersion + " to " + newVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d("onUpgrade", "onUpgrade() from " + oldVersion + " to " + newVersion);// 9th may
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
            db.execSQL(DbClassObject.ExportLoadPlanList5_7());
        }
        boolean ELPDetailFlag = tableExists(db, Common.TBL_EXPORTDETAILS);
        if (ELPDetailFlag == false) {
            db.execSQL(DbClassObject.ExportLoadPlanDetails5_7());
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
            if (mCursor.getColumnIndex(columnToCheck) != -1)
                return true;
            else
                return false;

        } catch (Exception Exp) {
            // Something went wrong. Missing the database? The table?
            Log.d("existsColumnInTable", "When checking whether a column exists in the table, an error occurred: " + Exp.getMessage());
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
                                                 int FromLocationid, int TransportTypeID, int TransferAgencyID, int DriverID, String TrucklicensePlateNo,
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
                                                    String OldSBBLabel, String FellingSecID, String TreeNumner, String Classfication, String IsChecked, String TransReceUniqueID) {
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
                    + " WHERE " + Common.RECEIVEDID + " = " + receivedID + " and " + Common.BARCODE + " = " + BarCode;
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
                inventoryTransModel.setLength(cursor.getInt(cursor.getColumnIndex(Common.LENGTH)));
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

    public boolean getScannedResultWithMapListIDCheck(int List_ID, String Barcode) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_SCANNEDRESULT + " where " + Common.LISTID + "=" + List_ID + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.SBBLabel + " = " + SbbLabel + " or " + Common.ORGSBBLABEL + " = " + SbbLabel + ")";
            String selectQuery = "SELECT * FROM " + Common.TBL_SCANNEDRESULT + " where " + Common.LISTID + "=" + List_ID + " and " + Common.ISACTIVE + " = " + 1 + " and " + Common.BARCODE + " = '" + Barcode + "'";
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
                                             int FromLocationid, int TransportTypeID, int TransferAgencyID, int DriverID, String TrucklicensePlateNo, int UserID, int Count,
                                             int SyncStatus, String SyncTime, String Volume, int Isactive, String transUniqueID) {
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
                                             String TrucklicensePlateNo, int UserID, int ScannedCount, int TransferID, String VoulumSum, int Isactive, String transUniqueID, int LoadedID) {
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
                    + Common.DRIVERID + " = '" + DriverID + "' , "
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
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferListID", e.toString(), false);
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
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferListID", e.toString(), false);
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
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferListID", e.toString(), false);
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
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferListID", e.toString(), false);
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
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferListID", e.toString(), false);
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
                                             String TrucklicensePlateNo, int UserID, int ScannedCount, int TransferID, String VoulumSum, int ReceivedID,
                                             int ISActive, String TransferUniqueID, String ReceivedUniquID, int ReceivedLoadedTypeID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYRECEIVEDLIST + " SET "
                    + Common.ENDDATETIME + " = '" + endDateTime + "' , "
                    + Common.LOCATION_ID + " = '" + ToLocationID + "' , "
                    + Common.FROMLOCATIONID + " = '" + FromLocationid + "' , "
                    + Common.TRANSPORTTYPEID + " = '" + TransportTypeId + "' , "
                    + Common.TRANSFERAGENCYID + " = '" + AgencyID + "' , "
                    + Common.DRIVERID + " = '" + DriverID + "' , "
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
                                                     String OldSBBLabel, String FellingSecID, String TreeNumner, String Classfication) {
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


    public boolean getInventoryTransferduplicateCheck(String vbb_number, int Trans_ID, String Barcode, String fromLocation) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            // String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where VBB_Number = '" + vbb_number + "'" + " and " + Common.TRANSFERID + "= '" + Trans_ID + "'" + " and " + SBBLabel + "= '" + SBBLabel + "'";
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.SBBLabel + "= '" + SbbLabel + "'" + " and FromLocation" + "= '" + fromLocation + "' and " + Common.ISACTIVE + "= 1";
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + " FromLocation" + "= '" + fromLocation + "' and " + Common.ISACTIVE + "= 1" + " and (" + Common.SBBLabel + " IN (" + SbbLabel + ")" + " or " + Common.ORGSBBLABEL + "=" + SbbLabel + ")";
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + " FromLocation" + "= '" + fromLocation + "' and " + Common.ISACTIVE + "= " + 1 + " and " + Common.BARCODE + " = '" + Barcode + "'";
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

    public boolean getInventoryReceivedduplicateCheck(int Received_ID, String Barcode) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            // String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where VBB_Number = '" + vbb_number + "'" + " and " + Common.TRANSFERID + "= '" + Trans_ID + "'" + " and " + SBBLabel + "= '" + Common.SBBLabel + "'";
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.SBBLabel + "= '" + SbbLabel + "'" + " and ReceivedID" + "= '" + Received_ID + "' and " + Common.ISACTIVE + "= 1";
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + " ReceivedID" + "= '" + Received_ID + "' and " + Common.ISACTIVE + "= 1" + " and (" + Common.SBBLabel + " IN (" + SbbLabel + ")" + " or " + Common.ORGSBBLABEL + "=" + SbbLabel + ")";
            String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + " ReceivedID" + "= '" + Received_ID + "' and " + Common.ISACTIVE+ "= " + 1 + " and " + Common.BARCODE + " = '" + Barcode + "'";
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


    public boolean UpdateInventoryTransferSyncStatusTransID(String SyncTime, int SyncStatus, String VbbNumber, int TransID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYTRANSFERIDLIST + " SET "
                    + Common.SYNCTIME + " = '" + SyncTime + "' , "
                    + Common.SYNCSTATUS + " = '" + SyncStatus + "' WHERE "
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
                    + Common.VBBNUMBER + " = '" + OrderNo + "' and "
                    + Common.TRANSFERID + " = " + ExportID;
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
                if (cursor.getString(cursor.getColumnIndex(Common.LENGTH)).equals("")) {
                    inventoryReceivedInputListModel.setLength(0.00);
                } else {
                    inventoryReceivedInputListModel.setLength(parseDouble(cursor.getString(cursor.getColumnIndex(Common.LENGTH))));
                }
                inventoryReceivedInputListModel.setOrgSBBLabel(cursor.getString(cursor.getColumnIndex(Common.ORGSBBLABEL)));
                inventoryReceivedInputListModel.setQuality(cursor.getString(cursor.getColumnIndex(Common.QULAITY)));
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
            String strSQL = "UPDATE " + Common.TBL_SCANNEDRESULT + " SET " + Common.ISACTIVE + " = '" + IsActiveValue + "'where " + Common.SBBLabel + "=" + Sbb_Label + " and " + ListID + "=" + ListID;
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
            String strSQL = "UPDATE " + Common.TBL_INVENTORYTRANSFERSCANNED + " SET " + Common.ISACTIVE + " = '" + IsActiveValue + "' where " + Common.SBBLabel + "=" + Sbb_Label + " and " + Common.TRANSFERID + "=" + TrnsFerID;
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

    public boolean RemoveFromReceivedlistview(String Sbb_Label, int IsActiveValue, int receivedID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_INVENTORYRECEIVED + " SET " + Common.ISACTIVE + " = '" + IsActiveValue + "' where " + Common.SBBLabel + "=" + Sbb_Label + " and " + Common.RECEIVEDID + "=" + receivedID;
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

    public boolean getAdvanceSearchCountListCheck(int List_IDs, String Barcodes, int size, boolean Flag) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "";
            if (Flag == true) {
                selectQuery = "SELECT * FROM " + Common.TBL_SCANNEDRESULT + " where " + Common.LISTID + "=" + List_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.BARCODE + " IN (" + Barcodes + "))";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_SCANNEDRESULT + " where " + Common.LISTID + "=" + List_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and " + Common.BARCODE + "='" + Barcodes + "'";
            }
           /* if (Flag == true) {
                selectQuery = "SELECT * FROM " + Common.TBL_SCANNEDRESULT + " where " + Common.LISTID + "=" + List_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.SBBLabel + " IN (" + SbbLabels + ")" + " or " + Common.ORGSBBLABEL + " IN (" + SbbLabels + "))";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_SCANNEDRESULT + " where " + Common.LISTID + "=" + List_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.SBBLabel + " IN (" + SbbLabels + ")" + " or " + Common.ORGSBBLABEL + "=" + SbbLabels + ")";
            }*/

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

    public boolean getAdvanceSearchTransferListCheck(int transfer_IDs, String Barcodes, int size, boolean Flag) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "";
            if (Flag == true) {
                selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + "=" + transfer_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.BARCODE + " IN (" + Barcodes + "))";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + "=" + transfer_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and " + Common.BARCODE + "='" + Barcodes + "'";
            }

            /*if (Flag == true) {
                selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + "=" + transfer_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.SBBLabel + " IN (" + SbbLabels + ")" + " or " + Common.ORGSBBLABEL + " IN (" + SbbLabels + "))";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + "=" + transfer_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.SBBLabel + " IN (" + SbbLabels + ")" + " or " + Common.ORGSBBLABEL + "=" + SbbLabels + ")";
            }*/
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

    public boolean getAdvanceSearchReceivedListCheck(int received_IDs, String Barcodes, int size, boolean Flag) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "";
            if (Flag == true) {
                selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.RECEIVEDID + "=" + received_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.BARCODE + " IN (" + Barcodes + "))";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYRECEIVED + " where " + Common.RECEIVEDID + "=" + received_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and " + Common.BARCODE + "='" + Barcodes + "'";
            }

            /*if (Flag == true) {
                selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + "=" + transfer_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.SBBLabel + " IN (" + SbbLabels + ")" + " or " + Common.ORGSBBLABEL + " IN (" + SbbLabels + "))";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYTRANSFERSCANNED + " where " + Common.TRANSFERID + "=" + transfer_IDs + " and " + Common.ISACTIVE + " = " + 1 + " and (" + Common.SBBLabel + " IN (" + SbbLabels + ")" + " or " + Common.ORGSBBLABEL + "=" + SbbLabels + ")";
            }*/
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
                                            String sbbLabelNoteF, String sbbLabelNoteT, String sbbLabelNoteL, String TreePartType, String Volume) {
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
                    + Common.LENGTH + " = '" + TreeLenght + "' " +
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

    public boolean updateFellRegSbbLabel(String SbbLabel, String FellingRegUniqueID, String Df1, String Df2, String Dt1, String Dt2,
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
                    + " WHERE " + Common.FELLIINGREGUNIQUEID + " = '" + FellingRegUniqueID + "' and " + Common.SBBLabel + "= '" + SbbLabel + "'";
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

    public boolean UpdategetTreeNumberTreepartDuplicationCheck(int fromLocationID, String FellingSecID, String treeNUm, String TreePartType, String SbbLabel) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            if (TreePartType.equals("")) {
                String selectQueryCount = "SELECT * FROM " + Common.TBL_FELLINGREGISTRATIONDETAILS + " where " + Common.LOCATIONID + "=" + fromLocationID
                        + " and " + Common.FELLING_SECTIONID + " = " + FellingSecID
                        + " and " + Common.ISACTIVE + " = " + 1
                        + " and " + Common.TREENO + " = " + treeNUm
                        + " and " + Common.SBBLabel + " = " + SbbLabel
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
                    + " and " + Common.SBBLabel + " != " + SbbLabel
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
            String selectQuery = "SELECT * from " + Common.TBL_INVENTORYCOUNTLIST;
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
            String selectQuery = "SELECT * from " + Common.TBL_INVENTORYTRANSFERIDLIST + " Where " + Common.TRANSPORTTYPEID + "=" + Common.TransportTypeId;
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

    public ArrayList<String> getInventoryReceivedDate() {
        ArrayList<String> fellingTreeList = new ArrayList<String>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * from " + Common.TBL_INVENTORYRECEIVEDLIST;
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
                String OrderNumber = cursor.getString(cursor.getColumnIndex(Common.ORDERNO));
                if (isNullOrEmpty(OrderNumber)) {
                    OrderNumber = "N/A";
                }
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

    public ArrayList<ExportModel> getExportList(String SelectedDate) {
        ArrayList<ExportModel> ExportContainerListList = new ArrayList<ExportModel>();
        mDatabase = this.getReadableDatabase();
        mDatabase.beginTransaction();
        String selectQuery = "";
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYCOUNTLIST + " WHERE IsActive is null or  IsActive  = 1";
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYCOUNTLIST + " WHERE IsActive  = 1";
            //String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTLOADPLANLIST + " WHERE substr(StartDateTime,1,11)='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1";
            if (isNullOrEmpty(SelectedDate) || SelectedDate.equals("N/A")) {
                selectQuery = "SELECT * FROM " + Common.TBL_EXPORTLIST + " WHERE ifnull(OrderNo, '') = '' and " + Common.ISACTIVE + " = 1";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_EXPORTLIST + " WHERE " + Common.ORDERNO + " = '" + SelectedDate + "' and " + Common.ISACTIVE + " = 1";
            }
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                exportListModel = new ExportModel();
                exportListModel.setExportID(cursor.getInt(cursor.getColumnIndex(Common.EXPORTID)));
                exportListModel.setExportUniqueID(cursor.getString(cursor.getColumnIndex(Common.EXPORTUNIQUEID)));
                //exportListModel.setContainerNo(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)))) {
                    exportListModel.setOrderNo("N/A");
                } else {
                    exportListModel.setOrderNo(cursor.getString(cursor.getColumnIndex(Common.ORDERNO)));
                }
                exportListModel.setIMEI(cursor.getString(cursor.getColumnIndex(Common.IMEINumber)));
                exportListModel.setLocationID(cursor.getInt(cursor.getColumnIndex(Common.LOCATIONID)));
                exportListModel.setStartDateTime(cursor.getString(cursor.getColumnIndex(Common.STARTDATETIME)));
                exportListModel.setEndDateTime(cursor.getString(cursor.getColumnIndex(Common.ENDDATETIME)));
                exportListModel.setUserID(cursor.getInt(cursor.getColumnIndex(Common.USERID)));
                exportListModel.setTotalCount(cursor.getInt(cursor.getColumnIndex(Common.COUNT)));
                exportListModel.setVolume(cursor.getDouble(cursor.getColumnIndex(Common.VOLUME)));
                exportListModel.setIsActive(cursor.getInt(cursor.getColumnIndex(Common.ISACTIVE)));
                exportListModel.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Common.SYNCSTATUS)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.SYNCTIME)))) {
                    exportListModel.setSyncDate("");
                } else {
                    exportListModel.setSyncDate(cursor.getString(cursor.getColumnIndex(Common.SYNCTIME)));
                }
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

    public double TotalVolumeForExport(String SelectedDate) {
        double TotVolume = 0.00;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        String selectQuery = "";
        try {
            //String selectQuery = "SELECT * FROM " + Common.TBL_INVENTORYCOUNTLIST + " where " + Common.ISACTIVE + " = " + 1;
            //String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTLOADPLANLIST + " WHERE substr(StartDateTime,1,11)='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1";
            if (isNullOrEmpty(SelectedDate) || SelectedDate.equals("N/A")) {
                selectQuery = "SELECT * FROM " + Common.TBL_EXPORTLIST + " WHERE ifnull(OrderNo, '') = '' and " + Common.ISACTIVE + " = 1";
            } else {
                selectQuery = "SELECT * FROM " + Common.TBL_EXPORTLIST + " WHERE " + Common.ORDERNO + " ='" + SelectedDate + "' and " + Common.ISACTIVE + " = 1";
            }
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

    public boolean DeleteExportScanned(int ExportID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_EXPORTDETAILS + " WHERE " + Common.EXPORTID + " = " + ExportID;
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

    /*Inventory Transfer Detials*/
    public boolean insertExportIDList(String order_Number, String IMEI, int LocationID, String StartDate, String EndDate,
                                      int UserID, int Count, String Volume, int Isactive, String ExporUniqueID) {
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

    public boolean UpdateExportUniqueID(int exportID, String exportUniqueID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_EXPORTLIST + " SET "
                    + Common.EXPORTUNIQUEID + " = '" + exportUniqueID + "'" +
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

    public ArrayList<ExportDetailsModel> getExportDetailsList(int Export_ID, boolean ExpoDetailsFlag) {
        int ID = 1;
        ArrayList<ExportDetailsModel> exportList = new ArrayList<ExportDetailsModel>();
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
                exportDetailsModel = new ExportDetailsModel();
                exportDetailsModel.setID(ID);
                exportDetailsModel.setExportID(cursor.getInt(cursor.getColumnIndex(Common.EXPORTID)));
                exportDetailsModel.setExportUniqueID(cursor.getInt(cursor.getColumnIndex(Common.EXPORTUNIQUEID)));
                if (isNullOrEmpty(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)))) {
                    exportDetailsModel.setContainerNo("");
                } else {
                    exportDetailsModel.setContainerNo(cursor.getString(cursor.getColumnIndex(Common.CONTAINERNO)));
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

    public boolean getExportLoadPlanListIDCheck(String SbbLabel) {
        boolean Result = false;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.TBL_EXPORTDETAILS + " where " + Common.ISACTIVE + " = " + 1 + " and " + Common.SBBLabel + " = " + SbbLabel;
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

    public boolean UpdateExportLoadPlanListID(String endDateTime, int ScannedCount, int ExportID, String VolumeSum, int Isactive, String CountUniqID) {
        boolean Flag = true;
        mDatabase = this.getWritableDatabase();
        mDatabase.beginTransaction();
        try {
            String strSQL = "UPDATE " + Common.TBL_EXPORTLIST + " SET "
                    + Common.ENDDATETIME + " = '" + endDateTime + "' , "
                    + "[" + Common.COUNT + "]" + "= '" + ScannedCount + "', "
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

    public boolean insertExportDetailsResult(int ExportID, String ExportUniqueID, String OrderNo, String ContainerNo, int ToLocationID, String IMEI, String PvNo, String PvDate, String SbbLabel, String BarCode,
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
            values.put(Common.CONTAINERNO, OrderNo);
            values.put(Common.CONTAINERNO, ContainerNo);
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
}