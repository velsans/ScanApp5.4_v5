package com.zebra.main.model.InvReceived;

public class InventoryReceivedListModel {

    int ReceivedID, TransferID, ToLocationID, FromLocationID, TransportTypeId, TransferAgencyID, DriverID, UserID, Count, SyncStatus,LoadedTypeID;
    String VBB_Number, IMEI, StartDateTime, EndDateTime, TruckPlateNumber, SyncTime, TransferUniqueID,ReceivedUniqueID;
    double Volume;

    public int getLoadedTypeID() {
        return LoadedTypeID;
    }

    public void setLoadedTypeID(int loadedTypeID) {
        LoadedTypeID = loadedTypeID;
    }

    public String getReceivedUniqueID() {
        return ReceivedUniqueID;
    }

    public void setReceivedUniqueID(String receivedUniqueID) {
        ReceivedUniqueID = receivedUniqueID;
    }

    public String getTransferUniqueID() {
        return TransferUniqueID;
    }

    public void setTransferUniqueID(String transferUniqueID) {
        TransferUniqueID = transferUniqueID;
    }

    public int getReceivedID() {
        return ReceivedID;
    }

    public void setReceivedID(int receivedID) {
        ReceivedID = receivedID;
    }

    public int getTransferID() {
        return TransferID;
    }

    public void setTransferID(int transferID) {
        TransferID = transferID;
    }

    public int getToLocationID() {
        return ToLocationID;
    }

    public void setToLocationID(int toLocationID) {
        ToLocationID = toLocationID;
    }

    public int getFromLocationID() {
        return FromLocationID;
    }

    public void setFromLocationID(int fromLocationID) {
        FromLocationID = fromLocationID;
    }

    public int getTransportTypeId() {
        return TransportTypeId;
    }

    public void setTransportTypeId(int transportTypeId) {
        TransportTypeId = transportTypeId;
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

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
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

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double totalVolume) {
        Volume = totalVolume;
    }
}
