package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class StorageHelper {
    // The old PixelBlacksmith folder at the root of external storage stopped being writable in
    // Android 10. This directory needs no permission.
    private static File getSaveDirectory(Activity activity) {
        return activity.getExternalFilesDir(null);
    }

    public static String saveLocalSave(Activity activity) {
        File path = getSaveDirectory(activity);
        if (path == null) {
            return activity.getString(R.string.error_storage_unavailable);
        }

        try {
            String filename = getFilename(activity);
            byte[] backup = SupportCodeHelper.encode(GooglePlayHelper.createBackup());

            File file = new File(path, filename);

            FileOutputStream outputStream;
            outputStream = new FileOutputStream(file);
            outputStream.write(backup);
            outputStream.close();
            return filename;
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public static String getFilename(Context context) {
        String date = new SimpleDateFormat("yyyyMMdd_kkmmss").format(new Date());
        return String.format(context.getString(R.string.saveNameFormat), date);
    }

    public static String loadLocalSave(Activity activity, boolean checkIsImprovement) {
        File directory = getSaveDirectory(activity);
        if (directory == null) {
            return activity.getString(R.string.error_storage_unavailable);
        }

        // Get a list of all files
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().startsWith("pixelblacksmith");
            }
        });

        if (files != null && files.length > 0) {
            Arrays.sort(files, Collections.reverseOrder());
            String backupText = SupportCodeHelper.decode(getStringFromFile(files[0]));

            if (!backupText.equals("")) {
                Pair<Integer, Integer> cloudData = GooglePlayHelper.getPrestigeAndXPFromSave(backupText.getBytes());

                if (!checkIsImprovement || GooglePlayHelper.newSaveIsBetter(cloudData)) {
                    GooglePlayHelper.applyBackup(backupText);
                } else {
                    AlertDialogHelper.confirmWorseLocalLoad(activity,
                            Player_Info.getPrestige(),
                            Player_Info.getXp(),
                            cloudData.first,
                            cloudData.second);
                }

                return files[0].getName();
            }
        }
        return activity.getString(R.string.error_no_save_found);
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
