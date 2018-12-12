package net.cassite.f;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class F {
    private F() {
    }

    // ------- start Monad initializer -------

    public static <T> Monad<T> unit(@NotNull T value) {
        if (value == null)
            throw new NullPointerException();
        return Monad.unit(value);
    }

    public static <T> Monad<T> unit() {
        return Monad.unit();
    }

    public static <T> Monad<T> tbd() {
        return new Monad<>(Future.future());
    }

    public static <T> Monad<T> fail(@NotNull String msg) {
        if (msg == null)
            throw new NullPointerException();
        return new Monad<>(Future.failedFuture(msg));
    }

    public static <T> Monad<T> fail(@NotNull Throwable t) {
        if (t == null)
            throw new NullPointerException();
        return new Monad<>(Future.failedFuture(t));
    }

    // ------- end Monad initializer -------

    // ------- start Monad transformer -------

    public static <T, R> Applicative<T, R> app(@NotNull Future<? extends Function<T, R>> monad) {
        if (monad == null)
            throw new NullPointerException();
        return new Applicative<>(Monad.transform(monad));
    }

    @SuppressWarnings("unchecked")
    public static <R> Monad<R> composite(@NotNull List<? extends Future<?>> monadList) {
        if (monadList == null)
            throw new NullPointerException();
        return flip((List) monadList).mapEmpty();
    }

    public static <E> Monad<MList<E>> flip(@NotNull List<? extends Future<E>> monadList) {
        if (monadList == null)
            throw new NullPointerException();
        Monad<MList<E>> m = tbd();
        boolean[] thrown = {false};
        Map<Integer, E> map = new HashMap<>(monadList.size());

        //noinspection UnnecessaryLocalVariable
        Object lock = map; // randomly picked object as the lock

        for (int i = 0; i < monadList.size(); i++) {
            int x = i;
            Future<E> fu = monadList.get(i);
            fu.setHandler(r -> {
                if (r.failed()) {
                    boolean doFail = false;
                    synchronized (lock) {
                        if (!thrown[0]) {
                            thrown[0] = true;
                            doFail = true;
                        }
                    }
                    if (doFail) {
                        m.fail(r.cause());
                    }
                    return;
                }

                boolean doComplete = false;

                synchronized (lock) {
                    if (thrown[0])
                        return;

                    map.put(x, r.result());
                    if (map.size() == monadList.size()) {
                        doComplete = true;
                    }
                }
                if (doComplete) {
                    MList<E> ls = MList.modifiable();
                    for (int j = 0; j < monadList.size(); ++j) {
                        ls.add(map.get(j));
                    }
                    m.complete(ls.immutable());
                }
            });
        }
        return m;
    }

    // ------- end Monad transformer -------

    // ------- start flow control -------

    // break loop
    public static <T> Monad<T> brk() {
        throw new Break();
    }

    // break loop with value
    public static <T> Monad<T> brk(@NotNull T result) {
        if (result == null)
            throw new NullPointerException();
        throw new Break(result);
    }

    // ------- end flow control -------

    // ------- start util -------

    // a common helper function
    public static <T> T value(@Nullable T result, @NotNull Runnable p) {
        if (p == null)
            throw new NullPointerException();
        p.run();
        return result;
    }

    public static <T> Monad<T> runcb(@NotNull Consumer<Handler<AsyncResult<T>>> func) {
        if (func == null)
            throw new NullPointerException();
        Monad<T> m = tbd();
        Handler<AsyncResult<T>> cb = handler(m);
        func.accept(cb);
        return m;
    }

    public static <T> Handler<AsyncResult<T>> handler(@NotNull Future<T> tbd) {
        if (tbd == null)
            throw new NullPointerException();
        return r -> {
            if (r.failed()) {
                tbd.fail(r.cause());
            } else if (r.result() == null) {
                tbd.complete();
            } else {
                tbd.complete(r.result());
            }
        };
    }

    // ------- end util -------
}
