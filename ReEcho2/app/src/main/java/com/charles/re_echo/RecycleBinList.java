package com.charles.re_echo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

public class RecycleBinList extends AppCompatActivity {
    final String RECYCLEBINPATH = "sdcard/.recycle-bin";
    String pathTest = "sdcard/recovered-files/";
    LinearLayout ll = null;
    String filePath;
    String fileName;
    FileHandler fh = new FileHandler();
    File[] files;
    boolean confirmation;
    int index;
    Button[] buttons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_bin_list);
        init();
        listFilesInRB();
    }

    private void init() {
        ll = (LinearLayout) findViewById(R.id.scrollView_LL);
        Log.d("Recycle Bin Path", "Recycle Bin Path: " + RECYCLEBINPATH);
        Log.d("Files", "Recovered Files Path: " + pathTest);
    }

    public void listFilesInRB(){
        File recDir = new File(RECYCLEBINPATH);
        files = recDir.listFiles();
        Log.d("Files", "Size: "+ files.length);
        buttons = new Button[files.length];
        ll.removeAllViews();
        for(int i = 0; i < files.length; i++){
            buttons[i] = new Button(this);
            Log.d("Files", "File Name: " + files[i].getName());
            File f = new File(files[i].toString());

            buttons[i].setText(files[i].getName());
            ll.addView(buttons[i]);
            buttons[i].setId(i);

            buttons[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    confirmationDialog();
                    for (int i = 0; i < buttons.length; i++)
                    {
                        if (buttons[i].getId() == v.getId())
                        {
                            index = i;
                            break;
                        }
                    }
                    Log.d("File Recovered", "File Name: " + fileName + "to" + pathTest);
                }
            });
        }
    }

    private void confirmationDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        fileName = files[index].getName();
                        filePath = files[index].getAbsolutePath().replace(fileName, "");

                        fh.moveFile(filePath,fileName,pathTest,fileName);
                        confirmation = true;
                        buttons[index].setBackgroundColor(0x1c1c1c);
                        listFilesInRB();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        confirmation = false;
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Restore File? Restored file will be found in: " + pathTest)
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
