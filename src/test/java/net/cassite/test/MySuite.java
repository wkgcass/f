package net.cassite.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestAll.class,
    TestF.class,
    TestMList.class,
    Example.class,
    TestCompilePass.class,
    TestFutureProxy.class
})
public class MySuite {
}
