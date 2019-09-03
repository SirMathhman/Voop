package com.meti.impl;

import com.meti.State;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.Task;
import com.meti.task.TaskResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

class SourceTask extends PathTask {
    @Override
    CompletableFuture<TaskResponse> runImpl(State state, Supplier<String> command) throws IOException {
        Path source = ensure(ROOT.resolve("src"));
        state.getSources().add(ensureContent(source.resolve("main")));
        state.getTests().add(ensureContent(source.resolve("test")));
        return Task.complete(new SimpleTaskResponse("Generated source directories."));
    }

    private Path ensureContent(Path path) throws IOException {
        Path content = ensure(path);
        ensure(content.resolve("java"));
        ensure(content.resolve("resources"));
        return path;
    }
}
