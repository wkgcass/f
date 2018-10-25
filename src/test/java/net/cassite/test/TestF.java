package net.cassite.test;

import net.cassite.f.F;
import org.junit.Assert;
import org.junit.Test;

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
}
