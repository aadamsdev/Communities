package com.aadamsdev.communities.fragments;

/**
 * Created by Andrew Adams on 6/18/2017.
 */

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.aadamsdev.communities.ChatArrayAdapter;
import com.aadamsdev.communities.ChatMessage;
import com.aadamsdev.communities.R;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private final String DEBUG_TAG = "ChatFragment";
    int count = 0;

    private View view;

    private ChatArrayAdapter chatArrayAdapter;

    private ListView chatListView;

    private EditText messageEditText;
    private ImageButton sendMessageButton;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chat_fragment, container, false);

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

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.send_button):
                Toast.makeText(getContext(), "Adding message", Toast.LENGTH_SHORT).show();

                ChatMessage chatMessage = new ChatMessage(getContext(), "Andrew", "Hello world " + count, "6:23 PM", null);
                ++count;

                chatArrayAdapter.add(chatMessage);
                chatArrayAdapter.notifyDataSetChanged();

                break;
        }
    }
}