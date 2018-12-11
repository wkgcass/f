# while

## syntax

```
<R>
While.cond(() -> boolean|Monad<Boolean>)
     .yield(() -> Monad<R>)                   // Monad<List<R>>
Do.yield(() -> Monad<R>)
  .whileCond(() -> boolean|Monad<Boolean>)    // Monad<List<R>>

F.brk([R]?)                                   // break with or without the last value to yield
```

## usage

```java
While.cond(() -> needMoreData())                  // the condition to check whether need more data
     .yield(() -> produceData())                  // produce data
     .compose(resultList -> process(resultList))  // get and process the result data
```
