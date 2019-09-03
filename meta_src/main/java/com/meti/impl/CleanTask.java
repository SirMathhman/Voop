package com.meti.impl;

import com.meti.Binding;
import com.meti.State;
import com.meti.task.NamedTask;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.Task;
import com.meti.task.TaskResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CleanTask implements NamedTask {
    private void delete(Path compileDirectory) throws IOException {
        for (Path path : Files.list(compileDirectory).collect(Collectors.toSet())) {
            if (Files.isDirectory(path)) {
                delete(path);
            }
            Files.delete(path);
        }
        Files.delete(compileDirectory);
    }

    @Override
    public String getName() {
        return "clean";
    }

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        Binding<Path> compiled = state.getCompiled();
        if (compiled.isEmpty()) state.run(state, "compile");
        try {
            Path compileDirectory = compiled.get();
            delete(compileDirectory);
            return Task.complete(new SimpleTaskResponse("Cleaned directory."));
        } catch (IOException e) {
            return Task.completeExceptionally(e);
        }
    }
}
