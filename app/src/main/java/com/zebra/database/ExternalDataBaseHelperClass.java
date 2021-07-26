package com.zebra.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zebra.main.activity.purchase.ui.logs.PurchaseLogsModels;
import com.zebra.main.model.AdvanceSearchModel;
import com.zebra.main.model.externaldb.AgencyDetailsExternalModel;
import com.zebra.main.model.externaldb.AgencyDetailsModel;
import com.zebra.main.model.externaldb.ConcessionNamesModel;
import com.zebra.main.model.externaldb.DriverDetailsExternalModel;
import com.zebra.main.model.externaldb.DriverDetailsModel;
import com.zebra.main.model.externaldb.FellingRegisterModel;
import com.zebra.main.model.externaldb.FellingRegisterWithPlotIDModel;
import com.zebra.main.model.externaldb.FellingSectionModel;
import com.zebra.main.model.externaldb.LoadedModel;
import com.zebra.main.model.externaldb.LocationDevicesModel;
import com.zebra.main.model.externaldb.LocationsModel;
import com.zebra.main.model.externaldb.MasterTotalCount;
import com.zebra.main.model.externaldb.PurchaseAgreementModel;
import com.zebra.main.model.externaldb.TransferLogDetailsExModel;
import com.zebra.main.model.externaldb.TransferLogDetailsModel;
import com.zebra.main.model.externaldb.TransportModesModel;
import com.zebra.main.model.externaldb.TruckDetailsExternalModel;
import com.zebra.main.model.externaldb.TruckDetailsModel;
import com.zebra.main.model.externaldb.WoodSpeciesModel;
import com.zebra.main.model.InvReceived.InventoryReceivedModel;
import com.zebra.main.model.InvReceived.ReceivedLogsModel;
import com.zebra.main.model.QualityModel;
import com.zebra.main.model.SyncTableModel;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExternalDataBaseHelperClass extends SQLiteOpenHelper {
    AlertDialogManager alert = new AlertDialogManager();

    private Context mContext;
    private SQLiteDatabase mExDatabase;
    TransferLogDetailsModel translogModel;
    LocationsModel locationModel;

    ConcessionNamesModel concessionNamesModel;
    AgencyDetailsModel agencyDetailsModel;
    TransportModesModel transportModeModel;
    LoadedModel loadedModel;
    TruckDetailsModel truckDetailsModel;
    DriverDetailsModel driverDetailsModel;
    AdvanceSearchModel advanceSearchModel;
    LocationDevicesModel locationDeviceModel;
    FellingSectionModel fellingSectionModel;
    QualityModel classificationModel;
    WoodSpeciesModel woodspiceModel;
    FellingRegisterModel fellingRegModel;
    FellingRegisterWithPlotIDModel fellingRegDistinctModel;
    SyncTableModel tableNamemodel;
    ReceivedLogsModel receivingmodel;
    PurchaseLogsModels purchaseLogsModel;

    public ExternalDataBaseHelperClass(Context context) {
        super(context, Common.EXTERNAL_MASTER_DB_NAME, null, 1);
        this.mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openDatabase() {
        String dbPath = mContext.getDatabasePath(Common.EXTERNAL_MASTER_DB_NAME).getPath();
        if (mExDatabase != null && mExDatabase.isOpen()) {
            return;
        }
        mExDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void closeDatabase() {
        if (mExDatabase != null) {
            mExDatabase.close();
        }
    }

    public ArrayList<LocationsModel> getToLocationsWithLocID(String LocationIDS) {
        int ID = 1;

        ArrayList<LocationsModel> labels = new ArrayList<LocationsModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_LOCATIONS + " where ToLocationId" + " IN (" + LocationIDS + ")";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                locationModel = new LocationsModel();
                locationModel.setID(ID);
                locationModel.setLocation(cursor.getString(cursor.getColumnIndex(Common.LOCATION)));
                locationModel.setToLocationId(cursor.getInt(cursor.getColumnIndex("ToLocationId")));
                labels.add(locationModel);
                ID = ID + 1;
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getToLocationsWithLocID", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public String getFellingRegisterwoodspeicecode(String ConcessionID, String tree_no, String felling_sec) {
        String labels = "";
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_FELLINGREGISTER + " where " + Common.ExternalDataBaseClass.ConcessionID + "='" + ConcessionID + "'" + " and " + Common.ExternalDataBaseClass.FELLINGSECTIONID + "='" + felling_sec + "'" + " and " + Common.ExternalDataBaseClass.TREENUMBER + "='" + tree_no + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                labels = (cursor.getString(cursor.getColumnIndex("WoodSpeciesCode")));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getFellingRegisterwoodspeicecode", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<LocationsModel> getToLocations() {
        int ID = 1;
        ArrayList<LocationsModel> labels = new ArrayList<LocationsModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_LOCATIONS;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
               /* if (Common.ToLocationID == cursor.getInt(cursor.getColumnIndex("ToLocationId"))) {
                } else {*/
                locationModel = new LocationsModel();
                locationModel.setID(ID);
                locationModel.setLocation(cursor.getString(cursor.getColumnIndex("Location")));
                locationModel.setToLocationId(cursor.getInt(cursor.getColumnIndex("ToLocationId")));
                labels.add(locationModel);
                ID = ID + 1;
                //}
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getToLocations", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }
  /*  public int getLocationType(int FromLocaId) {
        int labels = 0;
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT LocationType FROM " + TBL_CONCESSION + " where FromLocationId = '" + FromLocaId + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);

                while (cursor.moveToNext()) {
                    labels = (cursor.getInt(cursor.getColumnIndex("LocationType")));
                }
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }*/

    public ArrayList<FellingSectionModel> getFellingSectionDetails(int FromLocaId) {
        int ID = 1;
        ArrayList<FellingSectionModel> labels = new ArrayList<FellingSectionModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_FELLINGSECTION + " where ConcessionId = '" + FromLocaId + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingSectionModel = new FellingSectionModel();
                fellingSectionModel.setID(ID);
                fellingSectionModel.setConcessionId(cursor.getInt(cursor.getColumnIndex("ConcessionId")));
                fellingSectionModel.setConcessionName(cursor.getString(cursor.getColumnIndex("ConcessionName")));
                fellingSectionModel.setFellingSectionId(cursor.getString(cursor.getColumnIndex("FellingSectionId")));
                //fellingSectionModel.setFellingSection(cursor.getString(cursor.getColumnIndex("FellingSection")));
                fellingSectionModel.setFellingSectionNumber(cursor.getInt(cursor.getColumnIndex("FellingSectionNumber")));
                fellingSectionModel.setFellingCode(cursor.getString(cursor.getColumnIndex("FellingCode")));
                fellingSectionModel.setLocationType(cursor.getInt(cursor.getColumnIndex("LocationType")));

                labels.add(fellingSectionModel);
                ID = ID + 1;
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getFellingSectionDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public String getToLocationName(int ToLocaId) {
        String labels = "";
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT Location FROM " + Common.ExternalDataBaseClass.TBL_LOCATIONS + " where ToLocationId = '" + ToLocaId + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                labels = (cursor.getString(cursor.getColumnIndex("Location")));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getToLocationName", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }


    public Map<String, ArrayList<TransferLogDetailsModel>> getAllTransferLogDetails() {
        Map<String, ArrayList<TransferLogDetailsModel>> getAllTransferLogDetails = new HashMap<>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM  " + Common.ExternalDataBaseClass.TBL_TRANSFERLOG;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                ArrayList<TransferLogDetailsModel> labels = new ArrayList<TransferLogDetailsModel>();
                translogModel = new TransferLogDetailsModel();
                translogModel.setLocationId(cursor.getString(cursor.getColumnIndex("LocationId")));
                translogModel.setLocationName(cursor.getString(cursor.getColumnIndex("LocationName")));
                translogModel.setPlotNo(cursor.getString(cursor.getColumnIndex("PlotNo")));
                translogModel.setFellingSectionNumber(cursor.getString(cursor.getColumnIndex("FellingSectionNumber")));
                translogModel.setHarvestCropsId(cursor.getString(cursor.getColumnIndex("HarvestCropsId")));
                translogModel.setInStockId(cursor.getString(cursor.getColumnIndex("InStockId")));
                translogModel.setTreeNumber(cursor.getString(cursor.getColumnIndex("TreeNumber")));
                translogModel.setWoodSpeciesCode(cursor.getString(cursor.getColumnIndex("WoodSpeciesCode")));
                translogModel.setSBBLabel(cursor.getString(cursor.getColumnIndex("SBBLabel")));
                translogModel.setLength_dm(cursor.getString(cursor.getColumnIndex("Length_dm")));
                translogModel.setVolume(cursor.getString(cursor.getColumnIndex("Volume")));
                translogModel.setWoodSpeciesId(cursor.getString(cursor.getColumnIndex("WoodSpeciesId")));
                translogModel.setFellingSectionId(cursor.getString(cursor.getColumnIndex("FellingSectionId")));
                translogModel.setQuality(cursor.getString(cursor.getColumnIndex("LogQuality")));
                translogModel.setLogStatus(cursor.getString(cursor.getColumnIndex("LogStatus")));
                translogModel.setLogStatus(cursor.getString(cursor.getColumnIndex("BarCode")));
                labels.add(translogModel);
                getAllTransferLogDetails.put(cursor.getString(cursor.getColumnIndex("BarCode")), labels);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllTransferLogDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return getAllTransferLogDetails;
    }

    public ArrayList<TransferLogDetailsModel> getBarCodeTransferLogDetails(String BarCode) {
        ArrayList<TransferLogDetailsModel> labels = new ArrayList<TransferLogDetailsModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM  " + Common.ExternalDataBaseClass.TBL_TRANSFERLOG + " Where " + Common.BARCODE + "='" + BarCode + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                translogModel = new TransferLogDetailsModel();
                translogModel.setLocationId(cursor.getString(cursor.getColumnIndex("LocationId")));
                translogModel.setLocationName(cursor.getString(cursor.getColumnIndex("LocationName")));
                translogModel.setPlotNo(cursor.getString(cursor.getColumnIndex("PlotNo")));
                translogModel.setFellingSectionNumber(cursor.getString(cursor.getColumnIndex("FellingSectionNumber")));
                translogModel.setHarvestCropsId(cursor.getString(cursor.getColumnIndex("HarvestCropsId")));
                translogModel.setInStockId(cursor.getString(cursor.getColumnIndex("InStockId")));
                translogModel.setTreeNumber(cursor.getString(cursor.getColumnIndex("TreeNumber")));
                translogModel.setWoodSpeciesCode(cursor.getString(cursor.getColumnIndex("WoodSpeciesCode")));
                translogModel.setSBBLabel(cursor.getString(cursor.getColumnIndex("SBBLabel")));
                translogModel.setLength_dm(cursor.getString(cursor.getColumnIndex("Length_dm")));
                translogModel.setVolume(cursor.getString(cursor.getColumnIndex("Volume")));
                translogModel.setWoodSpeciesId(cursor.getString(cursor.getColumnIndex("WoodSpeciesId")));
                translogModel.setFellingSectionId(cursor.getString(cursor.getColumnIndex("FellingSectionId")));
                translogModel.setQuality(cursor.getString(cursor.getColumnIndex("LogQuality")));
                translogModel.setLogStatus(cursor.getString(cursor.getColumnIndex("LogStatus")));
                translogModel.setLogStatus(cursor.getString(cursor.getColumnIndex("BarCode")));
                /*18-Nov-2019*/
                translogModel.setF1(cursor.getInt(cursor.getColumnIndex("F1")));
                translogModel.setF2(cursor.getInt(cursor.getColumnIndex("F2")));
                translogModel.setT1(cursor.getInt(cursor.getColumnIndex("T1")));
                translogModel.setT2(cursor.getInt(cursor.getColumnIndex("T2")));
                translogModel.setHoleVolume(cursor.getString(cursor.getColumnIndex("HoleVolume")));
                translogModel.setGrossVolume(cursor.getString(cursor.getColumnIndex("GrossVolume")));
                translogModel.setRetributionStatus(cursor.getString(cursor.getColumnIndex("RetributionStatus")));
                translogModel.setExportExamination(cursor.getString(cursor.getColumnIndex("ExportExaminationStatus")));
                translogModel.setPurchaseID(cursor.getString(cursor.getColumnIndex("PurchaseID")));
                labels.add(translogModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getBarCodeTransferLogDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }


    public ArrayList<ReceivedLogsModel> getRecevingLogsTransferLogDetails(ArrayList<InventoryReceivedModel> InventoryReceivedScannedResultList) {
        ArrayList<ReceivedLogsModel> labels = new ArrayList<ReceivedLogsModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            for (InventoryReceivedModel inviReceivedModel : InventoryReceivedScannedResultList) {
                String selectQuery = "SELECT * FROM  " + Common.ExternalDataBaseClass.TBL_TRANSFERLOG + " Where " + Common.BARCODE + "='" + inviReceivedModel.getBarCode() + "'";
                Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
                while (cursor.moveToNext()) {
                    receivingmodel = new ReceivedLogsModel();
                    receivingmodel.setBarCode(inviReceivedModel.getBarCode());
                    receivingmodel.setWoodSpecieCode(cursor.getString(cursor.getColumnIndex("WoodSpeciesCode")));
                    receivingmodel.setFellingSection(cursor.getString(cursor.getColumnIndex("FellingSectionNumber")));
                    receivingmodel.setTreeNumber(cursor.getString(cursor.getColumnIndex("TreeNumber")));
                    receivingmodel.setRetributionStatus(cursor.getString(cursor.getColumnIndex("RetributionStatus")));
                    receivingmodel.setExportExamination(cursor.getString(cursor.getColumnIndex("ExportExaminationStatus")));
                    labels.add(receivingmodel);
                }
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getBarCodeTransferLogDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<ConcessionNamesModel> getAllConcessionNames(int FromLocationID) {
        int ID = 1;
        ArrayList<ConcessionNamesModel> labels = new ArrayList<ConcessionNamesModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_CONCESSION + " where FromLocationId = '" + FromLocationID + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                //if(Common.ToLocationID==)
                concessionNamesModel = new ConcessionNamesModel();
                concessionNamesModel.setID(ID);
                concessionNamesModel.setFromLocationId(cursor.getInt(cursor.getColumnIndex("FromLocationId")));
                concessionNamesModel.setLocationType(cursor.getInt(cursor.getColumnIndex("LocationType")));
                concessionNamesModel.setConcessionName(cursor.getString(cursor.getColumnIndex("ConcessionName")));
                ID = ID + 1;
                labels.add(concessionNamesModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllConcessionNames", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public String getFromLocationName(int FromLocaId) {
        String labels = "";
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT ConcessionName FROM " + Common.ExternalDataBaseClass.TBL_CONCESSION + " where FromLocationId = '" + FromLocaId + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                labels = (cursor.getString(cursor.getColumnIndex("ConcessionName")));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getFromLocationName", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<ConcessionNamesModel> getConcessionList() {
        int ID = 1;
        ArrayList<ConcessionNamesModel> labels = new ArrayList<ConcessionNamesModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_CONCESSION;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                concessionNamesModel = new ConcessionNamesModel();
                concessionNamesModel.setID(ID);
                concessionNamesModel.setFromLocationId(cursor.getInt(cursor.getColumnIndex("FromLocationId")));
                concessionNamesModel.setConcessionName(cursor.getString(cursor.getColumnIndex("ConcessionName")));
                concessionNamesModel.setLocationType(cursor.getInt(cursor.getColumnIndex("LocationType")));
                labels.add(concessionNamesModel);
                ID = ID + 1;
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getConcessionList", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }


    public ArrayList<AgencyDetailsModel> getAllAgencyDetails() {
        int ID = 1;
        ArrayList<AgencyDetailsModel> labels = new ArrayList<AgencyDetailsModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_AGENCY;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                agencyDetailsModel = new AgencyDetailsModel();
                agencyDetailsModel.setID(ID);
                agencyDetailsModel.setAgencyId(cursor.getInt(cursor.getColumnIndex(Common.ExternalDataBaseClass.AGENCYID)));
                agencyDetailsModel.setAgencyName(cursor.getString(cursor.getColumnIndex(Common.ExternalDataBaseClass.AGENCYNAME)));
                agencyDetailsModel.setAddress(cursor.getString(cursor.getColumnIndex(Common.ExternalDataBaseClass.ADDRESS)));
                agencyDetailsModel.setIsBlocked(cursor.getInt(cursor.getColumnIndex("IsBlocked")));
                labels.add(agencyDetailsModel);
                ID = ID + 1;
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllAgencyDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return labels;
    }

    public String getAgencyName(int AgencyId) {
        String labels = "";
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT AgencyName FROM " + Common.ExternalDataBaseClass.TBL_AGENCY + " where AgencyId = '" + AgencyId + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                labels = (cursor.getString(cursor.getColumnIndex(Common.ExternalDataBaseClass.AGENCYNAME)));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAgencyName", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public AgencyDetailsModel getOneAgencyDetails(String AgencyName) {
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_AGENCY + " where AgencyName = '" + AgencyName + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                agencyDetailsModel = new AgencyDetailsModel();
                agencyDetailsModel.setAgencyId(cursor.getInt(cursor.getColumnIndex(Common.ExternalDataBaseClass.AGENCYID)));
                agencyDetailsModel.setAgencyName(cursor.getString(cursor.getColumnIndex(Common.ExternalDataBaseClass.AGENCYNAME)));
                agencyDetailsModel.setAddress(cursor.getString(cursor.getColumnIndex(Common.ExternalDataBaseClass.ADDRESS)));
                agencyDetailsModel.setIsBlocked(cursor.getInt(cursor.getColumnIndex("IsBlocked")));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAgencyName", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return agencyDetailsModel;
    }

    public ArrayList<DriverDetailsModel> getAllDriverDetails() {
        int ID = 1;
        ArrayList<DriverDetailsModel> labels = new ArrayList<DriverDetailsModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_DRIVER;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                driverDetailsModel = new DriverDetailsModel();
                driverDetailsModel.setID(ID);
                driverDetailsModel.setTruckDriverId(cursor.getInt(cursor.getColumnIndex("TruckDriverId")));
                driverDetailsModel.setDriverLicenseNo(cursor.getString(cursor.getColumnIndex("DriverLicenseNo")));
                driverDetailsModel.setDriverName(cursor.getString(cursor.getColumnIndex(Common.ExternalDataBaseClass.DRIVERNAME)));
                driverDetailsModel.setIsBlocked(cursor.getInt(cursor.getColumnIndex("IsBlocked")));
                labels.add(driverDetailsModel);
                ID = ID + 1;
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllDriverDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public String getAllDriverName(int DriverID) {
        String labels = "";
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DriverName FROM " + Common.ExternalDataBaseClass.TBL_DRIVER + " where TruckDriverId = '" + DriverID + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                labels = (cursor.getString(cursor.getColumnIndex(Common.ExternalDataBaseClass.DRIVERNAME)));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllDriverName", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public String getAllTruckNames(int truckID) {
        String labels = "";
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT TruckLicensePlateNo FROM " + Common.ExternalDataBaseClass.TBL_TRUCK + " where TransportId=" + truckID;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                labels = (cursor.getString(cursor.getColumnIndex("TruckLicensePlateNo")));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllTruckNames", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }


    public DriverDetailsModel getOneDriverDetails(String DriverName) {
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_DRIVER + " where DriverName = '" + DriverName + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                driverDetailsModel = new DriverDetailsModel();
                driverDetailsModel.setTruckDriverId(cursor.getInt(cursor.getColumnIndex("TruckDriverId")));
                driverDetailsModel.setDriverLicenseNo(cursor.getString(cursor.getColumnIndex("DriverLicenseNo")));
                driverDetailsModel.setDriverName(cursor.getString(cursor.getColumnIndex(Common.ExternalDataBaseClass.DRIVERNAME)));
                driverDetailsModel.setIsBlocked(cursor.getInt(cursor.getColumnIndex("IsBlocked")));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getOneDriverDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return driverDetailsModel;
    }

    public ArrayList<TransportModesModel> getAllTransportModeDetails() {
        ArrayList<TransportModesModel> labels = new ArrayList<TransportModesModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_TRANSPORTMODE;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                transportModeModel = new TransportModesModel();
                transportModeModel.setTransportTypeId(cursor.getInt(cursor.getColumnIndex(Common.ExternalDataBaseClass.TRANSPORTTYPEID)));
                transportModeModel.setTransportMode(cursor.getString(cursor.getColumnIndex(Common.ExternalDataBaseClass.TRANSPORTMODE)));
                labels.add(transportModeModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllTransportModeDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<LoadedModel> getAllLoadedByDetails() {
        ArrayList<LoadedModel> labels = new ArrayList<LoadedModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_LOADED;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                loadedModel = new LoadedModel();
                loadedModel.setLoadedid(cursor.getInt(cursor.getColumnIndex(Common.LOADEDID)));
                loadedModel.setName(cursor.getString(cursor.getColumnIndex("Name")));
                loadedModel.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                labels.add(loadedModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllLoadedByDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public String getAllLoadedNames(int LoadedTypeID) {
        String labels = "";
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_LOADED + " where Loadedid = '" + LoadedTypeID + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                labels = (cursor.getString(cursor.getColumnIndex("Name")));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllLoadedNames", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public String getAllTransPortMode(int TransPortTypeID) {
        String labels = "";
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT TransportMode FROM " + Common.ExternalDataBaseClass.TBL_TRANSPORTMODE + " where TransportTypeId = '" + TransPortTypeID + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                labels = (cursor.getString(cursor.getColumnIndex("TransportMode")));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllTransPortMode", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public String getLoadedName(int LoadedId) {
        String labels = "";
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT Name FROM " + Common.ExternalDataBaseClass.TBL_LOADED + " where Loadedid = '" + LoadedId + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                labels = (cursor.getString(cursor.getColumnIndex("Name")));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLoadedName", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<TruckDetailsModel> getAllTruckDetails() {
        int ID = 1;
        ArrayList<TruckDetailsModel> labels = new ArrayList<TruckDetailsModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_TRUCK;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                truckDetailsModel = new TruckDetailsModel();
                truckDetailsModel.setID(ID);
                truckDetailsModel.setTransportId(cursor.getInt(cursor.getColumnIndex("TransportId")));
                truckDetailsModel.setTruckLicensePlateNo(cursor.getString(cursor.getColumnIndex("TruckLicensePlateNo")));
                truckDetailsModel.setDescription(cursor.getString(cursor.getColumnIndex("Description")));
                truckDetailsModel.setIsBlocked(cursor.getInt(cursor.getColumnIndex("IsBlocked")));
                labels.add(truckDetailsModel);
                ID = ID + 1;
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllTruckDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public TruckDetailsModel getOneTruckDetails(String TruckPlateNo) {
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_TRUCK + " where TruckLicensePlateNo = '" + TruckPlateNo + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                truckDetailsModel = new TruckDetailsModel();
                truckDetailsModel.setTransportId(cursor.getInt(cursor.getColumnIndex("TransportId")));
                truckDetailsModel.setTruckLicensePlateNo(cursor.getString(cursor.getColumnIndex("TruckLicensePlateNo")));
                truckDetailsModel.setDescription(cursor.getString(cursor.getColumnIndex("Description")));
                truckDetailsModel.setIsBlocked(cursor.getInt(cursor.getColumnIndex("IsBlocked")));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllTruckDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return truckDetailsModel;
    }

    public ArrayList<AdvanceSearchModel> getAdvanceSearchList(String TreeNo, String FellingID, String Log_status) {
        ArrayList<AdvanceSearchModel> labels = new ArrayList<AdvanceSearchModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            //String strSQL = "UPDATE " + TBL_INVENTORYCOUNT + "SET" + ENDDATETIME + "=" + endDateTime + " and " + COUNT + "=" + ScannedCount + " WHERE " + COUNTID + "=" + CountID;
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_TRANSFERLOG + " where " + Common.ExternalDataBaseClass.TREENUMBER + "='" + TreeNo + "'" + " and " + Common.ExternalDataBaseClass.FELLINGSECTIONID + "='" + FellingID + "'" + " and trim(" + Common.ExternalDataBaseClass.LOGSTATUS + ")='" + Log_status + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                advanceSearchModel = new AdvanceSearchModel();
                advanceSearchModel.setBarCode(cursor.getString(cursor.getColumnIndex("BarCode")));
                advanceSearchModel.setSBBLabel(cursor.getString(cursor.getColumnIndex("SBBLabel")));
                advanceSearchModel.setLength_dm(cursor.getString(cursor.getColumnIndex("Length_dm")));
                advanceSearchModel.setVolume(cursor.getString(cursor.getColumnIndex("Volume")));
                advanceSearchModel.setWoodSpeciesId(cursor.getInt(cursor.getColumnIndex("WoodSpeciesId")));
                advanceSearchModel.setWoodSpeciesCode(cursor.getString(cursor.getColumnIndex("WoodSpeciesCode")));
                advanceSearchModel.setFellingSectionId(cursor.getString(cursor.getColumnIndex("FellingSectionId")));
                advanceSearchModel.setTreeNumber(cursor.getString(cursor.getColumnIndex("TreeNumber")));
                advanceSearchModel.setClassification(cursor.getString(cursor.getColumnIndex("LogQuality")));
                advanceSearchModel.setF1(cursor.getString(cursor.getColumnIndex("F1")));
                advanceSearchModel.setF2(cursor.getString(cursor.getColumnIndex("F2")));
                advanceSearchModel.setT1(cursor.getString(cursor.getColumnIndex("T1")));
                advanceSearchModel.setT2(cursor.getString(cursor.getColumnIndex("T2")));
                advanceSearchModel.setHoleVolume(cursor.getString(cursor.getColumnIndex("HoleVolume")));
                advanceSearchModel.setGrossVolume(cursor.getString(cursor.getColumnIndex("GrossVolume")));
                advanceSearchModel.setPurchaseID(cursor.getString(cursor.getColumnIndex("PurchaseID")));
                advanceSearchModel.setExisiting(true);
                labels.add(advanceSearchModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAdvanceSearchList", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<LocationDevicesModel> getSingleLocationDevice(String IMEI_Number) {
        ArrayList<LocationDevicesModel> labels = new ArrayList<LocationDevicesModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {

            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_LOCATIONDEVICE + " where IMEI = '" + IMEI_Number + "'" + " and " + Common.ExternalDataBaseClass.ISBLOCKED + "=0";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                locationDeviceModel = new LocationDevicesModel();
                locationDeviceModel.setLDevid(cursor.getInt(cursor.getColumnIndex("LDevId")));
                locationDeviceModel.setLocationId(cursor.getInt(cursor.getColumnIndex("LocationId")));
                locationDeviceModel.setDeviceName(cursor.getString(cursor.getColumnIndex("DeviceName")));
                locationDeviceModel.setIMEI(cursor.getString(cursor.getColumnIndex("IMEI")));
                locationDeviceModel.setIsDefault(cursor.getInt(cursor.getColumnIndex("IsDefault")));
                locationDeviceModel.setFromLocationId(cursor.getInt(cursor.getColumnIndex("FromLocationId")));
                locationDeviceModel.setIsBlocked(cursor.getInt(cursor.getColumnIndex("IsBlocked")));
                labels.add(locationDeviceModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getSingleLocationDevice", e.toString(), false);

        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<LocationDevicesModel> getAllLocationDevice(String IMEI_Number) {
        ArrayList<LocationDevicesModel> labels = new ArrayList<LocationDevicesModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_LOCATIONDEVICE;// + " where IMEI = '" + IMEI_Number + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                locationDeviceModel = new LocationDevicesModel();
                locationDeviceModel.setLDevid(cursor.getInt(cursor.getColumnIndex("LDevId")));
                locationDeviceModel.setLocationId(cursor.getInt(cursor.getColumnIndex("LocationId")));
                locationDeviceModel.setDeviceName(cursor.getString(cursor.getColumnIndex("DeviceName")));
                locationDeviceModel.setIMEI(cursor.getString(cursor.getColumnIndex("IMEI")));
                locationDeviceModel.setIsDefault(cursor.getInt(cursor.getColumnIndex("IsDefault")));
                locationDeviceModel.setFromLocationId(cursor.getInt(cursor.getColumnIndex("FromLocationId")));
                locationDeviceModel.setIsBlocked(cursor.getInt(cursor.getColumnIndex("IsBlocked")));
                labels.add(locationDeviceModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllLocationDevice", e.toString(), false);

        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<QualityModel> getAllclassifications() {
        ArrayList<QualityModel> labels = new ArrayList<QualityModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_CLASSIFICATION;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                classificationModel = new QualityModel();
                classificationModel.setId(cursor.getInt(cursor.getColumnIndex("Id")));
                classificationModel.setQuality(cursor.getString(cursor.getColumnIndex("CCode")));
                labels.add(classificationModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllclassifications", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<TransferLogDetailsModel> getclassificationsname() {
        ArrayList<TransferLogDetailsModel> labels = new ArrayList<TransferLogDetailsModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_TRANSFERLOG;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                translogModel = new TransferLogDetailsModel();
                translogModel.setQuality(cursor.getString(cursor.getColumnIndex("Class")));
                labels.add(translogModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getclassificationsname", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<FellingSectionModel> getFellingSectionID(int ConcessionId) {
        int ID = 1;
        ArrayList<FellingSectionModel> labels = new ArrayList<FellingSectionModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_FELLINGSECTION + " where ConcessionId = '" + ConcessionId + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingSectionModel = new FellingSectionModel();
                fellingSectionModel.setID(ID);
                fellingSectionModel.setConcessionId(cursor.getInt(cursor.getColumnIndex("ConcessionId")));
                fellingSectionModel.setConcessionName(cursor.getString(cursor.getColumnIndex("ConcessionName")));
                fellingSectionModel.setFellingSectionId(cursor.getString(cursor.getColumnIndex("FellingSectionId")));
                fellingSectionModel.setFellingSectionNumber(cursor.getInt(cursor.getColumnIndex("FellingSectionNumber")));
                fellingSectionModel.setFellingCode(cursor.getString(cursor.getColumnIndex("FellingCode")));
                fellingSectionModel.setLocationType(cursor.getInt(cursor.getColumnIndex("LocationType")));
                labels.add(fellingSectionModel);
                ID = ID + 1;
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getFellingSectionID", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<FellingRegisterModel> getFellingRegisterFilter(int FromLoc, String felling_sec) {
        ArrayList<FellingRegisterModel> labels = new ArrayList<FellingRegisterModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_FELLINGREGISTER + " where " + Common.ExternalDataBaseClass.ConcessionID + "=" + FromLoc + " and " + Common.FELLING_SECTIONID + "='" + felling_sec + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingRegModel = new FellingRegisterModel();
                fellingRegModel.setConcessionId(cursor.getInt(cursor.getColumnIndex("ConcessionId")));
                fellingRegModel.setConcessionName(cursor.getString(cursor.getColumnIndex("ConcessionName")));
                fellingRegModel.setFellingSectionId(cursor.getInt(cursor.getColumnIndex("FellingSectionId")));
                fellingRegModel.setFellingSectionNumber(cursor.getInt(cursor.getColumnIndex("FellingSectionNumber")));
                fellingRegModel.setFellingCode(cursor.getString(cursor.getColumnIndex("FellingCode")));
                fellingRegModel.setPlotId(cursor.getInt(cursor.getColumnIndex("PlotId")));
                fellingRegModel.setPlotNumber(cursor.getString(cursor.getColumnIndex("PlotNumber")));
                fellingRegModel.setTreeNumber(cursor.getInt(cursor.getColumnIndex("TreeNumber")));
                fellingRegModel.setWoodSpeciesCode(cursor.getString(cursor.getColumnIndex("WoodSpeciesCode")));
                fellingRegModel.setWoodSpeciesId(cursor.getInt(cursor.getColumnIndex("WoodSpeciesId")));
                labels.add(fellingRegModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getFellingRegisterMasterData", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<FellingRegisterWithPlotIDModel> getFellingRegisterFilterWithDistinct(String felling_sec) {
        ArrayList<FellingRegisterWithPlotIDModel> labels = new ArrayList<FellingRegisterWithPlotIDModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT PlotId,PlotNumber  FROM " + Common.ExternalDataBaseClass.TBL_FELLINGREGISTER + " where " + Common.FELLING_SECTIONID + "='" + felling_sec + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingRegDistinctModel = new FellingRegisterWithPlotIDModel();
                fellingRegDistinctModel.setPlotId(cursor.getInt(cursor.getColumnIndex("PlotId")));
                fellingRegDistinctModel.setPlotNumber(cursor.getString(cursor.getColumnIndex("PlotNumber")));
                labels.add(fellingRegDistinctModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getFellingRegisterMasterData", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<FellingRegisterWithPlotIDModel> getPlotIDCheck(String PlotNumber) {
        ArrayList<FellingRegisterWithPlotIDModel> labels = new ArrayList<FellingRegisterWithPlotIDModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT PlotId,PlotNumber  FROM " + Common.ExternalDataBaseClass.TBL_FELLINGREGISTER + " where " + Common.ExternalDataBaseClass.ExPLOTNO + "='" + PlotNumber + "' and " + Common.FELLING_SECTIONID + "='" + Common.FellingSectionId + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                fellingRegDistinctModel = new FellingRegisterWithPlotIDModel();
                fellingRegDistinctModel.setPlotId(cursor.getInt(cursor.getColumnIndex("PlotId")));
                fellingRegDistinctModel.setPlotNumber(cursor.getString(cursor.getColumnIndex("PlotNumber")));
                labels.add(fellingRegDistinctModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getFellingRegisterMasterData", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    /*Insert ContantValues with loop*/
    public void insertAgencyDetailsREPLACE(ArrayList<AgencyDetailsExternalModel> AgencyDetailsExSync) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                for (int i = 0; i < AgencyDetailsExSync.size(); i++) {
                    boolean b = Boolean.parseBoolean(AgencyDetailsExSync.get(i).getIsBlocked());
                    int IsBlocked = (b) ? 1 : 0;
                    values.put("RowID", AgencyDetailsExSync.get(i).getRowID());
                    values.put(Common.ExternalDataBaseClass.AGENCYID, AgencyDetailsExSync.get(i).getAgencyId());
                    values.put(Common.ExternalDataBaseClass.AGENCYNAME, AgencyDetailsExSync.get(i).getAgencyName());
                    values.put(Common.ExternalDataBaseClass.ADDRESS, AgencyDetailsExSync.get(i).getAddress());
                    values.put(Common.ExternalDataBaseClass.ISBLOCKED, IsBlocked);
                    mExDatabase.replace(Common.ExternalDataBaseClass.TBL_AGENCY, null, values);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertAgencyDetailsREPLACE", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>> :%s", "db is null");
    }

    public void insertDriverDetailsREPLACE(ArrayList<DriverDetailsExternalModel> DriverDetailsExSync) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                for (int i = 0; i < DriverDetailsExSync.size(); i++) {
                    boolean b = Boolean.parseBoolean(DriverDetailsExSync.get(i).getIsBlocked());
                    int IsBlocked = (b) ? 1 : 0;
                    values.put("RowID", DriverDetailsExSync.get(i).getRowID());
                    values.put(Common.ExternalDataBaseClass.TRUCKDRIVERID, DriverDetailsExSync.get(i).getTruckDriverId());
                    values.put(Common.ExternalDataBaseClass.DRIVERLICNO, DriverDetailsExSync.get(i).getDriverLicenseNo());
                    values.put(Common.ExternalDataBaseClass.DRIVERNAME, DriverDetailsExSync.get(i).getDriverName());
                    values.put(Common.ExternalDataBaseClass.ISBLOCKED, IsBlocked);
                    mExDatabase.replace(Common.ExternalDataBaseClass.TBL_DRIVER, null, values);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertDriverDetailsREPLACE", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>> :%s", "db is null");
    }

    public void insertTransferLogDetailsREPLACE(ArrayList<TransferLogDetailsExModel> TransferLogDetailsExSync) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                for (int i = 0; i < TransferLogDetailsExSync.size(); i++) {
                    values.put("RowID", TransferLogDetailsExSync.get(i).getRowId());
                    values.put(Common.ExternalDataBaseClass.LOCATIONDID, TransferLogDetailsExSync.get(i).getLocationId());
                    values.put(Common.ExternalDataBaseClass.LOCNAME, TransferLogDetailsExSync.get(i).getLocationName());
                    values.put(Common.PLOTNO, TransferLogDetailsExSync.get(i).getPlotNo());
                    values.put(Common.ExternalDataBaseClass.FELLINGSECNO, TransferLogDetailsExSync.get(i).getFellingSectionNumber());
                    values.put(Common.ExternalDataBaseClass.HARCROPID, TransferLogDetailsExSync.get(i).getHarvestCropsId());
                    values.put(Common.ExternalDataBaseClass.INSTOCKID, TransferLogDetailsExSync.get(i).getInStockId());
                    values.put(Common.TREENO, TransferLogDetailsExSync.get(i).getTreeNumber());
                    values.put(Common.ExternalDataBaseClass.WOODSpeciesCODE, TransferLogDetailsExSync.get(i).getWoodSpeciesCode());
                    values.put(Common.SBBLabel, TransferLogDetailsExSync.get(i).getSBBLabel());
                    values.put("F1", TransferLogDetailsExSync.get(i).getF1());
                    values.put("F2", TransferLogDetailsExSync.get(i).getF2());
                    values.put("T1", TransferLogDetailsExSync.get(i).getT1());
                    values.put("T2", TransferLogDetailsExSync.get(i).getT2());

                    values.put(Common.ExternalDataBaseClass.LENGHTDM, TransferLogDetailsExSync.get(i).getLength_dm());
                    values.put(Common.VOLUME, TransferLogDetailsExSync.get(i).getVolume());
                    values.put(Common.ExternalDataBaseClass.WODESPEICEID, TransferLogDetailsExSync.get(i).getWoodSpeciesId());
                    values.put(Common.ExternalDataBaseClass.FELLINGSECTIONID, TransferLogDetailsExSync.get(i).getFellingSectionId());
                    values.put("LogQuality", TransferLogDetailsExSync.get(i).getLogQuality());
                    values.put(Common.ExternalDataBaseClass.LOGSTATUS, TransferLogDetailsExSync.get(i).getLogStatus());
                    values.put(Common.BARCODE, TransferLogDetailsExSync.get(i).getBarCode());
                    /*version 5.8*/
                    values.put(Common.CREATEDDATE, TransferLogDetailsExSync.get(i).getCreatedDate());
                    /*version 5.9*/
                    values.put("Note_F", TransferLogDetailsExSync.get(i).getNote_F());
                    values.put("Note_T", TransferLogDetailsExSync.get(i).getNote_T());
                    values.put("Note_L", TransferLogDetailsExSync.get(i).getNote_L());
                    values.put("HoleVolume ", TransferLogDetailsExSync.get(i).getHoleVolume());
                    values.put("GrossVolume ", TransferLogDetailsExSync.get(i).getGrossVolume());
                    //18-Aug-2020
                    values.put("RetributionStatus", TransferLogDetailsExSync.get(i).getRetributionStatus());
                    values.put("ExportExaminationStatus", TransferLogDetailsExSync.get(i).getExportExaminationStatus());
                    //08-july-2021
                    values.put("PurchaseID", TransferLogDetailsExSync.get(i).getPurchaseID());
                    mExDatabase.replace(Common.ExternalDataBaseClass.TBL_TRANSFERLOG, null, values);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertTransferLogDetailsREPLACE", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else {
            Log.d(">>>>>> :%s", "db is null");
        }
    }

    public void insertTruckDetailsREPLACE(ArrayList<TruckDetailsExternalModel> TruckDetailsExSync) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                for (int i = 0; i < TruckDetailsExSync.size(); i++) {
                    boolean b = Boolean.parseBoolean(TruckDetailsExSync.get(i).getIsBlocked());
                    int IsBlocked = (b) ? 1 : 0;
                    values.put("RowID", TruckDetailsExSync.get(i).getRowID());
                    values.put(Common.ExternalDataBaseClass.TRANSPORTID, TruckDetailsExSync.get(i).getTransportId());
                    values.put(Common.ExternalDataBaseClass.TRUCKPLATENO, TruckDetailsExSync.get(i).getTruckLicensePlateNo());
                    values.put(Common.ExternalDataBaseClass.DESCRIPTION, TruckDetailsExSync.get(i).getDescription());
                    values.put(Common.ExternalDataBaseClass.ISBLOCKED, IsBlocked);
                    mExDatabase.replace(Common.ExternalDataBaseClass.TBL_TRUCK, null, values);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertTruckDetailsREPLACE", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>> :%s", "db is null");
    }

    public void insertFellingRegisterREPLACE(ArrayList<FellingRegisterModel> fellingRegisterSync) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                for (int i = 0; i < fellingRegisterSync.size(); i++) {
                    values.put("RowID", fellingRegisterSync.get(i).getRowID());
                    values.put(Common.ExternalDataBaseClass.ConcessionID, fellingRegisterSync.get(i).getConcessionId());
                    values.put(Common.ExternalDataBaseClass.FELLINGSECTIONID, fellingRegisterSync.get(i).getFellingSectionId());
                    values.put(Common.ExternalDataBaseClass.FELLINGSECNO, fellingRegisterSync.get(i).getFellingSectionNumber());
                    values.put(Common.PLOTID, fellingRegisterSync.get(i).getPlotId());
                    values.put(Common.ExternalDataBaseClass.TREENUMBER, fellingRegisterSync.get(i).getTreeNumber());
                    values.put(Common.ExternalDataBaseClass.WODESPEICEID, fellingRegisterSync.get(i).getWoodSpeciesId());
                    values.put(Common.ExternalDataBaseClass.CONCESSIONNAME, fellingRegisterSync.get(i).getConcessionName());
                    values.put(Common.ExternalDataBaseClass.FELLINGCODE, fellingRegisterSync.get(i).getFellingCode());
                    values.put(Common.ExternalDataBaseClass.ExPLOTNO, fellingRegisterSync.get(i).getPlotNumber());
                    values.put(Common.ExternalDataBaseClass.WOODSpeciesCODE, fellingRegisterSync.get(i).getWoodSpeciesCode());
                    mExDatabase.replace(Common.ExternalDataBaseClass.TBL_FELLINGREGISTER, null, values);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertfellingregisterDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>> :%s", "db is null");

    }

    public void insertFellingSectionREPLACE(ArrayList<FellingSectionModel> fellingSectionSync) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                for (int i = 0; i < fellingSectionSync.size(); i++) {
                    values.put("RowID", fellingSectionSync.get(i).getRowID());
                    values.put(Common.ExternalDataBaseClass.ConcessionID, fellingSectionSync.get(i).getConcessionId());
                    values.put(Common.ExternalDataBaseClass.CONCESSIONNAME, fellingSectionSync.get(i).getConcessionName());
                    values.put(Common.ExternalDataBaseClass.FELLINGSECTIONID, fellingSectionSync.get(i).getFellingSectionId());
                    values.put(Common.ExternalDataBaseClass.FELLINGSECNO, fellingSectionSync.get(i).getFellingSectionNumber());
                    values.put(Common.ExternalDataBaseClass.FELLINGCODE, fellingSectionSync.get(i).getFellingCode());
                    values.put(Common.ExternalDataBaseClass.LOCATIONTYPE, fellingSectionSync.get(i).getLocationType());
                    mExDatabase.replace(Common.ExternalDataBaseClass.TBL_FELLINGSECTION, null, values);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertfellingsectionDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>> :%s", "db is null");
    }

    public void insertLocationDevicesREPLACE(ArrayList<LocationDevicesModel> LocationDevicesSync) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                for (int i = 0; i < LocationDevicesSync.size(); i++) {
                    values.put("RowID", LocationDevicesSync.get(i).getRowID());
                    values.put(Common.ExternalDataBaseClass.LDEVID, LocationDevicesSync.get(i).getLDevid());
                    values.put(Common.ExternalDataBaseClass.LOCATIONDID, LocationDevicesSync.get(i).getLocationId());
                    values.put(Common.IMEINumber, LocationDevicesSync.get(i).getIMEI());
                    values.put(Common.ExternalDataBaseClass.DEVICENAME, LocationDevicesSync.get(i).getDeviceName());
                    values.put(Common.ExternalDataBaseClass.ISDEFAULT, LocationDevicesSync.get(i).getIsDefault());
                    values.put(Common.ExternalDataBaseClass.FROMLOCATIONId, LocationDevicesSync.get(i).getFromLocationId());
                    values.put(Common.ExternalDataBaseClass.SYNCAPIURL, LocationDevicesSync.get(i).getSyncApiURL());
                    values.put(Common.ExternalDataBaseClass.ISBLOCKED, LocationDevicesSync.get(i).getIsBlocked());
                    mExDatabase.replace(Common.ExternalDataBaseClass.TBL_LOCATIONDEVICE, null, values);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertLocationDevicesDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>> :%s", "db is null");
    }

    // woodspecies
    //new 31-8-19
    public void insertwoodspecies(String woodspeciesID, String woodspeciesCode) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                values.put(Common.ExternalDataBaseClass.WODESPEICEID, woodspeciesID);
                values.put(Common.ExternalDataBaseClass.WOODSpeciesCODE, woodspeciesCode);
                mExDatabase.insert(Common.ExternalDataBaseClass.TBL_WOODSPEICES, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertwoodspeciesreferenceDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>> :%s", "db is null");
    }

    //ConcessionNames
    public void insertConcessionNames(int FromLocationId, int LocationType, String ConcessionName) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                values.put(Common.FROMLOCATIONID, FromLocationId);
                values.put(Common.ExternalDataBaseClass.LOCATIONTYPE, LocationType);
                values.put(Common.ExternalDataBaseClass.CONCESSIONNAME, ConcessionName);
                mExDatabase.insert(Common.ExternalDataBaseClass.TBL_CONCESSION, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertConcessionNamesDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>> :%s", "db is null");
    }

    //Loaded
    public void insertLoaded(int Loadedid, int IsActive, String Name) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                values.put(Common.LOADEDID, Loadedid);
                values.put(Common.ISACTIVE, IsActive);
                values.put(Common.NAME, Name);
                mExDatabase.insert(Common.ExternalDataBaseClass.TBL_LOADED, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertLOADEDDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>> :%s", "db is null");
    }

    //LocationDevices
    public void insertLocationDevices(int RowID, int LDevId, int LocationId, String imeiNUMBER, String DevName, int IsDefault, int FromLocationId, String SyncURL) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                values.put("RowID", RowID);
                values.put(Common.ExternalDataBaseClass.LDEVID, LDevId);
                values.put(Common.ExternalDataBaseClass.LOCATIONDID, LocationId);
                values.put(Common.IMEINumber, imeiNUMBER);
                values.put(Common.ExternalDataBaseClass.DEVICENAME, DevName);
                values.put(Common.ExternalDataBaseClass.ISDEFAULT, IsDefault);
                values.put(Common.ExternalDataBaseClass.FROMLOCATIONId, FromLocationId);
                values.put(Common.ExternalDataBaseClass.SYNCAPIURL, SyncURL);
                mExDatabase.insert(Common.ExternalDataBaseClass.TBL_LOCATIONDEVICE, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertLocationDevicesDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>> :%s", "db is null");
    }

    // 8-11-19 location Device Update
    public void updateLocationDevices(int RowID, String SyncURL) {
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        if (null != mExDatabase) {
            try {
                String strSQL = "UPDATE " + Common.ExternalDataBaseClass.TBL_LOCATIONDEVICE + " SET "
                        + Common.ExternalDataBaseClass.SYNCAPIURL + " = '" + SyncURL + "' " +
                        " WHERE " + "RowID" + " = " + RowID;
                mExDatabase.execSQL(strSQL);
                mExDatabase.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "UpdateLocationDevicesDetails", e.toString(), false);
            } finally {
                mExDatabase.endTransaction();
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>> :%s", "db is null");
    }

    //Locations
    public void insertLocations(int ToLocationId, String Location) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                values.put(Common.LOCATION_ID, ToLocationId);
                values.put(Common.LOCATION, Location);
                mExDatabase.insert(Common.ExternalDataBaseClass.TBL_LOCATIONS, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertLocationsDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>> :%s", "db is null");
    }

    //TransportModes
    public void insertTransportModes(int TransportTypeId, String TransportMode) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                values.put(Common.ExternalDataBaseClass.TRANSPORTTYPEID, TransportTypeId);
                values.put(Common.ExternalDataBaseClass.TRANSPORTMODE, TransportMode);
                mExDatabase.insert(Common.ExternalDataBaseClass.TBL_TRANSPORTMODE, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertTransportModesDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>> :%s", "db is null");
    }

    public String GetFellingSectionNumber(int LocaID, String FellingSecID) {
        String PlotNumber = "";
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT DISTINCT FellingSectionNumber FROM " + Common.ExternalDataBaseClass.TBL_FELLINGREGISTER + " where "
                    + Common.ExternalDataBaseClass.ConcessionID + " = " + LocaID
                    + " and " + Common.FELLING_SECTIONID + " = " + FellingSecID;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                PlotNumber = (cursor.getString(cursor.getColumnIndex("FellingSectionNumber")));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "GetFellingSectionNumber", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return PlotNumber;
    }

    public boolean getFellingRegisterMasterData(String felling_sec) {
        boolean Result = false;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_FELLINGREGISTER + " where " + Common.ExternalDataBaseClass.FELLINGSECTIONID + "='" + felling_sec + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getFellingRegisterMasterData", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return Result;
    }

    public void insertPurchaseAgreementREPLACEEB(ArrayList<PurchaseAgreementModel> agreementSync) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                for (int i = 0; i < agreementSync.size(); i++) {
                    ContentValues values = new ContentValues();
                    values.put(Common.Purchase.PurchaseId, agreementSync.get(i).getPurchaseId());
                    values.put(Common.Purchase.PurchaseNo, agreementSync.get(i).getPurchaseNo());
                    values.put(Common.Purchase.DiameterRange, agreementSync.get(i).getDiameterRange());
                    values.put("WoodSpiecesCode", agreementSync.get(i).getWoodSpeciesCode());
                    values.put(Common.WoodSpiceID, agreementSync.get(i).getWoodSpecieId());
                    values.put(Common.Purchase.ValidUntil, agreementSync.get(i).getValidUntil());
                    values.put(Common.Purchase.StatusID, agreementSync.get(i).getStatusId());
                    values.put(Common.Purchase.PurchaseListid, agreementSync.get(i).getPurchaseListid());
                    mExDatabase.replace(Common.ExternalDataBaseClass.TBL_EXTERNALDBPURCHASEAGREEMENT, null, values);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertPurchaseAgreementREPLACEEB", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        }
    }

    public void insertPurchaseLogsREPLACE(ArrayList<PurchaseLogsModels> agreementSync) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                for (int i = 0; i < agreementSync.size(); i++) {
                    ContentValues values = new ContentValues();
                    values.put("RowID", agreementSync.get(i).getRowId());
                    values.put(Common.ExternalDataBaseClass.LOCATIONDID, agreementSync.get(i).getLocationID());
                    values.put(Common.ExternalDataBaseClass.LOCNAME, agreementSync.get(i).getLocationName());
                    values.put(Common.ExternalDataBaseClass.FELLINGSECNO, agreementSync.get(i).getFellingSectionNumber());
                    values.put(Common.ExternalDataBaseClass.TREENUMBER, agreementSync.get(i).getTreeNumber());
                    values.put("WoodSpeciesCode", agreementSync.get(i).getWoodSpeciesCode());
                    values.put("F1", agreementSync.get(i).getF1());
                    values.put("F2", agreementSync.get(i).getF2());
                    values.put("T1", agreementSync.get(i).getT1());
                    values.put("T2", agreementSync.get(i).getT2());
                    values.put(Common.ExternalDataBaseClass.LENGHTDM, agreementSync.get(i).getLength_dm());
                    values.put(Common.VOLUME, agreementSync.get(i).getVolume());
                    values.put("WoodSpeciesId", agreementSync.get(i).getWoodSpeciesId());
                    values.put(Common.ExternalDataBaseClass.FELLINGSECTIONID, agreementSync.get(i).getFellingSectionID());
                    values.put("LogQuality", agreementSync.get(i).getLogQuality());
                    values.put(Common.ExternalDataBaseClass.LOGSTATUS, agreementSync.get(i).getLogStatus());
                    values.put(Common.BARCODE, agreementSync.get(i).getBarCode());
                    values.put(Common.Purchase.UpdatedDate, agreementSync.get(i).getUpdatedDate());
                    values.put("Note_F", agreementSync.get(i).getNoteF());
                    values.put("Note_T", agreementSync.get(i).getNoteT());
                    values.put("Note_L", agreementSync.get(i).getNoteL());
                    values.put("HoleVolume ", agreementSync.get(i).getHoleVolume());
                    values.put("GrossVolume ", agreementSync.get(i).getGrossVolume());
                    values.put("RetributionStatus", agreementSync.get(i).getRetributionStatus());
                    values.put("ExportExaminationStatus", agreementSync.get(i).getExportExaminationStatus());
                    values.put(Common.Purchase.UpdatedBy, agreementSync.get(i).getUpdatedBy());
                    values.put(Common.Purchase.PurchaseId, agreementSync.get(i).getPurchaseId());
                    values.put(Common.Purchase.PurchaseNo, agreementSync.get(i).getPurchaseNo());
                    values.put(Common.ENTRYMODE, agreementSync.get(i).getEntryMode());
                    values.put(Common.LHF1, agreementSync.get(i).getLHF1());
                    values.put(Common.LHF2, agreementSync.get(i).getLHF2());
                    values.put(Common.LHT1, agreementSync.get(i).getLHT1());
                    values.put(Common.LHT2, agreementSync.get(i).getLHT2());
                    values.put(Common.LHVOLUME, agreementSync.get(i).getLHVolume());
                    long rowInserted = mExDatabase.replace(Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS, null, values);
                    if (rowInserted != -1) {
                        //Log.e("PurchaseLogs-if", ">>>" + agreementSync.get(i).getBarCode());
                    }
                    else {
                        //Log.e("PurchaseLogs_else", ">>>" + agreementSync.get(i).getBarCode());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertPurchaseAgreementREPLACE", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        }
    }


    public String getExternalDBDetails() {
        String Result = "";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_EXTERNALDBDETIALS;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = "Version: " + cursor.getString(cursor.getColumnIndex("DBVersionNo")) + "\n";
                Result = Result + "Date: " + cursor.getString(cursor.getColumnIndex("UpdatedDate"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getFellingRegisterMasterData", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return Result;
    }

    public String[] getFellingRegisterWoodSpiceCode(String LocID, String TreeNo, String felling_sec) {
        String[] Result = new String[3];
        Result[0] = "";
        Result[1] = "";
        Result[2] = "0";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_FELLINGREGISTER +
                    " where " + Common.ExternalDataBaseClass.ConcessionID + "='" + LocID + "'" +
                    " and " + Common.TREENO + "='" + TreeNo + "'" +
                    " and " + Common.ExternalDataBaseClass.FELLINGSECTIONID + "='" + felling_sec + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result[0] = (cursor.getString(cursor.getColumnIndex("WoodSpeciesCode")));
                Result[1] = (cursor.getString(cursor.getColumnIndex("PlotNumber")));
                Result[2] = (cursor.getString(cursor.getColumnIndex("PlotId")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "getFellingRegisterMasterData", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return Result;
    }

    public boolean WoodSpeiceCodeCheck(String woodSpeiCode) {
        boolean Result = false;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_WOODSPEICES + " where " + Common.ExternalDataBaseClass.WOODSpeciesCODE + "='" + woodSpeiCode + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "WoodSpeiceCodeCheck", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return Result;
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(mContext, Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    /*WoodSpeice Tabel*/
    public ArrayList<WoodSpeciesModel> getWoodSpicesTabel() {
        int ID = 1;
        ArrayList<WoodSpeciesModel> labels = new ArrayList<WoodSpeciesModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_WOODSPEICES;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                woodspiceModel = new WoodSpeciesModel();
                woodspiceModel.setWoodSpeciesCode(cursor.getString(cursor.getColumnIndex("WoodSpeciesCode")));
                woodspiceModel.setWoodSpeciesId(cursor.getString(cursor.getColumnIndex("WoodSpeciesId")));
                labels.add(woodspiceModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getWoodSpicesTabel", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<WoodSpeciesModel> getWoodSpiceID(String WoodSpeicesCode) {
        ArrayList<WoodSpeciesModel> labels = new ArrayList<WoodSpeciesModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_WOODSPEICES + " where " + Common.ExternalDataBaseClass.WOODSpeciesCODE + "='" + WoodSpeicesCode + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                woodspiceModel = new WoodSpeciesModel();
                woodspiceModel.setWoodSpeciesCode(cursor.getString(cursor.getColumnIndex("WoodSpeciesCode")));
                woodspiceModel.setWoodSpeciesId(cursor.getString(cursor.getColumnIndex("WoodSpeciesId")));
                labels.add(woodspiceModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getWoodSpicesTabel", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    /*Get Last IndexValues*/
    public int getLastAgencyD() {
        int LastIndex = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_AGENCY;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLastAgencyD", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }

    public int getLastConsessionID() {
        int LastIndex = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_CONCESSION;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLastConsessionID", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }

    public int getLastDriverD() {
        int LastIndex = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_DRIVER;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLastDriverD", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }

    public int getLastFellingSectionID() {
        int LastIndex = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_FELLINGSECTION;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLastAgencyD", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }

    public int getLastFellingRegisterID() {
        int LastIndex = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_FELLINGREGISTER;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLastAgencyD", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }

    public int getLastLocationDeviceID() {
        int LastIndex = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_LOCATIONDEVICE;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLastLocationDeviceID", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }

    public int getLastLocationsID() {
        int LastIndex = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_LOCATIONS;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLocationsID", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }

    public int getLastTransferLogDetailsID() {
        int LastIndex = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_TRANSFERLOG;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLocationsID", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }

    public int getLastTransportModesID() {
        int LastIndex = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_TRANSPORTMODE;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLocationsID", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }

    public int getLastTruckDetailsID() {
        int LastIndex = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_TRUCK;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLocationsID", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }

    public int getLastWoodSpieceID() {
        int LastIndex = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_WOODSPEICES;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLastWoodSpieceID", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }

    public int getLoadedRowID() {
        int LastIndex = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_LOADED;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLastWoodSpieceID", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }

    public int getPurchaseLogsRowID() {
        int LastIndex = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = cursor.getInt(cursor.getColumnIndex("LastIndex"));
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLastWoodSpieceID", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return LastIndex;
    }

    public int getMaxTreeNumberExter(int FromLoca, String FellingSec) {
        String TreeNumber;
        int ExtNewTreeNumber = 0;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(TreeNumber) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_FELLINGREGISTER + " where " + Common.ExternalDataBaseClass.ConcessionID + "=" + FromLoca + " and " + Common.ExternalDataBaseClass.FELLINGSECTIONID + "='" + FellingSec + "'";
            Cursor Extercursor = mExDatabase.rawQuery(selectQuery, null);
            while (Extercursor.moveToNext()) {
                TreeNumber = (Extercursor.getString(Extercursor.getColumnIndex("LastIndex")));
                if (isNullOrEmpty(TreeNumber)) {
                    ExtNewTreeNumber = 1;
                } else {
                    ExtNewTreeNumber = Integer.parseInt(TreeNumber);
                }
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getLastAgencyD", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return ExtNewTreeNumber;
    }

    /*Master Detials Validations*/
    public boolean FromLocationMasterCheck(int FromLocationID) {
        boolean Result = false;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_CONCESSION + " where FromLocationId = '" + FromLocationID + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "FromLocationMasterCheck", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return Result;
    }

    public boolean AgencyMasterCheck(int AgencyID) {
        boolean Result = false;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_AGENCY + " where AgencyId = '" + AgencyID + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "AgencyMasterCheck", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return Result;
    }

    public boolean DriverDetailsMasterCheck(int DriverID) {
        boolean Result = false;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_DRIVER + " where TruckDriverId = '" + DriverID + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "DriverDetailsMasterCheck", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return Result;
    }

    public boolean TruckDetailsMasterCheck(int TruckID) {
        boolean Result = false;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_TRUCK + " where TransportId = '" + TruckID + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "TruckDetailsMasterCheck", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return Result;
    }

    public boolean TransportTypeMasterCheck(int TransportTypeId) {
        boolean Result = false;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_TRANSPORTMODE + " where TransportTypeId = '" + TransportTypeId + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                Result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "TransportTypeMasterCheck", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return Result;
    }

    public Map<String, ArrayList<TransferLogDetailsModel>> getAllExportListDetails() {
        Map<String, ArrayList<TransferLogDetailsModel>> getAllTransferLogDetails = new HashMap<>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM  " + Common.ExternalDataBaseClass.TBL_TRANSFERLOG;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                ArrayList<TransferLogDetailsModel> labels = new ArrayList<TransferLogDetailsModel>();
                translogModel = new TransferLogDetailsModel();
                translogModel.setLocationId(cursor.getString(cursor.getColumnIndex("LocationId")));
                translogModel.setLocationName(cursor.getString(cursor.getColumnIndex("LocationName")));
                translogModel.setPlotNo(cursor.getString(cursor.getColumnIndex("PlotNo")));
                translogModel.setFellingSectionNumber(cursor.getString(cursor.getColumnIndex("FellingSectionNumber")));
                translogModel.setHarvestCropsId(cursor.getString(cursor.getColumnIndex("HarvestCropsId")));
                translogModel.setInStockId(cursor.getString(cursor.getColumnIndex("InStockId")));
                translogModel.setTreeNumber(cursor.getString(cursor.getColumnIndex("TreeNumber")));
                translogModel.setWoodSpeciesCode(cursor.getString(cursor.getColumnIndex("WoodSpeciesCode")));
                translogModel.setSBBLabel(cursor.getString(cursor.getColumnIndex("SBBLabel")));
                translogModel.setLength_dm(cursor.getString(cursor.getColumnIndex("Length_dm")));
                translogModel.setVolume(cursor.getString(cursor.getColumnIndex("Volume")));
                translogModel.setWoodSpeciesId(cursor.getString(cursor.getColumnIndex("WoodSpeciesId")));
                translogModel.setFellingSectionId(cursor.getString(cursor.getColumnIndex("FellingSectionId")));
                translogModel.setQuality(cursor.getString(cursor.getColumnIndex("LogQuality")));
                translogModel.setLogStatus(cursor.getString(cursor.getColumnIndex("LogStatus")));
                /*version 5.9*/
                translogModel.setHoleVolume(cursor.getString(cursor.getColumnIndex("HoleVolume")));
                translogModel.setGrossVolume(cursor.getString(cursor.getColumnIndex("GrossVolume")));
                labels.add(translogModel);
                getAllTransferLogDetails.put(cursor.getString(cursor.getColumnIndex("SBBLabel")), labels);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllExportListDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return getAllTransferLogDetails;
    }


    public ArrayList<String> AllTableNames() {
        ArrayList<String> allTableList = new ArrayList<>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM sqlite_master WHERE type ='table'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndex("name")).equals("android_metadata") || cursor.getString(cursor.getColumnIndex("name")).equals("sqlite_sequence")) {
                } else {
                    allTableList.add(cursor.getString(cursor.getColumnIndex("name")));
                }
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllTableNames", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return allTableList;
    }


    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str == "null";
    }

    public ArrayList<SyncTableModel> getTableRowID(String tableName) {
        ArrayList<SyncTableModel> allTableList = new ArrayList<>();
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT Count(RowID) as LastIndex FROM " + tableName;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                tableNamemodel = new SyncTableModel();
                tableNamemodel.setTableName(tableName);
                int LastIndex = (cursor.getInt(cursor.getColumnIndex("LastIndex")));
                tableNamemodel.setTableRowCount(LastIndex);
                for (MasterTotalCount master : Common.masterTotalCountsSync) {
                    if (master.getTableName().equals(tableName)) {
                        tableNamemodel.setMasterTableCount(master.getRowCounts());
                    }
                }
                allTableList.add(tableNamemodel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getTableRowID", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return allTableList;
    }

    /*Validation for Transfers*/
    public Boolean IsValidFromLocation(String FromStr) {
        boolean isValid = false;
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_CONCESSION + " where " + Common.ExternalDataBaseClass.CONCESSIONNAME + " = '" + FromStr + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                isValid = true;
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getToLocations", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return isValid;
    }

    public Boolean IsValidToLocation(String LocStr) {
        boolean isValid = false;
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_LOCATIONS + " where " + Common.LOCATION + " = '" + LocStr + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                isValid = true;
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getToLocations", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return isValid;
    }

    public Boolean IsValidAgencyID(String AgencyStr) {
        boolean isValid = false;
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_AGENCY + " where " + Common.ExternalDataBaseClass.AGENCYNAME + " = '" + AgencyStr + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                isValid = true;
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "IsValidAgencyID", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return isValid;
    }

    public Boolean IsValidDriverID(String DriverStr) {
        boolean isValid = false;
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_DRIVER + " where " + Common.ExternalDataBaseClass.DRIVERNAME + " = '" + DriverStr + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                isValid = true;
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getToLocations", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return isValid;
    }

    public Boolean IsValidTruckID(String TruckStr) {
        boolean isValid = false;
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_TRUCK + " where " + Common.ExternalDataBaseClass.TRUCKPLATENO + " = '" + TruckStr + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                isValid = true;
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getToLocations", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return isValid;
    }

    public ArrayList<TruckDetailsModel> getTruckDetailsWithTruckID(String TruckStr, boolean IsValid5_9) {
        int ID = 1;
        ArrayList<TruckDetailsModel> labels = new ArrayList<TruckDetailsModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "";
            if (IsValid5_9 == true) {
                selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_TRUCK + " where " + Common.ExternalDataBaseClass.TRANSPORTID + " = '" + TruckStr + "'";
            } else {
                selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_TRUCK + " where " + Common.ExternalDataBaseClass.TRUCKPLATENO + " = '" + TruckStr + "'";
            }
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                truckDetailsModel = new TruckDetailsModel();
                truckDetailsModel.setID(ID);
                truckDetailsModel.setTransportId(cursor.getInt(cursor.getColumnIndex("TransportId")));
                truckDetailsModel.setTruckLicensePlateNo(cursor.getString(cursor.getColumnIndex("TruckLicensePlateNo")));
                truckDetailsModel.setDescription(cursor.getString(cursor.getColumnIndex("Description")));
                labels.add(truckDetailsModel);
                ID = ID + 1;
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getAllTruckDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<PurchaseLogsModels> getBarCodeExternalLogDetails(String BarCode) {
        ArrayList<PurchaseLogsModels> labels = new ArrayList<>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM  " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " Where " + Common.BARCODE + "='" + BarCode + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                purchaseLogsModel = new PurchaseLogsModels();
                purchaseLogsModel.setLocationID(cursor.getInt(cursor.getColumnIndex("LocationId")));
                purchaseLogsModel.setLocationName(cursor.getString(cursor.getColumnIndex("LocationName")));
                purchaseLogsModel.setFellingSectionNumber(cursor.getString(cursor.getColumnIndex("FellingSectionNumber")));
                purchaseLogsModel.setTreeNumber(cursor.getString(cursor.getColumnIndex("TreeNumber")));
                purchaseLogsModel.setWoodSpeciesCode(cursor.getString(cursor.getColumnIndex("WoodSpeciesCode")));
                purchaseLogsModel.setF1(cursor.getInt(cursor.getColumnIndex("F1")));
                purchaseLogsModel.setF2(cursor.getInt(cursor.getColumnIndex("F2")));
                purchaseLogsModel.setT1(cursor.getInt(cursor.getColumnIndex("T1")));
                purchaseLogsModel.setT2(cursor.getInt(cursor.getColumnIndex("T2")));
                purchaseLogsModel.setLength_dm(cursor.getDouble(cursor.getColumnIndex("Length_dm")));
                purchaseLogsModel.setVolume(cursor.getDouble(cursor.getColumnIndex("Volume")));
                purchaseLogsModel.setWoodSpeciesId(cursor.getInt(cursor.getColumnIndex("WoodSpeciesId")));
                purchaseLogsModel.setFellingSectionID(cursor.getString(cursor.getColumnIndex("FellingSectionId")));
                purchaseLogsModel.setQuality(cursor.getString(cursor.getColumnIndex("LogQuality")));
                purchaseLogsModel.setQuality(cursor.getString(cursor.getColumnIndex("LogStatus")));
                purchaseLogsModel.setBarCode(cursor.getString(cursor.getColumnIndex("BarCode")));
                purchaseLogsModel.setQuality(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                purchaseLogsModel.setNoteL(cursor.getDouble(cursor.getColumnIndex("Note_F")));
                purchaseLogsModel.setNoteT(cursor.getDouble(cursor.getColumnIndex("Note_T")));
                purchaseLogsModel.setNoteL(cursor.getDouble(cursor.getColumnIndex("Note_L")));
                purchaseLogsModel.setHoleVolume(cursor.getDouble(cursor.getColumnIndex("HoleVolume")));
                purchaseLogsModel.setGrossVolume(cursor.getDouble(cursor.getColumnIndex("GrossVolume")));
                purchaseLogsModel.setUpdatedBy(cursor.getString(cursor.getColumnIndex("UpdatedBy")));
                purchaseLogsModel.setPurchaseId(cursor.getInt(cursor.getColumnIndex("PurchaseId")));
                purchaseLogsModel.setPurchaseNo(cursor.getString(cursor.getColumnIndex("PurchaseNo")));
                purchaseLogsModel.setEntryMode(cursor.getInt(cursor.getColumnIndex("EntryMode")));
                purchaseLogsModel.setLHT1(cursor.getDouble(cursor.getColumnIndex("LHT1")));
                purchaseLogsModel.setLHT2(cursor.getDouble(cursor.getColumnIndex("LHT2")));
                purchaseLogsModel.setLHF1(cursor.getDouble(cursor.getColumnIndex("LHF1")));
                purchaseLogsModel.setLHF2(cursor.getDouble(cursor.getColumnIndex("LHF2")));
                purchaseLogsModel.setLHVolume(cursor.getDouble(cursor.getColumnIndex("LHVolume")));
                labels.add(purchaseLogsModel);
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getBarCodeTransferLogDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    public ArrayList<PurchaseLogsModels> getBarCodeExternalLogDetailsWithPurchaseCode(int purchaseID, ArrayList<String> PurchaseLogsDetails) {
        ArrayList<PurchaseLogsModels> labels = new ArrayList<>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM  " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " Where " + Common.Purchase.PurchaseId + "='" + purchaseID + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                if (PurchaseLogsDetails.contains(cursor.getString(cursor.getColumnIndex("BarCode")))) {
                } else {
                    purchaseLogsModel = new PurchaseLogsModels();
                    purchaseLogsModel.setLocationID(cursor.getInt(cursor.getColumnIndex("LocationId")));
                    purchaseLogsModel.setLocationName(cursor.getString(cursor.getColumnIndex("LocationName")));
                    purchaseLogsModel.setFellingSectionNumber(cursor.getString(cursor.getColumnIndex("FellingSectionNumber")));
                    purchaseLogsModel.setTreeNumber(cursor.getString(cursor.getColumnIndex("TreeNumber")));
                    purchaseLogsModel.setWoodSpeciesCode(cursor.getString(cursor.getColumnIndex("WoodSpeciesCode")));
                    purchaseLogsModel.setF1(cursor.getInt(cursor.getColumnIndex("F1")));
                    purchaseLogsModel.setF2(cursor.getInt(cursor.getColumnIndex("F2")));
                    purchaseLogsModel.setT1(cursor.getInt(cursor.getColumnIndex("T1")));
                    purchaseLogsModel.setT2(cursor.getInt(cursor.getColumnIndex("T2")));
                    purchaseLogsModel.setLength_dm(cursor.getDouble(cursor.getColumnIndex("Length_dm")));
                    purchaseLogsModel.setVolume(cursor.getDouble(cursor.getColumnIndex("Volume")));
                    purchaseLogsModel.setWoodSpeciesId(cursor.getInt(cursor.getColumnIndex("WoodSpeciesId")));
                    purchaseLogsModel.setFellingSectionID(cursor.getString(cursor.getColumnIndex("FellingSectionId")));
                    purchaseLogsModel.setQuality(cursor.getString(cursor.getColumnIndex("LogQuality")));
                    purchaseLogsModel.setQuality(cursor.getString(cursor.getColumnIndex("LogStatus")));
                    purchaseLogsModel.setBarCode(cursor.getString(cursor.getColumnIndex("BarCode")));
                    purchaseLogsModel.setQuality(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    purchaseLogsModel.setNoteL(cursor.getDouble(cursor.getColumnIndex("Note_F")));
                    purchaseLogsModel.setNoteT(cursor.getDouble(cursor.getColumnIndex("Note_T")));
                    purchaseLogsModel.setNoteL(cursor.getDouble(cursor.getColumnIndex("Note_L")));
                    purchaseLogsModel.setHoleVolume(cursor.getDouble(cursor.getColumnIndex("HoleVolume")));
                    purchaseLogsModel.setGrossVolume(cursor.getDouble(cursor.getColumnIndex("GrossVolume")));
                    purchaseLogsModel.setUpdatedBy(cursor.getString(cursor.getColumnIndex("UpdatedBy")));
                    purchaseLogsModel.setPurchaseId(cursor.getInt(cursor.getColumnIndex("PurchaseId")));
                    purchaseLogsModel.setPurchaseNo(cursor.getString(cursor.getColumnIndex("PurchaseNo")));
                    purchaseLogsModel.setEntryMode(cursor.getInt(cursor.getColumnIndex("EntryMode")));
                    purchaseLogsModel.setLHT1(cursor.getDouble(cursor.getColumnIndex("LHT1")));
                    purchaseLogsModel.setLHT2(cursor.getDouble(cursor.getColumnIndex("LHT2")));
                    purchaseLogsModel.setLHF1(cursor.getDouble(cursor.getColumnIndex("LHF1")));
                    purchaseLogsModel.setLHF2(cursor.getDouble(cursor.getColumnIndex("LHF2")));
                    purchaseLogsModel.setLHVolume(cursor.getDouble(cursor.getColumnIndex("LHVolume")));
                    labels.add(purchaseLogsModel);
                }
            }
            mExDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("E-DB-" + "getBarCodeTransferLogDetails", e.toString(), false);
        } finally {
            mExDatabase.endTransaction();
            mExDatabase.close();
        }
        return labels;
    }

    // Purchase Delete

    public boolean DeletePurchaseAgreementsExternal(int Purchase_ID) {
        boolean Flag = true;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_PURCHASEAGREEMENT + " WHERE " + Common.Purchase.PurchaseId + " = " + Purchase_ID;
            mExDatabase.execSQL(selectQuery);
            mExDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

    public boolean DeletePurchaseExternalLogDetailsExternal(int Purchase_ID) {
        boolean Flag = true;
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "DELETE FROM " + Common.TBL_PURCHASEAGREEMENTSCANNEDLOGS + " WHERE " + Common.Purchase.PurchaseId + " = " + Purchase_ID;
            mExDatabase.execSQL(selectQuery);
            mExDatabase.setTransactionSuccessful();
            Flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox("I-DB-" + "DeleteInventoryTransferScanned", e.toString(), false);
            Flag = false;
        } finally {
            mExDatabase.endTransaction();
            closeDatabase();
        }
        return Flag;
    }

}
