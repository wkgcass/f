package net.cassite.f.stream;

public interface IPublisher<T> extends ReactiveCloseable {
    Stream<T> subscribe();
}
