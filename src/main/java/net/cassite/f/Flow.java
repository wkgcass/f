package net.cassite.f;

import io.vertx.core.Future;

import java.util.function.Supplier;

public class Flow {
    private Flow() {
    }

    public static <T> FlowStmt exec(Supplier<Future<T>> f) {
        return new FlowStmt().exec(f);
    }

    public static FlowStmt exec(F.Procedure f) {
        return new FlowStmt().exec(f);
    }

    public static class FlowStmt {
        private Future<?> fu = null;

        FlowStmt() {
        }

        public <T> FlowStmt exec(Supplier<Future<T>> f) {
            if (fu == null) {
                fu = f.get();
            } else {
                fu = fu.compose(v -> f.get());
            }
            return this;
        }

        public FlowStmt exec(F.Procedure f) {
            return exec(() -> {
                f.run();
                return F.unit();
            });
        }

        public <T> Monad<T> plainResult(Supplier<Future<T>> f) {
            return Monad.transform(fu.compose(v -> f.get()));
        }

        public <T> Monad<T> ptrResult(Ptr<T> ptr) {
            return Monad.transform(fu.map(v -> ptr.value));
        }
    }
}
