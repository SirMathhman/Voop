package com.meti.task;

import com.meti.State;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class SetTaskManager implements TaskManager {
    private final Set<NamedTask> tasks = new HashSet<>();

    @Override
    public TaskManager add(NamedTask task) {
        tasks.add(task);
        return this;
    }

    @Override
    public Optional<CompletableFuture<TaskResponse>> run(State state, String line) {
        List<String> args = List.of(line.split(" "));
        Supplier<String> subArgs = new QueueSupplier(new LinkedList<>(args.subList(1, args.size())));
        if (args.size() == 0) {
            return Optional.empty();
        }
        return tasks.stream()
                .filter(task -> task.getName().equals(args.get(0)))
                .map(task -> task.run(state, subArgs))
                .findAny();
    }
}
