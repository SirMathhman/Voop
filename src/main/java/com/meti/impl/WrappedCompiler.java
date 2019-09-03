package com.meti.impl;

import com.meti.Binding;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.walk;
import static javax.tools.ToolProvider.getSystemJavaCompiler;

class WrappedCompiler implements VoopCompiler {
    private Binding<Path> compilationDirectory;
    private DiagnosticCollector<JavaFileObject> diagnostics;
    private StandardJavaFileManager fileManager;
    private JavaCompiler internalCompiler = getSystemJavaCompiler();
    private Set<Path> sourceDirectories;

    WrappedCompiler(Binding<Path> compilationDirectory, Set<Path> sources) {
        this.diagnostics = new DiagnosticCollector<>();
        this.fileManager = internalCompiler.getStandardFileManager(diagnostics, null, null);
        this.compilationDirectory = compilationDirectory;
        this.sourceDirectories = sources;
    }

    @Override
    public void compile() throws IOException {
        List<String> optionList = buildOptionsWithCompilationDirectory(compilationDirectory.get());
        List<File> sources = new ArrayList<>(convertSourcesExceptionally(sourceDirectories));
        Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjectsFromFiles(sources);
        JavaCompiler.CompilationTask task = buildTask(optionList, units);
        throwIfErrorsPresent(task, diagnostics);
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

    private JavaCompiler.CompilationTask buildTask(List<String> optionList,
                                                   Iterable<? extends JavaFileObject> units) {
        return internalCompiler.getTask(
                null, fileManager,
                diagnostics, optionList,
                null, units);
    }

    private void throwIfErrorsPresent(JavaCompiler.CompilationTask task,
                                      DiagnosticCollector<JavaFileObject> diagnostics) throws IOException {
        if (!task.call()) {
            throw new IOException(buildErrorMessage(diagnostics));
        }
    }

    private String buildErrorMessage(DiagnosticCollector<JavaFileObject> diagnostics) {
        return diagnostics.getDiagnostics()
                .stream()
                .map(diagnostic -> "Error on line " +
                        diagnostic.getLineNumber() + " in " +
                        diagnostic.getSource().toUri())
                .collect(Collectors.joining());
    }
}
