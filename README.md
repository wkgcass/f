# f

## dependency

gradle

```groovy
compile 'net.cassite:f:0.1.2'
```

maven

```xml
<dependency>
  <groupId>net.cassite</groupId>
  <artifactId>f</artifactId>
  <version>0.1.2</version>
</dependency>
```

## usage

You can get some examples and test cases in test directory.

Here's some simple usage examples:

```java
If.cond(boolFuture).run(() -> { ...; return future; })
  .elseif(() -> boolFuture).run(() -> { ...; return future; })
  .otherwise(() -> { ...; return future; })
  .compose(res -> ...);

// similar to
Value res;
if (boolValue) {
  res = ...;
} else if (boolValue) {
  res = ...;
} else {
  res = ...;
}

For.init(0).condSync(c -> c.i < list.size()).incrSync(c -> ++c.i)
    .yield(c -> { ...; return future; })
    .compose(resultList -> ...)

// similar to
List<value> resultList = new ArrayList<>();
for(i = 0; i < list.size(); ++i) {
  resultList.add(...);
}

For.each(iterable).yield(elem -> ...; return future;).compose(resultList -> ...)

// similar to
List<Value> resultList = new ArrayList<>();
for (Value value : iterable) {
  resultList.add(...);
}

While.cond(() -> boolValue).yield(() -> ...; return future;).compose(resultList -> ...)

// similar to
List<Value> resultList = new ArrayList<>();
while (boolValue) {
  // ...
  resultList.add(...)
}

Try.code(() -> ...).except(Throwable.class, t -> ...).composeFinally(() -> ...).compose(res -> ...)

// both code and catch(except) can return a value

// similar to
try {
  // ...
  return res1;
} catch (Throwable t) {
  return res2;
} finally {
  // ...
}
```

## function is sync or async?

>sync means to directly return a value or not return anything  
>async means to return a future object

* If cond : async
* If run : async
* If elseif : async
* If otherwise : async
* For init : sync
* For condSync : sync
* For incrSync : sync
* For cond : async
* For incr : asnyc
* For each : sync
* While cond : both sync and async
* For/While yield : async
* Try code : async
* Try except : async
* Try composeFinally : async

