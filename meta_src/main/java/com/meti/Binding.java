package com.meti;

public interface Binding<V> {
    V get();

    boolean isEmpty();

    void set(V other);
}
