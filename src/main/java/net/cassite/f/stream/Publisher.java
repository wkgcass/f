package net.cassite.f.stream;

import org.jetbrains.annotations.NotNull;

public class Publisher<T> implements IPublisher<T> {
    private final Stream<T> stream = new Stream<>();
    Runnable closeCallback;

    public static <T> Publisher<T> create() {
        return new Publisher<>();
    }

    private Publisher() {
    }

    @SuppressWarnings("unchecked")
    public void publish(T data) {
        stream.emit(data);
    }

    public void fail(@NotNull Throwable t) {
        if (t == null)
            throw new NullPointerException();
        stream.fail(t);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<T> subscribe() {
        return stream.subscribe();
    }

    @Override
    public boolean isClosed() {
        return stream.isClosed();
    }

    @Override
    public void close() {
        stream.close();
        if (closeCallback != null) {
            closeCallback.run();
        }
    }
}
