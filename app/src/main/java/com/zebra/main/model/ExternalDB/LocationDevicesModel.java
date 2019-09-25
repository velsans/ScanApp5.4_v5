package com.zebra.main.model.ExternalDB;

public class LocationDevicesModel {

    int LDevId, LocationId, IsDefault, FromLocationId;
    String IMEI, DeviceName;

    public int getLDevId() {
        return LDevId;
    }

    public void setLDevId(int LDevId) {
        this.LDevId = LDevId;
    }

    public int getLocationId() {
        return LocationId;
    }

    public void setLocationId(int locationId) {
        LocationId = locationId;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public int getIsDefault() {
        return IsDefault;
    }

    public void setIsDefault(int isDefault) {
        IsDefault = isDefault;
    }

    public int getFromLocationId() {
        return FromLocationId;
    }

    public void setFromLocationId(int fromLocationId) {
        FromLocationId = fromLocationId;
    }
}
