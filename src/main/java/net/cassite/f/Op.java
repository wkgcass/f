package net.cassite.f;

import io.vertx.core.Future;

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
    public static Monad<Boolean> not(Ptr<Boolean> ptr) {
        boolean b = ptr.value;
        return Monad.unit(!b);
    }

    // ++i
    @SuppressWarnings("unchecked")
    public static <T extends Number> Monad<T> leftIncr(Ptr<T> ptr) {
        T num = ptr.value;
        Number n = addNumber(num, 1);
        ptr.value = (T) n;
        return Monad.unit((T) n);
    }

    // i++
    @SuppressWarnings("unchecked")
    public static <T extends Number> Monad<T> rightIncr(Ptr<T> ptr) {
        T num = ptr.value;
        Number n = addNumber(num, 1);
        ptr.value = (T) n;
        return Monad.unit(num);
    }

    // --i
    @SuppressWarnings("unchecked")
    public static <T extends Number> Monad<T> leftDecr(Ptr<T> ptr) {
        T num = ptr.value;
        Number n = subNumber(num, 1);
        ptr.value = (T) n;
        return Monad.unit((T) n);
    }

    // i--
    @SuppressWarnings("unchecked")
    public static <T extends Number> Monad<T> rightDecr(Ptr<T> ptr) {
        T num = ptr.value;
        Number n = subNumber(num, 1);
        ptr.value = (T) n;
        return Monad.unit(num);
    }

    // &&
    public static Monad<Boolean> and(Ptr<Boolean> ptr, Future<Boolean> v) {
        return Monad.transform(v.map(vv -> ptr.value && vv));
    }

    // ||
    public static Monad<Boolean> or(Ptr<Boolean> ptr, Future<Boolean> v) {
        return Monad.transform(v.map(vv -> ptr.value || vv));
    }

    // &=
    public static <T extends Number> Monad<T> bitAndAsn(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            T n = (T) bitAndNumber(num, vv);
            ptr.value = n;
            return n;
        }));
    }

    // |=
    public static <T extends Number> Monad<T> bitOrAsn(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            T n = bitOrNumber(num, vv);
            ptr.value = n;
            return n;
        }));
    }

    // +=
    public static <T extends Number> Monad<T> incr(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            T n = addNumber(num, vv);
            ptr.value = n;
            return n;
        }));
    }

    // -=
    public static <T extends Number> Monad<T> decr(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            T n = subNumber(num, vv);
            ptr.value = n;
            return n;
        }));
    }

    // *=
    public static <T extends Number> Monad<T> mulAsn(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            T n = multiNumber(num, vv);
            ptr.value = n;
            return n;
        }));
    }

    // /=
    public static <T extends Number> Monad<T> divAsn(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            T n = divNumber(num, vv);
            ptr.value = n;
            return n;
        }));
    }

    // %=
    public static <T extends Number> Monad<T> modAsn(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            T n = modNumber(num, vv);
            ptr.value = n;
            return n;
        }));
    }

    // +
    public static <T extends Number> Monad<T> plus(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            return addNumber(num, vv);
        }));
    }

    // -
    public static <T extends Number> Monad<T> minus(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            return subNumber(num, vv);
        }));
    }

    // *
    public static <T extends Number> Monad<T> multiply(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            return multiNumber(num, vv);
        }));
    }

    // /
    public static <T extends Number> Monad<T> divide(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            return divNumber(num, vv);
        }));
    }

    // %
    public static <T extends Number> Monad<T> mod(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            return modNumber(num, vv);
        }));
    }

    // &
    public static <T extends Number> Monad<T> bitAnd(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            return bitAndNumber(num, vv);
        }));
    }

    // |
    public static <T extends Number> Monad<T> bitOr(Ptr<T> ptr, Future<T> v) {
        return Monad.transform(v.map(vv -> {
            T num = ptr.value;
            return bitOrNumber(num, vv);
        }));
    }
}
