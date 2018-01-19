package com.aadamsdev.communities.pojo;

import java.util.List;

/**
 * Created by Andrew Adams on 1/11/2018.
 */

public class ChatRoom {

    private String chatRoomName;
    private List<ChatMessage> messages;

    public ChatRoom(String chatRoomName, List<ChatMessage> messages) {
        this.chatRoomName = chatRoomName;
        this.messages = messages;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}
