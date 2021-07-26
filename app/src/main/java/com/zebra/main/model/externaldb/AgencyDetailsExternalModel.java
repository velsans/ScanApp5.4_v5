package com.zebra.main.model.externaldb;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AgencyDetailsExternalModel {

    @SerializedName("RowID")
    @Expose
    private Integer rowID;
    @SerializedName("AgencyId")
    @Expose
    private Integer agencyId;
    @SerializedName("AgencyName")
    @Expose
    private String agencyName;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("IsBlocked")
    @Expose
    private String IsBlocked;
    @SerializedName("ID")
    @Expose
    private Integer ID;

    public Integer getRowID() {
        return rowID;
    }

    public void setRowID(Integer rowID) {
        this.rowID = rowID;
    }

    public Integer getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Integer agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getIsBlocked() {
        return IsBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        IsBlocked = isBlocked;
    }
}