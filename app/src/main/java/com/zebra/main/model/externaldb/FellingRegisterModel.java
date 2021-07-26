package com.zebra.main.model.externaldb;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FellingRegisterModel {
    @SerializedName("RowID")
    @Expose
    private Integer rowID;
    @SerializedName("ConcessionId")
    @Expose
    private Integer concessionId;
    @SerializedName("ConcessionName")
    @Expose
    private String concessionName;
    @SerializedName("FellingSectionId")
    @Expose
    private Integer fellingSectionId;
    @SerializedName("FellingSectionNumber")
    @Expose
    private Integer fellingSectionNumber;
    @SerializedName("FellingCode")
    @Expose
    private String fellingCode;
    @SerializedName("PlotId")
    @Expose
    private Integer plotId;
    @SerializedName("PlotNumber")
    @Expose
    private String plotNumber;
    @SerializedName("TreeNumber")
    @Expose
    private Integer treeNumber;
    @SerializedName("WoodSpeciesCode")
    @Expose
    private String woodSpeciesCode;
    @SerializedName("WoodSpeciesId")
    @Expose
    private Integer woodSpeciesId;

    public Integer getRowID() {
        return rowID;
    }

    public void setRowID(Integer rowID) {
        this.rowID = rowID;
    }

    public Integer getConcessionId() {
        return concessionId;
    }

    public void setConcessionId(Integer concessionId) {
        this.concessionId = concessionId;
    }

    public String getConcessionName() {
        return concessionName;
    }

    public void setConcessionName(String concessionName) {
        this.concessionName = concessionName;
    }

    public Integer getFellingSectionId() {
        return fellingSectionId;
    }

    public void setFellingSectionId(Integer fellingSectionId) {
        this.fellingSectionId = fellingSectionId;
    }

    public Integer getFellingSectionNumber() {
        return fellingSectionNumber;
    }

    public void setFellingSectionNumber(Integer fellingSectionNumber) {
        this.fellingSectionNumber = fellingSectionNumber;
    }

    public String getFellingCode() {
        return fellingCode;
    }

    public void setFellingCode(String fellingCode) {
        this.fellingCode = fellingCode;
    }

    public Integer getPlotId() {
        return plotId;
    }

    public void setPlotId(Integer plotId) {
        this.plotId = plotId;
    }

    public String getPlotNumber() {
        return plotNumber;
    }

    public void setPlotNumber(String plotNumber) {
        this.plotNumber = plotNumber;
    }

    public Integer getTreeNumber() {
        return treeNumber;
    }

    public void setTreeNumber(Integer treeNumber) {
        this.treeNumber = treeNumber;
    }

    public String getWoodSpeciesCode() {
        return woodSpeciesCode;
    }

    public void setWoodSpeciesCode(String woodSpeciesCode) {
        this.woodSpeciesCode = woodSpeciesCode;
    }

    public Integer getWoodSpeciesId() {
        return woodSpeciesId;
    }

    public void setWoodSpeciesId(Integer woodSpeciesId) {
        this.woodSpeciesId = woodSpeciesId;
    }
}