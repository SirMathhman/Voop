package com.meti;

public class SimpleBinding<V> implements Binding<V> {
    private V value;

    public SimpleBinding() {
        this(null);
    }

    public SimpleBinding(V value) {
        this.value = value;
    }

    @Override
    public V get() {
        return value;
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public boolean isPresent() {
        return !isEmpty();
    }

    @Override
    public V set(V other) {
        V old = this.value;
        this.value = other;
        return old;
    }
}
