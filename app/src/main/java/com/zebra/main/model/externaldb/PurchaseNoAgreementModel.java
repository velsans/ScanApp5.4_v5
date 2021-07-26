package com.zebra.main.model.externaldb;

public class PurchaseNoAgreementModel {
    int PurchaseId,SyncStatus,TSyncStatus,RSyncStatus,TransferAgencyID,TDriverID,TTruckId,RAgencyID,RDriverID,RTruckId,
            TToLocationID,TFromLocationID,RToLocationID,RFromLocationID,TTransportMode,TLoadedby,RTransportMode,RLoadedby;
    String PurchaseNo,TStartDateTime,TEndDateTime,RStartDateTime,REndDateTime;
    public int getPurchaseId() {
        return PurchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        PurchaseId = purchaseId;
    }

    public String getPurchaseNo() {
        return PurchaseNo;
    }

    public void setPurchaseNo(String purchaseNo) {
        PurchaseNo = purchaseNo;
    }

    public int getSyncStatus() {
        return SyncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        SyncStatus = syncStatus;
    }

    public int getTSyncStatus() {
        return TSyncStatus;
    }

    public void setTSyncStatus(int TSyncStatus) {
        this.TSyncStatus = TSyncStatus;
    }

    public int getRSyncStatus() {
        return RSyncStatus;
    }

    public void setRSyncStatus(int RSyncStatus) {
        this.RSyncStatus = RSyncStatus;
    }

    public int getTransferAgencyID() {
        return TransferAgencyID;
    }

    public void setTransferAgencyID(int transferAgencyID) {
        TransferAgencyID = transferAgencyID;
    }

    public int getTDriverID() {
        return TDriverID;
    }

    public void setTDriverID(int TDriverID) {
        this.TDriverID = TDriverID;
    }

    public int getTTruckId() {
        return TTruckId;
    }

    public void setTTruckId(int TTruckId) {
        this.TTruckId = TTruckId;
    }

    public int getRAgencyID() {
        return RAgencyID;
    }

    public void setRAgencyID(int RAgencyID) {
        this.RAgencyID = RAgencyID;
    }

    public int getRDriverID() {
        return RDriverID;
    }

    public void setRDriverID(int RDriverID) {
        this.RDriverID = RDriverID;
    }

    public int getRTruckId() {
        return RTruckId;
    }

    public void setRTruckId(int RTruckId) {
        this.RTruckId = RTruckId;
    }

    public int getTToLocationID() {
        return TToLocationID;
    }

    public void setTToLocationID(int TToLocationID) {
        this.TToLocationID = TToLocationID;
    }

    public int getTFromLocationID() {
        return TFromLocationID;
    }

    public void setTFromLocationID(int TFromLocationID) {
        this.TFromLocationID = TFromLocationID;
    }

    public int getRToLocationID() {
        return RToLocationID;
    }

    public void setRToLocationID(int RToLocationID) {
        this.RToLocationID = RToLocationID;
    }

    public int getRFromLocationID() {
        return RFromLocationID;
    }

    public void setRFromLocationID(int RFromLocationID) {
        this.RFromLocationID = RFromLocationID;
    }

    public int getTTransportMode() {
        return TTransportMode;
    }

    public void setTTransportMode(int TTransportMode) {
        this.TTransportMode = TTransportMode;
    }

    public int getTLoadedby() {
        return TLoadedby;
    }

    public void setTLoadedby(int TLoadedby) {
        this.TLoadedby = TLoadedby;
    }

    public int getRTransportMode() {
        return RTransportMode;
    }

    public void setRTransportMode(int RTransportMode) {
        this.RTransportMode = RTransportMode;
    }

    public int getRLoadedby() {
        return RLoadedby;
    }

    public void setRLoadedby(int RLoadedby) {
        this.RLoadedby = RLoadedby;
    }

    public String getTStartDateTime() {
        return TStartDateTime;
    }

    public void setTStartDateTime(String TStartDateTime) {
        this.TStartDateTime = TStartDateTime;
    }

    public String getTEndDateTime() {
        return TEndDateTime;
    }

    public void setTEndDateTime(String TEndDateTime) {
        this.TEndDateTime = TEndDateTime;
    }

    public String getRStartDateTime() {
        return RStartDateTime;
    }

    public void setRStartDateTime(String RStartDateTime) {
        this.RStartDateTime = RStartDateTime;
    }

    public String getREndDateTime() {
        return REndDateTime;
    }

    public void setREndDateTime(String REndDateTime) {
        this.REndDateTime = REndDateTime;
    }
}
