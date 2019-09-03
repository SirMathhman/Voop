package com.meti.task;

import com.meti.State;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        if (line.isBlank()) return Optional.empty();
        List<String> args = List.of(line.split(" "));
        Supplier<String> argumentSupplier = buildArgumentSupplier(args);
        return evaluate(state, args.get(0), argumentSupplier);
    }

    private Supplier<String> buildArgumentSupplier(List<String> args) {
        List<String> subArgs = args.subList(1, args.size());
        return new QueueSupplier(subArgs);
    }

    private Optional<CompletableFuture<TaskResponse>> evaluate(State state, String name, Supplier<String> supplier) {
        return tasks.stream()
                .filter(task -> task.getName().equals(name))
                .map(task -> task.run(state, supplier))
                .findAny();
    }
}
