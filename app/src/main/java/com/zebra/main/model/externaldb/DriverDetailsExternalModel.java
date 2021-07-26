package com.zebra.main.model.externaldb;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverDetailsExternalModel {
    int ID;
    @SerializedName("RowID")
    @Expose
    private Integer rowID;
    @SerializedName("TruckDriverId")
    @Expose
    private Integer truckDriverId;
    @SerializedName("DriverLicenseNo")
    @Expose
    private String driverLicenseNo;
    @SerializedName("DriverName")
    @Expose
    private String driverName;
    @SerializedName("IsBlocked")
    @Expose
    private String IsBlocked;

    public Integer getRowID() {
        return rowID;
    }

    public void setRowID(Integer rowID) {
        this.rowID = rowID;
    }

    public Integer getTruckDriverId() {
        return truckDriverId;
    }

    public void setTruckDriverId(Integer truckDriverId) {
        this.truckDriverId = truckDriverId;
    }

    public String getDriverLicenseNo() {
        return driverLicenseNo;
    }

    public void setDriverLicenseNo(String driverLicenseNo) {
        this.driverLicenseNo = driverLicenseNo;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getIsBlocked() {
        return IsBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        IsBlocked = isBlocked;
    }
}