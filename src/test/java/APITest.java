import io.github.jadefalke2.Try;
import io.github.jadefalke2.TryBuilder;
import io.github.jadefalke2.exceptions.CatchBlockAlreadyExistsException;
import io.github.jadefalke2.interfaces.UnsafeSupplier;
import org.junit.Test;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class APITest {

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
        AtomicBoolean done = new AtomicBoolean(false);
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
                Try.attempt((UnsafeSupplier<?>) () -> {throw new Exception();}).obtain().orElseThrow());
    }

    @Test
    public void testMisc () {

    }
}
