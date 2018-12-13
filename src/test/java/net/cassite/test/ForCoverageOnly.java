package net.cassite.test;

import org.junit.Test;

import java.lang.reflect.Constructor;

public class ForCoverageOnly {
    @Test
    public void misc() throws Exception {
        Class<?> cls = Class.forName("net.cassite.f.Misc");
        Constructor<?> cons = cls.getDeclaredConstructor();
        cons.setAccessible(true);
        cons.newInstance();
    }
}
