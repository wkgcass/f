package net.cassite.f.stream;

import net.cassite.f.Monad;
import net.cassite.f.Symbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IEventEmitter {
    Symbol<Throwable> error = Symbol.create("error"); // error is a very common event

    static IEventEmitter create() {
        return EventEmitter.create();
    }

    interface Handler<T> extends Consumer<T> {
        void handleError(Throwable t);

        void handleRemoved();
    }

    class HandlerRemovedException extends Exception {
        HandlerRemovedException() {
        }
    }

    <T> void on(@NotNull Symbol<T> event, @NotNull Consumer<T> handler);

    <T> Stream<T> on(@NotNull Symbol<T> event);

    <T> void once(@NotNull Symbol<T> event, @NotNull Consumer<T> handler);

    <T> Monad<T> once(@NotNull Symbol<T> event);

    <T> void emit(@NotNull Symbol<T> event, @Nullable T data);
}
