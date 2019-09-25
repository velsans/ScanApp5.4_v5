package com.zebra.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zebra.R;
import com.zebra.main.model.ExternalDB.ConcessionNamesModel;

import java.util.ArrayList;

public class ArrivalAdapter extends BaseAdapter {
    Context context;
    ArrayList<ConcessionNamesModel> locname;
    LayoutInflater inflter;

    public ArrivalAdapter(Context applicationContext, ArrayList<ConcessionNamesModel> list) {
        this.context = applicationContext;
        this.locname = list;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return locname.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.arrival_infliater, null);

        TextView fromname = view.findViewById(R.id.txt_arrival);
        fromname.setText(locname.get(i).getConcessionName());
        return view;
    }
}