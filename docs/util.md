# util

## syntax

```
<T> Monad<T> F.unit([T]?)             // see docs in monad
<T> Monad<T> F.tbd()                  // see docs in monad
<T> Monad<T> F.fail(str|throwable)    // see docs in monad

F::app                                // switch from monad to applicative, used in Monad#as, see docs in monad

F::composite                          // composite for MList and Monad (similar to vertx CompositeFuture), used in MList#as
                                      // see docs in MList
F::flip                               // flip from List<Monad<T>> to Monad<List<T>>, used in MList#as
                                      // see docs in MList

F.brk([T]?)                           // break a loop with or without the last value to yield
                                      // see docs in loops (for while etc)

F.value(t, () -> {})                  // want to return a value but still have something to handle first
                                      // usually: () -> F.value(t, () -> { ... })
                                      // helpful when you don't want to write braces with lambda
F.runcb(cb -> { call(cb) })           // wrap a callback style api into a monad style api

Null                                  // a class that can never be instantiated, can be used in Monad<Null>
<T> Null.value()                      // always returns null
Null.value                            // return the type Null with value null
```

## usage

```java
Ptr<Integer> i = Ptr.of(0);                                                  // create an integer pointer with value 0
While.cond(() -> i.value < list.size())                                      // while condition
     .yield(() -> F.value(F.unit(xxx), () -> ++i.value));                    // increase i before returning a value

Monad<List<String>> m = F.runcb(cb -> vertx.fileSystem().readDir("/", cb));  // transform callback api into monad api
```
