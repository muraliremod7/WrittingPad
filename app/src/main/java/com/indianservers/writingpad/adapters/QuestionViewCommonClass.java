package com.indianservers.writingpad.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.indianservers.writingpad.QuestionsClass;
import com.indianservers.writingpad.R;

import java.util.ArrayList;

/**
 * Created by JNTUH on 12-07-2017.
 */

public class QuestionViewCommonClass extends ArrayAdapter<QuestionsClass> {
    TextView qno,direction,question,choice1,choice2,choice3,choice4,choice5,answer;
    public final Activity activity;
    public ArrayList<QuestionsClass> questionsClasses;
    public QuestionViewCommonClass(Activity activity,ArrayList<QuestionsClass> questionslist){
       super(activity,0,questionslist);
        this.activity = activity;
        this.questionsClasses = questionslist;
    }
    @Override
    public int getCount() {
        return questionsClasses.size();
    }

    @Override
    public QuestionsClass getItem(int location) {
        return questionsClasses.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertview, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertview= inflater.inflate(R.layout.question_touchpad, null, true);
        qno = (TextView) convertview.findViewById(R.id.qno);
        question = (TextView)convertview.findViewById(R.id.question);
        //direction = (TextView)convertview.findViewById(R.id.question_direction);
        choice1 = (TextView)convertview.findViewById(R.id.opt1);
        choice2 = (TextView)convertview.findViewById(R.id.opt2);
        choice3 = (TextView)convertview.findViewById(R.id.opt3);
        choice4 = (TextView)convertview.findViewById(R.id.opt4);
        choice5 = (TextView)convertview.findViewById(R.id.opt5);
        QuestionsClass commonClass =(QuestionsClass) getItem(position);
        qno.setText(commonClass.getmQno());
        question.setText(commonClass.getmQuestion());
        direction.setText(commonClass.getmQuestionDirection());
        choice1.setText(commonClass.getChoice1());
        choice2.setText(commonClass.getChoice2());
        choice3.setText(commonClass.getChoice3());
        choice4.setText(commonClass.getChoice4());
        choice5.setText(commonClass.getChoice5());
        answer.setText(commonClass.getmAnswer());

        return convertview;
    }
}
