package com.zebra.main.model.Export;

import java.util.ArrayList;

public class ContainerDetailsMainModel {
    int ExportId, ContainerId;
    ArrayList<ContainerDetailsModel> ContainerDetails = new ArrayList<>();

    public int getExportId() {
        return ExportId;
    }

    public void setExportId(int exportId) {
        ExportId = exportId;
    }

    public int getContainerId() {
        return ContainerId;
    }

    public void setContainerId(int containerId) {
        ContainerId = containerId;
    }

    public ArrayList<ContainerDetailsModel> getContainerDetails() {
        return ContainerDetails;
    }

    public void setContainerDetails(ArrayList<ContainerDetailsModel> containerDetails) {
        ContainerDetails = containerDetails;
    }
}
