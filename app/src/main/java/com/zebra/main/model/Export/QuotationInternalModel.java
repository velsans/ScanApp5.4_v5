package com.zebra.main.model.Export;

public class QuotationInternalModel {
    int QuotationID, UserID, IsActive,QuotationEntryFlag;
    String ExportID, ExportUniqueID,
            OrderNo,
            ContainerNo,
            IMEI,
            LocationID,
            DateTime,
            QutWoodSpieceCode,
            QutDiameter,
            QuotationNo,
            QuotationUniqueNo;
    double QutTotalCBM;

    public int getQuotationID() {
        return QuotationID;
    }

    public void setQuotationID(int quotationID) {
        QuotationID = quotationID;
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

    public String getExportID() {
        return ExportID;
    }

    public void setExportID(String exportID) {
        ExportID = exportID;
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

    public String getContainerNo() {
        return ContainerNo;
    }

    public void setContainerNo(String containerNo) {
        ContainerNo = containerNo;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getLocationID() {
        return LocationID;
    }

    public void setLocationID(String locationID) {
        LocationID = locationID;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getQutWoodSpieceCode() {
        return QutWoodSpieceCode;
    }

    public void setQutWoodSpieceCode(String qutWoodSpieceCode) {
        QutWoodSpieceCode = qutWoodSpieceCode;
    }

    public String getQutDiameter() {
        return QutDiameter;
    }

    public void setQutDiameter(String qutDiameter) {
        QutDiameter = qutDiameter;
    }

    public double getQutTotalCBM() {
        return QutTotalCBM;
    }

    public void setQutTotalCBM(double qutTotalCBM) {
        QutTotalCBM = qutTotalCBM;
    }

    public String getQuotationNo() {
        return QuotationNo;
    }

    public void setQuotationNo(String quotationNo) {
        QuotationNo = quotationNo;
    }

    public String getQuotationUniqueNo() {
        return QuotationUniqueNo;
    }

    public void setQuotationUniqueNo(String quotationUniqueNo) {
        QuotationUniqueNo = quotationUniqueNo;
    }

    public int getQuotationEntryFlag() {
        return QuotationEntryFlag;
    }

    public void setQuotationEntryFlag(int quotationEntryFlag) {
        QuotationEntryFlag = quotationEntryFlag;
    }
}
