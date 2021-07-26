package com.zebra.main.activity.purchase.ui.logs;

import com.zebra.main.model.SyncStatusModel;

import java.util.ArrayList;

public class PurchaseLogsSyncInputModel {
    ArrayList<SyncStatusModel> Status = new ArrayList<SyncStatusModel>();

    public ArrayList<SyncStatusModel> getStatus() {
        return Status;
    }

    public void setStatus(ArrayList<SyncStatusModel> status) {
        Status = status;
    }

    ArrayList<PurchaseLogsSyncModel> HHPurchaselist = new ArrayList<PurchaseLogsSyncModel>();

    public ArrayList<PurchaseLogsSyncModel> getHHPurchaselist() {
        return HHPurchaselist;
    }

    public void setHHPurchaselist(ArrayList<PurchaseLogsSyncModel> HHPurchaselist) {
        this.HHPurchaselist = HHPurchaselist;
    }
}
