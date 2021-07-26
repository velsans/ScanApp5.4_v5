package com.zebra.main.model.externaldb;

public class LocationsModel {

    int ID,ToLocationId;
    String Location;

    public int getToLocationId() {
        return ToLocationId;
    }

    public void setToLocationId(int toLocationId) {
        ToLocationId = toLocationId;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
