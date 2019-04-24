package net.cassite.f;

class Misc {
    private Misc() {
    }

    @FunctionalInterface
    interface IntFunction<T> {
        int apply(T t);
    }

    @FunctionalInterface
    interface FloatFunction<T> {
        float apply(T t);
    }

    @FunctionalInterface
    interface LongFunction<T> {
        long apply(T t);
    }

    @FunctionalInterface
    interface DoubleFunction<T> {
        double apply(T t);
    }

    @FunctionalInterface
    interface ShortFunction<T> {
        short apply(T t);
    }

    @FunctionalInterface
    interface ByteFunction<T> {
        byte apply(T t);
    }

    @FunctionalInterface
    interface CharFunction<T> {
        char apply(T t);
    }

    @FunctionalInterface
    interface BoolFunction<T> {
        boolean apply(T t);
    }
}
