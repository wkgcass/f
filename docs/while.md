# while

The while loop expression.

```java
While.cond(() -> boolMonad).yield(() -> monad)
// or the condition may be sync
While.cond(() -> boolValue).yield(() -> monad)
```

The result of while expression is a list of values returned by `monad`. Note that: null values will not be added into the result list.

## break

You can break a for loop using `F.brk()`, or use `F.brk(value)` to break the loop with a last value (which will be added into the result list).

## continue

Since null values won't be recorded, you can simply return `F.unit()` to `continue` the iteration.

## Can I run the while loop infinitely ?

Yes, you can. The while loop is based on the for loop, see [for](for.md) for more info.
