# event-emitter

EventEmitter is used to register event handlers and emit events.

## Create

```java
EventEmitter emitter = EventEmitter.create();
```

## Register event handlers

We use `Symbol` as event keys. See [util](util.md) to check how to use `Symbol`.

```java
Symbol<Type> event = Symbol.create();
```

```java
// handle the event
emitter.on(event, data -> {
    // handling process
});
// handle the event only once
emitter.once(event, data -> {
    // handling process
});

// remove the handler
emitter.remove(event, handler);
// remove all handlers of an event
emitter.removeAll(event);

// handle errors
emitter.on(IEventEmitter.error, err -> {
    // type of err is Throwable
});
```

## emit events

```java
emitter.emit(event, object);
```
