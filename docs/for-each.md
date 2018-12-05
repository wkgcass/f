# for-each

## syntax

```
<R>
For.each(Iterable<T>|Iterator<T>|T[])
   .yield(t -> Monad<R>)        // Monad<MList<R>>

F.brk([R]?)                     // break with or without the last value to yield

there will be no `null` value in the yield result list
```

## usage

```java
getProducts().compose(list ->                          // get all products
   For.each(list)                                      // for each element in list
      .yield(e -> getDetail(e.id)))                    // getDetail by product id and yield the result
      .compose(detailList -> process(detailList))      // handle the detailList
```
