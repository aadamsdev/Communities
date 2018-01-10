package com.aadamsdev.communities.fragments;

/**
 * Created by Andrew Adams on 6/18/2017.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.aadamsdev.communities.R;
import com.aadamsdev.communities.chat.ChatAdapter;
import com.aadamsdev.communities.chat.ChatClient;
import com.aadamsdev.communities.chat.ChatMessage;
import com.aadamsdev.communities.utils.CommunitiesUtils;
import com.aadamsdev.communities.utils.PreferenceManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatFragment extends Fragment implements View.OnClickListener, ChatClient.ChatClientCallback {

    private final String TAG = ChatFragment.class.getSimpleName();
    int count = 0;

    private final int LOCATION_PERMISSION = 100;

    private PreferenceManager preferenceManager;

    private String[] menuItems;
    private ActionBarDrawerToggle drawerToggle;

    private ChatClient chatClient;
    private ChatAdapter chatAdapter;

    private String currentUsername = null;

    private LocationManager locationManager;

    @BindView(R.id.chat_recycler_view)
    RecyclerView chatRecyclerView;

    @BindView(R.id.message_field)
    EditText messageEditText;

    @BindView(R.id.send_button)
    ImageButton sendMessageButton;

    private DrawerLayout drawerLayout;
    private ListView drawerList;

    public static ChatFragment newFragment() {
        return new ChatFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showActionBar();
        setSystemUiVisiblity();
        initChatClient();
        requestPermission();

        preferenceManager = PreferenceManager.getInstance(getContext());

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        chatAdapter = new ChatAdapter();
        chatClient.registerCallback(this);
        chatClient.startLocationRequests(locationManager);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        ButterKnife.bind(this, view);
//        setupDrawerSlider(view);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        sendMessageButton.setOnClickListener(this);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    sendMessageButton.setVisibility(View.GONE);
                } else {
                    sendMessageButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommunitiesUtils.checkLocationPermissions(getContext())) {
            try {
                ChatClient.getInstance().updateLocation(locationManager);
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onNewMessage(String username, String message, String timestamp, int userIconId) {
        ChatMessage chatMessage = new ChatMessage(getContext(), username, message + " " + count, timestamp, null);
        ++count;

        chatAdapter.add(chatMessage);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatAdapter.notifyItemInserted(chatAdapter.getItemCount());
            }
        });
    }

    @Override
    public void onChatRoomChanged(String chatRoomName) {
        Toast.makeText(getContext(), chatRoomName, Toast.LENGTH_SHORT).show();
        PreferenceManager.getInstance(getContext()).setLastKnownChatRoom(chatRoomName);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.send_button):
                sendMessage();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }

    private void initChatClient() {
        chatClient = ChatClient.getInstance();
        chatClient.connect();
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString();
        messageEditText.getText().clear();

        currentUsername = preferenceManager.getCurrentUser();
        chatClient.sendMessage(currentUsername, message);
    }

    private void showActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    private void setSystemUiVisiblity() {
        if (Build.VERSION.SDK_INT >= 21) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            Toast.makeText(getContext(), "Hiding action bar...", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }
    }

    //    private void setupDrawerSlider(View view) {
//        menuItems = getResources().getStringArray(R.array.menu_items);
//        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
//        drawerList = (ListView) view.findViewById(R.id.left_drawer);
//
//        drawerToggle = new ActionBarDrawerToggle(
//                getActivity(),                  /* host Activity */
//                drawerLayout,         /* DrawerLayout object */
//                R.string.drawer_open,  /* "open drawer" description */
//                R.string.drawer_close  /* "close drawer" description */
//        ) {
//
//            /** Called when a drawer has settled in a completely closed state. */
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
//                Log.i("ChatFragment", "Drawer closed");
//            }
//
//            /** Called when a drawer has settled in a completely open state. */
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                Log.i("ChatFragment", "Drawer open");
//            }
//        };
//
//        // Set the drawer toggle as the DrawerListener
//        drawerLayout.addDrawerListener(drawerToggle);
//
//        try {
//            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
//            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
//        } catch (NullPointerException ex) {
//            Log.i("ChatFragment", ex.toString());
//        }
//
//        // Set the adapter for the list view
//        drawerList.setAdapter(new ArrayAdapter<>(getContext(), R.layout.drawer_list_item, menuItems));
//        // Set the list's click listener
//        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
//    }


}