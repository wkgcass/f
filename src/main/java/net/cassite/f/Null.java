package net.cassite.f;

public class Null {
    private Null() throws Throwable {
        throw new Throwable("DO NOT INSTANTIATE ME!!!");
    }

    public static Null value() {
        return null;
    }
}
