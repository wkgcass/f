package net.cassite.f.stream;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import net.cassite.f.core.MonadLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface ISubscriber<T> extends MonadLike<T>, ReactiveCloseable {
    @Override
    <U> ISubscriber<U> map(@NotNull Function<T, U> mapper);

    @Override
    <U> ISubscriber<U> compose(@NotNull Function<T, Future<U>> mapper);

    @Override
    ISubscriber<T> setHandler(@NotNull Handler<AsyncResult<T>> handler);
}
