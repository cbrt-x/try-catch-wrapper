package io.github.jadefalke2.functions;

import java.util.function.Supplier;

public interface UnsafeSupplier<E extends Exception, T> {

    T supply() throws E;

    static <T> UnsafeSupplier<Exception, T> ofSupplier (Supplier<T> src) {
        return src::get;
    }
}
