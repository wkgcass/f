package net.cassite.f;

import io.vertx.core.Future;

import java.util.function.Supplier;

public class Flow {
    private Flow() {
    }

    public static <T> FlowStmt store(Ptr<T> ptr, Supplier<Future<T>> f) {
        return new FlowStmt().store(ptr, f);
    }

    public static <T> FlowStmt exec(Supplier<Future<T>> f) {
        return new FlowStmt().exec(f);
    }

    public static FlowStmt exec(Runnable f) {
        return new FlowStmt().exec(f);
    }

    public static class FlowStmt {
        private Future<?> fu = null;

        FlowStmt() {
        }

        public <T> FlowStmt store(Ptr<T> ptr, Supplier<Future<T>> f) {
            return exec(() -> ptr.store(f.get()));
        }

        public <T> FlowStmt exec(Supplier<Future<T>> f) {
            if (fu == null) {
                fu = f.get();
            } else {
                fu = fu.compose(v -> f.get());
            }
            return this;
        }

        public FlowStmt exec(Runnable f) {
            return exec(() -> {
                f.run();
                return F.unit();
            });
        }

        public <T> Monad<T> returnFuture(Supplier<Future<T>> f) {
            return Monad.transform(fu.compose(v -> f.get()));
        }

        public <T> Monad<T> returnValue(Supplier<T> f) {
            return Monad.transform(fu.map(v -> f.get()));
        }

        public <T> Monad<T> returnPtr(Ptr<T> ptr) {
            return Monad.transform(fu.map(v -> ptr.value));
        }

        public Monad<Null> returnNull() {
            return Monad.transform(fu.map(v -> Null.value));
        }
    }
}
