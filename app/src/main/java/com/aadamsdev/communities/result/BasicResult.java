package com.aadamsdev.communities.result;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andrewadams on 2017-12-27.
 */
public class BasicResult {
    @SerializedName("success")
    private Boolean isSuccessful;

    private String errorMessage;

    public BasicResult(Boolean isSuccessful, String message) {
        this.isSuccessful = isSuccessful;
        this.errorMessage = message;
    }

    public Boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(Boolean successful) {
        isSuccessful = successful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String message) {
        this.errorMessage = message;
    }

    @Override
    public String toString() {
        return "BasicResult{" +
                "isSuccessful=" + isSuccessful +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
