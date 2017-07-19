package com.indianservers.writingpad;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.indianservers.writingpad.component.DrawingVieww;
import com.indianservers.writingpad.services.AlertDialogManager;
import com.indianservers.writingpad.services.ConnectionDetector;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.os.Handler;
import butterknife.ButterKnife;

public class ExplanationTouchPad extends Fragment{

    DrawingVieww mDrawingView;
    private ProgressDialog pDialog;
    private int mCurrentBackgroundColor;
    private int mCurrentColor;
    private int mCurrentStroke;
    private static final int MAX_STROKE_WIDTH = 50;
    private SharedPreferences.Editor editor;
    private ProgressBar progressBar;
    private int STORAGE_PERMISSION_CODE = 23;
    private String Id;
    private AlertDialogManager dialogManager;
    File sdcard  = Environment.getExternalStorageDirectory();
    public static Fragment newInstance() {
        Fragment fragment = new ExplanationTouchPad();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View itemView = inflater.inflate(R.layout.touchpad, container, false);
        mDrawingView = (DrawingVieww) itemView.findViewById(R.id.main_drawing_view);
         progressBar = new ProgressBar(getContext());
        ButterKnife.bind(getActivity());
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        dialogManager = new AlertDialogManager();
        initDrawingView();
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
    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission

        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }
    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        //Checking the request code of our request
        if(requestCode == STORAGE_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Displaying a toast
                Toast.makeText(getActivity(),"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(getActivity(),"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
        return;
    }

    private void initDrawingView()
    {
        mCurrentBackgroundColor = ContextCompat.getColor(getContext(), android.R.color.white);
        mCurrentColor = ContextCompat.getColor(getContext(), android.R.color.black);
        mCurrentStroke = 10;

        mDrawingView.setBackgroundColor(mCurrentBackgroundColor);
        mDrawingView.setPaintColor(mCurrentColor);
        mDrawingView.setPaintStrokeWidth(mCurrentStroke);
    }

    private void startFillBackgroundDialog()
    {
        int[] colors = getResources().getIntArray(R.array.palette);

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                colors,
                mCurrentBackgroundColor,
                5,
                ColorPickerDialog.SIZE_SMALL);

        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener()
        {

            @Override
            public void onColorSelected(int color)
            {
                mCurrentBackgroundColor = color;
                mDrawingView.setBackgroundColor(mCurrentBackgroundColor);
            }

        });

        dialog.show(getActivity().getFragmentManager(), "ColorPickerDialog");
    }

    private void startColorPickerDialog()
    {
        int[] colors = getResources().getIntArray(R.array.palette);

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                colors,
                mCurrentColor,
                5,
                ColorPickerDialog.SIZE_SMALL);

        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener()
        {

            @Override
            public void onColorSelected(int color)
            {
                mCurrentColor = color;
                mDrawingView.setPaintColor(mCurrentColor);
            }

        });

        dialog.show(getActivity().getFragmentManager(), "ColorPickerDialog");
    }

    private void startStrokeSelectorDialog()
    {
        StrokeSelectorDialog dialog = StrokeSelectorDialog.newInstance(mCurrentStroke, MAX_STROKE_WIDTH);

        dialog.setOnStrokeSelectedListener(new StrokeSelectorDialog.OnStrokeSelectedListener()
        {
            @Override
            public void onStrokeSelected(int stroke)
            {
                mCurrentStroke = stroke;
                mDrawingView.setPaintStrokeWidth(mCurrentStroke);
            }
        });

        dialog.show(getActivity().getSupportFragmentManager(), "StrokeSelectorDialog");
    }
    public void timerDelayRemoveDialog(long time, final ProgressDialog d){
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
                    if(detector.isNetworkOn(getContext())){
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                    editor = settings.edit();
                    editor.putString("input", query);
                    editor.commit();

                    if(isReadStorageAllowed()){
                        startDownload(query);
                        //If permission is already having then showing the toast
                        //Existing the method with return
                        return true;
                    }else{
                        requestStoragePermission();
                    }
                    }else{
                        dialogManager.showAlertDialog1(getContext(),"No Internet Connection","Check Network Settings",false);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.eraser:
                if (item.getItemId()==R.id.eraser) {

                    if (mDrawingView.isEraserActive()) {

                        mDrawingView.deactivateEraser();

                        item.setIcon(R.drawable.earsers);

                    } else {

                        mDrawingView.activateEraser();

                        item.setIcon(R.drawable.pen);
                    }
                }
                break;
            case R.id.delete:
                mDrawingView.clearCanvas();
                break;
            case R.id.undo:
                mDrawingView.undo();
                break;
            case R.id.redo:
                mDrawingView.redo();
                break;
            case R.id.background:
                startFillBackgroundDialog();
                break;
            case R.id.pencolor:
                startColorPickerDialog();
                break;
            case R.id.thickness:
                startStrokeSelectorDialog();
                break;
            case R.id.save:
                saveImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void startDownload(String query) {
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Downloading");
        pDialog.setCancelable(false);
        pDialog.show();
        timerDelayRemoveDialog(5*1000,pDialog);
        SharedPreferences teamID = PreferenceManager.getDefaultSharedPreferences(getContext());
        Id = teamID.getString("input","0");
        Ion.with(getContext())
                .load("http://sreedharscce.com/android/"+query+"/"+query+".zip")
                .progress(new ProgressCallback() {@Override
                public void onProgress(long downloaded, long total) {
                    System.out.println("" + downloaded + " / " + total);
                }
                })
                .write(new File(sdcard,Id+".zip"))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        if(e!=null){

                        }
                        // download done...
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                        String zipFilePath = Environment.getExternalStorageDirectory()
                                .getAbsolutePath()+"/";
                        unpackZip(zipFilePath, Id+".zip");
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
            File fileDirectory = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath()+"/SreedharCCE/"+Id);
            // have the object build the directory structure, if needed.
            if(fileDirectory.equals(null)){
                fileDirectory.delete();
            }else{
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
        mDrawingView.setDrawingCacheEnabled(true);
        Bitmap bm = mDrawingView.getDrawingCache();
        SharedPreferences teamID = PreferenceManager.getDefaultSharedPreferences(getContext());
        String Id = teamID.getString("input","0");
        File fileDirectory = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()+"/SreedharCCE/"+Id+"/"+QuestionViewFragment.CurrentQuestionId);
        // have the object build the directory structure, if needed.
        fileDirectory.mkdirs();
        File f = null;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm a");
        // you can get seconds by adding  "...:ss" to it
        date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

        String localTime = date.format(currentLocalTime);
        f = new File(fileDirectory, QuestionViewFragment.CurrentQuestionId+"::"+localTime+ ".png");

        try {
            FileOutputStream strm = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 80, strm);
            strm.close();

            Toast.makeText(getContext(), "Image is saved successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }
    public void getFileNames(int position){
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "/SreedharCCE/"+Id+"/"+position+"/");
        if(yourDir.isDirectory()){
            if (yourDir == null||yourDir.equals(null)) {

            }else{
                for (File f : yourDir.listFiles()) {
                    if (f.isFile()) {
                        String name = f.getName();
                    }else {

                    }
                }
            }
        }

    }
    public void callfragment(){
        QuestionViewFragment fragment2 = new QuestionViewFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_split_pane_right_pane, fragment2);
        fragmentTransaction.commit();
    }
}
