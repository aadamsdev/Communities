package com.aadamsdev.communities;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Andrew Adams on 6/22/2017.
 */

public class ChatMessage extends RelativeLayout {

    private ImageView iconImageView;
    private TextView messageTextView;
    private TextView usernameTextView;
    private TextView timestampTextView;

    private Bitmap userIcon;
    private String message;
    private String username;
    private String timestamp;

    public ChatMessage(Context context, String username, String message, String timestamp, Bitmap userIcon) {
        super(context);
        this.userIcon = userIcon;
        this.message = message;
        this.username = username;
        this.timestamp = timestamp;
        init();
    }
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//
//        Log.i("ChatMessage", "onFinishInflate");
//
//        iconImageView = (ImageView) findViewById(R.id.user_icon);
////        iconImageView.setImageBitmap(userIcon);
//
//        messageTextView = (TextView) findViewById(R.id.message);
//        messageTextView.setText(message.toCharArray(), 0, message.length());
//
//        usernameTextView = (TextView) findViewById(R.id.username);
//        usernameTextView.setText(username.toCharArray(), 0, username.length());
//
//        timestampTextView = (TextView) findViewById(R.id.timestamp);
//        timestampTextView.setText(timestamp.toCharArray(), 0, timestamp.length());
//    }

    private void init(){
        View view = inflate(getContext(), R.layout.chat_message, this);

        iconImageView = (ImageView) view.findViewById(R.id.user_icon);
        Log.i("ChatMessage", iconImageView.toString());
//        iconImageView.setImageBitmap(userIcon);

        messageTextView = (TextView) view.findViewById(R.id.message);
        messageTextView.setText(message.toCharArray(), 0, message.length());

        usernameTextView = (TextView) view.findViewById(R.id.username);
        usernameTextView.setText(username.toCharArray(), 0, username.length());

        timestampTextView = (TextView) view.findViewById(R.id.timestamp);
        timestampTextView.setText(timestamp.toCharArray(), 0, timestamp.length());
    }

    public Bitmap getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(Bitmap userIcon) {
        this.userIcon = userIcon;
        iconImageView.setImageBitmap(userIcon);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        messageTextView.setText(message.toCharArray(), 0, message.length());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        usernameTextView.setText(username.toCharArray(), 0, username.length());
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        timestampTextView.setText(timestamp.toCharArray(), 0, timestamp.length());
    }


}
