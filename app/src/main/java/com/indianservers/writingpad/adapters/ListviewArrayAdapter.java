package com.indianservers.writingpad.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.indianservers.writingpad.QuestionViewFragment;
import com.indianservers.writingpad.QuestionsClass;
import com.indianservers.writingpad.R;

import java.util.ArrayList;
import java.util.List;

public class ListviewArrayAdapter extends BaseAdapter {
    private int PositionSelected = 0;
    Context context;
    List<QuestionsClass> mQuestionsSet = new ArrayList<>();

    public ListviewArrayAdapter(Context context, List<QuestionsClass> mQuestionsSet) {
        this.context = context;
        this.mQuestionsSet = mQuestionsSet;
    }

    @Override
    public int getCount() {
        return mQuestionsSet.size();
    }

    @Override
    public Object getItem(int position) {
        return QuestionViewFragment.CurrentQuestionId;
    }

    @Override
    public long getItemId(int position) {
        return QuestionViewFragment.CurrentQuestionId;
    }
    public void setPositionSelected(int position)
    {
        PositionSelected = position;
        this.notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.list_item_view, null);
        TextView textView = (TextView) rowView.findViewById(R.id.questionNumber);
            textView.setText(String.valueOf(mQuestionsSet.get(position).getmQno()));
            if(position==PositionSelected){
                rowView.setBackgroundColor(Color.GREEN);
                notifyDataSetChanged();
            }else{
                rowView.setBackgroundColor(Color.WHITE);
                notifyDataSetChanged();
            }
        return rowView;
    }
}
