package com.aadamsdev.communities.pojo;

import android.graphics.PointF;

import java.util.List;

/**
 * Created by Andrew Adams on 1/11/2018.
 */

public class ChatRoom {

    private String chatroomName;
    private List<PointF> boundaryPoints;

    public ChatRoom(String chatroomName, List<PointF> boundaryPoints) {
        this.chatroomName = chatroomName;
        this.boundaryPoints = boundaryPoints;
    }

    public String getChatroomName() {
        return chatroomName;
    }

    public void setChatroomName(String chatroomName) {
        this.chatroomName = chatroomName;
    }

    public List<PointF> getBoundaryPoints() {
        return boundaryPoints;
    }

    public void setBoundaryPoints(List<PointF> boundaryPoints) {
        this.boundaryPoints = boundaryPoints;
    }
}
