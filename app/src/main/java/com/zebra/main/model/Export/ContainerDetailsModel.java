package com.zebra.main.model.Export;

public class ContainerDetailsModel {
    Integer AgencyId, TruckId, TruckDriverId, ContainerTypeId,UpdateStatus;
    String Excavator, Loader, PointerName, StartTime, EndTime, ClosedBy, CheckedBy, CustomerOfficer, SBBOfficer, LVVOfficer, CustomSearNo,
            AgencySealNo, ContainerSerialNo, SecuritySeal1, SecuritySeal2, StuffingDate, SealKitPackageNumber,Message;
    double MaxGrossweight, VerifiedGrossWeight, maxPayLoad;

    public Integer getAgencyId() {
        return AgencyId;
    }

    public void setAgencyId(Integer agencyId) {
        AgencyId = agencyId;
    }

    public Integer getTruckId() {
        return TruckId;
    }

    public void setTruckId(Integer truckId) {
        TruckId = truckId;
    }

    public Integer getTruckDriverId() {
        return TruckDriverId;
    }

    public void setTruckDriverId(Integer truckDriverId) {
        TruckDriverId = truckDriverId;
    }

    public String getExcavator() {
        return Excavator;
    }

    public void setExcavator(String excavator) {
        Excavator = excavator;
    }

    public String getLoader() {
        return Loader;
    }

    public void setLoader(String loader) {
        Loader = loader;
    }

    public String getPointerName() {
        return PointerName;
    }

    public void setPointerName(String pointerName) {
        PointerName = pointerName;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getClosedBy() {
        return ClosedBy;
    }

    public void setClosedBy(String closedBy) {
        ClosedBy = closedBy;
    }

    public String getCheckedBy() {
        return CheckedBy;
    }

    public void setCheckedBy(String checkedBy) {
        CheckedBy = checkedBy;
    }

    public String getCustomerOfficer() {
        return CustomerOfficer;
    }

    public void setCustomerOfficer(String customerOfficer) {
        CustomerOfficer = customerOfficer;
    }

    public String getSBBOfficer() {
        return SBBOfficer;
    }

    public void setSBBOfficer(String SBBOfficer) {
        this.SBBOfficer = SBBOfficer;
    }

    public String getLVVOfficer() {
        return LVVOfficer;
    }

    public void setLVVOfficer(String LVVOfficer) {
        this.LVVOfficer = LVVOfficer;
    }

    public String getCustomSearNo() {
        return CustomSearNo;
    }

    public void setCustomSearNo(String customSearNo) {
        CustomSearNo = customSearNo;
    }

    public String getAgencySealNo() {
        return AgencySealNo;
    }

    public void setAgencySealNo(String agencySealNo) {
        AgencySealNo = agencySealNo;
    }


    public String getSecuritySeal1() {
        return SecuritySeal1;
    }

    public void setSecuritySeal1(String securitySeal1) {
        SecuritySeal1 = securitySeal1;
    }

    public String getSecuritySeal2() {
        return SecuritySeal2;
    }

    public void setSecuritySeal2(String securitySeal2) {
        SecuritySeal2 = securitySeal2;
    }

    public String getStuffingDate() {
        return StuffingDate;
    }

    public void setStuffingDate(String stuffingDate) {
        StuffingDate = stuffingDate;
    }

    public Integer getContainerTypeId() {
        return ContainerTypeId;
    }

    public void setContainerTypeId(Integer containerTypeId) {
        ContainerTypeId = containerTypeId;
    }

    public double getMaxGrossweight() {
        return MaxGrossweight;
    }

    public void setMaxGrossweight(double maxGrossweight) {
        MaxGrossweight = maxGrossweight;
    }

    public double getVerifiedGrossWeight() {
        return VerifiedGrossWeight;
    }

    public void setVerifiedGrossWeight(double verifiedGrossWeight) {
        VerifiedGrossWeight = verifiedGrossWeight;
    }

    public double getMaxPayLoad() {
        return maxPayLoad;
    }

    public void setMaxPayLoad(double maxPayLoad) {
        this.maxPayLoad = maxPayLoad;
    }

    public String getContainerSerialNo() {
        return ContainerSerialNo;
    }

    public void setContainerSerialNo(String containerSerialNo) {
        ContainerSerialNo = containerSerialNo;
    }

    public String getSealKitPackageNumber() {
        return SealKitPackageNumber;
    }

    public void setSealKitPackageNumber(String sealKitPackageNumber) {
        SealKitPackageNumber = sealKitPackageNumber;
    }

    public Integer getUpdateStatus() {
        return UpdateStatus;
    }

    public void setUpdateStatus(Integer updateStatus) {
        UpdateStatus = updateStatus;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
