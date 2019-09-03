package com.meti.impl;

import com.meti.Dependency;
import com.meti.State;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.Task;
import com.meti.task.TaskResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DependencyTask implements Task {
    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        try {
            StringBuilder builder = runExceptionally(state);
            return Task.complete(new SimpleTaskResponse(builder.toString()));
        } catch (Exception e) {
            return Task.completeExceptionally(e);
        }
    }

    private StringBuilder runExceptionally(State state) throws IOException {
        Path libs = state.getDependencyDirectory().get();
        if (!Files.exists(libs)) Files.createDirectory(libs);

        StringBuilder builder = new StringBuilder();
        Set<Dependency> dependencies = state.getDependencies();
        builder.append("Located ")
                .append(dependencies.size())
                .append(" dependencies:");
        for (Dependency dependency : dependencies) {
            String urlString = dependency.getURLString();
            builder.append("\t\n").append(urlString);
            saveDependency(dependency.formatPath(libs), urlString);
        }
        return builder;
    }

    private void saveDependency(Path libPath, String urlString) throws IOException {
        URL url = new URL(urlString);
        InputStream pathIn = url.openStream();
        saveTo(pathIn, libPath);
        pathIn.close();
    }

    private void saveTo(InputStream pathIn, Path libPath) throws IOException {
        if (!Files.exists(libPath.getParent())) Files.createDirectories(libPath.getParent());
        if (!Files.exists(libPath)) Files.createFile(libPath);
        OutputStream pathOut = Files.newOutputStream(libPath);
        pathIn.transferTo(pathOut);
        pathOut.flush();
        pathOut.close();
    }
}
