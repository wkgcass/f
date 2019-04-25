# docs

## index

1. [monad](monad.md) the monad and applicative
1. [if](if.md) the if expression
1. [for](for.md) the for expression
1. [for-each](for-each.md) the for-each expression
1. [while](while.md) the while and do-while expression
1. [try](try.md) the try expression
1. [MList](MList.md) list
1. [ptr](ptr.md) pointer, a value container
1. [flow](flow.md) code flow
1. [util](util.md) utilities
1. [event-emitter](event-emitter.md) the event emitter
1. [stream](stream.md) the event stream

## relations to vertx and java

The `f.Monad` implements `vertx.Future`  
The `f.MList` implements `java.List`

Almost all functions accepts `vertx.Future` or `java.List` as input parameter, and returns `f.Monad` or `f.MList` as output value, so you can easily use `lib f` to work with existing vertx or java world.

In docs, inputs might be marked with `f.Monad` or `f.MList`, but you should know that, it's guaranteed that `vertx.Future` or `java.List` can be the input parameter.

## structure

Utility functions are held in `class F`, including Monad construction.  
Operators are held in `class Op`.  
Other classes are holding their own functionalities.

Except for container types (e.g. MList, Monad, Ptr), all classes provide their entrance methods with static modifier. (As a result, you can use `import static` to simplify the code.)

## null check

Null checks are performed on all public methods. The following input arguments should not be null:

* monad/future objects
* functions
* container objects

Also, if a function would return a monad/future object, then the returned value should not be null.

For example:

The compose method takes a function, then the function should not be null; the function should returns a future object, then the returned value should not be null.
