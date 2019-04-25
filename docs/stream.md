# stream

## What is the stream lib

The stream is derived from event emitter.

When we don't have monads, we may use callback functions to handle async results, e.g.:

```java
fs.readFile("./foo", "utf8", (err, content) -> {
    // ...
});
```

If we want to separate the handling processes into multiple functions, we usually use callback functions:

```java
void run(Callback<String> cb) {
    fs.readFile("./foo", "utf8", cb);
}
```

Then we have monads, the code will look like this:

```java
fs.readFile("./foo", "utf8").compose(content -> ...);
// or when we separate it from the handling code
return fs.readFile("./foo", "utf8");
```

With monad, we can `return` an async process from a method.

Let's consider how the event emitter works.

```java
emitter.on(event, data -> {
});
```

The traditional way of handling events is to register a handler to the event, which looks like the callback style, but the handler function may be called many times.

We may build something which looks like monad, which can hold the context of handling a serie of events, and which can be returned.

Then we build the `Stream` lib.

## Create

A `stream` object can be derived from an event emitter or a publisher.

```java
Stream<T> stream = emitter.on(event);
```

```java
SimplePublisher<T> pub = Publisher.create();
Stream<T> stream = pub.subscribe();
Stream<T> stream2 = stream.subscribe(); // subscribe from a stream and generate a new stream
```

## Handler

There are a few ways of attaching handler to a stream: `map`, `compose`, `setHandler`. See [monad](monad.md) for more info (the MonadLike).

```java
stream.map(oldVal -> newVal)
      .compose(value -> monad)
      .setHandler(r -> { ... })
```

The `map()` and `compose()` methods return a new `Stream` instance, and you can keep calling methods on the new stream.

These methods of one stream can be called many times, which will form a tree:

```
            EventEmitter or Publisher
                         |
                         |
                     RootStream
                        /|\
                       / | \
                      /  |  \
                     /   |   \
                    /    |    \
                   /     |     \
               compose  map  setHandler
                 /       |
                /        |
               /         |
              /          |
        SubStream1   SubStream2
            /|           |\
           / |           | \
          /  |           |  \
         /   |           |   \
        /    |           |    \
       /     |           |     \
      /      |           |      \
  compsoe   map         map  setHandler
    /        |           |
   /         |           |
  /          |           |
StreamA   StreamB     StreamC
```

When an event emits, the data of the event will be processed by a stream, and when the handling is done, it will pass the data to the attached streams.

## Closing a stream

```java
stream.close()
```

When a stream is closed, or an event detached from event emitter, the stream will be closed, then it will no longer handle any event. When a stream is closed, it will send a `HandlerRemovedException` to the child streams, the child streams is responsible for handling the exception, and may do some resource release.

It's ok if child streams simply ignore the exception if you are not attaching any child streams to other objects. When a stream is closed, it will be detached from the parent stream, then all children will not be reachable and will be collected when doing gc.

If a stream is derived from an event emitter, if the event handler of the stream is removed from the emitter, the stream will also receive a `HandlerRemovedException`.

Note that child streams will not be closed when a parent is closed because they are unreachable so it's not necessary.

## Firing events

```java
emitter.emit(event, data);
simplePublisher.publish(data);
```

## Handler of closing

You can register closeCallback to a stream. When it's closed, the callback will be alerted.

```java
stream.addCloseHandler(() -> { ... });
```
