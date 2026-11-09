package com.example.bilku;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BillListAdapter extends ArrayAdapter<String> {

    private Activity context;
    private ArrayList<String> months;
    private ArrayList<String> costs;

    public BillListAdapter(Activity context,
                           ArrayList<String> months,
                           ArrayList<String> costs) {

        super(context, R.layout.list_bill_layout, months);
        this.context = context;
        this.months = months;
        this.costs = costs;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rootView = inflater.inflate(R.layout.list_bill_layout,
                null, true);

        TextView textViewMonth = (TextView)
                rootView.findViewById(R.id.textViewMonth);

        TextView textViewCost = (TextView)
                rootView.findViewById(R.id.textViewCost);

        textViewMonth.setText(months.get(position));
        textViewCost.setText(costs.get(position));

        return rootView;
    }
}
