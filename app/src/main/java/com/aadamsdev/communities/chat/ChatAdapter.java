package com.aadamsdev.communities.chat;

/**
 * Created by Andrew Adams on 6/24/2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aadamsdev.communities.R;
import com.aadamsdev.communities.pojo.ChatMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final String TAG = ChatAdapter.class.getSimpleName();

    private DateFormat dateFormat;
    private List<ChatMessage> messages;

    public ChatAdapter() {
        messages = new LinkedList<>();
        init();
    }

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
        init();
    }

    private void init() {
        setHasStableIds(true);
        dateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
    }

    public void add(ChatMessage object) {
        messages.add(object);
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.chat_message, parent, false);
        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        holder.timestampField.setText(createTimestampText(message.getTimestamp()));
        holder.usernameField.setText(message.getUsername());
        holder.messageField.setText(message.getMessage());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private String createTimestampText(Date date) {
        return dateFormat.format(date);
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public ChatMessage getFirstMessage() {
        if (messages != null && messages.size() > 0) {
            return messages.get(0);
        } else {
            return null;
        }
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.message)
        TextView messageField;

        @BindView(R.id.username)
        TextView usernameField;

        @BindView(R.id.timestamp)
        TextView timestampField;

        ChatViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}