package com.zebra.main.activity.purchase.ui.logs;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;

import com.zebra.BR;

import java.util.List;

public class LogsViewModel extends BaseObservable {

    String Barcode, Footer1, Footer2, Top1, Top2, Lenght, NoteF, NoteT, NoteL, LHF1, LHF2, LHT1, LHT2,Location, fellingSecNo, CurrentDate;


    public LogsViewModel() {
        //Location = new MutableLiveData<>();
        //fellingSecNo = new MutableLiveData<>();
        //CurrentDate = new MutableLiveData<>();
    }

    public void setBarcode(String barcode) {
        this.Barcode = barcode;
        notifyPropertyChanged(BR.barcode);
    }

    public void setFooter1(String footer1) {
        this.Footer1 = footer1;
        notifyPropertyChanged(BR.footer1);
    }

    public void setFooter2(String footer2) {
        this.Footer2 = footer2;
        notifyPropertyChanged(BR.footer2);
    }

    public void setTop1(String top1) {
        this.Top1 = top1;
        notifyPropertyChanged(BR.top1);
    }


    public void setTop2(String top2) {
        this.Top2 = top2;
        notifyPropertyChanged(BR.top2);
    }

    public void setLenght(String lenght) {
        this.Lenght = lenght;
        notifyPropertyChanged(BR.lenght);
    }

    public void setNoteF(String noteF) {
        this.NoteF = noteF;
        notifyPropertyChanged(BR.noteF);
    }

    public void setNoteT(String noteT) {
        this.NoteT = noteT;
        notifyPropertyChanged(BR.noteT);
    }

    public void setNoteL(String noteL) {
        this.NoteL = noteL;
        notifyPropertyChanged(BR.noteL);
    }

    public void setLHF1(String LHF1) {
        this.LHF1 = LHF1;
        notifyPropertyChanged(BR.lHF1);
    }

    public void setLHF2(String LHF2) {
        this.LHF2 = LHF2;
        notifyPropertyChanged(BR.lHF2);
    }

    public void setLHT1(String LHT1) {
        this.LHT1 = LHT1;
        notifyPropertyChanged(BR.lHT1);
    }

    public void setLHT2(String LHT2) {
        this.LHT2 = LHT2;
        notifyPropertyChanged(BR.lHT2);
    }

    @Bindable
    public String getBarcode() {
        return Barcode;
    }

    @Bindable
    public String getFooter1() {
        return Footer1;
    }

    @Bindable
    public String getFooter2() {
        return Footer2;
    }

    @Bindable
    public String getTop1() {
        return Top1;
    }

    @Bindable
    public String getTop2() {
        return Top2;
    }

    @Bindable
    public String getLenght() {
        return Lenght;
    }

    @Bindable
    public String getNoteF() {
        return NoteF;
    }

    @Bindable
    public String getNoteT() {
        return NoteT;
    }

    @Bindable
    public String getNoteL() {
        return NoteL;
    }

    @Bindable
    public String getLHF1() {
        return LHF1;
    }

    @Bindable
    public String getLHF2() {
        return LHF2;
    }

    @Bindable
    public String getLHT1() {
        return LHT1;
    }

    @Bindable
    public String getLHT2() {
        return LHT2;
    }
     public String getLocation(){
        return Location;
     }
    public String getFellingSectionNo(){
        return fellingSecNo;
    }
    public String getCurrentDate(){
        return CurrentDate;
    }


    public LiveData<List<PurchaseLogsModels>> getPuchaseLogs(FragmentActivity activity, int selectedPurchaseId) {
        return null;
    }
}