package net.cassite.test;

import io.vertx.core.Future;
import net.cassite.f.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class TestOp {
    private static final List<BiFunction<Integer, Integer, Integer>> intOperators = Arrays.asList(
        (a, b) -> a + b,
        (a, b) -> a - b,
        (a, b) -> a * b,
        (a, b) -> a / b,
        (a, b) -> a % b,
        (a, b) -> a & b,
        (a, b) -> a | b
    );
    private static final List<BiFunction<Float, Float, Float>> floatOperators = Arrays.asList(
        (a, b) -> a + b,
        (a, b) -> a - b,
        (a, b) -> a * b,
        (a, b) -> a / b,
        (a, b) -> a % b
    );
    private static final List<BiFunction<Long, Long, Long>> longOperators = Arrays.asList(
        (a, b) -> a + b,
        (a, b) -> a - b,
        (a, b) -> a * b,
        (a, b) -> a / b,
        (a, b) -> a % b,
        (a, b) -> a & b,
        (a, b) -> a | b
    );
    private static final List<BiFunction<Double, Double, Double>> doubleOperators = Arrays.asList(
        (a, b) -> a + b,
        (a, b) -> a - b,
        (a, b) -> a * b,
        (a, b) -> a / b,
        (a, b) -> a % b
    );
    private static final List<BiFunction<Byte, Byte, Byte>> byteOperators = Arrays.asList(
        (a, b) -> (byte) (a + b),
        (a, b) -> (byte) (a - b),
        (a, b) -> (byte) (a * b),
        (a, b) -> (byte) (a / b),
        (a, b) -> (byte) (a % b),
        (a, b) -> (byte) (a & b),
        (a, b) -> (byte) (a | b)
    );
    private static final List<BiFunction<Short, Short, Short>> shortOperators = Arrays.asList(
        (a, b) -> (short) (a + b),
        (a, b) -> (short) (a - b),
        (a, b) -> (short) (a * b),
        (a, b) -> (short) (a / b),
        (a, b) -> (short) (a % b),
        (a, b) -> (short) (a & b),
        (a, b) -> (short) (a | b)
    );
    private static final List<List<? extends BiFunction>> binFuncs = Arrays.asList(
        intOperators, longOperators, byteOperators, shortOperators, floatOperators, doubleOperators
    );

    private static final Supplier<Integer> intSupplier = () -> (int) (Math.random() * 100) + 1;
    private static final Supplier<Float> floatSupplier = () -> (float) (Math.random() * 100) + 1;
    private static final Supplier<Double> doubleSupplier = () -> Math.random() * 100 + 1;
    private static final Supplier<Long> longSupplier = () -> (long) (Math.random() * 100) + 1;
    private static final Supplier<Short> shortSupplier = () -> (short) (Math.random() * 20 + 1);
    private static final Supplier<Byte> byteSupplier = () -> (byte) (Math.random() * 20 + 1);
    private static final List<Supplier> suppliers = Arrays.asList(
        intSupplier, longSupplier, byteSupplier, shortSupplier, floatSupplier, doubleSupplier
    );

    private static final int IDX_INT = 0;
    // private static final int IDX_LNG = 1;
    // private static final int IDX_BYT = 2;
    // private static final int IDX_SHT = 3;
    private static final int IDX_FLT = 4;
    private static final int IDX_DBL = 5;

    private static final int IDX_PLS = 0;
    private static final int IDX_MNS = 1;
    private static final int IDX_MUL = 2;
    private static final int IDX_DIV = 3;
    private static final int IDX_MOD = 4;
    private static final int IDX_BITAND = 5;
    private static final int IDX_BITOR = 6;

    @SuppressWarnings("Duplicates")
    @Test
    public void binFuncs() {
        for (int i = IDX_PLS; i <= IDX_BITOR; ++i) {
            BiFunction<Ptr, Future, Object> func;
            switch (i) {
                case IDX_PLS:
                    func = Op::plus;
                    break;
                case IDX_MNS:
                    func = Op::minus;
                    break;
                case IDX_MUL:
                    func = Op::multiply;
                    break;
                case IDX_DIV:
                    func = Op::divide;
                    break;
                case IDX_MOD:
                    func = Op::mod;
                    break;
                case IDX_BITAND:
                    func = Op::bitAnd;
                    break;
                case IDX_BITOR:
                    func = Op::bitOr;
                    break;
                default:
                    throw new RuntimeException();
            }
            for (int j = IDX_INT; j <= IDX_DBL; ++j) {
                if (i >= IDX_BITAND && j >= IDX_FLT) continue;
                Supplier sup = suppliers.get(j);
                Object a = sup.get();
                Object b = sup.get();
                Ptr pa = Ptr.of(a);
                Future fb = F.unit(b);
                List<? extends BiFunction> ops = binFuncs.get(j);
                BiFunction op = ops.get(i);
                @SuppressWarnings("unchecked") Object res = pa.bin(func, fb).result();
                @SuppressWarnings("unchecked") Object exp = op.apply(a, b);
                assertEquals(exp, res);
                assertEquals(a, pa.get()); // unchanged
            }
        }
    }

    @Test
    public void unary() {
        Ptr<Integer> i = Ptr.of(1);
        assertEquals(2, i.unary(Op::leftIncr).result().intValue());
        assertEquals(2, i.get().intValue());
        assertEquals(2, i.unary(Op::rightIncr).result().intValue());
        assertEquals(3, i.get().intValue());

        assertEquals(2, i.unary(Op::leftDecr).result().intValue());
        assertEquals(2, i.get().intValue());
        assertEquals(2, i.unary(Op::rightDecr).result().intValue());
        assertEquals(1, i.get().intValue());
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void binAsn() {
        for (int i = IDX_PLS; i <= IDX_BITOR; ++i) {
            BiFunction<Ptr, Future, Object> func;
            switch (i) {
                case IDX_PLS:
                    func = Op::incr;
                    break;
                case IDX_MNS:
                    func = Op::decr;
                    break;
                case IDX_MUL:
                    func = Op::mulAsn;
                    break;
                case IDX_DIV:
                    func = Op::divAsn;
                    break;
                case IDX_MOD:
                    func = Op::modAsn;
                    break;
                case IDX_BITAND:
                    func = Op::bitAndAsn;
                    break;
                case IDX_BITOR:
                    func = Op::bitOrAsn;
                    break;
                default:
                    throw new RuntimeException();
            }
            for (int j = IDX_INT; j <= IDX_DBL; ++j) {
                if (i >= IDX_BITAND && j >= IDX_FLT) continue;
                Supplier sup = suppliers.get(j);
                Object a = sup.get();
                Object b = sup.get();
                Ptr pa = Ptr.of(a);
                Future fb = F.unit(b);
                List<? extends BiFunction> ops = binFuncs.get(j);
                BiFunction op = ops.get(i);
                @SuppressWarnings("unchecked") Object res = pa.bin(func, fb).result();
                @SuppressWarnings("unchecked") Object exp = op.apply(a, b);
                assertEquals(exp, res);
                assertEquals(exp, pa.get()); // changed
            }
        }
    }

    @SuppressWarnings({"Duplicates", "unchecked"})
    @Test
    public void binFail() {
        final String s1 = "only int/float/double/long/short/byte allowed";
        final String s2 = "only int/long/short/byte allowed";
        for (int i = IDX_PLS; i <= IDX_BITOR; ++i) {
            final int ii = i;
            BiFunction<Ptr, Future, Future<?>> func;
            switch (i) {
                case IDX_PLS:
                    func = Op::plus;
                    break;
                case IDX_MNS:
                    func = Op::minus;
                    break;
                case IDX_MUL:
                    func = Op::multiply;
                    break;
                case IDX_DIV:
                    func = Op::divide;
                    break;
                case IDX_MOD:
                    func = Op::mod;
                    break;
                case IDX_BITAND:
                    func = Op::bitAnd;
                    break;
                case IDX_BITOR:
                    func = Op::bitOr;
                    break;
                default:
                    throw new RuntimeException();
            }
            func.apply(Ptr.nil(), F.unit()).setHandler(r -> {
                assertTrue(r.failed());
                assertTrue(r.cause() instanceof IllegalArgumentException);
                if (ii < IDX_BITAND) {
                    assertEquals(s1, r.cause().getMessage());
                } else {
                    assertEquals(s2, r.cause().getMessage());
                }
            });
        }
    }

    @Test
    public void not() {
        ReadablePtr<Boolean, ?> b = Ptr.ofReadonly(true);
        assertFalse(b.unary(Op::not).result());

        b = Ptr.of(false);
        assertTrue(b.unary(Op::not).result());
    }

    @Test
    public void and() {
        ReadablePtr<Boolean, ?> b = Ptr.ofReadonly(true);
        assertTrue(b.bin(Op::and, F.unit(true)).result());
        assertFalse(b.bin(Op::and, F.unit(false)).result());

        b = Ptr.of(false);
        assertFalse(b.bin(Op::and, F.unit(true)).result());
        assertFalse(b.bin(Op::and, F.unit(false)).result());
    }

    @Test
    public void or() {
        ReadablePtr<Boolean, ?> b = Ptr.ofReadonly(false);
        assertTrue(b.bin(Op::or, F.unit(true)).result());
        assertFalse(b.bin(Op::or, F.unit(false)).result());

        b = Ptr.of(true);
        assertTrue(b.bin(Op::or, F.unit(true)).result());
        assertTrue(b.bin(Op::or, F.unit(false)).result());
    }

    @Test
    public void gt() {
        Ptr<Integer> p = Ptr.of(3);
        assertTrue(p.bin(Op::gt, F.unit(2)).result());
        assertFalse(p.bin(Op::gt, F.unit(3)).result());
        assertFalse(p.bin(Op::gt, F.unit(4)).result());
    }

    @Test
    public void lt() {
        Ptr<Integer> p = Ptr.of(3);
        assertTrue(p.bin(Op::lt, F.unit(4)).result());
        assertFalse(p.bin(Op::lt, F.unit(3)).result());
        assertFalse(p.bin(Op::lt, F.unit(2)).result());
    }

    @Test
    public void ge() {
        Ptr<Integer> p = Ptr.of(3);
        assertTrue(p.bin(Op::ge, F.unit(2)).result());
        assertTrue(p.bin(Op::ge, F.unit(3)).result());
        assertFalse(p.bin(Op::ge, F.unit(4)).result());
    }

    @Test
    public void le() {
        Ptr<Integer> p = Ptr.of(3);
        assertTrue(p.bin(Op::le, F.unit(4)).result());
        assertTrue(p.bin(Op::le, F.unit(3)).result());
        assertFalse(p.bin(Op::le, F.unit(2)).result());
    }

    @Test
    public void eq() {
        Ptr<Integer> p = Ptr.of(3);
        assertTrue(p.bin(Op::eq, F.unit(3)).result());
        assertFalse(p.bin(Op::eq, F.unit(4)).result());
        Ptr<String> nil = Ptr.nil();
        assertTrue(nil.bin(Op::eq, F.unit()).result());
        assertFalse(nil.bin(Op::eq, F.unit("")).result());
    }

    @Test
    public void ne() {
        Ptr<Integer> p = Ptr.of(3);
        assertTrue(p.bin(Op::ne, F.unit(4)).result());
        assertFalse(p.bin(Op::ne, F.unit(3)).result());
        Ptr<String> nil = Ptr.nil();
        assertTrue(nil.bin(Op::ne, F.unit("")).result());
        assertFalse(nil.bin(Op::ne, F.unit()).result());
    }
}
