package com.meti.task;

import com.meti.State;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TaskRunner {
    Optional<CompletableFuture<TaskResponse>> run(State state, String line);
}
