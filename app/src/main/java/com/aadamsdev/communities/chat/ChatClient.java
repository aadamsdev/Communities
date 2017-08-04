package com.aadamsdev.communities.chat;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Andrew Adams on 6/28/2017.
 */


public class ChatClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

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
            if (isEmulator()) {
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
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

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

    public void registerCallback(ChatClientCallback chatClientCallback) {
        this.chatClientCallback = chatClientCallback;
    }

    public interface ChatClientCallback {
        void onNewMessage(String username, String message, String timestamp, int userIconId);
    }

    private boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


}

//public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
//
//    //Define a request code to send to Google Play services
//    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
//
//    private String currentChatRoom;
//    private Socket socket;
//    private EditText editText;
//    private TextView textView;
//    private Button sendButton;
//
//    private GoogleApiClient googleApiClient;
//    private LocationRequest locationRequest;
//    private double currentLatitude;
//    private double currentLongitude;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, 0);
//
//        editText = (EditText) findViewById(R.id.text_edit);
//        textView = (TextView) findViewById(R.id.text_view);
//
//        sendButton = (Button) findViewById(R.id.send_button);
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("Main", "Send button clicked");
//                attemptSend();
//            }
//        });
//
//        try {
//            socket = IO.socket("http://192.168.1.5:3000/");
//        } catch (URISyntaxException ex) {
//            Log.i("Main", ex.toString());
//        }
//    }
//
//    private void attemptSend() {
//        String message = editText.getText().toString().trim();
//        if (TextUtils.isEmpty(message)) {
//            return;
//        }
//
//        editText.setText("");
//        JSONObject object = new JSONObject();
//
////        try {
////            object.put("message", message);
////            object.put()
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
//
//        socket.emit("chat message", message);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        //Now lets connect to the API
//        googleApiClient.connect();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        socket.disconnect();
//
//        Log.v(this.getClass().getSimpleName(), "onPause()");
//
//        //Disconnect from API onPause()
//        if (googleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//            googleApiClient.disconnect();
//        }
//    }
//
//    /**
//     * If connected get lat and long
//     */
//    @Override
//    public void onConnected(Bundle bundle) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "Permissions not granted", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//
//        if (location == null) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
//
//        } else {
//            //If everything went fine lets get latitude and longitude
//            currentLatitude = location.getLatitude();
//            currentLongitude = location.getLongitude();
//
//            JSONObject coordinates = new JSONObject();
//
//            try {
//                coordinates.put("latitude", currentLatitude);
//                coordinates.put("longitude", currentLongitude);
//                Log.i("Main", coordinates.length() + "");
//
//                socket.connect();
//                socket.emit("location", coordinates);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
////            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
////
////                @Override
////                public void call(Object... args) {
////
////                }
////
////            }).on("chat message", new Emitter.Listener() {
////
////                @Override
////                public void call(final Object... args) {
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            textView.setText((String) args[0]);
////                        }
////                    });
////
////                }
////
////            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
////
////                @Override
////                public void call(Object... args) {
////                }
////
////            });
//
//            Toast.makeText(this, currentLatitude + " " + currentLongitude, Toast.LENGTH_LONG).show();
//        }
//    }
//
//
//    @Override
//    public void onConnectionSuspended(int i) {
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//            /*
//             * Google Play services can resolve some errors it detects.
//             * If the error has a resolution, try sending an Intent to
//             * start a Google Play services activity that can resolve
//             * error.
//             */
//        if (connectionResult.hasResolution()) {
//            try {
//                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
//                    /*
//                     * Thrown if Google Play services canceled the original
//                     * PendingIntent
//                     */
//            } catch (IntentSender.SendIntentException e) {
//                // Log the error
//                e.printStackTrace();
//            }
//        } else {
//                /*
//                 * If no resolution is available, display a dialog to the
//                 * user with the error.
//                 */
//            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
//        }
//    }
//
//    /**
//     * If locationChanges change lat and long
//     *
//     * @param location
//     */
//    @Override
//    public void onLocationChanged(Location location) {
//        currentLatitude = location.getLatitude();
//        currentLongitude = location.getLongitude();
//
//        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
//    }
//
//    private void loadPermissions(String perm, int requestCode) {
//        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
//                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
//
//            }
//        } else if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) {
//            initLocationRequest();
//            Log.i("MainActivity", "Permissions granted");
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case 0: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    initLocationRequest();
//                    Log.i("MainActivity", "Permissions granted ");
//                } else {
//
//                    Log.i("MainActivity", "Permissions not granted");
//                }
//            }
//        }
//    }
//
//    public void initLocationRequest(){
//        googleApiClient = new GoogleApiClient.Builder(this)
//                // The next two lines tell the new client that “this” current class will handle connection stuff
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                //fourth line adds the LocationServices API endpoint from GooglePlayServices
//                .addApi(LocationServices.API)
//                .build();
//
//        // Create the LocationRequest object
//        locationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
//                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
//    }
//}


