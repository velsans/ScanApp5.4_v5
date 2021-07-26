package com.zebra.main.model.Export;

import java.util.ArrayList;

public class TransportDetailsModel {
    ArrayList<AgencyNameModel> AgencyName = new ArrayList<>();
    ArrayList<TruckLicensePlateNoDetailsModel> TruckLicensePlateNo = new ArrayList<>();
    ArrayList<DriverDetailsModel> DriverName = new ArrayList<>();
    ArrayList<ContainerTypeModel> ContainerType = new ArrayList<>();

    public ArrayList<AgencyNameModel> getAgencyName() {
        return AgencyName;
    }

    public void setAgencyName(ArrayList<AgencyNameModel> agencyName) {
        AgencyName = agencyName;
    }

    public ArrayList<TruckLicensePlateNoDetailsModel> getTruckLicensePlateNo() {
        return TruckLicensePlateNo;
    }

    public void setTruckLicensePlateNo(ArrayList<TruckLicensePlateNoDetailsModel> truckLicensePlateNo) {
        TruckLicensePlateNo = truckLicensePlateNo;
    }

    public ArrayList<DriverDetailsModel> getDriverName() {
        return DriverName;
    }

    public void setDriverName(ArrayList<DriverDetailsModel> driverName) {
        DriverName = driverName;
    }

    public ArrayList<ContainerTypeModel> getContainerType() {
        return ContainerType;
    }

    public void setContainerType(ArrayList<ContainerTypeModel> containerType) {
        ContainerType = containerType;
    }
}
