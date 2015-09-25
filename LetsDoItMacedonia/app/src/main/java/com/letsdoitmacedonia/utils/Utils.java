package com.letsdoitmacedonia.utils;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;

import com.letsdoitmacedonia.R;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

  public static boolean IS_SHARED = false;
  public static boolean ON_REG_LOG = false;
  public static boolean ZOOM_MAP = true;
  public static boolean TAKE_PHOTO = false;
  public static boolean SELECT_PHOTO = false;
  private static Utils singletonInstance;
  private Context context;

  private Utils() {

  }

  public static Utils getSingleton() {

    if (null == singletonInstance) {
      singletonInstance = new Utils();
    }
    return singletonInstance;
  }

  public static boolean isEmailValid(String email) {

    boolean isValid = false;

    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
    CharSequence inputStr = email;

    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(inputStr);
    if (matcher.matches()) {
      isValid = true;
    }
    return isValid;
  }

  public static boolean isPassValid(EditText password, Context context) {

    String pass = password.getText().toString();
    if (TextUtils.isEmpty(pass) || pass.length() < 6) {
      password.setError(context.getString(R.string.minimum_password));
      return false;
    }
    else {
      return true;
    }
  }

  public boolean isOnline(Context context) {

    try {
      ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      return cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    catch (Exception e) {
      return false;
    }
  }

  public Typeface setCustomFont(Context context) {

    Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Mountain-Demo.otf");
    return typeface;
  }

  public void checkGPS(final Context context) {

    LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      //Ask the user to enable GPS
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setTitle("Location Manager");
      builder.setMessage(context.getString(R.string.activate_gps));
      builder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
          //Launch settings, allowing user to make a change
          Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
          context.startActivity(i);


        }
      });
      builder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
          //No location service, no Activity
          Activity activity = new Activity();
          activity.finish();
        }
      });
      builder.create().show();
    }
  }

  public Locale checkLocale(Context context) {

    Locale currentLocale = context.getResources().getConfiguration().locale;
    return currentLocale;
  }
}
