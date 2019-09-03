package com.meti.task;

import com.meti.State;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class SetTaskManager implements TaskManager {
    private final Set<NamedTask> tasks = new HashSet<>();

    @Override
    public void add(NamedTask task) {
        tasks.add(task);
    }

    @Override
    public Optional<CompletableFuture<TaskResponse>> run(State state, String line) {
        List<String> args = List.of(line.split(" "));
        Supplier<String> subArgs = new ArgsSupplier(new LinkedList<>(args.subList(1, args.size())));
        if (args.size() == 0) {
            return Optional.empty();
        }
        return tasks.stream()
                .filter(task -> task.getName().equals(args.get(0)))
                .map(task -> task.run(state, subArgs))
                .findAny();
    }

    private static class ArgsSupplier implements Supplier<String> {
        private final Queue<String> args;

        ArgsSupplier(Queue<String> list) {
            this.args = list;
        }

        @Override
        public String get() {
            String next = args.poll();
            if(next == null){
                throw new TaskException("Not enough arguments.");
            }
            return next;
        }
    }
}
