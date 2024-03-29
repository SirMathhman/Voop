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
            ensure(ROOT, true);
            return runImpl(state);
        } catch (IOException e) {
            return Task.completeExceptionally(e);
        }
    }

    Path ensure(Path path, boolean isDirectory) throws IOException {
        if (!Files.exists(path)) {
            if(isDirectory) {
                Files.createDirectory(path);
            } else{
                Files.createFile(path);
            }
        }
        return path;
    }

    abstract CompletableFuture<TaskResponse> runImpl(State state) throws IOException;
}
