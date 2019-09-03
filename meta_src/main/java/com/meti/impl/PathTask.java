package com.meti.impl;

import com.meti.State;
import com.meti.task.Task;
import com.meti.task.TaskResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class PathTask implements Task {
    static final Path ROOT = Paths.get(".\\");

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        try {
            ensure(ROOT);
            return runImpl(state, command);
        } catch (IOException e) {
            return Task.completeExceptionally(e);
        }
    }

    Path ensure(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        return path;
    }

    abstract CompletableFuture<TaskResponse> runImpl(State state, Supplier<String> command) throws IOException;
}
