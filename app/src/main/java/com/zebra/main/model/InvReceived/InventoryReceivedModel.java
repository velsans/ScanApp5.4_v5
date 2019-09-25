package com.zebra.main.model.InvReceived;

public class InventoryReceivedModel {
    int ID, ReceivedID, TransferID, Length, WoodSpieceID, EntryMode, IsActive, IsSBBLabelCorrected;
    String VBB_Number, FromLocation, ToLocation, SbbLabel, BarCode, Volume, UserID, DateTime, WoodSpieceCode, OrgSBBLabel,
            FellingSectionId, TreeNumber, Quality, IsReceived, TransferUniqueID;

    public String getTransferUniqueID() {
        return TransferUniqueID;
    }

    public void setTransferUniqueID(String transferUniqueID) {
        TransferUniqueID = transferUniqueID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getReceivedID() {
        return ReceivedID;
    }

    public void setReceivedID(int receivedID) {
        ReceivedID = receivedID;
    }

    public int getTransferID() {
        return TransferID;
    }

    public void setTransferID(int transferID) {
        TransferID = transferID;
    }

    public int getLength() {
        return Length;
    }

    public void setLength(int length) {
        Length = length;
    }

    public int getWoodSpieceID() {
        return WoodSpieceID;
    }

    public void setWoodSpieceID(int woodSpieceID) {
        WoodSpieceID = woodSpieceID;
    }

    public int getEntryMode() {
        return EntryMode;
    }

    public void setEntryMode(int entryMode) {
        EntryMode = entryMode;
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

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
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

    public String getQuality() {
        return Quality;
    }

    public void setQuality(String quality) {
        Quality = quality;
    }

    public String getIsReceived() {
        return IsReceived;
    }

    public void setIsReceived(String isReceived) {
        IsReceived = isReceived;
    }
}
