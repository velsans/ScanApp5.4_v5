package com.zebra.main.model.Export;

import java.util.ArrayList;

public class ExportSbbLabelInputModel {
    String SbbLabel;
    int Exportid;

    public String getSbbLabel() {
        return SbbLabel;
    }

    public void setSbbLabel(String sbbLabel) {
        SbbLabel = sbbLabel;
    }

    public int getExportid() {
        return Exportid;
    }

    public void setExportid(int exportid) {
        Exportid = exportid;
    }

    ArrayList<ExportSbblabelOutputModel> SbbLabelDetails = new ArrayList<ExportSbblabelOutputModel>();

    public ArrayList<ExportSbblabelOutputModel> getSbbLabelDetails() {
        return SbbLabelDetails;
    }

    public void setSbbLabelDetails(ArrayList<ExportSbblabelOutputModel> sbbLabelDetails) {
        SbbLabelDetails = sbbLabelDetails;
    }
}
