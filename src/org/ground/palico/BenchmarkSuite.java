package org.ground.palico;

import com.opencsv.CSVWriter;

import java.io.FileWriter;

import static org.junit.Assert.assertEquals;

class BenchmarkSuite {
    ExecutionMode executionMode;
    private static final int MIN_PIXEL_COUNT = 1;       // MEGA pixels
    private static final int MAX_PIXEL_COUNT = 65;      // MEGA pixels
    private static final int PIXEL_COUNT_STEP = 7;      // MEGA pixels
    private static final int MIN_COMPLEXITY = 0;        // count
    private static final int MAX_COMPLEXITY = 10;       // count
    private static final int ELAPSED_TIME_UNIT = 1000;  // milli seconds

    public BenchmarkSuite(ExecutionMode executionMode) {
        this.executionMode = executionMode;
    }

    public void perform() throws Exception {
        String fileName = "data/performance_result.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(fileName, true));
        for(int pixelCount = MIN_PIXEL_COUNT; pixelCount < MAX_PIXEL_COUNT; pixelCount++) {
            ComplexityInputData inputData = new ComplexityInputData(pixelCount);
            for (int complexity = MIN_COMPLEXITY; complexity < MAX_COMPLEXITY; complexity++) {
                ComplexityBenchmarkInput input = new ComplexityBenchmarkInput(inputData, complexity);
                ExecutionResult exeResult = perform(input);
                exeResult.write(writer);
            }
            pixelCount += PIXEL_COUNT_STEP;
        }
        writer.close();
    }

    private ExecutionResult perform(ComplexityBenchmarkInput input) {
        final long start = System.currentTimeMillis();
        final float[] result = executionMode.run(input);
        final long end = System.currentTimeMillis();
        final double elapsed = (end - start) / (float)ELAPSED_TIME_UNIT;
        return new ExecutionResult(executionMode.toString(), input, elapsed, result);
    }
}
