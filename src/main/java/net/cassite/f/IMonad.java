package net.cassite.f;

import io.vertx.core.Future;

import java.util.function.Function;

interface IMonad<T> {
    <U> Monad<U> map(Function<T, U> mapper);

    <U> Monad<U> compose(Function<T, Future<U>> mapper);

    // ------- alias start -------

    // for scala users
    default <U> Monad<U> flatMap(Function<T, Future<U>> mapper) {
        return compose(mapper);
    }

    // for haskell users
    default <U> Monad<U> bind(Function<T, Future<U>> mapper) {
        return compose(mapper);
    }

    // for js promise users
    default <U> Monad<U> then(Function<T, Future<U>> mapper) {
        return compose(mapper);
    }

    // for haskell users
    default <U> Monad<U> fmap(Function<T, U> mapper) {
        return map(mapper);
    }

    // ------- alias end -------
}
