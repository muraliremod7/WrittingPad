package com.indianservers.writingpad;

import android.content.SharedPreferences;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.indianservers.writingpad.adapters.ListviewArrayAdapter;
import com.indianservers.writingpad.services.AlertDialogManager;
import com.indianservers.writingpad.services.ConnectionDetector;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.widget.AdapterView;

import static android.view.View.GONE;

public class QuestionViewFragment extends Fragment implements View.OnClickListener {
    private String Id;
    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"/SreedharCCE/");
    String JsonURL;
    // This string will hold the results
    String data = "";
    private static String questionDirection,questionn,choice1,choice2,choice3,choice4,choice5;
    private String s;
    // Defining the Volley request queue that handles the URL request concurrently
    RequestQueue requestQueue;
    private AlertDialogManager dialogManager;
    ImageButton clear;
    GestureOverlayView gestures;
    TextView questionNumber,itemNumber, direction, question, opt1, opt2, opt3, opt4, opt5;
    ImageView ch1Image,ch2Image,ch3Image,ch4Image,ch5Image,questionImage,directionImage;
    public List<QuestionsClass> mQuestionSet = new ArrayList<>();
    public List<QuestionsClass> mQuestions = new ArrayList<>();
    List<QuestionsClass> mQuestionsCopy = new ArrayList<>();
    List<QuestionsClass> mQuestionsCopy1 = new ArrayList<>();
    Bitmap bitmap,mutableBitmap;
    QuestionsClass questionsClassObj;
    ImageButton left, right;
    public static int CurrentQuestionId = 0;
    Boolean set = true;
    ConnectionDetector cd;
    it.sephiroth.android.library.widget.HListView listView;
    ListviewArrayAdapter adapter;
    String myString;
    WebView webView;
    int start,end;
    ExplanationTouchPad touchPad;
    final Handler handler = new Handler();
    public static Fragment newInstance() {
        Fragment fragment = new QuestionViewFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.question_touchpad, container, false);
        clear = (ImageButton) itemView.findViewById(R.id.clear);
        Typeface tfArial = Typeface.createFromAsset(getContext().getAssets(),"arial.ttf");
        questionNumber = (TextView) itemView.findViewById(R.id.question_number);
        direction = (TextView) itemView.findViewById(R.id.question_direction);
        direction.setTypeface(tfArial);
        question = (TextView) itemView.findViewById(R.id.question);
        question.setTypeface(tfArial);
        opt1 = (TextView) itemView.findViewById(R.id.opt1);
        opt1.setTypeface(tfArial);
        opt2 = (TextView) itemView.findViewById(R.id.opt2);
        opt2.setTypeface(tfArial);
        opt3 = (TextView) itemView.findViewById(R.id.opt3);
        opt3.setTypeface(tfArial);
        opt4 = (TextView) itemView.findViewById(R.id.opt4);
        opt4.setTypeface(tfArial);
        opt5 = (TextView) itemView.findViewById(R.id.opt5);
        opt5.setTypeface(tfArial);
        webView = (WebView)itemView.findViewById(R.id.web);
        directionImage = (ImageView)itemView.findViewById(R.id.image_direction);
        questionImage = (ImageView)itemView.findViewById(R.id.image_question);
        ch1Image = (ImageView)itemView.findViewById(R.id.choi1_image);
        ch2Image = (ImageView)itemView.findViewById(R.id.choi2_image);
        ch3Image = (ImageView)itemView.findViewById(R.id.choi3_image);
        ch4Image = (ImageView)itemView.findViewById(R.id.choi4_image);
        ch5Image = (ImageView)itemView.findViewById(R.id.choi5_image);
        itemNumber = (TextView)getActivity().findViewById(R.id.questionNumber);
        cd = new ConnectionDetector(getActivity());
        questionsClassObj = new QuestionsClass();
        left = (ImageButton) getActivity().findViewById(R.id.left);
        right = (ImageButton) getActivity().findViewById(R.id.right);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        dialogManager = new AlertDialogManager();
            SharedPreferences teamID = PreferenceManager.getDefaultSharedPreferences(getContext());
            Id = teamID.getString("input","0");
                JsonURL = file+"/"+Id+"/"+Id+".json";
                gettingQuestionfromOffline(JsonURL);

        gestures = (GestureOverlayView) itemView.findViewById(R.id.gestures);
        Log.v("q", "question----" + mQuestions.size());
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestures.cancelClearAnimation();
                gestures.clear(true);
            }
        });
         adapter = new ListviewArrayAdapter(getActivity(), mQuestionSet);

        Runnable refresh = new Runnable() {
            @Override
            public void run() {
                gettingQuestionfromOffline(JsonURL);
                handler.postDelayed(this, 15 * 1000);
            }
        };
            handler.postDelayed(refresh, 15 * 1000);
        handler.removeCallbacksAndMessages(null);
        return itemView;
    }
    private void gettingQuestionfromOffline(String jsonURL) {
        Ion.with(getContext()).load(jsonURL)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e!=null){

                        }else{
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //gets each JSON object within the JSON array
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int questionNumber = jsonObject.getInt("qno");
                                    int answer = jsonObject.getInt("answer");
                                    questionDirection = jsonObject.getString("direction");
                                    questionn = jsonObject.getString("question");
                                    choice1 = jsonObject.getString("choice1");
                                    choice2 = jsonObject.getString("choice2");
                                    choice3 = jsonObject.getString("choice3");
                                    choice4 = jsonObject.getString("choice4");
                                    choice5 = jsonObject.getString("choice5");
                                        questionsClassObj = new QuestionsClass(questionNumber, answer, questionDirection, questionn, choice1, choice2, choice3, choice4, choice5);
                                        mQuestionSet.add(questionsClassObj);
                                        mQuestionsCopy = mQuestionSet;
                                }
                                // RecycleViewAdapter adapter1 = new RecycleViewAdapter(getActivity(),result);
                                listView = (it.sephiroth.android.library.widget.HListView) getActivity().findViewById(R.id.qno);
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        CurrentQuestionId = position;
                                        int Position = position+1;
                                        touchPad = new ExplanationTouchPad();
                                        touchPad.getFileNames(Position);
                                        loadQuestion(position,view);
                                    }
                                });
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.left:
                if (CurrentQuestionId > 0) {
                    loadPreviousQuestion(CurrentQuestionId - 1,v);
                    Log.v("sai", "Current Back QuestionID----" + CurrentQuestionId);
                    CurrentQuestionId--;
                    adapter.notifyDataSetChanged();
                   // adapter.getItem(CurrentQuestionId)
                } else {
                    Toast.makeText(getActivity(), "No Questions", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.right:
                if (CurrentQuestionId <= mQuestionsCopy.size() - 2) {

                    loadNextQuestion(CurrentQuestionId + 1,v);
                    Log.v("sai", "Current Next Question ID------" + CurrentQuestionId);
                    CurrentQuestionId++;
                } else {
                    Toast.makeText(getActivity(), "No Questions", Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }

    public void loadQuestion(int i,View v) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(1000, 1000);
        LinearLayout.LayoutParams questionparam = new LinearLayout.LayoutParams(200, 200);

        if (i >= 0 || i <= mQuestionsCopy.size()) {
            QuestionsClass quetionClass = mQuestionsCopy.get(i);
            Log.v("sai", "current question number-----" + CurrentQuestionId);
            Log.v("sai", "Total Questions-----" + mQuestionsCopy.size());
            direction.setText(Html.fromHtml("\n" + quetionClass.getmQuestionDirection()));
            question.setText(quetionClass.getmQno() + "  " + Html.fromHtml(quetionClass.getmQuestion()));
            if(quetionClass.getmQuestionDirection().endsWith(".jpg")){
                String mCurrentPhotoPath = file.getAbsolutePath()+"/"+Id+"/"+quetionClass.getmQuestionDirection();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                direction.setVisibility(GONE);
                webView.setVisibility(GONE);
                directionImage.setVisibility(View.VISIBLE);
                directionImage.setImageBitmap(bitmap);
                directionImage.setLayoutParams(layoutParams);
            }else {
                directionImage.setVisibility(GONE);
                direction.setVisibility(View.VISIBLE);
                start = quetionClass.getmQuestionDirection().indexOf("<table");
                if( start < 0 )
                    Log.d(this.toString(), "Table start tag not found");
                else {
                    end = quetionClass.getmQuestionDirection().indexOf("</table>", start) + 8;
                    if( end < 0 )
                        Log.d(this.toString(), "Table end tag not found");
                    else{
                        myString = quetionClass.getmQuestionDirection().substring(start, end) ;
                    }
                }
                if(myString==null||quetionClass.getmQuestionDirection().endsWith(".jpg")){
                    webView.setVisibility(GONE);
                    direction.setText(Html.fromHtml(quetionClass.getmQuestionDirection()));
                }
                else if(quetionClass.getmQuestionDirection().contains(myString)){
                    webView.setVisibility(View.VISIBLE);
                    webView.loadData("<html><body>" + myString+ "</body></html>","text/html", "utf-8");
                    String directionn = quetionClass.getmQuestionDirection().substring(0,start);
                    direction.setText( Html.fromHtml(directionn));
                }else{
                    webView.setVisibility(GONE);
                }
            }
            if(quetionClass.getmQuestion().endsWith(".jpg")){
                String mCurrentPhotoPath = file.getAbsolutePath()+"/"+Id+"/"+quetionClass.getmQuestion();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                question.setVisibility(GONE);
                questionImage.setVisibility(View.VISIBLE);
                questionImage.setImageBitmap(bitmap);
                questionImage.setLayoutParams(questionparam);
            }else {
                question.setVisibility(View.VISIBLE);
                questionImage.setVisibility(GONE);
                question.setText(quetionClass.getmQno() + "  " + Html.fromHtml(quetionClass.getmQuestion()));
            }

            if(quetionClass.getChoice1().endsWith(".jpg")){
                String mCurrentPhotoPath = file.getAbsolutePath()+"/"+Id+"/"+quetionClass.getChoice1();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                opt1.setVisibility(View.VISIBLE);
                opt1.setText("1)");
                ch1Image.setVisibility(View.VISIBLE);
                ch1Image.setImageBitmap(bitmap);
            }else {
                opt1.setVisibility(View.VISIBLE);
                ch1Image.setVisibility(GONE);
                opt1.setText("\n"+"1)  " + Html.fromHtml(quetionClass.getChoice1()));
            }
            if(quetionClass.getChoice2().endsWith(".jpg")){
                String mCurrentPhotoPath = file.getAbsolutePath()+"/"+Id+"/"+quetionClass.getChoice2();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                opt2.setVisibility(View.VISIBLE);
                opt2.setText("2)");
                ch2Image.setVisibility(View.VISIBLE);
                ch2Image.setImageBitmap(bitmap);
            }else {
                opt2.setVisibility(View.VISIBLE);
                ch2Image.setVisibility(GONE);
                opt2.setText("\n"+"2)  "+ Html.fromHtml(quetionClass.getChoice2()));
            }
            if(quetionClass.getChoice3().endsWith(".jpg")){
                String mCurrentPhotoPath = file.getAbsolutePath()+"/"+Id+"/"+quetionClass.getChoice3();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                opt3.setVisibility(View.VISIBLE);
                opt3.setText("3)");
                ch3Image.setVisibility(View.VISIBLE);
                ch3Image.setImageBitmap(bitmap);
            }else {
                opt3.setVisibility(View.VISIBLE);
                ch3Image.setVisibility(GONE);
                opt3.setText("\n"+"3)  "+ Html.fromHtml(quetionClass.getChoice3()));
            }
            if(quetionClass.getChoice4().endsWith(".jpg")){
                String mCurrentPhotoPath = file.getAbsolutePath()+"/"+Id+"/"+quetionClass.getChoice4();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                opt4.setVisibility(View.VISIBLE);
                opt4.setText("4)");
                ch4Image.setVisibility(View.VISIBLE);
                ch4Image.setImageBitmap(bitmap);
            }else {
                opt4.setVisibility(View.VISIBLE);
                ch4Image.setVisibility(GONE);
                opt4.setText("\n"+"4)   "+ Html.fromHtml(quetionClass.getChoice4()));
            }
            if(quetionClass.getChoice5().endsWith(".jpg")){
                String mCurrentPhotoPath = file.getAbsolutePath()+"/"+Id+"/"+quetionClass.getChoice5();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                opt5.setVisibility(View.VISIBLE);
                opt5.setText("5)");
                ch5Image.setVisibility(View.VISIBLE);
                ch5Image.setImageBitmap(bitmap);
            }else {
                opt5.setVisibility(View.VISIBLE);
                ch5Image.setVisibility(GONE);
                opt5.setText("\n"+"5)  "+ Html.fromHtml(quetionClass.getChoice5()));
            }
        }
    }

    public void loadNextQuestion(int i,View v) {
        loadQuestion(i,v);
    }

    public void loadPreviousQuestion(int i,View v) {
        loadQuestion(i,v);
    }
}
