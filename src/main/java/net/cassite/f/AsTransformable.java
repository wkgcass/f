package net.cassite.f;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface AsTransformable<T extends AsTransformable<T>> {
    @SuppressWarnings("unchecked")
    default <U> U as(@NotNull Function<T, U> f) {
        if (f == null)
            throw new NullPointerException();
        return f.apply((T) this);
    }
}
