package com.zebra.main.model.Export;

public class ContainerDetailsSuncModel {
    String ExportID,ExportUniqueID,OrderNo,ContainerNo;
    int IsActive;

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

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }
}
