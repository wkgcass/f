package net.cassite.f;

import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class For {
    private For() {
    }

    public static <T> ForEach<T> each(Iterable<T> it) {
        return new ForEach<>(it.iterator());
    }

    public static <T> ForEach<T> each(T[] tArr) {
        return new ForEach<>(Arrays.stream(tArr).iterator());
    }

    public static <T> ForEach<T> each(Iterator<T> ite) {
        return new ForEach<>(ite);
    }

    private interface ClearLoop {
        void clear();
    }

    private static <T, R> Future<Object> handleLoopFunc(List<R> results, T t, Function<T, Future<R>> func, ClearLoop clearLoop) {
        Future<R> fu;
        try {
            fu = func.apply(t);
        } catch (Break b) {
            if (b.ins != null)
                //noinspection unchecked
                results.add((R) b.ins);
            clearLoop.clear();
            return F.unit();
        }
        Future<Object> rFu = Future.future();
        fu.setHandler(res -> {
            if (res.failed()) {
                if (res.cause() instanceof Break) {
                    Break b = (Break) res.cause();
                    if (b.ins != null)
                        //noinspection unchecked
                        results.add((R) b.ins);
                    clearLoop.clear();
                    rFu.complete();
                } else {
                    rFu.fail(res.cause());
                }
                return;
            }
            R r = res.result();
            if (r != null)
                results.add(r);
            rFu.complete();
        });
        return rFu;
    }

    public static class ForEach<T> {
        private final Iterator<T> ite;

        ForEach(Iterator<T> ite) {
            this.ite = ite;
        }

        public <R> Monad<List<R>> yield(Function<T, Future<R>> func) {
            List<R> results = new ArrayList<>();
            return Monad.transform(handle(results, func).map(v -> results));
        }

        private <R> Future<Object> handle(List<R> results, Function<T, Future<R>> func) {
            if (!ite.hasNext())
                return F.unit();
            T t = ite.next();
            return handleLoopFunc(results, t, func, () -> {
                while (ite.hasNext()) ite.next();
            }).compose(v -> handle(results, func));
        }
    }

    public static <I> ForLoop<I> init(I initVal) {
        return new ForLoop<>(initVal);
    }

    public static class ForLoopCtx<I> {
        public I i;
    }

    public static class ForLoop<I> {
        private ForLoopCtx<I> ctx = new ForLoopCtx<>();

        ForLoop(I i) {
            ctx.i = i;
        }

        public WithCondition cond(Function<ForLoopCtx<I>, Future<Boolean>> condition) {
            return new WithCondition(condition);
        }

        public WithCondition condSync(Predicate<ForLoopCtx<I>> condition) {
            return new WithCondition(ctx -> F.unit(condition.test(ctx)));
        }

        public class WithCondition {
            private final Function<ForLoopCtx<I>, Future<Boolean>> condition;

            WithCondition(Function<ForLoopCtx<I>, Future<Boolean>> condition) {
                this.condition = condition;
            }

            public WithIncr incr(Function<ForLoopCtx<I>, Future<?>> incrFunc) {
                return new WithIncr(incrFunc);
            }

            public WithIncr incrSync(Consumer<ForLoopCtx<I>> incrFunc) {
                return incr(ctx -> {
                    incrFunc.accept(ctx);
                    return F.unit();
                });
            }

            public class WithIncr {
                private final Function<ForLoopCtx<I>, Future<?>> incr;

                WithIncr(Function<ForLoopCtx<I>, Future<?>> incr) {
                    this.incr = incr;
                }

                public <R> Monad<List<R>> yield(Function<ForLoopCtx<I>, Future<R>> func) {
                    List<R> results = new ArrayList<>();
                    return Monad.transform(handle(new boolean[]{true}, results, func).map(v -> results));
                }

                private <R> Future<Object> handle(boolean[] doContinue, List<R> results, Function<ForLoopCtx<I>, Future<R>> func) {
                    return condition.apply(ctx).compose(b -> {
                        if (!b) return F.unit();
                        if (!doContinue[0]) return F.unit();
                        return handleLoopFunc(results, ctx, func, () -> doContinue[0] = false)
                            .compose(v -> incr.apply(ctx))
                            .compose(v -> handle(doContinue, results, func));
                    });
                }
            }
        }
    }
}
