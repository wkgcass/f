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
        return cond(() -> Future.succeededFuture(condition.getAsBoolean()));
    }

    public static class WhileLoop {
        private final Supplier<Future<Boolean>> condition;

        WhileLoop(Supplier<Future<Boolean>> condition) {
            this.condition = condition;
        }

        public <R> Future<List<R>> yield(Supplier<Future<R>> func) {
            List<R> results = new ArrayList<>();
            return handle(results, func).map(v -> results);
        }

        private <R> Future<List<R>> handle(List<R> results, Supplier<Future<R>> func) {
            return condition.get().compose(b -> {
                if (!b) return Future.succeededFuture();
                return func.get().map(r -> {
                    if (r != null) results.add(r);
                    return null;
                }).compose(v -> handle(results, func));
            });
        }
    }
}
