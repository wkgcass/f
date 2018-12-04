package net.cassite.test;

import net.cassite.f.Export;
import net.cassite.f.Immutable;
import net.cassite.f.MList;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

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

    @Test
    public void collect() {
        MList<Integer> init = MList.unit(1, 3, 2, 4, 5);
        MList<String> result = init.stream().filter(i -> i <= 4).sorted().map(i -> "" + i).collect(MList.collector());
        Assert.assertEquals(MList.unit("1", "2", "3", "4"), result);
    }

    @Test
    public void collector() {
        Collector<Integer, MList<Integer>, MList<Integer>> collector = Export.collector();
        Assert.assertEquals(1, collector.characteristics().size());
        Assert.assertEquals(Collector.Characteristics.IDENTITY_FINISH, collector.characteristics().iterator().next());
        Assert.assertEquals("SimpleMutableMListImpl", collector.supplier().get().getClass().getSimpleName());
        MList<Integer> a = MList.modifiable();
        a.add(0);
        collector.accumulator().accept(a, 1);
        Assert.assertEquals(MList.unit(0, 1), a);
        MList<Integer> b = MList.modifiable();
        b.addAll(MList.unit(2, 3, 4));
        Assert.assertSame(a, collector.combiner().apply(a, b));
        Assert.assertEquals(MList.unit(0, 1, 2, 3, 4), a);
        Assert.assertSame(a, collector.finisher().apply(a));
    }

    @Test
    public void mutable() {
        MList<Integer> ls = MList.unit(1, 2, 3);
        Assert.assertTrue(ls instanceof Immutable);
        Assert.assertEquals(MList.unit(1, 2, 3), ls.mutable());
        ls.mutable().add(1); // pass

        ls = MList.modifiable();
        ls.addAll(MList.unit(1, 2, 3));
        Assert.assertSame(ls.mutable(), ls);
    }

    @Test
    public void subList() {
        MList<Integer> list = MList.modifiable();
        list.addAll(MList.unit(1, 2, 3, 4, 5));
        Assert.assertEquals(MList.unit(2, 3, 4), list.subList(1, 4));
        Assert.assertFalse(list.subList(1, 4) instanceof Immutable);
        list.subList(1, 4).add(123); // pass

        @SuppressWarnings("unchecked")
        MList<Integer>[] mList = new MList[]{null};
        Runnable r = () -> {
            Assert.assertEquals(MList.unit(2, 3, 4), mList[0].subList(1, 4));
            Assert.assertTrue(mList[0].subList(1, 4) instanceof Immutable);
        };

        list = MList.unit(1, 2, 3, 4, 5);
        mList[0] = list;
        r.run();

        list = MList.unit(0, 1, 2, 3, 4).map(i -> i + 1);
        mList[0] = list;
        r.run();

        list = MList.unit(0, 1, 2, 3, 4, 5).tail();
        mList[0] = list;
        r.run();

        list = MList.unit(1, 2, 3, 4, 5, 6).init();
        mList[0] = list;
        r.run();
    }
}
