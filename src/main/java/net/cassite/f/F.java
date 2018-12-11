package net.cassite.f;

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

    public static <T> Monad<T> unit(T value) {
        return Monad.unit(value);
    }

    public static <T> Monad<T> unit() {
        return Monad.unit();
    }

    public static <T> Monad<T> tbd() {
        return new Monad<>(Future.future());
    }

    public static <T> Monad<T> fail(String msg) {
        return new Monad<>(Future.failedFuture(msg));
    }

    public static <T> Monad<T> fail(Throwable t) {
        return new Monad<>(Future.failedFuture(t));
    }

    // ------- end Monad initializer -------

    // ------- start Monad transformer -------

    public static <T, R> Applicative<T, R> app(Future<? extends Function<T, R>> monad) {
        return new Applicative<>(Monad.transform(monad));
    }

    @SuppressWarnings("unchecked")
    public static <R> Monad<R> composite(List<? extends Future<?>> monadList) {
        return flip((List) monadList).mapEmpty();
    }

    public static <E> Monad<MList<E>> flip(List<? extends Future<E>> monadList) {
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
    public static <T> Monad<T> brk(T result) {
        throw new Break(result);
    }

    // ------- end flow control -------

    // ------- start util -------

    // a common helper function
    public static <T> T value(T result, Runnable p) {
        p.run();
        return result;
    }

    public static <T> Monad<T> runcb(Consumer<Handler<AsyncResult<T>>> func) {
        Monad<T> m = tbd();
        Handler<AsyncResult<T>> cb = r -> {
            if (r.failed()) {
                m.fail(r.cause());
            } else {
                m.complete(r.result());
            }
        };
        func.accept(cb);
        return m;
    }

    // ------- end util -------
}
