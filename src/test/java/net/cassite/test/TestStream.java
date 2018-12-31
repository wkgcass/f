package net.cassite.test;

import net.cassite.f.F;
import net.cassite.f.stream.Publisher;
import net.cassite.f.stream.Stream;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestStream {
    private int step = 0;

    @SuppressWarnings("Duplicates")
    @Test
    public void setHandler() {
        Publisher<Integer> pub = Publisher.create();
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
        Publisher<Integer> pub = Publisher.create();
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
        Publisher<Integer> pub = Publisher.create();
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
        Publisher<Integer> pub = Publisher.create();
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
        Publisher<Integer> pub = Publisher.create();
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
        Publisher<Integer> pub = Publisher.create();
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
}
