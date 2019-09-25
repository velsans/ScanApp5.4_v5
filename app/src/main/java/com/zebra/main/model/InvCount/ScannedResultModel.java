package com.zebra.main.model.InvCount;

public class ScannedResultModel {
    int ToLocationID, WoodSpieceID, EntryMode, ListID, IsActive, IsSBBLabelCorrected;
    String ToLocationName, SbbLabel, WoodSpieceCode, DateTime, IMEI, BarCode, OrgSBBLabel, FellingSectionId, TreeNumber,Quality;
    Double Volume;

    public String getQuality() {
        return Quality;
    }

    public void setQuality(String quality) {
        Quality = quality;
    }

    public int getToLocationID() {
        return ToLocationID;
    }

    public void setToLocationID(int toLocationID) {
        ToLocationID = toLocationID;
    }

    public int getWoodSpieceID() {
        return WoodSpieceID;
    }

    public void setWoodSpieceID(int woodSpieceID) {
        WoodSpieceID = woodSpieceID;
    }

    public String getToLocationName() {
        return ToLocationName;
    }

    public void setToLocationName(String toLocationName) {
        ToLocationName = toLocationName;
    }

    public String getSbbLabel() {
        return SbbLabel;
    }

    public void setSbbLabel(String sbbLabel) {
        SbbLabel = sbbLabel;
    }

    public String getWoodSpieceCode() {
        return WoodSpieceCode;
    }

    public void setWoodSpieceCode(String woodSpieceCode) {
        WoodSpieceCode = woodSpieceCode;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
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

    public int getEntryMode() {
        return EntryMode;
    }

    public void setEntryMode(int entryMode) {
        EntryMode = entryMode;
    }

    public int getListID() {
        return ListID;
    }

    public void setListID(int listID) {
        ListID = listID;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }

    public int getIsSBBLabelCorrected() {
        return IsSBBLabelCorrected;
    }

    public void setIsSBBLabelCorrected(int isSBBLabelCorrected) {
        IsSBBLabelCorrected = isSBBLabelCorrected;
    }

    public String getOrgSBBLabel() {
        return OrgSBBLabel;
    }

    public void setOrgSBBLabel(String orgSBBLabel) {
        OrgSBBLabel = orgSBBLabel;
    }

    public Double getVolume() {
        return Volume;
    }

    public void setVolume(Double volume) {
        Volume = volume;
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
}
