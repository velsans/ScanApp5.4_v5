package com.zebra.main.model.InvTransfer;

public class InventoryTransferInputListModel {

    int ID, WoodSpieceID, ToLocationId, EntryModeID, IsSBBLabelCorrected, IsActive, FellingSectionId, TreeNumber;
    String SBBLabel, IMEI, BarCode, Datetime, WoodSpieceCode, OrgSBBLabel, Quality;
    double Length, Volume;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getWoodSpieceID() {
        return WoodSpieceID;
    }

    public void setWoodSpieceID(int woodSpieceID) {
        WoodSpieceID = woodSpieceID;
    }

    public int getEntryModeID() {
        return EntryModeID;
    }

    public void setEntryModeID(int entryModeID) {
        EntryModeID = entryModeID;
    }

    public int getIsSBBLabelCorrected() {
        return IsSBBLabelCorrected;
    }

    public void setIsSBBLabelCorrected(int isSBBLabelCorrected) {
        IsSBBLabelCorrected = isSBBLabelCorrected;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }

    public int getToLocationId() {
        return ToLocationId;
    }

    public void setToLocationId(int toLocationId) {
        ToLocationId = toLocationId;
    }

    public String getSBBLabel() {
        return SBBLabel;
    }

    public void setSBBLabel(String SBBLabel) {
        this.SBBLabel = SBBLabel;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getBarCode() {
        return BarCode;
    }

    public void setBarCode(String barCode) {
        BarCode = barCode;
    }

    public String getDatetime() {
        return Datetime;
    }

    public void setDatetime(String datetime) {
        Datetime = datetime;
    }

    public String getWoodSpieceCode() {
        return WoodSpieceCode;
    }

    public void setWoodSpieceCode(String woodSpieceCode) {
        WoodSpieceCode = woodSpieceCode;
    }

    public String getOrgSBBLabel() {
        return OrgSBBLabel;
    }

    public void setOrgSBBLabel(String orgSBBLabel) {
        OrgSBBLabel = orgSBBLabel;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public double getLength() {
        return Length;
    }

    public void setLength(double length) {
        Length = length;
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

    public String getQuality() {
        return Quality;
    }

    public void setQuality(String quality) {
        Quality = quality;
    }
}
