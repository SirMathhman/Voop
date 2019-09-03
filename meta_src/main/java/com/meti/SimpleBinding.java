package com.meti;

public class SimpleBinding<V> implements Binding<V> {
    private V value;

    @Override
    public V get() {
        return value;
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public void set(V other) {
        this.value = other;
    }
}
