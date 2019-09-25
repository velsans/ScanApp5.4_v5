package com.zebra.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.zebra.R;
import com.zebra.main.model.ExternalDB.TransportModesModel;
import com.zebra.utilities.Common;

import java.util.ArrayList;


public class TransportAdapter extends BaseAdapter {
    Context context;
    ArrayList<TransportModesModel> modename;
    LayoutInflater inflter;

    public TransportAdapter(Context applicationContext, ArrayList<TransportModesModel> list) {
        this.context = applicationContext;
        this.modename = list;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return modename.size();
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.transportmode_infliator, null);
        CheckBox TransferMode = view.findViewById(R.id.transMode_infi);
        if (Common.TransportTypeId == modename.get(position).getTransportTypeId()) {
            TransferMode.setChecked(true);
        } else {
            TransferMode.setChecked(false);
        }
        TransferMode.setText(modename.get(position).getTransportMode());
        TransferMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.TransportTypeId = modename.get(position).getTransportTypeId();
                Common.TransportMode = modename.get(position).getTransportMode();
                notifyDataSetChanged();
            }
        });

        TransferMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.TransportTypeId = modename.get(position).getTransportTypeId();
                Common.TransportMode = modename.get(position).getTransportMode();
                notifyDataSetChanged();
            }
        });
        return view;
    }
}

