package com.zebra.main.model.Export;

public class ContainersModel {
    int ContainerId, LogCount;
    String ContainerNumber;
    double ScannedCBM,GrossVolume;

    public int getContainerId() {
        return ContainerId;
    }

    public void setContainerId(int containerId) {
        ContainerId = containerId;
    }

    public String getContainerNumber() {
        return ContainerNumber;
    }

    public void setContainerNumber(String containerNumber) {
        ContainerNumber = containerNumber;
    }

    public double getScannedCBM() {
        return ScannedCBM;
    }

    public void setScannedCBM(double scannedCBM) {
        ScannedCBM = scannedCBM;
    }

    public int getLogCount() {
        return LogCount;
    }

    public void setLogCount(int logCount) {
        LogCount = logCount;
    }

    public double getGrossVolume() {
        return GrossVolume;
    }

    public void setGrossVolume(double grossVolume) {
        GrossVolume = grossVolume;
    }
}
