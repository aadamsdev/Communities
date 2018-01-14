package com.aadamsdev.communities.pojo;

/**
 * Created by Andrew Adams on 1/11/2018.
 */

public class ChatRoom {

    private String chatRoomName;

    public ChatRoom(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getChatroomName() {
        return chatRoomName;
    }

    public void setChatroomName(String chatroomName) {
        this.chatRoomName = chatroomName;
    }
}
