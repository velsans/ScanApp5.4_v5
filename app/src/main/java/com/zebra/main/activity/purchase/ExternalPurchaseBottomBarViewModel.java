package com.zebra.main.activity.purchase;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.api.ApiClient;
import com.zebra.main.api.ApiInterface;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.utilities.Common;

import java.util.ArrayList;

public class ExternalPurchaseBottomBarViewModel extends ViewModel {
    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;
    private ArrayList<String> purchaseAgreementWSCData;
    public MutableLiveData<Integer> progress;
    ApiInterface ClientInfoApi;
    Context context;

    public ExternalPurchaseBottomBarViewModel() {
        purchaseAgreementWSCData = new ArrayList<>();
        ClientInfoApi = ApiClient.getInstance().getUserService();
        progress = new MutableLiveData<>();
        progress.setValue(8);
    }
    // Agreement List
    public ArrayList<String> getPuchaseAgreement(Context context, int SelectedPurchaseID) {
        purchaseAgreementWSCData = new ArrayList<>();
        try {
            this.context = context;
            mDBInternalHelper = new InternalDataBaseHelperClass(context);
            purchaseAgreementWSCData = mDBInternalHelper.getExternalPurchaseAgreementWSC(SelectedPurchaseID);

            Common.Purchase.purchaseAgreementData.clear();
            Common.Purchase.purchaseAgreementData = mDBInternalHelper.getExternalPurchaseAgreement(SelectedPurchaseID);
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
        return purchaseAgreementWSCData;
    }
}
