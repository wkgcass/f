package net.cassite.f;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * the monad instance of this library<br>
 * now it's adapted to vertx Future ({@link Future}) and can be used in the vertx world
 *
 * @param <T> the type
 */
public class Monad<T> implements Future<T>, IMonad<T>, AsTransformable<Monad<T>> {
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

    public static <E> Monad<E> unit(@NotNull E e) {
        if (e == null)
            throw new NullPointerException();
        return new Monad<>(Future.succeededFuture(e));
    }

    public static <E> Monad<E> unit() {
        return new Monad<>(Future.succeededFuture());
    }

    @Override
    public boolean isComplete() {
        return vertxFuture.isComplete();
    }

    @Override
    public Monad<T> setHandler(@NotNull Handler<AsyncResult<T>> handler) {
        if (handler == null)
            throw new NullPointerException();
        return transform(vertxFuture.setHandler(handler));
    }

    @Override
    public void complete(@NotNull T result) {
        if (result == null)
            throw new NullPointerException();
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
    public boolean tryComplete(@NotNull T result) {
        if (result == null)
            throw new NullPointerException();
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
    public <U> Monad<U> compose(@NotNull Function<T, Future<U>> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return transform(vertxFuture.compose(mapper));
    }

    @Override
    public <U> Monad<U> map(@NotNull Function<T, U> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return transform(vertxFuture.map(mapper));
    }

    @Override
    public <V> Monad<V> map(@NotNull V value) {
        if (value == null)
            throw new NullPointerException();
        return transform(vertxFuture.map(value));
    }

    @Override
    public <V> Monad<V> mapEmpty() {
        return transform(vertxFuture.mapEmpty());
    }

    @Override
    public Handler<AsyncResult<T>> completer() {
        return vertxFuture.completer();
    }

    @Override
    public Monad<T> recover(@NotNull Function<Throwable, Future<T>> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return transform(vertxFuture.recover(mapper));
    }

    @Override
    public Monad<T> otherwise(@NotNull Function<Throwable, T> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return transform(vertxFuture.otherwise(mapper));
    }

    @Override
    public Monad<T> otherwise(@NotNull T value) {
        if (value == null)
            throw new NullPointerException();
        return transform(vertxFuture.otherwise(value));
    }

    @Override
    public Monad<T> otherwiseEmpty() {
        return transform(vertxFuture.otherwiseEmpty());
    }
}
