package net.cassite.f;

import io.vertx.core.Future;

import java.util.function.Function;

public class F {
    private F() {
    }

    public static <T> Monad<T> unit(T t) {
        return new Monad<>(Future.succeededFuture(t));
    }

    public static <T> Monad<T> unit() {
        return new Monad<>(Future.succeededFuture());
    }

    public static <T> Monad<T> tbd() {
        return new Monad<>(Future.future());
    }

    public static <T> Monad<T> fail(String msg) {
        return new Monad<>(Future.failedFuture(msg));
    }

    public static <T> Monad<T> fail(Throwable t) {
        return new Monad<>(Future.failedFuture(t));
    }

    public static <T, R> Applicative<T, R> app(Monad<? extends Function<T, R>> monad) {
        return new Applicative<>(monad);
    }
}
