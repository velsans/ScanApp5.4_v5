package com.zebra.main.model.externaldb;

public class PurchaseAgreementModel {

    int PurchaseId ,StatusId,PurchaseListid,TotalCount;
    String PurchaseNo ,WoodSpeciesCode,WoodSpecieId,DiameterRange, ValidUntil ;

    public int getPurchaseId() {
        return PurchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        PurchaseId = purchaseId;
    }

    public int getStatusId() {
        return StatusId;
    }

    public void setStatusId(int statusId) {
        StatusId = statusId;
    }

    public String getPurchaseNo() {
        return PurchaseNo;
    }

    public void setPurchaseNo(String purchaseNo) {
        PurchaseNo = purchaseNo;
    }

    public String getWoodSpeciesCode() {
        return WoodSpeciesCode;
    }

    public void setWoodSpeciesCode(String woodSpeciesCode) {
        WoodSpeciesCode = woodSpeciesCode;
    }

    public String getDiameterRange() {
        return DiameterRange;
    }

    public void setDiameterRange(String diameterRange) {
        DiameterRange = diameterRange;
    }

    public String getValidUntil() {
        return ValidUntil;
    }

    public void setValidUntil(String validUntil) {
        ValidUntil = validUntil;
    }

    public String getWoodSpecieId() {
        return WoodSpecieId;
    }

    public void setWoodSpecieId(String woodSpecieId) {
        WoodSpecieId = woodSpecieId;
    }

    public int getPurchaseListid() {
        return PurchaseListid;
    }

    public void setPurchaseListid(int purchaseListid) {
        PurchaseListid = purchaseListid;
    }

    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int totalCount) {
        TotalCount = totalCount;
    }
}
