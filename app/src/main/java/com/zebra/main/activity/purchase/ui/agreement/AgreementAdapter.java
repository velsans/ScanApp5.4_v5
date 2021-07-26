package com.zebra.main.activity.purchase.ui.agreement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.zebra.R;
import com.zebra.main.fragments.LogSummaryFragment;
import com.zebra.main.model.Export.LogSummaryModel;
import com.zebra.main.model.externaldb.PurchaseAgreementModel;
import com.zebra.utilities.Common;

import java.util.ArrayList;
import java.util.List;

public class AgreementAdapter extends RecyclerView.Adapter<AgreementAdapter.ParentViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<String> itemList;
    private Context _Context;
    LinearLayoutManager horizontalLayoutManager;

    public AgreementAdapter(Context _context, List<String> itemList) {
        this.itemList = itemList;
        this._Context = _context;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(
            @NonNull ViewGroup viewGroup,
            int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.agreement_row, viewGroup, false);
        return new ParentViewHolder(view);
    }

    public String getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder parentViewHolder, int position) {
        String parentItem = itemList.get(position);
        ArrayList<PurchaseAgreementModel> logSummaryChild = new ArrayList<>();
        if (Common.Purchase.purchaseAgreementData.size() > 0) {
            for (PurchaseAgreementModel logSummaryModel : Common.Purchase.purchaseAgreementData) {
                if (logSummaryModel.getWoodSpeciesCode().equals(itemList.get(position))) {
                    logSummaryChild.add(logSummaryModel);
                }
            }
        }
        parentViewHolder.ParentItemTitle.setText(parentItem);
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
        int color = generator.getColor(getItem(position));
        if (position % 2 == 0) {
            parentViewHolder.headingLAY.setBackgroundColor(color);
        } else {
            parentViewHolder.headingLAY.setBackgroundColor(color);
        }
        AgreementDetailsAdapter itemInnerRecyclerView = new AgreementDetailsAdapter(_Context, logSummaryChild);
        parentViewHolder.ChildRecyclerView.setVisibility(View.VISIBLE);
        horizontalLayoutManager = new LinearLayoutManager(_Context, LinearLayoutManager.VERTICAL, true);
        horizontalLayoutManager.setStackFromEnd(true);
        parentViewHolder.ChildRecyclerView.setLayoutManager(horizontalLayoutManager);
        parentViewHolder.ChildRecyclerView.setAdapter(itemInnerRecyclerView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ParentViewHolder extends RecyclerView.ViewHolder {
        private TextView ParentItemTitle;
        private LinearLayout headingLAY;
        private RecyclerView ChildRecyclerView;

        ParentViewHolder(final View itemView) {
            super(itemView);
            ParentItemTitle = itemView.findViewById(R.id.agreementWSC);
            ChildRecyclerView = itemView.findViewById(R.id.agreementDetailsRecylcerView);
            headingLAY = itemView.findViewById(R.id.heading_agreement);
        }
    }
}