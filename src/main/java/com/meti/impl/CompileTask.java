package com.meti.impl;

import com.meti.State;
import com.meti.task.NamedTask;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.TaskResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.meti.task.Task.complete;
import static com.meti.task.Task.completeExceptionally;

public class CompileTask implements NamedTask {
    @Override
    public String getName() {
        return "compile";
    }

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        if (state.getSourceDirectories().isEmpty()) state.run("generate source");
        if (state.getCompilationDirectory().isEmpty()) state.run("generate compile");
        return compile(state);
    }

    private CompletableFuture<TaskResponse> compile(State state) {
        try {
            compileExceptionally(state);
            return complete(new SimpleTaskResponse("Compilation performed successfully."));
        } catch (IOException e) {
            return completeExceptionally(e);
        }
    }

    private void compileExceptionally(State state) throws IOException {
        VoopCompiler compiler = new WrappedCompiler(
                state.getCompilationDirectory(),
                state.getSourceDirectories());
        compiler.compile();
        copyResources(state);
    }

    private void copyResources(State state) throws IOException {
        for (Path source : state.getSourceDirectories()) {
            Path resourceDirectory = source.resolve("resources");
            Path compileDirectory = state.getCompilationDirectory().get();
            Copier copier = new PathCopier(resourceDirectory, compileDirectory);
            copier.copy();
        }
    }
}
