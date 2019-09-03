package com.meti.impl;

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

public class PackageTask implements NamedTask {
    @Override
    public String getName() {
        return "package";
    }

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        state.run("compile");

        try {
            Path compilationDirectory = state.getCompilationDirectory().get();
            removePreviousJARs(compilationDirectory);
            packageExceptionally(compilationDirectory, command.get());
            return Task.complete(new SimpleTaskResponse("Built JAR."));
        } catch (IOException | InterruptedException e) {
            return Task.completeExceptionally(e);
        }
    }

    private void removePreviousJARs(Path compilationDirectory) throws IOException {
        Set<Path> jarFiles = Files.list(compilationDirectory)
                .filter(path -> path.endsWith(".jar"))
                .collect(Collectors.toSet());
        for (Path jarFile : jarFiles) {
            Files.delete(jarFile);
        }
    }

    private void packageExceptionally(Path compilationDirectory, String fileName) throws IOException,
            InterruptedException {
        new ProcessBuilder("jar", "cmf", "META-INF/MANIFEST.MF", fileName, "*")
                .directory(compilationDirectory.toFile())
                .inheritIO()
                .start()
                .waitFor();
    }
}
