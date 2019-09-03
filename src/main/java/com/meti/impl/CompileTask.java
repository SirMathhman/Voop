package com.meti.impl;

import com.meti.State;
import com.meti.task.NamedTask;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.Task;
import com.meti.task.TaskResponse;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CompileTask implements NamedTask {
    @Override
    public String getName() {
        return "compile";
    }

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        state.run("generate source");
        state.run("generate compile");

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        List<String> optionList = new ArrayList<>();
        optionList.add("-d");
        optionList.add(state.getCompiled().get().toAbsolutePath().toString());
        List<File> sources = new ArrayList<>();
        for (Path source : state.getSources()) {
            try {
                Files.walk(source)
                        .filter(path -> Files.isRegularFile(path))
                        .filter(path -> path.toString().endsWith(".java"))
                        .map(Path::toFile)
                        .forEach(sources::add);
            } catch (IOException e) {
                return Task.complete(new SimpleTaskResponse("Failed to walk: " + source.toAbsolutePath()));
            }
        }
        Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(sources);
        JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                fileManager,
                diagnostics,
                optionList,
                null,
                compilationUnit);
        if (!task.call()) {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                System.out.format("Error on line %d in %s%n",
                        diagnostic.getLineNumber(),
                        diagnostic.getSource().toUri());
            }
        }
        for (Path source : state.getSources()) {
            try {
                copy(source.resolve("resources"), state.getCompiled().get());
            } catch (IOException e) {
                return Task.completeExceptionally(e);
            }
        }
        return Task.complete(new SimpleTaskResponse("Compilation performed successfully."));
    }

    private void copy(Path from, Path to) throws IOException {
        if (!Files.exists(to)) {
            Files.copy(from, to);
        }

        for (Path path : Files.list(from).collect(Collectors.toSet())) {
            Path copyPath = to.resolve(path.getFileName());
            if(Files.isDirectory(path)){
                copy(path, copyPath);
            } else {
                Files.delete(copyPath);
                Files.copy(path, copyPath);
            }
        }
    }
}
