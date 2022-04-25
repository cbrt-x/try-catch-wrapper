package io.github.jadefalke2;


import lombok.NonNull;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface TryBuilder <T> {

	void run ();
	Optional<T> obtain ();

	CompletableFuture<Optional<T>> obtainLater();

	interface CatchBuilder <T> extends TryBuilder<T> {
		CatchBuilder<T> onCatch(@NonNull Consumer<Exception> onCatch);

		<E extends Exception> CatchBuilder<T> onCatch(
				@NonNull Class<E> exceptionType,
				@NonNull Consumer<E> onCatch
		);

		CatchBuilder<T> onCatch(
				@NonNull Collection<Class<? extends Exception>> exceptionTypes,
				@NonNull Consumer<Exception> onCatch
		);

		TryBuilder<T> onFinally (Runnable runnable);
	}
}
