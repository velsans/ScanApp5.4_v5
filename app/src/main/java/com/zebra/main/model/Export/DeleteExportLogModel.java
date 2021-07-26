package com.zebra.main.model.Export;

import java.util.ArrayList;

public class DeleteExportLogModel {
    int ExportId,UserID;
    String SBBLabel;

    public int getExportId() {
        return ExportId;
    }

    public void setExportId(int exportId) {
        ExportId = exportId;
    }

    public String getSBBLabel() {
        return SBBLabel;
    }

    public void setSBBLabel(String SBBLabel) {
        this.SBBLabel = SBBLabel;
    }

    ArrayList<LogSummaryModel> LogSummaryDetails = new ArrayList<>();
    ArrayList<ContainersModel> ContainersDetails = new ArrayList<>();
    ArrayList<LogDetailsModel> LogDetails = new ArrayList<>();
    ArrayList<StatusVerificationModel> DeleteLogStatus = new ArrayList<>();
    ArrayList<TotalCBMDetailsModel> TotalCBMDetails = new ArrayList<>();

    public ArrayList<StatusVerificationModel> getDeleteLogStatus() {
        return DeleteLogStatus;
    }

    public void setDeleteLogStatus(ArrayList<StatusVerificationModel> deleteLogStatus) {
        DeleteLogStatus = deleteLogStatus;
    }

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

    public ArrayList<TotalCBMDetailsModel> getTotalCBMDetails() {
        return TotalCBMDetails;
    }

    public void setTotalCBMDetails(ArrayList<TotalCBMDetailsModel> totalCBMDetails) {
        TotalCBMDetails = totalCBMDetails;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }
}
