package com.indianservers.writingpad.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indianservers.writingpad.QuestionsClass;
import com.indianservers.writingpad.R;

import java.util.List;

/**
 * Created by hp on 02-Jul-17.
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.mViewHolder> {

    Context context;
    private List<QuestionsClass> questionsClassList;

    public class mViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public mViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.questionNumber);
            //textView.setText(String.valueOf(questionsClassList.get(position).getmQno()));

        }
    }
    public RecycleViewAdapter(Context context,List<QuestionsClass> questionsClassList1) {
        this.context = context;
        this.questionsClassList = questionsClassList1;
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_view, parent, false);

        return new mViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {

        QuestionsClass questionsClass = questionsClassList.get(position);
        holder.textView.setText(String.valueOf(questionsClass.getmQno()));

    }

    @Override
    public int getItemCount() {
        return questionsClassList.size();
    }
}
