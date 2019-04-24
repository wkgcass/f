package net.cassite.f;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import net.cassite.f.core.MonadLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class Applicative<T, R> implements MonadLike<Function<T, R>>, AsTransformable<Applicative<T, R>> {
    private final Monad<? extends Function<T, R>> monad;

    Applicative(Monad<? extends Function<T, R>> monad) {
        this.monad = monad;
    }

    @Override
    public <U> Monad<U> map(@NotNull Function<Function<T, R>, U> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return monad.map(mapper::apply);
    }

    @Override
    public <U> Monad<U> compose(@NotNull Function<Function<T, R>, @NotNull Future<U>> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return monad.compose(f -> {
            Future<U> u = mapper.apply(f);
            if (u == null)
                throw new NullPointerException();
            return u;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public Monad<Function<T, R>> setHandler(@NotNull Handler<AsyncResult<Function<T, R>>> handler) {
        if (handler == null)
            throw new NullPointerException();
        return ((Monad<Function<T, R>>) monad).setHandler(handler);
    }

    public Monad<R> ap(@NotNull Future<T> fu) {
        if (fu == null)
            throw new NullPointerException();
        return monad.compose(fu::map);
    }
}
