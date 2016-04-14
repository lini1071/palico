package org.ground.palico;

import com.opencsv.CSVWriter;

public class ExecutionResult {
    private String name;
    private int size;
    private int complexity;
    private double elapsed;    //milli seconds
    private float[] result;

    public ExecutionResult(String name, ComplexityBenchmarkInput input, double elapsed, float[] result){
        this.name = name;
        this.size = input.getData().getDataSize();
        this.complexity = input.getComplexity();
        this.elapsed = elapsed;
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getComplexity() {
        return complexity;
    }

    public double getElapsed() {
        return elapsed;
    }

    public float[] getResult(){
        return result;
    }

    public void write(CSVWriter writer) {
        writer.writeNext(new String[] { name, String.valueOf(size), String.valueOf(complexity), String.format("%.16f",elapsed)});
    }
}
