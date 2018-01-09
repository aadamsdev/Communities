package com.aadamsdev.communities.utils;

/**
 * Created by Andrew Adams on 8/3/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.aadamsdev.communities.R;
import com.aadamsdev.communities.chat.ChatClient;

public class PreferenceManager {
    private static SharedPreferences sharedPreferences;
    private static Context context;

    private static PreferenceManager instance;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "communities";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String USER_NAME = "COMMUNITIES_USERNAME_KEY";

    private PreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public static PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, username);
        editor.apply();
    }

    public String getCurrentUser() {
        return sharedPreferences.getString(USER_NAME, "");
    }


}