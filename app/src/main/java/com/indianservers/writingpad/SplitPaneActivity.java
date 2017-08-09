package com.indianservers.writingpad;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SplitPaneActivity extends AppCompatActivity  {
    Toolbar toolbar;
    private static final String EXTRA_CURRENT_INDEX = "MySplitPaneActivity.EXTRA_CURRENT_INDEX";
    private static final String EXTRA_PERCENT_LEFT = "MySplitPaneActivity.EXTRA_PERCENT_LEFT";
    private static final String EXTRA_MINIMUM_WIDTH_DIP = "MySplitPaneActivity.EXTRA_MINIMUM_WIDTH_DIP";
    private float mTotalWidth; // pixels
    private float mPercentLeft; // percent of screen
    private FrameLayout mRightPane;
    private FrameLayout mLeftPane;
    private float mMinimumWidth; // percent of screen
    private ImageButton mLeft, mRight;
    int pos;
    private QuestionViewFragment viewFragment;
    FragmentManager fragmentManager;
    Fragment fragment;
    List<QuestionsClass> mQuestionSet = new ArrayList<>();
    FrameLayout fl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_pane);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       getSupportActionBar().setDisplayShowTitleEnabled(false);
        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.activity_split_pane_left_pane);

        if (fragment == null) {
            ExplanationTouchPad fragment = ExplanationTouchPad.newInstance();
            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .add(R.id.activity_split_pane_left_pane, fragment)
                        .commit();
            }
        }
        mPercentLeft = getIntent().getFloatExtra(EXTRA_PERCENT_LEFT, 50);
        int minimumWidthDip = getIntent().getIntExtra(EXTRA_MINIMUM_WIDTH_DIP, 100);

        // get screen size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mTotalWidth = size.x;

        // convert minimum width from dip to percent
        mMinimumWidth = convertDipToPercent(minimumWidthDip);

        // set touch listener on divider
        ImageView divider = (ImageView) findViewById(R.id.activity_split_pane_divider);
        divider.setOnTouchListener(new DividerTouchListener());

        // get left and right pane and set weights
        mLeftPane = (FrameLayout) findViewById(R.id.activity_split_pane_left_pane);
        mRightPane = (FrameLayout) findViewById(R.id.activity_split_pane_right_pane);
        mLeft = (ImageButton) findViewById(R.id.left);


        setWeights(mPercentLeft);

        // display right pane
        int currentIndex = getIntent().getIntExtra(EXTRA_CURRENT_INDEX, 0);
        setupDetailPane(currentIndex);
    }


    private void setWeights(float percentLeft) {
        Log.d("TAG", "minimum width = " + mMinimumWidth);

        float percentRight = 100 - percentLeft;

        // if left side too small, resize
        if (percentLeft < mMinimumWidth) {
            percentLeft = mMinimumWidth;
            percentRight = 100 - percentLeft;
        }

        // if right side too small, resize
        if (percentRight < mMinimumWidth) {
            percentRight = mMinimumWidth;
            percentLeft = 100 - percentRight;
        }

        // set weights
        mLeftPane.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, percentLeft));
        mRightPane.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, percentRight));
    }

    private void rebuildView(float draggedToX) {
        // reset weights
        mPercentLeft = computeNewPercentLeft(draggedToX);
        setWeights(mPercentLeft);

        // save to extras
        getIntent().putExtra(EXTRA_PERCENT_LEFT, mPercentLeft);

        // force layout pass
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.activity_split_pane);
        viewGroup.requestLayout();
    }

    private void setupDetailPane(int index) {
        // get fragment manager and create new fragment transaction

        fragmentManager = getSupportFragmentManager();
        if (fragment == null) {
            QuestionViewFragment fragment = new QuestionViewFragment().newInstance();
            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .add(R.id.activity_split_pane_right_pane, fragment)
                        .commit();
            }
        }

    }

    private float computeNewPercentLeft(float draggedToX) {
        return (100 - (100 * (mTotalWidth - draggedToX) / mTotalWidth));
    }

    private float convertDipToPercent(int dip) {
        return (dip / mTotalWidth) * 100;
    }

    public void onItemSelected(int index) {
        setupDetailPane(index);
        // update Extra
        getIntent().putExtra(EXTRA_CURRENT_INDEX, index);
    }

    private class DividerTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_MOVE:
                    rebuildView(event.getRawX());
                    break;

                case MotionEvent.ACTION_UP:
                    rebuildView(event.getRawX());
                    break;

                case MotionEvent.ACTION_CANCEL:
                    break;
            }
            return true;
        }
    }
}

