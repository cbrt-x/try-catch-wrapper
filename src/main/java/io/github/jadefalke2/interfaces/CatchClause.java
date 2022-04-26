package io.github.jadefalke2.interfaces;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * A catch clause declares a collection of exception types which it can handle,
 * and a consumer that can be called upon to handle any of the given types of
 * exceptions.
 */
public interface CatchClause {
    /**
     * Gets the exception types which this catch clause can handle.
     * @return The collection of exception types which this catch clause can handle.
     */
    Collection<Class<? extends Exception>> exceptionTypes();

    /**
     * Gets the consumer that can be used to handle exceptions.
     * @return The consumer that can be used to handle exceptions.
     */
    Consumer<? super Exception> consumer();
}
