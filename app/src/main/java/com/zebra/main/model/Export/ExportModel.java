package com.zebra.main.model.Export;

public class ExportModel {
    int ExportID, QuotationEntryFlag, ContainerCount, UserID, TotalCount, SyncStatus, IsActive, LocationID;
    String ExportUniqueID, OrderNo, QuotationNo, IMEI, StartDateTime, EndDateTime, SyncTime, BookingNo, ShippingCompany, StuffingDateTime, CuttingDateTime;
    double Volume;

    public int getExportID() {
        return ExportID;
    }

    public void setExportID(int exportID) {
        ExportID = exportID;
    }

    public int getQuotationEntryFlag() {
        return QuotationEntryFlag;
    }

    public void setQuotationEntryFlag(int quotationEntryFlag) {
        QuotationEntryFlag = quotationEntryFlag;
    }

    public int getContainerCount() {
        return ContainerCount;
    }

    public void setContainerCount(int containerCount) {
        ContainerCount = containerCount;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int totalCount) {
        TotalCount = totalCount;
    }

    public int getSyncStatus() {
        return SyncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        SyncStatus = syncStatus;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public String getExportUniqueID() {
        return ExportUniqueID;
    }

    public void setExportUniqueID(String exportUniqueID) {
        ExportUniqueID = exportUniqueID;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public String getQuotationNo() {
        return QuotationNo;
    }

    public void setQuotationNo(String quotationNo) {
        QuotationNo = quotationNo;
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

    public String getSyncTime() {
        return SyncTime;
    }

    public void setSyncTime(String syncTime) {
        SyncTime = syncTime;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public String getBookingNo() {
        return BookingNo;
    }

    public void setBookingNo(String bookingNo) {
        BookingNo = bookingNo;
    }

    public String getShippingCompany() {
        return ShippingCompany;
    }

    public void setShippingCompany(String shippingCompany) {
        ShippingCompany = shippingCompany;
    }

    public String getStuffingDateTime() {
        return StuffingDateTime;
    }

    public void setStuffingDateTime(String stuffingDateTime) {
        StuffingDateTime = stuffingDateTime;
    }

    public String getCuttingDateTime() {
        return CuttingDateTime;
    }

    public void setCuttingDateTime(String cuttingDateTime) {
        CuttingDateTime = cuttingDateTime;
    }
}
