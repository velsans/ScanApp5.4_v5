package com.zebra.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zebra.R;
import com.zebra.main.model.ExternalDB.FellingSectionModel;


import java.util.ArrayList;


public class FellingSectionAdapter extends BaseAdapter {
    Context context;
    ArrayList<FellingSectionModel> locname;
    LayoutInflater inflater;

    public FellingSectionAdapter(Context applicationContext, ArrayList<FellingSectionModel> list) {
        this.context = applicationContext;
        this.locname = list;
        inflater = (LayoutInflater.from(applicationContext));
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
        view = inflater.inflate(R.layout.fellingsection_infliater, null);

        TextView fromname = view.findViewById(R.id.txt_fellingsec);
        fromname.setText(String.valueOf(locname.get(i).getFellingSectionNumber()));
        return view;
    }
}
