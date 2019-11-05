package com.zebra.main.model.Export;

public class QuotationModel {
    String QuatationDetailsId,WoodSpeciesCode,WoodSpeciesId,DiameterSizes,QuotationNumber;
    double TotalVolume;

    public String getQuatationDetailsId() {
        return QuatationDetailsId;
    }

    public void setQuatationDetailsId(String quatationDetailsId) {
        QuatationDetailsId = quatationDetailsId;
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
}
