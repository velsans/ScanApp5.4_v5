package com.zebra.main.model.Export;

import java.util.ArrayList;

public class InsertExportLogsModel {

    int ExportId, ContainerId, Status, DiameterF1, DiameterF2, DiameterT1, DiameterT2, SpecieId, UpdateFlag, UserID, IsSplit;
    String SBBLabel, DiameterAverage, Length, Volume, Message, Remarks,RemarksType;
    double LengthCutFoot, LengthCutTop, HoleFoot1, HoleFoot2, HoleTop1, HoleTop2, CrackFoot1, CrackFoot2, CrackTop1, CrackTop2, SapDeduction,
            HF1,HF2,HT1,HT2,HAvg,Hvolume;

    public int getExportId() {
        return ExportId;
    }

    public void setExportId(int exportId) {
        ExportId = exportId;
    }

    public int getContainerId() {
        return ContainerId;
    }

    public void setContainerId(int containerId) {
        ContainerId = containerId;
    }

    public String getSBBLabel() {
        return SBBLabel;
    }

    public void setSBBLabel(String SBBLabel) {
        this.SBBLabel = SBBLabel;
    }

    public String getDiameterAverage() {
        return DiameterAverage;
    }

    public void setDiameterAverage(String diameterAverage) {
        DiameterAverage = diameterAverage;
    }

    public String getLength() {
        return Length;
    }

    public void setLength(String length) {
        Length = length;
    }

    public String getVolume() {
        return Volume;
    }

    public void setVolume(String volume) {
        Volume = volume;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getDiameterF1() {
        return DiameterF1;
    }

    public void setDiameterF1(int diameterF1) {
        DiameterF1 = diameterF1;
    }

    public int getDiameterF2() {
        return DiameterF2;
    }

    public void setDiameterF2(int diameterF2) {
        DiameterF2 = diameterF2;
    }

    public int getDiameterT1() {
        return DiameterT1;
    }

    public void setDiameterT1(int diameterT1) {
        DiameterT1 = diameterT1;
    }

    public int getDiameterT2() {
        return DiameterT2;
    }

    public void setDiameterT2(int diameterT2) {
        DiameterT2 = diameterT2;
    }

    public int getSpecieId() {
        return SpecieId;
    }

    public void setSpecieId(int specieId) {
        SpecieId = specieId;
    }

    public int getUpdateFlag() {
        return UpdateFlag;
    }

    public void setUpdateFlag(int updateFlag) {
        UpdateFlag = updateFlag;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getIsSplit() {
        return IsSplit;
    }

    public void setIsSplit(int isSplit) {
        IsSplit = isSplit;
    }


    public double getLengthCutFoot() {
        return LengthCutFoot;
    }

    public void setLengthCutFoot(double lengthCutFoot) {
        LengthCutFoot = lengthCutFoot;
    }

    public double getLengthCutTop() {
        return LengthCutTop;
    }

    public void setLengthCutTop(double lengthCutTop) {
        LengthCutTop = lengthCutTop;
    }

    public double getHoleFoot1() {
        return HoleFoot1;
    }

    public void setHoleFoot1(double holeFoot1) {
        HoleFoot1 = holeFoot1;
    }

    public double getHoleFoot2() {
        return HoleFoot2;
    }

    public void setHoleFoot2(double holeFoot2) {
        HoleFoot2 = holeFoot2;
    }

    public double getHoleTop1() {
        return HoleTop1;
    }

    public void setHoleTop1(double holeTop1) {
        HoleTop1 = holeTop1;
    }

    public double getHoleTop2() {
        return HoleTop2;
    }

    public void setHoleTop2(double holeTop2) {
        HoleTop2 = holeTop2;
    }

    public double getCrackFoot1() {
        return CrackFoot1;
    }

    public void setCrackFoot1(double crackFoot1) {
        CrackFoot1 = crackFoot1;
    }

    public double getCrackFoot2() {
        return CrackFoot2;
    }

    public void setCrackFoot2(double crackFoot2) {
        CrackFoot2 = crackFoot2;
    }

    public double getCrackTop1() {
        return CrackTop1;
    }

    public void setCrackTop1(double crackTop1) {
        CrackTop1 = crackTop1;
    }

    public double getCrackTop2() {
        return CrackTop2;
    }

    public void setCrackTop2(double crackTop2) {
        CrackTop2 = crackTop2;
    }

    public double getSapDeduction() {
        return SapDeduction;
    }

    public void setSapDeduction(double sapDeduction) {
        SapDeduction = sapDeduction;
    }

    public String getRemarksType() {
        return RemarksType;
    }

    public void setRemarksType(String remarksType) {
        RemarksType = remarksType;
    }

    ArrayList<LogSummaryModel> LogSummaryDetails = new ArrayList<>();
    ArrayList<ContainersModel> ContainersDetails = new ArrayList<>();
    ArrayList<LogDetailsModel> LogDetails = new ArrayList<>();
    ArrayList<StatusVerificationModel> InsertLogStatus = new ArrayList<>();
    ArrayList<TotalCBMDetailsModel> TotalCBMDetails = new ArrayList<>();

    public ArrayList<LogSummaryModel> getLogSummaryDetails() {
        return LogSummaryDetails;
    }

    public void setLogSummaryDetails(ArrayList<LogSummaryModel> logSummaryDetails) {
        LogSummaryDetails = logSummaryDetails;
    }

    public ArrayList<ContainersModel> getContainersDetails() {
        return ContainersDetails;
    }

    public void setContainersDetails(ArrayList<ContainersModel> containersDetails) {
        ContainersDetails = containersDetails;
    }

    public ArrayList<LogDetailsModel> getLogDetails() {
        return LogDetails;
    }

    public void setLogDetails(ArrayList<LogDetailsModel> logDetails) {
        LogDetails = logDetails;
    }

    public ArrayList<StatusVerificationModel> getInsertLogStatus() {
        return InsertLogStatus;
    }

    public void setInsertLogStatus(ArrayList<StatusVerificationModel> insertLogStatus) {
        InsertLogStatus = insertLogStatus;
    }

    public ArrayList<TotalCBMDetailsModel> getTotalCBMDetails() {
        return TotalCBMDetails;
    }

    public void setTotalCBMDetails(ArrayList<TotalCBMDetailsModel> totalCBMDetails) {
        TotalCBMDetails = totalCBMDetails;
    }

    public double getHF1() {
        return HF1;
    }

    public void setHF1(double HF1) {
        this.HF1 = HF1;
    }

    public double getHF2() {
        return HF2;
    }

    public void setHF2(double HF2) {
        this.HF2 = HF2;
    }

    public double getHT1() {
        return HT1;
    }

    public void setHT1(double HT1) {
        this.HT1 = HT1;
    }

    public double getHT2() {
        return HT2;
    }

    public void setHT2(double HT2) {
        this.HT2 = HT2;
    }

    public double getHAvg() {
        return HAvg;
    }

    public void setHAvg(double HAvg) {
        this.HAvg = HAvg;
    }

    public double getHvolume() {
        return Hvolume;
    }

    public void setHvolume(double hvolume) {
        Hvolume = hvolume;
    }
}
