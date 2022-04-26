package io.github.jadefalke2.functions;

public interface UnsafeRunnable <E extends Exception> {

    void run () throws E;

    static UnsafeRunnable<Exception> ofRunnable (Runnable src) {
        return src::run;
    }
}
