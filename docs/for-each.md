# for-each

Run async operation on each element of an `Iterable|Iterator|Array`.

```java
For.each(list).yield(element -> monad)
```

It will run operations defined by the lambda in `yield` on each element in `list`, and then join the return values on the `monad` into one list, which will be the final result of the for-each expression. Note that: null values will not be recorded in the list.

## break

You can break a for-each loop using `F.brk()`, or use `F.brk(value)` to break the loop with a last value (which will be added into the result list).

## continue

Since null values won't be recorded, you can simply return `F.unit()` to `continue` the iteration.
