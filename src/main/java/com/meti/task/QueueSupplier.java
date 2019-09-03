package com.meti.task;

import java.util.Queue;
import java.util.function.Supplier;

class QueueSupplier implements Supplier<String> {
    private final Queue<String> args;

    QueueSupplier(Queue<String> list) {
        this.args = list;
    }

    @Override
    public String get() {
        String next = args.poll();
        if(next == null){
            throw new IllegalArgumentException("Not enough arguments.");
        }
        return next;
    }
}
