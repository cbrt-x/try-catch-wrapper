package io.github.jadefalke2;

import io.github.jadefalke2.exceptions.CatchClauseAlreadyExistsException;
import io.github.jadefalke2.interfaces.CatchClause;
import io.github.jadefalke2.interfaces.UnsafeRunnable;
import io.github.jadefalke2.interfaces.UnsafeSupplier;
import lombok.NonNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * This class describes a wrapper around a try-catch-finally block.
 * @param <T> the return type of this try-catch-finally block
 */
public class Try <T, Ex extends Exception> implements TryBuilder.CatchBuilder<T>, TryBuilder<T> {

    private final UnsafeSupplier<Ex, T> attempt;
    private final List<CatchClause> catchClauses;
    private Runnable onFinally;

    /**
     * Constructs a new Try-catch-builder with the given Exception-throwing supplier as the code to execute
     * @param attempt the code to run inside the try-block
     */
    private Try (UnsafeSupplier<Ex, T> attempt) {
        this.attempt = attempt;
        catchClauses = new LinkedList<>();
    }

    /**
     * Factory method for a try without a return type
     * @param runnable the code to be executed
     * @return the newly constructed try
     */
    public static <E extends Exception> TryBuilder.CatchBuilder<Void> attempt (@NonNull UnsafeRunnable<E> runnable) {
        return attempt(() -> {
            runnable.run();
            return null;
        });
    }

    /**
     * Factory method for a try
     * @param supplier the code to run and return a value inside the try
     * @param <T> the type to be returned
     * @return the newly constructed try
     */
    public static <E extends Exception, T> TryBuilder.CatchBuilder<T> attempt (@NonNull UnsafeSupplier<E, T> supplier) {
        return new Try<>(supplier);
    }

    /**
     * Internal method to add a catch clause to this builder. This checks that
     * we don't already have a catch clause that is eligible to accept any
     * exception type that the new clause handles.
     * @param clause The catch clause to add.
     * @throws CatchClauseAlreadyExistsException If the given clause handles
     * exceptions that are already handled by catch clauses that have been
     * previously added to this builder.
     */
    private void addCatchClause(CatchClause clause) throws CatchClauseAlreadyExistsException {
        for (var existingClause : catchClauses) {
            for (var type : clause.exceptionTypes()) {
                for (var existingType : existingClause.exceptionTypes()) {
                    if (existingType.equals(type)) {
                        throw new CatchClauseAlreadyExistsException(existingType, existingClause);
                    }
                }
            }
        }
        catchClauses.add(clause);
    }

    /**
     * Adds a catch clause that is called to handle any exception thrown when
     * this try is run.
     * @param onCatch The consumer to handle exceptions.
     * @return This builder object.
     */
    @Override
    public CatchBuilder<T> onCatch(@NonNull Consumer<? super Exception> onCatch) {
        addCatchClause(new SingleCatchClause<>(Exception.class, onCatch));
        return this;
    }

    /**
     * Adds a catch clause to handle a specific type of exception.
     * @param exceptionType The type of exception.
     * @param onCatch The consumer to handle exceptions.
     * @return This builder object.
     * @param <E> The exception type.
     */
    @Override
    public <E extends Exception> CatchBuilder<T> onCatch(@NonNull Class<E> exceptionType, @NonNull Consumer<? super E> onCatch) {
        addCatchClause(new SingleCatchClause<>(exceptionType, onCatch));
        return this;
    }

    /**
     * Adds a catch clause to handle a collection of specific exceptions.
     * @param exceptionTypes The types of exceptions to handle.
     * @param onCatch The consumer to handle exceptions.
     * @return This builder object.
     */
    @Override
    public CatchBuilder<T> onCatch(@NonNull Collection<Class<? extends Exception>> exceptionTypes, @NonNull Consumer<? super Exception> onCatch) {
        addCatchClause(new CompoundCatchClause(exceptionTypes, onCatch));
        return this;
    }

    /**
     * Appends a finally-block
     * @param onFinally the code run to run in the finally-block
     * @return the try instance
     */
    @Override
    public TryBuilder<T> onFinally (@NonNull Runnable onFinally) {
        this.onFinally = onFinally;
        return this;
    }

    /**
     * Executes the code in the try-block, catching with the respective catch-block in case an Exception.
     * Finally, whatever branch is executed prior the finally-block will be executed.
     */
    @Override
    public void run () {
        obtain();
    }

    /**
     * Executes the code in the try-block, catching with the respective catch-block in case an Exception.
     * Finally, whatever branch is executed prior the finally-block will be executed.
     * @return the Optional describing the value obtained within the try-catch block
     */
    @Override
    public Optional<T> obtain () {
        Optional<T> val = Optional.empty();
        try {
            val = Optional.ofNullable(attempt.supply());
        } catch (Exception e) {
            getCatchClause(e.getClass())
                    .ifPresent(exConsumer -> exConsumer.accept(e));
        } finally {
            if (onFinally != null)
                onFinally.run();
        }
        return val;
    }

    /**
     * Asynchronously computes the result and returns it in form of a future
     * @return A future holding the returned Object
     */
    @Override
    public CompletableFuture<Optional<T>> obtainLater () {
        return CompletableFuture.supplyAsync(this::obtain);
    }

    /**
     * Gets the correct catch clause for the given class
     * @param c the Exception class
     * @return the optional Exception from the map
     */
    private Optional<Consumer<? super Exception>> getCatchClause (Class<?> c) {
        for (var clause : catchClauses) {
            if (clause.exceptionTypes().contains(c)) {
                return Optional.of(clause.consumer());
            }
        }
        if (c.getSuperclass().equals(Exception.class) || catchClauses.isEmpty()) {
            return Optional.empty();
        }
        return getCatchClause(c.getSuperclass());
    }
}
