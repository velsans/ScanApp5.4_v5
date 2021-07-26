package com.zebra.main.model.externaldb;

import java.util.ArrayList;

public class ExternalDataBaseTableSizesModel {

    public Integer AgencyDetailsIndex,
            ConcessionNamesIndex,
            DrivedDetailsIndex,
            FellingRegistrationIndex,
            FellingSectionIndex,
            LocationDeviceIndex,
            LocationsIndex,
            TransferLogDetilsIndex,
            TransportModesIndex,
            TruckDetailsIndex,
            WoodSpicesIndex,
            LoadedIndex;

    public int getWoodSpicesIndex() {
        return WoodSpicesIndex;
    }

    public void setWoodSpicesIndex(int woodSpicesIndex) {
        WoodSpicesIndex = woodSpicesIndex;
    }

    public int getFellingRegistrationIndex() {
        return FellingRegistrationIndex;
    }

    public void setFellingRegistrationIndex(int fellingRegistrationIndex) {
        FellingRegistrationIndex = fellingRegistrationIndex;
    }

    public int getAgencyDetailsIndex() {
        return AgencyDetailsIndex;
    }

    public void setAgencyDetailsIndex(int agencyDetailsIndex) {
        AgencyDetailsIndex = agencyDetailsIndex;
    }

    public int getConcessionNamesIndex() {
        return ConcessionNamesIndex;
    }

    public void setConcessionNamesIndex(int concessionNamesIndex) {
        ConcessionNamesIndex = concessionNamesIndex;
    }

    public int getDrivedDetailsIndex() {
        return DrivedDetailsIndex;
    }

    public void setDrivedDetailsIndex(int drivedDetailsIndex) {
        DrivedDetailsIndex = drivedDetailsIndex;
    }

    public int getFellingSectionIndex() {
        return FellingSectionIndex;
    }

    public void setFellingSectionIndex(int fellingSectionIndex) {
        FellingSectionIndex = fellingSectionIndex;
    }

    public int getLocationDeviceIndex() {
        return LocationDeviceIndex;
    }

    public void setLocationDeviceIndex(int locationDeviceIndex) {
        LocationDeviceIndex = locationDeviceIndex;
    }

    public int getLocationsIndex() {
        return LocationsIndex;
    }

    public void setLocationsIndex(int locationsIndex) {
        LocationsIndex = locationsIndex;
    }

    public int getTransferLogDetilsIndex() {
        return TransferLogDetilsIndex;
    }

    public void setTransferLogDetilsIndex(int transferLogDetilsIndex) {
        TransferLogDetilsIndex = transferLogDetilsIndex;
    }

    public int getTransportModesIndex() {
        return TransportModesIndex;
    }

    public void setTransportModesIndex(int transportModesIndex) {
        TransportModesIndex = transportModesIndex;
    }

    public int getTruckDetailsIndex() {
        return TruckDetailsIndex;
    }

    public void setTruckDetailsIndex(int truckDetailsIndex) {
        TruckDetailsIndex = truckDetailsIndex;
    }

    public int getLoadedIndex() {
        return LoadedIndex;
    }

    public void setLoadedIndex(int loadedIndex) {
        LoadedIndex = loadedIndex;
    }

    /*External Device*/
    ArrayList<AgencyDetailsExternalModel> AgencyDetails = new ArrayList<>();
    ArrayList<DriverDetailsExternalModel> DriverDetails = new ArrayList<>();
    ArrayList<TransferLogDetailsExModel> TransferLogDetils = new ArrayList<>();
    ArrayList<TruckDetailsExternalModel> TruckDetails = new ArrayList<>();
    ArrayList<FellingSectionModel> FellingSection = new ArrayList<>();
    ArrayList<FellingRegisterModel> FellingRegister = new ArrayList<>();
    ArrayList<LocationDevicesModel> LocationDevices = new ArrayList<>();
    ArrayList<MasterTotalCount> MasterTotalCount = new ArrayList<MasterTotalCount>();

    public ArrayList<AgencyDetailsExternalModel> getAgencyDetails() {
        return AgencyDetails;
    }

    public void setAgencyDetails(ArrayList<AgencyDetailsExternalModel> agencyDetails) {
        AgencyDetails = agencyDetails;
    }

    public ArrayList<DriverDetailsExternalModel> getDriverDetails() {
        return DriverDetails;
    }

    public void setDriverDetails(ArrayList<DriverDetailsExternalModel> driverDetails) {
        DriverDetails = driverDetails;
    }

    public ArrayList<TransferLogDetailsExModel> getTransferLogDetils() {
        return TransferLogDetils;
    }

    public void setTransferLogDetils(ArrayList<TransferLogDetailsExModel> transferLogDetils) {
        TransferLogDetils = transferLogDetils;
    }

    public ArrayList<TruckDetailsExternalModel> getTruckDetails() {
        return TruckDetails;
    }

    public void setTruckDetails(ArrayList<TruckDetailsExternalModel> truckDetails) {
        TruckDetails = truckDetails;
    }

    public ArrayList<FellingSectionModel> getFellingSection() {
        return FellingSection;
    }

    public void setFellingSection(ArrayList<FellingSectionModel> fellingSection) {
        FellingSection = fellingSection;
    }

    public ArrayList<FellingRegisterModel> getFellingRegister() {
        return FellingRegister;
    }

    public void setFellingRegister(ArrayList<FellingRegisterModel> fellingRegister) {
        FellingRegister = fellingRegister;
    }

    public ArrayList<LocationDevicesModel> getLocationDevices() {
        return LocationDevices;
    }

    public void setLocationDevices(ArrayList<LocationDevicesModel> locationDevices) {
        LocationDevices = locationDevices;
    }

    public ArrayList<com.zebra.main.model.externaldb.MasterTotalCount> getMasterTotalCount() {
        return MasterTotalCount;
    }

    public void setMasterTotalCount(ArrayList<com.zebra.main.model.externaldb.MasterTotalCount> masterTotalCount) {
        MasterTotalCount = masterTotalCount;
    }
}
