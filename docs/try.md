# try

## syntax

```
<T, EX extends Throwable>
Try.code(() -> Monad<T>)
  [.except(Class<EX>, ex -> Monad<T>)]*
  [.composeFinally(() -> Monad<?>)]?
   .compose|map|setHandler
```

## usage

```java
Try.code(() -> getResource().compose(v -> handleFlow()))                                     // get resource and start handling
   .except(Step1Ex.class, e -> rollback1().map(v -> failResult()))                           // rollback if step 1 failed
   .except(Step2Ex.class, e -> rollback2().compose(v -> rollback1()).map(v -> failResult())  // rollback if step 2 failed
   .composeFinally(() -> releaseResource())                                                  // release the resource
   .compose(result -> {                                                                      // handle result
       // ...
   });
```
