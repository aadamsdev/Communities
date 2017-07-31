package com.aadamsdev.communities;

/**
 * Created by Andrew Adams on 6/24/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends BaseAdapter {

    private final String DEBUG_TAG = "ChatArrayAdapter";

    private List<ChatMessage> chatMessageList;
    private LayoutInflater inflater;

    private Context context;

    public ChatArrayAdapter(Context context) {
        this.context = context;
        chatMessageList = new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(ChatMessage object) {
        chatMessageList.add(object);
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public ChatMessage getItem(int index) {
        return chatMessageList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessage = getItem(position);

        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.chat_message, null);
        }

        ImageView iconImageView = (ImageView) view.findViewById(R.id.user_icon);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.robot);
        iconImageView.setImageBitmap(bitmap);

        TextView messageTextView = (TextView) view.findViewById(R.id.message);
        messageTextView.setText(chatMessage.getMessage().toCharArray(), 0, chatMessage.getMessage().length());

        TextView usernameTextView = (TextView) view.findViewById(R.id.username);
        usernameTextView.setText(chatMessage.getUsername().toCharArray(), 0, chatMessage.getUsername().length());

        TextView timestampTextView = (TextView) view.findViewById(R.id.timestamp);
        timestampTextView.setText(chatMessage.getTimestamp().toCharArray(), 0, chatMessage.getTimestamp().length());

        return view;
    }
}