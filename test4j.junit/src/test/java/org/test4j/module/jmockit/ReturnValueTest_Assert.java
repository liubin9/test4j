package org.test4j.module.jmockit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import mockit.Mocked;

import org.junit.Test;
import org.test4j.fortest.beans.ComplexObject;
import org.test4j.junit.Test4J;
import org.test4j.module.inject.annotations.Inject;
import org.test4j.module.jmockit.ReturnValueTest.SomeInterface;
import org.test4j.module.jmockit.ReturnValueTest.SomeService;

@SuppressWarnings({ "rawtypes" })
public class ReturnValueTest_Assert extends Test4J {
    public SomeService   someService = new SomeService();

    @Mocked
    @Inject(targets = "someService")
    public SomeInterface someInterface;

    /**
     * 断言与值混用
     * 
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testJMockit() throws IOException {
        new Expectations() {
            {
                someInterface.someCallException();

                someInterface.someCall("darui.wu", (List) any, (HashMap) any);
                result = ComplexObject.instance();

            }
        };
        someInterface.someCallException();

        String result = this.someService.call("darui.wu");
        want.string(result).contains("name=");
    }
}
