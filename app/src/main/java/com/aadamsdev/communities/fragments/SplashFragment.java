package com.aadamsdev.communities.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aadamsdev.communities.R;
import com.aadamsdev.communities.activities.ChatActivity;
import com.aadamsdev.communities.utils.PreferenceManager;

import java.util.ArrayList;

import butterknife.BindView;

import static android.content.SharedPreferences.*;

/**
 * Created by Andrew Adams on 6/10/2017.
 */
public class SplashFragment extends Fragment {

    private PreferenceManager preferenceManager;

    @BindView(R.id.username_field)
    EditText usernameField;

    @BindView(R.id.password_field)
    EditText passwordField;

    @BindView(R.id.login_button)
    Button loginButton;

    @BindView(R.id.register_button)
    Button registerButton;

    @BindView(R.id.forgot_password_button)
    Button forgotPasswordButton;

    public static SplashFragment newFragment() {
        return new SplashFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideActionBar();
        // Checking for first time launch - before calling setContentView()
//        preferenceManager = PreferenceManager.getInstance(getActivity());
//        //TODO: Remove comment to skip splash screen
//        if (!preferenceManager.isFirstTimeLaunch()) {
//            launchChatActivity();
//        } else {
//            preferenceManager.setFirstTimeLaunch(false);
//        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash_fragment, container, false);

        return view;
    }

    private void hideActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void launchChatActivity() {
        Intent intent = ChatActivity.newIntent(getContext());
        getActivity().startActivity(intent);
    }
}