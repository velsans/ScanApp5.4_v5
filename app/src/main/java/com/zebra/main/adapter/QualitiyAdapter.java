package com.zebra.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zebra.R;
import com.zebra.main.model.QualityModel;

import java.util.ArrayList;

public class QualitiyAdapter extends BaseAdapter {
    Context context;
    ArrayList<QualityModel> loc;
    LayoutInflater inflter;

    public QualitiyAdapter(Context applicationContext, ArrayList<QualityModel> list) {
        this.context = applicationContext;
        this.loc = list;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return loc.size();
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
        view = inflter.inflate(R.layout.classification_infiliator, null);

        TextView names = view.findViewById(R.id.txt_class);
        names.setText(loc.get(i).getQuality());
        return view;
    }
}