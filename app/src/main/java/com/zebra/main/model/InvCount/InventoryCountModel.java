package com.zebra.main.model.InvCount;

public class InventoryCountModel {
    String Imei, StartTime, EndTime, SyncTime;
    int ListID, SyncStatus, Count, LocationID, ISActive, OLDListID;
    double Volume;

    public String getImei() {
        return Imei;
    }

    public void setImei(String imei) {
        Imei = imei;
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

    public String getSyncTime() {
        return SyncTime;
    }

    public void setSyncTime(String syncTime) {
        SyncTime = syncTime;
    }

    public int getSyncStatus() {
        return SyncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        SyncStatus = syncStatus;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public int getListID() {
        return ListID;
    }

    public void setListID(int listID) {
        ListID = listID;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public int getISActive() {
        return ISActive;
    }

    public void setISActive(int ISActive) {
        this.ISActive = ISActive;
    }

    public int getOLDListID() {
        return OLDListID;
    }

    public void setOLDListID(int OLDListID) {
        this.OLDListID = OLDListID;
    }
}
