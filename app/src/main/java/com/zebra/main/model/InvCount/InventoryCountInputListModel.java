package com.zebra.main.model.InvCount;

public class InventoryCountInputListModel {
    int ID, WoodSpecieId, ToLocationId, EntryMode, IsSBBLabelCorrected, FellingSectionId;
    String SBBLabel, WoodSpecieCode, ToLocation, BarCode, Datetime, OrgSBBLabel, TreeNumber, Quality;
    Double Volume;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getWoodSpecieId() {
        return WoodSpecieId;
    }

    public void setWoodSpecieId(int woodSpecieId) {
        WoodSpecieId = woodSpecieId;
    }

    public int getToLocationId() {
        return ToLocationId;
    }

    public void setToLocationId(int toLocationId) {
        ToLocationId = toLocationId;
    }

    public int getEntryMode() {
        return EntryMode;
    }

    public void setEntryMode(int entryMode) {
        EntryMode = entryMode;
    }

    public int getIsSBBLabelCorrected() {
        return IsSBBLabelCorrected;
    }

    public void setIsSBBLabelCorrected(int isSBBLabelCorrected) {
        IsSBBLabelCorrected = isSBBLabelCorrected;
    }

    public String getSBBLabel() {
        return SBBLabel;
    }

    public void setSBBLabel(String SBBLabel) {
        this.SBBLabel = SBBLabel;
    }

    public String getWoodSpecieCode() {
        return WoodSpecieCode;
    }

    public void setWoodSpecieCode(String woodSpecieCode) {
        WoodSpecieCode = woodSpecieCode;
    }

    public String getToLocation() {
        return ToLocation;
    }

    public void setToLocation(String toLocation) {
        ToLocation = toLocation;
    }

    public String getBarCode() {
        return BarCode;
    }

    public void setBarCode(String barCode) {
        BarCode = barCode;
    }

    public String getOrgSBBLabel() {
        return OrgSBBLabel;
    }

    public void setOrgSBBLabel(String orgSBBLabel) {
        OrgSBBLabel = orgSBBLabel;
    }

    public String getDatetime() {
        return Datetime;
    }

    public void setDatetime(String datetime) {
        Datetime = datetime;
    }

    public Double getVolume() {
        return Volume;
    }

    public void setVolume(Double volume) {
        Volume = volume;
    }

    public String getTreeNumber() {
        return TreeNumber;
    }

    public void setTreeNumber(String treeNumber) {
        TreeNumber = treeNumber;
    }

    public String getQuality() {
        return Quality;
    }

    public void setQuality(String quality) {
        Quality = quality;
    }

    public int getFellingSectionId() {
        return FellingSectionId;
    }

    public void setFellingSectionId(int fellingSectionId) {
        FellingSectionId = fellingSectionId;
    }
}
