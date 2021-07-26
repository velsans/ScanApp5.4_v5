package com.zebra.main.model.FellingRegistration;

import com.zebra.main.model.SyncStatusModel;

import java.util.ArrayList;

public class FellingRegistrationSyncModel {

    int FellingRegID, LocationID, TotalCount, UserID;
    String FellingSectionID, EndDateTime, FellingRegistrationDate, IMEI, FellingRegisterUniqueID, FellingRegistrationNumber;
   /* double Volume;

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }*/

    ArrayList<FellingRegisterInputListModel> FellingReg = new ArrayList<>();
    ArrayList<FellingTreeDetailsModel> FellingTreeDetails = new ArrayList<>();

    public String getFellingRegisterUniqueID() {
        return FellingRegisterUniqueID;
    }

    public void setFellingRegisterUniqueID(String fellingRegisterUniqueID) {
        FellingRegisterUniqueID = fellingRegisterUniqueID;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
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

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int totalCount) {
        TotalCount = totalCount;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getFellingSectionID() {
        return FellingSectionID;
    }

    public void setFellingSectionID(String fellingSectionID) {
        FellingSectionID = fellingSectionID;
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

    public ArrayList<FellingRegisterInputListModel> getFellingReg() {
        return FellingReg;
    }

    public void setFellingReg(ArrayList<FellingRegisterInputListModel> fellingReg) {
        FellingReg = fellingReg;
    }

    public ArrayList<FellingTreeDetailsModel> getFellingTreeDetails() {
        return FellingTreeDetails;
    }

    public void setFellingTreeDetails(ArrayList<FellingTreeDetailsModel> fellingTreeDetails) {
        FellingTreeDetails = fellingTreeDetails;
    }

    ArrayList<SyncStatusModel> Status = new ArrayList<SyncStatusModel>();

    public ArrayList<SyncStatusModel> getStatus() {
        return Status;
    }

    public void setStatus(ArrayList<SyncStatusModel> status) {
        Status = status;
    }
}
