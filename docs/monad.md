# monad

## syntax

Supports all functions that vertx Future provides.

```
<T> Monad.unit([T]?) // construct
<T> F.unit([T]?) // construct
<T> F.tbd() // to be done
<T> F.fail(msg|throwable) // failed monad

<T> monad.complete([T]?)         // complete a tbd monad (only use this with tbd)
<T> monad.fail(msg|throwable)    // fail a tbd monad (only use this with tbd)
<U> monad.compose(v -> Monad<U>) // handle the process with value inside the monad,
                                 // and return a new monad
                                 // the result monad would contain the returned monad's contained value
<U> monad.map(v -> U)            // handle the process with value inside the monad,
                                 // and return a new value,
                                 // the result would be monad containing the new value
<U> monad.setHandler(r -> {})    // r.succeeded() or r.failed() or r.result() or r.cause() might be called
<U> monad.as(self -> U)          // transform into another type
```

You can transform a `Monad` into an `Applicative` using `F::app`.

```
Applicative<T->U>
ap(Monad<T>)                     // return Monad<U>, useful when handle things in parallel and want to handle all the results
```

## usage

```java
Monad<Integer> intMonad = F.unit(1);        // construct monad with value 1
Monad<SomeType> monad   = F.unit();         // construct monad with null value
Monad<?> failedMonad    = F.fail("reason"); // construct failed monad with error message
Monad<Integer> tbd      = F.tbd();          // construct a tbd monad
someAsyncProcess(r -> {                     // run some async process and set the callback function
  if (r.failed()) {                         // if the async process failed
    tbd.fail(r.cause());                    // let the tbd fail with async fail cause
  } else {                                  // otherwise succeeded
    tbd.complete(r.result());               // let the tbd complete with async result
  }
});
F.unit((X) a -> b -> c -> a + b + c)        // a function that adds three numbers
    .as(F::app).ap(category1.count())       // count category one
    .as(F::app).ap(category2.count())       // count category two
    .as(F::app).ap(category3.count())       // count category three, all thess count operations are handled in parallel
    .compose(i -> someProcess(i))           // give the sum result and handle
```
