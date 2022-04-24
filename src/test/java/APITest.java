import io.github.jadefalke2.Try;
import io.github.jadefalke2.exceptions.CatchBlockAlreadyExistsException;
import io.github.jadefalke2.interfaces.UnsafeRunnable;
import io.github.jadefalke2.interfaces.UnsafeSupplier;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class APITest {

    private AtomicBoolean done;

    @Before
    public void setUp () {
        done = new AtomicBoolean(false);
    }

    @Test
    public void testMultipleCatchBlocks () {
        assertThrows(CatchBlockAlreadyExistsException.class,
                () -> Try.attempt(() -> System.out.println("Test"))
                        .onCatch(RuntimeException.class, Throwable::printStackTrace)
                        .onCatch(RuntimeException.class, e -> System.out.println(e.getMessage()))
        );
    }

    @Test
    public void testCorrectCatchClause () {
        Try.attempt(() -> {throw new IOException();})
                .onCatch(IOException.class, e -> {})
                .onCatch(Exception.class, e -> fail())
                .run();

        Try.attempt(() -> {throw new Exception();})
                .onCatch(Exception.class, e -> {})
                .onCatch(IOException.class, e -> fail());

        AtomicBoolean done = new AtomicBoolean(false);
        Try.attempt(() -> {throw new Exception();})
                .onCatch(e -> done.set(true))
                .run();
        assertTrue(done.get());
    }

    @Test
    public void testSuccessfulRun () {
        done.set(false);
        Try.attempt(() -> done.set(true))
                .onCatch(e -> fail())
                .run();
        assertTrue(done.get());
    }

    @Test
    public void testReturnValue () {
        assertEquals(Integer.valueOf(5),
                Try.attempt(() -> 5).obtain().orElseThrow());

        assertEquals(10, Try.attempt(() -> {throw new IOException();})
                            .obtain().orElse(10));

        assertThrows(NoSuchElementException.class, () ->
                Try.attempt(() -> null).obtain().orElseThrow());

        assertThrows(NoSuchElementException.class, () ->
                Try.attempt((UnsafeSupplier<?, ?>) () -> {throw new Exception();}).obtain().orElseThrow());
    }

    @Test
    public void testFinally () {
        done.set(false);
        Try.attempt(() -> {})
            .onCatch(e -> fail())
            .onFinally(() -> done.set(true))
            .run();
        assertTrue(done.get());

        AtomicInteger found = new AtomicInteger(0);
        Try.attempt(() -> {throw new Exception();})
           .onCatch(e -> found.incrementAndGet())
           .onFinally(found::incrementAndGet)
           .run();
        assertEquals(2, found.get());
    }

    @Test
    public void testFuture () {
        done.set(false);
        var future = Try.attempt(() -> Thread.sleep(100))
            .onCatch(InterruptedException.class, Throwable::printStackTrace)
            .onFinally(() -> done.set(true))
            .obtainLater();
        assertFalse(done.get());

        Try.attempt(() -> future.get())
            .onCatch(Throwable::printStackTrace)
            .run();
        assertTrue(done.get());
    }

    @Test
    public void testFactoryMethods () {
        done.set(false);
        Runnable run = () -> done.set(true);
        Try.attempt(UnsafeRunnable.ofRunnable(run))
            .run();
        assertTrue(done.get());

        done.set(false);
        Supplier<Boolean> supplier = () -> true;
        Try.attempt(UnsafeSupplier.ofSupplier(supplier))
            .obtain()
            .ifPresent(done::set);
        assertTrue(done.get());
    }
}
