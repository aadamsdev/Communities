package com.aadamsdev.communities.fragments;

/**
 * Created by Andrew Adams on 6/18/2017.
 */

import com.aadamsdev.communities.chat.ChatArrayAdapter;
import com.aadamsdev.communities.chat.ChatClient;
import com.aadamsdev.communities.chat.ChatMessage;
import com.aadamsdev.communities.R;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainFragment extends Fragment implements View.OnClickListener, ChatClient.ChatClientCallback {

    private final String DEBUG_TAG = "MainFragment";
    int count = 0;

    private View view;

    private ChatArrayAdapter chatArrayAdapter;

    private ListView chatListView;

    private EditText messageEditText;
    private ImageButton sendMessageButton;

    private String[] menuItems;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private ChatClient chatClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        chatClient = ChatClient.getInstance();
        chatClient.connect();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fragment, container, false);

        setupDrawerSlider(view);

        chatListView = (ListView) view.findViewById(R.id.chat_scrollview);
        chatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        chatArrayAdapter = new ChatArrayAdapter(getContext());
        chatListView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                chatListView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        sendMessageButton = (ImageButton) view.findViewById(R.id.send_button);
        sendMessageButton.setOnClickListener(this);

        messageEditText = (EditText) view.findViewById(R.id.message_field);
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

        chatClient.registerCallback(this);

        return view;
    }

    @Override
    public void onNewMessage(String username, String message, String timestamp, int userIconId) {
        ChatMessage chatMessage = new ChatMessage(getContext(), "Andrew", message + " " + count, timestamp, null);
        ++count;

        chatArrayAdapter.add(chatMessage);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.send_button):
                String message = messageEditText.getText().toString();
                messageEditText.getText().clear();

                Log.i("MainFragment", message);
                chatClient.sendMessage("Andrew", message);
                break;
        }
    }

    private void setupDrawerSlider(View view) {
        menuItems = getResources().getStringArray(R.array.menu_items);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        drawerList = (ListView) view.findViewById(R.id.left_drawer);

        drawerToggle = new ActionBarDrawerToggle(
                getActivity(),                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.addDrawerListener(drawerToggle);

//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<>(getContext(), R.layout.drawer_list_item, menuItems));
        // Set the list's click listener
//        drawerList.setOnItemClickListener(new DrawerItemClickListener());
    }


}