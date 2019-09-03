package com.meti.impl;

import com.meti.State;
import com.meti.task.NamedTask;
import com.meti.task.SimpleTaskResponse;
import com.meti.task.TaskResponse;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.meti.task.Task.complete;
import static com.meti.task.Task.completeExceptionally;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.walk;

public class CompileTask implements NamedTask {
    @Override
    public String getName() {
        return "compile";
    }

    @Override
    public CompletableFuture<TaskResponse> run(State state, Supplier<String> command) {
        if (state.getSources().isEmpty()) state.run("generate source");
        if (state.getCompiled().isEmpty()) state.run("generate compile");
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
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        List<String> optionList = buildOptionsWithCompilationDirectory(state.getCompiled().get());
        List<File> sources = new ArrayList<>(convertSourcesExceptionally(state.getSources()));
        Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(sources);
        JavaCompiler.CompilationTask task = compiler.getTask(
                null, fileManager,
                diagnostics, optionList,
                null, compilationUnit);
        if (!task.call()) {
            throw new IOException(listErrors(diagnostics));
        }
        copyResources(state);
    }

    private List<String> buildOptionsWithCompilationDirectory(Path compilationDirectory) {
        List<String> options = new ArrayList<>();
        options.add("-d");
        options.add(compilationDirectory.toAbsolutePath().toString());
        return options;
    }

    private List<File> convertSourcesExceptionally(Set<Path> sourcePaths) throws IOException {
        List<File> sourcesFiles = new ArrayList<>();
        for (Path source : sourcePaths) {
            walk(source).filter(path -> isRegularFile(path))
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .forEach(sourcesFiles::add);
        }
        return sourcesFiles;
    }

    private String listErrors(DiagnosticCollector<JavaFileObject> diagnostics) {
        return diagnostics.getDiagnostics()
                .stream()
                .map(diagnostic -> "Error on line " +
                        diagnostic.getLineNumber() + " in " +
                        diagnostic.getSource().toUri())
                .collect(Collectors.joining());
    }

    private void copyResources(State state) throws IOException {
        for (Path source : state.getSources()) {
            Path resourceDirectory = source.resolve("resources");
            Path compileDirectory = state.getCompiled().get();
            Copier copier = new PathCopier(resourceDirectory, compileDirectory);
            copier.copy();
        }
    }
}
