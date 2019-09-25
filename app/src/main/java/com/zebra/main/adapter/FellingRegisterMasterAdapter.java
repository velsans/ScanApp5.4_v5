package com.zebra.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zebra.R;
import com.zebra.main.model.ExternalDB.FellingRegisterModel;

import java.util.ArrayList;

    public class FellingRegisterMasterAdapter extends BaseAdapter {
        Context context;
        ArrayList<FellingRegisterModel> locname;
        LayoutInflater inflater;

        public FellingRegisterMasterAdapter(Context applicationContext,ArrayList<FellingRegisterModel> list) {
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
            view = inflater.inflate(R.layout.fellingregistermaster_infiliator, null);

            TextView treeNo = view.findViewById(R.id.txt_fellingTreeNo);
            TextView Wspeice = view.findViewById(R.id.txt_fellingWSpeice);
            treeNo.setText(locname.get(i).getTreeNumber());
            Wspeice.setText(locname.get(i).getWoodSpeciesCode());
            return view;
        }
    }