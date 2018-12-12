package net.cassite.f;

import com.sun.istack.internal.NotNull;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class If {
    private If() {
    }

    public static IfCondition cond(@NotNull Future<Boolean> condFu) {
        if (condFu == null)
            throw new NullPointerException();
        return new IfCondition(condFu);
    }

    public static class IfCondition {
        private final Future<Boolean> condFu;

        IfCondition(Future<Boolean> condFu) {
            this.condFu = condFu;
        }

        public <T> IfStatement<T> run(@NotNull Supplier<Future<T>> code) {
            if (code == null)
                throw new NullPointerException();
            return new IfStatement<>(condFu, code);
        }
    }

    public static class IfStatement<T> {
        private final LinkedHashMap<Supplier<Future<Boolean>>, Supplier<Future<T>>> conditionMap = new LinkedHashMap<>();

        IfStatement(Future<Boolean> condFu, Supplier<Future<T>> code) {
            conditionMap.put(() -> condFu, code);
        }

        public IfElseif elseif(@NotNull Supplier<Future<Boolean>> condFu) {
            if (condFu == null)
                throw new NullPointerException();
            return new IfElseif(condFu);
        }

        public class IfElseif {
            private final Supplier<Future<Boolean>> condFu;

            IfElseif(Supplier<Future<Boolean>> condFu) {
                this.condFu = condFu;
            }

            public IfStatement<T> run(@NotNull Supplier<Future<T>> code) {
                if (code == null)
                    throw new NullPointerException();
                conditionMap.put(condFu, code);
                return IfStatement.this;
            }
        }

        public Monad<T> otherwise(@NotNull Supplier<Future<T>> code) {
            if (code == null)
                throw new NullPointerException();
            boolean[] finished = {false};
            Monad<T> fu = F.tbd();
            For.each(conditionMap.entrySet()).yield(e -> {
                // ignore when already finished
                if (finished[0]) return F.unit();

                Future<Boolean> test = e.getKey().get();
                Supplier<Future<T>> stmt = e.getValue();
                return test.map(b -> {
                    if (b) {
                        stmt.get().setHandler(fu);
                        finished[0] = true;
                    }
                    return null;
                });
            }).map(v -> {
                if (!finished[0]) {
                    code.get().setHandler(fu);
                }
                return null;
            }).setHandler(r -> {
                if (r.failed()) {
                    // only get here when supplier.get throws exception
                    fu.fail(r.cause());
                }
                // otherwise fu is already handled
            });
            return fu;
        }

        public <U> Monad<U> compose(@NotNull Function<T, Future<U>> f) {
            if (f == null)
                throw new NullPointerException();
            return otherwise(() -> {
                throw new MatchError("clear into `otherwise`, but default condition not specified");
            }).compose(f);
        }

        public <U> Monad<U> map(@NotNull Function<T, U> f) {
            if (f == null)
                throw new NullPointerException();
            return compose(t -> F.unit(f.apply(t)));
        }

        public void setHandler(@NotNull Handler<AsyncResult<T>> handler) {
            if (handler == null)
                throw new NullPointerException();
            compose(Future::succeededFuture).setHandler(handler);
        }
    }
}
