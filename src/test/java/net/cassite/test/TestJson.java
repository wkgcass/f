package net.cassite.test;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.json.Json;
import net.cassite.f.MList;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestJson {
    @Test
    public void encode() {
        MList<Integer> list = MList.unit(1, 2, 3);
        String json = Json.encode(list);
        assertEquals("[1,2,3]", json);
    }

    @SuppressWarnings("WeakerAccess")
    public static class ToDecode {
        public MList<Integer> list;
    }

    @Test
    public void decode() {
        ToDecode td = Json.decodeValue("{\"list\":[1,2,3]}", ToDecode.class);
        assertNotNull(td.list);
        assertEquals(MList.unit(1, 2, 3), td.list);
    }

    @SuppressWarnings("WeakerAccess")
    public static class DecodeComplex {
        public String str;
        public int i;
        public MList<ToDecode> dec;
        public MList<DecodeComplex> complex;
    }

    @Test
    public void decodeComplex() {
        DecodeComplex complex = Json.decodeValue("" +
            "{" +
            /**/"\"str\":\"hello\"," +
            /**/"\"i\":123," +
            /**/"\"dec\":[" +
            /**//**/"{" +
            /**//**//**/"\"list\":[1,2,3]" +
            /**//**/"}," +
            /**//**/"{" +
            /**//**//**/"\"list\":[4,5,6]" +
            /**//**/"}" +
            /**/"]," +
            /**/"\"complex\":[" +
            /**//**/"{" +
            /**//**//**/"\"str\":\"world\"," +
            /**//**//**/"\"i\":4," +
            /**//**//**/"\"dec\":[" +
            /**//**//**//**/"{" +
            /**//**//**//**//**/"\"list\":[7,8,9]" +
            /**//**//**//**/"}," +
            /**//**//**//**/"{" +
            /**//**//**//**//**/"\"list\":[10,11]" +
            /**//**//**//**/"}" +
            /**//**//**/"]" +
            /**//**/"}," +
            /**//**/"{" +
            /**//**//**/"\"str\":\"f\"," +
            /**//**//**/"\"i\":8," +
            /**//**//**/"\"complex\":[]" +
            /**//**/"}" +
            /**/"]" +
            "}", DecodeComplex.class);
        assertEquals("hello", complex.str);
        assertEquals(123, complex.i);
        assertEquals(2, complex.dec.size());
        {
            ToDecode _0 = complex.dec.get(0);
            assertEquals(MList.unit(1, 2, 3), _0.list);
            ToDecode _1 = complex.dec.get(1);
            assertEquals(MList.unit(4, 5, 6), _1.list);
        }
        assertEquals(2, complex.complex.size());
        {
            DecodeComplex _0 = complex.complex.get(0);
            assertEquals("world", _0.str);
            assertEquals(4, _0.i);
            assertNull(_0.complex);
            assertEquals(2, _0.dec.size());
            {
                ToDecode _00 = _0.dec.get(0);
                assertEquals(MList.unit(7, 8, 9), _00.list);
                ToDecode _01 = _0.dec.get(1);
                assertEquals(MList.unit(10, 11), _01.list);
            }

            DecodeComplex _1 = complex.complex.get(1);
            assertEquals("f", _1.str);
            assertEquals(8, _1.i);
            assertNull(_1.dec);
            assertEquals(0, _1.complex.size());
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class DecodeRaw {
        public MList list;
        public MList<?> listWild;
    }

    @Test
    public void decodeRaw() {
        DecodeRaw dr = Json.decodeValue("{\"list\":[1,\"2\",3],\"listWild\":[4,\"5\",6]}", DecodeRaw.class);
        assertEquals(MList.unit(1, "2", 3), dr.list);
        assertEquals(MList.unit(4, "5", 6), dr.listWild);
    }

    @Test
    public void decodePlain() {
        MList<Integer> list = Json.decodeValue("[1,2,3]", new TypeReference<MList<Integer>>() {
        });
        assertEquals(MList.unit(1, 2, 3), list);
    }
}
