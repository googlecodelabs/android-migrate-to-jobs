package com.google.codelabs.migratingtojobs.common;

public final class ReadOnly<T> {
    private final T value;

    public ReadOnly(T v) {
        value = v;
    }

    public T get() {
        return value;
    }
}
