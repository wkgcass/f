package net.cassite.f;

import com.sun.istack.internal.NotNull;
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

    public static <T> Ptr<T> of(@NotNull T value) {
        if (value == null)
            throw new NullPointerException();
        return new Ptr<>(value);
    }

    public Monad<T> store(@NotNull Future<T> fu) {
        if (fu == null)
            throw new NullPointerException();
        Monad<T> tbd = F.tbd();
        fu.setHandler(r -> {
            if (r.failed()) {
                tbd.fail(r.cause());
            } else {
                value = r.result();
                if (value == null) {
                    tbd.complete();
                } else {
                    tbd.complete(value);
                }
            }
        });
        return tbd;
    }

    public <R> Monad<R> unary(@NotNull Function<Ptr<T>, Future<R>> f) {
        if (f == null)
            throw new NullPointerException();
        return Monad.transform(f.apply(this));
    }

    public <R> Monad<R> bin(@NotNull BiFunction<Ptr<T>, Future<T>, Future<R>> f, @NotNull Future<T> fu) {
        if (f == null)
            throw new NullPointerException();
        if (fu == null)
            throw new NullPointerException();
        return Monad.transform(f.apply(this, fu));
    }
}
