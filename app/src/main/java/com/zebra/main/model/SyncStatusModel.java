package com.zebra.main.model;

public class SyncStatusModel {
    int status,syncStatus;
    String Message,SyncTime;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getSyncTime() {
        return SyncTime;
    }

    public void setSyncTime(String syncTime) {
        SyncTime = syncTime;
    }
}
