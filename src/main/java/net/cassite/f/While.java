package net.cassite.f;

import io.vertx.core.Future;

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

        public <R> Monad<MList<R>> yield(Supplier<Future<R>> func) {
            return For.init(null).cond(c -> condition.get()).incrSync(c -> {
            }).yield(c -> func.get());
        }
    }
}
