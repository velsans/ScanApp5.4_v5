package com.zebra.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zebra.main.model.AdvanceSearchModel;
import com.zebra.main.model.ExternalDB.AgencyDetailsModel;
import com.zebra.main.model.ExternalDB.ConcessionNamesModel;
import com.zebra.main.model.ExternalDB.DriverDetailsModel;
import com.zebra.main.model.ExternalDB.FellingRegisterWithPlotIDModel;
import com.zebra.main.model.ExternalDB.FellingSectionModel;
import com.zebra.main.model.ExternalDB.LoadedModel;
import com.zebra.main.model.ExternalDB.LocationsModel;
import com.zebra.main.model.ExternalDB.TruckDetailsModel;


import com.zebra.main.model.ExternalDB.LocationDevicesModel;
import com.zebra.main.model.ExternalDB.WoodSpeciesModel;
import com.zebra.main.model.QualityModel;
import com.zebra.main.model.ExternalDB.TransferLogDetailsModel;
import com.zebra.main.model.ExternalDB.TransportModesModel;
import com.zebra.main.model.ExternalDB.FellingRegisterModel;


import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExternalDataBaseHelperClass extends SQLiteOpenHelper {
    AlertDialogManager alert = new AlertDialogManager();

    private Context mContext;
    private SQLiteDatabase mExDatabase;
    Common com_obj;
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

    public ExternalDataBaseHelperClass(Context context) {
        super(context, Common.EXTERNAL_MASTER_DB_NAME, null, 1);
        this.mContext = context;
        com_obj = new Common();
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
                labels = (cursor.getString(cursor.getColumnIndex("AgencyName")));
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
                driverDetailsModel.setDriverName(cursor.getString(cursor.getColumnIndex("DriverName")));
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
                labels = (cursor.getString(cursor.getColumnIndex("DriverName")));
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

    public ArrayList<LocationDevicesModel> getAllLocationDevice(String IMEI_Number) {
        ArrayList<LocationDevicesModel> labels = new ArrayList<LocationDevicesModel>();
        mExDatabase = this.getReadableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + Common.ExternalDataBaseClass.TBL_LOCATIONDEVICE + " where IMEI = '" + IMEI_Number + "'";
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                locationDeviceModel = new LocationDevicesModel();
                locationDeviceModel.setLDevId(cursor.getInt(cursor.getColumnIndex("LDevId")));
                locationDeviceModel.setLocationId(cursor.getInt(cursor.getColumnIndex("LocationId")));
                locationDeviceModel.setDeviceName(cursor.getString(cursor.getColumnIndex("DeviceName")));
                locationDeviceModel.setIMEI(cursor.getString(cursor.getColumnIndex("IMEI")));
                locationDeviceModel.setIsDefault(cursor.getInt(cursor.getColumnIndex("IsDefault")));
                locationDeviceModel.setFromLocationId(cursor.getInt(cursor.getColumnIndex("FromLocationId")));
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
            ;
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
            String selectQuery = "SELECT DISTINCT PlotId,PlotNumber  FROM " + Common.ExternalDataBaseClass.TBL_FELLINGREGISTER + " where " + Common.ExternalDataBaseClass.ExPLOTNO + "='" + PlotNumber + "' and "+Common.FELLING_SECTIONID + "='" + Common.FellingSectionId + "'";
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

    /*InsertAgencyDetails*/
    public void insertAgencyDetails(int RowID, int AgencyID, String AgencyName, String AgencyAddress) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                values.put("RowID", RowID);
                values.put(Common.ExternalDataBaseClass.AGENCYID, AgencyID);
                values.put(Common.ExternalDataBaseClass.AGENCYNAME, AgencyName);
                values.put(Common.ExternalDataBaseClass.ADDRESS, AgencyAddress);
                mExDatabase.insert(Common.ExternalDataBaseClass.TBL_AGENCY, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertAgencyDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>>", "db is null");
    }

    public void insertDriverDetails(int RowID, int TruckDriverId, String DriverLicenseNo, String DriverName) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                values.put("RowID", RowID);
                values.put(Common.ExternalDataBaseClass.TRUCKDRIVERID, TruckDriverId);
                values.put(Common.ExternalDataBaseClass.DRIVERLICNO, DriverLicenseNo);
                values.put(Common.ExternalDataBaseClass.DRIVERNAME, DriverName);
                mExDatabase.insert(Common.ExternalDataBaseClass.TBL_DRIVER, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertAgencyDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>>", "db is null");
    }

    public void insertTruckDetails(int RowID, int transportID, String TruckplatNo, String Description) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                values.put("RowID", RowID);
                values.put(Common.ExternalDataBaseClass.TRANSPORTID, transportID);
                values.put(Common.ExternalDataBaseClass.TRUCKPLATENO, TruckplatNo);
                values.put(Common.ExternalDataBaseClass.DESCRIPTION, Description);
                mExDatabase.insert(Common.ExternalDataBaseClass.TBL_TRUCK, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertAgencyDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>>", "db is null");
    }

    public void insertTransferLogDetails(int RowID, String LocID, String LocName, String PlotNo, String FellingSecNum, String HarrcropID, String InstockID, String treeNo, String WScode, String SbbLabel,
                                         String F1, String F2, String T1, String T2, String LenghtDM, String Volume, String WSID, String FellingSecID, String Quality, String LogStats) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                values.put("RowID", RowID);
                values.put(Common.ExternalDataBaseClass.LOCATIONDID, LocID);
                values.put(Common.ExternalDataBaseClass.LOCNAME, LocName);
                values.put(Common.PLOTNO, PlotNo);
                values.put(Common.ExternalDataBaseClass.FELLINGSECNO, FellingSecNum);
                values.put(Common.ExternalDataBaseClass.HARCROPID, HarrcropID);
                values.put(Common.ExternalDataBaseClass.INSTOCKID, InstockID);
                values.put(Common.TREENO, treeNo);
                values.put(Common.ExternalDataBaseClass.WOODSpeciesCODE, WScode);
                values.put(Common.SBBLabel, SbbLabel);
                values.put("F1", F1);
                values.put("F2", F2);
                values.put("T1", T1);
                values.put("T2", T2);
                values.put(Common.ExternalDataBaseClass.LENGHTDM, LenghtDM);
                values.put(Common.VOLUME, Volume);
                values.put(Common.ExternalDataBaseClass.WODESPEICEID, WSID);
                values.put(Common.ExternalDataBaseClass.FELLINGSECTIONID, FellingSecID);
                values.put("LogQuality", Quality);
                values.put(Common.ExternalDataBaseClass.LOGSTATUS, LogStats);
                mExDatabase.insert(Common.ExternalDataBaseClass.TBL_TRANSFERLOG, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertAgencyDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else {
            Log.d(">>>>>>", "db is null");
        }
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
            Log.d(">>>>>>", "db is null");
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
            Log.d(">>>>>>", "db is null");
    }

    // FellingRegister
    public void insertFellingRegister(int RowID, int ConcessionId, int FellingSectionId, int FellingSectionNumber, int PlotId, int TreeNumber, int WoodSpeciesId, String ConcessionName, String FellingCode, String PlotNumber, String WoodSpeciesCode) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                values.put("RowID", RowID);
                values.put(Common.ExternalDataBaseClass.ConcessionID, ConcessionId);
                values.put(Common.ExternalDataBaseClass.FELLINGSECTIONID, FellingSectionId);
                values.put(Common.ExternalDataBaseClass.FELLINGSECNO, FellingSectionNumber);
                values.put(Common.PLOTID, PlotId);
                values.put(Common.ExternalDataBaseClass.TREENUMBER, TreeNumber);
                values.put(Common.ExternalDataBaseClass.WODESPEICEID, WoodSpeciesId);
                values.put(Common.ExternalDataBaseClass.CONCESSIONNAME, ConcessionName);
                values.put(Common.ExternalDataBaseClass.FELLINGCODE, FellingCode);
                values.put(Common.ExternalDataBaseClass.ExPLOTNO, PlotNumber);
                values.put(Common.ExternalDataBaseClass.WOODSpeciesCODE, WoodSpeciesCode);
                mExDatabase.insert(Common.ExternalDataBaseClass.TBL_FELLINGREGISTER, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertfellingregisterDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>>", "db is null");

    }

    //FellingSection
    public void insertFellingSection(int RowID,int ConcessionId, int FellingSectionNumber, int LocationType, String ConcessionName, String FellingSectionId, String FellingCode) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                values.put("RowID", RowID);
                values.put(Common.ExternalDataBaseClass.ConcessionID, ConcessionId);
                values.put(Common.ExternalDataBaseClass.CONCESSIONNAME, ConcessionName);
                values.put(Common.ExternalDataBaseClass.FELLINGSECTIONID, FellingSectionId);
                values.put(Common.ExternalDataBaseClass.FELLINGSECNO, FellingSectionNumber);
                values.put(Common.ExternalDataBaseClass.FELLINGCODE, FellingCode);
                values.put(Common.ExternalDataBaseClass.LOCATIONTYPE, LocationType);
                mExDatabase.insert(Common.ExternalDataBaseClass.TBL_FELLINGSECTION, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertfellingsectionDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>>", "db is null");
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
            Log.d(">>>>>>", "db is null");
    }

    //LocationDevices
    public void insertLocationDevices(int LDevId, int LocationId, int IsDefault, int FromLocationId, String IMEI, String DeviceName) {
        mExDatabase = this.getWritableDatabase();
        if (null != mExDatabase) {
            try {
                ContentValues values = new ContentValues();
                values.put(Common.ExternalDataBaseClass.LDEVID, LDevId);
                values.put(Common.ExternalDataBaseClass.LOCATIONDID, LocationId);
                values.put(Common.ExternalDataBaseClass.ISDEFAULT, IsDefault);
                values.put(Common.ExternalDataBaseClass.FROMLOCATIONId, FromLocationId);
                values.put(Common.IMEINumber, IMEI);
                values.put(Common.ExternalDataBaseClass.DEVICENAME, DeviceName);
                mExDatabase.insert(Common.ExternalDataBaseClass.TBL_LOCATIONDEVICE, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBox("EI-DB-" + "insertLocationDevicesDetails", e.toString(), false);
            } finally {
                mExDatabase.close();
            }
        } else
            Log.d(">>>>>>", "db is null");
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
            Log.d(">>>>>>", "db is null");
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
            Log.d(">>>>>>", "db is null");
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
    public String getLastAgencyD() {
        String LastIndex = "";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_AGENCY;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = (cursor.getString(cursor.getColumnIndex("LastIndex")));
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

    public String getLastConsessionID() {
        String LastIndex = "";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_CONCESSION;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = (cursor.getString(cursor.getColumnIndex("LastIndex")));
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

    public String getLastDriverD() {
        String LastIndex = "";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_DRIVER;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = (cursor.getString(cursor.getColumnIndex("LastIndex")));
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

    public String getLastFellingSectionID() {
        String LastIndex = "";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_FELLINGSECTION;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = (cursor.getString(cursor.getColumnIndex("LastIndex")));
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

    public String getLastFellingRegisterID() {
        String LastIndex = "";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_FELLINGREGISTER;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = (cursor.getString(cursor.getColumnIndex("LastIndex")));
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

    public String getLastLocationDeviceID() {
        String LastIndex = "";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_LOCATIONDEVICE;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = (cursor.getString(cursor.getColumnIndex("LastIndex")));
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

    public String getLastLocationsID() {
        String LastIndex = "";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_LOCATIONS;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = (cursor.getString(cursor.getColumnIndex("LastIndex")));
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

    public String getLastTransferLogDetailsID() {
        String LastIndex = "";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_TRANSFERLOG;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = (cursor.getString(cursor.getColumnIndex("LastIndex")));
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

    public String getLastTransportModesID() {
        String LastIndex = "";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_TRANSPORTMODE;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = (cursor.getString(cursor.getColumnIndex("LastIndex")));
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

    public String getLastTruckDetailsID() {
        String LastIndex = "";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_TRUCK;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = (cursor.getString(cursor.getColumnIndex("LastIndex")));
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

    public String getLastWoodSpieceID() {
        String LastIndex = "";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_WOODSPEICES;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = (cursor.getString(cursor.getColumnIndex("LastIndex")));
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

    public String getLoadedRowID() {
        String LastIndex = "";
        mExDatabase = this.getWritableDatabase();
        mExDatabase.beginTransaction();
        try {
            String selectQuery = "SELECT MAX(RowID) as LastIndex FROM " + Common.ExternalDataBaseClass.TBL_LOADED;
            Cursor cursor = mExDatabase.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                LastIndex = (cursor.getString(cursor.getColumnIndex("LastIndex")));
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


    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str == "null";
    }
}
