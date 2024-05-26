package com.diev.salarymaster.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.diev.salarymaster.Model.Business;

import java.util.List;

public class SpinnerBusinessAdapter extends ArrayAdapter<Business> {

    public SpinnerBusinessAdapter(@NonNull Context context, @NonNull List<Business> businessList) {
        super(context, android.R.layout.simple_spinner_item, businessList);
        setDropDownViewResource(android.R.layout.simple_list_item_checked);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        Business business = getItem(position);
        if (business != null) {
            textView.setText(business.getName());
            textView.setTextColor(Color.BLACK);
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        Business business = getItem(position);
        if (business != null) {
            textView.setText(business.getName());
        }
        return view;
    }
}
