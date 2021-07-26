package com.zebra.main.model.externaldb;

import com.zebra.main.activity.purchase.ui.logs.PurchaseLogsModels;

import java.util.ArrayList;

public class PurchaseAgreementInputModel {

    int ExternalLogRowId;

    ArrayList<PurchaseAgreementModel> PurchaseAgreement=new ArrayList<>();

    public ArrayList<PurchaseAgreementModel> getPurchaseAgreement() {
        return PurchaseAgreement;
    }

    public void setPurchaseAgreement(ArrayList<PurchaseAgreementModel> purchaseAgreement) {
        PurchaseAgreement = purchaseAgreement;
    }

    public int getExternalLogRowId() {
        return ExternalLogRowId;
    }

    public void setExternalLogRowId(int externalLogRowId) {
        ExternalLogRowId = externalLogRowId;
    }

    ArrayList<PurchaseLogsModels> ExternalPurchaseLogs=new ArrayList<>();

    public ArrayList<PurchaseLogsModels> getExternalPurchaseLogs() {
        return ExternalPurchaseLogs;
    }

    public void setExternalPurchaseLogs(ArrayList<PurchaseLogsModels> externalPurchaseLogs) {
        ExternalPurchaseLogs = externalPurchaseLogs;
    }
}
