package io.github.jadefalke2.interfaces;

public interface UnsafeRunnable {

    void run () throws Exception;

    static UnsafeRunnable ofRunnable (Runnable src) {
        return src::run;
    }
}
