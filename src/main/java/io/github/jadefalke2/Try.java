package io.github.jadefalke2;

import io.github.jadefalke2.exceptions.CatchBlockAlreadyExistsException;
import io.github.jadefalke2.interfaces.UnsafeRunnable;
import io.github.jadefalke2.interfaces.UnsafeSupplier;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * This class describes a wrapper around a try-catch-finally block.
 * @param <T> the return type of this try-catch-finally block
 */
public class Try <T> implements TryBuilder.CatchBuilder<T>, TryBuilder<T> {

    private final UnsafeSupplier<T> attempt;
    private final Map<Class<? extends Exception>, Consumer<? super Exception>> catchClauses;
    private Runnable onFinally;

    /**
     * Constructs a new Try-catch-builder with the given Exception-throwing supplier as the code to execute
     * @param attempt the code to run inside the try-block
     */
    private Try (UnsafeSupplier<T> attempt) {
        this.attempt = attempt;
        catchClauses = new HashMap<>();
    }

    /**
     * Factory method for a try without a return type
     * @param runnable the code to be executed
     * @return the newly constructed try
     */
    public static TryBuilder.CatchBuilder<Void> attempt (@NonNull UnsafeRunnable runnable) {
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
    public static <T> TryBuilder.CatchBuilder<T> attempt (@NonNull UnsafeSupplier<T> supplier) {
        return new Try<>(supplier);
    }

    /**
     * Appends a catch-block with the given type
     * @param exceptionType the type of exception
     * @param onCatch the code to run when this catch block is executed
     * @return the try instance
     */
    @SuppressWarnings("unchecked")
    public <E extends Exception> TryBuilder.CatchBuilder<T> onCatch (@NonNull Class<E> exceptionType, @NonNull Consumer<? super E> onCatch) {
        if (catchClauses.containsKey(exceptionType))
            throw new CatchBlockAlreadyExistsException();

        catchClauses.put(exceptionType, (Consumer<? super Exception>) onCatch);
        return this;
    }

    /**
     * Appends a catch-block with the Exception type
     * @param onCatch the code to run when this catch block is executed
     * @return the try instance
     */
    public TryBuilder.CatchBuilder<T> onCatch (@NonNull Consumer<? super Exception> onCatch) {
        return onCatch(Exception.class, onCatch);
    }

    /**
     * Appends a finally-block
     * @param onFinally the code run to run in the finally-block
     * @return the try instance
     */
    public TryBuilder<T> onFinally (@NonNull Runnable onFinally) {
        this.onFinally = onFinally;
        return this;
    }

    /**
     * Executes the code in the try-block, catching with the respective catch-block in case an Exception.
     * Finally, whatever branch is executed prior the finally-block will be executed.
     */
    public void run () {
        obtain();
    }

    /**
     * Executes the code in the try-block, catching with the respective catch-block in case an Exception.
     * Finally, whatever branch is executed prior the finally-block will be executed.
     * @return the Optional describing the value obtained within the try-catch block
     */
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
        if (catchClauses.containsKey(c)) {
            return Optional.of(catchClauses.get(c));
        } else {
            if (c.getSuperclass().equals(Exception.class) || catchClauses.isEmpty()) {
                return Optional.empty();
            }
            return getCatchClause(c.getSuperclass());
        }
    }
}
