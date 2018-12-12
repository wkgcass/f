package net.cassite.f;

import com.sun.istack.internal.NotNull;
import io.vertx.core.Future;

import java.util.function.Supplier;

public class Flow {
    private Flow() {
    }

    public static <T> FlowStmt store(@NotNull Ptr<T> ptr, @NotNull Supplier<Future<T>> f) {
        if (ptr == null)
            throw new NullPointerException();
        if (f == null)
            throw new NullPointerException();
        return new FlowStmt().store(ptr, f);
    }

    public static <T> FlowStmt exec(@NotNull Supplier<Future<T>> f) {
        if (f == null)
            throw new NullPointerException();
        return new FlowStmt().exec(f);
    }

    public static FlowStmt exec(@NotNull Runnable f) {
        if (f == null)
            throw new NullPointerException();
        return new FlowStmt().exec(f);
    }

    public static class FlowStmt {
        private Future<?> fu = null;

        FlowStmt() {
        }

        public <T> FlowStmt store(@NotNull Ptr<T> ptr, @NotNull Supplier<Future<T>> f) {
            if (ptr == null)
                throw new NullPointerException();
            if (f == null)
                throw new NullPointerException();
            return exec(() -> ptr.store(f.get()));
        }

        public <T> FlowStmt exec(@NotNull Supplier<Future<T>> f) {
            if (f == null)
                throw new NullPointerException();
            if (fu == null) {
                fu = f.get();
            } else {
                fu = fu.compose(v -> f.get());
            }
            return this;
        }

        public FlowStmt exec(@NotNull Runnable f) {
            if (f == null)
                throw new NullPointerException();
            return exec(() -> {
                f.run();
                return F.unit();
            });
        }

        public <T> Monad<T> returnFuture(@NotNull Supplier<Future<T>> f) {
            if (f == null)
                throw new NullPointerException();
            return Monad.transform(fu.compose(v -> f.get()));
        }

        public <T> Monad<T> returnValue(@NotNull Supplier<T> f) {
            if (f == null)
                throw new NullPointerException();
            return Monad.transform(fu.map(v -> f.get()));
        }

        public <T> Monad<T> returnPtr(@NotNull Ptr<T> ptr) {
            if (ptr == null)
                throw new NullPointerException();
            return Monad.transform(fu.map(v -> ptr.value));
        }

        public Monad<Null> returnNull() {
            return Monad.transform(fu.map(v -> Null.value));
        }
    }
}
