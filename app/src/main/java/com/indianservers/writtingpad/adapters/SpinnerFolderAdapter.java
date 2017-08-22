package com.indianservers.writtingpad.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.indianservers.writtingpad.QuestionViewFragment;
import com.indianservers.writtingpad.R;
import com.indianservers.writtingpad.model.SpinnerFolderModel;
import com.indianservers.writtingpad.model.SpinnerModel;

import java.util.ArrayList;

/**
 * Created by JNTUH on 22-08-2017.
 */

public class SpinnerFolderAdapter extends ArrayAdapter<String> {
    private Context context1;
    private ArrayList data;
    public Resources res;
    public String string;
    private LayoutInflater inflater;
    SpinnerFolderModel tempValues = null;
    TextView tvCategory;
    public SpinnerFolderAdapter(Context context,  ArrayList objects) {
        super(context,  R.layout.spinner_folders, objects);
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
    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_folders, parent, false);
        tvCategory = (TextView) row.findViewById(R.id.spfoldersName);
        if(position==0){
            if(data.size()>1){
                tvCategory.setText("Select");
            }else if(data.size()==1){
                tvCategory.setText("No Folders");
            }
        }else{
            tempValues = (SpinnerFolderModel) data.get(position);
            tvCategory.setText(tempValues.getFolderName());
        }
        return row;
    }

}
