package com.aadamsdev.communities.chat;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.aadamsdev.communities.pojo.ChatMessage;
import com.aadamsdev.communities.pojo.ChatRoom;
import com.aadamsdev.communities.pojo.UserStatus;
import com.aadamsdev.communities.utils.CommunitiesUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Andrew Adams on 6/28/2017.
 */


public class ChatClient implements android.location.LocationListener {

    private final static String TAG = ChatClient.class.getSimpleName();

    private static ChatClient client;

    private Gson gson;

    private String username;
    private String lastKnownChatRoom;
    private boolean isInChatRoom;

    private Socket socket;
    private ChatClientCallback chatClientCallback;

    private final static String HOST_URL = "http://192.168.0.10:8000/";

    private final static String OUTGOING_MESSAGE = "outgoing_message";
    private final static String INCOMING_MESSAGE = "incoming_message";
    private final static String LOCATION_UPDATE = "location_update";
    private final static String CHATROOM_UPDATE = "chatroom_update";
    private final static String USER_STATUS_UPDATE = "user_status_update";

    private ChatClient() {
        gson = new Gson();
    }

    public static ChatClient newInstance(String username) {
        client = new ChatClient();
        client.username = username;
        return client;
    }

    public static ChatClient getInstance() {
        if (client == null) {
            Log.w(TAG, "Chat client hasn't been instantiated yet.");
        }
        return client;
    }

    public void connect() {
        try {
            if (CommunitiesUtils.isEmulator()) {
                socket = IO.socket("http://10.0.2.2:8000/");
            } else {
                socket = IO.socket(HOST_URL);
            }
            isInChatRoom = false;
            registerEvents();
            socket.connect();
        } catch (URISyntaxException ex) {
            Log.i("ChatClient", ex.toString());
        }
    }

    private void registerEvents() {
        socket.on(CHATROOM_UPDATE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (chatClientCallback != null) {
                    String jsonString = getJsonStringFromSocketArgs(args);

                    ChatRoom chatRoom = gson.fromJson(jsonString, ChatRoom.class);

                    lastKnownChatRoom = chatRoom.getChatRoomName();
                    chatClientCallback.onChatRoomChanged(chatRoom);

                    // Must be after callback; will not show dialog and retrieve chat history otherwise
                    isInChatRoom = true;
                }
            }

        }).on(INCOMING_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (chatClientCallback != null) {
                    String jsonString = getJsonStringFromSocketArgs(args);

                    ChatMessage chatMessage = gson.fromJson(jsonString, ChatMessage.class);
                    chatClientCallback.onNewMessage(chatMessage);
                }
            }

        }).on(USER_STATUS_UPDATE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (chatClientCallback != null) {
                    String jsonString = getJsonStringFromSocketArgs(args);

                    UserStatus status = gson.fromJson(jsonString, UserStatus.class);
                    chatClientCallback.onUserStatusesUpdated(status);
                }
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, "Disconnected socket!!");
            }
        });
    }

    private String getJsonStringFromSocketArgs(Object... args) {
        JSONObject data = (JSONObject) args[0];
        return data.toString();
    }

    public void sendMessage(String message, String chatRoomName) {
        JSONObject object = new JSONObject();

        try {
            object.put("username", username);
            object.put("message", message);
            object.put("chatRoomName", chatRoomName);

            socket.emit(OUTGOING_MESSAGE, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.i(TAG, location.getLatitude() + " " + location.getLongitude());

            //If everything went fine lets get latitude and longitude
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("latitude", currentLatitude);
                jsonObject.put("longitude", currentLongitude);
                jsonObject.put("lastKnownChatRoom", lastKnownChatRoom);
                jsonObject.put("username", username);

                socket.connect();
                socket.emit(LOCATION_UPDATE, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void updateLocation(LocationManager locationManager) {
        try {
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
            onLocationChanged(location);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    public void startLocationRequests(LocationManager locationManager) {
        try {
            locationManager.requestLocationUpdates(locationManager.getBestProvider(new Criteria(), false), 400, 1, this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isInChatRoom() {
        return isInChatRoom;
    }

    public String getUsername() {
        return username;
    }

    public void registerCallback(ChatClientCallback chatClientCallback) {
        this.chatClientCallback = chatClientCallback;
    }

    public interface ChatClientCallback {
        void onNewMessage(ChatMessage chatMessage);

        void onChatRoomChanged(ChatRoom chatRoom);

        void onUserStatusesUpdated(UserStatus statuses);
    }
}

