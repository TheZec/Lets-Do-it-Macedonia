package com.letsdoitmacedonia.preferences;


import android.content.Context;
import android.content.SharedPreferences;


public class PrefsWelcome {

  private static final String PREFS_NAME = "letsdoit_prefs";
  private static final String PREF_WELCOME_SHOWN = "intro_complete";

  private static PrefsWelcome instance;
  private final SharedPreferences sharedPreferences;

  public PrefsWelcome(Context context) {

    sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
  }

  public static PrefsWelcome with(Context context) {

    if (instance == null) {
      instance = new PrefsWelcome(context);
    }
    return instance;
  }

  public boolean isWelcomeShown() {

    return sharedPreferences.getBoolean(PREF_WELCOME_SHOWN, false);
  }

  public void setWelcomeShown() {

    sharedPreferences
            .edit()
            .putBoolean(PREF_WELCOME_SHOWN, true)
            .apply();
  }

  public void setWelcomeNotShown() {

    sharedPreferences
            .edit()
            .putBoolean(PREF_WELCOME_SHOWN, false)
            .apply();
  }
}
