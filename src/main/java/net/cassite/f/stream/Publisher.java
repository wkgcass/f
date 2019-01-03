package net.cassite.f.stream;

public interface Publisher<T> extends ReactiveCloseable {
    static <T> SimplePublisher<T> create() {
        return new SimplePublisher<>();
    }

    Stream<T> subscribe();
}
