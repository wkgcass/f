package net.cassite.f;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IEventEmitter {
    Symbol<Throwable> error = Symbol.create("error"); // error is a very common event

    <T> void on(@NotNull Symbol<T> event, @NotNull Consumer<T> handler);

    <T> void once(@NotNull Symbol<T> event, @NotNull Consumer<T> handler);

    <T> void emit(@NotNull Symbol<T> event, @Nullable T data);
}
