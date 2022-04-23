package io.github.jadefalke2.interfaces;

public interface UnsafeSupplier<T> {
    T supply() throws Exception;
}
