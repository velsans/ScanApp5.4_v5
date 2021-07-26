package com.zebra.main.activity.purchase;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.zebra.R;
import com.zebra.database.ExternalDataBaseHelperClass;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.databinding.ActivityExternalPurchaseAgreementBinding;
import com.zebra.main.activity.Common.GwwMainActivity;
import com.zebra.main.activity.Transfer.InventoryTransferActivity;
import com.zebra.main.activity.purchase.ui.agreement.AgreementFragment;
import com.zebra.main.model.externaldb.PurchaseAgreementModel;
import com.zebra.main.model.externaldb.PurchaseNoAgreementModel;
import com.zebra.utilities.Common;

import java.util.ArrayList;
import java.util.List;

public class ExternalPurchaseAgreementActivity extends AppCompatActivity implements LifecycleOwner {

    ExternalPurchaseAgreementViewModel viewModel;
    ActivityExternalPurchaseAgreementBinding externalPurchaseAgreementBinding;
    List<PurchaseNoAgreementModel> userArrayLists = new ArrayList<>();
    RecyclerView PurchaseAgreementRV;
    SwipeRefreshLayout refreshLay;
    ExternalPurchaseAgreementAdapter externalAgreementAdapter;
    GridLayoutManager horizontalLayoutManager;
    AlertDialog RemoveAlert = null;
    AlertDialog.Builder Removebuilder = null;

    public void refresh() {
        viewModel = ViewModelProviders.of(this).get(ExternalPurchaseAgreementViewModel.class);
        viewModel.RefreshAggrement();
        viewModel.progress.setValue(8);
        viewModel.getPuchaseNoAgreement(this).observe(this, userListUpdateObserver);
        externalPurchaseAgreementBinding.setViewModel(viewModel);
    }

    @Override
    protected void onResume() {
        refresh();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent oneIntent = new Intent(this, GwwMainActivity.class);
        startActivity(oneIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        externalPurchaseAgreementBinding = DataBindingUtil.setContentView(this, R.layout.activity_external_purchase_agreement);
        PurchaseAgreementRV = externalPurchaseAgreementBinding.agreementRecylcerView;
        refreshLay = externalPurchaseAgreementBinding.activityMainSwipeRefreshLayout;
        //refresh();
        refreshLay.setOnRefreshListener(() -> {
            refreshLay.setRefreshing(true);
            (new Handler()).postDelayed(() -> {
                refreshLay.setRefreshing(false);
                refresh();
            }, 1000);
        });
    }


    Observer<List<PurchaseNoAgreementModel>> userListUpdateObserver = new Observer<List<PurchaseNoAgreementModel>>() {
        @Override
        public void onChanged(List<PurchaseNoAgreementModel> userArrayList) {
            userArrayLists = userArrayList;
            externalAgreementAdapter = new ExternalPurchaseAgreementAdapter(ExternalPurchaseAgreementActivity.this, userArrayList);
            PurchaseAgreementRV.setLayoutManager(horizontalLayoutManager);
            RecyclerView.LayoutManager manager = new GridLayoutManager(ExternalPurchaseAgreementActivity.this, 4);
            PurchaseAgreementRV.setLayoutManager(manager);
            PurchaseAgreementRV.addItemDecoration(new DividerItemDecoration(ExternalPurchaseAgreementActivity.this, LinearLayoutManager.VERTICAL));
            externalAgreementAdapter.notifyDataSetChanged();
            PurchaseAgreementRV.setAdapter(externalAgreementAdapter);
            if (userArrayList.size() > 0) {
                //cashoutTodaysTransCount.setText(String.valueOf(userArrayList.size()));
                //cashoutTodaysTransAmount.setText(String.valueOf(TotalAmountVolume(userArrayList)));
                //todays_trans_empty.setVisibility(View.GONE);
                /*Onclick Item listener*/
                externalAgreementAdapter.setOnItemClickListener(onItemClickListener);
                externalAgreementAdapter.setOnItemLongClickListener(onItemLongClickListener);
            } else {
                viewModel.RefreshAggrement();
                viewModel.progress.setValue(8);
                //todays_trans_empty.setVisibility(View.VISIBLE);
            }
        }
    };

    private View.OnClickListener onItemClickListener = view -> {
        try {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            Log.e("position", ">>>>>>>>>>" + position);
            Common.Purchase.SelectedPurchaseId = userArrayLists.get(position).getPurchaseId();
            Common.Purchase.SelectedPurchaseNo = userArrayLists.get(position).getPurchaseNo();
            Common.Purchase.SelectedPurchaseIdStatus = userArrayLists.get(position).getSyncStatus();
            Common.Purchase.SelectedTransferStatus = userArrayLists.get(position).getTSyncStatus();
            Common.Purchase.SelectedReceivedStatus = userArrayLists.get(position).getRSyncStatus();

           /* Common.Purchase.SelectedTransAgencyID = userArrayLists.get(position).getTransferAgencyID();
            Common.Purchase.SelectedTransDriverID = userArrayLists.get(position).getTDriverID();
            Common.Purchase.SelectedTransTruckID = userArrayLists.get(position).getTTruckId();
            Common.Purchase.SelectedReceivedAgencyID = userArrayLists.get(position).getRAgencyID();
            Common.Purchase.SelectedReceivedDriverID = userArrayLists.get(position).getRDriverID();
            Common.Purchase.SelectedReceivedTruckID = userArrayLists.get(position).getRTruckId();

            Common.Purchase.SelectedReceivedToLocationID = userArrayLists.get(position).getRToLocationID();
            Common.Purchase.SelectedReceivedFromLocationID = userArrayLists.get(position).getRFromLocationID();
            Common.Purchase.SelectedTransToLocationID = userArrayLists.get(position).getTToLocationID();
            Common.Purchase.SelectedTransFromLocationID = userArrayLists.get(position).getTFromLocationID();

            Common.Purchase.SelectedTransTransPortMode = userArrayLists.get(position).getTTransportMode();
            Common.Purchase.SelectedTransLoadedby = userArrayLists.get(position).getTLoadedby();
            Common.Purchase.SelectedReceivedTransPortMode = userArrayLists.get(position).getRTransportMode();
            Common.Purchase.SelectedReceivedLoadedby = userArrayLists.get(position).getRLoadedby();

            Common.Purchase.SelectedTransStatrDate = userArrayLists.get(position).getTStartDateTime();
            Common.Purchase.SelectedTransEndDate = userArrayLists.get(position).getTEndDateTime();
            Common.Purchase.SelectedReceivedStartDate = userArrayLists.get(position).getRStartDateTime();
            Common.Purchase.SelectedReceivedEndDate = userArrayLists.get(position).getREndDateTime();*/

            if (Common.Purchase.SelectedPurchaseIdStatus == 0) {
                Common.Purchase.PurchaseScannedIsEditorViewFlag = false;
                Common.Purchase.IsScannrdEditorViewFlag = true;
            } else {
                Common.Purchase.PurchaseScannedIsEditorViewFlag = true;
                Common.Purchase.IsScannrdEditorViewFlag = false;
            }
           /* if (Common.Purchase.SelectedTransferStatus == 0) {
                Common.Purchase.PurchaseTransferIsEditorViewFlag = true;
            } else {
                Common.Purchase.PurchaseTransferIsEditorViewFlag = false;
            }
            if (Common.Purchase.SelectedReceivedStatus == 0) {
                Common.Purchase.PurchaseReceivedIsEditorViewFlag= true;
            } else {
                Common.Purchase.PurchaseReceivedIsEditorViewFlag = false;
            }*/
            Intent purchaseLogs = new Intent(this, ExternalPurchaseBottomBarActivity.class);
            startActivity(purchaseLogs);
        } catch (Exception ex) {

        }
    };
    private View.OnLongClickListener onItemLongClickListener = view -> {
        try {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            Log.e("Long-position", ">>>>>>>>>>" + position);
            Common.Purchase.SelectedPurchaseId = userArrayLists.get(position).getPurchaseId();
            RemoveMessage(CommonMessage(R.string.purchaseLogsHead), Common.Purchase.SelectedPurchaseId);
        } catch (Exception ex) {

        }
        return true;
    };

    public void RemoveMessage(String ErrorMessage, int Purchase_ID) {
        if (RemoveAlert != null && RemoveAlert.isShowing()) {
            return;
        }
        Removebuilder = new AlertDialog.Builder(this);
        Removebuilder.setMessage(ErrorMessage);
        Removebuilder.setCancelable(true);
        Removebuilder.setPositiveButton(CommonMessage(R.string.action_cancel),
                (dialog, id) -> dialog.cancel());
        Removebuilder.setNegativeButton(CommonMessage(R.string.action_Remove),
                (dialog, id) -> {
                    try {
                        viewModel.DeleteCompletely(Purchase_ID);
                    } catch (Exception e) {
                    }
                    dialog.cancel();
                });

        RemoveAlert = Removebuilder.create();
        RemoveAlert.show();
    }

    public String CommonMessage(int Common_Msg) {
        return this.getResources().getString(Common_Msg);
    }
}