package net.cassite.f;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@SuppressWarnings("unused")
public class Symbol<T> {
    private static final HashMap<String, Symbol> stored = new HashMap<>();

    public final String name;

    protected Symbol() {
        name = null;
    }

    protected Symbol(String name) {
        this.name = name;
    }

    public static <T> Symbol<T> create() {
        return new Symbol<>();
    }

    public static <T> Symbol<T> create(@NotNull String name) {
        if (name == null)
            throw new NullPointerException();

        return new Symbol<>(name);
    }

    @Override
    public String toString() {
        return "Symbol" + (name == null ? "" : "(" + name + ")") + "@" + Integer.toHexString(hashCode());
    }
}
