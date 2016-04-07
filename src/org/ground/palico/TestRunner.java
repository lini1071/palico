package org.ground.palico;

import com.amd.aparapi.Kernel;
import org.junit.Test;

public class TestRunner {
    @Test
    public void testGPU() throws Exception {
        TestPlan.perform(new TestPlan(Kernel.EXECUTION_MODE.GPU));
    }

    @Test
    public void testJTP() throws Exception {
        TestPlan.perform(new TestPlan(Kernel.EXECUTION_MODE.JTP));
    }

    @Test
    public void testSEQ() throws Exception {
        TestPlan.perform(new TestPlan(Kernel.EXECUTION_MODE.SEQ));
    }

    @Test
    public void testAll() throws Exception {
        TestPlan.perform(new TestPlan(Kernel.EXECUTION_MODE.GPU));
        TestPlan.perform(new TestPlan(Kernel.EXECUTION_MODE.JTP));
        TestPlan.perform(new TestPlan(Kernel.EXECUTION_MODE.SEQ));
    }

    @Test
    public void testCal() {
        TestPlan.testCal(new TestPlan(Kernel.EXECUTION_MODE.GPU));
        TestPlan.testCal(new TestPlan(Kernel.EXECUTION_MODE.JTP));
        TestPlan.testCal(new TestPlan(Kernel.EXECUTION_MODE.SEQ));
    }
}