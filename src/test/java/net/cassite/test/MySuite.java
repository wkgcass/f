package net.cassite.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestAll.class,
    TestF.class,
    TestOp.class,
    TestMList.class,
    TestJson.class,
    Example.class,
    TestCompilePass.class,
    TestFutureProxy.class,
    TestNPE.class
})
public class MySuite {
}
