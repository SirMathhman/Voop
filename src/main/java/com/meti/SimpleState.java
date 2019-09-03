package com.meti;

import com.meti.task.TaskResponse;
import com.meti.task.TaskRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SimpleState implements State {
    private final Binding<Path> compiled = new SimpleBinding<>();
    private final Set<Dependency> dependencies = new HashSet<>();
    private final Binding<Path> dependencyDirectory = new SimpleBinding<>(Paths.get(".\\lib"));
    private final TaskRunner manager;
    private final Binding<Path> metaDirectory = new SimpleBinding<>(Paths.get(".\\meta"));
    private final Set<Path> sources = new HashSet<>();
    private final Set<Path> tests = new HashSet<>();

    SimpleState(TaskRunner manager) {
        this.manager = manager;
    }

    @Override
    public Binding<Path> getCompilationDirectory() {
        return compiled;
    }

    @Override
    public Set<Dependency> getDependencies() {
        return dependencies;
    }

    @Override
    public Binding<Path> getDependencyDirectory() {
        return dependencyDirectory;
    }

    @Override
    public Binding<Path> getMetaDirectory() {
        return metaDirectory;
    }

    @Override
    public Set<Path> getSourceDirectories() {
        return sources;
    }

    @Override
    public Set<Path> getTestDirectories() {
        return tests;
    }

    @Override
    public Optional<CompletableFuture<TaskResponse>> run(State state, String line) {
        return manager.run(state, line);
    }
}
