package net.cassite.f;

import com.sun.istack.internal.NotNull;
import io.vertx.core.Future;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class While {
    private While() {
    }

    public static WhileLoop cond(@NotNull Supplier<Future<Boolean>> condition) {
        if (condition == null)
            throw new NullPointerException();
        return new WhileLoop(condition);
    }

    public static WhileLoop cond(@NotNull BooleanSupplier condition) {
        if (condition == null)
            throw new NullPointerException();
        return cond(() -> F.unit(condition.getAsBoolean()));
    }

    public static class WhileLoop {
        private final Supplier<Future<Boolean>> condition;

        WhileLoop(Supplier<Future<Boolean>> condition) {
            this.condition = condition;
        }

        public <R> Monad<MList<R>> yield(@NotNull Supplier<Future<R>> func) {
            if (func == null)
                throw new NullPointerException();
            return For.init(null).cond(c -> condition.get()).incrSync(c -> {
            }).yield(c -> func.get());
        }
    }
}
