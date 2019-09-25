package com.zebra.main.model.InvReceived;

public class InventoryReceivedInputModel {
    String BarCode, Datetime, OrgSBBLabel, TreeNumber, WoodSpieceCode, SBBLabel, Quality, TransferUniqueID;
    int EntryModeID, FellingSectionId, FromLocationID, ID, IsActive, IsSBBLabelCorrected, ReceivedID, ToLocationId, WoodSpieceID, IsReceived;
    double Length, Volume;

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

    public String getOrgSBBLabel() {
        return OrgSBBLabel;
    }

    public void setOrgSBBLabel(String orgSBBLabel) {
        OrgSBBLabel = orgSBBLabel;
    }

    public String getTreeNumber() {
        return TreeNumber;
    }

    public void setTreeNumber(String treeNumber) {
        TreeNumber = treeNumber;
    }

    public String getWoodSpieceCode() {
        return WoodSpieceCode;
    }

    public void setWoodSpieceCode(String woodSpieceCode) {
        WoodSpieceCode = woodSpieceCode;
    }

    public String getSBBLabel() {
        return SBBLabel;
    }

    public void setSBBLabel(String SBBLabel) {
        this.SBBLabel = SBBLabel;
    }

    public int getEntryModeID() {
        return EntryModeID;
    }

    public void setEntryModeID(int entryModeID) {
        EntryModeID = entryModeID;
    }

    public int getFellingSectionId() {
        return FellingSectionId;
    }

    public void setFellingSectionId(int fellingSectionId) {
        FellingSectionId = fellingSectionId;
    }

    public int getFromLocationID() {
        return FromLocationID;
    }

    public void setFromLocationID(int fromLocationID) {
        FromLocationID = fromLocationID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public int getReceivedID() {
        return ReceivedID;
    }

    public void setReceivedID(int receivedID) {
        ReceivedID = receivedID;
    }

    public int getToLocationId() {
        return ToLocationId;
    }

    public void setToLocationId(int toLocationId) {
        ToLocationId = toLocationId;
    }

    public int getWoodSpieceID() {
        return WoodSpieceID;
    }

    public void setWoodSpieceID(int woodSpieceID) {
        WoodSpieceID = woodSpieceID;
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

    public String getQuality() {
        return Quality;
    }

    public void setQuality(String quality) {
        Quality = quality;
    }

    public int getIsReceived() {
        return IsReceived;
    }

    public void setIsReceived(int isReceived) {
        IsReceived = isReceived;
    }

    public String getTransferUniqueID() {
        return TransferUniqueID;
    }

    public void setTransferUniqueID(String transferUniqueID) {
        TransferUniqueID = transferUniqueID;
    }
}
