package com.zebra.main.model.Export;

public class QuotationExternalModel {

    String QuotationDetailsId, WoodSpeciesCode, WoodSpeciesId, DiameterSizes, QuotationNumber, OrderNumber,Status;
    double TotalVolume;
    int StatusId;

    public String getQuotationDetailsId() {
        return QuotationDetailsId;
    }

    public void setQuotationDetailsId(String quotationDetailsId) {
        QuotationDetailsId = quotationDetailsId;
    }

    public String getWoodSpeciesCode() {
        return WoodSpeciesCode;
    }

    public void setWoodSpeciesCode(String woodSpeciesCode) {
        WoodSpeciesCode = woodSpeciesCode;
    }

    public String getWoodSpeciesId() {
        return WoodSpeciesId;
    }

    public void setWoodSpeciesId(String woodSpeciesId) {
        WoodSpeciesId = woodSpeciesId;
    }

    public String getDiameterSizes() {
        return DiameterSizes;
    }

    public void setDiameterSizes(String diameterSizes) {
        DiameterSizes = diameterSizes;
    }

    public String getQuotationNumber() {
        return QuotationNumber;
    }

    public void setQuotationNumber(String quotationNumber) {
        QuotationNumber = quotationNumber;
    }

    public double getTotalVolume() {
        return TotalVolume;
    }

    public void setTotalVolume(double totalVolume) {
        TotalVolume = totalVolume;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getStatusId() {
        return StatusId;
    }

    public void setStatusId(int statusId) {
        StatusId = statusId;
    }
}
