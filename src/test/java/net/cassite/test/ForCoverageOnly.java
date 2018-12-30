package net.cassite.test;

import net.cassite.f.EventEmitter;
import net.cassite.f.MList;
import net.cassite.f.Symbol;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ForCoverageOnly {
    @Test
    public void misc() throws Exception {
        Class<?> cls = Class.forName("net.cassite.f.Misc");
        Constructor<?> cons = cls.getDeclaredConstructor();
        cons.setAccessible(true);
        cons.newInstance();
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "EqualsWithItself", "SimplifiableJUnitAssertion"})
    @Test
    public void consumerHandler() {
        EventEmitter e = new EventEmitter();
        Symbol<Integer> event = Symbol.create();
        e.on(event, data -> {
        });
        MList<Consumer<Integer>> ls = e.handlers(event);
        assertFalse(ls.get(0).equals(null));
        assertTrue(ls.get(0).equals(ls.get(0)));
        assertFalse(ls.get(0).equals((Consumer<Integer>) data -> {
        }));
    }
}
