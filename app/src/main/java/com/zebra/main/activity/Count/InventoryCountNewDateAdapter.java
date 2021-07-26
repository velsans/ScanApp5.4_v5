package com.zebra.main.activity.Count;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zebra.R;

import java.util.ArrayList;

public class InventoryCountNewDateAdapter extends RecyclerView.Adapter<InventoryCountNewDateAdapter.ViewHolder> {

    ArrayList<String> Dates;

    public InventoryCountNewDateAdapter(ArrayList<String> Dates) {
        this.Dates = Dates;
    }

    @Override
    public InventoryCountNewDateAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inventory_count_dates_infliator, viewGroup, false);
        return new ViewHolder(view1);
    }

    @Override
    public void onBindViewHolder(InventoryCountNewDateAdapter.ViewHolder Viewholder, int i) {
        Viewholder.SubjectTextView.setText(Dates.get(i));
    }

    @Override
    public int getItemCount() {

        return Dates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView SubjectTextView;

        public ViewHolder(View view) {
            super(view);
            SubjectTextView = view.findViewById(R.id.textview_dates);
        }
    }

}