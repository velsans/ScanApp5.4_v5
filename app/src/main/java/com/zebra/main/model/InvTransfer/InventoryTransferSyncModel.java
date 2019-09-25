package com.zebra.main.model.InvTransfer;

import java.util.ArrayList;

public class InventoryTransferSyncModel {

    String VBBNumber, IMEINumber, StartTime, EndTime, TruckPlateNumber, TransferUniqueID;
    int TransferID, LocationID, TransferModeID, TransferAgencyId, DriverId, TranferredCount, UserID, ToLocationID,LoadedTypeID;
    ArrayList<InventoryTransferInputListModel> HHInventoryTransfer = new ArrayList<>();

    public int getLoadedTypeID() {
        return LoadedTypeID;
    }

    public void setLoadedTypeID(int loadedTypeID) {
        LoadedTypeID = loadedTypeID;
    }

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

    public ArrayList<InventoryTransferInputListModel> getHHInventoryTransfer() {
        return HHInventoryTransfer;
    }

    public void setHHInventoryTransfer(ArrayList<InventoryTransferInputListModel> HHInventoryTransfer) {
        this.HHInventoryTransfer = HHInventoryTransfer;
    }

    public String getTransferUniqueID() {
        return TransferUniqueID;
    }

    public void setTransferUniqueID(String transferUniqueID) {
        TransferUniqueID = transferUniqueID;
    }
}
