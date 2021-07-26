package com.zebra.main.activity.purchase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.zebra.R;
import com.zebra.databinding.ExternalpurchaseagreementRowBinding;
import com.zebra.main.model.externaldb.PurchaseAgreementModel;
import com.zebra.main.model.externaldb.PurchaseNoAgreementModel;

import java.util.ArrayList;
import java.util.List;

public class ExternalPurchaseAgreementAdapter extends RecyclerView.Adapter<ExternalPurchaseAgreementAdapter.ExternalPurchaseAgreementViewHolder> implements Filterable {

    Context mCtx;
    List<PurchaseNoAgreementModel> TransactionList;
    List<PurchaseNoAgreementModel> TransactionFilterList;
    private View.OnClickListener mOnItemClickListener;
    private View.OnLongClickListener mOnItemLongClickListener;
    ValueFilter valueFilter;

    public ExternalPurchaseAgreementAdapter(Context mCtx, List<PurchaseNoAgreementModel> transactionList) {
        this.mCtx = mCtx;
        this.TransactionList = transactionList;
        this.TransactionFilterList = transactionList;
    }

    @NonNull
    @Override
    public ExternalPurchaseAgreementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.externalpurchaseagreement_row, parent, false);
        return new ExternalPurchaseAgreementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExternalPurchaseAgreementViewHolder holder, int position) {
        PurchaseNoAgreementModel cashouttransactiopojo = TransactionFilterList.get(position);
        //Glide.with(mCtx).load(hero.getImageurl()).into(holder.imageView);
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT // generate random color
        int color = generator.getColor(position);
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(color);
        } else {
            holder.itemView.setBackgroundColor(color);
        }
        holder.bind(cashouttransactiopojo);
    }

    public void setTransactionDetials(List<PurchaseNoAgreementModel> transactionListsa) {
        this.TransactionFilterList = transactionListsa;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (TransactionFilterList == null) {
            return 0;
        }
        return TransactionFilterList.size();
    }

    public class ExternalPurchaseAgreementViewHolder extends RecyclerView.ViewHolder {
        private ExternalpurchaseagreementRowBinding binding;

        public ExternalPurchaseAgreementViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setTag(this);
            /*Onclick Item listener*/
            itemView.setOnClickListener(mOnItemClickListener);
            itemView.setOnLongClickListener(mOnItemLongClickListener);
        }

        public void bind(PurchaseNoAgreementModel item) {
            binding.setAgreementdetails(item);
        }
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(View.OnLongClickListener itemLongClickListener) {
        mOnItemLongClickListener = itemLongClickListener;
    }


    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String charString = charSequence.toString();
            if (charString.isEmpty()) {
                TransactionFilterList = TransactionList;
            } else {
                List<PurchaseNoAgreementModel> filteredList = new ArrayList<>();
                for (PurchaseNoAgreementModel row : TransactionFilterList) {
                    //if (row.getCASHOUTCODE().toLowerCase().contains(charString.toLowerCase()) || row.getCASHOUTCODE().contains(charSequence)) {
                    filteredList.add(row);
                    // }
                }
                TransactionFilterList = filteredList;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = TransactionFilterList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            TransactionFilterList = (ArrayList<PurchaseNoAgreementModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
