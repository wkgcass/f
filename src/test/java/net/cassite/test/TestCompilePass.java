package net.cassite.test;

import io.vertx.core.Future;
import net.cassite.f.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

public class TestCompilePass {
    @SuppressWarnings("unused") private Monad<?> mo;
    @SuppressWarnings({"FieldCanBeLocal", "unused"}) private Try.TryCode<?>.TryCatch tryCatch;
    @SuppressWarnings({"FieldCanBeLocal", "unused"}) private If.IfStatement<?> ifStmt;
    private MList<?> list;

    @Test
    public void forEach() {
        mo = For.each(Arrays.asList("a", "b", "c")).yield(s -> F.unit(s.toCharArray()));
        mo = For.each(Arrays.asList("a", "b", "c")).yield(s -> Future.succeededFuture(s.toCharArray()));
    }

    @Test
    public void forLoop() {
        mo = For.init(0).cond(c -> F.unit(c.i < 2)).incr(c -> F.unit(c.i++)).yield(c -> F.unit());
        mo = For.init(0)
            .cond(c -> Future.succeededFuture(c.i < 2))
            .incr(c -> Future.succeededFuture(c.i++))
            .yield(c -> Future.succeededFuture());
    }

    @Test
    public void whileLoop() {
        mo = While.cond(() -> F.unit(Math.random() < 0.5)).yield(() -> F.unit(1));
        mo = While.cond(() -> Future.succeededFuture(Math.random() < 0.5)).yield(() -> Future.succeededFuture(1));
    }

    @Test
    public void tryExceptComposeFinally() {
        mo = Try.code(() -> F.unit(1)).except(Throwable.class, t -> F.unit(2)).composeFinally(() -> F.unit(3));
        mo = Try.code(() -> Future.succeededFuture(1))
            .except(Throwable.class, t -> Future.succeededFuture(2))
            .composeFinally(() -> Future.succeededFuture(3));
    }

    @Test
    public void tryExceptExcept() {
        tryCatch = Try.code(() -> F.unit(1)).except(Throwable.class, t -> F.unit(2));
        tryCatch = Try.code(() -> Future.succeededFuture(1))
            .except(Throwable.class, t -> Future.succeededFuture(2));

        tryCatch = Try.code(() -> F.unit(1)).except(Throwable.class, t -> F.unit(2)).except(Error.class, t -> F.unit(3));
        tryCatch = Try.code(() -> Future.succeededFuture(1))
            .except(Throwable.class, t -> Future.succeededFuture(2))
            .except(Error.class, t -> Future.succeededFuture(3));
    }

    @Test
    public void tryComposeFinally() {
        mo = Try.code(() -> F.unit(1)).composeFinally(() -> F.unit(2));
        mo = Try.code(() -> Future.succeededFuture(1))
            .composeFinally(() -> Future.succeededFuture(2));
    }

    @Test
    public void tryExceptCompose() {
        mo = Try.code(() -> F.unit(1)).except(Throwable.class, t -> F.unit(2)).compose(i -> F.unit(3));
        mo = Try.code(() -> Future.succeededFuture(1))
            .except(Throwable.class, t -> Future.succeededFuture(2))
            .compose(i -> Future.succeededFuture(3));
    }

    @Test
    public void ifElseIfOtherwise() {
        mo = If.cond(F.unit(true)).run(() -> F.unit(1)).elseif(() -> F.unit(false)).run(() -> F.unit(2)).otherwise(() -> F.unit(3));
        mo = If.cond(Future.succeededFuture(true)).run(() -> Future.succeededFuture(1))
            .elseif(() -> Future.succeededFuture(false)).run(() -> Future.succeededFuture(2))
            .otherwise(() -> Future.succeededFuture(3));
    }

    @Test
    public void ifElseIfElseIf() {
        ifStmt = If.cond(F.unit(true)).run(() -> F.unit(1)).elseif(() -> F.unit(false)).run(() -> F.unit(2));
        ifStmt = If.cond(Future.succeededFuture(true)).run(() -> F.unit(1))
            .elseif(() -> F.unit(false)).run(() -> Future.succeededFuture(2));

        ifStmt = If.cond(F.unit(true)).run(() -> F.unit(1)).elseif(() -> F.unit(false)).run(() -> F.unit(2)).elseif(() -> F.unit(true)).run(() -> F.unit(3));
        ifStmt = If.cond(Future.succeededFuture(true)).run(() -> Future.succeededFuture(1))
            .elseif(() -> Future.succeededFuture(false)).run(() -> Future.succeededFuture(2))
            .elseif(() -> Future.succeededFuture(true)).run(() -> Future.succeededFuture(3));
    }

    @Test
    public void ifCompose() {
        mo = If.cond(F.unit(true)).run(() -> F.unit(1)).compose(i -> F.unit("a"));
        mo = If.cond(Future.succeededFuture(true)).run(() -> Future.succeededFuture(1)).compose(i -> Future.succeededFuture("a"));
    }

    @Test
    public void ifElseIfCompose() {
        mo = If.cond(F.unit(true)).run(() -> F.unit(1)).elseif(() -> F.unit(false)).run(() -> F.unit(2)).compose(i -> F.unit("a"));
        mo = If.cond(Future.succeededFuture(true)).run(() -> F.unit(1))
            .elseif(() -> F.unit(false)).run(() -> Future.succeededFuture(2))
            .compose(i -> Future.succeededFuture("a"));
    }

    interface X extends Function<Integer, Function<Integer, Function<Integer, Integer>>> {
    }

    @Test
    public void applicative() {
        F.unit((X) x -> y -> z -> x + y + z)
            .as(F::app).ap(F.unit(1))
            .as(F::app).ap(F.unit(2))
            .as(F::app).ap(F.unit(3))
            .setHandler(r -> {
                Assert.assertTrue(r.succeeded());
                Integer result = r.result();
                Assert.assertEquals(6, result.intValue());
            });
    }

    @Test
    public void applicativeComposeMap() {
        F.unit((X) x -> y -> z -> x + y + z)
            .as(F::app).compose(f -> F.unit());
        F.unit((X) x -> y -> z -> x + y + z)
            .as(F::app).map(f -> null);
    }

    @Test
    public void foreachResultList() {
        list = For.each(Collections.emptyList()).yield(F::unit).result();
        Assert.assertTrue(list instanceof Immutable);
    }

    @Test
    public void forResultList() {
        list = For.init(0).condSync(c -> false).incrSync(c -> {
        }).yield(c -> F.unit(0)).result();
        Assert.assertTrue(list instanceof Immutable);
    }

    @Test
    public void whileResultList() {
        list = While.cond(() -> false).yield(F::unit).result();
        Assert.assertTrue(list instanceof Immutable);
    }

    @Test
    public void monadUnit() {
        Monad.unit();
        Monad.unit(1);
    }

    @Test
    public void as() {
        Assert.assertEquals(1, Monad.unit().as(m -> 1).intValue());
        Assert.assertEquals(2, Monad.unit((Function<String, Integer>) Integer::parseInt).as(F::app).as(a -> 2).intValue());
        Assert.assertEquals(3, MList.unit().as(l -> 3).intValue());
    }
}
