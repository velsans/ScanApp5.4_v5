package com.zebra.main.model;

public class AdvanceSearchModel {
    String BarCode,SBBLabel, Length_dm, Volume, WoodSpeciesCode, FellingSectionId, TreeNumber, Classification,F1,F2,T1,T2;
    int WoodSpeciesId;
    boolean Exisiting;

    public String getF1() {
        return F1;
    }

    public void setF1(String f1) {
        F1 = f1;
    }

    public String getF2() {
        return F2;
    }

    public void setF2(String f2) {
        F2 = f2;
    }

    public String getT1() {
        return T1;
    }

    public void setT1(String t1) {
        T1 = t1;
    }

    public String getT2() {
        return T2;
    }

    public void setT2(String t2) {
        T2 = t2;
    }

    public String getSBBLabel() {
        return SBBLabel;
    }

    public void setSBBLabel(String SBBLabel) {
        this.SBBLabel = SBBLabel;
    }

    public String getVolume() {
        return Volume;
    }

    public void setVolume(String volume) {
        Volume = volume;
    }

    public String getLength_dm() {
        return Length_dm;
    }

    public void setLength_dm(String length_dm) {
        Length_dm = length_dm;
    }

    public String getWoodSpeciesCode() {
        return WoodSpeciesCode;
    }

    public void setWoodSpeciesCode(String woodSpeciesCode) {
        WoodSpeciesCode = woodSpeciesCode;
    }

    public int getWoodSpeciesId() {
        return WoodSpeciesId;
    }

    public void setWoodSpeciesId(int woodSpeciesId) {
        WoodSpeciesId = woodSpeciesId;
    }

    public boolean isExisiting() {
        return Exisiting;
    }

    public void setExisiting(boolean exisiting) {
        Exisiting = exisiting;
    }

    public String getFellingSectionId() {
        return FellingSectionId;
    }

    public void setFellingSectionId(String fellingSectionId) {
        FellingSectionId = fellingSectionId;
    }

    public String getTreeNumber() {
        return TreeNumber;
    }

    public void setTreeNumber(String treeNumber) {
        TreeNumber = treeNumber;
    }

    public String getClassification() {
        return Classification;
    }

    public void setClassification(String classification) {
        Classification = classification;
    }

    public String getBarCode() {
        return BarCode;
    }

    public void setBarCode(String barCode) {
        BarCode = barCode;
    }
}
