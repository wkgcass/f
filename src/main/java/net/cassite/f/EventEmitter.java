package net.cassite.f;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class EventEmitter implements IEventEmitter {
    private Map<Symbol, LinkedList<HandlerData>> handlers = new HashMap<>();

    static class HandlerData {
        boolean isOnce;
        Handler handler;
    }

    static class MonadHandler<T> implements Handler<T> {
        private final Monad<T> tbd;

        MonadHandler(Monad<T> tbd) {
            this.tbd = tbd;
        }

        @Override
        public void handleError(Throwable t) {
            // do nothing
        }

        @Override
        public void handleRemoved() {
            tbd.fail(new HandlerRemovedException());
        }

        @Override
        public void accept(T o) {
            if (o == null) {
                tbd.complete();
            } else {
                tbd.complete(o);
            }
        }
    }

    public EventEmitter() {
    }

    @SuppressWarnings("WeakerAccess")
    protected <T> Handler<T> transformConsumer(Consumer<T> consumer) {
        return new ConsumerHandler<>(this, consumer);
    }

    @SuppressWarnings("Java8MapApi")
    private <T> void addEvent(Symbol event, Consumer<T> handler, boolean isOnce) {
        LinkedList<HandlerData> handlerList = handlers.get(event);
        if (handlerList == null) {
            handlerList = new LinkedList<>();
            handlers.put(event, handlerList);
        }
        HandlerData d = new HandlerData();
        d.isOnce = isOnce;
        if (handler instanceof Handler) {
            d.handler = (Handler) handler;
        } else {
            d.handler = transformConsumer(handler);
        }
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
        once(event, new MonadHandler<>(m));
        return m;
    }

    public void removeAll(@NotNull Symbol event) {
        if (event == null)
            throw new NullPointerException();

        LinkedList<HandlerData> list = handlers.remove(event);
        if (list == null)
            return;
        for (HandlerData d : list) {
            d.handler.handleRemoved();
        }
    }

    public void remove(@NotNull Symbol event, @NotNull Consumer handler) {
        if (event == null)
            throw new NullPointerException();
        if (handler == null)
            throw new NullPointerException();

        LinkedList<HandlerData> handlerList = handlers.get(event);
        if (handlerList != null) {
            if (handlerList.size() == 1 && handlerList.get(0).handler.equals(handler)) {
                HandlerData d = handlers.remove(event).get(0);
                d.handler.handleRemoved();
            } else {
                Iterator<HandlerData> it = handlerList.iterator();
                LinkedList<HandlerData> removed = new LinkedList<>();
                while (it.hasNext()) {
                    HandlerData d = it.next();
                    if (handlerList.get(0).handler.equals(handler)) {
                        it.remove();
                        removed.add(d);
                    }
                }
                for (HandlerData d : removed) {
                    d.handler.handleRemoved();
                }
            }
        }
    }

    public <T> MList<Consumer<T>> handlers(@NotNull Symbol<T> event) {
        if (event == null)
            throw new NullPointerException();

        LinkedList<HandlerData> handlerList = handlers.get(event);
        if (handlerList == null)
            return MList.unit();
        //noinspection unchecked
        return (MList) handlerList.stream().map(d -> d.handler).collect(MList.collector());
    }

    @SuppressWarnings("Java8MapApi")
    @Override
    public <T> void emit(@NotNull Symbol<T> event, @Nullable T data) {
        if (event == null)
            throw new NullPointerException();

        LinkedList<HandlerData> handlerList = handlers.get(event);
        if (handlerList != null) {
            Iterator<HandlerData> it = handlerList.iterator();
            while (it.hasNext()) {
                HandlerData c = it.next();
                if (c.isOnce) {
                    it.remove();
                }
                try {
                    //noinspection unchecked
                    c.handler.accept(data);
                } catch (Throwable t) {
                    c.handler.handleError(t);
                }
            }
        }
    }
}

class ConsumerHandler<T> implements IEventEmitter.Handler<T> {
    private final IEventEmitter emitter;
    private final Consumer<T> consumer;

    ConsumerHandler(IEventEmitter emitter, Consumer<T> consumer) {
        this.emitter = emitter;
        this.consumer = consumer;
    }

    @Override
    public void handleError(Throwable t) {
        emitter.emit(IEventEmitter.error, t);
    }

    @Override
    public void handleRemoved() {
        // do nothing
    }

    @Override
    public void accept(T t) {
        consumer.accept(t);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return obj == this || consumer.equals(obj);
    }
}
