package net.cassite.f;

public class Null {
    private Null() throws Throwable {
        throw new Throwable("DO NOT INSTANTIATE ME!!!");
    }

    public static final Null value = null;

    public static <T> T value() {
        return null;
    }
}
