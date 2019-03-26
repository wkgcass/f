package net.cassite.f.utils;

import net.cassite.f.MList;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Iterator;

public class MListOp {

    private MListOp() {
    }

    public static MListIntOperator intOp(@NotNull MList<Integer> list) {
        return new MListIntOperator(list);
    }

    public static MListLongOperator longOp(@NotNull MList<Long> list) {
        return new MListLongOperator(list);
    }

    public static MListFloatOperator floatOp(@NotNull MList<Float> list) {
        return new MListFloatOperator(list);
    }

    public static MListDoubleOperator doubleOp(@NotNull MList<Double> list) {
        return new MListDoubleOperator(list);
    }

    public static <T> MListOperator<T> op(@NotNull MList<T> list) {
        return new MListOperator<>(list);
    }

    public static class MListOperator<T> {
        protected final MList<? extends T> list;

        MListOperator(MList<? extends T> list) {
            this.list = list.immutable();
        }

        public MList<T> sort(@NotNull Comparator<T> c) {
            return list.stream().sorted(c).collect(MList.collector());
        }

        public String join(@NotNull String separator) {
            return join("", separator, "");
        }

        public String join(@NotNull String start, @NotNull String separator, @NotNull String end) {
            StringBuilder sb = new StringBuilder(start);
            Iterator<? extends T> ite = list.iterator();
            if (ite.hasNext()) {
                sb.append(ite.next());
            }
            while (ite.hasNext()) {
                sb.append(separator).append(ite.next());
            }
            return sb.append(end).toString();
        }
    }

    public static abstract class MListIntNumberOperator<T extends Number> extends MListOperator<T> {
        MListIntNumberOperator(MList<T> numbers) {
            super(numbers);
        }

        public long sum() {
            long s = 0;
            for (Number n : list) {
                s += n.longValue();
            }
            return s;
        }

        public double avg() {
            return ((double) sum()) / list.size();
        }

        public MList<T> sortAsc() {
            return list.stream().sorted(Comparator.comparingLong(Number::longValue)).collect(MList.collector());
        }

        public MList<T> sortDesc() {
            return list.stream().sorted((a, b) -> (int) (b.longValue() - a.longValue())).collect(MList.collector());
        }
    }

    public static class MListIntOperator extends MListIntNumberOperator<Integer> {
        MListIntOperator(MList<Integer> numbers) {
            super(numbers);
        }
    }

    public static class MListLongOperator extends MListIntNumberOperator<Long> {
        MListLongOperator(MList<Long> numbers) {
            super(numbers);
        }
    }

    public static abstract class MListFloatNumberOperator<T extends Number> extends MListOperator<T> {
        MListFloatNumberOperator(MList<T> numbers) {
            super(numbers);
        }

        public double sum() {
            double s = 0;
            for (Number n : list) {
                s += n.doubleValue();
            }
            return s;
        }

        public double avg() {
            return sum() / list.size();
        }

        public MList<T> sortAsc() {
            return list.stream().sorted(Comparator.comparingDouble(Number::doubleValue)).collect(MList.collector());
        }

        public MList<T> sortDesc() {
            return list.stream().sorted((a, b) -> (int) (b.doubleValue() - a.doubleValue())).collect(MList.collector());
        }
    }

    public static class MListFloatOperator extends MListFloatNumberOperator<Float> {
        MListFloatOperator(MList<Float> numbers) {
            super(numbers);
        }
    }

    public static class MListDoubleOperator extends MListFloatNumberOperator<Double> {
        MListDoubleOperator(MList<Double> numbers) {
            super(numbers);
        }
    }
}
