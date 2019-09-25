package com.zebra.main.model.FellingRegistration;

public class FellingRegisterInputListModel {
    int FellingRegID, LocationID, WoodSpieceID, EntryMode, IsActive, IsNewTree, IsWoodSpieceCode;
    String SbbLabel, WoodSpieceCode, TreeNumber, Quality, BarCode, OldWoodSpieceCode, Footer_1, Footer_2, Top_1, Top_2, Length, NoteF, NoteT, NoteL, TreePartType;
    double Volume;

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public String getTreePartType() {
        return TreePartType;
    }

    public void setTreePartType(String treePartType) {
        TreePartType = treePartType;
    }

    public String getNoteF() {
        return NoteF;
    }

    public void setNoteF(String noteF) {
        NoteF = noteF;
    }

    public String getNoteT() {
        return NoteT;
    }

    public void setNoteT(String noteT) {
        NoteT = noteT;
    }

    public String getNoteL() {
        return NoteL;
    }

    public void setNoteL(String noteL) {
        NoteL = noteL;
    }

    public int getIsWoodSpieceCode() {
        return IsWoodSpieceCode;
    }

    public void setIsWoodSpieceCode(int isWoodSpieceCode) {
        IsWoodSpieceCode = isWoodSpieceCode;
    }

    public String getOldWoodSpieceCode() {
        return OldWoodSpieceCode;
    }

    public void setOldWoodSpieceCode(String oldWoodSpieceCode) {
        OldWoodSpieceCode = oldWoodSpieceCode;
    }

    public int getIsNewTree() {
        return IsNewTree;
    }

    public void setIsNewTree(int IsNewTree) {
        this.IsNewTree = IsNewTree;
    }

    public int getFellingRegID() {
        return FellingRegID;
    }

    public void setFellingRegID(int fellingRegID) {
        FellingRegID = fellingRegID;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
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

    public String getBarCode() {
        return BarCode;
    }

    public void setBarCode(String barCode) {
        BarCode = barCode;
    }

    public String getFooter_1() {
        return Footer_1;
    }

    public void setFooter_1(String footer_1) {
        Footer_1 = footer_1;
    }

    public String getFooter_2() {
        return Footer_2;
    }

    public void setFooter_2(String footer_2) {
        Footer_2 = footer_2;
    }

    public String getTop_1() {
        return Top_1;
    }

    public void setTop_1(String top_1) {
        Top_1 = top_1;
    }

    public String getTop_2() {
        return Top_2;
    }

    public void setTop_2(String top_2) {
        Top_2 = top_2;
    }

    public String getLength() {
        return Length;
    }

    public void setLength(String length) {
        Length = length;
    }
}
