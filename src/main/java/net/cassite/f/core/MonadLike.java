package net.cassite.f.core;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface MonadLike<T> {
    <U> MonadLike<U> map(@NotNull Function<T, U> mapper);

    <U> MonadLike<U> compose(@NotNull Function<T, @NotNull Future<U>> mapper);

    MonadLike<T> setHandler(@NotNull Handler<AsyncResult<T>> handler);
}
