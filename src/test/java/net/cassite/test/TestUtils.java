package net.cassite.test;

import net.cassite.f.MList;
import net.cassite.f.utils.MListOp;
import net.cassite.f.utils.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.Assert.*;

public class TestUtils {
    @Test
    public void pair() {
        Pair<String, Integer> pair = Pair.of("a", 1);
        assertEquals("a", pair.a);
        assertEquals(1, pair.b.intValue());

        assertEquals(pair, Pair.of("a", 1));
        assertNotEquals(pair, Pair.of("b", 1));
        assertNotEquals(pair, Pair.of("a", 2));

        assertEquals(pair.hashCode(), Pair.of("a", 1).hashCode());

        assertEquals("Pair(a, 1)", pair.toString());

        assertEquals(pair, pair);
        assertNotEquals(pair, null);
        assertNotEquals(pair, new Object());
    }

    @Test
    public void sum() {
        {
            MList<Integer> ls = MList.unit(1, 2, 3, 4);
            assertEquals(10, ls.as(MListOp::intOp).sum());
        }
        {
            MList<Long> ls = MList.unit(1L, 2L, 3L, 4L);
            assertEquals(10, ls.as(MListOp::longOp).sum());
        }
        {
            MList<Float> ls = MList.unit(1f, 2f, 3f, 4f);
            assertEquals(10, ls.as(MListOp::floatOp).sum(), 0);
        }
        {
            MList<Double> ls = MList.unit(1d, 2d, 3d, 4d);
            assertEquals(10, ls.as(MListOp::doubleOp).sum(), 0);
        }
    }

    @Test
    public void avg() {
        {
            MList<Integer> ls = MList.unit(1, 2, 3, 4);
            assertEquals(2.5, ls.as(MListOp::intOp).avg(), 0);
        }
        {
            MList<Long> ls = MList.unit(1L, 2L, 3L, 4L);
            assertEquals(2.5, ls.as(MListOp::longOp).avg(), 0);
        }
        {
            MList<Float> ls = MList.unit(1f, 2f, 3f, 4f);
            assertEquals(2.5, ls.as(MListOp::floatOp).avg(), 0);
        }
        {
            MList<Double> ls = MList.unit(1d, 2d, 3d, 4d);
            assertEquals(2.5, ls.as(MListOp::doubleOp).avg(), 0);
        }
    }

    @Test
    public void sort() {
        assertEquals(Arrays.asList("23", "123", "1234"),
            MList.unit("123", "23", "1234").as(MListOp::op).sort(Comparator.comparingInt(Integer::parseInt)));

        assertEquals(Arrays.asList(23, 123, 1234),
            MList.unit(123, 23, 1234).as(MListOp::intOp).sortAsc());
        assertEquals(Arrays.asList(1234, 123, 23),
            MList.unit(123, 23, 1234).as(MListOp::intOp).sortDesc());

        assertEquals(Arrays.asList(23L, 123L, 1234L),
            MList.unit(123L, 23L, 1234L).as(MListOp::longOp).sortAsc());
        assertEquals(Arrays.asList(1234L, 123L, 23L),
            MList.unit(123L, 23L, 1234L).as(MListOp::longOp).sortDesc());

        assertEquals(Arrays.asList(23f, 123f, 1234f),
            MList.unit(123f, 23f, 1234f).as(MListOp::floatOp).sortAsc());
        assertEquals(Arrays.asList(1234f, 123f, 23f),
            MList.unit(123f, 23f, 1234f).as(MListOp::floatOp).sortDesc());

        assertEquals(Arrays.asList(23d, 123d, 1234d),
            MList.unit(123d, 23d, 1234d).as(MListOp::doubleOp).sortAsc());
        assertEquals(Arrays.asList(1234d, 123d, 23d),
            MList.unit(123d, 23d, 1234d).as(MListOp::doubleOp).sortDesc());
    }

    @Test
    public void join() {
        MList<String> ls = MList.unit("a", "b", "c", "d");
        assertEquals("a,b,c,d", ls.as(MListOp::op).join(","));
        assertEquals("[a,b,c,d]", ls.as(MListOp::op).join("[", ",", "]"));
        assertEquals("", MList.unit().as(MListOp::op).join(","));
        assertEquals("[]", MList.unit().as(MListOp::op).join("[", ",", "]"));
    }
}
