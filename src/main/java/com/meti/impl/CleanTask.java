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
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CleanTask implements NamedTask {
    private void delete(Path compileDirectory) throws IOException {
        if(Files.isDirectory(compileDirectory)){
            Set<Path> children = Files.list(compileDirectory)
                    .collect(Collectors.toSet());
            deleteChildren(children);
        }
        Files.delete(compileDirectory);
    }

    private void deleteChildren(Set<Path> children) throws IOException {
        for (Path path : children) {
            delete(path);
        }
    }

    @Override
    public String getName() {
        return "clean";
    }

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        Binding<Path> compiled = state.getCompilationDirectory();
        if (compiled.isEmpty()) state.run(state, "compile");
        try {
            delete(compiled.get());
            return Task.complete(new SimpleTaskResponse("Cleaned directory."));
        } catch (IOException e) {
            return Task.completeExceptionally(e);
        }
    }
}
