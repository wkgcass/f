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

class TailMListImpl<E> extends AbstractList<E> implements MList<E>, List<E>, Immutable {
    private final List<E> fullList;

    TailMListImpl(List<E> fullList) {
        if (fullList.isEmpty()) {
            throw new IllegalArgumentException("list is empty");
        }
        this.fullList = fullList;
    }

    @Override
    public E get(int index) {
        if (index + 1 >= fullList.size() || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }
        return fullList.get(index + 1);
    }

    @Override
    public int size() {
        return fullList.size() - 1;
    }
}

class InitMListImpl<E> extends AbstractList<E> implements MList<E>, List<E>, Immutable {
    private final List<E> fullList;

    InitMListImpl(List<E> fullList) {
        if (fullList.isEmpty()) {
            throw new IllegalArgumentException("list is empty");
        }
        this.fullList = fullList;
    }

    @Override
    public E get(int index) {
        if (index < 0 || index + 1 >= fullList.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }
        return fullList.get(index);
    }

    @Override
    public int size() {
        return fullList.size() - 1;
    }
}
