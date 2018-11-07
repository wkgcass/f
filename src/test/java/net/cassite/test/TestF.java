package net.cassite.test;

import io.vertx.core.Vertx;
import net.cassite.f.F;
import net.cassite.f.Monad;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestF {
    @Test
    public void unit0() {
        Assert.assertNull(F.unit().result());
    }

    @Test
    public void unit1() {
        Assert.assertEquals("a", F.unit("a").result());
    }

    @Test
    public void failEx() {
        Throwable t = F.fail(new Error("a")).cause();
        Assert.assertTrue(t instanceof Error);
        Assert.assertEquals("a", t.getMessage());
    }

    @Test
    public void failStr() {
        Throwable t = F.fail("a").cause();
        Assert.assertEquals("a", t.getMessage());
    }

    @Test
    public void runcbFail() throws Exception {
        Vertx v = Vertx.vertx();
        Monad<List<String>> m = F.runcb(cb -> v.fileSystem().readDir("klsdjklfjdsklfjl乱七八糟的路径randompath", cb));
        while (!m.isComplete()) {
            Thread.sleep(1);
        }
        Assert.assertTrue(m.failed());
        v.close();
    }

    @Test
    public void runcbDone() throws Exception {
        Vertx v = Vertx.vertx();
        Monad<String> m = F.runcb(cb -> v.setTimer(1000, l -> cb.handle(F.unit("abc"))));
        while (!m.isComplete()) {
            Thread.sleep(1);
        }
        Assert.assertTrue(m.succeeded());
        Assert.assertEquals("abc", m.result());
        v.close();
    }
}
