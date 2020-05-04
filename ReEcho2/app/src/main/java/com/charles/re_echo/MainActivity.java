package com.charles.re_echo;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static String PATHNAME = Environment.getExternalStorageDirectory().getPath() + "/";
    private String uri;
    //private final String FILE_NAME = "recycle_bin.zip";

    private final String OUTPUT_PATH = "sdcard/.recycle-bin/";
    String filename;
    String filepath;

    Context con = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkIfPermissionGranted();
        init();
    }
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
        private void checkIfPermissionGranted() {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                if(hasWritePermission != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                    return;
                }
                if(hasReadPermission != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                    return;
                }
            }

    }

    private void init() {
        ImageButton button_DF = (ImageButton)findViewById(R.id.button_DeleteFile);
        ImageButton button_RF = (ImageButton)findViewById(R.id.button_RecoverFile);
        ImageButton button_Boost = (ImageButton)findViewById(R.id.button_Boost);

        button_DF.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Button Pressed: Delete Files",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                        .setType("*/*")
                        .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        .setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 111);
            }
        });

        button_RF.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Button Pressed: Recover Files",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(con, RecycleBinList.class);
                startActivity(intent);
            }
        });

        button_Boost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Clearing Memory",Toast.LENGTH_SHORT).show();
                checkRAM();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Clearing RAM please wait" ,Toast.LENGTH_SHORT).show();
                    }


                },2000);

                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkRAM();
                    }


                },4000);

            }
        });
    }
    public void checkRAM(){
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)con. getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;

        Toast.makeText(getApplicationContext(),"Available RAM: " + availableMegs + "MB",Toast.LENGTH_SHORT).show();
    }
    //Do when startActivityForResult is called.
    public void onActivityResult(int requestCode, int resultCode, Intent result){
        if(requestCode == 111) {
            if(null != result) { // checking empty selection
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if(null != result.getClipData()) { // checking multiple selection or not
                        for(int i = 0; i < result.getClipData().getItemCount(); i++) {
                            uri = result.getClipData().getItemAt(i).getUri().getPath();
                            File f = new File(uri);

                            filename = f.getName();
                            filepath = uri.replace(filename,"/");
                            Log.d("URL: ",filepath + filename);
                        }
                        confirmationDialog();
                    } else {
                        uri = result.getData().getPath();
                        File f = new File(uri);

                        filename = f.getName();
                        filepath = uri.replace(filename,"/");
                        Log.d("URL: ",uri + filename);
                    }
                    confirmationDialog();
                }else{Toast.makeText(getApplicationContext(),
                        "An error has occured: API level requirements not met",Toast.LENGTH_SHORT).show();};
            }
        }
    }


    private void confirmationDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        FileHandler fh = new FileHandler();
                        fh.moveFile(filepath,filename,OUTPUT_PATH, filename);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
