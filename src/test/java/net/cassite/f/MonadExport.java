package net.cassite.f;

import io.vertx.core.Future;

public class MonadExport {
    public static <T> Monad<T> get(Future<T> f) {
        return Monad.transform(f);
    }
}
