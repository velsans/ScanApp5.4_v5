package com.zebra.main.model.ExternalDB;

public class FellingRegisterModel {
    int ConcessionId, FellingSectionId, FellingSectionNumber, PlotId, TreeNumber, WoodSpeciesId,RowID;
    String ConcessionName, FellingCode, PlotNumber, WoodSpeciesCode;

    public int getConcessionId() {
        return ConcessionId;
    }

    public void setConcessionId(int concessionId) {
        ConcessionId = concessionId;
    }

    public int getFellingSectionId() {
        return FellingSectionId;
    }

    public void setFellingSectionId(int fellingSectionId) {
        FellingSectionId = fellingSectionId;
    }

    public int getFellingSectionNumber() {
        return FellingSectionNumber;
    }

    public void setFellingSectionNumber(int fellingSectionNumber) {
        FellingSectionNumber = fellingSectionNumber;
    }

    public int getPlotId() {
        return PlotId;
    }

    public void setPlotId(int plotId) {
        PlotId = plotId;
    }

    public int getTreeNumber() {
        return TreeNumber;
    }

    public void setTreeNumber(int treeNumber) {
        TreeNumber = treeNumber;
    }

    public int getWoodSpeciesId() {
        return WoodSpeciesId;
    }

    public void setWoodSpeciesId(int woodSpeciesId) {
        WoodSpeciesId = woodSpeciesId;
    }

    public String getConcessionName() {
        return ConcessionName;
    }

    public void setConcessionName(String concessionName) {
        ConcessionName = concessionName;
    }

    public String getFellingCode() {
        return FellingCode;
    }

    public void setFellingCode(String fellingCode) {
        FellingCode = fellingCode;
    }

    public String getPlotNumber() {
        return PlotNumber;
    }

    public void setPlotNumber(String plotNumber) {
        PlotNumber = plotNumber;
    }

    public String getWoodSpeciesCode() {
        return WoodSpeciesCode;
    }

    public void setWoodSpeciesCode(String woodSpeciesCode) {
        WoodSpeciesCode = woodSpeciesCode;
    }

    public int getRowID() {
        return RowID;
    }

    public void setRowID(int rowID) {
        RowID = rowID;
    }
}

