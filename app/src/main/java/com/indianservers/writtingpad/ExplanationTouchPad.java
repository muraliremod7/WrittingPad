package com.indianservers.writtingpad;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.indianservers.writtingpad.component.DrawingVieww;
import com.indianservers.writtingpad.services.AlertDialogManager;
import com.indianservers.writtingpad.services.ConnectionDetector;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.os.Handler;

import butterknife.ButterKnife;

public class ExplanationTouchPad extends Fragment implements View.OnClickListener {
    ImageView up,down,line,circle,traingle,rect,sqaure, select;
    DrawingVieww mDrawingView;
    private ProgressDialog pDialog;
    private int mCurrentBackgroundColor;
    private int mCurrentColor;
    private int mCurrentStroke=5;
    private static final int MAX_STROKE_WIDTH = 50;
    private SharedPreferences.Editor editor;
    private ProgressBar progressBar;
    private int STORAGE_PERMISSION_CODE = 23;
    private String Id;
    ImageButton pen,eraser, delete, undo, redo, backgroundcolor, pencolor, pensize, save;
    QuestionViewFragment questionViewFragment;
    private AlertDialogManager dialogManager;
    SharedPreferences teamID;
    File sdcard = Environment.getExternalStorageDirectory();

    ImageView view;
    public static ExplanationTouchPad newInstance() {
        ExplanationTouchPad fragment = new ExplanationTouchPad();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.touchpad, container, false);
        mDrawingView = (DrawingVieww) itemView.findViewById(R.id.first_drawing_view);
        progressBar = new ProgressBar(getContext());
        ButterKnife.bind(getActivity());
        dialogManager = new AlertDialogManager();
        questionViewFragment = new QuestionViewFragment();
        teamID = PreferenceManager.getDefaultSharedPreferences(getContext());
        pen = (ImageButton)getActivity().findViewById(R.id.pencil);
        eraser = (ImageButton) getActivity().findViewById(R.id.eraser1);
        delete = (ImageButton) getActivity().findViewById(R.id.delete1);
        undo = (ImageButton) getActivity().findViewById(R.id.undo1);
        redo = (ImageButton) getActivity().findViewById(R.id.redo1);
        backgroundcolor = (ImageButton) getActivity().findViewById(R.id.background1);
        pencolor = (ImageButton) getActivity().findViewById(R.id.pencolor1);
        pensize = (ImageButton) getActivity().findViewById(R.id.thickness1);
        save = (ImageButton) getActivity().findViewById(R.id.save1);

        up = (ImageView)itemView.findViewById(R.id.up);
        up.setOnClickListener(this);
        down = (ImageView)itemView.findViewById(R.id.down);
        down.setOnClickListener(this);
        line = (ImageView)itemView.findViewById(R.id.line);
        line.setOnClickListener(this);
        circle = (ImageView)itemView.findViewById(R.id.circle);
        circle.setOnClickListener(this);
        traingle = (ImageView)itemView.findViewById(R.id.traingle);
        traingle.setOnClickListener(this);
        rect = (ImageView)itemView.findViewById(R.id.rectangle);
        rect.setOnClickListener(this);
        sqaure = (ImageView)itemView.findViewById(R.id.square);
        sqaure.setOnClickListener(this);
        select = (ImageView)itemView.findViewById(R.id.select);
        select.setOnClickListener(this);
        pen.setOnClickListener(this);
        eraser.setOnClickListener(this);
        delete.setOnClickListener(this);
        undo.setOnClickListener(this);
        redo.setOnClickListener(this);
        backgroundcolor.setOnClickListener(this);
        pencolor.setOnClickListener(this);
        pensize.setOnClickListener(this);
        save.setOnClickListener(this);
        view = (ImageView)itemView.findViewById(R.id.img);

        ImageView one = (ImageView)itemView.findViewById(R.id.screen1);
        one.setOnClickListener(this);
        ImageView two = (ImageView)itemView.findViewById(R.id.screen2);
        two.setOnClickListener(this);
        ImageView three = (ImageView)itemView.findViewById(R.id.screen3);
        three.setOnClickListener(this);
        ImageView four = (ImageView)itemView.findViewById(R.id.screen4);
        four.setOnClickListener(this);

        String value = teamID.getString("pathname", "1");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = settings.edit();
        editor.putString("previousImage", value);
        editor.commit();
        mDrawingView.mCurrentShape = DrawingVieww.SMOOTHLINE;
        try{
            Bitmap bmp = BitmapFactory.decodeFile(value);
            if(bmp==null){
            }else{
                BitmapDrawable drawable = new BitmapDrawable(getResources(), bmp);
                if (drawable != null) {
                    mCurrentBackgroundColor = ContextCompat.getColor(getContext(), android.R.color.transparent);
                    mDrawingView.setBackgroundColor(mCurrentBackgroundColor);
                    drawable.setGravity(100);
                    view.setImageDrawable(drawable);
                    mDrawingView.mCurrentShape = DrawingVieww.SMOOTHLINE;
                    Log.v("sai","width---"+bmp.getWidth());

                } else if (drawable == null) {
                    initDrawingView();
                }
            }
        }catch (OutOfMemoryError error){
            error.printStackTrace();
        }

        return itemView;
    }

    //We are calling this method to check the permission status
    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission

        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(getActivity(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
        return;
    }

    private void initDrawingView() {
        mCurrentBackgroundColor = ContextCompat.getColor(getContext(), android.R.color.black);
        mCurrentColor = ContextCompat.getColor(getContext(), android.R.color.white);
        mCurrentStroke = 5;
        mDrawingView.setBackgroundColor(mCurrentBackgroundColor);
        mDrawingView.setPaintColor(mCurrentColor);
        mDrawingView.setPaintStrokeWidth(mCurrentStroke);
    }

    private void startFillBackgroundDialog() {
        int[] colors = getResources().getIntArray(R.array.palette);

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                colors,
                mCurrentBackgroundColor,
                5,
                ColorPickerDialog.SIZE_SMALL);

        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                mCurrentBackgroundColor = color;
                mDrawingView.setBackgroundColor(mCurrentBackgroundColor);
            }

        });

        dialog.show(getActivity().getFragmentManager(), "ColorPickerDialog");
    }

    private void startColorPickerDialog() {
        int[] colors = getResources().getIntArray(R.array.palette);

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                colors,
                mCurrentColor,
                5,
                ColorPickerDialog.SIZE_SMALL);

        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                mCurrentColor = color;
                mDrawingView.setPaintColor(mCurrentColor);
            }

        });

        dialog.show(getActivity().getFragmentManager(), "ColorPickerDialog");
    }

    private void startStrokeSelectorDialog() {
        StrokeSelectorDialog dialog = StrokeSelectorDialog.newInstance(mCurrentStroke, MAX_STROKE_WIDTH);
        dialog.setOnStrokeSelectedListener(new StrokeSelectorDialog.OnStrokeSelectedListener() {
            @Override
            public void onStrokeSelected(int stroke) {
                mCurrentStroke = stroke;
                mDrawingView.setPaintStrokeWidth(mCurrentStroke);
            }
        });
        dialog.show(getActivity().getSupportFragmentManager(), "StrokeSelectorDialog");
    }

    public void timerDelayRemoveDialog(long time, final ProgressDialog d) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
            }
        }, time);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        menu.clear();
        inflater.inflate(R.menu.action_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ConnectionDetector detector = new ConnectionDetector(getContext());
                if (detector.isNetworkOn(getContext())) {
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                    editor = settings.edit();
                    editor.putString("input", query);
                    editor.commit();

                    if (isReadStorageAllowed()) {
                        startDownload(query);
                        //If permission is already having then showing the toast
                        //Existing the method with return
                        return true;
                    } else {
                        requestStoragePermission();
                    }
                } else {
                    dialogManager.showAlertDialog1(getContext(), "No Internet Connection", "Check Network Settings", false);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("well", " this worked");
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void startDownload(String query) {
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Downloading");
        pDialog.setCancelable(false);
        pDialog.show();
        timerDelayRemoveDialog(5 * 1000, pDialog);
        teamID = PreferenceManager.getDefaultSharedPreferences(getContext());
        Id = teamID.getString("input", "0");
        Ion.with(getContext())
                .load("http://sreedharscce.com/android/" + query + "/" + query + ".zip")
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        System.out.println("" + downloaded + " / " + total);
                    }
                })
                .write(new File(sdcard, Id + ".zip"))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        if (e != null) {

                        }
                        // download done...
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                        String zipFilePath = sdcard
                                .getAbsolutePath() + "/";
                        unpackZip(zipFilePath, Id + ".zip");
                        callfragment();
                        // do stuff with the File or error
                    }
                });
    }

    private boolean unpackZip(String path, String zipname) {
        InputStream is;
        ZipInputStream zis;
        try {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry mZipEntry;
            byte[] buffer = new byte[1024];
            int count;
            File fileDirectory = new File(sdcard.getAbsolutePath() + "/SreedharCCE/" + Id);
            // have the object build the directory structure, if needed.
            if (fileDirectory.equals(null)) {
                fileDirectory.delete();
            } else {
                fileDirectory.mkdirs();
                while ((mZipEntry = zis.getNextEntry()) != null) {
                    // zapis do souboru
                    filename = mZipEntry.getName();

                    // Need to create directories if not exists, or
                    // it will generate an Exception...
                    if (mZipEntry.isDirectory()) {
                        File fmd = new File(path + filename);
                        fmd.mkdirs();
                        continue;
                    }
                    File outputFile = new File(fileDirectory, filename);
                    FileOutputStream fout = new FileOutputStream(outputFile);

                    // cteni zipu a zapis
                    while ((count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                    }
                    fout.close();
                    zis.closeEntry();
                }
            }

            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public File saveImage() {
        File f = null;
        BitmapDrawable drawable = (BitmapDrawable) view.getDrawable();
        if(drawable==null){
            try {
                mDrawingView.setDrawingCacheEnabled(true);
                //mDrawingView.
                Bitmap bmm = mDrawingView.getDrawingCache(true);//.getDrawingCache();
                //bmm.setWidth(1200);

                String Id = teamID.getString("input", "0");
                int currentquestionid = QuestionViewFragment.CurrentQuestionId + 1;
                File fileDirectory = new File(sdcard.getAbsolutePath() + "/SreedharCCE/" + Id + "/" + currentquestionid);
                // have the object build the directory structure, if needed.
                fileDirectory.mkdirs();

                String timeStamp = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss").format(Calendar.getInstance().getTime());
                f = new File(fileDirectory, currentquestionid + "::" + timeStamp + ".png");
                FileOutputStream strm = new FileOutputStream(f);
                bmm.compress(Bitmap.CompressFormat.PNG, 80, strm);
                //imageBm.compress(Bitmap.CompressFormat.PNG, 80, strm);
                strm.close();

                Toast.makeText(getContext(), "Image is saved successfully.", Toast.LENGTH_SHORT).show();
                FragmentManager fm = getFragmentManager();
                QuestionViewFragment fragm = (QuestionViewFragment) fm.findFragmentById(R.id.activity_split_pane_right_pane);
                fragm.getFileNames(currentquestionid,Id);
            }
            catch (FileNotFoundException ex){
                ex.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }else if(view!=null){
            try {
                BitmapDrawable drawabl = (BitmapDrawable) view.getDrawable();
                Bitmap bitmap = drawabl.getBitmap();
                mDrawingView.setDrawingCacheEnabled(true);
                //   mDrawingView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                Bitmap bmm = mDrawingView.getDrawingCache();
                Bitmap mBackground = Bitmap.createBitmap(bmm.getWidth(), bmm.getHeight(), Bitmap.Config.ARGB_8888);
                //Toast.makeText(getActivity(), "width---"+bmm.getWidth(), Toast.LENGTH_SHORT).show();
                Log.v("sai","width---"+bmm.getWidth());
                Canvas mComboImage = new Canvas(mBackground);
                mComboImage.drawBitmap(bitmap, 0f, 0f, null);
                mComboImage.drawBitmap(bmm, 0f, 0f, null);
                BitmapDrawable mBitmapDrawable = new BitmapDrawable(mBackground);
                Bitmap mNewSaving = ((BitmapDrawable) mBitmapDrawable).getBitmap();
                String Id = teamID.getString("input", "0");
                int currentquestionid = QuestionViewFragment.CurrentQuestionId + 1;
                File fileDirectory = new File(sdcard.getAbsolutePath() + "/SreedharCCE/" + Id + "/" + currentquestionid);
                // have the object build the directory structure, if needed.
                fileDirectory.mkdirs();

                String timeStamp = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss").format(Calendar.getInstance().getTime());
                f = new File(fileDirectory, currentquestionid + "::" + timeStamp + ".png");
                FileOutputStream strm = new FileOutputStream(f);
                mNewSaving.compress(Bitmap.CompressFormat.PNG, 80, strm);
                strm.close();
                Toast.makeText(getContext(), "Image is saved successfully.", Toast.LENGTH_SHORT).show();
                FragmentManager fm = getFragmentManager();
                QuestionViewFragment fragm = (QuestionViewFragment) fm.findFragmentById(R.id.activity_split_pane_right_pane);
                fragm.getFileNames(currentquestionid,Id);
            }
            catch (FileNotFoundException ex){
                ex.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;

    }
    public void callfragment() {
        QuestionViewFragment fragment2 = new QuestionViewFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_split_pane_right_pane, fragment2);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.pencil:
                mDrawingView.deactivateEraser();
                mDrawingView.mCurrentShape = DrawingVieww.SMOOTHLINE;
                break;
            case R.id.eraser1:
                if (v.getId() == R.id.eraser1) {
                        mDrawingView.activateEraser();
                    Toast.makeText(getContext(),"Eraser Selected",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.delete1:
                mDrawingView.clearCanvas();
                view.setImageResource(0);
                break;
            case R.id.undo1:
                mDrawingView.undo();
                break;
            case R.id.redo1:
                mDrawingView.redo();
                break;
            case R.id.screen1:
                mDrawingView.mCurrentShape = DrawingVieww.SMOOTHLINE;
                View screenOne = getActivity().findViewById(R.id.screenone);
                screenOne.setVisibility(View.VISIBLE);
                mDrawingView = (DrawingVieww) getActivity().findViewById(R.id.first_drawing_view);
                View screenTwo = getActivity().findViewById(R.id.screentwo);
                screenTwo.setVisibility(View.GONE);
                View screenThree = getActivity().findViewById(R.id.screentthree);
                screenThree.setVisibility(View.GONE);
                View screenFour = getActivity().findViewById(R.id.screenfour);
                screenFour.setVisibility(View.GONE);
                break;
            case R.id.screen2:
                mDrawingView.mCurrentShape = DrawingVieww.SMOOTHLINE;
                mDrawingView = (DrawingVieww) getActivity().findViewById(R.id.second_drawing_view);
                View screenTwo1 = getActivity().findViewById(R.id.screentwo);
                screenTwo1.setVisibility(View.VISIBLE);
                View screenThree1 = getActivity().findViewById(R.id.screentthree);
                screenThree1.setVisibility(View.GONE);
                View screenFour1 = getActivity().findViewById(R.id.screenfour);
                screenFour1.setVisibility(View.GONE);
                View screenOne1 = getActivity().findViewById(R.id.screenone);
                screenOne1.setVisibility(View.GONE);
                break;
            case R.id.screen3:
                mDrawingView.mCurrentShape = DrawingVieww.SMOOTHLINE;
                mDrawingView = (DrawingVieww) getActivity().findViewById(R.id.third_drawing_view);
                View screenTwo2 = getActivity().findViewById(R.id.screentwo);
                screenTwo2.setVisibility(View.GONE);
                View screenThree2 = getActivity().findViewById(R.id.screentthree);
                screenThree2.setVisibility(View.VISIBLE);
                View screenFour2 = getActivity().findViewById(R.id.screenfour);
                screenFour2.setVisibility(View.GONE);
                View screenOne2 = getActivity().findViewById(R.id.screenone);
                screenOne2.setVisibility(View.GONE);
                break;
            case R.id.screen4:
                mDrawingView.mCurrentShape = DrawingVieww.SMOOTHLINE;
                mDrawingView = (DrawingVieww) getActivity().findViewById(R.id.fourth_drawing_view);
                View screenTwo3 = getActivity().findViewById(R.id.screentwo);
                screenTwo3.setVisibility(View.GONE);
                View screenThree3 = getActivity().findViewById(R.id.screentthree);
                screenThree3.setVisibility(View.GONE);
                View screenFour3 = getActivity().findViewById(R.id.screenfour);
                screenFour3.setVisibility(View.VISIBLE);
                View screenOne3 = getActivity().findViewById(R.id.screenone);
                screenOne3.setVisibility(View.GONE);
                break;
            case R.id.background1:
                startFillBackgroundDialog();
                break;
            case R.id.pencolor1:
                startColorPickerDialog();
                break;
            case R.id.thickness1:
                startStrokeSelectorDialog();
                break;
            case R.id.save1:
                saveImage();
                break;
            case R.id.up:
                up.setVisibility(View.GONE);
                down.setVisibility(View.VISIBLE);
                View forgotLayout = getActivity().findViewById(R.id.shapeslayout);
                forgotLayout.setAnimation(AnimationUtils.makeInAnimation(getActivity(),true));
                forgotLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.down:
                down.setVisibility(View.GONE);
                up.setVisibility(View.VISIBLE);
                View forgotLayoutt = getActivity().findViewById(R.id.shapeslayout);
                forgotLayoutt.setVisibility(View.GONE);
                break;
            case R.id.line:
                mDrawingView.deactivateEraser();
              mDrawingView.mCurrentShape = DrawingVieww.LINE;
                break;
            case R.id.circle:
                mDrawingView.deactivateEraser();
                mDrawingView.mCurrentShape = DrawingVieww.CIRCLE;
                break;
            case R.id.traingle:
                mDrawingView.deactivateEraser();
                mDrawingView.mCurrentShape = DrawingVieww.TRIANGLE;
                break;
            case R.id.rectangle:
                mDrawingView.deactivateEraser();
                mDrawingView.mCurrentShape = DrawingVieww.RECTANGLE;
                break;
            case R.id.square:
                mDrawingView.deactivateEraser();
                mDrawingView.mCurrentShape = DrawingVieww.SQUARE;
                break;
            case R.id.select:
                mDrawingView.deactivateEraser();
                mDrawingView.mCurrentShape = DrawingVieww.SELECT;
                break;


        }

    }

}
