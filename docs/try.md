# try

Async version of `try` statement. It's the same as the java try block.

```java
// try expression
Try.code(() -> {
    // body of the try block
    return monad;
}).except(SomeException.class, e -> {
    // type of `e` is SomeException

    // handling process of SomeException

    // you can recover from the exception by returning a monad
    return monad;
}).except(AnotherException.class, e -> {
    // type of `e` is AnotherException
    // you can wrap the exception and throw again
    throw new Error(e);
}).composeFinally(() -> {
    // the `finally` block
    // will be executed after try block or some `except` block
    return monad;
}) // here returns Monad
    // compose(...)
```

Method `composeFinally` returns a monad. If `finally` block is not required, you can use `map`, `compose` or `setHandler` after `code()` or `excpet()` to continue the handling process.

See [moand](monad.md) for more info.
