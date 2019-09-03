package com.meti.impl;

import com.meti.State;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.Task;
import com.meti.task.TaskResponse;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.util.Collections.singleton;

public class ConfigurationTask implements Task {
    private static final Path META_COMPILE = Paths.get(".\\metaCompile");

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        try {
            WrappedCompiler compiler = new WrappedCompiler(META_COMPILE, singleton(state.getMetaDirectory().get()));
            compiler.compile();

            ClassLoader loader = new URLClassLoader(new URL[]{META_COMPILE.toUri().toURL()});
            Class<?> buildClass = loader.loadClass("Build");
            Object instance = buildClass.getConstructor().newInstance();
            buildClass.getDeclaredMethod("build", State.class)
                    .invoke(instance, state);
            return Task.complete(new SimpleTaskResponse("Loaded configuration successfully."));
        } catch (Exception e) {
            return Task.completeExceptionally(e);
        }
    }
}
