package com.zebra.main.model.externaldb;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TruckDetailsModel {
    int ID;
    @SerializedName("RowID")
    @Expose
    private Integer rowID;
    @SerializedName("TransportId")
    @Expose
    private Integer transportId;
    @SerializedName("TruckLicensePlateNo")
    @Expose
    private String truckLicensePlateNo;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("IsBlocked")
    @Expose
    private Integer IsBlocked;

    public Integer getRowID() {
        return rowID;
    }

    public void setRowID(Integer rowID) {
        this.rowID = rowID;
    }

    public Integer getTransportId() {
        return transportId;
    }

    public void setTransportId(Integer transportId) {
        this.transportId = transportId;
    }

    public String getTruckLicensePlateNo() {
        return truckLicensePlateNo;
    }

    public void setTruckLicensePlateNo(String truckLicensePlateNo) {
        this.truckLicensePlateNo = truckLicensePlateNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Integer getIsBlocked() {
        return IsBlocked;
    }

    public void setIsBlocked(Integer isBlocked) {
        IsBlocked = isBlocked;
    }
}
