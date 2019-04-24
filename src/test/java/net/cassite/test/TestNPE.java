package net.cassite.test;

import io.vertx.core.Future;
import net.cassite.f.*;
import net.cassite.f.stream.EventEmitter;
import net.cassite.f.stream.Publisher;
import net.cassite.f.utils.MListOp;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SuppressWarnings({"ConstantConditions"})
public class TestNPE {
    private void test(Runnable r) {
        try {
            r.run();
            fail();
        } catch (NullPointerException ignore) {
        } catch (IllegalArgumentException e) {
            // for idea runtime assertion
            // disable it in preference->build,exec,deploy->compiler
            assertTrue(e.getMessage().startsWith("Argument for @NotNull parameter "));
        }
    }

    @Test
    public void Applicative() {
        Monad<Function<Integer, String>> m = F.unit(Object::toString);
        Applicative<Integer, String> a = m.as(F::app);
        test(() -> a.map(null));
        test(() -> a.compose(null));
        test(() -> a.ap(null));
        test(() -> a.setHandler(null));
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
        test(() -> F.fail((String) null));
        test(() -> F.fail((Throwable) null));
        test(() -> F.app(null));
        test(() -> F.composite(null));
        test(() -> F.flip(null));
        test(() -> F.value(null, null));
        test(() -> F.value(1, null));
        test(() -> F.runcb(null));
    }

    @Test
    public void Flow() {
        Flow flow = Flow.flow();
        test(() -> flow.next().store(null));
        test(() -> flow.returnFuture(null));
        test(() -> flow.returnValue(null));
        test(() -> flow.returnPtr(null));
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

    @SuppressWarnings("unchecked")
    @Test
    public void Monad() {
        test(() -> Monad.unit(1).setHandler(null));
        test(() -> F.tbd().fail((String) null));
        test(() -> F.tbd().fail((Throwable) null));
        test(() -> Monad.unit(1).tryFail((String) null));
        test(() -> Monad.unit(1).tryFail((Throwable) null));
        test(() -> Monad.unit(1).handle(null));
        test(() -> Monad.unit(1).compose((Function) null));
        test(() -> Monad.unit(1).compose((Supplier) null));
        test(() -> Monad.unit(1).compose(null, null));
        test(() -> Monad.unit(1).compose(t -> {
        }, null));
        test(() -> Monad.unit(1).compose(null, F.unit()));
        test(() -> Monad.unit(1).map((Function<Integer, String>) null));
        test(() -> Monad.unit(1).map((Supplier<String>) null));
        test(() -> Monad.unit(1).mapEmpty((Consumer<Integer>) null));
        test(() -> Monad.unit(1).mapEmpty((Runnable) null));
        test(() -> Monad.unit(1).bypass(null));
        test(() -> Monad.unit(1).otherwise((Function<Throwable, Integer>) null));
        test(() -> Monad.unit(1).recover(null));
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
        test(() -> Op.gt(null, null));
        test(() -> Op.gt(Ptr.of(1), null));
        test(() -> Op.gt(null, F.unit(1)));
        test(() -> Op.lt(null, null));
        test(() -> Op.lt(Ptr.of(1), null));
        test(() -> Op.lt(null, F.unit(1)));
        test(() -> Op.ge(null, null));
        test(() -> Op.ge(Ptr.of(1), null));
        test(() -> Op.ge(null, F.unit(1)));
        test(() -> Op.le(null, null));
        test(() -> Op.le(Ptr.of(1), null));
        test(() -> Op.le(null, F.unit(1)));
        test(() -> Op.eq(null, null));
        test(() -> Op.eq(Ptr.of(1), null));
        test(() -> Op.eq(null, F.unit(1)));
        test(() -> Op.ne(null, null));
        test(() -> Op.ne(Ptr.of(1), null));
        test(() -> Op.ne(null, F.unit(1)));
    }

    @Test
    public void Ptr() {
        test(() -> Ptr.of(null));
        test(() -> Ptr.of(null, null));
        test(() -> Ptr.of(() -> null, null));
        test(() -> Ptr.of(null, t -> {
        }));
        test(() -> Ptr.ofReadonly(null));
        test(() -> Ptr.of(1).store((Future<Integer>) null));
        test(() -> Ptr.of(1).store((Integer) null));
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

    @Test
    public void symbol() {
        test(() -> Symbol.create(null));
        test(() -> EventEmitter.create().on(null, null));
        test(() -> EventEmitter.create().on(Symbol.create(), null));
        test(() -> EventEmitter.create().on(null, d -> {
        }));
        test(() -> EventEmitter.create().once(null, null));
        test(() -> EventEmitter.create().once(Symbol.create(), null));
        test(() -> EventEmitter.create().once(null, d -> {
        }));
        test(() -> EventEmitter.create().once(null));
        test(() -> EventEmitter.create().handlers(null));
        test(() -> EventEmitter.create().remove(null, null));
        test(() -> EventEmitter.create().remove(Symbol.create(), null));
        test(() -> EventEmitter.create().remove(null, d -> {
        }));
        test(() -> EventEmitter.create().removeAll(null));
        test(() -> EventEmitter.create().emit(null, null));
        test(() -> EventEmitter.create().emit(null, 1));
        test(() -> EventEmitter.create().on(null));
        EventEmitter.create().emit(Symbol.create(), null); // pass
    }

    @Test
    public void publisher() {
        test(() -> Publisher.create().fail(null));
        Publisher.create().publish(null); // pass
    }

    @Test
    public void stream() {
        test(() -> Publisher.create().subscribe().setHandler(null));
        test(() -> Publisher.create().subscribe().map(null));
        test(() -> Publisher.create().subscribe().compose(null));
    }

    @Test
    public void mlistop() {
        test(() -> MListOp.intOp(null));
        test(() -> MListOp.longOp(null));
        test(() -> MListOp.doubleOp(null));
        test(() -> MListOp.floatOp(null));
        test(() -> MListOp.op(null));
        test(() -> MListOp.op(MList.unit()).sort(null));
        test(() -> MListOp.op(MList.unit()).join(null));
        test(() -> MListOp.op(MList.unit()).join(null, "", ""));
        test(() -> MListOp.op(MList.unit()).join("", null, ""));
        test(() -> MListOp.op(MList.unit()).join("", "", null));
    }
}
