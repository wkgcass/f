package net.cassite.f;

import com.sun.istack.internal.NotNull;
import io.vertx.core.Future;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * a container, store data inside the container
 *
 * @param <T> type of the Ptr
 */
public class Ptr<T> implements ReadablePtr<T, Ptr<T>>, WritablePtr<T>, AsTransformable<Ptr<T>> {
    private Supplier<T> getter;
    private Consumer<T> setter;

    private Ptr(Supplier<T> getter, Consumer<T> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @SuppressWarnings("unchecked")
    public static <T> Ptr<T> nil() {
        Object[] t = new Object[]{null};
        return new Ptr<>(() -> (T) t[0], v -> t[0] = v);
    }

    @SuppressWarnings("unchecked")
    public static <T> Ptr<T> of(@NotNull T value) {
        if (value == null)
            throw new NullPointerException();
        Object[] t = new Object[]{value};
        return new Ptr<>(() -> (T) t[0], v -> t[0] = v);
    }

    public static <T> Ptr<T> of(@NotNull Supplier<T> getter, @NotNull Consumer<T> setter) {
        if (getter == null)
            throw new NullPointerException();
        if (setter == null)
            throw new NullPointerException();
        return new Ptr<>(getter, setter);
    }

    @SuppressWarnings("unchecked")
    public static <T> ReadablePtr<T, ?> ofReadonly(@NotNull T value) {
        if (value == null)
            throw new NullPointerException();
        Object[] t = new Object[]{value};
        return new Ptr<>(() -> (T) t[0], v -> {
            throw new UnsupportedOperationException();
        });
    }

    @Override
    public T get() {
        return getter.get();
    }

    @Override
    public Ptr<T> store(@NotNull T t) {
        if (t == null)
            throw new NullPointerException();
        setter.accept(t);
        return this;
    }

    @Override
    public Ptr<T> storeNil() {
        setter.accept(null);
        return this;
    }

    @Override
    public Monad<T> store(@NotNull Future<T> fu) {
        if (fu == null)
            throw new NullPointerException();
        Monad<T> tbd = F.tbd();
        fu.setHandler(r -> {
            if (r.failed()) {
                tbd.fail(r.cause());
            } else {
                T value = r.result();
                setter.accept(value);
                if (value == null) {
                    tbd.complete();
                } else {
                    tbd.complete(value);
                }
            }
        });
        return tbd;
    }

    @Override
    public <R> Monad<R> unary(@NotNull Function<Ptr<T>, Future<R>> f) {
        if (f == null)
            throw new NullPointerException();
        return Monad.transform(f.apply(this));
    }

    @Override
    public <R> Monad<R> bin(@NotNull BiFunction<Ptr<T>, Future<T>, Future<R>> f, @NotNull Future<T> fu) {
        if (f == null)
            throw new NullPointerException();
        if (fu == null)
            throw new NullPointerException();
        return Monad.transform(f.apply(this, fu));
    }

    // --------- begin primitive transformer -----------
    public static final Misc.IntFunction<ReadablePtr<Integer, ?>> Int = ReadablePtr::get;

    public static final Misc.FloatFunction<ReadablePtr<Float, ?>> Float = ReadablePtr::get;

    public static final Misc.LongFunction<ReadablePtr<Long, ?>> Long = ReadablePtr::get;

    public static final Misc.DoubleFunction<ReadablePtr<Double, ?>> Double = ReadablePtr::get;

    public static final Misc.ShortFunction<ReadablePtr<Short, ?>> Short = ReadablePtr::get;

    public static final Misc.ByteFunction<ReadablePtr<Byte, ?>> Byte = ReadablePtr::get;

    public static final Misc.BoolFunction<ReadablePtr<Boolean, ?>> Bool = ReadablePtr::get;

    public static final Misc.CharFunction<ReadablePtr<Character, ?>> Char = ReadablePtr::get;
    // --------- end primitive transformer -----------
}
