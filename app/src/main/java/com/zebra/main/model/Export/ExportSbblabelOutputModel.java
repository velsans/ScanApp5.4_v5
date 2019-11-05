package com.zebra.main.model.Export;

public class ExportSbblabelOutputModel {
    String StockLocation, ConcessionNumber, FellingSection, WoodSpeciesCode, ExaminationNo, ExaminationDate, StockAge, ScannedStatus, ScannedDate, ChildSBBLabel, FellingRegisterNo;
    int FellingSectionId, TreeNumber, InstockId, WoodSpeciesId, StockLocationId,Diameter;
    double Top1_cm, Top2_cm, Foot1_cm, Foot2_cm, Length_dm, Volume;

    public int getDiameter() {
        return Diameter;
    }

    public void setDiameter(int diameter) {
        Diameter = diameter;
    }

    public String getFellingRegisterNo() {
        return FellingRegisterNo;
    }

    public void setFellingRegisterNo(String fellingRegisterNo) {
        FellingRegisterNo = fellingRegisterNo;
    }

    public String getStockLocation() {
        return StockLocation;
    }

    public void setStockLocation(String stockLocation) {
        StockLocation = stockLocation;
    }

    public String getConcessionNumber() {
        return ConcessionNumber;
    }

    public void setConcessionNumber(String concessionNumber) {
        ConcessionNumber = concessionNumber;
    }

    public String getFellingSection() {
        return FellingSection;
    }

    public void setFellingSection(String fellingSection) {
        FellingSection = fellingSection;
    }

    public String getWoodSpeciesCode() {
        return WoodSpeciesCode;
    }

    public void setWoodSpeciesCode(String woodSpeciesCode) {
        WoodSpeciesCode = woodSpeciesCode;
    }

    public String getExaminationNo() {
        return ExaminationNo;
    }

    public void setExaminationNo(String examinationNo) {
        ExaminationNo = examinationNo;
    }

    public String getExaminationDate() {
        return ExaminationDate;
    }

    public void setExaminationDate(String examinationDate) {
        ExaminationDate = examinationDate;
    }

    public String getStockAge() {
        return StockAge;
    }

    public void setStockAge(String stockAge) {
        StockAge = stockAge;
    }

    public String getScannedStatus() {
        return ScannedStatus;
    }

    public void setScannedStatus(String scannedStatus) {
        ScannedStatus = scannedStatus;
    }

    public String getScannedDate() {
        return ScannedDate;
    }

    public void setScannedDate(String scannedDate) {
        ScannedDate = scannedDate;
    }

    public int getFellingSectionId() {
        return FellingSectionId;
    }

    public void setFellingSectionId(int fellingSectionId) {
        FellingSectionId = fellingSectionId;
    }

    public int getTreeNumber() {
        return TreeNumber;
    }

    public void setTreeNumber(int treeNumber) {
        TreeNumber = treeNumber;
    }

    public int getInstockId() {
        return InstockId;
    }

    public void setInstockId(int instockId) {
        InstockId = instockId;
    }

    public int getWoodSpeciesId() {
        return WoodSpeciesId;
    }

    public void setWoodSpeciesId(int woodSpeciesId) {
        WoodSpeciesId = woodSpeciesId;
    }

    public int getStockLocationId() {
        return StockLocationId;
    }

    public void setStockLocationId(int stockLocationId) {
        StockLocationId = stockLocationId;
    }

    public double getTop1_cm() {
        return Top1_cm;
    }

    public void setTop1_cm(double top1_cm) {
        Top1_cm = top1_cm;
    }

    public double getTop2_cm() {
        return Top2_cm;
    }

    public void setTop2_cm(double top2_cm) {
        Top2_cm = top2_cm;
    }

    public double getFoot1_cm() {
        return Foot1_cm;
    }

    public void setFoot1_cm(double foot1_cm) {
        Foot1_cm = foot1_cm;
    }

    public double getFoot2_cm() {
        return Foot2_cm;
    }

    public void setFoot2_cm(double foot2_cm) {
        Foot2_cm = foot2_cm;
    }

    public double getLength_dm() {
        return Length_dm;
    }

    public void setLength_dm(double length_dm) {
        Length_dm = length_dm;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public String getChildSBBLabel() {
        return ChildSBBLabel;
    }

    public void setChildSBBLabel(String childSBBLabel) {
        ChildSBBLabel = childSBBLabel;
    }
}
