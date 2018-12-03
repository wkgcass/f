package net.cassite.f;

import io.vertx.core.Future;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * a container, store data inside the container
 *
 * @param <T> type of the Ptr
 */
public class Ptr<T> implements AsTransformable<Ptr<T>> {
    public T value;

    private Ptr(T value) {
        this.value = value;
    }

    public static <T> Ptr<T> nil() {
        return new Ptr<>(null);
    }

    public static <T> Ptr<T> of(T value) {
        return new Ptr<>(value);
    }

    public Monad<T> store(Future<T> fu) {
        Monad<T> tbd = F.tbd();
        fu.setHandler(r -> {
            if (r.failed()) {
                tbd.fail(r.cause());
            } else {
                value = r.result();
                tbd.complete(value);
            }
        });
        return tbd;
    }

    public <R> Monad<R> unary(Function<Ptr<T>, Future<R>> f) {
        return Monad.transform(f.apply(this));
    }

    public <R> Monad<R> bin(BiFunction<Ptr<T>, Future<T>, Future<R>> f, Future<T> fu) {
        return Monad.transform(f.apply(this, fu));
    }
}
