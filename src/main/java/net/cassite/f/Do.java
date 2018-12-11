package net.cassite.f;

import io.vertx.core.Future;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class Do {
    private Do() {
    }

    public static <T> DoYield<T> yield(Supplier<Future<T>> loop) {
        return new DoYield<>(loop);
    }

    public static class DoYield<T> {
        private final Supplier<Future<T>> loop;

        DoYield(Supplier<Future<T>> loop) {
            this.loop = loop;
        }

        public Monad<MList<T>> whileCond(BooleanSupplier cond) {
            return whileCond(() -> F.unit(cond.getAsBoolean()));
        }

        public Monad<MList<T>> whileCond(Supplier<Future<Boolean>> cond) {
            Monad<MList<T>> tbd = F.tbd();
            Future<T> f;
            try {
                f = loop.get();
            } catch (Break brk) {
                if (brk.ins == null) {
                    tbd.complete(MList.unit());
                } else {
                    //noinspection unchecked
                    tbd.complete(MList.unit((T) brk.ins));
                }
                return tbd;
            } catch (Throwable t) {
                tbd.fail(t);
                return tbd;
            }
            f.setHandler(r -> {
                if (r.failed()) {
                    if (r.cause() instanceof Break) {
                        @SuppressWarnings({"unchecked", "ThrowableNotThrown"})
                        T t = (T) ((Break) r.cause()).ins;
                        if (t == null) {
                            tbd.complete(MList.unit());
                        } else {
                            tbd.complete(MList.unit(t));
                        }
                    } else {
                        tbd.fail(r.cause());
                    }
                    return;
                }
                MList<T> ls = MList.modifiable();
                T t = r.result();
                if (t != null) {
                    ls.add(t);
                }
                While.cond(cond).yield(loop).setHandler(r2 -> {
                    if (r2.failed()) {
                        tbd.fail(r2.cause());
                        return;
                    }
                    ls.addAll(r2.result());
                    tbd.complete(ls.immutable());
                });
            });
            return tbd;
        }
    }
}
