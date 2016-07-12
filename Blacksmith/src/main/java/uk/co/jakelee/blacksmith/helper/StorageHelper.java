package uk.co.jakelee.blacksmith.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import uk.co.jakelee.blacksmith.R;

public class StorageHelper {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void confirmStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static String writeToSDFile(Activity activity){
        confirmStoragePermissions(activity);
        // Encrypt them
        File file;
        FileOutputStream outputStream;
        try {
            String filename = getFilename(activity);
            file = new File(Environment.getExternalStorageDirectory(), filename);
            byte[] backup = GooglePlayHelper.createBackup();

            outputStream = new FileOutputStream(file);
            outputStream.write(backup);
            outputStream.close();
            return filename;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String getFilename(Context context) {
        String date = new SimpleDateFormat("yyyyMMdd_kkmmss").format(new Date());
        return String.format(context.getString(R.string.saveNameFormat), date);
    }

    public static String readRaw(Context context){
        List<String> paths = new ArrayList<>();
        File directory = new File(Environment.getExternalStorageDirectory().getPath());

        // Get a list of all files
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().startsWith("pixelblacksmith");
            }
        });

        // Get the highest named save file, and apply it
        if (files.length > 0) {
            Arrays.sort(files, Collections.reverseOrder());
            String backupText = getStringFromFile(files[0]);

            if (!backupText.equals("")) {
                GooglePlayHelper.applyBackup(backupText);
                return "Maybe it worked!";
            }
        }
        return "Couldn't find a save.";
    }

    public static String getStringFromFile(File file) {
        String extractedText = "";
        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            boolean done = false;
            while (!done) {
                final String line = reader.readLine();
                done = (line == null);

                if (line != null) {
                    stringBuilder.append(line);
                }
            }

            reader.close();
            inputStream.close();
            extractedText = stringBuilder.toString();
        } catch (IOException e) {
            Log.d("Error", "Error loading");
        }
        return extractedText;
    }
}
