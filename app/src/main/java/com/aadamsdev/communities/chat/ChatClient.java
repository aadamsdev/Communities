package com.aadamsdev.communities.chat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.aadamsdev.communities.utils.CommunitiesUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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

    //    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private double currentLatitude;
    private double currentLongitude;

    private static ChatClient client;

    private Socket socket;
    private ChatClientCallback chatClientCallback;

    private final static String HOST_URL = "http://192.168.1.5:3000/";
    private final static String OUTGOING_MESSAGE = "OUTGOING_MESSAGE";
    private final static String INCOMING_MESSAGE = "INCOMING_MESSAGE";
    private final static String LOCATION_UPDATE = "LOCATION_UPDATE";
    private final static String CHATROOM_UPDATE = "CHATROOM_UPDATE";

    private ChatClient() {

    }

    public static ChatClient getInstance() {
        if (client == null) {
            client = new ChatClient();
        }
        return client;
    }

    public void connect() {
        try {
            if (CommunitiesUtils.isEmulator()) {
                socket = IO.socket("http://10.0.2.2:3000/");
            } else {
                socket = IO.socket(HOST_URL);
            }
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
                JSONObject data = (JSONObject) args[0];

                try {
                    String chatRoomName = data.getString("chatRoomName");

                    chatClientCallback.onChatRoomChanged(chatRoomName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }).on(INCOMING_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (chatClientCallback != null) {
                    JSONObject data = (JSONObject) args[0];

                    try {
                        String username = data.getString("username");
                        String message = data.getString("message");
                        String timestamp = data.getString("timestamp");
                        int userIconId = data.getInt("userIconId");

                        chatClientCallback.onNewMessage(username, message, timestamp, userIconId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
            }

        });
    }

    public void sendMessage(String username, String message) {
        JSONObject object = new JSONObject();

        try {
            object.put("username", username);
            object.put("message", message);
            object.put("userIconId", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit(OUTGOING_MESSAGE, object);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, location.getLatitude() + " " + location.getLongitude());

        //If everything went fine lets get latitude and longitude
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        JSONObject coordinates = new JSONObject();

        try {
            coordinates.put("latitude", currentLatitude);
            coordinates.put("longitude", currentLongitude);

            socket.connect();
            socket.emit(LOCATION_UPDATE, coordinates);
        } catch (JSONException e) {
            e.printStackTrace();
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

    public void registerCallback(ChatClientCallback chatClientCallback) {
        this.chatClientCallback = chatClientCallback;
    }

    public interface ChatClientCallback {
        void onNewMessage(String username, String message, String timestamp, int userIconId);

        void onChatRoomChanged(String chatRoomName);
    }
}

