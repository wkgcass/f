# f

## dependency

gradle

```groovy
compile 'net.cassite:f:0.3.0'
```

maven

```xml
<dependency>
  <groupId>net.cassite</groupId>
  <artifactId>f</artifactId>
  <version>0.3.0</version>
</dependency>
```

## usage

You can get some examples and test cases in test directory.

Here's some simple usage examples:

```java
/* if */
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

/* for */
For.init(0).condSync(c -> c.i < list.size()).incrSync(c -> ++c.i)
    .yield(c -> { ...; return future; })
    .compose(resultList -> ...)

// similar to
List<value> resultList = new ArrayList<>();
for(i = 0; i < list.size(); ++i) {
  resultList.add(...);
}

// all loop dsls support something similar to `break` keyword
// use the following expression inside yield
return F.brk();
// or
return F.brk(result);

For.each(iterable).yield(elem -> ...; return future;).compose(resultList -> ...)

// similar to
List<Value> resultList = new ArrayList<>();
for (Value value : iterable) {
  resultList.add(...);
}

/* while */
While.cond(() -> boolValue).yield(() -> ...; return future;).compose(resultList -> ...)

// similar to
List<Value> resultList = new ArrayList<>();
while (boolValue) {
  // ...
  resultList.add(...)
}

/* Try */
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

/* break */
For.each(list).yield(e -> { ...; F.brk(/* here can have the last value to yield*/); })

// similar to
for (Value e : list) {
  ...
  break;
}

/* MList */
MList.unit(1, 2, 3) // Arrays.asList(1, 2, 3)

MList<Value> list = MList.mutable();
// operate on the list
list = list.immutable();
list.map(e -> ...)
list.compose(e -> MList.unit(...))
list.head() // first elem
list.init() // a list except the last elem
list.tail() // a list except the first elem
list.last() // last elem

/* pointer and flow */
Ptr<Integer> a = Ptr.nil();
Ptr<Integer> b = Ptr.of(3);
Flow.store(a, () -> b.bin(Op::add, /* a Future<int> object */))
    .exec(() -> b.value = a.value + b.value)
    .returnPtr(a); // or returnFuture or returnValue
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
* MList (all): sync
* Flow returnPtr : async
* Flow returnFuture : async
* Flow returnValue : async

