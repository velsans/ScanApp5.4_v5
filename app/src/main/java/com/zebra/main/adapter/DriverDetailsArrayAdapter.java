package com.zebra.main.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.zebra.R;
import com.zebra.main.model.externaldb.DriverDetailsModel;

import java.util.ArrayList;
import java.util.List;

public class DriverDetailsArrayAdapter extends ArrayAdapter {

    private List<DriverDetailsModel> dataList;
    private Context mContext;
    private int itemLayout;

    private ListFilter listFilter = new ListFilter();
    private List<DriverDetailsModel> dataListAllItems;


    public DriverDetailsArrayAdapter(Context context, int resource, List<DriverDetailsModel> storeDataLst) {
        super(context, resource, storeDataLst);
        dataList = storeDataLst;
        mContext = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        Log.d("CustomListAdapter",
                dataList.get(position).getDriverName());
        return dataList.get(position).getDriverName();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }
        TextView fromname = view.findViewById(R.id.txt_driver);
        fromname.setText(getItem(position));
        return view;
    }

    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<DriverDetailsModel>(dataList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<DriverDetailsModel> matchValues = new ArrayList<DriverDetailsModel>();

                for (DriverDetailsModel dataItem : dataListAllItems) {
                    if (dataItem.getDriverName().toLowerCase().startsWith(searchStrLowerCase)) {
                        matchValues.add(dataItem);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<DriverDetailsModel>) results.values;
            } else {
                dataList = null;
            }
            notifyDataSetChanged();
        }

    }
}
