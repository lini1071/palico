package org.ground.palico;

import com.amd.aparapi.Kernel;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

class TestPlan {
    int bufferSize = 0;
    int complexity = 0;
    String name;
    Kernel.EXECUTION_MODE executionMode;

    public TestPlan(Kernel.EXECUTION_MODE executionMode) {
        this.executionMode = executionMode;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public String getName() {
        if (executionMode.equals(Kernel.EXECUTION_MODE.GPU)) {
            name = "GPU";
        } else if (executionMode.equals(Kernel.EXECUTION_MODE.JTP)) {
            name = "JTP";
        } else if (executionMode.equals(Kernel.EXECUTION_MODE.SEQ)) {
            name = "SEQ";
        }
        return name;
    }

    public void perform() {
        final float[] band1 = new float[bufferSize];
        final float[] band2 = new float[bufferSize];
        final float[] band3 = new float[bufferSize];
        final float[] result = new float[bufferSize];

        for (int i = 0; i < bufferSize; i++) {
            band1[i] = 3.2345F;
            band2[i] = 7.5678F;
            band3[i] = 2.8456F;
        }

        if (executionMode.equals(Kernel.EXECUTION_MODE.GPU) || executionMode.equals(Kernel.EXECUTION_MODE.JTP)) {
            Operations operationMode = new Operations();
            operationMode.initializeOperation();
            operationMode.getOperation(complexity).run(bufferSize, band1, band2, band3, result, executionMode);
        } else {
            org.ground.palico.base.Operations operationSeq = new org.ground.palico.base.Operations();
            operationSeq.initializeOperation();
            operationSeq.getOperation(complexity).run(bufferSize, band1, band2, band3, result);
        }
    }

    public static void perform(TestPlan plan) throws Exception {
        int mega = 1;
        int cnt = 1;
        String fileName = "performance_result.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(fileName, true));
        while (mega < 65) {
            String[] data = new String[4];
            plan.setBufferSize(mega * 1024 * 1024);

            for (int i = 9; i < 10; i++) {
                plan.setComplexity(i);
                long start = System.currentTimeMillis();
                plan.perform();
                long end = System.currentTimeMillis();

                data[0] = plan.getName();
                data[1] = String.valueOf(mega + " mega");
                data[2] = String.valueOf(i);
                data[3] = String.valueOf((end - start) / 1000.0);
                writer.writeNext(data);
                System.out.printf("No.%d %s [mega : %d] [복잡도 : %d] 실행 시간: %f\n", cnt, plan.getName(), mega, i, (end - start) / 1000.0);
                cnt++;
            }
            mega += 7;
        }
        writer.close();
    }

    public static void testCal(TestPlan testPlan) {
        HashMap<Integer, Float> expected = new HashMap<>();
        expected.put(0, 3.2345f);
        expected.put(1, 0.002156319f);
        expected.put(2, 4.4443857E-7f);
        expected.put(3, 9.160317E-11f);
        expected.put(4, 1.8880315E-14f);
        expected.put(5, 3.8914187E-18f);
        expected.put(6, 8.0205975E-22f);
        expected.put(7, 1.6531241E-25f);
        expected.put(8, 3.4072514E-29f);
        expected.put(9, 7.02268E-33f);

        int mega = 1;
        while (mega < 65) {
            int bufferSize = mega * 1 * 1;
            for (int i = 0; i < 10; i++) {
                testPlan.setBufferSize(bufferSize);
                testPlan.setComplexity(i);
                for (int j = 0; j < bufferSize; j++) {
                    System.out.println(mega + ", " + i + ", " + j + ", " + testPlan.perform()[j]);
                    assertEquals(expected.get(i), testPlan.perform()[j], 1e-4);
                }
            }
            mega += 20;
        }
    }
}
