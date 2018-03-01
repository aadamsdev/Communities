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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.aadamsdev.communities.R;
import com.aadamsdev.communities.chat.ChatAdapter;
import com.aadamsdev.communities.chat.ChatClient;
import com.aadamsdev.communities.chat.UserStatusAdapter;
import com.aadamsdev.communities.dialogs.ProgressDialogFragment;
import com.aadamsdev.communities.dialogs.SimpleDialogFragment;
import com.aadamsdev.communities.pojo.ChatMessage;
import com.aadamsdev.communities.pojo.ChatRoom;
import com.aadamsdev.communities.pojo.UserStatus;
import com.aadamsdev.communities.request.GenericRequest;
import com.aadamsdev.communities.utils.CommunitiesUtils;
import com.aadamsdev.communities.utils.DialogUtils;
import com.aadamsdev.communities.utils.HttpHelper;
import com.aadamsdev.communities.utils.PreferenceManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

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

    private UserStatusAdapter userStatusAdapter;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.chat_recycler_view)
    RecyclerView chatRecyclerView;

    @BindView(R.id.status_recycler_view)
    RecyclerView statusRecyclerView;

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
        userStatusAdapter = new UserStatusAdapter(getActivity());

        chatClient.registerCallback(this);
        chatClient.startLocationRequests(locationManager);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        ButterKnife.bind(this, view);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatRecyclerView.setAdapter(chatAdapter);

        statusRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        statusRecyclerView.setAdapter(userStatusAdapter);

        setupDrawerSlider();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getChatHistoryPage();
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

        ChatRoom currentChatRoom = PreferenceManager.getInstance(getActivity()).getLastChatRoom();
        if ((currentChatRoom != null && !currentChatRoom.getChatRoomName().equals(newChatRoom.getChatRoomName())) || !chatClient.isInChatRoom()) {
            showChatRoomChangedDialog(newChatRoom);

            updateTitleWithChatRoomName(newChatRoom);
            PreferenceManager.getInstance(getActivity()).setLastKnownChatRoom(newChatRoom);

            clearChat();
            setChatHistory(newChatRoom.getMessages());
            scrollToBottomOfChat();


//            for (int i = 0; i < 15; ++i) {
//                onUserStatusesUpdated(new UserStatus("sdlkjflsdjfsldkfj " + i, i % 2 == 0));
//            }
        }
    }

    @Override
    public void onUserStatusesUpdated(UserStatus status) {
        userStatusAdapter.add(status);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userStatusAdapter.notifyDataSetChanged();
            }
        });
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
        String username = PreferenceManager.getInstance(getActivity()).getCurrentUser();
        chatClient = ChatClient.newInstance(username);
        chatClient.connect();
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString();
        messageEditText.getText().clear();

        ChatRoom currentChatRoom = PreferenceManager.getInstance(getActivity()).getLastChatRoom();
        if (currentChatRoom != null) {
            chatClient.sendMessage(message, currentChatRoom.getChatRoomName());
        } else {
            chatClient.sendMessage(message, null);
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
        chatAdapter.getMessages().clear();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int size = chatAdapter.getItemCount();
                chatAdapter.notifyItemRangeRemoved(0, size);
            }
        });
    }

    private void setChatHistory(LinkedList<ChatMessage> messages) {
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

    private void getChatHistoryPage() {
        ChatMessage firstMessage = chatAdapter.getFirstMessage();
        ChatRoom currentChatRoom = PreferenceManager.getInstance(getActivity()).getLastChatRoom();

        if (currentChatRoom != null && firstMessage != null) {
            final ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(getString(R.string.loading_chat_history));
            DialogUtils.show(this, dialogFragment, CHATROOM_HISTORY_DIALOG);

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
                    if (response.length > 0) {
                        final LinkedList<ChatMessage> messages = new LinkedList<>(Arrays.asList(response));
                        chatAdapter.getMessages().addAll(0, messages);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatAdapter.notifyItemRangeInserted(0, messages.size());
                            }
                        });
                    }
                    dialogFragment.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialogFragment.dismiss();
                    //TODO Add error dialog for chat history
                }
            });

            HttpHelper.getInstance(getActivity()).getRequestQueue().add(request);
        }
    }


    private void setupDrawerSlider() {
        drawerToggle = new ActionBarDrawerToggle(
                getActivity(),                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                showActionBar();
                Log.i("ChatFragment", "Drawer closed");
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.addDrawerListener(drawerToggle);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }
    }
}