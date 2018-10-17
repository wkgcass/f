package net.cassite.test;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import net.cassite.f.For;
import net.cassite.f.Try;
import net.cassite.f.While;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TestAll {
    private <T> Handler<AsyncResult<T>> assertOk() {
        return r -> {
            if (r.failed()) {
                r.cause().printStackTrace();
            }
            assertTrue(r.succeeded());
        };
    }

    private Future<String> capToUpper(String e) {
        return Future.succeededFuture(e.substring(0, 1).toUpperCase() + e.substring(1));
    }

    private Future<?> doAssert(List<String> list) {
        assertEquals(Arrays.asList("Tom", "Jerry", "Alice", "Bob", "Eva"), list);
        return Future.succeededFuture();
    }

    @Test
    public void foreachArray() {
        int[] ii = {0};
        String[] array = {"tom", "jerry", "alice", "bob", "eva"};
        For.each(array).yield(this::capToUpper).compose(list -> {
            ++ii[0];
            return doAssert(list);
        }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void foreachList() {
        int[] ii = {0};
        String[] array = {"tom", "jerry", "alice", "bob", "eva"};
        For.each(Arrays.asList(array)).yield(this::capToUpper)
            .compose(list -> {
                ++ii[0];
                return doAssert(list);
            }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void foreachIterable() {
        int[] ii = {0};
        String[] array = {"tom", "jerry", "alice", "bob", "eva"};
        For.each(Arrays.stream(array).iterator()).yield(this::capToUpper)
            .compose(list -> {
                ++ii[0];
                return doAssert(list);
            }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void forSyncCondSyncIncr() {
        int[] ii = {0};
        String[] array = {"tom", "jerry", "alice", "bob", "eva"};
        For.init(0).condSync(c -> c.i < array.length).incrSync(c -> ++c.i)
            .yield(c -> capToUpper(array[c.i]))
            .compose(list -> {
                ++ii[0];
                return doAssert(list);
            }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void forCondSyncIncr() {
        int[] ii = {0};
        String[] array = {"tom", "jerry", "alice", "bob", "eva"};
        For.init(0).cond(c -> Future.succeededFuture(c.i < array.length)).incrSync(c -> ++c.i)
            .yield(c -> capToUpper(array[c.i]))
            .compose(list -> {
                ++ii[0];
                return doAssert(list);
            }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void forSyncCondIncr() {
        int[] ii = {0};
        String[] array = {"tom", "jerry", "alice", "bob", "eva"};
        For.init(0).condSync(c -> c.i < array.length).incr(c -> Future.succeededFuture(++c.i))
            .yield(c -> capToUpper(array[c.i]))
            .compose(list -> {
                ++ii[0];
                return doAssert(list);
            }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void forCondIncr() {
        int[] ii = {0};
        String[] array = {"tom", "jerry", "alice", "bob", "eva"};
        For.init(0).cond(c -> Future.succeededFuture(c.i < array.length)).incr(c -> Future.succeededFuture(++c.i))
            .yield(c -> capToUpper(array[c.i]))
            .compose(list -> {
                ++ii[0];
                return doAssert(list);
            }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void whileSyncCond() {
        int[] ii = {0};
        String[] array = {"tom", "jerry", "alice", "bob", "eva"};
        int[] i = {0};
        While.cond(() -> i[0] < array.length).yield(() ->
            capToUpper(array[i[0]]).map(e -> {
                ++i[0];
                return e;
            }))
            .compose(list -> {
                ++ii[0];
                return doAssert(list);
            })
            .setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void whileCond() {
        int[] ii = {0};
        String[] array = {"tom", "jerry", "alice", "bob", "eva"};
        int[] i = {0};
        While.cond(() -> Future.succeededFuture(i[0] < array.length)).yield(() ->
            capToUpper(array[i[0]]).map(e -> {
                ++i[0];
                return e;
            }))
            .compose(list -> {
                ++ii[0];
                return doAssert(list);
            })
            .setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void tryNoException() {
        int[] ii = {0};
        Try.code(() ->
            Future.succeededFuture(233)
        ).except(Throwable.class, t ->
            Future.succeededFuture(-1)
        ).compose(i -> {
            assertEquals(233, i.intValue());
            ++ii[0];
            return Future.succeededFuture();
        }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void tryThrows() {
        int[] ii = {0};
        Try.code(() -> {
            throw new Error("123");
        }).except(Throwable.class, t -> {
            assertEquals("123", t.getMessage());
            assertTrue(t instanceof Error);
            ++ii[0];
            return Future.succeededFuture();
        }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void tryFails() {
        int[] ii = {0};
        Try.code(() -> {
            Future<Integer> f = Future.future();
            f.fail(new Exception("123"));
            return f;
        }).except(Throwable.class, t -> {
            String msg = t.getMessage();
            return Future.succeededFuture(Integer.parseInt(msg));
        }).compose(i -> {
            assertEquals(123, i.intValue());
            ++ii[0];
            return Future.succeededFuture();
        }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void tryMultiExcept() {
        int[] ii = {0};
        Try.code(() -> {
            Future<Integer> f = Future.future();
            f.fail(new IllegalArgumentException("123"));
            return f;
        }).except(AssertionError.class, t ->
            Future.succeededFuture(456)
        ).except(IllegalArgumentException.class, t -> {
            String msg = t.getMessage();
            return Future.succeededFuture(Integer.parseInt(msg));
        }).compose(i -> {
            assertEquals(123, i.intValue());
            ++ii[0];
            return Future.succeededFuture();
        }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void tryNotCaught() {
        int[] i = {0};
        Try.code(() -> {
            Future<Integer> f = Future.future();
            f.fail(new IllegalArgumentException("123"));
            return f;
        }).except(AssertionError.class, t ->
            Future.succeededFuture(456)
        ).map(v -> v
        ).setHandler(r -> {
            assertTrue(r.failed());
            assertEquals("123", r.cause().getMessage());
            assertTrue(r.cause() instanceof IllegalArgumentException);
            ++i[0];
        });
        assertEquals(1, i[0]);
    }

    @Test
    public void tryExceptThrow() {
        int[] i = {0};
        Try.code(() -> {
            Future<Integer> f = Future.future();
            f.fail(new IllegalArgumentException("123"));
            return f;
        }).except(IllegalArgumentException.class, t -> {
            String msg = "" + (Integer.parseInt(t.getMessage()) + 456);
            throw new UnsupportedOperationException(msg);
        }).map(v -> v
        ).setHandler(r -> {
            assertTrue(r.failed());
            assertEquals("579", r.cause().getMessage());
            assertTrue(r.cause() instanceof UnsupportedOperationException);
            ++i[0];
        });
        assertEquals(1, i[0]);
    }

    @Test
    public void tryExceptFail() {
        int[] i = {0};
        Try.code(() -> {
            Future<Integer> f = Future.future();
            f.fail(new IllegalArgumentException("123"));
            return f;
        }).except(IllegalArgumentException.class, t -> {
            Future<Integer> f = Future.future();
            String msg = "" + (Integer.parseInt(t.getMessage()) + 456);
            f.fail(new UnsupportedOperationException(msg));
            return f;
        }).map(v -> v
        ).setHandler(r -> {
            assertTrue(r.failed());
            assertEquals("579", r.cause().getMessage());
            assertTrue(r.cause() instanceof UnsupportedOperationException);
            ++i[0];
        });
        assertEquals(1, i[0]);
    }

    @Test
    public void trySetHandler() {
        int[] i = {0};
        Try.code(() -> {
            Future<Integer> f = Future.future();
            f.fail(new Exception("123"));
            return f;
        }).except(Throwable.class, t -> {
            String msg = t.getMessage();
            return Future.succeededFuture(Integer.parseInt(msg));
        }).setHandler(r -> {
            assertTrue(r.succeeded());
            assertEquals(123, r.result().intValue());
            ++i[0];
        });
        assertEquals(1, i[0]);
    }

    @Test
    public void tryMap() {
        int[] i = {0};
        Try.code(() -> {
            Future<Integer> f = Future.future();
            f.fail(new Exception("123"));
            return f;
        }).except(Throwable.class, t -> {
            String msg = t.getMessage();
            return Future.succeededFuture(Integer.parseInt(msg));
        }).map(v -> v + 1).setHandler(r -> {
            assertTrue(r.succeeded());
            assertEquals(124, r.result().intValue());
            ++i[0];
        });
        assertEquals(1, i[0]);
    }

    @Test
    public void tryFinally() {
        int[] i = {0};
        int[] j = {0};
        Try.code(() -> Future.succeededFuture(123)
        ).composeFinally(() -> {
            ++i[0];
            return Future.succeededFuture();
        }).compose(v -> {
            assertEquals(123, v.intValue());
            ++j[0];
            return Future.succeededFuture();
        }).setHandler(assertOk());
        assertEquals(1, i[0]);
        assertEquals(1, j[0]);
    }

    @Test
    public void tryExceptionFinally() {
        int[] i = {0};
        int[] j = {0};
        Try.code(() -> {
            Future<Integer> f = Future.future();
            f.fail(new IllegalArgumentException("123"));
            return f;
        }).composeFinally(() -> {
            ++i[0];
            return Future.succeededFuture();
        }).setHandler(r -> {
            ++j[0];
            assertTrue(r.failed());
            assertEquals("123", r.cause().getMessage());
            assertTrue(r.cause() instanceof IllegalArgumentException);
        });
        assertEquals(1, i[0]);
        assertEquals(1, j[0]);
    }

    @Test
    public void tryCodeFailAndFinallyThrow() {
        int[] i = {0};
        int[] j = {0};
        Try.code(() -> {
            Future<Integer> f = Future.future();
            f.fail(new IllegalArgumentException("123"));
            return f;
        }).composeFinally(() -> {
            ++i[0];
            throw new UnsupportedOperationException("456");
        }).setHandler(r -> {
            ++j[0];
            assertTrue(r.failed());
            assertEquals("456", r.cause().getMessage());
            assertTrue(r.cause() instanceof UnsupportedOperationException);
        });
        assertEquals(1, i[0]);
        assertEquals(1, j[0]);
    }

    @Test
    public void tryFinallyThrow() {
        int[] i = {0};
        int[] j = {0};
        Try.code(() ->
            Future.succeededFuture(123)
        ).composeFinally(() -> {
            ++i[0];
            throw new UnsupportedOperationException("456");
        }).setHandler(r -> {
            ++j[0];
            assertTrue(r.failed());
            assertEquals("456", r.cause().getMessage());
            assertTrue(r.cause() instanceof UnsupportedOperationException);
        });
        assertEquals(1, i[0]);
        assertEquals(1, j[0]);
    }

    @Test
    public void tryCodeFailAndFinallyFail() {
        int[] i = {0};
        int[] j = {0};
        Try.code(() -> {
            Future<Integer> f = Future.future();
            f.fail(new IllegalArgumentException("123"));
            return f;
        }).composeFinally(() -> {
            ++i[0];
            return Future.failedFuture(new UnsupportedOperationException("456"));
        }).setHandler(r -> {
            ++j[0];
            assertTrue(r.failed());
            assertEquals("456", r.cause().getMessage());
            assertTrue(r.cause() instanceof UnsupportedOperationException);
        });
        assertEquals(1, i[0]);
        assertEquals(1, j[0]);
    }

    @Test
    public void tryFinallyFail() {
        int[] i = {0};
        int[] j = {0};
        Try.code(() ->
            Future.succeededFuture(123)
        ).composeFinally(() -> {
            ++i[0];
            return Future.failedFuture(new UnsupportedOperationException("456"));
        }).setHandler(r -> {
            ++j[0];
            assertTrue(r.failed());
            assertEquals("456", r.cause().getMessage());
            assertTrue(r.cause() instanceof UnsupportedOperationException);
        });
        assertEquals(1, i[0]);
        assertEquals(1, j[0]);
    }

    @Test
    public void tryExceptFinally() {
        int[] ii = {0};
        int[] jj = {0};
        Try.code(() -> {
            throw new Error("123");
        }).except(Throwable.class, t -> {
            assertEquals("123", t.getMessage());
            assertTrue(t instanceof Error);
            ++ii[0];
            return Future.succeededFuture();
        }).composeFinally(() -> {
            ++jj[0];
            return Future.succeededFuture();
        }).setHandler(assertOk());
        assertEquals(1, ii[0]);
        assertEquals(1, jj[0]);
    }

    @Test
    public void tryExceptAddSameThrowableType() {
        try {
            Try.code(() -> Future.succeededFuture(123))
                .except(IllegalArgumentException.class, t -> Future.succeededFuture(1))
                .except(IllegalArgumentException.class, t -> Future.succeededFuture(2));
            fail();
        } catch (Error e) {
            assertEquals("try-expression already has handler for java.lang.IllegalArgumentException", e.getMessage());
        }
    }

    @Test
    public void trySequence() throws InterruptedException {
        int[] step = {0};
        boolean[] finished = {false};
        Vertx vertx = Vertx.vertx();

        Try.code(() -> {
            assertEquals(0, step[0]++);
            Future<String> fu = Future.future();
            vertx.setTimer(1, l -> fu.complete("a"));
            return fu;
        }).except(Throwable.class, t -> {
            fail();
            return Future.succeededFuture("x");
        }).composeFinally(() -> {
            assertEquals(1, step[0]++);
            return Future.succeededFuture();
        }).compose(s -> {
            assertEquals("a", s);
            assertEquals(2, step[0]++);
            finished[0] = true;
            return Future.succeededFuture();
        }).setHandler(assertOk());

        while (!finished[0]) {
            Thread.sleep(1);
        }
    }

    @Test
    public void tryCatchSequence() throws InterruptedException {
        int[] step = {0};
        boolean[] finished = {false};
        Vertx vertx = Vertx.vertx();

        Try.code(() -> {
            assertEquals(0, step[0]++);
            Future<String> fu = Future.future();
            vertx.setTimer(1, l -> fu.fail(new Exception("a")));
            return fu;
        }).except(Throwable.class, t -> {
            assertEquals(1, step[0]++);
            assertEquals("a", t.getMessage());
            return Future.succeededFuture("x");
        }).composeFinally(() -> {
            assertEquals(2, step[0]++);
            return Future.succeededFuture();
        }).compose(s -> {
            assertEquals("x", s);
            assertEquals(3, step[0]++);
            finished[0] = true;
            return Future.succeededFuture();
        }).setHandler(assertOk());

        while (!finished[0]) {
            Thread.sleep(1);
        }
    }

    @Test
    public void forLoopNull() {
        int[] i = {0};
        For.init(0).condSync(c -> c.i < 10).incrSync(c -> ++c.i).yield(c -> {
            if (c.i % 3 == 0) return Future.succeededFuture();
            return Future.succeededFuture(c.i);
        }).map(list -> {
            assertEquals(Arrays.asList(1, 2, 4, 5, 7, 8), list);
            ++i[0];
            return null;
        }).setHandler(assertOk());
        assertEquals(1, i[0]);
    }

    @Test
    public void forEachNull() {
        int[] ii = {0};
        For.each(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)).yield(i -> {
            if (i % 3 == 0) return Future.succeededFuture();
            return Future.succeededFuture(i);
        }).map(list -> {
            assertEquals(Arrays.asList(1, 2, 4, 5, 7, 8), list);
            ++ii[0];
            return null;
        }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void whileNull() {
        int[] i = {0};
        int[] current = {0};
        While.cond(() -> current[0] < 10).yield(() -> {
            int n = current[0];
            ++current[0];
            if (n % 3 == 0) return Future.succeededFuture();
            return Future.succeededFuture(n);
        }).map(list -> {
            assertEquals(Arrays.asList(1, 2, 4, 5, 7, 8), list);
            ++i[0];
            return null;
        }).setHandler(assertOk());
        assertEquals(1, i[0]);
    }
}
