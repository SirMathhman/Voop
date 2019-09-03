package com.meti.impl;

import com.meti.State;
import com.meti.task.NamedTask;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.Task;
import com.meti.task.TaskResponse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PackageTask implements NamedTask {
    @Override
    public String getName() {
        return "package";
    }

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        state.run("compile");

        try {
            Process process = new ProcessBuilder("jar", "cmf", "META-INF/MANIFEST.MF", command.get(), "*")
                    .directory(state.getCompiled().get().toFile())
                    .inheritIO()
                    .start();
            process.waitFor();
            return Task.complete(new SimpleTaskResponse("Built JAR."));
        } catch (IOException | InterruptedException e) {
            return Task.completeExceptionally(e);
        }
    }
}
