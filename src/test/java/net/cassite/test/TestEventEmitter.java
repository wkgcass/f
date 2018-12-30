package net.cassite.test;

import net.cassite.f.*;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestEventEmitter {
    private int step = 0;

    @Test
    public void eventEmit() {
        Symbol<Integer> event = Symbol.create();
        IEventEmitter emitter = new EventEmitter();
        emitter.on(event, data -> {
            assertEquals(123, data.intValue());
            ++step;
        });
        emitter.emit(event, 123);
        assertEquals(1, step);
    }

    @Test
    public void on() {
        Symbol<Integer> event = Symbol.create();
        IEventEmitter emitter = new EventEmitter();
        emitter.on(event, data -> {
            assertEquals(1, data.intValue());
            ++step;
            assertTrue(1 == step || 3 == step);
        });
        emitter.on(event, data -> {
            assertEquals(1, data.intValue());
            ++step;
            assertTrue(2 == step || 4 == step);
        });
        emitter.emit(event, 1);
        emitter.emit(event, 1);
        assertEquals(4, step);
    }

    @Test
    public void once() {
        Symbol<Integer> event = Symbol.create();
        IEventEmitter emitter = new EventEmitter();
        emitter.once(event, data -> {
            assertEquals(1, data.intValue());
            ++step;
            assertEquals(1, step);
        });
        emitter.once(event, data -> {
            assertEquals(1, data.intValue());
            ++step;
            assertEquals(2, step);
        });
        emitter.emit(event, 1);
        emitter.emit(event, 1);
        assertEquals(2, step);
    }

    @Test
    public void removeAll() {
        Symbol<Integer> event = Symbol.create();
        EventEmitter emitter = new EventEmitter();
        emitter.on(event, data -> {
            assertEquals(1, data.intValue());
            ++step;
            assertEquals(1, step);
        });
        emitter.on(event, data -> {
            assertEquals(1, data.intValue());
            ++step;
            assertEquals(2, step);
        });
        emitter.emit(event, 1);
        emitter.removeAll(event);
        emitter.emit(event, 1);
        assertEquals(2, step);
    }

    @Test
    public void remove() {
        Symbol<Integer> event = Symbol.create();
        EventEmitter emitter = new EventEmitter();
        Consumer<Integer> handler = data -> {
            assertEquals(1, data.intValue());
            ++step;
            assertEquals(1, step);
        };
        Consumer<Integer> notAddedHandler = data -> {
        };
        emitter.remove(event, handler); // does nothing
        emitter.on(event, handler);
        emitter.remove(event, notAddedHandler);
        emitter.remove(event, handler);
        emitter.emit(event, 1);
        assertEquals(0, step);
        emitter.on(event, handler);
        emitter.on(event, data -> {
            assertEquals(1, data.intValue());
            ++step;
            assertTrue(2 == step || 3 == step);
        });
        emitter.emit(event, 1);
        assertEquals(2, step);
        emitter.remove(event, handler);
        emitter.emit(event, 1);
        assertEquals(3, step);
    }

    @Test
    public void handlers() {
        Symbol<Integer> event = Symbol.create();
        EventEmitter emitter = new EventEmitter();
        MList<Consumer<Integer>> handlers = emitter.handlers(event);
        assertTrue(handlers.isEmpty());

        Consumer<Integer> handler = data -> {
        };
        Consumer<Integer> handler2 = data -> {
        };
        emitter.on(event, handler);
        handlers = emitter.handlers(event);
        assertEquals(MList.unit(handler), handlers);
        emitter.on(event, handler2);
        handlers = emitter.handlers(event);
        assertEquals(MList.unit(handler, handler2), handlers);
    }

    @Test
    public void removeBothOnAndOnce() {
        Symbol<Integer> event = Symbol.create();
        EventEmitter emitter = new EventEmitter();
        Consumer<Integer> handler = data -> {
        };
        emitter.on(event, handler);
        emitter.once(event, handler);
        assertEquals(2, emitter.handlers(event).size());
        emitter.remove(event, handler);
        assertEquals(MList.unit(), emitter.handlers(event));
    }

    @Test
    public void error() {
        EventEmitter emitter = new EventEmitter();
        emitter.on(IEventEmitter.error, err -> {
            ++step;
            assertEquals(2, step);
            assertEquals("abc", err.getMessage());
        });
        Symbol<Integer> event = Symbol.create();
        emitter.on(event, data -> {
            ++step;
            assertEquals(1, step);
            throw new RuntimeException("abc");
        });
        emitter.emit(event, 1);
        assertEquals(2, step);
    }

    @Test
    public void onceFuture() {
        Symbol<Integer> event = Symbol.create();
        EventEmitter emitter = new EventEmitter();
        Monad<Integer> m = emitter.once(event);
        m.compose(i -> {
            assertEquals(1, i.intValue());
            ++step;
            assertEquals(1, step);
            return F.unit();
        });
        emitter.emit(event, 1);
        assertEquals(1, step);
        m = emitter.once(event);
        m.compose(i -> {
            assertNull(i);
            ++step;
            assertEquals(2, step);
            return F.unit();
        });
        emitter.emit(event, null);
        assertEquals(2, step);
    }
}
