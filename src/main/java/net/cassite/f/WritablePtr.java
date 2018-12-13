package net.cassite.f;

import org.jetbrains.annotations.NotNull;
import io.vertx.core.Future;

public interface WritablePtr<T> {
    WritablePtr<T> store(@NotNull T t);

    WritablePtr<T> storeNil();

    Monad<T> store(@NotNull Future<T> fu);
}
