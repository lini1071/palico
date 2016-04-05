package com.satreci.palico;

import com.amd.aparapi.Kernel;
import com.satreci.palico.gpu.aparapi.AparapiBandOperator2;
import com.satreci.palico.base.SequentialBandOperator2;
import org.junit.Test;
import static com.satreci.palico.TestPlan.perform;
import static org.junit.Assert.assertEquals;

public class TestRunner {
    @Test
    public void testGPU() throws Exception{
        perform(new TestPlan(Kernel.EXECUTION_MODE.GPU));
    }

    @Test
    public void testJTP() throws Exception {
        perform(new TestPlan(Kernel.EXECUTION_MODE.JTP));
    }

    @Test
    public void testSEQ() throws Exception {
        perform(new TestPlan(Kernel.EXECUTION_MODE.SEQ));
    }

    @Test
    public void testAll() throws Exception {
        perform(new TestPlan(Kernel.EXECUTION_MODE.GPU));
        perform(new TestPlan(Kernel.EXECUTION_MODE.JTP));
        perform(new TestPlan(Kernel.EXECUTION_MODE.SEQ));
    }

    @Test
    public void testCalMode() {
        float expected = 0.000000444f;
        int size = 1 * 1024 * 1024;
        float[] band1 = new float[size];
        float[] band2 = new float[size];
        float[] band3 = new float[size];
        float[] result = new float[size];

        for (int i = 0; i < size; i++) {
            band1[i] = 3.2345F;
            band2[i] = 7.5678F;
            band3[i] = 2.8456F;
        }

        AparapiBandOperator2 complexityMode = new AparapiBandOperator2();
        complexityMode.run(1, band1, band2, band3, result, Kernel.EXECUTION_MODE.GPU);
        assertEquals(expected, result[0], 1e-7);
    }

    @Test
    public void testCalSEQ() {
        float expected = 0.000000444f; // (3.2345 / 7.5678 / 2.8456) =
        // 0.15019783
        int size = 1 * 1024 * 1024;
        float[] band1 = new float[size];
        float[] band2 = new float[size];
        float[] band3 = new float[size];
        float[] result = new float[size];

        for (int i = 0; i < size; i++) {
            band1[i] = 3.2345F;
            band2[i] = 7.5678F;
            band3[i] = 2.8456F;
        }

        SequentialBandOperator2 complexitySEQ = new SequentialBandOperator2();
        complexitySEQ.run(1, band1, band2, band3, result);
        assertEquals(expected, result[0], 1e-7);
    }
}