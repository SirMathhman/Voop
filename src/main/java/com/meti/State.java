package com.meti;

import com.meti.task.TaskRunner;

import java.nio.file.Path;
import java.util.Set;

public interface State extends TaskRunner {
    Binding<Path> getCompilationDirectory();

    Set<Path> getSourceDirectories();

    Set<Path> getTestDirectories();

    default void run(String command) {
        run(this, command);
    }
}
