package com.zebra.main.model.InvTransfer;


public class InventoryTransferModel {

    String VBB_Number, IMEI, StartDateTime, EndDateTime, TransferMode, TruckPlateNumber, SyncTime, TransUniqueID;
    int TransferID, UserID, ToLocationID, TransferAgencyID, DriverID, Count, SyncStatus, TransportTypeId, FromLocationID, ISActive, OLDTransferID,LoadedTypeID;
    double Volume;

    public int getLoadedTypeID() {
        return LoadedTypeID;
    }

    public void setLoadedTypeID(int loadedTypeID) {
        LoadedTypeID = loadedTypeID;
    }

    public int getFromLocationID() {
        return FromLocationID;
    }

    public void setFromLocationID(int fromLocationID) {
        FromLocationID = fromLocationID;
    }

    public String getVBB_Number() {
        return VBB_Number;
    }

    public void setVBB_Number(String VBB_Number) {
        this.VBB_Number = VBB_Number;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getStartDateTime() {
        return StartDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        StartDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return EndDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        EndDateTime = endDateTime;
    }

    public String getTransferMode() {
        return TransferMode;
    }

    public void setTransferMode(String transferMode) {
        TransferMode = transferMode;
    }

    public String getTruckPlateNumber() {
        return TruckPlateNumber;
    }

    public void setTruckPlateNumber(String truckPlateNumber) {
        TruckPlateNumber = truckPlateNumber;
    }

    public String getSyncTime() {
        return SyncTime;
    }

    public void setSyncTime(String syncTime) {
        SyncTime = syncTime;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getToLocationID() {
        return ToLocationID;
    }

    public void setToLocationID(int toLocationID) {
        ToLocationID = toLocationID;
    }

    public int getTransferAgencyID() {
        return TransferAgencyID;
    }

    public void setTransferAgencyID(int transferAgencyID) {
        TransferAgencyID = transferAgencyID;
    }

    public int getDriverID() {
        return DriverID;
    }

    public void setDriverID(int driverID) {
        DriverID = driverID;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public int getSyncStatus() {
        return SyncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        SyncStatus = syncStatus;
    }

    public int getTransportTypeId() {
        return TransportTypeId;
    }

    public void setTransportTypeId(int transportTypeId) {
        TransportTypeId = transportTypeId;
    }

    public int getTransferID() {
        return TransferID;
    }

    public void setTransferID(int transferID) {
        TransferID = transferID;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public String getTransUniqueID() {
        return TransUniqueID;
    }

    public void setTransUniqueID(String transUniqueID) {
        TransUniqueID = transUniqueID;
    }

    public int getISActive() {
        return ISActive;
    }

    public void setISActive(int ISActive) {
        this.ISActive = ISActive;
    }

    public int getOLDTransferID() {
        return OLDTransferID;
    }

    public void setOLDTransferID(int OLDTransferID) {
        this.OLDTransferID = OLDTransferID;
    }
}
