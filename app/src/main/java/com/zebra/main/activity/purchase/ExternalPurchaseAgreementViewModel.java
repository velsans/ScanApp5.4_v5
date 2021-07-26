package com.zebra.main.activity.purchase;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.main.api.ApiClient;
import com.zebra.main.api.ApiInterface;
import com.zebra.main.firebase.CrashAnalytics;
import com.zebra.main.model.externaldb.PurchaseAgreementInputModel;
import com.zebra.main.model.externaldb.PurchaseAgreementModel;
import com.zebra.main.model.externaldb.PurchaseNoAgreementModel;
import com.zebra.utilities.Common;
import com.zebra.utilities.GwwException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExternalPurchaseAgreementViewModel extends ViewModel {

    private ExternalDataBaseHelperClass mDBExternalHelper;
    private InternalDataBaseHelperClass mDBInternalHelper;
    private MutableLiveData<List<PurchaseNoAgreementModel>> purchaseNoAgreementData;
    public MutableLiveData<Integer> progress;
    ApiInterface ClientInfoApi;
    Context context;

    public ExternalPurchaseAgreementViewModel() {
        purchaseNoAgreementData = new MutableLiveData<>();
        ClientInfoApi = ApiClient.getInstance().getUserService();
        progress = new MutableLiveData<>();
        progress.setValue(8);
    }

    public void RefreshAggrement() {
        try {
            progress.setValue(0);
            mDBExternalHelper = new ExternalDataBaseHelperClass(context);
            mDBInternalHelper = new InternalDataBaseHelperClass(context);
            Common.PurchaseLogsIndex = mDBInternalHelper.getPurchaseLogsRowID();
            Log.e("RefreshAggrement", ">>>>>>>>>>");
            PurchaseAgreementInputModel purchaseModel = new PurchaseAgreementInputModel();
            purchaseModel.setExternalLogRowId(Common.PurchaseLogsIndex);
            ClientInfoApi = ApiClient.getApiInterface();
            ClientInfoApi.GetAgreementPurchaseLogs(purchaseModel).enqueue(new Callback<PurchaseAgreementInputModel>() {
                @Override
                public void onResponse(Call<PurchaseAgreementInputModel> call, Response<PurchaseAgreementInputModel> response) {
                    if (GwwException.GwwException(response.code()) == true) {
                        if (response.body() != null) {
                            Common.PurchaseAgreementSync.clear();
                            Common.PurchaseAgreementSync.addAll(response.body().getPurchaseAgreement());
                            if (Common.PurchaseAgreementSync.size() > 0) {
                                mDBInternalHelper.insertInternalPurchaseAgreementREPLACEIB(Common.PurchaseAgreementSync);
                                /*
                                for (PurchaseAgreementModel purchasMod : Common.PurchaseAgreementSync)
                                {
                                    boolean insetFlag = mDBInternalHelper.getPurchasePurcahseListIDCheck(purchasMod.getPurchaseId(), purchasMod.getPurchaseNo(), purchasMod.getDiameterRange(), purchasMod.getWoodSpeciesCode());
                                    if (insetFlag == false) {
                                        mDBInternalHelper.insertInternalPurchaseAgreementREPLACEIB(Common.PurchaseAgreementSync);
                                    }
                                }
                                */
                            }
                            Common.PurchaseLogsSync.clear();
                            Common.PurchaseLogsSync.addAll(response.body().getExternalPurchaseLogs());
                            if (Common.PurchaseLogsSync.size() > 0) {
                                mDBExternalHelper.insertPurchaseLogsREPLACE(Common.PurchaseLogsSync);
                                ExportDatabaseToStorage(context);
                            }
                            progress.setValue(8);
                            getPuchaseNoAgreement(context);
                        }
                    }
                }

                @Override
                public void onFailure(Call<PurchaseAgreementInputModel> call, Throwable t) {
                    progress.setValue(8);
                }
            });
        } catch (Exception ex) {
            progress.setValue(8);
            CrashAnalytics.CrashReport(ex);
        }
    }

    public MutableLiveData<List<PurchaseNoAgreementModel>> getPuchaseNoAgreement(Context context) {
        try {
            this.context = context;
            mDBInternalHelper = new InternalDataBaseHelperClass(context);
            purchaseNoAgreementData.setValue(mDBInternalHelper.getPurchaseNoAgreement());
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
        return purchaseNoAgreementData;
    }

    public void ExportDatabaseToStorage(Context context) {
        try {
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GWW";
            File sd = new File(dir);
            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + context.getPackageName() + "/databases/GWW.db";
                String backupDBPath = "GWW.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }

    public void DeleteCompletely(int Purchase_ID) {
        try {
            mDBInternalHelper.DeletePurchaseAgreements(Purchase_ID);
            mDBInternalHelper.DeletePurchaseExternalLogDetails(Purchase_ID);
            mDBInternalHelper.DeletePurchaseInventoryTransferScanned(Purchase_ID);
            mDBInternalHelper.DeletePurchaseInventoryReceivedScanned(Purchase_ID);
            mDBInternalHelper.DeletePurchaseTransferList(Purchase_ID);
            mDBInternalHelper.DeletePurchaseReceivedList(Purchase_ID);
            // Delete external logs
            //mDBExternalHelper.DeletePurchaseAgreementsExternal(Purchase_ID);
            //mDBExternalHelper.DeletePurchaseExternalLogDetailsExternal(Purchase_ID);
            // Refresh Agreement
            getPuchaseNoAgreement(context);
            Toast.makeText(context, "Successfully Removed from List", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            CrashAnalytics.CrashReport(ex);
        }
    }
}
