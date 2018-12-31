package net.cassite.f;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface IMonad<T> {
    <U> IMonad<U> map(@NotNull Function<T, U> mapper);

    <U> IMonad<U> compose(@NotNull Function<T, Future<U>> mapper);

    IMonad<T> setHandler(@NotNull Handler<AsyncResult<T>> handler);
}
