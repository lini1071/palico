package org.ground.palico;

import com.amd.aparapi.Kernel;
import com.opencsv.CSVWriter;
import org.ground.palico.base.SeqOperations;
import org.ground.palico.gpu.aparapi.AparapiBandOperations;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

class TestPlan {
    int bufferSize = 0;
    int complexity = 0;
    float[] band1;
    float[] band2;
    float[] band3;
    float[] result;
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

    public void initBand() {
        band1 = new float[bufferSize];
        band2 = new float[bufferSize];
        band3 = new float[bufferSize];
        result = new float[bufferSize];

        for (int i = 0; i < bufferSize; i++) {
            band1[i] = 3.2345F;
            band2[i] = 7.5678F;
            band3[i] = 2.8456F;
        }
    }

    public float[] run() {
        if (executionMode.equals(Kernel.EXECUTION_MODE.GPU) || executionMode.equals(Kernel.EXECUTION_MODE.JTP)) {
            AparapiBandOperations.getOperation(complexity).run(bufferSize, band1, band2, band3, result, executionMode);
        } else {
            SeqOperations.getOperation(complexity).run(bufferSize, band1, band2, band3, result);
        }
        return result;
    }

    public static void perform(TestPlan plan) throws Exception {
        int pixelNum = 64;
        int cnt = 1;
        String fileName = "data/aparapi_benchmark_result.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(fileName, true));
        while (pixelNum < (64) + 10) {
            String[] data = new String[4];
            plan.setBufferSize(pixelNum * 1024 * 1024);
            plan.initBand();

            for (int i = 0; i < 10; i++) {
                perform(plan, pixelNum, cnt, writer, data, i);
                cnt++;
            }
            pixelNum += 1;
        }
        writer.close();
    }

    private static void perform(TestPlan plan, int mega, int cnt, CSVWriter writer, String[] data, int i) {
        plan.setComplexity(i);
        long start = System.currentTimeMillis();
        plan.run();
        long end = System.currentTimeMillis();

        data[0] = plan.getName();
        data[1] = String.valueOf(mega + " mega");
        data[2] = String.valueOf(i);
        data[3] = String.valueOf((end - start) / 1000.0);
        writer.writeNext(data);
        System.out.printf("No.%d %s [mega : %d] [복잡도 : %d] 실행 시간: %f\n", cnt, plan.getName(), mega, i, (end - start) / 1000.0);
    }

    public static void testCal(TestPlan Plan) {
        float expected[] = {
                3.2345f,
                0.002156319f,
                4.4443857E-7f,
                9.160317E-11f,
                1.8880315E-14f,
                3.8914187E-18f,
                8.0205975E-22f,
                1.6531241E-25f,
                3.4072514E-29f,
                7.02268E-33f
        };

        int mega = 1;
        while (mega < 65) {
            int bufferSize = mega * 1 * 1;
            for (int i = 0; i < 10; i++) {
                Plan.setBufferSize(bufferSize);
                Plan.setComplexity(i);
                for (int j = 0; j < bufferSize; j++) {
                    System.out.println(mega + ", " + i + ", " + j + ", " + Plan.run()[j]);
                    assertEquals(expected[i], Plan.run()[j], 1e-4);
                }
            }
            mega += 20;
        }
    }
}
