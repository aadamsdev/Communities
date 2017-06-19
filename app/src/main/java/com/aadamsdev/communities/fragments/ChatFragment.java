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
import android.widget.Toast;

import com.aadamsdev.communities.R;

/**
 * Created by Andrew Adams on 6/18/2017.
 */

public class ChatFragment extends Fragment {

    private View view;

    private String email, password;

    private EditText usernameField;
    private EditText passwordField;

    private Button loginButton;

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.chat_fragment, container, false);

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
}