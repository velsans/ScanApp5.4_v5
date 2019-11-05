package com.zebra.main.model.Export;

import java.io.Serializable;
import java.util.ArrayList;

public class ExportContainerDetailsModel implements Serializable {
    int ExportID, TotalCount, LocationID, UserID, IsActive,SyncStatus;
    double Volume;
    String ExportUniqueID, IMEI, StartDateTime, EndDateTime, OrderNo,SyncDate;

    public int getSyncStatus() {
        return SyncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        SyncStatus = syncStatus;
    }

    public String getSyncDate() {
        return SyncDate;
    }

    public void setSyncDate(String syncDate) {
        SyncDate = syncDate;
    }

    public int getExportID() {
        return ExportID;
    }

    public void setExportID(int exportID) {
        ExportID = exportID;
    }

    public String getExportUniqueID() {
        return ExportUniqueID;
    }

    public void setExportUniqueID(String exportUniqueID) {
        ExportUniqueID = exportUniqueID;
    }


    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int totalCount) {
        TotalCount = totalCount;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
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


    private ArrayList<ExportDetailsModel> containterDetailsItems;


    public ArrayList<ExportDetailsModel> getContainterDetailsItems() {
        return containterDetailsItems;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public void setContainterDetailsItems(ArrayList<ExportDetailsModel> containterDetailsItems) {
        this.containterDetailsItems = containterDetailsItems;
    }
}
