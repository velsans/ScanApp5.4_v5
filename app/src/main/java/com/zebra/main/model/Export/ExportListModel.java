package com.zebra.main.model.Export;

import java.util.ArrayList;

public class ExportListModel {

    ArrayList<ExportCodeModel> ExportList=new ArrayList<>();

    public ArrayList<ExportCodeModel> getExportList() {
        return ExportList;
    }

    public void setExportList(ArrayList<ExportCodeModel> exportList) {
        ExportList = exportList;
    }
}
