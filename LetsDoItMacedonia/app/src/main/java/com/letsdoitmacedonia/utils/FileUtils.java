package com.letsdoitmacedonia.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;

import com.letsdoitmacedonia.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;


public class FileUtils {

  public static File getOutputPictureFile(Context context) {

    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.
    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name));
    // This location works best if you want the created images to be shared
    // between applications and persist after your app has been uninstalled.

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        // Log.d("Failed to create directory");
        return null;
      }
    }
    // Create a media file name
    Calendar calendar = Calendar.getInstance();
    return new File(mediaStorageDir.getPath()
            + File.separator
            + "_"
            + calendar.get(Calendar.DAY_OF_YEAR) + "_"
            + calendar.getTimeInMillis() + ".jpg");
  }

  public static boolean persistImage(Context context, Bitmap bitmap, String filePath) {


    File imageFile = new File(filePath);
    try {
      OutputStream outputStream = new FileOutputStream(imageFile);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
      outputStream.flush();
      outputStream.close();

      // scan the file using the media scanner so it's displayed in gallery
      MediaScannerConnection.scanFile(context, new String[] {filePath}, new String[] {"image/jpeg"}, null);
      return true;
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}



