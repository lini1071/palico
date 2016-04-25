package org.ground.palico;

import org.junit.Test;

public class BenchmarkSuiteTest {
    private void testPerform(ExecutionMode mode) throws Exception {
        new BenchmarkSuite(mode).perform();
    }

    @Test
    public void testBenchmarkByGPU() throws Exception {
        testPerform(ExecutionMode.GPU);
    }

    @Test
    public void testBenchmarkByJTP() throws Exception {
        testPerform(ExecutionMode.JTP);
    }

    @Test
    public void testBenchmarkBySEQ() throws Exception {
        testPerform(ExecutionMode.SEQ);
    }

    @Test
    public void testAllBenchmark() throws Exception {
        testBenchmarkByGPU();
        testBenchmarkByJTP();
        testBenchmarkBySEQ();
    }
}