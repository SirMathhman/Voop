package com.meti.impl;

import com.meti.State;
import com.meti.task.NamedTask;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.TaskResponse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.meti.task.Task.complete;
import static com.meti.task.Task.completeExceptionally;

public class RunTask implements NamedTask {
    @Override
    public String getName() {
        return "run";
    }

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        try {
            return runExceptionally(state, command);
        } catch (IOException | InterruptedException e) {
            return completeExceptionally(e);
        }
    }

    private CompletableFuture<TaskResponse> runExceptionally(State state, Supplier<String> command) throws IOException, InterruptedException {
        if (state.getCompilationDirectory().isEmpty()) state.run(state, "compile");
        String mainClass = command.get();
        buildRunProcess(state, mainClass).waitFor();
        return complete(new SimpleTaskResponse("Successfully ran program."));
    }

    private Process buildRunProcess(State state, String mainClass) throws IOException {
        return new ProcessBuilder("java", mainClass)
                .inheritIO()
                .directory(state.getCompilationDirectory().get().toFile())
                .start();
    }
}
