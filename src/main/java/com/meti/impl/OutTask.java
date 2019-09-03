package com.meti.impl;

import com.meti.State;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.Task;
import com.meti.task.TaskResponse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

class OutTask extends PathTask {
    @Override
    CompletableFuture<TaskResponse> runImpl(State state) {
        try {
            state.getCompilationDirectory().set(ensure(ROOT.resolve("compile"), true));
            return Task.complete(new SimpleTaskResponse("Generated compile directory."));
        } catch (IOException e) {
            return Task.completeExceptionally(e);
        }
    }
}
