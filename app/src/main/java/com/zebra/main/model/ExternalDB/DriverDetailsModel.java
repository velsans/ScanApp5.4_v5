package com.zebra.main.model.ExternalDB;

public class DriverDetailsModel {
    int ID,TruckDriverId,RowID;
    String DriverLicenseNo;
    String DriverName;

    public int getTruckDriverId() {
        return TruckDriverId;
    }

    public void setTruckDriverId(int truckDriverId) {
        TruckDriverId = truckDriverId;
    }

    public String getDriverLicenseNo() {
        return DriverLicenseNo;
    }

    public void setDriverLicenseNo(String driverLicenseNo) {
        DriverLicenseNo = driverLicenseNo;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getRowID() {
        return RowID;
    }

    public void setRowID(int rowID) {
        RowID = rowID;
    }
}
