# MList

A list with some helper functions.

Note: we already implemented jackson serializing and deserialing methods for MList, you can freely use it with jackson lib (and vertx).

## syntax

```java
<E> MList.unit()                          // create an immutable empty MList

<E> MList.unit(E...)                      // create an immutable MList with given elements in it

<E> MList.unit(Collection<? extends E>)   // create an immutable MList with same elements as the given collection,
                                          // or will use the input collection if it's MList and immutable

<E> MList.modifiable()                    // create a mutable MList

<E> MList.modifiable(E...)                // create a mutable MList with given elements in it

<E> MList.modifiable(Collection<? extends E>) // create a mutable Mlist with same elements as the given collection
                                          // (always create a new collection)

MList.collector()                         // create a collector for java stream api to collect elements into an immutable MList, always create a new collection

MList.mutableCollector()                  // create a collector for java stream api to collect elements into a mutable MList, always create a new collection

list.mutable()                            // transform the list into a mutable list
                                          // (always create a new collection)

list.immutable()                          // transform the list into an immutable list
                                          // (might be the list itself if already immutable)

<U> list.map(e -> U)                      // map each element to another value and join all values into a new IMMUTABLE list

<U> list.flatMap(e -> List<U>)            // map each element to a new list, and join all elements in all lists into a new IMMUTABLE list

list.filter(e -> boolean)                 // filter the list and create a new list

list.head()                               // the first element in list

list.tail()                               // a new IMMUTABLE list without the first element

list.init()                               // a new IMMUTABLE list without the last element

list.last()                               // the last element in list

<U> list.as(self -> U)                    // transform into another type
```

All methods that say 'creating a mutable list' will always generate a new list.  
All methods that say 'creating an immutable list' will try to directly return the input collection if possible.

## use with monad

We provide some functions to let monad work well with monad.

```java
F::composite    // composite for MList and Monad, use it with MList#as
                // (similar to vertx CompositeFuture)

F::flip         // flip from List<Monad<T>> to Monad<List<T>>, use it with MList#as
```

When you have a list of monads to run, and want to wait for all of them to finish, you can use `composite`. Note: the result of composite is always `null`.

```java
MList<Monad<?>> monads = MList.unit(m1, m2, m3, m4);
monads.as(F::composite).compose(() -> ...)
```

When you have a list of monads which all are bond to the same type, you can use `flip` to wait for them to finish, and give you the list of corresponding results. Note: the index of the result values and the monad int the original list corresponds. As a result, null values are counted as well.

The reason for the method name is that, the input type before calling the method is `MList<Monad<T>>` and will become `Monad<MList<T>>` after, which flips the `MList` and `Monad`.

```java
MList<Monad<Integer>> moands = MList.unit(countA(), countB(), countC());
monads.as(F::flip).compose(numberList -> ...)
```

## usage

```java
MList.unit(1,2,3)                                    // create an immutable list with [1, 2, 3] in it
MList.unit()                                         // create an empty immutable list
MList.unit(list)                                     // create MList from existing list
MList<Integer> list = MList.modifiable();            // create a mutable list
list.add(1); list.add(2); list.add(3);               // manipulate the list
list.immutable()                                     // create an immutable list with all elements in the old list
list.mutable()                                       // create a mutable list with all elements in the old list

list.map(e -> ("" + e))                              // map to string, result would be ["1", "2", "3"]
list.flatMap(e -> MList.unit(e, e + 10, e + 100))    // flatMap, result would be [1, 11, 101, 2, 12, 102, 3, 13, 103]

MList<Monad<Result>> monadList
monadList.as(F::flip).compose(results -> ...)        // flip the MList<Monad<Result>> into Monad<MList<Result>>
monadList.as(F::composite).compose(v -> ...)         // wait for all monads in list to finish, if one monad got error then the result will be set to failed

Json.decodeValue("[1, 2, 3]",                        // json decode into the MList
    new TypeReference<MList<Integer>>() {});
Json.encode(MList.unit(1,2,3));                      // json encode from the MList
```
