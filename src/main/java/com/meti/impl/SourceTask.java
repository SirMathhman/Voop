package com.meti.impl;

import com.meti.State;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.Task;
import com.meti.task.TaskResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

class SourceTask extends PathTask {
    @Override
    CompletableFuture<TaskResponse> runImpl(State state) throws IOException {
        Path source = ensure(ROOT.resolve("src"), true);
        Path main = source.resolve("main");
        state.getSourceDirectories().add(ensureContent(main));
        Path metaDirectory = ensure(main.resolve("resources").resolve("META-INF"), true);
        ensure(metaDirectory.resolve("MANIFEST.MF"), false);
        state.getTests().add(ensureContent(source.resolve("test")));
        return Task.complete(new SimpleTaskResponse("Generated source directories."));
    }

    private Path ensureContent(Path path) throws IOException {
        Path content = ensure(path, true);
        ensure(content.resolve("java"), true);
        ensure(content.resolve("resources"), true);
        return path;
    }
}
