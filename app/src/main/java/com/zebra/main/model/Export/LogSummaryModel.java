package com.zebra.main.model.Export;

public class LogSummaryModel {
    int LogSummaryId,ExportId,SpecieId,DiameterMax,DiameterMin,ScannedLogCount;
    String WoodSpeciesCode;
    double QuantityCBM,ScannedCBM;

    public int getExportId() {
        return ExportId;
    }

    public void setExportId(int exportId) {
        ExportId = exportId;
    }

    public int getDiameterMax() {
        return DiameterMax;
    }

    public void setDiameterMax(int diameterMax) {
        DiameterMax = diameterMax;
    }

    public int getDiameterMin() {
        return DiameterMin;
    }

    public void setDiameterMin(int diameterMin) {
        DiameterMin = diameterMin;
    }

    public String getWoodSpeciesCode() {
        return WoodSpeciesCode;
    }

    public void setWoodSpeciesCode(String woodSpeciesCode) {
        WoodSpeciesCode = woodSpeciesCode;
    }

    public double getQuantityCBM() {
        return QuantityCBM;
    }

    public void setQuantityCBM(double quantityCBM) {
        QuantityCBM = quantityCBM;
    }

    public double getScannedCBM() {
        return ScannedCBM;
    }

    public void setScannedCBM(double scannedCBM) {
        ScannedCBM = scannedCBM;
    }

    public int getLogSummaryId() {
        return LogSummaryId;
    }

    public void setLogSummaryId(int logSummaryId) {
        LogSummaryId = logSummaryId;
    }

    public int getSpecieId() {
        return SpecieId;
    }

    public void setSpecieId(int specieId) {
        SpecieId = specieId;
    }

    public int getScannedLogCount() {
        return ScannedLogCount;
    }

    public void setScannedLogCount(int scannedLogCount) {
        ScannedLogCount = scannedLogCount;
    }
}
