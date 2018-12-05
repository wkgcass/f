# if

The if expression.

## syntax

```
<T>
If.cond(Monad<Boolean>).run(() -> Monad<T>)
[.elseif(Monad<Boolean>).run(() -> Monad<T>)]*
[.otherwise(() -> Monad<T>)]?
.compose|map|setHandler(...)
```

## usage

```java
If.cond(cacheL1.exists("foo"))                        // check whether L1 cache has foo
        .run(() -> cacheL1.get("foo"))                // get from L1
  .elseif(() -> cacheL2.exists("foo"))                // not in L1, then check whether L2 cache has foo
        .run(() -> cacheL2.get("foo"))                // get from L2
  .otherwise(() -> cacheL1.setAndGet("foo", "bar"))   // not in both L1 and L2, then set into L1 and return the set value
  .compose(s -> someProcess(s))                       // do other work
```
