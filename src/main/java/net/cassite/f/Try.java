package net.cassite.f;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class Try {
    private Try() {
    }

    public static <T> TryCode<T> code(@NotNull Supplier<Future<T>> c) {
        if (c == null)
            throw new NullPointerException();
        return new TryCode<>(c);
    }

    public static class TryCode<T> {
        private final Supplier<Future<T>> c;

        TryCode(Supplier<Future<T>> c) {
            this.c = c;
        }

        public <EX extends Throwable> TryCatch except(@NotNull Class<EX> exType, @NotNull Function<EX, Future<T>> exHandler) {
            if (exType == null)
                throw new NullPointerException();
            if (exHandler == null)
                throw new NullPointerException();
            return new TryCatch(exType, exHandler);
        }

        public Monad<T> composeFinally(@NotNull Supplier<Future<Null>> func) {
            if (func == null)
                throw new NullPointerException();
            return except(Throwable.class, Future::failedFuture).composeFinally(func);
        }

        public class TryCatch {
            private final LinkedHashMap<Class<? extends Throwable>, Function<Throwable, Future<T>>> handlers = new LinkedHashMap<>();

            <EX extends Throwable> TryCatch(Class<EX> exType, Function<EX, Future<T>> exHandler) {
                //noinspection unchecked
                handlers.put(exType, (Function<Throwable, Future<T>>) exHandler);
            }

            public <EX extends Throwable> TryCatch except(@NotNull Class<EX> exType, @NotNull Function<EX, Future<T>> exHandler) {
                if (exType == null)
                    throw new NullPointerException();
                if (exHandler == null)
                    throw new NullPointerException();
                if (handlers.containsKey(exType))
                    throw new Error("try-expression already has handler for " + exType.getName());
                //noinspection unchecked
                handlers.put(exType, (Function<Throwable, Future<T>>) exHandler);
                return this;
            }

            public <X> Monad<X> map(@NotNull Function<T, X> f) {
                if (f == null)
                    throw new NullPointerException();
                return compose(t -> F.unit(f.apply(t)));
            }

            public void setHandler(@NotNull Handler<AsyncResult<T>> handler) {
                if (handler == null)
                    throw new NullPointerException();
                compose(Future::succeededFuture).setHandler(handler);
            }

            public Monad<T> composeFinally(@NotNull Supplier<Future<Null>> func) {
                if (func == null)
                    throw new NullPointerException();
                Monad<T> fu = F.tbd();
                setHandler(r -> {
                    // always try to run finally code
                    Future<?> f;
                    try {
                        f = func.get();
                    } catch (Throwable t) {
                        // exception thrown in finally scope
                        fu.fail(t);
                        return;
                    }
                    f.setHandler(r2 -> {
                        if (r2.failed()) {
                            fu.fail(r2.cause());
                        } else {
                            // then check the actual result
                            if (r.failed()) {
                                fu.fail(r.cause());
                            } else {
                                T t = r.result();
                                if (t == null) {
                                    fu.complete();
                                } else {
                                    fu.complete(t);
                                }
                            }
                        }
                    });
                });
                return fu;
            }

            private <X> void handleFailed(Function<T, Future<X>> f, Future<X> fu, Throwable ex) {
                boolean found = false;
                for (Map.Entry<Class<? extends Throwable>, Function<Throwable, Future<T>>> entry : handlers.entrySet()) {
                    Class<? extends Throwable> type = entry.getKey();
                    if (type.isInstance(ex)) {
                        found = true;

                        Future<T> fuT;
                        try {
                            fuT = entry.getValue().apply(ex);
                        } catch (Throwable t) {
                            // exception thrown in handler function
                            fu.fail(t);
                            return;
                        }
                        fuT.compose(f).setHandler(fu);
                        break;
                    }
                }
                if (!found) {
                    fu.fail(ex);
                }
            }

            public <X> Monad<X> compose(@NotNull Function<T, Future<X>> f) {
                if (f == null)
                    throw new NullPointerException();
                Monad<X> fu = F.tbd();
                try {
                    c.get().setHandler(res -> {
                        if (res.succeeded()) {
                            T t = res.result();
                            f.apply(t).setHandler(fu);
                            return;
                        }
                        Throwable ex = res.cause();
                        handleFailed(f, fu, ex);
                    });
                } catch (Throwable ex) {
                    // exception thrown in code function
                    handleFailed(f, fu, ex);
                }
                return fu;
            }
        }
    }
}
