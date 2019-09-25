package com.zebra.main.model.InvCount;

import java.util.ArrayList;

public class InventoryCountSyncModel {
    String IMEI,StartedTime,EndDateTime;
    int ListID,LocationID,UserId,BarCodeCount;


    ArrayList<InventoryCountInputListModel> HHScannedResult=new ArrayList<>();

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getStartedTime() {
        return StartedTime;
    }

    public void setStartedTime(String startedTime) {
        StartedTime = startedTime;
    }

    public String getEndDateTime() {
        return EndDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        EndDateTime = endDateTime;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public ArrayList<InventoryCountInputListModel> getHHScannedResult() {
        return HHScannedResult;
    }

    public void setHHScannedResult(ArrayList<InventoryCountInputListModel> HHScannedResult) {
        this.HHScannedResult = HHScannedResult;
    }

    public int getListID() {
        return ListID;
    }

    public void setListID(int listID) {
        ListID = listID;
    }

    public int getBarCodeCount() {
        return BarCodeCount;
    }

    public void setBarCodeCount(int barCodeCount) {
        BarCodeCount = barCodeCount;
    }
}
