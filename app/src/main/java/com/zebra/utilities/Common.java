package com.zebra.utilities;

import android.os.Environment;
import android.widget.ArrayAdapter;

import com.zebra.main.model.AdvanceSearchModel;
import com.zebra.main.model.ExternalDB.AgencyDetailsModel;
import com.zebra.main.model.ExternalDB.ConcessionNamesModel;
import com.zebra.main.model.ExternalDB.DriverDetailsModel;
import com.zebra.main.model.ExternalDB.FellingRegisterModel;
import com.zebra.main.model.ExternalDB.FellingRegisterWithPlotIDModel;
import com.zebra.main.model.ExternalDB.FellingSectionModel;
import com.zebra.main.model.ExternalDB.LoadedModel;
import com.zebra.main.model.ExternalDB.LocationsModel;


import com.zebra.main.model.ExternalDB.LocationDevicesModel;
import com.zebra.main.model.ExternalDB.LocationsModel;
import com.zebra.main.model.ExternalDB.TransferLogDetails;
import com.zebra.main.model.ExternalDB.TransferLogDetailsExModel;
import com.zebra.main.model.ExternalDB.WoodSpeciesModel;
import com.zebra.main.model.FellingRegistration.FellingTreeDetailsModel;
import com.zebra.main.model.LoginAuthenticationModel;
import com.zebra.main.model.QualityModel;
import com.zebra.main.model.SyncStatusModel;
import com.zebra.main.model.ExternalDB.TransferLogDetailsModel;
import com.zebra.main.model.ExternalDB.TransportModesModel;
import com.zebra.main.model.ExternalDB.TruckDetailsModel;
import com.zebra.main.model.FellingRegistration.FellingRegisterInputListModel;
import com.zebra.main.model.FellingRegistration.FellingRegisterListModel;
import com.zebra.main.model.FellingRegistration.FellingRegisterResultModel;
import com.zebra.main.model.InvCount.InventoryCountInputListModel;
import com.zebra.main.model.InvCount.InventoryCountModel;
import com.zebra.main.model.InvCount.InventoryCountSyncModel;
import com.zebra.main.model.InvCount.ScannedResultModel;
import com.zebra.main.model.InvReceived.InventoryReceivedInputModel;
import com.zebra.main.model.InvReceived.InventoryReceivedListModel;
import com.zebra.main.model.InvReceived.InventoryReceivedModel;
import com.zebra.main.model.InvTransfer.InventoryTransferInputListModel;
import com.zebra.main.model.InvTransfer.InventoryTransferModel;
import com.zebra.main.model.InvTransfer.InventoryTransferScannedResultModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Common {
    public static boolean AlertDialogVisibleFlag = true, AuthorizationFlag = false, ScanMode = true, IsNetworkConnection, IsConnected = true, IsExternalSync = false, IsPrintBtnClickFlag = true,
            QRCodeScan = true, QrBarCodeScan = true, isSpinnerTouched = false, IsTransferEditListFlag = true, IsReceivedEditListFlag = true, IsFellingRegEditListFlag = true,
            ScannedEditTXTFlag = false, IsEditorViewFlag = true, isFellingSpinnerTouched = true, IsSBBLabelCorrected = false, FellingRegSyncALL = false;

    public static String devAddress, TSCstatus, Printerstatus, ToLocationName, BarCode, SbbLabel, WoodSpieceID, WoodSpieceCode, DateTime, IMEI, StartDate,
            EndDate, ReceivedDate, SyncTime, OrgSBBLabel, ServiceURLPath, Length, InventoryErrorMsg, RemoveSBBLabel, FromLocationname, AgencyName, TrucklicensePlateNo,
            DriverName, NewClassificationName, NewSbblabel, VBB_Number, Volume, TransportMode, PACKAGE_NAME, VersionName, FellingSectionName, FellingSectionId, FellingSectionNumber, TreeNumber,
            QualityName, CheckedFlag, CountUniqueID, TransferUniqueID, ReceivedUniqueID, TransferReciveUniqueID, FellingRegUniqueID, FellingRegDate, DeviceName, AppPackageName,
            SyncStartDateTime, SyncEndDateTime, AppUpdateURL, VersionNamefromWebApi, FellingRegNo = "", FsWoodSpieceCode = "", FsWoodSpieceID = "", FsTreeNumber = "",
            RecFromLocationname, LoadedName, OldWSCode, TreeDF1, TreeDF2, TreeDT1, TreeDT2, TreeLenght, SbbLabelDF1, SbbLabelDF2, SbbLabelDT1, SbbLabelDT2, SbbLabelLenght,
            SbbLabelNoteF, SbbLabelNoteT, SbbLabelNoteL, UpdateSBBLabel, UpdateFellRegUnique, PlotNo, OldPlotNo, TreePart, UpdatedTreeNumber, ReceivedLoadedTypeName, ReceivedTransferID;

    public static int SPLASH_TIME_OUT = 3000;
    public static int NETWORK_TIME_OUT = 10000;
    public static final int VVBLimitation = 21, FellingRegLimit = 17, SBBlenght = 7, ScannedValueLenght = 10, MinimumScannedItemSize = 5;

    public static int DATABASE_VERSION = 1, IsActive, EntryMode = 1, ListID = 0, ReceivedID = 0, TransferID = 0, SyncStatus, UserID, SyncListID, SyncBarCodeCount,
            LocationID, TransferAgencyID, DriverID, Count = 0, FromLocationID, TransportTypeId = 1, VersionCode, ToLocationID, FromTransLocID, ToLocaTransID, TransportId,
            classificationID, InventoryPageID = 1, CheckedSize = 0, TotalReceivedVVBLimitation, LDeviceID, ToLocReceivedID, FellingRegID, HttpResponceCode,
            AgencyDetailsIndex = 0, ConcessionNamesIndex = 0, DrivedDetailsIndex = 0, FellingRegistrationIndex = 0, FellingSectionIndex = 0, LocationDeviceIndex = 0,
            LocationsIndex = 0, TransferLogDetilsIndex = 0, TransportModesIndex = 0, TruckDetailsIndex = 0, WoodSpicesIndex = 0, LoadedIndex = 0, RecFromLocationID,
            IsNewTreeNumber = 0, IsNewWSCode = 0, LoadedTypeID = 1, ReceivedLoadedTypeID = 0, TreeFromLocation, PlotId, IsNewPlotNumber = 0, IsNewWoodSpiceCode = 0;
    // Common.TotalfellingRegisterScannedItems= cursor.getCount();
    public static double VolumeSum, DensityOFWood = 0.7854;
    public static String INTERNAL_DB_Path = "/data/com.zebra/databases/GWW.db";
    public static String INTERNAL_DB_Path1 = "/data/com.zebra/databases/";
    public static String EXTERNAL_MASTER_DB_NAME = "GWW.db";
    public static final String INTERNELDBNAME = "GWWINTERNAL.db";
    public static String[] Tabs = {"FELLING REGISTRATION", "INVENTORY COUNT", "INVENTORY TRANSFER", "INVENTORY RECEIVED"};

    public static DecimalFormat decimalFormat = new DecimalFormat("###,###.####");

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    public static SimpleDateFormat UniqueIDdateFormat = new SimpleDateFormat("yyyyMMdd");

    /*Inventory Count Details*/
    public static ArrayList<ScannedResultModel> ScannnedResultExportList = new ArrayList<ScannedResultModel>();
    public static ArrayList<InventoryCountModel> InventoryCountList = new ArrayList<InventoryCountModel>();
    public static ArrayList<InventoryCountInputListModel> InventoryCountInputList = new ArrayList<InventoryCountInputListModel>();

    public static ArrayList<InventoryCountSyncModel> InventoryCountSyncModel = new ArrayList<com.zebra.main.model.InvCount.InventoryCountSyncModel>();
    public static ArrayList<SyncStatusModel> SyncStatusList = new ArrayList<SyncStatusModel>();
    /*Inventory Transfer Details*/
    public static ArrayList<AgencyDetailsModel> TransportAgencyList = new ArrayList<AgencyDetailsModel>();
    public static ArrayList<ConcessionNamesModel> ConcessionList = new ArrayList<ConcessionNamesModel>();
    public static ArrayList<DriverDetailsModel> DriverList = new ArrayList<DriverDetailsModel>();
    public static ArrayList<FellingSectionModel> FellingSectionList = new ArrayList<FellingSectionModel>();
    public static ArrayList<LocationDevicesModel> LocationDeviceList = new ArrayList<LocationDevicesModel>();
    public static ArrayList<LocationsModel> LocationList = new ArrayList<LocationsModel>();
    public static ArrayList<TransferLogDetailsModel> SearchedTransLogDetils = new ArrayList<TransferLogDetailsModel>();
    public static ArrayList<TransportModesModel> TransportModeList = new ArrayList<TransportModesModel>();
    public static ArrayList<LoadedModel> LoadedByList = new ArrayList<LoadedModel>();
    public static ArrayList<TruckDetailsModel> TruckDeatilsList = new ArrayList<TruckDetailsModel>();
    public static ArrayList<WoodSpeciesModel> WoodSpeicesDeatilsList = new ArrayList<WoodSpeciesModel>();
    public static ArrayList<WoodSpeciesModel> WoodSpeicesFilterDeatilsList = new ArrayList<WoodSpeciesModel>();

    public static String[] ConcessionListStringList;
    public static String[] DriverListStringList;
    public static String[] AgencyDetailsStringList;
    public static String[] TruckDetialsStringList;
    public static ArrayList<InventoryTransferScannedResultModel> InventorytransferScannedResultList = new ArrayList<InventoryTransferScannedResultModel>();
    public static ArrayList<InventoryTransferModel> InventoryTransferList = new ArrayList<InventoryTransferModel>();
    public static ArrayList<InventoryTransferModel> InventoryTransferListDuplicate = new ArrayList<InventoryTransferModel>();
    // Tabel Columns Variable

    public static Map<String, ArrayList<TransferLogDetailsModel>> AllTransLogDetailsmap = new HashMap<>();
    public static ArrayList<ScannedResultModel> ScannnedResultList = new ArrayList<ScannedResultModel>();
    public static ArrayList<AdvanceSearchModel> AdvancedSearchList = new ArrayList<AdvanceSearchModel>();


    public static ArrayList<InventoryTransferInputListModel> InventoryTransferInputList = new ArrayList<InventoryTransferInputListModel>();


    public static ArrayAdapter<String> mPairedDevicesArrayAdapter;

    public static String Username, Password;
    public static ArrayList<LoginAuthenticationModel> LoginDetailsList = new ArrayList<LoginAuthenticationModel>();
    public static ArrayList<QualityModel> classificationlist = new ArrayList<QualityModel>();

    public static ArrayList<InventoryReceivedListModel> InventoryReceivedList = new ArrayList<InventoryReceivedListModel>();
    public static ArrayList<InventoryReceivedModel> InventoryReceivedScannedResultList = new ArrayList<InventoryReceivedModel>();
    public static ArrayList<InventoryReceivedInputModel> InventoryReceivedInputList = new ArrayList<InventoryReceivedInputModel>();

    public static ArrayList<String> QulaityDefaultList = new ArrayList<>();
    public static ArrayList<String> TransferIDsList = new ArrayList<>();
    public static String[] TransferIDsStringList;
    public static String[] FelllingSectionIDsStringList;

    public static ArrayList<FellingRegisterModel> FellingRegTreeFilterList = new ArrayList<FellingRegisterModel>();
    public static ArrayList<FellingRegisterWithPlotIDModel> FellingRegPlotNumberFilterList = new ArrayList<FellingRegisterWithPlotIDModel>();
    public static ArrayList<FellingRegisterWithPlotIDModel> FellingRegPlotNumberFilterSinglrList = new ArrayList<FellingRegisterWithPlotIDModel>();
    public static ArrayList<FellingRegisterListModel> FellingRegisterList = new ArrayList<FellingRegisterListModel>();
    public static ArrayList<FellingRegisterResultModel> FellingRegisterLogsDetails = new ArrayList<FellingRegisterResultModel>();
    public static ArrayList<FellingRegisterInputListModel> FellingRegisterInputList = new ArrayList<FellingRegisterInputListModel>();
    public static ArrayList<FellingTreeDetailsModel> FellingTreeDetailsCheckList = new ArrayList<FellingTreeDetailsModel>();
    public static ArrayList<FellingTreeDetailsModel> FellingTreeDetailsList = new ArrayList<FellingTreeDetailsModel>();
    public static String[] FellingRegTreeNoStringList;
    public static String[] FellingRegPlotNoStringList;
    public static String[] FellingRegTreePartStringList = new String[]{"A", "B", "C", "D"};
    public static String[] FellingRegExcelExportList = new String[]{"FellingRegistrationList", "FellingRegistrationDetails"};
    public static ArrayList<FellingRegisterResultModel> FellingRegisterLogsExportDetails = new ArrayList<FellingRegisterResultModel>();

    public static String[] FellingRegWoodSpeicesStringList;
    public static ArrayList<String> TreeNosList = new ArrayList<>();


    /*DataBase Details*/
    public static final String TBL_SCANNEDRESULT = "ScannedResult", TBL_INVENTORYCOUNTLIST = "InventoryCountList",
            TBL_INVENTORYTRANSFERSCANNED = "InventoryTransfer", TBL_INVENTORYTRANSFERIDLIST = "InventoryTransferList",
            TBL_INVENTORYRECEIVED = "InventoryReceived", TBL_INVENTORYRECEIVEDLIST = "InventoryReceivedList",
            TBL_FELLINGREGISTRATIONLIST = "FellingRegistrationList", TBL_FELLINGREGISTRATIONDETAILS = "FellingRegistrationDetails",
            TBL_LOGINAUTHENTICATION = "LoginAuthentication", TBL_FELLINGTREEDETAILS = "FellingTreeDetails";
    public static final String LOCATION_ID = "ToLocationID", LOCATION_NAME = "ToLocationName", FROMLOCATION = "FromLocation", FROMLOCATIONID = "FromLocationID", SBBLabel = "SbbLabel",
            WoodSpiceID = "WoodSpieceID", WoodSPiceCode = "WoodSpieceCode", DATETIME = "DateTime", IMEINumber = "IMEI", BARCODE = "BarCode", ISACTIVE = "IsActive", ENTRYMODE = "EntryMode",
            LISTID = "ListID", ISSBLABELCORRECT = "IsSBBLabelCorrected", ORGSBBLABEL = "OrgSBBLabel", VBBNUMBER = "VBB_Number", STARTDATETIME = "StartDateTime", ENDDATETIME = "EndDateTime",
            COUNT = "TotalCount", SYNCSTATUS = "SyncStatus", SYNCTIME = "SyncTime", TOLOCATION = "ToLocation", TRANSFERID = "TransferID", TRANSPORTTYPEID = "TransportTypeId",
            TRANSFERAGENCYID = "TransferAgencyID", DRIVERID = "DriverID", TRUCKPLATENUMBER = "TruckPlateNumber", LENGTH = "Length", VOLUME = "Volume", SYNCDEVICETIME = "SyncDeviceDateTime",
            USERID = "UserID", FELLING_SECTIONID = "FellingSectionId", ISCHECKED = "IsReceived", TREENO = "TreeNumber", QULAITY = "Quality", RECEIVEDID = "ReceivedID",
            TRANSFERUNIQUEID = "TransferUniqueID", USER_NAME = "UserName", PASSWORD = "Password", FELLINGREGID = "FellingRegID", FELLINGREGNO = "FellingRegistrationNumber",
            FELLINGREGDATE = "FellingRegistrationDate", LOCATIONID = "LocationID", FELLIINGREGUNIQUEID = "FellingRegistrationUniqueID", RECEIVEDUNIQUEID = "ReceivedUniqueID",
            COUNTUNIQUEID = "CountUniqueID", ISNEWTREENO = "IsNewTreeNumber", ISWOODSPECODE = "IsWoodSpieceCode", ISOLDWOODSPECODE = "IsOldWoodSpieceCode", OLDWOODSPECODE = "OldWoodSpieceCode",
            LOADEDID = "Loadedid", NAME = "Name", LOADEDTYPE = "LoadedTypeID", DF1 = "Footer_1", DF2 = "Footer_2", DT1 = "Top_1", DT2 = "Top_2", NOTET = "NoteT", NOTEF = "NoteF", NOTEL = "NoteL",
            PLOTID = "PlotId", PLOTNO = "PlotNo", OLDPLOTNO = "OldPlotNo", TREEPARTTYPE = "TreePartType", ISNEWPLOTNO = "IsNewPlotNumber", LOCATION = "Location";

    public class ExternalDataBaseClass {
        public static final String WOODSpeciesCODE = "WoodSpeciesCode", TBL_TRANSFERLOG = "TransferLogDetails", TBL_CONCESSION = "ConcessionNames", TBL_DRIVER = "DriverDetails",
                TBL_AGENCY = "AgencyDetails", TBL_TRUCK = "TruckDetails", TBL_TRANSPORTMODE = "TransportModes", TBL_LOCATIONDEVICE = "LocationDevices", TBL_LOCATIONS = "Locations",
                TBL_FELLINGSECTION = "FellingSection", TBL_FELLINGREGISTER = "FellingRegister", TBL_CLASSIFICATION = "Classifications", TBL_WOODSPEICES = "WoodSpecies", TBL_LOADED = "Loaded";
        public static final String TREENUMBER = "TreeNumber", FELLINGSECTIONID = "FellingSectionId", LOGSTATUS = "LogStatus",
                LOCATIONDID = "LocationId", LOCNAME = "LocationName", FELLINGSECNO = "FellingSectionNumber", HARCROPID = "HarvestCropsId", INSTOCKID = "InStockId",
                LENGHTDM = "Length_dm", WODESPEICEID = "WoodSpeciesId", AGENCYID = "AgencyId", AGENCYNAME = "AgencyName", ADDRESS = "Address", ConcessionID = "ConcessionId",
                TRANSPORTID = "TransportId", TRUCKPLATENO = "TruckLicensePlateNo", DESCRIPTION = "Description", TRUCKDRIVERID = "TruckDriverId", DRIVERLICNO = "DriverLicenseNo",
                DRIVERNAME = "DriverName", ExPLOTNO = "PlotNumber", TRANSPORTTYPEID = "TransportTypeId", TRANSPORTMODE = "TransportMode", CONCESSIONNAME = "ConcessionName", FELLINGCODE = "FellingCode",
                LOCATIONTYPE = "LocationType", FROMLOCATIONId = "FellingSectionId", ISDEFAULT = "IsDefault", LDEVID = "LDevId", DEVICENAME = "DeviceName";
    }

    public static ArrayList<AgencyDetailsModel> AgencyDetailsExSync = new ArrayList<AgencyDetailsModel>();
    public static ArrayList<DriverDetailsModel> DriverDetailsExSync = new ArrayList<DriverDetailsModel>();
    public static ArrayList<TransferLogDetailsExModel> TransferLogDetailsExSync = new ArrayList<TransferLogDetailsExModel>();
    public static ArrayList<TruckDetailsModel> TruckDetailsExSync = new ArrayList<TruckDetailsModel>();
    public static ArrayList<FellingRegisterModel> fellingRegisterSync = new ArrayList<FellingRegisterModel>();
    public static ArrayList<FellingSectionModel> fellingSectionSync = new ArrayList<FellingSectionModel>();
}
