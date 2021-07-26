package com.zebra.main.model.Export;

public class ExportDetailsModel {
    int ID, ExportID, LocationID, WoodSpieceID, UserID, EntryMode, IsActive, IsValidDiameter,IsValidPvNo, IsValidVolume, IsValidWSCode;
    String ExportUniqueID,PvNo, PvDate, AgeOfLog, QutWoodSpieceCode, WoodSpieceCode, SbbLabel,BarCode, Footer_1, Footer_2, Top_1, Top_2, Length,
            DateTime, NoteT, NoteF,NoteL, OrderNo,Diameter,QutDiameter,ContainerNo;
    double Volume, QutTotalCBM;

    public String getBarCode() {
        return BarCode;
    }

    public void setBarCode(String barCode) {
        BarCode = barCode;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public String getQutDiameter() {
        return QutDiameter;
    }

    public void setQutDiameter(String qutDiameter) {
        QutDiameter = qutDiameter;
    }

    public double getQutTotalCBM() {
        return QutTotalCBM;
    }

    public void setQutTotalCBM(double qutTotalCBM) {
        QutTotalCBM = qutTotalCBM;
    }

    public String getDiameter() {
        return Diameter;
    }

    public void setDiameter(String diameter) {
        Diameter = diameter;
    }

    public String getNoteT() {
        return NoteT;
    }

    public void setNoteT(String noteT) {
        NoteT = noteT;
    }

    public String getNoteF() {
        return NoteF;
    }

    public void setNoteF(String noteF) {
        NoteF = noteF;
    }

    public String getNoteL() {
        return NoteL;
    }

    public void setNoteL(String noteL) {
        NoteL = noteL;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getExportUniqueID() {
        return ExportUniqueID;
    }

    public void setExportUniqueID(String exportUniqueID) {
        ExportUniqueID = exportUniqueID;
    }

    public String getPvNo() {
        return PvNo;
    }

    public void setPvNo(String pvNo) {
        PvNo = pvNo;
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

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
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

    public String getAgeOfLog() {
        return AgeOfLog;
    }

    public void setAgeOfLog(String ageOfLog) {
        AgeOfLog = ageOfLog;
    }

    public String getWoodSpieceCode() {
        return WoodSpieceCode;
    }

    public void setWoodSpieceCode(String woodSpieceCode) {
        WoodSpieceCode = woodSpieceCode;
    }

    public String getSbbLabel() {
        return SbbLabel;
    }

    public void setSbbLabel(String sbbLabel) {
        SbbLabel = sbbLabel;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public int getExportID() {
        return ExportID;
    }

    public void setExportID(int exportID) {
        ExportID = exportID;
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

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getPvDate() {
        return PvDate;
    }

    public void setPvDate(String pvDate) {
        PvDate = pvDate;
    }

    public String getQutWoodSpieceCode() {
        return QutWoodSpieceCode;
    }

    public void setQutWoodSpieceCode(String qutWoodSpieceCode) {
        QutWoodSpieceCode = qutWoodSpieceCode;
    }

    public int getIsValidPvNo() {
        return IsValidPvNo;
    }

    public void setIsValidPvNo(int isValidPvNo) {
        IsValidPvNo = isValidPvNo;
    }

    public int getIsValidVolume() {
        return IsValidVolume;
    }

    public void setIsValidVolume(int isValidVolume) {
        IsValidVolume = isValidVolume;
    }

    public int getIsValidWSCode() {
        return IsValidWSCode;
    }

    public void setIsValidWSCode(int isValidWSCode) {
        IsValidWSCode = isValidWSCode;
    }

    public int getIsValidDiameter() {
        return IsValidDiameter;
    }

    public void setIsValidDiameter(int isValidDiameter) {
        IsValidDiameter = isValidDiameter;
    }

    public String getContainerNo() {
        return ContainerNo;
    }

    public void setContainerNo(String containerNo) {
        ContainerNo = containerNo;
    }
}
