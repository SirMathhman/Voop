package com.meti.task;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

class QueueSupplier implements Supplier<String> {
    private final Queue<String> args;

    QueueSupplier(Collection<String> collection) {
        this(new LinkedList<>(collection));
    }

    private QueueSupplier(Queue<String> list) {
        this.args = list;
    }

    @Override
    public String get() {
        String next = args.poll();
        if (next == null) {
            throw new IllegalArgumentException("Not enough arguments.");
        }
        return next;
    }
}
