package io.github.jadefalke2;

import io.github.jadefalke2.interfaces.CatchClause;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A catch clause that handles a single type of exception.
 * @param <T> The type of exception to handle.
 */
public class SingleCatchClause<T extends Exception> implements CatchClause {
    private final Class<T> exceptionType;
    private final Consumer<? super T> consumer;

    public SingleCatchClause(Class<T> exceptionType, Consumer<T> consumer) {
        this.exceptionType = exceptionType;
        this.consumer = consumer;
    }

    @Override
    public Collection<Class<? extends Exception>> exceptionTypes() {
        return Set.of(exceptionType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Consumer<? super Exception> consumer() {
        return (Consumer<? super Exception>) consumer;
    }
}
