package com.zebra.main.activity.purchase.ui.agreement

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zebra.database.InternalDataBaseHelperClass
import com.zebra.main.firebase.CrashAnalytics
import com.zebra.main.model.externaldb.PurchaseAgreementModel
import com.zebra.utilities.Common

class AgreementViewModel : ViewModel() {
    private val mText: MutableLiveData<String>
    private var mDBInternalHelper: InternalDataBaseHelperClass? = null
    //private var purchaseAgreementData: MutableLiveData<List<PurchaseAgreementModel>>
    private var purchaseAgreementWSCData: MutableLiveData<List<String>>
    var context: Context? = null
    fun getPuchaseAgreement(context: Context?, SelectedPurchaseID: Int): MutableLiveData<List<String>> {
        try {
            this.context = context
            //purchaseAgreementData = MutableLiveData()
            purchaseAgreementWSCData = MutableLiveData()
            mDBInternalHelper = InternalDataBaseHelperClass(context)
            purchaseAgreementWSCData.value = mDBInternalHelper!!.getExternalPurchaseAgreementWSC(SelectedPurchaseID)
            //purchaseAgreementData.value = mDBInternalHelper!!.getExternalPurchaseAgreement(SelectedPurchaseID)

            Common.Purchase.purchaseAgreementData.clear()
            Common.Purchase.purchaseAgreementData = mDBInternalHelper!!.getExternalPurchaseAgreement(SelectedPurchaseID)
        } catch (ex: Exception) {
            CrashAnalytics.CrashReport(ex)
        }
        return purchaseAgreementWSCData
    }

    val text: LiveData<String>
        get() = mText

    init {
        //purchaseAgreementData = MutableLiveData()
        purchaseAgreementWSCData = MutableLiveData()
        mText = MutableLiveData()
        mText.value = "This is Agreement fragment"
    }
}