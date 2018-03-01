package com.aadamsdev.communities.chat;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aadamsdev.communities.R;
import com.aadamsdev.communities.pojo.ChatMessage;
import com.aadamsdev.communities.pojo.UserStatus;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andrewadams on 2018-02-27.
 */

public class UserStatusAdapter extends Adapter<UserStatusAdapter.UserStatusViewHolder> {
    private final static String TAG = UserStatusAdapter.class.getSimpleName();

    private ArrayList<UserStatus> statuses;
    private Context context;

    public UserStatusAdapter(Context context) {
        this.context = context;
        statuses = new ArrayList<>();
    }

    public UserStatusAdapter(Context context, ArrayList<UserStatus> statuses) {
        this.context = context;
        this.statuses = statuses;
    }

    @Override
    public UserStatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.user_status, parent, false);
        return new UserStatusViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserStatusViewHolder holder, int position) {
        UserStatus status = statuses.get(position);

        Log.i(TAG, "Binding view holder " + status.toString() + " " + holder.toString());

        holder.username.setText(status.getUsername());
        holder.status.setImageDrawable(status.isOnline() != null && status.isOnline() ?
                ContextCompat.getDrawable(context, R.drawable.user_status_online)
                : ContextCompat.getDrawable(context, R.drawable.user_status_offline));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    public void add(UserStatus status) {
        statuses.add(status);
    }

    public ArrayList<UserStatus> getStatuses() {
        return statuses;
    }

    public void updateStatus(UserStatus status) {
        int index = statuses.indexOf(status);
        if (index != -1) {
            UserStatus toUpdate = statuses.get(index);
            toUpdate.setIsOnline(status.isOnline());
        } else {
            add(status);
        }
    }

    class UserStatusViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.username)
        TextView username;

        @BindView(R.id.status)
        ImageView status;

        UserStatusViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "USER STATUS CLICKED");
                }
            });
        }
    }
}
