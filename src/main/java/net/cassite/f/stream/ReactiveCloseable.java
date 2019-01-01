package net.cassite.f.stream;

interface ReactiveCloseable {
    void close();

    boolean isClosed();

    void addCloseHandler(Runnable handler);
}
