package com.meti.task;

public interface TaskManager extends TaskRunner {
    void add(NamedTask task);
}