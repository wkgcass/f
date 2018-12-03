package net.cassite.test;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import net.cassite.f.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;

public class Example {
    private <T> Handler<AsyncResult<T>> assertOk() {
        return r -> {
            if (r.failed()) {
                r.cause().printStackTrace();
            }
            assertTrue(r.succeeded());
        };
    }

    @Test
    public void monadBindExample() {
        Monad<String> m = F.unit("mot");
        Monad<String> m2 = m.compose(s -> F.unit(new StringBuilder(s).reverse().toString()));
        Assert.assertEquals("tom", m2.result());
    }

    @Test
    public void functorFmapExample() {
        Monad<String> m = F.unit("mot");
        Monad<String> m2 = m.map(s -> new StringBuilder(s).reverse().toString());
        Assert.assertEquals("tom", m2.result());
    }

    interface X extends Function<Integer, Function<Integer, Function<Integer, Integer>>> {
    }

    @Test
    public void applicativeApExample() {
        F.unit((X) a -> b -> c -> a + b + c)
            .as(F::app).ap(F.unit(1))
            .as(F::app).ap(F.unit(2))
            .as(F::app).ap(F.unit(3))
            .compose(i -> {
                Assert.assertEquals(6, i.intValue());
                return F.unit();
            }).setHandler(assertOk());
    }

    @Test
    public void forExample() {
        Future<List<String>> fuListOfNames =
            F.unit(Arrays.asList("mot", "yrrej", "ecila", "bob", "ave"));
        fuListOfNames.compose(list ->
            For.init(0).condSync(c -> c.i < list.size()).incrSync(c -> ++c.i)
                .yield(c -> {
                    String name = list.get(c.i);
                    String reversed = new StringBuilder(name).reverse().toString();
                    return F.unit(reversed);
                })
        ).compose(results -> {
            Assert.assertEquals(Arrays.asList("tom", "jerry", "alice", "bob", "eva"), results);
            return F.unit();
        }).setHandler(assertOk());
    }

    @Test
    public void foreachExample() {
        Future<List<String>> fuListOfNames =
            F.unit(Arrays.asList("mot", "yrrej", "ecila", "bob", "ave"));
        fuListOfNames.compose(list ->
            For.each(list).yield(name -> {
                String reversed = new StringBuilder(name).reverse().toString();
                return F.unit(reversed);
            })
        ).compose(results -> {
            Assert.assertEquals(Arrays.asList("tom", "jerry", "alice", "bob", "eva"), results);
            return F.unit();
        }).setHandler(assertOk());
    }

    @Test
    public void whileExample() {
        Future<List<String>> fuListOfNames =
            F.unit(Arrays.asList("mot", "yrrej", "ecila", "bob", "ave"));
        fuListOfNames.compose(list -> {
            int[] i = {0};
            return While.cond(() -> i[0] < list.size())
                .yield(() -> F.value(F.unit(new StringBuilder(list.get(i[0])).reverse().toString()), () -> ++i[0]));
        }).compose(results -> {
            Assert.assertEquals(Arrays.asList("tom", "jerry", "alice", "bob", "eva"), results);
            return F.unit();
        }).setHandler(assertOk());
    }

    private Vertx vertx;

    @Before
    public void setUp() {
        vertx = Vertx.vertx();
    }

    @After
    public void tearDown() {
        vertx.close();
    }

    @Test
    public void tryExample() throws InterruptedException {
        boolean[] reaches = {false};
        boolean[] finished = {false};

        Try.code(() -> {
            Future<String> f = Future.future();
            vertx.setTimer(1, l -> f.fail(new IllegalArgumentException("throw me")));
            return f;
        }).except(IllegalArgumentException.class, ex -> {
            Assert.assertEquals("throw me", ex.getMessage());
            return F.unit("caught");
        }).composeFinally(() -> {
            reaches[0] = true;
            return F.unit();
        }).compose(s -> {
            Assert.assertEquals("caught", s);
            finished[0] = true;
            return F.unit();
        }).setHandler(assertOk());

        while (!finished[0]) {
            Thread.sleep(1);
        }
        Assert.assertTrue(reaches[0]);
    }

    @Test
    public void ifExample() {
        runTest(F.unit(3), "0 ~ 5");
        runTest(F.unit(7), "5 ~ 10");
        runTest(F.unit(12), "10 ~");
        runTest(F.unit(-1), "~ 0");
    }

    private void runTest(Future<Integer> test, String expectedResult) {
        boolean[] reaches = {false};

        If.cond(test.map(i -> 0 < i && i <= 5)).run(() -> F.unit("0 ~ 5"))
            .elseif(() -> test.map(i -> 5 < i && i <= 10)).run(() -> F.unit("5 ~ 10"))
            .elseif(() -> test.map(i -> i > 10)).run(() -> F.unit("10 ~"))
            .otherwise(() -> F.unit("~ 0"))

            .compose(s -> {
                Assert.assertEquals(expectedResult, s);
                reaches[0] = true;
                return F.unit();
            }).setHandler(assertOk());
        Assert.assertTrue(reaches[0]);
    }

    @Test
    public void loopBreakExample() {
        Future<List<String>> fuListOfNames =
            F.unit(Arrays.asList("mot", "yrrej", "ecila", "bob", "ave"));
        fuListOfNames.compose(list ->
            For.each(list).yield(name -> {
                if (name.equals("bob")) return F.brk("sam");
                String reversed = new StringBuilder(name).reverse().toString();
                return F.unit(reversed);
            })
        ).compose(results -> {
            Assert.assertEquals(Arrays.asList("tom", "jerry", "alice", "sam"), results);
            return F.unit();
        }).setHandler(assertOk());
    }

    @Test
    public void mList() {
        MList<Integer> list = MList.unit(1, 2, 3);
        list = list.map(i -> MList.unit(i, i + 10)).flatMap(l -> l).immutable();
        Assert.assertEquals(Arrays.asList(1, 11, 2, 12, 3, 13), list);
    }

    @Test
    public void flow() {

        // define variables

        Ptr<Integer> a = Ptr.nil();
        Ptr<Integer> b = Ptr.nil();
        Ptr<Integer> c = Ptr.nil();

        // run

        Flow.exec(() -> a.value = 3)
            .exec(() -> b.store(a.unary(Op::leftIncr)))
            .exec(() -> c.store(F.unit(a.value + b.value)))
            .returnPtr(c)
            .map(cc -> {
                Assert.assertEquals(8, cc.intValue());
                return null;
            })
            .setHandler(assertOk());
    }
}
