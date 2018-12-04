package net.cassite.test;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import net.cassite.f.Export;
import net.cassite.f.Monad;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;

public class TestFutureProxy {
    private int count = 0;

    private void here() {
        ++count;
    }

    private void done() {
        Assert.assertEquals(1, count);
    }

    @Test
    public void isComplete() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public boolean isComplete() {
                here();
                return super.isComplete();
            }
        });
        m.isComplete();
        done();
    }

    @Test
    public void setHandler() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public Future<String> setHandler(Handler<AsyncResult<String>> handler) {
                here();
                return super.setHandler(handler);
            }
        });
        m.setHandler(r -> {
        });
        done();
    }

    @Test
    public void complete1() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public void complete(String result) {
                here();
                super.complete(result);
            }
        });
        m.complete("a");
        done();
    }

    @Test
    public void complete0() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public void complete() {
                here();
                super.complete();
            }
        });
        m.complete();
        done();
    }

    @Test
    public void failThrowable() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public void fail(Throwable cause) {
                here();
                super.fail(cause);
            }
        });
        m.fail(new Throwable());
        done();
    }

    @Test
    public void failString() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public void fail(String failureMessage) {
                here();
                super.fail(failureMessage);
            }
        });
        m.fail("a");
        done();
    }

    @Test
    public void tryComplete1() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public boolean tryComplete(String result) {
                here();
                return super.tryComplete(result);
            }
        });
        m.tryComplete("a");
        done();
    }

    @Test
    public void tryComplete0() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public boolean tryComplete() {
                here();
                return super.tryComplete();
            }
        });
        m.tryComplete();
        done();
    }

    @Test
    public void tryFailThrowable() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public boolean tryFail(Throwable cause) {
                here();
                return super.tryFail(cause);
            }
        });
        m.tryFail(new Throwable());
        done();
    }

    @Test
    public void tryFailString() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public boolean tryFail(String failureMessage) {
                here();
                return super.tryFail(failureMessage);
            }
        });
        m.tryFail("a");
        done();
    }

    @Test
    public void result() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public String result() {
                here();
                return super.result();
            }
        });
        m.result();
        done();
    }

    @Test
    public void cause() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public Throwable cause() {
                here();
                return super.cause();
            }
        });
        //noinspection ThrowableNotThrown
        m.cause();
        done();
    }

    @Test
    public void succeeded() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public boolean succeeded() {
                here();
                return super.succeeded();
            }
        });
        m.succeeded();
        done();
    }

    @Test
    public void failed() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public boolean failed() {
                here();
                return super.failed();
            }
        });
        m.failed();
        done();
    }

    @Test
    public void handle() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
                here();
                super.handle(asyncResult);
            }
        });
        m.handle(new FutureMock<>());
        done();
    }

    @Test
    public void compose2() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public <U> Future<U> compose(Handler<String> handler, Future<U> next) {
                here();
                return null;
            }
        });
        m.compose(r -> {
        }, new FutureMock<>());
        done();
    }

    @Test
    public void compose1() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public <U> Future<U> compose(Function<String, Future<U>> mapper) {
                here();
                return null;
            }
        });
        m.compose(s -> new FutureMock<>());
        done();
    }

    @Test
    public void mapFunc() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public <U> Future<U> map(Function<String, U> mapper) {
                here();
                return null;
            }
        });
        m.map(s -> "a");
        done();
    }

    @Test
    public void mapValue() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public <V> Future<V> map(V value) {
                here();
                return null;
            }
        });
        m.map("a");
        done();
    }

    @Test
    public void mapEmpty() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public <V> Future<V> mapEmpty() {
                here();
                return null;
            }
        });
        m.mapEmpty();
        done();
    }

    @Test
    public void completer() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public Handler<AsyncResult<String>> completer() {
                here();
                return null;
            }
        });
        m.completer();
        done();
    }

    @Test
    public void recover() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public Future<String> recover(Function<Throwable, Future<String>> mapper) {
                here();
                return null;
            }
        });
        m.recover(t -> new FutureMock<>());
        done();
    }

    @Test
    public void otherwiseFunc() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public Future<String> otherwise(Function<Throwable, String> mapper) {
                here();
                return null;
            }
        });
        m.otherwise(s -> "a");
        done();
    }

    @Test
    public void otherwiseValue() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public Future<String> otherwise(String value) {
                here();
                return null;
            }
        });
        m.otherwise("a");
        done();
    }

    @Test
    public void otherwiseEmpty() {
        Monad<String> m = Export.get(new FutureMock<String>() {
            @Override
            public Future<String> otherwiseEmpty() {
                here();
                return null;
            }
        });
        m.otherwiseEmpty();
        done();
    }
}

class FutureMock<T> implements Future<T> {
    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public Future<T> setHandler(Handler<AsyncResult<T>> handler) {
        return null;
    }

    @Override
    public void complete(T result) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void fail(Throwable cause) {

    }

    @Override
    public void fail(String failureMessage) {

    }

    @Override
    public boolean tryComplete(T result) {
        return false;
    }

    @Override
    public boolean tryComplete() {
        return false;
    }

    @Override
    public boolean tryFail(Throwable cause) {
        return false;
    }

    @Override
    public boolean tryFail(String failureMessage) {
        return false;
    }

    @Override
    public T result() {
        return null;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public boolean succeeded() {
        return false;
    }

    @Override
    public boolean failed() {
        return false;
    }

    @Override
    public void handle(AsyncResult<T> asyncResult) {

    }
}
