package com.zebra.main.model.FellingRegistration;

public class FellingRegisterListModel {
    int FellingRegID, Count, SyncStatus, IsActive, LocationID,UserID;
    String EndDateTime, FellingRegistrationDate, SyncTime, FellingSectionID, FellingRegistrationUniqueID, FellingRegistrationNumber,IMEI;
    double Volume;

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public String getFellingRegistrationUniqueID() {
        return FellingRegistrationUniqueID;
    }

    public void setFellingRegistrationUniqueID(String fellingRegistrationUniqueID) {
        FellingRegistrationUniqueID = fellingRegistrationUniqueID;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }

    public int getSyncStatus() {
        return SyncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        SyncStatus = syncStatus;
    }

    public String getSyncTime() {
        return SyncTime;
    }

    public void setSyncTime(String syncTime) {
        SyncTime = syncTime;
    }

    public int getFellingRegID() {
        return FellingRegID;
    }

    public void setFellingRegID(int fellingRegID) {
        FellingRegID = fellingRegID;
    }

    public String getFellingRegistrationNumber() {
        return FellingRegistrationNumber;
    }

    public void setFellingRegistrationNumber(String fellingRegistrationNumber) {
        FellingRegistrationNumber = fellingRegistrationNumber;
    }

    public String getFellingSectionID() {
        return FellingSectionID;
    }

    public void setFellingSectionID(String fellingSectionID) {
        FellingSectionID = fellingSectionID;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }


    public String getEndDateTime() {
        return EndDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        EndDateTime = endDateTime;
    }

    public String getFellingRegistrationDate() {
        return FellingRegistrationDate;
    }

    public void setFellingRegistrationDate(String fellingRegistrationDate) {
        FellingRegistrationDate = fellingRegistrationDate;
    }
}
