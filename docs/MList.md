# MList

## syntax

```
<E> MList.unit()                          // create an immutable empty MList
<E> MList.unit(E...)                      // create an immutable MList with given elements in it
<E> MList.unit(Collection<? extends E>)   // create an immutable MList with same elements as the given collection
<E> MList.modifiable()                    // create a mutable MList
MList.collector()                         // create the collector for java stream api to collect elements into the MList

list.mutable()                            // transform the list into a mutable list
                                          // (might be the list itself if already mutable)
list.immutable()                          // transform the list into an immutable list
                                          // (might be the list itself if already immutable)
<U> list.map(e -> U)                      // map each element to another value and join all values into a new IMMUTABLE list
<U> list.flatMap(e -> List<U>)            // map each element to a new list, and join all elements in all lists into a new IMMUTABLE list
list.head()                               // the first element in list
list.tail()                               // a new IMMUTABLE list without the first element
list.init()                               // a new IMMUTABLE list without the last element
list.last()                               // the last element in list

<U> list.as(self -> U)                    // transform into another type
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
```
