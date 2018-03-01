package com.aadamsdev.communities.pojo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andrew Adams on 1/11/2018.
 */

public class ChatRoom {

    private String chatRoomName;
    private LinkedList<ChatMessage> messages;
    private ArrayList<UserStatus> statuses;

    public ChatRoom(String chatRoomName, LinkedList<ChatMessage> messages, ArrayList<UserStatus> statuses) {
        this.chatRoomName = chatRoomName;
        this.messages = messages;
        this.statuses = statuses;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public LinkedList<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(LinkedList<ChatMessage> messages) {
        this.messages = messages;
    }

    public ArrayList<UserStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<UserStatus> statuses) {
        this.statuses = statuses;
    }
}
