package org.test4j.json.decoder.array;

import org.test4j.json.JSON;
import org.test4j.testng.Test4J;
import org.testng.annotations.Test;

@Test(groups = { "test4j", "json" })
public class BooleanArrayDecoderTest extends Test4J {

    @Test
    public void testParseFromJSONArray() {
        String json = "[1,false,'true',true]";
        Boolean[] ints = JSON.toObject(json, boolean[].class);
        want.array(ints).sizeEq(4).reflectionEq(new boolean[] { true, false, true, true });
    }
}
