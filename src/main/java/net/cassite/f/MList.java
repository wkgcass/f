package net.cassite.f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface MList<E> extends List<E> {
    static <E> MList<E> unit() {
        return new MListImpl<>();
    }

    static <E> MList<E> unit(Collection<? extends E> c) {
        if (c instanceof MList) {
            //noinspection unchecked
            return (MList<E>) c;
        }
        return new MListImpl<>(c);
    }

    @SafeVarargs
    static <E> MList<E> unit(E... es) {
        return unit(Arrays.asList(es));
    }

    default <U> MList<U> map(Function<E, U> mapper) {
        MList<U> resultList = unit();
        for (E e : this) {
            resultList.add(mapper.apply(e));
        }
        return resultList;
    }

    default <U> MList<U> compose(Function<E, List<U>> mapper) {
        MList<U> resultList = unit();
        for (E e : this) {
            resultList.addAll(mapper.apply(e));
        }
        return resultList;
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

class MListImpl<E> extends ArrayList<E> implements MList<E>, List<E> {
    MListImpl() {
    }

    MListImpl(Collection<? extends E> c) {
        super(c);
    }
}
