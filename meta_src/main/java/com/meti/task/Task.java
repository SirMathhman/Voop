package com.meti.task;

import com.meti.State;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface Task {
    static CompletableFuture<TaskResponse> complete(TaskResponse response) {
        CompletableFuture<TaskResponse> future = new CompletableFuture<>();
        future.complete(response);
        return future;
    }

    static CompletableFuture<TaskResponse> completeExceptionally(Exception exception) {
        CompletableFuture<TaskResponse> future = new CompletableFuture<>();
        future.completeExceptionally(exception);
        return future;
    }

    CompletableFuture<TaskResponse> run(State state, Supplier<String> command);
}
