package com.aadamsdev.communities.pojo;

/**
 * Created by andrewadams on 2018-02-27.
 */

public class UserStatus {

    private String username;
    private Boolean isOnline;

    public UserStatus(String username, Boolean isOnline) {
        this.username = username;
        this.isOnline = isOnline;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean isOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "username='" + username + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserStatus)) {
            return false;
        } else {
            UserStatus toCompare = (UserStatus) obj;
            return toCompare.getUsername().equalsIgnoreCase(username);
        }
    }
}
