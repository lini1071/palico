package org.ground.palico;

import com.amd.aparapi.Kernel;
import org.apache.log4j.BasicConfigurator;
import org.ground.palico.lv2Generator.CHLRawGenerator;
import org.ground.palico.hdf.TestPlan;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class Lv2GeneratorTest {
    private static final int ELAPSED_TIME_UNIT = 1000;  // milli seconds

    private void perform(CHLRawGenerator chlGenerator) throws IOException {
        final long start = System.currentTimeMillis();
        chlGenerator.perform();
        final long end = System.currentTimeMillis();
        final double elapsed = (end - start) / (float) ELAPSED_TIME_UNIT;

        System.out.println(chlGenerator.getModeName() + " mode spend time : " + elapsed);
    }

    @Test
    public void testRawPerformanceByGPU() throws IOException {
        CHLRawGenerator chlGenerator = new CHLRawGenerator("data/input.raw", "data/output.raw", Kernel.EXECUTION_MODE.GPU);
        perform(chlGenerator);
    }

    @Test
    public void testRawPerformanceBySEQ() throws IOException {
        CHLRawGenerator chlGenerator = new CHLRawGenerator("data/input.raw", "data/output.raw", Kernel.EXECUTION_MODE.SEQ);
        perform(chlGenerator);
    }

    @Test
    public void testRawPerformanceByJTP() throws IOException {
        CHLRawGenerator chlGenerator = new CHLRawGenerator("data/input.raw", "data/output.raw", Kernel.EXECUTION_MODE.JTP);
        perform(chlGenerator);
    }

    @Test
    public void testCalculation() throws Exception {
        BasicConfigurator.configure();
        TestPlan testPlan = new TestPlan(Kernel.EXECUTION_MODE.JTP);
        float[] result = testPlan.perform("data/COMS_GOCI_L1B_GA_20160301001642.he5", "data/CHL.he5", "CHL dataset");

        float expected = 1.153370052f;
        assertEquals(expected, result[221], 1e-5);
        float expected2 = 1.199449412f;
        assertEquals(expected2, result[1000], 1e-5);
    }
}
