package com.zebra.main.model.Export;

import java.util.ArrayList;

public class ExportDetailsInputSavedListModel {

    int ExportID, LocationID, TotalCount, UserID;
    String OrderNo, IMEINumber, StartTime, EndTime, ExportUniqueID;
    ArrayList<ExportDetailsModel> HHExportListandDetailsObj = new ArrayList<>();

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

    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int totalCount) {
        TotalCount = totalCount;
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

    public String getExportUniqueID() {
        return ExportUniqueID;
    }

    public void setExportUniqueID(String exportUniqueID) {
        ExportUniqueID = exportUniqueID;
    }

    public ArrayList<ExportDetailsModel> getHHExportListandDetailsObj() {
        return HHExportListandDetailsObj;
    }

    public void setHHExportListandDetailsObj(ArrayList<ExportDetailsModel> HHExportListandDetailsObj) {
        this.HHExportListandDetailsObj = HHExportListandDetailsObj;
    }
}
