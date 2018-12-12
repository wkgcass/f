package net.cassite.f;

import com.sun.istack.internal.NotNull;
import io.vertx.core.Future;

import java.util.function.Function;

public class Applicative<T, R> implements IMonad<Function<T, R>>, AsTransformable<Applicative<T, R>> {
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
    public <U> Monad<U> compose(@NotNull Function<Function<T, R>, Future<U>> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return monad.compose(mapper::apply);
    }

    public Monad<R> ap(@NotNull Future<T> fu) {
        if (fu == null)
            throw new NullPointerException();
        return monad.compose(fu::map);
    }
}
