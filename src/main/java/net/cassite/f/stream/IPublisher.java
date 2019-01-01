package net.cassite.f.stream;

public interface IPublisher<T> {
    Stream<T> subscribe();

    boolean isClosed();

    void close();
}
