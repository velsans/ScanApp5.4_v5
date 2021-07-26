package com.zebra.main.model.externaldb;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FellingSectionModel {
    int ID;
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
    private String fellingSectionId;
    @SerializedName("FellingSectionNumber")
    @Expose
    private Integer fellingSectionNumber;
    @SerializedName("FellingCode")
    @Expose
    private String fellingCode;
    @SerializedName("LocationType")
    @Expose
    private Integer locationType;

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

    public String getFellingSectionId() {
        return fellingSectionId;
    }

    public void setFellingSectionId(String fellingSectionId) {
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

    public Integer getLocationType() {
        return locationType;
    }

    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
