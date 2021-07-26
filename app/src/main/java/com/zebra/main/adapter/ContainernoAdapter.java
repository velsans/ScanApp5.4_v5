package com.zebra.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zebra.R;

import java.util.ArrayList;
import java.util.List;

public class ContainernoAdapter extends BaseAdapter {
    Context context;
    List<String> locname;
    LayoutInflater inflter;

    public ContainernoAdapter(Context applicationContext, List<String> list) {
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
        view = inflter.inflate(R.layout.containernoselect, null);

        TextView fromname = view.findViewById(R.id.txt_containerNo);
        if (isNullOrEmpty(locname.get(i))) {
            fromname.setText("-");
        } else {
            fromname.setText(locname.get(i));
        }
        return view;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

}