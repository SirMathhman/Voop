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
        Path sourceDirectory = ensure(ROOT.resolve("src"), true);
        state.getSourceDirectories().add(ensureMain(sourceDirectory));
        state.getTestDirectories().add(ensureTest(sourceDirectory));
        return Task.complete(new SimpleTaskResponse("Generated directories."));
    }

    private Path ensureMain(Path source) throws IOException {
        Path main = source.resolve("main");
        ensureManifest(main);
        return ensureContent(main);
    }

    private Path ensureTest(Path source) throws IOException {
        return ensureContent(source.resolve("test"));
    }

    private void ensureManifest(Path directory) throws IOException {
        Path metaDirectory = ensure(directory.resolve("resources").resolve("META-INF"), true);
        ensure(metaDirectory.resolve("MANIFEST.MF"), false);
    }

    private Path ensureContent(Path path) throws IOException {
        Path content = ensure(path, true);
        ensure(content.resolve("java"), true);
        ensure(content.resolve("resources"), true);
        return path;
    }
}
