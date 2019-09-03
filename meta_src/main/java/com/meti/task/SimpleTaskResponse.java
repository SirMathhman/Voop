package com.meti.task;

public class SimpleTaskResponse implements TaskResponse {
    private final String message;

    public SimpleTaskResponse(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
