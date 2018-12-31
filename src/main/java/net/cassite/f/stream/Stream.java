package net.cassite.f.stream;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import net.cassite.f.F;
import net.cassite.f.IMonad;
import net.cassite.f.MList;
import net.cassite.f.Monad;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class Stream<T> implements IMonad<T>, IPublisher<T> {
    private final MList<Handler<AsyncResult<T>>> handlers = MList.modifiable();

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

    @Override
    public <U> Stream<U> map(@NotNull Function<T, U> mapper) {
        if (mapper == null)
            throw new NullPointerException();

        return compose(t -> F.unit(mapper.apply(t)));
    }

    @Override
    public <U> Stream<U> compose(@NotNull Function<T, Future<U>> mapper) {
        if (mapper == null)
            throw new NullPointerException();

        Stream<U> uStream = new Stream<>();
        setHandler(r -> {
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
        });
        return uStream;
    }

    @Override
    public Stream<T> setHandler(@NotNull Handler<AsyncResult<T>> handler) {
        if (handler == null)
            throw new NullPointerException();

        handlers.add(handler);
        return this;
    }

    @Override
    public Stream<T> subscribe() {
        Stream<T> s = new Stream<>();
        setHandler(r -> {
            if (r.failed()) {
                s.fail(r.cause());
            } else {
                s.emit(r.result());
            }
        });
        return s;
    }
}
