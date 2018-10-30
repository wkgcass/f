package net.cassite.f;

import java.util.*;
import java.util.function.BiConsumer;

class SimpleMutableMListImpl<E> extends ArrayList<E> implements MList<E>, List<E> {
    SimpleMutableMListImpl() {
    }

    SimpleMutableMListImpl(Collection<? extends E> c) {
        super(c);
    }
}

class ImmutableMListImpl<E> extends AbstractList<E> implements MList<E>, List<E>, Immutable {
    private final List<E> ls;

    ImmutableMListImpl(List<E> ls) {
        this.ls = ls;
    }

    @Override
    public E get(int index) {
        return ls.get(index);
    }

    @Override
    public int size() {
        return ls.size();
    }
}

class LazyMListImpl<E, U> extends AbstractList<U> implements MList<U>, List<U>, Immutable {
    private final Iterator<E> ite;
    private final List<U> newList = new ArrayList<>();
    private final BiConsumer<List<U>, E> fillElementFunc;

    LazyMListImpl(List<E> oldList, BiConsumer<List<U>, E> fillElementFunc) {
        ite = oldList.iterator();
        this.fillElementFunc = fillElementFunc;
    }

    @Override
    public U get(int index) {
        while (ite.hasNext() && newList.size() <= index) {
            E e = ite.next();
            fillElementFunc.accept(newList, e);
        }
        return newList.get(index);
    }

    @Override
    public int size() {
        while (ite.hasNext()) {
            E e = ite.next();
            fillElementFunc.accept(newList, e);
        }
        return newList.size();
    }
}
