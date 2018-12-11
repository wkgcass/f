package net.cassite.f;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class If {
    private If() {
    }

    public static IfCondition cond(Future<Boolean> condFu) {
        return new IfCondition(condFu);
    }

    public static class IfCondition {
        private final Future<Boolean> condFu;

        IfCondition(Future<Boolean> condFu) {
            this.condFu = condFu;
        }

        public <T> IfStatement<T> run(Supplier<Future<T>> code) {
            return new IfStatement<>(condFu, code);
        }
    }

    public static class IfStatement<T> {
        private final LinkedHashMap<Supplier<Future<Boolean>>, Supplier<Future<T>>> conditionMap = new LinkedHashMap<>();

        IfStatement(Future<Boolean> condFu, Supplier<Future<T>> code) {
            conditionMap.put(() -> condFu, code);
        }

        public IfElseif elseif(Supplier<Future<Boolean>> condFu) {
            return new IfElseif(condFu);
        }

        public class IfElseif {
            private final Supplier<Future<Boolean>> condFu;

            IfElseif(Supplier<Future<Boolean>> condFu) {
                this.condFu = condFu;
            }

            public IfStatement<T> run(Supplier<Future<T>> code) {
                conditionMap.put(condFu, code);
                return IfStatement.this;
            }
        }

        public Monad<T> otherwise(Supplier<Future<T>> code) {
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

        public <U> Monad<U> compose(Function<T, Future<U>> f) {
            return otherwise(() -> {
                throw new MatchError("clear into `otherwise`, but default condition not specified");
            }).compose(f);
        }

        public <U> Monad<U> map(Function<T, U> f) {
            return compose(t -> F.unit(f.apply(t)));
        }

        public void setHandler(Handler<AsyncResult<T>> handler) {
            compose(Future::succeededFuture).setHandler(handler);
        }
    }
}
