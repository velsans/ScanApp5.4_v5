package com.zebra.main.model.ExternalDB;

public class FellingSectionModel {
    int ID, ConcessionId, FellingSectionNumber, LocationType,RowID;
    String ConcessionName, FellingSectionId, FellingCode;


    public String getFellingSectionId() {
        return FellingSectionId;
    }

    public void setFellingSectionId(String fellingSectionId) {
        FellingSectionId = fellingSectionId;
    }

    public int getConcessionId() {
        return ConcessionId;
    }

    public void setConcessionId(int concessionId) {
        ConcessionId = concessionId;
    }

    public String getConcessionName() {
        return ConcessionName;
    }

    public void setConcessionName(String concessionName) {
        ConcessionName = concessionName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getFellingSectionNumber() {
        return FellingSectionNumber;
    }

    public void setFellingSectionNumber(int fellingSectionNumber) {
        FellingSectionNumber = fellingSectionNumber;
    }

    public int getLocationType() {
        return LocationType;
    }

    public void setLocationType(int locationType) {
        LocationType = locationType;
    }

    public String getFellingCode() {
        return FellingCode;
    }

    public void setFellingCode(String fellingCode) {
        FellingCode = fellingCode;
    }

    public int getRowID() {
        return RowID;
    }

    public void setRowID(int rowID) {
        RowID = rowID;
    }
}
