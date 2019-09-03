package com.meti.impl;

import com.meti.State;
import com.meti.task.NamedTask;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.Task;
import com.meti.task.TaskResponse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class RunTask implements NamedTask {
    @Override
    public String getName() {
        return "run";
    }

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        if (state.getCompilationDirectory().isEmpty()) state.run(state, "compile");

        try {
            String mainClass = command.get();
            Process process = new ProcessBuilder("java", mainClass)
                    .inheritIO()
                    .directory(state.getCompilationDirectory().get().toFile())
                    .start();
            process.waitFor();
            return Task.complete(new SimpleTaskResponse("Successfully ran program."));
        } catch (IOException | InterruptedException e) {
            return Task.completeExceptionally(e);
        }
    }
}
