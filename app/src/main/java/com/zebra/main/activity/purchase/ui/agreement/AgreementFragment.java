package com.zebra.main.activity.purchase.ui.agreement;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zebra.R;
import com.zebra.databinding.FragmentAgreementBinding;
import com.zebra.main.model.externaldb.PurchaseAgreementModel;
import com.zebra.utilities.Common;

import java.util.ArrayList;
import java.util.List;

public class AgreementFragment extends Fragment {

    private AgreementViewModel mViewModel;
    public static List<String> purchaseAgreementWSCLists = new ArrayList<>();
   // public static List<PurchaseAgreementModel> purchaseAgreementLists = new ArrayList<>();
    RecyclerView AgreementList;
    AgreementAdapter agreementAdapter;
    LinearLayoutManager horizontalLayoutManager;
    FragmentAgreementBinding agreementBinding;
    View agreement_rootview;

    public static AgreementFragment newInstance() {
        return new AgreementFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        agreementBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_agreement, container, false);
        //viewModel = new TransactionViewModel(getContext());
        agreementBinding.setLifecycleOwner(this);
        agreement_rootview = agreementBinding.getRoot();
        AgreementList = agreementBinding.agreementRecylcerView;
        refresh();
        return agreement_rootview;
    }

    public void refresh() {
        mViewModel = ViewModelProviders.of(getActivity()).get(AgreementViewModel.class);
        mViewModel.getPuchaseAgreement(getActivity(), Common.Purchase.SelectedPurchaseId).observe(getActivity(), userListUpdateObserver);
    }

    Observer<List<String>> userListUpdateObserver = new Observer<List<String>>() {
        @Override
        public void onChanged(List<String> userArrayList) {
            purchaseAgreementWSCLists.clear();
            purchaseAgreementWSCLists = userArrayList;
            agreementAdapter = new AgreementAdapter(getActivity(), purchaseAgreementWSCLists);
            horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            AgreementList.setLayoutManager(horizontalLayoutManager);
            agreementAdapter.notifyDataSetChanged();
            AgreementList.setAdapter(agreementAdapter);
            if (userArrayList.size() > 0) {
                //cashoutTodaysTransCount.setText(String.valueOf(userArrayList.size()));
                //cashoutTodaysTransAmount.setText(String.valueOf(TotalAmountVolume(userArrayList)));
            }
        }
    };
   /* Observer<List<PurchaseAgreementModel>> userListUpdateObserver = new Observer<List<PurchaseAgreementModel>>() {
        @Override
        public void onChanged(List<PurchaseAgreementModel> userArrayList) {
            purchaseAgreementLists.clear();
            purchaseAgreementLists = userArrayList;
            agreementAdapter = new AgreementAdapter(getActivity(), purchaseAgreementLists);
            horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            AgreementList.setLayoutManager(horizontalLayoutManager);
            agreementAdapter.notifyDataSetChanged();
            AgreementList.setAdapter(agreementAdapter);
            if (userArrayList.size() > 0) {
                //cashoutTodaysTransCount.setText(String.valueOf(userArrayList.size()));
                //cashoutTodaysTransAmount.setText(String.valueOf(TotalAmountVolume(userArrayList)));
            }
        }
    };*/

}