package net.cassite.f;

import com.sun.istack.internal.Nullable;

public class Null {
    private Null() throws Throwable {
        throw new Throwable("DO NOT INSTANTIATE ME!!!");
    }

    @Nullable
    public static final Null value = null;

    @Nullable
    public static <T> T value() {
        return null;
    }
}
