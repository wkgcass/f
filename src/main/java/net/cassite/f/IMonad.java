package net.cassite.f;

import io.vertx.core.Future;

import java.util.function.Function;

interface IMonad<T> {
    <U> Monad<U> map(Function<T, U> mapper);

    <U> Monad<U> compose(Function<T, Future<U>> mapper);
}
