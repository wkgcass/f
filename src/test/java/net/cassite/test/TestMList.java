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
        MList<Integer> ls = MList.unit();
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
        MList[] ls2 = new MList[]{ls.compose(mapper)};
        F.Procedure p = () -> {
            Assert.assertEquals(Arrays.asList(1, 2, 3), ls);
            Assert.assertEquals(Arrays.asList(11, 21, 12, 22, 13, 23), ls2[0]);
        };
        p.run();

        ls2[0] = ls.flatMap(mapper);
        p.run();

        ls2[0] = ls.bind(mapper);
        p.run();

        ls2[0] = ls.then(mapper);
        p.run();
    }

    @Test
    public void map() {
        MList<Integer> ls = MList.unit(1, 2, 3);
        Function<Integer, Integer> mapper = i -> i + 10;
        MList[] ls2 = new MList[]{ls.map(mapper)};
        F.Procedure p = () -> {
            Assert.assertEquals(Arrays.asList(1, 2, 3), ls);
            Assert.assertEquals(Arrays.asList(11, 12, 13), ls2[0]);
        };
        p.run();

        ls2[0] = ls.fmap(mapper);
        p.run();
    }
}
