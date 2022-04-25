package io.github.jadefalke2;

import io.github.jadefalke2.interfaces.CatchClause;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A catch clause that maps many exception types to a single consumer.
 */
public class CompoundCatchClause implements CatchClause {
    private final Set<Class<? extends Exception>> exceptionTypes;
    private final Consumer<? super Exception> consumer;

    public CompoundCatchClause(Collection<Class<? extends Exception>> exceptionTypes, Consumer<? super Exception> consumer) {
        this.exceptionTypes = new HashSet<>(exceptionTypes);
        this.consumer = consumer;
    }

    @SafeVarargs
    public CompoundCatchClause(Consumer<? super Exception> consumer, Class<? extends Exception>... exceptionTypes) {
        this(Set.of(exceptionTypes), consumer);
    }

    @Override
    public Collection<Class<? extends Exception>> exceptionTypes() {
        return exceptionTypes;
    }

    @Override
    public Consumer<? super Exception> consumer() {
        return consumer;
    }
}
