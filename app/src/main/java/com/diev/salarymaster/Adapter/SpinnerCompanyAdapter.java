package com.diev.salarymaster.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.diev.salarymaster.Model.Company;

import java.util.List;

public class SpinnerCompanyAdapter extends ArrayAdapter<Company> {

    public SpinnerCompanyAdapter(@NonNull Context context, @NonNull List<Company> companies) {
        super(context, android.R.layout.simple_spinner_item, companies);
        setDropDownViewResource(android.R.layout.simple_list_item_checked);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        Company company = getItem(position);
        if (company != null) {
            textView.setText(company.getName());
            textView.setTextColor(Color.BLACK);
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        Company company = getItem(position);
        if (company != null) {
            textView.setText(company.getName());
        }
        return view;
    }
}
