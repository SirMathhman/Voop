package com.meti.impl;

import com.meti.State;
import com.meti.task.NamedTask;
import com.meti.task.TaskResponse;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ExitTask implements NamedTask {
    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        CompletableFuture<TaskResponse> future = new CompletableFuture<>();
        future.complete(new ExitTaskResponse());
        return future;
    }

    private static class ExitTaskResponse implements TaskResponse {
        @Override
        public String getMessage() {
            return "Exiting build system.";
        }

        @Override
        public boolean shouldTerminate() {
            return true;
        }
    }
}
