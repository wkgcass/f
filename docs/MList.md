# MList

## syntax

```
<E> MList.unit()                          // create an immutable empty MList
<E> MList.unit(E...)                      // create an immutable MList with given elements in it
<E> MList.unit(Collection<? extends E>)   // create an immutable MList with same elements as the given collection, use the collection if it's MList and immutable
<E> MList.modifiable()                    // create a mutable MList
<E> MList.modifiable(E...)                // create a mutable MList with given elements in it
<E> MList.modifiable(Collection<? extends E>) // create a mutable Mlist with same elements as the given collection
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
All methods that say 'creating an immutable list' will return the input collection if possible.

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
