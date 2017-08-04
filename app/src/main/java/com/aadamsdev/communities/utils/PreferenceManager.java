package com.aadamsdev.communities.utils;

/**
 * Created by Andrew Adams on 8/3/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private SharedPreferences sharedPreferences;
    private Context context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "communities";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}