package com.aadamsdev.communities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aadamsdev.communities.fragments.SplashFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import java.util.HashMap;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {

    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private String currentChatRoom;
    private Socket socket;
    private EditText editText;
    private TextView textView;
    private Button sendButton;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private double currentLatitude;
    private double currentLongitude;

    private FragmentTransaction fragmentTransaction;
    private SplashFragment splashFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, 0);

        splashFragment = new SplashFragment();

        String test = "This is a string";
        String[] testArray = test.split(" ");

        String result = "";

//        StringBuilder stringBuilder = new StringBuilder();
//        for (String string : testArray) {
////
//            for (int i = string.length() - 1; i >= 0 ; --i) {
//                stringBuilder.append(string.charAt(i));
////                result += string.charAt(i);
//            }
////            result += " ";
//            stringBuilder.append(" ");
//        }
//
//        result = stringBuilder.toString();
//        Log.i("Main", result);

        HashMap<Integer, Integer> map = new HashMap<>();
        int[] integers = {1, 1, 1, 1, 1, 2, 2, 3, 3, 4, 4, 4, 4};

        for (int num : integers) {
            if (map.get(num) == null) {
                map.put(num, 1);
            } else {
                map.put(num, map.get(num) + 1);
            }
        }

        int mostCommonKey = 0;
        int mostCommonVal = 0;
        for (int num : map.keySet()) {
            if (map.get(num) > mostCommonVal) {
                mostCommonKey = num;
                mostCommonVal = map.get(num);
            }
        }

        Log.i("Main", mostCommonKey + "");

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.activity_main, splashFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);

            }
        } else if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) {
            Log.i("MainActivity", "Permissions granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("MainActivity", "Permissions granted ");
                } else {

                    Log.i("MainActivity", "Permissions not granted");
                }
            }
        }
    }
}


