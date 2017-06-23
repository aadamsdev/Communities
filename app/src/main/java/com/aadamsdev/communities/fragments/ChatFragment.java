package com.aadamsdev.communities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.aadamsdev.communities.ChatMessage;
import com.aadamsdev.communities.R;

/**
 * Created by Andrew Adams on 6/18/2017.
 */

public class ChatFragment extends Fragment implements View.OnClickListener{

    private View view;

    private ScrollView chatScrollView;
    private LinearLayout chatLayout;
    private ImageButton sendMessageButton;

    private ChatMessage previousChatMessage;

    private Context context;

    int count = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.chat_fragment, container, false);

        chatScrollView = (ScrollView) view.findViewById(R.id.chat_scrollview);
        chatLayout = (LinearLayout) view.findViewById(R.id.chat_layout);

        sendMessageButton = (ImageButton) view.findViewById(R.id.send_button);
        sendMessageButton.setOnClickListener(this);

//        Button loginButton = (Button) view.findViewById(R.id.btn_login);
//
//        usernameField = (EditText) view.findViewById(R.id.emailTextInputLayout);
//        passwordField = (EditText) view.findViewById(R.id.passwordTextInputLayout);
//
//        usernameField.setText("TestAccount");
//        passwordField.setText("1234!");
//
//        loginButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.send_button):
                Toast.makeText(getContext(), "Adding message", Toast.LENGTH_SHORT).show();

                ++count;
                ChatMessage chatMessage = new ChatMessage(getContext(), "Andrew", "Hello world " + count, "6:23 PM", null);
                chatMessage.setId(View.generateViewId());
                if (previousChatMessage != null) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, previousChatMessage.getId());
                    chatMessage.setLayoutParams(params);
                }
                chatLayout.addView(chatMessage);

                chatScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        chatScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });

                previousChatMessage = chatMessage;

                break;
        }
    }
}