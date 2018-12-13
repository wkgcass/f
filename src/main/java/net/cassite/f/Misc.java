package net.cassite.f;

class Misc {
    private Misc() {
    }

    @FunctionalInterface
    interface IntFunction<T> {
        int apply(T t);
    }

    interface FloatFunction<T> {
        float apply(T t);
    }

    interface LongFunction<T> {
        long apply(T t);
    }

    interface DoubleFunction<T> {
        double apply(T t);
    }

    interface ShortFunction<T> {
        short apply(T t);
    }

    interface ByteFunction<T> {
        byte apply(T t);
    }

    interface CharFunction<T> {
        char apply(T t);
    }

    interface BoolFunction<T> {
        boolean apply(T t);
    }
}
