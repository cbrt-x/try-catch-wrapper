package io.github.jadefalke2.interfaces;

import java.util.function.Supplier;

public interface UnsafeSupplier<T> {

    T supply() throws Exception;

    static <T> UnsafeSupplier<T> ofSupplier (Supplier<T> src) {
        return src::get;
    }
}
