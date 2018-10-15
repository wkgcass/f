package net.cassite.test;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import net.cassite.f.For;
import net.cassite.f.Try;
import net.cassite.f.While;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class Example {
    @Test
    public void forExample() {
        Future<List<String>> fuListOfNames =
            Future.succeededFuture(Arrays.asList("mot", "yrrej", "ecila", "bob", "ave"));
        fuListOfNames.compose(list ->
            For.init(0).condSync(c -> c.i < list.size()).incrSync(c -> ++c.i)
                .yield(c -> {
                    String name = list.get(c.i);
                    String reversed = new StringBuilder(name).reverse().toString();
                    return Future.succeededFuture(reversed);
                })
        ).compose(results -> {
            Assert.assertEquals(Arrays.asList("tom", "jerry", "alice", "bob", "eva"), results);
            return Future.succeededFuture();
        });
    }

    @Test
    public void foreachExample() {
        Future<List<String>> fuListOfNames =
            Future.succeededFuture(Arrays.asList("mot", "yrrej", "ecila", "bob", "ave"));
        fuListOfNames.compose(list ->
            For.each(list).yield(name -> {
                String reversed = new StringBuilder(name).reverse().toString();
                return Future.succeededFuture(reversed);
            })
        ).compose(results -> {
            Assert.assertEquals(Arrays.asList("tom", "jerry", "alice", "bob", "eva"), results);
            return Future.succeededFuture();
        });
    }

    @Test
    public void whileExample() {
        Future<List<String>> fuListOfNames =
            Future.succeededFuture(Arrays.asList("mot", "yrrej", "ecila", "bob", "ave"));
        fuListOfNames.compose(list -> {
            int[] i = {0};
            return While.cond(() -> i[0] < list.size())
                .yield(() -> {
                    String name = list.get(i[0]);
                    String reversed = new StringBuilder(name).reverse().toString();
                    return Future.succeededFuture(reversed);
                });
        }).compose(results -> {
            Assert.assertEquals(Arrays.asList("tom", "jerry", "alice", "bob", "eva"), results);
            return Future.succeededFuture();
        });
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
            return Future.succeededFuture("caught");
        }).composeFinally(() -> {
            reaches[0] = true;
            return Future.succeededFuture();
        }).compose(s -> {
            Assert.assertEquals("caught", s);
            finished[0] = true;
            return Future.succeededFuture();
        });

        while (!finished[0]) {
            Thread.sleep(1);
        }
        Assert.assertTrue(reaches[0]);
    }
}
