package com.zebra.main.model.Export;

import java.util.ArrayList;

public class ExportOrderDetailsModel {
    int ExportID;

    public int getExportID() {
        return ExportID;
    }

    public void setExportID(int exportID) {
        ExportID = exportID;
    }

    ArrayList<LogSummaryModel> LogSummaryDetails = new ArrayList<>();
    ArrayList<ContainersModel> ContainersDetails = new ArrayList<>();
    ArrayList<LogDetailsModel> LogDetails = new ArrayList<>();
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

    public ArrayList<TotalCBMDetailsModel> getTotalCBMDetails() {
        return TotalCBMDetails;
    }

    public void setTotalCBMDetails(ArrayList<TotalCBMDetailsModel> totalCBMDetails) {
        TotalCBMDetails = totalCBMDetails;
    }
}
