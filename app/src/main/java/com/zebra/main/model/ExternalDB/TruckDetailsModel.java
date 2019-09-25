package com.zebra.main.model.ExternalDB;

public class TruckDetailsModel {
    int ID, TransportId,RowID;
    String TruckLicensePlateNo, Description;

    public int getTransportId() {
        return TransportId;
    }

    public void setTransportId(int transportId) {
        TransportId = transportId;
    }

    public String getTruckLicensePlateNo() {
        return TruckLicensePlateNo;
    }

    public void setTruckLicensePlateNo(String truckLicensePlateNo) {
        TruckLicensePlateNo = truckLicensePlateNo;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
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
