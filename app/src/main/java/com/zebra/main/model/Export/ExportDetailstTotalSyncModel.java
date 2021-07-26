package com.zebra.main.model.Export;

import com.zebra.main.model.SyncStatusModel;

import java.util.ArrayList;

public class ExportDetailstTotalSyncModel {

    int ExportID, LocationID, TotalLogCount, UserID, TotalContainerCount;
    String OrderNo, ExportUniqueID, IMEI, StartTime, EndTime, TotalLogVolume;

    ArrayList<ContainerDetailsSuncModel> HHConDetails = new ArrayList();
    ArrayList<QuoatationDetailsSyncModel> HHQuoDetails = new ArrayList();
    ArrayList<ExportDetailsModel> HHBarCodeDetails = new ArrayList();

    public int getExportID() {
        return ExportID;
    }

    public void setExportID(int exportID) {
        ExportID = exportID;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public int getTotalLogCount() {
        return TotalLogCount;
    }

    public void setTotalLogCount(int totalLogCount) {
        TotalLogCount = totalLogCount;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public String getExportUniqueID() {
        return ExportUniqueID;
    }

    public void setExportUniqueID(String exportUniqueID) {
        ExportUniqueID = exportUniqueID;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
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

    public int getTotalContainerCount() {
        return TotalContainerCount;
    }

    public void setTotalContainerCount(int totalContainerCount) {
        TotalContainerCount = totalContainerCount;
    }

    public String getTotalLogVolume() {
        return TotalLogVolume;
    }

    public void setTotalLogVolume(String totalLogVolume) {
        TotalLogVolume = totalLogVolume;
    }

    public ArrayList<ContainerDetailsSuncModel> getHHConDetails() {
        return HHConDetails;
    }

    public void setHHConDetails(ArrayList<ContainerDetailsSuncModel> HHConDetails) {
        this.HHConDetails = HHConDetails;
    }

    public ArrayList<QuoatationDetailsSyncModel> getHHQuoDetails() {
        return HHQuoDetails;
    }

    public void setHHQuoDetails(ArrayList<QuoatationDetailsSyncModel> HHQuoDetails) {
        this.HHQuoDetails = HHQuoDetails;
    }

    public ArrayList<ExportDetailsModel> getHHBarCodeDetails() {
        return HHBarCodeDetails;
    }

    public void setHHBarCodeDetails(ArrayList<ExportDetailsModel> HHBarCodeDetails) {
        this.HHBarCodeDetails = HHBarCodeDetails;
    }

    /*Responce*/
    ArrayList<SyncStatusModel> Status = new ArrayList<SyncStatusModel>();

    public ArrayList<SyncStatusModel> getStatus() {
        return Status;
    }

    public void setStatus(ArrayList<SyncStatusModel> status) {
        Status = status;
    }

}
