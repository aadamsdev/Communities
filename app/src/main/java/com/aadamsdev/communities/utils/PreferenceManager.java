package com.aadamsdev.communities.utils;

/**
 * Created by Andrew Adams on 8/3/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.aadamsdev.communities.R;
import com.aadamsdev.communities.chat.ChatClient;
import com.aadamsdev.communities.pojo.ChatRoom;
import com.google.gson.Gson;

public class PreferenceManager {

    private static PreferenceManager instance;

    private SharedPreferences sharedPreferences;
    private Gson gson;

    // shared pref mode
    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "communities";
    private static final String IS_FIRST_TIME_LAUNCH = "is_first_launch";
    private static final String USER_NAME = "username_key";
    private static final String LAST_CHAT_ROOM = "last_chat_room";

    private PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        gson = new Gson();
    }

    public static PreferenceManager newInstance(Context context) {
        instance = new PreferenceManager(context);
        return instance;
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

    public void setLastKnownChatRoom(ChatRoom chatRoom) {
        String chatRoomJson = gson.toJson(chatRoom);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_CHAT_ROOM, chatRoomJson);
        editor.apply();
    }

    public ChatRoom getLastChatRoom() {
        String chatRoomJson = sharedPreferences.getString(LAST_CHAT_ROOM, "");
        return gson.fromJson(chatRoomJson, ChatRoom.class);
    }
}