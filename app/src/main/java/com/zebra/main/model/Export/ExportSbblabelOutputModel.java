package com.zebra.main.model.Export;

public class ExportSbblabelOutputModel {
    int InStockId, WoodSpeciesId, Foot1_cm, Foot2_cm, Top1_cm, Top2_cm, ReturnMsg, SplittedLogId,Diameter;
    String SBBLabel, WoodSpeciesCode, ErrorMessage, StatusName, ExaminationNo, ExaminationDate,ExpiredLog,IsValidLog,StockAge;
    double Volume, Length_dm;

    public int getInStockId() {
        return InStockId;
    }

    public void setInStockId(int inStockId) {
        InStockId = inStockId;
    }

    public int getWoodSpeciesId() {
        return WoodSpeciesId;
    }

    public void setWoodSpeciesId(int woodSpeciesId) {
        WoodSpeciesId = woodSpeciesId;
    }

    public int getFoot1_cm() {
        return Foot1_cm;
    }

    public void setFoot1_cm(int foot1_cm) {
        Foot1_cm = foot1_cm;
    }

    public int getFoot2_cm() {
        return Foot2_cm;
    }

    public void setFoot2_cm(int foot2_cm) {
        Foot2_cm = foot2_cm;
    }

    public int getTop1_cm() {
        return Top1_cm;
    }

    public void setTop1_cm(int top1_cm) {
        Top1_cm = top1_cm;
    }

    public int getTop2_cm() {
        return Top2_cm;
    }

    public void setTop2_cm(int top2_cm) {
        Top2_cm = top2_cm;
    }

    public double getLength_dm() {
        return Length_dm;
    }

    public void setLength_dm(double length_dm) {
        Length_dm = length_dm;
    }

    public String getStockAge() {
        return StockAge;
    }

    public void setStockAge(String stockAge) {
        StockAge = stockAge;
    }

    public int getReturnMsg() {
        return ReturnMsg;
    }

    public void setReturnMsg(int returnMsg) {
        ReturnMsg = returnMsg;
    }

    public int getSplittedLogId() {
        return SplittedLogId;
    }

    public void setSplittedLogId(int splittedLogId) {
        SplittedLogId = splittedLogId;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public String getSBBLabel() {
        return SBBLabel;
    }

    public void setSBBLabel(String SBBLabel) {
        this.SBBLabel = SBBLabel;
    }

    public String getWoodSpeciesCode() {
        return WoodSpeciesCode;
    }

    public void setWoodSpeciesCode(String woodSpeciesCode) {
        WoodSpeciesCode = woodSpeciesCode;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public String getExaminationNo() {
        return ExaminationNo;
    }

    public void setExaminationNo(String examinationNo) {
        ExaminationNo = examinationNo;
    }

    public String getExaminationDate() {
        return ExaminationDate;
    }

    public void setExaminationDate(String examinationDate) {
        ExaminationDate = examinationDate;
    }

    public String getIsValidLog() {
        return IsValidLog;
    }

    public void setIsValidLog(String isValidLog) {
        IsValidLog = isValidLog;
    }

    public String getExpiredLog() {
        return ExpiredLog;
    }

    public void setExpiredLog(String expiredLog) {
        ExpiredLog = expiredLog;
    }

    public int getDiameter() {
        return Diameter;
    }

    public void setDiameter(int diameter) {
        Diameter = diameter;
    }
}
