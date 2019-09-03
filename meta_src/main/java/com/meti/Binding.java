package com.meti;

public interface Binding<V> {
    V get();

    boolean isEmpty();

    boolean isPresent();

    V set(V other);
}
