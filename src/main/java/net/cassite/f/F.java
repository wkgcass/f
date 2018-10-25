package net.cassite.f;

import io.vertx.core.Future;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class F {
    private F() {
    }

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

    public static <T, R> Applicative<T, R> app(Monad<? extends Function<T, R>> monad) {
        return new Applicative<>(monad);
    }

    public static <E> Monad<MList<E>> flip(List<Future<E>> monadList) {
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
                    MList<E> ls = MList.unit();
                    for (int j = 0; j < monadList.size(); ++j) {
                        ls.add(map.get(j));
                    }
                    m.complete(ls);
                }
            });
        }
        return m;
    }

    // break loop
    public static <T> Monad<T> brk() {
        throw new Break();
    }

    // break loop with value
    public static <T> Monad<T> brk(T result) {
        throw new Break(result);
    }

    @FunctionalInterface
    public interface Procedure {
        void run();
    }

    // a common helper function
    public static <T> T value(T result, Procedure p) {
        p.run();
        return result;
    }
}
