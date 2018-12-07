package net.cassite.f;

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
    public MList<E> subList(int fromIndex, int toIndex) {
        return MList.unit(super.subList(fromIndex, toIndex));
    }
}

class LazyMListImpl<E, U> extends AbstractList<U> implements MList<U>, Immutable {
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

    @Override
    public MList<U> subList(int fromIndex, int toIndex) {
        return MList.unit(super.subList(fromIndex, toIndex));
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
    private final Function<SimpleMutableMListImpl<E>, MList<E>> FINISHER = a -> a;
    private static final Set<Characteristics> C = Collections.unmodifiableSet(Collections.singleton(Characteristics.IDENTITY_FINISH));
    private static final MListCollector self = new MListCollector();

    @SuppressWarnings("unchecked")
    static <E> Collector<E, MList<E>, MList<E>> collector() {
        return self;
    }

    private MListCollector() {
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
