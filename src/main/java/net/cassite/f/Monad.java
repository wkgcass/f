package net.cassite.f;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.function.Function;

/**
 * the monad instance of this library<br>
 * now it's adapted to vertx Future ({@link Future}) and can be used in the vertx world
 *
 * @param <T> the type
 */
public class Monad<T> implements Future<T>, IMonad<T> {
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

    @Override
    public boolean isComplete() {
        return vertxFuture.isComplete();
    }

    @Override
    public Monad<T> setHandler(Handler<AsyncResult<T>> handler) {
        return transform(vertxFuture.setHandler(handler));
    }

    @Override
    public void complete(T result) {
        vertxFuture.complete(result);
    }

    @Override
    public void complete() {
        vertxFuture.complete();
    }

    @Override
    public void fail(Throwable cause) {
        vertxFuture.fail(cause);
    }

    @Override
    public void fail(String failureMessage) {
        vertxFuture.fail(failureMessage);
    }

    @Override
    public boolean tryComplete(T result) {
        return vertxFuture.tryComplete(result);
    }

    @Override
    public boolean tryComplete() {
        return vertxFuture.tryComplete();
    }

    @Override
    public boolean tryFail(Throwable cause) {
        return vertxFuture.tryFail(cause);
    }

    @Override
    public boolean tryFail(String failureMessage) {
        return vertxFuture.tryFail(failureMessage);
    }

    @Override
    public T result() {
        return vertxFuture.result();
    }

    @Override
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
    public void handle(AsyncResult<T> asyncResult) {
        vertxFuture.handle(asyncResult);
    }

    @Override
    public <U> Monad<U> compose(Handler<T> handler, Future<U> next) {
        return transform(vertxFuture.compose(handler, next));
    }

    @Override
    public <U> Monad<U> compose(Function<T, Future<U>> mapper) {
        return transform(vertxFuture.compose(mapper));
    }

    @Override
    public <U> Monad<U> map(Function<T, U> mapper) {
        return transform(vertxFuture.map(mapper));
    }

    @Override
    public <V> Monad<V> map(V value) {
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
    public Monad<T> recover(Function<Throwable, Future<T>> mapper) {
        return transform(vertxFuture.recover(mapper));
    }

    @Override
    public Monad<T> otherwise(Function<Throwable, T> mapper) {
        return transform(vertxFuture.otherwise(mapper));
    }

    @Override
    public Monad<T> otherwise(T value) {
        return transform(vertxFuture.otherwise(value));
    }

    @Override
    public Monad<T> otherwiseEmpty() {
        return transform(vertxFuture.otherwiseEmpty());
    }

    // ------- extension start -------

    public <U, M extends IMonad<U>> M lift(Function<Monad<T>, M> func) {
        return func.apply(this);
    }

    // ------- extension end -------
}
