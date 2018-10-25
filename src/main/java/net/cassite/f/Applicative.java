package net.cassite.f;

import io.vertx.core.Future;

import java.util.function.Function;

public class Applicative<T, R> implements IMonad<Function<T, R>> {
    private final Monad<? extends Function<T, R>> monad;

    Applicative(Monad<? extends Function<T, R>> monad) {
        this.monad = monad;
    }

    @Override
    public <U> Monad<U> map(Function<Function<T, R>, U> mapper) {
        return monad.map(mapper::apply);
    }

    @Override
    public <U> Monad<U> compose(Function<Function<T, R>, Future<U>> mapper) {
        return monad.compose(mapper::apply);
    }

    public Monad<R> ap(Future<T> fu) {
        return monad.compose(fu::map);
    }
}
