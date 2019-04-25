# util

## syntax

```java
F.value(t, () -> {})                  // want to return a value but still have something to handle
                                      // usually: () -> F.value(t, () -> { ... })
                                      // helpful when you don't want to write `return` keyword with lambda
F.runcb(cb -> { call(cb) })           // wrap a callback style api into a monad style api
F.handler(Monad<T>)                   // return a Handler<AsyncResult<T>> object which will pass the null check

Null                                  // a class that can never be instantiated, can be used as `Monad<Null>`
<T> Null.value()                      // this method always returns null
Null.value                            // return the type Null with value null

Symbol<T>                             // a symbol used as an identifier
Symbol.create()                       // create a symbol without name
Symbol.create("name")                 // create a symbol with a name
```
