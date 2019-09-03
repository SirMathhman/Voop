package com.meti.task;

public class TaskException extends RuntimeException {
    public TaskException() {
    }

    public TaskException(String message) {
        super(message);
    }
}
