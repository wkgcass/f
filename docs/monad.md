# monad

Before reading this doc, you should have knowledge about monad and `Future` in vertx (not in java standard library).

## Construct

### Constructing a monad

```java
F.unit()  // create a monad with null value
F.unit(1) // create a monad with a value: integer 1

// same as the following:
Monad.unit()
Monad.unit(1)

// or you can create some "failed" monads
F.fail("reason")
F.fail(new Exception())
```

### Constructing a "not finished" monad

```java
Monad<Integer> tbd = F.tbd(); // tbd stands for "to be done"
tbd.complete(1);              // complete the monad

// or you can "fail" the monad
tbd.fail("reason");
// or
tbd.fail(new Exception());
```

## Operations

### Map

To map a monad with some value to another monad which is bond with your new value calculated from the original value.

```java
monad.map(oldVal -> newVal)  // map from value to another value
monad.map(()) -> newVal)     // ignore the old value and give a new value
monad.map(newValue)          // if your new value can be calculated when forming the monad chain,
                             // you can use this method to give a value to the monad

monad.mapEmpty()             // ignore the old value and map the monad to null
monad.mapEmpty(oldVal -> {}) // do some handlings with the old value and map to null
monad.mapEmpty(() -> {})     // ignore the old value and do some handling, then map to null

monad.bypass(() -> {})       // do some handling, and do not change the value of the monad
```

### Compose

To continue running an async operation and map the value to the result of the async operation.

```java
monad.compose(oldVal -> newMonad) // use the old value to determin an async operation to handle
monad.compose(() -> newMonad)     // ignore the old value and handle an async operation

// when the oldVal is a monad, you can do this to release the nested monad
monad.compose(monad -> monad)
```

### setHandler

Handle the result when async operation finishes.

```java
monad.setHandler(r -> {
    if (r.succeeded()) {
        r.result(); // the result
    }
    // or check failed
    if (r.failed()) {
        r.cause();  // the exception
    }
});
```

### Applicative

Join multiple async operations and handle when all of them are done.

First you might want to define an interface to make your code look better.

```java
interface SomeHandler extends Function<A, Function<B, Function<C, RESULT>>>
```

Then:

```java
F.unit((SomeHandler) a -> b -> c -> { /* some handling */ return result; })
    .as(F::app).ap(monadForA)
    .as(F::app).ap(monadForB)
    .as(F::app).ap(monadForC)
```

The three monads would be executed at the same time, and when all three are done, the `SomeHandler` would be called.

It's useful, however a little annoying that the handling process should be wrote before the executing commands, and futher handlings are attached just after the executing commands.

## MonadLike

It's an interface for some classes that look like monad. It defines three mostly used functions:

* map
* compose
* setHandler

The effects of the methods in monad-like classes are the same as moand.

MonadLike is used in `If`, `Try` and *many* other places in this lib.
