package com.zebra.main.model.InvReceived;

import java.util.ArrayList;

public class InventoryReceivedSyncModel {

    String VBBNumber, IMEINumber, StartTime, EndTime, TruckPlateNumber;
    int ReceivedID, TransferID, LocationID, TransferModeID, TransferAgencyId, DriverId, TranferredCount, UserID, ToLocationID;
    ArrayList<InventoryReceivedInputModel> HHReceived = new ArrayList<>();

    public String getVBBNumber() {
        return VBBNumber;
    }

    public void setVBBNumber(String VBBNumber) {
        this.VBBNumber = VBBNumber;
    }

    public String getIMEINumber() {
        return IMEINumber;
    }

    public void setIMEINumber(String IMEINumber) {
        this.IMEINumber = IMEINumber;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getTruckPlateNumber() {
        return TruckPlateNumber;
    }

    public void setTruckPlateNumber(String truckPlateNumber) {
        TruckPlateNumber = truckPlateNumber;
    }

    public int getTransferID() {
        return TransferID;
    }

    public void setTransferID(int transferID) {
        TransferID = transferID;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public int getTransferModeID() {
        return TransferModeID;
    }

    public void setTransferModeID(int transferModeID) {
        TransferModeID = transferModeID;
    }

    public int getTransferAgencyId() {
        return TransferAgencyId;
    }

    public void setTransferAgencyId(int transferAgencyId) {
        TransferAgencyId = transferAgencyId;
    }

    public int getDriverId() {
        return DriverId;
    }

    public void setDriverId(int driverId) {
        DriverId = driverId;
    }

    public int getTranferredCount() {
        return TranferredCount;
    }

    public void setTranferredCount(int tranferredCount) {
        TranferredCount = tranferredCount;
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

    public int getReceivedID() {
        return ReceivedID;
    }

    public void setReceivedID(int receivedID) {
        ReceivedID = receivedID;
    }

    public ArrayList<InventoryReceivedInputModel> getHHReceived() {
        return HHReceived;
    }

    public void setHHReceived(ArrayList<InventoryReceivedInputModel> HHReceived) {
        this.HHReceived = HHReceived;
    }
}
