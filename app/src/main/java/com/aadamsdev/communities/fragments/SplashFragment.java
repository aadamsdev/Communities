package com.aadamsdev.communities.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aadamsdev.communities.R;
import com.aadamsdev.communities.activities.ChatActivity;
import com.aadamsdev.communities.dialogs.ProgressDialogFragment;
import com.aadamsdev.communities.dialogs.SimpleDialogFragment;
import com.aadamsdev.communities.request.GenericRequest;
import com.aadamsdev.communities.result.BasicResult;
import com.aadamsdev.communities.utils.CommunitiesUtils;
import com.aadamsdev.communities.utils.DialogUtils;
import com.aadamsdev.communities.utils.HttpHelper;
import com.aadamsdev.communities.utils.PreferenceManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrew Adams on 6/10/2017.
 */
public class SplashFragment extends Fragment implements View.OnClickListener {

    private final String TAG = SplashFragment.class.getSimpleName();

    private final String SPLASH_FRAGMENT_LOADING_DIALOG = "splashFragmentLoadingDialog";
    private final String SPLASH_FRAGMENT_ERROR_DIALOG = "splashFragmentErrorDialog";

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
        ButterKnife.bind(this, view);

        loginButton.setOnClickListener(this);
        return view;
    }

    private void hideActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void launchChatActivity() {
        Intent intent = ChatActivity.newIntent(getContext());
        getActivity().startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                login();
                break;
            case R.id.register_button:
                break;
            case R.id.forgot_password_button:
                break;
        }
    }

    private void login() {
        String url;
        if (CommunitiesUtils.isEmulator()) {
            url = getString(R.string.host_url_emulator);
        } else {
            url = getString(R.string.host_url);
        }

        String username = usernameField.getText().toString().trim().toLowerCase();
        String password = passwordField.getText().toString();

        HashMap<String, Object> requestParams = new HashMap<>();
        requestParams.put("username", username);
        requestParams.put("password", password);

        final ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(getString(R.string.logging_in));
        DialogUtils.show(this, dialogFragment, SPLASH_FRAGMENT_LOADING_DIALOG);

        GenericRequest<BasicResult> request = new GenericRequest<>(Request.Method.POST, url + "/user/login", requestParams, BasicResult.class, new Response.Listener<BasicResult>() {
            @Override
            public void onResponse(BasicResult response) {
                DialogUtils.dismiss(dialogFragment, SPLASH_FRAGMENT_LOADING_DIALOG);

                if (response.isSuccessful()) {
                    launchChatActivity();
                } else {
                    SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance(getString(R.string.login_error), response.getErrorMessage());
                    DialogUtils.show(SplashFragment.this, simpleDialogFragment, SPLASH_FRAGMENT_ERROR_DIALOG);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss(dialogFragment, SPLASH_FRAGMENT_LOADING_DIALOG);

                SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance(getString(R.string.network_error), error.toString());
                DialogUtils.show(SplashFragment.this, simpleDialogFragment, SPLASH_FRAGMENT_ERROR_DIALOG);

                Log.e(TAG, error.toString());
            }
        });

        HttpHelper.getInstance(getActivity()).getRequestQueue().add(request);
    }
}