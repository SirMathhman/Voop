package com.meti.impl;

import com.meti.State;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.Task;
import com.meti.task.TaskResponse;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class MetaTask implements Task {
    private static final String TEMPLATE_CONTENT = "import com.meti.State;\n" +
            "import com.meti.impl.Script;\n" +
            "class Build implements Script {\n" +
            "@Override\n" +
            "public void build(State state) {\n" +
            "}}";

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        try {
            Path metaDirectory = state.getMetaDirectory().get();
            if (!Files.exists(metaDirectory)) {
                Files.createDirectory(metaDirectory);
            }
            Path buildFile = metaDirectory.resolve("Build.java");
            if (!Files.exists(buildFile)) {
                Files.createFile(buildFile);
                BufferedWriter bufferedWriter = Files.newBufferedWriter(buildFile);
                try (PrintWriter writer = new PrintWriter(bufferedWriter)) {
                    writer.print(TEMPLATE_CONTENT);
                    writer.flush();
                }
            }
            return Task.complete(new SimpleTaskResponse("Generated meta directory."));
        } catch (IOException e) {
            return Task.completeExceptionally(e);
        }
    }
}
