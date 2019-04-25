# if

A class for async if expressions. The syntax is very similar to the raw if statement.

```java
If.cond(boolMonad).run(() -> monad)
  .elseif(() -> boolMonad).run(() -> monad)
  // maybe many elseif sub statements
  .otherwise(() -> monad) // this statement returns a Monad
```

The cond method directly accepts a monad because the first bool expression will always be executed, there's no need to wrap it into a supplier function.

Besides the monad conditions, `If` also provides plain primitive boolean in `cond` and `elseif`.

```java
If.cond(boolValue).run(() -> moand)
  .elseif(boolValue).run(() -> monad)
  .elseif(() -> boolMonad /* two styles can be used together */).run(() -> moand)
  .otherwise(() -> monad)
```

The if expression can omit the `elseif` or `otherwise` sub statements. You can use the following methods to continue the handling process:

```
compose(value -> monad)
map(oldValue -> newValue)
setHandler(r -> {...})
```

See [monad](monad.md) for more info.

## Example

```java
If.cond(service.canHandle(target))
    .run(() -> service.handle(target))
    .otherwise(() -> F.unit(EMPTY_RESULT))
```
