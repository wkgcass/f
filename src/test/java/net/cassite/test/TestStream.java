package net.cassite.test;

import net.cassite.f.F;
import net.cassite.f.Symbol;
import net.cassite.f.stream.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestStream {
    private int step = 0;

    @SuppressWarnings("Duplicates")
    @Test
    public void setHandler() {
        SimplePublisher<Integer> pub = Publisher.create();
        Stream<Integer> stream = pub.subscribe();
        stream.setHandler(r -> {
            assertTrue(r.succeeded());
            assertEquals(1, r.result().intValue());
            ++step;
            assertEquals(1, step);
        });
        pub.publish(1);
        assertEquals(1, step);
    }

    @Test
    public void map() {
        SimplePublisher<Integer> pub = Publisher.create();
        pub.subscribe().map(i -> i + 10).setHandler(r -> {
            assertTrue(r.succeeded());
            assertEquals(11, r.result().intValue());
            ++step;
            assertEquals(1, step);
        });
        pub.publish(1);
        assertEquals(1, step);
    }

    @Test
    public void compose() {
        SimplePublisher<Integer> pub = Publisher.create();
        pub.subscribe().compose(i -> F.unit(i + 20)).setHandler(r -> {
            assertTrue(r.succeeded());
            assertEquals(21, r.result().intValue());
            ++step;
            assertEquals(1, step);
        });
        pub.publish(1);
        assertEquals(1, step);
    }

    @Test
    public void streamHandlerFail() {
        int[] i = {0};
        int[] j = {0};
        SimplePublisher<Integer> pub = Publisher.create();
        Stream<Integer> stream = pub.subscribe();
        stream.setHandler(r -> {
            if (r.failed()) {
                ++j[0];
                throw new RuntimeException();
            }
            ++i[0];
            throw new RuntimeException();
        });
        pub.publish(1);
        assertEquals(1, i[0]);
        assertEquals(1, j[0]);
        pub.fail(new RuntimeException());
        assertEquals(2, j[0]);
    }

    @Test
    public void streamComposeFailedFuture() {
        SimplePublisher<Integer> pub = Publisher.create();
        Stream<Integer> stream = pub.subscribe();
        stream.compose(i -> F.fail("abc")).setHandler(r -> {
            assertTrue(r.failed());
            assertEquals("abc", r.cause().getMessage());
            ++step;
            assertEquals(1, step);
        });
        pub.publish(1);
        assertEquals(1, step);
    }

    @Test
    public void streamComposeThrow() {
        SimplePublisher<Integer> pub = Publisher.create();
        Stream<Integer> stream = pub.subscribe();
        stream.compose(i -> {
            throw new RuntimeException("abc");
        }).setHandler(r -> {
            assertTrue(r.failed());
            assertEquals("abc", r.cause().getMessage());
            ++step;
            assertEquals(1, step);
        });
        pub.publish(1);
        assertEquals(1, step);
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void streamSubscribeCloseRemoveSelf() {
        int[] i = {0};
        int[] j = {0};
        int[] k = {0};
        int[] l = {0};
        SimplePublisher<Integer> pub = Publisher.create();
        Stream<Integer> stream = pub.subscribe();
        stream.setHandler(r -> {
            if (r.failed()) {
                ++k[0];
                assertEquals(1, k[0]);
                assertTrue(r.cause() instanceof HandlerRemovedException);
                ++l[0];
                assertEquals(1, l[0]);
                return;
            }
            ++i[0];
            assertEquals(1, i[0]);
            assertTrue(r.succeeded());
            assertEquals(100, r.result().intValue());
            ++j[0];
            assertEquals(1, j[0]);
        });
        pub.publish(100);
        stream.close();
        pub.publish(100);
        assertEquals(1, i[0]);
        assertEquals(1, j[0]);
        assertEquals(1, k[0]);
        assertEquals(1, l[0]);
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void streamMapCloseRemoveSelf() {
        int[] i = {0};
        int[] j = {0};
        int[] k = {0};
        int[] l = {0};
        SimplePublisher<Integer> pub = Publisher.create();
        Stream<Integer> stream = pub.subscribe().map(x -> x + 10);
        stream.setHandler(r -> {
            if (r.failed()) {
                ++k[0];
                assertEquals(1, k[0]);
                assertTrue(r.cause() instanceof HandlerRemovedException);
                ++l[0];
                assertEquals(1, l[0]);
                return;
            }
            ++i[0];
            assertEquals(1, i[0]);
            assertTrue(r.succeeded());
            assertEquals(110, r.result().intValue());
            ++j[0];
            assertEquals(1, j[0]);
        });
        pub.publish(100);
        stream.close();
        pub.publish(100);
        assertEquals(1, i[0]);
        assertEquals(1, j[0]);
        assertEquals(1, k[0]);
        assertEquals(1, l[0]);
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void streamComposeCloseRemoveSelf() {
        int[] i = {0};
        int[] j = {0};
        int[] k = {0};
        int[] l = {0};
        SimplePublisher<Integer> pub = Publisher.create();
        Stream<Integer> stream = pub.subscribe().compose(x -> F.unit(x + 10));
        stream.setHandler(r -> {
            if (r.failed()) {
                ++k[0];
                assertEquals(1, k[0]);
                assertTrue(r.cause() instanceof HandlerRemovedException);
                ++l[0];
                assertEquals(1, l[0]);
                return;
            }
            ++i[0];
            assertEquals(1, i[0]);
            assertTrue(r.succeeded());
            assertEquals(110, r.result().intValue());
            ++j[0];
            assertEquals(1, j[0]);
        });
        pub.publish(100);
        stream.close();
        pub.publish(100);
        assertEquals(1, i[0]);
        assertEquals(1, j[0]);
        assertEquals(1, k[0]);
        assertEquals(1, l[0]);
    }

    @Test
    public void alreadyClosedStream() {
        Stream<Integer> stream = Publisher.<Integer>create().subscribe();
        assertFalse(stream.isClosed());
        stream.close();
        assertTrue(stream.isClosed());
        try {
            stream.map(Object::toString);
            fail();
        } catch (IllegalStateException ignore) {
        }
        try {
            stream.compose(i -> F.unit(i.toString()));
            fail();
        } catch (IllegalStateException ignore) {
        }
        try {
            stream.setHandler(r -> {
            });
            fail();
        } catch (IllegalStateException ignore) {
        }
        try {
            stream.subscribe();
            fail();
        } catch (IllegalStateException ignore) {
        }
    }

    @Test
    public void plainPublisherClose() {
        SimplePublisher<Integer> pub = Publisher.create();
        pub.subscribe().setHandler(r -> {
            assertTrue(r.failed());
            assertTrue(r.cause() instanceof HandlerRemovedException);
            ++step;
        });
        assertFalse(pub.isClosed());
        pub.close();
        assertEquals(1, step);
        assertTrue(pub.isClosed());
    }

    @Test
    public void eventEmitterPublisherClose() {
        EventEmitter emitter = EventEmitter.create();
        Symbol<Integer> symbol = Symbol.create();
        Stream<Integer> stream = emitter.on(symbol);
        stream.setHandler(r -> {
            assertTrue(r.failed());
            assertTrue(r.cause() instanceof HandlerRemovedException);
            ++step;
        });
        emitter.removeAll(symbol);
        assertEquals(1, step);
    }
}
