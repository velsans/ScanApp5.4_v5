package com.zebra.main.model.Export;

public class ExportCodeModel {
    int ExportId, OrderId, CarrierId, ShippingAgentId, ContainerQuantity;
    String ExportCode, BookingNumber, CuttOffDateTime, StuffingDate;


    public int getExportId() {
        return ExportId;
    }

    public void setExportId(int exportId) {
        ExportId = exportId;
    }

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public int getCarrierId() {
        return CarrierId;
    }

    public void setCarrierId(int carrierId) {
        CarrierId = carrierId;
    }

    public int getShippingAgentId() {
        return ShippingAgentId;
    }

    public void setShippingAgentId(int shippingAgentId) {
        ShippingAgentId = shippingAgentId;
    }

    public int getContainerQuantity() {
        return ContainerQuantity;
    }

    public void setContainerQuantity(int containerQuantity) {
        ContainerQuantity = containerQuantity;
    }

    public String getExportCode() {
        return ExportCode;
    }

    public void setExportCode(String exportCode) {
        ExportCode = exportCode;
    }

    public String getBookingNumber() {
        return BookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        BookingNumber = bookingNumber;
    }

    public String getCuttOffDateTime() {
        return CuttOffDateTime;
    }

    public void setCuttOffDateTime(String cuttOffDateTime) {
        CuttOffDateTime = cuttOffDateTime;
    }

    public String getStuffingDate() {
        return StuffingDate;
    }

    public void setStuffingDate(String stuffingDate) {
        StuffingDate = stuffingDate;
    }

}