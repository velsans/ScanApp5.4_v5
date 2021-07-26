
package com.zebra.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zebra.R;
import com.zebra.main.model.externaldb.DriverDetailsModel;

import java.util.ArrayList;

public class DriverAdapter extends BaseAdapter  {
    Context context;
    ArrayList<DriverDetailsModel> locname;
    ArrayList<DriverDetailsModel> tempCustomer;
    LayoutInflater inflter;

    public DriverAdapter(Context applicationContext, ArrayList<DriverDetailsModel> list) {
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        DriverDetailsModel customer = locname.get(position);
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.driver_infliator, null);
        }

        TextView fromname = convertView.findViewById(R.id.txt_driver);
        fromname.setText(customer.getDriverName());

        return convertView;
    }

  /*  @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            DriverDetailsModel customer = (DriverDetailsModel) resultValue;
            return customer.getDriverName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                //locname.clear();
                for (DriverDetailsModel people : tempCustomer) {
                    if (people.getDriverName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        locname.add(people);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = locname;
                filterResults.count = locname.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<DriverDetailsModel> c = (ArrayList<DriverDetailsModel>) results.values;
            if (results != null && results.count > 0) {
                // c.clear();
                for (DriverDetailsModel cust : c) {
                    c.add(cust);
                    notifyDataSetChanged();
                }
            } else {
                //  c.clear();
                notifyDataSetChanged();
            }
        }
    };*/
}