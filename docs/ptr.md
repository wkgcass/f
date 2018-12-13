# ptr

## syntax

```
<T> Ptr.of(T)                          // create a pointer with a value
<T> Ptr.nil()                          // create a pointer to null
<T> Ptr.of(() -> T, t -> {})           // create a pointer with getter and setter
<T> Ptr.ofReadonly(T)                  // create a readonly pointer (type is ReadablePtr<T, ?>)

ptr.store(Monad<T>)                    // return Monad<T> with the input value, and after the monad the store will complete
ptr.store(T)                           // store the value, and return pointer itself
ptr.storeNil()                         // let the pointer point to null
<R> ptr.unary(self -> Monad<R>)        // unary operation. return Monad<R> with the returned value
<R> ptr.bin((self, T) -> Monad<R>, T)  // binary operation. return Monad<R> with the returned value
ptr.get()                              // get current pointed value

ptr.getAs(Ptr.Int|Float|Long|...)      // get primitive value from the pointer

// primitive transformer
Ptr.Int                                // to int
Ptr.Float                              // to float
Ptr.Long                               // to long
Ptr.Double                             // to double
Ptr.Byte                               // to byte
Ptr.Short                              // to short
Ptr.Char                               // to char
Ptr.Bool                               // to bool

// unary
Op::not           // !b
Op::leftIncr      // ++i
Op::rightIncr     // i++
Op::leftDecr      // --i
Op::rightDecr     // i--

// bin
Op::and           // a && b
Op::or            // a || b
Op::bitAndAsn     // a &= b, asn stands for "assign"
Op::bitOrAsn      // a |= b
Op::incr          // a += b
Op::decr          // a -= b
Op::mulAsn        // a *= b
Op::divAsn        // a /= b
Op::modAsn        // a %= b
Op::plus          // a + b
Op::minus         // a - b
Op::multiply      // a * b
Op::divide        // a / b
Op::mod           // a % b
Op::bitAnd        // a & b
Op::bitOr         // a | b
```

## usage

```java
Ptr<Integer> a = Ptr.of(1);       // create an integer pointer with value 1
Ptr<Integer> b = Ptr.nil();       // create an integer pointer points to null
b.store(count())                  // count something and store it to b
    .compose(r -> ...);           // the compose lambda argument r will be the same as count result
a.bin(Op::plus, b)                // plus a and b
    .compose(r -> ...);           // the compose lambda argument r will be the sum of a and b
a.unary(Op::rightIncr)            // call something like "a++"
    .compose(r -> ...);           // the compose lambda argument r will be 1, and now a will hold value 2

a.getAs(Ptr.Int)                  // the result is primitive int. The transform will be check by javac at compile time.
```
