# flow

The `flow` class is built for writing long async code more fluently.

# WARNING: The class may be fully rewrote in the future.

You may notice the syntax and implementation a little strange. This is a considerably nice syntax summarized in practical use. If I have some better idea, I will fully rewrite this class. If you have some idea, welcome to share them with me through issues.

## syntax

### Create flow object

```java
Flow flow = Flow.flow();
```

### Handling

There are many aspects and ways of adding async operations to the flow:

```java
flow.next().async = () -> monad;       // run an async operation
flow.next().statement = () -> { ... }; // run a sync operation

flow.next().store(Ptr ptr).async = () -> monad; // run an async operation and store the value in the pointer
flow.next().store(Ptr ptr).value = () -> value; // run a sync operation and store the value in the pointer
```

You may notice here lambda is not passed into a method, but assigned to a field. This is because we can omit the braces surrounding the lambda.

### Returning

To use the flow with the outside world, you have to "return" a moand from flow.

```java
flow.returnFuture(() -> monad)  // run the whole flow then return this moand.
flow.returnValue(() -> value)   // run the whole flow then return a monad bond with your value.
flow.returnPtr(ptr)             // run the whole flow then return what is contained in the ptr with a monad.
flow.returnNull()               // run the whole flow then return null monad.
```
