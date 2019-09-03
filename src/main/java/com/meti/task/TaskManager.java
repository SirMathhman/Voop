package com.meti.task;

public interface TaskManager extends TaskRunner {
    TaskManager add(NamedTask task);
}