package com.aadamsdev.communities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.aadamsdev.communities.R;

/**
 * Created by Andrew Adams on 6/10/2017.
 */
public class SplashFragment extends Fragment implements View.OnClickListener{

    private View view;

    private Button loginButton;
    private Button signupButton;

    private LoginFragment loginFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.splash_fragment, container, false);

        loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

        signupButton = (Button) view.findViewById(R.id.signup_button);
        signupButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.login_button):
                Toast.makeText(getContext(), "Logging in..", Toast.LENGTH_SHORT).show();

                loginFragment = new LoginFragment();

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.activity_main, loginFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;

            case (R.id.signup_button):
                Toast.makeText(getContext(), "Sign up..", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}