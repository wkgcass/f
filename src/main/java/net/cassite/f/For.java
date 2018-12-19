package net.cassite.f;

import io.vertx.core.Future;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class For {
    private For() {
    }

    public static <T> ForEach<T> each(@NotNull Iterable<T> it) {
        if (it == null)
            throw new NullPointerException();
        return new ForEach<>(it.iterator(), true);
    }


    public static <T> ForEach<T> each(@NotNull T[] tArr) {
        if (tArr == null)
            throw new NullPointerException();
        return new ForEach<>(Arrays.stream(tArr).iterator(), true);
    }

    public static <T> ForEach<T> each(@NotNull Iterator<T> ite) {
        if (ite == null)
            throw new NullPointerException();
        return new ForEach<>(ite, true);
    }

    static <T> ForEach<T> eachThrowBreak(Iterable<T> it) {
        return new ForEach<>(it.iterator(), false);
    }

    private interface ClearLoop {
        void clear();
    }

    private static <T, R> Future<Object> handleLoopFunc(MList<R> results, T t, Function<T, Future<R>> func, ClearLoop clearLoop, boolean handleBreak) {
        Future<R> fu;
        try {
            fu = func.apply(t);
        } catch (Break b) {
            if (!handleBreak) {
                throw b;
            }
            if (b.ins != null)
                //noinspection unchecked
                results.add((R) b.ins);
            clearLoop.clear();
            return F.unit();
        }
        Future<Object> rFu = Future.future();
        fu.setHandler(res -> {
            if (res.failed()) {
                clearLoop.clear();
                if (handleBreak && res.cause() instanceof Break) {
                    Break b = (Break) res.cause();
                    if (b.ins != null)
                        //noinspection unchecked
                        results.add((R) b.ins);
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
        private final boolean handleBreak;

        ForEach(Iterator<T> ite, boolean handleBreak) {
            this.ite = ite;
            this.handleBreak = handleBreak;
        }

        public <R> Monad<MList<R>> yield(@NotNull Function<T, Future<R>> func) {
            if (func == null)
                throw new NullPointerException();
            MList<R> results = MList.modifiable();
            return Monad.transform(handle(results, func).map(v -> results.immutable()));
        }

        private <R> Future<Object> handle(MList<R> results, Function<T, Future<R>> func) {
            if (!ite.hasNext())
                return F.unit();
            T t = ite.next();
            return handleLoopFunc(results, t, func, () -> {
                while (ite.hasNext()) ite.next();
            }, handleBreak).compose(v -> handle(results, func));
        }
    }

    public static <I> ForLoop<I> init(@Nullable I initVal) {
        return new ForLoop<>(initVal);
    }

    public static class ForLoopCtx<I> {
        @Nullable
        public I i;
    }

    public static class ForLoop<I> {
        private ForLoopCtx<I> ctx = new ForLoopCtx<>();

        ForLoop(I i) {
            ctx.i = i;
        }

        public WithCondition cond(@NotNull Function<ForLoopCtx<I>, Future<Boolean>> condition) {
            if (condition == null)
                throw new NullPointerException();
            return new WithCondition(condition);
        }

        public WithCondition condSync(@NotNull Predicate<ForLoopCtx<I>> condition) {
            if (condition == null)
                throw new NullPointerException();
            return new WithCondition(ctx -> F.unit(condition.test(ctx)));
        }

        public class WithCondition {
            private final Function<ForLoopCtx<I>, Future<Boolean>> condition;

            WithCondition(Function<ForLoopCtx<I>, Future<Boolean>> condition) {
                this.condition = condition;
            }

            public WithIncr incr(@NotNull Function<ForLoopCtx<I>, Future<?>> incrFunc) {
                if (incrFunc == null)
                    throw new NullPointerException();
                return new WithIncr(incrFunc);
            }

            public WithIncr incrSync(@NotNull Consumer<ForLoopCtx<I>> incrFunc) {
                if (incrFunc == null)
                    throw new NullPointerException();
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

                public <R> Monad<MList<R>> yield(@NotNull Function<ForLoopCtx<I>, Future<R>> func) {
                    if (func == null)
                        throw new NullPointerException();
                    MList<R> results = MList.modifiable();
                    return Monad.transform(handle(new boolean[]{true}, results, func).map(v -> results.immutable()));
                }

                private <R> Future<Object> handle(boolean[] doContinue, MList<R> results, Function<ForLoopCtx<I>, Future<R>> func) {
                    return condition.apply(ctx).compose(b -> {
                        if (!b) return F.unit();
                        if (!doContinue[0]) return F.unit();
                        return handleLoopFunc(results, ctx, func, () -> doContinue[0] = false, true)
                            .compose(v -> incr.apply(ctx))
                            .compose(v -> handle(doContinue, results, func));
                    });
                }
            }
        }
    }
}
