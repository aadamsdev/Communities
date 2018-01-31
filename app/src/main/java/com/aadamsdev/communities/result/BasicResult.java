package com.aadamsdev.communities.result;

/**
 * Created by andrewadams on 2017-12-27.
 */
public class BasicResult {
    public static final int CODE_SUCCESS = 0;

    public static BasicResult genericSuccess() {
        return new BasicResult(CODE_SUCCESS, "");
    }

    protected int code;
    protected String message;

    public int getCode() { return code; }
    public String getMessage() { return message; }

    public BasicResult() {
        this(0, null);
    }

    public BasicResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BasicResult(BasicResult result) {
        this(result.getCode(), result.getMessage());
    }
}
