package net.cassite.f;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class EventEmitter implements IEventEmitter {
    private Map<Symbol, LinkedList<ConsumerData>> handlers = new HashMap<>();

    static class ConsumerData {
        boolean isOnce;
        Consumer consumer;
    }

    public EventEmitter() {
    }

    @SuppressWarnings("Java8MapApi")
    private void addEvent(Symbol event, Consumer handler, boolean isOnce) {
        LinkedList<ConsumerData> handlerList = handlers.get(event);
        if (handlerList == null) {
            handlerList = new LinkedList<>();
            handlers.put(event, handlerList);
        }
        ConsumerData d = new ConsumerData();
        d.isOnce = isOnce;
        d.consumer = handler;
        handlerList.add(d);
    }

    @Override
    public <T> void on(@NotNull Symbol<T> event, @NotNull Consumer<T> handler) {
        if (event == null)
            throw new NullPointerException();
        if (handler == null)
            throw new NullPointerException();

        addEvent(event, handler, false);
    }

    @Override
    public <T> void once(@NotNull Symbol<T> event, @NotNull Consumer<T> handler) {
        if (event == null)
            throw new NullPointerException();
        if (handler == null)
            throw new NullPointerException();

        addEvent(event, handler, true);
    }

    @Override
    public <T> Monad<T> once(@NotNull Symbol<T> event) {
        if (event == null)
            throw new NullPointerException();

        Monad<T> m = F.tbd();
        once(event, data -> {
            if (data == null) {
                m.complete();
            } else {
                m.complete(data);
            }
        });
        return m;
    }

    public void removeAll(@NotNull Symbol event) {
        if (event == null)
            throw new NullPointerException();

        handlers.remove(event);
    }

    public void remove(@NotNull Symbol event, @NotNull Consumer handler) {
        if (event == null)
            throw new NullPointerException();
        if (handler == null)
            throw new NullPointerException();

        LinkedList<ConsumerData> handlerList = handlers.get(event);
        if (handlerList != null) {
            if (handlerList.size() == 1 && handlerList.get(0).consumer.equals(handler)) {
                handlers.remove(event);
            } else {
                handlerList.removeIf(d -> d.consumer.equals(handler));
            }
        }
    }

    public <T> MList<Consumer<T>> handlers(@NotNull Symbol<T> event) {
        if (event == null)
            throw new NullPointerException();

        LinkedList<ConsumerData> handlerList = handlers.get(event);
        if (handlerList == null)
            return MList.unit();
        //noinspection unchecked
        return (MList) handlerList.stream().map(d -> d.consumer).collect(MList.collector());
    }

    @SuppressWarnings("Java8MapApi")
    @Override
    public <T> void emit(@NotNull Symbol<T> event, @Nullable T data) {
        if (event == null)
            throw new NullPointerException();

        LinkedList<ConsumerData> handlerList = handlers.get(event);
        if (handlerList != null) {
            Iterator<ConsumerData> it = handlerList.iterator();
            while (it.hasNext()) {
                ConsumerData c = it.next();
                if (c.isOnce) {
                    it.remove();
                }
                try {
                    //noinspection unchecked
                    c.consumer.accept(data);
                } catch (Throwable t) {
                    emit(error, t);
                }
            }
        }
    }
}
