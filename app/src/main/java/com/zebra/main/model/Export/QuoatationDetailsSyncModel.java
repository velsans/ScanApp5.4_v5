package com.zebra.main.model.Export;

public class QuoatationDetailsSyncModel {

    String ExportID,
            ExportUniqueID,
            OrderNo,
            ContainerNo,
            IMEI,
            DateTime,
            QutWoodSpieceCode,
            QutDiameter,
            QutTotalCBM,
            QuotationUniqueNo;
    int UserID, IsActive, LocationID;

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

    public String getQutTotalCBM() {
        return QutTotalCBM;
    }

    public void setQutTotalCBM(String qutTotalCBM) {
        QutTotalCBM = qutTotalCBM;
    }

    public String getQuotationUniqueNo() {
        return QuotationUniqueNo;
    }

    public void setQuotationUniqueNo(String quotationUniqueNo) {
        QuotationUniqueNo = quotationUniqueNo;
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

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }
}
