package net.cassite.f;

import io.vertx.core.Future;
import org.jetbrains.annotations.NotNull;

public interface WritablePtr<T> {
    WritablePtr<T> store(@NotNull T t);

    WritablePtr<T> storeNil();

    Monad<T> store(@NotNull Future<T> fu);
}
