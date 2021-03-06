package org.test4j.spec.scenario.step;

import java.io.InputStream;
import java.util.List;

import org.test4j.spec.inner.IScenario;
import org.test4j.spec.inner.StepType;
import org.test4j.spec.scenario.TxtJSpecScenario;
import org.test4j.testng.Test4J;
import org.test4j.tools.commons.ResourceHelper;
import org.testng.annotations.Test;

@SuppressWarnings("serial")
public class TxtJSpecStepTest extends Test4J {
    /**
     * 验证txt文件格式的模板方法
     * 
     * @throws Exception
     */
    @Test
    public void testParseStepType() throws Exception {
        InputStream is = ResourceHelper.getResourceAsStream(XmlJSpecStepTest.class, "template-step.story");
        List<IScenario> scenarios = TxtJSpecScenario.parseJSpecScenarioFrom(is, "utf-8").getScenarios();
        want.list(scenarios).sizeEq(2).reflectionEqMap(2, new DataMap() {
            {
                this.put("scenario", "scenario1", "scenario2");
            }
        });
        want.list(scenarios.get(0).getSteps()).reflectionEqMap(2, new DataMap() {
            {
                this.put("method", "whenMethod");
                this.put("type", StepType.When, StepType.Then);
                this.put("paras", new DataMap() {
                    {
                        this.put("参数1", "[1,2,3]");
                        this.put("参数2", "[2,3,4]");
                    }
                }, new DataMap() {
                    {
                        this.put("参数", "234");
                    }
                });
                this.put("displayText", "模板方法测试，参数一参数1=[1,2,3],参数二参数2=[2,3,4]", "验证方法，同名不同类型\n参数=234");
            }
        });
        want.list(scenarios.get(1).getSteps()).reflectionEqMap(2, new DataMap() {
            {
                this.put("method", "givenMethod", "whenMethod");
                this.put("type", StepType.Given, StepType.When);
                this.put("paras", new DataMap() {
                    {
                        this.put("参数", "init");
                    }
                }, new DataMap() {
                    {
                        this.put("参数1", "[1,2,3]");
                        this.put("参数2", "[4,5,6]");
                    }
                });
            }
        });
    }
}
