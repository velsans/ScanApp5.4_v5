package com.zebra.database;

import com.zebra.utilities.Common;

public class ExternalDataBaseTablesClass {

    public String CreateLogin() {
        String SqlCreateQuery = "CREATE TABLE IF NOT EXISTS " + Common.ExternalDataBaseClass.TBL_LOADED + "("
                + Common.LOADEDID + " TEXT,"
                + Common.PASSWORD + " TEXT,"
                + Common.IMEINumber + " TEXT)";
        return SqlCreateQuery;
    }

}
