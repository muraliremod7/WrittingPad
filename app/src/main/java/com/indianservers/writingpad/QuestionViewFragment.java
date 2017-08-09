package com.indianservers.writingpad;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.indianservers.writingpad.adapters.ListviewArrayAdapter;
import com.indianservers.writingpad.adapters.SpinnerAdapter;
import com.indianservers.writingpad.component.DrawingVieww;
import com.indianservers.writingpad.model.SpinnerModel;
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
    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "/SreedharCCE/");
    String JsonURL;
    private int mCurrentStroke = 18;
    // This string will hold the results
    String data = "";
    private static String questionDirection, questionn, choice1, choice2, choice3, choice4, choice5;
    private String s;
    // Defining the Volley request queue that handles the URL request concurrently
    RequestQueue requestQueue;
    private AlertDialogManager dialogManager;
    ImageButton clear, textStroke, gesturepen,gesturestop;
    GestureOverlayView gestures;
    TextView questionNumber, itemNumber, direction, question, opt1, opt2, opt3, opt4, opt5;
    ImageView ch1Image, ch2Image, ch3Image, ch4Image, ch5Image, questionImage, directionImage;
    public List<QuestionsClass> mQuestionSet = new ArrayList<>();
    public List<QuestionsClass> mQuestions = new ArrayList<>();
    List<QuestionsClass> mQuestionsCopy = new ArrayList<>();
    Bitmap bitmap;
    QuestionsClass questionsClassObj;
    ImageButton left, right;
    public static int CurrentQuestionId = 0;
    Boolean set = true;
    ConnectionDetector cd;
    it.sephiroth.android.library.widget.HListView listView;
    ListviewArrayAdapter adapter;
    String myString;
    WebView webView;
    int start, end;
    SharedPreferences teamID;
    private ArrayList<SpinnerModel> imagesList = new ArrayList<SpinnerModel>();
    ArrayAdapter<String> arrayAdapter;
    private SpinnerAdapter spinAdapter;
    public Spinner spinner;
    File sdcard = Environment.getExternalStorageDirectory();
    final Handler handler = new Handler();
    private SharedPreferences.Editor editor;
    private ScrollView scrollView;
    public static QuestionViewFragment newInstance() {
        QuestionViewFragment fragment = new QuestionViewFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.question_touchpad, container, false);
        clear = (ImageButton) itemView.findViewById(R.id.clear);
        textStroke = (ImageButton) itemView.findViewById(R.id.textsize);
        textStroke.setOnClickListener(this);
        gesturepen = (ImageButton) itemView.findViewById(R.id.pengesture);
        gesturepen.setOnClickListener(this);
        gesturestop = (ImageButton) itemView.findViewById(R.id.pengesturecancel);
        gesturestop.setOnClickListener(this);
        Typeface tfArial = Typeface.createFromAsset(getContext().getAssets(), "arial.ttf");
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
        webView = (WebView) itemView.findViewById(R.id.web);
        directionImage = (ImageView) itemView.findViewById(R.id.image_direction);
        questionImage = (ImageView) itemView.findViewById(R.id.image_question);
        ch1Image = (ImageView) itemView.findViewById(R.id.choi1_image);
        ch2Image = (ImageView) itemView.findViewById(R.id.choi2_image);
        ch3Image = (ImageView) itemView.findViewById(R.id.choi3_image);
        ch4Image = (ImageView) itemView.findViewById(R.id.choi4_image);
        ch5Image = (ImageView) itemView.findViewById(R.id.choi5_image);
        itemNumber = (TextView) getActivity().findViewById(R.id.questionNumber);
        cd = new ConnectionDetector(getActivity());
        questionsClassObj = new QuestionsClass();
        left = (ImageButton) getActivity().findViewById(R.id.left);
        right = (ImageButton) getActivity().findViewById(R.id.right);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        dialogManager = new AlertDialogManager();
        teamID = PreferenceManager.getDefaultSharedPreferences(getContext());
        Id = teamID.getString("input", "0");
        JsonURL = file + "/" + Id + "/" + Id + ".json";
        gettingQuestionfromOffline(JsonURL);
        gestures = (GestureOverlayView) itemView.findViewById(R.id.gestures);
        gestures.setGestureVisible(false);
        scrollView = (ScrollView)itemView.findViewById(R.id.scroll);
        Log.v("q", "question----" + mQuestions.size());
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestures.cancelClearAnimation();
                gestures.clear(true);
            }
        });
        Runnable refresh = new Runnable() {
            @Override
            public void run() {
                gettingQuestionfromOffline(JsonURL);
                handler.postDelayed(this, 5 * 1000);
            }
        };
        handler.postDelayed(refresh, 5 * 1000);
        handler.removeCallbacksAndMessages(null);
        spinner = (Spinner) getActivity().findViewById(R.id.spinner_nav);
        if(spinner==null){

        }else{
            spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    String currentImage = ((TextView) view.findViewById(R.id.sptext)).getText().toString();
                    spinAdapter.notifyDataSetChanged();
                    int pos = CurrentQuestionId + 1;
                    if(currentImage.equals("Choose One Image")){
                        String value = teamID.getString("pathname", "1");
                        //currentImage = value;
                        String pathName = new File(sdcard.getAbsolutePath() + "/SreedharCCE/" + Id + "/" + pos + "/" + value).getAbsolutePath();
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                        editor = settings.edit();
                        editor.putString("pathname", pathName);
                        editor.commit();
                        callfragment();
                    }else{
                        String pathName = new File(sdcard.getAbsolutePath() + "/SreedharCCE/" + Id + "/" + pos + "/" + currentImage).getAbsolutePath();
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                        editor = settings.edit();
                        editor.putString("pathname", pathName);
                        editor.commit();
                        callfragment();
                    }
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {

                }
            });
        }


        return itemView;
    }
    public void gettingQuestionfromOffline(String jsonURL) {
        Ion.with(getContext()).load(jsonURL)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {

                        } else {
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
                                adapter = new ListviewArrayAdapter(getActivity(), mQuestionSet);
                                listView = (it.sephiroth.android.library.widget.HListView) getActivity().findViewById(R.id.qno);
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        CurrentQuestionId = position;
                                        int Position = position + 1;
                                        getFileNames(Position, Id);
                                        loadQuestion(position, view);
                                        int op = CurrentQuestionId;
                                        adapter.setPositionSelected(op);
                                    }
                                });
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                });
    }

    public void getFileNames(int position, String id) {
        //String Idd = teamID.getString("input","0");
        imagesList.clear();
        File yourDir = new File(sdcard, "/SreedharCCE/" + id + "/" + position + "/");
        if (yourDir.isDirectory()) {
            if (yourDir == null || yourDir.equals(null)) {
                SpinnerModel spinnerMode = new SpinnerModel();
                spinnerMode.setImageName("");
                spinnerMode.setImage("");
                imagesList.add(spinnerMode);
                spinAdapter = new SpinnerAdapter(getActivity().getApplicationContext(), imagesList);
                try{
                    spinner.setAdapter(spinAdapter);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            } else {
                    SpinnerModel spinnerMode = new SpinnerModel();
                    spinnerMode.setImageName("");
                    spinnerMode.setImage("");
                    imagesList.add(spinnerMode);
                for (File f : yourDir.listFiles()) {
                    SpinnerModel spinnerModel = new SpinnerModel();

                    if (f.isFile()) {
                        String name = f.getName();
                        spinnerModel.setImageName(f.getName());
                        spinnerModel.setImage(yourDir.getAbsolutePath() + "/" + name);
                        imagesList.add(spinnerModel);
                        spinAdapter = new SpinnerAdapter(getActivity().getApplicationContext(), imagesList);
                        try{
                            spinner.setAdapter(spinAdapter);
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    } else {

                    }
                }
                if (imagesList.size() == 0) {
                    SpinnerModel spinnerModee = new SpinnerModel();
                    spinnerModee.setImageName("");
                    spinnerModee.setImage("");
                    imagesList.add(spinnerModee);
                    spinAdapter = new SpinnerAdapter(getActivity().getApplicationContext(), imagesList);
                    try{
                        spinner.setAdapter(spinAdapter);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                } else {
                    spinAdapter = new SpinnerAdapter(getActivity().getApplicationContext(), imagesList);
                    spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    try{
                        spinner.setAdapter(spinAdapter);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            }
        } else {
            SpinnerModel spinnerMode = new SpinnerModel();
            spinnerMode.setImageName("");
            spinnerMode.setImage("");
            imagesList.add(spinnerMode);
            spinAdapter = new SpinnerAdapter(getActivity().getApplicationContext(), imagesList);
            try{
                spinner.setAdapter(spinAdapter);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }
    public void callfragment() {
        ExplanationTouchPad fragment2 = new ExplanationTouchPad();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_split_pane_left_pane, fragment2);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.left:
                if (CurrentQuestionId > 0) {
                    loadPreviousQuestion(CurrentQuestionId - 1, v);
                    Log.v("sai", "Current Back QuestionID----" + CurrentQuestionId);
                    CurrentQuestionId--;
                    int op = CurrentQuestionId;
                    adapter.setPositionSelected(op);
                } else {
                    Toast.makeText(getActivity(), "No Questions", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.right:
                if (CurrentQuestionId <= mQuestionsCopy.size() - 2) {
                    FragmentManager fm = getFragmentManager();
                    ExplanationTouchPad fragm = (ExplanationTouchPad) fm.findFragmentById(R.id.activity_split_pane_left_pane);
                    fragm.saveImage();
                    loadNextQuestion(CurrentQuestionId + 1, v);
                    Log.v("sai", "Current Next Question ID------" + CurrentQuestionId);
                    CurrentQuestionId++;
                    int op = CurrentQuestionId;
                    adapter.setPositionSelected(op);
                    int oq = CurrentQuestionId+1;
                    getFileNames(oq,Id);
                } else {
                    Toast.makeText(getActivity(), "No Questions", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.textsize:
                startStrokeSelectorDialog();
                break;
            case R.id.pengesture:
                gestures.setGestureVisible(true);
                gestures.cancelClearAnimation();
                gestures.clear(true);
                scrollView.setOnTouchListener(null);
                scrollView.setOnTouchListener( new View.OnTouchListener(){
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                break;
            case R.id.pengesturecancel:
                gestures.cancelClearAnimation();
                gestures.clear(true);
                gestures.setGestureVisible(false);
                scrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                break;
        }

    }

    private void startStrokeSelectorDialog() {
        StrokeSelectorDialog dialog = StrokeSelectorDialog.newInstance(mCurrentStroke, 60);

        dialog.setOnStrokeSelectedListener(new StrokeSelectorDialog.OnStrokeSelectedListener() {
            @Override
            public void onStrokeSelected(int stroke) {
                mCurrentStroke = stroke;
                direction.setTextSize(mCurrentStroke);
                question.setTextSize(mCurrentStroke);
                opt1.setTextSize(mCurrentStroke);
                opt2.setTextSize(mCurrentStroke);
                opt3.setTextSize(mCurrentStroke);
                opt4.setTextSize(mCurrentStroke);
                opt5.setTextSize(mCurrentStroke);

            }
        });

        dialog.show(getActivity().getSupportFragmentManager(), "StrokeSelectorDialog");
    }

    public void loadQuestion(int i, View v) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(1000, 1000);
        LinearLayout.LayoutParams questionparam = new LinearLayout.LayoutParams(500, 500);
        if (i >= 0 || i <= mQuestionsCopy.size()) {

            QuestionsClass quetionClass = mQuestionsCopy.get(i);
            Log.v("sai", "current question number-----" + CurrentQuestionId);
            Log.v("sai", "Total Questions-----" + mQuestionsCopy.size());
            direction.setText(Html.fromHtml("\n" + quetionClass.getmQuestionDirection()));
            question.setText(Html.fromHtml(quetionClass.getmQuestion()));
            questionNumber.setText("Question"+quetionClass.getmQno());
            if (quetionClass.getmQuestionDirection().endsWith(".jpg")) {
                String mCurrentPhotoPath = file.getAbsolutePath() + "/" + Id + "/" + quetionClass.getmQuestionDirection();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                direction.setVisibility(GONE);
                webView.setVisibility(GONE);
                directionImage.setVisibility(View.VISIBLE);
                directionImage.setImageBitmap(bitmap);
                directionImage.setLayoutParams(layoutParams);
            } else {
                directionImage.setVisibility(GONE);
                direction.setVisibility(View.VISIBLE);
                start = quetionClass.getmQuestionDirection().indexOf("<table");
                if (start < 0)
                    Log.d(this.toString(), "Table start tag not found");
                else {
                    end = quetionClass.getmQuestionDirection().indexOf("</table>", start) + 8;
                    if (end < 0)
                        Log.d(this.toString(), "Table end tag not found");
                    else {
                        myString = quetionClass.getmQuestionDirection().substring(start, end);
                    }
                }
                if (myString == null || quetionClass.getmQuestionDirection().endsWith(".jpg")) {
                    webView.setVisibility(GONE);
                    direction.setText(Html.fromHtml(quetionClass.getmQuestionDirection()));
                } else if (quetionClass.getmQuestionDirection().contains(myString)) {
                    webView.setVisibility(View.VISIBLE);
                    webView.loadData("<html><body>" + myString + "</body></html>", "text/html", "utf-8");
                    String directionn = quetionClass.getmQuestionDirection().substring(0, start);
                    direction.setText(Html.fromHtml(directionn));
                } else {
                    webView.setVisibility(GONE);
                }
            }
            if (quetionClass.getmQuestion().endsWith(".jpg")) {
                String mCurrentPhotoPath = file.getAbsolutePath() + "/" + Id + "/" + quetionClass.getmQuestion();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                question.setVisibility(GONE);
                questionImage.setVisibility(View.VISIBLE);
                questionImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 1500, 50, false));
            } else {
                question.setVisibility(View.VISIBLE);
                questionImage.setVisibility(GONE);
                question.setText(quetionClass.getmQno() + "  " + Html.fromHtml(quetionClass.getmQuestion()));
            }

            if (quetionClass.getChoice1().endsWith(".jpg")) {
                String mCurrentPhotoPath = file.getAbsolutePath() + "/" + Id + "/" + quetionClass.getChoice1();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                opt1.setVisibility(View.VISIBLE);
                opt1.setText("1)");
                ch1Image.setVisibility(View.VISIBLE);
                ch1Image.setImageBitmap(bitmap);
            } else {
                opt1.setVisibility(View.VISIBLE);
                ch1Image.setVisibility(GONE);
                if (quetionClass.getmAnswer() == 1) {
                    opt1.setText("\n" + "1)  " + Html.fromHtml(quetionClass.getChoice1()));

                } else {
                    opt1.setTypeface(null, Typeface.NORMAL);
                    opt1.setText("\n" + "1)  " + Html.fromHtml(quetionClass.getChoice1()));
                }
            }
            if (quetionClass.getChoice2().endsWith(".jpg")) {
                String mCurrentPhotoPath = file.getAbsolutePath() + "/" + Id + "/" + quetionClass.getChoice2();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                opt2.setVisibility(View.VISIBLE);
                opt2.setText("2)");
                ch2Image.setVisibility(View.VISIBLE);
                ch2Image.setImageBitmap(bitmap);
            } else {
                opt2.setVisibility(View.VISIBLE);
                ch2Image.setVisibility(GONE);
                if (quetionClass.getmAnswer() == 2) {
                    opt2.setText("\n" + "2)  " + Html.fromHtml(quetionClass.getChoice2()));
                } else {
                    opt2.setTypeface(null, Typeface.NORMAL);
                    opt2.setText("\n" + "2)  " + Html.fromHtml(quetionClass.getChoice2()));
                }
            }
            if (quetionClass.getChoice3().endsWith(".jpg")) {
                String mCurrentPhotoPath = file.getAbsolutePath() + "/" + Id + "/" + quetionClass.getChoice3();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                opt3.setVisibility(View.VISIBLE);
                opt3.setText("3)");
                ch3Image.setVisibility(View.VISIBLE);
                ch3Image.setImageBitmap(bitmap);
            } else {
                opt3.setVisibility(View.VISIBLE);
                ch3Image.setVisibility(GONE);

                if (quetionClass.getmAnswer() == 3) {
                    opt3.setText("\n" + "3)  " + Html.fromHtml(quetionClass.getChoice3()));
                } else {
                    opt3.setTypeface(null, Typeface.NORMAL);
                    opt3.setText("\n" + "3)  " + Html.fromHtml(quetionClass.getChoice3()));
                }
            }
            if (quetionClass.getChoice4().endsWith(".jpg")) {
                String mCurrentPhotoPath = file.getAbsolutePath() + "/" + Id + "/" + quetionClass.getChoice4();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                opt4.setVisibility(View.VISIBLE);
                opt4.setText("4)");
                ch4Image.setVisibility(View.VISIBLE);
                ch4Image.setImageBitmap(bitmap);
            } else {
                opt4.setVisibility(View.VISIBLE);
                ch4Image.setVisibility(GONE);
                if (quetionClass.getmAnswer() == 4) {
                    opt4.setText("\n" + "4)   " + Html.fromHtml(quetionClass.getChoice4()));

                } else {
                    opt4.setTypeface(null, Typeface.NORMAL);
                    opt4.setText("\n" + "4)   " + Html.fromHtml(quetionClass.getChoice4()));
                }
            }
            if (quetionClass.getChoice5().endsWith(".jpg")) {
                String mCurrentPhotoPath = file.getAbsolutePath() + "/" + Id + "/" + quetionClass.getChoice5();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                opt5.setVisibility(View.VISIBLE);
                opt5.setText("5)");
                ch5Image.setVisibility(View.VISIBLE);
                ch5Image.setImageBitmap(bitmap);
            } else {
                opt5.setVisibility(View.VISIBLE);
                ch5Image.setVisibility(GONE);
                if (quetionClass.getmAnswer() == 5) {
                    opt5.setText("\n" + "5)   " + Html.fromHtml(quetionClass.getChoice5()));
                } else {
                    opt5.setTypeface(null, Typeface.NORMAL);
                    opt5.setText("\n" + "5)   " + Html.fromHtml(quetionClass.getChoice5()));
                }
            }
        }
    }

    public void loadNextQuestion(int i, View v) {
        loadQuestion(i, v);
    }

    public void loadPreviousQuestion(int i, View v) {
        loadQuestion(i, v);
    }

}
