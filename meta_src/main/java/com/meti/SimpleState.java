package com.meti;

import com.meti.task.TaskResponse;
import com.meti.task.TaskRunner;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SimpleState implements State {
    private final Binding<Path> compiled = new SimpleBinding<>();
    private final TaskRunner manager;
    private final Set<Path> sources = new HashSet<>();
    private final Set<Path> tests = new HashSet<>();

    public SimpleState(TaskRunner manager) {
        this.manager = manager;
    }

    @Override
    public Binding<Path> getCompiled() {
        return compiled;
    }

    @Override
    public Set<Path> getSources() {
        return sources;
    }

    @Override
    public Set<Path> getTests() {
        return tests;
    }

    @Override
    public Optional<CompletableFuture<TaskResponse>> run(State state, String line) {
        return manager.run(state, line);
    }
}
