package com.aadamsdev.communities.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.aadamsdev.communities.R;
import com.aadamsdev.communities.fragments.ChatFragment;

/**
 * Created by Andrew Adams on 1/7/2018.
 */

public class ChatActivity extends AppCompatActivity {

    private ChatFragment chatFragment;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ChatActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        chatFragment = ChatFragment.newFragment();
        launchFragment(chatFragment);
    }

    private void launchFragment(ChatFragment chatFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.activity_main, chatFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
