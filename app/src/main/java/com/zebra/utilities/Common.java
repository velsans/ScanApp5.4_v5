package com.zebra.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import com.zebra.R;
import com.zebra.main.activity.purchase.ui.logs.PurchaseLogsModels;
import com.zebra.main.activity.purchase.ui.logs.PurchaseLogsSyncModel;
import com.zebra.main.activity.purchase.ui.received.PurchaseReceivedModel;
import com.zebra.main.activity.purchase.ui.transfer.PurchaseTransferModel;
import com.zebra.main.model.AdvanceSearchModel;
import com.zebra.main.model.Export.ContainersModel;
import com.zebra.main.model.Export.ExportContainerDetailsModel;
import com.zebra.main.model.Export.ExportDetailsModel;
import com.zebra.main.model.Export.ExportModel;
import com.zebra.main.model.Export.ExportSbblabelOutputModel;
import com.zebra.main.model.Export.LogDetailsModel;
import com.zebra.main.model.Export.LogSummaryModel;
import com.zebra.main.model.Export.QuotationInternalModel;
import com.zebra.main.model.Export.StatusVerificationModel;
import com.zebra.main.model.Export.SyncStatusInputModel;
import com.zebra.main.model.Export.TotalCBMDetailsModel;
import com.zebra.main.model.Export.WoodSpeiceModel;
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
import com.zebra.main.model.externaldb.TruckDetailsExternalModel;
import com.zebra.main.model.externaldb.WoodSpeciesModel;
import com.zebra.main.model.FellingRegistration.FellingTreeDetailsModel;
import com.zebra.main.model.InvReceived.ReceivedLogsModel;
import com.zebra.main.model.LoginAuthenticationModel;
import com.zebra.main.model.QualityModel;
import com.zebra.main.model.SyncStatusModel;
import com.zebra.main.model.externaldb.TransferLogDetailsModel;
import com.zebra.main.model.externaldb.TransportModesModel;
import com.zebra.main.model.externaldb.TruckDetailsModel;
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
import com.zebra.main.model.vessel.VesselListModel;
import com.zebra.main.model.vessel.VesselLogDetailsPrintModel;
import com.zebra.main.model.vessel.VesselLogsDetailsModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Common {
    /*SVN Sample*/
    public static boolean AlertDialogVisibleFlag = true, AuthorizationFlag = false, ScanMode = true, IsConnected = true, IsExternalSync = false,
            IsPrintBtnClickFlag = true, QRCodeScan = true, QrBarCodeScan = true, isSpinnerTouched = false, IsTransferEditListFlag = true, IsReceivedEditListFlag = true,
            IsFellingRegEditListFlag = true, ScannedEditTXTFlag = false, IsEditorViewFlag = true, IsSBBLabelCorrected = false, IsVesselEditListFlag = true,
            FellingRegSyncALL = false, InventoryCountSyncALL = false, InventoryTransferSyncALL = false, InventoryReceivedSyncALL = false, IsExportEditListFlag = true,
            IsNewTreeNumberAdded = false, IsExportSelectFlag = false, IsUpdateTruckPlateNo = false, IsQuotationNew = true,
            IsContainerSelectFlag = false, IsContainerSelectFilterFlag = false, IsVolumeCalculationFlag = false, IsExternalSyncFlag, IsProgressVisible = true, BarcodeScannerFlagForExport = false,
            BarcodeScannerforLogdetails = false, BarcodeScannerforVessel = false, ExternalSyncFlag = true, AutoGenerateTreeNoFlag = true;

    public static final String EXTERNAL_MASTER_DB_NAME = "GWW.db", ServiceBASE_URLNetwork = "", INTERNELDBNAME = "GWWINTERNAL.db";

    public static String devAddress, TSCstatus, Printerstatus, Username, Password, ToLocationName, BarCode, SbbLabel, OLDWoodSpieceCode, WoodSpieceCode, DateTime, IMEI, StartDate,
            EndDate, ReceivedDate, SyncTime, OrgBarCode, OrgSBBLabel, ServiceURLPath, Length, InventoryErrorMsg, RemoveSBBLabel, FromLocationname, AgencyName,
            TrucklicensePlateNo, DriverName, NewClassificationName, NewSbblabel, VBB_Number, Order_Number, QuotationNo, ContainerNo, OLDVolume, Volume, TransportMode,
            PACKAGE_NAME, VersionName, FellingSectionName, FellingSectionId, FellingSectionNumber, TreeNumber, QualityName, CheckedFlag, CountUniqueID, TransferUniqueID,
            ReceivedUniqueID, TransferReciveUniqueID, FellingRegUniqueID, FellingRegDate, DeviceName, AppPackageName, SyncStartDateTime, SyncEndDateTime, AppUpdateURL,
            VersionNamefromWebApi, FellingRegNo = "", FsWoodSpieceCode = "", FsWoodSpieceID = "", FsTreeNumber = "", RecFromLocationname, LoadedName, OldWSCode, TreeDF1,
            TreeDF2, TreeDT1, TreeDT2, TreeLenght, SbbLabelDF1, SbbLabelDF2, SbbLabelDT1, SbbLabelDT2, SbbLabelLenght, SbbLabelNoteF, SbbLabelNoteT, SbbLabelNoteL,
            UpdateBarCode, UpdateSBBLabel, UpdateFellRegUnique, PlotNo, OldPlotNo, TreePart, UpdatedTreeNumber, ReceivedLoadedTypeName, ReceivedTransferID, ExportUniqueID,
            WoodSpieceID, Export_Diameter, Export_OLDDiameter, Export_Sbblabel, Export_WSC, Export_PvNo, Export_PvDate, Export_AgeOFLog, Export_NoteT, Export_NoteF,
            Export_NoteL, Export_Length, Export_Barcode, Export_Volume = "0.0", Selected_QutWsCode, Selected_QutDiameter, Selected_QutTotCBM, HoleVolume="0.0", GrossVolume, QuotationUniqueNo,
            INTERNAL_DB_Path = "", StuffingDate, BookingNumber, CuttOffDateTime, ShippingAgentId, Export_Remarks = "", ExportCode, Export_ContainerCount, SbbLabelLHT1, SbbLabelLHT2,
            SbbLabelLHF1, SbbLabelLHF2, SbbLabelLHVolume = "0.0", LengthCutVolume = "0.0", CrackVolume = "0.0",SapVolume = "0.0";

    public static final int VVBLimitation = 21, FellingRegLimit = 17, SBBlenght = 10, SBBlenghtVessel = 11, ContainerSealNolenght = 13, ScannedValueLenght = 10, MinimumScannedItemSize = 5, SPLASH_TIME_OUT = 1500,
            NETWORK_TIME_OUT = 18000, NETWORK_API_TIME_OUT = 300;

    public static int DATABASE_VERSION = 1, IsActive, EntryMode = 1, ListID = 0, ReceivedID = 0, TransferID = 0, SyncStatus, UserID, SyncListID, SyncBarCodeCount,
            LocationID, TransferAgencyID, DriverID, Count = 0, FromLocationID, TransportTypeId = 1, VersionCode, ToLocationID, FromTransLocID, ToLocaTransID, TransportId,
            classificationID, InventoryPageID = 1, CheckedSize = 0, TotalReceivedVVBLimitation, LDeviceID, ToLocReceivedID, FellingRegID, HttpResponceCode,
            AgencyDetailsIndex = 0, ConcessionNamesIndex = 0, DrivedDetailsIndex = 0, FellingRegistrationIndex = 0, FellingSectionIndex = 0, LocationDeviceIndex = 0,
            LocationsIndex = 0, TransferLogDetilsIndex = 0, TransportModesIndex = 0, TruckDetailsIndex = 0, WoodSpicesIndex = 0, LoadedIndex = 0, RecFromLocationID,
            IsNewTreeNumber = 0, IsNewWSCode = 0, LoadedTypeID = 2, ReceivedLoadedTypeID = 0, TreeFromLocation, PlotId, IsNewPlotNumber = 0, IsNewWoodSpiceCode = 0,
            InveCountSyncALlIndex = 0, InveTransferSyncALlIndex = 0, InveReceivedSyncALlIndex = 0, FellingRegSyncALlIndex = 0, InventCountDateSelectedIndex = 0,
            InventTransDateSelectedIndex = 0, InventReceivedDateSelectedIndex = 0, FellingRegSelectedIndex = 0, ExportDateSelectedIndex = 0, ExportInterQuoSelectedIndex = 0,
            Export_Count, ExportID, IsValidVolume = 0, IsValidWSCode, IsValidDiameter, ExternalSyncLimit = 500, ContainerLimit = 11, QuotationEntryFlag,
            QuotationID, ContainerFilterpostion = 0, ContainerID, Export_Footer_1 = 0, Export_Footer_2 = 0, Export_Top_1 = 0, Export_Top_2 = 0, Export_UpdateFlag,
            SelectedTabPosition = 0, PurchaseLogsIndex = 0, PurchaseScannedTabs = 0;
    public static final double DensityOFWood = 0.7854, ExportSingleContainerTotValue = 20.5, TreelenghtLimit = 30.0;
    public static double VolumeSum;

    public static final String[] ExportDetailsTabs = {"QUOTATIONS", "CONTAINER", "LOGS"},
            defaultDevicePer = {"Count", "LoadedBy Truck", "LoadedBy Boat", "Received", "Fuel Filling",
                    "Felling Registration", "Export", "External Purchase"};
    public static final int[] defaultDevicePer_img = {R.mipmap.count, R.mipmap.trans_truck, R.mipmap.trans_boat,
            R.mipmap.received, R.mipmap.fuel_filling, R.mipmap.fellingregistration, R.mipmap.export, R.mipmap.external_purchase};
    public static ArrayList<String> POSDefault_ClientProducts = new ArrayList<>();
    public static ArrayList<Bitmap> POSDefault_ClientProducts_img = new ArrayList<>();
    public static ArrayList<String> Filter_ClientProdName = new ArrayList<>();
    public static ArrayList<Bitmap> Filter_ClientProdImg = new ArrayList<>();

    public static ArrayList<String> Filter_InventoryCountDate = new ArrayList<>();
    public static ArrayList<String> Filter_InventoryTransDate = new ArrayList<>();
    public static ArrayList<String> Filter_VesselTransportDate = new ArrayList<>();
    public static ArrayList<String> Filter_InventoryReceivedDate = new ArrayList<>();
    public static ArrayList<String> Filter_ExportDate = new ArrayList<>();
    public static DecimalFormat decimalFormat = new DecimalFormat("###,###.###");

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    public static SimpleDateFormat UniqueIDdateFormat = new SimpleDateFormat("yyyyMMdd");

    /*Inventory Count Details*/
    public static ArrayList<InventoryCountModel> InventoryCountList = new ArrayList<>();
    public static ArrayList<InventoryCountInputListModel> InventoryCountInputList = new ArrayList<>();

    public static ArrayList<SyncStatusModel> SyncStatusList = new ArrayList<>();
    public static ArrayList<SyncStatusInputModel> SyncStatusExportInputList = new ArrayList<>();
    public static ArrayList<StatusVerificationModel> StatusVerificationList = new ArrayList<>();

    /*Inventory Transfer Details*/
    public static ArrayList<AgencyDetailsModel> TransportAgencyList = new ArrayList<>();
    public static ArrayList<ConcessionNamesModel> ConcessionList = new ArrayList<>();
    public static ArrayList<DriverDetailsModel> DriverList = new ArrayList<>();
    public static ArrayList<FellingSectionModel> FellingSectionList = new ArrayList<>();
    public static ArrayList<LocationDevicesModel> LocationDeviceList = new ArrayList<>();
    public static ArrayList<LocationDevicesModel> AllLocationDeviceList = new ArrayList<>();
    public static ArrayList<LocationsModel> LocationList = new ArrayList<>();
    public static ArrayList<TransferLogDetailsModel> SearchedTransLogDetils = new ArrayList<>();
    public static ArrayList<PurchaseLogsModels> SearchedExternalLogsDetils = new ArrayList<>();
    public static ArrayList<TransportModesModel> TransportModeList = new ArrayList<>();
    public static ArrayList<LoadedModel> LoadedByList = new ArrayList<>();
    public static ArrayList<TruckDetailsModel> TruckDeatilsList = new ArrayList<>();
    public static ArrayList<TruckDetailsModel> TruckDetailsSingle = new ArrayList<>();
    public static ArrayList<WoodSpeciesModel> WoodSpeicesDeatilsList = new ArrayList<>();
    public static ArrayList<WoodSpeciesModel> WoodSpeicesFilterDeatilsList = new ArrayList<>();
    public static ArrayList<ExportModel> ExportList = new ArrayList<>();

    public static String[] ConcessionListStringList, DriverListStringList, AgencyDetailsStringList, TruckDetialsStringList;
    public static ArrayList<InventoryTransferScannedResultModel> InventorytransferScannedResultList = new ArrayList<InventoryTransferScannedResultModel>();
    public static ArrayList<InventoryTransferModel> InventoryTransferList = new ArrayList<>();
    public static ArrayList<InventoryTransferModel> InventoryTransferListDuplicate = new ArrayList<>();

    // Tabel Columns Variable
    public static Map<String, ArrayList<TransferLogDetailsModel>> AllTransLogDetailsmap = new HashMap<>();
    public static ArrayList<ScannedResultModel> ScannnedResultList = new ArrayList<>();
    public static ArrayList<AdvanceSearchModel> AdvancedSearchList = new ArrayList<>();

    public static ArrayList<InventoryTransferInputListModel> InventoryTransferInputList = new ArrayList<>();

    public static ArrayAdapter<String> mPairedDevicesArrayAdapter;

    public static ArrayList<LoginAuthenticationModel> LoginDetailsList = new ArrayList<>();
    public static ArrayList<QualityModel> classificationlist = new ArrayList<>();

    public static ArrayList<InventoryReceivedListModel> InventoryReceivedList = new ArrayList<>();
    public static ArrayList<InventoryReceivedModel> InventoryReceivedScannedResultList = new ArrayList<>();
    public static ArrayList<InventoryReceivedInputModel> InventoryReceivedInputList = new ArrayList<>();

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
    public static String[] FellingRegTreeNoStringList, FellingRegPlotNoStringList;
    public static final String[] FellingRegTreePartStringList = new String[]{"A", "B", "C", "D"};
    public static String[] FellingRegExcelExportList = new String[]{"FellingRegistrationList", "FellingRegistrationDetails", "FellingTreeDetails"};
    public static String[] ExportListExcelExportList = new String[]{"ExportList", "ExportQuotationList", "ExportContainerList", "ExportDetails"};
    public static ArrayList<FellingRegisterResultModel> FellingRegisterLogsExportDetails = new ArrayList<FellingRegisterResultModel>();
    public static ArrayList<FellingTreeDetailsModel> FellingExportTreeDetailsList = new ArrayList<FellingTreeDetailsModel>();

    // Vessel Transportation
    public static ArrayList<VesselListModel> VesselLists = new ArrayList<VesselListModel>();
    public static ArrayList<VesselLogsDetailsModel> VesselScannedResultList = new ArrayList<VesselLogsDetailsModel>();
    public static ArrayList<VesselLogDetailsPrintModel> VesselLogsPrintOutList = new ArrayList<VesselLogDetailsPrintModel>();


    public static String[] FellingRegWoodSpeicesStringList;
    public static ArrayList<String> TreeNosList = new ArrayList<>();


    /*DataBase Details*/
    public static final String TBL_SCANNEDRESULT = "ScannedResult", TBL_INVENTORYCOUNTLIST = "InventoryCountList",
            TBL_INVENTORYTRANSFERSCANNED = "InventoryTransfer", TBL_INVENTORYTRANSFERIDLIST = "InventoryTransferList",
            TBL_INVENTORYRECEIVED = "InventoryReceived", TBL_INVENTORYRECEIVEDLIST = "InventoryReceivedList",
            TBL_FELLINGREGISTRATIONLIST = "FellingRegistrationList", TBL_FELLINGREGISTRATIONDETAILS = "FellingRegistrationDetails",
            TBL_LOGINAUTHENTICATION = "LoginAuthentication", TBL_FELLINGTREEDETAILS = "FellingTreeDetails", TBL_EXPORTLIST = "ExportList",
            TBL_EXPORTDETAILS = "ExportDetails", TBL_EXPORTQUOTATIONLIST = "ExportQuotationList", TBL_EXPORTCONTAINERLIST = "ExportContainerList",
            TBL_PURCHASEAGREEMENT = "PurchaseAgreement", TBL_PURCHASETRANSFER_LIST = "PurchaseTransferList", TBL_PURCHASERECEIVED_LIST = "PurchaseReceivedList",
            TBL_PURCHASEAGREEMENTSCANNEDLOGS = "ExternalPurcahseLogDetails";

    public static final String LOCATION_ID = "ToLocationID", LOCATION_NAME = "ToLocationName", FROMLOCATION = "FromLocation", FROMLOCATIONID = "FromLocationID", SBBLabel = "SbbLabel",
            WoodSpiceID = "WoodSpieceID", WoodSPiceCode = "WoodSpieceCode", DATETIME = "DateTime", IMEINumber = "IMEI", BARCODE = "BarCode", ISACTIVE = "IsActive", ENTRYMODE = "EntryMode",
            LISTID = "ListID", ISSBLABELCORRECT = "IsSBBLabelCorrected", ORGSBBLABEL = "OrgSBBLabel", VBBNUMBER = "VBB_Number", STARTDATETIME = "StartDateTime", ENDDATETIME = "EndDateTime",
            COUNT = "TotalCount", SYNCSTATUS = "SyncStatus", SYNCTIME = "SyncTime", TOLOCATION = "ToLocation", TRANSFERID = "TransferID", TRANSPORTTYPEID = "TransportTypeId",
            TRANSFERAGENCYID = "TransferAgencyID", DRIVERID = "DriverID", TRUCKPLATENUMBER = "TruckPlateNumber", LENGTH = "Length", VOLUME = "Volume", SYNCDEVICETIME = "SyncDeviceDateTime",
            USERID = "UserID", FELLING_SECTIONID = "FellingSectionId", ISCHECKED = "IsReceived", TREENO = "TreeNumber", QULAITY = "Quality", RECEIVEDID = "ReceivedID",
            TRANSFERUNIQUEID = "TransferUniqueID", PURCHASETRANSFERUNIQUEID = "PurchaseTransferUniqueID", PURCHASERECEIVEDUNIQUEID = "PurchaseReceivedUniqueID", USER_NAME = "UserName", PASSWORD = "Password", FELLINGREGID = "FellingRegID", FELLINGREGNO = "FellingRegistrationNumber",
            FELLINGREGDATE = "FellingRegistrationDate", LOCATIONID = "LocationID", FELLIINGREGUNIQUEID = "FellingRegistrationUniqueID", RECEIVEDUNIQUEID = "ReceivedUniqueID",
            COUNTUNIQUEID = "CountUniqueID", ISNEWTREENO = "IsNewTreeNumber", ISWOODSPECODE = "IsWoodSpieceCode", ISOLDWOODSPECODE = "IsOldWoodSpieceCode", OLDWOODSPECODE = "OldWoodSpieceCode",
            LOADEDID = "Loadedid", NAME = "Name", LOADEDTYPE = "LoadedTypeID", DF1 = "Footer_1", DF2 = "Footer_2", DT1 = "Top_1", DT2 = "Top_2", NOTET = "NoteT", NOTEF = "NoteF", NOTEL = "NoteL",
            PLOTID = "PlotId", PLOTNO = "PlotNo", OLDPLOTNO = "OldPlotNo", TREEPARTTYPE = "TreePartType", ISNEWPLOTNO = "IsNewPlotNumber", LOCATION = "Location", EXPORTUNIQUEID = "ExportUniqueID",
            ORDERNO = "OrderNo", CONTAINERNO = "ContainerNo", AGEOFLOG = "AgeOfLog", PVNO = "PvNo", PVDATE = "PvDate", EXPORTID = "ExportID", QUTTOTALCBM = "QutTotalCBM", ISVALIDPVNO = "IsValidPvNo",
            ISVALIDVOLUME = "IsValidVolume", ISVALIDWSCODE = "IsValidWSCode", DIAMETER = "Diameter", QUTWOODSPECODE = "QutWoodSpieceCode", QUTDIAMETER = "QutDiameter",
            ISVALIDDIAMETER = "IsValidDiameter", CREATEDDATE = "CreatedDate", QUOTATIONID = "QuotationID", QUOTATIONNO = "QuotationNo", QUOTATIONUNIQUENO = "QuotationUniqueNo",
            CONTAINERCOUNT = "ContainerCount", QUOTATIONNOFLAG = "QuotationEntryFlag", HOLEVOLUME = "HoleVolume", GROSSVOLUME = "GrossVolume", BOOKINGNO = "BookingNo", SHIPCMPY = "ShippingCompany",
            STUFFDATE = "StuffingDateTime", CUTTDATE = "CuttingDateTime", LHT1 = "LHT1", LHT2 = "LHT2", LHF1 = "LHF1", LHF2 = "LHF2", LHVOLUME = "LHVolume", VESSELID = "VesselID",
            VESSELUNIQUEID = "VessalUniqueID", TRUCKID = "TruckId", TRANSFERAGENCY = "TAgencyID", TRANSFERDRIVERID = "TDriverID", TRANSFERTRUCKID = "TTruckId", RECEIVEDAGENCYID = "RAgencyID",
            RECEIVEDDRIVERID = "RDriverID", RECEIVEDTRUCKID = "RTruckId", REMARKS = "Remarks", REMARKSTYPE = "RemarksType", LENGTHCUTFOOT = "LengthCutFoot",
            LENGTHCUTTOP = "LengthCutTop", LENGTHCUTVOLUME = "LengthCutVolume", CRACKF1 = "CrackFoot1", CRACKF2 = "CrackFoot2", CRACKT1 = "CrackTop1", CRACKT2 = "CrackTop2",
            CRACKVOLUME = "CrackVolume", SAPDEDUCTION = "SapDeduction", SAPVOLUME = "SapVolume", TOTALDISCOUNTEDVOLUME = "TotalDiscountedVolume", HOLEF1 = "Holefoot1", HOLEF2 = "Holefoot2", HOLET1 = "Holetop1", HOLET2 = "Holetop2";


    public class ExternalDataBaseClass {
        public static final String WOODSpeciesCODE = "WoodSpeciesCode", TBL_TRANSFERLOG = "TransferLogDetails", TBL_CONCESSION = "ConcessionNames", TBL_DRIVER = "DriverDetails",
                TBL_AGENCY = "AgencyDetails", TBL_TRUCK = "TruckDetails", TBL_TRANSPORTMODE = "TransportModes", TBL_LOCATIONDEVICE = "LocationDevices", TBL_LOCATIONS = "Locations",
                TBL_FELLINGSECTION = "FellingSection", TBL_FELLINGREGISTER = "FellingRegister", TBL_CLASSIFICATION = "Classifications", TBL_WOODSPEICES = "WoodSpecies",
                TBL_LOADED = "Loaded", TBL_EXTERNALDBDETIALS = "ExternalDBDetails", TBL_EXTERNALDBPURCHASEAGREEMENT = "PurchaseAgreement";
        public static final String TREENUMBER = "TreeNumber", FELLINGSECTIONID = "FellingSectionId", LOGSTATUS = "LogStatus",
                LOCATIONDID = "LocationId", LOCNAME = "LocationName", FELLINGSECNO = "FellingSectionNumber", HARCROPID = "HarvestCropsId", INSTOCKID = "InStockId",
                LENGHTDM = "Length_dm", WODESPEICEID = "WoodSpeciesId", AGENCYID = "AgencyId", AGENCYNAME = "AgencyName", ADDRESS = "Address", ConcessionID = "ConcessionId",
                TRANSPORTID = "TransportId", TRUCKPLATENO = "TruckLicensePlateNo", DESCRIPTION = "Description", TRUCKDRIVERID = "TruckDriverId", DRIVERLICNO = "DriverLicenseNo",
                DRIVERNAME = "DriverName", ExPLOTNO = "PlotNumber", TRANSPORTTYPEID = "TransportTypeId", TRANSPORTMODE = "TransportMode", CONCESSIONNAME = "ConcessionName",
                FELLINGCODE = "FellingCode", LOCATIONTYPE = "LocationType", FROMLOCATIONId = "FromLocationId", ISDEFAULT = "IsDefault", LDEVID = "LDevId", DEVICENAME = "DeviceName",
                SYNCAPIURL = "SyncApiURL", ISBLOCKED = "IsBlocked";
    }

    public static ArrayList<AgencyDetailsExternalModel> AgencyDetailsExSync = new ArrayList<>();
    public static ArrayList<DriverDetailsExternalModel> DriverDetailsExSync = new ArrayList<>();
    public static ArrayList<TransferLogDetailsExModel> TransferLogDetailsExSync = new ArrayList<>();
    public static ArrayList<TruckDetailsExternalModel> TruckDetailsExSync = new ArrayList<>();
    public static ArrayList<FellingRegisterModel> fellingRegisterSync = new ArrayList<>();
    public static ArrayList<FellingSectionModel> fellingSectionSync = new ArrayList<>();
    public static ArrayList<LocationDevicesModel> LocationDevicesSync = new ArrayList<>();
    public static ArrayList<MasterTotalCount> masterTotalCountsSync = new ArrayList<>();
    public static ArrayList<ReceivedLogsModel> ReceivingLogsList = new ArrayList<>();
    public static ArrayList<PurchaseAgreementModel> PurchaseAgreementSync = new ArrayList<>();
    public static ArrayList<PurchaseLogsModels> PurchaseLogsSync = new ArrayList<>();

    public static class Transfer {

    }

    public static class Export {
        public static ArrayList<String> ContainersList = new ArrayList<>();
        public static ArrayList<LogDetailsModel> ExportDetailsList = new ArrayList<LogDetailsModel>();
        public static ArrayList<ExportDetailsModel> ExportContainerDetailsList = new ArrayList<>();
        public static ArrayList<ExportDetailsModel> ExportDetailsInputList = new ArrayList<ExportDetailsModel>();
        public static ArrayList<ExportContainerDetailsModel> ExportHeaderList = new ArrayList<ExportContainerDetailsModel>();
        public static ArrayList<ExportSbblabelOutputModel> AllExportDetails = new ArrayList<ExportSbblabelOutputModel>();
        public static ArrayList<LogSummaryModel> LogSummaryList = new ArrayList<LogSummaryModel>();
        public static ArrayList<WoodSpeiceModel> LogSummaryWSCList = new ArrayList<WoodSpeiceModel>();
        public static ArrayList<String> LogSummaryWSCcodeList = new ArrayList<String>();
        //public static ArrayList<QuotationInternalModel> QuotationDetailsInternalList = new ArrayList<QuotationInternalModel>();
        public static ArrayList<ContainersModel> ContainerList = new ArrayList<ContainersModel>();
        public static ArrayList<String> LogSummaryInternalWSCList = new ArrayList<String>();
        public static ArrayList<TotalCBMDetailsModel> TotalCBMDetailsList = new ArrayList<TotalCBMDetailsModel>();
        public static ArrayList<QuotationInternalModel> QuotationDetailsTotalVolumeRCBMInternalList = new ArrayList<QuotationInternalModel>();

        public static int IsSplite = 0;

        public static int Vessel_TransferAgencyID, Vessel_DriverID, Vessel_TransportID, Vessel_LoadedTypeID = 2, Vessel_FromLocationID, Vessel_ToLocationID, VesselID, Vessel_TransportTypeId;
        public static String VesselUniqueID, Vessel_TrucklicensePlateNo, Vessel_AgencyName, Vessel_DriverName, Vessel_TransportMode, Vessel_LoadedName, Vessel_ToLocationName, Vessel_FromLocationName;

    }

    public static class Purchase {
        public static ArrayList<PurchaseLogsModels> PurchaseLogsDetailsInternal = new ArrayList<>();
        public static ArrayList<PurchaseLogsModels> PurchaseLogsDetailsExternal = new ArrayList<>();
        public static ArrayList<PurchaseLogsModels> PurchaseLogsDetails = new ArrayList<>();
        public static ArrayList<String> PurchaseLogsDetailsBarcode = new ArrayList<>();
        public static ArrayList<PurchaseLogsSyncModel> PurchaseLogsSyncDetails = new ArrayList<>();
        public static final String PurchaseId = "PurchaseId", PurchaseNo = "PurchaseNo", DiameterRange = "DiameterRange",
                ValidUntil = "ValidUntil", StatusID = "StatusID", UpdatedDate = "UpdatedDate", UpdatedBy = "UpdatedBy",
                PurchaseListid = "PurchaseListid", TransferStatus = "TSyncStatus", ReceivedStatus = "RSyncStatus", TTOLOCATIONID = "TToLocationID",
                TFROMLOCATIONID = "TFromLocationID", RTOLOCATIONID = "RToLocationID", RFROMLOCATIONID = "RFromLocationID", TTRANSPORTMODE = "TTransportMode",
                TLOADEDBY = "TLoadedby", RTRANSPORTMODE = "RTransportMode", RLOADEDBY = "RLoadedby", TSTARTDATETIME = "TStartDateTime", TENDDATETIME = "TEndDateTime",
                RSTARTDATETIME = "RStartDateTime", RENDDATETIME = "REndDateTime", TSYNCSTATUS = "TSyncStatus", RSYNCSTATUS = "RSyncStatus";
        public static int SelectedPurchaseId, SelectedPurchaseIdStatus, SelectedPurchaseListID, SelectedTransferStatus, SelectedReceivedStatus, SelectedTransToLocationID,
                SelectedTransFromLocationID, SelectedTransAgencyID, SelectedTransDriverID, SelectedTransTruckID, SelectedTransTransPortMode, SelectedTransLoadedby = 2,
                SelectedReceivedToLocationID, SelectedReceivedFromLocationID, SelectedReceivedAgencyID, SelectedReceivedDriverID, SelectedReceivedTruckID, SelectedReceivedTransPortMode,
                SelectedReceivedLoadedby = 2, PurcahseTransDateSelectedIndex = 0, PurcahseReceivedDateSelectedIndex = 0;
        public static String PurchaseID,SelectedPurchaseNo, SelectedTransStatrDate, SelectedTransEndDate, SelectedReceivedStartDate, SelectedReceivedEndDate;
        public static boolean IsScannrdEditorViewFlag = true, IsTransferEditorViewFlag = true, IsReceivedEditorViewFlag = true, PurchaseScannedIsEditorViewFlag = true, PurchaseTransferIsEditorViewFlag = true,
                PurchaseReceivedIsEditorViewFlag = true,purchaseStatus=false;

        public static ArrayList<PurchaseAgreementModel> purchaseAgreementData = new ArrayList<>();
        public static ArrayList<String> Filter_PurchaseTransDate = new ArrayList<>();
        public static ArrayList<String> Filter_PurchaseReceivedDate = new ArrayList<>();
        public static ArrayList<PurchaseTransferModel> PurchaseTransferList = new ArrayList<>();
        public static ArrayList<PurchaseReceivedModel> PurchaseReceivedList = new ArrayList<>();
    }


    public static void HideKeyboard(Activity activity) {
        try {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
