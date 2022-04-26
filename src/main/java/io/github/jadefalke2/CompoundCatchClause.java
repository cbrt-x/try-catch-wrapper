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
        checkTypes(exceptionTypes);
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

    /**
     * Checks that the given exception types are valid for a compound catch
     * clause. That is, all the types must not be subtypes of each other.
     * @param exceptionTypes The exception types to check.
     * @throws IllegalArgumentException If some types are invalid.
     */
    private void checkTypes(Collection<Class<? extends Exception>> exceptionTypes) throws IllegalArgumentException {
        for (var typeA : exceptionTypes) {
            for (var typeB : exceptionTypes) {
                if (typeA == typeB) continue;
                if (typeA.isAssignableFrom(typeB) || typeB.isAssignableFrom(typeA)) {
                    throw new IllegalArgumentException(String.format(
                            "%s and %s may not both be caught by the same compound catch clause.",
                            typeA.getSimpleName(),
                            typeB.getSimpleName()
                    ));
                }
            }
        }
    }
}
