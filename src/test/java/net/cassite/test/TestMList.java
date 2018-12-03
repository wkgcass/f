package net.cassite.test;

import net.cassite.f.F;
import net.cassite.f.MList;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class TestMList {
    @Test
    public void construct() {
        MList<Integer> ls = MList.modifiable();
        Assert.assertEquals(0, ls.size());
        //noinspection ConstantConditions
        Assert.assertTrue(ls.isEmpty());
        ls.add(1);
        Assert.assertEquals(1, ls.get(0).intValue());

        ls = MList.unit(Arrays.asList(1, 2, 3));
        Assert.assertEquals(3, ls.size());
        Assert.assertEquals(Arrays.asList(1, 2, 3), ls);

        ls = MList.unit(1, 2, 3);
        Assert.assertEquals(3, ls.size());
        Assert.assertEquals(Arrays.asList(1, 2, 3), ls);

        MList<Integer> lsx = MList.unit(ls);
        Assert.assertSame(lsx, ls);
    }

    @Test
    public void compose() {
        MList<Integer> ls = MList.unit(1, 2, 3);
        Function<Integer, List<Integer>> mapper = i -> MList.unit(i + 10, i + 20);
        MList[] ls2 = new MList[]{ls.flatMap(mapper)};
        Runnable p = () -> {
            Assert.assertEquals(Arrays.asList(1, 2, 3), ls);
            Assert.assertEquals(Arrays.asList(11, 21, 12, 22, 13, 23), ls2[0]);
        };
        p.run();
    }

    @Test
    public void map() {
        MList<Integer> ls = MList.unit(1, 2, 3);
        Function<Integer, Integer> mapper = i -> i + 10;
        MList[] ls2 = new MList[]{ls.map(mapper)};
        Runnable p = () -> {
            Assert.assertEquals(Arrays.asList(1, 2, 3), ls);
            Assert.assertEquals(Arrays.asList(11, 12, 13), ls2[0]);
        };
        p.run();
    }

    @Test
    public void immutable() {
        MList<Integer> ls = MList.modifiable();
        ls.add(1);
        ls.add(2);
        ls.add(3);

        ls.set(1, 4);
        Assert.assertEquals(Arrays.asList(1, 4, 3), ls);
        ls = ls.immutable();
        try {
            ls.set(1, 2);
            Assert.fail();
        } catch (UnsupportedOperationException ignore) {
        }
        Assert.assertEquals(Arrays.asList(1, 4, 3), ls);
        MList<Integer> ls2 = ls.immutable();
        Assert.assertSame(ls, ls2);
    }

    @Test
    public void lazyFunc() {
        MList<Integer> ls = MList.unit(1, 2, 3);
        ls = ls.map(n -> n + 10);
        Assert.assertEquals("LazyMListImpl", ls.getClass().getSimpleName());
        MList<String> ls2 = ls.map(n -> n + "");
        Assert.assertEquals("LazyMListImpl", ls2.getClass().getSimpleName());
        MList<String> ls3 = ls2.flatMap(s -> MList.unit(s, s + "x"));
        Assert.assertEquals("LazyMListImpl", ls3.getClass().getSimpleName());
        Assert.assertEquals("12", ls3.get(2));
        Assert.assertEquals(Arrays.asList("11", "11x", "12", "12x", "13", "13x"), ls3);
    }

    @Test
    public void head() {
        MList<Integer> ls = MList.unit(1, 2, 3);
        Assert.assertEquals(1, ls.head().intValue());
        ls = MList.unit();
        try {
            ls.head();
            Assert.fail();
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void tail() {
        MList<Integer> tail = MList.unit(1, 2, 3, 4).tail();
        Assert.assertEquals(Arrays.asList(2, 3, 4), tail);
        try {
            tail.get(3);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            Assert.assertEquals("Index: 3, Size: 3", e.getMessage());
        }
        try {
            tail.get(-1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            Assert.assertEquals("Index: -1, Size: 3", e.getMessage());
        }
        try {
            MList.unit().tail();
            Assert.fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void last() {
        MList<Integer> ls = MList.unit(1, 2, 3);
        Assert.assertEquals(3, ls.last().intValue());
        ls = MList.unit();
        try {
            ls.last();
            Assert.fail();
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void init() {
        MList<Integer> init = MList.unit(1, 2, 3, 4).init();
        Assert.assertEquals(Arrays.asList(1, 2, 3), init);
        try {
            init.get(3);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            Assert.assertEquals("Index: 3, Size: 3", e.getMessage());
        }
        try {
            init.get(-1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            Assert.assertEquals("Index: -1, Size: 3", e.getMessage());
        }
        try {
            MList.unit().init();
            Assert.fail();
        } catch (IllegalArgumentException ignore) {
        }
    }
}
