package com.zebra.main.model.InvReceived;

public class ReceivedLogsModel {
    String BarCode,WoodSpecieCode,FellingSection,TreeNumber,RetributionStatus,ExportExamination;

    public String getBarCode() {
        return BarCode;
    }

    public void setBarCode(String barCode) {
        BarCode = barCode;
    }

    public String getWoodSpecieCode() {
        return WoodSpecieCode;
    }

    public void setWoodSpecieCode(String woodSpecieCode) {
        WoodSpecieCode = woodSpecieCode;
    }

    public String getFellingSection() {
        return FellingSection;
    }

    public void setFellingSection(String fellingSection) {
        FellingSection = fellingSection;
    }

    public String getTreeNumber() {
        return TreeNumber;
    }

    public void setTreeNumber(String treeNumber) {
        TreeNumber = treeNumber;
    }

    public String getRetributionStatus() {
        return RetributionStatus;
    }

    public void setRetributionStatus(String retributionStatus) {
        RetributionStatus = retributionStatus;
    }

    public String getExportExamination() {
        return ExportExamination;
    }

    public void setExportExamination(String exportExamination) {
        ExportExamination = exportExamination;
    }
}
