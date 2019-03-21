package net.cassite.f;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonDeserialize(using = MListDeserializer.class)
public interface MList<E> extends List<E>, AsTransformable<MList<E>> {
    static <E> Collector<E, ?, MList<E>> collector() {
        return MListCollector.collector();
    }

    static <E> Collector<E, ?, MList<E>> mutableCollector() {
        return MutableMListCollector.collector();
    }

    static <E> MList<E> modifiable() {
        return new SimpleMutableMListImpl<>();
    }

    static <E> MList<E> modifiable(@NotNull Collection<? extends E> c) {
        if (c == null)
            throw new NullPointerException();
        MList<E> ls = new SimpleMutableMListImpl<>();
        ls.addAll(c);
        return ls;
    }

    @SafeVarargs
    static <E> MList<E> modifiable(@NotNull E... es) {
        if (es == null)
            throw new NullPointerException();
        return modifiable(Arrays.asList(es));
    }

    static <E> MList<E> unit() {
        return new SimpleMutableMListImpl<E>().immutable();
    }

    static <E> MList<E> unit(@NotNull Collection<? extends E> c) {
        if (c == null)
            throw new NullPointerException();
        if (c instanceof MList) {
            //noinspection unchecked
            return ((MList<E>) c).immutable();
        }
        return new SimpleMutableMListImpl<E>(c).immutable();
    }

    @SafeVarargs
    static <E> MList<E> unit(@NotNull E... es) {
        if (es == null)
            throw new NullPointerException();
        return unit(Arrays.asList(es)).immutable();
    }

    default MList<E> immutable() {
        if (this instanceof Immutable) {
            return this;
        }
        return new ImmutableMListImpl<>(this);
    }

    default MList<E> mutable() {
        return new SimpleMutableMListImpl<>(this);
    }

    default E head() {
        if (isEmpty())
            throw new IndexOutOfBoundsException("list is empty");
        return get(0);
    }

    default MList<E> tail() {
        return new TailMListImpl<>(this.immutable());
    }

    default E last() {
        if (isEmpty())
            throw new IndexOutOfBoundsException("list is empty");
        return get(size() - 1);
    }

    default MList<E> init() {
        return new InitMListImpl<>(this.immutable());
    }

    default <U> MList<U> map(@NotNull Function<E, U> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return new LazyMListImpl<>(this.immutable(), (ls, u) -> ls.add(mapper.apply(u)));
    }

    default <U> MList<U> flatMap(@NotNull Function<E, List<U>> mapper) {
        if (mapper == null)
            throw new NullPointerException();
        return new LazyMListImpl<>(this.immutable(), (ls, u) -> ls.addAll(mapper.apply(u)));
    }

    default MList<E> filter(@NotNull Predicate<E> predicate) {
        if (predicate == null)
            throw new NullPointerException();
        return new LazyMListImpl<>(this.immutable(), (ls, u) -> {
            if (predicate.test(u)) {
                ls.add(u);
            }
        });
    }

    @Override
    MList<E> subList(int from, int to);
}
