package net.cassite.f;

import java.util.function.Function;

public interface AsTransformable<T extends AsTransformable<T>> {
    @SuppressWarnings("unchecked")
    default <U> U as(Function<T, U> f) {
        return f.apply((T) this);
    }
}
