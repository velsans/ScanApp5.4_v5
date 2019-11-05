package com.zebra.database;

import com.zebra.utilities.Common;

public class InternalDataBaseTabelsClass {

    public String CreateLogin() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_LOGINAUTHENTICATION + "("
                + Common.USER_NAME + " TEXT,"
                + Common.PASSWORD + " TEXT,"
                + Common.IMEINumber + " TEXT)";
        return SqlCreateQuery;
    }

    public String CountIDList() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_INVENTORYCOUNTLIST + "("
                + Common.LISTID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "OLDListID" + " INTEGER,"
                + Common.IMEINumber + " TEXT,"
                + Common.LOCATION_ID + " INTEGER,"
                + Common.STARTDATETIME + " TEXT,"
                + Common.ENDDATETIME + " TEXT,"
                + Common.COUNT + " INTEGER,"
                + Common.VOLUME + " TEXT,"
                + Common.ISACTIVE + " TEXT,"
                + Common.COUNTUNIQUEID + " TEXT,"
                + Common.SYNCSTATUS + " INTEGER,"
                + Common.SYNCTIME + " TEXT,"
                + Common.SYNCDEVICETIME + " TEXT)";
        return SqlCreateQuery;
    }

    public String CountIDScannedDetails() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_SCANNEDRESULT + "("
                + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.LOCATION_ID + " INTEGER,"
                + Common.LOCATION_NAME + " TEXT,"
                + Common.SBBLabel + " TEXT,"
                + Common.WoodSpiceID + " INTEGER,"
                + Common.WoodSPiceCode + " TEXT,"
                + Common.DATETIME + " TEXT,"
                + Common.IMEINumber + " TEXT,"
                + Common.BARCODE + " TEXT,"
                + Common.ENTRYMODE + " TEXT,"
                + Common.ISACTIVE + " TEXT,"
                + Common.LISTID + " TEXT,"
                + Common.VOLUME + " TEXT,"
                + Common.LENGTH + " TEXT,"
                + Common.ISSBLABELCORRECT + " INTEGER,"
                + Common.QULAITY + " TEXT,"
                + Common.FELLING_SECTIONID + " TEXT,"
                + Common.TREENO + " TEXT,"
                + Common.ORGSBBLABEL + " TEXT)";
        return SqlCreateQuery;
    }

    public String TransferIDList() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_INVENTORYTRANSFERIDLIST + "("
                + Common.TRANSFERID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "OLDTransferID" + " INTEGER,"
                + Common.VBBNUMBER + " TEXT,"
                + Common.IMEINumber + " TEXT,"
                + Common.LOCATION_ID + " INTEGER,"
                + Common.STARTDATETIME + " TEXT,"
                + Common.ENDDATETIME + " TEXT,"
                + Common.FROMLOCATIONID + " INTEGER,"
                + Common.TRANSPORTTYPEID + " INTEGER,"
                + Common.TRANSFERAGENCYID + " INTEGER,"
                + Common.DRIVERID + " INTEGER,"
                + Common.TRUCKPLATENUMBER + " TEXT,"
                + Common.USERID + " INTEGER,"
                + Common.COUNT + " INTEGER,"
                + Common.VOLUME + " TEXT,"
                + Common.TRANSFERUNIQUEID + " TEXT,"
                + Common.ISACTIVE + " INTEGER,"
                + Common.SYNCSTATUS + " INTEGER,"
                + Common.SYNCTIME + " TEXT)";
        return SqlCreateQuery;
    }

    public String TransferIDScannedDetails() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_INVENTORYTRANSFERSCANNED + "("
                + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.VBBNUMBER + " TEXT,"
                + Common.TRANSFERID + " INTEGER,"
                + Common.FROMLOCATION + " TEXT,"
                + Common.TOLOCATION + " TEXT,"
                + Common.SBBLabel + " TEXT,"
                + Common.BARCODE + " TEXT,"
                + Common.LENGTH + " INTEGER,"
                + Common.VOLUME + " TEXT,"
                + Common.USERID + " TEXT,"
                + Common.DATETIME + " TEXT,"
                + Common.ENTRYMODE + " INTEGER,"
                + Common.ISACTIVE + " INTEGER,"
                + Common.ISSBLABELCORRECT + " INTEGER,"
                + Common.ORGSBBLABEL + " TEXT,"
                + Common.FELLING_SECTIONID + " TEXT,"
                + Common.TREENO + " TEXT,"
                + Common.QULAITY + " TEXT,"
                + Common.WoodSpiceID + " INTEGER,"
                + Common.WoodSPiceCode + " TEXT)";
        return SqlCreateQuery;
    }

    public String ReceivedIDList() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_INVENTORYRECEIVEDLIST + "("
                + Common.RECEIVEDID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                /*+ TRANSFERID + " INTEGER,"*/
                + Common.VBBNUMBER + " TEXT,"
                + Common.IMEINumber + " TEXT,"
                + Common.LOCATION_ID + " INTEGER,"
                + Common.STARTDATETIME + " TEXT,"
                + Common.ENDDATETIME + " TEXT,"
                + Common.FROMLOCATIONID + " INTEGER,"
                + Common.TRANSPORTTYPEID + " INTEGER,"
                + Common.TRANSFERAGENCYID + " INTEGER,"
                + Common.DRIVERID + " INTEGER,"
                + Common.TRUCKPLATENUMBER + " TEXT,"
                + Common.USERID + " INTEGER,"
                + Common.COUNT + " INTEGER,"
                + Common.VOLUME + " TEXT,"
                + Common.RECEIVEDUNIQUEID + " TEXT,"
                + Common.ISACTIVE + " INTEGER,"
                + Common.SYNCSTATUS + " INTEGER,"
                + Common.SYNCTIME + " TEXT)";
        return SqlCreateQuery;
    }

    public String ReceivedIDScannedDetails() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_INVENTORYRECEIVED + "("
                + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.RECEIVEDID + " INTEGER,"
                + Common.TRANSFERID + " INTEGER,"
                + Common.VBBNUMBER + " TEXT,"
                + Common.FROMLOCATION + " TEXT,"
                + Common.TOLOCATION + " TEXT,"
                + Common.SBBLabel + " TEXT,"
                + Common.BARCODE + " TEXT,"
                + Common.LENGTH + " INTEGER,"
                + Common.VOLUME + " TEXT,"
                + Common.USERID + " TEXT,"
                + Common.DATETIME + " TEXT,"
                + Common.ENTRYMODE + " INTEGER,"
                + Common.ISACTIVE + " INTEGER,"
                + Common.ISSBLABELCORRECT + " INTEGER,"
                + Common.ORGSBBLABEL + " TEXT,"
                + Common.WoodSpiceID + " INTEGER,"
                + Common.WoodSPiceCode + " TEXT,"
                + Common.TRANSFERUNIQUEID + " TEXT,"
                + Common.FELLING_SECTIONID + " TEXT,"
                + Common.ISCHECKED + " INTEGER,"
                + Common.QULAITY + " TEXT,"
                + Common.TREENO + " TEXT)";
        return SqlCreateQuery;
    }

    public String FellingIDList() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_FELLINGREGISTRATIONLIST + "("
                + Common.FELLINGREGID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.FELLINGREGNO + " INTEGER,"
                + Common.FELLINGREGDATE + " TEXT,"
                + Common.LOCATIONID + " TEXT,"
                + Common.FELLING_SECTIONID + " INTEGER,"
                + Common.ENDDATETIME + " TEXT,"
                + Common.COUNT + " INTEGER,"
                + Common.FELLIINGREGUNIQUEID + " TEXT,"
                + Common.SYNCSTATUS + " INTEGER,"
                + Common.SYNCTIME + " TEXT,"
                + Common.ISACTIVE + " INTEGER,"
                + Common.USERID + " INTEGER,"
                + Common.IMEINumber + " TEXT)";
        return SqlCreateQuery;
    }

    public String FellingIDScannedDetails() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_FELLINGREGISTRATIONDETAILS + "("
                + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.FELLINGREGID + " INTEGER,"
                + Common.LOCATIONID + " TEXT,"
                + Common.FELLING_SECTIONID + " INTEGER,"
                + Common.SBBLabel + " TEXT,"
                + Common.WoodSpiceID + " INTEGER,"
                + Common.WoodSPiceCode + " TEXT,"
                + Common.TREENO + " TEXT,"
                + Common.QULAITY + " TEXT,"
                + Common.BARCODE + " TEXT,"
                + Common.FELLIINGREGUNIQUEID + " TEXT,"
                + Common.USERID + " TEXT,"
                + Common.ENTRYMODE + " INTEGER,"
                + Common.ISACTIVE + " INTEGER)";
        return SqlCreateQuery;
    }

    public String FellingIDScannedDetailsUpdated4_9() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_FELLINGREGISTRATIONDETAILS + "("
                + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.FELLINGREGID + " INTEGER,"
                + Common.LOCATIONID + " TEXT,"
                + Common.FELLING_SECTIONID + " INTEGER,"
                + Common.SBBLabel + " TEXT,"
                + Common.WoodSpiceID + " INTEGER,"
                + Common.WoodSPiceCode + " TEXT,"
                + Common.TREENO + " TEXT,"
                + Common.QULAITY + " TEXT,"
                + Common.BARCODE + " TEXT,"
                + Common.FELLIINGREGUNIQUEID + " TEXT,"
                + Common.USERID + " TEXT,"
                + Common.ENTRYMODE + " INTEGER,"
                + Common.ISNEWTREENO + " INTEGER,"
                + Common.ISWOODSPECODE + " INTEGER,"
                + Common.ISOLDWOODSPECODE + " TEXT,"
                + Common.ISACTIVE + " INTEGER)";
        return SqlCreateQuery;
    }

    public String TransferIDListUpdated4_9() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_INVENTORYTRANSFERIDLIST + "("
                + Common.TRANSFERID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "OLDTransferID" + " INTEGER,"
                + Common.VBBNUMBER + " TEXT,"
                + Common.IMEINumber + " TEXT,"
                + Common.LOCATION_ID + " INTEGER,"
                + Common.STARTDATETIME + " TEXT,"
                + Common.ENDDATETIME + " TEXT,"
                + Common.FROMLOCATIONID + " INTEGER,"
                + Common.TRANSPORTTYPEID + " INTEGER,"
                + Common.TRANSFERAGENCYID + " INTEGER,"
                + Common.DRIVERID + " INTEGER,"
                + Common.TRUCKPLATENUMBER + " TEXT,"
                + Common.USERID + " INTEGER,"
                + Common.COUNT + " INTEGER,"
                + Common.VOLUME + " TEXT,"
                + Common.TRANSFERUNIQUEID + " TEXT,"
                + Common.ISACTIVE + " INTEGER,"
                + Common.SYNCSTATUS + " INTEGER,"
                + Common.LOADEDTYPE + " INTEGER,"
                + Common.SYNCTIME + " TEXT)";
        return SqlCreateQuery;
    }

    /*5.0*/
    public String FellingIDScannedDetailsUpdated5_0() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_FELLINGREGISTRATIONDETAILS + "("
                + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.FELLINGREGID + " INTEGER,"
                + Common.LOCATIONID + " TEXT,"
                + Common.FELLING_SECTIONID + " INTEGER,"
                + Common.SBBLabel + " TEXT,"
                + Common.WoodSpiceID + " INTEGER,"
                + Common.WoodSPiceCode + " TEXT,"
                + Common.TREENO + " TEXT,"
                + Common.QULAITY + " TEXT,"
                + Common.BARCODE + " TEXT,"
                + Common.FELLIINGREGUNIQUEID + " TEXT,"
                + Common.USERID + " TEXT,"
                + Common.ENTRYMODE + " INTEGER,"
                + Common.ISNEWTREENO + " INTEGER,"
                + Common.ISWOODSPECODE + " INTEGER,"
                + Common.ISOLDWOODSPECODE + " TEXT,"
                + Common.DF1 + " TEXT,"
                + Common.DF2 + " TEXT,"
                + Common.DT1 + " TEXT,"
                + Common.DT2 + " TEXT,"
                + Common.LENGTH + " INTEGER,"
                + Common.ISACTIVE + " INTEGER)";
        return SqlCreateQuery;
    }

    public String FellingTreeDetails5_0() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_FELLINGTREEDETAILS + "("
                + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.FELLINGREGID + " INTEGER,"
                + Common.LOCATIONID + " TEXT,"
                + Common.FELLIINGREGUNIQUEID + " TEXT,"
                + Common.FELLING_SECTIONID + " INTEGER,"
                + Common.TREENO + " TEXT,"
                + Common.DF1 + " TEXT,"
                + Common.DF2 + " TEXT,"
                + Common.DT1 + " TEXT,"
                + Common.DT2 + " TEXT,"
                + Common.LENGTH + " INTEGER,"
                + Common.WoodSPiceCode + " TEXT,"
                + Common.ISNEWTREENO + " INTEGER,"
                + Common.PLOTNO + " TEXT,"
                + Common.OLDPLOTNO + " TEXT,"
                + Common.OLDWOODSPECODE + " TEXT)";
        return SqlCreateQuery;
    }

    /*5.1*/
    public String FellingIDScannedDetailsUpdated5_1() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_FELLINGREGISTRATIONDETAILS + "("
                + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.FELLINGREGID + " INTEGER,"
                + Common.LOCATIONID + " TEXT,"
                + Common.FELLING_SECTIONID + " INTEGER,"
                + Common.SBBLabel + " TEXT,"
                + Common.WoodSpiceID + " INTEGER,"
                + Common.WoodSPiceCode + " TEXT,"
                + Common.TREENO + " TEXT,"
                + Common.QULAITY + " TEXT,"
                + Common.BARCODE + " TEXT,"
                + Common.FELLIINGREGUNIQUEID + " TEXT,"
                + Common.USERID + " TEXT,"
                + Common.ENTRYMODE + " INTEGER,"
                + Common.ISNEWTREENO + " INTEGER,"
                + Common.ISWOODSPECODE + " INTEGER,"
                + Common.ISOLDWOODSPECODE + " TEXT,"
                + Common.DF1 + " TEXT,"
                + Common.DF2 + " TEXT,"
                + Common.DT1 + " TEXT,"
                + Common.DT2 + " TEXT,"
                + Common.LENGTH + " INTEGER,"
                + Common.NOTEF + " TEXT,"
                + Common.NOTET + " TEXT,"
                + Common.NOTEL + " TEXT,"
                + Common.TREEPARTTYPE + " TEXT,"
                + Common.ISACTIVE + " INTEGER)";
        return SqlCreateQuery;
    }

    public String FellingTreeDetails5_1() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_FELLINGTREEDETAILS + "("
                + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.FELLINGREGID + " INTEGER,"
                + Common.LOCATIONID + " TEXT,"
                + Common.FELLIINGREGUNIQUEID + " TEXT,"
                + Common.FELLING_SECTIONID + " INTEGER,"
                + Common.TREENO + " TEXT,"
                + Common.DF1 + " TEXT,"
                + Common.DF2 + " TEXT,"
                + Common.DT1 + " TEXT,"
                + Common.DT2 + " TEXT,"
                + Common.LENGTH + " INTEGER,"
                + Common.WoodSPiceCode + " TEXT,"
                + Common.ISNEWTREENO + " INTEGER,"
                + Common.PLOTNO + " TEXT,"
                + Common.OLDPLOTNO + " TEXT,"
                + Common.PLOTID + " INTEGER,"
                + Common.ISNEWPLOTNO + " TEXT,"
                + Common.ISWOODSPECODE + " TEXT,"
                + Common.WoodSpiceID + " TEXT,"
                + Common.OLDWOODSPECODE + " TEXT)";
        return SqlCreateQuery;
    }

    /*5.2*/
    public String FellingIDList5_2() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_FELLINGREGISTRATIONLIST + "("
                + Common.FELLINGREGID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.FELLINGREGNO + " INTEGER,"
                + Common.FELLINGREGDATE + " TEXT,"
                + Common.LOCATIONID + " TEXT,"
                + Common.FELLING_SECTIONID + " INTEGER,"
                + Common.ENDDATETIME + " TEXT,"
                + Common.COUNT + " INTEGER,"
                + Common.FELLIINGREGUNIQUEID + " TEXT,"
                + Common.SYNCSTATUS + " INTEGER,"
                + Common.SYNCTIME + " TEXT,"
                + Common.ISACTIVE + " INTEGER,"
                + Common.VOLUME + " TEXT,"
                + Common.USERID + " INTEGER,"
                + Common.IMEINumber + " TEXT)";
        return SqlCreateQuery;
    }

    public String FellingRegistrationDetails5_2() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_FELLINGREGISTRATIONDETAILS + "("
                + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.FELLINGREGID + " INTEGER,"
                + Common.LOCATIONID + " TEXT,"
                + Common.FELLING_SECTIONID + " INTEGER,"
                + Common.SBBLabel + " TEXT,"
                + Common.WoodSpiceID + " INTEGER,"
                + Common.WoodSPiceCode + " TEXT,"
                + Common.TREENO + " TEXT,"
                + Common.QULAITY + " TEXT,"
                + Common.BARCODE + " TEXT,"
                + Common.FELLIINGREGUNIQUEID + " TEXT,"
                + Common.USERID + " TEXT,"
                + Common.ENTRYMODE + " INTEGER,"
                + Common.ISNEWTREENO + " INTEGER,"
                + Common.ISWOODSPECODE + " INTEGER,"
                + Common.ISOLDWOODSPECODE + " TEXT,"
                + Common.DF1 + " TEXT,"
                + Common.DF2 + " TEXT,"
                + Common.DT1 + " TEXT,"
                + Common.DT2 + " TEXT,"
                + Common.LENGTH + " TEXT,"
                + Common.NOTEF + " TEXT,"
                + Common.NOTET + " TEXT,"
                + Common.NOTEL + " TEXT,"
                + Common.VOLUME + " TEXT,"
                + Common.TREEPARTTYPE + " TEXT,"
                + Common.ISACTIVE + " INTEGER)";
        return SqlCreateQuery;
    }

    public String FellingTreeDetails5_2() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_FELLINGTREEDETAILS + "("
                + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.FELLINGREGID + " INTEGER,"
                + Common.LOCATIONID + " TEXT,"
                + Common.FELLIINGREGUNIQUEID + " TEXT,"
                + Common.FELLING_SECTIONID + " INTEGER,"
                + Common.TREENO + " TEXT,"
                + Common.DF1 + " TEXT,"
                + Common.DF2 + " TEXT,"
                + Common.DT1 + " TEXT,"
                + Common.DT2 + " TEXT,"
                + Common.LENGTH + " INTEGER,"
                + Common.WoodSPiceCode + " TEXT,"
                + Common.OLDWOODSPECODE + " TEXT,"
                + Common.ISNEWTREENO + " INTEGER,"
                + Common.PLOTNO + " TEXT,"
                + Common.OLDPLOTNO + " TEXT,"
                + Common.PLOTID + " INTEGER,"
                + Common.WoodSpiceID + " INTEGER,"
                + Common.ISNEWPLOTNO + " INTEGER,"
                + Common.ISWOODSPECODE + " INTEGER)";
        return SqlCreateQuery;
    }
    /*%.3*/

    public String FellingRegistrationDetails5_3() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_FELLINGREGISTRATIONDETAILS + "("
                + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.FELLINGREGID + " INTEGER,"
                + Common.LOCATIONID + " TEXT,"
                + Common.FELLING_SECTIONID + " INTEGER,"
                + Common.SBBLabel + " TEXT,"
                + Common.WoodSpiceID + " INTEGER,"
                + Common.WoodSPiceCode + " TEXT,"
                + Common.TREENO + " TEXT,"
                + Common.QULAITY + " TEXT,"
                + Common.BARCODE + " TEXT,"
                + Common.FELLIINGREGUNIQUEID + " TEXT,"
                + Common.USERID + " TEXT,"
                + Common.ENTRYMODE + " INTEGER,"
                + Common.ISNEWTREENO + " INTEGER,"
                + Common.ISWOODSPECODE + " INTEGER,"
                + Common.ISOLDWOODSPECODE + " TEXT,"
                + Common.DF1 + " TEXT,"
                + Common.DF2 + " TEXT,"
                + Common.DT1 + " TEXT,"
                + Common.DT2 + " TEXT,"
                + Common.LENGTH + " TEXT,"
                + Common.NOTEF + " TEXT,"
                + Common.NOTET + " TEXT,"
                + Common.NOTEL + " TEXT,"
                + Common.VOLUME + " TEXT,"
                + Common.TREEPARTTYPE + " TEXT,"
                + Common.ISACTIVE + " INTEGER)";
        return SqlCreateQuery;
    }

    public String ReceivedIDList5_3() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_INVENTORYRECEIVEDLIST + "("
                + Common.RECEIVEDID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.TRANSFERUNIQUEID + " TEXT,"
                + Common.VBBNUMBER + " TEXT,"
                + Common.IMEINumber + " TEXT,"
                + Common.LOCATION_ID + " INTEGER,"
                + Common.STARTDATETIME + " TEXT,"
                + Common.ENDDATETIME + " TEXT,"
                + Common.FROMLOCATIONID + " INTEGER,"
                + Common.TRANSPORTTYPEID + " INTEGER,"
                + Common.TRANSFERAGENCYID + " INTEGER,"
                + Common.DRIVERID + " INTEGER,"
                + Common.TRUCKPLATENUMBER + " TEXT,"
                + Common.USERID + " INTEGER,"
                + Common.COUNT + " INTEGER,"
                + Common.VOLUME + " TEXT,"
                + Common.LOADEDTYPE + " INTEGER,"
                + Common.RECEIVEDUNIQUEID + " TEXT,"
                + Common.ISACTIVE + " INTEGER,"
                + Common.SYNCSTATUS + " INTEGER,"
                + Common.SYNCTIME + " TEXT)";
        return SqlCreateQuery;
    }

    public String ExportLoadPlanList5_7() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_EXPORTLIST + "("
                + Common.EXPORTID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.EXPORTUNIQUEID + " TEXT,"
                + Common.ORDERNO + " TEXT,"
                + Common.IMEINumber + " TEXT,"
                + Common.LOCATIONID + " INTEGER,"
                + Common.STARTDATETIME + " TEXT,"
                + Common.ENDDATETIME + " TEXT,"
                + Common.USERID + " INTEGER,"
                + Common.COUNT + " INTEGER,"
                + Common.VOLUME + " TEXT,"
                + Common.SYNCSTATUS + " INTEGER,"
                + Common.SYNCTIME + " TEXT,"
                + Common.ISACTIVE + " INTEGER)";
        return SqlCreateQuery;
    }

    public String ExportLoadPlanDetails5_7() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.TBL_EXPORTDETAILS + "("
                + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Common.EXPORTID + " INTEGER,"
                + Common.EXPORTUNIQUEID + " INTEGER,"
                + Common.ORDERNO + " TEXT,"
                + Common.CONTAINERNO + " TEXT,"
                + Common.PVNO + " TEXT,"
                + Common.PVDATE + " TEXT,"
                + Common.LOCATIONID + " TEXT,"
                + Common.IMEINumber + " TEXT,"
                + Common.SBBLabel + " TEXT,"
                + Common.BARCODE + " TEXT,"
                + Common.WoodSpiceID + " INTEGER,"
                + Common.QUTWOODSPECODE + " TEXT,"
                + Common.WoodSPiceCode + " TEXT,"
                + Common.AGEOFLOG + " TEXT,"
                + Common.DF1 + " TEXT,"
                + Common.DF2 + " TEXT,"
                + Common.DT1 + " TEXT,"
                + Common.DT2 + " TEXT,"
                + Common.LENGTH + " TEXT,"
                + Common.NOTEF + " TEXT,"
                + Common.NOTET + " TEXT,"
                + Common.NOTEL + " TEXT,"
                + Common.USERID + " TEXT,"
                + Common.ENTRYMODE + " INTEGER,"
                + Common.QUTTOTALCBM + " TEXT,"
                + Common.VOLUME + " TEXT,"
                + Common.DATETIME + " TEXT,"
                + Common.ISVALIDPVNO + " INTEGER,"
                + Common.ISVALIDVOLUME + " INTEGER,"
                + Common.ISVALIDWSCODE + " INTEGER,"
                + Common.DIAMETER + " TEXT,"
                + Common.QUTDIAMETER + " TEXT,"
                + Common.ISVALIDDIAMETER + " INTEGER,"
                + Common.ISACTIVE + " INTEGER)";
        return SqlCreateQuery;
    }
}

