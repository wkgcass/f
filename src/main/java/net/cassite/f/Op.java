package net.cassite.f;

import org.jetbrains.annotations.NotNull;
import io.vertx.core.Future;

import java.util.Objects;

public class Op {
    private Op() {
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T addNumber(T num, T incr) {
        T res;
        if (num instanceof Integer) {
            res = (T) Integer.valueOf(num.intValue() + incr.intValue());
        } else if (num instanceof Float) {
            res = (T) Float.valueOf(num.floatValue() + incr.floatValue());
        } else if (num instanceof Long) {
            res = (T) Long.valueOf(num.longValue() + incr.longValue());
        } else if (num instanceof Double) {
            res = (T) Double.valueOf(num.doubleValue() + incr.doubleValue());
        } else if (num instanceof Short) {
            res = (T) Short.valueOf((short) (num.shortValue() + incr.shortValue()));
        } else if (num instanceof Byte) {
            res = (T) Byte.valueOf((byte) (num.byteValue() + incr.byteValue()));
        } else {
            throw new IllegalArgumentException("only int/float/double/long/short/byte allowed");
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T subNumber(T num, T incr) {
        T res;
        if (num instanceof Integer) {
            res = (T) Integer.valueOf(num.intValue() - incr.intValue());
        } else if (num instanceof Float) {
            res = (T) Float.valueOf(num.floatValue() - incr.floatValue());
        } else if (num instanceof Long) {
            res = (T) Long.valueOf(num.longValue() - incr.longValue());
        } else if (num instanceof Double) {
            res = (T) Double.valueOf(num.doubleValue() - incr.doubleValue());
        } else if (num instanceof Short) {
            res = (T) Short.valueOf((short) (num.shortValue() - incr.shortValue()));
        } else if (num instanceof Byte) {
            res = (T) Byte.valueOf((byte) (num.byteValue() - incr.byteValue()));
        } else {
            throw new IllegalArgumentException("only int/float/double/long/short/byte allowed");
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T multiNumber(T num, T incr) {
        T res;
        if (num instanceof Integer) {
            res = (T) Integer.valueOf(num.intValue() * incr.intValue());
        } else if (num instanceof Float) {
            res = (T) Float.valueOf(num.floatValue() * incr.floatValue());
        } else if (num instanceof Long) {
            res = (T) Long.valueOf(num.longValue() * incr.longValue());
        } else if (num instanceof Double) {
            res = (T) Double.valueOf(num.doubleValue() * incr.doubleValue());
        } else if (num instanceof Short) {
            res = (T) Short.valueOf((short) (num.shortValue() * incr.shortValue()));
        } else if (num instanceof Byte) {
            res = (T) Byte.valueOf((byte) (num.byteValue() * incr.byteValue()));
        } else {
            throw new IllegalArgumentException("only int/float/double/long/short/byte allowed");
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T divNumber(T num, T incr) {
        T res;
        if (num instanceof Integer) {
            res = (T) Integer.valueOf(num.intValue() / incr.intValue());
        } else if (num instanceof Float) {
            res = (T) Float.valueOf(num.floatValue() / incr.floatValue());
        } else if (num instanceof Long) {
            res = (T) Long.valueOf(num.longValue() / incr.longValue());
        } else if (num instanceof Double) {
            res = (T) Double.valueOf(num.doubleValue() / incr.doubleValue());
        } else if (num instanceof Short) {
            res = (T) Short.valueOf((short) (num.shortValue() / incr.shortValue()));
        } else if (num instanceof Byte) {
            res = (T) Byte.valueOf((byte) (num.byteValue() / incr.byteValue()));
        } else {
            throw new IllegalArgumentException("only int/float/double/long/short/byte allowed");
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T modNumber(T num, T incr) {
        T res;
        if (num instanceof Integer) {
            res = (T) Integer.valueOf(num.intValue() % incr.intValue());
        } else if (num instanceof Float) {
            res = (T) Float.valueOf(num.floatValue() % incr.floatValue());
        } else if (num instanceof Long) {
            res = (T) Long.valueOf(num.longValue() % incr.longValue());
        } else if (num instanceof Double) {
            res = (T) Double.valueOf(num.doubleValue() % incr.doubleValue());
        } else if (num instanceof Short) {
            res = (T) Short.valueOf((short) (num.shortValue() % incr.shortValue()));
        } else if (num instanceof Byte) {
            res = (T) Byte.valueOf((byte) (num.byteValue() % incr.byteValue()));
        } else {
            throw new IllegalArgumentException("only int/float/double/long/short/byte allowed");
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T bitAndNumber(T a, T b) {
        T res;
        if (a instanceof Integer) {
            res = (T) Integer.valueOf(a.intValue() & b.intValue());
        } else if (a instanceof Long) {
            res = (T) Long.valueOf(a.longValue() & b.longValue());
        } else if (a instanceof Short) {
            res = (T) Short.valueOf((short) (a.shortValue() & b.shortValue()));
        } else if (a instanceof Byte) {
            res = (T) Byte.valueOf((byte) (a.byteValue() & b.byteValue()));
        } else {
            throw new IllegalArgumentException("only int/long/short/byte allowed");
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T bitOrNumber(T a, T b) {
        T res;
        if (a instanceof Integer) {
            res = (T) Integer.valueOf(a.intValue() | b.intValue());
        } else if (a instanceof Long) {
            res = (T) Long.valueOf(a.longValue() | b.longValue());
        } else if (a instanceof Short) {
            res = (T) Short.valueOf((short) (a.shortValue() | b.shortValue()));
        } else if (a instanceof Byte) {
            res = (T) Byte.valueOf((byte) (a.byteValue() | b.byteValue()));
        } else {
            throw new IllegalArgumentException("only int/long/short/byte allowed");
        }
        return res;
    }

    // !
    public static Monad<Boolean> not(@NotNull ReadablePtr<Boolean, ?> ptr) {
        if (ptr == null)
            throw new NullPointerException();
        boolean b = ptr.get();
        return Monad.unit(!b);
    }

    // ++i
    @SuppressWarnings("unchecked")
    public static <T extends Number> Monad<T> leftIncr(@NotNull Ptr<T> ptr) {
        if (ptr == null)
            throw new NullPointerException();
        T num = ptr.get();
        Number n = addNumber(num, 1);
        ptr.store((T) n);
        return Monad.unit((T) n);
    }

    // i++
    @SuppressWarnings("unchecked")
    public static <T extends Number> Monad<T> rightIncr(@NotNull Ptr<T> ptr) {
        if (ptr == null)
            throw new NullPointerException();
        T num = ptr.get();
        Number n = addNumber(num, 1);
        ptr.store((T) n);
        return Monad.unit(num);
    }

    // --i
    @SuppressWarnings("unchecked")
    public static <T extends Number> Monad<T> leftDecr(@NotNull Ptr<T> ptr) {
        if (ptr == null)
            throw new NullPointerException();
        T num = ptr.get();
        Number n = subNumber(num, 1);
        ptr.store((T) n);
        return Monad.unit((T) n);
    }

    // i--
    @SuppressWarnings("unchecked")
    public static <T extends Number> Monad<T> rightDecr(@NotNull Ptr<T> ptr) {
        if (ptr == null)
            throw new NullPointerException();
        T num = ptr.get();
        Number n = subNumber(num, 1);
        ptr.store((T) n);
        return Monad.unit(num);
    }

    // &&
    public static Monad<Boolean> and(@NotNull ReadablePtr<Boolean, ?> ptr, @NotNull Future<Boolean> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> ptr.get() && vv));
    }

    // ||
    public static Monad<Boolean> or(@NotNull ReadablePtr<Boolean, ?> ptr, @NotNull Future<Boolean> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> ptr.get() || vv));
    }

    // &=
    public static <T extends Number> Monad<T> bitAndAsn(@NotNull Ptr<T> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            T n = bitAndNumber(num, vv);
            ptr.store(n);
            return n;
        }));
    }

    // |=
    public static <T extends Number> Monad<T> bitOrAsn(@NotNull Ptr<T> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            T n = bitOrNumber(num, vv);
            ptr.store(n);
            return n;
        }));
    }

    // +=
    public static <T extends Number> Monad<T> incr(@NotNull Ptr<T> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            T n = addNumber(num, vv);
            ptr.store(n);
            return n;
        }));
    }

    // -=
    public static <T extends Number> Monad<T> decr(@NotNull Ptr<T> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            T n = subNumber(num, vv);
            ptr.store(n);
            return n;
        }));
    }

    // *=
    public static <T extends Number> Monad<T> mulAsn(@NotNull Ptr<T> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            T n = multiNumber(num, vv);
            ptr.store(n);
            return n;
        }));
    }

    // /=
    public static <T extends Number> Monad<T> divAsn(@NotNull Ptr<T> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            T n = divNumber(num, vv);
            ptr.store(n);
            return n;
        }));
    }

    // %=
    public static <T extends Number> Monad<T> modAsn(@NotNull Ptr<T> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            T n = modNumber(num, vv);
            ptr.store(n);
            return n;
        }));
    }

    // +
    public static <T extends Number> Monad<T> plus(@NotNull ReadablePtr<T, ?> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            return addNumber(num, vv);
        }));
    }

    // -
    public static <T extends Number> Monad<T> minus(@NotNull ReadablePtr<T, ?> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            return subNumber(num, vv);
        }));
    }

    // *
    public static <T extends Number> Monad<T> multiply(@NotNull ReadablePtr<T, ?> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            return multiNumber(num, vv);
        }));
    }

    // /
    public static <T extends Number> Monad<T> divide(@NotNull ReadablePtr<T, ?> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            return divNumber(num, vv);
        }));
    }

    // %
    public static <T extends Number> Monad<T> mod(@NotNull ReadablePtr<T, ?> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            return modNumber(num, vv);
        }));
    }

    // &
    public static <T extends Number> Monad<T> bitAnd(@NotNull ReadablePtr<T, ?> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            return bitAndNumber(num, vv);
        }));
    }

    // |
    public static <T extends Number> Monad<T> bitOr(@NotNull ReadablePtr<T, ?> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            return bitOrNumber(num, vv);
        }));
    }

    // >
    public static <T extends Number> Monad<Boolean> gt(@NotNull ReadablePtr<T, ?> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            return num.doubleValue() > vv.doubleValue();
        }));
    }

    // <
    public static <T extends Number> Monad<Boolean> lt(@NotNull ReadablePtr<T, ?> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            return num.doubleValue() < vv.doubleValue();
        }));
    }

    // >=
    public static <T extends Number> Monad<Boolean> ge(@NotNull ReadablePtr<T, ?> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            return num.doubleValue() >= vv.doubleValue();
        }));
    }

    // <=
    public static <T extends Number> Monad<Boolean> le(@NotNull ReadablePtr<T, ?> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T num = ptr.get();
            return num.doubleValue() <= vv.doubleValue();
        }));
    }

    // ==
    public static <T> Monad<Boolean> eq(@NotNull ReadablePtr<T, ?> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T ptrValue = ptr.get();
            return Objects.equals(ptrValue, vv);
        }));
    }

    // !=
    public static <T> Monad<Boolean> ne(@NotNull ReadablePtr<T, ?> ptr, @NotNull Future<T> v) {
        if (ptr == null)
            throw new NullPointerException();
        if (v == null)
            throw new NullPointerException();
        return Monad.transform(v.map(vv -> {
            T ptrValue = ptr.get();
            return !Objects.equals(ptrValue, vv);
        }));
    }
}
