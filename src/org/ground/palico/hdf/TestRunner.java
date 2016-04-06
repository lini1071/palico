package org.ground.palico.hdf;

import com.amd.aparapi.Kernel;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import java.nio.ByteOrder;

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
        HDFReader hdfReader = new HDFReader("data/COMS_GOCI_L1B_GA_20160301001642.he5");
        int[] band3 = hdfReader.getDataSet(3);

        for (int i = 0; i < 1000; i++) {
//            if(band3[i] > 0) {
            System.out.println(i + ", " + band3[i]);
//            }
        }
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