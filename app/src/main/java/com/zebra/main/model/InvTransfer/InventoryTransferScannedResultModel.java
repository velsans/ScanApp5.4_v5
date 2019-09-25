package com.zebra.main.model.InvTransfer;

public class InventoryTransferScannedResultModel {
    String VBB_Number, FromLocation, ToLocation, SbbLabel, BarCode, Volume, UserID, Qualitiy;
    int Length, WoodSpieceID, EntryMode, IsSBBLabelCorrected, IsActive, TreeNumber, FellingSectionId;

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }

    String WoodSpieceCode, DateTime;

    public int getLength() {
        return Length;
    }

    public void setLength(int length) {
        Length = length;
    }

    public String getVBB_Number() {
        return VBB_Number;
    }

    public void setVBB_Number(String VBB_Number) {
        this.VBB_Number = VBB_Number;
    }

    public String getFromLocation() {
        return FromLocation;
    }

    public void setFromLocation(String fromLocation) {
        FromLocation = fromLocation;
    }

    public String getToLocation() {
        return ToLocation;
    }

    public void setToLocation(String toLocation) {
        ToLocation = toLocation;
    }

    public String getSbbLabel() {
        return SbbLabel;
    }

    public void setSbbLabel(String sbbLabel) {
        SbbLabel = sbbLabel;
    }

    public String getBarCode() {
        return BarCode;
    }

    public void setBarCode(String barCode) {
        BarCode = barCode;
    }

    public String getVolume() {
        return Volume;
    }

    public void setVolume(String volume) {
        Volume = volume;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getWoodSpieceID() {
        return WoodSpieceID;
    }

    public void setWoodSpieceID(int woodSpieceID) {
        WoodSpieceID = woodSpieceID;
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

    public String getQualitiy() {
        return Qualitiy;
    }

    public void setQualitiy(String qualitiy) {
        Qualitiy = qualitiy;
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

    public int getTreeNumber() {
        return TreeNumber;
    }

    public void setTreeNumber(int treeNumber) {
        TreeNumber = treeNumber;
    }

    public int getFellingSectionId() {
        return FellingSectionId;
    }

    public void setFellingSectionId(int fellingSectionId) {
        FellingSectionId = fellingSectionId;
    }

}
