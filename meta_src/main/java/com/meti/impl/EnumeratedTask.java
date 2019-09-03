package com.meti.impl;

import com.meti.State;
import com.meti.task.NamedTask;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.Task;
import com.meti.task.TaskResponse;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class EnumeratedTask implements NamedTask {
    private Map<GenerateType, Task> tasks;

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        lazyTasks();
        return runImpl(state, command);
    }

    private void lazyTasks() {
        if (tasks == null || tasks.isEmpty()) {
            tasks = new EnumMap<>(getEnumClass());
            putTasks(tasks);
        }
    }

    private CompletableFuture<TaskResponse> runImpl(State state, Supplier<String> command) {
        String typeString = command.get();
        try {
            return parseType(state, command, GenerateType.valueOf(typeString.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Task.complete(new SimpleTaskResponse("Unknown type: " + typeString));
        }
    }

    protected abstract Class<GenerateType> getEnumClass();

    protected abstract void putTasks(Map<GenerateType, Task> tasks);

    private CompletableFuture<TaskResponse> parseType(State state, Supplier<String> command,
                                                      GenerateType type) {
        return tasks.containsKey(type) ?
                tasks.get(type).run(state, command) :
                Task.completeExceptionally(new IllegalArgumentException("Unsupported type: " + type.name()));
    }
}
