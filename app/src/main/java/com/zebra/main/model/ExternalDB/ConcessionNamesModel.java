package com.zebra.main.model.ExternalDB;

public class ConcessionNamesModel {
            int ID,FromLocationId,LocationType;
            String ConcessionName;

    public int getFromLocationId() {
        return FromLocationId;
    }

    public void setFromLocationId(int fromLocationId) {
        FromLocationId = fromLocationId;
    }

    public String getConcessionName() {
        return ConcessionName;
    }

    public void setConcessionName(String concessionName) {
        ConcessionName = concessionName;
    }

    public int getLocationType() {
        return LocationType;
    }

    public void setLocationType(int locationType) {
        LocationType = locationType;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
