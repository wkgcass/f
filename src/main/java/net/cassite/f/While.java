package net.cassite.f;

import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class While {
    private While() {
    }

    public static WhileLoop cond(Supplier<Future<Boolean>> condition) {
        return new WhileLoop(condition);
    }

    public static WhileLoop cond(BooleanSupplier condition) {
        return cond(() -> F.unit(condition.getAsBoolean()));
    }

    public static class WhileLoop {
        private final Supplier<Future<Boolean>> condition;

        WhileLoop(Supplier<Future<Boolean>> condition) {
            this.condition = condition;
        }

        public <R> Monad<List<R>> yield(Supplier<Future<R>> func) {
            List<R> results = new ArrayList<>();
            return Monad.transform(handle(results, func).map(v -> results));
        }

        private <R> Future<Object> handle(List<R> results, Supplier<Future<R>> func) {
            return condition.get().compose(b -> {
                if (!b) return F.unit();
                return func.get().map(r -> {
                    if (r != null) results.add(r);
                    return null;
                }).compose(v -> handle(results, func));
            });
        }
    }
}
