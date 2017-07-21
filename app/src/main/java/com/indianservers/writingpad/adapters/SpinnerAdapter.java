package com.indianservers.writingpad.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.indianservers.writingpad.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JNTUH on 19-07-2017.
 */

public class SpinnerAdapter extends ArrayAdapter<String> {
    private Context context1;
    private ArrayList<String> data;
    public Resources res;
    private LayoutInflater inflater;
    public SpinnerAdapter( Context context, ArrayList<String> objects) {
        super(context,  R.layout.spinner, objects);
        this.context1 = context;
        this.data = objects;
        inflater = (LayoutInflater) context1
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.spinner, parent, false);

        TextView tvCategory = (TextView) row.findViewById(R.id.sptext);

        tvCategory.setText(data.get(position).toString());

        return row;
    }

}
