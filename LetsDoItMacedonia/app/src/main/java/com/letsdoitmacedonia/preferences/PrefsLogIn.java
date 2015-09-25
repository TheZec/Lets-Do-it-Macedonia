package com.letsdoitmacedonia.preferences;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PrefsLogIn {

  private static final String PREFS_NAME = "log_prefs";
  private static final String IS_LOG_IN = "loged";
  public static final String  API_KEY = "apiKey";

  private static PrefsLogIn instance;
  private final SharedPreferences sharedPreferences;

  public PrefsLogIn(Context context) {

    sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
  }

  public static PrefsLogIn with(Context context) {

    if (instance == null) {
      instance = new PrefsLogIn(context);
    }
    return instance;
  }

  public void setLogInKey(String apiKey){

    sharedPreferences
            .edit()
            .putString(API_KEY, apiKey)
            .putBoolean(IS_LOG_IN, true)
            .apply();
  }

  public String getApiKey() {

    return  sharedPreferences.getString(API_KEY, null);
  }

  public void removeLogInKey(){

    sharedPreferences
            .edit()
            .clear()
            .apply();
  }

  public boolean checkLogIn(){
    return  sharedPreferences
            .getBoolean(IS_LOG_IN, false);
  }
}
