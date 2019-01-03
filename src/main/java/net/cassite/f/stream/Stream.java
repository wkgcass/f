package net.cassite.f.stream;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import net.cassite.f.F;
import net.cassite.f.Monad;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.function.Function;

public class Stream<T> implements Publisher<T>, Subscriber<T> {
    private final LinkedHashSet<Handler<AsyncResult<T>>> handlers = new LinkedHashSet<>();
    private LinkedList<Runnable> closeCallbacks = new LinkedList<>();
    private boolean closed = false;

    Stream() {
    }

    void emit(T value) {
        Monad<T> toHandle;
        if (value == null) {
            toHandle = F.unit();
        } else {
            toHandle = F.unit(value);
        }
        for (Handler<AsyncResult<T>> handler : handlers) {
            try {
                handler.handle(toHandle);
            } catch (Throwable err) {
                try {
                    handler.handle(F.fail(err));
                } catch (Throwable ignore) {
                    // we can do nothing about the error
                }
            }
        }
    }

    void fail(Throwable err) {
        for (Handler<AsyncResult<T>> handler : handlers) {
            try {
                handler.handle(F.fail(err));
            } catch (Throwable ignore) {
                // we can do nothing about the error
            }
        }
    }

    private void closeCheck() {
        if (closed) {
            throw new IllegalStateException("the stream is already closed");
        }
    }

    @Override
    public <U> Stream<U> map(@NotNull Function<T, U> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        closeCheck();

        return compose(t -> F.unit(mapper.apply(t)));
    }

    @Override
    public <U> Stream<U> compose(@NotNull Function<T, Future<U>> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        closeCheck();

        Stream<U> uStream = new Stream<>();
        addHandler(r -> {
            if (r.failed()) {
                uStream.fail(r.cause());
            } else {
                T t = r.result();
                Future<U> uFu = mapper.apply(t);
                uFu.setHandler(r2 -> {
                    if (r2.failed()) {
                        uStream.fail(r2.cause());
                    } else {
                        uStream.emit(r2.result());
                    }
                });
            }
        }, uStream);
        return uStream;
    }

    private <U> void addHandler(Handler<AsyncResult<T>> handler, Stream<U> bondStream) {
        handlers.add(handler);
        if (bondStream != null) {
            bondStream.closeCallbacks.add(() -> handlers.remove(handler));
        }
    }

    @Override
    public Stream<T> setHandler(@NotNull Handler<AsyncResult<T>> handler) {
        if (handler == null)
            throw new NullPointerException();
        closeCheck();

        addHandler(handler, null);
        return this;
    }

    @Override
    public void close() {
        closed = true;
        fail(new HandlerRemovedException());
        handlers.clear();
        for (Runnable closeCallback : closeCallbacks) {
            closeCallback.run();
        }
        closeCallbacks.clear();
    }

    @Override
    public Stream<T> subscribe() {
        closeCheck();

        Stream<T> s = new Stream<>();
        addHandler(r -> {
            if (r.failed()) {
                s.fail(r.cause());
            } else {
                s.emit(r.result());
            }
        }, s);
        return s;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void addCloseHandler(Runnable handler) {
        closeCallbacks.add(handler);
    }
}
