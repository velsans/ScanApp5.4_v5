package com.zebra.main.activity.purchase.ui.agreement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.zebra.R;
import com.zebra.main.model.externaldb.PurchaseAgreementModel;

import java.util.List;

public class AgreementDetailsAdapter extends RecyclerView.Adapter<AgreementDetailsAdapter.ChildViewHolder> {
    private List<PurchaseAgreementModel> ChildItemList;
    private Context _Context;

    AgreementDetailsAdapter(Context _context,List<PurchaseAgreementModel> childItemList) {
        this.ChildItemList = childItemList;
        this._Context = _context;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.agreement_details_row, viewGroup, false);
        return new ChildViewHolder(view);
    }

    public PurchaseAgreementModel getItem(int position) {
        return ChildItemList.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder childViewHolder, int position) {
        PurchaseAgreementModel childItem = ChildItemList.get(position);
       /* ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
        int color = generator.getColor(getItem(position));
        if (position % 2 == 0) {
            childViewHolder.ChildItemLAY.setBackgroundColor(color);
        } else {
            childViewHolder.ChildItemLAY.setBackgroundColor(color);
        }*/
        childViewHolder.ChildItemWSC.setText(childItem.getWoodSpeciesCode());
        childViewHolder.ChildItemRange.setText(childItem.getDiameterRange());
    }

    @Override
    public int getItemCount() {
        return ChildItemList.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView ChildItemWSC, ChildItemRange;
        LinearLayout ChildItemLAY;

        ChildViewHolder(View itemView) {
            super(itemView);
            ChildItemWSC = itemView.findViewById(R.id.purchase_details_WSC);
            ChildItemRange = itemView.findViewById(R.id.purchase_details_Range);
            //ChildItemLAY = itemView.findViewById(R.id.purchase_details_layout);
        }
    }
}