package com.zebra.main.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zebra.R;
import com.zebra.main.model.ExternalDB.LocationsModel;

import java.util.ArrayList;


public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<LocationsModel> loc;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, ArrayList<LocationsModel> list) {
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
        view = inflter.inflate(R.layout.tolocation_infliator, null);

        TextView names = view.findViewById(R.id.txt);
        names.setText(loc.get(i).getLocation());
        return view;
    }
}