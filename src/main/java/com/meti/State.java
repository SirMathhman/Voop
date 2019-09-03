package com.meti;

import com.meti.task.TaskResponse;
import com.meti.task.TaskRunner;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface State extends TaskRunner {
    Binding<Path> getCompiled();

    Set<Path> getSources();

    Set<Path> getTests();

    default Optional<CompletableFuture<TaskResponse>> run(String command) {
        return run(this, command);
    }
}
