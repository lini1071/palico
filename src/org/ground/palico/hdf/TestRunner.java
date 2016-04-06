package org.ground.palico.hdf;

import com.amd.aparapi.Kernel;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import java.nio.ByteOrder;

public class TestRunner {

    public void testPerformByOpr(TestPlan testPlan) {
        try {
            long start = System.currentTimeMillis();
            testPlan.perform();
            long end = System.currentTimeMillis();
            String mode = testPlan.getModeName();

            double spendTime = (end - start) / 1000.0;
            System.out.println(mode + " spend time : " + spendTime);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPerformance() {
        BasicConfigurator.configure();
//        testPerformByOpr(new TestPlan(Kernel.EXECUTION_MODE.GPU));
        testPerformByOpr(new TestPlan(Kernel.EXECUTION_MODE.JTP));
    }

    @Test
    public void testCal() throws Exception {
        BasicConfigurator.configure();
        HDFReader hdfReader = new HDFReader("/home/shlee/GOCI/20160301_GOCIdata/LV1B/COMS_GOCI_L1B_GA_20160301001642.he5");
        int[] band3 = hdfReader.getDataset(3);

        for(int i = 0; i< 1000; i++){
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
}