package com.zebra.main.model.ExternalDB;

public class LoadedModel {
    int Loadedid,IsActive;
    String Name;

    public int getLoadedid() {
        return Loadedid;
    }

    public void setLoadedid(int loadedid) {
        Loadedid = loadedid;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
