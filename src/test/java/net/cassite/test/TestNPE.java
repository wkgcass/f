package net.cassite.test;

import io.vertx.core.Future;
import net.cassite.f.*;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.*;

@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
public class TestNPE {
    private void test(Runnable r) {
        try {
            r.run();
            fail();
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void Applicative() {
        Monad<Function<Integer, String>> m = F.unit(Object::toString);
        Applicative<Integer, String> a = m.as(F::app);
        test(() -> a.map(null));
        test(() -> a.compose(null));
        test(() -> a.ap(null));
    }

    @Test
    public void AsTransformable() {
        Monad<Integer> m = F.unit(1);
        test(() -> m.as(null));
    }

    @Test
    public void Do() {
        test(() -> Do.yield(null));
        test(() -> Do.yield(() -> F.unit(1)).whileCond((BooleanSupplier) null));
        test(() -> Do.yield(() -> F.unit(1)).whileCond((Supplier<Future<Boolean>>) null));
    }

    @Test
    public void F() {
        test(() -> F.unit(null));
        test(() -> F.fail((String) null));
        test(() -> F.fail((Throwable) null));
        test(() -> F.app(null));
        test(() -> F.composite(null));
        test(() -> F.flip(null));
        test(() -> F.brk(null));
        test(() -> F.value(null, null));
        test(() -> F.value(1, null));
        test(() -> F.runcb(null));
        test(() -> F.handler(null));
    }

    @Test
    public void Flow() {
        test(() -> Flow.store(null, null));
        test(() -> Flow.store(Ptr.nil(), null));
        test(() -> Flow.store(null, () -> F.unit(1)));
        test(() -> Flow.exec((Runnable) null));
        test(() -> Flow.exec((Supplier<Future<Object>>) null));
        Flow.exec(() -> null);
        test(() -> Flow.exec(() -> null).store(null, null));
        test(() -> Flow.exec(() -> null).store(Ptr.nil(), null));
        test(() -> Flow.exec(() -> null).store(null, () -> F.unit(1)));
        test(() -> Flow.exec(() -> null).exec((Runnable) null));
        test(() -> Flow.exec(() -> null).exec((Supplier<Future<Object>>) null));
        test(() -> Flow.exec(() -> null).returnFuture(null));
        test(() -> Flow.exec(() -> null).returnValue(null));
        test(() -> Flow.exec(() -> null).returnPtr(null));
    }

    @Test
    public void For() {
        test(() -> For.each((Iterable<Object>) null));
        test(() -> For.each((Object[]) null));
        test(() -> For.each((Iterator<Object>) null));
        test(() -> For.each(MList.unit()).yield(null));
        For.init(null);
        test(() -> For.init(1).condSync(null));
        test(() -> For.init(1).cond(null));
        test(() -> For.init(1).condSync(c -> c.i < 1).incrSync(null));
        test(() -> For.init(1).condSync(c -> c.i < 1).incr(null));
        test(() -> For.init(1).condSync(c -> c.i < 1).incrSync(c -> ++c.i).yield(null));
    }

    @Test
    public void If() {
        test(() -> If.cond(null));
        test(() -> If.cond(F.unit(false)).run(null));
        test(() -> If.cond(F.unit(false)).run(F::unit).elseif(null));
        test(() -> If.cond(F.unit(false)).run(F::unit).elseif(() -> F.unit(false)).run(null));
        test(() -> If.cond(F.unit(false)).run(F::unit).otherwise(null));
        test(() -> If.cond(F.unit(false)).run(F::unit).compose(null));
        test(() -> If.cond(F.unit(false)).run(F::unit).map(null));
        test(() -> If.cond(F.unit(false)).run(F::unit).setHandler(null));
    }

    @Test
    public void MList() {
        test(() -> MList.modifiable((Collection<?>) null));
        test(() -> MList.modifiable((Object[]) null));
        test(() -> MList.unit((Collection<?>) null));
        test(() -> MList.unit((Object[]) null));
        test(() -> MList.unit(1).map(null));
        test(() -> MList.unit(1).flatMap(null));
        test(() -> MList.unit(1).filter(null));
    }

    @Test
    public void Monad() {
        test(() -> Monad.unit(null));
        test(() -> Monad.unit(1).setHandler(null));
        test(() -> Monad.unit(1).complete(null));
        test(() -> Monad.unit(1).fail((String) null));
        test(() -> Monad.unit(1).fail((Throwable) null));
        test(() -> Monad.unit(1).tryComplete(null));
        test(() -> Monad.unit(1).tryFail((String) null));
        test(() -> Monad.unit(1).tryFail((Throwable) null));
        test(() -> Monad.unit(1).handle(null));
        test(() -> Monad.unit(1).compose(null));
        test(() -> Monad.unit(1).compose(null, null));
        test(() -> Monad.unit(1).compose(t -> {
        }, null));
        test(() -> Monad.unit(1).compose(null, F.unit()));
        test(() -> Monad.unit(1).map((Function<Integer, String>) null));
        test(() -> Monad.unit(1).map((Object) null));
        test(() -> Monad.unit(1).recover(null));
        test(() -> Monad.unit(1).otherwise((Function<Throwable, Integer>) null));
        test(() -> Monad.unit(1).otherwise((Integer) null));
    }

    @Test
    public void Op() {
        test(() -> Op.not(null));
        test(() -> Op.leftIncr(null));
        test(() -> Op.rightIncr(null));
        test(() -> Op.leftDecr(null));
        test(() -> Op.rightDecr(null));
        test(() -> Op.and(null, null));
        test(() -> Op.and(Ptr.of(true), null));
        test(() -> Op.and(null, F.unit(true)));
        test(() -> Op.or(null, null));
        test(() -> Op.or(Ptr.of(true), null));
        test(() -> Op.or(null, F.unit(true)));
        test(() -> Op.bitAndAsn(null, null));
        test(() -> Op.bitAndAsn(Ptr.of(1), null));
        test(() -> Op.bitAndAsn(null, F.unit(1)));
        test(() -> Op.bitOrAsn(null, null));
        test(() -> Op.bitOrAsn(Ptr.of(1), null));
        test(() -> Op.bitOrAsn(null, F.unit(1)));
        test(() -> Op.incr(null, null));
        test(() -> Op.incr(Ptr.of(1), null));
        test(() -> Op.incr(null, F.unit(1)));
        test(() -> Op.decr(null, null));
        test(() -> Op.decr(Ptr.of(1), null));
        test(() -> Op.decr(null, F.unit(1)));
        test(() -> Op.mulAsn(null, null));
        test(() -> Op.mulAsn(Ptr.of(1), null));
        test(() -> Op.mulAsn(null, F.unit(1)));
        test(() -> Op.divAsn(null, null));
        test(() -> Op.divAsn(Ptr.of(1), null));
        test(() -> Op.divAsn(null, F.unit(1)));
        test(() -> Op.modAsn(null, null));
        test(() -> Op.modAsn(Ptr.of(1), null));
        test(() -> Op.modAsn(null, F.unit(1)));
        test(() -> Op.plus(null, null));
        test(() -> Op.plus(Ptr.of(1), null));
        test(() -> Op.plus(null, F.unit(1)));
        test(() -> Op.minus(null, null));
        test(() -> Op.minus(Ptr.of(1), null));
        test(() -> Op.minus(null, F.unit(1)));
        test(() -> Op.multiply(null, null));
        test(() -> Op.multiply(Ptr.of(1), null));
        test(() -> Op.multiply(null, F.unit(1)));
        test(() -> Op.divide(null, null));
        test(() -> Op.divide(Ptr.of(1), null));
        test(() -> Op.divide(null, F.unit(1)));
        test(() -> Op.mod(null, null));
        test(() -> Op.mod(Ptr.of(1), null));
        test(() -> Op.mod(null, F.unit(1)));
        test(() -> Op.bitAnd(null, null));
        test(() -> Op.bitAnd(Ptr.of(1), null));
        test(() -> Op.bitAnd(null, F.unit(1)));
        test(() -> Op.bitOr(null, null));
        test(() -> Op.bitOr(Ptr.of(1), null));
        test(() -> Op.bitOr(null, F.unit(1)));
    }

    @Test
    public void Ptr() {
        test(() -> Ptr.of(null));
        test(() -> Ptr.of(1).store(null));
        test(() -> Ptr.of(1).unary(null));
        test(() -> Ptr.of(1).bin(null, null));
        test(() -> Ptr.of(1).bin(Op::plus, null));
        test(() -> Ptr.of(1).bin(null, F.unit(1)));
    }

    @Test
    public void Try() {
        test(() -> Try.code(null));
        test(() -> Try.code(() -> F.unit(1)).except(null, null));
        test(() -> Try.code(() -> F.unit(1)).except(Throwable.class, null));
        test(() -> Try.code(() -> F.unit(1)).except(null, t -> F.unit(1)));
        test(() -> Try.code(() -> F.unit(1)).composeFinally(null));
        test(() -> Try.code(() -> F.unit(1)).except(Throwable.class, t -> F.unit(1))
            .except(null, null));
        test(() -> Try.code(() -> F.unit(1)).except(Throwable.class, t -> F.unit(1))
            .except(Throwable.class, null));
        test(() -> Try.code(() -> F.unit(1)).except(Throwable.class, t -> F.unit(1))
            .except(null, t -> F.unit(1)));
        test(() -> Try.code(() -> F.unit(1)).except(Throwable.class, t -> F.unit(1))
            .map(null));
        test(() -> Try.code(() -> F.unit(1)).except(Throwable.class, t -> F.unit(1))
            .compose(null));
        test(() -> Try.code(() -> F.unit(1)).except(Throwable.class, t -> F.unit(1))
            .setHandler(null));
        test(() -> Try.code(() -> F.unit(1)).except(Throwable.class, t -> F.unit(1))
            .composeFinally(null));
    }

    @Test
    public void While() {
        test(() -> While.cond((BooleanSupplier) null));
        test(() -> While.cond((Supplier<Future<Boolean>>) null));
        test(() -> While.cond(() -> true).yield(null));
    }
}
