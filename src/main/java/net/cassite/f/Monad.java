package net.cassite.f;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import net.cassite.f.core.MonadLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.*;

/**
 * the monad instance of this library<br>
 * now it's adapted to vertx Future ({@link Future}) and can be used in the vertx world
 *
 * @param <T> the type
 */
public class Monad<@Nullable T> implements Future<T>, MonadLike<T>, AsTransformable<Monad<T>> {
    private final Future<T> vertxFuture;

    Monad(Future<T> vertxFuture) {
        this.vertxFuture = vertxFuture;
    }

    static <T> Monad<T> transform(Future<T> f) {
        if (f instanceof Monad) {
            return (Monad<T>) f;
        } else {
            return new Monad<>(f);
        }
    }

    public static <E> Monad<E> unit(@Nullable E e) {
        return new Monad<>(Future.succeededFuture(e));
    }

    public static <E> Monad<E> unit() {
        return new Monad<>(Future.succeededFuture());
    }

    private Monad<T> transformMaybeSelf(Future<T> resultF) {
        if (resultF == vertxFuture) {
            return this;
        } else {
            return transform(resultF);
        }
    }

    private <A, B> Function<A, Future<B>> mapperAvoidNull(Function<A, Future<B>> mapper) {
        return a -> {
            Future<B> b = mapper.apply(a);
            if (b == null)
                throw new NullPointerException();
            return b;
        };
    }

    @Override
    public boolean isComplete() {
        return vertxFuture.isComplete();
    }

    @Override
    public Monad<T> setHandler(@NotNull Handler<AsyncResult<T>> handler) {
        if (handler == null)
            throw new NullPointerException();
        return transformMaybeSelf(vertxFuture.setHandler(handler));
    }

    @Override
    public void complete(@Nullable T result) {
        vertxFuture.complete(result);
    }

    @Override
    public void complete() {
        vertxFuture.complete();
    }

    @Override
    public void fail(@NotNull Throwable cause) {
        if (cause == null)
            throw new NullPointerException();
        vertxFuture.fail(cause);
    }

    @Override
    public void fail(@NotNull String failureMessage) {
        if (failureMessage == null)
            throw new NullPointerException();
        vertxFuture.fail(failureMessage);
    }

    @Override
    public boolean tryComplete(@Nullable T result) {
        return vertxFuture.tryComplete(result);
    }

    @Override
    public boolean tryComplete() {
        return vertxFuture.tryComplete();
    }

    @Override
    public boolean tryFail(@NotNull Throwable cause) {
        if (cause == null)
            throw new NullPointerException();
        return vertxFuture.tryFail(cause);
    }

    @Override
    public boolean tryFail(@NotNull String failureMessage) {
        if (failureMessage == null)
            throw new NullPointerException();
        return vertxFuture.tryFail(failureMessage);
    }

    @Override
    @Nullable
    public T result() {
        return vertxFuture.result();
    }

    @Override
    @Nullable
    public Throwable cause() {
        return vertxFuture.cause();
    }

    @Override
    public boolean succeeded() {
        return vertxFuture.succeeded();
    }

    @Override
    public boolean failed() {
        return vertxFuture.failed();
    }

    @Override
    public void handle(@NotNull AsyncResult<T> asyncResult) {
        if (asyncResult == null)
            throw new NullPointerException();
        vertxFuture.handle(asyncResult);
    }

    @Override
    public <U> Monad<U> compose(@NotNull Handler<T> handler, @NotNull Future<U> next) {
        if (handler == null)
            throw new NullPointerException();
        if (next == null)
            throw new NullPointerException();
        return transform(vertxFuture.compose(handler, next));
    }

    @Override
    public <U> Monad<U> compose(@NotNull Function<T, @NotNull Future<U>> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return transform(vertxFuture.compose(mapperAvoidNull(mapper)));
    }

    public <U> Monad<U> compose(@NotNull Supplier<@NotNull Future<U>> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return compose(v -> {
            Future<U> fu = mapper.get();
            if (fu == null)
                throw new NullPointerException();
            return fu;
        });
    }

    @Override
    public <U> Monad<U> map(@NotNull Function<T, @Nullable U> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return transform(vertxFuture.map(mapper));
    }

    @Override
    public <V> Monad<V> map(@Nullable V value) {
        return transform(vertxFuture.map(value));
    }

    public <U> Monad<U> map(@NotNull Supplier<@Nullable U> supplier) {
        if (supplier == null)
            throw new NullPointerException();
        return map(v -> supplier.get());
    }

    @Override
    public <V> Monad<V> mapEmpty() {
        return transform(vertxFuture.mapEmpty());
    }

    public <V> Monad<V> mapEmpty(@NotNull Consumer<T> consumer) {
        if (consumer == null)
            throw new NullPointerException();
        return map(t -> {
            consumer.accept(t);
            return null;
        });
    }

    public <V> Monad<V> mapEmpty(@NotNull Runnable code) {
        if (code == null)
            throw new NullPointerException();
        return map(t -> {
            code.run();
            return null;
        });
    }

    public Monad<T> bypass(@NotNull Runnable code) {
        if (code == null)
            throw new NullPointerException();
        return map(t -> {
            code.run();
            return t;
        });
    }

    @Override
    public Handler<AsyncResult<T>> completer() {
        return vertxFuture.completer();
    }

    @Override
    public Monad<T> recover(@NotNull Function<Throwable, @NotNull Future<T>> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return transformMaybeSelf(vertxFuture.recover(mapperAvoidNull(mapper)));
    }

    @Override
    public Monad<T> otherwise(@NotNull Function<Throwable, @Nullable T> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return transformMaybeSelf(vertxFuture.otherwise(mapper));
    }

    @Override
    public Monad<T> otherwise(@Nullable T value) {
        return transformMaybeSelf(vertxFuture.otherwise(value));
    }

    @Override
    public Monad<T> otherwiseEmpty() {
        return transformMaybeSelf(vertxFuture.otherwiseEmpty());
    }
}
