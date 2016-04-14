package org.ground.palico;

import org.junit.Test;

public class BenchmarkSuiteTest {
    private void testPerform(ExecutionMode mode) throws Exception{
        new BenchmarkSuite(mode).perform();
    }

    @Test
    public void testGPU() throws Exception{
        testPerform(ExecutionMode.GPU);
    }

    @Test
    public void testJTP() throws Exception {
        testPerform(ExecutionMode.JTP);
    }

    @Test
    public void testSEQ() throws Exception {
        testPerform(ExecutionMode.SEQ);
    }

    @Test
    public void testAll() throws Exception {
        testGPU();
        testJTP();
        testSEQ();
    }
}