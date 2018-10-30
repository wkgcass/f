package net.cassite.f;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface MList<E> extends List<E> {
    static <E> MList<E> unit() {
        return new SimpleMutableMListImpl<>();
    }

    static <E> MList<E> unit(Collection<? extends E> c) {
        if (c instanceof MList) {
            //noinspection unchecked
            return (MList<E>) c;
        }
        return new SimpleMutableMListImpl<>(c);
    }

    default MList<E> immutable() {
        if (this instanceof Immutable) {
            return this;
        }
        return new ImmutableMListImpl<>(this);
    }

    @SafeVarargs
    static <E> MList<E> unit(E... es) {
        return unit(Arrays.asList(es));
    }

    default <U> MList<U> map(Function<E, U> mapper) {
        return new LazyMListImpl<>(this, (ls, u) -> ls.add(mapper.apply(u)));
    }

    default <U> MList<U> compose(Function<E, List<U>> mapper) {
        return new LazyMListImpl<>(this, (ls, u) -> ls.addAll(mapper.apply(u)));
    }

    // ------- alias start -------

    // for scala users
    default <U> MList<U> flatMap(Function<E, List<U>> mapper) {
        return compose(mapper);
    }

    // for haskell users
    default <U> MList<U> bind(Function<E, List<U>> mapper) {
        return compose(mapper);
    }

    // for js promise users
    default <U> MList<U> then(Function<E, List<U>> mapper) {
        return compose(mapper);
    }

    // for haskell users
    default <U> MList<U> fmap(Function<E, U> mapper) {
        return map(mapper);
    }

    // ------- alias end -------
}
