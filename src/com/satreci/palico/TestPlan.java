package com.satreci.palico;

import com.amd.aparapi.Kernel;
import com.opencsv.CSVWriter;
import com.satreci.palico.base.Operations;

import java.io.FileWriter;

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
            com.satreci.palico.gpu.aparapi.Operations operationMode = new com.satreci.palico.gpu.aparapi.Operations();
            operationMode.initializeOperation();
            operationMode.getOperation(complexity).run(bufferSize, band1, band2, band3, result, executionMode);
        } else {
            Operations operationSeq = new Operations();
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
}
