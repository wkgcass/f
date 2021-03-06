package net.cassite.f;

import io.vertx.core.Future;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @param <T> the pointed type
 * @param <P> the pointer type it self
 */
public interface ReadablePtr<T, P extends ReadablePtr<T, P>> {
    T get();

    // ----------- begin helper functions ------------
    default boolean isNull() {
        return get() == null;
    }

    default boolean isPresent() {
        return get() != null;
    }

    default boolean is(@Nullable T that) {
        T t = get();
        return (t == null && that == null) || (t != null && t.equals(that));
    }

    default boolean not(@Nullable T that) {
        return !is(that);
    }
    // ----------- end helper functions ------------

    <R> Monad<R> unary(@NotNull Function<P, Future<R>> f);

    <R> Monad<R> bin(@NotNull BiFunction<P, Future<T>, Future<R>> f, @NotNull Future<T> fu);

    // --------- begin primitive getter -----------
    default int getAs(Misc.IntFunction<ReadablePtr<T, ?>> p) {
        return p.apply(this);
    }

    default float getAs(Misc.FloatFunction<ReadablePtr<T, ?>> p) {
        return p.apply(this);
    }

    default long getAs(Misc.LongFunction<ReadablePtr<T, ?>> p) {
        return p.apply(this);
    }

    default double getAs(Misc.DoubleFunction<ReadablePtr<T, ?>> p) {
        return p.apply(this);
    }

    default short getAs(Misc.ShortFunction<ReadablePtr<T, ?>> p) {
        return p.apply(this);
    }

    default byte getAs(Misc.ByteFunction<ReadablePtr<T, ?>> p) {
        return p.apply(this);
    }

    default char getAs(Misc.CharFunction<ReadablePtr<T, ?>> p) {
        return p.apply(this);
    }

    default boolean getAs(Misc.BoolFunction<ReadablePtr<T, ?>> p) {
        return p.apply(this);
    }
    // --------- end primitive getter -----------
}
