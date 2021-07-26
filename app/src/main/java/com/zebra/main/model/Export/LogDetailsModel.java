package com.zebra.main.model.Export;

public class LogDetailsModel {
    int ExportId, ContainerId, LogListId,SpecieId,DiameterF1, DiameterF2, DiameterT1, DiameterT2,DiameterAverage;
    String SBBLabel, Barcode, WoodSpeciesCode,Remarks;
    double Length, Volume,LengthCutFoot, LengthCutTop , HoleFoot1, HoleFoot2, HoleTop1, HoleTop2,CrackFoot1,CrackFoot2, CrackTop1, CrackTop2,SapDeduction,
            HF1,HF2,HT1,HT2,HAvg,Hvolume;

    public int getExportId() {
        return ExportId;
    }

    public void setExportId(int exportId) {
        ExportId = exportId;
    }

    public String getSBBLabel() {
        return SBBLabel;
    }

    public void setSBBLabel(String SBBLabel) {
        this.SBBLabel = SBBLabel;
    }

    public int getDiameterF1() {
        return DiameterF1;
    }

    public void setDiameterF1(int diameterF1) {
        DiameterF1 = diameterF1;
    }

    public int getDiameterF2() {
        return DiameterF2;
    }

    public void setDiameterF2(int diameterF2) {
        DiameterF2 = diameterF2;
    }

    public int getDiameterT1() {
        return DiameterT1;
    }

    public void setDiameterT1(int diameterT1) {
        DiameterT1 = diameterT1;
    }

    public int getDiameterT2() {
        return DiameterT2;
    }

    public void setDiameterT2(int diameterT2) {
        DiameterT2 = diameterT2;
    }

    public double getLength() {
        return Length;
    }

    public void setLength(double length) {
        Length = length;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public int getContainerId() {
        return ContainerId;
    }

    public void setContainerId(int containerId) {
        ContainerId = containerId;
    }


    public int getLogListId() {
        return LogListId;
    }

    public void setLogListId(int logListId) {
        LogListId = logListId;
    }

    public int getSpecieId() {
        return SpecieId;
    }

    public void setSpecieId(int specieId) {
        SpecieId = specieId;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getWoodSpeciesCode() {
        return WoodSpeciesCode;
    }

    public void setWoodSpeciesCode(String woodSpeciesCode) {
        WoodSpeciesCode = woodSpeciesCode;
    }

    public int getDiameterAverage() {
        return DiameterAverage;
    }

    public void setDiameterAverage(int diameterAverage) {
        DiameterAverage = diameterAverage;
    }

    public double getLengthCutFoot() {
        return LengthCutFoot;
    }

    public void setLengthCutFoot(double lengthCutFoot) {
        LengthCutFoot = lengthCutFoot;
    }

    public double getLengthCutTop() {
        return LengthCutTop;
    }

    public void setLengthCutTop(double lengthCutTop) {
        LengthCutTop = lengthCutTop;
    }

    public double getHoleFoot1() {
        return HoleFoot1;
    }

    public void setHoleFoot1(double holeFoot1) {
        HoleFoot1 = holeFoot1;
    }

    public double getHoleFoot2() {
        return HoleFoot2;
    }

    public void setHoleFoot2(double holeFoot2) {
        HoleFoot2 = holeFoot2;
    }

    public double getHoleTop1() {
        return HoleTop1;
    }

    public void setHoleTop1(double holeTop1) {
        HoleTop1 = holeTop1;
    }

    public double getHoleTop2() {
        return HoleTop2;
    }

    public void setHoleTop2(double holeTop2) {
        HoleTop2 = holeTop2;
    }

    public double getCrackFoot1() {
        return CrackFoot1;
    }

    public void setCrackFoot1(double crackFoot1) {
        CrackFoot1 = crackFoot1;
    }

    public double getCrackFoot2() {
        return CrackFoot2;
    }

    public void setCrackFoot2(double crackFoot2) {
        CrackFoot2 = crackFoot2;
    }

    public double getCrackTop1() {
        return CrackTop1;
    }

    public void setCrackTop1(double crackTop1) {
        CrackTop1 = crackTop1;
    }

    public double getCrackTop2() {
        return CrackTop2;
    }

    public void setCrackTop2(double crackTop2) {
        CrackTop2 = crackTop2;
    }

    public double getSapDeduction() {
        return SapDeduction;
    }

    public void setSapDeduction(double sapDeduction) {
        SapDeduction = sapDeduction;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public double getHF1() {
        return HF1;
    }

    public void setHF1(double HF1) {
        this.HF1 = HF1;
    }

    public double getHF2() {
        return HF2;
    }

    public void setHF2(double HF2) {
        this.HF2 = HF2;
    }

    public double getHT1() {
        return HT1;
    }

    public void setHT1(double HT1) {
        this.HT1 = HT1;
    }

    public double getHT2() {
        return HT2;
    }

    public void setHT2(double HT2) {
        this.HT2 = HT2;
    }

    public double getHAvg() {
        return HAvg;
    }

    public void setHAvg(double HAvg) {
        this.HAvg = HAvg;
    }

    public double getHvolume() {
        return Hvolume;
    }

    public void setHvolume(double hvolume) {
        Hvolume = hvolume;
    }
}
