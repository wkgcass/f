package net.cassite.f;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface MList<E> extends List<E>, AsTransformable<MList<E>> {
    static <E> MList<E> modifiable() {
        return new SimpleMutableMListImpl<>();
    }

    static <E> MList<E> unit() {
        return new SimpleMutableMListImpl<E>().immutable();
    }

    static <E> MList<E> unit(Collection<? extends E> c) {
        if (c instanceof MList) {
            //noinspection unchecked
            return (MList<E>) c;
        }
        return new SimpleMutableMListImpl<E>(c).immutable();
    }

    default MList<E> immutable() {
        if (this instanceof Immutable) {
            return this;
        }
        return new ImmutableMListImpl<>(this);
    }

    @SafeVarargs
    static <E> MList<E> unit(E... es) {
        return unit(Arrays.asList(es)).immutable();
    }

    default E head() {
        return get(0);
    }

    default MList<E> tail() {
        return new TailMListImpl<>(this);
    }

    default E last() {
        if (isEmpty())
            throw new IndexOutOfBoundsException("list is empty");
        return get(size() - 1);
    }

    default MList<E> init() {
        return new InitMListImpl<>(this);
    }

    default <U> MList<U> map(Function<E, U> mapper) {
        return new LazyMListImpl<>(this, (ls, u) -> ls.add(mapper.apply(u)));
    }

    default <U> MList<U> flatMap(Function<E, List<U>> mapper) {
        return new LazyMListImpl<>(this, (ls, u) -> ls.addAll(mapper.apply(u)));
    }
}
