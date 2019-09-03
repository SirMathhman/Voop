package com.meti.impl;

import com.meti.State;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.Task;
import com.meti.task.TaskResponse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

class OutTask extends PathTask {
    @Override
    CompletableFuture<TaskResponse> runImpl(State state, Supplier<String> command) {
        try {
            state.getCompiled().set(ensure(ROOT.resolve("compile")));
            return Task.complete(new SimpleTaskResponse("Generated compile directory."));
        } catch (IOException e) {
            return Task.completeExceptionally(e);
        }
    }
}
