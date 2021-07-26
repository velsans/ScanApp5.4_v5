package com.zebra.main.activity.purchase.ui.logs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.zebra.R;
import com.zebra.databinding.LogsRowBinding;
import java.util.ArrayList;
import java.util.List;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.LogsViewHolder> implements Filterable {

    Context mCtx;
    List<PurchaseLogsModels> LogsList;
    List<PurchaseLogsModels> LogsFilterList;
    private View.OnClickListener mOnItemClickListener;
    ValueFilter valueFilter;

    public LogsAdapter(Context mCtx, List<PurchaseLogsModels> LogsList) {
        this.mCtx = mCtx;
        this.LogsList = LogsList;
        this.LogsFilterList = LogsList;
    }

    @NonNull
    @Override
    public LogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.logs_row, parent, false);
        return new LogsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogsViewHolder holder, int position) {
        PurchaseLogsModels cashouttransactiopojo = LogsFilterList.get(position);
        holder.bind(cashouttransactiopojo);

    }

    public void setLogsDetials(List<PurchaseLogsModels> LogsList) {
        this.LogsFilterList = LogsList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (LogsFilterList == null) {
            return 0;
        }
        return LogsFilterList.size();
    }

    public class LogsViewHolder extends RecyclerView.ViewHolder {
        private LogsRowBinding binding;

        public LogsViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setTag(this);
            /*Onclick Item listener*/
            itemView.setOnClickListener(mOnItemClickListener);
        }

        public void bind(PurchaseLogsModels item) {
            binding.setPurchaseLogsDetails(item);
        }


    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
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
                LogsFilterList = LogsList;
            } else {
                List<PurchaseLogsModels> filteredList = new ArrayList<>();
                for (PurchaseLogsModels row : LogsFilterList) {
                    //if (row.getCASHOUTCODE().toLowerCase().contains(charString.toLowerCase()) || row.getCASHOUTCODE().contains(charSequence)) {
                      //  filteredList.add(row);
                   // }
                }
                LogsFilterList = filteredList;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = LogsFilterList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                    FilterResults results) {
                LogsFilterList = (ArrayList<PurchaseLogsModels>) results.values;
                notifyDataSetChanged();
        }
    }
}
