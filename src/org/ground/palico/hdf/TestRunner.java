package org.ground.palico.hdf;

import com.amd.aparapi.Kernel;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import java.nio.ByteOrder;

import static org.junit.Assert.assertEquals;

public class TestRunner {
    @Test
    public void testPerformance() throws Exception {
        BasicConfigurator.configure();
//        testPerformByOpr(new TestPlan(Kernel.EXECUTION_MODE.GPU));
        TestPlan testPlan = new TestPlan(Kernel.EXECUTION_MODE.JTP);

        long start = System.currentTimeMillis();
        testPlan.perform("data/COMS_GOCI_L1B_GA_20160301001642.he5", "data/CHL.he5", "CHL dataset");
        long end = System.currentTimeMillis();

        double spendTime = (end - start) / 1000.0;
        String mode = testPlan.getModeName();
        System.out.println(mode + "mode spend time : " + spendTime);
    }

    @Test
    public void testCal() throws Exception {
        BasicConfigurator.configure();
        TestPlan testPlan = new TestPlan(Kernel.EXECUTION_MODE.JTP);
        float[] result = testPlan.perform("data/COMS_GOCI_L1B_GA_20160301001642.he5", "data/CHL.he5", "CHL dataset");

        float expected = 1.153370052f;
        assertEquals(expected, result[221], 1e-5);
        float expected2 = 1.199449412f;
        assertEquals(expected2, result[1000], 1e-5);
    }

    @Test
    public void testHeapSize() {
        long heapsize = Runtime.getRuntime().totalMemory();
        System.out.println("heapsize is :: " + heapsize);
    }

    @Test
    public void testEndian() {
        if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
            System.out.println("Big-endian");
        } else {
            System.out.println("Little-endian");
        }
    }

    @Test
    public void testLog(){
        System.out.println(Math.log(10));
        System.out.println(Math.log((float)3/10)/Math.log(10));
    }
}