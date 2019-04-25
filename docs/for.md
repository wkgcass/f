# for

The for loop expression for async process.

The basic syntax is:

```java
For.init(initValue).cond(ctx -> boolFuture).incr(ctx -> monad).yield(ctx -> monad)
// or maybe cond and incr are sync
For.init(initValue).condSync(ctx -> boolValue).incrSync(ctx -> { /*...*/ }).yield(ctx -> monad)
// the condSync/cond and incrSync/incr can be used together
```

The `init`, `cond`, and `incr` are similar to expressions separated by `;` in traditional for statement.

When `init()` is called, a `ForLoopCtx` will be created, and you can get the context from the lambda argument in `cond()`, `incr()` and `yield()`. The `ForLoopCtx` has only one field `i`, which initially holds your input value of method `init()`, and may be changed in lambda of other methods.

The result returned by the lambda of `yield()` will be added into a list, the list will be the result of the `For` expression. Note that: null values will not be recorded in the result list.

For example, if you write this with blocking java code:

```java
List<T> result = new ArrayList<>();
for (int i = 0; i < array.length; ++i) {
    result.add(someBlockingIO(array[i]));
}
// ...
```

you can change this into an async for expression:

```java
For.init(0).condSync(c -> c.i < array.length).incrSync(c -> ++c.i).yield(c ->
    someAsyncIO(array[c.i])
) // here returns a Monad<MList<T>>
  // .compose(/* ... */)
```

For iterating on one array or collection and don't care about the cursor, you can use the [for-each](for-each.md) expression instead.

## break

You can break a for loop using `F.brk()`, or use `F.brk(value)` to break the loop with a last value (which will be added into the result list).

## continue

Since null values won't be recorded, you can simply return `F.unit()` to `continue` the iteration.

## Can I run the for loop infinitely ?

Yes you can. However you should return a monad with null value in `yield()`, in this case, the returned value will be ignored. Otherwise the memory will be consumed for recording values and result in oom.
