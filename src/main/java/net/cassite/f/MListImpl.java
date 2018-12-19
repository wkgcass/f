package net.cassite.f;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class SimpleMutableMListImpl<E> extends ArrayList<E> implements MList<E> {
    SimpleMutableMListImpl() {
    }

    SimpleMutableMListImpl(Collection<? extends E> c) {
        super(c);
    }

    @Override
    @NotNull
    public MList<E> subList(int fromIndex, int toIndex) {
        return new SimpleMutableMListImpl<>(super.subList(fromIndex, toIndex));
    }
}

class ImmutableMListImpl<E> extends AbstractList<E> implements MList<E>, Immutable {
    private final List<E> ls;

    ImmutableMListImpl(List<E> ls) {
        this.ls = new ArrayList<>(ls);
    }

    @Override
    public E get(int index) {
        return ls.get(index);
    }

    @Override
    public int size() {
        return ls.size();
    }

    @Override
    @NotNull
    public MList<E> subList(int fromIndex, int toIndex) {
        return MList.unit(super.subList(fromIndex, toIndex));
    }
}

class LazyMListImpl<E, U> extends AbstractList<U> implements MList<U>, Immutable {
    private final Iterator<E> ite;
    private final BiConsumer<List<U>, E> fillElementFunc;
    private final List<U> newList = new ArrayList<>();

    LazyMListImpl(List<E> oldList, BiConsumer<List<U>, E> fillElementFunc) {
        ite = oldList.iterator();
        this.fillElementFunc = fillElementFunc;
    }

    private void drainUntil(int index) {
        while (ite.hasNext() && newList.size() <= index) {
            E e = ite.next();
            fillElementFunc.accept(newList, e);
        }
    }

    @Override
    public U get(int index) {
        drainUntil(index);
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

    @Override
    @NotNull
    public MList<U> subList(int fromIndex, int toIndex) {
        return MList.unit(super.subList(fromIndex, toIndex));
    }

    private IndexOutOfBoundsException oob(int index) {
        return new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
    }

    private class LazyMListIteratorImpl implements ListIterator<U> {
        int curIdx;

        LazyMListIteratorImpl(int curIdx) {
            this.curIdx = curIdx;
        }

        @Override
        public boolean hasNext() {
            if (newList.size() > curIdx) return true;
            drainUntil(curIdx);
            return newList.size() > curIdx;
        }

        @Override
        public U next() {
            if (hasNext()) {
                return newList.get(curIdx++);
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public boolean hasPrevious() {
            return curIdx != 0;
        }

        @Override
        public U previous() {
            if (hasPrevious()) {
                return newList.get(--curIdx);
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            return curIdx;
        }

        @Override
        public int previousIndex() {
            return curIdx - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(U u) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(U u) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    @NotNull
    public Iterator<U> iterator() {
        return listIterator();
    }

    @Override
    @NotNull
    public ListIterator<U> listIterator() {
        return listIterator(0);
    }

    @Override
    @NotNull
    public ListIterator<U> listIterator(int index) {
        if (index < 0) // throw for invalid index
            throw oob(index);
        if (index > newList.size()) {
            drainUntil(index);
            if (index > newList.size())
                throw oob(index); // throw for oob
        }
        return new LazyMListIteratorImpl(index);
    }
}

class TailMListImpl<E> extends AbstractList<E> implements MList<E>, List<E>, Immutable {
    private final List<E> fullList;

    TailMListImpl(List<E> fullList) {
        if (fullList.isEmpty()) {
            throw new IndexOutOfBoundsException("list is empty");
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

    @Override
    @NotNull
    public MList<E> subList(int fromIndex, int toIndex) {
        return MList.unit(super.subList(fromIndex, toIndex));
    }
}

class InitMListImpl<E> extends AbstractList<E> implements MList<E>, List<E>, Immutable {
    private final List<E> fullList;

    InitMListImpl(List<E> fullList) {
        if (fullList.isEmpty()) {
            throw new IndexOutOfBoundsException("list is empty");
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

    @Override
    @NotNull
    public MList<E> subList(int fromIndex, int toIndex) {
        return MList.unit(super.subList(fromIndex, toIndex));
    }
}

class MListCollector<E> implements Collector<E, SimpleMutableMListImpl<E>, MList<E>> {
    private final Supplier<SimpleMutableMListImpl<E>> SUPPLIER = SimpleMutableMListImpl::new;
    private final BiConsumer<SimpleMutableMListImpl<E>, E> ACCUMULATOR = List::add;
    private final BinaryOperator<SimpleMutableMListImpl<E>> COMBINER = (a, b) -> {
        a.addAll(b);
        return a;
    };
    private final Function<SimpleMutableMListImpl<E>, MList<E>> FINISHER = MList::immutable;
    private static final Set<Characteristics> C = Collections.unmodifiableSet(Collections.emptySet());
    private static final MListCollector self = new MListCollector();

    @SuppressWarnings("unchecked")
    static <E> Collector<E, MList<E>, MList<E>> collector() {
        return self;
    }

    MListCollector() {
    }

    @Override
    public Supplier<SimpleMutableMListImpl<E>> supplier() {
        return SUPPLIER;
    }

    @Override
    public BiConsumer<SimpleMutableMListImpl<E>, E> accumulator() {
        return ACCUMULATOR;
    }

    @Override
    public BinaryOperator<SimpleMutableMListImpl<E>> combiner() {
        return COMBINER;
    }

    @Override
    public Function<SimpleMutableMListImpl<E>, MList<E>> finisher() {
        return FINISHER;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return C;
    }
}

class MutableMListCollector<E> extends MListCollector<E> {
    private static final Set<Characteristics> C = Collections.unmodifiableSet(Collections.singleton(Characteristics.IDENTITY_FINISH));
    private static final MutableMListCollector self = new MutableMListCollector();

    @SuppressWarnings("unchecked")
    static <E> Collector<E, MList<E>, MList<E>> collector() {
        return self;
    }

    private MutableMListCollector() {
    }

    @Override
    public Set<Characteristics> characteristics() {
        return C;
    }
}
