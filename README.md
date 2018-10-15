# f

```java
For.init(0).condSync(c -> c.i < list.size()).incrSync(c -> ++c.i)
    .yield(c -> { ...; return future; })
    .compose(resultList -> ...)

For.each(iterable).yield(elem -> ...; return future;).compose(resultList -> ...)

While.cond(() -> boolValue).yield(() -> ...; return future;).compose(resultList -> ...)

Try.code(() -> ...).except(Throwable.class, t -> ...).composeFinally(() -> ...).compose(res -> ...)
```

* For init sync
* For condSync sync
* For incrSync sync
* For cond async
* For incr asnyc
* For each sync
* While cond both sync and async
* For/While yield async
* Try code async
* Try except async
* Try composeFinally async

