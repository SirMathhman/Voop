package com.meti;

import com.meti.task.TaskRunner;

import java.nio.file.Path;
import java.util.Set;

public interface State extends TaskRunner {
    Binding<Path> getCompiled();

    Set<Path> getSources();

    Set<Path> getTests();

    default void run(String command) {
        run(this, command);
    }
}
