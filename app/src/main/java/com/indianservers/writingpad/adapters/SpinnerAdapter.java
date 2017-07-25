package com.indianservers.writingpad.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.indianservers.writingpad.R;
import com.indianservers.writingpad.model.SpinnerModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JNTUH on 19-07-2017.
 */

public class SpinnerAdapter extends ArrayAdapter<String> {
    private Context context1;
    private ArrayList data;
    public Resources res;
    public String string;
    private LayoutInflater inflater;
    SpinnerModel tempValues=null;
    public SpinnerAdapter( Context context, ArrayList objects) {
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
    public View getCustomView(final int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.spinner, parent, false);
        tempValues = null;
        tempValues = (SpinnerModel) data.get(position);
        TextView tvCategory = (TextView) row.findViewById(R.id.sptext);
        tvCategory.setText(tempValues.getImageName());
        ImageView imageView = (ImageView)row.findViewById(R.id.imagespinner);
        Bitmap bmp = BitmapFactory.decodeFile(tempValues.getImage());
        imageView.setImageBitmap(bmp);
        ImageView delete = (ImageView)row.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file= new File(tempValues.getImage());
                if(file.exists())
                {
                    file.delete();
                    data.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(getContext(),"File Deleted",Toast.LENGTH_LONG).show();
                }
            }
        });
        return row;
    }

}
