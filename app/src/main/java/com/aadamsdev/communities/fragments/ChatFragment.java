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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.aadamsdev.communities.dialogs.ProgressDialogFragment;
import com.aadamsdev.communities.dialogs.SimpleDialogFragment;
import com.aadamsdev.communities.pojo.ChatMessage;
import com.aadamsdev.communities.pojo.ChatRoom;
import com.aadamsdev.communities.request.GenericRequest;
import com.aadamsdev.communities.utils.CommunitiesUtils;
import com.aadamsdev.communities.utils.DialogUtils;
import com.aadamsdev.communities.utils.HttpHelper;
import com.aadamsdev.communities.utils.PreferenceManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatFragment extends Fragment implements View.OnClickListener, ChatClient.ChatClientCallback {

    private final String TAG = ChatFragment.class.getSimpleName();

    private final int LOCATION_PERMISSION = 100;

    private final String CHATROOM_CHANGED_DIALOG = "ChatRoomChangedDialog";
    private final String CHATROOM_HISTORY_DIALOG = "ChatHistoryChangedDialog";
    private String[] menuItems;
    private ActionBarDrawerToggle drawerToggle;

    private ChatClient chatClient;
    private ChatAdapter chatAdapter;

    private LocationManager locationManager;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.chat_recycler_view)
    RecyclerView chatRecyclerView;

    @BindView(R.id.message_field)
    EditText messageEditText;

    @BindView(R.id.send_button)
    ImageButton sendMessageButton;

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

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        chatAdapter = new ChatAdapter();

        chatClient.registerCallback(this);
        chatClient.startLocationRequests(locationManager);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        ButterKnife.bind(this, view);

        // setupDrawerSlider(view);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showRetrievingChatDialog();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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

        //TODO Add dialog and wait for callback before showing current chatrooom
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
    public void onNewMessage(ChatMessage chatMessage) {
        chatAdapter.add(chatMessage);
        scrollToBottomOfChat();
    }

    @Override
    public void onChatRoomChanged(final ChatRoom newChatRoom) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), newChatRoom.getChatRoomName(), Toast.LENGTH_SHORT).show();
            }
        });

        updateTitleWithChatRoomName(newChatRoom);

        ChatRoom currentChatRoom = PreferenceManager.getInstance(getActivity()).getLastChatRoom();
        if ((currentChatRoom != null && !currentChatRoom.getChatRoomName().equals(newChatRoom.getChatRoomName())) || !ChatClient.getInstance().isInChatRoom()) {
            PreferenceManager.getInstance(getActivity()).setLastKnownChatRoom(newChatRoom);
            showChatRoomChangedDialog(newChatRoom);
            clearChat();
            setChatHistory(newChatRoom.getMessages());
            scrollToBottomOfChat();
        }
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

        String currentUsername = PreferenceManager.getInstance(getActivity()).getCurrentUser();

        for (int i = 0; i < 10; ++i) {
            ChatRoom currentChatRoom = PreferenceManager.getInstance(getActivity()).getLastChatRoom();
            if (currentChatRoom != null) {
                chatClient.sendMessage(currentUsername, message + i, currentChatRoom.getChatRoomName());
            } else {
                chatClient.sendMessage(currentUsername, message, null);
            }
        }

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

    private void updateTitleWithChatRoomName(final ChatRoom chatRoom) {
        final ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    supportActionBar.setTitle(chatRoom.getChatRoomName());
                }
            });
        }
    }

    private void scrollToBottomOfChat() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });
    }

    private void clearChat() {
        int size = chatAdapter.getItemCount();
        chatAdapter.getMessages().clear();
        chatAdapter.notifyItemRangeRemoved(0, size);
    }

    private void setChatHistory(List<ChatMessage> messages) {
        chatAdapter.getMessages().addAll(messages);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatAdapter.notifyDataSetChanged();
            }
        });

    }

    private void showChatRoomChangedDialog(ChatRoom chatRoom) {
        String title = getString(R.string.chat_room_change, chatRoom.getChatRoomName());
        String message = getString(R.string.chat_room_change_message, chatRoom.getChatRoomName());

        SimpleDialogFragment dialogFragment = SimpleDialogFragment.newInstance(title, message);
        DialogUtils.show(this, dialogFragment, CHATROOM_CHANGED_DIALOG);
    }

    private void showRetrievingChatDialog() {
        String title = "Test";
        final String message = "Test Message";

        final ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(title);
        DialogUtils.show(this, dialogFragment, CHATROOM_HISTORY_DIALOG);

        ChatMessage firstMessage = chatAdapter.getFirstMessage();
        ChatRoom currentChatRoom = PreferenceManager.getInstance(getActivity()).getLastChatRoom();

        if (currentChatRoom != null && firstMessage != null) {
            String url;
            if (CommunitiesUtils.isEmulator()) {
                url = getString(R.string.host_url_emulator);
            } else {
                url = getString(R.string.host_url);
            }

            url += "/chatHistory/page?chatRoomName=" + currentChatRoom.getChatRoomName() + "&lastMessageInChat=" + firstMessage.getId();

            final GenericRequest<ChatMessage[]> request = new GenericRequest<>(Request.Method.GET, url, new HashMap<String, Object>(), ChatMessage[].class, new Response.Listener<ChatMessage[]>() {
                @Override
                public void onResponse(ChatMessage[] response) {
                    LinkedList<ChatMessage> messages = new LinkedList<>(Arrays.asList(response));
                    chatAdapter.getMessages().addAll(0, messages);
                    dialogFragment.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialogFragment.dismiss();
                }
            });

            HttpHelper.getInstance(getActivity()).getRequestQueue().add(request);
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