package net.cassite.f.stream;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import net.cassite.f.core.MonadLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface Subscriber<T> extends MonadLike<T>, ReactiveCloseable {
    @Override
    <U> Subscriber<U> map(@NotNull Function<T, U> mapper);

    @Override
    <U> Subscriber<U> compose(@NotNull Function<T, @NotNull Future<U>> mapper);

    @Override
    Subscriber<T> setHandler(@NotNull Handler<AsyncResult<T>> handler);
}
