# for

## syntax

```
<R>
For.init(I)
   .cond(ctx -> Monad<Boolean>)|.condSync(ctx -> Boolean)
   .incr(ctx -> Monad<?>)|.incrSync(ctx -> {})
   .yield(ctx -> Monad<R>)       // Monad<MList<R>>

F.brk([R]?)                      // break with or without the last value to yield
```

there will be no `null` value in the yield result list

## usage

```java
For.init(0)                                     // the initial value is set to 0
   .cond(c -> count().map(n -> c.i < n))        // count and check current index < the count
   .incrSync(c -> ++c.i)                        // c.i self increases after every loop
   .yield(c -> {                                // yield
      if (c.i > 10) {                           // maximum loop count is count or 10
         return F.brk("tail");                  // break the loop with value "tail"
      }
      return get(c.i).map(o -> o.name);         // return the retrieved object's field: name
   }).compose(nameList -> process(nameList))    // handle the nameList
```
