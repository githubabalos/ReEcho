package com.charles.re_echo;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHandler {

    public void moveFile(String inputPath, String inputName, String outputPath, String outputName) {
        InputStream is = null;
        OutputStream os = null;

        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            is = new FileInputStream(inputPath + inputName);
            Log.d("D", "Input file: " + inputPath+inputName);
            os = new FileOutputStream(outputPath + outputName);
            Log.d("D", "Output file: " + outputPath+outputName);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            is.close();
            is = null;

            // write the output file
            os.flush();
            os.close();
            os = null;

            // delete the original file
            new File(inputPath + inputName).delete();
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
