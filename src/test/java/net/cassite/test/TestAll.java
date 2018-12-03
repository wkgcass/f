package net.cassite.test;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import net.cassite.f.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TestAll {
    private Vertx vertx;

    @Before
    public void setUp() {
        vertx = Vertx.vertx();
    }

    @After
    public void tearDown() {
        vertx.close();
    }

    private <T> Handler<AsyncResult<T>> assertOk() {
        return r -> {
            if (r.failed()) {
                r.cause().printStackTrace();
            }
            assertTrue(r.succeeded());
        };
    }

    private Monad<String> capToUpper(String e) {
        return F.unit(e.substring(0, 1).toUpperCase() + e.substring(1));
    }

    private Monad<?> doAssert(List<String> list) {
        assertEquals(Arrays.asList("Tom", "Jerry", "Alice", "Bob", "Eva"), list);
        return F.unit();
    }

    private <T> void assertFuture(Future<T> fu) {
        boolean[] finished = {false};
        Throwable[] t = {null};
        fu.setHandler(r -> {
            try {
                assertTrue(r.succeeded());
            } catch (Throwable tt) {
                t[0] = tt;
            }
            finished[0] = true;
        });
        while (!finished[0]) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (t[0] != null) {
            t[0].printStackTrace();
        }
        assertNull(t[0]);
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
        For.init(0).cond(c -> F.unit(c.i < array.length)).incrSync(c -> ++c.i)
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
        For.init(0).condSync(c -> c.i < array.length).incr(c -> F.unit(++c.i))
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
        For.init(0).cond(c -> F.unit(c.i < array.length)).incr(c -> F.unit(++c.i))
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
        While.cond(() -> F.unit(i[0] < array.length)).yield(() ->
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
            F.unit(233)
        ).except(Throwable.class, t ->
            F.unit(-1)
        ).compose(i -> {
            assertEquals(233, i.intValue());
            ++ii[0];
            return F.unit();
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
            return F.unit();
        }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void tryFails() {
        int[] ii = {0};
        Try.code(() -> {
            Monad<Integer> f = F.tbd();
            f.fail(new Exception("123"));
            return f;
        }).except(Throwable.class, t -> {
            String msg = t.getMessage();
            return F.unit(Integer.parseInt(msg));
        }).compose(i -> {
            assertEquals(123, i.intValue());
            ++ii[0];
            return F.unit();
        }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void tryMultiExcept() {
        int[] ii = {0};
        Try.code(() -> {
            Monad<Integer> f = F.tbd();
            f.fail(new IllegalArgumentException("123"));
            return f;
        }).except(AssertionError.class, t ->
            F.unit(456)
        ).except(IllegalArgumentException.class, t -> {
            String msg = t.getMessage();
            return F.unit(Integer.parseInt(msg));
        }).compose(i -> {
            assertEquals(123, i.intValue());
            ++ii[0];
            return F.unit();
        }).setHandler(assertOk());
        assertEquals(1, ii[0]);
    }

    @Test
    public void tryNotCaught() {
        int[] i = {0};
        Try.code(() -> {
            Monad<Integer> f = F.tbd();
            f.fail(new IllegalArgumentException("123"));
            return f;
        }).except(AssertionError.class, t ->
            F.unit(456)
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
            Monad<Integer> f = F.tbd();
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
            Monad<Integer> f = F.tbd();
            f.fail(new IllegalArgumentException("123"));
            return f;
        }).except(IllegalArgumentException.class, t -> {
            Monad<Integer> f = F.tbd();
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
            Monad<Integer> f = F.tbd();
            f.fail(new Exception("123"));
            return f;
        }).except(Throwable.class, t -> {
            String msg = t.getMessage();
            return F.unit(Integer.parseInt(msg));
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
            Monad<Integer> f = F.tbd();
            f.fail(new Exception("123"));
            return f;
        }).except(Throwable.class, t -> {
            String msg = t.getMessage();
            return F.unit(Integer.parseInt(msg));
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
        Try.code(() -> F.unit(123)
        ).composeFinally(() -> {
            ++i[0];
            return F.unit();
        }).compose(v -> {
            assertEquals(123, v.intValue());
            ++j[0];
            return F.unit();
        }).setHandler(assertOk());
        assertEquals(1, i[0]);
        assertEquals(1, j[0]);
    }

    @Test
    public void tryExceptionFinally() {
        int[] i = {0};
        int[] j = {0};
        Try.code(() -> {
            Monad<Integer> f = F.tbd();
            f.fail(new IllegalArgumentException("123"));
            return f;
        }).composeFinally(() -> {
            ++i[0];
            return F.unit();
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
            Monad<Integer> f = F.tbd();
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
            F.unit(123)
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
            Monad<Integer> f = F.tbd();
            f.fail(new IllegalArgumentException("123"));
            return f;
        }).composeFinally(() -> {
            ++i[0];
            return F.fail(new UnsupportedOperationException("456"));
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
            F.unit(123)
        ).composeFinally(() -> {
            ++i[0];
            return F.fail(new UnsupportedOperationException("456"));
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
            return F.unit();
        }).composeFinally(() -> {
            ++jj[0];
            return F.unit();
        }).setHandler(assertOk());
        assertEquals(1, ii[0]);
        assertEquals(1, jj[0]);
    }

    @Test
    public void tryExceptAddSameThrowableType() {
        try {
            Try.code(() -> F.unit(123))
                .except(IllegalArgumentException.class, t -> F.unit(1))
                .except(IllegalArgumentException.class, t -> F.unit(2));
            fail();
        } catch (Error e) {
            assertEquals("try-expression already has handler for java.lang.IllegalArgumentException", e.getMessage());
        }
    }

    @Test
    public void trySequence() throws InterruptedException {
        int[] step = {0};
        boolean[] finished = {false};

        Try.code(() -> {
            assertEquals(0, step[0]++);
            Monad<String> fu = F.tbd();
            vertx.setTimer(1, l -> fu.complete("a"));
            return fu;
        }).except(Throwable.class, t -> {
            fail();
            return F.unit("x");
        }).composeFinally(() -> {
            assertEquals(1, step[0]++);
            return F.unit();
        }).compose(s -> {
            assertEquals("a", s);
            assertEquals(2, step[0]++);
            finished[0] = true;
            return F.unit();
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
            Monad<String> fu = F.tbd();
            vertx.setTimer(10, l -> {
                assertEquals(0, step[0]++);
                fu.fail(new Exception("a"));
            });
            return fu;
        }).except(Throwable.class, t -> {
            assertEquals("a", t.getMessage());
            Monad<String> fu = F.tbd();
            vertx.setTimer(5, l -> {
                assertEquals(1, step[0]++);
                fu.complete("x");
            });
            return fu;
        }).composeFinally(() -> {
            Monad<String> fu = F.tbd();
            vertx.setTimer(1, l -> {
                assertEquals(2, step[0]++);
                fu.complete();
            });
            return fu;
        }).compose(s -> {
            assertEquals("x", s);
            assertEquals(3, step[0]++);
            finished[0] = true;
            return F.unit();
        }).setHandler(assertOk());

        while (!finished[0]) {
            Thread.sleep(1);
        }
        vertx.close();
    }

    @Test
    public void forLoopNull() {
        int[] i = {0};
        For.init(0).condSync(c -> c.i < 10).incrSync(c -> ++c.i).yield(c -> {
            if (c.i % 3 == 0) return F.unit();
            return F.unit(c.i);
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
            if (i % 3 == 0) return F.unit();
            return F.unit(i);
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
            if (n % 3 == 0) return F.unit();
            return F.unit(n);
        }).map(list -> {
            assertEquals(Arrays.asList(1, 2, 4, 5, 7, 8), list);
            ++i[0];
            return null;
        }).setHandler(assertOk());
        assertEquals(1, i[0]);
    }

    @Test
    public void forEachRealAsyncYield() throws Exception {
        Vertx v = Vertx.vertx();
        boolean[] finished = {false};

        For.each(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)).yield(i -> {
            Monad<Integer> fu = F.tbd();
            v.setTimer(10, l -> {
                if (i % 3 == 0) fu.complete();
                else fu.complete(i);
            });
            return fu;
        }).map(list -> {
            assertEquals(Arrays.asList(1, 2, 4, 5, 7, 8), list);
            finished[0] = true;
            return null;
        }).setHandler(assertOk());

        while (finished[0]) {
            Thread.sleep(1);
        }
        v.close();
    }

    @Test
    public void forLoopRealAsyncYield() throws Exception {
        Vertx v = Vertx.vertx();
        boolean[] finished = {false};

        For.init(0).condSync(c -> c.i < 10).incrSync(c -> ++c.i).yield(c -> {
            Monad<Integer> fu = F.tbd();
            v.setTimer(10, l -> {
                if (c.i % 3 == 0) fu.complete();
                else fu.complete(c.i);
            });
            return fu;
        }).map(list -> {
            assertEquals(Arrays.asList(1, 2, 4, 5, 7, 8), list);
            finished[0] = true;
            return null;
        }).setHandler(assertOk());

        while (finished[0]) {
            Thread.sleep(1);
        }
        v.close();
    }

    @Test
    public void whileRealAsyncYield() throws Exception {
        Vertx v = Vertx.vertx();
        boolean[] finished = {false};
        int[] current = {0};

        While.cond(() -> current[0] < 10).yield(() -> {
            Monad<Integer> fu = F.tbd();
            v.setTimer(10, l -> {
                int n = current[0];
                ++current[0];
                if (n % 3 == 0) fu.complete();
                else fu.complete(n);
            });
            return fu;
        }).map(list -> {
            assertEquals(Arrays.asList(1, 2, 4, 5, 7, 8), list);
            finished[0] = true;
            return null;
        }).setHandler(assertOk());

        while (finished[0]) {
            Thread.sleep(1);
        }
        v.close();
    }

    @Test
    public void ifTrueCompose() {
        If.cond(F.unit(true)).run(() -> F.unit(123))
            .compose(i -> {
                assertEquals(123, i.intValue());
                return F.unit();
            }).setHandler(assertOk());
    }

    @Test
    public void ifElseTrueCompose() {
        If.cond(F.unit(false)).run(() -> F.unit(456))
            .elseif(() -> F.unit(true)).run(() -> F.unit(123))
            .compose(i -> {
                assertEquals(123, i.intValue());
                return F.unit();
            }).setHandler(assertOk());
    }

    @Test
    public void ifElseOtherwiseCompose() {
        If.cond(F.unit(false)).run(() -> F.unit(789))
            .elseif(() -> F.unit(false)).run(() -> F.unit(456))
            .otherwise(() -> F.unit(123))
            .compose(i -> {
                assertEquals(123, i.intValue());
                return F.unit();
            }).setHandler(assertOk());
    }

    @Test
    public void ifOtherwiseCompose() {
        If.cond(F.unit(false)).run(() -> F.unit(456))
            .otherwise(() -> F.unit(123))
            .compose(i -> {
                assertEquals(123, i.intValue());
                return F.unit();
            }).setHandler(assertOk());
    }

    @Test
    public void ifComposeFail() {
        If.cond(F.unit(false)).run(() -> F.unit(123))
            .compose(i -> null)
            .setHandler(r -> {
                assertTrue(r.failed());
                assertTrue(r.cause() instanceof net.cassite.f.MatchError);
                assertEquals("clear into `otherwise`, but default condition not specified", r.cause().getMessage());
            });
    }

    @Test
    public void ifElseComposeFail() {
        If.cond(F.unit(false)).run(() -> F.unit(456))
            .elseif(() -> F.unit(false)).run(() -> F.unit(123))
            .compose(i -> null)
            .setHandler(r -> {
                assertTrue(r.failed());
                assertTrue(r.cause() instanceof net.cassite.f.MatchError);
                assertEquals("clear into `otherwise`, but default condition not specified", r.cause().getMessage());
            });
    }

    @Test
    public void ifOrElseTrueWithOtherwise() {
        If.cond(F.unit(true)).run(() -> F.unit(789))
            .elseif(() -> F.unit(false)).run(() -> F.unit(456))
            .otherwise(() -> F.unit(123))
            .compose(i -> {
                assertEquals(789, i.intValue());
                return F.unit();
            }).setHandler(assertOk());
        If.cond(F.unit(false)).run(() -> F.unit(789))
            .elseif(() -> F.unit(true)).run(() -> F.unit(456))
            .otherwise(() -> F.unit(123))
            .compose(i -> {
                assertEquals(456, i.intValue());
                return F.unit();
            }).setHandler(assertOk());
    }

    @Test
    public void ifElseMap() {
        If.cond(F.unit(false)).run(() -> F.unit(456))
            .elseif(() -> F.unit(true)).run(() -> F.unit(123))
            .map(i -> {
                assertEquals(123, i.intValue());
                return 1024;
            })
            .map(i -> {
                assertEquals(1024, i.intValue());
                return null;
            }).setHandler(assertOk());
    }

    @Test
    public void ifElseSetHandler() {
        boolean[] reached = {false};
        If.cond(F.unit(false)).run(() -> F.unit(456))
            .elseif(() -> F.unit(true)).run(() -> F.unit(123))
            .setHandler(r -> {
                assertEquals(123, r.result().intValue());
                reached[0] = true;
            });
        assertTrue(reached[0]);
    }

    @Test
    public void forBreak() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        For.init(0).condSync(c -> c.i < list.size()).incrSync(c -> ++c.i).yield(c -> {
            if (c.i >= 2) {
                return F.brk();
            }
            return F.unit(list.get(c.i) + 10);
        }).compose(ls -> {
            assertEquals(Arrays.asList(11, 12), ls);
            return F.unit();
        }).setHandler(assertOk());
    }

    @Test
    public void forBreakWithValue() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        For.init(0).condSync(c -> c.i < list.size()).incrSync(c -> ++c.i).yield(c -> {
            if (c.i >= 2) {
                return F.brk(33);
            }
            return F.unit(list.get(c.i) + 10);
        }).compose(ls -> {
            assertEquals(Arrays.asList(11, 12, 33), ls);
            return F.unit();
        }).setHandler(assertOk());
    }

    @Test
    public void forBreakFuture() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        assertFuture(For.init(0).condSync(c -> c.i < list.size()).incrSync(c -> ++c.i).yield(c -> {
            Monad<Integer> mo = F.tbd();
            vertx.setTimer(1, l -> {
                if (c.i >= 2) {
                    try {
                        F.brk();
                    } catch (Throwable t) {
                        mo.fail(t);
                    }
                } else {
                    mo.complete(list.get(c.i) + 10);
                }
            });
            return mo;
        }).compose(ls -> {
            assertEquals(Arrays.asList(11, 12), ls);
            return F.unit();
        }));
    }

    @Test
    public void forBreakWithValueFuture() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        assertFuture(For.init(0).condSync(c -> c.i < list.size()).incrSync(c -> ++c.i).yield(c -> {
            Monad<Integer> mo = F.tbd();
            vertx.setTimer(1, l -> {
                if (c.i >= 2) {
                    try {
                        F.brk(33);
                    } catch (Throwable t) {
                        mo.fail(t);
                    }
                } else {
                    mo.complete(list.get(c.i) + 10);
                }
            });
            return mo;
        }).compose(ls -> {
            assertEquals(Arrays.asList(11, 12, 33), ls);
            return F.unit();
        }));
    }

    @Test
    public void forThrowFuture() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        assertFuture(For.init(0).condSync(c -> c.i < list.size()).incrSync(c -> ++c.i).yield(c -> {
            Monad<Integer> mo = F.tbd();
            vertx.setTimer(1, l -> mo.fail(new UnsupportedOperationException("a")));
            return mo;
        }).recover(t -> {
            assertTrue(t instanceof UnsupportedOperationException);
            assertEquals("a", t.getMessage());
            return F.unit(MList.unit(Collections.emptyList()));
        }));
    }

    @Test
    public void foreachBreak() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        For.each(list).yield(i -> {
            if (i >= 3) {
                return F.brk();
            }
            return F.unit(i + 10);
        }).compose(ls -> {
            assertEquals(Arrays.asList(11, 12), ls);
            return F.unit();
        }).setHandler(assertOk());
    }

    @Test
    public void foreachBreakWithValue() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        For.each(list).yield(i -> {
            if (i >= 3) {
                return F.brk(33);
            }
            return F.unit(i + 10);
        }).compose(ls -> {
            assertEquals(Arrays.asList(11, 12, 33), ls);
            return F.unit();
        }).setHandler(assertOk());
    }

    @Test
    public void foreachBreakFuture() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        assertFuture(For.each(list).yield(i -> {
            Monad<Integer> mo = F.tbd();
            vertx.setTimer(1, l -> {
                if (i >= 3) {
                    try {
                        F.brk();
                    } catch (Throwable t) {
                        mo.fail(t);
                    }
                } else {
                    mo.complete(i + 10);
                }
            });
            return mo;
        }).compose(ls -> {
            assertEquals(Arrays.asList(11, 12), ls);
            return F.unit();
        }));
    }

    @Test
    public void foreachBreakWithValueFuture() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        assertFuture(For.each(list).yield(i -> {
            Monad<Integer> mo = F.tbd();
            vertx.setTimer(1, l -> {
                if (i >= 3) {
                    try {
                        F.brk(33);
                    } catch (Throwable t) {
                        mo.fail(t);
                    }
                } else {
                    mo.complete(i + 10);
                }
            });
            return mo;
        }).compose(ls -> {
            assertEquals(Arrays.asList(11, 12, 33), ls);
            return F.unit();
        }));
    }

    @Test
    public void foreachThrowFuture() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        assertFuture(For.each(list).yield(i -> {
            Monad<Integer> mo = F.tbd();
            vertx.setTimer(1, l -> mo.fail(new IllegalArgumentException("a")));
            return mo;
        }).recover(t -> {
            assertTrue(t instanceof IllegalArgumentException);
            assertEquals("a", t.getMessage());
            return F.unit(MList.unit(Collections.emptyList()));
        }));
    }

    @Test
    public void whileBreak() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        int[] i = {0};
        While.cond(() -> i[0] < list.size()).yield(() -> {
            if (i[0] >= 2) {
                return F.brk();
            }
            return F.unit(list.get(i[0]++) + 10);
        }).compose(ls -> {
            assertEquals(Arrays.asList(11, 12), ls);
            return F.unit();
        }).setHandler(assertOk());
    }

    @Test
    public void whileBreakWithValue() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        int[] i = {0};
        While.cond(() -> i[0] < list.size()).yield(() -> {
            if (i[0] >= 2) {
                return F.brk(33);
            }
            return F.unit(list.get(i[0]++) + 10);
        }).compose(ls -> {
            assertEquals(Arrays.asList(11, 12, 33), ls);
            return F.unit();
        }).setHandler(assertOk());
    }

    @Test
    public void whileBreakFuture() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        int[] i = {0};
        assertFuture(While.cond(() -> i[0] < list.size()).yield(() -> {
            Monad<Integer> mo = F.tbd();
            vertx.setTimer(1, l -> {
                if (i[0] >= 2) {
                    try {
                        F.brk();
                    } catch (Throwable t) {
                        mo.fail(t);
                    }
                } else {
                    mo.complete(list.get(i[0]++) + 10);
                }
            });
            return mo;
        }).compose(ls -> {
            assertEquals(Arrays.asList(11, 12), ls);
            return F.unit();
        }));
    }

    @Test
    public void whileBreakWithValueFuture() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        int[] i = {0};
        assertFuture(While.cond(() -> i[0] < list.size()).yield(() -> {
            Monad<Integer> mo = F.tbd();
            vertx.setTimer(1, l -> {
                if (i[0] >= 2) {
                    try {
                        F.brk(33);
                    } catch (Throwable t) {
                        mo.fail(t);
                    }
                } else {
                    mo.complete(list.get(i[0]++) + 10);
                }
            });
            return mo;
        }).compose(ls -> {
            assertEquals(Arrays.asList(11, 12, 33), ls);
            return F.unit();
        }));
    }

    @Test
    public void flip() {
        List<Future<Integer>> list = new ArrayList<>();
        for (int i = 1; i <= 3; ++i) {
            int x = i;
            Future<Integer> f = Future.future();
            vertx.setTimer((5 - i) * 100, l -> f.complete(x));
            list.add(f);
        }
        assertFuture(F.flip(list).compose(ls -> {
            assertEquals(Arrays.asList(1, 2, 3), ls);
            return F.unit();
        }));
    }

    @Test
    public void flipThrow() throws InterruptedException {
        List<Future<Integer>> list = new ArrayList<>();
        for (int i = 1; i <= 3; ++i) {
            int x = i;
            Future<Integer> f = Future.future();
            vertx.setTimer((5 - i) * 100, l -> {
                if (x == 2) {
                    f.fail(new IllegalArgumentException("a"));
                } else {
                    f.complete(x);
                }
            });
            list.add(f);
        }
        assertFuture(F.flip(list).recover(ex -> {
            assertTrue(ex instanceof IllegalArgumentException);
            assertEquals("a", ex.getMessage());
            return F.unit();
        }));
        Thread.sleep(1000);
    }

    @Test
    public void flipThrow2() throws InterruptedException {
        List<Future<Integer>> list = new ArrayList<>();
        for (int i = 1; i <= 3; ++i) {
            int x = i;
            Future<Integer> f = Future.future();
            vertx.setTimer((5 - i) * 100, l -> {
                if (x == 2 || x == 3) {
                    f.fail(new IllegalArgumentException("a" + x));
                } else {
                    f.complete(x);
                }
            });
            list.add(f);
        }
        assertFuture(F.flip(list).recover(ex -> {
            assertTrue(ex instanceof IllegalArgumentException);
            assertEquals("a3", ex.getMessage());
            return F.unit();
        }));
        Thread.sleep(1000);
    }

    @Test
    public void flowExecFuture() {
        Ptr<Integer> p = Ptr.nil();
        Ptr<Double> d = Ptr.of(1.2);
        Flow.exec(() -> p.store(F.unit(1)))
            .exec(() -> d.store(F.unit(2D)))
            .exec(() -> p.unary(Op::leftIncr))
            .exec(() -> p.store(p.bin(Op::plus, F.unit(d.value.intValue()))))
            .returnPtr(p)
            .compose(pp -> {
                assertEquals(4, pp.intValue());
                assertEquals(2D, d.value, 0);
                return F.unit();
            })
            .setHandler(assertOk());
    }

    @Test
    public void flowExecProcess() {
        Ptr<Integer> p = Ptr.nil();
        Flow.exec(() -> p.value = 3)
            .returnFuture(() -> F.unit(p.value))
            .compose(pp -> {
                assertEquals(3, pp.intValue());
                return F.unit();
            })
            .setHandler(assertOk());
        Ptr<Integer> q = Ptr.nil();
        Flow.exec(() -> q.value = 3).returnValue(() -> q.value)
            .setHandler(r -> assertEquals(3, r.result().intValue()));
    }

    @Test
    public void ptrStoreFail() {
        Ptr<Integer> p = Ptr.of(1);
        Monad<Integer> m = p.store(F.fail("err"));
        m.setHandler(r -> {
            assertTrue(r.failed());
            assertEquals("err", r.cause().getMessage());
        });
    }
}
