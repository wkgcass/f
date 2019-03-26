package net.cassite.f;

import io.vertx.core.Future;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class Flow {
    private final MList<Next> nexts = MList.modifiable();

    private Flow() {
    }

    public static Flow flow() {
        return new Flow();
    }

    public static class Next {
        // one and only one of the following could be set
        public Supplier<Future<?>> async;
        public Runnable statement;
        StoreNext<?> storeNext;

        Next() {
        }

        Next(Next n) {
            this.async = n.async;
            this.statement = n.statement;
            this.storeNext = n.storeNext == null ? null : new StoreNext<>(n.storeNext);
        }

        public <T> StoreNext<T> store(@NotNull Ptr<T> ptr) {
            if (ptr == null)
                throw new NullPointerException();
            if (this.storeNext != null)
                throw new IllegalStateException();

            StoreNext<T> storeNext = new StoreNext<>(ptr);
            this.storeNext = storeNext;
            return storeNext;
        }
    }

    public static class StoreNext<T> {
        private final Ptr<T> ptr;
        public Supplier<Future<T>> async;
        public Supplier<T> value;

        StoreNext(Ptr<T> ptr) {
            this.ptr = ptr;
        }

        @SuppressWarnings("unchecked")
        StoreNext(StoreNext s) {
            this.ptr = s.ptr;
            this.async = s.async;
            this.value = s.value;
        }
    }

    public Next next() {
        Next n = new Next();
        nexts.add(n);
        return n;
    }

    private Monad<?> buildMonad() throws IllegalStateException {
        MList<Next> nexts = this.nexts.map(Next::new); // make a copy to make sure it won't change during the process

        // check
        nexts.forEach(n -> {
            int nullCnt = 0;
            if (n.storeNext == null) ++nullCnt;
            if (n.async == null) ++nullCnt;
            if (n.statement == null) ++nullCnt;
            if (nullCnt != 2)
                throw new IllegalStateException();

            if (n.storeNext != null) {
                if (n.storeNext.async == null && n.storeNext.value == null)
                    throw new NullPointerException();
                if (n.storeNext.async != null && n.storeNext.value != null)
                    throw new IllegalStateException();
            }
        });

        // build
        Monad<?> tbd = F.unit();
        for (Next nx : nexts) {
            if (nx.async != null) {
                tbd = tbd.compose(v -> nx.async.get());
            } else if (nx.statement != null) {
                tbd = tbd.map(v -> {
                    nx.statement.run();
                    return Null.value;
                });
            } else {
                // assert nx.storeNext != null;
                if (nx.storeNext.async != null) {
                    //noinspection unchecked
                    tbd = tbd.compose(v -> nx.storeNext.ptr.store((Future) nx.storeNext.async.get()));
                } else {
                    // assert nx.storeNext.value != null;
                    tbd = tbd.map(v -> {
                        //noinspection unchecked
                        ((Ptr) nx.storeNext.ptr).store(nx.storeNext.value.get());
                        return Null.value;
                    });
                }
            }
        }
        return tbd;
    }

    public <T> Monad<T> returnFuture(@NotNull Supplier<Future<T>> f) {
        if (f == null)
            throw new NullPointerException();
        return Monad.transform(buildMonad().compose(v -> f.get()));
    }

    public <T> Monad<T> returnValue(@NotNull Supplier<T> f) {
        if (f == null)
            throw new NullPointerException();
        return buildMonad().map(v -> f.get());
    }

    public <T> Monad<T> returnPtr(@NotNull Ptr<T> ptr) {
        if (ptr == null)
            throw new NullPointerException();
        return buildMonad().map(v -> ptr.get());
    }

    public Monad<Null> returnNull() {
        return buildMonad().map(v -> Null.value);
    }
}
