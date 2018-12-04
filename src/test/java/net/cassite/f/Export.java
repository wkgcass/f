package net.cassite.f;

import io.vertx.core.Future;

import java.util.stream.Collector;

public class Export {
    public static <T> Monad<T> get(Future<T> f) {
        return Monad.transform(f);
    }

    public static <T> Collector<T, MList<T>, MList<T>> collector() {
        return MListCollector.collector();
    }
}
