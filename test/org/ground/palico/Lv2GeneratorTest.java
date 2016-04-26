package org.ground.palico;

import com.amd.aparapi.Kernel;
import org.apache.log4j.BasicConfigurator;
import org.ground.palico.lv2Generator.CHLRawGenerator;
import org.ground.palico.hdf.TestPlan;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static junit.framework.Assert.assertEquals;

public class Lv2GeneratorTest {
    private static final int ELAPSED_TIME_UNIT = 1000;  // milli seconds

    private void perform(String srcFile, String defFile, Kernel.EXECUTION_MODE mode) throws IOException {
        CHLRawGenerator chlGenerator = new CHLRawGenerator(srcFile, defFile, mode);
        final long start = System.currentTimeMillis();
        chlGenerator.perform();
        final long end = System.currentTimeMillis();
        final double elapsed = (end - start) / (float) ELAPSED_TIME_UNIT;

        System.out.println(chlGenerator.getModeName() + " mode spend time : " + elapsed);
    }

    private void testRawCalculation(String outputFile) {
        float expected = 1.8904529f;
        int maxAvailableMem = 1024 * 1024 * 512;
        ByteBuffer readBuffer = ByteBuffer.allocate(maxAvailableMem);
        try {
            FileChannel fileChannel = FileChannel.open(Paths.get(outputFile), StandardOpenOption.READ);
            while (fileChannel.read(readBuffer) > 0) {
                byte[] byteArray = readBuffer.array();
                FloatBuffer floatBuffer = ByteBuffer.wrap(byteArray).asFloatBuffer();

                float band[] = new float[readBuffer.capacity() / Float.BYTES];
                int bandSize = band.length;

                floatBuffer.get(band);
                for (int i = 0; i < bandSize; i++) {
                    assertEquals(expected, band[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testRawPerformanceByGPU() throws IOException {
        perform("data/Aparapi/input.raw", "data/Aparapi/output.raw", Kernel.EXECUTION_MODE.GPU);
        testRawCalculation("data/Aparapi/output.raw");
    }

    @Test
    public void testRawPerformanceBySEQ() throws IOException {
        perform("data/Aparapi/input.raw", "data/Aparapi/output.raw", Kernel.EXECUTION_MODE.SEQ);
        testRawCalculation("data/Aparapi/output.raw");
    }

    @Test
    public void testRawPerformanceByJTP() throws IOException {
        perform("data/Aparapi/input.raw", "data/Aparapi/output.raw", Kernel.EXECUTION_MODE.JTP);
        testRawCalculation("data/Aparapi/output.raw");
    }

    @Test
    public void testAll() throws IOException {
        perform("data/Aparapi/input.raw", "data/Aparapi/output.raw", Kernel.EXECUTION_MODE.GPU);
        testRawCalculation("data/Aparapi/output.raw");
        perform("data/Aparapi/input.raw", "data/Aparapi/output.raw", Kernel.EXECUTION_MODE.SEQ);
        testRawCalculation("data/Aparapi/output.raw");
        perform("data/Aparapi/input.raw", "data/Aparapi/output.raw", Kernel.EXECUTION_MODE.JTP);
        testRawCalculation("data/Aparapi/output.raw");
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
